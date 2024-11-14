package org.geogebra.common.gui.view.spreadsheet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellDataSerializer;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class RelativeCopyTest extends BaseUnitTest {

	private DefaultSpreadsheetCellDataSerializer serializer =
			new DefaultSpreadsheetCellDataSerializer();

	@Test
	public void conditionToShowRelative() throws CircularDefinitionException {
		GeoElement a1 = add("A1=(1,1)");
		GeoBoolean b1 = add("B1=true");
		add("B2=false");
		a1.setShowObjectCondition(b1);
		newRelativeCopy().doDragCopy(0, 0, 0, 0,
				0, 1, 0, 1);
		assertEquals(lookup("A2").getShowObjectCondition(), lookup("B2"));
	}

	@Test
	public void conditionToShowAbsolute() throws CircularDefinitionException {
		GeoElement a1 = add("A1=(1,1)");
		GeoBoolean b = add("b=true");
		a1.setShowObjectCondition(b);
		newRelativeCopy().doDragCopy(0, 0, 0, 0,
				0, 1, 0, 1);
		assertEquals(lookup("A2").getShowObjectCondition(), b);
	}

	@Test
	public void numericCopy() {
		add("A1=1");
		newRelativeCopy().doDragCopy(0, 0, 0, 0,
				0, 1, 0, 1);
		assertThat(lookup("A2"), hasValue("1"));
	}

	@Test
	public void numericCopyHasCorrectPrecision() {
		add("C3 = 1 / 8");
		getApp().setRounding("2");
		newRelativeCopy().doDragCopy(2, 2, 2, 2,
				3, 2, 3, 2);
		getApp().setRounding("3");
		assertThat(lookup("D3"), hasValue("0.125"));
	}

	@Test
	public void functionCopyHasCorrectValue() {
		add("C2 = 3 / 8 ");
		add("D2 = 5 / 8 ");
		add("C3 = C2 + x");
		getApp().setRounding("2");
		newRelativeCopy().doDragCopy(2, 2, 2, 2,
				3, 2, 3, 2);
		getApp().setRounding("3");
		assertThat(lookup("D3"), hasValue("0.625 + x"));
	}

	@Issue("APPS-6058")
	@Test
	public void dragCopyRelativeExpression() {
		for (int i = 1; i < 11; i++) {
			add("A" + i + " = " + i);
			add("B" + i + " = (A" + i + ", 0)");
			assertThat(lookup("B" + i), hasValue("(" + i + ", 0)"));
		}

		add("C1 = Circle(B1, 0.4)");
		newRelativeCopy().doDragCopy(2, 0, 2, 0,
				2, 1, 2, 10);
		for (int row = 2; row < 10; row++) {
			assertThat(lookup("C" + row), hasValue("(x - " + row + ")\u00B2 + y\u00B2 = 0.16"));
			shouldBeInEditor("C" + row, "=Circle(B" + row + "," + "0.4)");
		}
	}

	private RelativeCopy newRelativeCopy() {
		return new RelativeCopy(getKernel(), TestErrorHandler.INSTANCE);
	}

	private void shouldBeInEditor(String CellName, String value) {
		assertThat(serializer.getStringForEditor(lookup(CellName)), is(value));
	}

	@Test
	public void commandsShouldBeCaseInsensitiveCommandsInCells() {
		add("c = Circle((0, 0), 1)");
		assertThat(prepareAddingValue("Dilate(c, 2)"), hasValue("x\u00B2 + y\u00B2 = 4"));
		assertThat(prepareAddingValue("DiLaTe(c, 2)"), hasValue("x\u00B2 + y\u00B2 = 4"));
		assertThat(prepareAddingValue("diLaTE(c, 2)"), hasValue("x\u00B2 + y\u00B2 = 4"));
	}

	private GeoElementND prepareAddingValue(String inputText) {
		return newRelativeCopy().prepareAddingValueToTableNoStoringUndoInfo(
				inputText, null, 1, 1, false);
	}
}
