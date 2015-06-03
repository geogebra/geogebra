package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.main.Localization;

public class LineEqnModel extends MultipleOptionsModel {


	private List<Integer> eqnValues;

	public LineEqnModel() {
		eqnValues = Arrays.asList(GeoLine.EQUATION_IMPLICIT,
				GeoLine.EQUATION_EXPLICIT,
				GeoLine.PARAMETRIC);
	}

	@Override
	public boolean isValidAt(int index) {
		boolean valid = true;
		Object geo = getObjectAt(index);
		if (!(geo instanceof GeoLine)
				|| geo instanceof GeoSegment) {
			valid = false;
		}
	
		return valid;
	}
	
	private GeoLine getLineAt(int index) {
		return (GeoLine)getObjectAt(index);
	}
	
	@Override
	public void updateProperties() {
		int value0 = getValueAt(0);
		boolean equalMode = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (getValueAt(i) != value0) {
				equalMode = false;
			}
		}
		
		getListener().setSelectedIndex(equalMode ? 
				eqnValues.indexOf(value0): -1);
		
	}
	@Override
	public List<String> getChoiches(Localization loc) {
		return Arrays.asList(loc.getPlain("ImplicitLineEquation"), // index 0
				loc.getPlain("ExplicitLineEquation"), // index 1
				loc.getPlain("ParametricForm") // index 2
				);
	}

	@Override
	protected void apply(int index, int value) {
		getLineAt(index).setMode(eqnValues.get(value));
		getGeoAt(index).updateRepaint();
	
	}

	@Override
	public int getValueAt(int index) {
		return getLineAt(index).getMode();
	}

}
