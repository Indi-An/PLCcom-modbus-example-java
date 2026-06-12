package example_app;

import static com.indian.plccom.modbus.UnsignedDatatypes.UBuilder.*;

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
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
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
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.awt.Dialog.ModalExclusionType;

@SuppressWarnings("serial")
class ReadWriteBox extends JFrame {

	private ModbusMaster Device;
	private final ResourceBundle resources = ResourceBundle.getBundle("example_app.resources.resources");
	private String C_Values_Multiple_Coil = "true" + System.getProperty("line.separator") + "false"
			+ System.getProperty("line.separator") + "false" + System.getProperty("line.separator") + "true"
			+ System.getProperty("line.separator");
	private String C_Values_Single_Register = "0";
	private String C_Values_Multiple_Register = "0" + System.getProperty("line.separator") + "0"
			+ System.getProperty("line.separator") + "0" + System.getProperty("line.separator") + "0"
			+ System.getProperty("line.separator");
	private String ValuetoWrite = "";
	private static final eDataType[] ALL_REGISTER_MODE_DATA_TYPES = eDataType.values();
	private static final eDataType[] DATA_TYPES_64_BIT_REGISTER_MODE = new eDataType[] { eDataType.BOOLEAN,
			eDataType.DOUBLE, eDataType.LONG, eDataType.ULONG, eDataType.PLC_LINT, eDataType.PLC_LWORD };
	private JPanel grpAddress;
	private DisabledJPanel panAddress;
	private JComboBox<eByteOrder> cmbByteOrder;
	private JComboBox<eDataType> cmbDataType;
	private JComboBox<Integer> cmbSlaveID;
	private JComboBox<String> cmbFunction;
	private JTextField txtReadAddress;
	private JTextField txtBit;
	private JTextField txtQuantity;
	private JTextField txtWriteAddress;
	private JLabel lblFunction;
	private JLabel lblSlaveID;
	private JLabel lblDataType;
	private JLabel lblByteOrder;
	private JLabel lblReadAddress;
	private JLabel lblBit;
	private JLabel lblQuantity;
	private JLabel lblWriteAddress;
	private JTextPane txtInfoRB;
	private JTextPane lblBroadcast;
	private JPanel grbWriteValues;
	private DisabledJPanel panWriteValues;
	private JCheckBox chkSingleValue;
	private JLabel lblEnterValues;
	private JTextArea txtMultipleValues;
	private JScrollPane scrollMultipleValues;
	private JLabel lblWriteMaskRegister_OR;
	private JLabel lblWriteMaskRegister_AND;
	private JPanel grpAction;
	private DisabledJPanel panAction;
	private JTextField txtAND;
	private JTextField txtOR;
	private JTable lvLog;
	private JTable lvValues;
	private JPanel statusBar = new JPanel();
	private JTextField lblDeviceGUID;
	private JTextField lblDeviceType;
	private JRadioButton rbOn;
	private JRadioButton rbOff;
	private JTextField txtSingleValues;

	/**
	 * Create the dialog.
	 */
	ReadWriteBox(ModbusMaster Device) {
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		this.Device = Device;
		initialize();

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

		setTitle(ResourceBundle.getBundle("example_app.resources.resources").getString("ReadWriteBox.this.title_1"));

		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(Master_Example.class.getResource("/example_app/resources/node.png")));
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 687, 745);

		this.getContentPane().setLayout(null);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				formWindowOpened(arg0);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				formWindowClosing();
			}
		});

		this.grpAddress = new JPanel();
		this.grpAddress.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAddress_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.grpAddress.setBounds(12, 4, 656, 241);
		this.grpAddress.setLayout(null);
		this.getContentPane().add(grpAddress);

		panAddress = new DisabledJPanel(grpAddress);
		panAddress.setBounds(grpAddress.getBounds());
		panAddress.setDisabledColor(new Color(240, 240, 240, 100));
		panAddress.setEnabled(true);
		this.getContentPane().add(panAddress);

		cmbFunction = new JComboBox<String>();
		cmbFunction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				cmbFunction_actionPerformed(arg);
			}
		});
		cmbFunction.setBounds(126, 19, 164, 21);
		grpAddress.add(cmbFunction);

		cmbSlaveID = new JComboBox<Integer>();
		cmbSlaveID.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbSlaveID_actionPerformed(e);
			}
		});
		cmbSlaveID.setBounds(126, 47, 164, 21);
		grpAddress.add(cmbSlaveID);

		cmbDataType = new JComboBox<eDataType>();
		cmbDataType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbDataType_actionPerformed(e);
			}
		});
		cmbDataType.setBounds(126, 74, 164, 21);
		grpAddress.add(cmbDataType);

		cmbByteOrder = new JComboBox<eByteOrder>();
		cmbByteOrder.setBounds(126, 102, 164, 21);
		grpAddress.add(cmbByteOrder);

		txtReadAddress = new JTextField();
		txtReadAddress.setText("0");
		txtReadAddress.setBounds(126, 129, 164, 20);
		grpAddress.add(txtReadAddress);
		txtReadAddress.setColumns(10);

		txtBit = new JTextField();
		txtBit.setText("0");
		txtBit.setColumns(10);
		txtBit.setBounds(126, 156, 164, 20);
		grpAddress.add(txtBit);

		txtQuantity = new JTextField();
		txtQuantity.setText("8");
		txtQuantity.setColumns(10);
		txtQuantity.setBounds(126, 184, 164, 20);
		grpAddress.add(txtQuantity);

		txtWriteAddress = new JTextField();
		txtWriteAddress.setText("0");
		txtWriteAddress.setColumns(10);
		txtWriteAddress.setBounds(126, 210, 164, 20);
		grpAddress.add(txtWriteAddress);

		lblFunction = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblFunction_Text"));
		lblFunction.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFunction.setBounds(3, 22, 116, 14);
		grpAddress.add(lblFunction);

		lblSlaveID = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblSlaveID_Text"));
		lblSlaveID.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSlaveID.setBounds(3, 50, 116, 14);
		grpAddress.add(lblSlaveID);

		lblDataType = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblDataType_Text"));
		lblDataType.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDataType.setBounds(3, 77, 116, 14);
		grpAddress.add(lblDataType);

		lblByteOrder = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblByteOrder_Text"));
		lblByteOrder.setHorizontalAlignment(SwingConstants.RIGHT);
		lblByteOrder.setBounds(0, 105, 116, 14);
		grpAddress.add(lblByteOrder);

		lblReadAddress = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblReadAddress_Text"));
		lblReadAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		lblReadAddress.setBounds(3, 132, 116, 14);
		grpAddress.add(lblReadAddress);

		lblBit = new JLabel("Bit");
		lblBit.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBit.setBounds(3, 159, 116, 14);
		grpAddress.add(lblBit);

		lblQuantity = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblQuantity_Text"));
		lblQuantity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblQuantity.setBounds(3, 187, 116, 14);
		grpAddress.add(lblQuantity);

		lblWriteAddress = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblWriteAddress_Text"));
		lblWriteAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		lblWriteAddress.setBounds(3, 213, 116, 14);
		grpAddress.add(lblWriteAddress);

		txtInfoRB = new JTextPane();
		txtInfoRB.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtInfoRB.setText(ResourceBundle.getBundle("example_app.resources.resources").getString("txtInfoRB_OR_Text"));
		txtInfoRB.setEditable(false);
		txtInfoRB.setBackground(SystemColor.info);
		txtInfoRB.setBounds(372, 21, 275, 47);
		grpAddress.add(txtInfoRB);

		lblBroadcast = new JTextPane();
		lblBroadcast.setForeground(Color.RED);
		lblBroadcast
				.setText(ResourceBundle.getBundle("example_app.resources.resources").getString("lblBroadcast_Text"));
		lblBroadcast.setEditable(false);
		lblBroadcast.setBackground(SystemColor.menu);
		lblBroadcast.setBounds(290, 30, 80, 60);
		grpAddress.add(lblBroadcast);

		grbWriteValues = new JPanel();
		grbWriteValues.setBorder(null);
		grbWriteValues.setBounds(331, 77, 319, 158);
		grpAddress.add(grbWriteValues);
		grbWriteValues.setLayout(null);

		panWriteValues = new DisabledJPanel(grbWriteValues);
		panWriteValues.setBounds(grbWriteValues.getBounds());
		panWriteValues.setDisabledColor(new Color(240, 240, 240, 100));
		panWriteValues.setEnabled(false);
		grpAddress.add(panWriteValues);

		lblWriteMaskRegister_OR = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblWriteMaskRegister_OR_Text"));
		lblWriteMaskRegister_OR.setVisible(false);

		txtAND = new JTextField("0");
		txtAND.setVisible(false);
		txtAND.setBounds(41, 55, 275, 20);
		grbWriteValues.add(txtAND);
		txtAND.setColumns(10);

		txtOR = new JTextField("0");
		txtOR.setVisible(false);
		txtOR.setColumns(10);
		txtOR.setBounds(41, 110, 275, 20);
		grbWriteValues.add(txtOR);

		lblWriteMaskRegister_OR.setBounds(38, 91, 242, 13);
		grbWriteValues.add(lblWriteMaskRegister_OR);

		chkSingleValue = new JCheckBox(
				ResourceBundle.getBundle("example_app.resources.resources").getString("chkSingleValue_Text"));
		chkSingleValue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg) {
				chkSingleValue_mouseClicked(arg);
			}
		});
		chkSingleValue.setBounds(41, 11, 82, 17);
		grbWriteValues.add(chkSingleValue);

		txtMultipleValues = new JTextArea();
		txtMultipleValues.setVisible(false);
		txtMultipleValues.setBorder(null);
		txtMultipleValues.setBounds(41, 54, 275, 94);
		txtMultipleValues.setLineWrap(true);
		txtMultipleValues.setWrapStyleWord(true);
		scrollMultipleValues = new JScrollPane(txtMultipleValues);
		scrollMultipleValues.setBounds(txtMultipleValues.getBounds());
		scrollMultipleValues.setVisible(false);
		grbWriteValues.add(scrollMultipleValues);
		txtMultipleValues.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtMultipleValues_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtMultipleValues_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtMultipleValues_TextChanged(e);
			}
		});

		lblWriteMaskRegister_AND = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblWriteMaskRegister_AND_Text"));
		lblWriteMaskRegister_AND.setVisible(false);

		lblEnterValues = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblValues_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblEnterValues.setBounds(38, 32, 219, 13);
		grbWriteValues.add(lblEnterValues);
		lblWriteMaskRegister_AND.setBounds(38, 32, 230, 13);
		grbWriteValues.add(lblWriteMaskRegister_AND);

		rbOn = new JRadioButton("ON");
		rbOn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rbOn_mouseClicked(e);
			}
		});
		rbOn.setVisible(false);
		rbOn.setBounds(41, 53, 52, 17);
		grbWriteValues.add(rbOn);

		rbOff = new JRadioButton("OFF");
		rbOff.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rbOff_mouseClicked(e);
			}
		});
		rbOff.setVisible(false);
		rbOff.setBounds(179, 53, 49, 17);
		grbWriteValues.add(rbOff);

		ButtonGroup btnGroupOnOFF = new ButtonGroup();
		btnGroupOnOFF.add(rbOn);
		btnGroupOnOFF.add(rbOff);

		txtSingleValues = new JTextField();
		txtSingleValues.setVisible(false);
		txtSingleValues.setText("0");
		txtSingleValues.setBounds(41, 53, 275, 20);
		grbWriteValues.add(txtSingleValues);
		txtSingleValues.setColumns(10);
		txtSingleValues.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				txtSingleValues_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtSingleValues_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtSingleValues_TextChanged(e);
			}
		});

		grpAction = new JPanel();
		grpAction.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grpAction.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				ResourceBundle.getBundle("example_app.resources.resources").getString("grpAction_Text"),
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grpAction.setBounds(12, 251, 656, 180);
		getContentPane().add(grpAction);
		grpAction.setLayout(null);

		panAction = new DisabledJPanel(grpAction);

		JButton btnExecute = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnExecute_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnExecute_actionPerformed(e);
			}
		});
		btnExecute.setIcon(new ImageIcon(ReadWriteBox.class.getResource("/example_app/resources/gear_replace.png")));
		btnExecute.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnExecute.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnExecute.setMargin(new Insets(0, 0, 0, 0));
		btnExecute.setHorizontalTextPosition(SwingConstants.CENTER);
		btnExecute.setBounds(15, 19, 68, 68);
		grpAction.add(btnExecute);
		panAction.setBounds(grpAction.getBounds());
		panAction.setDisabledColor(new Color(240, 240, 240, 100));
		panAction.setEnabled(true);
		getContentPane().add(panAction);

		JButton btnSaveLogtoClipboard = new JButton(ResourceBundle.getBundle("example_app.resources.resources") //$NON-NLS-1$
				.getString("btnSaveSlaveLogtoClipboard_Text")); //$NON-NLS-1$
		btnSaveLogtoClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoClipboard_actionPerformed(e);
			}
		});
		btnSaveLogtoClipboard.setIcon(new ImageIcon(ReadWriteBox.class.getResource("/example_app/resources/copy.png")));
		btnSaveLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoClipboard.setBounds(27, 449, 68, 68);
		getContentPane().add(btnSaveLogtoClipboard);

		JButton btnSaveLogtoFile = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnSaveSlaveLogtoFile_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnSaveLogtoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoFile_actionPerformed(e);
			}
		});
		btnSaveLogtoFile
				.setIcon(new ImageIcon(ReadWriteBox.class.getResource("/example_app/resources/data_floppy_disk.png")));
		btnSaveLogtoFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoFile.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoFile.setBounds(27, 523, 68, 68);
		getContentPane().add(btnSaveLogtoFile);

		JButton btnLoadRequest = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnLoadRequest_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnLoadRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLoadRequest_actionPerformed(e);
			}
		});
		btnLoadRequest
				.setIcon(new ImageIcon(ReadWriteBox.class.getResource("/example_app/resources/folder_document.png")));
		btnLoadRequest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnLoadRequest.setMargin(new Insets(0, 0, 0, 0));
		btnLoadRequest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnLoadRequest.setBounds(446, 610, 68, 68);
		getContentPane().add(btnLoadRequest);

		JButton btnSaveRequest = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnSaveRequest_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnSaveRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveRequest_actionPerformed(e);
			}
		});
		btnSaveRequest.setIcon(new ImageIcon(ReadWriteBox.class.getResource("/example_app/resources/save_as.png")));
		btnSaveRequest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveRequest.setMargin(new Insets(0, 0, 0, 0));
		btnSaveRequest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveRequest.setBounds(520, 610, 68, 68);
		getContentPane().add(btnSaveRequest);

		JButton btnClose = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnClose_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(new ImageIcon(ReadWriteBox.class.getResource("/example_app/resources/exit.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(594, 610, 68, 68);
		getContentPane().add(btnClose);

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
		lvLog.setBounds(112, 449, 550, 142);
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
		statusBar.setBounds(0, 695, 681, 22);
		statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		statusBar.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(statusBar);

		lblDeviceType = new JTextField("Adapter Type: nothing");
		lblDeviceType.setFocusable(false);
		lblDeviceType.setEditable(false);
		lblDeviceType.setBackground(Color.WHITE);
		lblDeviceType.setSize(339, 18);
		lblDeviceType.setLocation(2, 2);
		statusBar.add(lblDeviceType);

		lblDeviceGUID = new JTextField("GUID: nothing");
		lblDeviceGUID.setFocusable(false);
		lblDeviceGUID.setEditable(false);
		lblDeviceGUID.setBackground(Color.WHITE);
		lblDeviceGUID.setSize(339, 18);
		lblDeviceGUID.setLocation(341, 2);
		statusBar.add(lblDeviceGUID);

		WorkshopUiScaler.apply(this);
	}

	protected void formWindowClosing() {
		Master_Example.CountOpenDialogs--;
	}

	protected void formWindowOpened(WindowEvent arg0) {
		try {
			lblDeviceGUID.setText("Device Guid: " + Device.getDeviceUUID().toString());
			lblDeviceType.setText("Adapter Type: " + Device.getConnector().getClass().toString());

			// fill combobox with enum values
			cmbByteOrder.setModel(new DefaultComboBoxModel<eByteOrder>(eByteOrder.values()));

			setRegisterModeDataTypeModel(getPreferredRegisterDataType());

			// Set cmbFunction datasource
			DefaultComboBoxModel<String> modFunctions = new DefaultComboBoxModel<String>();
			for (eReadFunction FunctionEntry : eReadFunction.values()) {
				modFunctions.addElement(FunctionEntry.toString());
			}
			for (eWriteFunction FunctionEntry : eWriteFunction.values()) {
				modFunctions.addElement(FunctionEntry.toString());
			}
			for (eMaskWriteRegisterFunction FunctionEntry : eMaskWriteRegisterFunction.values()) {
				modFunctions.addElement(FunctionEntry.toString());
			}
			for (eReadWriteFunction FunctionEntry : eReadWriteFunction.values()) {
				modFunctions.addElement(FunctionEntry.toString());
			}

			cmbFunction.setModel(modFunctions);

			cmbFunction.setSelectedItem(eReadFunction.F01_Read_Coils.toString());

			// init slave id
			DefaultComboBoxModel<Integer> modSlaveID = new DefaultComboBoxModel<Integer>();
			for (int i = 0; i < 248; i++) {
				modSlaveID.addElement(i);
			}
			cmbSlaveID.setModel(modSlaveID);
			cmbSlaveID.setSelectedItem(1);

			lblBroadcast.setVisible(Integer.valueOf(cmbSlaveID.getSelectedItem().toString()) == 0);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void cmbFunction_actionPerformed(ActionEvent arg) {
		try {
			// set controls with specific values
			eMBFunction myFunction = eMBFunction.valueOf(cmbFunction.getSelectedItem().toString());

			boolean booReadNumberAreaEnabled = false;
			boolean booReadBoolAreaEnabled = false;
			boolean booWriteNumberAreaEnabled = false;
			boolean booWriteBoolAreaEnabled = false;

			switch (myFunction) {
			case F01_Read_Coils:
			case F02_Read_Discrete_Inputs:
				booReadNumberAreaEnabled = false;
				booReadBoolAreaEnabled = true;
				booWriteNumberAreaEnabled = false;
				booWriteBoolAreaEnabled = false;
				break;
			case F03_Read_Holding_Registers:
			case F04_Read_Input_Register:
				booReadNumberAreaEnabled = true;
				booReadBoolAreaEnabled = true;
				booWriteNumberAreaEnabled = false;
				booWriteBoolAreaEnabled = false;
				break;
			case F05_Write_Single_Coil:
			case F15_Write_Multiple_Coils:
			case Auto_Write_Coils:
				booReadNumberAreaEnabled = false;
				booReadBoolAreaEnabled = false;
				booWriteBoolAreaEnabled = true;
				booWriteNumberAreaEnabled = false;
				break;
			case F06_Write_Single_Register:
			case F16_Write_Multiple_Registers:
			case Auto_Write_Register:
				booReadNumberAreaEnabled = false;
				booReadBoolAreaEnabled = false;
				booWriteBoolAreaEnabled = false;
				booWriteNumberAreaEnabled = true;
				break;
			case F22_Mask_Write_Register:
				booReadNumberAreaEnabled = false;
				booWriteNumberAreaEnabled = false;
				booWriteBoolAreaEnabled = false;
				booReadBoolAreaEnabled = false;
				break;
			case F23_Read_Write_Multiple_Registers:
				booReadNumberAreaEnabled = false;
				booWriteNumberAreaEnabled = false;
				booWriteBoolAreaEnabled = false;
				booReadBoolAreaEnabled = false;
				break;
			case F07_Read_Exception_status:
			case F08_Diagnostic:
			case F11_Get_Com_event_counter:
			case F12_Get_Com_Event_Log:
			case F17_Report_Server_ID:
			case UserFunction:
			default:
				booReadNumberAreaEnabled = false;
				booWriteNumberAreaEnabled = false;
				booWriteBoolAreaEnabled = false;
				booReadBoolAreaEnabled = false;
				break;
			}

			// <editor-fold defaultstate="collapsed" desc="set ReadArea">
			lblReadAddress.setForeground(booReadNumberAreaEnabled || booReadBoolAreaEnabled ? Color.BLACK : Color.GRAY);
			lblBit.setForeground(booReadNumberAreaEnabled ? Color.BLACK : Color.GRAY);
			lblQuantity.setForeground(booReadNumberAreaEnabled || booReadBoolAreaEnabled ? Color.BLACK : Color.GRAY);
			txtReadAddress.setEnabled(booReadNumberAreaEnabled || booReadBoolAreaEnabled);
			txtBit.setEnabled(booReadNumberAreaEnabled);
			txtQuantity.setEnabled(booReadNumberAreaEnabled || booReadBoolAreaEnabled);

			// clear lvValues
			DefaultTableModel model = (DefaultTableModel) lvValues.getModel();
			// clear model
			while (model.getRowCount() > 0) {
				model.removeRow(0);
			}
			model.fireTableDataChanged();
			// lvValues.setBackground(booReadNumberAreaEnabled ||
			// booReadBoolAreaEnabled ? Color.WHITE : Color.GRAY);
			lvValues.setEnabled(booReadNumberAreaEnabled || booReadBoolAreaEnabled);
			// </editor-fold>

			// <editor-fold defaultstate="collapsed" desc="set WriteArea">
			lblWriteAddress
					.setForeground(booWriteNumberAreaEnabled || booWriteBoolAreaEnabled ? Color.BLACK : Color.GRAY);
			txtWriteAddress.setEnabled(booWriteNumberAreaEnabled || booWriteBoolAreaEnabled);
			// </editor-fold>

			// <editor-fold defaultstate="collapsed"
			// desc="set Mask write register">
			if (myFunction == eMBFunction.F22_Mask_Write_Register) {
				lblWriteAddress.setForeground(Color.BLACK);
				txtWriteAddress.setEnabled(true);
			}

			// </editor-fold>
			// <editor-fold defaultstate="collapsed"
			// desc="set read write register">
			if (myFunction == eMBFunction.F23_Read_Write_Multiple_Registers) {

				lblWriteAddress.setForeground(Color.BLACK);
				txtWriteAddress.setEnabled(true);

				lblReadAddress.setForeground(Color.BLACK);
				lblQuantity.setForeground(Color.BLACK);
				txtReadAddress.setEnabled(true);
				txtQuantity.setEnabled(true);
				lvValues.setEnabled(true);

			}

			// </editor-fold>
			// <editor-fold defaultstate="collapsed" desc="set byteorder">
			// set data type
			if (myFunction == eMBFunction.F03_Read_Holding_Registers
					|| myFunction == eMBFunction.F04_Read_Input_Register
					|| myFunction == eMBFunction.F06_Write_Single_Register
					|| myFunction == eMBFunction.F16_Write_Multiple_Registers
					|| myFunction == eMBFunction.F23_Read_Write_Multiple_Registers
					|| myFunction == eMBFunction.Auto_Write_Register) {
				setRegisterModeDataTypeModel(getPreferredRegisterDataType());
				updateByteOrderStateForDataType();
				cmbDataType.setEnabled(true);
			} else {
				cmbByteOrder.setEnabled(false);
				cmbDataType.setEnabled(false);
				cmbDataType.setSelectedItem(eDataType.BOOLEAN);
			}

			// </editor-fold>
			// <editor-fold defaultstate="collapsed"
			// desc="sset input controls for write operations">
			if (myFunction == eMBFunction.F05_Write_Single_Coil || myFunction == eMBFunction.F15_Write_Multiple_Coils
					|| myFunction == eMBFunction.F06_Write_Single_Register
					|| myFunction == eMBFunction.F16_Write_Multiple_Registers
					|| myFunction == eMBFunction.F22_Mask_Write_Register
					|| myFunction == eMBFunction.F23_Read_Write_Multiple_Registers
					|| myFunction == eMBFunction.Auto_Write_Coils || myFunction == eMBFunction.Auto_Write_Register) {
				panWriteValues.setEnabled(true);

				switch (myFunction) {
				case Auto_Write_Coils:
					txtMultipleValues.setText(C_Values_Multiple_Coil);
					chkSingleValue.setSelected(true);
					chkSingleValue.setVisible(true);
					rbOn.setVisible(chkSingleValue.isSelected());
					rbOff.setVisible(chkSingleValue.isSelected());
					rbOn.setSelected(true);
					txtSingleValues.setVisible(false);
					txtMultipleValues.setVisible(!chkSingleValue.isSelected());
					scrollMultipleValues.setVisible(txtMultipleValues.isVisible());
					lblEnterValues.setVisible(true);
					this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
							: resources.getString("lblMultipleValues_Text"));

					lblWriteMaskRegister_AND.setVisible(false);
					lblWriteMaskRegister_OR.setVisible(false);
					txtAND.setVisible(false);
					txtOR.setVisible(false);
					break;
				case Auto_Write_Register:
					txtMultipleValues.setText(C_Values_Multiple_Register);
					chkSingleValue.setSelected(true);
					chkSingleValue.setVisible(true);
					rbOn.setVisible(false);
					rbOff.setVisible(false);
					txtSingleValues.setVisible(true);
					txtSingleValues.setVisible(chkSingleValue.isSelected());
					txtMultipleValues.setVisible(!chkSingleValue.isSelected());
					scrollMultipleValues.setVisible(txtMultipleValues.isVisible());
					txtSingleValues.setText(C_Values_Single_Register);
					lblEnterValues.setVisible(true);
					this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
							: resources.getString("lblMultipleValues_Text"));

					lblWriteMaskRegister_AND.setVisible(false);
					lblWriteMaskRegister_OR.setVisible(false);
					txtAND.setVisible(false);
					txtOR.setVisible(false);
					break;
				case F05_Write_Single_Coil:
					txtMultipleValues.setText(C_Values_Multiple_Coil);
					chkSingleValue.setVisible(true);
					chkSingleValue.setSelected(true);
					rbOn.setVisible(chkSingleValue.isSelected());
					rbOff.setVisible(chkSingleValue.isSelected());
					rbOn.setSelected(true);
					txtSingleValues.setVisible(false);
					txtMultipleValues.setVisible(!chkSingleValue.isSelected());
					scrollMultipleValues.setVisible(txtMultipleValues.isVisible());
					lblEnterValues.setVisible(true);
					this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
							: resources.getString("lblMultipleValues_Text"));

					lblWriteMaskRegister_AND.setVisible(false);
					lblWriteMaskRegister_OR.setVisible(false);
					txtAND.setVisible(false);
					txtOR.setVisible(false);
					break;
				case F06_Write_Single_Register:
					txtSingleValues.setText(C_Values_Single_Register);
					txtMultipleValues.setText(C_Values_Multiple_Register);
					chkSingleValue.setVisible(true);
					chkSingleValue.setSelected(true);
					rbOn.setVisible(false);
					rbOff.setVisible(false);
					txtSingleValues.setVisible(chkSingleValue.isSelected());
					txtMultipleValues.setVisible(!chkSingleValue.isSelected());
					scrollMultipleValues.setVisible(txtMultipleValues.isVisible());
					lblEnterValues.setVisible(true);
					this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
							: resources.getString("lblMultipleValues_Text"));

					lblWriteMaskRegister_AND.setVisible(false);
					lblWriteMaskRegister_OR.setVisible(false);
					txtAND.setVisible(false);
					txtOR.setVisible(false);
					break;
				case F15_Write_Multiple_Coils:
					txtSingleValues.setText(C_Values_Single_Register);
					txtMultipleValues.setText(C_Values_Multiple_Coil);
					chkSingleValue.setVisible(true);
					chkSingleValue.setSelected(false);
					rbOn.setVisible(chkSingleValue.isSelected());
					rbOff.setVisible(chkSingleValue.isSelected());
					rbOn.setSelected(true);
					txtSingleValues.setVisible(chkSingleValue.isSelected());
					txtMultipleValues.setVisible(!chkSingleValue.isSelected());
					scrollMultipleValues.setVisible(txtMultipleValues.isVisible());
					lblEnterValues.setVisible(true);
					this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
							: resources.getString("lblMultipleValues_Text"));

					lblWriteMaskRegister_AND.setVisible(false);
					lblWriteMaskRegister_OR.setVisible(false);
					txtAND.setVisible(false);
					txtOR.setVisible(false);
					break;
				case F16_Write_Multiple_Registers:
					txtMultipleValues.setText(C_Values_Multiple_Register);
					chkSingleValue.setVisible(true);
					chkSingleValue.setSelected(false);
					rbOn.setVisible(false);
					rbOff.setVisible(false);
					txtSingleValues.setVisible(chkSingleValue.isSelected());
					txtMultipleValues.setVisible(!chkSingleValue.isSelected());
					scrollMultipleValues.setVisible(txtMultipleValues.isVisible());
					lblEnterValues.setVisible(true);
					this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
							: resources.getString("lblMultipleValues_Text"));

					lblWriteMaskRegister_AND.setVisible(false);
					lblWriteMaskRegister_OR.setVisible(false);
					txtAND.setVisible(false);
					txtOR.setVisible(false);
					break;
				case F22_Mask_Write_Register:
					chkSingleValue.setVisible(false);
					rbOn.setVisible(false);
					rbOff.setVisible(false);
					txtSingleValues.setVisible(false);
					txtMultipleValues.setVisible(false);
					scrollMultipleValues.setVisible(txtMultipleValues.isVisible());
					lblEnterValues.setVisible(false);

					lblWriteMaskRegister_AND.setVisible(true);
					lblWriteMaskRegister_OR.setVisible(true);
					txtAND.setVisible(true);
					txtOR.setVisible(true);
					break;
				case F23_Read_Write_Multiple_Registers:
					txtSingleValues.setText(C_Values_Single_Register);
					txtMultipleValues.setText(C_Values_Multiple_Register);
					chkSingleValue.setSelected(false);
					chkSingleValue.setVisible(true);
					rbOn.setVisible(false);
					rbOff.setVisible(false);
					txtSingleValues.setVisible(chkSingleValue.isSelected());
					txtMultipleValues.setVisible(!chkSingleValue.isSelected());
					scrollMultipleValues.setVisible(txtMultipleValues.isVisible());
					lblEnterValues.setVisible(true);
					this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
							: resources.getString("lblMultipleValues_Text"));

					lblWriteMaskRegister_AND.setVisible(false);
					lblWriteMaskRegister_OR.setVisible(false);
					txtAND.setVisible(false);
					txtOR.setVisible(false);
					break;
				default:
					break;
				}
				// set writable data
				if (myFunction == eMBFunction.F05_Write_Single_Coil
						|| myFunction == eMBFunction.F15_Write_Multiple_Coils
						|| myFunction == eMBFunction.Auto_Write_Coils) {
					ValuetoWrite = chkSingleValue.isSelected() ? String.valueOf(rbOn.isSelected())
							: txtMultipleValues.getText();
				} else {
					ValuetoWrite = chkSingleValue.isSelected() ? txtSingleValues.getText()
							: txtMultipleValues.getText();
				}

			} else {
				panWriteValues.setEnabled(false);
			}

			// </editor-fold>
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void cmbSlaveID_actionPerformed(ActionEvent e) {
		try {
			// TypeOfCommunication = ASCII or RTU and slave id = 0 then
			// broadcast mode will be enabled
			lblBroadcast.setVisible(Integer.valueOf(cmbSlaveID.getSelectedItem().toString()) == 0
					&& (Device.getConnector().TypeOfCommunication == eTypeOfCommunication.ASCII
							|| Device.getConnector().TypeOfCommunication == eTypeOfCommunication.RTU
							|| Device.getConnector().TypeOfCommunication == eTypeOfCommunication.RTU_over_TCP));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void btnClose_actionPerformed(ActionEvent e) {
		this.setVisible(false);

		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void btnExecute_actionPerformed(ActionEvent e) {
		try {
					
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			switch (eMBFunction.valueOf(cmbFunction.getSelectedItem().toString())) {
			case F01_Read_Coils:
			case F02_Read_Discrete_Inputs:
			case F03_Read_Holding_Registers:
			case F04_Read_Input_Register:
				// execute read
				ExecRead();
				break;
			case Auto_Write_Coils:
			case Auto_Write_Register:
			case F05_Write_Single_Coil:
			case F06_Write_Single_Register:
			case F15_Write_Multiple_Coils:
			case F16_Write_Multiple_Registers:
				// execute write
				ExecWrite();
				break;
			case F22_Mask_Write_Register:
				// execute mask write register
				ExecMaskWrite();
				break;
			case F23_Read_Write_Multiple_Registers:
				// execute read write
				ExecReadWrite();
				break;
			default:
				break;
			}
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void ExecRead() {
		try {
			ReadRequest myReadRequest = null;
			// declare a ReadRequest object
			// and set the request parameters
			// @formatter:off
			if ((eDataType) cmbDataType.getSelectedItem() == eDataType.BOOLEAN
					&& ((eReadFunction.valueOf(cmbFunction.getSelectedItem().toString()))
							.equals(eReadFunction.F03_Read_Holding_Registers)
							|| (eReadFunction.valueOf(cmbFunction.getSelectedItem().toString()))
									.equals(eReadFunction.F04_Read_Input_Register))) {

				myReadRequest = RequestBuilder.ReadRequestBuilder.create(
						Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
						eReadFunction.valueOf(cmbFunction.getSelectedItem().toString()), // modbusfunction
						Integer.valueOf(txtReadAddress.getText()), // Read start adress
						eDataType.valueOf(cmbDataType.getSelectedItem().toString()), // Target Datatype
						Integer.valueOf(txtQuantity.getText()), // quantity of objects to be read
						Byte.valueOf(txtBit.getText())); // Address of first Bit by reading register
			} else {
				myReadRequest = RequestBuilder.ReadRequestBuilder.create(
						Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
						eReadFunction.valueOf(cmbFunction.getSelectedItem().toString()), // modbus function
						Integer.valueOf(txtReadAddress.getText()), // Read start adress
						eDataType.valueOf(cmbDataType.getSelectedItem().toString()), // Target Datatype
						Integer.valueOf(txtQuantity.getText())); // quantity of objects to be read
			}
			// @formatter:on
			// set eventual byte order, standard = eByteOrder.AB_CD;
			myReadRequest.setByteOrder(eByteOrder.valueOf(cmbByteOrder.getSelectedItem().toString()));

			// read from device
			ReadResult res = Device.read(myReadRequest);

			// evaluate results
			// set diagnostic output
			DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
			// clear model
			while (modLog.getRowCount() > 0) {
				modLog.removeRow(0);
			}

			// add summary log entry
			modLog.addRow(new Object[] {
					res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
							: eLogLevel.Error.toString(),
					DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
					" Message: " + res.getMessage() });

			// add log entrys
			for (LogEntry le : res.getDiagnosticLog()) {
				modLog.addRow(new Object[] { le.getLogLevel().toString(),
						DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
						le.getText() + " " + le.getStackTraceString() });

			}
			((AbstractTableModel) lvLog.getModel()).fireTableDataChanged();

			// evaluate values
			DefaultTableModel modValues = (DefaultTableModel) lvValues.getModel();
			// clear model
			while (modValues.getRowCount() > 0) {
				modValues.removeRow(0);
			}
			lvValues.getColumnModel().getColumn(1)
					.setHeaderValue((res.getFunction() == eMBFunction.F03_Read_Holding_Registers
							|| res.getFunction() == eMBFunction.F04_Read_Input_Register) ? "Register" : "Address");
			if (res.getQuality() == OperationResult.eQuality.GOOD) {

				for (ReadValue item : res.fetchValues()) {
					modValues.addRow(new Object[] {
							String.valueOf(item.getAddress()) + " / Pos" + String.valueOf(item.getAddressPosition()),
							String.valueOf(item.getValue()) });
				}
			}

			modValues.fireTableDataChanged();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void ExecWrite() {
		try {
			// parse valuestring and add writable Data here
			Utilities.sValues_to_Write vtw = null;
			vtw = Utilities.CheckValues(ValuetoWrite, eDataType.valueOf(cmbDataType.getSelectedItem().toString()));

			if (!vtw.ParseError) {
				// last warning
				if (JOptionPane.showConfirmDialog(null,
						resources.getString("Continue_Warning_Write") + System.getProperty("line.separator")
								+ resources.getString("Continue_Question"),
						resources.getString("Important_question"),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

					// declare a WriteRequest object and
					// set the request parameters
					// @formatter:off
					WriteRequest myWriteRequest = RequestBuilder.WriteRequestBuilder.create(
							Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave
																						// ID
							eWriteFunction.valueOf(cmbFunction.getSelectedItem().toString()), // modbus
																								// function
							Integer.valueOf(txtWriteAddress.getText()));// write
																		// start
																		// adress

					// set eventual byte order, standard = eByteOrder.AB_CD;
					myWriteRequest.setByteOrder(eByteOrder.valueOf(cmbByteOrder.getSelectedItem().toString()));
					// @formatter:on

					// add writable data to request
					for (Object writevalue : vtw.values) {
						switch (eDataType.valueOf(cmbDataType.getSelectedItem().toString())) {
						case BOOLEAN:
							myWriteRequest.addBoolean((boolean) writevalue);
							break;
						case SHORT:
							myWriteRequest.addShort((short) writevalue);
							break;
						case USHORT:
							myWriteRequest.addUShort((int) (writevalue));
							break;
						case INTEGER:
							myWriteRequest.addInteger((int) writevalue);
							break;
						case UINTEGER:
							myWriteRequest.addUInteger((long) writevalue);
							break;
						case LONG:
							myWriteRequest.addLong((long) writevalue);
							break;
						case ULONG:
							myWriteRequest.addULong(createUlong(writevalue.toString()));
							break;
						case FLOAT:
							myWriteRequest.addFloat((float) writevalue);
							break;
						case DOUBLE:
							myWriteRequest.addDouble((double) writevalue);
							break;
						case STRING:
							myWriteRequest.addString((String) writevalue);
							break;
						case PLC_INT:
							myWriteRequest.addPLC_INT((short) writevalue);
							break;
						case PLC_DINT:
							myWriteRequest.addPLC_DINT((int) writevalue);
							break;
						case PLC_LINT:
							myWriteRequest.addPLC_LINT((long) writevalue);
							break;
						case PLC_WORD:
							myWriteRequest.addPLC_WORD((int) writevalue);
							break;
						case PLC_DWORD:
							myWriteRequest.addPLC_DWORD((long) writevalue);
							break;
						case PLC_LWORD:
							myWriteRequest.addPLC_LWORD(createUlong(writevalue.toString()));
							break;
						case PLC_BCD16:
							myWriteRequest.addPLC_BCD16((short) writevalue);
							break;
						case PLC_BCD32:
							myWriteRequest.addPLC_BCD32((int) writevalue);
							break;
						case BYTE:
							// abort message
							JOptionPane.showMessageDialog(null,
									resources.getString("wrong_datatype_byte") + " "
											+ resources.getString("operation_aborted"),
									"", JOptionPane.INFORMATION_MESSAGE);
							return;
						default:
							// abort message
							JOptionPane.showMessageDialog(null,
									resources.getString("wrong_datatype") + " "
											+ resources.getString("operation_aborted"),
									"", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}

					// write
					WriteResult res = Device.write(myWriteRequest);

					// evaluate results
					// set diagnostic output
					DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
					// clear model
					while (modLog.getRowCount() > 0) {
						modLog.removeRow(0);
					}

					// add summary log entry
					modLog.addRow(new Object[] {
							res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
									: eLogLevel.Error.toString(),
							DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
							" Message: " + res.getMessage() });

					// add log entrys
					for (LogEntry le : res.getDiagnosticLog()) {
						modLog.addRow(new Object[] { le.getLogLevel().toString(),
								DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
								le.getText() + " " + le.getStackTraceString() });

					}
					modLog.fireTableDataChanged();
				} else {
					// abort message
					JOptionPane.showMessageDialog(null, resources.getString("operation_aborted"), "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				// parse error message
				DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
				// clear model
				while (modLog.getRowCount() > 0) {
					modLog.removeRow(0);
				}

				// add log entry
				modLog.addRow(new Object[] { eLogLevel.Error.toString(),
						DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
						resources.getString("ParseError") });

				modLog.fireTableDataChanged();
				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void ExecReadWrite() {
		try {
			Utilities.sValues_to_Write vtw = Utilities.CheckValues(ValuetoWrite, getPreferredRegisterDataType());
			if (!vtw.ParseError) {

				if (JOptionPane.showConfirmDialog(null,
						resources.getString("Continue_Warning_Write") + System.getProperty("line.separator")
								+ resources.getString("Continue_Question"),
						resources.getString("Important_question"),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					// declare a ReadRequest object
					// and set the request parameters
					// ReadWriteRequest will allways using FC23
					// @formatter:off
					ReadWriteRegisterRequest myReadWriteRequest = RequestBuilder.ReadWriteRequestBuilder.create(
							Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
							Integer.valueOf(txtReadAddress.getText()), // read
																		// address
							eDataType.valueOf(cmbDataType.getSelectedItem().toString()), // Target
																							// //
																							// Datattype
							Integer.valueOf(txtQuantity.getText()), // quantity
																	// of
																	// objects
																	// to
																	// be
																	// read
							Integer.valueOf(txtWriteAddress.getText())); // WriteAddress

					// set eventual byte order, standard = eByteOrder.AB_CD;
					myReadWriteRequest.setByteOrder(eByteOrder.valueOf(cmbByteOrder.getSelectedItem().toString()));
					// @formatter:on

					for (Object writevalue : vtw.values) {
						if (Device.getRegisterMode().equals(eRegisterMode._16Bit)) {
							myReadWriteRequest.addUShort((int) writevalue);
						} else if (Device.getRegisterMode().equals(eRegisterMode._32Bit)) {
							myReadWriteRequest.addUInteger((long) writevalue);
						} else {
							myReadWriteRequest.addULong(createUlong(writevalue.toString()));
						}
					}

					// read from device
					ReadWriteRegisterResult res = Device.readWrite(myReadWriteRequest);

					// evaluate results
					// set diagnostic output
					DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
					// clear model
					while (modLog.getRowCount() > 0) {
						modLog.removeRow(0);
					}

					// add summary log entry
					modLog.addRow(new Object[] {
							res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
									: eLogLevel.Error.toString(),
							DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
							" Message: " + res.getMessage() });

					// add log entrys
					for (LogEntry le : res.getDiagnosticLog()) {
						modLog.addRow(new Object[] { le.getLogLevel().toString(),
								DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
								le.getText() + " " + le.getStackTraceString() });

					}
					modLog.fireTableDataChanged();

					// evaluate values
					DefaultTableModel modValues = (DefaultTableModel) lvValues.getModel();
					// clear model
					while (modValues.getRowCount() > 0) {
						modValues.removeRow(0);
					}
					lvValues.getColumnModel().getColumn(1)
							.setHeaderValue((res.getFunction() == eMBFunction.F03_Read_Holding_Registers
									|| res.getFunction() == eMBFunction.F04_Read_Input_Register) ? "Register"
											: "Address");
					if (res.getQuality() == OperationResult.eQuality.GOOD) {

						for (ReadValue item : res.fetchValues()) {
							modValues.addRow(new Object[] {
									String.valueOf(item.getAddress()) + " / Pos"
											+ String.valueOf(item.getAddressPosition()),
									String.valueOf(item.getValue()) });
						}
					}
					modValues.fireTableDataChanged();
				} else {
					// abort message
					JOptionPane.showMessageDialog(null, resources.getString("operation_aborted"), "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				// parse error message
				DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
				// clear model
				while (modLog.getRowCount() > 0) {
					modLog.removeRow(0);
				}

				// add log entry
				modLog.addRow(new Object[] { eLogLevel.Error.toString(),
						DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
						resources.getString("ParseError") });

				modLog.fireTableDataChanged();
				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void ExecMaskWrite() {
		try {
			// parse valuestring and add writable Data here
			Utilities.sValues_to_Write vtw = Utilities.CheckValues(
					txtAND.getText() + System.getProperty("line.separator") + txtOR.getText(), eDataType.USHORT);
			if (!vtw.ParseError) {
				if (JOptionPane.showConfirmDialog(null,
						resources.getString("Continue_Warning_Write") + System.getProperty("line.separator")
								+ resources.getString("Continue_Question"),
						resources.getString("Important_question"),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

					// declare a WriteRequest object and
					// set the request parameters
					// Mask write register will allway using FC22
					// @formatter:off
					MaskWriteRegisterRequest myWriteRequest = RequestBuilder.MaskWriteRequestBuilder.create(
							Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
							Integer.valueOf(txtWriteAddress.getText()), // write
																		// address
							Integer.valueOf(vtw.values.get(0).toString()), // AND Mask
							Integer.valueOf(vtw.values.get(1).toString())); // OR mask
					// @formatter:on
					// write
					MaskWriteRegisterResult res = Device.maskWriteRegister(myWriteRequest);

					// evaluate results
					// set diagnostic output
					DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
					// clear model
					while (modLog.getRowCount() > 0) {
						modLog.removeRow(0);
					}

					// add summary log entry
					modLog.addRow(new Object[] {
							res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
									: eLogLevel.Error.toString(),
							DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
							" Message: " + res.getMessage() });

					// add log entrys
					for (LogEntry le : res.getDiagnosticLog()) {
						modLog.addRow(new Object[] { le.getLogLevel().toString(),
								DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
								le.getText() + " " + le.getStackTraceString() });

					}
					modLog.fireTableDataChanged();

				} else {
					// abort message
					JOptionPane.showMessageDialog(null, resources.getString("operation_aborted"), "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				// parse error message
				DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
				// clear model
				while (modLog.getRowCount() > 0) {
					modLog.removeRow(0);
				}

				// add log entry
				modLog.addRow(new Object[] { eLogLevel.Error.toString(),
						DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
						resources.getString("ParseError") });

				modLog.fireTableDataChanged();
				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void chkSingleValue_mouseClicked(MouseEvent arg) {
		// switch between txtMultipleValues and txtSingleValues
		if (eMBFunction.valueOf(cmbFunction.getSelectedItem().toString()).equals(eMBFunction.F05_Write_Single_Coil)
				|| eMBFunction.valueOf(cmbFunction.getSelectedItem().toString())
						.equals(eMBFunction.F15_Write_Multiple_Coils)
				|| eMBFunction.valueOf(cmbFunction.getSelectedItem().toString()).equals(eMBFunction.Auto_Write_Coils)) {
			txtSingleValues.setVisible(false);
			rbOn.setVisible(chkSingleValue.isSelected());
			rbOff.setVisible(chkSingleValue.isSelected());
			ValuetoWrite = chkSingleValue.isSelected() ? String.valueOf(rbOn.isSelected())
					: txtMultipleValues.getText();
		} else {
			txtSingleValues.setVisible(chkSingleValue.isSelected());
			rbOn.setVisible(false);
			rbOff.setVisible(false);
			ValuetoWrite = chkSingleValue.isSelected() ? txtSingleValues.getText() : txtMultipleValues.getText();
		}
		txtMultipleValues.setVisible(!chkSingleValue.isSelected());
		scrollMultipleValues.setVisible(txtMultipleValues.isVisible());
		lblEnterValues.setVisible(true);
		this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
				: resources.getString("lblMultipleValues_Text"));

	}

	private void txtMultipleValues_TextChanged(DocumentEvent e) {
		// set writable string
		ValuetoWrite = chkSingleValue.isSelected() ? txtSingleValues.getText() : txtMultipleValues.getText();
	}

	private void txtSingleValues_TextChanged(DocumentEvent e) {
		// set writable string
		ValuetoWrite = txtSingleValues.getText();
	}

	private void rbOn_mouseClicked(MouseEvent e) {
		// set writable string
		ValuetoWrite = Boolean.toString(true);
	}

	private void rbOff_mouseClicked(MouseEvent e) {
		// set writable string
		ValuetoWrite = String.valueOf( Boolean.toString(false));
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

	private void btnSaveRequest_actionPerformed(ActionEvent e) {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// Write Settings in PLCcomModbusSingleRequest.xml
			Properties p = new Properties();

			if (cmbFunction.getSelectedItem() != null) {
				p.setProperty("Function", cmbFunction.getSelectedItem().toString());
			}

			if (cmbSlaveID.getSelectedItem() != null) {
				p.setProperty("SlaveID", cmbSlaveID.getSelectedItem().toString());
			}

			if (cmbDataType.getSelectedItem() != null) {
				p.setProperty("Target_Datatype", cmbDataType.getSelectedItem().toString());
			}

			if (cmbByteOrder.getSelectedItem() != null) {
				p.setProperty("ByteOrder", cmbByteOrder.getSelectedItem().toString());
			}

			p.setProperty("ReadAddress", txtReadAddress.getText());
			p.setProperty("Quantity", txtQuantity.getText());
			p.setProperty("StartBit", txtBit.getText());
			p.setProperty("WriteAddress", txtWriteAddress.getText());
			p.setProperty("isSingleValue", String.valueOf(chkSingleValue.isSelected()));
			p.setProperty("SingleValueBool", String.valueOf(rbOn.isSelected()));
			p.setProperty("SingleValues", txtSingleValues.getText());
			p.setProperty("MultipleValues", txtMultipleValues.getText());
			p.setProperty("AND", txtAND.getText());
			p.setProperty("OR", txtOR.getText());

			FileOutputStream fos = new FileOutputStream(new File("PLCcomModbusSingleRequest.xml").getAbsolutePath());
			p.storeToXML(fos, "PLCcom Example Settings", "UTF8");
			fos.close();
			JOptionPane.showMessageDialog(null,
					resources.getString("successfully_saved") + System.getProperty("line.separator") + "File: "
							+ new File("PLCcomModbusSingleRequest.xml").getAbsolutePath(),
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

	private void btnLoadRequest_actionPerformed(ActionEvent e) {
		Properties p = new Properties();

		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// open file
			FileInputStream fis = new FileInputStream(new File("PLCcomModbusSingleRequest.xml").getAbsolutePath());

			// load saved settiungs from PLCcomModbusSingleRequest.xml
			p.loadFromXML(fis);

			if (p.containsKey("Function")) {
				cmbFunction.setSelectedItem(p.getProperty("Function"));
			}
			if (p.containsKey("SlaveID")) {
				cmbSlaveID.setSelectedItem(Integer.valueOf(p.getProperty("SlaveID")));
			}
			if (p.containsKey("Target_Datatype")) {
				eDataType savedDataType = eDataType.valueOf(p.getProperty("Target_Datatype"));
				cmbDataType.setSelectedItem(isDataTypeSupportedByCurrentRegisterMode(savedDataType) ? savedDataType
						: getPreferredRegisterDataType());
			}
			if (p.containsKey("ByteOrder")) {
				cmbByteOrder.setSelectedItem(eByteOrder.valueOf(p.getProperty("ByteOrder")));
			}
			if (p.containsKey("ReadAddress")) {
				txtReadAddress.setText(p.getProperty("ReadAddress"));
			}
			if (p.containsKey("Quantity")) {
				txtQuantity.setText(p.getProperty("Quantity"));
			}
			if (p.containsKey("StartBit")) {
				txtBit.setText(p.getProperty("StartBit"));
			}
			if (p.containsKey("WriteAddress")) {
				txtWriteAddress.setText(p.getProperty("WriteAddress"));
			}
			if (p.containsKey("isSingleValue")) {
				chkSingleValue.setSelected(Boolean.valueOf(p.getProperty("isSingleValue")));
			}
			if (p.containsKey("SingleValueBool")) {
				rbOn.setSelected(Boolean.valueOf(p.getProperty("SingleValueBool")));
			}
			if (p.containsKey("SingleValues")) {
				txtSingleValues.setText(p.getProperty("SingleValues"));
			}
			if (p.containsKey("MultipleValues")) {
				txtMultipleValues.setText(p.getProperty("MultipleValues"));
			}
			if (p.containsKey("AND")) {
				txtAND.setText(p.getProperty("AND"));
			}
			if (p.containsKey("OR")) {
				txtOR.setText(p.getProperty("OR"));
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

	private void cmbDataType_actionPerformed(ActionEvent e) {
		// switch byteorder depending on the selected function
		if (cmbFunction.getSelectedItem() != null) {
			if (eMBFunction.valueOf(cmbFunction.getSelectedItem().toString())
					.equals(eMBFunction.F03_Read_Holding_Registers)
					|| eMBFunction.valueOf(cmbFunction.getSelectedItem().toString())
							.equals(eMBFunction.F04_Read_Input_Register)
					|| eMBFunction.valueOf(cmbFunction.getSelectedItem().toString())
							.equals(eMBFunction.F06_Write_Single_Register)
					|| eMBFunction.valueOf(cmbFunction.getSelectedItem().toString())
							.equals(eMBFunction.F16_Write_Multiple_Registers)
					|| eMBFunction.valueOf(cmbFunction.getSelectedItem().toString())
							.equals(eMBFunction.F23_Read_Write_Multiple_Registers)
					|| eMBFunction.valueOf(cmbFunction.getSelectedItem().toString())
							.equals(eMBFunction.Auto_Write_Register)) {
				updateByteOrderStateForDataType();
			} else {
				cmbByteOrder.setEnabled(false);
			}
		}
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
