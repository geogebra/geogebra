package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
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

	@Test
	public void shouldAcceptIndividualLists() {
		assertThat(addTable("TableText({1,2})"), equalTo(new Dimension(2, 1)));
		assertThat(addTable("TableText({{1},{2},\"c\"})"), equalTo(new Dimension(1, 2)));
		assertThat(addTable("TableText({1,2,\"c\"})"), equalTo(new Dimension(3, 1)));
		assertThat(addTable("TableText({{1},{2}})"), equalTo(new Dimension(1, 2)));
		assertThat(addTable("TableText({\"c\"})"), equalTo(new Dimension(1, 1)));
	}

	@Test
	public void shouldAllowInitiallyEmptyList() {
		add("l={}");
		GeoText table = add("TableText(l)");
		GeoText tableVert = add("TableText(l,\"v\")");
		add("SetValue(l,1,1)");
		assertThat(table, hasValue("{\\begin{array}{l}1 \\\\ \\end{array}}"));
		add("SetValue(l,2,1)");
		assertThat(table, hasValue("{\\begin{array}{ll}1&1 \\\\ \\end{array}}"));
		assertThat(tableVert, hasValue("{\\begin{array}{l}1 \\\\ 1 \\\\ \\end{array}}"));
		add("SetValue(l,3,\"c\")");
		// "c" here is just a list element, should NOT change alignment
		assertThat(table, hasValue("{\\begin{array}{lll}1&1&c \\\\ \\end{array}}"));
	}

	@Test
	public void shouldAllowInitiallyEmptyListOfText() {
		add("l={}");
		GeoText table = add("TableText(l)");
		add("SetValue(l,1,\"c\")");
		// "c" here is just a list element, should NOT change alignment
		assertThat(table, hasValue("{\\begin{array}{l}c \\\\ \\end{array}}"));
	}

	private GDimension addTable(String s) {
		return ((AlgoTableText) add(s).getParentAlgorithm()).getSize();
	}

}
