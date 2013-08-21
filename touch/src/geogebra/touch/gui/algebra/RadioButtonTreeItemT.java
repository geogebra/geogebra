package geogebra.touch.gui.algebra;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.gui.view.algebra.RadioButtonTreeItem;
import geogebra.touch.controller.TouchController;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Event;

public class RadioButtonTreeItemT extends RadioButtonTreeItem {

	private final TouchController controller;

	public RadioButtonTreeItemT(GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl, MouseDownHandler mdh, TouchController controller) {

		super(ge, showUrl, hiddenUrl, mdh);
		sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS);
		this.controller = controller;
	}

	@Override
	public void onClick(ClickEvent evt) {
		handleClick();
	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		openRedefine();
	}

	@Override
	public void onMouseMove(MouseMoveEvent evt) {
		// don't do anything
	}

	protected void openRedefine() {
		this.controller.redefine(this.getGeo());
	}

	protected void handleClick() {
		final Hits hits = new Hits();
		hits.add(this.getGeo());
		this.controller.handleEvent(hits);
	}
}
