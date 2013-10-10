package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.AngleProperties;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.main.App;
import geogebra.common.main.Localization;

public class ReflexAngleModel extends OptionsModel {
	public interface IReflexAngleListener {

		void setComboLabels();
		void addComboItem(final String item);

		void setSelectedIndex(int xmlVal);
		
	}
	
	private IReflexAngleListener listener;
	private boolean hasOrientation;
	private boolean isDrawable;
	private boolean isDefaults;
	private App app;
	public ReflexAngleModel(IReflexAngleListener listener, App app, boolean isDefaults) {
		this.listener = listener;
		this.app = app;
		this.isDefaults = isDefaults;
	}
	
	@Override
	public void updateProperties() {
		AngleProperties temp, geo0 = (AngleProperties) getGeoAt(0);
		boolean equalangleStyle = true;
		boolean hasOrientationOld = hasOrientation;
		boolean isDrawableOld = isDrawable;
		hasOrientation = true;
		isDrawable = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = (AngleProperties) getGeos()[i];
			if (!temp.hasOrientation()) {
				hasOrientation = false;
			}
			if (!temp.isDrawable()) {
				isDrawable = false;
			}
			if (geo0.getAngleStyle() != temp.getAngleStyle()) {
				equalangleStyle = false;
			}

		}

		if (hasOrientation != hasOrientationOld || isDrawableOld != isDrawable) {
			listener.setComboLabels();
		}

		if (equalangleStyle) {
			listener.setSelectedIndex(geo0.getAngleStyle().xmlVal);
		}
		

	}
	
	public void fillCombo() {
		Localization loc = app.getLocalization();
		if (hasOrientation) {
			int length = GeoAngle.INTERVAL_MIN.length;
			
			if (isDrawable) {
				// don't want to allow (-inf, +inf)
				length --;
			}
			
			for (int i = 0; i < length; i++)
				listener.addComboItem(loc.getPlain("AandB",
								GeoAngle.INTERVAL_MIN[i],
								GeoAngle.INTERVAL_MAX[i]));
		} else {// only 180° wide interval are possible
			listener.addComboItem(loc.getPlain("AandB",
					GeoAngle.INTERVAL_MIN[1], GeoAngle.INTERVAL_MAX[1]));
			listener.addComboItem(loc.getPlain("AandB",
					GeoAngle.INTERVAL_MIN[2], GeoAngle.INTERVAL_MAX[2]));
		}
	}
	
	public void applyChanges(int index) {
		for (int i = 0; i < getGeosLength(); i++) {
			AngleProperties geo = (AngleProperties) getGeoAt(0);
			geo.setAngleStyle(index);
			((GeoElementND) geo).updateRepaint();
		}
	}
	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(0);
			if ((geo.isIndependent() && !isDefaults)
					|| !(geo instanceof AngleProperties))
				return false;
		}
		return true;
	}

	public boolean hasOrientation() {
		return hasOrientation;
	}
}
