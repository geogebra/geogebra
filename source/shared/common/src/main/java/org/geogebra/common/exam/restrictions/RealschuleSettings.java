package org.geogebra.common.exam.restrictions;

import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;

public class RealschuleSettings implements RestorableSettings {
	private int coordFormat;
	private int gridType;
	private String xLabel;
	private String yLabel;
	private GeoNumberValue xDistance;
	private GeoNumberValue yDistance;
	private boolean equationChangeRestricted;

	@Override
	public void save(Settings settings) {
		coordFormat = settings.getGeneral().getCoordFormat();
		EuclidianSettings euclidian = settings.getEuclidian(1);
		gridType = euclidian.getGridType();
		String[] axesLabels = euclidian.getAxesLabels();
		xLabel = axesLabels[0];
		yLabel = axesLabels[1];
		xDistance = euclidian.getAxisNumberingDistance(0);
		yDistance = euclidian.getAxisNumberingDistance(1);
		equationChangeRestricted = settings.getAlgebra().isEquationChangeByDragRestricted();
	}

	@Override
	public void restore(Settings settings) {
		settings.getGeneral().setCoordFormat(coordFormat);
		EuclidianSettings euclidian = settings.getEuclidian(1);
		euclidian.setGridType(gridType);
		euclidian.setAxisLabel(0, xLabel);
		euclidian.setAxisLabel(1, yLabel);
		euclidian.setAxisNumberingDistance(0, xDistance);
		euclidian.setAxisNumberingDistance(1, yDistance);
		settings.getAlgebra().setEquationChangeByDragRestricted(equationChangeRestricted);
	}
}
