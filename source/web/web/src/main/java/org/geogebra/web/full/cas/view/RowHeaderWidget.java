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

import org.geogebra.common.cas.view.CASInputHandler;
import org.geogebra.common.cas.view.MarbleRenderer;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

/**
 * Row header with marble
 */
public class RowHeaderWidget extends FlowPanel implements MarbleRenderer {
	private final Image marble;
	private boolean oldValue;
	private final RowHeaderHandler handler;
	private final CASTableW casTable;

	/**
	 * @param casTable
	 *            table
	 * @param n
	 *            row number
	 * @param cell
	 *            cell
	 * @param app
	 *            application
	 */
	public RowHeaderWidget(CASTableW casTable, int n, GeoCasCell cell, AppW app) {
		this.casTable = casTable;
		Label label = new Label();
		label.setText(n + "");
		marble = new Image(AppResources.INSTANCE.hidden().getSafeUri().asString());
		oldValue = false;
		add(label);
		add(marble);
		if (cell != null) {
			CASInputHandler.handleMarble(cell, this);
		}
		addStyleName("casRowHeader");
		marble.addClickHandler(new MarbleClickHandler(cell, this));
		ClickStartHandler.initDefaults(marble, false, true);
		// instead of here, from now on the whole of header areas should
		// handle this event, so this is moved to CASTableCellControllerW
		// but still, create the RowHeaderHandler for quick implementation
		// addDomHandler(
		handler = new RowHeaderHandler(app, casTable, this);
		// , MouseUpEvent.getType());
	}

	/**
	 * @return handler
	 */
	public RowHeaderHandler getHandler() {
		return handler;
	}

	/**
	 * @param number
	 *            row number
	 */
	public void setLabel(int number) {
		((Label) getWidget(0)).setText(number + "");
	}

	/**
	 * @return index in view (starts with 0)
	 */
	public int getIndex() {
		return Integer.parseInt(((Label) getWidget(0)).getText()) - 1;
	}

	@Override
	public void setMarbleValue(boolean value) {
		if (value == oldValue) {
			return;
		}
		marble.setUrl(value ? AppResources.INSTANCE.shown().getSafeUri().asString()
				: AppResources.INSTANCE.hidden().getSafeUri().asString());
		oldValue = value;

	}

	@Override
	public void setMarbleVisible(boolean visible) {
		marble.setVisible(visible);
	}

	/**
	 * Handler for marble
	 */
	protected static class MarbleClickHandler implements ClickHandler {
		private final GeoCasCell cell;
		private final RowHeaderWidget rowHeaderWidget;

		/**
		 * @param cell
		 *            cas cell
		 * @param rowHeaderWidget
		 *            row header
		 */
		protected MarbleClickHandler(GeoCasCell cell,
				RowHeaderWidget rowHeaderWidget) {
			this.cell = cell;
			this.rowHeaderWidget = rowHeaderWidget;
		}

		@Override
		public void onClick(ClickEvent event) {
			cell.toggleTwinGeoEuclidianVisible();
			rowHeaderWidget.cancelAnyEditing();
			CASInputHandler.handleMarble(cell, rowHeaderWidget);
			event.stopPropagation();
		}
	}

	private void cancelAnyEditing() {
		casTable.cancelEditing();
	}

}
