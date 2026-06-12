package example_app;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.indian.plccom.modbus.SecureModbusPkiStore;
import com.indian.plccom.modbus.SecureModbusRoleExtension;

final class CertificateManagerDialog extends JDialog {
	private static final long serialVersionUID = 6429678105744821283L;

	private static final String STORE_OWN = "Own";
	private static final String STORE_TRUSTED = "Trusted";
	private static final String STORE_ISSUERS = "Issuers";
	private static final String STORE_REJECTED = "Rejected";

	private static final Color HEADER_BACKGROUND = new Color(245, 248, 252);
	private static final Color FOOTER_BACKGROUND = new Color(248, 248, 248);
	private static final Color TRUSTED_BACKGROUND = new Color(240, 250, 240);
	private static final Color REJECTED_BACKGROUND = new Color(255, 238, 238);
	private static final Color WARNING_BACKGROUND = new Color(255, 248, 220);
	private static final Color ERROR_BACKGROUND = new Color(255, 228, 225);
	private static final Dimension FOOTER_BUTTON_SIZE = new Dimension(68, 68);

	private final ResourceBundle resources = ResourceBundle.getBundle("example_app.resources.resources");
	private final SecureModbusPkiStore pkiStore;
	private final ArrayList<CertificateEntry> entries = new ArrayList<CertificateEntry>();
	private final DefaultTableModel model;
	private final JTable table;
	private final JTextArea details;
	private JLabel status;
	private final ImageIcon ownIcon16 = createOwnCertificateIcon(16);
	private final ImageIcon trustedIcon16 = createTrustIcon(16);
	private final ImageIcon issuerIcon16 = createIssuerIcon(16);
	private final ImageIcon rejectedIcon16 = createRejectIcon(16);
	private final ImageIcon errorIcon16 = createErrorIcon(16);
	private JButton buttonTrust;
	private JButton buttonIssuer;
	private JButton buttonReject;
	private JButton buttonDelete;
	private JMenuItem menuTrust;
	private JMenuItem menuIssuer;
	private JMenuItem menuReject;
	private JMenuItem menuDelete;
	private JMenuItem menuOpenFolder;
	private JMenuItem menuCopyThumbprint;

	static void showDialog(Frame owner) {
		try {
			CertificateManagerDialog dialog = new CertificateManagerDialog(owner);
			dialog.setVisible(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(owner, ex.getClass().getName() + " " + ex.getMessage(),
					"SecureTCP Certificate Manager", JOptionPane.ERROR_MESSAGE);
		}
	}

	static int countRejectedCertificates() {
		try {
			SecureModbusPkiStore store = SecureTcpExampleOptions.createPkiStore();
			return countCertificateFiles(new File(store.getRejectedCertificatesPath()));
		} catch (Exception ex) {
			return 0;
		}
	}

	static ImageIcon createShieldIcon(int size) {
		return new ImageIcon(createShieldImage(size));
	}

	private CertificateManagerDialog(Frame owner) {
		super(owner, textStatic("CertificateManagerForm_Text", "SecureTCP Certificate Manager"), true);
		this.pkiStore = SecureTcpExampleOptions.createPkiStore();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setIconImage(createShieldImage(16));

		JPanel root = new JPanel(new BorderLayout(0, 0));
		root.setPreferredSize(new Dimension(1060, 650));
		root.add(createHeaderPanel(), BorderLayout.NORTH);

		model = new DefaultTableModel(new String[] { text("CertificateManager_ColumnStore_Text", "Store"),
				text("CertificateManager_ColumnStatus_Text", "Status"), text("CertificateManager_ColumnFile_Text", "File"),
				text("CertificateManager_ColumnSubject_Text", "Subject"),
				text("CertificateManager_ColumnIssuer_Text", "Issuer"),
				text("CertificateManager_ColumnValidTo_Text", "Valid to"),
				text("CertificateManager_ColumnRole_Text", "Role") }, 0) {
			private static final long serialVersionUID = -6122937724033936455L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = createCertificateTable();
		details = createDetailsArea();
		root.add(createContentPanel(), BorderLayout.CENTER);
		root.add(createFooterPanel(), BorderLayout.SOUTH);
		setContentPane(root);

		refresh();
		pack();
		WorkshopUiScaler.apply(this);
		setLocationRelativeTo(owner);
	}

	private JPanel createHeaderPanel() {
		JPanel header = new JPanel(new BorderLayout(14, 0));
		header.setBackground(HEADER_BACKGROUND);
		header.setBorder(BorderFactory.createEmptyBorder(16, 24, 14, 24));

		JLabel icon = new JLabel(createShieldIcon(40));
		icon.setPreferredSize(new Dimension(48, 48));
		header.add(icon, BorderLayout.WEST);

		JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 2));
		textPanel.setOpaque(false);

		JLabel title = new JLabel(text("CertificateManager_Title_Text", "SecureTCP PKI store"));
		title.setFont(title.getFont().deriveFont(Font.BOLD));
		textPanel.add(title);

		JLabel pkiPath = new JLabel(pkiStore.getBasePath());
		textPanel.add(pkiPath);

		status = new JLabel(text("CertificateManager_Loading_Text", "Loading certificates..."));
		textPanel.add(status);

		header.add(textPanel, BorderLayout.CENTER);
		return header;
	}

	private JTable createCertificateTable() {
		JTable certificateTable = new JTable(model);
		certificateTable.setFillsViewportHeight(true);
		certificateTable.setGridColor(new Color(230, 230, 230));
		certificateTable.setIntercellSpacing(new Dimension(0, 1));
		certificateTable.setRowHeight(24);
		certificateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		certificateTable.setShowHorizontalLines(false);
		certificateTable.setShowVerticalLines(false);
		certificateTable.setDefaultRenderer(Object.class, new CertificateTableCellRenderer());
		certificateTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					updateDetails();
				}
			}
		});
		certificateTable.setComponentPopupMenu(createPopupMenu());
		configureColumns(certificateTable);
		return certificateTable;
	}

	private JPanel createContentPanel() {
		JPanel content = new JPanel(new BorderLayout(0, 12));
		content.setBorder(BorderFactory.createEmptyBorder(18, 24, 16, 24));

		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(1010, 330));
		content.add(tableScrollPane, BorderLayout.CENTER);

		JPanel detailsPanel = new JPanel(new BorderLayout());
		detailsPanel.setBorder(BorderFactory.createTitledBorder(text("CertificateManager_DetailsGroup_Text",
				"Certificate details")));
		JScrollPane detailsScrollPane = new JScrollPane(details);
		detailsScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));
		detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
		detailsPanel.setPreferredSize(new Dimension(1010, 112));
		content.add(detailsPanel, BorderLayout.SOUTH);

		return content;
	}

	private JTextArea createDetailsArea() {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(true);
		area.setMargin(new Insets(6, 8, 6, 8));
		area.setWrapStyleWord(true);
		return area;
	}

	private JPanel createFooterPanel() {
		JPanel footer = new JPanel(new BorderLayout(0, 0));
		footer.setBackground(FOOTER_BACKGROUND);
		footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
		footer.setPreferredSize(new Dimension(1060, 88));

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
		left.setOpaque(false);
		left.add(createFooterButton(text("CertificateManager_ButtonOwn_Text", "Own"), createOwnCertificateIcon(32),
				text("CertificateManager_ButtonOwn_ToolTip", "Create or load the own SecureTCP certificate."),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						ensureOwnCertificate();
					}
				}));
		left.add(createFooterButton(text("CertificateManager_ButtonOpenPki_Text", "PKI"), createOpenPkiIcon(32),
				text("CertificateManager_ButtonOpenPki_ToolTip", "Open the SecureTCP PKI folder."), new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						openPkiFolder();
					}
				}));
		left.add(createFooterButton(text("CertificateManager_ButtonRefresh_Text", "Refresh"), createRefreshIcon(32),
				text("CertificateManager_ButtonRefresh_ToolTip", "Refresh the certificate list."), new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						refresh();
					}
				}));

		JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 10));
		center.setOpaque(false);
		buttonTrust = createFooterButton(text("CertificateManager_ButtonTrust_Text", "Trust"), createTrustIcon(32),
				text("CertificateManager_ButtonTrust_ToolTip", "Move the selected rejected certificate to trusted."),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						moveSelectedTo(STORE_TRUSTED);
					}
				});
		buttonIssuer = createFooterButton(text("CertificateManager_ButtonIssuer_Text", "Issuer"), createIssuerIcon(32),
				text("CertificateManager_ButtonIssuer_ToolTip", "Move the selected certificate to issuers."),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						moveSelectedTo(STORE_ISSUERS);
					}
				});
		buttonReject = createFooterButton(text("CertificateManager_ButtonReject_Text", "Reject"), createRejectIcon(32),
				text("CertificateManager_ButtonReject_ToolTip", "Move the selected certificate to rejected."),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						moveSelectedTo(STORE_REJECTED);
					}
				});
		buttonDelete = createFooterButton(text("CertificateManager_ButtonDelete_Text", "Delete"), createDeleteIcon(32),
				text("CertificateManager_ButtonDelete_ToolTip", "Delete the selected certificate file."),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						deleteSelected();
					}
				});
		center.add(buttonTrust);
		center.add(buttonIssuer);
		center.add(buttonReject);
		center.add(buttonDelete);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
		right.setOpaque(false);
		right.add(createFooterButton(text("CertificateManager_ButtonClose_Text", "Close"), createCloseIcon(32),
				text("CertificateManager_ButtonClose_ToolTip", "Close this window."), new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						dispose();
					}
				}));

		footer.add(left, BorderLayout.WEST);
		footer.add(center, BorderLayout.CENTER);
		footer.add(right, BorderLayout.EAST);
		return footer;
	}

	private JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		menuTrust = createMenuItem(text("CertificateManager_MenuTrust_Text", "Trust certificate"), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				moveSelectedTo(STORE_TRUSTED);
			}
		});
		menuIssuer = createMenuItem(text("CertificateManager_MenuTrustIssuer_Text", "Trust as issuer"),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						moveSelectedTo(STORE_ISSUERS);
					}
				});
		menuReject = createMenuItem(text("CertificateManager_MenuReject_Text", "Reject certificate"),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						moveSelectedTo(STORE_REJECTED);
					}
				});
		menuCopyThumbprint = createMenuItem(text("CertificateManager_MenuCopyThumbprint_Text", "Copy thumbprint"),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						copySelectedThumbprint();
					}
				});
		menuOpenFolder = createMenuItem(text("CertificateManager_MenuOpenFolder_Text", "Open containing folder"),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						openSelectedFolder();
					}
				});
		menuDelete = createMenuItem(text("CertificateManager_MenuDelete_Text", "Delete file"), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				deleteSelected();
			}
		});
		JMenuItem menuRefresh = createMenuItem(text("CertificateManager_MenuRefresh_Text", "Refresh"),
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						refresh();
					}
				});

		menu.add(menuTrust);
		menu.add(menuIssuer);
		menu.add(menuReject);
		menu.addSeparator();
		menu.add(menuCopyThumbprint);
		menu.add(menuOpenFolder);
		menu.addSeparator();
		menu.add(menuDelete);
		menu.add(menuRefresh);
		menu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
				updateActionState();
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent event) {
			}
		});
		return menu;
	}

	private JButton createFooterButton(String text, ImageIcon icon, String toolTip, ActionListener listener) {
		JButton button = new JButton(text);
		button.setIcon(icon);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setMaximumSize(FOOTER_BUTTON_SIZE);
		button.setMinimumSize(FOOTER_BUTTON_SIZE);
		button.setPreferredSize(FOOTER_BUTTON_SIZE);
		button.setToolTipText(toolTip);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.addActionListener(listener);
		return button;
	}

	private JMenuItem createMenuItem(String text, ActionListener listener) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(listener);
		return item;
	}

	private void configureColumns(JTable certificateTable) {
		TableColumnModel columns = certificateTable.getColumnModel();
		int[] widths = new int[] { 120, 90, 190, 260, 240, 110, 90 };
		for (int i = 0; i < widths.length; i++) {
			columns.getColumn(i).setPreferredWidth(widths[i]);
		}
	}

	private void refresh() {
		entries.clear();
		pkiStore.ensureDirectories();
		addEntries(STORE_OWN, new File(pkiStore.getOwnCertificatesPath()));
		addEntries(STORE_TRUSTED, new File(pkiStore.getTrustedCertificatesPath()));
		addEntries(STORE_ISSUERS, new File(pkiStore.getIssuerCertificatesPath()));
		addEntries(STORE_REJECTED, new File(pkiStore.getRejectedCertificatesPath()));

		model.setRowCount(0);
		for (CertificateEntry entry : entries) {
			model.addRow(new Object[] { entry.storeName, statusText(entry), entry.file.getName(), subject(entry),
					issuer(entry), validTo(entry), roleName(entry) });
		}

		status.setText(MessageFormat.format(text("CertificateManager_StatusCounts_Text",
				"Own: {0}    Trusted: {1}    Issuers: {2}    Rejected: {3}"), countStore(STORE_OWN),
				countStore(STORE_TRUSTED), countStore(STORE_ISSUERS), countStore(STORE_REJECTED)));
		status.setForeground(countStore(STORE_REJECTED) > 0 ? Color.RED.darker() : new Color(30, 115, 45));
		updateDetails();
	}

	private void addEntries(String storeName, File directory) {
		File[] files = directory.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		Arrays.sort(files);
		for (File file : files) {
			if (!file.isFile() || !hasCertificateExtension(file)) {
				continue;
			}
			try {
				List<X509Certificate> certificates = loadCertificates(file);
				if (certificates.isEmpty()) {
					entries.add(CertificateEntry.error(storeName, file,
							text("CertificateManager_ErrorNoCertificate_Text", "The file does not contain a certificate.")));
					continue;
				}
				for (X509Certificate certificate : certificates) {
					entries.add(CertificateEntry.certificate(storeName, file, certificate));
				}
			} catch (Exception ex) {
				entries.add(CertificateEntry.error(storeName, file, ex.getMessage()));
			}
		}
	}

	private List<X509Certificate> loadCertificates(File file) throws Exception {
		ArrayList<String> passwords = new ArrayList<String>();
		passwords.add(null);
		addPassword(passwords, System.getenv("PLCCOM_MODBUS_SECURETCP_CERT_PASSWORD"));
		addPassword(passwords, System.getenv("PLCCOM_MODBUS_SECURETCP_TRUSTED_CERT_PASSWORD"));
		addPassword(passwords, System.getenv("PLCCOM_MODBUS_SECURETCP_OWN_PASSWORD"));

		Exception lastException = null;
		for (String password : passwords) {
			try {
				return SecureTcpExampleOptions.loadCertificates(file,
						password == null ? new char[0] : password.toCharArray());
			} catch (Exception ex) {
				lastException = ex;
			}
		}
		throw lastException;
	}

	private void addPassword(ArrayList<String> passwords, String password) {
		if (password != null && password.length() > 0 && !passwords.contains(password)) {
			passwords.add(password);
		}
	}

	private int countStore(String storeName) {
		int count = 0;
		for (CertificateEntry entry : entries) {
			if (storeName.equals(entry.storeName)) {
				count++;
			}
		}
		return count;
	}

	private void updateDetails() {
		updateActionState();
		CertificateEntry entry = getSelectedEntry();
		if (entry == null) {
			details.setText(text("CertificateManager_SelectCertificate_Text",
					"Select a certificate to see its details. Use the context menu to manage PKI files."));
			return;
		}
		details.setText(buildDetails(entry));
		details.setCaretPosition(0);
	}

	private void updateActionState() {
		CertificateEntry entry = getSelectedEntry();
		boolean hasCertificate = entry != null && entry.certificate != null;
		boolean canTrust = hasCertificate && STORE_REJECTED.equals(entry.storeName);
		boolean canReject = hasCertificate && !STORE_REJECTED.equals(entry.storeName) && !STORE_OWN.equals(entry.storeName);
		boolean canTrustIssuer = hasCertificate && !STORE_ISSUERS.equals(entry.storeName)
				&& !STORE_OWN.equals(entry.storeName);
		boolean canDelete = entry != null;

		if (buttonTrust != null) {
			buttonTrust.setEnabled(canTrust);
		}
		if (buttonIssuer != null) {
			buttonIssuer.setEnabled(canTrustIssuer);
		}
		if (buttonReject != null) {
			buttonReject.setEnabled(canReject);
		}
		if (buttonDelete != null) {
			buttonDelete.setEnabled(canDelete);
		}
		if (menuTrust != null) {
			menuTrust.setEnabled(canTrust);
			menuIssuer.setEnabled(canTrustIssuer);
			menuReject.setEnabled(canReject);
			menuDelete.setEnabled(canDelete);
			menuOpenFolder.setEnabled(canDelete);
			menuCopyThumbprint.setEnabled(hasCertificate);
		}
	}

	private CertificateEntry getSelectedEntry() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			return null;
		}
		int modelRow = table.convertRowIndexToModel(selectedRow);
		return modelRow >= 0 && modelRow < entries.size() ? entries.get(modelRow) : null;
	}

	private void ensureOwnCertificate() {
		try {
			X509Certificate certificate = SecureTcpExampleOptions.ensureOwnCertificate();
			refresh();
			JOptionPane.showMessageDialog(this,
					text("CertificateManager_OwnCertificateAvailable_Text", "Own SecureTCP certificate is available:")
							+ System.lineSeparator()
							+ (certificate == null ? text("CertificateManager_NotLoaded_Text", "<not loaded>")
									: certificate.getSubjectX500Principal().getName()),
					getTitle(), JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void moveSelectedTo(String targetStore) {
		CertificateEntry entry = getSelectedEntry();
		if (entry == null || entry.certificate == null) {
			JOptionPane.showMessageDialog(this,
					text("CertificateManager_SelectLoadedCertificate_Text", "Select a loaded certificate first."),
					getTitle(), JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			ensureInsidePki(entry.file);
			String targetPath;
			if (STORE_TRUSTED.equals(targetStore)) {
				targetPath = pkiStore.storeTrustedCertificate(entry.certificate);
			} else if (STORE_ISSUERS.equals(targetStore)) {
				targetPath = pkiStore.storeIssuerCertificate(entry.certificate);
			} else if (STORE_REJECTED.equals(targetStore)) {
				targetPath = pkiStore.storeRejectedCertificate(entry.certificate);
			} else {
				throw new IllegalArgumentException("Unsupported target store: " + targetStore);
			}

			File targetFile = new File(targetPath).getCanonicalFile();
			File sourceFile = entry.file.getCanonicalFile();
			boolean keepSourceFile = STORE_OWN.equals(entry.storeName);
			if (!keepSourceFile && !targetFile.equals(sourceFile) && !sourceFile.delete()) {
				throw new IllegalStateException("Certificate was copied, but the source file could not be deleted: "
						+ sourceFile.getAbsolutePath());
			}
			refresh();
			JOptionPane.showMessageDialog(this,
					MessageFormat.format(text("CertificateManager_CertificateMoved_Text", "Certificate moved to {0}."),
							storeText(targetStore)) + System.lineSeparator() + targetPath,
					getTitle(), JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void deleteSelected() {
		CertificateEntry entry = getSelectedEntry();
		if (entry == null) {
			return;
		}
		String message = text("CertificateManager_DeleteQuestion_Text", "Delete certificate file?") + System.lineSeparator()
				+ entry.file.getAbsolutePath();
		if (STORE_OWN.equals(entry.storeName)) {
			message += System.lineSeparator()
					+ text("CertificateManager_DeleteOwnPrivateKeyHint_Text",
							"The matching private key will also be deleted if present.");
		}
		if (JOptionPane.showConfirmDialog(this, message, getTitle(), JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			ensureInsidePki(entry.file);
			if (!entry.file.delete()) {
				throw new IllegalStateException("The certificate file could not be deleted.");
			}
			if (STORE_OWN.equals(entry.storeName)) {
				deleteMatchingOwnPrivateKeys(entry.file);
			}
			refresh();
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void deleteMatchingOwnPrivateKeys(File ownCertificateFile) throws Exception {
		String certificateStem = fileNameWithoutExtension(ownCertificateFile.getName());
		File privateDirectory = new File(pkiStore.getOwnPrivatePath());
		File[] files = privateDirectory.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (File file : files) {
			if (!file.isFile() || !hasPrivateKeyExtension(file)) {
				continue;
			}
			if (!certificateStem.equalsIgnoreCase(fileNameWithoutExtension(file.getName()))) {
				continue;
			}
			ensureInsidePki(file);
			if (!file.delete()) {
				throw new IllegalStateException("The matching private key could not be deleted: "
						+ file.getAbsolutePath());
			}
		}
	}

	private void copySelectedThumbprint() {
		CertificateEntry entry = getSelectedEntry();
		if (entry == null || entry.certificate == null) {
			return;
		}
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(thumbprint(entry.certificate, "SHA-256")), null);
	}

	private void openPkiFolder() {
		openFolder(new File(pkiStore.getBasePath()));
	}

	private void openSelectedFolder() {
		CertificateEntry entry = getSelectedEntry();
		if (entry != null) {
			openFolder(entry.file.getParentFile());
		}
	}

	private void openFolder(File folder) {
		try {
			if (folder == null || !folder.isDirectory()) {
				throw new IllegalStateException("The folder does not exist: " + String.valueOf(folder));
			}
			if (!Desktop.isDesktopSupported()) {
				throw new IllegalStateException("Desktop folder opening is not supported on this system.");
			}
			Desktop.getDesktop().open(folder);
		} catch (Exception ex) {
			showError(ex);
		}
	}

	private void ensureInsidePki(File file) throws Exception {
		File base = new File(pkiStore.getBasePath()).getCanonicalFile();
		File candidate = file.getCanonicalFile();
		String basePath = base.getPath();
		String candidatePath = candidate.getPath();
		if (!candidatePath.equals(basePath) && !candidatePath.startsWith(basePath + File.separator)) {
			throw new IllegalStateException(text("CertificateManager_ErrorOutsidePki_Text",
					"Refusing to modify a file outside the PKI store:") + " " + candidatePath);
		}
	}

	private void showError(Exception ex) {
		JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), getTitle(),
				JOptionPane.ERROR_MESSAGE);
	}

	private String buildDetails(CertificateEntry entry) {
		StringBuilder builder = new StringBuilder();
		builder.append(text("CertificateManager_DetailStore_Text", "Store:")).append(' ')
				.append(storeText(entry.storeName)).append(System.lineSeparator());
		builder.append(text("CertificateManager_DetailStatus_Text", "Status:")).append(' ')
				.append(statusText(entry)).append(System.lineSeparator());
		builder.append(text("CertificateManager_DetailFile_Text", "File:")).append(' ')
				.append(entry.file.getAbsolutePath()).append(System.lineSeparator());
		if (entry.certificate == null) {
			builder.append(text("CertificateManager_DetailLoadError_Text", "Load error:")).append(' ')
					.append(entry.loadError).append(System.lineSeparator());
			return builder.toString();
		}
		builder.append(text("CertificateManager_DetailSubject_Text", "Subject:")).append(' ')
				.append(subject(entry)).append(System.lineSeparator());
		builder.append(text("CertificateManager_DetailIssuer_Text", "Issuer:")).append(' ')
				.append(issuer(entry)).append(System.lineSeparator());
		builder.append(text("CertificateManager_DetailThumbprint_Text", "Thumbprint:")).append(' ')
				.append(thumbprint(entry.certificate, "SHA-1")).append(System.lineSeparator());
		builder.append("SHA-256: ").append(thumbprint(entry.certificate, "SHA-256")).append(System.lineSeparator());
		builder.append(text("CertificateManager_DetailSerial_Text", "Serial:")).append(' ')
				.append(entry.certificate.getSerialNumber()).append(System.lineSeparator());
		builder.append(text("CertificateManager_DetailValidFrom_Text", "Valid from:")).append(' ')
				.append(formatDate(entry.certificate.getNotBefore())).append(System.lineSeparator());
		builder.append(text("CertificateManager_DetailValidTo_Text", "Valid to:")).append(' ')
				.append(formatDate(entry.certificate.getNotAfter())).append(System.lineSeparator());
		builder.append(text("CertificateManager_DetailRole_Text", "Role:")).append(' ')
				.append(roleName(entry)).append(System.lineSeparator());
		return builder.toString();
	}

	private String statusText(CertificateEntry entry) {
		if (entry.certificate == null) {
			return text("CertificateManager_StatusLoadError_Text", "Load error");
		}
		Date now = new Date();
		if (now.before(entry.certificate.getNotBefore())) {
			return text("CertificateManager_StatusNotYetValid_Text", "Not yet valid");
		}
		if (now.after(entry.certificate.getNotAfter())) {
			return text("CertificateManager_StatusExpired_Text", "Expired");
		}
		return text("CertificateManager_StatusValid_Text", "Valid");
	}

	private String storeText(String storeName) {
		if (STORE_OWN.equals(storeName)) {
			return text("CertificateManager_StoreOwn_Text", "Own");
		}
		if (STORE_TRUSTED.equals(storeName)) {
			return text("CertificateManager_StoreTrusted_Text", "Trusted");
		}
		if (STORE_ISSUERS.equals(storeName)) {
			return text("CertificateManager_StoreIssuers_Text", "Issuers");
		}
		if (STORE_REJECTED.equals(storeName)) {
			return text("CertificateManager_StoreRejected_Text", "Rejected");
		}
		return storeName;
	}

	private String subject(CertificateEntry entry) {
		return entry.certificate == null ? text("CertificateManager_NotLoaded_Text", "<not loaded>")
				: entry.certificate.getSubjectX500Principal().getName();
	}

	private String issuer(CertificateEntry entry) {
		return entry.certificate == null ? text("CertificateManager_NotLoaded_Text", "<not loaded>")
				: entry.certificate.getIssuerX500Principal().getName();
	}

	private String validTo(CertificateEntry entry) {
		return entry.certificate == null ? "" : formatTableDate(entry.certificate.getNotAfter());
	}

	private String roleName(CertificateEntry entry) {
		if (entry.certificate == null) {
			return "";
		}
		String roleName = SecureModbusRoleExtension.getRoleName(entry.certificate);
		return roleName == null || roleName.length() == 0 ? text("CertificateManager_None_Text", "<none>") : roleName;
	}

	private String text(String key, String fallback) {
		try {
			String value = resources.getString(key);
			return value == null || value.length() == 0 ? fallback : value;
		} catch (Exception ex) {
			return fallback;
		}
	}

	private static String textStatic(String key, String fallback) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("example_app.resources.resources");
			String value = bundle.getString(key);
			return value == null || value.length() == 0 ? fallback : value;
		} catch (Exception ex) {
			return fallback;
		}
	}

	private static int countCertificateFiles(File directory) {
		File[] files = directory.listFiles();
		if (files == null) {
			return 0;
		}
		int count = 0;
		for (File file : files) {
			if (file.isFile() && hasCertificateExtension(file)) {
				count++;
			}
		}
		return count;
	}

	private static boolean hasCertificateExtension(File file) {
		String name = file.getName().toLowerCase();
		return name.endsWith(".der") || name.endsWith(".cer") || name.endsWith(".cert") || name.endsWith(".crt")
				|| name.endsWith(".pem") || name.endsWith(".pfx") || name.endsWith(".p12")
				|| name.endsWith(".pkcs12");
	}

	private static boolean hasPrivateKeyExtension(File file) {
		String name = file.getName().toLowerCase();
		return name.endsWith(".pem") || name.endsWith(".key") || name.endsWith(".der") || name.endsWith(".pfx")
				|| name.endsWith(".p12") || name.endsWith(".pkcs12");
	}

	private static String fileNameWithoutExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
	}

	private static String formatDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	private static String formatTableDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	private static String thumbprint(X509Certificate certificate, String algorithm) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			byte[] hash = digest.digest(certificate.getEncoded());
			StringBuilder builder = new StringBuilder(hash.length * 2);
			for (byte value : hash) {
				int unsigned = value & 0xff;
				if (unsigned < 16) {
					builder.append('0');
				}
				builder.append(Integer.toHexString(unsigned).toUpperCase());
			}
			return builder.toString();
		} catch (Exception ex) {
			return "<not available>";
		}
	}

	private ImageIcon storeIcon(CertificateEntry entry) {
		if (entry == null) {
			return null;
		}
		if (entry.certificate == null) {
			return errorIcon16;
		}
		if (STORE_OWN.equals(entry.storeName)) {
			return ownIcon16;
		}
		if (STORE_TRUSTED.equals(entry.storeName)) {
			return trustedIcon16;
		}
		if (STORE_ISSUERS.equals(entry.storeName)) {
			return issuerIcon16;
		}
		if (STORE_REJECTED.equals(entry.storeName)) {
			return rejectedIcon16;
		}
		return null;
	}

	private static Image createShieldImage(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(new Color(40, 125, 210));
		graphics.fillPolygon(new int[] { 16, 27, 25, 16, 7, 5 }, new int[] { 3, 8, 20, 29, 20, 8 }, 6);
		graphics.setColor(new Color(235, 60, 60));
		graphics.fillPolygon(new int[] { 16, 7, 7, 16 }, new int[] { 5, 9, 19, 27 }, 4);
		graphics.setColor(new Color(245, 204, 48));
		graphics.fillPolygon(new int[] { 16, 25, 25, 16 }, new int[] { 5, 9, 19, 27 }, 4);
		graphics.setColor(new Color(235, 245, 255, 190));
		graphics.fillPolygon(new int[] { 16, 16, 7, 7 }, new int[] { 5, 27, 19, 9 }, 4);
		graphics.setColor(new Color(115, 135, 150));
		graphics.setStroke(new BasicStroke(1.3f));
		graphics.drawPolygon(new int[] { 16, 27, 25, 16, 7, 5 }, new int[] { 3, 8, 20, 29, 20, 8 }, 6);
		graphics.dispose();
		return image;
	}

	private static ImageIcon createOwnCertificateIcon(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(new Color(255, 248, 220));
		graphics.fillRect(6, 3, 17, 23);
		graphics.setColor(new Color(148, 126, 70));
		graphics.drawRect(6, 3, 17, 23);
		graphics.setColor(new Color(64, 132, 206));
		graphics.fillOval(17, 17, 8, 8);
		graphics.setColor(new Color(88, 88, 88));
		graphics.setStroke(new BasicStroke(2.0f));
		graphics.drawOval(7, 20, 6, 6);
		graphics.drawLine(13, 23, 25, 23);
		graphics.drawLine(21, 23, 21, 27);
		graphics.dispose();
		return new ImageIcon(image);
	}

	private static ImageIcon createOpenPkiIcon(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(new Color(245, 196, 72));
		graphics.fillRect(4, 8, 10, 5);
		graphics.setColor(new Color(255, 214, 93));
		graphics.fillRect(4, 12, 24, 14);
		graphics.setColor(new Color(163, 125, 42));
		graphics.drawRect(4, 8, 10, 5);
		graphics.drawRect(4, 12, 24, 14);
		graphics.dispose();
		return new ImageIcon(image);
	}

	private static ImageIcon createRefreshIcon(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(new Color(44, 112, 176));
		graphics.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.drawArc(6, 6, 20, 20, 35, 260);
		graphics.fillPolygon(new int[] { 24, 29, 26 }, new int[] { 7, 8, 13 }, 3);
		graphics.dispose();
		return new ImageIcon(image);
	}

	private static ImageIcon createTrustIcon(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(new Color(47, 150, 82));
		graphics.fillPolygon(new int[] { 16, 27, 25, 16, 7, 5 }, new int[] { 3, 8, 20, 29, 20, 8 }, 6);
		graphics.setColor(Color.WHITE);
		graphics.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.drawPolyline(new int[] { 9, 14, 24 }, new int[] { 17, 22, 11 }, 3);
		graphics.dispose();
		return new ImageIcon(image);
	}

	private static ImageIcon createIssuerIcon(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(Color.WHITE);
		graphics.fillRect(7, 4, 18, 23);
		graphics.setColor(new Color(78, 126, 176));
		graphics.drawRect(7, 4, 18, 23);
		graphics.setColor(new Color(145, 160, 178));
		graphics.drawLine(11, 10, 21, 10);
		graphics.drawLine(11, 14, 21, 14);
		graphics.setColor(new Color(64, 132, 206));
		graphics.fillOval(12, 17, 8, 8);
		graphics.dispose();
		return new ImageIcon(image);
	}

	private static ImageIcon createRejectIcon(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(new Color(198, 57, 57));
		graphics.fillOval(5, 5, 22, 22);
		graphics.setColor(Color.WHITE);
		graphics.setStroke(new BasicStroke(3.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.drawLine(10, 16, 22, 16);
		graphics.dispose();
		return new ImageIcon(image);
	}

	private static ImageIcon createDeleteIcon(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(new Color(118, 126, 135));
		graphics.fillRect(10, 12, 13, 15);
		graphics.setColor(new Color(80, 88, 96));
		graphics.drawRect(10, 12, 13, 15);
		graphics.drawLine(8, 10, 25, 10);
		graphics.drawLine(13, 7, 20, 7);
		graphics.setColor(new Color(205, 58, 58));
		graphics.setStroke(new BasicStroke(2.2f));
		graphics.drawLine(13, 16, 20, 23);
		graphics.drawLine(20, 16, 13, 23);
		graphics.dispose();
		return new ImageIcon(image);
	}

	private static ImageIcon createCloseIcon(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(new Color(130, 145, 155));
		graphics.fillRect(10, 4, 10, 23);
		graphics.setColor(new Color(70, 80, 88));
		graphics.drawRect(10, 4, 10, 23);
		graphics.setColor(new Color(48, 145, 70));
		graphics.fillPolygon(new int[] { 17, 28, 22, 22 }, new int[] { 16, 8, 14, 18 }, 4);
		graphics.fillRect(17, 13, 7, 6);
		graphics.dispose();
		return new ImageIcon(image);
	}

	private static ImageIcon createErrorIcon(int size) {
		BufferedImage image = createIconImage(size);
		Graphics2D graphics = prepare(image);
		double scale = size / 32.0;
		graphics.scale(scale, scale);
		graphics.setColor(new Color(198, 57, 57));
		graphics.fillOval(5, 5, 22, 22);
		graphics.setColor(Color.WHITE);
		graphics.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.drawLine(11, 11, 21, 21);
		graphics.drawLine(21, 11, 11, 21);
		graphics.dispose();
		return new ImageIcon(image);
	}

	private static BufferedImage createIconImage(int size) {
		return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	}

	private static Graphics2D prepare(BufferedImage image) {
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setBackground(new Color(0, 0, 0, 0));
		graphics.clearRect(0, 0, image.getWidth(), image.getHeight());
		return graphics;
	}

	private final class CertificateTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 2844933877877235682L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			CertificateEntry entry = null;
			int modelRow = table.convertRowIndexToModel(row);
			if (modelRow >= 0 && modelRow < entries.size()) {
				entry = entries.get(modelRow);
			}

			setHorizontalAlignment(SwingConstants.LEFT);
			if (column == 0) {
				setIcon(storeIcon(entry));
				setText(entry == null ? String.valueOf(value) : storeText(entry.storeName));
				setIconTextGap(6);
			} else {
				setIcon(null);
			}

			if (!isSelected) {
				component.setForeground(table.getForeground());
				component.setBackground(Color.WHITE);
				if (entry != null) {
					if (entry.certificate == null) {
						component.setForeground(Color.RED.darker());
						component.setBackground(ERROR_BACKGROUND);
					} else if (!isDateValid(entry)) {
						component.setForeground(Color.ORANGE.darker());
						component.setBackground(WARNING_BACKGROUND);
					} else if (STORE_REJECTED.equals(entry.storeName)) {
						component.setBackground(REJECTED_BACKGROUND);
					} else if (STORE_TRUSTED.equals(entry.storeName) || STORE_ISSUERS.equals(entry.storeName)) {
						component.setBackground(TRUSTED_BACKGROUND);
					}
				}
			}
			return component;
		}
	}

	private static boolean isDateValid(CertificateEntry entry) {
		if (entry.certificate == null) {
			return false;
		}
		Date now = new Date();
		return !now.before(entry.certificate.getNotBefore()) && !now.after(entry.certificate.getNotAfter());
	}

	private static final class CertificateEntry {
		final String storeName;
		final File file;
		final X509Certificate certificate;
		final String loadError;

		private CertificateEntry(String storeName, File file, X509Certificate certificate, String loadError) {
			this.storeName = storeName;
			this.file = file;
			this.certificate = certificate;
			this.loadError = loadError;
		}

		static CertificateEntry certificate(String storeName, File file, X509Certificate certificate) {
			return new CertificateEntry(storeName, file, certificate, null);
		}

		static CertificateEntry error(String storeName, File file, String loadError) {
			return new CertificateEntry(storeName, file, null, loadError == null ? "Load error" : loadError);
		}
	}
}
