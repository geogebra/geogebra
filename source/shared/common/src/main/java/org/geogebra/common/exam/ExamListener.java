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

package org.geogebra.common.exam;

/**
 * Listener interface for changes in the {@link ExamController}.
 */
@FunctionalInterface
public interface ExamListener {

	/**
	 * The exam state did change in the {@link ExamController}.
	 *
	 * @param newState The new exam state.
	 */
	void examStateChanged(ExamState newState);

	/**
	 * The first cheating event occurred.
	 */
	default void cheatingStarted() {
		// Do nothing by default
	}
}

