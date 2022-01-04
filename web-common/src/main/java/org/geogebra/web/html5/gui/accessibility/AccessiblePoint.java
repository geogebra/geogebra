package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.util.sliderPanel.SliderW;

/**
 * Accessibility adapter for 2D and 3D points
 */
public class AccessiblePoint implements AccessibleWidget, HasSliders {

	private final List<SliderW> sliders;
	private final double[] oldVal = new double[4];
	private final AccessibilityView view;
	private final Kernel kernel;
	private final GeoPointND point;

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
		App app = kernel.getApplication();
		EuclidianView ev = app.getActiveEuclidianView();
		if (ev instanceof EuclidianView3D && index == 2) {
			range.setMinimum(Math.floor(((EuclidianView3D) ev).getZmin()));
			range.setMaximum(Math.ceil(((EuclidianView3D) ev).getZmax()));
		} else if (index == 0) {
			range.setMinimum(Math.floor(ev.getXmin()));
			range.setMaximum(Math.ceil(ev.getXmax()));
		} else {
			range.setMinimum(Math.floor(ev.getYmin()));
			range.setMaximum(Math.ceil(ev.getYmax()));
		}
		range.setStep(point.getAnimationStep());
		double coord = point.getInhomCoords().get(index + 1);
		double[] coords = point.getInhomCoords().get();
		System.arraycopy(coords, 0, oldVal, 0, coords.length);
		range.setValue(coord);
		updateLabel(range, index);
	}

	private void updateLabel(SliderW range, int index) {
		String[] labels = { "x coordinate of ", "y coordinate of ", "z coordinate of " };
		ScreenReaderBuilder sb = new ScreenReaderBuilder(kernel.getLocalization());
		point.addAuralName(sb);
		AriaHelper.setLabel(range, labels[index] + sb.toString());
	}

	@Override
	public void onValueChange(int index, double value) {
		double step = value - oldVal[index];
		oldVal[index] += step;
		if (point != null && point.isGeoPoint()) {
			double[] increments = { 0, 0, 0 };
			increments[index] = step;
			kernel.getApplication().getGlobalKeyDispatcher().handleArrowKeyMovement(
					Collections.singletonList(point.toGeoElement()), increments);
			ScreenReaderBuilder sb = new ScreenReaderBuilder(kernel.getLocalization());
			if (!point.addAuralCaption(sb)) {
				point.addAuralLabel(sb);
			}
			view.updateValueText(sliders.get(index), value,
					kernel.getLocalization().getPlain("PointAMovedToB",
					sb.toString(), getCoords()));
		}
	}

	private String getCoords() {
		double[] coords = point.getInhomCoords().get();
		StringBuilder coordString = new StringBuilder();
		for (int i = 0; i < coords.length; i++) {
			if (i > 0) {
				coordString.append(" comma ");
			}
			coordString.append(coords[i]);
		}
		return coordString.toString();
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
