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
