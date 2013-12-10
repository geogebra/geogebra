package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.CoordStyle;
import geogebra.common.main.Localization;

import java.util.Arrays;
import java.util.List;

public class CoordsModel extends MultipleOptionsModel {


	private List<Integer> coordValues;
	public CoordsModel(IComboListener listener) {
		super(listener);
		coordValues = Arrays.asList(Kernel.COORD_CARTESIAN,
				Kernel.COORD_POLAR,
				Kernel.COORD_COMPLEX,
				Kernel.COORD_CARTESIAN_3D,
				Kernel.COORD_SPHERICAL);
	}

	@Override
	public boolean isValidAt(int index) {
		boolean valid = true;
		GeoElement geo = getGeoAt(index);
		if (!(geo.isGeoPoint() || geo instanceof GeoVector)) {
			valid = false;
		}

		// check if fixed
		if (geo.isFixed()) {
			valid = false;
		}
		return valid;
	}
	
	private CoordStyle getCoordStyleAt(int index) {
		return (CoordStyle)getObjectAt(index);
	}
	@Override
	public void updateProperties() {
		CoordStyle geo0 = getCoordStyleAt(0);
		getListener().setSelectedIndex(coordValues.indexOf(geo0.getMode()));
		
	}
	@Override
	public List<String> getChoiches(Localization loc) {
		return Arrays.asList(loc.getPlain("CartesianCoords"), // index 0
				loc.getPlain("PolarCoords"), // index 1
				loc.getPlain("ComplexNumber"), // index 2
				loc.getPlain("CartesianCoords3D"), // index 3
				loc.getPlain("Spherical")); // index 4
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
