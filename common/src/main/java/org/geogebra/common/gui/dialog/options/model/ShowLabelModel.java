package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;

public class ShowLabelModel extends OptionsModel{
	public interface IShowLabelListener {
		void update(boolean isEqualVal, boolean isEqualMode);


	}
	
	private App app;
	private Kernel kernel;

	private static final long serialVersionUID = 1L;
	private boolean showNameValue;
	private IShowLabelListener listener;
	
	public ShowLabelModel(App app, IShowLabelListener listener) {
		this.app = app;
		kernel = app.getKernel();
		this.listener = listener;
	}

	
	@Override
	public void updateProperties() {
	
		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalLabelVal = true;
		boolean equalLabelMode = true;
		showNameValue = geo0.isLabelValueShowable() && !isDropDownList(geo0) && !geo0.isGeoTextField();

		for (int i = 1; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same label visible value
			if (geo0.isLabelVisible() != temp.isLabelVisible())
				equalLabelVal = false;
			// same label mode
			if (geo0.getLabelMode() != temp.getLabelMode())
				equalLabelMode = false;

			showNameValue = showNameValue
					&& temp.isLabelValueShowable() && !isDropDownList(temp) && !temp.isGeoTextField();
		}

		// change "Show Label:" to "Show Label" if there's no menu
		// Michael Borcherds 2008-02-18
		listener.update(equalLabelVal, equalLabelMode);
		}


	private boolean isDropDownList(GeoElement geo) {
		return (geo.isGeoList() && ((GeoList)geo).drawAsComboBox());
		
	}
	
	public void applyShowChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
				GeoElement geo = getGeoAt(i);
				geo.setLabelVisible(value);
				geo.updateRepaint();
			}
			updateProperties();
	}
	
	public void applyModeChanges(int mode) {
			GeoElement geo;
			for (int i = 0; i < getGeosLength(); i++) {
				geo = getGeoAt(i);
				geo.setLabelMode(mode);
				geo.updateVisualStyle();
			}
			kernel.notifyRepaint();
			app.storeUndoInfo();
		}
	
	public boolean isNameValueShown() {
		return showNameValue;
	}


	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return geo.isLabelShowable() || isDropDownList(geo) || geo.isGeoTextField();
	}
}