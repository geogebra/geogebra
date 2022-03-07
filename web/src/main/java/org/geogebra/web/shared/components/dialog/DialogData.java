package org.geogebra.web.shared.components.dialog;

/**
 * object holding the settings of the dialog
 */
public class DialogData {

	private String titleTransKey;
	private String subTitleTransKey;
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
	 * dialog settings constructor
	 * @param titleTransKey - ggb trans key for the dialog title
	 * @param subTitleTransKey - ggb trans key for the dialog subtitle
	 * @param negativeBtnTransKey - negative button trans key
	 * @param positiveBtnTransKey - positive button trans key
	 */
	public DialogData(String titleTransKey, String subTitleTransKey,
			String negativeBtnTransKey, String positiveBtnTransKey) {
		this(titleTransKey, negativeBtnTransKey, positiveBtnTransKey);
		setSubTitleTransKey(subTitleTransKey);
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
	 * @return trans key of the dialog subtitle
	 */
	public String getSubTitleTransKey() {
		return subTitleTransKey;
	}

	/**
	 * @param subTitleTransKey - ggb trans key of the dialog subtitle
	 */
	public void setSubTitleTransKey(String subTitleTransKey) {
		this.subTitleTransKey = subTitleTransKey;
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