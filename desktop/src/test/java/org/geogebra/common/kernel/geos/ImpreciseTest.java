package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.junit.Test;

public class ImpreciseTest extends BaseUnitTest {
	@Test
	public void name() {

		EvalInfo info = EvalInfoFactory.getEvalInfoForAV(getApp(), true);
		GeoNumeric num = add("a = 1", info);
		assertTrue(num.isSliderable());
	}
}
