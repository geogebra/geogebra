package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.awt.GColorD;

public class MyCellEditorButton extends AbstractCellEditor
		implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	private final JButton delegate = new JButton();
	private GeoButton editGeo;

	/**
	 * Creates cell editor
	 */
	public MyCellEditorButton() {
		delegate.addActionListener(actionEvent -> SwingUtilities.invokeLater(
				() -> editGeo.runClickScripts(null)));
	}

	@Override
	public Object getCellEditorValue() {
		return editGeo;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		editGeo = (GeoButton) value;

		// show hide label by setting text
		if (editGeo.isLabelVisible()) {
			// get caption to show r
			String caption = editGeo.getCaption(StringTemplate.defaultTemplate);
			caption = GeoElement.indicesToHTML(caption, true);
			delegate.setText(caption);
		} else {
			delegate.setText(" ");
		}

		delegate.setOpaque(true);
		delegate.setHorizontalAlignment(SwingConstants.CENTER);
		// delegate.setFont(view.fontPoint);
		delegate.setForeground(GColorD.getAwtColor(editGeo.getObjectColor()));

		return delegate;
	}
}
