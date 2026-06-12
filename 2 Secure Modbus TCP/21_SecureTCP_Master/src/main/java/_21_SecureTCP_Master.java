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

import java.net.InetAddress;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import com.indian.plccom.modbus.ISecureModbusCertificateValidator;
import com.indian.plccom.modbus.ModbusMaster;
import com.indian.plccom.modbus.OperationResult;
import com.indian.plccom.modbus.ReadRequest;
import com.indian.plccom.modbus.ReadResult;
import com.indian.plccom.modbus.ReadValue;
import com.indian.plccom.modbus.RequestBuilder;
import com.indian.plccom.modbus.SecureModbusCertificateValidationContext;
import com.indian.plccom.modbus.SecureModbusOwnCertificate;
import com.indian.plccom.modbus.SecureModbusPkiCertificateStoredEventArgs;
import com.indian.plccom.modbus.SecureModbusPkiCertificateStoredListener;
import com.indian.plccom.modbus.SecureModbusPkiStore;
import com.indian.plccom.modbus.SecureModbusTcpOptions;
import com.indian.plccom.modbus.SecureModbusTlsProtocolMode;
import com.indian.plccom.modbus.Enums.eByteOrder;
import com.indian.plccom.modbus.Enums.eDataType;
import com.indian.plccom.modbus.Enums.eReadFunction;
import com.indian.plccom.modbus.Enums.eRegisterMode;

/**
 * Workshop 21 - SecureTCP Master.
 *
 * This workshop connects a PLCcom Modbus master to a Secure Modbus TCP slave.
 * SecureTCP keeps the normal Modbus request API, but wraps the transport in TLS
 * and uses certificates for endpoint identity.
 */
public final class _21_SecureTCP_Master {
	private static final String LICENSE_USER_NAME = "<Enter your UserName here>";
	private static final String LICENSE_SERIAL = "<Enter your Serial here>";
	private static final String SLAVE_HOST = "127.0.0.1";
	private static final String EXPECTED_CERTIFICATE_HOST_NAME = "localhost";
	private static final int SECURE_TCP_PORT = SecureModbusTcpOptions.DEFAULT_SECURE_MODBUS_TCP_PORT;
	private static final int SLAVE_ID = 1;

	private _21_SecureTCP_Master() {
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
		PLCcomConsole.open("PLCcom for Modbus - Workshop 21", 1000);
		System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
		System.out.println("║ PLCcom for Modbus - Workshop 21: SecureTCP Master                  ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Connects through Secure Modbus TCP with a local certificate, a PKI ║");
		System.out.println("║ store, and a workshop certificate validator.                       ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ What you will learn:                                               ║");
		System.out.println("║   * Configure the SecureTCP connector                              ║");
		System.out.println("║   * See the PKI folder layout used by PLCcom                       ║");
		System.out.println("║   * Understand how a custom validator bypasses PKI validation      ║");
		System.out.println("║                                                                    ║");
		System.out.println("║ Required peer: Workshop 22 on 127.0.0.1:802                        ║");
		System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
		System.out.println();

		ModbusMaster master = null;
		try {
			SecureModbusTcpOptions options = createSecureTcpOptions();
			master = new ModbusMaster(LICENSE_USER_NAME, LICENSE_SERIAL);

			// Step 1: SecureTCP is selected on the connector. From the request
			// builder's point of view the Modbus telegram remains a normal F03
			// read request.
			master.setConnector_SecureTCP(SLAVE_HOST, SECURE_TCP_PORT, options);
			master.setRegisterMode(eRegisterMode._16Bit);
			master.getConnector().setMaxIdleTime(5000);

			ReadRequest readRequest = RequestBuilder.ReadRequestBuilder.create(SLAVE_ID,
					eReadFunction.F03_Read_Holding_Registers, 0, eDataType.USHORT, 4);
			readRequest.setByteOrder(eByteOrder.AB_CD);
			printReadResult("SecureTCP read", master.read(readRequest));
		} catch (Exception ex) {
			System.err.println("Workshop failed: " + ex.getMessage());
			System.err.println("Start Workshop 22 first, or review rejected certificates in the PKI store.");
		} finally {
			if (master != null) {
				master.unload();
			}
		}
		System.out.println();
		PLCcomConsole.waitForEnter();
		PLCcomConsole.close();
	}

	private static SecureModbusTcpOptions createSecureTcpOptions() throws Exception {
		SecureModbusPkiStore pkiStore = new SecureModbusPkiStore("./pki/securetcp-master");
		pkiStore.ensureDirectories();
		pkiStore.addCertificateStoredListener(new PkiTraceListener());
		printPkiStoreLayout(pkiStore);

		SecureModbusOwnCertificate ownCertificate = new SecureModbusOwnCertificate(pkiStore,
				"PLCcom Modbus Workshop Master", "workshop-password", 3650, "Indi.An GmbH",
				Arrays.asList("localhost"), Arrays.asList(InetAddress.getByName("127.0.0.1")));

		SecureModbusTcpOptions options = new SecureModbusTcpOptions();
		options.setPkiStore(pkiStore);
		options.setOwnCertificate(ownCertificate);
		options.setTargetHostName(EXPECTED_CERTIFICATE_HOST_NAME);
		options.setTlsProtocolMode(SecureModbusTlsProtocolMode.Auto);

		// Default PKI behavior when no custom validator is configured:
		// own/certs and own/private contain this master's local certificate and
		// private key. trusted/certs contains remote certificates that are trusted
		// directly. issuers/certs contains trusted issuing CA certificates. Unknown
		// or invalid remote certificates are written to rejected/certs and the
		// connection fails so an operator can review them.
		//
		// Workshop-only override:
		// Setting a RemoteCertificateValidator replaces PLCcom's default
		// certificate-level PKI validation. Because this validator returns true,
		// the PKI trust decision is intentionally bypassed for teaching purposes.
		options.setRemoteCertificateValidator(new UnsafeAcceptAllCertificateValidator("SecureTCP slave"));
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
		 * SecureTCP handshake and request flow.
		 *
		 * @param context
		 *            certificate information supplied by the SecureTCP TLS layer
		 * @return always {@code true}; do not use this policy in production
		 */
		@Override
		public boolean validate(SecureModbusCertificateValidationContext context) {
			System.out.println("WARNING: accepting every " + remoteEndpointName + " certificate.");
			System.out.println("         PKI trust checks are bypassed by this workshop validator.");
			System.out.println("         Expected host name: " + context.getHostName());
			System.out.println("         Remote chain length: " + context.getCertificateChain().size());
			for (X509Certificate certificate : context.getCertificateChain()) {
				System.out.println("         " + certificate.getSubjectX500Principal());
			}
			return true;
		}
	}
}
