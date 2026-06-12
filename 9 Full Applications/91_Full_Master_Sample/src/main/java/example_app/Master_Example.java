package example_app;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.indian.plccom.modbus.*;
import com.indian.plccom.modbus.Enums.*;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JTextPane;

import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.JComboBox;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Insets;

import javax.swing.ImageIcon;

import jssc.SerialPortList;
import example_app.DisabledJPanel.DisabledJPanel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

/**
 * Full PLCcom for Modbus master workshop application.
 * <p>
 * The frame demonstrates all major master-side workflows of the SDK, including
 * classic serial/TCP transports, SecureTCP, request configuration dialogs, and
 * connection-state feedback. It is intentionally kept as an executable Swing
 * sample rather than a reusable API type.
 * </p>
 */
public class Master_Example extends JFrame implements iConnectionStateChangeEvent {

	private ModbusMaster Device = null;
	private ResourceBundle resources = ResourceBundle.getBundle("example_app.resources.resources");
	static int CountOpenDialogs = 0;

	private PortStateEventHandler portStateEventHandler = new PortStateEventHandler(this);

	private static final long serialVersionUID = -2325402036455831449L;
	private JPanel contentPane;
	private JTextField txtSerial;
	private JTextField txtUser;
	private DisabledJPanel panAddress;
	private DisabledJPanel panIPSettings;
	private DisabledJPanel panSerialSettings;
	private DisabledJPanel panFunctions;
	private JTextPane txtWarning;
	private JPanel grbSerial;
	private JPanel grbAddress;
	private JButton btnEditConnectionSettings;
	private JButton btnSaveConnectionSettings;
	private JLabel lblConnectionType;
	private JLabel lblRegisterMode;
	private JTextPane lblmaxIdleTime;
	private JComboBox<eTypeOfCommunication> cmbConnectionType;
	private JComboBox<eRegisterMode> cmbRegisterMode;
	private JTextField txtIdleTime;
	private JLabel lblMs;
	private JPanel grbIPSettings;
	private JPanel grbSerialSettings;
	private JTextField txtAddressIP;
	private JTextField txtRemotePort;
	private JTextField txtLocalPort;
	private JLabel lblAddress0;
	private JLabel lblAddress1;
	private JLabel lblAddress2;
	private JComboBox<String> cmbSerialPort;
	private JComboBox<eBaudrate> cmbBaudrate;
	private JComboBox<eParity> cmbParity;
	private JComboBox<eDataBits> cmbDataBits;
	private JComboBox<eStopBits> cmbStopbits;
	private JComboBox<eFlowControl> cmbFlowControl;
	private JLabel lblSerialPort;
	private JLabel lblBaudrate;
	private JLabel lblParity;
	private JLabel lblDataBits;
	private JLabel lblStopBits;
	private JLabel lblFlowControl;
	private JPanel panel;
	private JPanel grbFunctions;
	private JButton btnReadWriteModbus;
	private JButton btnReadCollection;
	private JButton btnOtherFunctions;
	private JButton btnClose;
	private JButton btnCertificateManager;
	private Color certificateManagerDefaultBackColor;
	private javax.swing.Timer certificateAlertTimer;
	private boolean certificateAlertBlinkState;
	private boolean certificateManagerAutoOpened;
	private boolean certificateManagerOpen;
	private JPanel statusBar = new JPanel();
	private JTextField lblDeviceGUID;
	private JTextField lblDeviceState;
	private JTextField lblDeviceType;
	private JLabel lblLanguage;
	private JComboBox<String> cmbLanguage;
	private boolean languageSelectorInitializing;
	private JLabel lblSerialCode;
	private JTextField txtOperationTimeout;
	private JCheckBox chkCRCCheck;
	private JLabel lblOperationTimeout;

	/**
	 * Launches the master workshop application.
	 *
	 * @param args
	 *            command-line arguments; currently ignored
	 */
	public static void main(String[] args) {
		WorkshopLogging.configure();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Master_Example frame = new Master_Example();
					frame.setVisible(true);
				} catch (Exception e) {					
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Master_Example() {
		setTitle(ResourceBundle.getBundle("example_app.resources.resources").getString("main_Text"));
		initialize();
		initializeCertificateAlert();
	}

	/**
	 * Create the frame.
	 */
	private void initialize() {

		// set global lock and feel platform independent
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

			@SuppressWarnings("rawtypes")
			java.util.Enumeration keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
					UIManager.put(key, new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 11));
				}
			}
			WorkshopUi.installScaledCheckBoxIcon();

			// UIManager.setLookAndFeel(
			// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}

		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(Master_Example.class.getResource("/example_app/resources/node.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 747, 729);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtWarning = new JTextPane();
		txtWarning.setBackground(SystemColor.info);
		txtWarning.setEditable(false);

		txtWarning.setText(ResourceBundle.getBundle("example_app.resources.resources").getString("txtWarning_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		txtWarning.setBounds(12, 12, 713, 204);

		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
		txtWarning.selectAll();
		txtWarning.setParagraphAttributes(attributeSet, false);
		txtWarning.select(0, 0);
		contentPane.setLayout(null);
		JScrollPane scrollPanelvLog = new JScrollPane(txtWarning);
		JPanel lvLogContainer = new JPanel();
		lvLogContainer.setBounds(12, 12, 713, 204);
		lvLogContainer.setLayout(new BorderLayout());
		lvLogContainer.add(scrollPanelvLog);
		contentPane.add(lvLogContainer);

		grbSerial = new JPanel();
		grbSerial.setBounds(12, 222, 710, 64);
		grbSerial.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "serial", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPane.add(grbSerial);
		grbSerial.setLayout(null);

		lblSerialCode = new JLabel("enter serialcode first     >>>>>>>>>");
		lblSerialCode.setFont(new Font("Arial", Font.BOLD, 13));
		lblSerialCode.setBounds(6, 26, 250, 16);
		grbSerial.add(lblSerialCode);

		JLabel lblUser = new JLabel("user:");
		lblUser.setBounds(328, 15, 46, 14);
		grbSerial.add(lblUser);

		JLabel lblSerial = new JLabel("serial:");
		lblSerial.setBounds(328, 41, 46, 14);
		grbSerial.add(lblSerial);

		txtSerial = new JTextField();
		txtSerial.setBounds(370, 38, 326, 20);
		txtSerial.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtSerial_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtSerial_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtSerial_TextChanged(e);
			}
		});
		grbSerial.add(txtSerial);

		txtUser = new JTextField();
		txtUser.setBounds(370, 12, 326, 20);
		txtUser.getDocument().addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				txtUser_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtUser_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtUser_TextChanged(e);
			}
		});
		grbSerial.add(txtUser);

		btnEditConnectionSettings = new JButton(ResourceBundle.getBundle("example_app.resources.resources")
				.getString("btnEditConnectionSettings_Text"));
		btnEditConnectionSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnEditConnectionSettings_actionPerformed(e);
			}
		});
		btnEditConnectionSettings.setBounds(27, 288, 68, 68);
		btnEditConnectionSettings
				.setIcon(new ImageIcon(Master_Example.class.getResource("/example_app/resources/pencil.png")));
		btnEditConnectionSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnEditConnectionSettings
				.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnEditConnectionSettings.setMargin(new Insets(0, 0, 0, 0));
		btnEditConnectionSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		contentPane.add(btnEditConnectionSettings);

		grbAddress = new JPanel();
		grbAddress.setBounds(12, 361, 713, 207);
		grbAddress.setLayout(null);
		grbAddress.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(grbAddress);

		panAddress = new DisabledJPanel(grbAddress);
		panAddress.setBounds(12, 361, 713, 207);
		panAddress.setDisabledColor(new Color(240, 240, 240, 100));
		panAddress.setEnabled(false);
		contentPane.add(panAddress);

		btnSaveConnectionSettings = new JButton(ResourceBundle.getBundle("example_app.resources.resources")
				.getString("btnSaveConnectionSettings_Text"));
		btnSaveConnectionSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				btnSaveConnectionSettings_actionPerformed(arg);
			}
		});
		btnSaveConnectionSettings
				.setIcon(new ImageIcon(Master_Example.class.getResource("/example_app/resources/save_as.png")));
		btnSaveConnectionSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveConnectionSettings
				.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnSaveConnectionSettings.setMargin(new Insets(0, 0, 0, 0));
		btnSaveConnectionSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveConnectionSettings.setBounds(14, 131, 68, 68);
		grbAddress.add(btnSaveConnectionSettings);

		lblConnectionType = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblConnectionType_Text"));
		lblConnectionType.setHorizontalAlignment(SwingConstants.TRAILING);
		lblConnectionType.setBounds(2, 23, 105, 18);
		grbAddress.add(lblConnectionType);

		lblRegisterMode = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblRegisterMode_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblRegisterMode.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRegisterMode.setBounds(0, 52, 105, 18);
		grbAddress.add(lblRegisterMode);

		lblmaxIdleTime = new JTextPane();
		lblmaxIdleTime.setBackground(SystemColor.control);
		lblmaxIdleTime.setEditable(false);
		lblmaxIdleTime
				.setText(ResourceBundle.getBundle("example_app.resources.resources").getString("lblmaxIdleTime_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblmaxIdleTime.setBounds(8, 72, 98, 36);
		SimpleAttributeSet attributeSetmaxIdleTime = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributeSetmaxIdleTime, StyleConstants.ALIGN_RIGHT);
		lblmaxIdleTime.selectAll();
		lblmaxIdleTime.setParagraphAttributes(attributeSetmaxIdleTime, false);
		lblmaxIdleTime.select(0, 0);
		grbAddress.add(lblmaxIdleTime);

		cmbConnectionType = new JComboBox<eTypeOfCommunication>();
		cmbConnectionType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbConnectionType_actionPerformed(e);
			}
		});
		cmbConnectionType.setBounds(108, 23, 112, 21);
		grbAddress.add(cmbConnectionType);

		cmbRegisterMode = new JComboBox<eRegisterMode>();
		cmbRegisterMode.setBounds(108, 50, 112, 21);
		grbAddress.add(cmbRegisterMode);

		txtIdleTime = new JTextField();
		txtIdleTime.setText("3000");
		txtIdleTime.setBounds(108, 77, 112, 20);
		grbAddress.add(txtIdleTime);

		lblMs = new JLabel("ms");
		lblMs.setBounds(226, 81, 20, 13);
		grbAddress.add(lblMs);

		grbIPSettings = new JPanel();
		grbIPSettings.setLayout(null);
		grbIPSettings.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbIPSettings.setBounds(246, 13, 250, 188);
		grbAddress.add(grbIPSettings);

		panIPSettings = new DisabledJPanel(grbIPSettings);
		panIPSettings.setLocation(grbIPSettings.getLocation());
		panIPSettings.setSize(grbIPSettings.getSize());
		panIPSettings.setDisabledColor(new Color(240, 240, 240, 100));
		panIPSettings.setEnabled(true);
		grbAddress.add(panIPSettings);

		txtAddressIP = new JTextField();
		txtAddressIP.setText("127.0.0.1");
		txtAddressIP.setBounds(124, 16, 112, 20);
		grbIPSettings.add(txtAddressIP);

		txtRemotePort = new JTextField();
		txtRemotePort.setText("502");
		txtRemotePort.setBounds(124, 45, 112, 20);
		grbIPSettings.add(txtRemotePort);

		txtLocalPort = new JTextField();
		txtLocalPort.setText("0");
		txtLocalPort.setBounds(124, 75, 112, 20);
		grbIPSettings.add(txtLocalPort);

		lblAddress0 = new JLabel("IP/HostAddress");
		lblAddress0.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAddress0.setBounds(7, 18, 111, 13);
		grbIPSettings.add(lblAddress0);

		lblAddress1 = new JLabel("Slave Port (502)");
		lblAddress1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAddress1.setBounds(7, 51, 111, 13);
		grbIPSettings.add(lblAddress1);

		lblAddress2 = new JLabel("Master Port (auto=0)");
		lblAddress2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAddress2.setBounds(7, 78, 111, 13);
		grbIPSettings.add(lblAddress2);
		SimpleAttributeSet attributeSetNetworkAddressFamily = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributeSetNetworkAddressFamily, StyleConstants.ALIGN_RIGHT);

		grbSerialSettings = new JPanel();
		grbSerialSettings.setLayout(null);
		grbSerialSettings.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbSerialSettings.setBounds(502, 13, 205, 188);
		grbAddress.add(grbSerialSettings);

		panSerialSettings = new DisabledJPanel(grbSerialSettings);
		panSerialSettings.setLocation(grbSerialSettings.getLocation());
		panSerialSettings.setSize(grbSerialSettings.getSize());
		panSerialSettings.setDisabledColor(new Color(240, 240, 240, 100));
		panSerialSettings.setEnabled(true);
		grbAddress.add(panSerialSettings);

		cmbSerialPort = new JComboBox<String>();
		cmbSerialPort.setBounds(73, 15, 112, 21);
		grbSerialSettings.add(cmbSerialPort);

		cmbBaudrate = new JComboBox<eBaudrate>();
		cmbBaudrate.setBounds(73, 44, 112, 21);
		grbSerialSettings.add(cmbBaudrate);

		cmbParity = new JComboBox<eParity>();
		cmbParity.setBounds(73, 72, 112, 21);
		grbSerialSettings.add(cmbParity);

		cmbDataBits = new JComboBox<eDataBits>();
		cmbDataBits.setBounds(73, 99, 112, 21);
		grbSerialSettings.add(cmbDataBits);

		cmbStopbits = new JComboBox<eStopBits>();
		cmbStopbits.setBounds(73, 126, 112, 21);
		grbSerialSettings.add(cmbStopbits);

		cmbFlowControl = new JComboBox<eFlowControl>();
		cmbFlowControl.setBounds(73, 153, 112, 21);
		grbSerialSettings.add(cmbFlowControl);

		lblSerialPort = new JLabel("serial port");
		lblSerialPort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSerialPort.setBounds(2, 18, 61, 13);
		grbSerialSettings.add(lblSerialPort);

		lblBaudrate = new JLabel("baudrate");
		lblBaudrate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBaudrate.setBounds(2, 48, 61, 13);
		grbSerialSettings.add(lblBaudrate);

		lblParity = new JLabel("parity");
		lblParity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblParity.setBounds(2, 75, 61, 13);
		grbSerialSettings.add(lblParity);

		lblDataBits = new JLabel("data bits");
		lblDataBits.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDataBits.setBounds(2, 102, 61, 13);
		grbSerialSettings.add(lblDataBits);

		lblStopBits = new JLabel("stop bits");
		lblStopBits.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStopBits.setBounds(2, 129, 61, 13);
		grbSerialSettings.add(lblStopBits);

		lblFlowControl = new JLabel("flowcontrol");
		lblFlowControl.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFlowControl.setBounds(2, 156, 61, 13);
		grbSerialSettings.add(lblFlowControl);

		txtOperationTimeout = new JTextField();
		txtOperationTimeout.setText("2000");
		txtOperationTimeout.setBounds(108, 104, 112, 20);
		grbAddress.add(txtOperationTimeout);

		lblOperationTimeout = new JLabel("Operation Timeout"); 
		lblOperationTimeout.setHorizontalAlignment(SwingConstants.TRAILING);
		lblOperationTimeout.setBounds(0, 105, 105, 18);
		grbAddress.add(lblOperationTimeout);

		chkCRCCheck = new JCheckBox(formatCrcCheckText());
		chkCRCCheck.setSelected(true);
		chkCRCCheck.setBackground(SystemColor.menu);
		chkCRCCheck.setHorizontalTextPosition(SwingConstants.LEFT);
		chkCRCCheck.setIconTextGap(12);
		chkCRCCheck.setMargin(new Insets(0, 0, 0, 0));
		chkCRCCheck.setOpaque(false);
		chkCRCCheck.setBounds(116, 161, 132, 44);
		grbAddress.add(chkCRCCheck);

		panel = new JPanel();
		panel.setBounds(12, 570, 713, 87);
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel);

		grbFunctions = new JPanel();
		grbFunctions.setLayout(null);
		grbFunctions.setBorder(null);
		grbFunctions.setBounds(10, 9, 248, 75);
		panel.add(grbFunctions);

		panFunctions = new DisabledJPanel(grbFunctions);
		panFunctions.setLocation(grbFunctions.getLocation());
		panFunctions.setSize(grbFunctions.getSize());
		panFunctions.setDisabledColor(new Color(240, 240, 240, 100));
		panFunctions.setEnabled(true);
		panel.add(panFunctions);

		btnReadWriteModbus = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnReadWriteModbus_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnReadWriteModbus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReadWriteModbus_actionPerformed(e);
			}
		});
		btnReadWriteModbus
				.setIcon(new ImageIcon(Master_Example.class.getResource("/example_app/resources/registry.png")));
		btnReadWriteModbus.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnReadWriteModbus.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnReadWriteModbus.setMargin(new Insets(0, 0, 0, 0));
		btnReadWriteModbus.setHorizontalTextPosition(SwingConstants.CENTER);
		btnReadWriteModbus.setBounds(6, 3, 68, 68);
		grbFunctions.add(btnReadWriteModbus);

		btnReadCollection = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnReadCollection_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnReadCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReadCollection_actionPerformed(e);
			}
		});
		btnReadCollection
				.setIcon(new ImageIcon(Master_Example.class.getResource("/example_app/resources/compress_green.png")));
		btnReadCollection.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnReadCollection.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnReadCollection.setMargin(new Insets(0, 0, 0, 0));
		btnReadCollection.setHorizontalTextPosition(SwingConstants.CENTER);
		btnReadCollection.setBounds(90, 3, 68, 68);
		grbFunctions.add(btnReadCollection);

		btnOtherFunctions = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnOtherFunctions_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnOtherFunctions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnOtherFunctions_actionPerformed(e);
			}
		});
		btnOtherFunctions.setIcon(new ImageIcon(Master_Example.class.getResource("/example_app/resources/gears.png")));
		btnOtherFunctions.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnOtherFunctions.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnOtherFunctions.setMargin(new Insets(0, 0, 0, 0));
		btnOtherFunctions.setHorizontalTextPosition(SwingConstants.CENTER);
		btnOtherFunctions.setBounds(172, 3, 68, 68);
		grbFunctions.add(btnOtherFunctions);

		btnCertificateManager = new JButton(resources.getString("btnCertificateManager_Text"));
		btnCertificateManager.setIcon(CertificateManagerDialog.createShieldIcon(32));
		btnCertificateManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openCertificateManager();
			}
		});
		btnCertificateManager.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnCertificateManager.setMargin(new Insets(0, 0, 0, 0));
		btnCertificateManager.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCertificateManager.setBounds(544, 12, 68, 68);
		btnCertificateManager.setToolTipText(resources.getString("btnCertificateManager_ToolTip_Text"));
		panel.add(btnCertificateManager);

		btnClose = new JButton(ResourceBundle.getBundle("example_app.resources.resources").getString("btnClose_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(new ImageIcon(Master_Example.class.getResource("/example_app/resources/exit.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(626, 12, 68, 68);
		panel.add(btnClose);

		lblLanguage = new JLabel(resources.getString("lblLanguage_Text"));
		lblLanguage.setBounds(295, 32, 78, 14);
		panel.add(lblLanguage);

		cmbLanguage = new JComboBox<String>();
		cmbLanguage.setBounds(379, 28, 70, 21);
		cmbLanguage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbLanguage_actionPerformed(e);
			}
		});
		panel.add(cmbLanguage);
		configureLanguageSelector();

		statusBar.setBounds(0, 670, 741, 25);

		statusBar.setLayout(null);
		// Creating the StatusBar.
		// statusBar.setLayout(new BorderLayout());
		statusBar.setBounds(0, 674, 741, 22);
		statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		statusBar.setBackground(Color.LIGHT_GRAY);
		contentPane.add(statusBar);

		lblDeviceState = new JTextField(
				ResourceBundle.getBundle("example_app.resources.resources").getString("State_closed"));
		lblDeviceState.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeviceState.setBackground(Color.WHITE);
		lblDeviceState.setFocusable(false);
		lblDeviceState.setEditable(false);
		lblDeviceState.setSize(100, 18);
		lblDeviceState.setLocation(1, 2);
		statusBar.add(lblDeviceState);

		lblDeviceType = new JTextField("Adapter Type: nothing");
		lblDeviceType.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeviceType.setFocusable(false);
		lblDeviceType.setEditable(false);
		lblDeviceType.setBackground(Color.WHITE);
		lblDeviceType.setSize(318, 18);
		lblDeviceType.setLocation(102, 2);
		statusBar.add(lblDeviceType);

		lblDeviceGUID = new JTextField("GUID: nothing");
		lblDeviceGUID.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeviceGUID.setFocusable(false);
		lblDeviceGUID.setEditable(false);
		lblDeviceGUID.setBackground(Color.WHITE);
		lblDeviceGUID.setSize(318, 18);
		lblDeviceGUID.setLocation(421, 2);
		statusBar.add(lblDeviceGUID);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				formWindowOpened(arg0);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				formWindowClosing(e);
			}
		});

		WorkshopUiScaler.apply(this);
	}

	void formWindowOpened(WindowEvent arg0) {

		try {

			// Set location to center screen
			int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (this.getWidth() / 2);
			int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (this.getHeight() / 2);
			this.setLocation(x, y);

			// fill combobox with enum values
			cmbConnectionType.setModel(new DefaultComboBoxModel<eTypeOfCommunication>(eTypeOfCommunication.values()));

			cmbRegisterMode.setModel(new DefaultComboBoxModel<eRegisterMode>(eRegisterMode.values()));
			cmbBaudrate.setModel(new DefaultComboBoxModel<eBaudrate>(eBaudrate.values()));
			cmbParity.setModel(new DefaultComboBoxModel<eParity>(eParity.values()));
			cmbDataBits.setModel(new DefaultComboBoxModel<eDataBits>(eDataBits.values()));
			cmbStopbits.setModel(new DefaultComboBoxModel<eStopBits>(eStopBits.values()));
			cmbFlowControl.setModel(new DefaultComboBoxModel<eFlowControl>(eFlowControl.values()));

			// set combobox default values
			cmbBaudrate.setSelectedItem(eBaudrate.b9600);
			cmbParity.setSelectedItem(eParity.None);
			cmbDataBits.setSelectedItem(eDataBits.DataBits8);
			cmbStopbits.setSelectedItem(eStopBits.One);
			cmbFlowControl.setSelectedItem(eFlowControl.None);

			String[] allSerialPorts = SerialPortList.getPortNames();

			if (allSerialPorts != null && allSerialPorts.length > 0) {
				cmbSerialPort.setModel(new DefaultComboBoxModel<String>(allSerialPorts));
			} else {
				cmbSerialPort.removeAll();
				cmbSerialPort.setSelectedItem("No serial ports detected");
			}

			LoadSettingsFromFile();
			CreateAndInitDevice();

			txtUser.requestFocusInWindow();

		} catch (Exception ex) {

			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void LoadSettingsFromFile() {
		Properties p = new Properties();

		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// open file
			FileInputStream fis = new FileInputStream(new File("PLCcomModbusCoreExSettings.xml").getAbsolutePath());
			p.loadFromXML(fis);

			// load saved settiungs from PLCcomModbusSlaveSettings.xml
			if (p.containsKey("user")) {
				txtUser.setText(p.getProperty("user"));
			}
			if (p.containsKey("serial")) {
				txtSerial.setText(p.getProperty("serial"));
			}

			if (p.containsKey("TypeOfCommunication")) {
				cmbConnectionType.setSelectedItem(eTypeOfCommunication.valueOf(p.getProperty("TypeOfCommunication")));
			}

			if (p.containsKey("RegisterMode")) {
				cmbRegisterMode.setSelectedItem(eRegisterMode.valueOf(p.getProperty("RegisterMode")));
			}

			if (p.containsKey("Baudrate")) {
				cmbBaudrate.setSelectedItem(eBaudrate.valueOf(p.getProperty("Baudrate")));
			}

			if (p.containsKey("SerialPort")) {
				cmbSerialPort.setSelectedItem(p.getProperty("SerialPort"));
			}

			if (p.containsKey("Parity")) {
				cmbParity.setSelectedItem(eParity.valueOf(p.getProperty("Parity")));
			}

			if (p.containsKey("DataBits")) {
				cmbDataBits.setSelectedItem(eDataBits.valueOf(p.getProperty("DataBits")));
			}

			if (p.containsKey("Stopbits")) {
				cmbStopbits.setSelectedItem(eStopBits.valueOf(p.getProperty("Stopbits")));
			}

			if (p.containsKey("FlowControl")) {
				cmbFlowControl.setSelectedItem(eFlowControl.valueOf(p.getProperty("FlowControl")));
			}

			if (p.containsKey("txtAddressIP")) {
				txtAddressIP.setText(p.getProperty("txtAddressIP"));
			}

			if (p.containsKey("txtRemotePort")) {
				txtRemotePort.setText(p.getProperty("txtRemotePort"));
			}

			if (p.containsKey("txtLocalPort")) {
				txtLocalPort.setText(p.getProperty("txtLocalPort"));
			}

			if (p.containsKey("txtIdleTime")) {
				txtIdleTime.setText(p.getProperty("txtIdleTime"));
			}

			if (p.containsKey("txtOperationTimeout")) {
				txtOperationTimeout.setText(p.getProperty("txtOperationTimeout"));
			}

			if (p.containsKey("chkCRCCheck")) {
				chkCRCCheck.setSelected(Boolean.getBoolean(p.getProperty("chkCRCCheck")));
			}

		} catch (FileNotFoundException ignore) {

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} catch (Throwable ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}

	}

	private void initializeCertificateAlert() {
		if (btnCertificateManager == null) {
			return;
		}
		certificateManagerDefaultBackColor = btnCertificateManager.getBackground();
		certificateAlertTimer = new javax.swing.Timer(550, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				certificateAlertBlinkState = !certificateAlertBlinkState;
				btnCertificateManager.setBackground(certificateAlertBlinkState ? Color.PINK : new Color(255, 230, 230));
				btnCertificateManager.setOpaque(true);
			}
		});
		updateCertificateAlertState();
	}

	private void configureLanguageSelector() {
		if (cmbLanguage == null) {
			return;
		}
		languageSelectorInitializing = true;
		cmbLanguage.setModel(new DefaultComboBoxModel<String>(new String[] { "de", "en" }));
		cmbLanguage.setSelectedItem(getCurrentLanguageCode());
		languageSelectorInitializing = false;
	}

	private String getCurrentLanguageCode() {
		return "de".equalsIgnoreCase(Locale.getDefault().getLanguage()) ? "de" : "en";
	}

	private void cmbLanguage_actionPerformed(ActionEvent e) {
		if (languageSelectorInitializing || cmbLanguage == null || cmbLanguage.getSelectedItem() == null) {
			return;
		}
		String selectedLanguage = cmbLanguage.getSelectedItem().toString();
		if (selectedLanguage.equals(getCurrentLanguageCode())) {
			return;
		}
		switchUiLanguage(selectedLanguage);
	}

	private void switchUiLanguage(String languageCode) {
		Locale.setDefault("de".equalsIgnoreCase(languageCode) ? Locale.GERMAN : Locale.ENGLISH);
		ResourceBundle.clearCache();
		resources = ResourceBundle.getBundle("example_app.resources.resources");
		SetLanguage();
		configureLanguageSelector();
	}

	private void SetLanguage() {
		txtWarning.setText(resources.getString("txtWarning_Text"));
		setTitle(resources.getString("main_Text"));
		btnEditConnectionSettings.setText(resources.getString("btnEditConnectionSettings_Text"));
		btnSaveConnectionSettings.setText(resources.getString("btnSaveConnectionSettings_Text"));
		lblConnectionType.setText(resources.getString("lblConnectionType_Text"));
		lblSerialCode.setText(resources.getString("lblSerialCode_Text"));
		btnOtherFunctions.setText(resources.getString("btnOtherFunctions_Text"));
		btnClose.setText(resources.getString("btnClose_Text"));
		lblmaxIdleTime.setText(resources.getString("lblmaxIdleTime_Text"));
		btnReadWriteModbus.setText(resources.getString("btnReadWriteModbus_Text"));
		btnReadCollection.setText(resources.getString("btnReadCollection_Text"));
		lblRegisterMode.setText(resources.getString("lblRegisterMode_Text"));
		lblLanguage.setText(resources.getString("lblLanguage_Text"));
		btnCertificateManager.setText(resources.getString("btnCertificateManager_Text"));
		chkCRCCheck.setText(formatCrcCheckText());
		updateCertificateAlertState();
		contentPane.revalidate();
		contentPane.repaint();
	}

	private String formatCrcCheckText() {
		String text = resources.getString("Master_Example.txtpnRtuasciiCrclcrCheck.text");
		return "<html>" + text.replace(" CRC", "<br>CRC").replace(" Check", "<br>Check") + "</html>";
	}

	private SecureModbusTcpOptions prepareSecureTcpOptionsForUi(SecureModbusTcpOptions options) {
		if (options != null && options.getPkiStore() != null) {
			options.getPkiStore().addCertificateStoredListener(new SecureModbusPkiCertificateStoredListener() {
				@Override
				public void certificateStored(final SecureModbusPkiCertificateStoredEventArgs eventArgs) {
					if (eventArgs == null || eventArgs.getStoreKind() != SecureModbusPkiStoreKind.Rejected) {
						return;
					}
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							onRejectedCertificateStored(eventArgs.getIsNewFile());
						}
					});
				}
			});
		}
		return options;
	}

	private void onRejectedCertificateStored(boolean isNewFile) {
		updateCertificateAlertState();
		if (!isNewFile || certificateManagerOpen || certificateManagerAutoOpened) {
			return;
		}
		certificateManagerAutoOpened = true;
		openCertificateManager();
	}

	private void updateCertificateAlertState() {
		if (btnCertificateManager == null) {
			return;
		}
		int rejectedCertificates = CertificateManagerDialog.countRejectedCertificates();
		if (rejectedCertificates > 0) {
			btnCertificateManager.setToolTipText(MessageFormat.format(
					resources.getString("btnCertificateManager_RejectedToolTip_Text"), rejectedCertificates));
			if (certificateAlertTimer != null && !certificateAlertTimer.isRunning()) {
				certificateAlertTimer.start();
			}
		} else {
			if (certificateAlertTimer != null && certificateAlertTimer.isRunning()) {
				certificateAlertTimer.stop();
			}
			certificateAlertBlinkState = false;
			btnCertificateManager.setOpaque(false);
			btnCertificateManager.setBackground(certificateManagerDefaultBackColor);
			btnCertificateManager.setToolTipText(resources.getString("btnCertificateManager_ToolTip_Text"));
		}
	}

	private void openCertificateManager() {
		certificateManagerOpen = true;
		try {
			CertificateManagerDialog.showDialog(this);
		} finally {
			certificateManagerOpen = false;
			updateCertificateAlertState();
		}
	}

	private void CreateAndInitDevice() {
		// clean up
		if (Device != null) {
			if (Device.getConnector() != null) {
				Device.getConnector().removeConnectionStateEventHandler(portStateEventHandler);
			}
			Device.unload();
		}

		try {
			// create new device instance
			Device = new ModbusMaster(txtUser.getText(), txtSerial.getText());

			// linking device with adapter
			switch (cmbConnectionType.getSelectedItem().toString()) {
			case "TCP":
				// set tcp adapter
				Device.setConnector_TCP(txtAddressIP.getText(), Integer.valueOf(txtRemotePort.getText()),
						Integer.valueOf(txtLocalPort.getText()));
				break;
			case "UDP":
				// set udp adapter
				Device.setConnector_UDP(txtAddressIP.getText(), Integer.valueOf(txtRemotePort.getText()),
						Integer.valueOf(txtLocalPort.getText()));
				break;
			case "RTU_over_TCP":
				// set tcp adapter
				Device.setConnector_RTU_over_TCP(txtAddressIP.getText(), Integer.valueOf(txtRemotePort.getText()),
						Integer.valueOf(txtLocalPort.getText()));
				break;
			case "SecureTCP":
				Device.setConnector_SecureTCP(txtAddressIP.getText(), Integer.valueOf(txtRemotePort.getText()),
						Integer.valueOf(txtLocalPort.getText()), prepareSecureTcpOptionsForUi(SecureTcpExampleOptions.create()));
				break;
			case "RTU":
				// set rtu adapter
				Device.setConnector_RTU((String) cmbSerialPort.getSelectedItem(),
						(eBaudrate) cmbBaudrate.getSelectedItem(), (eDataBits) cmbDataBits.getSelectedItem(),
						(eParity) cmbParity.getSelectedItem(), (eStopBits) cmbStopbits.getSelectedItem(),
						(eFlowControl) cmbFlowControl.getSelectedItem());
				break;
			case "ASCII":
				// set ascii adapter
				Device.setConnector_ASCII((String) cmbSerialPort.getSelectedItem(),
						(eBaudrate) cmbBaudrate.getSelectedItem(), (eDataBits) cmbDataBits.getSelectedItem(),
						(eParity) cmbParity.getSelectedItem(), (eStopBits) cmbStopbits.getSelectedItem(),
						(eFlowControl) cmbFlowControl.getSelectedItem());
				break;
			default:
				JOptionPane.showMessageDialog(null, resources.getString("undefinend_Connectiontype"), "",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// set max idleTime, Standard = 10000ms
			try {
				Device.getConnector().setMaxIdleTime(Integer.valueOf(txtIdleTime.getText()));
			} catch (NumberFormatException ex) {
				Device.getConnector().setMaxIdleTime(10000);
			}

			// Set operation timeout, Standard = 2000ms
			try {
				Device.getConnector().setOperationTimeout(Integer.valueOf(txtOperationTimeout.getText()));
			} catch (NumberFormatException ex) {
				Device.getConnector().setOperationTimeout(2000);
			}

			// Set crc/lcr Check
			Device.setCRCCheckEnabled(chkCRCCheck.isSelected());

			// Set RegisterMode >> 16bit or 32bit register, settings must match
			// with settings of slave
			Device.setRegisterMode((eRegisterMode) cmbRegisterMode.getSelectedItem());

			// add eventhandler
			Device.getConnector().addConnectionStateEventHandler(portStateEventHandler);
			lblDeviceGUID.setText("Device UUID: " + Device.getDeviceUUID().toString());
			lblDeviceType.setText("Adapter Type: " + Device.getConnector().getClass().getName());

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	void btnSaveConnectionSettings_actionPerformed(ActionEvent arg) {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			btnEditConnectionSettings.setEnabled(true);
			panFunctions.setEnabled(true);
			panAddress.setEnabled(false);

			CreateAndInitDevice();

			if (panAddress.isEnabled()) {
				btnSaveConnectionSettings_actionPerformed(null);
			}

			// Write Settings in PLCcomModbusCoreExSettings.xml
			Properties p = new Properties();
			p.setProperty("user", txtUser.getText());
			p.setProperty("serial", txtSerial.getText());

			if (cmbConnectionType.getSelectedItem() != null) {
				p.setProperty("TypeOfCommunication", cmbConnectionType.getSelectedItem().toString());
			}
			if (cmbRegisterMode.getSelectedItem() != null) {
				p.setProperty("RegisterMode", cmbRegisterMode.getSelectedItem().toString());
			}
			if (cmbBaudrate.getSelectedItem() != null) {
				p.setProperty("Baudrate", cmbBaudrate.getSelectedItem().toString());
			}
			if (cmbSerialPort.getSelectedItem() != null) {
				p.setProperty("SerialPort", cmbSerialPort.getSelectedItem().toString());
			}
			if (cmbParity.getSelectedItem() != null) {
				p.setProperty("Parity", cmbParity.getSelectedItem().toString());
			}
			if (cmbDataBits.getSelectedItem() != null) {
				p.setProperty("DataBits", cmbDataBits.getSelectedItem().toString());
			}
			if (cmbStopbits.getSelectedItem() != null) {
				p.setProperty("Stopbits", cmbStopbits.getSelectedItem().toString());
			}
			if (cmbFlowControl.getSelectedItem() != null) {
				p.setProperty("FlowControl", cmbFlowControl.getSelectedItem().toString());
			}

			p.setProperty("txtAddressIP", txtAddressIP.getText());
			p.setProperty("txtRemotePort", txtRemotePort.getText());
			p.setProperty("txtLocalPort", txtLocalPort.getText());
			p.setProperty("txtIdleTime", txtIdleTime.getText());
			p.setProperty("txtOperationTimeout", txtOperationTimeout.getText());
			p.setProperty("chkCRCCheck", Boolean.toString(chkCRCCheck.isSelected()));

			FileOutputStream fos = new FileOutputStream(new File("PLCcomModbusCoreExSettings.xml").getAbsolutePath());
			p.storeToXML(fos, "PLCcom Example Settings", "UTF8");
			fos.close();
			JOptionPane.showMessageDialog(null,
					resources.getString("successfully_saved") + System.getProperty("line.separator") + "File: "
							+ new File("PLCcomModbusCoreExSettings.xml").getAbsolutePath(),
					"", JOptionPane.INFORMATION_MESSAGE);

		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} catch (HeadlessException ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private void cmbConnectionType_actionPerformed(ActionEvent e) {
		try {
			eTypeOfCommunication selectedCommunication = (eTypeOfCommunication) this.cmbConnectionType.getSelectedItem();
			switch (selectedCommunication) {
			case TCP:
			case UDP:
			case RTU_over_TCP:
			case SecureTCP:
				panIPSettings.setEnabled(true);
				panSerialSettings.setEnabled(false);
				if (selectedCommunication == eTypeOfCommunication.SecureTCP && "502".equals(txtRemotePort.getText())) {
					txtRemotePort.setText(String.valueOf(SecureModbusTcpOptions.DEFAULT_SECURE_MODBUS_TCP_PORT));
				} else if (selectedCommunication != eTypeOfCommunication.SecureTCP
						&& String.valueOf(SecureModbusTcpOptions.DEFAULT_SECURE_MODBUS_TCP_PORT).equals(txtRemotePort.getText())) {
					txtRemotePort.setText("502");
				}
				break;
			case RTU:
			case ASCII:
				panIPSettings.setEnabled(false);
				panSerialSettings.setEnabled(true);
				break;
			default:
				panIPSettings.setEnabled(false);
				panSerialSettings.setEnabled(true);
				break;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void formWindowClosing(WindowEvent e) {
		if (Device != null) {
			Device.unload();
		}
	}

	protected void txtSerial_TextChanged(DocumentEvent e) {
		if (Device != null) {
			Device.setSerial(txtSerial.getText());
		}
	}

	protected void txtUser_TextChanged(DocumentEvent e) {
		if (Device != null) {
			Device.setUser(txtUser.getText());
		}
	}

	private void btnEditConnectionSettings_actionPerformed(ActionEvent e) {
		try {
			if (CountOpenDialogs > 0) {
				JOptionPane.showMessageDialog(null, resources.getString("to_many_windows"), "",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			btnEditConnectionSettings.setEnabled(false);
			panFunctions.setEnabled(false);
			panAddress.setEnabled(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Receives connection-state notifications from the SDK connector and updates
	 * the visible state indicator in the Swing status bar.
	 * <p>
	 * The SDK may raise this callback from connector or idle-control worker
	 * threads. Swing components must only be touched on the event-dispatch thread,
	 * therefore this method marshals background notifications back to the UI thread
	 * before changing text, colors, or showing an error dialog.
	 * </p>
	 *
	 * @param e
	 *            the new connection state reported by the active connector
	 */
	@Override
	public void On_ConnectionStateChange(final eConnectionState e) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					On_ConnectionStateChange(e);
				}
			});
			return;
		}
		try {
			lblDeviceState.setText(resources.getString("State_" + e.toString()) + (Device != null ? " " : ""));
			lblDeviceState.setBackground(
					(e == eConnectionState.open || e == eConnectionState.connected) ? Color.BLUE : Color.WHITE);
			lblDeviceState.setForeground(
					(e == eConnectionState.open || e == eConnectionState.connected) ? Color.WHITE : Color.BLACK);
		} catch (Exception ex) {

			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnReadWriteModbus_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;
		ReadWriteBox rwb = new ReadWriteBox(Device);

		rwb.setVisible(true);
	}

	private void btnReadCollection_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;
		ReadCollectionBox rcb = new ReadCollectionBox(Device);
		rcb.setVisible(true);
	}

	private void btnOtherFunctions_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;
		OtherFunctions otf = new OtherFunctions(Device);
		otf.setVisible(true);
	}

	private void btnClose_actionPerformed(ActionEvent e) {
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			if (Device != null) {
				// unload and dispose all objects
				Device.unload();
				Device = null;
			}

		} finally {
			this.setCursor(Cursor.getDefaultCursor());
			System.exit(0);
		}
	}
}
