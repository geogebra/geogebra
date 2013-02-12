package geogebra.web.cas.view;

import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.MarbleRenderer;
import geogebra.web.gui.images.AppResources;

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
		if(cell!=null)
			cell.handleMarble(this);
	}

	public void setMarbleValue(boolean value) {
		if (value == oldValue)
			return;
		marble.setUrl(value ? AppResources.INSTANCE.shown().getSafeUri()
		        : AppResources.INSTANCE.hidden().getSafeUri());

	}

	public void setMarbleVisible(boolean visible) {
		marble.setVisible(visible);

	}

}
