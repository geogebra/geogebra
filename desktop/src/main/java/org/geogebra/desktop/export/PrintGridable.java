/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Gridable.java
 *
 * Created on 18.08.2011, 17:37
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

		int pageIndex = gridable.getApplication().getPrintPreview().adjustIndex(pageIndex0);
		
		double pWidth = pageFormat.getImageableWidth();
		double pHeight = pageFormat.getImageableHeight() - this.titleOffset;

		// double pSum=0;
		int sum = 0;
		// int pagesHor=0;
		ArrayList<Integer> boundsHor = new ArrayList<>();
		boundsHor.add(sum);
		for (int i = 0; i < colWidths.length; i++) {
			if ((sum + colWidths[i]
					- boundsHor.get(boundsHor.size() - 1) > pWidth) && // the
																		// next
																		// cell
																		// won't
																		// fit
					(sum > boundsHor.get(boundsHor.size() - 1))) { // the size
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
					- boundsVer.get(boundsVer.size() - 1) > pHeight) && // the
																		// next
																		// cell
																		// won't
																		// fit
					(sum > boundsVer.get(boundsVer.size() - 1))) { // the size
																	// increased
				boundsVer.add(sum);
			}
			sum += rowHeights[i];
		}
		boundsVer.add(sum);
		int pagesHor = boundsHor.size() - 1;
		int pagesVer = boundsVer.size() - 1;

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
