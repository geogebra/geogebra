package org.geogebra.common.cas.view;

import org.geogebra.common.main.App;

public class RowHeaderPopupMenu {

	protected App app;

	public String getDeleteString(int[] selRows) {
		String strRows;
		if (selRows.length == 1) {
			strRows = app.getLocalization().getPlain("DeleteRowA",
					Integer.toString(selRows[0] + 1));
		} else {
			strRows = app.getLocalization().getPlain("DeleteARows",
					Integer.toString(selRows.length));
		}
		return strRows;
	}

}
