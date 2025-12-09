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

package org.geogebra.common.move.operations;

import org.geogebra.common.move.events.GenericEvent;
import org.geogebra.common.move.views.BaseView;

/**
 * @author gabor
 * 
 *         Base class for all operations in Common
 * @param <T>
 *            Type of handlers this operation notifies
 */
public class BaseOperation<T> {

	/**
	 * The Common view component to operate on (if exists)
	 */
	private final BaseView<T> view = new BaseView<>();

	/**
	 * @return the Common View to operate on
	 */
	public BaseView<T> getView() {
		return view;
	}

	protected BaseOperation() {
		// must be subclassed
	}

	protected void dispatchEvent(GenericEvent<T> event) {
		view.onEvent(event);
	}

}
