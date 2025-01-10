package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.main.App;

public abstract class SliderOptionsModel extends NumberOptionsModel {
	private ISliderListener listener;

	public SliderOptionsModel(App app) {
		super(app);
	}

	@Override
	public void updateProperties() {
		getListener().setValue(getValueAt(0));

	}

	@Override
	public ISliderListener getListener() {
		return listener;
	}

	public void setListener(ISliderListener listener) {
		this.listener = listener;
	}

}