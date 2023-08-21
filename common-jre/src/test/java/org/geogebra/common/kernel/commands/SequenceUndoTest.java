package org.geogebra.common.kernel.commands;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.test.UndoRedoTester;
import org.junit.Test;

public class SequenceUndoTest extends BaseUnitTest {
	@Test
	public void testBug() {
		App app = getApp();
		UndoRedoTester undoRedo = new UndoRedoTester(app);
		undoRedo.setupUndoRedo();
		add("cars=A1:A5");
		addSpreadsheetData();
		add("carsRef=cars");
		add("rotatedText=?");
		add("rotatedText=RotateText(Element(carsRef,2), 320°);");
		add("(1, 2)");
		undoRedo.undo();
		GeoElement text1 = lookup("rotatedText");

		assertThat(text1, isDefined());


	}

	private void addSpreadsheetData() {
		add("A1 = \"Ford\"");
		add("A2 = \"Chevy\"");
		add("A3 = \"Honda\"");
		add("A4 = \"Toyota\"");
		add("A5 = \"Nissan\"");
		add("A6 = \"Other\"");
	}
}
