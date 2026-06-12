package example_app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.UIManager;

final class WorkshopUi {
	private static final int CHECK_BOX_ICON_SIZE = 18;

	private WorkshopUi() {
	}

	static void installScaledCheckBoxIcon() {
		UIManager.put("CheckBox.icon", new ScaledCheckBoxIcon(CHECK_BOX_ICON_SIZE));
	}

	private static final class ScaledCheckBoxIcon implements Icon {
		private final int size;

		ScaledCheckBoxIcon(int size) {
			this.size = size;
		}

		@Override
		public int getIconWidth() {
			return size;
		}

		@Override
		public int getIconHeight() {
			return size;
		}

		@Override
		public void paintIcon(Component component, Graphics graphics, int x, int y) {
			Graphics2D g = (Graphics2D) graphics.create();
			try {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				boolean enabled = component == null || component.isEnabled();
				boolean selected = component instanceof AbstractButton && ((AbstractButton) component).isSelected();
				g.setColor(enabled ? Color.WHITE : new Color(238, 238, 238));
				g.fillRect(x + 1, y + 1, size - 3, size - 3);
				g.setColor(enabled ? new Color(117, 132, 150) : new Color(170, 170, 170));
				g.drawRect(x + 1, y + 1, size - 3, size - 3);
				if (selected) {
					g.setColor(enabled ? new Color(51, 95, 145) : new Color(145, 145, 145));
					int baseX = x + 4;
					int baseY = y + size / 2;
					g.drawLine(baseX, baseY, baseX + 4, baseY + 4);
					g.drawLine(baseX + 1, baseY, baseX + 4, baseY + 3);
					g.drawLine(baseX + 4, baseY + 4, x + size - 4, y + 4);
					g.drawLine(baseX + 4, baseY + 3, x + size - 4, y + 5);
				}
			} finally {
				g.dispose();
			}
		}
	}
}
