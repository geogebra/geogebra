package org.geogebra.common.cas.view;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class RowHeaderPopupMenu {

	protected final App app;
	protected final Localization loc;

	protected RowHeaderPopupMenu(App app) {
		this.app = app;
		this.loc = app.getLocalization();
	}

	public String getDeleteString(int[] selRows) {
		if (selRows.length == 1) {
			return loc.getPlain("DeleteRowA",
					Integer.toString(selRows[0] + 1));
		}
		return loc.getPlain("DeleteARows",
				Integer.toString(selRows.length));
	}

}
