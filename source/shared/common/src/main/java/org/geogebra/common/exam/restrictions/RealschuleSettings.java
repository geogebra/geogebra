/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
	private boolean engineeringNotationEnabled;

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
		engineeringNotationEnabled = settings.getAlgebra().isEngineeringNotationEnabled();
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
		settings.getAlgebra().setEngineeringNotationEnabled(engineeringNotationEnabled);
	}
}
