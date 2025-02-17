package org.geogebra.common.exam.restrictions;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;

public class RealschuleSettings implements RestorableSettings {
	private int coordFormat;
	private int gridType;
	private String xLabel;
	private String yLabel;
	private final Map<Integer, Integer> pointStyles = new HashMap<>();
	private boolean equationChangeRestricted;

	@Override
	public void save(Settings settings, ConstructionDefaults defaults) {
		coordFormat = settings.getGeneral().getCoordFormat();
		EuclidianSettings euclidian = settings.getEuclidian(1);
		gridType = euclidian.getGridType();
		String[] axesLabels = euclidian.getAxesLabels();
		xLabel = axesLabels[0];
		yLabel = axesLabels[1];
		for (int index: ConstructionDefaults.POINT_INDICES) {
			GeoPointND point = (GeoPointND) defaults.getDefaultGeo(index);
			if (point != null) {
				pointStyles.put(index, point.getPointStyle());
			}
		}
		equationChangeRestricted = settings.getAlgebra().isEquationChangeByDragRestricted();
	}

	@Override
	public void restore(Settings settings, ConstructionDefaults defaults) {
		settings.getGeneral().setCoordFormat(coordFormat);
		EuclidianSettings euclidian = settings.getEuclidian(1);
		euclidian.setGridType(gridType);
		euclidian.setAxisLabel(0, xLabel);
		euclidian.setAxisLabel(1, yLabel);
		for (Map.Entry<Integer, Integer> entry: pointStyles.entrySet()) {
			GeoPointND defaultGeo = (GeoPointND) defaults.getDefaultGeo(entry.getKey());
			if (defaultGeo != null) {
				defaultGeo.setPointStyle(entry.getValue());
			}
		}
		settings.getAlgebra().setEquationChangeByDragRestricted(equationChangeRestricted);
	}
}
