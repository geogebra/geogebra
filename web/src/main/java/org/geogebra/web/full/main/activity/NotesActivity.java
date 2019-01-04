package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.AppConfigNotes;
import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;

public class NotesActivity extends BaseActivity {

	public NotesActivity() {
		super(new AppConfigNotes());
	}

	@Override
	public DockPanelW createAVPanel() {
		return new AlgebraDockPanelW(new DockPanelDecorator());
	}

}
