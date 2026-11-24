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

package org.geogebra.common.kernel.arithmetic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PiecewiseEditorTemplateTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		setupApp(SuiteSubApp.GRAPHING);
	}

	@AfterEach
	void teardown() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"f(x)=If(x<1, x)							-> f(x)=$piecewise(x,x<1)",
				"f(x)=x, x<1								-> f(x)=$piecewise(x,x<1)",
				"f(x)=If(x<1, x, 4)							-> f(x)=$piecewise(x,x<1,4)",
				"f(x)=If(x<0, 2x+5, If(x<3, 3, -x))			-> f(x)=$piecewise(2 x+5,x<0,3,x<3,-x)",
				"f(x)=If(x<0, 2x+5, If(x<3, 3))				-> f(x)=$piecewise(2 x+5,x<0,3,x<3)",
				"f(x)=If(x<0, 1, x<1, 2, x<2, 3, 4)			-> f(x)=$piecewise(1,x<0,2,x<1,3,x<2,4)",
				"f(x)=If(x<0, 1, x<1, 2)					-> f(x)=$piecewise(1,x<0,2,x<1)",
				"f(x)=If(x<0, If(x<-1, 1, 2), 3)			-> f(x)=$piecewise(If(x<-1, 1, 2),x<0,3)",
				"f(x)=If(x<0, nroot(x, 3), 3)				-> f(x)=$piecewise(nroot(x,3),x<0,3)",
			})
	void conditionalFunctionsShouldBeEditedAsPiecewise(String input, String editorDefinition) {
		GeoElement element = evaluateGeoElement(input);
		assertEquals(editorDefinition, element.getDefinitionForEditor());
	}

	@Test
	void dependentConditionalFunctionsShouldBeEditedAsPiecewise() {
		evaluate("a=2");
		GeoElement function = evaluateGeoElement("f(x)=If(x<a, x, a)");
		assertEquals("f(x)=$piecewise(x,x<a,a)", function.getDefinitionForEditor());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"f(x)=x+2									-> f(x)=x+2",
				"b=If(a<1, 2, 3)							-> b=If(a<1,2,3)",
				"c=If(a==1, \"Matthew\", a==2, \"Larry\", \"Alex\")"
						+ "	-> c=If(a≟1,\"Matthew\",a≟2,\"Larry\",\"Alex\")",
				"f(x,y)=If(x<y, x, y)						-> f(x,y)=If(x<y, x, y)",
			})
	void otherObjectsShouldKeepTheirSyntax(String input, String editorDefinition) {
		evaluate("a=2");
		GeoElement element = evaluateGeoElement(input);
		assertEquals(editorDefinition, element.getDefinitionForEditor());
	}

	@Test
	void piecewiseSyntaxShouldOnlyBeUsedForTheEditor() {
		GeoElement function = evaluateGeoElement("f(x)=If(x<1, x, 4)");
		assertEquals("f(x) = If(x < 1, x, 4)", function.getDefinitionForInputBar());
		assertEquals("If[x < 1, x, 4]", function.toValueString(StringTemplate.xmlTemplate));
		assertFalse(function
				.getLaTeXAlgebraDescription(true, StringTemplate.latexTemplate)
				.contains("piecewise"));
	}

	@Test
	void piecewiseSyntaxShouldBeAvoidableForEditorTemplate() {
		GeoElement function = evaluateGeoElement("f(x)=If(x<1, x, 4)");
		assertEquals(
				"If(x<1, x, 4)",
				function.getDefinitionNoLabel(
						StringTemplate.editorTemplate.deriveWithoutPiecewiseTemplate()));
	}

	@Test
	void piecewiseSyntaxShouldNotBeUsedWithoutPreviewFeature() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
		GeoElement function = evaluateGeoElement("f(x)=If(x<1, x, 4)");
		assertEquals("f(x)=If(x<1, x, 4)", function.getDefinitionForEditor());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"f(x)=$piecewise(x)							-> f(x) = x",
				"f(x)=$piecewise(x,x<1)						-> f(x) = If(x < 1, x)",
				"f(x)=$piecewise(x,x<1,4)					-> f(x) = If(x < 1, x, 4)",
				"f(x)=$piecewise(x,x<1,4,x>3)				-> f(x) = If(x < 1, x, x > 3, 4)",
				"f(x)=$piecewise(2x+5,x<0,3,x<3,-x)			-> f(x) = If(x < 0, 2x + 5, x < 3, 3, -x)",
				"f(x)=2 $piecewise(x,x<1,4)					-> f(x) = 2If(x < 1, x, 4)",
				"f(x)=$piecewise(x,x<1,$piecewise(1,x<5,2))	-> f(x) = If(x < 1, x, If(x < 5, 1, 2))",
			})
	void piecewiseSyntaxShouldEvaluateLikePlainInput(String input, String definition) {
		GeoElementND function = evaluateGeoElement(input);
		assertEquals(GeoFunction.class, function.getClass());
		assertEquals(definition, ((GeoElement) function).getDefinitionForInputBar());
	}

	@Test
	void singleRestrictedCaseShouldEvaluateLikeCompactNotation() {
		GeoFunction compact = evaluateGeoElement("f(x)=x, x<1");
		GeoFunction piecewise = evaluateGeoElement("g(x)=$piecewise(x,x<1)");
		assertEquals(
				compact.getFunctionExpression().getOperation(),
				piecewise.getFunctionExpression().getOperation());
		assertEquals(
				compact.getFormulaString(StringTemplate.latexTemplate, false),
				piecewise.getFormulaString(StringTemplate.latexTemplate, false));
	}

	@Test
	void piecewiseSyntaxShouldCreateFunctionWithoutLabel() {
		GeoFunction function = evaluateGeoElement("$piecewise(x^2,x<1,4)");
		assertEquals(0.25, function.value(0.5), 1E-10);
		assertEquals(4, function.value(2), 1E-10);
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				// nothing entered
				"$piecewise()",
				"$piecewise(,,)",
				// expression missing
				"$piecewise(,x<1)",
				"$piecewise(,x<1,4)",
				"$piecewise(x,x<1,)",
				"$piecewise(x,x<1,,x>3)",
				// condition missing in a case other than the final one
				"$piecewise(x,,4)",
				"$piecewise(x,x<1,4,,5)",
			})
	void incompleteDefinitionsShouldBeRejected(String input) {
		assertNull(evaluate("f(x)=" + input));
		assertNotNull(errorAccumulator.getErrorsSinceReset());
		assertFalse(errorAccumulator.getErrorsSinceReset().isEmpty());
		assertNull(lookup("f"));
	}

	@Test
	void redefiningWithEditorDefinitionShouldNotChangeFunction() {
		GeoElement function = evaluateGeoElement("f(x)=If(x<0, 2x+5, If(x<3, 3, -x))");
		editGeoElement(function, function.getDefinitionForEditor());
		assertEquals("", errorAccumulator.getErrorsSinceReset());
		assertEquals("f(x) = If(x < 0, 2x + 5, x < 3, 3, -x)", lookup("f").getDefinitionForInputBar());
		assertEquals(3, ((GeoFunction) lookup("f")).value(1), 1E-10);
		assertEquals(-5, ((GeoFunction) lookup("f")).value(5), 1E-10);
	}
}
