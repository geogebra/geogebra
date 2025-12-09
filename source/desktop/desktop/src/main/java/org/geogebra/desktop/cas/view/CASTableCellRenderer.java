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

package org.geogebra.desktop.cas.view;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.desktop.awt.GColorD;

/**
 * CAS cell renderer
 */
public class CASTableCellRenderer extends CASTableCell
		implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * @param view
	 *            CAS view
	 */
	CASTableCellRenderer(CASViewD view) {
		super(view);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Font casFont = view.getCASViewComponent().getFont();
		if (value instanceof GeoCasCell) {
			GeoCasCell cell = (GeoCasCell) value;
			// set CASTableCell value
			setValue(cell);

			// update font and row height
			if (cell.isUseAsText()) {
				setFont(casFont.deriveFont(cell.getFontStyle(),
						(float) (casFont.getSize()
								* (cell.getFontSizeMultiplier()))));

				setForeground(GColorD.getAwtColor(cell.getFontColor()));
				dummyField.setForeground(
						GColorD.getAwtColor(cell.getFontColor()));
				this.getInputArea().setForeground(
						GColorD.getAwtColor(cell.getFontColor()));
			} else {
				setFont(casFont);
			}
			updateTableRowHeight(table, row);

			// set inputPanel width to match table column width
			// -1 = set to table column width (even if larger than viewport)
			setInputPanelWidth(-1);
		}
		return this;
	}

}
