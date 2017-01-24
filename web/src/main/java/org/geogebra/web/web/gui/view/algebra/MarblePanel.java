package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

public class MarblePanel extends FlowPanel {
	private Marble marble;
	private boolean selected = false;
	private GeoElement geo;

	public MarblePanel(RadioTreeItem item) {
		this.geo = item.geo;
		marble = new Marble(item);
		marble.setStyleName("marble");
		marble.setEnabled(geo.isEuclidianShowable()
				&& (!item.app.isExam() || item.app.enableGraphing()));
		marble.setChecked(geo.isEuclidianVisible());

		addStyleName("marblePanel");
		add(marble);
		update();
	}

	public void setHighlighted(boolean selected) {
		this.selected = selected;
	}

	public void update() {
		marble.setEnabled(geo.isEuclidianShowable()
				&& (!geo.getKernel().getApplication().isExam()
						|| ((AppW) geo.getKernel().getApplication())
								.enableGraphing()));

		marble.setChecked(geo.isEuclidianVisible());

		setHighlighted(selected);
	}

	public boolean isHit(int x, int y) {
		return x > getAbsoluteLeft()
				&& x < getAbsoluteLeft() + getOffsetWidth()
				&& y < getAbsoluteTop() + getOffsetHeight();
	}
}