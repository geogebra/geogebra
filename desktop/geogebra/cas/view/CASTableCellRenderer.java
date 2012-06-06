package geogebra.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CASTableCellRenderer extends CASTableCell implements
		TableCellRenderer {

	private static final long serialVersionUID = 1L;

	CASTableCellRenderer(CASView view) {
		super(view);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value instanceof GeoCasCell) {
			// set CASTableCell value
			setValue((GeoCasCell) value);

			// update font and row height
			if(((GeoCasCell) value).isUseAsText()){
				setFont(view.getCASViewComponent().getFont().deriveFont(((GeoCasCell) value).getFontStyle()));//, ((GeoCasCell) value).getFontSize()));
			}else
				setFont(view.getCASViewComponent().getFont());
			updateTableRowHeight(table, row);

			// set inputPanel width to match table column width
			// -1 = set to table column width (even if larger than viewport)
			setInputPanelWidth(-1);
		}
		return this;
	}

}









