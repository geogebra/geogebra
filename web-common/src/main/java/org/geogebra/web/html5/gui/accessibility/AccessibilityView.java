package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import elemental2.dom.DomGlobal;

/**
 * View for representation of geo elements as hidden DOM controls
 */
public class AccessibilityView implements View {
	private final BaseWidgetFactory sliderFactory;
	private final FlowPanel controls;
	private final Map<GeoElement, AccessibleWidget> widgets;
	private final AppW app;
	private AccessibleGraphicsView graphicsView3D;
	private final static List<String> ALT_TEXT_LABELS =
			Arrays.asList("altText", "altText1", "altText2", "altText3D");

	/**
	 * @param app
	 *            application
	 * @param sliderFactory
	 *            slider factory
	 */
	public AccessibilityView(final AppW app, BaseWidgetFactory sliderFactory) {
		this.app = app;
		this.controls = sliderFactory.newPanel();
		this.sliderFactory = sliderFactory;
		widgets = new HashMap<>();
		app.getKernel().attach(this);
		new Timer() {

			@Override
			public void run() {
				app.getKernel().notifyAddAll(AccessibilityView.this);
			}
		}.schedule(500);
		initGraphics3DControls();
	}

	private void initGraphics3DControls() {
		if (app.showView(App.VIEW_EUCLIDIAN3D)) {
			if (graphicsView3D == null) {
				graphicsView3D = new AccessibleGraphicsView(app, sliderFactory, this);
			}
			addControl(graphicsView3D, null);
		}
	}

	@Override
	public void add(final GeoElement geo) {

		if (!isInteractive(geo) || widgets.containsKey(geo)) {
			return;
		}
		AccessibleWidget control = createControl(geo);
		AccessibleWidget prevWidget = getPreviousWidget(geo);
		addControl(control, prevWidget);
		widgets.put(geo, control);
		attachToDom();
	}

	private AccessibleWidget createControl(GeoElement geo) {
		AccessibleWidget control;
		if (geo instanceof GeoNumeric && ((GeoNumeric) geo).isSlider()) {
			control = new AccessibleSlider((GeoNumeric) geo, sliderFactory, this);
		} else if (geo instanceof GeoBoolean) {
			control = new AccessibleCheckbox((GeoBoolean) geo, this);
		} else if (geo instanceof GeoPointND) {
			control = new AccessiblePoint((GeoPointND) geo, sliderFactory, this);
		} else if (geo instanceof GeoInputBox) {
			control = new AccessibleInputBox((GeoInputBox) geo, app);
		} else if (geo instanceof GeoList && ((GeoList) geo).drawAsComboBox()) {
			control = new AccessibleDropDown((GeoList) geo, app, this, sliderFactory);
		} else  {
			control = new AccessibleGeoElement(geo, app, this, sliderFactory);
		}
		return control;
	}

	private AccessibleWidget getPreviousWidget(GeoElement geo) {
		AccessibleWidget prevWidget;
		ArrayList<GeoElement> accessibleGeos = new ArrayList<>();
		for (String label: ALT_TEXT_LABELS) {
			if (app.getKernel().lookupLabel(label) != null) {
				accessibleGeos.add(app.getKernel().lookupLabel(label));
			}
		}
		app.getSelectionManager().getEVFilteredTabbingStream().forEach(accessibleGeos::add);
		int index = accessibleGeos.indexOf(geo);
		do {
			index--;
			prevWidget = index < 0 ? null : widgets.get(accessibleGeos.get(index));
		} while (index > 0 && prevWidget == null);
		return prevWidget;
	}

	private static boolean isInteractive(GeoElement geo) {
		if (geo.getLabelSimple() != null && ALT_TEXT_LABELS.contains(geo.getLabelSimple())) {
			return true;
		}
		if (!geo.isEuclidianVisible() || !geo.isSelectionAllowed(null)
				|| geo.getLabelSimple() == null) {
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

	private void addControl(AccessibleWidget widget, AccessibleWidget prevWidget) {
		int position = -1;
		if (prevWidget != null) {
			position = findLastControlOf(prevWidget);
		}
		for (Widget control : widget.getWidgets()) {
			control.addStyleName("accessibilityControl");
			controls.insert(control, position + 1);
			position++;
		}
	}

	private int findLastControlOf(AccessibleWidget prevWidget) {
		int i = controls.getWidgetCount() - 1;
		while (i >= 0 && !prevWidget.getWidgets().contains(controls.getWidget(i))) {
			i--;
		}
		return i;
	}

	@Override
	public void remove(GeoElement geo) {
		AccessibleWidget widget = widgets.get(geo);
		if (widget != null) {
			for (Widget control : widget.getWidgets()) {
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
			AccessibleWidget widget = widgets.get(geo);
			if (widget.isCompatible(geo)) {
				widget.update();
			} else {
				remove(geo);
				add(geo);
			}
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
		initGraphics3DControls();
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
		ArrayList<GeoElement> list = app.getSelectionManager().getSelectedGeos();
		if (list != null && list.size() == 1) {
			selectWidget(list.get(0));
		}
	}

	private void selectWidget(GeoElement geoElement) {
		if (widgets.get(geoElement) != null) {
			widgets.get(geoElement).setFocus(true);
		}
	}

	/**
	 * @param range slider
	 * @param value numeric value
	 * @param unit  unit
	 */
	public void updateValueText(SliderW range, double value, String unit) {
		Element el = range.getElement();
		String label = el.getAttribute("aria-label");
		el.removeAttribute("aria-label");
		el.setAttribute("aria-valuetext",
				app.getKernel().format(value, StringTemplate.screenReader) + " " + unit);
		DomGlobal.setTimeout(ignore -> el.setAttribute("aria-label", label),
				1000);
	}

	/**
	 * Close all dropdowns
	 */
	public void closeAllDropdowns() {
		for (AccessibleWidget widget: widgets.values()) {
			if (widget instanceof AccessibleDropDown) {
				((AccessibleDropDown) widget).close();
			}
		}
	}

	/**
	 * @param geo element
	 * @return caption or name in screen readable format
	 */
	public String getCaption(GeoElement geo) {
		ScreenReaderBuilder sb = new ScreenReaderBuilder(app.getLocalization());
		if (!geo.addAuralCaption(sb)) {
			geo.addAuralLabel(sb);
		}
		return sb.toString();
	}
}
