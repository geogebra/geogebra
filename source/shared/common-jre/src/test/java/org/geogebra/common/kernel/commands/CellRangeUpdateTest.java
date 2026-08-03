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

package org.geogebra.common.kernel.commands;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

public class CellRangeUpdateTest extends BaseUnitTest {
	@Test
	@Issue("APPS-7726")
	void cellRangesShouldNotBeNotifiedByUnlabeledGeos() {
		add("A1=\"foo\"");
		add("A2=\"bar\"");
		add("range=A1:A2");
		add("a=true");
		add("b=true");
		add("c=?");
		add("d=true");
		add("e=true");

		AlgoElement renameListener = mock(AlgoElement.class);
		getKernel().registerRenameListenerAlgo(renameListener);
		add("If(a && b, SetValue(c, d && e))");

		verify(renameListener, never()).compute();

		add("f=true");
		verify(renameListener, atLeastOnce()).compute();
	}
}
