/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Algorithm for SurdText(Point)
 *
 */
public class AlgoSurdTextPoint extends AlgoSurdText {

	private GeoPointND p; // input
	private GeoText text; // output
	private StringBuilder sbp;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param p
	 *            input point
	 */
	public AlgoSurdTextPoint(Construction cons, String label, GeoPointND p) {
		this(cons, p);
		text.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param p
	 *            input point
	 */
	AlgoSurdTextPoint(Construction cons, GeoPointND p) {
		super(cons);
		this.p = p;
		sbp = new StringBuilder(50);
		text = new GeoText(cons);

		text.setLaTeX(true, false);

		text.setIsTextCommand(true); // stop editing as text

		if (p.isLabelSet()) {
			try {
				text.setStartPoint(p, 0);
			} catch (CircularDefinitionException e) {
				// should never happen
			}
		}

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.SurdText;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) p;

		setOutputLength(1);
		setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public GeoText getResult() {
		return text;
	}

	@Override
	public final void compute() {
		boolean complex = p.getToStringMode() == Kernel.COORD_COMPLEX;

		if (input[0].isDefined()) {
			sbp.setLength(0);

			if (!complex) {
				sbp.append(" \\left( ");
			}

			int coordMode = p.getToStringMode();
			if (coordMode == Kernel.COORD_CARTESIAN_3D
					|| coordMode == Kernel.COORD_SPHERICAL) {
				// we want 3D coords
				Coords coords = p.getInhomCoordsInD3();
				append3dCoords(coords);
			} else if (p.isGeoElement3D()) {
				// we need to check if z == 0
				Coords coords = p.getInhomCoordsInD3();
				if (DoubleUtil.isZero(coords.getZ())) {
					// z==0 so 2D coords
					append2dCoords(coords, false);
				} else {
					// z!=0 so 3D coords
					append3dCoords(coords);
				}
			} else {
				Coords coords = p.getInhomCoordsInD2();
				append2dCoords(coords, complex);
			}

			if (!complex) {
				sbp.append(" \\right) ");
			}

			text.setTextString(sbp.toString());
			text.setLaTeX(true, false);

		} else {
			text.setTextString(complex ? "?" : "(?,?)");
		}
	}

	private void append3dCoords(Coords coords) {
		append(coords.getX());
		sbp.append(" , ");
		append(coords.getY());
		sbp.append(" , ");
		append(coords.getZ());
	}

	private void append2dCoords(Coords coords, boolean complex) {
		double x = coords.getX();
		double y = coords.getY();

		StringBuilder sb = new StringBuilder();
		pslqAppendQuadratic(sb, x, text.getStringTemplate());

		String xStr = sb.toString();

		sb.setLength(0);
		pslqAppendQuadratic(sb, y, text.getStringTemplate());

		String yStr = sb.toString();

		boolean bracketsNeeded = bracketsNeeded(yStr);

		if (complex) {

			// no "+" and only "-" is at start
			if (!yStr.contains("+") && yStr.lastIndexOf("-") == 0) {
				sbp.append(xStr);
				sbp.append("-");
				if (bracketsNeeded) {
					sbp.append("\\left(");
				}
				sbp.append(yStr.substring(1));
				if (bracketsNeeded) {
					sbp.append("\\right)");
				}
				sbp.append(Unicode.IMAGINARY);

			} else {
				sbp.append(xStr);
				sbp.append("+");
				if (bracketsNeeded) {
					sbp.append("\\left(");
				}
				sbp.append(yStr);
				if (bracketsNeeded) {
					sbp.append("\\right)");
				}
				sbp.append(Unicode.IMAGINARY);

			}

		} else {
			append(coords.getX());
			sbp.append(" , ");
			append(coords.getY());
		}
	}

	private static boolean bracketsNeeded(String str0) {
		
		String str = str0.trim();

		// everything surrounded in \frac{...}
		if (str.startsWith("\\frac{") && str.endsWith("}")) {
			return false;
		}
		
		// -sqrt(2) but not -sqrt(2)+1
		if (str.lastIndexOf("+") < 1 && str.lastIndexOf("-") < 1) {
			return false;
		}

		// eg 1 + sqrt(2)
		// or -1 + sqrt(2)
		// -sqrt(2) gets brackets, 
		return str.contains("+") || str.contains("-");
	}

	private void append(double value) {
		pslqAppendQuadratic(sbp, value, text.getStringTemplate());
	}

}
