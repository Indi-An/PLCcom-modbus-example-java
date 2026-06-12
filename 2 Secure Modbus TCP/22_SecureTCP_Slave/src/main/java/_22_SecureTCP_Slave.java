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
import com.indian.plccom.modbus.IncomingLogEntryEventHandler;
import com.indian.plccom.modbus.LogEntry;
import com.indian.plccom.modbus.ModbusSlave;
import com.indian.plccom.modbus.PortStateEventHandler;
import com.indian.plccom.modbus.SecureModbusCertificateValidationContext;
import com.indian.plccom.modbus.SecureModbusOwnCertificate;
import com.indian.plccom.modbus.SecureModbusPkiCertificateStoredEventArgs;
import com.indian.plccom.modbus.SecureModbusPkiCertificateStoredListener;
import com.indian.plccom.modbus.SecureModbusPkiStore;
import com.indian.plccom.modbus.SecureModbusTcpOptions;
import com.indian.plccom.modbus.SecureModbusTlsProtocolMode;
import com.indian.plccom.modbus.iConnectionStateChangeEvent;
import com.indian.plccom.modbus.iIncomingLogEntryEvent;
import com.indian.plccom.modbus.Enums.eByteOrder;
import com.indian.plccom.modbus.Enums.eConnectionState;
import com.indian.plccom.modbus.Enums.eModbusRegion;
import com.indian.plccom.modbus.Enums.eRegisterMode;

/**
 * Workshop 22 - SecureTCP Slave.
 *
 * This workshop starts a Secure Modbus TCP slave, creates or loads a local
 * certificate, and exposes prepared holding-register values through TLS.
 */
public final class _22_SecureTCP_Slave implements iConnectionStateChangeEvent, iIncomingLogEntryEvent {
	private static final String LICENSE_USER_NAME = "<Enter your UserName here>";
	private static final String LICENSE_SERIAL = "<Enter your Serial here>";
	private static final String LISTENER_ID = "securetcp";
	private static final int SECURE_TCP_PORT = SecureModbusTcpOptions.DEFAULT_SECURE_MODBUS_TCP_PORT;
	private static final int SLAVE_ID = 1;

	private final IncomingLogEntryEventHandler incomingLogEntryEventHandler = new IncomingLogEntryEventHandler(this);
	private final PortStateEventHandler portStateEventHandler = new PortStateEventHandler(this);

	private _22_SecureTCP_Slave() {
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
		new _22_SecureTCP_Slave().run();
	}

	private void run() throws Exception {
		PLCcomConsole.open("PLCcom for Modbus - Workshop 22", 1000);
		System.out.println("╔════════════════════════════════════════════════════════════════════╗");
		System.out.println("║ PLCcom for Modbus - Workshop 22: SecureTCP Slave                   ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Starts a Secure Modbus TCP slave with a PKI store and a local      ║");
		System.out.println("║ certificate.                                                       ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ What you will learn:                                               ║");
		System.out.println("║   * Create or load the slave own certificate                       ║");
		System.out.println("║   * Override client certificate validation for lab testing         ║");
		System.out.println("║   * Serve normal Modbus data through a TLS listener                ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Used by Workshop 21 on SecureTCP port 802                          ║");
		System.out.println("╚════════════════════════════════════════════════════════════════════╝");
		System.out.println();

		ModbusSlave slave = null;
		try {
			SecureModbusTcpOptions options = createSecureTcpOptions();
			slave = new ModbusSlave(LICENSE_USER_NAME, LICENSE_SERIAL, SLAVE_ID, eRegisterMode._16Bit,
					eByteOrder.AB_CD);
			slave.addIncomingLogEntryEventHandler(incomingLogEntryEventHandler);
			slave.addPortStateEventHandler(portStateEventHandler);

			// Seed deterministic values before opening the listener. A master can
			// read them immediately after the TLS handshake succeeds.
			slave.setValue(createUshort(0), eModbusRegion.HoldingRegister, createUshort(700));
			slave.setValue(createUshort(1), eModbusRegion.HoldingRegister, createUshort(701));
			slave.setValue(createUshort(2), eModbusRegion.HoldingRegister, createUshort(702));
			slave.setValue(createUshort(3), eModbusRegion.HoldingRegister, createUshort(703));

			slave.addOrReplaceListener_SecureTCP(LISTENER_ID, SECURE_TCP_PORT, options);
			System.out.println("SecureTCP slave is listening on port " + SECURE_TCP_PORT + ".");
			System.out.println("Start Workshop 21 in another console to connect as master.");
			System.out.println("Press ENTER to stop.");
			PLCcomConsole.readLine("  Press ENTER to stop...");
		} catch (Exception ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
			PLCcomConsole.waitForEnter();
		} catch (Throwable ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
			PLCcomConsole.waitForEnter();
		} finally {
			if (slave != null) {
				slave.removeListener(LISTENER_ID);
				slave.removeIncomingLogEntryEventHandler(incomingLogEntryEventHandler);
				slave.removePortStateEventHandler(portStateEventHandler);
				slave.unload();
			}
		}
		PLCcomConsole.close();
	}

	private static SecureModbusTcpOptions createSecureTcpOptions() throws Exception {
		SecureModbusPkiStore pkiStore = new SecureModbusPkiStore("./pki/securetcp-slave");
		pkiStore.ensureDirectories();
		pkiStore.addCertificateStoredListener(new PkiTraceListener());
		printPkiStoreLayout(pkiStore);

		SecureModbusOwnCertificate ownCertificate = new SecureModbusOwnCertificate(pkiStore,
				"PLCcom Modbus Workshop Slave", "workshop-password", 3650, "Indi.An GmbH",
				Arrays.asList("localhost"), Arrays.asList(InetAddress.getByName("127.0.0.1")));

		SecureModbusTcpOptions options = new SecureModbusTcpOptions();
		options.setPkiStore(pkiStore);
		options.setOwnCertificate(ownCertificate);
		options.setTlsProtocolMode(SecureModbusTlsProtocolMode.Auto);

		// PKI default behavior is the same as on the master side: own contains
		// the local identity, trusted and issuers contain accepted client trust
		// material, and rejected receives unknown or invalid remote certificates.
		//
		// Workshop-only override:
		// This validator accepts every client certificate. While it is assigned,
		// PLCcom does not make the normal certificate-level PKI decision. Remove
		// this assignment to test the real trusted/rejected workflow.
		options.setRemoteCertificateValidator(new UnsafeAcceptAllCertificateValidator("SecureTCP client"));
		return options;
	}

	/**
	 * Reports a listener state change.
	 *
	 * @param arg
	 *            the new listener state supplied by PLCcom
	 */
	@Override
	public void On_ConnectionStateChange(eConnectionState arg) {
		System.out.println("Listener state: " + arg);
	}

	/**
	 * Reports diagnostic log entries emitted by the SecureTCP slave.
	 *
	 * @param arg
	 *            log entries raised by the slave for the current request flow
	 */
	@Override
	public void OnIncomingLogEntry(LogEntry[] arg) {
		for (LogEntry entry : arg) {
			System.out.println(entry);
		}
	}

	private static void printPkiStoreLayout(SecureModbusPkiStore pkiStore) {
		System.out.println("PKI store:");
		System.out.println("  own/certs      : " + pkiStore.getOwnCertificatesPath());
		System.out.println("  own/private    : " + pkiStore.getOwnPrivatePath());
		System.out.println("  trusted/certs  : " + pkiStore.getTrustedCertificatesPath());
		System.out.println("  issuers/certs  : " + pkiStore.getIssuerCertificatesPath());
		System.out.println("  rejected/certs : " + pkiStore.getRejectedCertificatesPath());
		System.out.println();
	}


	private static final class PkiTraceListener implements SecureModbusPkiCertificateStoredListener {
		/**
		 * Prints every certificate write or confirmation reported by the PKI store.
		 *
		 * @param eventArgs
		 *            information about the logical store folder and certificate file
		 */
		@Override
		public void certificateStored(SecureModbusPkiCertificateStoredEventArgs eventArgs) {
			System.out.println("PKI " + eventArgs.getStoreKind() + ": " + eventArgs.getFilePath());
		}
	}

	private static final class UnsafeAcceptAllCertificateValidator implements ISecureModbusCertificateValidator {
		private final String remoteEndpointName;

		private UnsafeAcceptAllCertificateValidator(String remoteEndpointName) {
			this.remoteEndpointName = remoteEndpointName;
		}

		/**
		 * Accepts every remote certificate so the workshop can focus on the
		 * SecureTCP listener lifecycle.
		 *
		 * @param context
		 *            certificate information supplied by the SecureTCP TLS layer
		 * @return always {@code true}; do not use this policy in production
		 */
		@Override
		public boolean validate(SecureModbusCertificateValidationContext context) {
			System.out.println("WARNING: accepting every " + remoteEndpointName + " certificate.");
			System.out.println("         PKI trust checks are bypassed by this workshop validator.");
			System.out.println("         Remote chain length: " + context.getCertificateChain().size());
			for (X509Certificate certificate : context.getCertificateChain()) {
				System.out.println("         " + certificate.getSubjectX500Principal());
			}
			return true;
		}
	}
}
