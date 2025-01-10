package org.geogebra.desktop.gui.autocompletion;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * 
 * Cell renderer for {@link CompletionsPopup}
 * 
 * @author Arnaud Delobelle
 */
public class CommandCompletionListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String cmd = (String) value;
		JLabel label = (JLabel) super.getListCellRendererComponent(list, cmd,
				index, isSelected, cellHasFocus);
		return label;
	}

}
