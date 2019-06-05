package org.geogebra.web.html5.gui.accessibility;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EventType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AccessibleGeoElement implements AccessibleWidget {

	private Button button;
	private GeoElement geo;
	private Label label;
	private App app;
	private Widget activeWidget;

	public AccessibleGeoElement(final GeoElement geo, final App app,
			final AccessibilityView view) {
		this.geo = geo;
		this.label = new Label();
		this.button = new Button();
		this.app = app;
		update();
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				view.select(geo);
				app.handleSpaceKey();
				button.setFocus(true);
			}
		});
	}

	@Override
	public void update() {
		button.setText(app.getAccessibilityManager().getSpaceAction(geo));
		label.setText(geo.getAuralText(new ScreenReaderBuilder()));
		activeWidget = geo.getScript(EventType.CLICK) == null ? label : button;
	}

	@Override
	public List<Widget> getControl() {
		return Collections.singletonList(activeWidget);
	}

}
