package org.geogebra.common.util.lang.subtags;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

public class LanguageSubtagRegistryParserTests {

	private static final String HEADER = "File-Date: 2023-05-11\n%%\n";
	private static final String SINGLE_ENTRY = "Type: language\n"
			+ "Subtag: aa\n"
			+ "Description: Afar\n"
			+ "Added: 2005-10-16";
	private static final String TWO_ENTRIES = "Type: language\n"
			+ "Subtag: aa\n"
			+ "Description: Afar\n"
			+ "Added: 2005-10-16\n"
			+ "%%\n"
			+ "Type: language\n"
			+ "Subtag: ab\n"
			+ "Description: Abkhazian\n"
			+ "Added: 2005-10-16\n"
			+ "Suppress-Script: Cyrl";
	private static final String LINE_CONTINUATION = "Type: variant\n"
			+ "Subtag: ao1990\n"
			+ "Description: Portuguese Language Orthographic Agreement of 1990 (Acordo\n"
			+ "  Ortográfico da Língua Portuguesa de 1990)\n"
			+ "Added: 2015-05-06\n"
			+ "Prefix: pt\n"
			+ "Prefix: gl\n"
			+ "Comments: Portuguese orthography conventions established in 1990 but\n"
			+ "  not brought into effect until 2009";
	private static final String COLON_IN_FIELD_BODY = "Type: language\n"
			+ "Subtag: jw\n"
			+ "Description: Javanese\n"
			+ "Added: 2005-10-16\n"
			+ "Deprecated: 2001-08-13\n"
			+ "Preferred-Value: jv\n"
			+ "Comments: published by error in Table 1 of ISO 639:1988";

	@Test
	public void testSingleEntry() {
		List<Record> records = null;
		try {
			records = parseString(HEADER + SINGLE_ENTRY);
		} catch (Exception e) {
			fail("It should not throw exception");
		}
		assertThat(records, hasSize(1));
		List<Field> fields = records.get(0).getFields();
		assertThat(fields, hasSize(4));
		Field field = fields.get(0);
		assertThat(field.getName(), is("Type"));
		assertThat(field.getBody(), is("language"));
	}

	@Test
	public void testTwoEntries() {
		List<Record> records = null;
		try {
			records = parseString(HEADER + TWO_ENTRIES);
		} catch (Exception e) {
			fail("It should not throw exception");
		}
		assertThat(records, hasSize(2));
		assertThat(records.get(0).getFields(), hasSize(4));
		assertThat(records.get(1).getFields(), hasSize(5));
	}

	@Test
	public void testContinuation() {
		List<Record> records = null;
		try {
			records = parseString(HEADER + LINE_CONTINUATION);
		} catch (Exception e) {
			fail("It should not throw exception, got " + e);
		}
		assertThat(records, hasSize(1));
		List<Field> fields = records.get(0).getFields();
		assertThat(fields.get(2).getBody(),
				is("Portuguese Language Orthographic Agreement of 1990 (Acordo\n"
						+ "  Ortográfico da Língua Portuguesa de 1990)"
				));
		assertThat(fields.get(6).getBody(),
				is("Portuguese orthography conventions established in 1990 but\n"
						+ "  not brought into effect until 2009"
				));
	}

	@Test
	public void testColonInFieldBody() {
		List<Record> records = null;
		try {
			records = parseString(HEADER + COLON_IN_FIELD_BODY);
		} catch (Exception e) {
			fail("It should not throw exception, got " + e);
		}
		assertThat(records.get(0).getFields().get(6).getBody(),
				is("published by error in Table 1 of ISO 639:1988"));

	}

	@Test
	public void testInvalidFormatThrowsException() {
		try {
			parseString("Invalid format");
			fail("This should not be reached, an exception should have been thrown");
		} catch (Exception e) {
			// Success
		}
	}

	private static List<Record> parseString(String string) throws Exception {
		return LanguageSubtagRegistryParser.parse(new BufferedReader(new StringReader(string)));
	}
}
