package example_app;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import com.indian.plccom.modbus.ISecureModbusAuthorizationValidator;
import com.indian.plccom.modbus.SecureModbusAuthorizationContext;
import com.indian.plccom.modbus.SecureModbusOwnCertificate;
import com.indian.plccom.modbus.SecureModbusPkiStore;
import com.indian.plccom.modbus.SecureModbusTcpOptions;

final class SecureTcpExampleOptions {
	private static final String DEFAULT_OWN_CERTIFICATE_ALIAS = "PLCcom Modbus Slave TestApp";
	private static final String DEFAULT_OWN_CERTIFICATE_PASSWORD = "PLCcomModbusSecureTcpTestApp";
	private static final int DEFAULT_OWN_CERTIFICATE_VALIDITY_DAYS = 3650;
	private static final String DEFAULT_APPLICATION_FOLDER = "ModbusSlave";

	private SecureTcpExampleOptions() {
	}

	static SecureModbusTcpOptions create() throws Exception {
		SecureModbusTcpOptions options = new SecureModbusTcpOptions();

		String certificatePath = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_CERT_PATH",
				"PLCCOM_MODBUS_SECURETCP_CERT");
		String certificatePassword = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_CERT_PASSWORD",
				"PLCCOM_MODBUS_SECURETCP_PASSWORD");
		String trustedCertificatePaths = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_TRUSTED_CERT_PATHS",
				"PLCCOM_MODBUS_SECURETCP_TRUSTED_CERT");

		options.setAcceptAllRemoteCertificates(getEnvironmentBool(
				"PLCCOM_MODBUS_SECURETCP_ACCEPT_ALL_REMOTE_CERTIFICATES",
				options.getAcceptAllRemoteCertificates()));
		options.setRequireLocalCertificate(getEnvironmentBool("PLCCOM_MODBUS_SECURETCP_REQUIRE_LOCAL_CERTIFICATE",
				options.getRequireLocalCertificate()));
		options.setRequireRemoteCertificate(getEnvironmentBool("PLCCOM_MODBUS_SECURETCP_REQUIRE_REMOTE_CERTIFICATE",
				options.getRequireRemoteCertificate()));
		options.setValidateRemoteCertificateHostName(getEnvironmentBool("PLCCOM_MODBUS_SECURETCP_VALIDATE_HOSTNAME",
				options.getValidateRemoteCertificateHostName()));
		options.setTargetHostName(getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_TARGET_HOST",
				"PLCCOM_MODBUS_SECURETCP_TARGET_HOSTNAME"));

		SecureModbusPkiStore pkiStore = createPkiStore();
		options.setPkiStore(pkiStore);

		boolean localIdentityConfigured = false;
		if (hasText(certificatePath)) {
			configureLocalCertificate(options, certificatePath, certificatePassword);
			localIdentityConfigured = true;
		} else if (options.getRequireLocalCertificate()) {
			options.setOwnCertificate(createOwnCertificate(pkiStore));
			localIdentityConfigured = true;
		}

		addTrustedCertificates(options, trustedCertificatePaths);
		configureAuthorizationValidator(options);

		if (options.getRequireLocalCertificate() && !localIdentityConfigured) {
			throw new IllegalStateException(
					"SecureTCP requires a local certificate. Set PLCCOM_MODBUS_SECURETCP_CERT_PATH to a PFX/P12 file, "
							+ "or set PLCCOM_MODBUS_SECURETCP_CERT_PATH together with PLCCOM_MODBUS_SECURETCP_KEY_PATH. "
							+ "Alternatively set PLCCOM_MODBUS_SECURETCP_PKI_PATH and let the workshop create an own certificate.");
		}

		return options;
	}

	static SecureModbusPkiStore createPkiStore() {
		SecureModbusPkiStore pkiStore = new SecureModbusPkiStore(getPkiBasePath());
		pkiStore.ensureDirectories();
		return pkiStore;
	}

	static String getPkiBasePath() {
		String configuredPath = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_PKI_PATH",
				"PLCCOM_MODBUS_SECURETCP_PKI_BASE");
		if (hasText(configuredPath)) {
			return configuredPath;
		}

		String localAppData = System.getenv("LOCALAPPDATA");
		File localFolder = hasText(localAppData) ? new File(localAppData)
				: new File(new File(System.getProperty("user.home")), "AppData\\Local");
		return new File(new File(new File(localFolder, "PLCcom"), DEFAULT_APPLICATION_FOLDER), "SecureTCP\\pki")
				.getAbsolutePath();
	}

	static X509Certificate ensureOwnCertificate() throws Exception {
		SecureModbusOwnCertificate ownCertificate = createOwnCertificate(createPkiStore());
		return firstCertificate(ownCertificate.loadOrCreate());
	}

	private static SecureModbusOwnCertificate createOwnCertificate(SecureModbusPkiStore pkiStore) throws Exception {
		String alias = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_OWN_ALIAS");
		if (!hasText(alias)) {
			alias = DEFAULT_OWN_CERTIFICATE_ALIAS;
		}

		String password = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_OWN_PASSWORD");
		if (!hasText(password)) {
			password = DEFAULT_OWN_CERTIFICATE_PASSWORD;
		}

		String validityText = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_OWN_VALIDITY_DAYS");
		int validityDays = hasText(validityText) ? Integer.parseInt(validityText)
				: DEFAULT_OWN_CERTIFICATE_VALIDITY_DAYS;

		SecureModbusOwnCertificate ownCertificate = new SecureModbusOwnCertificate(pkiStore, alias, password,
				validityDays);
		ownCertificate.getDnsNames().add("localhost");
		try {
			String hostName = InetAddress.getLocalHost().getHostName();
			if (hasText(hostName)) {
				ownCertificate.getDnsNames().add(hostName);
			}
		} catch (Exception ex) {
			// A missing local host name should not prevent the workshop certificate.
		}
		ownCertificate.getIpAddresses().add(InetAddress.getByName("127.0.0.1"));
		ownCertificate.getIpAddresses().add(InetAddress.getByName("::1"));
		addDnsNames(ownCertificate, getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_OWN_DNS_NAMES"));
		addIpAddresses(ownCertificate, getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_OWN_IP_ADDRESSES"));
		ownCertificate.setRoleName(getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_OWN_ROLE",
				"PLCCOM_MODBUS_SECURETCP_ROLE"));
		return ownCertificate;
	}

	private static void configureAuthorizationValidator(SecureModbusTcpOptions options) {
		String requiredRole = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_REQUIRED_ROLE",
				"PLCCOM_MODBUS_SECURETCP_ALLOWED_ROLE");
		boolean allowNullRole = getEnvironmentBool("PLCCOM_MODBUS_SECURETCP_ALLOW_NULL_ROLE", false);
		if (!hasText(requiredRole) && !allowNullRole) {
			return;
		}

		options.setAuthorizationValidator(new RequiredRoleAuthorizationValidator(requiredRole, allowNullRole));
	}

	private static void configureLocalCertificate(SecureModbusTcpOptions options, String certificatePath,
			String certificatePassword) throws Exception {
		File certificateFile = new File(certificatePath.trim());
		if (!certificateFile.isFile()) {
			throw new IllegalArgumentException("SecureTCP certificate file was not found: " + certificatePath);
		}

		if (isPkcs12File(certificateFile)) {
			char[] password = toPassword(certificatePassword);
			KeyStore keyStore = loadPkcs12(certificateFile, password);
			if (!hasPrivateKey(keyStore)) {
				throw new IllegalStateException("The SecureTCP local certificate must contain a private key.");
			}
			options.setLocalKeyStore(keyStore, password,
					getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_CERT_ALIAS",
							"PLCCOM_MODBUS_SECURETCP_KEY_ALIAS"));
			return;
		}

		String keyPath = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_KEY_PATH",
				"PLCCOM_MODBUS_SECURETCP_PRIVATE_KEY_PATH");
		if (!hasText(keyPath)) {
			throw new IllegalStateException(
					"PEM/DER local certificates require PLCCOM_MODBUS_SECURETCP_KEY_PATH for the private key.");
		}
		File keyFile = new File(keyPath.trim());
		if (!keyFile.isFile()) {
			throw new IllegalArgumentException("SecureTCP private key file was not found: " + keyPath);
		}

		String keyPassword = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_KEY_PASSWORD",
				"PLCCOM_MODBUS_SECURETCP_PRIVATE_KEY_PASSWORD");
		if (!hasText(keyPassword)) {
			keyPassword = certificatePassword;
		}
		options.setLocalCertificateFiles(certificateFile.getAbsolutePath(), keyFile.getAbsolutePath(), toPassword(keyPassword));
	}

	private static void addDnsNames(SecureModbusOwnCertificate ownCertificate, String dnsNames) {
		if (!hasText(dnsNames)) {
			return;
		}
		for (String dnsName : dnsNames.split(";")) {
			if (hasText(dnsName)) {
				ownCertificate.getDnsNames().add(dnsName.trim());
			}
		}
	}

	private static void addIpAddresses(SecureModbusOwnCertificate ownCertificate, String ipAddresses) throws Exception {
		if (!hasText(ipAddresses)) {
			return;
		}
		for (String ipAddress : ipAddresses.split(";")) {
			if (hasText(ipAddress)) {
				ownCertificate.getIpAddresses().add(InetAddress.getByName(ipAddress.trim()));
			}
		}
	}

	private static void addTrustedCertificates(SecureModbusTcpOptions options, String trustedCertificatePaths)
			throws Exception {
		if (!hasText(trustedCertificatePaths)) {
			return;
		}
		String trustedPassword = getEnvironmentValue("PLCCOM_MODBUS_SECURETCP_TRUSTED_CERT_PASSWORD");
		for (String certificatePath : trustedCertificatePaths.split(";")) {
			if (!hasText(certificatePath)) {
				continue;
			}
			for (X509Certificate certificate : loadCertificates(new File(certificatePath.trim()),
					toPassword(trustedPassword))) {
				options.addTrustedRemoteCertificate(certificate);
			}
		}
	}

	static List<X509Certificate> loadCertificates(File file, char[] password) throws Exception {
		if (!file.isFile()) {
			throw new IllegalArgumentException("SecureTCP certificate file was not found: " + file.getAbsolutePath());
		}
		if (isPkcs12File(file)) {
			KeyStore keyStore = loadPkcs12(file, password);
			ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
			Enumeration<String> aliases = keyStore.aliases();
			while (aliases.hasMoreElements()) {
				java.security.cert.Certificate certificate = keyStore.getCertificate(aliases.nextElement());
				if (certificate instanceof X509Certificate) {
					certificates.add((X509Certificate) certificate);
				}
			}
			return certificates;
		}

		FileInputStream stream = new FileInputStream(file);
		try {
			byte[] bytes = readAllBytes(stream);
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			Collection<? extends java.security.cert.Certificate> parsed = factory
					.generateCertificates(new ByteArrayInputStream(bytes));
			ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
			for (java.security.cert.Certificate certificate : parsed) {
				if (certificate instanceof X509Certificate) {
					certificates.add((X509Certificate) certificate);
				}
			}
			return certificates;
		} finally {
			stream.close();
		}
	}

	static X509Certificate firstCertificate(KeyStore keyStore) throws Exception {
		Enumeration<String> aliases = keyStore.aliases();
		while (aliases.hasMoreElements()) {
			java.security.cert.Certificate certificate = keyStore.getCertificate(aliases.nextElement());
			if (certificate instanceof X509Certificate) {
				return (X509Certificate) certificate;
			}
		}
		return null;
	}

	private static KeyStore loadPkcs12(File file, char[] password) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream stream = new FileInputStream(file);
		try {
			keyStore.load(stream, password);
			return keyStore;
		} finally {
			stream.close();
		}
	}

	private static boolean hasPrivateKey(KeyStore keyStore) throws Exception {
		Enumeration<String> aliases = keyStore.aliases();
		while (aliases.hasMoreElements()) {
			if (keyStore.isKeyEntry(aliases.nextElement())) {
				return true;
			}
		}
		return false;
	}

	private static boolean isPkcs12File(File file) {
		String name = file.getName().toLowerCase();
		return name.endsWith(".pfx") || name.endsWith(".p12") || name.endsWith(".pkcs12");
	}

	private static byte[] readAllBytes(FileInputStream stream) throws Exception {
		byte[] buffer = new byte[8192];
		java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
		int read;
		while ((read = stream.read(buffer)) >= 0) {
			output.write(buffer, 0, read);
		}
		return output.toByteArray();
	}

	private static char[] toPassword(String value) {
		return value == null ? new char[0] : value.toCharArray();
	}

	private static String getEnvironmentValue(String... names) {
		for (String name : names) {
			String value = System.getenv(name);
			if (hasText(value)) {
				return value;
			}
		}
		return null;
	}

	private static boolean getEnvironmentBool(String name, boolean defaultValue) {
		String value = System.getenv(name);
		if (!hasText(value)) {
			return defaultValue;
		}
		return "1".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)
				|| "on".equalsIgnoreCase(value);
	}

	private static boolean hasText(String value) {
		return value != null && value.trim().length() > 0;
	}

	private static final class RequiredRoleAuthorizationValidator implements ISecureModbusAuthorizationValidator {
		private final String requiredRole;
		private final boolean allowNullRole;

		RequiredRoleAuthorizationValidator(String requiredRole, boolean allowNullRole) {
			this.requiredRole = requiredRole;
			this.allowNullRole = allowNullRole;
		}

		@Override
		public boolean validate(SecureModbusAuthorizationContext context) {
			if (context == null) {
				return false;
			}
			if (context.getIsNullRole()) {
				return allowNullRole;
			}
			return hasText(requiredRole) && requiredRole.equals(context.getRoleName());
		}
	}
}
