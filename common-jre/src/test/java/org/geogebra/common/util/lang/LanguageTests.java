package org.geogebra.common.util.lang;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.geogebra.common.util.lang.subtags.LanguageSubtagRegistryParser;
import org.geogebra.common.util.lang.subtags.Subtag;
import org.junit.Test;

public class LanguageTests {

	@Test
	public void testLanguageTags() throws IOException {
		List<Subtag> acceptedSubtags = parseTags();
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
				Optional<Subtag> acceptedSubtag = findSubtag(acceptedSubtags, subtag);
				assertTrue(subtag + " must be present in accepted subtags",
						acceptedSubtag.isPresent());
			}
		}
	}

	private List<Subtag> parseTags() throws IOException {
		try (InputStream in = getClass().getResourceAsStream("language-subtag-registry.txt")) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.US_ASCII));
			return LanguageSubtagRegistryParser.parse(reader);
		}
	}

	private Optional<Subtag> findSubtag(List<Subtag> subtags, String subtag) {
		return subtags.stream().filter(s -> s.getSubtag().equals(subtag)).findFirst();
	}
}
