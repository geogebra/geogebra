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

package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Point;

import com.jogamp.opengl.GLEventListener;


/**
 * Simple interface for GL JPanel/Canvas
 * 
 * @author Mathieu
 *
 */
public interface Component3D {

	void addGLEventListener(GLEventListener renderer);

	void display();

	Point getLocationOnScreen();

	Component getParent();

	Dimension getSize();

	GraphicsConfiguration getGraphicsConfiguration();

}
