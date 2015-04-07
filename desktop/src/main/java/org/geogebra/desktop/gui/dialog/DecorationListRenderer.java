package org.geogebra.desktop.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * @author Le Coq Loic 30/10/2006 This class defines the renderer for the
 *         ComboBox where the user chooses the decoration for GeoSegment
 * 
 */
public class DecorationListRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;
	int id = 0;

	public DecorationListRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// Get the selected index. (The index param isn't
		// always valid, so just use the value.)
		int selectedIndex = ((Integer) value).intValue();
		this.id = selectedIndex;
		if (isSelected) {
			// Michael Borcherds 20071006 start
			// setBackground(list.getSelectionBackground());
			setBackground(Color.LIGHT_GRAY);
			// Michael Borcherds 20071006 end
			// setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			// setForeground(list.getForeground());
		}

		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		return this;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// Michael Borcherds 20071006 start
		// g.setColor(getBackground());
		if (getBackground() == Color.LIGHT_GRAY)
			g.setColor(Color.LIGHT_GRAY);
		else
			g.setColor(Color.WHITE);
		// Michael Borcherds 20071006 end
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		int mid = getHeight() / 2;
		g.drawLine(0, mid, getWidth(), mid);

		switch (id) {
		case GeoElement.DECORATION_NONE:
			break;
		case GeoElement.DECORATION_SEGMENT_ONE_TICK:
			int quart = mid / 2;
			int mid_width = getWidth() / 2;
			g.drawLine(mid_width, quart, mid_width, mid + quart);
			break;
		case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
			quart = mid / 2;
			mid_width = getWidth() / 2;
			g.drawLine(mid_width - 1, quart, mid_width - 1, mid + quart);
			g.drawLine(mid_width + 2, quart, mid_width + 2, mid + quart);
			break;
		case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
			quart = mid / 2;
			mid_width = getWidth() / 2;
			g.drawLine(mid_width, quart, mid_width, mid + quart);
			g.drawLine(mid_width + 3, quart, mid_width + 3, mid + quart);
			g.drawLine(mid_width - 3, quart, mid_width - 3, mid + quart);
			break;
		// Michael Borcherds 20071006 start
		case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
			quart = mid / 2;
			mid_width = getWidth() / 2;
			g.drawLine(mid_width, mid, mid_width - quart, mid - quart);
			g.drawLine(mid_width, mid, mid_width - quart, mid + quart);
			break;
		case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
			quart = mid / 2;
			mid_width = getWidth() / 2;
			g.drawLine(mid_width - 3, mid, mid_width - quart - 3, mid - quart);
			g.drawLine(mid_width - 3, mid, mid_width - quart - 3, mid + quart);
			g.drawLine(mid_width + 3, mid, mid_width - quart + 3, mid - quart);
			g.drawLine(mid_width + 3, mid, mid_width - quart + 3, mid + quart);
			break;
		case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
			quart = mid / 2;
			mid_width = getWidth() / 2;
			g.drawLine(mid_width, mid, mid_width - quart, mid - quart);
			g.drawLine(mid_width, mid, mid_width - quart, mid + quart);
			g.drawLine(mid_width + 6, mid, mid_width - quart + 6, mid - quart);
			g.drawLine(mid_width + 6, mid, mid_width - quart + 6, mid + quart);
			g.drawLine(mid_width - 6, mid, mid_width - quart - 6, mid - quart);
			g.drawLine(mid_width - 6, mid, mid_width - quart - 6, mid + quart);
			break;
		// Michael Borcherds 20071006 end
		}
	}
}
