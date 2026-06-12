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

import com.indian.plccom.modbus.ISecureModbusCertificateValidator;
import com.indian.plccom.modbus.ModbusMaster;
import com.indian.plccom.modbus.ModbusSlave;
import com.indian.plccom.modbus.OperationResult;
import com.indian.plccom.modbus.ReadRequest;
import com.indian.plccom.modbus.ReadResult;
import com.indian.plccom.modbus.ReadValue;
import com.indian.plccom.modbus.RequestBuilder;
import com.indian.plccom.modbus.SecureModbusCertificateValidationContext;
import com.indian.plccom.modbus.SecureModbusOwnCertificate;
import com.indian.plccom.modbus.SecureModbusPkiStore;
import com.indian.plccom.modbus.SecureModbusTcpOptions;
import com.indian.plccom.modbus.WriteRequest;
import com.indian.plccom.modbus.WriteResult;
import com.indian.plccom.modbus.Enums.eByteOrder;
import com.indian.plccom.modbus.Enums.eDataType;
import com.indian.plccom.modbus.Enums.eModbusRegion;
import com.indian.plccom.modbus.Enums.eReadFunction;
import com.indian.plccom.modbus.Enums.eRegisterMode;
import com.indian.plccom.modbus.Enums.eWriteFunction;

/**
 * Workshop 23 - Mutual TLS Loopback.
 *
 * This self-contained workshop starts a SecureTCP slave and a SecureTCP master
 * in one process. Both endpoints present their own certificate, so the TLS
 * handshake demonstrates mutual authentication before the normal Modbus
 * read/write cycle begins.
 */
public final class _23_MutualTLS_Loopback {
	private static final String LICENSE_USER_NAME = "<Enter your UserName here>";
	private static final String LICENSE_SERIAL = "<Enter your Serial here>";
	private static final String LISTENER_ID = "secure-mutualtls-loopback";
	private static final String CONNECT_HOST = "127.0.0.1";
	private static final int LOOPBACK_PORT = SecureModbusTcpOptions.DEFAULT_SECURE_MODBUS_TCP_PORT;
	private static final int SLAVE_ID = 1;

	private _23_MutualTLS_Loopback() {
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
		PLCcomConsole.open("PLCcom for Modbus - Workshop 23", 1000);
		System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
		System.out.println("║ PLCcom for Modbus - Workshop 23: Mutual TLS Loopback               ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Starts one local SecureTCP slave and connects with a SecureTCP     ║");
		System.out.println("║ master. Both peers provide local endpoint certificates.            ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ What you will learn:                                               ║");
		System.out.println("║   * Configure local certificates on both SecureTCP peers           ║");
		System.out.println("║   * Watch a custom certificate validator run on both sides         ║");
		System.out.println("║   * Run normal Modbus reads and writes over TLS                    ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Self-contained endpoint: 127.0.0.1:802                             ║");
		System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
		System.out.println();

		ModbusSlave slave = null;
		ModbusMaster master = null;
		try {
			// Step 1: Prepare both TLS identities before any network socket is
			// opened. Mutual TLS means that the slave shows an own certificate to
			// the master, and the master shows an own certificate to the slave.
			// Each endpoint therefore receives its own small PKI folder below the
			// workshop directory.
			SecureModbusTcpOptions slaveOptions = createOptions("./pki/mutualtls-slave",
					"PLCcom Modbus Workshop 23 Slave", "SecureTCP master");
			SecureModbusTcpOptions masterOptions = createOptions("./pki/mutualtls-master",
					"PLCcom Modbus Workshop 23 Master", "SecureTCP slave");

			// The generated certificates contain "localhost" as DNS name and
			// 127.0.0.1 as IP address. Setting the target host name makes the
			// example explicit and keeps the intent readable when the connector
			// itself uses the numeric loopback address.
			masterOptions.setTargetHostName("localhost");

			// Step 2: Create the in-process slave and preload two holding
			// registers. The SecureTCP listener exposes the same Modbus data model
			// as a plain TCP listener; only the transport channel is protected by
			// TLS before Modbus bytes are exchanged.
			slave = new ModbusSlave(LICENSE_USER_NAME, LICENSE_SERIAL, SLAVE_ID, eRegisterMode._16Bit,
					eByteOrder.AB_CD);
			slave.setValue(createUshort(0), eModbusRegion.HoldingRegister, createUshort(2300));
			slave.setValue(createUshort(1), eModbusRegion.HoldingRegister, createUshort(2301));
			slave.addOrReplaceListener_SecureTCP(LISTENER_ID, LOOPBACK_PORT, slaveOptions);

			// Give the listener thread a moment to enter its accept loop before the
			// master opens the client socket.
			Thread.sleep(250L);

			// Step 3: Create the master with its own certificate and connect it to
			// the local SecureTCP listener. From this point on, the master API is
			// used exactly like the plain TCP workshop: the connector hides the TLS
			// handshake and presents a normal Modbus request interface.
			master = new ModbusMaster(LICENSE_USER_NAME, LICENSE_SERIAL);
			master.setConnector_SecureTCP(CONNECT_HOST, LOOPBACK_PORT, masterOptions);
			master.setRegisterMode(eRegisterMode._16Bit);

			// Step 4: Read the initial values through Modbus function 03. The
			// request still names slave id, function code, start address, datatype,
			// and quantity; SecureTCP does not change the Modbus addressing model.
			ReadRequest readRequest = RequestBuilder.ReadRequestBuilder.create(SLAVE_ID,
					eReadFunction.F03_Read_Holding_Registers, 0, eDataType.USHORT, 2);
			readRequest.setByteOrder(eByteOrder.AB_CD);
			printReadResult("Initial SecureTCP read", master.read(readRequest));

			// Step 5: Write two new unsigned 16-bit values and read them back over
			// the same TLS session. This confirms that the encrypted channel is not
			// only established, but is also carrying normal Modbus traffic.
			WriteRequest writeRequest = RequestBuilder.WriteRequestBuilder.create(SLAVE_ID,
					eWriteFunction.F16_Write_Multiple_Registers, 0);
			writeRequest.setByteOrder(eByteOrder.AB_CD);
			writeRequest.addUShortRange(new int[] { 2310, 2311 });
			printWriteResult("SecureTCP write", master.write(writeRequest));
			printReadResult("Read back after SecureTCP write", master.read(readRequest));
		} catch (Exception ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
		} catch (Throwable ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
		} finally {
			if (master != null) {
				// Always unload the active connector first. That closes the client
				// side of the TLS connection before the listener disappears.
				master.unload();
			}
			if (slave != null) {
				// Remove the named listener and unload the slave so the background
				// listener thread and its socket are released before the console
				// window is closed.
				slave.removeListener(LISTENER_ID);
				slave.unload();
			}
		}
		System.out.println();
		PLCcomConsole.waitForEnter();
		PLCcomConsole.close();
	}

	private static SecureModbusTcpOptions createOptions(String pkiPath, String certificateAlias, String remoteName)
			throws Exception {
		// Each endpoint receives a PKI store with the usual own, trusted, issuer,
		// and rejected folders. The folders are deliberately created below the
		// workshop so a learner can inspect the generated files after a run.
		SecureModbusPkiStore pkiStore = new SecureModbusPkiStore(pkiPath);
		pkiStore.ensureDirectories();

		// The own certificate is created on demand and reused on the next start.
		// The alias becomes the readable file name and subject common name. DNS
		// and IP subject alternative names are important for TLS hostname checks.
		SecureModbusOwnCertificate ownCertificate = new SecureModbusOwnCertificate(pkiStore, certificateAlias,
				"workshop-password", 3650, "Indi.An GmbH", Arrays.asList("localhost"),
				Arrays.asList(InetAddress.getByName("127.0.0.1")));

		// SecureTCP options bundle the PKI store, the local identity, and the
		// validation callbacks used by the connector or listener.
		SecureModbusTcpOptions options = new SecureModbusTcpOptions();
		options.setPkiStore(pkiStore);
		options.setOwnCertificate(ownCertificate);

		// In a production mutual-TLS setup, each side would trust the other side
		// through trusted/certs or issuers/certs. This workshop intentionally uses
		// a custom validator so users can see exactly where certificate decisions
		// enter the flow. While this validator is assigned, the normal PKI trust
		// decision is bypassed.
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


	private static final class UnsafeAcceptAllCertificateValidator implements ISecureModbusCertificateValidator {
		private final String remoteName;

		private UnsafeAcceptAllCertificateValidator(String remoteName) {
			this.remoteName = remoteName;
		}

		/**
		 * Accepts every remote certificate for this isolated loopback workshop.
		 *
		 * @param context
		 *            certificate information supplied by the SecureTCP TLS layer
		 * @return always {@code true}; do not use this policy in production
		 */
		@Override
		public boolean validate(SecureModbusCertificateValidationContext context) {
			System.out.println("WARNING: accepting every " + remoteName + " certificate.");
			System.out.println("         PKI trust checks are bypassed by this workshop validator.");
			for (X509Certificate certificate : context.getCertificateChain()) {
				System.out.println("         " + certificate.getSubjectX500Principal());
			}
			return true;
		}
	}
}
