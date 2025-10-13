package org.geogebra.common.cas.view;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * Row popup menu for CAS
 */
public class RowHeaderPopupMenu {
	/** localization */
	protected final Localization loc;

	/**
	 * @param app
	 *            application
	 */
	protected RowHeaderPopupMenu(App app) {
		this.loc = app.getLocalization();
	}

	/**
	 * @param selRows
	 *            selected rows
	 * @return localized text for delete item
	 */
	public String getDeleteString(int[] selRows) {
		if (selRows.length == 1) {
			return loc.getPlain("DeleteRowA", Integer.toString(selRows[0] + 1));
		}
		return loc.getPlain("DeleteARows", Integer.toString(selRows.length));
	}

}
