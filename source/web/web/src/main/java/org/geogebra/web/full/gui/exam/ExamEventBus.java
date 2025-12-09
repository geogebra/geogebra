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

package org.geogebra.web.full.gui.exam;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;

public final class ExamEventBus implements ExamListener {
	private final List<ExamListener> childListeners = new ArrayList<>();

	/**
	 * Add a listener to the bus.
	 * @param listener listener
	 */
	public void add(ExamListener listener) {
		childListeners.add(listener);
	}

	@Override
	public void examStateChanged(ExamState newState) {
		childListeners.forEach(listener -> listener.examStateChanged(newState));
	}
}
