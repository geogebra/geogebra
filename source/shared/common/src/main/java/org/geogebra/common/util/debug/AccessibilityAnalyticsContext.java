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

package org.geogebra.common.util.debug;

/**
 * Stores short-lived accessibility analytics context across chained UI flows.
 */
public class AccessibilityAnalyticsContext {

	private String trigger = AccessibilityAnalytics.Value.HEADER;
	private String flow = AccessibilityAnalytics.Value.UNSET;
	private boolean saveDialogShown = false;

	/**
	 * @param trigger button location
	 * @return this context
	 */
	public AccessibilityAnalyticsContext setTrigger(String trigger) {
		this.trigger = trigger;
		return this;
	}

	/**
	 * @param flow active analytics flow
	 * @return this context
	 */
	public AccessibilityAnalyticsContext setFlow(String flow) {
		this.flow = flow;
		return this;
	}

	/**
	 * @return button location for the current flow
	 */
	public String getTrigger() {
		return trigger;
	}

	/**
	 * @return active analytics flow
	 */
	public String getFlow() {
		return flow;
	}

	/**
	 * Marks the save dialog as shown for the current flow.
	 *
	 * @return this context
	 */
	public AccessibilityAnalyticsContext markSaveDialogShown() {
		saveDialogShown = true;
		return this;
	}

	/**
	 * @return whether the save dialog was shown for the current flow
	 */
	public boolean isSaveDialogShown() {
		return saveDialogShown;
	}

	/**
	 * Clears save dialog state while keeping trigger and flow.
	 */
	public void resetSave() {
		saveDialogShown = false;
	}

	/**
	 * Clears login context after direct login, but keeps share or assign context alive.
	 */
	public void resetLogin() {
		if (AccessibilityAnalytics.Value.DIRECT.equals(flow)
				|| AccessibilityAnalytics.Value.UNSET.equals(flow)) {
			reset();
		}
	}

	/**
	 * Clears all accessibility analytics context.
	 */
	public void reset() {
		trigger = AccessibilityAnalytics.Value.HEADER;
		flow = AccessibilityAnalytics.Value.UNSET;
		saveDialogShown = false;
	}
}
