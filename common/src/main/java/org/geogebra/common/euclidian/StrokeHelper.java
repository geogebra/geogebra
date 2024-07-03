package org.geogebra.common.euclidian;

import static org.geogebra.common.kernel.StringTemplate.xmlTemplate;

import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.geos.GeoElement;

public class StrokeHelper {

	/**
	 * returns the xml of the given strokes
	 * @param strokes strokes
	 * @return xmls
	 */
	public List<String> getStrokesXML(List<GeoElement> strokes) {
		return strokes.stream().map(stroke -> getXML(stroke)).collect(Collectors.toList());
	}

	/**
	 * getXml
	 * @param stroke stroke
	 * @return style xml
	 */
	public String getXML(GeoElement stroke) {
		return "<expression label=\"" + stroke.getLabelSimple() + "\" exp=\""
				+ stroke.getRedefineString(false, true, xmlTemplate)
				+ "\"/>\n" + stroke.getStyleXML();
	}
}