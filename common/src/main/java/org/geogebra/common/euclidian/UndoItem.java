package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoWidget;

class UndoItem {
	private final String previousContent;
	private final GeoElement geo;
	private final boolean isXml;

	public UndoItem(GeoElement geo) {
		this.geo = geo;
		isXml = geo instanceof Locateable || geo instanceof GeoWidget || geo instanceof GeoInline;
		previousContent = content();
	}

	private String getDefinition() {
		return geo.getLabelSimple() + ":"
				+ geo.getRedefineString(false, true, StringTemplate.xmlTemplate);
	}

	public String content() {
		return isXml ? geo.getStyleXML() : getDefinition();
	}

	public String previousContent() {
		return previousContent;
	}

	public String getLabel() {
		return geo.getLabelSimple();
	}

	public boolean hasGeo(GeoElement geo) {
		return this.geo == geo;
	}
}
