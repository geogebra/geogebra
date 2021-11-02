package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.web.full.css.MebisResources;
import org.geogebra.web.full.css.ResourceIconProvider;
import org.geogebra.web.html5.gui.util.BrowserStorage;

public class MebisNotesActivity extends NotesActivity {

	@Override
	public ResourceIconProvider getResourceIconProvider() {
		return MebisResources.INSTANCE;
	}

	@Override
	public void markSearchOpen() {
		markOpen("search:");
	}

	private void markOpen(String id) {
		BrowserStorage.SESSION.setItem("tafelOpenDialog", id);
	}

	@Override
	public void markSaveOpen() {
		markOpen("save:");
	}

	@Override
	public void markSaveProcess(String title, MaterialVisibility visibility) {
		BrowserStorage.SESSION.setItem("matTitle", title);
		BrowserStorage.SESSION.setItem("matVisibility", visibility.getToken());
	}
}
