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

package org.geogebra.common.gui.view.algebra;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class SuggestionSolveForSymbolicTest extends BaseSymbolicTest {

	@Test
	public void testMultiVariableSolveOnFirstEquation() {
		GeoElement first = add("x + y = 0");
		add("y + 1 = 0");
		SuggestionSolveForSymbolic.get(first).execute(first);
		String solveOutput = first.getKernel().getConstruction().getLastGeoElement()
				.toOutputValueString(StringTemplate.algebraTemplate);
		assertThat(solveOutput, is(oneOf("{{y = -1, x = 1}}", "{{x = 1, y = -1}}")));
		List<String> values = Arrays.stream(app.getGgbApi().getAllObjectNames())
				.map(this::lookup).map(GeoElement::getDefinitionForEditor).collect(
				Collectors.toList());
		assertThat(values, is(oneOf(asList("eq1: x+y=0", "eq2: y+1=0", "Solve({eq1,eq2},{x,y})"),
				asList("eq1: x+y=0", "eq2: y+1=0", "Solve({eq1,eq2},{y,x})"))));
	}

	@Test
	public void incompleteVariableSets() {
		GeoElement first = add("x + y = 1");
		add("y + z = 2");
		add("z + x = 3");
		SuggestionSolveForSymbolic.get(first).execute(first);
		String solveOutput = first.getKernel().getConstruction().getLastGeoElement()
				.toOutputValueString(StringTemplate.algebraTemplate);
		assertThat(solveOutput, is("{{x = 1, y = 0, z = 2}}"));
	}

	@Test
	public void incompleteVariableSetsInvalid() {
		GeoElement first = add("x + y = 1");
		add("y + z = 2");
		add("z + x + a = 3");
		assertNull(SuggestionSolveForSymbolic.get(first));
	}

	@Test
	public void incompleteVariableSetsShouldPreferPrevious() {
		add("y + z = 2");
		add("x + y = 3");
		GeoElement first = add("x + z = 1");
		add("x + y = 5");
		add("y + z = 3");
		SuggestionSolveForSymbolic.get(first).execute(first);
		String solveOutput = first.getKernel().getConstruction().getLastGeoElement()
				.toOutputValueString(StringTemplate.algebraTemplate);
		assertThat(solveOutput, is("{{x = 1, y = 2, z = 0}}"));
	}

	@Test
	public void incompleteVariableSetsShouldTakeBothPreviousAndNext() {
		add("a + b = 2");
		add("x + y = 3");
		GeoElement first = add("x + z = 1");
		add("x + 2y = 5");
		add("a + b = 3");
		SuggestionSolveForSymbolic.get(first).execute(first);
		String solveOutput = first.getKernel().getConstruction().getLastGeoElement()
				.toOutputValueString(StringTemplate.algebraTemplate);
		assertThat(solveOutput, is("{{x = 1, y = 2, z = 0}}"));
	}
}