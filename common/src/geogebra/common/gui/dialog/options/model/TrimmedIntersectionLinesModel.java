package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.algos.AlgoIntersectAbstract;
import geogebra.common.kernel.geos.GeoElement;

public class TrimmedIntersectionLinesModel extends OptionsModel {
	public interface ITrimmedIntersectionLinesListener {
		void updateCheckbox(boolean value);
	}
	private ITrimmedIntersectionLinesListener listener;
	
	public TrimmedIntersectionLinesModel(ITrimmedIntersectionLinesListener listener) {
		this.listener = listener;
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

		listener.updateCheckbox(equalVal);

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

