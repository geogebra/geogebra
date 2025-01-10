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
