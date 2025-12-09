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

package org.geogebra.editor.share.catalog;

import java.io.Serializable;

/**
 * Parameter navigation specification for function arguments.
 */
class Parameter implements Serializable {
	/** Param without up/down functionality */
	static final Parameter BASIC = new Parameter(-1, -1);

	private final int upIndex;
	private final int downIndex;

	/**
	 * @param upIndex index of param upwards
	 * @param downIndex index of param downwards
	 */
	Parameter(int upIndex, int downIndex) {
		this.upIndex = upIndex;
		this.downIndex = downIndex;
	}

	/**
	 * @return index of param upwards
	 */
	int getUpIndex() {
		return upIndex;
	}

	/**
	 * @return index of param downwards
	 */
	int getDownIndex() {
		return downIndex;
	}

	/**
	 * @param upIndex index to navigate to when pressing up
	 * @return parameter with up navigation
	 */
	static Parameter createUpParameter(int upIndex) {
		return new Parameter(upIndex, -1);
	}

	/**
	 * @param downIndex index to navigate to when pressing down
	 * @return parameter with down navigation
	 */
	static Parameter createDownParameter(int downIndex) {
		return new Parameter(-1, downIndex);
	}
}
