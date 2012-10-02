package geogebra.gui.util;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * List or ComboBox renderer that supports a separator element.
 * 
 * @author G. Sturr
 *
 */
//============================================================
// ComboBox Renderer with SEPARATOR
// ============================================================

public class ListSeparatorRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;

	public static final String SEPARATOR = "---";
	JSeparator separator;

	public ListSeparatorRenderer() {
		setOpaque(true);
		setBorder(new EmptyBorder(1, 1, 1, 1));
		separator = new JSeparator(SwingConstants.HORIZONTAL);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String str = (value == null) ? "" : value.toString();
		if (SEPARATOR.equals(str)) {
			return separator;
		}
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setFont(list.getFont());
		setText(str);
		return this;
	}
}