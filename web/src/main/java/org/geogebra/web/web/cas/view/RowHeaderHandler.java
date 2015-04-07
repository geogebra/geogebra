package org.geogebra.web.web.cas.view;

import org.geogebra.common.awt.GPoint;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;

public class RowHeaderHandler implements MouseUpHandler {

	private AppW app;
	private RowHeaderWidget rowHeader;
	private CASTableW table;

	public RowHeaderHandler(AppW appl, CASTableW casTableW,
	        RowHeaderWidget rowHeaderWidget) {
		super();
		app = appl;
		rowHeader = rowHeaderWidget;
		table = casTableW;
	}

	public void onMouseUp(MouseUpEvent event) {
		int releasedRow = rowHeader.getIndex();
		table.getCASView().getCASStyleBar()
		        .setSelectedRow(table.getGeoCasCell(releasedRow));
		if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
			if (!table.isSelectedIndex(releasedRow)) {
				table.setSelectedRows(releasedRow, releasedRow);
			}
			if (table.getSelectedRows().length > 0) {
				// Don't istantiate RowHeaderPopupMenuW() directly. Use
				// guimanager for this,
				// because it must store in GuiManagerW.currentPopup - in this
				// way the popup will hide
				// when a newer popup will be shown.
				RowHeaderPopupMenuW popupMenu = ((GuiManagerW) app
				        .getGuiManager()).getCASContextMenu(rowHeader, table);
				popupMenu.show(new GPoint(event.getClientX()
				        + Window.getScrollLeft(), event.getClientY()
				        + Window.getScrollTop()));
			}
		}
	}
}
