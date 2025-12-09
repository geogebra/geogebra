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

package org.geogebra.common.kernel.algos;

import static org.junit.Assert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Test;

public class AlgoTextCornerTest extends BaseUnitTest {

	@Test
	public void cornerInputBox() {
		add("ib=InputBox()");
		assertThat(add("Sequence(Corner(ib,k),k,1,4)"),
				hasValue("{(-3.7, 5.2), (0.22, 5.2), (0.22, 5.7), (-3.7, 5.7)}"));
	}

	@Test
	public void cornerCheckbox() {
		add("cb=Checkbox()");
		assertThat(add("Sequence(Corner(cb,k),k,1,4)"),
				hasValue("{(-4.2, 5.44), (-3.42, 5.44), (-3.42, 6.2), (-4.2, 6.2)}"));
	}

	@Test
	public void cornerButton() {
		add("btn=Button()");
		assertThat(add("Sequence(Corner(btn,k),k,1,4)"),
				hasValue("{(-4.3, 5.82), (-3.54, 5.82), (-3.54, 6.3), (-4.3, 6.3)}"));
	}

	@Test
	public void cornerDropDownList() {
		GeoList dropDown = add("dl={}");
		dropDown.setDrawAsComboBox(true);
		dropDown.updateRepaint();
		assertThat(add("Sequence(Corner(dl,k),k,1,4)"),
				hasValue("{(-4.5, 6.3), (-4.3, 6.3), (-4.3, 6.3), (-4.5, 6.3)}"));
	}

}