package org.geogebra.common.kernel.geos.symbolic.vector;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class SymbolicVectorTest extends BaseSymbolicTest {

	@Test
	public void testDotProduct() {
		t("Dot[Vector[(1,2)],Vector[(3,4)]]", "11");
		t("Dot[Vector[(p,q)],Vector[(r,s)]]", "p * r + q * s");
	}

	@Test
	public void testCrossProduct() {
		t("Cross[Vector[(1,2)],Vector[(3,4)]]", "-2");
		t("Cross[Vector[(p,q)], Vector[(r,s)]]", "p * s - q * r");
	}

	@Test
	public void testVectors() {
		// these should give Vector not point
		t("u=(1,2)", "(1, 2)");
		t("u=(1,2,3)", "(1, 2, 3)");
		t("Length(Vector((3,4)))", "5");
		t("x(Vector((3,4)))", "3");
		t("y(Vector((3,4)))", "4");
		t("z(Vector((3,4)))", "0");
		t("x(Vector((3,4,5)))", "3");
		t("y(Vector((3,4,5)))", "4");
		t("z(Vector((3,4,5)))", "5");
		t("abs(Vector((1,2)))", "sqrt(5)");
		t("UnitVector((1,2))", "(1 / 5 * sqrt(5), 2 / 5 * sqrt(5))");
		t("UnitVector((p,q))", "(p / sqrt(p^(2) + q^(2)), q / sqrt(p^(2) + q^(2)))");
		t("UnitPerpendicularVector((1,2))", "(-2 / sqrt(5), 1 / sqrt(5))");
		t("UnitPerpendicularVector((p,q))", "((-q) / sqrt(p^(2) + q^(2)), p / sqrt(p^(2) + q^(2)))");
		t("PerpendicularVector((1,2))", "(-2, 1)");
		t("PerpendicularVector((p,q))", "(-q, p)");
		t("Dot((p,q),(r,s))", "p * r + q * s");
		t("Dot((1,2),(3,4))", "11");
	}

	@Test
	public void testCreationWithLabel() {
		GeoSymbolic vector = add("v=(1,1)");
		assertThat(vector.getTwinGeo(), CoreMatchers.<GeoElementND>instanceOf(GeoVector.class));
	}

	@Test
	public void testVectorDefinitionForIndependent() {
		GeoSymbolic vector = add("v = (1, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.editorTemplate),
				equalTo("{{1}, {2}}"));
		assertThat(
				vector.getDefinition(StringTemplate.latexTemplate),
				equalTo("\\left( \\begin{align}1 \\\\ 2 \\end{align} \\right)"));
	}

	@Test
	public void testVectorDefinitionForDependent() {
		add("a = 1");
		GeoSymbolic vector = add("v = (a, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.editorTemplate),
				equalTo("{{a}, {2}}"));
		assertThat(
				vector.getDefinition(StringTemplate.latexTemplate),
				equalTo("\\left( \\begin{align}a \\\\ 2 \\end{align} \\right)"));
	}

	@Test
	public void testIsGeoVector() {
		GeoSymbolic vector = add("v = (a, 2)");
		assertThat(vector.isGeoVector(), is(true));
	}

	@Test
	public void testVectorLatexStringForDependent() {
		GeoSymbolic vector = add("v = (a, 2)");
		assertThat(
				vector.toLaTeXString(false, StringTemplate.latexTemplate),
				equalTo("\\left( \\begin{align}a \\\\ 2 \\end{align} \\right)"));
	}

	@Test
	public void testIsCasVectorAfterRedefinition() {
		GeoSymbolic vector = add("v = (1, 2)");
		vector.getTwinGeo();
		vector = add("v = (2, 1)");
		vector.getTwinGeo();
		ExpressionValue unwrappedDefinition = vector.getDefinition().unwrap();
		MyVecNDNode vecNode = (MyVecNDNode) unwrappedDefinition;
		assertThat(vecNode.isCASVector(), is(true));
	}

	@Test
	public void testAdditionAndRedefinition() {
		add("u = (1, 2)");
		add("v = (2, 1)");
		GeoSymbolic sum = add("sum = u + v");
		assertThat(
				sum.getLaTeXDescriptionRHS(true, StringTemplate.latexTemplate),
				equalTo("\\left( \\begin{align}3 \\\\ 3 \\\\ \\end{align} \\right)"));

		add("u = (1, 2)");
		assertThat(
				sum.getLaTeXDescriptionRHS(true, StringTemplate.latexTemplate),
				equalTo("\\left( \\begin{align}3 \\\\ 3 \\\\ \\end{align} \\right)"));
	}
}
