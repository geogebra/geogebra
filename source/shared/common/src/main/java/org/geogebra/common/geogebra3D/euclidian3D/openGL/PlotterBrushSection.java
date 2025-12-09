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

package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * class describing the section of the brush
 * 
 * @author mathieu
 *
 */
public class PlotterBrushSection {

	/**
	 * Tick drawing step
	 */
	public enum TickStep {
		/** not drawing a tick */
		NOT,
		/** starts a tick/arrow */
		START,
		/** in middle (outer part) of the tick */
		MIDDLE,
		/** ends a tick */
		END,
		/** out of a tick drawing */
		OUT
	}

	private Manager manager;

	/** center and clock vectors */
	private Coords center;

	private Coords clockU;

	private Coords clockV;

	/** direction from last point */
	private Coords direction;

	double length;

	/** normal (for caps) */
	private final Coords normal;

	/** normal deviation along direction */
	private double normalDevD = 0;
	private double normalDevN = 1;

	/** thickness = radius of the section */
	private float thickness;

	private Coords tmpCoords = new Coords(3);

	/**
	 * constructor
	 * 
	 * @param manager
	 *            geometry manager
	 */
	public PlotterBrushSection(Manager manager) {
		this.manager = manager;
		center = Coords.createInhomCoorsInD3();
		clockU = new Coords(4);
		clockU.setUndefined();
		clockV = new Coords(4);
		clockV.setUndefined();
		direction = new Coords(4);
		direction.setUndefined();
		normal = new Coords(4);
		normal.setUndefined();
		normalDevD = 0;
	}

	private void setCenter(Coords point) {
		center.set(point);
		manager.enlargeBounds(point);
		manager.scaleXYZ(center);
	}

	/**
	 * 
	 * @param point
	 *            point
	 * @return true if section center is equal to point with Kernel standard
	 *         precision
	 */
	public boolean centerEqualsForKernel(Coords point) {
		return point.equalsForKernel(center, Kernel.STANDARD_PRECISION);
	}

	/**
	 * @param point
	 *            coordinates
	 * @param thickness
	 *            thickness
	 */
	public void set(Coords point, float thickness, Coords clockU,
			Coords clockV) {
		setCenter(point);
		this.thickness = thickness;
		this.clockU.set(clockU);
		this.clockV.set(clockV);

		direction.setUndefined();
		normal.setUndefined();
		normalDevD = 0;
	}

	/**
	 * @param point
	 *            coordinates
	 * @param thickness
	 *            thickness
	 */
	public void set(Coords point, float thickness) {
		setCenter(point);
		this.thickness = thickness;

		clockU.setUndefined();
		clockV.setUndefined();
		direction.setUndefined();
		normal.setUndefined();
		normalDevD = 0;
	}

	/**
	 * @param point
	 *            coordinates
	 * @param thickness
	 *            thickness
	 */
	public void set(PlotterBrushSection s, Coords point, float thickness,
			boolean updateClock, TickStep tick) {
		setCenter(point);
		this.thickness = thickness;

		switch (tick) {
		case START:
			normal.setMul(s.direction, -1);
			direction.set(s.direction);
			break;
		case MIDDLE:
			normal.setUndefined();
			normalDevD = 0;
			direction.set(s.direction);
			break;
		case END:
			normal.set(s.direction);
			direction.set(s.direction);
			break;
		case OUT:
			// normal will be set at next step
			direction.set(s.direction);
			break;
		case NOT:
		default:
			direction.setSub(center, s.center);

			if (center.equalsForKernel(s.center, Kernel.STANDARD_PRECISION)) {
				if (this.thickness < s.thickness) {
					normal.set(s.direction);
				} else {
					normal.setMul(s.direction, -1);
				}
				s.normal.set(normal);
				// keep last direction
				direction.set(s.direction);

				normalDevD = 0;
			} else {
				// calc normal deviation
				double dt = this.thickness - s.thickness;
				if (dt != 0) {
					direction.calcNorm();
					double l = direction.getNorm();
					double h = Math.sqrt(l * l + dt * dt);
					normalDevD = -dt / h;
					normalDevN = l / h;

					// normalDevD = 0.0000; normalDevN = 1;

					s.normalDevD = normalDevD;
					s.normalDevN = normalDevN;
				} else {
					normalDevD = 0;
				}

				direction.normalize();
				s.direction.set(direction);
				normal.setUndefined();
				s.normal.setUndefined();

				// calc new clocks
				if (updateClock) {
					direction.completeOrthonormal(s.clockU, s.clockV);
				}

			}
			break;
		}
		clockU.set(s.clockU);
		clockV.set(s.clockV);
	}

	/**
	 * set the normal vector and position for parameters u,v
	 * 
	 * @param u
	 *            cosinus
	 * @param v
	 *            sinus
	 * @param vn
	 *            normal vector
	 * @param pos
	 *            position
	 */
	public void getNormalAndPosition(double u, double v, Coords vn,
			Coords pos) {

		vn.setAdd(vn.setMul(clockU, u), tmpCoords.setMul(clockV, v));
		pos.setAdd(pos.setMul(vn, thickness), center);

		if (normal.isDefined()) {
			vn.setValues(normal, 3);
		} else if (normalDevD != 0) {
			vn.setAdd(vn.setMul(vn, normalDevN),
					tmpCoords.setMul(direction, normalDevD));
		}

	}

	/**
	 * @return the center of the section
	 */
	public Coords getCenter() {
		return center;
	}

	// //////////////////////////////////
	// FOR 3D CURVE
	// //////////////////////////////////
	/**
	 * @param s
	 *            brush section
	 * @param point
	 *            coordinates
	 * @param thickness
	 *            thickness
	 */
	public void set(PlotterBrushSection s, Coords point, float thickness) {
		setCenter(point);
		this.thickness = thickness;
		this.direction.setSub(center, s.getCenter());
		direction.calcNorm();
		length = direction.getNorm();
		direction.mulInside(1 / length);

		if (!s.clockU.isDefined()) {
			direction.completeOrthonormal(s.clockU, s.clockV);
		}

		clockV.setCrossProduct4(direction, s.clockU);
		clockV.normalize();
		// normalize it to avoid little errors propagation
		clockU.setCrossProduct4(clockV, direction);
		clockU.normalize();

		normal.setUndefined();
		normalDevD = 0;
	}

}
