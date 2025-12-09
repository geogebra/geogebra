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

package org.geogebra.web.full.gui.components.sideSheet;

import org.geogebra.web.shared.components.dialog.DialogData;

public class SideSheetData extends DialogData {

	/**
	 * Object holding data for side sheet
	 * @param titleTransKey title of side sheet
	 * @param negativeBtnTransKey negative button text
	 * @param positiveBtnTransKey positive button text
	 */
	public SideSheetData(String titleTransKey, String negativeBtnTransKey,
			String positiveBtnTransKey) {
		super(titleTransKey, negativeBtnTransKey, positiveBtnTransKey);
	}

	/**
	 * Side sheet data only with title, no button labels
	 * @param titleTransKey title of side sheet
	 */
	public SideSheetData(String titleTransKey) {
		super(titleTransKey, null, null);
	}

	/**
	 * @return whether the positive button trans key is null or not
	 */
	public boolean hasPositiveBtn() {
		return getPositiveBtnTransKey() != null;
	}

	/**
	 * @return whether the negative button trans key is null or not
	 */
	public boolean hasNegativeBtn() {
		return getNegativeBtnTransKey() != null;
	}
}
