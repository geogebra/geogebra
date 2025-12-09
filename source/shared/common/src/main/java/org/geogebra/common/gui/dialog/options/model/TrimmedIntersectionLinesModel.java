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
