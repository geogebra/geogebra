package org.geogebra.web.full.gui.view.spreadsheet;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.web.full.gui.util.AdvancedFocusPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub(AdvancedFocusPanel.class)
public class SpreadsheetStyleBarWTest {

	@Test
	public void updateForSetGrid() {
		AppletParameters params = new AppletParameters("geometry")
				.setAttribute("perspective" , "GS")
				.setAttribute("allowStyleBar" , "true");
		AppW app = AppMocker.mockApplet(params);
		SpreadsheetViewW spreadsheetView =
				(SpreadsheetViewW) app.getGuiManager().getSpreadsheetView();
		spreadsheetView.setShowGrid(true);
		// what we're really asserting is that the stylebar update didn't cause a crash
		assertThat(spreadsheetView.getSpreadsheetStyleBar(), notNullValue());
	}
}
