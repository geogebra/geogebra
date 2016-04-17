package org.geogebra.desktop.awt;

import java.awt.Composite;

import org.geogebra.common.awt.GComposite;

public class GCompositeD implements org.geogebra.common.awt.GComposite {
	private Composite impl;

	public GCompositeD(Composite composite) {
		impl = composite;
	}

	public static Composite getAwtComposite(GComposite c) {
		if (!(c instanceof GCompositeD))
			return null;
		return ((GCompositeD) c).impl;
	}

}
