package org.geogebra.web.web.gui.view.spreadsheet;

import org.geogebra.web.web.gui.util.AdvancedFocusPanelI;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;

public class GFocusPanel extends FocusPanel implements AdvancedFocusPanelI {

	public GFocusPanel(AbsolutePanel spreadsheet) {
		super(spreadsheet);
	}

	public void addPasteHandler(SpreadsheetKeyListenerW sskl) {

	}

	public void setSelectedContent(String cs) {
		// TODO Auto-generated method stub

	}

}
