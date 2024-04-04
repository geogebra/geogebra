package org.geogebra.common.io;

import java.util.LinkedHashMap;
import java.util.Map;

import org.geogebra.common.util.StringUtil;

/**
 * Represents a single (opening or closing) tag
 */
public class ParsedXMLTag {
	public final String name;
	public final LinkedHashMap<String, String> attributes;
	public final boolean open;

	ParsedXMLTag(String eName, LinkedHashMap<String, String> attrs, boolean open) {
		this.name = eName;
		this.attributes = attrs == null ? null : new LinkedHashMap<>(attrs);
		this.open = open;
	}

	/**
	 * Append to string builder
	 * @param sb builder
	 */
	public void appendTo(StringBuilder sb) {
		if (!open) {
			sb.append("</").append(name).append(">\n");
			return;
		}
		sb.append("<").append(name);
		appendAttrs(sb);
		sb.append(">\n");
	}

	/**
	 * Assuming this is an open tag, append it as self-closing
	 * @param sb builder
	 */
	public void appendSelfClosing(StringBuilder sb) {
		sb.append("<").append(name);
		appendAttrs(sb);
		sb.append("/>\n");
	}

	private void appendAttrs(StringBuilder sb) {
		for (Map.Entry<String, String> entry: attributes.entrySet()) {
			sb.append(' ').append(entry.getKey()).append("=\"");
			StringUtil.encodeXML(sb, entry.getValue());
			sb.append('\"');
		}
	}

}
