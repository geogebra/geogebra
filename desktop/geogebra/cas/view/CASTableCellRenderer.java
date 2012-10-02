package geogebra.cas.view;

import geogebra.awt.GColorD;
import geogebra.common.kernel.geos.GeoCasCell;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * CAS cell renderer
 */
public class CASTableCellRenderer extends CASTableCell implements
		TableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * @param view CAS view
	 */
	CASTableCellRenderer(CASViewD view) {
		super(view);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value instanceof GeoCasCell) {
			// set CASTableCell value
			setValue((GeoCasCell) value);

			// update font and row height
			if (((GeoCasCell) value).isUseAsText()) {
				setFont(view.getCASViewComponent().getFont()
						.deriveFont(((GeoCasCell) value).getFontStyle(),(float)(12*((GeoCasCell) value).getFontSizeMultiplier())));// ,
																			// ((GeoCasCell)
				setForeground(GColorD.getAwtColor(((GeoCasCell) value).getFontColor()));
				dummyField.setForeground(GColorD.getAwtColor(((GeoCasCell) value).getFontColor()));
				this.getInputArea().setForeground(GColorD.getAwtColor(((GeoCasCell) value).getFontColor()));
			} else
				setFont(view.getCASViewComponent().getFont());
			updateTableRowHeight(table, row);

			// set inputPanel width to match table column width
			// -1 = set to table column width (even if larger than viewport)
			setInputPanelWidth(-1);
		}
		return this;
	}

}
