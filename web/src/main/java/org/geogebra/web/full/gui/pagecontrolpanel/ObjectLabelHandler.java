package org.geogebra.web.full.gui.pagecontrolpanel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.geogebra.common.io.DocHandler;

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
	public void text(String str) throws Exception {
		// really do not care
	}

	public String[] getObjectNames() {
		return labels.toArray(new String[0]);
	}
}
