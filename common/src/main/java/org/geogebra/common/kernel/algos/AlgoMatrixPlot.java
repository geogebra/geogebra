/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCanvasImage;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.util.GgbMat;

/**
 * Draws a matrix plot.
 * 
 * @author G.Sturr
 *
 */
public class AlgoMatrixPlot extends AlgoElement {

	private GeoList inputList; // input
	private GeoCanvasImage outputImage; // output
	private double[][] data;

	public AlgoMatrixPlot(Construction cons, String label, GeoList inputList) {
		this(cons, inputList);
		outputImage.setLabel(label);
	}

	public AlgoMatrixPlot(Construction cons, GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		outputImage = new GeoCanvasImage(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.MatrixPlot;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = inputList;
		super.setOutputLength(1);
		super.setOutput(0, outputImage);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting list
	 */
	public GeoCanvasImage getResult() {
		return outputImage;
	}

	@Override
	public final void compute() {

		GgbMat matrix = new GgbMat(inputList);

		if (matrix.isUndefined()) {
			outputImage.setUndefined();
			return;
		}

		data = matrix.getData();
		drawPlot();
	}

	private void drawPlot() {

		GGraphics2D g = outputImage.getGraphics();

		int width = outputImage.getWidth();
		int height = outputImage.getHeight();

		g.setPaint(GColor.WHITE);
		g.fillRect(0, 0, width, height);

		int rowStep = height / data.length;
		int columnStep = width / data[0].length;

		// draw tiles
		for (int row = 0; row < data.length; row += 1) {
			for (int col = 0; col < data[0].length; col += 1) {
				int c = (int) (data[row][col] * 256) % 256;
				g.setColor(GColor.newColor(c, c, c, 150));
				g.fillRect(columnStep * (col), rowStep * (row), columnStep,
						rowStep);
			}
		}

		// draw grid
		g.setPaint(GColor.BLACK);

		for (int row = 0; row <= height; row += rowStep) {
			g.drawLine(0, row, width, row);
		}
		for (int col = 0; col <= width; col += columnStep) {
			g.drawLine(col, 0, col, height);
		}

	}

	

}
