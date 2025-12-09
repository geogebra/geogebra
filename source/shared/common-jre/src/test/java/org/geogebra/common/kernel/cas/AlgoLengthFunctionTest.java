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

package org.geogebra.common.kernel.cas;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoLengthFunctionTest extends BaseUnitTest {

	@Test
	@Issue("APPS-6513")
	public void functionLengthWithVerticalSegment() {
		assertThat(add("Length(sqrt(x-1),1,2)"), hasValue("1.48"));
		assertThat(add("Length(1/(x-1),1,2)"), hasValue("?"));
	}
}
