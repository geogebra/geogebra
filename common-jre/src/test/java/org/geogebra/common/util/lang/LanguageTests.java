package org.geogebra.common.util.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.geogebra.common.util.lang.subtags.LanguageSubtagRegistryParser;
import org.geogebra.common.util.lang.subtags.Subtag;
import org.junit.Assert;
import org.junit.Test;

public class LanguageTests {

	@Test
	public void testLanguageTags() throws IOException {
		List<Subtag> acceptedSubtags = parseTags();
		for (Language language : Language.values()) {
			String tag = language.toLanguageTag();
			for (String subtag : tag.split("-")) {
				Optional<Subtag> acceptedSubtag = findSubtag(acceptedSubtags, subtag);
				Assert.assertTrue(subtag + " must be present in accepted subtags",
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
