package org.geogebra.common.gui.view.spreadsheet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.TestErrorHandler;
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

	@Test
	public void numericCopy() {
		add("A1=1");
		new RelativeCopy(getKernel(), TestErrorHandler.INSTANCE).doDragCopy(0, 0, 0, 0,
				0, 1, 0, 1);
		assertThat(lookup("A2"), hasValue("1"));
	}

	@Test
	public void numericCopyHasCorrectPrecision() {
		add("C3 = 1 / 8");
		getApp().setRounding("2");
		new RelativeCopy(getKernel(), TestErrorHandler.INSTANCE).doDragCopy(2, 2, 2, 2,
				3, 2, 3, 2);
		getApp().setRounding("3");
		assertThat(lookup("D3"), hasValue("0.125"));
	}

	@Test
	public void commandsShouldBeCaseInsensitiveCommandsInCells() {
		add("c = Circle((0, 0), 1)");
		assertThat(prepareAddingValue("Dilate(c, 2)"), hasValue("x\u00B2 + y\u00B2 = 4"));
		assertThat(prepareAddingValue("DiLaTe(c, 2)"), hasValue("x\u00B2 + y\u00B2 = 4"));
		assertThat(prepareAddingValue("diLaTE(c, 2)"), hasValue("x\u00B2 + y\u00B2 = 4"));
	}

	private GeoElementND prepareAddingValue(String inputText) {
		return new RelativeCopy(getKernel(),
				TestErrorHandler.INSTANCE).prepareAddingValueToTableNoStoringUndoInfo(
				inputText, null, 1, 1, false);
	}
}
