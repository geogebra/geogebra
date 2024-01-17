package org.geogebra.desktop.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.UndoRedoMode;
import org.junit.Test;

public class CopyPasteDTest {

	@Test
	public void clipboardStringShouldDisappearOnInsert() {
		AppCommon fromApp = AppCommonFactory.create3D();
		fromApp.setUndoRedoMode(UndoRedoMode.GUI);
		fromApp.setUndoActive(true);
		fromApp.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("s:Sequence(2k,k,1,3)", true);
		fromApp.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("c:Curve(sin(t),cos(t),t,0,2)", true);
		AppCommon toApp = AppCommonFactory.create3D();
		toApp.setUndoRedoMode(UndoRedoMode.GUI);
		toApp.setUndoActive(true);
		CopyPasteD copy = new CopyPasteD();
		copy.insertFrom(fromApp, toApp);
		assertEquals(Arrays.asList("s_{1}", "c_{1}"),
				Arrays.asList(toApp.getGgbApi().getAllObjectNames()));
		assertThat(toApp.getKernel().lookupLabel("s_{1}").getDefinitionForEditor(),
				equalTo("s_{1}=Sequence(2 k,k,1,3)"));
		assertThat(toApp.getKernel().lookupLabel("c_{1}").getDefinitionForEditor(),
				equalTo("c_{1}=Curve(sin(t),cos(t),t,0,2)"));
		assertThat(toApp.getKernel().lookupLabel("c_{1}").toString(StringTemplate.testTemplate),
				equalTo("c_{1}:(sin(t), cos(t))"));
	}
}
