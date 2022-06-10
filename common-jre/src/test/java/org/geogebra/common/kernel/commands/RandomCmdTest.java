package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Before;
import org.junit.Test;

public class RandomCmdTest extends BaseUnitTest {

	@Before
	public void setSeed() {
		getApp().setRandomSeed(42);
	}

	@Test
	public void randomBetweenShouldBeStable() {
		shouldBeStable("RandomBetween(1,100)");
		shouldBeStable("RandomBetween(-1,-100)");
	}

	@Test
	public void randomPointInShouldBeStable() {
		shouldBeStable("RandomPointIn(1,2,3,4)");
		shouldBeStable("RandomPointIn({(1,0),(0,1),(1,1)})");
		shouldBeStable("RandomPointIn(Polygon((1,0),(0,1),(1,1)))");
		shouldBeStable("RandomPointIn(xx+yy=4)");
	}

	@Test
	public void randomBinomialShouldBeStable() {
		shouldBeStable("RandomBinomial(100, 0.6)");
	}

	@Test
	public void randomElementShouldBeStable() {
		shouldBeStable("RandomElement(1..10)");
		shouldBeStable("RandomElement(Identity(10))");
		shouldBeStable("RandomElement((1..10, 2..11))");
		shouldBeStable("RandomElement(2y=(1..10)x)");
		shouldBeStable("RandomElement(2y=(1..10)x^2)");
		shouldBeStable("RandomElement(y=(1..10)sin(x))");
		shouldBeStable("RandomElement(Zip(UnicodeToLetter(A),A,65..75))");
	}

	@Test
	public void shuffleShouldBeStable() {
		shouldBeStable("Shuffle(1..10)");
	}

	@Test
	public void sampleShouldBeStable() {
		shouldBeStable("Sample(1..10,5)");
		shouldBeStable("Sample(1..10,5,true)");
	}

	@Test
	public void sampleShouldBeUniqueAfterSetValue() {
		add("l1=Sample({2,3,4,5,6,7,8,9,10,12,14},RandomBetween(2,3),false)");
		GeoBoolean check = add("check=Length(l1)==Length(Unique(l1))");
		add("SetValue(l1,{7,9})");
		for (int i = 0; i < 20; i++) {
			assertTrue("Should be unique on iteration " + i, check.getBoolean());
			getApp().getGgbApi().updateConstruction();
		}
	}

	@Test
	public void sequenceRandomShouldBeStable() {
		shouldBeStable("Sequence(RandomBetween(1,100),k,1,5)");
		shouldBeStable("Sequence(RandomBetween(1,k),k,1,5)");
	}

	@Test
	public void latexListElementsShouldStayLatex() {
		addLatex("f", "\\log x");
		addLatex("g", "\\log y");
		add("l1={f,g}");
		GeoList l2 = add("l2=Shuffle(l1)");
		add("SetValue(l2,{\"\\log x\", \"\\log y\"})");
		GeoText firstElement = (GeoText) l2.get(0);
		assertTrue("List element should be LaTeX", firstElement.isLaTeX());
	}

	private void addLatex(String label, String latex) {
		GeoText text = add(label + "=\"" + latex + "\"");
		text.setLaTeX(true, false);
	}

	private void shouldBeStable(String cmd) {
		getApp().getKernel().getConstruction().clearConstruction();
		GeoElement a = add("a=" + cmd);
		String old = a.toValueString(StringTemplate.editTemplate);
		try {
			getApp().getXMLio().processXMLString(getApp().getXML(), true, false, false);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		assertEquals(cmd + " is not stable", old,
				lookup("a").toValueString(StringTemplate.editTemplate));
	}
}
