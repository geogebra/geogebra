package org.geogebra.common.io;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.geogebra.common.util.StringUtil;

public class ObjectLabelHandler implements DocHandler {

	private final ArrayList<String> labels = new ArrayList<>();

	@Override
	public void startDocument() {
		labels.clear();
	}

	@Override
	public void startElement(String tag, LinkedHashMap<String, String> h) {
		if ("element".equals(tag) && h.containsKey("label")) {
			labels.add(h.get("label"));
		}
	}

	@Override
	public void endElement(String tag) {
		// do not care
	}

	@Override
	public void endDocument() {
		// still do not care
	}

	@Override
	public void text(String str) throws XMLParseException {
		// really do not care
	}

	public String[] getObjectNames() {
		return labels.toArray(new String[0]);
	}

	/**
	 * @param string construction XML
	 * @return list of object names
	 */
	public static String[] findObjectNames(String string) {
		if (StringUtil.empty(string)) {
			return new String[0];
		}
		QDParser qd = new QDParser();
		ObjectLabelHandler handler = new ObjectLabelHandler();
		try {
			qd.parse(handler, new StringReader(string));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return handler.getObjectNames();
	}
}
