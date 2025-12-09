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

package org.geogebra.common.properties.impl.algebra;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.junit.Test;

public class SortByPropertyTest  extends BaseUnitTest {

	@Test
	public void shouldStayAfterReload() {
		SortByProperty sortProp = new SortByProperty(getSettings().getAlgebra(), getLocalization());
		sortProp.setValue(AlgebraView.SortMode.LAYER);
		String xml = getApp().getXML();
		getApp().getSettings().resetSettings(getApp());
		assertThat(sortProp.getValue(), equalTo(AlgebraView.SortMode.TYPE));
		getApp().setXML(xml, true);
		assertThat(getSettings().getAlgebra().getTreeMode(), equalTo(AlgebraView.SortMode.LAYER));
	}
}
