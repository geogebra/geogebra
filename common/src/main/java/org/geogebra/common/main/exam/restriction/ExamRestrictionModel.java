package org.geogebra.common.main.exam.restriction;

import java.util.Arrays;
import java.util.List;

/**
 * Model containing the exam restrictions.
 */
public class ExamRestrictionModel {
	private List<String> subAppCodes;

	void setSubAppCodes(String... list) {
		subAppCodes = Arrays.asList(list);
	}

	/**
	 *
	 * @param subAppCode Sub application code to query.
	 * @return if the subApp specified is restricted during exam or not.
	 */
	public boolean isAppRestricted(String subAppCode) {
		return subAppCodes.contains(subAppCode);
	}

	/**
	 *
	 * @return the default subApp code if current one
	 * (before starting exam) is restricted
	 */
	public String getDefaultAppCode() {
		return subAppCodes.get(0);
	}

	/**
	 *
	 * @return if model has restricted subApps.
	 */
	public boolean hasSubApps() {
		return !subAppCodes.isEmpty();
	}
}
