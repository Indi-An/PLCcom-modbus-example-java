package example_app;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.indian.plccom.modbus.Enums.*;

import jssc.SerialPortList;
import example_app.DisabledJPanel.DisabledJPanel;

class ListenerSettings extends JDialog {


	
	/**
	 * private member
	 */
	private static final long serialVersionUID = 240347472097691404L;
	private HashMap<String, Object> Args = new HashMap<String, Object>();
	private final ResourceBundle resources = ResourceBundle
			.getBundle("example_app.resources.resources");
	private JTextArea txtInfoCreateListener;
	private JTextField txtListenerName;
	private JPanel grbAddress;
	private JComboBox<eTypeOfCommunication> cmbConnectionType;
	private JLabel lblListenerName;
	private JLabel lblConnectionType;
	private JPanel grbListenerSettings;
	private JPanel grbIPSettings;
	private JPanel grbSerialSettings;
	private DisabledJPanel panIPSettings;
	private DisabledJPanel panSerialSettings;
	private JTextField txtLocalPort;
	private JComboBox<String> cmbSerialPort;
	private JComboBox<eBaudrate> cmbBaudrate;
	private JComboBox<eParity> cmbParity;
	private JComboBox<eDataBits> cmbDataBits;
	private JComboBox<eStopBits> cmbStopbits;
	private JComboBox<eFlowControl> cmbFlowcontrol;
	private JLabel lblLocalPort;
	private JLabel lblSerialPort;
	private JLabel lblBaudrate;
	private JLabel lblParity;
	private JLabel lblDataBits;
	private JLabel lblStopBits;
	private JLabel lblFlowcontrol;


	/**
	 * Create the dialog.
	 */
	ListenerSettings() {


		initialize();
	}

	private void initialize() {

		// set global lock and feel platform independent
		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());

			@SuppressWarnings("rawtypes")
			java.util.Enumeration keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value != null
						&& value instanceof javax.swing.plaf.FontUIResource)
					UIManager.put(key, new javax.swing.plaf.FontUIResource(
							"Arial", Font.PLAIN, 11));
			}
			WorkshopUi.installScaledCheckBoxIcon();

		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				formWindowOpened(arg0);
			}
		});

		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setTitle(ResourceBundle.getBundle("example_app.resources.resources")
				.getString("create_listener_Text"));
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				ListenerSettings.class
						.getResource("/example_app/resources/ear.png")));
		setResizable(false);
		setBounds(100, 100, 365, 510);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		getContentPane().setLayout(null);

		txtInfoCreateListener = new JTextArea();
		txtInfoCreateListener.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtInfoCreateListener.setFocusable(false);
		txtInfoCreateListener.setFocusTraversalKeysEnabled(false);
		txtInfoCreateListener.setRequestFocusEnabled(false);
		txtInfoCreateListener.setBackground(SystemColor.info);
		txtInfoCreateListener.setEditable(false);
		txtInfoCreateListener.setBounds(12, 12, 330, 46);
		txtInfoCreateListener.setLineWrap(true);
		txtInfoCreateListener.setWrapStyleWord(true);
		txtInfoCreateListener.setText(ResourceBundle.getBundle(
				"example_app.resources.resources").getString(
				"txtInfoCreateListener_Text"));
		getContentPane().add(txtInfoCreateListener);

		grbAddress = new JPanel();
		grbAddress.setLayout(null);
		grbAddress.setBorder(new TitledBorder(null, "",

		TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbAddress.setBounds(12, 61, 330, 403);
		getContentPane().add(grbAddress);

		txtListenerName = new JTextField();
		txtListenerName.setBounds(162, 22, 144, 20);
		grbAddress.add(txtListenerName);
		txtListenerName.setColumns(10);

		cmbConnectionType = new JComboBox<eTypeOfCommunication>();
		cmbConnectionType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cmbConnectionType_actionPerformed(arg0);
			}
		});
		cmbConnectionType.setBounds(162, 51, 144, 21);
		grbAddress.add(cmbConnectionType);

		lblListenerName = new JLabel(ResourceBundle.getBundle(
				"example_app.resources.resources").getString(
				"lblListenerName_Text"));
		lblListenerName.setBounds(22, 22, 105, 18);
		grbAddress.add(lblListenerName);

		lblConnectionType = new JLabel(ResourceBundle.getBundle(
				"example_app.resources.resources").getString(
				"lblConnectionType_Text"));
		lblConnectionType.setBounds(22, 54, 105, 18);
		grbAddress.add(lblConnectionType);

		grbListenerSettings = new JPanel();
		grbListenerSettings.setLayout(null);
		grbListenerSettings.setBorder(new TitledBorder(null, ResourceBundle
				.getBundle("example_app.resources.resources").getString(
						"grbListenerSettings_Text"),

		TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbListenerSettings.setBounds(7, 83, 313, 235);
		grbAddress.add(grbListenerSettings);

		grbIPSettings = new JPanel();
		grbIPSettings.setLayout(null);
		grbIPSettings.setBorder(null);
		grbIPSettings.setBounds(137, 19, 170, 33);
		grbListenerSettings.add(grbIPSettings);

		txtLocalPort = new JTextField();
		txtLocalPort.setText("502");
		txtLocalPort.setBounds(17, 6, 144, 20);
		grbIPSettings.add(txtLocalPort);
		txtLocalPort.setColumns(10);

		panIPSettings = new DisabledJPanel(grbIPSettings);
		panIPSettings.setLocation(grbIPSettings.getLocation());
		panIPSettings.setSize(grbIPSettings.getSize());
		panIPSettings.setDisabledColor(new Color(240, 240, 240, 100));
		panIPSettings.setEnabled(true);
		grbListenerSettings.add(panIPSettings);

		grbSerialSettings = new JPanel();
		grbSerialSettings.setLayout(null);
		grbSerialSettings.setBorder(null);
		grbSerialSettings.setBounds(136, 56, 171, 172);
		grbListenerSettings.add(grbSerialSettings);

		panSerialSettings = new DisabledJPanel(grbSerialSettings);

		cmbSerialPort = new JComboBox<String>();
		cmbSerialPort.setBounds(18, 3, 144, 21);
		grbSerialSettings.add(cmbSerialPort);

		cmbBaudrate = new JComboBox<eBaudrate>();
		cmbBaudrate.setBounds(18, 32, 144, 21);
		grbSerialSettings.add(cmbBaudrate);

		cmbParity = new JComboBox<eParity>();
		cmbParity.setBounds(18, 60, 144, 21);
		grbSerialSettings.add(cmbParity);

		cmbDataBits = new JComboBox<eDataBits>();
		cmbDataBits.setBounds(18, 87, 144, 21);
		grbSerialSettings.add(cmbDataBits);

		cmbStopbits = new JComboBox<eStopBits>();
		cmbStopbits.setBounds(18, 114, 144, 21);
		grbSerialSettings.add(cmbStopbits);

		cmbFlowcontrol = new JComboBox<eFlowControl>();
		cmbFlowcontrol.setBounds(18, 141, 144, 21);
		grbSerialSettings.add(cmbFlowcontrol);

		panSerialSettings.setLocation(grbSerialSettings.getLocation());
		panSerialSettings.setSize(grbSerialSettings.getSize());
		panSerialSettings.setDisabledColor(new Color(240, 240, 240, 100));
		panSerialSettings.setEnabled(false);
		grbListenerSettings.add(panSerialSettings);

		lblLocalPort = new JLabel("local port (default 502)");
		lblLocalPort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLocalPort.setBounds(15, 28, 117, 14);
		grbListenerSettings.add(lblLocalPort);

		lblSerialPort = new JLabel("serial port");
		lblSerialPort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSerialPort.setBounds(15, 62, 117, 14);
		grbListenerSettings.add(lblSerialPort);

		lblBaudrate = new JLabel("baudrate");
		lblBaudrate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBaudrate.setBounds(15, 91, 117, 14);
		grbListenerSettings.add(lblBaudrate);

		lblParity = new JLabel("parity");
		lblParity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblParity.setBounds(15, 119, 117, 14);
		grbListenerSettings.add(lblParity);

		lblDataBits = new JLabel("data bits");
		lblDataBits.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDataBits.setBounds(15, 146, 117, 14);
		grbListenerSettings.add(lblDataBits);

		lblStopBits = new JLabel("stop bits");
		lblStopBits.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStopBits.setBounds(15, 175, 117, 14);
		grbListenerSettings.add(lblStopBits);

		lblFlowcontrol = new JLabel("flow control");
		lblFlowcontrol.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFlowcontrol.setBounds(15, 200, 117, 14);
		grbListenerSettings.add(lblFlowcontrol);

		JButton btnAccept = new JButton(ResourceBundle.getBundle(
				"example_app.resources.resources").getString("btnAccept_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAccept_actionPerformed(e);
			}
		});
		btnAccept.setIcon(new ImageIcon(ListenerSettings.class
				.getResource("/example_app/resources/hand_thumb_up.png")));
		btnAccept.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAccept.setToolTipText("start slave");
		btnAccept.setMargin(new Insets(0, 0, 0, 0));
		btnAccept.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAccept.setBounds(178, 324, 68, 68);
		grbAddress.add(btnAccept);

		JButton btnAbort = new JButton(ResourceBundle.getBundle(
				"example_app.resources.resources").getString("btnAbort_Text")); //$NON-NLS-1$ //$NON-NLS-2$
		btnAbort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAbort_actionPerformed(e);
			}
		});
		btnAbort.setIcon(new ImageIcon(ListenerSettings.class
				.getResource("/example_app/resources/hand_thumb_down.png")));
		btnAbort.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAbort.setToolTipText("start slave");
		btnAbort.setMargin(new Insets(0, 0, 0, 0));
		btnAbort.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAbort.setBounds(252, 324, 68, 68);
		grbAddress.add(btnAbort);

		WorkshopUiScaler.apply(this);
	}

	private void formWindowOpened(java.awt.event.WindowEvent evt) {

		cmbConnectionType
				.setModel(new DefaultComboBoxModel<eTypeOfCommunication>(
						eTypeOfCommunication.values()));

		cmbBaudrate.setModel(new DefaultComboBoxModel<eBaudrate>(eBaudrate
				.values()));
		cmbBaudrate.setSelectedItem(eBaudrate.b9600);

		cmbParity.setModel(new DefaultComboBoxModel<eParity>(eParity.values()));
		cmbParity.setSelectedItem(eParity.None);

		cmbDataBits.setModel(new DefaultComboBoxModel<eDataBits>(eDataBits
				.values()));
		cmbDataBits.setSelectedItem(eDataBits.DataBits8);

		cmbFlowcontrol.setModel(new DefaultComboBoxModel<eFlowControl>(
				eFlowControl.values()));
		cmbFlowcontrol.setSelectedItem(eFlowControl.None);

		cmbStopbits.setModel(new DefaultComboBoxModel<eStopBits>(eStopBits
				.values()));
		cmbStopbits.setSelectedItem(eStopBits.One);

		String[] allSerialPorts = SerialPortList.getPortNames();

		if (allSerialPorts != null && allSerialPorts.length > 0) {
			cmbSerialPort.setModel(new DefaultComboBoxModel<String>(
					allSerialPorts));
		} else {
			cmbSerialPort.removeAll();
			cmbSerialPort.setSelectedItem("No serial ports detected");
		}
	}

	void cmbConnectionType_actionPerformed(ActionEvent arg) {
		try {
			eTypeOfCommunication selectedCommunication = eTypeOfCommunication.valueOf(this.cmbConnectionType
					.getSelectedItem().toString());
			switch (selectedCommunication) {

			case TCP:
			case UDP:
			case RTU_over_TCP:
			case SecureTCP:
				panIPSettings.setEnabled(true);
				panSerialSettings.setEnabled(false);
				if (selectedCommunication == eTypeOfCommunication.SecureTCP && "502".equals(txtLocalPort.getText())) {
					txtLocalPort.setText("802");
				} else if (selectedCommunication != eTypeOfCommunication.SecureTCP
						&& "802".equals(txtLocalPort.getText())) {
					txtLocalPort.setText("502");
				}
				break;
			case RTU:
			case ASCII:
				panIPSettings.setEnabled(false);
				panSerialSettings.setEnabled(true);
				break;
			default:
				panIPSettings.setEnabled(false);
				panSerialSettings.setEnabled(false);
				break;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					resources.getString("undefinend_Connectiontype"), "",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	void btnAccept_actionPerformed(ActionEvent e) {
		try {
			Args.put("TypeOfCommunication",
					(eTypeOfCommunication) cmbConnectionType.getSelectedItem());
			Args.put("ListerName", txtListenerName.getText());

			switch (eTypeOfCommunication.valueOf(this.cmbConnectionType
					.getSelectedItem().toString())) {
			case TCP:
			case UDP:
			case RTU_over_TCP:
			case SecureTCP:
				Args.put("LocalPort", Integer.parseInt(txtLocalPort.getText()));
				break;
			case RTU:
			case ASCII:
				Args.put("SerialPortName", cmbSerialPort.getSelectedItem());
				Args.put("Baudrate", (eBaudrate) cmbBaudrate.getSelectedItem());
				Args.put("DataBits", (eDataBits) cmbDataBits.getSelectedItem());
				Args.put("Parity", (eParity) cmbParity.getSelectedItem());
				Args.put("StopBits", (eStopBits) cmbStopbits.getSelectedItem());
				Args.put("FlowControl",
						(eFlowControl) cmbFlowcontrol.getSelectedItem());
				break;
			default:
				Args.clear();
				break;
			}
		} catch (Exception ex) {
			Args.clear();
		}
		this.setVisible(false);
		this.dispose() ;
	}

	void btnAbort_actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
	
	 HashMap<String, Object> CreateListener( int ListenerNumber) {
		txtListenerName.setText("Listener_"
				+ String.format(String.valueOf(ListenerNumber + 1), "00"));
		Args.clear();
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		

		return Args;
		
	}
}
