package org.geogebra.cas;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.cas.view.CASInputHandler;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

public class CasToolTest extends BaseCASIntegrationTest {

	@Test
	public void checkNsolveExpansion() {
		CASViewNoGui view = new CASViewNoGui(getApp(), "Sum(T/2^n,n,3,10)=1500000", "$1");
		CASInputHandler cih = new CASInputHandler(view);
		cih.processCurrentRow("NSolve", false, null);
		view.getConsoleTable().setSelected(1);
		cih.processCurrentRow("Evaluate", false, null);
		assertEquals("{T = 1204705882353 / 200000}", getValue("$2"));
	}

	@Test
	public void nsolveToolTest() {
		CASViewNoGui view = new CASViewNoGui(getApp(), "x^2+1", "$1", "$2");
		CASInputHandler cih = new CASInputHandler(view);

		view.getConsoleTable().setSelected(0);
		cih.processCurrentRow("Evaluate", false, null);
		assertEquals("x^(2) + 1", getValue("$1"));

		view.getConsoleTable().setSelected(1);
		cih.processCurrentRow("Derivative", false, null);
		assertEquals("2 * x", getValue("$2"));

		view.getConsoleTable().setSelected(2);
		cih.processCurrentRow("NSolve", false, null);
		assertEquals("{x = 0}", getValue("$3"));
	}

	private String getValue(String label) {
		return kernel.lookupLabel(label).toValueString(StringTemplate.testTemplate);
	}
}
