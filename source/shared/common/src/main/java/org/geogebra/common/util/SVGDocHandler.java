package org.geogebra.common.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.geogebra.common.io.DocHandler;
import org.geogebra.common.io.XMLParseException;
import org.geogebra.common.io.XMLStringBuilder;

/**
 * Parse a single svg element to get width, height and viewBox
 * 
 * @author Zbynek
 */
public class SVGDocHandler implements DocHandler {

	private Map<String, String> attrs = new HashMap<>();

	@Override
	public void startElement(String tag, LinkedHashMap<String, String> h) {
		// copy the map: h is emptied after parsing
		for (Entry<String, String> entry : h.entrySet()) {
			this.attrs.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void endElement(String tag) throws XMLParseException {
		// just one tag, ignore
	}

	@Override
	public void startDocument() throws XMLParseException {
		// just one tag, ignore
	}

	@Override
	public void endDocument() throws XMLParseException {
		// just one tag, ignore
	}

	@Override
	public void text(String str) throws XMLParseException {
		// just one tag, ignore
	}

	/**
	 * @return svg header tag with fixed dimensions
	 */
	public String getSVGTag() {
		XMLStringBuilder sb = new XMLStringBuilder(new StringBuilder(50));
		sb.startOpeningTag("svg", 0);
		for (Entry<String, String> entry : attrs.entrySet()) {
			if (!"width".equals(entry.getKey())
					&& !"height".equals(entry.getKey())
					&& !"viewBox".equals(entry.getKey())) {
				appendAttr(sb, entry.getKey(), entry.getValue());
			}
		}
		appendAttr(sb, "viewBox", getViewBox());
		appendAttr(sb, "height", getHeight());
		appendAttr(sb, "width", getWidth());
		sb.endTag();
		return sb.toString().trim();
	}

	private String getWidth() {
		return attrs.get("width") == null ? viewportDiff(0, 2)
				: attrs.get("width");
	}

	private String viewportDiff(int i, int j) {
		if (getViewBox() == null) {
			return null;
		}
		String[] params = getViewBox().trim().split(" ");
		return String.valueOf(Double.parseDouble(params[j]) - Double.parseDouble(params[i]));
	}

	private String getHeight() {
		return attrs.get("height") == null ? viewportDiff(1, 3)
				: attrs.get("height");
	}

	private String getViewBox() {
		return attrs.get("viewBox");
	}

	private static void appendAttr(XMLStringBuilder sb, String key, String value) {
		if (value != null) {
			sb.attr(key, value);
		}
	}

	/**
	 * Make sure the SVG document does not preserve aspect ratio.
	 */
	public void removeAspectRatio() {
		attrs.put("preserveAspectRatio", "none");
	}
}
