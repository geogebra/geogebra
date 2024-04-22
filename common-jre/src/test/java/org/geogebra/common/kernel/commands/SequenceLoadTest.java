package org.geogebra.common.kernel.commands;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.test.UndoRedoTester;
import org.junit.Before;
import org.junit.Test;

public class SequenceLoadTest extends BaseUnitTest {

	public static final String M2 =
			"{{2, 0, 0, 0}, {2, 1, 0, 0}, {2, 0, 0, 0}, {2, 1, 0, 0}, {8, 1, 0, 0}, {8, 4, 1, 0},"
					+ " {8, 4, 0, 0}}";
	private UndoRedoTester undoRedoTester;

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Before
	public void setUp() throws IOException {
		getApp().enableCAS(false);
		getApp().getGgbApi().setXML(Files.readString(
				Path.of("src/test/resources/sequence-undo.xml")));
		undoRedoTester = new UndoRedoTester(getApp());
		undoRedoTester.setupUndoRedo();
	}

	@Test
	public void testMatrixIsLoaded() {
		assertThat(getKernel().lookupLabel("m2"), hasValue(M2));
	}

	@Test
	public void testMatrixFromCommandAfterLoad() {
		add("m4 = Sequence(Sequence(Element(m1, lig, col) - Element(m1, lig, col + 1), "
				+ "col, 1, dimjeu2), lig, 1, Dimension(jeunonnul))");
		assertThat(getKernel().lookupLabel("m4"), hasValue(M2));
	}

	@Test
	public void testUndo() {
		add("(1,1)");
		getApp().storeUndoInfo();
		undoRedoTester.undo();
	}

}