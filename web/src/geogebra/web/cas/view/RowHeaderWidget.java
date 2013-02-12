package geogebra.web.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.MarbleRenderer;
import geogebra.web.gui.images.AppResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RowHeaderWidget extends VerticalPanel implements MarbleRenderer {
	private Image marble;
	private boolean oldValue;

	public RowHeaderWidget(int n, GeoCasCell cell) {
		Label label = new Label();
		label.setText(n + "");
		marble = new Image(AppResources.INSTANCE.hidden());
		oldValue = false;
		add(label);
		add(marble);
		if (cell != null)
			cell.handleMarble(this);
		marble.addClickHandler(new MarbleClickHandler(cell,this));
	}

	public void setMarbleValue(boolean value) {
		if (value == oldValue)
			return;
		marble.setUrl(value ? AppResources.INSTANCE.shown().getSafeUri()
		        : AppResources.INSTANCE.hidden().getSafeUri());
		oldValue = value;

	}

	public void setMarbleVisible(boolean visible) {
		marble.setVisible(visible);

	}

	protected class MarbleClickHandler implements ClickHandler {
		private GeoCasCell cell;
		private RowHeaderWidget rowHeaderWidget;

		protected MarbleClickHandler(GeoCasCell cell, RowHeaderWidget rowHeaderWidget) {
			this.cell = cell;
			this.rowHeaderWidget = rowHeaderWidget;
		}

		public void onClick(ClickEvent event) {
			cell.toggleTwinGeoEuclidianVisible();
			cell.handleMarble(rowHeaderWidget);
			event.stopPropagation();
		}
	}

}
