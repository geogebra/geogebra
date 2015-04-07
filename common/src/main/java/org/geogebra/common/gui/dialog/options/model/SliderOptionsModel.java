package org.geogebra.common.gui.dialog.options.model;


public abstract class SliderOptionsModel extends NumberOptionsModel {
	private ISliderListener listener;
	public SliderOptionsModel(ISliderListener listener) {
		this.setListener(listener);
	}
	

	@Override
	public void updateProperties() {
		getListener().setValue(getValueAt(0));

	}


	public ISliderListener getListener() {
		return listener;
	}


	public void setListener(ISliderListener listener) {
		this.listener = listener;
	}

}