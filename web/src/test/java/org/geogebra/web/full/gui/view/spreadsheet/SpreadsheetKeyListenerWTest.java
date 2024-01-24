package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.web.full.gui.util.AdvancedFocusPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.dom.client.KeyCodes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub(AdvancedFocusPanel.class)
public class SpreadsheetKeyListenerWTest {

	private AppWFull app;
	private SpreadsheetViewW view;

	@Before
	public void setup() {
		app = AppMocker.mockApplet(new AppletParameters("classic"));
		view = new SpreadsheetViewW(app);
	}

	@Test
	public void test() {
		NativeEvent event = Document.get().createKeyDownEvent(false, false, false, false, KeyCodes.KEY_A);
		view.getSpreadsheetTable().setSelection(1, 1);
		DomEvent.fireNativeEvent(event, view.getFocusPanel(), view.getFocusPanel().getElement());
		view.getSpreadsheetTable().editCellAt(2, 2);

		Object o = app.getSpreadsheetTableModel().getValueAt(1, 1);
		assert (o instanceof Integer);
	}
}
