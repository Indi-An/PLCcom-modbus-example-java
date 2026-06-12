package example_app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

final class WorkshopUiScaler {

	static final String SCALE_PROPERTY = "plccom.example.uiScale";

	private static final String APPLIED_PROPERTY = "plccom.example.uiScale.applied";
	private static final double DEFAULT_SCALE = 1.50d;
	private static final double MAX_SCALE = 1.60d;
	private static final double SCREEN_WIDTH_USAGE = 0.92d;
	private static final double SCREEN_HEIGHT_USAGE = 0.96d;

	private WorkshopUiScaler() {
	}

	static double apply(Window window) {
		if (window == null) {
			return 1.0d;
		}

		Container root = window instanceof javax.swing.RootPaneContainer
				? ((javax.swing.RootPaneContainer) window).getContentPane()
				: null;
		if (root instanceof JComponent) {
			JComponent component = (JComponent) root;
			if (Boolean.TRUE.equals(component.getClientProperty(APPLIED_PROPERTY))) {
				return 1.0d;
			}
			component.putClientProperty(APPLIED_PROPERTY, Boolean.TRUE);
		}

		double scale = resolveScale(window);
		if (scale <= 1.01d) {
			return 1.0d;
		}

		scaleWindow(window, scale);
		if (root != null) {
			scaleComponentTree(root, scale, false);
		}
		window.validate();
		window.repaint();
		return scale;
	}

	private static double resolveScale(Window window) {
		double scale = parseScale(System.getProperty(SCALE_PROPERTY));
		if (scale <= 0.0d) {
			scale = DEFAULT_SCALE;
		}
		return capToScreen(window, clamp(scale, 1.0d, MAX_SCALE));
	}

	private static double parseScale(String configuredValue) {
		if (configuredValue == null || configuredValue.trim().length() == 0) {
			return -1.0d;
		}

		String normalized = configuredValue.trim().toLowerCase();
		if ("auto".equals(normalized)) {
			return -1.0d;
		}
		if ("off".equals(normalized) || "false".equals(normalized) || "disabled".equals(normalized)
				|| "none".equals(normalized)) {
			return 1.0d;
		}

		try {
			return Double.parseDouble(normalized.replace(',', '.'));
		} catch (NumberFormatException ignore) {
			return 1.0d;
		}
	}

	private static double capToScreen(Window window, double scale) {
		Rectangle bounds = window.getBounds();
		if (bounds.width <= 0 || bounds.height <= 0) {
			return scale;
		}

		try {
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			double widthLimit = (screen.width * SCREEN_WIDTH_USAGE) / bounds.width;
			double heightLimit = (screen.height * SCREEN_HEIGHT_USAGE) / bounds.height;
			return clamp(Math.min(scale, Math.min(widthLimit, heightLimit)), 1.0d, MAX_SCALE);
		} catch (RuntimeException ignore) {
			return scale;
		}
	}

	private static void scaleWindow(Window window, double scale) {
		Rectangle bounds = window.getBounds();
		window.setBounds(bounds.x, bounds.y, scaled(bounds.width, scale), scaled(bounds.height, scale));
	}

	private static void scaleComponentTree(Component component, double scale, boolean scaleBounds) {
		if (scaleBounds) {
			Rectangle bounds = component.getBounds();
			component.setBounds(scaled(bounds.x, scale), scaled(bounds.y, scale), scaled(bounds.width, scale),
					scaled(bounds.height, scale));
		}

		Font originalFont = component.getFont();
		if (component instanceof JComponent) {
			JComponent swingComponent = (JComponent) component;
			scaleBorder(swingComponent, originalFont, scale);
			scaleExplicitSizes(swingComponent, scale);
		}
		if (originalFont != null) {
			component.setFont(scaleFont(originalFont, scale));
		}
		if (component instanceof AbstractButton) {
			scaleButtonIcons((AbstractButton) component, scale);
		}
		if (component instanceof JTable) {
			scaleTable((JTable) component, scale);
		}
		if (component instanceof Container) {
			Container container = (Container) component;
			boolean scaleChildBounds = container.getLayout() == null;
			Component[] children = container.getComponents();
			for (int i = 0; i < children.length; i++) {
				scaleComponentTree(children[i], scale, scaleChildBounds);
			}
		}
	}

	private static void scaleBorder(JComponent component, Font componentFont, double scale) {
		Border border = component.getBorder();
		if (border instanceof TitledBorder) {
			TitledBorder titledBorder = (TitledBorder) border;
			Font titleFont = titledBorder.getTitleFont();
			if (titleFont == null) {
				titleFont = componentFont;
			}
			if (titleFont != null) {
				titledBorder.setTitleFont(scaleFont(titleFont, scale));
			}
		}
	}

	private static void scaleExplicitSizes(Component component, double scale) {
		if (component.isPreferredSizeSet()) {
			component.setPreferredSize(scaleDimension(component.getPreferredSize(), scale));
		}
		if (component.isMinimumSizeSet()) {
			component.setMinimumSize(scaleDimension(component.getMinimumSize(), scale));
		}
		if (component.isMaximumSizeSet()) {
			component.setMaximumSize(scaleDimension(component.getMaximumSize(), scale));
		}
	}

	private static void scaleButtonIcons(AbstractButton button, double scale) {
		Icon icon = scaleIcon(button.getIcon(), scale);
		button.setIcon(icon);
		button.setDisabledIcon(createDisabledIcon(icon));
		button.setPressedIcon(scaleIcon(button.getPressedIcon(), scale));
		button.setRolloverIcon(scaleIcon(button.getRolloverIcon(), scale));
		Icon selectedIcon = scaleIcon(button.getSelectedIcon(), scale);
		button.setSelectedIcon(selectedIcon);
		button.setDisabledSelectedIcon(createDisabledIcon(selectedIcon));
		button.setRolloverSelectedIcon(scaleIcon(button.getRolloverSelectedIcon(), scale));
	}

	private static Icon scaleIcon(Icon icon, double scale) {
		if (!(icon instanceof ImageIcon) || icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
			return icon;
		}

		ImageIcon imageIcon = (ImageIcon) icon;
		Image scaledImage = imageIcon.getImage().getScaledInstance(scaled(icon.getIconWidth(), scale),
				scaled(icon.getIconHeight(), scale), Image.SCALE_SMOOTH);
		ImageIcon scaledIcon = new ImageIcon(scaledImage);
		scaledIcon.setDescription(imageIcon.getDescription());
		return scaledIcon;
	}

	private static Icon createDisabledIcon(Icon icon) {
		if (icon == null || icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
			return null;
		}

		Image image;
		if (icon instanceof ImageIcon) {
			image = ((ImageIcon) icon).getImage();
		} else {
			BufferedImage renderedIcon = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = renderedIcon.createGraphics();
			try {
				icon.paintIcon(null, graphics, 0, 0);
			} finally {
				graphics.dispose();
			}
			image = renderedIcon;
		}

		return new ImageIcon(GrayFilter.createDisabledImage(image));
	}

	private static void scaleTable(JTable table, double scale) {
		table.setRowHeight(scaled(table.getRowHeight(), scale));
		table.setIntercellSpacing(scaleDimension(table.getIntercellSpacing(), scale));

		TableColumnModel columns = table.getColumnModel();
		for (int i = 0; i < columns.getColumnCount(); i++) {
			TableColumn column = columns.getColumn(i);
			column.setPreferredWidth(scaled(column.getPreferredWidth(), scale));
			column.setMinWidth(scaled(column.getMinWidth(), scale));
			if (column.getMaxWidth() < Integer.MAX_VALUE) {
				column.setMaxWidth(scaled(column.getMaxWidth(), scale));
			}
		}
	}

	private static Font scaleFont(Font font, double scale) {
		return font.deriveFont((float) (font.getSize2D() * scale));
	}

	private static Dimension scaleDimension(Dimension size, double scale) {
		if (size == null) {
			return null;
		}
		return new Dimension(scaled(size.width, scale), scaled(size.height, scale));
	}

	private static int scaled(int value, double scale) {
		return Math.max(1, (int) Math.round(value * scale));
	}

	private static double clamp(double value, double minimum, double maximum) {
		return Math.max(minimum, Math.min(maximum, value));
	}
}
