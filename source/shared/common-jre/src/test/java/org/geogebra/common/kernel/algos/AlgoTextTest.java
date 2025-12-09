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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Test;

public class AlgoTextTest extends BaseUnitTest {

	@Test
	public void testZipCommandWithText() {
		addAvInput("t1=\"text1\"");
		addAvInput("t2=\"text2\"");
		addAvInput("t3=\"text3\"");
		addAvInput("locations={-1, 0, 1}");
		addAvInput("points={(0,1),(0,2),(0,3)}");
		addAvInput("texts={t1,t2,t3}");

		GeoElement zipText = addAvInput("Zip(Text[A,B,true,true,C,0], A, texts, B, "
				+ "points, C, locations)");

		assertThat(((GeoText) ((GeoList) zipText).get(0)).getHorizontalAlignment(), equalTo(-1));
		assertThat(((GeoText) ((GeoList) zipText).get(1)).getHorizontalAlignment(), equalTo(0));
		assertThat(((GeoText) ((GeoList) zipText).get(2)).getHorizontalAlignment(), equalTo(1));
	}
}