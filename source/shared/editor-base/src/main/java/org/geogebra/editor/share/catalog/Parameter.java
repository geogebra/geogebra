/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
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
