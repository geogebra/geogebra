package org.geogebra.common.util.lang.subtags;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LanguageSubtagRegistryParser {

	private static final String TYPE = "Type: ";
	private static final String SUBTAG = "Subtag: ";
	private static final String DESCRIPTION = "Description: ";

	private LanguageSubtagRegistryParser() {
	}

	public static List<Subtag> parse(BufferedReader text) throws IOException {
		String line;
		String subtag = null;
		String description = null;
		Type type = null;

		LinkedList<Subtag> subtags = new LinkedList<>();

		while ((line = text.readLine()) != null) {
			if (line.equals("%%")) {
				if (type != null && subtag != null) {
					subtags.add(new Subtag(type, subtag, description));
					type = null;
					subtag = null;
					description = null;
				}
			} else if (line.startsWith(TYPE)) {
				try {
					type = Type.valueOf(line.substring(TYPE.length()));
				} catch (IllegalArgumentException exception) {
					// ignore
				}
			} else if (line.startsWith(SUBTAG)) {
				subtag = line.substring(SUBTAG.length());
			} else if (line.startsWith(DESCRIPTION)) {
				description = line.substring(DESCRIPTION.length());
			}
		}

		return subtags;
	}
}
