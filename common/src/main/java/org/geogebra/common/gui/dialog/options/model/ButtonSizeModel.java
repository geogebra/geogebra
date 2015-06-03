package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoButton;



public class ButtonSizeModel extends OptionsModel {
	public interface IButtonSizeListener extends PropertyListener {
		void updateSizes(int width, int height, boolean isFixed);

	}
	
	private IButtonSizeListener listener;
	
	public ButtonSizeModel() {
	}

	public void setListener(IButtonSizeListener listener) {
		this.listener = listener;
	}
	private GeoButton getButtonAt(int index) {
		return (GeoButton)getObjectAt(index);
	}
	
	@Override
	public void updateProperties() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = getButtonAt(i);
			
			listener.updateSizes(geo.getWidth(), geo.getHeight(), geo.isFixed());
		}
	
	}
   
	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isGeoButton() && !getGeoAt(index).isGeoTextField();
	}
	
	public void setSizesFromString(String strWidth, String strHeight, boolean isFixed) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = getButtonAt(i);
			geo.setFixedSize(isFixed);
			if (isFixed) {
				geo.setHeight(Integer.parseInt(strHeight));
				geo.setWidth(Integer.parseInt(strWidth));
			} else {
				geo.setFixedSize(false);
			}
		}		
	}
	
	public void applyChanges(boolean isFixed) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = getButtonAt(i);
			geo.setFixedSize(isFixed);
			listener.updateSizes(geo.getWidth(), geo.getHeight(), isFixed);
		}
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	};
}
