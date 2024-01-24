package org.geogebra.desktop.javax.swing;

import javax.swing.Icon;

import org.geogebra.common.javax.swing.GImageIcon;

public class GImageIconD extends GImageIcon {

	private Icon impl;

	public GImageIconD(Icon ii) {
		impl = ii;
	}

	public Icon getImpl() {
		return impl;
	}
}
