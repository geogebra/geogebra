package org.geogebra.common.gui.view.spreadsheet;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class RelativeCopyTest extends BaseUnitTest {

	@Test
	public void conditionToShowRelative() throws CircularDefinitionException {
		GeoElement a1 = add("A1=(1,1)");
		GeoBoolean b1 = add("B1=true");
		add("B2=false");
		a1.setShowObjectCondition(b1);
		new RelativeCopy(getKernel()).doDragCopy(0, 0, 0, 0,
				0, 1, 0, 1);
		assertEquals(lookup("A2").getShowObjectCondition(), lookup("B2"));
	}

	@Test
	public void conditionToShowAbsolute() throws CircularDefinitionException {
		GeoElement a1 = add("A1=(1,1)");
		GeoBoolean b = add("b=true");
		a1.setShowObjectCondition(b);
		new RelativeCopy(getKernel()).doDragCopy(0, 0, 0, 0,
				0, 1, 0, 1);
		assertEquals(lookup("A2").getShowObjectCondition(), b);
	}
}
