package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.algos.AlgoIntersectAbstract;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class TrimmedIntersectionLinesModel extends BooleanOptionModel {
	public TrimmedIntersectionLinesModel(IBooleanOptionListener listener,
			App app) {
		super(listener, app);
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index)
				.getParentAlgorithm() instanceof AlgoIntersectAbstract;
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).getShowTrimmedIntersectionLines();
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setShowTrimmedIntersectionLines(value);
		geo.getParentAlgorithm().getInput()[0].setEuclidianVisible(!value);
		geo.getParentAlgorithm().getInput()[1].setEuclidianVisible(!value);
		geo.getParentAlgorithm().getInput()[0].updateRepaint();
		geo.getParentAlgorithm().getInput()[1].updateRepaint();
		geo.updateRepaint();

	}

	@Override
	public String getTitle() {
		return "ShowTrimmed";
	}
}
