package org.geogebra.web.html5.gui.accessibility;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderW;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for representation of geo elements as hidden DOM controls
 */
public class AccessibilityView implements View {
	private SliderFactory sliderFactory;
	private FlowPanel controls;
	private Map<GeoElement, AccessibleWidget> widgets;
	private AppW app;
	private AccessibleGraphicsView graphicsView;

	/**
	 * @param app application
	 */
	public AccessibilityView(final AppW app) {
		this.app = app;
		controls = new FlowPanel();
		controls.addStyleName("accessibilityView");
		sliderFactory = new SliderFactory();
		graphicsView = new AccessibleGraphicsView(app, sliderFactory, this);
		hideUIElement(controls);
		widgets = new HashMap<>();
		app.getKernel().attach(this);
		new Timer() {

			@Override
			public void run() {
				app.getKernel().notifyAddAll(AccessibilityView.this);
			}
		}.schedule(500);
	}

	@Override
	public void add(final GeoElement geo) {
		if (!isInteractive(geo) || widgets.containsKey(geo)) {
			return;
		}
		for (Widget drawcontrol : graphicsView.getControl()) {
			drawcontrol.removeFromParent();
		}
		AccessibleWidget control;
		if (geo instanceof GeoNumeric && ((GeoNumeric) geo).isSlider()) {
			control = new AccessibleNumeric((GeoNumeric) geo, sliderFactory, this);
		} else if (geo instanceof GeoBoolean) {
			control = new AccessibleCheckbox(
					(GeoBoolean) geo, this);
		} else if (geo instanceof GeoPointND) {
			control = new AccessiblePoint((GeoPointND) geo, sliderFactory, this);
		} else {
			control = new AccessibleGeoElement(geo, app, this);
		}
		addControl(control);
		addControl(graphicsView);
		widgets.put(geo, control);
		attachToDom();
	}

	private static boolean isInteractive(GeoElement geo) {
		if (!geo.isEuclidianVisible() || !geo.isSelectionAllowed(null)) {
			return false;
		}
		for (int euclidianView = 1; euclidianView < 4; euclidianView++) {
			if (geo.isVisibleInEV(euclidianView)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param geo
	 *            element to select
	 */
	protected void select(GeoElement geo) {
		app.getSelectionManager().clearSelectedGeos();
		app.getSelectionManager().addSelectedGeo(geo);
	}

	private void addControl(AccessibleWidget widget) {
		for (Widget control : widget.getControl()) {
			controls.add(control);
		}
	}

	@Override
	public void remove(GeoElement geo) {
		AccessibleWidget widget = widgets.get(geo);
		if (widget != null) {
			for (Widget control : widget.getControl()) {
				control.removeFromParent();
			}
		}
		widgets.remove(geo);
	}

	@Override
	public void rename(GeoElement geo) {
		update(geo);
	}

	@Override
	public void update(GeoElement geo) {
		if (!isInteractive(geo)) {
			remove(geo);
		} else if (!widgets.containsKey(geo)) {
			add(geo);
		} else {
			widgets.get(geo).update();
		}
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		if (prop == GProperty.VISIBLE || prop == GProperty.COMBINED) {
			update(geo);
		}
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// not needed
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// not needed
	}

	@Override
	public void repaintView() {
		// not needed
	}

	@Override
	public boolean suggestRepaint() {
		return false; // no repaint
	}

	@Override
	public void reset() {
		// not needed
	}

	@Override
	public void clearView() {
		controls.clear();
		widgets.clear();
		addControl(graphicsView);
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// not needed
	}

	@Override
	public int getViewID() {
		return App.VIEW_ACCESSIBILITY;
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void startBatchUpdate() {
		// not needed
	}

	@Override
	public void endBatchUpdate() {
		// not needed
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// not needed
	}

	/**
	 * Attach the panel with controls to DOM
	 */
	public void attachToDom() {
		app.getAppletFrame().add(controls);
	}

	/**
	 * Remove / add widgets to match currently open views.
	 */
	public void rebuild() {
		attachToDom();
		for (GeoElement geo : new TreeSet<>(widgets.keySet())) {
			update(geo);
		}
		app.getKernel().notifyAddAll(this);
	}

	/**
	 * @param range slider
	 * @param value numeric value
	 * @param unit  unit
	 */
	public void updateValueText(SliderW range, double value, String unit) {
		range.getElement().setAttribute("aria-valuetext",
				app.getKernel().format(value, StringTemplate.screenReader) + " " + unit);
	}

	/** For buttons we need to make sure click handler still works */
	private static void hideUIElement(Widget range) {
		range.getElement().getStyle().setOpacity(.01);
		range.getElement().getStyle().setPosition(Position.FIXED);
	}

}
