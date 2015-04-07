package org.geogebra.desktop.javax.swing;

import javax.swing.ImageIcon;

import org.geogebra.common.javax.swing.GImageIcon;

public class GImageIconD extends GImageIcon {

	private ImageIcon impl;

	public GImageIconD(ImageIcon ii) {
		impl = ii;
	}

	public ImageIcon getImpl() {
		return impl;
	}
}
