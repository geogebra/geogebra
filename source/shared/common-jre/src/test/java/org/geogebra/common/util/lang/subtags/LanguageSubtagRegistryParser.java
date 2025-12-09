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

package org.geogebra.common.util.lang.subtags;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Parses the Language Subtag Registry. The complete format is defined in RFC 4646, section 3.
 * @see <a href="https://www.iana.org/assignments/language-subtag-registry/language-subtag-registry">Language Subtag Registry hosted by IANA</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4646.txt">RFC 4646</a>
 */
public class LanguageSubtagRegistryParser {

	private static final String STARTSWITH_WHITESPACE = "^[\t ]+.*";

	private LanguageSubtagRegistryParser() {
	}

	/**
	 * Parses the Language Subtag Registry.
	 * @param reader a buffered reader
	 * @return list of records
	 * @throws IOException if an I/O error occurs
	 * @throws Exception if format is not recognized
	 */
	public static List<Record> parse(BufferedReader reader) throws Exception {
		String line;

		ArrayList<Field> fields = new ArrayList<>();
		LinkedList<Record> records = new LinkedList<>();

		// First line is date
		Field date = parseField(reader.readLine());
		if (!"File-Date".equals(date.name)) {
			throw new Exception(
					"First line should contain field File-Date, found " + date.name);
		}
		// Second line is %%
		String secondLine = reader.readLine();
		if (!"%%".equals(secondLine)) {
			throw new Exception("Second line must be %%, found " + reader.readLine());
		}

		while ((line = reader.readLine()) != null) {
			if (line.equals("%%")) {
				records.add(new Record(fields));
				fields = new ArrayList<>();
			} else if (line.matches(STARTSWITH_WHITESPACE)) {
				if (fields.size() >= 1) {
					Field lastField = fields.remove(fields.size() - 1);
					Field newField =
							new Field(lastField.name, lastField.body + "\n" + line);
					fields.add(newField);
				} else {
					throw new Exception("Invalid format");
				}
			} else {
				fields.add(parseField(line));
			}
		}
		if (fields.size() != 0) {
			// Last record might not end in %%
			records.add(new Record(fields));
		}
		return records;
	}

	private static Field parseField(String line) throws Exception {
		String[] field = line.split(" *: *", 2);
		if (field.length == 2) {
			return new Field(field[0], field[1]);
		} else {
			throw new Exception("Line must be of format 'NAME : BODY', found " + line);
		}
	}
}
