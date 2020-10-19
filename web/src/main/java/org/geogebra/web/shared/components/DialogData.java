package org.geogebra.web.shared.components;

/**
 * object holding the settings of the dialog
 */
public class DialogData {

	private String titleTransKey;
	private String negativeBtnTransKey;
	private String positiveBtnTransKey;

	/**
	 * dialog settings constructor
	 * @param titleTransKey - ggb trans key for the dialog title
	 * @param negativeBtnTransKey - negative button trans key
	 * @param positiveBtnTransKey - positive button trans key
	 */
	public DialogData(String titleTransKey, String negativeBtnTransKey,
					  String positiveBtnTransKey) {
		setTitleTransKey(titleTransKey);
		setButtonTransKeys(negativeBtnTransKey, positiveBtnTransKey);
	}

	/**
	 * dialog with custom title and buttons Cancel and OK
	 * @param titleTransKey - ggb trans key for the dialog title
	 */
	public DialogData(String titleTransKey) {
		this(titleTransKey, "Cancel", "OK");
	}

	/**
	 * @return trans key of the dialog title
	 */
	public String getTitleTransKey() {
		return titleTransKey;
	}

	/**
	 * @param titleTransKey - ggb trans key of the dialog title
	 */
	public void setTitleTransKey(String titleTransKey) {
		this.titleTransKey = titleTransKey;
	}

	/**
	 * setter for the buttons trans key
	 * @param negativeBtnTransKey - trans key for the negative button (e.g. Cancel)
	 * @param positiveBtnTransKey - trans key for the positive button (e.g. Insert)
	 */
	public void setButtonTransKeys(String negativeBtnTransKey, String positiveBtnTransKey) {
		this.negativeBtnTransKey = negativeBtnTransKey;
		this.positiveBtnTransKey = positiveBtnTransKey;
	}

	/**
	 * @return negative button trans key
	 */
	public String getNegativeBtnTransKey() {
		return negativeBtnTransKey;
	}

	/**
	 * @return positive button trans key
	 */
	public String getPositiveBtnTransKey() {
		return positiveBtnTransKey;
	}
}