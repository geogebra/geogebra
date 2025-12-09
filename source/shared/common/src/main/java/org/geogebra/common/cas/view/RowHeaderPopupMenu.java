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
