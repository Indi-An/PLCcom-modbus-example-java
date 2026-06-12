package example_app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.JComboBox;

import javax.swing.JTextField;

import example_app.DisabledJPanel.DisabledJPanel;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

import java.awt.SystemColor;

import javax.swing.border.LineBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.indian.plccom.modbus.*;
import com.indian.plccom.modbus.Enums.*;

import javax.swing.JButton;

import java.awt.Insets;

import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

@SuppressWarnings("serial")
class ReadCollectionBox extends JFrame {

	private ModbusMaster Device;
	private final ResourceBundle resources = ResourceBundle.getBundle("example_app.resources.resources");
	private ReadRequestCollection RequestCollection = new ReadRequestCollection();

	private JPanel grpAddress;
	private DisabledJPanel panAddress;
	private static final eDataType[] ALL_REGISTER_MODE_DATA_TYPES = eDataType.values();
	private static final eDataType[] DATA_TYPES_64_BIT_REGISTER_MODE = new eDataType[] { eDataType.BOOLEAN,
			eDataType.DOUBLE, eDataType.LONG, eDataType.ULONG, eDataType.PLC_LINT, eDataType.PLC_LWORD };
	private JComboBox<eByteOrder> cmbByteOrder;
	private JComboBox<eDataType> cmbDataType;
	private JComboBox<Integer> cmbSlaveID;
	private JComboBox<eReadFunction> cmbFunction;
	private JTextField txtReadAddress;
	private JTextField txtBit;
	private JTextField txtQuantity;
	private JLabel lblFunction;
	private JLabel lblSlaveID;
	private JLabel lblDataType;
	private JLabel lblByteOrder;
	private JLabel lblReadAddress;
	private JLabel lblBit;
	private JLabel lblQuantity;
	private JTextPane txtInfoRCB;
	private JPanel grpAction;
	private DisabledJPanel panAction;
	private JTable lvRequests;
	private JTable lvLog;
	private JTable lvValues;
	private JPanel statusBar = new JPanel();
	private JTextField lblDeviceGUID;
	private JTextField lblDeviceType;
	private JTextField txtRequestKey;
	private JLabel lblRequestKey;
	private JButton btnAddRequest;
	private JButton btnRemoveRequest;
	private JButton btnReadCollection;
	private JButton btnSaveLogtoClipboard;
	private JButton btnSaveLogtoFile;
	private JButton btnLoadRequest;
	private JButton btnSaveRequest;
	private JButton btnClose;

	/**
	 * Create the dialog.
	 */
	ReadCollectionBox(ModbusMaster Device) {
		setTitle("ReadCollectionBox");
		initialize();
		this.Device = Device;
	}

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
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

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

		this.setBounds(150, 150, 687, 800);

		this.getContentPane().setLayout(null);

		this.grpAddress = new JPanel();
		this.grpAddress.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAddress_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.grpAddress.setBounds(12, 4, 656, 305);
		this.grpAddress.setLayout(null);
		this.getContentPane().add(grpAddress);

		panAddress = new DisabledJPanel(grpAddress);
		panAddress.setBounds(grpAddress.getBounds());
		panAddress.setDisabledColor(new Color(240, 240, 240, 100));
		panAddress.setEnabled(true);
		this.getContentPane().add(panAddress);

		cmbFunction = new JComboBox<eReadFunction>();
		cmbFunction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbFunction_actionPerformed(e);
			}
		});
		cmbFunction.setBounds(126, 46, 205, 21);
		grpAddress.add(cmbFunction);

		cmbSlaveID = new JComboBox<Integer>();
		cmbSlaveID.setBounds(126, 74, 205, 21);
		grpAddress.add(cmbSlaveID);

		cmbDataType = new JComboBox<eDataType>();
		cmbDataType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbDataType_actionPerformed(e);
			}
		});
		cmbDataType.setBounds(126, 101, 205, 21);
		grpAddress.add(cmbDataType);

		cmbByteOrder = new JComboBox<eByteOrder>();
		cmbByteOrder.setBounds(126, 129, 205, 21);
		grpAddress.add(cmbByteOrder);

		txtReadAddress = new JTextField();
		txtReadAddress.setText("0");
		txtReadAddress.setBounds(402, 74, 245, 20);
		grpAddress.add(txtReadAddress);
		txtReadAddress.setColumns(10);

		txtBit = new JTextField();
		txtBit.setText("0");
		txtBit.setColumns(10);
		txtBit.setBounds(402, 101, 245, 20);
		grpAddress.add(txtBit);

		txtQuantity = new JTextField();
		txtQuantity.setText("8");
		txtQuantity.setColumns(10);
		txtQuantity.setBounds(402, 129, 245, 20);
		grpAddress.add(txtQuantity);

		lblFunction = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblFunction_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblFunction.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFunction.setBounds(3, 49, 116, 14);
		grpAddress.add(lblFunction);

		lblSlaveID = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblSlaveID_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblSlaveID.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSlaveID.setBounds(3, 77, 116, 14);
		grpAddress.add(lblSlaveID);

		lblDataType = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblDataType_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblDataType.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDataType.setBounds(3, 104, 116, 14);
		grpAddress.add(lblDataType);

		lblByteOrder = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblByteOrder_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblByteOrder.setHorizontalAlignment(SwingConstants.RIGHT);
		lblByteOrder.setBounds(0, 132, 116, 14);
		grpAddress.add(lblByteOrder);

		lblReadAddress = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblReadAddress_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblReadAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		lblReadAddress.setBounds(330, 74, 69, 14);
		grpAddress.add(lblReadAddress);

		lblBit = new JLabel("Bit");
		lblBit.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBit.setBounds(330, 101, 69, 14);
		grpAddress.add(lblBit);

		lblQuantity = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblQuantity_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblQuantity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblQuantity.setBounds(330, 129, 69, 14);
		grpAddress.add(lblQuantity);

		txtInfoRCB = new JTextPane();
		txtInfoRCB.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtInfoRCB.setText(ResourceBundle.getBundle("example_app.resources.resources").getString("txtInfoRCB_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		txtInfoRCB.setEditable(false);
		txtInfoRCB.setBackground(SystemColor.info);
		txtInfoRCB.setBounds(402, 21, 245, 47);
		grpAddress.add(txtInfoRCB);

		txtRequestKey = new JTextField();
		txtRequestKey.setColumns(10);
		txtRequestKey.setBounds(126, 21, 205, 20);
		grpAddress.add(txtRequestKey);

		lblRequestKey = new JLabel(ResourceBundle.getBundle("example_app.resources.resources") //$NON-NLS-1$
				.getString("ReadCollectionBox.lblRequestKey.text")); //$NON-NLS-1$
		lblRequestKey.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRequestKey.setBounds(3, 24, 116, 14);
		grpAddress.add(lblRequestKey);

		grpAction = new JPanel();
		grpAction.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grpAction.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				ResourceBundle.getBundle("example_app.resources.resources").getString("grpAction_Text"),
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grpAction.setBounds(12, 315, 656, 169);
		getContentPane().add(grpAction);
		grpAction.setLayout(null);

		panAction = new DisabledJPanel(grpAction);

		btnReadCollection = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnReadCollection_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnReadCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReadCollection_actionPerformed(e);
			}
		});
		btnReadCollection
				.setIcon(new ImageIcon(ReadCollectionBox.class.getResource("/example_app/resources/gear_replace.png")));
		btnReadCollection.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnReadCollection.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnReadCollection.setMargin(new Insets(0, 0, 0, 0));
		btnReadCollection.setHorizontalTextPosition(SwingConstants.CENTER);
		btnReadCollection.setBounds(9, 18, 68, 68);
		grpAction.add(btnReadCollection);
		panAction.setBounds(grpAction.getBounds());
		panAction.setDisabledColor(new Color(240, 240, 240, 100));
		panAction.setEnabled(true);
		getContentPane().add(panAction);

		btnSaveLogtoClipboard = new JButton(ResourceBundle.getBundle("example_app.resources.resources") //$NON-NLS-1$
				.getString("btnSaveSlaveLogtoClipboard_Text")); //$NON-NLS-1$
		btnSaveLogtoClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoClipboard_actionPerformed(e);
			}
		});
		btnSaveLogtoClipboard
				.setIcon(new ImageIcon(ReadCollectionBox.class.getResource("/example_app/resources/copy.png")));
		btnSaveLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoClipboard.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnSaveLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoClipboard.setBounds(20, 509, 68, 68);
		getContentPane().add(btnSaveLogtoClipboard);

		btnSaveLogtoFile = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnSaveSlaveLogtoFile_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnSaveLogtoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoFile_actionPerformed(e);
			}
		});
		btnSaveLogtoFile.setIcon(
				new ImageIcon(ReadCollectionBox.class.getResource("/example_app/resources/data_floppy_disk.png")));
		btnSaveLogtoFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoFile.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnSaveLogtoFile.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoFile.setBounds(20, 583, 68, 68);
		getContentPane().add(btnSaveLogtoFile);

		btnLoadRequest = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnLoadRequests_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnLoadRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLoadRequest_actionPerformed(e);
			}
		});
		btnLoadRequest.setIcon(
				new ImageIcon(ReadCollectionBox.class.getResource("/example_app/resources/folder_document.png")));
		btnLoadRequest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnLoadRequest.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnLoadRequest.setMargin(new Insets(0, 0, 0, 0));
		btnLoadRequest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnLoadRequest.setBounds(446, 670, 68, 68);
		getContentPane().add(btnLoadRequest);

		btnSaveRequest = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnSaveRequests_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnSaveRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveRequest_actionPerformed(e);
			}
		});
		btnSaveRequest
				.setIcon(new ImageIcon(ReadCollectionBox.class.getResource("/example_app/resources/save_as.png")));
		btnSaveRequest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveRequest.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnSaveRequest.setMargin(new Insets(0, 0, 0, 0));
		btnSaveRequest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveRequest.setBounds(520, 670, 68, 68);
		getContentPane().add(btnSaveRequest);

		btnClose = new JButton(ResourceBundle.getBundle("example_app.resources.resources").getString("btnClose_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(new ImageIcon(ReadCollectionBox.class.getResource("/example_app/resources/exit.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(594, 670, 68, 68);
		getContentPane().add(btnClose);

		// ############### begin init lvRequests #####################
		lvRequests = new JTable();
		lvRequests.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvRequests.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "RequestKey", "Request" }) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		lvRequests.getColumnModel().getColumn(0).setResizable(true);
		lvRequests.getColumnModel().getColumn(0).setMinWidth(0);
		lvRequests.getColumnModel().getColumn(0).setPreferredWidth(0);
		lvRequests.getColumnModel().getColumn(0).setMaxWidth(0);
		lvRequests.getColumnModel().getColumn(1).setResizable(true);
		lvRequests.getColumnModel().getColumn(1).setPreferredWidth(520);

		lvRequests.setBounds(100, 153, 550, 140);

		lvRequests.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lvRequests_SelectedIndexChanged(e);
			}
		});

		JScrollPane scrollPanelvListener = new JScrollPane(lvRequests);
		lvRequests.setFillsViewportHeight(true);

		JPanel lvListenerContainer = new JPanel();

		lvListenerContainer.setLayout(new BorderLayout());
		lvListenerContainer.add(lvRequests.getTableHeader(), BorderLayout.PAGE_START);
		scrollPanelvListener.setBounds(lvRequests.getBounds());
		lvListenerContainer.add(scrollPanelvListener, BorderLayout.NORTH);

		lvListenerContainer.setBounds(lvRequests.getBounds());
		grpAddress.add(lvListenerContainer);

		btnAddRequest = new JButton("<html><center>read</center><center>Collection</center></html>");
		btnAddRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				btnAddRequest_actionPerformed(arg);
			}
		});
		btnAddRequest.setIcon(new ImageIcon(ReadCollectionBox.class.getResource("/example_app/resources/add2.png")));
		btnAddRequest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAddRequest.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnAddRequest.setMargin(new Insets(0, 0, 0, 0));
		btnAddRequest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAddRequest.setBounds(9, 153, 68, 68);
		grpAddress.add(btnAddRequest);

		btnRemoveRequest = new JButton("<html><center>read</center><center>Collection</center></html>");
		btnRemoveRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRemoveRequest_actionPerformed(e);
			}
		});
		btnRemoveRequest
				.setIcon(new ImageIcon(ReadCollectionBox.class.getResource("/example_app/resources/delete2.png")));
		btnRemoveRequest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnRemoveRequest.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnRemoveRequest.setMargin(new Insets(0, 0, 0, 0));
		btnRemoveRequest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRemoveRequest.setBounds(9, 227, 68, 68);
		grpAddress.add(btnRemoveRequest);

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

		lvValues.getColumnModel().getColumn(0).setResizable(true);
		lvValues.getColumnModel().getColumn(0).setPreferredWidth(60);
		lvValues.getColumnModel().getColumn(1).setResizable(true);
		lvValues.getColumnModel().getColumn(1).setPreferredWidth(268);

		lvValues.setBounds(100, 19, 550, 140);

		JScrollPane scrollPanelvValues = new JScrollPane(lvValues);
		lvValues.setFillsViewportHeight(true);

		JPanel lvValuesContainer = new JPanel();

		lvValuesContainer.setLayout(new BorderLayout());
		lvValuesContainer.add(lvValues.getTableHeader(), BorderLayout.PAGE_START);
		lvValuesContainer.add(scrollPanelvValues, BorderLayout.CENTER);

		lvValuesContainer.setBounds(lvValues.getBounds());
		grpAction.add(lvValuesContainer);

		// ############### end init lvValues #####################
		// ############### begin init lvLog #####################
		lvLog = new JTable();
		lvLog.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvLog.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "LogLevel",
				ResourceBundle.getBundle("example_app.resources.resources").getString("colTimeStamp_Text"), "Text" }) {
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
		lvLog.setBounds(112, 509, 550, 142);
		lvLog.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		lvLog.setAutoscrolls(true);

		JScrollPane scrollPanelvLog = new JScrollPane(lvLog);
		lvLog.setFillsViewportHeight(true);

		JPanel lvLogContainer = new JPanel();

		lvLogContainer.setLayout(new BorderLayout());
		lvLogContainer.add(lvLog.getTableHeader(), BorderLayout.PAGE_START);
		lvLogContainer.add(scrollPanelvLog, BorderLayout.CENTER);

		lvLogContainer.setBounds(lvLog.getBounds());
		getContentPane().add(lvLogContainer);
		// ############### end init lvLog #####################

		statusBar.setLayout(null);
		// Creating the StatusBar.
		// statusBar.setLayout(new BorderLayout());
		statusBar.setBounds(0, 750, 681, 22);
		statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		statusBar.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(statusBar);

		lblDeviceType = new JTextField("Adapter Type: nothing");
		lblDeviceType.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeviceType.setFocusable(false);
		lblDeviceType.setEditable(false);
		lblDeviceType.setBackground(Color.WHITE);
		lblDeviceType.setSize(339, 18);
		lblDeviceType.setLocation(2, 2);
		statusBar.add(lblDeviceType);

		lblDeviceGUID = new JTextField(ResourceBundle.getBundle("example_app.resources.resources") //$NON-NLS-1$
				.getString("ReadCollectionBox.lblDeviceGUID.text_1")); //$NON-NLS-1$
		lblDeviceGUID.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeviceGUID.setFocusable(false);
		lblDeviceGUID.setEditable(false);
		lblDeviceGUID.setBackground(Color.WHITE);
		lblDeviceGUID.setSize(339, 18);
		lblDeviceGUID.setLocation(341, 2);
		statusBar.add(lblDeviceGUID);

		WorkshopUiScaler.apply(this);
	}

	protected void formWindowClosing(WindowEvent e) {
		Master_Example.CountOpenDialogs--;
	}

	protected void formWindowOpened(WindowEvent arg0) {

		lblDeviceGUID.setText("Device UUID: " + Device.getDeviceUUID().toString());
		lblDeviceType.setText("Adapter Type: " + Device.getConnector().getClass().getName());

		// fill combobox with enum values
		cmbByteOrder.setModel(new DefaultComboBoxModel<eByteOrder>(eByteOrder.values()));

		setRegisterModeDataTypeModel(getPreferredRegisterDataType());

		cmbFunction.setModel(new DefaultComboBoxModel<eReadFunction>(eReadFunction.values()));

		// init slave id
		DefaultComboBoxModel<Integer> modSlaveID = new DefaultComboBoxModel<Integer>();
		for (int i = 0; i < 248; i++) {
			modSlaveID.addElement(i);
		}
		cmbSlaveID.setModel(modSlaveID);
		cmbSlaveID.setSelectedItem(1);

		txtRequestKey.setText("Request_" + String.format("%03d", RequestCollection.getReadRequests().length + 1));
	}

	private void btnAddRequest_actionPerformed(ActionEvent arg) {
		try {
			// define new request
			ReadRequest RequestItem = null;
			// @formatter:off
			if (cmbDataType.getSelectedItem().equals(eDataType.BOOLEAN)
					&& (cmbFunction.getSelectedItem().equals(eReadFunction.F03_Read_Holding_Registers)
							|| cmbFunction.getSelectedItem().equals(eReadFunction.F04_Read_Input_Register))) {
				RequestItem = RequestBuilder.ReadRequestBuilder.create(
						Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
						(eReadFunction) cmbFunction.getSelectedItem(), // modbus function
						Integer.valueOf(txtReadAddress.getText()), // Read start adress
						(eDataType) cmbDataType.getSelectedItem(), // Target Datatype
						Integer.valueOf(txtQuantity.getText()), // quantity of objects to be read
						Byte.valueOf(txtBit.getText())); // Address of first Bit by reading register
			} else {
				RequestItem = RequestBuilder.ReadRequestBuilder.create(
						Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
						(eReadFunction) cmbFunction.getSelectedItem(), // modbus function
						Integer.valueOf(txtReadAddress.getText()), // Read start adress
						(eDataType) cmbDataType.getSelectedItem(), // Target Datatype
						Integer.valueOf(txtQuantity.getText())); // quantity of objects to be read)
			}

			// set eventual byte order, standard = eByteOrder.AB_CD;
			RequestItem.setByteOrder(eByteOrder.valueOf(cmbByteOrder.getSelectedItem().toString()));

			// @formatter:on
			// add new request to request collection
			RequestCollection.addReadRequest(txtRequestKey.getText(), RequestItem);

			txtRequestKey.setText("Request_" + String.format("%03d", RequestCollection.getReadRequests().length + 1));

			// update listview
			fillRequestListView();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnRemoveRequest_actionPerformed(ActionEvent e) {
		// remove request from request collection
		try {
			if (lvRequests.getSelectedRow() > -1) {
				RequestCollection.removeReadItemRequest(
						(String) lvRequests.getModel().getValueAt(lvRequests.getSelectedRow(), 0));
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			fillRequestListView();
		}

	}

	private void lvRequests_SelectedIndexChanged(ListSelectionEvent e) {
		btnRemoveRequest.setEnabled(lvRequests.getSelectedRow() > -1);
	}

	private void fillRequestListView() {
		try {
			// clear ListView initial
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// clear lvValues
			DefaultTableModel model = (DefaultTableModel) lvRequests.getModel();
			// clear model
			while (model.getRowCount() > 0) {
				model.removeRow(0);
			}

			// fill ListView with current ReadRequests
			for (ReadRequest rr : RequestCollection.getReadRequests()) {
				model.addRow(new Object[] { rr.getRequestKey(), rr.toString() });
			}
			model.fireTableDataChanged();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void btnReadCollection_actionPerformed(ActionEvent e) {
		try {
			// Optmize Mode
			RequestCollection.setOptmize(true);

			// read from device
			ReadResultCollection ResultCollection = Device.read(RequestCollection);

			// clear lvValues
			DefaultTableModel modValues = (DefaultTableModel) lvValues.getModel();
			// clear model
			while (modValues.getRowCount() > 0) {
				modValues.removeRow(0);
			}

			// set diagnostic output
			DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
			// clear model
			while (modLog.getRowCount() > 0) {
				modLog.removeRow(0);
			}

			// getting readrequestcollection diagnoctic logs
			// add summary log entry
			modLog.addRow(new Object[] {
					ResultCollection.getQuality().equals(OperationResult.eQuality.GOOD)
							? eLogLevel.Information.toString()
							: eLogLevel.Error.toString(),
					DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
					" " + ResultCollection.getClass().getName() + " Message: " + ResultCollection.getMessage() });

			// add log entrys
			for (LogEntry le : ResultCollection.getDiagnosticLog()) {
				modLog.addRow(new Object[] { le.getLogLevel().toString(),
						DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
						le.getText() + " " + le.getStackTraceString() });

			}

			// evaluate results
			for (ReadResult res : ResultCollection.getReadItemResults()) {
				if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
					StringBuilder sb = new StringBuilder();
					sb.append("Start Item:");
					sb.append(res.getRequestKey());
					sb.append(" ");
					sb.append("Quality:");
					sb.append(" ");
					sb.append(res.getQuality().toString());
					sb.append(System.getProperty("line.separator"));

					modValues.addRow(new Object[] { "", sb.toString() });

					for (ReadValue item : res.fetchValues()) {
						modValues
								.addRow(new Object[] {
										"Address => " + String.valueOf(item.getAddress()) + " Position => "
												+ String.valueOf(item.getAddressPosition()),
										String.valueOf(item.getValue()) });
					}

					sb = new StringBuilder();
					sb.append("End Item:");
					sb.append(res.getRequestKey());

					modValues.addRow(new Object[] { "", sb.toString() });
				}

				// getting readrequest diagnoctic logs
				modLog.addRow(new Object[] {
						ResultCollection.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(),
						DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
						" " + (!(res.getRequestKey() == null || "".equals(res.getRequestKey()))
								? " " + res.getRequestKey()
								: "") + " Message: " + res.getMessage() });

				for (LogEntry le : res.getDiagnosticLog()) {
					modLog.addRow(new Object[] { le.getLogLevel().toString(),
							DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()), le.toString() });
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			((AbstractTableModel) lvLog.getModel()).fireTableDataChanged();
			((AbstractTableModel) lvValues.getModel()).fireTableDataChanged();
		}
	}

	private void cmbFunction_actionPerformed(ActionEvent e) {
		// switch byteorder depending on the selected function
		if (eReadFunction.valueOf(cmbFunction.getSelectedItem().toString())
				.equals(eReadFunction.F03_Read_Holding_Registers)
				|| eReadFunction.valueOf(cmbFunction.getSelectedItem().toString())
						.equals(eReadFunction.F04_Read_Input_Register)) {
			setRegisterModeDataTypeModel(getPreferredRegisterDataType());
			updateByteOrderStateForDataType();
			cmbDataType.setEnabled(true);
		} else {
			cmbByteOrder.setEnabled(false);
			cmbDataType.setEnabled(false);
			cmbDataType.setSelectedItem(eDataType.BOOLEAN);
		}
	}

	private void btnClose_actionPerformed(ActionEvent e) {
		this.setVisible(false);

		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

	}

	private void cmbDataType_actionPerformed(ActionEvent e) {
		// switch byteorder depending on the selected data type
		updateByteOrderStateForDataType();
	}

	private void setRegisterModeDataTypeModel(eDataType preferredDataType) {
		eDataType selectedDataType = preferredDataType == null ? getPreferredRegisterDataType() : preferredDataType;
		if (!isDataTypeSupportedByCurrentRegisterMode(selectedDataType)) {
			selectedDataType = getPreferredRegisterDataType();
		}
		cmbDataType.setModel(new DefaultComboBoxModel<eDataType>(getSupportedDataTypesForCurrentRegisterMode()));
		cmbDataType.setSelectedItem(selectedDataType);
	}

	private eDataType[] getSupportedDataTypesForCurrentRegisterMode() {
		return Device.getRegisterMode().equals(eRegisterMode._64Bit) ? DATA_TYPES_64_BIT_REGISTER_MODE
				: ALL_REGISTER_MODE_DATA_TYPES;
	}

	private boolean isDataTypeSupportedByCurrentRegisterMode(eDataType dataType) {
		if (dataType == null) {
			return false;
		}
		eDataType[] supportedDataTypes = getSupportedDataTypesForCurrentRegisterMode();
		for (int i = 0; i < supportedDataTypes.length; i++) {
			if (supportedDataTypes[i] == dataType) {
				return true;
			}
		}
		return false;
	}

	private eDataType getPreferredRegisterDataType() {
		if (Device.getRegisterMode().equals(eRegisterMode._64Bit)) {
			return eDataType.ULONG;
		}
		if (Device.getRegisterMode().equals(eRegisterMode._32Bit)) {
			return eDataType.UINTEGER;
		}
		return eDataType.USHORT;
	}

	private void updateByteOrderStateForDataType() {
		Object selectedItem = cmbDataType.getSelectedItem();
		if (!(selectedItem instanceof eDataType)) {
			cmbByteOrder.setEnabled(false);
			return;
		}
		eDataType dataType = (eDataType) selectedItem;
		cmbByteOrder.setEnabled(!(dataType.equals(eDataType.BOOLEAN) || dataType.equals(eDataType.BYTE)
				|| dataType.equals(eDataType.STRING)));
	}

	private void btnSaveLogtoClipboard_actionPerformed(ActionEvent e) {
		// copy diagnostic log to clipboard

		try {
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

		}
	}

	private void btnSaveLogtoFile_actionPerformed(ActionEvent e) {
		try {
			// copy diagnostic log to file
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

		}
	}

	private void btnLoadRequest_actionPerformed(ActionEvent e) {
		Properties p = new Properties();

		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// open file
			FileInputStream fis = new FileInputStream(new File("PLCcomModbusRequestCollection.xml").getAbsolutePath());

			// load saved settiungs from PLCcomModbusRequestCollection.xml
			p.loadFromXML(fis);
			// load listener
			Enumeration<?> prop = p.propertyNames();
			while (prop.hasMoreElements()) {
				String key = (String) prop.nextElement();
				String value = p.getProperty(key);
				boolean isIncomplete = false;

				// found request
				if (key.startsWith("Request#")) {
					ReadRequest RequestItem = null;
					String RequestKey = value;
					eReadFunction rfc = null;
					eDataType dt = null;
					int SlaveID = 0;
					int ReadAddress = 0;
					int Quantity = 0;
					byte StartBit = 0;

					if (p.containsKey(RequestKey + "#" + "Function")) {
						rfc = eReadFunction.valueOf(p.getProperty(RequestKey + "#" + "Function"));
					} else {
						isIncomplete = true;
					}

					if (p.containsKey(RequestKey + "#" + "Target_Datatype")) {
						dt = eDataType.valueOf(p.getProperty(RequestKey + "#" + "Target_Datatype"));
					} else {
						isIncomplete = true;
					}

					if (p.containsKey(RequestKey + "#" + "SlaveID")) {
						SlaveID = Integer.valueOf(p.getProperty(RequestKey + "#" + "SlaveID"));
					} else {
						isIncomplete = true;
					}

					if (p.containsKey(RequestKey + "#" + "ReadAddress")) {
						ReadAddress = Integer.valueOf(p.getProperty(RequestKey + "#" + "ReadAddress"));
					} else {
						isIncomplete = true;
					}

					if (p.containsKey(RequestKey + "#" + "Quantity")) {
						Quantity = Integer.valueOf(p.getProperty(RequestKey + "#" + "Quantity"));
					} else {
						isIncomplete = true;
					}

					if (p.containsKey(RequestKey + "#" + "StartBit")) {
						StartBit = Byte.valueOf(p.getProperty(RequestKey + "#" + "StartBit"));
					}

					// @formatter:off
					if (!isIncomplete) {
						if (rfc != eReadFunction.F03_Read_Holding_Registers && rfc != eReadFunction.F04_Read_Input_Register) {
							dt = eDataType.BOOLEAN;
						} else if (!isDataTypeSupportedByCurrentRegisterMode(dt)) {
							dt = getPreferredRegisterDataType();
						}
						if (dt.equals(eDataType.BOOLEAN) && (rfc == eReadFunction.F03_Read_Holding_Registers
								|| (rfc == eReadFunction.F04_Read_Input_Register))) {
							RequestItem = RequestBuilder.ReadRequestBuilder.create(SlaveID, // Slave ID
									rfc, // modbus function
									ReadAddress, // Read start adress
									dt, // Target Datatype
									Quantity, // quantity of objects to be read
									StartBit); // Address of first Bit by reading register
						} else {
							RequestItem = RequestBuilder.ReadRequestBuilder.create(SlaveID, // Slave ID
									rfc, // modbus function
									ReadAddress, // Read start adress
									dt, // Target Datatype
									Quantity); // quantity of objects to be read

						}

						RequestCollection.addReadRequest(RequestKey, RequestItem);

					}
					// @formatter:on

				}
			}
			// Refresh listview
			fillRequestListView();

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

	private void btnSaveRequest_actionPerformed(ActionEvent e) {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// Write Settings in PLCcomModbusRequestCollection.xml
			Properties p = new Properties();

			for (ReadRequest rreq : RequestCollection.getReadRequests()) {
				String RequestName = rreq.getRequestKey();
				p.setProperty("Request#" + RequestName, RequestName);
				p.setProperty(RequestName + "#" + "Function", String.valueOf(rreq.getFunction()));
				p.setProperty(RequestName + "#" + "SlaveID", String.valueOf(rreq.getSlaveID()));
				p.setProperty(RequestName + "#" + "Target_Datatype", String.valueOf(rreq.getTarget_Datatype()));
				p.setProperty(RequestName + "#" + "ByteOrder", String.valueOf(rreq.getByteOrder()));
				p.setProperty(RequestName + "#" + "ReadAddress", String.valueOf(rreq.getReadAddress()));
				p.setProperty(RequestName + "#" + "Quantity", String.valueOf(rreq.getQuantity()));
				p.setProperty(RequestName + "#" + "StartBit", String.valueOf(rreq.getStartBit()));
			}

			FileOutputStream fos = new FileOutputStream(
					new File("PLCcomModbusRequestCollection.xml").getAbsolutePath());
			p.storeToXML(fos, "PLCcom Example Settings", "UTF8");
			fos.close();
			JOptionPane.showMessageDialog(null,
					resources.getString("successfully_saved") + System.getProperty("line.separator") + "File: "
							+ new File("PLCcomModbusRequestCollection.xml").getAbsolutePath(),
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

	// LogTableCellRenderer
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

	// UniversalTableCellRenderer
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
			JLabel rendererComponent = (JLabel) renderer.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
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
}
