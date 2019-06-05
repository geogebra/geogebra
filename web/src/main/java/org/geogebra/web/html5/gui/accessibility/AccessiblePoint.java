package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.panels.HasSliders;
import org.geogebra.web.full.gui.layout.panels.SliderFactory;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.util.sliderPanel.SliderW;

public class AccessiblePoint implements AccessibleWidget, HasSliders {

	private List<SliderW> slider;
	private double[] oldVal = new double[3];
	private AccessibilityView view;
	private Kernel kernel;
	private GeoPointND sel;

	/**
	 * @param point  point
	 * @param sliderFactory slider factory
	 * @param view   accessibility view
	 */
	public AccessiblePoint(GeoPointND point, SliderFactory sliderFactory, AccessibilityView view) {
		this.view = view;
		this.sel = point;
		slider = new ArrayList<>(3);
		kernel = point.getKernel();
		for (int i = 0; i < point.getDimension(); i++) {
			slider.add(sliderFactory.makeSlider(i, this));
		}
		update();
	}

	@Override
	public List<SliderW> getControl() {
		return slider;
	}

	private void updatePointSlider(SliderW range, GeoPointND sel, int index) {
		String[] labels = { "x coordinate of", "y coordinate of", "z coordinate of" };
		AriaHelper.setLabel(range, labels[index] + sel.getNameDescription());
		App app = sel.getKernel().getApplication();
		range.setMinimum(Math.floor(app.getActiveEuclidianView().getXmin()));
		range.setMaximum(Math.ceil(app.getActiveEuclidianView().getXmax()));
		range.setStep(sel.getAnimationStep());
		double coord = sel.getInhomCoords().get(index + 1);
		double[] coords = sel.getInhomCoords().get();
		for (int i = 0; i < coords.length; i++) {
			oldVal[i] = coords[i];
		}
		range.setValue(coord);
		view.updateValueText(range, coord, "");
	}

	@Override
	public void onValueChange(int index, double value) {
		double step = slider.get(index).getValue() - oldVal[index];

		oldVal[index] += step;
		if (sel != null && sel.isGeoPoint()) {
			kernel.getApplication().getGlobalKeyDispatcher().handleArrowKeyMovement(
					Collections.singletonList(sel.toGeoElement()),
					index == 0 ? step : 0, index == 1 ? step : 0,
					index == 2 ? step : 0, 1);
		}
		view.updateValueText(slider.get(index), slider.get(index).getValue(), "");
	}

	@Override
	public void update() {
		for (int i = 0; i < sel.getDimension(); i++) {
			updatePointSlider(slider.get(i), sel, i);
		}
	}

}
