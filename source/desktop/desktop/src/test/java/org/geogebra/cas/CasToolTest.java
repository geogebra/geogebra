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
