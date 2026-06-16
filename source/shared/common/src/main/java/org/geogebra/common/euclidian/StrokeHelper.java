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

package org.geogebra.common.euclidian;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;

public class StrokeHelper {

	/**
	 * returns the xml of the given strokes
	 * @param strokes strokes
	 * @return xmls
	 */
	public List<String> getStrokesXML(List<GeoElement> strokes) {
		return strokes.stream().map(this::getXML).collect(Collectors.toList());
	}

	/**
	 * @param stroke stroke
	 * @return full xml
	 */
	public String getXML(GeoElement stroke) {
		AlgoElement parentAlgorithm = stroke.getParentAlgorithm();
		return parentAlgorithm != null ? parentAlgorithm.getXML() : stroke.getXML();
	}
}
