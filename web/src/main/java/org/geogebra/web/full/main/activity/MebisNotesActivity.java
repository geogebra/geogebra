package org.geogebra.web.full.main.activity;

import org.geogebra.web.full.css.MebisResources;
import org.geogebra.web.full.css.ResourceIconProvider;
import org.geogebra.web.full.gui.menubar.MainMenuItemProvider;
import org.geogebra.web.full.gui.menubar.MebisMenuItemProvider;
import org.geogebra.web.html5.main.AppW;

public class MebisNotesActivity extends NotesActivity {

	@Override
	public MainMenuItemProvider getMenuItemProvider(AppW app) {
		return new MebisMenuItemProvider(app);
	}

	@Override
	public ResourceIconProvider getResourceIconProvider() {
		return MebisResources.INSTANCE;
	}

}
