package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.algos.AlgoIntersectAbstract;
import geogebra.common.kernel.geos.GeoElement;

public class TrimmedIntersectionLinesModel extends BooleanOptionModel {
	public TrimmedIntersectionLinesModel(IBooleanOptionListener listener) {
		super(listener);
	}

	@Override
	public void updateProperties() {
		boolean equalVal = true;
		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
	
		for (int i = 1; i < getGeosLength(); i++) {
			temp = getGeoAt(0);
			// same object visible value
			if (geo0.getShowTrimmedIntersectionLines() != temp
					.getShowTrimmedIntersectionLines()) {
				equalVal = false;
				break;
			}

		}

		getListener().updateCheckbox(equalVal ? geo0.getShowTrimmedIntersectionLines(): false);

	}

	public void applyChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(0);
			geo.setShowTrimmedIntersectionLines(value);
			geo.getParentAlgorithm().getInput()[0]
					.setEuclidianVisible(!value);
			geo.getParentAlgorithm().getInput()[1]
					.setEuclidianVisible(!value);
			geo.getParentAlgorithm().getInput()[0].updateRepaint();
			geo.getParentAlgorithm().getInput()[1].updateRepaint();
			geo.updateRepaint();
		}	
	}
	
	@Override
	public boolean checkGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			if (!(getGeoAt(i).getParentAlgorithm() instanceof AlgoIntersectAbstract)) {
				return false;
			}
		}
		return true;
	}
}

