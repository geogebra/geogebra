package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.media.opengl.GLEventListener;


/**
 * Simple interface for GL JPanel/Canvas
 * 
 * @author Mathieu
 *
 */


public interface Component3D {

	public void addGLEventListener(GLEventListener renderer);

	public void display();

	public Point getLocationOnScreen();

	public Component getParent();

	public Dimension getSize();
	

}
