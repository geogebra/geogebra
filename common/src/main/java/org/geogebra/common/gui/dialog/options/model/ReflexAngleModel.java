package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class ReflexAngleModel extends MultipleOptionsModel {
	public interface IReflexAngleListener extends IComboListener {
		void setComboLabels();
	}

	private boolean hasOrientation;
	private boolean isDrawable;
	private boolean isDefaults;
	private App app;
	public ReflexAngleModel(IReflexAngleListener listener, App app, boolean isDefaults) {
		super(listener);
		this.app = app;
		this.isDefaults = isDefaults;
	}

	private AngleProperties getAnglePropertiesAt(int index) {
		return (AngleProperties) getObjectAt(index);
		};
	
	
	@Override
	public void updateProperties() {
		AngleProperties temp, geo0 = getAnglePropertiesAt(0);
		boolean equalangleStyle = true;
		boolean hasOrientationOld = hasOrientation;
		boolean isDrawableOld = isDrawable;
		hasOrientation = true;
		isDrawable = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = getAnglePropertiesAt(i);
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
			((IReflexAngleListener)getListener()).setComboLabels();
		}

		if (equalangleStyle) {
			getListener().setSelectedIndex(geo0.getAngleStyle().getXmlVal());
		}


	}

	@Override
	public List<String> getChoiches(Localization loc) {
		List<String> result = new ArrayList<String>();

		if (hasOrientation) {
			int length = GeoAngle.INTERVAL_MIN.length;

			if (isDrawable) {
				// don't want to allow (-inf, +inf)
				length --;
			}

			for (int i = 0; i < length; i++) {
				result.add(loc.getPlain("AandB",
						GeoAngle.INTERVAL_MIN[i],
						GeoAngle.INTERVAL_MAX[i]));
			}
		} else {// only 180degree wide interval are possible
			result.add(loc.getPlain("AandB",
					GeoAngle.INTERVAL_MIN[1], GeoAngle.INTERVAL_MAX[1]));
			result.add(loc.getPlain("AandB",
					GeoAngle.INTERVAL_MIN[2], GeoAngle.INTERVAL_MAX[2]));
		}
		return result;
	}

	@Override
	protected boolean isValidAt(int index){
		GeoElement geo = getGeoAt(index);
	
		return (!((geo.isIndependent() && !isDefaults))
				&& (geo instanceof AngleProperties) 
				&& !geo.isGeoList()) || isAngleList(geo);
		
	}

	@Override
	protected void apply(int index, int value) {
		AngleProperties geo = getAnglePropertiesAt(index);
		geo.setAngleStyle(value);
		((GeoElementND) geo).updateRepaint();
	}

	public boolean hasOrientation() {
		return hasOrientation;
	}

	@Override
	public int getValueAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}


}
