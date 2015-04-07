package org.geogebra.desktop.awt;

public class GCompositeD implements org.geogebra.common.awt.GComposite {
	private java.awt.Composite impl;

	public GCompositeD(java.awt.Composite composite) {
		impl = composite;
	}

	public static java.awt.Composite getAwtComposite(
			org.geogebra.common.awt.GComposite c) {
		if (!(c instanceof GCompositeD))
			return null;
		return ((GCompositeD) c).impl;
	}

}
