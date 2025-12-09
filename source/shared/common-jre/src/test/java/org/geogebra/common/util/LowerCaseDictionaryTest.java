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

package org.geogebra.common.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class LowerCaseDictionaryTest {

	private LowerCaseDictionary dict;

	@Before
	public void setup() {
		dict = new LowerCaseDictionary();
	}

	@Test
	public void testCompletions() {
		dict.addEntry("b\u00e4cd");
		assertThat(completionOf("ac"), equalTo("b[\u00e4c]d"));
		assertThat(completionOf("bac"), equalTo("[b\u00e4c]d"));
		assertThat(completionOf("ba"), equalTo("[b\u00e4]cd"));
	}

	private String completionOf(String content) {
		MatchedString match = dict.getCompletions(content).get(0);
		return String.format("%s[%s]%s", (Object[]) match.getParts());
	}

}
