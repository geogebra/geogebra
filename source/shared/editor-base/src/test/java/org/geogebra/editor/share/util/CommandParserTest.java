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

package org.geogebra.editor.share.util;

import static org.geogebra.editor.share.util.CommandParser.parseCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CommandParserTest {

	@Test
	public void testRegularCommandSyntaxes() {
		List<String> constructionStep = Collections.singletonList("ConstructionStep");
		assertEquals(constructionStep, parseCommand("ConstructionStep( )"));

		List<String> midpoint = Arrays.asList("Midpoint", "Point", "Point");
		assertEquals(midpoint, parseCommand("Midpoint(<Point>,<Point>)"));
		assertEquals(midpoint, parseCommand("Midpoint( <Point>, <Point> )"));
	}

	@Test
	public void testTrickySyntaxes() {
		List<String> locus = Arrays.asList("Locus", "f(x, y)", "Point");
		assertEquals(locus, parseCommand("Locus(<f(x, y)>, <Point>)"));

		List<String> join = Arrays.asList("Join", "List", "List", "...");
		assertEquals(join, parseCommand("Join(<List>, <List>, ...)"));

		List<String> uniform = Arrays.asList("Uniform", "Lower Bound",
				"Upper Bound", "x", "Boolean Cumulative");
		assertEquals(uniform,
				parseCommand("Uniform( <Lower Bound>, <Upper Bound>, x, <Boolean Cumulative> )"));
	}

}