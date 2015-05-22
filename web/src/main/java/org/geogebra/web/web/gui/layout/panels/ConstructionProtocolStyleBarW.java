package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView.ColumnData;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView.RowData;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.AppResourcesConverter;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.util.PopupMenuHandler;
import org.geogebra.web.web.gui.util.StyleBarW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolViewW;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;

public class ConstructionProtocolStyleBarW extends StyleBarW implements
		PopupMenuHandler {

	private ConstructionProtocolViewW cpView;
	private PopupMenuButton btnColumns;

	String[] tableColumns = {};
	boolean[] show = {};

	public ConstructionProtocolStyleBarW(ConstructionProtocolViewW cpw, AppW app) {
		super(app, App.VIEW_CONSTRUCTION_PROTOCOL);
		cpView = cpw;
		addButtons();
		addViewButton();
	}

	protected void addButtons() {
		int k0 = 0;
		for (int k = 0; k < cpView.getData().getColumnCount(); k++) {
			ColumnData colData = cpView.getData().getColumns()[k];
			 // On web there is no all columns yet, so temporary must hide
			// some
			// column on stylebar too
			if (!"No.".equals(colData.getTitle())
					&& !"ToolbarIcon".equals(colData.getTitle())
					&& !"Command".equals(colData.getTitle())
					&& !"Caption".equals(colData.getTitle())
					&& !"Breakpoint".equals(colData.getTitle())) {
				tableColumns[k0] = colData.getTranslatedTitle();
				show[k0] = colData.isVisible();
				k0++;
			}
		}

		btnColumns = new PopupMenuButton(app,
		        ImageOrText.convert(tableColumns), -1, 1,
		        org.geogebra.common.gui.util.SelectionTable.MODE_TEXT, true, false,
		        show);

		ImageResource ic = AppResources.INSTANCE.header_column();
		AppResourcesConverter.setIcon(ic, btnColumns);
		btnColumns.addPopupHandler(this);
		btnColumns.setKeepVisible(false);
		add(btnColumns);
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub
	}

	public void fireActionPerformed(PopupMenuButton actionButton) {
		if (actionButton == btnColumns) {

			// which item was selected in the menu
			int selIndex = btnColumns.getSelectedIndex();

			// which column should be add or remove
			int colIndex = 0;
			for (int i = 0; i < selIndex + 1; i++) {
				if (btnColumns.isSelected(i)) {
					colIndex++;
				}
			}

			if (show[selIndex]) { // adding a new column for the table
				Column<RowData, ?> col = cpView
						.getColumn(tableColumns[selIndex]);
				cpView.getTable().insertColumn(colIndex, col,
						app.getPlain(tableColumns[selIndex]));
				cpView.getData().columns[selIndex + 1].setVisible(true);
				
			} else { // removing a column from the table
				App.debug("remove!");
				cpView.getTable().removeColumn(colIndex + 1);
				cpView.getData().columns[selIndex + 1].setVisible(false);
			}
		}
	}

}
