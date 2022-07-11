package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EventType;

public class CoordsModel extends MultipleOptionsModel {

	private List<Integer> coordValues;

	public CoordsModel(App app) {
		super(app);
		coordValues = Arrays.asList(Kernel.COORD_CARTESIAN, Kernel.COORD_POLAR,
				Kernel.COORD_COMPLEX, Kernel.COORD_CARTESIAN_3D,
				Kernel.COORD_SPHERICAL);
	}

	@Override
	public boolean isValidAt(int index) {
		boolean valid = true;
		GeoElement geo = getGeoAt(index);
		if (!(geo.isGeoPoint() || geo.isGeoVector())) {
			valid = false;
		}

		// check if fixed
		if (geo.isProtected(EventType.UPDATE)) {
			valid = false;
		}
		return valid;
	}

	private CoordStyle getCoordStyleAt(int index) {
		return (CoordStyle) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		CoordStyle geo0 = getCoordStyleAt(0);
		getListener()
				.setSelectedIndex(coordValues.indexOf(geo0.getToStringMode()));

	}

	@Override
	public List<String> getChoices(Localization loc) {
		return Arrays.asList(loc.getMenu("CartesianCoords"), // index 0
				loc.getMenu("PolarCoords"), // index 1
				loc.getMenu("ComplexNumber"), // index 2
				loc.getMenu("CartesianCoords3D"), // index 3
				loc.getMenu("Spherical")); // index 4
	}

	@Override
	protected void apply(int index, int value) {
		getCoordStyleAt(index).setMode(coordValues.get(value));
		getGeoAt(index).updateRepaint();
	}

	@Override
	public int getValueAt(int index) {
		// not used
		return 0;
	}

}
