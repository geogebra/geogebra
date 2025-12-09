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

package org.geogebra.desktop.cas.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.geogebra.common.awt.GDimension;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GDimensionD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.main.AppD;

/**
 * LaTeX panel for CAS output
 *
 */
public class LaTeXPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private String latex;
	private BufferedImage image;
	private double ratio = 1.0;
	private Graphics2D g2image;
	private Dimension equSize;

	/**
	 * @param app
	 *            application
	 */
	public LaTeXPanel(AppD app) {
		this.app = app;
		ensureImageSize(100, 100);
	}

	/**
	 * @param latex
	 *            LaTeX text
	 */
	public void setLaTeX(String latex) {
		if (latex.equals(this.latex)) {
			return;
		}

		this.latex = latex;

		updateLaTeX();
	}

	private void updateLaTeX() {
		// draw equation to get its size
		equSize = drawEquationToImage();

		// check if image was big enough for equation
		if (ensureImageSize(equSize.width * ratio, equSize.height * ratio)) {
			equSize = drawEquationToImage();
		}

		setPreferredSize(equSize);
		setSize(equSize);
		validate();

	}

	@Override
	public void setForeground(Color c) {
		super.setForeground(c);
		// repaint image, but keep size
		drawEquationToImage();
	}

	private Dimension drawEquationToImage() {
		if (g2image == null || latex == null) {
			return new Dimension(0, 0);
		}
		g2image.setBackground(getBackground());
		ratio = app.getImageManager().getPixelRatio();
		g2image.scale(ratio, ratio);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());

		GGraphics2DD.setAntialiasing(g2image);

		GGraphics2DD g2 = new GGraphics2DD(g2image);

		GDimension fd = app.getDrawEquation().drawEquation(app, null,
				g2, 0, 0, latex,
				app.getPlainFontCommon(), false,
				GColorD.newColor(getForeground()),
				GColorD.newColor(getBackground()), true, false, null);
		g2.scale(1 / ratio, 1 / ratio);
		return GDimensionD.getAWTDimension(fd);
	}

	private boolean ensureImageSize(double width, double height) {
		if (image == null || image.getWidth() < width
				|| image.getHeight() < height) {
			image = new BufferedImage((int) Math.ceil(width), (int) Math.ceil(height),
					BufferedImage.TYPE_INT_ARGB);
			g2image = image.createGraphics();
			return true;
		}
		return false;
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		if (latex != null) {
			updateLaTeX();
		}
	}

	@Override
	public void paint(Graphics g) {
		if (app.isExporting()) {
			app.getDrawEquation();
			// draw full resolution image directly on g
			app.getDrawEquation().drawEquation(app, null,
					new GGraphics2DD((Graphics2D) g), 0, 0, latex,
					app.getPlainFontCommon(), false,
					GColorD.newColor(getForeground()),
					GColorD.newColor(getBackground()), true, false, null);
		} else {
			// draw part of image that contains equation
			if (image != null && equSize != null) {
				g.drawImage(image, 0, 0, equSize.width, equSize.height, 0, 0,
						physicalPx(equSize.width), physicalPx(equSize.height), null);
			}
		}
	}

	private int physicalPx(int size) {
		return (int) Math.round(size * ratio);
	}

}
