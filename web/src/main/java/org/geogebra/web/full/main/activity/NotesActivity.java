package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.AppConfigNotes;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;

/**
 * Activity class for the notes app
 */
public class NotesActivity extends BaseActivity {

	/**
	 * New notes activity
	 */
	public NotesActivity() {
		super(new AppConfigNotes());
	}

	@Override
	public DockPanelW createAVPanel() {
		return new AlgebraDockPanelW(null);
	}

}
