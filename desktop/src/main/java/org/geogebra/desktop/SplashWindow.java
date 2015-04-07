/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop;

/*
 * Copyright (c) 1999-2003 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland
 * All rights reserved.
 *
 * This material is provided "as is", with absolutely no warranty expressed
 * or implied. Any use is at your own risk.
 *
 * Permission to use or copy this software is hereby granted without fee,
 * provided this copyright notice is retained on all copies.
 */
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Splash Window to show an image during startup of an application.
 * <p>
 * 
 * Usage:
 * 
 * <pre>
 * // open the splash window
 * Frame splashOwner = SplashWindow.splash(anImage);
 * 
 * // start the application
 * // ...
 * 
 * // dispose the splash window by disposing the frame that owns the window.
 * splashOwner.dispose();
 * </pre>
 * 
 * <p>
 * To use the splash window as an about dialog write this:
 * 
 * <pre>
 * new SplashWindow(this, getToolkit().createImage(
 * 		getClass().getResource(GeoGebra.SPLASH_STRING))).show();
 * </pre>
 * 
 * The splash window disposes itself when the user clicks on it.
 * 
 * @author Werner Randelshofer, Staldenmattweg 2, Immensee, CH-6405,
 *         Switzerland.
 * @version 1.3 2003-06-01 Revised.
 */
public class SplashWindow extends Window {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Image splashImage;

	/**
	 * This attribute indicates whether the method paint(Graphics) has been
	 * called at least once since the construction of this window.<br>
	 * This attribute is used to notify method splash(Image) that the window has
	 * been drawn at least once by the AWT event dispatcher thread.<br>
	 * This attribute acts like a latch. Once set to true, it will never be
	 * changed back to false again.
	 * 
	 * @see #paint
	 * @see #splash
	 */
	private boolean paintCalled = false;

	/**
	 * Constructs a splash window and centers it on the screen. The user can
	 * click on the window to dispose it.
	 * 
	 * @param owner
	 *            The frame owning the splash window.
	 * @param splashImage
	 *            The splashImage to be displayed.
	 * @param canDispose
	 *            If a mouse click should dispose the window
	 */
	public SplashWindow(Frame owner, Image splashImage, boolean canDispose) {
		super(owner);
		this.splashImage = splashImage;

		// Load the image
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(splashImage, 0);
		try {
			mt.waitForID(0);
		} catch (InterruptedException ie) {
		}

		// Center the window on the screen.
		int imgWidth = splashImage.getWidth(this);
		int imgHeight = splashImage.getHeight(this);
		setSize(imgWidth, imgHeight);
		setLocationRelativeTo(null);

		// Florian Sonner (24.6.09)
		if (canDispose) {
			// Users shall be able to close the splash window by
			// clicking on its display area. This mouse listener
			// listens for mouse clicks and disposes the splash window.
			MouseAdapter disposeOnClick = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent evt) {
					// Note: To avoid that method splash hangs, we
					// must set paintCalled to true and call notifyAll.
					// This is necessary because the mouse click may
					// occur before the contents of the window
					// has been painted.
					synchronized (SplashWindow.this) {
						paintCalled = true;
						notifyAll();
					}
					setVisible(false);
				}
			};
			addMouseListener(disposeOnClick);
		}
	}

	/**
	 * Updates the display area of the window.
	 */
	@Override
	public void update(Graphics g) {
		// Note: Since the paint method is going to draw an
		// image that covers the complete area of the component we
		// do not fill the component with its background color
		// here. This avoids flickering.
		g.setColor(getForeground());
		paint(g);
	}

	/**
	 * Paints the image on the window.
	 */
	@Override
	public void paint(Graphics g) {
		g.drawImage(splashImage, 0, 0, this);

		// Markus Hohenwarter (14. 4. 2006): add border to splashImage
		g.setColor(Color.darkGray);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		// Notify method splash that the window
		// has been painted.
		// Note: To improve performance we do not enter
		// the synchronized block unless we have to.
		if (!paintCalled) {
			paintCalled = true;
			synchronized (this) {
				notifyAll();
			}
		}
	}

	/**
	 * Constructs and displays a SplashWindow.
	 * <p>
	 * This method is useful for startup splashs. Dispose the return frame to
	 * get rid of the splash window.
	 * <p>
	 * 
	 * @param splashImage
	 *            The image to be displayed.
	 * @return Returns the frame that owns the SplashWindow.
	 */
	public static Frame splash(Image splashImage) {
		Frame f = new Frame();
		SplashWindow w = new SplashWindow(f, splashImage, false);

		// Show the window.
		w.toFront();
		w.setVisible(true);

		// Note: To make sure the user gets a chance to see the
		// splash window we wait until its paint method has been
		// called at least by the AWT event dispatcher thread.
		if (!EventQueue.isDispatchThread()) {
			synchronized (w) {
				while (!w.paintCalled) {
					try {
						w.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}

		return f;
	}
}
