// MIT License
// Copyright (c) Indi.An GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

import static com.indian.plccom.modbus.UnsignedDatatypes.UBuilder.createUshort;

import java.net.InetAddress;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import com.indian.plccom.modbus.ISecureModbusAuthorizationValidator;
import com.indian.plccom.modbus.ISecureModbusCertificateValidator;
import com.indian.plccom.modbus.ModbusMaster;
import com.indian.plccom.modbus.ModbusSlave;
import com.indian.plccom.modbus.OperationResult;
import com.indian.plccom.modbus.ReadRequest;
import com.indian.plccom.modbus.ReadResult;
import com.indian.plccom.modbus.ReadValue;
import com.indian.plccom.modbus.RequestBuilder;
import com.indian.plccom.modbus.SecureModbusAuthorizationContext;
import com.indian.plccom.modbus.SecureModbusCertificateValidationContext;
import com.indian.plccom.modbus.SecureModbusOwnCertificate;
import com.indian.plccom.modbus.SecureModbusPkiStore;
import com.indian.plccom.modbus.SecureModbusTcpOptions;
import com.indian.plccom.modbus.WriteRequest;
import com.indian.plccom.modbus.WriteResult;
import com.indian.plccom.modbus.Enums.eByteOrder;
import com.indian.plccom.modbus.Enums.eDataType;
import com.indian.plccom.modbus.Enums.eMBFunction;
import com.indian.plccom.modbus.Enums.eModbusRegion;
import com.indian.plccom.modbus.Enums.eReadFunction;
import com.indian.plccom.modbus.Enums.eRegisterMode;
import com.indian.plccom.modbus.Enums.eWriteFunction;

/**
 * Workshop 24 - Role-Based Authorization.
 *
 * This workshop builds on SecureTCP mutual TLS and adds an authorization
 * validator on the slave. The validator reads the role from the client
 * certificate and decides whether the Modbus function is allowed.
 */
public final class _24_RoleBasedAuthorization {
	private static final String LICENSE_USER_NAME = "<Enter your UserName here>";
	private static final String LICENSE_SERIAL = "<Enter your Serial here>";
	private static final String LISTENER_ID = "secure-role-loopback";
	private static final String CONNECT_HOST = "127.0.0.1";
	private static final int LOOPBACK_PORT = SecureModbusTcpOptions.DEFAULT_SECURE_MODBUS_TCP_PORT;
	private static final int SLAVE_ID = 1;

	private _24_RoleBasedAuthorization() {
	}

	/**
	 * Starts the workshop.
	 *
	 * @param args
	 *            command-line arguments; not used by this workshop
	 * @throws Exception
	 *             when console input or certificate setup fails unexpectedly
	 */
	public static void main(String[] args) throws Exception {
		PLCcomConsole.open("PLCcom for Modbus - Workshop 24", 1000);
		System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
		System.out.println("║ PLCcom for Modbus - Workshop 24: Role-Based Authorization          ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Starts a SecureTCP loopback pair and lets the slave decide whether ║");
		System.out.println("║ the authenticated client role may execute each Modbus function.    ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ What you will learn:                                               ║");
		System.out.println("║   * Put a Modbus Security role into the client certificate         ║");
		System.out.println("║   * Validate roles on the slave side                               ║");
		System.out.println("║   * Keep certificate validation and authorization separate         ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Self-contained endpoint: 127.0.0.1:802                             ║");
		System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
		System.out.println();

		ModbusSlave slave = null;
		ModbusMaster master = null;
		try {
			// Step 1: Configure the SecureTCP slave endpoint. The slave owns the
			// server certificate used during the TLS handshake. It does not need a
			// Modbus Security role for this workshop because it is the server that
			// authorizes incoming client requests.
			SecureModbusTcpOptions slaveOptions = createOptions("./pki/roleauth-slave",
					"PLCcom Modbus Workshop 24 Slave", null, "SecureTCP master");

			// The authorization validator runs after TLS certificate validation has
			// accepted the peer. Keeping both decisions separate is important:
			// certificate validation answers "who connected?", authorization
			// answers "what is this peer allowed to do?".
			slaveOptions.setAuthorizationValidator(new RoleAuthorizationValidator());

			// Step 2: Configure the SecureTCP master endpoint. The role name is
			// written into the master's own certificate and is later visible to the
			// slave-side authorization validator.
			SecureModbusTcpOptions masterOptions = createOptions("./pki/roleauth-master",
					"PLCcom Modbus Workshop 24 Maintenance Master", "maintenance", "SecureTCP slave");

			// The certificate contains localhost as a subject alternative name.
			// Setting the target host name makes the hostname check intention clear
			// even though the socket connects to 127.0.0.1.
			masterOptions.setTargetHostName("localhost");

			// Step 3: Start a small slave data model. Role-based authorization does
			// not change how Modbus data is stored; it only decides whether an
			// already authenticated request may reach this data store.
			slave = new ModbusSlave(LICENSE_USER_NAME, LICENSE_SERIAL, SLAVE_ID, eRegisterMode._16Bit,
					eByteOrder.AB_CD);
			slave.setValue(createUshort(0), eModbusRegion.HoldingRegister, createUshort(2400));
			slave.setValue(createUshort(1), eModbusRegion.HoldingRegister, createUshort(2401));
			slave.addOrReplaceListener_SecureTCP(LISTENER_ID, LOOPBACK_PORT, slaveOptions);

			// The listener is started on a background thread. A short pause keeps
			// the sample deterministic when master and slave live in one process.
			Thread.sleep(250L);

			// Step 4: Connect the master. During the TLS handshake, the master
			// presents its certificate with role "maintenance". The slave accepts
			// that certificate through the workshop validator and then asks the
			// authorization validator about each Modbus request.
			master = new ModbusMaster(LICENSE_USER_NAME, LICENSE_SERIAL);
			master.setConnector_SecureTCP(CONNECT_HOST, LOOPBACK_PORT, masterOptions);
			master.setRegisterMode(eRegisterMode._16Bit);

			// Step 5: Reads are allowed for every authenticated role in this
			// workshop. The request itself is a normal function-code 03 read over
			// the protected SecureTCP channel.
			ReadRequest readRequest = RequestBuilder.ReadRequestBuilder.create(SLAVE_ID,
					eReadFunction.F03_Read_Holding_Registers, 0, eDataType.USHORT, 2);
			readRequest.setByteOrder(eByteOrder.AB_CD);
			printReadResult("Authorized read", master.read(readRequest));

			// Step 6: Writes are allowed only for the "maintenance" role. Change
			// the role name above to something else to see the authorization
			// validator reject this function while the TLS connection still works.
			WriteRequest writeRequest = RequestBuilder.WriteRequestBuilder.create(SLAVE_ID,
					eWriteFunction.F16_Write_Multiple_Registers, 0);
			writeRequest.setByteOrder(eByteOrder.AB_CD);
			writeRequest.addUShortRange(new int[] { 2410, 2411 });
			printWriteResult("Authorized write", master.write(writeRequest));
			printReadResult("Read back after authorized write", master.read(readRequest));
		} catch (Exception ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
		} catch (Throwable ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
		} finally {
			if (master != null) {
				// Close the client connector before stopping the in-process server.
				// This avoids leaving an open TLS socket behind while the workshop
				// console is shutting down.
				master.unload();
			}
			if (slave != null) {
				// Remove the named listener first, then unload the slave. This is
				// the same lifecycle pattern used by the plain TCP slave workshop.
				slave.removeListener(LISTENER_ID);
				slave.unload();
			}
		}
		System.out.println();
		PLCcomConsole.waitForEnter();
		PLCcomConsole.close();
	}

	private static SecureModbusTcpOptions createOptions(String pkiPath, String certificateAlias, String roleName,
			String remoteName) throws Exception {
		// The PKI folder contains own/certs, own/private, trusted/certs,
		// issuers/certs, and rejected/certs. In this workshop it is intentionally
		// local to the example so the generated certificate and key files remain
		// easy to find and inspect.
		SecureModbusPkiStore pkiStore = new SecureModbusPkiStore(pkiPath);
		pkiStore.ensureDirectories();

		// The own certificate is created if it does not exist yet. The optional
		// role name is embedded into the certificate so the slave can read it from
		// the authenticated client identity during authorization.
		SecureModbusOwnCertificate ownCertificate = new SecureModbusOwnCertificate(pkiStore, certificateAlias,
				"workshop-password", 3650, "Indi.An GmbH", Arrays.asList("localhost"),
				Arrays.asList(InetAddress.getByName("127.0.0.1")));
		ownCertificate.setRoleName(roleName);

		// SecureTCP options keep transport security configuration together:
		// where the PKI lives, which own certificate is presented, how remote
		// certificates are validated, and whether authorization is applied.
		SecureModbusTcpOptions options = new SecureModbusTcpOptions();
		options.setPkiStore(pkiStore);
		options.setOwnCertificate(ownCertificate);

		// Workshop-only override:
		// This validator accepts every remote certificate so that the workshop can
		// focus on role authorization. While it is set, PLCcom's normal PKI trust
		// decision is not executed.
		options.setRemoteCertificateValidator(new UnsafeAcceptAllCertificateValidator(remoteName));
		return options;
	}

	private static void printReadResult(String title, ReadResult result) {
		System.out.println(title + ": " + result.getMessage());
		if (result.getQuality() != OperationResult.eQuality.GOOD) {
			System.out.println();
			return;
		}
		for (ReadValue value : result.fetchValues()) {
			System.out.println("  Holding register " + value.getAddress() + " = " + value.getValue());
		}
		System.out.println();
	}

	private static void printWriteResult(String title, WriteResult result) {
		System.out.println(title + ": " + result.getMessage());
		System.out.println();
	}


	private static final class RoleAuthorizationValidator implements ISecureModbusAuthorizationValidator {
		/**
		 * Allows reads for any authenticated role and writes only for maintenance.
		 *
		 * @param context
		 *            authenticated role and Modbus request information
		 * @return {@code true} when the request should be executed by the slave
		 */
		@Override
		public boolean validate(SecureModbusAuthorizationContext context) {
			// The role comes from the already accepted client certificate. This
			// example grants full access to maintenance clients and read-only
			// access to every other authenticated client. Real projects usually
			// map roles to plant-specific permissions here.
			String role = context.getRoleName();
			eMBFunction function = context.getFunction();
			boolean allowed = "maintenance".equalsIgnoreCase(role)
					|| function == eMBFunction.F03_Read_Holding_Registers
					|| function == eMBFunction.F04_Read_Input_Register;
			System.out.println("Authorization role='" + role + "', function=" + function + " => " + allowed);
			return allowed;
		}
	}

	private static final class UnsafeAcceptAllCertificateValidator implements ISecureModbusCertificateValidator {
		private final String remoteName;

		private UnsafeAcceptAllCertificateValidator(String remoteName) {
			this.remoteName = remoteName;
		}

		/**
		 * Accepts every remote certificate so role validation can be studied in
		 * isolation.
		 *
		 * @param context
		 *            certificate information supplied by the SecureTCP TLS layer
		 * @return always {@code true}; do not use this policy in production
		 */
		@Override
		public boolean validate(SecureModbusCertificateValidationContext context) {
			System.out.println("WARNING: accepting every " + remoteName + " certificate.");
			for (X509Certificate certificate : context.getCertificateChain()) {
				System.out.println("         " + certificate.getSubjectX500Principal());
			}
			return true;
		}
	}
}
