package org.geogebra.common.main.exam.restriction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.commands.selector.CommandFilter;

/**
 * Model containing the exam restrictions.
 */
@Deprecated // use org.geogebra.common.exam API instead
public class ExamRestrictionModel {
	private List<String> restrictedSubAppCodes = Collections.emptyList();
	private String defaultAppCode;
	private CommandFilter commandFilter;
	private List<FeatureRestriction> featureRestrictions = Collections.emptyList();

	public void setRestrictedSubAppCodes(String... subAppCodes) {
		restrictedSubAppCodes = List.of(subAppCodes);
	}

	/**
	 *
	 * @param subAppCode Sub application code to query.
	 * @return if the subApp specified is restricted during exam or not.
	 */
	public boolean isAppRestricted(String subAppCode) {
		return restrictedSubAppCodes.contains(subAppCode);
	}

	/**
	 *
	 * @return the default subApp code if current one
	 * (before starting exam) is restricted
	 */
	public String getDefaultAppCode() {
		return defaultAppCode;
	}

	/**
	 * define default sub application code on exam mode start
	 * @param subAppCode - default sub app code
	 */
	public void setDefaultAppCode(String subAppCode) {
		defaultAppCode = subAppCode;
	}

	/**
	 *
	 * @return if model has restricted subApps.
	 */
	public boolean hasSubApps() {
		return !restrictedSubAppCodes.isEmpty();
	}

	public CommandFilter getCommandFilter() {
		return commandFilter;
	}

	public void setCommandFilter(CommandFilter graphingCommandFilter) {
		this.commandFilter = graphingCommandFilter;
	}

	/**
	 * UI elements which should be disabled
	 * @param featureRestrictions - restriction to be added
	 */
	public void setFeatureRestrictions(FeatureRestriction... featureRestrictions) {
		this.featureRestrictions = Arrays.asList(featureRestrictions);
	}

	/**
	 * is certain feature restricted
	 * @param featureRestriction - UI element
	 * @return true, if UI element should be restricted
	 */
	public boolean isFeatureRestricted(FeatureRestriction featureRestriction) {
		return featureRestrictions.contains(featureRestriction);
	}
}
