/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.web.html5.gui.util.BrowserStorage;

public class MebisNotesActivity extends NotesActivity {

	@Override
	public void markSearchOpen() {
		markOpen("search:");
	}

	private void markOpen(String id) {
		BrowserStorage.SESSION.setItem("boardOpenDialog", id);
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
