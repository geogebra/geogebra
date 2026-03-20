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

package org.geogebra.desktop.gui.util;

import java.awt.Image;

import javax.swing.Icon;

import org.geogebra.desktop.main.ScaledIcon;

public class JoinedScaledIcon extends ScaledIcon {
	private final Icon leftIcon;

	/**
	 * Creates joined icon.
	 * @param imageIcon joned icon without scale
	 * @param scale scale
	 * @param leftIcon main icon
	 */
	public JoinedScaledIcon(Image imageIcon, double scale, Icon leftIcon) {
		super(imageIcon, scale);
		this.leftIcon = leftIcon;
	}

	public Icon getLeftIcon() {
		return leftIcon;
	}
}
