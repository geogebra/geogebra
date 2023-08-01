package org.geogebra.common.util.lang;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.geogebra.common.util.lang.subtags.Field;
import org.geogebra.common.util.lang.subtags.LanguageSubtagRegistryParser;
import org.geogebra.common.util.lang.subtags.Record;
import org.junit.BeforeClass;
import org.junit.Test;

public class LanguageTests {

	private static List<Record> acceptedSubtags;

	@BeforeClass
	public static void setup() throws Exception {
		acceptedSubtags = parseRecords();
	}

	@Test
	public void testLanguageTags() {
		for (Language language : Language.values()) {
			String tag = language.toLanguageTag();
			assertNotNull(tag);
			assertNotEquals("", tag);
			for (String subtag : tag.split("-")) {
				if ("XV".equals(subtag)) {
					// XV presents a special case, stands for valencia region.
					// Android Gradle Plugin cannot parse the correct valencia variant subtag.
					// We stick to the original version by using the non-existing XV region.
					continue;
				}
				Optional<Record> acceptedSubtag = findRecord(acceptedSubtags, subtag);
				assertTrue(subtag + " must be present in accepted subtags",
						acceptedSubtag.isPresent());
			}
		}
	}

	private static List<Record> parseRecords() throws Exception {
		try (InputStream in = LanguageTests.class.getResourceAsStream(
				"language-subtag-registry.txt")) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.US_ASCII));
			return LanguageSubtagRegistryParser.parse(reader);
		}
	}

	private static Optional<Record> findRecord(List<Record> records, String subtag) {
		return records.stream().filter(r -> hasSubtag(r, subtag)).findFirst();
	}

	private static boolean hasSubtag(Record record, String subtag) {
		return record.fields.stream().anyMatch(f -> isSubtag(f, subtag));
	}

	private static boolean isSubtag(Field field, String subtag) {
		return field.name.equals("Subtag") && field.body.equals(subtag);
	}
}
