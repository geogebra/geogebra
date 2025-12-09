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
 * The states the {@link ExamController} can be in.
 */
public enum ExamState {
	/** No exam is currently active. */
	IDLE,

	/** The exam is about to start (e.g., the app is showing a Start Exam dialog, the browser
	 * is going into fullscreen, etc.). */
	PREPARING,

	/** An exam is active. */
	ACTIVE,

	/** The exam has ended and is in the wrap-up phase (e.g., the app is showing the exam
	 * summary dialog). */
	FINISHED
}
