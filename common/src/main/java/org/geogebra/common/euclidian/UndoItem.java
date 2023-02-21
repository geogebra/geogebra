package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoWidget;

class UndoItem {
	private final String previousContent;
	private final GeoElement geo;
	private final boolean isXml;

	public UndoItem(GeoElement geo) {
		this.geo = geo;
		isXml = geo instanceof Locateable || geo instanceof GeoWidget;
		previousContent = content();
	}

	private String getDefintion() {
		return geo.getLabelSimple() + ":"
				+ geo.getRedefineString(false, true, StringTemplate.xmlTemplate);
	}

	public String content() {
		return isXml ? geo.getXML() : getDefintion();
	}

	public String previousContent() {
		return previousContent;
	}
}
