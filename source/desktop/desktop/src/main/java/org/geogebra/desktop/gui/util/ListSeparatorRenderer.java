/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.util;

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
// ============================================================
// ComboBox Renderer with SEPARATOR
// ============================================================

public class ListSeparatorRenderer extends JLabel
		implements ListCellRenderer<String> {

	private static final long serialVersionUID = 1L;

	public static final String SEPARATOR = "<------->";
	JSeparator separator;

	/**
	 * Creates the renderer
	 */
	public ListSeparatorRenderer() {
		setOpaque(true);
		setBorder(new EmptyBorder(1, 1, 1, 1));
		separator = new JSeparator(SwingConstants.HORIZONTAL);
	}

	@Override
	public Component getListCellRendererComponent(JList list, String value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String str = (value == null) ? "" : value;
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