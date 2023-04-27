package org.geogebra.common.euclidian;

import java.util.stream.Collectors;

import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoWidget;

class UndoItem {
	private final String previousContent;
	private final GeoElement geo;
	private final boolean isXml;

	public UndoItem(GeoElement geo) {
		this.geo = geo;
		isXml = geo instanceof Locateable || geo instanceof GeoWidget || geo instanceof GeoInline
				|| geo instanceof GeoLocusStroke;
		previousContent = content();
	}

	private String getDefinition() {
		return geo.getLabelSimple() + ":"
				+ geo.getRedefineString(false, true, StringTemplate.xmlTemplate);
	}

	public String content() {
		return isXml ?
				(geo.getParentAlgorithm() != null)?
						geo.getParentAlgorithm().getXML()
						: geo.getXML()
				: getDefinition();
	}

	public String previousContent() {
		return previousContent;
	}

	public String getLabel() {
		return geo.getLabelSimple();
	}
}
