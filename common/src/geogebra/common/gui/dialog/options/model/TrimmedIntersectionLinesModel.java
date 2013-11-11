package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.algos.AlgoIntersectAbstract;
import geogebra.common.kernel.geos.GeoElement;

public class TrimmedIntersectionLinesModel extends BooleanOptionModel {
	public TrimmedIntersectionLinesModel(IBooleanOptionListener listener) {
		super(listener);
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
	@Override
	public boolean isValidAt(int index) {
		return (getGeoAt(index).getParentAlgorithm() instanceof AlgoIntersectAbstract);	
		}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).getShowTrimmedIntersectionLines();
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
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

