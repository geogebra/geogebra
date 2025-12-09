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

package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import java.awt.Component;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian3D.Mouse3DEvent;
import org.geogebra.desktop.euclidian.event.MouseEventND;

/**
 * Class for 3D mouse event
 * 
 * @author mathieu
 *
 */
public class Mouse3DEventD extends Mouse3DEvent implements MouseEventND {

	private Component component;

	/**
	 * constructor
	 * 
	 * @param point
	 *            point
	 * @param component
	 *            target component
	 */
	public Mouse3DEventD(GPoint point, Component component) {
		super(point);
		this.component = component;
	}

	@Override
	public Component getComponent() {
		return component;
	}

}
