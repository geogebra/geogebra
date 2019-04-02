package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.AppConfigNotes;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.full.gui.menubar.MainMenuItemProvider;
import org.geogebra.web.full.gui.menubar.NotesMenuItemProvider;
import org.geogebra.web.html5.main.AppW;

/**
 * Activity class for the notes app
 */
public class NotesActivity extends BaseActivity {

	/**
	 * New notes activity
	 */
	public NotesActivity(boolean isMebisVendor) {
		super(new AppConfigNotes(isMebisVendor));
	}

	@Override
	public DockPanelW createAVPanel() {
		return new AlgebraDockPanelW(null, true);
	}

	@Override
	public MainMenuItemProvider getMenuItemProvider(AppW app) {
		return new NotesMenuItemProvider(app);
	}

}
