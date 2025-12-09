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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.jupiter.api.Test;

public class SuggestionIntersectExtremumTest {

	private AppCommon app = AppCommonFactory.create3D();

	@Test
	void intersectRange() {
		GeoElement function = add("1/x");
		SuggestionIntersectExtremum.get(function).execute(function);
		List<String> definitions = Arrays.stream(app.getGgbApi().getAllObjectNames())
				.map(n -> lookup(n).getDefinition(StringTemplate.algebraTemplate))
				.collect(Collectors.toList());
		assertEquals(List.of("", "Intersect(f, xAxis, -4.3, 11.7)",
				"Extremum(f, -4.3, 11.7)", "Intersect(f, yAxis, (0, 0))"), definitions);
	}

	@Test
	public void rootSuggestionShouldVanish() {
		add("f:x");
		GeoElement line = lookup("f");
		assertNotNull(SuggestionIntersectExtremum.get(line));
		SuggestionIntersectExtremum.get(line).execute(line);
		assertNull(SuggestionIntersectExtremum.get(line));
		lookup("B").remove();
		assertNotNull(SuggestionIntersectExtremum.get(line));
	}

	@Test
	public void rootSuggestionForParabolaShouldVanish() {
		add("f:y=x^2-6x+8");
		GeoElement parabola = lookup("f");
		assertNotNull(SuggestionIntersectExtremum.get(parabola));
		SuggestionIntersectExtremum.get(parabola).execute(parabola);
		assertNull(SuggestionIntersectExtremum.get(parabola));
		lookup("B").remove();
		assertNotNull(SuggestionIntersectExtremum.get(parabola));
	}

	@Test
	public void rootSuggestionForParabolaShouldCreatePoints() {
		add("f:y=x^2-6x+8");
		GeoElement parabola = lookup("f");
		assertNotNull(SuggestionIntersectExtremum.get(parabola));
		SuggestionIntersectExtremum.get(parabola).execute(parabola);
		assertEquals(4,
				app.getGgbApi().getAllObjectNames("point").length);
	}

	@Test
	public void rootSuggestionForHyperbola() {
		add("f:xx-yy=1");
		GeoElement hyperbola = lookup("f");
		assertNull(SuggestionIntersectExtremum.get(hyperbola));
	}

	@Test
	public void suggestionShouldNotCreateTwice() {
		add("f:x");
		GeoElement line = lookup("f");
		SuggestionIntersectExtremum.get(line).execute(line);
		assertEquals(3, app.getGgbApi().getObjectNumber());
		lookup("B").remove();
		assertEquals(2, app.getGgbApi().getObjectNumber());
		SuggestionIntersectExtremum.get(line).execute(line);
		assertEquals(3, app.getGgbApi().getObjectNumber());
	}

	@Test
	public void suggestionShouldNotCreateTwiceNonPolynomial() {
		add("f:1/x");
		GeoElement line = lookup("f");
		SuggestionIntersectExtremum.get(line).execute(line);
		assertEquals(4, app.getGgbApi().getObjectNumber());
		lookup("B").remove();
		assertEquals(3, app.getGgbApi().getObjectNumber());
		SuggestionIntersectExtremum.get(line).execute(line);
		assertEquals(4, app.getGgbApi().getObjectNumber());
	}

	private GeoElement add(String command) {
		return (GeoElement) app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(command, false)[0];
	}

	private GeoElement lookup(String s) {
		return app.getKernel().lookupLabel(s);
	}
}