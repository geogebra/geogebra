package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.IneqTree;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class AlgoElementTest extends BaseUnitTest {

	@Test
	public void latexIntegral() {
		add("f(n)=n^2");
		assertThat(add("Integral(x,1,2)"), hasLaTeXDefinition(
				"\\int\\limits_{1}^{2}x\\,\\mathrm{d}x"));
		assertThat(add("Integral(x)"), hasLaTeXDefinition("\\int x\\,\\mathrm{d}x"));
		assertThat(add("Integral(f,1,2)"), hasLaTeXDefinition(
				"\\int\\limits_{1}^{2}f\\,\\mathrm{d}n"));
		assertThat(add("Integral(f)"), hasLaTeXDefinition("\\int f\\,\\mathrm{d}n"));
		assertThat(add("Sequence(Integral(x^k),k,1,2)"), hasLaTeXDefinition(
				"Sequence\\left(\\int x^{k}\\,\\mathrm{d}x, k, 1, 2 \\right)"));
	}

	@Test
	public void testSequenceInequality() {
		add("a: y" + Unicode.LESS_EQUAL + "x^(2) " + Unicode.AND
				+ " y" + Unicode.GREATER_EQUAL + " 0 " + Unicode.AND + " x" + Unicode.GREATER_EQUAL
				+ "-0 " + Unicode.AND + " x" + Unicode.LESS_EQUAL + "1");
		GeoList seq = add("l1=Sequence(a(x+i,y),i,1,2)");
		GeoList list = add("{Element[l1,1], Element[l1,2]}");
		assertEquals(list.toValueString(StringTemplate.defaultTemplate),
				seq.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testSequenceInequality2() {
		add("a: y" + Unicode.LESS_EQUAL + "x^(2) " + Unicode.AND
				+ " y" + Unicode.GREATER_EQUAL + " 0 " + Unicode.AND + " x" + Unicode.GREATER_EQUAL
				+ "-0 " + Unicode.AND + " x" + Unicode.LESS_EQUAL + "1");
		GeoList list = add("{a(x+1,y), a(x+2,y)}");
		assertEquals(list.toValueString(StringTemplate.defaultTemplate),
				add("Sequence(a(x+i,y),i,1,2)").toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testSequenceIneqTree() {
		GeoElementND f = add("a: y" + Unicode.LESS_EQUAL + "x^(2) " + Unicode.AND
				+ " y" + Unicode.GREATER_EQUAL + " 0 " + Unicode.AND + " x" + Unicode.GREATER_EQUAL
				+ "-0 " + Unicode.AND + " x" + Unicode.LESS_EQUAL + "1");
		GeoList list = add("{a(x+1,y), a(x+2,y)}");
		IneqTree ineqs1 = ((GeoFunctionNVar) list.get(0)).getIneqs();
		GeoList sequence = add("Sequence(a(x+i,y),i,1,2)");
		f.updateRepaint();
		IneqTree ineqs2 = ((GeoFunctionNVar) sequence.get(0)).getIneqs();
		assertEquals(ineqs1.getLeft().getRight().getIneq().getBorder()
						.toValueString(StringTemplate.defaultTemplate),
				ineqs2.getLeft().getRight().getIneq().getBorder()
						.toValueString(StringTemplate.defaultTemplate));
	}

	private TypeSafeMatcher<GeoElement> hasLaTeXDefinition(String def) {
		return hasProperty("definition", item ->
				item.getDefinition(StringTemplate.latexTemplate), def);
	}
}
