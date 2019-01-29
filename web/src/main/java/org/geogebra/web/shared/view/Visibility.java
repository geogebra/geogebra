package org.geogebra.web.shared.view;

public enum Visibility {

	VISIBLE,
	HIDDEN,
	NOT_SET;

	public boolean boolVal() {
		return equals(VISIBLE);
	}
}
