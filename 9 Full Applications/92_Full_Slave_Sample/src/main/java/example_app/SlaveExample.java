package example_app;

import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.border.TitledBorder;
import javax.swing.JLabel;

import static com.indian.plccom.modbus.UnsignedDatatypes.UBuilder.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.SystemColor;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;

import example_app.DisabledJPanel.DisabledJPanel;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.ImageIcon;

import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.indian.plccom.modbus.*;
import com.indian.plccom.modbus.Enums.*;
import com.indian.plccom.modbus.UnsignedDatatypes.*;

import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Full PLCcom for Modbus slave workshop application.
 * <p>
 * The frame demonstrates the slave-side SDK workflows used by the public
 * workshops: listener management, data-store editing, diagnostic logging,
 * classic transports, SecureTCP, and certificate-management comfort. The class
 * is public only because it is the executable sample entry point.
 * </p>
 */
public class SlaveExample extends JFrame implements iConnectionStateChangeEvent, iIncomingLogEntryEvent,
		iDataStoreItemChangeEvent, iDataStoreItemReadEvent {

	private static final long serialVersionUID = 1594570757489334376L;
	private customCheckBoxes myCheckBoxes = new customCheckBoxes();
	private ResourceBundle resources = ResourceBundle.getBundle("example_app.resources.resources");
	private ModbusSlave slave = null;

	private ReentrantLock mLock = new ReentrantLock();

	private IncomingLogEntryEventHandler incomingLogEntryEventHandler = new IncomingLogEntryEventHandler(this);
	private DataStoreItemChangeEventHandler dataStoreItemChangeEventHandler = new DataStoreItemChangeEventHandler(this);
	private DataStoreItemReadEventHandler dataStoreItemReadEventHandler = new DataStoreItemReadEventHandler(this);
	private PortStateEventHandler portStateEventHandler = new PortStateEventHandler(this);

	private JPanel contentPane;
	private JTextField txtUser;
	private JTextField txtSerial;
	private JLabel lblUser;
	private JLabel lblSerial;
	private DisabledJPanel panSerial;
	private DisabledJPanel panListener;
	private DisabledJPanel panSlaveSettings;
	private DisabledJPanel panSupportedFunctions;
	private DisabledJPanel panStatistics;
	private DisabledJPanel panDataStore;
	private DisabledJPanel panAddressType;
	private DisabledJPanel panValues;
	private DisabledJPanel panLog;
	private JPanel grbSlaveSettings;
	private JPanel grbSupportedFunctions;
	private JPanel grbStatistics;
	private JPanel grbDataStore;
	private JPanel grbAddressType;
	private JPanel grbValues;
	private JPanel grbLog;
	private JPanel grbListener;
	private JPanel grbSerial;
	private JLabel lblSerialCode;
	private JButton btnStartSlave;
	private JButton btnStopSlave;
	private JButton btnAddListener;
	private JButton btnRemoveListener;
	private JButton btnSetValue;
	private JButton btnCopyLogtoClipboard;
	private JButton btnCopyLogtoFile;
	private JButton btnLoadSettings;
	private JButton btnSaveSettings;
	private JButton btnClose;
	private JButton btnCertificateManager;
	private Color certificateManagerDefaultBackColor;
	private javax.swing.Timer certificateAlertTimer;
	private boolean certificateAlertBlinkState;
	private boolean certificateManagerAutoOpened;
	private boolean certificateManagerOpen;
	private JComboBox<eLogLevel> cmbLogLevel;
	private JLabel lblLanguage;
	private JComboBox<String> cmbLanguage;
	private boolean languageSelectorInitializing;
	private JLabel lblLoglevel;
	private JCheckBox chkLogging;
	private JCheckBox chkAutoScroll;
	private JCheckBox chkSupportsFC01;
	private JCheckBox chkSupportsFC02;
	private JCheckBox chkSupportsFC03;
	private JCheckBox chkSupportsFC04;
	private JCheckBox chkSupportsFC05;
	private JCheckBox chkSupportsFC06;
	private JCheckBox chkSupportsFC08;
	private JCheckBox chkSupportsFC15;
	private JCheckBox chkSupportsFC16;
	private JCheckBox chkSupportsFC23;
	private JLabel lblFC01;
	private JLabel lblFC02;
	private JLabel lblFC03;
	private JLabel lblFC04;
	private JLabel lblFC05;
	private JLabel lblFC06;
	private JLabel lblFC08;
	private JLabel lblFC15;
	private JLabel lblFC16;
	private JLabel lblFC23;
	private JComboBox<eRegisterMode> cmbRegisterMode;
	private JLabel lblRegisterMode;
	private JLabel lblSlaveID;
	private JComboBox<Integer> cmbSlaveID;
	private JComboBox<eByteOrder> cmbByteOrder;
	private JLabel lblByteOrder;
	private JTextField txtIncomingBytes;
	private JTextField txtOutgoingBytes;
	private JTextField txtIncomingRequests;
	private JTextField txtIncorectRequests;
	private JTextField txtUnparseableRequests;
	private JLabel lblIncomingBytes;
	private JLabel lblOutgoingBytes;
	private JLabel lblIncomingRequests;
	private JLabel lblIncorrectRequests;
	private JLabel lblUnparseableRequests;
	private JRadioButton rbRegisterTypeCoils;
	private JRadioButton rbRegisterTypeDiscreteInputs;
	private JRadioButton rbRegisterTypeHoldingRegister;
	private JRadioButton rbRegisterTypeInputRegister;
	private JTextField txtAddress;
	private JLabel lblAddress;
	private JLabel lblValue;
	private JTextField txtValue;
	private JRadioButton rbValueOn;
	private JRadioButton rbValueOFF;
	private JCheckBox chkBit0;
	private JCheckBox chkBit1;
	private JCheckBox chkBit2;
	private JCheckBox chkBit3;
	private JCheckBox chkBit4;
	private JCheckBox chkBit5;
	private JCheckBox chkBit6;
	private JCheckBox chkBit7;
	private JCheckBox chkBit8;
	private JCheckBox chkBit9;
	private JCheckBox chkBit10;
	private JCheckBox chkBit11;
	private JCheckBox chkBit12;
	private JCheckBox chkBit13;
	private JCheckBox chkBit14;
	private JCheckBox chkBit15;
	private JCheckBox chkBit16;
	private JCheckBox chkBit17;
	private JCheckBox chkBit18;
	private JCheckBox chkBit19;
	private JCheckBox chkBit20;
	private JCheckBox chkBit21;
	private JCheckBox chkBit22;
	private JCheckBox chkBit23;
	private JCheckBox chkBit24;
	private JCheckBox chkBit25;
	private JCheckBox chkBit26;
	private JCheckBox chkBit27;
	private JCheckBox chkBit28;
	private JCheckBox chkBit29;
	private JCheckBox chkBit30;
	private JCheckBox chkBit31;

	private JLabel lblBit0;
	private JLabel lblBit1;
	private JLabel lblBit2;
	private JLabel lblBit3;
	private JLabel lblBit4;
	private JLabel lblBit5;
	private JLabel lblBit6;
	private JLabel lblBit7;
	private JLabel lblBit8;
	private JLabel lblBit9;
	private JLabel lblBit10;
	private JLabel lblBit11;
	private JLabel lblBit12;
	private JLabel lblBit13;
	private JLabel lblBit14;
	private JLabel lblBit15;
	private JLabel lblBit16;
	private JLabel lblBit17;
	private JLabel lblBit18;
	private JLabel lblBit19;
	private JLabel lblBit20;
	private JLabel lblBit21;
	private JLabel lblBit22;
	private JLabel lblBit23;
	private JLabel lblBit24;
	private JLabel lblBit25;
	private JLabel lblBit26;
	private JLabel lblBit27;
	private JLabel lblBit28;
	private JLabel lblBit29;
	private JLabel lblBit30;
	private JLabel lblBit31;
	private JTable lvListener;
	private JTable lvValues;
	private JTable lvLog;
	private JPanel grbBoolValues;
	private JPanel grbTextValue;

	/**
	 * Launches the slave workshop application.
	 *
	 * @param args
	 *            command-line arguments; currently ignored
	 */
	public static void main(String[] args) {
		WorkshopLogging.configure();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SlaveExample frame = new SlaveExample();
					frame.setVisible(true);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Creates and initializes the full slave workshop frame.
	 */
	public SlaveExample() {
		initialize();
		initializeCertificateAlert();
		addControlListener_to_Button();
		addControlListener_to_CheckBoxes();
		addControlListener_to_OtherControls();
		initCheckBoxArray();
	}

	/**
	 * Create the frame.
	 */
	private void initialize() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				formWindowOpened(arg0);
			}
		});

		// set global lock and feel platform independent
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

			@SuppressWarnings("rawtypes")
			java.util.Enumeration keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value != null && value instanceof javax.swing.plaf.FontUIResource)
					UIManager.put(key, new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 11));
			}
			WorkshopUi.installScaledCheckBoxIcon();

			// UIManager.setLookAndFeel(
			// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
		}

		setResizable(false);
		setTitle(ResourceBundle.getBundle("example_app.resources.resources").getString("main_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(SlaveExample.class.getResource("/example_app/resources/pci_card_network.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1093, 837);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		grbSerial = new JPanel();
		grbSerial.setBorder(new TitledBorder(null, "serial", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbSerial.setBounds(7, 12, 710, 64);
		contentPane.add(grbSerial);
		grbSerial.setLayout(null);

		lblSerialCode = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblSerialCode_Text"));
		lblSerialCode.setFont(new Font("Arial", Font.BOLD, 13));
		lblSerialCode.setBounds(9, 26, 268, 17);
		grbSerial.add(lblSerialCode);

		txtUser = new JTextField();
		txtUser.setBounds(503, 10, 190, 20);
		grbSerial.add(txtUser);

		txtSerial = new JTextField();
		txtSerial.setBounds(503, 35, 190, 20);
		grbSerial.add(txtSerial);

		lblUser = new JLabel("user:");
		lblUser.setBounds(447, 13, 46, 14);
		grbSerial.add(lblUser);

		lblSerial = new JLabel("serial:");
		lblSerial.setBounds(447, 38, 46, 14);
		grbSerial.add(lblSerial);

		grbListener = new JPanel();
		grbListener.setLayout(null);
		grbListener.setBorder(new TitledBorder(null,
				ResourceBundle.getBundle("example_app.resources.resources").getString("grbListener_Text"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbListener.setBounds(7, 77, 710, 366);
		contentPane.add(grbListener);

		grbSlaveSettings = new JPanel();
		grbSlaveSettings.setLayout(null);
		grbSlaveSettings.setBorder(new TitledBorder(null,
				ResourceBundle.getBundle("example_app.resources.resources").getString("grbSlaveSettings_Text"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbSlaveSettings.setBounds(172, 13, 279, 73);
		grbListener.add(grbSlaveSettings);

		grbSupportedFunctions = new JPanel();
		grbSupportedFunctions.setLayout(null);
		grbSupportedFunctions.setBorder(new TitledBorder(null,
				ResourceBundle.getBundle("example_app.resources.resources").getString("grbSupportedFunctions_Text"),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbSupportedFunctions.setBounds(172, 85, 279, 75);
		grbListener.add(grbSupportedFunctions);

		grbStatistics = new JPanel();
		grbStatistics.setLayout(null);
		grbStatistics.setBorder(new TitledBorder(null, ResourceBundle

				.getBundle("example_app.resources.resources").getString(

						"grbStatistics_Text"),
				TitledBorder.LEADING,

				TitledBorder.TOP, null, null));
		grbStatistics.setBounds(458, 13, 246, 148);
		grbListener.add(grbStatistics);

		grbDataStore = new JPanel();
		grbDataStore.setLayout(null);
		grbDataStore.setBorder(new TitledBorder(null, ResourceBundle

				.getBundle("example_app.resources.resources").getString(

						"grbDataStore_Text"),
				TitledBorder.LEADING,

				TitledBorder.TOP, null, null));
		grbDataStore.setBounds(726, 12, 347, 431);
		contentPane.add(grbDataStore);

		grbAddressType = new JPanel();
		grbAddressType.setLayout(null);
		grbAddressType.setBorder(new TitledBorder(null, ResourceBundle

				.getBundle("example_app.resources.resources").getString(

						"grbAddressType_Text"),
				TitledBorder.LEADING,

				TitledBorder.TOP, null, null));
		grbAddressType.setBounds(9, 15, 328, 49);
		grbDataStore.add(grbAddressType);

		grbValues = new JPanel();
		grbValues.setLayout(null);
		grbValues.setBorder(new TitledBorder(null, ResourceBundle

				.getBundle("example_app.resources.resources").getString(

						"grbValues_Text"),
				TitledBorder.LEADING,

				TitledBorder.TOP, null, null));
		grbValues.setBounds(9, 65, 330, 159);
		grbDataStore.add(grbValues);

		grbLog = new JPanel();
		grbLog.setLayout(null);
		grbLog.setBorder(new TitledBorder(null, ResourceBundle

				.getBundle("example_app.resources.resources").getString(

						"grbLog_Text"),
				TitledBorder.LEADING,

				TitledBorder.TOP, null, null));
		grbLog.setBounds(7, 449, 1066, 269);
		contentPane.add(grbLog);

		// Add switchable Panels
		panSerial = new DisabledJPanel(grbSerial);
		panSerial.setLocation(grbSerial.getLocation());
		panSerial.setSize(grbSerial.getSize());
		panSerial.setDisabledColor(new Color(240, 240, 240, 100));
		panSerial.setEnabled(true);
		contentPane.add(panSerial);

		panListener = new DisabledJPanel(grbListener);
		panListener.setLocation(grbListener.getLocation());
		panListener.setSize(grbListener.getSize());
		panListener.setDisabledColor(new Color(240, 240, 240, 100));
		panListener.setEnabled(true);
		contentPane.add(panListener);

		panSlaveSettings = new DisabledJPanel(grbSlaveSettings);

		panSlaveSettings.setLocation(grbSlaveSettings.getLocation());
		panSlaveSettings.setSize(grbSlaveSettings.getSize());
		panSlaveSettings.setDisabledColor(new Color(240, 240, 240, 100));
		panSlaveSettings.setEnabled(true);
		grbListener.add(panSlaveSettings);

		panSupportedFunctions = new DisabledJPanel(grbSupportedFunctions);
		panSupportedFunctions.setLocation(grbSupportedFunctions.getLocation());
		panSupportedFunctions.setSize(grbSupportedFunctions.getSize());
		panSupportedFunctions.setDisabledColor(new Color(240, 240, 240, 100));
		panSupportedFunctions.setEnabled(false);
		grbListener.add(panSupportedFunctions);

		panStatistics = new DisabledJPanel(grbStatistics);

		txtIncomingBytes = new JTextField();
		txtIncomingBytes.setText("?????"); //$NON-NLS-1$ //$NON-NLS-2$
		txtIncomingBytes.setBounds(160, 12, 74, 20);
		grbStatistics.add(txtIncomingBytes);
		txtIncomingBytes.setColumns(10);

		txtOutgoingBytes = new JTextField();
		txtOutgoingBytes.setText("?????");
		txtOutgoingBytes.setColumns(10);
		txtOutgoingBytes.setBounds(160, 38, 74, 20);
		grbStatistics.add(txtOutgoingBytes);

		txtIncomingRequests = new JTextField();
		txtIncomingRequests.setText("?????");
		txtIncomingRequests.setColumns(10);
		txtIncomingRequests.setBounds(160, 63, 74, 20);
		grbStatistics.add(txtIncomingRequests);

		txtIncorectRequests = new JTextField();
		txtIncorectRequests.setText("?????");
		txtIncorectRequests.setColumns(10);
		txtIncorectRequests.setBounds(160, 89, 74, 20);
		grbStatistics.add(txtIncorectRequests);

		txtUnparseableRequests = new JTextField();
		txtUnparseableRequests.setText("?????");
		txtUnparseableRequests.setColumns(10);
		txtUnparseableRequests.setBounds(160, 114, 74, 20);
		grbStatistics.add(txtUnparseableRequests);

		lblIncomingBytes = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblIncomingBytes_Text"));
		lblIncomingBytes.setHorizontalAlignment(SwingConstants.TRAILING);
		lblIncomingBytes.setBounds(1, 14, 155, 13);
		grbStatistics.add(lblIncomingBytes);

		lblOutgoingBytes = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblOutgoingBytes_Text"));
		lblOutgoingBytes.setHorizontalAlignment(SwingConstants.TRAILING);
		lblOutgoingBytes.setBounds(1, 40, 155, 13);
		grbStatistics.add(lblOutgoingBytes);

		lblIncomingRequests = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblIncomingRequests_Text"));
		lblIncomingRequests.setHorizontalAlignment(SwingConstants.TRAILING);
		lblIncomingRequests.setBounds(1, 65, 155, 13);
		grbStatistics.add(lblIncomingRequests);

		lblIncorrectRequests = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblIncorrectRequests_Text"));
		lblIncorrectRequests.setHorizontalAlignment(SwingConstants.TRAILING);
		lblIncorrectRequests.setBounds(1, 91, 155, 13);
		grbStatistics.add(lblIncorrectRequests);

		lblUnparseableRequests = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblUnparseableRequests_Text"));
		lblUnparseableRequests.setHorizontalAlignment(SwingConstants.TRAILING);
		lblUnparseableRequests.setBounds(1, 116, 155, 13);
		grbStatistics.add(lblUnparseableRequests);

		panStatistics.setLocation(grbStatistics.getLocation());
		panStatistics.setSize(grbStatistics.getSize());
		panStatistics.setDisabledColor(new Color(240, 240, 240, 100));
		panStatistics.setEnabled(true);
		grbListener.add(panStatistics);

		panDataStore = new DisabledJPanel(grbDataStore);
		panDataStore.setLocation(grbDataStore.getLocation());
		panDataStore.setSize(grbDataStore.getSize());
		panDataStore.setDisabledColor(new Color(240, 240, 240, 100));
		panDataStore.setEnabled(false);
		contentPane.add(panDataStore);

		ButtonGroup bgRegisterType = new ButtonGroup();

		rbRegisterTypeCoils = new JRadioButton("Coils");
		rbRegisterTypeCoils.setBounds(6, 19, 50, 17);
		rbRegisterTypeCoils.setToolTipText(rbRegisterTypeCoils.getText());
		bgRegisterType.add(rbRegisterTypeCoils);
		grbAddressType.add(rbRegisterTypeCoils);

		rbRegisterTypeDiscreteInputs = new JRadioButton("Inputs");
		rbRegisterTypeDiscreteInputs.setBounds(57, 19, 58, 17);
		rbRegisterTypeDiscreteInputs.setToolTipText(rbRegisterTypeDiscreteInputs.getText());
		bgRegisterType.add(rbRegisterTypeDiscreteInputs);
		grbAddressType.add(rbRegisterTypeDiscreteInputs);

		rbRegisterTypeHoldingRegister = new JRadioButton("Holding Register");
		rbRegisterTypeHoldingRegister.setBounds(117, 19, 103, 17);
		rbRegisterTypeHoldingRegister.setToolTipText(rbRegisterTypeHoldingRegister.getText());
		bgRegisterType.add(rbRegisterTypeHoldingRegister);
		grbAddressType.add(rbRegisterTypeHoldingRegister);

		rbRegisterTypeInputRegister = new JRadioButton("Input Register");
		rbRegisterTypeInputRegister.setBounds(227, 19, 95, 17);
		rbRegisterTypeInputRegister.setToolTipText(rbRegisterTypeInputRegister.getText());
		bgRegisterType.add(rbRegisterTypeInputRegister);
		grbAddressType.add(rbRegisterTypeInputRegister);

		panAddressType = new DisabledJPanel(grbAddressType);
		panAddressType.setLocation(grbAddressType.getLocation());
		panAddressType.setSize(grbAddressType.getSize());
		panAddressType.setDisabledColor(new Color(240, 240, 240, 100));
		panAddressType.setEnabled(true);
		grbDataStore.add(panAddressType);

		panValues = new DisabledJPanel(grbValues);
		panValues.setLocation(grbValues.getLocation());
		panValues.setSize(grbValues.getSize());
		panValues.setDisabledColor(new Color(240, 240, 240, 100));
		panValues.setEnabled(true);
		grbDataStore.add(panValues);

		panLog = new DisabledJPanel(grbLog);
		panLog.setLocation(grbLog.getLocation());
		panLog.setSize(grbLog.getSize());
		panLog.setDisabledColor(new Color(240, 240, 240, 100));
		panLog.setEnabled(true);
		contentPane.add(panLog);

		btnStartSlave = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnStartSlave_Text"));
		btnStartSlave.setMargin(new Insets(0, 0, 0, 0));
		btnStartSlave.setHorizontalTextPosition(SwingConstants.CENTER);
		btnStartSlave.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnStartSlave.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/step.png")));
		btnStartSlave.setBounds(8, 16, 68, 68);
		btnStartSlave.setToolTipText(btnStartSlave.getText());
		grbListener.add(btnStartSlave);

		btnStopSlave = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnStopSlave_Text"));
		btnStopSlave.setEnabled(false);
		btnStopSlave.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/sign_stop.png")));
		btnStopSlave.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnStopSlave.setMargin(new Insets(0, 0, 0, 0));
		btnStopSlave.setHorizontalTextPosition(SwingConstants.CENTER);
		btnStopSlave.setBounds(86, 16, 68, 68);
		btnStopSlave.setToolTipText(btnStopSlave.getText());
		grbListener.add(btnStopSlave);

		btnAddListener = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnAddListener_Text"));
		btnAddListener.setEnabled(false);
		btnAddListener.setMargin(new Insets(0, 0, 0, 0));
		btnAddListener.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAddListener.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/ear_into.png")));
		btnAddListener.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAddListener.setBounds(8, 92, 68, 68);
		btnAddListener.setToolTipText(btnAddListener.getText());
		grbListener.add(btnAddListener);

		btnRemoveListener = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnRemoveListener_Text"));
		btnRemoveListener.setEnabled(false);
		btnRemoveListener
				.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/ear_delete1.png")));
		btnRemoveListener.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnRemoveListener.setMargin(new Insets(0, 0, 0, 0));
		btnRemoveListener.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRemoveListener.setBounds(86, 92, 68, 68);
		btnRemoveListener.setToolTipText(btnRemoveListener.getText());
		grbListener.add(btnRemoveListener);

		ButtonGroup bgValue = new ButtonGroup();

		grbBoolValues = new JPanel();
		grbBoolValues.setBounds(90, 45, 101, 42);
		grbValues.add(grbBoolValues);
		grbBoolValues.setLayout(null);

		rbValueOFF = new JRadioButton("false");
		rbValueOFF.setBounds(5, 22, 82, 23);
		grbBoolValues.add(rbValueOFF);
		bgValue.add(rbValueOFF);

		rbValueOn = new JRadioButton("true");
		rbValueOn.setBounds(5, 2, 82, 24);
		grbBoolValues.add(rbValueOn);
		rbValueOn.setSelected(true);
		bgValue.add(rbValueOn);

		btnSetValue = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnSetValue_Text"));
		btnSetValue.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/auction_hammer.png")));
		btnSetValue.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSetValue.setMargin(new Insets(0, 0, 0, 0));
		btnSetValue.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSetValue.setBounds(256, 13, 68, 68);
		btnSetValue.setToolTipText(btnSetValue.getText());
		grbValues.add(btnSetValue);

		txtAddress = new JTextField();
		txtAddress.setText("0");
		txtAddress.setBounds(98, 19, 100, 20);
		grbValues.add(txtAddress);
		txtAddress.setColumns(10);

		lblAddress = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblAddress_Text"));
		lblAddress.setBounds(6, 22, 91, 14);
		grbValues.add(lblAddress);

		lblValue = new JLabel(ResourceBundle.getBundle("example_app.resources.resources").getString("lblValue_Text"));
		lblValue.setBounds(6, 64, 91, 14);
		grbValues.add(lblValue);

		grbTextValue = new JPanel();
		grbTextValue.setBounds(98, 61, 100, 20);
		grbValues.add(grbTextValue);
		grbTextValue.setLayout(null);
		grbTextValue.setVisible(false);
		txtValue = new JTextField();
		txtValue.setBounds(0, 0, 100, 20);
		grbTextValue.add(txtValue);
		txtValue.setColumns(10);

		chkBit0 = new JCheckBox("");
		chkBit0.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit0.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit0.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit0.setEnabled(false);
		chkBit0.setBounds(302, 88, 21, 21);
		chkBit0.setToolTipText("Bit 0");
		grbValues.add(chkBit0);

		lblBit0 = new JLabel("0");
		lblBit0.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit0.setBounds(302, 108, 20, 10);
		grbValues.add(lblBit0);

		chkBit1 = new JCheckBox("");
		chkBit1.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit1.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit1.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit1.setEnabled(false);
		chkBit1.setBounds(282, 88, 21, 21);
		chkBit1.setToolTipText("Bit 1");
		grbValues.add(chkBit1);

		lblBit1 = new JLabel("1");
		lblBit1.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit1.setBounds(282, 108, 20, 10);
		grbValues.add(lblBit1);

		chkBit2 = new JCheckBox("");
		chkBit2.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit2.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit2.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit2.setEnabled(false);
		chkBit2.setBounds(262, 88, 21, 21);
		chkBit2.setToolTipText("Bit 2");
		grbValues.add(chkBit2);

		lblBit2 = new JLabel("2");
		lblBit2.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit2.setBounds(262, 108, 20, 10);
		grbValues.add(lblBit2);

		chkBit3 = new JCheckBox("");
		chkBit3.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit3.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit3.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit3.setEnabled(false);
		chkBit3.setBounds(242, 88, 21, 21);
		chkBit3.setToolTipText("Bit 3");
		grbValues.add(chkBit3);

		lblBit3 = new JLabel("3");
		lblBit3.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit3.setBounds(242, 108, 20, 10);
		grbValues.add(lblBit3);

		chkBit4 = new JCheckBox("");
		chkBit4.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit4.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit4.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit4.setEnabled(false);
		chkBit4.setBounds(222, 88, 21, 21);
		chkBit4.setToolTipText("Bit 4");
		grbValues.add(chkBit4);

		lblBit4 = new JLabel("4");
		lblBit4.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit4.setBounds(222, 108, 20, 10);
		grbValues.add(lblBit4);

		chkBit5 = new JCheckBox("");
		chkBit5.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit5.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit5.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit5.setEnabled(false);
		chkBit5.setBounds(202, 88, 21, 21);
		chkBit5.setToolTipText("Bit 5");
		grbValues.add(chkBit5);

		lblBit5 = new JLabel("5");
		lblBit5.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit5.setBounds(202, 108, 20, 10);
		grbValues.add(lblBit5);

		chkBit6 = new JCheckBox("");
		chkBit6.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit6.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit6.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit6.setEnabled(false);
		chkBit6.setBounds(182, 88, 21, 21);
		chkBit6.setToolTipText("Bit 6");
		grbValues.add(chkBit6);

		lblBit6 = new JLabel("6");
		lblBit6.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit6.setBounds(182, 108, 20, 10);
		grbValues.add(lblBit6);

		chkBit7 = new JCheckBox("");
		chkBit7.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit7.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit7.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit7.setEnabled(false);
		chkBit7.setBounds(162, 88, 21, 21);
		chkBit7.setToolTipText("Bit 7");
		grbValues.add(chkBit7);

		lblBit7 = new JLabel("7");
		lblBit7.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit7.setBounds(162, 108, 20, 10);
		grbValues.add(lblBit7);

		chkBit8 = new JCheckBox("");
		chkBit8.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit8.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit8.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit8.setEnabled(false);
		chkBit8.setBounds(142, 88, 21, 21);
		chkBit8.setToolTipText("Bit 8");
		grbValues.add(chkBit8);

		lblBit8 = new JLabel("8");
		lblBit8.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit8.setBounds(142, 108, 20, 10);
		grbValues.add(lblBit8);

		chkBit9 = new JCheckBox("");
		chkBit9.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit9.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit9.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit9.setEnabled(false);
		chkBit9.setBounds(122, 88, 21, 21);
		chkBit9.setToolTipText("Bit 9");
		grbValues.add(chkBit9);

		lblBit9 = new JLabel("9");
		lblBit9.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit9.setBounds(122, 108, 20, 10);
		grbValues.add(lblBit9);

		chkBit10 = new JCheckBox("");
		chkBit10.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit10.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit10.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit10.setEnabled(false);
		chkBit10.setBounds(102, 88, 21, 21);
		chkBit10.setToolTipText("Bit 10");
		grbValues.add(chkBit10);

		lblBit10 = new JLabel("10");
		lblBit10.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit10.setBounds(102, 108, 20, 10);
		grbValues.add(lblBit10);

		chkBit11 = new JCheckBox("");
		chkBit11.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit11.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit11.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit11.setEnabled(false);
		chkBit11.setBounds(82, 88, 21, 21);
		chkBit11.setToolTipText("Bit 11");
		grbValues.add(chkBit11);

		lblBit11 = new JLabel("11");
		lblBit11.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit11.setBounds(82, 108, 20, 10);
		grbValues.add(lblBit11);

		chkBit12 = new JCheckBox("");
		chkBit12.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit12.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit12.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit12.setEnabled(false);
		chkBit12.setBounds(62, 88, 21, 21);
		chkBit12.setToolTipText("Bit 12");
		grbValues.add(chkBit12);

		lblBit12 = new JLabel("12");
		lblBit12.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit12.setBounds(62, 108, 20, 10);
		grbValues.add(lblBit12);

		chkBit13 = new JCheckBox("");
		chkBit13.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit13.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit13.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit13.setEnabled(false);
		chkBit13.setBounds(42, 88, 21, 21);
		chkBit13.setToolTipText("Bit 13");
		grbValues.add(chkBit13);

		lblBit13 = new JLabel("13");
		lblBit13.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit13.setBounds(42, 108, 20, 10);
		grbValues.add(lblBit13);

		chkBit14 = new JCheckBox("");
		chkBit14.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit14.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit14.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit14.setEnabled(false);
		chkBit14.setBounds(22, 88, 21, 21);
		chkBit14.setToolTipText("Bit 14");
		grbValues.add(chkBit14);

		lblBit14 = new JLabel("14");
		lblBit14.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit14.setBounds(22, 108, 20, 10);
		grbValues.add(lblBit14);

		chkBit15 = new JCheckBox("");
		chkBit15.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit15.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit15.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit15.setEnabled(false);
		chkBit15.setBounds(3, 88, 21, 21);
		chkBit15.setToolTipText("Bit 15");
		grbValues.add(chkBit15);

		lblBit15 = new JLabel("15");
		lblBit15.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit15.setBounds(3, 108, 20, 10);
		grbValues.add(lblBit15);

		chkBit16 = new JCheckBox("");
		chkBit16.setEnabled(false);
		chkBit16.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit16.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit16.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit16.setBounds(302, 118, 21, 21);
		chkBit16.setToolTipText("Bit 16");
		grbValues.add(chkBit16);

		lblBit16 = new JLabel("16");
		lblBit16.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit16.setBounds(302, 138, 20, 10);
		grbValues.add(lblBit16);

		chkBit17 = new JCheckBox("");
		chkBit17.setEnabled(false);
		chkBit17.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit17.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit17.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit17.setBounds(282, 118, 21, 21);
		chkBit17.setToolTipText("Bit 17");
		grbValues.add(chkBit17);

		lblBit17 = new JLabel("17");
		lblBit17.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit17.setBounds(282, 138, 20, 10);
		grbValues.add(lblBit17);

		chkBit18 = new JCheckBox("");
		chkBit18.setEnabled(false);
		chkBit18.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit18.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit18.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit18.setBounds(262, 118, 21, 21);
		chkBit18.setToolTipText("Bit 18");
		grbValues.add(chkBit18);

		lblBit18 = new JLabel("18");
		lblBit18.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit18.setBounds(262, 138, 20, 10);
		grbValues.add(lblBit18);

		chkBit19 = new JCheckBox("");
		chkBit19.setEnabled(false);
		chkBit19.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit19.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit19.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit19.setBounds(242, 118, 21, 21);
		chkBit19.setToolTipText("Bit 19");
		grbValues.add(chkBit19);

		lblBit19 = new JLabel("19");
		lblBit19.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit19.setBounds(242, 138, 20, 10);
		grbValues.add(lblBit19);

		chkBit20 = new JCheckBox("");
		chkBit20.setEnabled(false);
		chkBit20.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit20.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit20.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit20.setBounds(222, 118, 21, 21);
		chkBit20.setToolTipText("Bit 20");
		grbValues.add(chkBit20);

		lblBit20 = new JLabel("20");
		lblBit20.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit20.setBounds(222, 138, 20, 10);
		grbValues.add(lblBit20);

		chkBit21 = new JCheckBox("");
		chkBit21.setEnabled(false);
		chkBit21.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit21.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit21.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit21.setBounds(202, 118, 21, 21);
		chkBit21.setToolTipText("Bit 21");
		grbValues.add(chkBit21);

		lblBit21 = new JLabel("21");
		lblBit21.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit21.setBounds(202, 138, 20, 10);
		grbValues.add(lblBit21);

		chkBit22 = new JCheckBox("");
		chkBit22.setEnabled(false);
		chkBit22.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit22.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit22.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit22.setBounds(182, 118, 21, 21);
		chkBit22.setToolTipText("Bit 22");
		grbValues.add(chkBit22);

		lblBit22 = new JLabel("22");
		lblBit22.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit22.setBounds(182, 138, 20, 10);
		grbValues.add(lblBit22);

		chkBit23 = new JCheckBox("");
		chkBit23.setEnabled(false);
		chkBit23.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit23.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit23.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit23.setBounds(162, 118, 21, 21);
		chkBit23.setToolTipText("Bit 23");
		grbValues.add(chkBit23);

		lblBit23 = new JLabel("23");
		lblBit23.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit23.setBounds(162, 138, 20, 10);
		grbValues.add(lblBit23);

		chkBit24 = new JCheckBox("");
		chkBit24.setEnabled(false);
		chkBit24.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit24.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit24.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit24.setBounds(142, 118, 21, 21);
		chkBit24.setToolTipText("Bit 24");
		grbValues.add(chkBit24);

		lblBit24 = new JLabel("24");
		lblBit24.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit24.setBounds(142, 138, 20, 10);
		grbValues.add(lblBit24);

		chkBit25 = new JCheckBox("");
		chkBit25.setEnabled(false);
		chkBit25.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit25.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit25.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit25.setBounds(122, 118, 21, 21);
		chkBit25.setToolTipText("Bit 25");
		grbValues.add(chkBit25);

		lblBit25 = new JLabel("25");
		lblBit25.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit25.setBounds(122, 138, 20, 10);
		grbValues.add(lblBit25);

		chkBit26 = new JCheckBox("");
		chkBit26.setEnabled(false);
		chkBit26.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit26.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit26.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit26.setBounds(102, 118, 21, 21);
		chkBit26.setToolTipText("Bit 26");
		grbValues.add(chkBit26);

		lblBit26 = new JLabel("26");
		lblBit26.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit26.setBounds(102, 138, 20, 10);
		grbValues.add(lblBit26);

		chkBit27 = new JCheckBox("");
		chkBit27.setEnabled(false);
		chkBit27.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit27.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit27.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit27.setBounds(82, 118, 21, 21);
		chkBit27.setToolTipText("Bit 27");
		grbValues.add(chkBit27);

		lblBit27 = new JLabel("27");
		lblBit27.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit27.setBounds(82, 138, 20, 10);
		grbValues.add(lblBit27);

		chkBit28 = new JCheckBox("");
		chkBit28.setEnabled(false);
		chkBit28.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit28.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit28.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit28.setBounds(62, 118, 21, 21);
		chkBit28.setToolTipText("Bit 28");
		grbValues.add(chkBit28);

		lblBit28 = new JLabel("28");
		lblBit28.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit28.setBounds(62, 138, 20, 10);
		grbValues.add(lblBit28);

		chkBit29 = new JCheckBox("");
		chkBit29.setEnabled(false);
		chkBit29.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit29.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit29.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit29.setBounds(42, 118, 21, 21);
		chkBit29.setToolTipText("Bit 29");
		grbValues.add(chkBit29);

		lblBit29 = new JLabel("29");
		lblBit29.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit29.setBounds(42, 138, 20, 10);
		grbValues.add(lblBit29);

		chkBit30 = new JCheckBox("");
		chkBit30.setEnabled(false);
		chkBit30.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit30.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit30.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit30.setBounds(22, 118, 21, 21);
		chkBit30.setToolTipText("Bit 30");
		grbValues.add(chkBit30);

		lblBit30 = new JLabel("30");
		lblBit30.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit30.setBounds(22, 138, 20, 10);
		grbValues.add(lblBit30);

		chkBit31 = new JCheckBox("");
		chkBit31.setEnabled(false);
		chkBit31.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkBit31.setHorizontalTextPosition(SwingConstants.CENTER);
		chkBit31.setHorizontalAlignment(SwingConstants.CENTER);
		chkBit31.setBounds(3, 118, 21, 21);
		chkBit31.setToolTipText("Bit 31");
		grbValues.add(chkBit31);

		lblBit31 = new JLabel("31");
		lblBit31.setHorizontalAlignment(SwingConstants.CENTER);
		lblBit31.setBounds(3, 138, 20, 10);
		grbValues.add(lblBit31);

		btnCopyLogtoClipboard = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnSaveLogtoClipboard_Text"));
		btnCopyLogtoClipboard.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/copy.png")));
		btnCopyLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnCopyLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnCopyLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCopyLogtoClipboard.setBounds(19, 724, 68, 68);
		btnCopyLogtoClipboard.setToolTipText(btnCopyLogtoClipboard.getText());
		contentPane.add(btnCopyLogtoClipboard);

		btnCopyLogtoFile = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnCopySlaveLogtoFile_Text"));
		btnCopyLogtoFile
				.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/data_floppy_disk.png")));
		btnCopyLogtoFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnCopyLogtoFile.setMargin(new Insets(0, 0, 0, 0));
		btnCopyLogtoFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCopyLogtoFile.setBounds(97, 724, 68, 68);
		btnCopyLogtoFile.setToolTipText(btnCopyLogtoFile.getText());
		contentPane.add(btnCopyLogtoFile);

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
		btnCertificateManager.setBounds(745, 724, 68, 68);
		btnCertificateManager.setToolTipText(resources.getString("btnCertificateManager_ToolTip_Text"));
		contentPane.add(btnCertificateManager);

		btnLoadSettings = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnLoadSettings_Text"));
		btnLoadSettings
				.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/folder_document.png")));
		btnLoadSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnLoadSettings.setMargin(new Insets(0, 0, 0, 0));
		btnLoadSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		btnLoadSettings.setBounds(830, 724, 68, 68);
		btnLoadSettings.setToolTipText(btnLoadSettings.getText());
		contentPane.add(btnLoadSettings);

		btnSaveSettings = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnSaveSettings_Text"));
		btnSaveSettings
				.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/SaveSettings.png")));
		btnSaveSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveSettings.setMargin(new Insets(0, 0, 0, 0));
		btnSaveSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveSettings.setBounds(912, 724, 68, 68);
		btnSaveSettings.setToolTipText(btnSaveSettings.getText());
		contentPane.add(btnSaveSettings);

		btnClose = new JButton(ResourceBundle.getBundle("example_app.resources.resources").getString("btnClose_Text"));
		btnClose.setIcon(new ImageIcon(SlaveExample.class.getResource("/example_app/resources/exit.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(997, 724, 68, 68);
		btnClose.setToolTipText(btnClose.getText());
		contentPane.add(btnClose);

		cmbLogLevel = new JComboBox<eLogLevel>();
		cmbLogLevel.setBounds(233, 724, 91, 21);
		contentPane.add(cmbLogLevel);

		lblLoglevel = new JLabel("LogLevel");
		lblLoglevel.setBounds(176, 727, 51, 13);
		contentPane.add(lblLoglevel);

		chkLogging = new JCheckBox(
				ResourceBundle.getBundle("example_app.resources.resources").getString("chkLogging_Text"));
		chkLogging.setSelected(true);
		chkLogging.setBounds(342, 723, 138, 23);
		contentPane.add(chkLogging);

		chkAutoScroll = new JCheckBox("auto scroll");
		chkAutoScroll.setSelected(true);
		chkAutoScroll.setBounds(342, 746, 138, 23);
		contentPane.add(chkAutoScroll);

		lblLanguage = new JLabel(resources.getString("lblLanguage_Text"));
		lblLanguage.setBounds(585, 727, 72, 13);
		contentPane.add(lblLanguage);

		cmbLanguage = new JComboBox<String>();
		cmbLanguage.setBounds(663, 724, 58, 21);
		cmbLanguage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbLanguage_actionPerformed(e);
			}
		});
		contentPane.add(cmbLanguage);
		configureLanguageSelector();

		chkSupportsFC01 = new JCheckBox("");
		chkSupportsFC01.setName("chkSupportsFC01");
		chkSupportsFC01.setSelected(true);
		chkSupportsFC01.setHorizontalAlignment(SwingConstants.CENTER);
		chkSupportsFC01.setBounds(17, 13, 21, 21);
		chkSupportsFC01.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC01.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC01.setToolTipText("FC01");
		grbSupportedFunctions.add(chkSupportsFC01);

		chkSupportsFC02 = new JCheckBox("");
		chkSupportsFC02.setName("chkSupportsFC02");
		chkSupportsFC02.setSelected(true);
		chkSupportsFC02.setBounds(70, 13, 21, 21);
		chkSupportsFC02.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC02.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC02.setToolTipText("FC02");
		grbSupportedFunctions.add(chkSupportsFC02);

		chkSupportsFC03 = new JCheckBox("");
		chkSupportsFC03.setName("chkSupportsFC03");
		chkSupportsFC03.setSelected(true);
		chkSupportsFC03.setBounds(123, 13, 21, 21);
		chkSupportsFC03.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC03.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC03.setToolTipText("FC03");
		grbSupportedFunctions.add(chkSupportsFC03);

		chkSupportsFC04 = new JCheckBox("");
		chkSupportsFC04.setName("chkSupportsFC04");
		chkSupportsFC04.setSelected(true);
		chkSupportsFC04.setBounds(176, 13, 21, 21);
		chkSupportsFC04.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC04.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC04.setToolTipText("FC04");
		grbSupportedFunctions.add(chkSupportsFC04);

		chkSupportsFC05 = new JCheckBox("");
		chkSupportsFC05.setName("chkSupportsFC05");
		chkSupportsFC05.setSelected(true);
		chkSupportsFC05.setBounds(229, 13, 21, 21);
		chkSupportsFC05.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC05.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC05.setToolTipText("FC05");
		grbSupportedFunctions.add(chkSupportsFC05);

		chkSupportsFC06 = new JCheckBox("");
		chkSupportsFC06.setName("chkSupportsFC06");
		chkSupportsFC06.setSelected(true);
		chkSupportsFC06.setHorizontalAlignment(SwingConstants.CENTER);
		chkSupportsFC06.setBounds(17, 42, 21, 21);
		chkSupportsFC06.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC06.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC06.setToolTipText("FC06");
		grbSupportedFunctions.add(chkSupportsFC06);

		chkSupportsFC08 = new JCheckBox("");
		chkSupportsFC08.setName("chkSupportsFC08");
		chkSupportsFC08.setSelected(true);
		chkSupportsFC08.setBounds(70, 42, 21, 21);
		chkSupportsFC08.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC08.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC08.setToolTipText("FC08");
		grbSupportedFunctions.add(chkSupportsFC08);

		chkSupportsFC15 = new JCheckBox("");
		chkSupportsFC15.setName("chkSupportsFC15");
		chkSupportsFC15.setSelected(true);
		chkSupportsFC15.setBounds(123, 42, 21, 21);
		chkSupportsFC15.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC15.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC15.setToolTipText("FC15");
		grbSupportedFunctions.add(chkSupportsFC15);

		chkSupportsFC16 = new JCheckBox("");
		chkSupportsFC16.setName("chkSupportsFC16");
		chkSupportsFC16.setSelected(true);
		chkSupportsFC16.setBounds(176, 42, 21, 21);
		chkSupportsFC16.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC16.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC16.setToolTipText("FC16");
		grbSupportedFunctions.add(chkSupportsFC16);

		chkSupportsFC23 = new JCheckBox("");
		chkSupportsFC23.setName("chkSupportsFC23");
		chkSupportsFC23.setSelected(true);
		chkSupportsFC23.setBounds(229, 42, 21, 21);
		chkSupportsFC23.setHorizontalTextPosition(SwingConstants.CENTER);
		chkSupportsFC23.setVerticalTextPosition(SwingConstants.BOTTOM);
		chkSupportsFC23.setToolTipText("FC23");
		grbSupportedFunctions.add(chkSupportsFC23);

		lblFC01 = new JLabel("FC01");
		lblFC01.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC01.setBounds(8, 33, 40, 10);
		grbSupportedFunctions.add(lblFC01);

		lblFC02 = new JLabel("FC02");
		lblFC02.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC02.setBounds(61, 33, 40, 10);
		grbSupportedFunctions.add(lblFC02);

		lblFC03 = new JLabel("FC03");
		lblFC03.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC03.setBounds(114, 33, 40, 10);
		grbSupportedFunctions.add(lblFC03);

		lblFC04 = new JLabel("FC04");
		lblFC04.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC04.setBounds(167, 33, 40, 10);
		grbSupportedFunctions.add(lblFC04);

		lblFC05 = new JLabel("FC05");
		lblFC05.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC05.setBounds(220, 33, 40, 10);
		grbSupportedFunctions.add(lblFC05);

		lblFC06 = new JLabel("FC06");
		lblFC06.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC06.setBounds(8, 62, 40, 10);
		grbSupportedFunctions.add(lblFC06);

		lblFC08 = new JLabel("FC08");
		lblFC08.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC08.setBounds(61, 62, 40, 10);
		grbSupportedFunctions.add(lblFC08);

		lblFC15 = new JLabel("FC15");
		lblFC15.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC15.setBounds(114, 62, 40, 10);
		grbSupportedFunctions.add(lblFC15);

		lblFC16 = new JLabel("FC16");
		lblFC16.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC16.setBounds(167, 62, 40, 10);
		grbSupportedFunctions.add(lblFC16);

		lblFC23 = new JLabel("FC23");
		lblFC23.setHorizontalAlignment(SwingConstants.CENTER);
		lblFC23.setBounds(220, 62, 40, 10);
		grbSupportedFunctions.add(lblFC23);

		cmbRegisterMode = new JComboBox<eRegisterMode>();
		cmbRegisterMode.setBounds(90, 15, 60, 21);
		cmbRegisterMode.setToolTipText(cmbRegisterMode.getSelectedItem() == null
				? ResourceBundle.getBundle("example_app.resources.resources").getString("lblRegisterMode_Text")
				: cmbRegisterMode.getSelectedItem().toString());
		grbSlaveSettings.add(cmbRegisterMode);

		lblRegisterMode = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblRegisterMode_Text"));
		lblRegisterMode.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRegisterMode.setBounds(8, 20, 77, 13);
		grbSlaveSettings.add(lblRegisterMode);

		lblSlaveID = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblSlaveID_Text"));
		lblSlaveID.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSlaveID.setBounds(154, 18, 55, 13);
		grbSlaveSettings.add(lblSlaveID);

		cmbSlaveID = new JComboBox<Integer>();
		cmbSlaveID.setBounds(214, 15, 60, 21);
		grbSlaveSettings.add(cmbSlaveID);

		cmbByteOrder = new JComboBox<eByteOrder>();
		cmbByteOrder.setBounds(90, 43, 183, 21);
		grbSlaveSettings.add(cmbByteOrder);

		lblByteOrder = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblByteOrder_Text"));
		lblByteOrder.setHorizontalAlignment(SwingConstants.TRAILING);
		lblByteOrder.setBounds(1, 46, 87, 13);
		grbSlaveSettings.add(lblByteOrder);

		// ############### begin init lvListener #####################
		lvListener = new JTable();
		lvListener.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvListener.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvListener.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { ResourceBundle.getBundle("example_app.resources.resources").getString("colName_Text"),
						ResourceBundle.getBundle("example_app.resources.resources").getString("colType_Text"),
						ResourceBundle.getBundle("example_app.resources.resources").getString("colParameter_Text") }) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		lvListener.getColumnModel().getColumn(0).setResizable(true);
		lvListener.getColumnModel().getColumn(0).setPreferredWidth(85);
		lvListener.getColumnModel().getColumn(1).setResizable(true);
		lvListener.getColumnModel().getColumn(1).setPreferredWidth(43);
		lvListener.getColumnModel().getColumn(2).setResizable(true);
		lvListener.getColumnModel().getColumn(2).setPreferredWidth(557);
		lvListener.setBounds(8, 165, 685, 195);

		JScrollPane scrollPanelvListener = new JScrollPane(lvListener);
		lvListener.setFillsViewportHeight(true);

		JPanel lvListenerContainer = new JPanel();

		lvListenerContainer.setLayout(new BorderLayout());
		lvListenerContainer.add(lvListener.getTableHeader(), BorderLayout.PAGE_START);
		lvListenerContainer.add(scrollPanelvListener, BorderLayout.NORTH);

		lvListenerContainer.setBounds(lvListener.getBounds());
		grbListener.add(lvListenerContainer);

		// ############### end init lvListener #####################

		// ############### begin init lvValues #####################
		lvValues = new JTable();
		lvValues.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvValues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvValues.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { ResourceBundle.getBundle("example_app.resources.resources").getString("colAddress_Text"),
						ResourceBundle.getBundle("example_app.resources.resources").getString("colValue_Text") }) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});

		// set renderer for colored rows
		lvValues.setDefaultRenderer(Object.class, new UniversalTableCellRenderer(lvValues, JLabel.CENTER, Font.PLAIN,
				lvValues.getForeground(), lvValues.getBackground()));

		// centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		//
		// lvValues.setDefaultRenderer(String.class, centerRenderer);

		lvValues.getColumnModel().getColumn(0).setResizable(true);
		lvValues.getColumnModel().getColumn(0).setPreferredWidth(60);
		lvValues.getColumnModel().getColumn(1).setResizable(true);
		lvValues.getColumnModel().getColumn(1).setPreferredWidth(268);

		lvValues.setBounds(6, 230, 328, 195);

		JScrollPane scrollPanelvValues = new JScrollPane(lvValues);
		lvValues.setFillsViewportHeight(true);

		JPanel lvValuesContainer = new JPanel();

		lvValuesContainer.setLayout(new BorderLayout());
		lvValuesContainer.add(lvValues.getTableHeader(), BorderLayout.PAGE_START);
		lvValuesContainer.add(scrollPanelvValues, BorderLayout.CENTER);

		lvValuesContainer.setBounds(lvValues.getBounds());
		grbDataStore.add(lvValuesContainer);

		// ############### end init lvValues #####################

		// ############### begin init lvLog #####################
		lvLog = new JTable();
		lvLog.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvLog.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "LogLevel",
				ResourceBundle.getBundle("example_app.resources.resources").getString("colTimeStamp_Text"), "Text" })

		{
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

		});

		// set cell renderer for colored rows
		lvLog.setDefaultRenderer(Object.class, new LogTableCellRenderer(lvLog.getDefaultRenderer(Object.class)));

		// set header renderer for horizontal alignment left
		DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) lvLog.getTableHeader()
				.getDefaultRenderer();
		headerRenderer.setHorizontalAlignment(JLabel.LEFT);

		// set columns
		lvLog.getColumnModel().getColumn(0).setResizable(true);
		lvLog.getColumnModel().getColumn(0).setPreferredWidth(70);
		lvLog.getColumnModel().getColumn(1).setResizable(true);
		lvLog.getColumnModel().getColumn(1).setPreferredWidth(110);
		lvLog.getColumnModel().getColumn(2).setResizable(true);
		lvLog.getColumnModel().getColumn(2).setPreferredWidth(6000);
		lvLog.setBounds(11, 17, 1041, 246);
		lvLog.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		lvLog.setAutoscrolls(true);

		JScrollPane scrollPanelvLog = new JScrollPane(lvLog);
		lvLog.setFillsViewportHeight(true);

		JPanel lvLogContainer = new JPanel();

		lvLogContainer.setLayout(new BorderLayout());
		lvLogContainer.add(lvLog.getTableHeader(), BorderLayout.PAGE_START);
		lvLogContainer.add(scrollPanelvLog, BorderLayout.CENTER);

		lvLogContainer.setBounds(lvLog.getBounds());
		grbLog.add(lvLogContainer);
		// ############### end init lvLog #####################

		WorkshopUiScaler.apply(this);
	}

	private void addControlListener_to_Button() {

		btnStartSlave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStartSlave_actionPerformed(e);
			}
		});

		btnStopSlave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStopSlave_actionPerformed(e);
			}
		});

		btnAddListener.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAddListener_actionPerformed(e);
			}
		});

		btnRemoveListener.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRemoveListener_actionPerformed(e);
			}
		});

		btnSetValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSetValue_actionPerformed(e);
			}
		});

		btnCopyLogtoClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCopyLogtoClipboard_actionPerformed(e);
			}
		});

		btnCopyLogtoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCopyLogtoFile_actionPerformed(e);
			}
		});

		btnLoadSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLoadSettings_actionPerformed(e);
			}
		});

		btnSaveSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveSettings_actionPerformed(e);
			}
		});

		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
	}

	private void addControlListener_to_CheckBoxes() {

		chkBit0.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit5.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit6.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit7.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit8.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit9.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit10.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit11.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit12.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit13.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit14.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit15.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit16.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit17.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit18.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit19.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit20.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit21.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit22.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit23.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit24.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit25.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit26.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit27.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit28.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit29.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit30.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkBit31.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chkBit_mouseClicked(e);
			}
		});

		chkSupportsFC01.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC01, e);
			}
		});

		chkSupportsFC02.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC02, e);
			}
		});

		chkSupportsFC03.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC03, e);
			}
		});

		chkSupportsFC04.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC04, e);
			}
		});

		chkSupportsFC05.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC05, e);
			}
		});

		chkSupportsFC06.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC06, e);
			}
		});

		chkSupportsFC08.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC08, e);
			}
		});

		chkSupportsFC15.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC15, e);
			}
		});

		chkSupportsFC16.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC16, e);
			}
		});

		chkSupportsFC23.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkSupportsFC_CheckedChanged(chkSupportsFC23, e);
			}
		});
	}

	private void addControlListener_to_OtherControls() {

		rbRegisterTypeCoils.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				rbRegisterTypeCoils_itemStateChanged(e);
			}
		});

		rbRegisterTypeDiscreteInputs.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				rbRegisterTypeDiscreteInputs_itemStateChanged(e);
			}
		});

		rbRegisterTypeHoldingRegister.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				rbRegisterTypeHoldingRegister_itemStateChanged(e);
			}
		});

		rbRegisterTypeInputRegister.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				rbRegisterTypeInputRegister_itemStateChanged(e);
			}
		});

		txtAddress.getDocument().addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				txtAddress_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtAddress_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtAddress_TextChanged(e);
			}
		});

		txtValue.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtValue_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtValue_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtValue_TextChanged(e);
			}
		});

		lvListener.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lvListener_SelectedIndexChanged(e);
			}
		});

		lvValues.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lvValues_SelectedIndexChanged(e);
			}
		});
	}

	// <editor-fold defaultstate="collapsed" desc="init checkboxes">

	private void initCheckBoxArray() {
		// initialize checkbox array
		myCheckBoxes.addCheckBox(chkBit0);
		myCheckBoxes.addCheckBox(chkBit1);
		myCheckBoxes.addCheckBox(chkBit2);
		myCheckBoxes.addCheckBox(chkBit3);
		myCheckBoxes.addCheckBox(chkBit4);
		myCheckBoxes.addCheckBox(chkBit5);
		myCheckBoxes.addCheckBox(chkBit6);
		myCheckBoxes.addCheckBox(chkBit7);
		myCheckBoxes.addCheckBox(chkBit8);
		myCheckBoxes.addCheckBox(chkBit9);
		myCheckBoxes.addCheckBox(chkBit10);
		myCheckBoxes.addCheckBox(chkBit11);
		myCheckBoxes.addCheckBox(chkBit12);
		myCheckBoxes.addCheckBox(chkBit13);
		myCheckBoxes.addCheckBox(chkBit14);
		myCheckBoxes.addCheckBox(chkBit15);
		myCheckBoxes.addCheckBox(chkBit16);
		myCheckBoxes.addCheckBox(chkBit17);
		myCheckBoxes.addCheckBox(chkBit18);
		myCheckBoxes.addCheckBox(chkBit19);
		myCheckBoxes.addCheckBox(chkBit20);
		myCheckBoxes.addCheckBox(chkBit21);
		myCheckBoxes.addCheckBox(chkBit22);
		myCheckBoxes.addCheckBox(chkBit23);
		myCheckBoxes.addCheckBox(chkBit24);
		myCheckBoxes.addCheckBox(chkBit25);
		myCheckBoxes.addCheckBox(chkBit26);
		myCheckBoxes.addCheckBox(chkBit27);
		myCheckBoxes.addCheckBox(chkBit28);
		myCheckBoxes.addCheckBox(chkBit29);
		myCheckBoxes.addCheckBox(chkBit30);
		myCheckBoxes.addCheckBox(chkBit31);

	}

	// </editor-fold>

	private void formWindowOpened(java.awt.event.WindowEvent evt) {

		// init virtual lvValues listview filler
		LoadValuesIn_LvValuesAsync();

		// set DataSource to comboboxes
		cmbRegisterMode.setModel(new DefaultComboBoxModel<eRegisterMode>(eRegisterMode.values()));

		cmbLogLevel.setModel(new DefaultComboBoxModel<eLogLevel>(eLogLevel.values()));
		cmbByteOrder.setModel(new DefaultComboBoxModel<eByteOrder>(eByteOrder.values()));

		// init cmbSlaveID and set DataSource to combobox
		for (int i = 1; i < 248; i++) {
			cmbSlaveID.addItem(i);
		}
		cmbSlaveID.setSelectedItem(1);
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
		setTitle(resources.getString("main_Text"));
		lblSerialCode.setText(resources.getString("lblSerialCode_Text"));
		setBorderTitle(grbListener, resources.getString("grbListener_Text"));
		setBorderTitle(grbSlaveSettings, resources.getString("grbSlaveSettings_Text"));
		lblByteOrder.setText(resources.getString("lblByteOrder_Text"));
		setBorderTitle(grbSupportedFunctions, resources.getString("grbSupportedFunctions_Text"));
		setBorderTitle(grbStatistics, resources.getString("grbStatistics_Text"));
		lblIncomingBytes.setText(resources.getString("lblIncomingBytes_Text"));
		lblOutgoingBytes.setText(resources.getString("lblOutgoingBytes_Text"));
		lblIncomingRequests.setText(resources.getString("lblIncomingRequests_Text"));
		lblIncorrectRequests.setText(resources.getString("lblIncorrectRequests_Text"));
		lblUnparseableRequests.setText(resources.getString("lblUnparseableRequests_Text"));
		setBorderTitle(grbDataStore, resources.getString("grbDataStore_Text"));
		setBorderTitle(grbAddressType, resources.getString("grbAddressType_Text"));
		setBorderTitle(grbValues, resources.getString("grbValues_Text"));
		lblAddress.setText(resources.getString("lblAddress_Text"));
		lblValue.setText(resources.getString("lblValue_Text"));
		btnSetValue.setText(resources.getString("btnSetValue_Text"));
		updateTableHeader(lvValues, 0, resources.getString("colAddress_Text"));
		updateTableHeader(lvValues, 1, resources.getString("colValue_Text"));
		updateTableHeader(lvListener, 0, resources.getString("colName_Text"));
		updateTableHeader(lvListener, 1, resources.getString("colType_Text"));
		updateTableHeader(lvListener, 2, resources.getString("colParameter_Text"));
		setBorderTitle(grbLog, resources.getString("grbLog_Text"));
		chkLogging.setText(resources.getString("chkLogging_Text"));
		btnCopyLogtoFile.setText(resources.getString("btnCopySlaveLogtoFile_Text"));
		btnCopyLogtoClipboard.setText(resources.getString("btnSaveLogtoClipboard_Text"));
		btnLoadSettings.setText(resources.getString("btnLoadSettings_Text"));
		btnSaveSettings.setText(resources.getString("btnSaveSettings_Text"));
		btnClose.setText(resources.getString("btnClose_Text"));
		btnStartSlave.setText(resources.getString("btnStartSlave_Text"));
		btnStopSlave.setText(resources.getString("btnStopSlave_Text"));
		btnAddListener.setText(resources.getString("btnAddListener_Text"));
		btnRemoveListener.setText(resources.getString("btnRemoveListener_Text"));
		lblRegisterMode.setText(resources.getString("lblRegisterMode_Text"));
		lblSlaveID.setText(resources.getString("lblSlaveID_Text"));
		lblLanguage.setText(resources.getString("lblLanguage_Text"));
		btnCertificateManager.setText(resources.getString("btnCertificateManager_Text"));
		updateCertificateAlertState();
		contentPane.revalidate();
		contentPane.repaint();
	}

	private void setBorderTitle(JPanel panel, String title) {
		if (panel != null && panel.getBorder() instanceof TitledBorder) {
			((TitledBorder) panel.getBorder()).setTitle(title);
		}
	}

	private void updateTableHeader(JTable table, int columnIndex, String title) {
		if (table != null && columnIndex >= 0 && columnIndex < table.getColumnModel().getColumnCount()) {
			table.getColumnModel().getColumn(columnIndex).setHeaderValue(title);
			table.getTableHeader().repaint();
		}
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

	void btnStartSlave_actionPerformed(ActionEvent e) {
		try {
			lvLog.removeAll();

			// create new modbus slave
			slave = new ModbusSlave(txtUser.getText(), txtSerial.getText(), (int) cmbSlaveID.getSelectedItem(),
					(eRegisterMode) cmbRegisterMode.getSelectedItem(), (eByteOrder) cmbByteOrder.getSelectedItem());

			// adding slave events
			slave.addIncomingLogEntryEventHandler(incomingLogEntryEventHandler);
			slave.addDataStoreItemChangeEventHandler(dataStoreItemChangeEventHandler);
			slave.addDataStoreItemReadEventHandler(dataStoreItemReadEventHandler);
			slave.addPortStateEventHandler(portStateEventHandler);

			// set controls
			btnStartSlave.setEnabled(false);
			panSlaveSettings.setEnabled(false);
			panSupportedFunctions.setEnabled(true);
			panDataStore.setEnabled(true);
			rbRegisterTypeCoils.setSelected(true);

			btnStopSlave.setEnabled(true);
			btnAddListener.setEnabled(true);
			txtValue.setText("0");
			txtUser.setEnabled(false);
			txtSerial.setEnabled(false);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	void btnStopSlave_actionPerformed(ActionEvent arg0) {
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// unload existing listener
			String[] ListenerKeys = slave.getListener().keySet()
					.toArray(new String[slave.getListener().keySet().size()]);
			for (String key : ListenerKeys) {
				slave.removeListener(key);
			}

			// unregister slave events
			slave.removeIncomingLogEntryEventHandler(incomingLogEntryEventHandler);
			slave.removeDataStoreItemChangeEventHandler(dataStoreItemChangeEventHandler);
			slave.removeDataStoreItemReadEventHandler(dataStoreItemReadEventHandler);
			slave.removePortStateEventHandler(portStateEventHandler);

			RefreshListViewListener();

			// dispose modbus slave
			slave.unload();
			slave = null;

			// set controls
			panDataStore.setEnabled(false);
			panSlaveSettings.setEnabled(true);
			panSupportedFunctions.setEnabled(false);
			btnStartSlave.setEnabled(true);
			cmbRegisterMode.setEnabled(true);
			cmbSlaveID.setEnabled(true);
			// lvValues.Refresh();
			btnStopSlave.setEnabled(false);
			btnAddListener.setEnabled(false);
			btnRemoveListener.setEnabled(false);
			txtValue.setText("0");
			txtUser.setEnabled(true);
			txtSerial.setEnabled(true);
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	void btnAddListener_actionPerformed(ActionEvent e) {

		// ListenerSettings stuff = new ListenerSettings();
		// stuff.CreateListener(1);

		try {
			// create new listener
			ListenerSettings frmListenerSettings = new ListenerSettings();
			HashMap<String, Object> Args = frmListenerSettings.CreateListener(slave.getListener().size());

			// add or replace listener
			if (Args.size() > 0 && !(Args.get("ListerName") == null || Args.get("ListerName") == "")) {
				switch ((eTypeOfCommunication) Args.get("TypeOfCommunication")) {
				case TCP:
					slave.addOrReplaceListener_TCP((String) Args.get("ListerName"), (int) Args.get("LocalPort"));
					break;
				case SecureTCP:
					slave.addOrReplaceListener_SecureTCP((String) Args.get("ListerName"), (int) Args.get("LocalPort"),
							prepareSecureTcpOptionsForUi(SecureTcpExampleOptions.create()));
					break;
				case UDP:
					slave.addOrReplaceListener_UDP((String) Args.get("ListerName"), (int) Args.get("LocalPort"));
					break;
				case RTU_over_TCP:
					slave.addOrReplaceListener_RTU_over_TCP((String) Args.get("ListerName"),
							(int) Args.get("LocalPort"));
					break;
				case RTU:
					slave.addOrReplaceListener_RTU((String) Args.get("ListerName"), (String) Args.get("SerialPortName"),
							(eBaudrate) Args.get("Baudrate"), (eDataBits) Args.get("DataBits"),
							(eParity) Args.get("Parity"), (eStopBits) Args.get("StopBits"),
							(eFlowControl) Args.get("FlowControl"));
					break;
				case ASCII:
					slave.addOrReplaceListener_ASCII((String) Args.get("ListerName"),
							(String) Args.get("SerialPortName"), (eBaudrate) Args.get("Baudrate"),
							(eDataBits) Args.get("DataBits"), (eParity) Args.get("Parity"),
							(eStopBits) Args.get("StopBits"), (eFlowControl) Args.get("FlowControl"));
					break;
				default:
					break;
				}
			}
		} catch (Throwable ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			RefreshListViewListener();
		}

	}

	void btnRemoveListener_actionPerformed(ActionEvent e) {
		try {
			// removed named listener
			if (lvListener.getSelectedRow() > -1) {
				slave.removeListener((String) lvListener.getModel().getValueAt(lvListener.getSelectedRow(), 0));
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			RefreshListViewListener();
		}
	}

	/**
	 * Updates the statistics display after the slave data store has been read.
	 *
	 * @param arg
	 *            read-event arguments reported by the PLCcom slave data store
	 */
	@Override
	public void OnDataStoreItemRead(final List<DataStoreItemReadArg> arg) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					OnDataStoreItemRead(arg);
				}
			});
			return;
		}

		updateStatisticsDisplay();

	}

	/**
	 * Updates the visible value table after one or more data-store items changed.
	 *
	 * @param args
	 *            change-event arguments reported by the PLCcom slave data store
	 */
	@Override
	public void OnDataStoreItemChange(final List<DataStoreItemChangeArg> args) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					OnDataStoreItemChange(args);
				}
			});
			return;
		}
		if (args == null) {
			return;
		}

		// set new values in listview
		for (DataStoreItemChangeArg e : args) {
			if (e == null || e.getAddress() == null || e.getModbusRegion() == null) {
				continue;
			}
			switch (e.getModbusRegion()) {
			case Coil:
				if (!rbRegisterTypeCoils.isSelected())
					continue;
				break;
			case Input:
				if (!rbRegisterTypeDiscreteInputs.isSelected())
					continue;
				break;
			case HoldingRegister:
				if (!rbRegisterTypeHoldingRegister.isSelected())
					continue;
				break;
			case InputRegister:
				if (!rbRegisterTypeInputRegister.isSelected())
					continue;
				break;
			default:
				continue;
			}

			int row = e.getAddress().intValue();
			if (row < 0 || row >= lvValues.getModel().getRowCount()) {
				continue;
			}
			lvValues.getModel().setValueAt(e.getNewValue(), row, 1);

			// refresh ListView only by change of values
			if (!Objects.equals(e.getOldValue(), e.getNewValue())) {
				((AbstractTableModel) lvValues.getModel()).fireTableDataChanged();
			}

			updateStatisticsDisplay();
		}

	}

	/**
	 * Appends incoming diagnostic log entries to the workshop log table.
	 *
	 * @param args
	 *            log entries raised by PLCcom components
	 */
	@Override
	public void OnIncomingLogEntry(final LogEntry[] args) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					OnIncomingLogEntry(args);
				}
			});
			return;
		}
		if (args == null || !chkLogging.isSelected()) {
			return;
		}

		// write LogEntry into listview
		for (LogEntry le : args) {
			if (le == null) {
				continue;
			}
			if (le.getLogLevel().getValue() < ((eLogLevel) cmbLogLevel.getSelectedItem()).getValue())
				return;

			try {
				mLock.lock();

				// adding row
				DefaultTableModel model = (DefaultTableModel) lvLog.getModel();
				model.addRow(new Object[] { le.getLogLevel().toString(),
						DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
						le.getText() + " " + le.getStackTraceString() });

				while (model.getRowCount() > 100) {
					model.removeRow(0);
				}

				// ensure last row is visible
				if (chkAutoScroll.isSelected() && model.getRowCount() > 1) {
					Rectangle rect = lvLog.getCellRect(model.getRowCount() - 1, 0, true);
					lvLog.scrollRectToVisible(rect);
				}

				((AbstractTableModel) lvLog.getModel()).fireTableDataChanged();
			} finally {
				mLock.unlock();
			}
		}
	}

	private void updateStatisticsDisplay() {
		if (slave == null) {
			return;
		}
		Statistics st = slave.getStatistics();
		txtIncomingBytes.setText(String.valueOf(st.getNumberIncomingBytes()));
		txtOutgoingBytes.setText(String.valueOf(st.getNumberOutgoingBytes()));
		txtIncomingRequests.setText(String.valueOf(st.getNumberIncomingRequests()));
		txtIncorectRequests.setText(String.valueOf(st.getNumberIncorrectRequests()));
		txtUnparseableRequests.setText(String.valueOf(st.getNumberUnparseableRequests()));
	}

	/**
	 * Receives connection-state changes from the slave listeners.
	 *
	 * @param arg
	 *            the new connection or listener state
	 */
	@Override
	public void On_ConnectionStateChange(eConnectionState arg) {
		// this event occurs when changing the connection or port status
	}

	void lvListener_SelectedIndexChanged(ListSelectionEvent e) {
		// enable or disable btnRemoveListener
		btnRemoveListener.setEnabled(lvListener.getSelectedRow() > -1);
	}

	private void RefreshListViewListener() {
		// refresh listener listview
		try {
			if (slave == null)
				return;
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			DefaultTableModel model = (DefaultTableModel) lvListener.getModel();

			// clear model
			while (model.getRowCount() > 0) {
				model.removeRow(0);
			}

			// get all active listener, and put entrys into current model
			for (Entry<String, ModbusListener> entry : slave.getListener().entrySet()) {
				ModbusListener ml = entry.getValue();
				model.addRow(new Object[] { ml.getName(), String.valueOf(ml.getTypeOfCommunication()),
						ml.getListenerParameterString() });
			}
			model.fireTableDataChanged();
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	protected void rbRegisterTypeCoils_itemStateChanged(ItemEvent e) {
		// select Coils
		if (e.getStateChange() == ItemEvent.SELECTED) {
			LoadValuesIn_LvValuesAsync();
			grbBoolValues.setVisible(true);
			grbTextValue.setVisible(false);
			TitledBorder tb = (TitledBorder) grbValues.getBorder();
			tb.setTitle(resources.getString("grbValues_Text"));
			grbValues.setBorder(tb);
			myCheckBoxes.enableBitBarComplete(false, false);
		}
	}

	protected void rbRegisterTypeDiscreteInputs_itemStateChanged(ItemEvent e) {
		// select Discrete Inputs
		if (e.getStateChange() == ItemEvent.SELECTED) {
			LoadValuesIn_LvValuesAsync();
			grbBoolValues.setVisible(true);
			grbTextValue.setVisible(false);
			TitledBorder tb = (TitledBorder) grbValues.getBorder();
			tb.setTitle(resources.getString("grbValues_Text"));
			grbValues.setBorder(tb);
			myCheckBoxes.enableBitBarComplete(false, false);
		}
	}

	protected void rbRegisterTypeHoldingRegister_itemStateChanged(ItemEvent e) {
		// select Holding Register
		if (e.getStateChange() == ItemEvent.SELECTED) {
			LoadValuesIn_LvValuesAsync();
			grbBoolValues.setVisible(false);
			grbTextValue.setVisible(true);
			TitledBorder tb = (TitledBorder) grbValues.getBorder();
			tb.setTitle(resources.getString("grbValues_Text_between")
					+ (slave.GetMaxCountOfHoldingRegister().intValue() - 1));
			grbValues.setBorder(tb);
			myCheckBoxes.enableBitBarComplete(true,
					(eRegisterMode) cmbRegisterMode.getSelectedItem() == eRegisterMode._32Bit
							|| (eRegisterMode) cmbRegisterMode.getSelectedItem() == eRegisterMode._64Bit);
		}
	}

	protected void rbRegisterTypeInputRegister_itemStateChanged(ItemEvent e) {
		// select Input Register
		if (e.getStateChange() == ItemEvent.SELECTED) {
			LoadValuesIn_LvValuesAsync();
			grbBoolValues.setVisible(false);
			grbTextValue.setVisible(true);
			TitledBorder tb = (TitledBorder) grbValues.getBorder();
			tb.setTitle(resources.getString("grbValues_Text_between")
					+ (slave.GetMaxCountOfInputRegister().intValue() - 1));
			grbValues.setBorder(tb);
			myCheckBoxes.enableBitBarComplete(true,
					(eRegisterMode) cmbRegisterMode.getSelectedItem() == eRegisterMode._32Bit
							|| (eRegisterMode) cmbRegisterMode.getSelectedItem() == eRegisterMode._64Bit);
		}

	}

	@SuppressWarnings("unused")
	private void clearLvValues() {
		// clear lvValues
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTableModel model = (DefaultTableModel) lvValues.getModel();

			// clear model
			while (model.getRowCount() > 0) {
				model.removeRow(0);
			}
			model.fireTableDataChanged();
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void LoadValuesIn_LvValuesAsync() {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// clear lvValues
			DefaultTableModel model = (DefaultTableModel) lvValues.getModel();
			// clear model
			while (model.getRowCount() > 0) {
				model.removeRow(0);
			}

			int Address = 0;
			while (true) {

				// load data in virtual listview mode
				if (slave != null) {
					Object value = null;

					if (rbRegisterTypeCoils.isSelected())
						value = slave.getValue(createUshort(Address), eModbusRegion.Coil);
					else if (rbRegisterTypeDiscreteInputs.isSelected())
						value = slave.getValue(createUshort(Address), eModbusRegion.Input);
					else if (rbRegisterTypeHoldingRegister.isSelected())
						value = slave.getValue(createUshort(Address), eModbusRegion.HoldingRegister);
					else if (rbRegisterTypeInputRegister.isSelected())
						value = slave.getValue(createUshort(Address), eModbusRegion.InputRegister);
					if (value != null) {

						model.addRow(new Object[] { String.valueOf(Address), String.valueOf(value) });

					} else {
						model.addRow(new Object[] { String.valueOf(Address), "value is null" });
					}
				} else {
					model.addRow(new Object[] { String.valueOf(Address), resources.getString("slave_is_null") });
					break;
				}
				Address++;

				if (rbRegisterTypeCoils.isSelected() && Address >= slave.GetMaxCountOfCoils().intValue())
					break;
				else if (rbRegisterTypeDiscreteInputs.isSelected()&& Address >= slave.GetMaxCountOfInputs().intValue())
					break;
				else if (rbRegisterTypeHoldingRegister.isSelected()&& Address >= slave.GetMaxCountOfHoldingRegister().intValue())
					break;
				else if (rbRegisterTypeInputRegister.isSelected()&& Address >= slave.GetMaxCountOfInputRegister().intValue())
					break;
			}

			model.fireTableDataChanged();
			txtAddress.setText("0");
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	void btnSetValue_actionPerformed(ActionEvent e) {
		try {
			// set value in datastore to desired value
			if (slave == null) {
				JOptionPane.showMessageDialog(null, resources.getString("slave_is_null"), "",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (rbRegisterTypeCoils.isSelected())
				slave.setValue(createUshort(txtAddress.getText()), eModbusRegion.Coil,
						rbValueOn.isSelected() ? true : false);
			else if (rbRegisterTypeDiscreteInputs.isSelected())
				slave.setValue(createUshort(txtAddress.getText()), eModbusRegion.Input,
						rbValueOn.isSelected() ? true : false);
			else if (rbRegisterTypeHoldingRegister.isSelected()) {
				switch ((eRegisterMode) cmbRegisterMode.getSelectedItem()) {
				case _16Bit:
					// 16Bit = ushort
					slave.setValue(createUshort(txtAddress.getText()), eModbusRegion.HoldingRegister,
							createUshort(txtValue.getText()));
					break;
				case _32Bit:
					// 32Bit = uint
					slave.setValue(createUshort(txtAddress.getText()), eModbusRegion.HoldingRegister,
							createUint(txtValue.getText()));
					break;
				case _64Bit:
					// 64Bit = ulong
					slave.setValue(createUshort(txtAddress.getText()), eModbusRegion.HoldingRegister,
							createUlong(txtValue.getText()));
					break;
				default:
					break;
				}

			} else if (rbRegisterTypeInputRegister.isSelected()) {
				switch ((eRegisterMode) cmbRegisterMode.getSelectedItem()) {
				case _16Bit:
					// 16Bit = ushort
					slave.setValue(createUshort(txtAddress.getText()), eModbusRegion.InputRegister,
							createUshort(txtValue.getText()));
					break;
				case _32Bit:
					// 32Bit = uint
					slave.setValue(createUshort(txtAddress.getText()), eModbusRegion.InputRegister,
							createUint(txtValue.getText()));
					break;
				case _64Bit:
					// 64Bit = ulong
					slave.setValue(createUshort(txtAddress.getText()), eModbusRegion.InputRegister,
							createUlong(txtValue.getText()));
					break;
				default:
					break;
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	void txtAddress_TextChanged(DocumentEvent e) {

		if (lvValues == null || txtAddress.getText().equals(""))
			return;

		// check textbox to numeric values
		if (CheckAddressField(txtAddress.getText())) {
			txtAddress.setBackground(SystemColor.window);

			// ensure addressed row is visible
			if (chkAutoScroll.isSelected() && lvValues.getRowCount() > 1) {
				Rectangle rect = lvValues.getCellRect(Integer.valueOf(txtAddress.getText()), 0, true);
				lvValues.scrollRectToVisible(rect);
			}

		} else {
			txtAddress.setBackground(Color.red);
		}

		if (CheckAddressField(txtAddress.getText()) && CheckValueField(txtValue.getText())) {
			btnSetValue.setEnabled(true);
		} else {
			btnSetValue.setEnabled(false);
		}
	}

	private void txtValue_TextChanged(DocumentEvent e) {

		if (txtValue.getText() == null || txtValue.getText().equals("")) {
			// txtValue.setText("0");
			return;
		}
		// check textbox to numeric values
		if (CheckValueField(txtValue.getText())) {
			txtValue.setBackground(SystemColor.window);

			switch ((eRegisterMode) cmbRegisterMode.getSelectedItem()) {
			case _16Bit:
				myCheckBoxes.setCheckBoxesfromValue(createUshort(txtValue.getText()));
				break;
			case _32Bit:
				myCheckBoxes.setCheckBoxesfromValue(createUint(txtValue.getText()));
				break;
			case _64Bit:
				myCheckBoxes.setCheckBoxesfromValue(createUint(createUlong(txtValue.getText()).longValue()
						& UInteger.MAX_VALUE));
				break;
			}
		} else {
			txtValue.setBackground(Color.red);
		}

		if (CheckAddressField(txtAddress.getText()) && CheckValueField(txtValue.getText())) {
			btnSetValue.setEnabled(true);
		} else {
			btnSetValue.setEnabled(false);
		}
	}

	private boolean CheckAddressField(String text) {
		// check AddressField on allowed values
		if (!(text == null || text == "")) {
			try {
				// try parse to ushort
				createUshort(text);
				// text is numeric
				return true;
			} catch (NumberFormatException ex) {
				// text not numeric
				return false;
			}
		} else {
			// text is empty >> not numeric
			return false;
		}

	}

	private boolean CheckValueField(String text) {
		// check ValueField on allowed values
		try {
			if (rbRegisterTypeCoils.isSelected() || rbRegisterTypeDiscreteInputs.isSelected()) {
				return true;
			} else if (rbRegisterTypeHoldingRegister.isSelected() || rbRegisterTypeInputRegister.isSelected()) {
				switch ((eRegisterMode) cmbRegisterMode.getSelectedItem()) {
				case _16Bit:
					// 16Bit = ushort
					if (!(text == null || text.equals(""))) {
						try {
							createUshort(text);
							// text is numeric
							return true;
						} catch (NumberFormatException ex) {
							// text not numeric
							return false;
						}
					} else {
						// text is empty >> not numeric
						return false;
					}
				case _32Bit:
					// 32Bit = uint
					if (!(text == null || text.equals(""))) {
						try {
							createUint(text);
							// text is numeric
							return true;
						} catch (NumberFormatException ex) {
							// text not numeric
							return false;
						}
					} else {
						// text is empty >> not numeric
						return false;
					}
				case _64Bit:
					// 64Bit = ulong
					if (!(text == null || text.equals(""))) {
						try {
							createUlong(text);
							// text is numeric
							return true;
						} catch (NumberFormatException ex) {
							// text not numeric
							return false;
						}
					} else {
						// text is empty >> not numeric
						return false;
					}

				default:
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
	}

	private void lvValues_SelectedIndexChanged(ListSelectionEvent e) {
		// set value fields from lvValues.SelectedItem
		if (lvValues.getSelectedRow() > -1) {

			UShort Address = UShort.valueOf((String) lvValues.getModel().getValueAt(lvValues.getSelectedRow(), 0));

			txtAddress.setText(String.valueOf(lvValues.getSelectedRow()));

			if (rbRegisterTypeCoils.isSelected()) {
				if ((boolean) slave.getValue(Address, eModbusRegion.Coil)) {
					rbValueOn.setSelected(true);
				} else {
					rbValueOFF.setSelected(true);
				}
			} else if (rbRegisterTypeDiscreteInputs.isSelected()) {
				if ((boolean) slave.getValue(Address, eModbusRegion.Input)) {
					rbValueOn.setSelected(true);
				} else {
					rbValueOFF.setSelected(true);
				}
			} else if (rbRegisterTypeHoldingRegister.isSelected())
				txtValue.setText(slave.getValue(Address, eModbusRegion.HoldingRegister).toString());
			else if (rbRegisterTypeInputRegister.isSelected())
				txtValue.setText((String) slave.getValue(Address, eModbusRegion.InputRegister).toString());
		}
	}

	private void chkBit_mouseClicked(MouseEvent e) {
		// set value field with checkbox settings
		String temptext = myCheckBoxes.getValue().toString();
		if (!txtValue.getText().equals(temptext))
			txtValue.setText(temptext);
	}

	private void chkSupportsFC_CheckedChanged(JCheckBox sender, ItemEvent e) {
		// check if FC01 supported
		if (slave != null) {
			switch (sender.getName()) {
			case "chkSupportsFC01":
				slave.setSupportsFunction01(sender.isSelected());
				break;
			case "chkSupportsFC02":
				slave.setSupportsFunction02(sender.isSelected());
				break;
			case "chkSupportsFC03":
				slave.setSupportsFunction03(sender.isSelected());
				break;
			case "chkSupportsFC04":
				slave.setSupportsFunction04(sender.isSelected());
				break;
			case "chkSupportsFC05":
				slave.setSupportsFunction05(sender.isSelected());
				break;
			case "chkSupportsFC06":
				slave.setSupportsFunction06(sender.isSelected());
				break;
			case "chkSupportsFC08":
				slave.setSupportsFunction08(sender.isSelected());
				break;
			case "chkSupportsFC15":
				slave.setSupportsFunction15(sender.isSelected());
				break;
			case "chkSupportsFC16":
				slave.setSupportsFunction16(sender.isSelected());
				break;
			case "chkSupportsFC23":
				slave.setSupportsFunction23(sender.isSelected());
				break;
			default:
				JOptionPane.showMessageDialog(null, "unknown sender", "", JOptionPane.WARNING_MESSAGE);
				break;
			}

		} else {
			JOptionPane.showMessageDialog(null, resources.getString("action_not_possible_slave_is_null"), "",
					JOptionPane.WARNING_MESSAGE);
			sender.setSelected(true);
		}

	}

	void btnClose_actionPerformed(ActionEvent e) {
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			if (slave != null) {
				// unload and dispose all Listener
				// get all active listener, and remove all listener
				for (Entry<String, ModbusListener> entry : slave.getListener().entrySet()) {
					slave.removeListener(entry.getValue().getName());
				}
				slave.getListener().clear();
				slave.unload();
				slave = null;
			}

		} finally {
			this.setCursor(Cursor.getDefaultCursor());
			System.exit(0);
		}
	}

	private void btnLoadSettings_actionPerformed(ActionEvent e) {

		Properties p = new Properties();
		FileInputStream fis = null;
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// open file
			fis = new FileInputStream(new File("PLCcomModbusSlaveSettings.xml").getAbsolutePath());
			p.loadFromXML(fis);
			fis.close();
			if (slave != null)// unload eventual actual ModbusSlave instance
			{
				btnStopSlave_actionPerformed(null);
			}

			// load saved settiungs from PLCcomModbusSlaveSettings.xml

			if (p.containsKey("user")) {
				txtUser.setText(p.getProperty("user"));
			}
			if (p.containsKey("serial")) {
				txtSerial.setText(p.getProperty("serial"));
			}
			if (p.containsKey("SlaveID")) {
				cmbSlaveID.setSelectedItem(Integer.parseInt(p.getProperty("SlaveID")));
			}
			if (p.containsKey("RegisterMode")) {
				cmbRegisterMode.setSelectedItem(eRegisterMode.valueOf(p.getProperty("RegisterMode")));
			}
			if (p.containsKey("ByteOrder")) {
				cmbByteOrder.setSelectedItem(eByteOrder.valueOf(p.getProperty("ByteOrder")));
			}

			// start slave
			btnStartSlave_actionPerformed(null);

			if (p.containsKey("SupportsFunction01")) {
				chkSupportsFC01.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction01")));
			}
			if (p.containsKey("SupportsFunction02")) {
				chkSupportsFC02.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction02")));
			}
			if (p.containsKey("SupportsFunction03")) {
				chkSupportsFC03.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction03")));
			}
			if (p.containsKey("SupportsFunction04")) {
				chkSupportsFC04.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction04")));
			}
			if (p.containsKey("SupportsFunction05")) {
				chkSupportsFC05.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction05")));
			}
			if (p.containsKey("SupportsFunction06")) {
				chkSupportsFC06.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction06")));
			}
			if (p.containsKey("SupportsFunction08")) {
				chkSupportsFC08.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction08")));
			}
			if (p.containsKey("SupportsFunction15")) {
				chkSupportsFC15.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction15")));
			}
			if (p.containsKey("SupportsFunction16")) {
				chkSupportsFC16.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction16")));
			}
			if (p.containsKey("SupportsFunction23")) {
				chkSupportsFC23.setSelected(Boolean.valueOf(p.getProperty("SupportsFunction23")));
			}

			if (p.containsKey("AutoScroll")) {
				chkAutoScroll.setSelected(Boolean.valueOf(p.getProperty("AutoScroll")));
			}

			if (p.containsKey("LogEnabled")) {
				chkLogging.setSelected(Boolean.valueOf(p.getProperty("LogEnabled")));
			}

			if (p.containsKey("LogLevel")) {
				cmbLogLevel.setSelectedItem(eLogLevel.valueOf(p.getProperty("LogLevel")));
			}

			// load listener
			Enumeration<?> prop = p.propertyNames();
			while (prop.hasMoreElements()) {
				String key = (String) prop.nextElement();
				String value = p.getProperty(key);

				if (key.startsWith("Listener#")) {
					String ListenerName = value;
					eTypeOfCommunication ToC = eTypeOfCommunication
							.valueOf(p.getProperty("TypeOfCommunication" + "@" + ListenerName));
					// create listner
					switch (ToC) {
					case TCP:
						// TCP listener
						if (p.containsKey("LocalPort" + "@" + ListenerName)) {
							slave.addOrReplaceListener_TCP(ListenerName,
									Integer.valueOf(p.getProperty("LocalPort" + "@" + ListenerName)));
						}
						break;
					case UDP:
						// UDP listener
						if (p.containsKey("LocalPort" + "@" + ListenerName)) {
							slave.addOrReplaceListener_UDP(ListenerName,
									Integer.valueOf(p.getProperty("LocalPort" + "@" + ListenerName)));
						}
						break;
					case RTU_over_TCP:
						// TCP listener
						if (p.containsKey("LocalPort" + "@" + ListenerName)) {
							slave.addOrReplaceListener_RTU_over_TCP(ListenerName,
									Integer.valueOf(p.getProperty("LocalPort" + "@" + ListenerName)));
						}
						break;
					case SecureTCP:
						if (p.containsKey("LocalPort" + "@" + ListenerName)) {
							slave.addOrReplaceListener_SecureTCP(ListenerName,
									Integer.valueOf(p.getProperty("LocalPort" + "@" + ListenerName)),
									prepareSecureTcpOptionsForUi(SecureTcpExampleOptions.create()));
						}
						break;
					case RTU:
						// RTU listener
						if (p.containsKey("SerialPortName" + "@" + ListenerName)
								&& p.containsKey("Baudrate" + "@" + ListenerName)
								&& p.containsKey("DataBits" + "@" + ListenerName)
								&& p.containsKey("Parity" + "@" + ListenerName)
								&& p.containsKey("StopBits" + "@" + ListenerName)
								&& p.containsKey("FlowControl" + "@" + ListenerName)) {
							slave.addOrReplaceListener_RTU(ListenerName,
									p.getProperty("SerialPortName" + "@" + ListenerName),
									eBaudrate.valueOf(p.getProperty("Baudrate" + "@" + ListenerName)),
									eDataBits.valueOf(p.getProperty("DataBits" + "@" + ListenerName)),
									eParity.valueOf(p.getProperty("Parity" + "@" + ListenerName)),
									eStopBits.valueOf(p.getProperty("StopBits" + "@" + ListenerName)),
									eFlowControl.valueOf(p.getProperty("FlowControl" + "@" + ListenerName)));
						}
						break;
					case ASCII:
						// ASCII listener
						if (p.containsKey("SerialPortName" + "@" + ListenerName)
								&& p.containsKey("Baudrate" + "@" + ListenerName)
								&& p.containsKey("DataBits" + "@" + ListenerName)
								&& p.containsKey("Parity" + "@" + ListenerName)
								&& p.containsKey("StopBits" + "@" + ListenerName)
								&& p.containsKey("FlowControl" + "@" + ListenerName)) {
							slave.addOrReplaceListener_ASCII(ListenerName,
									p.getProperty("SerialPortName" + "@" + ListenerName),
									eBaudrate.valueOf(p.getProperty("Baudrate" + "@" + ListenerName)),
									eDataBits.valueOf(p.getProperty("DataBits" + "@" + ListenerName)),
									eParity.valueOf(p.getProperty("Parity" + "@" + ListenerName)),
									eStopBits.valueOf(p.getProperty("StopBits" + "@" + ListenerName)),
									eFlowControl.valueOf(p.getProperty("FlowControl" + "@" + ListenerName)));
						}
						break;
					default:
						// ignore
						break;
					}
				}
			}

			// JOptionPane.showMessageDialog(
			// null,
			// resources.getString("successfully_loaded")
			// + System.getProperty("line.separator")
			// + "File: "
			// + new File("PlccomExSettings.xml")
			// .getAbsolutePath(), "",
			// JOptionPane.INFORMATION_MESSAGE);

		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} catch (Throwable ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			RefreshListViewListener();
			setCursor(Cursor.getDefaultCursor());
		}

	}

	void btnSaveSettings_actionPerformed(ActionEvent e) {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (slave == null) {
				JOptionPane.showMessageDialog(null, resources.getString("action_not_possible_slave_is_null"), "",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Write Settings in PLCcomModbusSlaveSettings.xml
			Properties p = new Properties();

			p.setProperty("user", txtUser.getText());
			p.setProperty("serial", txtSerial.getText());
			p.setProperty("SlaveID", cmbSlaveID.getSelectedItem().toString());
			p.setProperty("RegisterMode", cmbRegisterMode.getSelectedItem().toString());
			p.setProperty("ByteOrder", cmbByteOrder.getSelectedItem().toString());
			p.setProperty("LogLevel", cmbLogLevel.getSelectedItem().toString());

			p.setProperty("SupportsFunction01", String.valueOf(chkSupportsFC01.isSelected()));
			p.setProperty("SupportsFunction02", String.valueOf(chkSupportsFC02.isSelected()));
			p.setProperty("SupportsFunction03", String.valueOf(chkSupportsFC03.isSelected()));
			p.setProperty("SupportsFunction04", String.valueOf(chkSupportsFC04.isSelected()));
			p.setProperty("SupportsFunction05", String.valueOf(chkSupportsFC05.isSelected()));
			p.setProperty("SupportsFunction06", String.valueOf(chkSupportsFC06.isSelected()));
			p.setProperty("SupportsFunction08", String.valueOf(chkSupportsFC08.isSelected()));
			p.setProperty("SupportsFunction15", String.valueOf(chkSupportsFC15.isSelected()));
			p.setProperty("SupportsFunction16", String.valueOf(chkSupportsFC16.isSelected()));
			p.setProperty("SupportsFunction23", String.valueOf(chkSupportsFC23.isSelected()));

			p.setProperty("AutoScroll", String.valueOf(chkAutoScroll.isSelected()));
			p.setProperty("LogEnabled", String.valueOf(chkLogging.isSelected()));

			for (Entry<String, ModbusListener> entry : slave.getListener().entrySet()) {
				ModbusListener ml = entry.getValue();
				p.setProperty("Listener" + "#" + ml.getName(), ml.getName());
				p.setProperty("TypeOfCommunication" + "@" + ml.getName(), String.valueOf(ml.getTypeOfCommunication()));
				switch (ml.getTypeOfCommunication()) {
				case TCP:
				case UDP:
				case RTU_over_TCP:
				case SecureTCP:
					p.setProperty("LocalPort" + "@" + ml.getName(),
							String.valueOf(ml.getListenerParameter().get("LocalPort")));
					break;
				case RTU:
				case ASCII:
					p.setProperty("SerialPortName" + "@" + ml.getName(),
							String.valueOf(ml.getListenerParameter().get("SerialPortName")));
					p.setProperty("Baudrate" + "@" + ml.getName(),
							String.valueOf(ml.getListenerParameter().get("Baudrate")));
					p.setProperty("DataBits" + "@" + ml.getName(),
							String.valueOf(ml.getListenerParameter().get("DataBits")));
					p.setProperty("Parity" + "@" + ml.getName(),
							String.valueOf(ml.getListenerParameter().get("Parity")));
					p.setProperty("StopBits" + "@" + ml.getName(),
							String.valueOf(ml.getListenerParameter().get("StopBits")));
					p.setProperty("FlowControl" + "@" + ml.getName(),
							String.valueOf(ml.getListenerParameter().get("FlowControl")));
					break;
				default:
					break;
				}
			}

			FileOutputStream fos = new FileOutputStream(new File("PLCcomModbusSlaveSettings.xml").getAbsolutePath());
			p.storeToXML(fos, "PLCcom Example Settings", "UTF8");
			fos.close();
			JOptionPane.showMessageDialog(null,
					resources.getString("successfully_saved") + System.getProperty("line.separator") + "File: "
							+ new File("PLCcomModbusSlaveSettings.xml").getAbsolutePath(),
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

	void btnCopyLogtoClipboard_actionPerformed(ActionEvent e) {
		// copy diagnostic log to clipboard

		try {
			mLock.lock();
			// get model from JTable
			DefaultTableModel model = (DefaultTableModel) lvLog.getModel();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < model.getRowCount(); i++) {
				sb.append(model.getValueAt(i, 0));
				sb.append(" ");
				sb.append(model.getValueAt(i, 1));
				sb.append(" ");
				sb.append(model.getValueAt(i, 2));
				sb.append(System.getProperty("line.separator"));
			}

			if (sb.length() > 0) {
				// copy log in clipboard
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

				clipboard.setContents(new StringSelection(sb.toString()), null);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		} finally {
			mLock.unlock();
		}
	}

	void btnCopyLogtoFile_actionPerformed(ActionEvent e) {
		try {
			// copy diagnostic log to file
			mLock.lock();

			// get model from JTable
			DefaultTableModel model = (DefaultTableModel) lvLog.getModel();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < model.getRowCount(); i++) {
				sb.append(model.getValueAt(i, 0));
				sb.append(" ");
				sb.append(model.getValueAt(i, 1));
				sb.append(" ");
				sb.append(model.getValueAt(i, 2));
			}
			if (sb.length() > 0) {
				try (Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(new File("PLCcomModbusDiagnosticLog.log").getAbsolutePath()), "utf-8"))) {
					writer.write(sb.toString());
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			JOptionPane.showMessageDialog(null,
					resources.getString("successfully_saved") + System.getProperty("line.separator") + "File: "
							+ new File("PLCcomModbusDiagnosticLog.log").getAbsolutePath(),
					"", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		} finally {
			mLock.unlock();
		}
	}

	private class LogTableCellRenderer implements TableCellRenderer {
		private TableCellRenderer wrappedCellRenderer;

		LogTableCellRenderer(TableCellRenderer cellRenderer) {
			super();
			this.wrappedCellRenderer = cellRenderer;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component rendererComponent = wrappedCellRenderer.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			if (column == 0) {
				if (hasFocus || isSelected) {

				} else {
					if (value.equals(eLogLevel.Error.toString())) {
						rendererComponent.setForeground(Color.red);
					} else {
						rendererComponent.setForeground(Color.black);
					}
				}
			} else {
				if (lvLog.getModel().getValueAt(row, 0).equals(eLogLevel.Error.toString())) {
					rendererComponent.setForeground(Color.red);
				} else {
					rendererComponent.setForeground(Color.black);
				}
			}
			return rendererComponent;
		}
	}
}

class UniversalTableCellRenderer extends DefaultTableCellRenderer.UIResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6694904025574964767L;
	private DefaultTableCellRenderer renderer;
	private int horizontalAlignment = SwingConstants.CENTER;
	private Color foregroundColor = null;
	private Color backgroundColor = null;
	private Border border = null;
	private int fontstyle = Font.PLAIN;

	public UniversalTableCellRenderer(JTable table) {
		renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
	}

	public UniversalTableCellRenderer(JTable table, int horizontalAlignment) {
		this(table);
		this.horizontalAlignment = horizontalAlignment;
	}

	public UniversalTableCellRenderer(JTable table, int horizontalAlignment, Color foregroundColor) {
		this(table);
		this.horizontalAlignment = horizontalAlignment;
		this.foregroundColor = foregroundColor;
	}

	public UniversalTableCellRenderer(JTable table, int horizontalAlignment, int fontstyle, Color foregroundColor) {
		this(table);
		this.horizontalAlignment = horizontalAlignment;
		this.foregroundColor = foregroundColor;
		this.fontstyle = fontstyle;
	}

	public UniversalTableCellRenderer(JTable table, int horizontalAlignment, int fontstyle, Color foregroundColor,
			Color backgroundColor) {
		this(table);
		this.horizontalAlignment = horizontalAlignment;
		this.foregroundColor = foregroundColor;
		this.fontstyle = fontstyle;
		this.backgroundColor = backgroundColor;
	}

	public UniversalTableCellRenderer(JTable table, int horizontalAlignment, Border border) {
		this(table);
		this.horizontalAlignment = horizontalAlignment;
		this.border = border;
	}

	public UniversalTableCellRenderer(JTable table, Border border) {
		this(table);
		this.border = border;
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setFontstyle(int fontstyle) {
		this.fontstyle = fontstyle;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel rendererComponent = (JLabel) renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		rendererComponent.setFont(rendererComponent.getFont().deriveFont(fontstyle));
		rendererComponent.setHorizontalAlignment(horizontalAlignment);
		if (foregroundColor != null) {
			rendererComponent.setForeground(foregroundColor);
		}
		if (column == 1) {
			if (!(hasFocus || isSelected)) {
				if (value.equals(Boolean.TRUE)) {
					rendererComponent.setForeground(Color.blue);
				} else {
					rendererComponent.setForeground(Color.black);
				}
			}
		}

		if (backgroundColor != null) {
			if (!(hasFocus || isSelected)) {
				rendererComponent.setBackground(backgroundColor);
			}

		}
		if (border != null) {
			rendererComponent.setBorder(border);
		}
		return rendererComponent;
	}

}
