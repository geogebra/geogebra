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

package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

public class IntervalFunctionDomainInfo {

	private Interval domainBefore = IntervalConstants.undefined();

	/**
	 * @param domain new domain
	 * @return whether min has decreased AND max has increased
	 */
	public boolean hasZoomedOut(Interval domain) {
		return isMinLower(domain) && isMaxHigher(domain);
	}

	/**
	 * @param domain new domain
	 * @return whether min has decreased
	 */
	public boolean hasPannedLeft(Interval domain) {
		return isMinLower(domain);
	}

	/**
	 * @param domain new domain
	 * @return whether max has increased
	 */
	public boolean hasPannedRight(Interval domain) {
		return isMaxHigher(domain);
	}

	private boolean isMaxHigher(Interval domain) {
		return domain.getHigh() > domainBefore.getHigh();
	}

	private boolean isMinLower(Interval domain) {
		return domain.getLow() < domainBefore.getLow();
	}

	/**
	 * Mark for update: set former domain.
	 * @param domain former domain
	 */
	public void update(Interval domain) {
		domainBefore = domain;
	}

	/**
	 * @param domain other interval
	 * @return whether old domain intersects new interval
	 */
	public boolean intersects(Interval domain) {
		// if low1 < low2, we need low1 <= low2 <= high1, otherwise low2 <= low1 <= high2
		return domainBefore.contains(domain.getLow()) || domain.contains(domainBefore.getLow());
	}

	public double getLength() {
		return domainBefore.getLength();
	}
}
