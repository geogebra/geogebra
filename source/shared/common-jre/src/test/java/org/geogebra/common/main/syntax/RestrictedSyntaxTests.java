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
 
package org.geogebra.common.main.syntax;

import static org.geogebra.common.kernel.commands.Commands.BinomialDist;
import static org.geogebra.common.kernel.commands.Commands.Sum;
import static org.geogebra.common.main.MyError.Errors.IllegalArgument;
import static org.geogebra.common.main.MyError.Errors.IllegalArgumentNumber;
import static org.geogebra.common.main.syntax.Syntax.ArgumentMatcher.isNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.MyError;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RestrictedSyntaxTests extends BaseAppTestSetup {
	@BeforeEach
	public void appSetup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testAllowedSyntax() throws ParseException {
		Command command = parseCommandExpression("Sum({1, 2, 3})");
		Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
				Sum, Set.of(
						Syntax.of(Sum, GeoElement::isGeoList), // <-- matching (allowed) syntax
						Syntax.of(Sum, isNumber(), isNumber(), isNumber()),
						Syntax.of(Sum, isNumber(), isNumber())));
		assertDoesNotThrow(() -> Syntax.checkRestrictedSyntaxes(
				allowedSyntaxesForRestrictedCommands, command, commandProcessorOf(command)));
	}

	@Test
	public void testWrongNumberOfArguments() throws ParseException {
		Command command = parseCommandExpression("Sum({1, 2, 3})");
		Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
				Sum, Set.of(
						Syntax.of(Sum, isNumber(), isNumber()),
						Syntax.of(Sum, isNumber(), isNumber(), isNumber())));

		MyError exception = assertThrows(MyError.class, () -> Syntax.checkRestrictedSyntaxes(
				allowedSyntaxesForRestrictedCommands, command, commandProcessorOf(command)));
		assertEquals(IllegalArgumentNumber, exception.getErrorType());
	}

	@Test
	public void testWrongArgument() throws ParseException {
		Command command = parseCommandExpression("Sum({1, 2, 3})");
		Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
				Sum, Set.of(
						Syntax.of(Sum, isNumber()), // <-- closest candidate, wrong argument type
						Syntax.of(Sum, isNumber(), isNumber()),
						Syntax.of(Sum, isNumber(), isNumber(), isNumber())));

		MyError exception = assertThrows(MyError.class, () -> Syntax.checkRestrictedSyntaxes(
				allowedSyntaxesForRestrictedCommands, command, commandProcessorOf(command)));
		assertEquals(IllegalArgument, exception.getErrorType());
	}

	@Test
	public void testWrongArgumentWithClosestOption() throws ParseException {
		Command command = parseCommandExpression("BinomialDist(10, 0.2, {1, 2, 3})");
		Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
				BinomialDist, Set.of(
						Syntax.of(BinomialDist, // Matching first argument
								isNumber(), GeoElement::isGeoFunction, isNumber()),
						Syntax.of(BinomialDist, // Matching first argument
								GeoElement::isGeoFunction, GeoElement::isGeoBoolean, isNumber()),
						Syntax.of(BinomialDist, // Matching first and second arguments (closest)
								isNumber(), isNumber(), GeoElement::isGeoFunction)));

		MyError exception = assertThrows(MyError.class, () -> Syntax.checkRestrictedSyntaxes(
				allowedSyntaxesForRestrictedCommands, command, commandProcessorOf(command)));
		assertEquals(IllegalArgument, exception.getErrorType());
		assertThat(exception.getMessage(), containsString("Illegal argument: {1, 2, 3}"));
	}

	private Command parseCommandExpression(String expression) throws ParseException {
		return (Command) getKernel().getParser().parseCmdExpression(expression).getLeft();
	}

	private CommandProcessor commandProcessorOf(Command command) {
		return getCommandDispatcher().commandTableSwitch(command);
	}
}
