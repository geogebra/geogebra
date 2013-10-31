package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;



public class ButtonSizeModel extends OptionsModel {
	public interface IButtonSizeListener {
		void updateSizes(int width, int height, boolean isFixed);
	}
	
	private IButtonSizeListener listener;
	
	public ButtonSizeModel(IButtonSizeListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void updateProperties() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = (GeoButton)getGeoAt(i);
			listener.updateSizes(geo.getWidth(), geo.getHeight(), geo.isFixed());
		}
	
	}
   
	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (!geo.isGeoButton())
				return false;
		}
		return true;
	
	}
	
	public void setSizesFromString(String strWidth, String strHeight, boolean isFixed) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = (GeoButton) getGeoAt(i);
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
			GeoButton geo = (GeoButton) getGeoAt(i);
			geo.setFixedSize(isFixed);
			listener.updateSizes(geo.getWidth(), geo.getHeight(), isFixed);
		}
	}
}
