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

import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.main.undo.ConstructionActionExecutor;

class UndoItem {
	private final String previousContent;
	private final GeoElement geo;
	private final boolean isXml;
	private final String previousLabel;

	UndoItem(GeoElement geo, MoveMode moveMode) {
		this.geo = geo;
		isXml = (geo instanceof Locateable && moveMode != MoveMode.NUMERIC)
				|| geo instanceof GeoWidget || geo instanceof GeoInline;
		previousContent = content();
		previousLabel = geo.getLabelSimple();
	}

	private String getDefinition() {
		return geo.getLabelSimple() + ":"
				+ geo.getRedefineString(false, true, StringTemplate.xmlTemplate);
	}

	String content() {
		if (geo.isPointOnPath() || geo.isPointInRegion()) {
			return ConstructionActionExecutor.SET + geo.getLabelSimple() + "="
					+ geo.toValueString(StringTemplate.xmlTemplate);
		}
		return isXml ? geo.getStyleXML() : getDefinition();
	}

	String previousContent() {
		return previousContent;
	}

	String getLabel() {
		return geo.getLabelSimple();
	}

	boolean hasGeo(GeoElement geo) {
		return this.geo == geo;
	}

	String getPreviousLabel() {
		return previousLabel;
	}
}
