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

package org.geogebra.web.full.cas.view;

import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseUpHandler;

/**
 * Handles mouse events in row headers
 */
public class RowHeaderHandler implements MouseUpHandler {

	private AppW app;
	private RowHeaderWidget rowHeader;
	private CASTableW table;

	/**
	 * @param appl
	 *            app
	 * @param casTableW
	 *            CAS table
	 * @param rowHeaderWidget
	 *            row headers
	 */
	public RowHeaderHandler(AppW appl, CASTableW casTableW,
	        RowHeaderWidget rowHeaderWidget) {
		super();
		app = appl;
		rowHeader = rowHeaderWidget;
		table = casTableW;
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		int releasedRow = rowHeader.getIndex();
		table.getCASView().getCASStyleBar()
				.setSelectedRow(table.getGeoCasCell(releasedRow));
		if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
			if (!table.isSelectedIndex(releasedRow)) {
				table.setSelectedRows(releasedRow, releasedRow);
			}
			if (table.getSelectedRows().length > 0) {
				// Don't instantiate RowHeaderPopupMenuW() directly. Use
				// guimanager for this,
				// because it must store in GuiManagerW.currentPopup - in this
				// way the popup will hide
				// when a newer popup will be shown.
				RowHeaderPopupMenuW popupMenu = ((GuiManagerW) app
						.getGuiManager()).getCASContextMenu(table);
				popupMenu.show(event.getClientX() + NavigatorUtil.getWindowScrollLeft(),
						event.getClientY() + NavigatorUtil.getWindowScrollTop());
			}
		}
	}
}
