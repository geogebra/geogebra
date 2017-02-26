/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */
package org.geogebra.desktop.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.desktop.main.AppD;

import com.himamis.retex.renderer.desktop.graphics.ColorD;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;

/**
 * A JPanel to preview LaTeX on typing !
 *
 * @author Calixte DENIZET
 */
public class LaTeXPreviewerPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int INSET = 3;

	private static final int defaultSize = 15;

	private BufferedImage im;
	// private Icon icon;
	private int width;
	private int height;

	/**
	 * Default constructor
	 */
	public LaTeXPreviewerPanel() {
		// I disable the double-buffering, it's useless here
		super(false);
	}

	public void setLaTeX(AppD app, String str0) {
		String str = str0;
		if (str.indexOf('"') > -1) {

			GeoText text = app.getKernel().getAlgebraProcessor()
					.evaluateToText(str, false, false);

			if (text != null) {
				text.setLaTeX(true, false);
				str = text.getTextString();
			} else {
				// bad syntax, remove all quotes and use raw string
				while (str.indexOf('"') > -1)
				 {
					str = str.replace('"', ' ');
				// latexPreview.setLaTeX(str);
				}
			}
		} else {
			// latexPreview.setLaTeX(str);

		}

		String f = str.trim();
		if (f.length() >= 2 && f.startsWith("$") && f.endsWith("$")) {
			f = f.substring(1, f.length() - 1);
		}

		im = (BufferedImage) TeXFormula.getPartialTeXFormula(f)
				.createBufferedImage(TeXConstants.STYLE_DISPLAY, defaultSize,
						new ColorD(Color.black), new ColorD(Color.white));

		/*
		 * icon = TeXFormula.getPartialTeXFormula(f).createTeXIcon(
		 * TeXConstants.STYLE_DISPLAY, defaultSize); if (icon == null) { icon =
		 * TeXFormula.getPartialTeXFormula("").createTeXIcon(
		 * TeXConstants.STYLE_DISPLAY, defaultSize); }
		 * 
		 * width = icon.getIconWidth(); height = icon.getIconHeight();
		 */
		width = im.getWidth();
		height = im.getHeight();
		Dimension dim = new Dimension(width + 2 * INSET, height + 2 * INSET);
		setPreferredSize(dim);
		setSize(dim);
		setLocation(0, 0);
		setVisible(true);
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		/*
		 * if (icon != null) { g.setColor(Color.WHITE); g.fillRect(0, 0, width +
		 * 2 * INSET, height + 2 * INSET); // g.setColor(Color.BLACK); //
		 * g.drawRect(0, 0, width + 2 * INSET - 1, height + 2 * INSET - 1);
		 * icon.paintIcon(this, g, INSET, INSET); }
		 */
		if (im != null) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width + 2 * INSET, height + 2 * INSET);
			g.drawImage(im, 0, 0, null);
		}
	}
}
