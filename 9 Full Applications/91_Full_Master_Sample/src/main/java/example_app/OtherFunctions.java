package example_app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
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
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
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

@SuppressWarnings("serial")
class OtherFunctions extends JFrame {

	private ModbusMaster Device;
	private final ResourceBundle resources = ResourceBundle.getBundle("example_app.resources.resources");

	private JPanel grpAction;
	private DisabledJPanel panAddress;
	private JComboBox<Integer> cmbSlaveID;
	private JComboBox<eOperationFunction> cmbFunction;
	private JLabel lblFunction;
	private JLabel lblSlaveID;
	private JTextPane txtInfoOF;
	private JTextPane lblBroadcast;
	private JTextArea txtResult;
	private JTable lvLog;
	private JPanel statusBar = new JPanel();
	private JTextField lblDeviceGUID;
	private JTextField lblDeviceType;

	/**
	 * Create the dialog.
	 * 
	 * @param Device
	 *            a modbusmaster object
	 */
	OtherFunctions(ModbusMaster Device) {
		setTitle(ResourceBundle.getBundle("example_app.resources.resources").getString("ReadWriteBox.this.title_1")); //$NON-NLS-1$ //$NON-NLS-2$
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
				if (value != null && value instanceof javax.swing.plaf.FontUIResource)
					UIManager.put(key, new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 11));
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

		this.setBounds(100, 100, 687, 496);

		this.getContentPane().setLayout(null);

		this.grpAction = new JPanel();
		this.grpAction.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAction_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.grpAction.setBounds(12, 4, 656, 181);
		this.grpAction.setLayout(null);
		this.getContentPane().add(grpAction);

		panAddress = new DisabledJPanel(grpAction);
		panAddress.setBounds(grpAction.getBounds());
		panAddress.setDisabledColor(new Color(240, 240, 240, 100));
		panAddress.setEnabled(true);
		this.getContentPane().add(panAddress);

		cmbFunction = new JComboBox<eOperationFunction>();
		cmbFunction.setBounds(91, 19, 155, 21);
		grpAction.add(cmbFunction);

		cmbSlaveID = new JComboBox<Integer>();
		cmbSlaveID.setBounds(91, 47, 155, 21);
		grpAction.add(cmbSlaveID);

		lblFunction = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblFunction_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblFunction.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFunction.setBounds(3, 22, 78, 14);
		grpAction.add(lblFunction);

		lblSlaveID = new JLabel(
				ResourceBundle.getBundle("example_app.resources.resources").getString("lblSlaveID_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		lblSlaveID.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSlaveID.setBounds(3, 50, 78, 14);
		grpAction.add(lblSlaveID);

		txtInfoOF = new JTextPane();
		txtInfoOF.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtInfoOF.setText(ResourceBundle.getBundle("example_app.resources.resources").getString("txtInfoOF_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		txtInfoOF.setEditable(false);
		txtInfoOF.setBackground(SystemColor.info);
		txtInfoOF.setBounds(266, 21, 381, 47);
		grpAction.add(txtInfoOF);

		lblBroadcast = new JTextPane();
		lblBroadcast.setForeground(Color.RED);
		lblBroadcast
				.setText(ResourceBundle.getBundle("example_app.resources.resources").getString("lblBroadcast_Text"));
		lblBroadcast.setEditable(false);
		lblBroadcast.setBackground(SystemColor.menu);
		lblBroadcast.setBounds(91, 70, 101, 47);
		grpAction.add(lblBroadcast);

		JButton btnExecute = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnExecute_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnExecute_actionPerformed(e);
			}
		});
		btnExecute.setBounds(6, 98, 68, 68);
		grpAction.add(btnExecute);
		btnExecute.setIcon(new ImageIcon(OtherFunctions.class.getResource("/example_app/resources/gear_replace.png")));
		btnExecute.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnExecute.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnExecute.setMargin(new Insets(0, 0, 0, 0));
		btnExecute.setHorizontalTextPosition(SwingConstants.CENTER);

		txtResult = new JTextArea();
		txtResult.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.LIGHT_GRAY));
		txtResult.setBounds(260, 96, 381, 68);
		txtResult.setLineWrap(true);
		txtResult.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(txtResult);
		scrollPane.setBounds(txtResult.getBounds());
		grpAction.add(scrollPane);

		JButton btnSaveLogtoClipboard = new JButton(ResourceBundle.getBundle("example_app.resources.resources") //$NON-NLS-1$
				.getString("btnSaveSlaveLogtoClipboard_Text")); //$NON-NLS-1$
		btnSaveLogtoClipboard
				.setIcon(new ImageIcon(OtherFunctions.class.getResource("/example_app/resources/copy.png")));
		btnSaveLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoClipboard.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnSaveLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoClipboard.setBounds(18, 214, 68, 68);
		getContentPane().add(btnSaveLogtoClipboard);

		JButton btnSaveLogtoFile = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnSaveSlaveLogtoFile_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnSaveLogtoFile.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/resources/data_floppy_disk.png")));
		btnSaveLogtoFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoFile.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnSaveLogtoFile.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoFile.setBounds(18, 288, 68, 68);
		getContentPane().add(btnSaveLogtoFile);

		JButton btnClose = new JButton(
				ResourceBundle.getBundle("example_app.resources.resources").getString("btnClose_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_addActionListener(e);
			}
		});
		btnClose.setIcon(new ImageIcon(OtherFunctions.class.getResource("/example_app/resources/exit.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setToolTipText("<html><center>copy log to</center><center>clipboard</center></html> ");
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(585, 366, 68, 68);
		getContentPane().add(btnClose);

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
		lvLog.setBounds(103, 214, 550, 142);
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
		statusBar.setBounds(0, 447, 681, 22);
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

	protected void formWindowClosing(WindowEvent e) {
		Master_Example.CountOpenDialogs--;
	}

	protected void formWindowOpened(WindowEvent arg0) {

		lblDeviceGUID.setText("Device Guid: " + Device.getDeviceUUID().toString());
		lblDeviceType.setText("Adapter Type: " + Device.getConnector().getClass().toString());

		// Set cmbFunction datasource
		cmbFunction.setModel(new DefaultComboBoxModel<eOperationFunction>(eOperationFunction.values()));
		cmbFunction.setSelectedItem(eOperationFunction.F08_Diagnostic);

		// init slave id
		DefaultComboBoxModel<Integer> modSlaveID = new DefaultComboBoxModel<Integer>();
		for (int i = 0; i < 248; i++) {
			modSlaveID.addElement(i);
		}
		
		cmbSlaveID.setModel(modSlaveID);
		cmbSlaveID.setSelectedItem(1);

		lblBroadcast.setVisible(Integer.valueOf(cmbSlaveID.getSelectedItem().toString()) == 0);
	}

	private void btnClose_addActionListener(ActionEvent e) {
		this.setVisible(false);

		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void btnExecute_actionPerformed(ActionEvent e) {
		// execute desired function
		switch (eOperationFunction.valueOf(cmbFunction.getSelectedItem().toString())) {
		case F07_Read_Exception_status:
			getExceptionStatus();
			break;
		case F08_Diagnostic:
			GetDiagnostic();
			break;
		case F11_Get_Com_event_counter:
			getCommEventCounter();
			break;
		case F12_Get_Com_Event_Log:
			getCommEventlog();
			break;
		case F17_Report_Server_ID:
			ReportServerID();
			break;
		case UserFunction:
			MBUserFunction();
			break;
		}
	}

	private void getExceptionStatus() {
		// declare a OperationRequest object
		// and set the request parameters
		// @formatter:off
		OperationRequest myRequest = RequestBuilder.OperationRequestBuilder.create(
				Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
				eOperationFunction.F07_Read_Exception_status);

		// @formatter:on
		// execute function
		ExceptionStatusResult res = Device.getExceptionStatus(myRequest);

		// evaluate results

		// set diagnostic output
		// set diagnostic output
		DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
		// clear model
		while (modLog.getRowCount() > 0) {
			modLog.removeRow(0);
		}
		for (LogEntry le : res.getDiagnosticLog()) {
			modLog.addRow(new Object[] { le.getLogLevel().toString(),
					DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
					le.getText() + " " + le.getStackTraceString() });

		}

		txtResult.setText("");
		if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
			txtResult.append("Data >> ");
			txtResult.append(String.valueOf(res.getData()));
			txtResult.append(System.getProperty("line.separator"));
		}
	}

	private void GetDiagnostic() {
		// declare a ReadRequest object
		// and set the request parameters
		// @formatter:off
		DiagnosticRequest myDiagnosticRequest = RequestBuilder.DiagnosticRequestBuilder.create(
				Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
				0, // quantity of objects to be read
				new byte[] { 3, 4 }); // diagnostic data
		// @formatter:on
		// execute function
		DiagnosticResult res = Device.getDiagnostics(myDiagnosticRequest);

		// evaluate results

		// set diagnostic output
		DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
		// clear model
		while (modLog.getRowCount() > 0) {
			modLog.removeRow(0);
		}
		for (LogEntry le : res.getDiagnosticLog()) {
			modLog.addRow(new Object[] { le.getLogLevel().toString(),
					DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
					le.getText() + " " + le.getStackTraceString() });

		}

		txtResult.setText("");
		if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
			int counter = 0;
			for (Object value : res.getResultData()) {
				txtResult.append(String.format("%04d", counter++));
				txtResult.append(" >> ");
				txtResult.append(String.valueOf(value));
				txtResult.append(System.getProperty("line.separator"));
			}
		}

	}

	private void getCommEventCounter() {
		// declare a OperationRequest object
		// and set the request parameters
		// @formatter:off
		OperationRequest myRequest = RequestBuilder.OperationRequestBuilder.create(
				Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
				eOperationFunction.F11_Get_Com_event_counter);
		// @formatter:on
		// execute function
		CommEventCounterResult res = Device.getCommEventCounter(myRequest);

		// evaluate results

		// set diagnostic output
		DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
		// clear model
		while (modLog.getRowCount() > 0) {
			modLog.removeRow(0);
		}
		for (LogEntry le : res.getDiagnosticLog()) {
			modLog.addRow(new Object[] { le.getLogLevel().toString(),
					DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
					le.getText() + " " + le.getStackTraceString() });

		}

		txtResult.setText("");
		if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
			txtResult.append("Status >> ");
			txtResult.append(String.valueOf(res.getStatus()));
			txtResult.append(System.getProperty("line.separator"));
			txtResult.append("EventCount >> ");
			txtResult.append(String.valueOf(res.getEventCount()));
		}
	}

	private void getCommEventlog() {
		// declare a OperationRequest object
		// and set the request parameters
		// @formatter:off
		OperationRequest myRequest = RequestBuilder.OperationRequestBuilder.create(
				Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
				eOperationFunction.F12_Get_Com_Event_Log);

		// @formatter:on
		// execute function
		CommEventLogResult res = Device.getCommEventLog(myRequest);

		// evaluate results

		// set diagnostic output
		DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
		// clear model
		while (modLog.getRowCount() > 0) {
			modLog.removeRow(0);
		}
		for (LogEntry le : res.getDiagnosticLog()) {
			modLog.addRow(new Object[] { le.getLogLevel().toString(),
					DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
					le.getText() + " " + le.getStackTraceString() });

		}

		txtResult.setText("");
		if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
			txtResult.append("Status >> ");
			txtResult.append(String.valueOf(res.getStatus()));
			txtResult.append(System.getProperty("line.separator"));
			txtResult.append("EventCount >> ");
			txtResult.append(String.valueOf(res.getEventCount()));
			txtResult.append(System.getProperty("line.separator"));
			txtResult.append("MessageCount >> ");
			txtResult.append(String.valueOf(res.getMessageCount()));
			int counter = 0;
			for (byte value : res.getEvents()) {
				txtResult.append("Event ");
				txtResult.append(String.format("%04d", counter++));
				txtResult.append(" >> ");
				txtResult.append(String.valueOf(value));
				txtResult.append(System.getProperty("line.separator"));
			}
		}
	}

	private void ReportServerID() {
		// declare a OperationRequest object
		// and set the request parameters
		// @formatter:off
		OperationRequest myRequest = RequestBuilder.OperationRequestBuilder.create(
				Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
				eOperationFunction.F17_Report_Server_ID);

		// @formatter:on
		// execute function
		ReportServerIDResult res = Device.getReportServerID(myRequest);

		// evaluate results

		// set diagnostic output
		DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
		// clear model
		while (modLog.getRowCount() > 0) {
			modLog.removeRow(0);
		}
		for (LogEntry le : res.getDiagnosticLog()) {
			modLog.addRow(new Object[] { le.getLogLevel().toString(),
					DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
					le.getText() + " " + le.getStackTraceString() });

		}

		txtResult.setText("");
		if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
			txtResult.append("ServerID >> ");
			txtResult.append(String.valueOf(res.getServerID()));
			txtResult.append(System.getProperty("line.separator"));
			txtResult.append("Run Indicator Status >> ");
			txtResult.append(String.valueOf(res.getRunIndicatorStatus()));
			int counter = 0;
			for (byte value : res.getAdditionalData()) {
				txtResult.append("Event ");
				txtResult.append(String.format("%04d", counter++));
				txtResult.append(" >> ");
				txtResult.append(String.valueOf(value));
				txtResult.append(System.getProperty("line.separator"));
			}
		}
	}

	private void MBUserFunction() {
		// declare a OperationRequest object
		// and set the request parameters
		// @formatter:ff
		OperationRequest myRequest = RequestBuilder.OperationRequestBuilder.createUserRequest(
				Integer.valueOf(cmbSlaveID.getSelectedItem().toString()), // Slave ID
				65, new byte[] { 0, 2, 0, 10 }); // fictional user function 65 with data

		// @formatter:on
		// execute function
		UserFunctionResult res = Device.callUserFunction(myRequest);

		// evaluate results

		// set diagnostic output
		DefaultTableModel modLog = (DefaultTableModel) lvLog.getModel();
		// clear model
		while (modLog.getRowCount() > 0) {
			modLog.removeRow(0);
		}
		for (LogEntry le : res.getDiagnosticLog()) {
			modLog.addRow(new Object[] { le.getLogLevel().toString(),
					DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
					le.getText() + " " + le.getStackTraceString() });

		}

		txtResult.setText("");
		if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
			int counter = 0;
			for (byte value : res.getResultData()) {
				txtResult.append("return byte ");
				txtResult.append(String.format("%04d", counter++));
				txtResult.append(" >> ");
				txtResult.append(String.valueOf(value));
				txtResult.append(System.getProperty("line.separator"));
			}
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

}
