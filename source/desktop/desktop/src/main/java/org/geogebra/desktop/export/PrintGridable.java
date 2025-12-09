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

package org.geogebra.desktop.export;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;

import org.geogebra.common.main.App.ExportType;
import org.geogebra.desktop.gui.view.Gridable;

public class PrintGridable implements Printable {

	protected Gridable gridable;
	protected int[] colWidths;
	protected int[] rowHeights;
	private double scale;
	private int titleOffset;

	/**
	 * @param g grid-able component
	 */
	public PrintGridable(Gridable g) {
		this.gridable = g;
		colWidths = gridable.getGridColwidths();
		rowHeights = gridable.getGridRowHeights();
		scale = 1;
		titleOffset = 0;
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex0)
			throws PrinterException {

		double pWidth = pageFormat.getImageableWidth();
		double pHeight = pageFormat.getImageableHeight() - this.titleOffset;

		// double pSum=0;
		int sum = 0;
		// int pagesHor=0;
		ArrayList<Integer> boundsHor = new ArrayList<>();
		boundsHor.add(sum);
		for (int i = 0; i < colWidths.length; i++) {
			if ((sum + colWidths[i]
					- boundsHor.get(boundsHor.size() - 1) > pWidth) // the
																		// next
																		// cell
																		// won't
																		// fit
					&& (sum > boundsHor.get(boundsHor.size() - 1))) { // the size
																	// increased
				boundsHor.add(sum);
			}
			sum += colWidths[i];
		}
		boundsHor.add(sum);

		sum = 0;
		ArrayList<Integer> boundsVer = new ArrayList<>();
		boundsVer.add(sum);
		for (int i = 0; i < rowHeights.length; i++) {
			if ((sum + rowHeights[i]
					- boundsVer.get(boundsVer.size() - 1) > pHeight) // the
																		// next
																		// cell
																		// won't
																		// fit
					&& (sum > boundsVer.get(boundsVer.size() - 1))) { // the size
																	// increased
				boundsVer.add(sum);
			}
			sum += rowHeights[i];
		}
		boundsVer.add(sum);
		int pagesHor = boundsHor.size() - 1;
		int pagesVer = boundsVer.size() - 1;
		int pageIndex = gridable.getApplication().getPrintPreview().adjustIndex(pageIndex0);

		if (pageIndex >= pagesHor * pagesVer) {
			return Printable.NO_SUCH_PAGE;
		}

		int px = pageIndex % pagesHor;
		int py = pageIndex / pagesHor;

		Rectangle bounds = new Rectangle(boundsHor.get(px), boundsVer.get(py),
				boundsHor.get(px + 1) - boundsHor.get(px),
				boundsVer.get(py + 1) - boundsVer.get(py));

		Graphics2D g2d = (Graphics2D) graphics;
		g2d.scale(scale, scale);
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		g2d.translate(-bounds.x, -bounds.y);
		g2d.clipRect(bounds.x, bounds.y, bounds.width, bounds.height);
		gridable.getApplication().setExporting(ExportType.PRINTING, 10);
		Component[][] comp = gridable.getPrintComponents();
		int down = 0;
		for (int i = 0; i < comp.length; i++) {
			int height = 0;
			int left = 0;
			for (int j = 0; j < comp[i].length; j++) {
				comp[i][j].print(g2d);
				g2d.translate(comp[i][j].getWidth(), 0);
				left += comp[i][j].getWidth();
				height = Math.max(height, comp[i][j].getHeight());
			}
			g2d.translate(-left, height);
			down += height;
		}
		g2d.translate(0, -down);
		g2d.setColor(Color.BLACK);
		g2d.draw(bounds);

		gridable.getApplication().setExporting(ExportType.NONE, 1);
		return Printable.PAGE_EXISTS;
	}

	void setScale(double scale) {
		this.scale = scale;
	}

	public void setTitleOffset(int offset) {
		this.titleOffset = offset;
	}

}
