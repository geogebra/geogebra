package com.himamis.retex.editor.share.util;

import static com.himamis.retex.editor.share.util.CommandParser.parseCommand;
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