package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Test;

public class AlgoTableTextTest extends BaseUnitTest {

	@Test
	public void shouldUpdateOnStyleChange() {
		add("a=1");
		add("SetColor(a,1,0,0)");
		GeoText table = add("TableText({{a}})");
		assertThat(table, hasValue("{\\begin{array}{l}\\textcolor{#FF0000}{1} \\\\ \\end{array}}"));
		add("SetColor(a,0,1,0)");
		assertThat(table, hasValue("{\\begin{array}{l}\\textcolor{#00FF00}{1} \\\\ \\end{array}}"));
		add("SetDynamicColor(a,0,0,1)");
		assertThat(table, hasValue("{\\begin{array}{l}\\textcolor{#0000FF}{1} \\\\ \\end{array}}"));
	}
}
