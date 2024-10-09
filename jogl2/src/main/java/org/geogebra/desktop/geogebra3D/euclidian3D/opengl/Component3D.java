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
