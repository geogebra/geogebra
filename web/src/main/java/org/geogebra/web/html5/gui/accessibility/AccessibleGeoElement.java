package org.geogebra.web.html5.gui.accessibility;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.BaseWidgetFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Accessibility adapter for any element that may or may not have attached
 * scripts
 */
public class AccessibleGeoElement implements AccessibleWidget {

	private Button button;
	private GeoElement geo;
	private Label label;
	private Widget activeWidget;

	/**
	 * @param geo
	 *            construction element
	 * @param app
	 *            app
	 * @param view
	 *            accessibility view
	 * @param factory
	 *            controls factory
	 */
	public AccessibleGeoElement(final GeoElement geo, final App app,
			final AccessibilityView view, BaseWidgetFactory factory) {
		this.geo = geo;
		this.label = factory.newLabel();
		this.button = factory.newButton();
		update();
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				view.select(geo);
				app.handleSpaceKey();
				setFocus(true);
			}
		});
	}

	@Override
	public void update() {
		button.setText(getAction(geo));
		label.setText(geo.getAuralText(new ScreenReaderBuilder(Browser.isMobile())));
		activeWidget = geo.getScript(EventType.CLICK) == null ? label : button;
	}

	@Override
	public List<Widget> getWidgets() {
		return Collections.singletonList(activeWidget);
	}

	@Override
	public void setFocus(boolean focus) {
		if (activeWidget == button) {
			button.setFocus(focus);
		}
	}

	/**
	 * @param sel
	 *            selected element associated with the action
	 * @return action description (eg Run script)
	 */
	private String getAction(GeoElement sel) {
		if (sel instanceof GeoButton || sel instanceof GeoBoolean) {
			return sel.getCaption(StringTemplate.screenReader);
		}
		if (sel != null && sel.getScript(EventType.CLICK) != null) {
			return ScreenReader.getAuralText(sel, new ScreenReaderBuilder(Browser.isMobile()));
		}

		return null;
	}

}
