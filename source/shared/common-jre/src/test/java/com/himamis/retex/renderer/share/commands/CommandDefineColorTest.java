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

package com.himamis.retex.renderer.share.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

class CommandDefineColorTest {

	@BeforeEach
	void setUp() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
	}

	@Test
	@Issue("APPS-7807")
	void testHslaRejectsThreeComponents() {
		assertThrows(ParseException.class, () -> CommandDefinecolor.getColor(
				new TeXParser("[hsla]{180,1,0.5}")));
	}

	@Test
	@Issue("APPS-7807")
	void testHslaAcceptsFourComponents() {
		assertDoesNotThrow(() -> CommandDefinecolor.getColor(
				new TeXParser("[hsla]{180,1,0.5,1}")));
	}
}
