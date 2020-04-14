package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class GeoNumericTest extends BaseUnitTest {

	@Test
	public void getLaTeXDescriptionRHS() {
		GeoNumeric numeric = addAvInput("1/2");

		String descriptionRHS =
				numeric.getLaTeXDescriptionRHS(
						true, StringTemplate.latexTemplate);
		assertThat(descriptionRHS, equalTo("0.5"));

		descriptionRHS =
				numeric.getLaTeXDescriptionRHS(
						false, StringTemplate.latexTemplate);
		assertThat(descriptionRHS, equalTo("\\frac{1}{2}"));
	}

	@Test
	public void getLaTeXAlgebraDescription() {
		GeoNumeric numeric = addAvInput("a = 1/2");

		String description =
				numeric.getLaTeXAlgebraDescription(
						true, StringTemplate.latexTemplate);
		assertThat(description, equalTo("a\\, = \\,0.5"));

		description =
				numeric.getLaTeXAlgebraDescription(
						false, StringTemplate.latexTemplate);
		assertThat(description, equalTo("a\\, = \\,\\frac{1}{2}"));
	}
}