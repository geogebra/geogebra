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

package org.geogebra.jogl2.test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class TestSeveralViews {

	/**
	 * @param args command line args
	 */
	public static void main(String[] args) {
		final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		Container c = frame.getContentPane();
		c.setLayout(new FlowLayout());

		View view = new View(1f, 0f, 0f, 1);
		Component canvas = (Component) view.canvas;
		canvas.setPreferredSize(new Dimension(100, 100));
		c.add(canvas);

		view = new View(0f, 1f, 0f, 2);
		canvas = (Component) view.canvas;
		canvas.setPreferredSize(new Dimension(100, 100));
		c.add(canvas);

		view = new View(0f, 0f, 1f, 4);
		canvas = (Component) view.canvas;
		canvas.setPreferredSize(new Dimension(100, 100));
		c.add(canvas);

		frame.setTitle("TestSeveralViews");
		frame.pack();
		frame.setVisible(true);
	}
}
