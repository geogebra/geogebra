package org.geogebra.web.full.gui.menu.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.gui.menubar.action.ShowSearchView;
import org.geogebra.web.full.main.AppWFull;

public class OpenFileActionSuite extends DefaultMenuAction<Void> {
	private final ShowSearchView searchView;

	public OpenFileActionSuite(ShowSearchView searchView) {
		this.searchView = searchView;
	}

	@Override
	public void execute(Void item, AppWFull app) {
		if (app.isExam()) {
			openTemporaryFileView(app);
		} else {
			searchView.execute(item, app);
		}
	}

	private void openTemporaryFileView(AppWFull app) {
		app.openSearch(null);
	}
}
