package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;

public abstract class AbsoluteScreenPositionModel extends TextPropertyModel {

	public interface PositionListener extends PropertyListener {

		void setLocation(int x);
	}

	public static class ForX extends AbsoluteScreenPositionModel {
		public ForX(App app) {
			super(app);
		}

		@Override
		public String getTitle() {
			return "x";
		}

		@Override
		protected void setCoord(AbsoluteScreenLocateable geo, int coord) {
			geo.setAbsoluteScreenLoc(coord, geo.getAbsoluteScreenLocY());
		}

		@Override
		public int getCoord(AbsoluteScreenLocateable abs) {
			return abs.getAbsoluteScreenLocX();
		}
	}

	public static class ForY extends AbsoluteScreenPositionModel {
		public ForY(App app) {
			super(app);
		}

		@Override
		public String getTitle() {
			return "y";
		}

		@Override
		protected void setCoord(AbsoluteScreenLocateable geo, int coord) {
			geo.setAbsoluteScreenLoc(geo.getAbsoluteScreenLocX(), coord);
		}

		@Override
		public int getCoord(AbsoluteScreenLocateable abs) {
			return abs.getAbsoluteScreenLocY();
		}
	}

	protected AbsoluteScreenPositionModel(App app) {
		super(app);
	}

	@Override
	protected boolean isValidAt(int index) {
		return getGeoAt(index) instanceof AbsoluteScreenLocateable
				&& ((AbsoluteScreenLocateable) getGeoAt(index)).isAbsoluteScreenLocActive();
	}

	@Override
	public void applyChanges(GeoNumberValue value) {
		if (value != null) {
			for (GeoElement geo : getGeosAsList()) {
				setCoord((AbsoluteScreenLocateable) geo, (int) value.getDouble());
				geo.updateVisualStyleRepaint(GProperty.POSITION);
			}
		}
	}

	protected abstract void setCoord(AbsoluteScreenLocateable geo, int coord);

	@Override
	public void updateProperties() {
		AbsoluteScreenLocateable temp, geo0 = (AbsoluteScreenLocateable) getGeoAt(0);
		boolean equalCoord = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = (AbsoluteScreenLocateable) getGeoAt(i);
			if (getCoord(geo0) != getCoord(temp)) {
				equalCoord = false;
			}
		}

		if (equalCoord) {
			listener.setText(getCoord(geo0) + "");
		} else {
			listener.setText("");
		}
	}

	protected abstract int getCoord(AbsoluteScreenLocateable geoAt);
}
