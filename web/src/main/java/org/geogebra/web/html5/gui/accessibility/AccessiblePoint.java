package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.util.sliderPanel.SliderW;

/**
 * Accessibility adapter for 2D and 3D points
 */
public class AccessiblePoint implements AccessibleWidget, HasSliders {

	private List<SliderW> sliders;
	private double[] oldVal = new double[4];
	private AccessibilityView view;
	private Kernel kernel;
	private GeoPointND point;

	/**
	 * @param point
	 *            point
	 * @param widgetFactory
	 *            slider factory
	 * @param view
	 *            accessibility view
	 */
	public AccessiblePoint(GeoPointND point, BaseWidgetFactory widgetFactory,
			AccessibilityView view) {
		this.view = view;
		this.point = point;
		sliders = new ArrayList<>(3);
		kernel = point.getKernel();
		initSliders(widgetFactory);
		update();
	}

	private void initSliders(BaseWidgetFactory widgetFactory) {
		for (int i = 0; i < point.getDimension(); i++) {
			sliders.add(WidgetFactory.makeSlider(i, this, widgetFactory));
		}
	}

	@Override
	public List<SliderW> getWidgets() {
		return sliders;
	}

	private void updatePointSlider(SliderW range, int index) {
		String[] labels = { "x coordinate of ", "y coordinate of ", "z coordinate of " };
		AriaHelper.setLabel(range, labels[index] + point.getNameDescription());
		App app = kernel.getApplication();
		range.setMinimum(Math.floor(app.getActiveEuclidianView().getXmin()));
		range.setMaximum(Math.ceil(app.getActiveEuclidianView().getXmax()));
		range.setStep(point.getAnimationStep());
		double coord = point.getInhomCoords().get(index + 1);
		double[] coords = point.getInhomCoords().get();
		for (int i = 0; i < coords.length; i++) {
			oldVal[i] = coords[i];
		}
		range.setValue(coord);
		view.updateValueText(range, coord, "");
	}

	@Override
	public void onValueChange(int index, double value) {
		double step = sliders.get(index).getValue() - oldVal[index];

		oldVal[index] += step;
		if (point != null && point.isGeoPoint()) {
			double[] increments = { 0, 0, 0 };
			increments[index] = step;
			kernel.getApplication().getGlobalKeyDispatcher().handleArrowKeyMovement(
					Collections.singletonList(point.toGeoElement()),
					increments, step);
		}
		view.updateValueText(sliders.get(index), sliders.get(index).getValue(), "");
	}

	@Override
	public void update() {
		for (int i = 0; i < point.getDimension(); i++) {
			updatePointSlider(sliders.get(i), i);
		}
	}

	@Override
	public void setFocus(boolean focus) {
		sliders.get(0).setFocus(focus);
	}

	@Override
	public boolean isCompatible(GeoElement geo) {
		return true;
	}

}
