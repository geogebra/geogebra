/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.controller;

public enum ExpRelation {
	EMPTY("empty %0"), END_OF("end of %0"), START_OF("start of %0"), AFTER(
			"after %0"), BEFORE("before %0");

	private final String pattern;

	ExpRelation(String s) {
		this.pattern = s;
	}

	@Override
	public String toString() {
		return pattern;
	}

}
