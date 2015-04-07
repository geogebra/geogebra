package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * class describing the section of the brush
 * 
 * @author mathieu
 *
 */
public class PlotterBrushSection {

	/** center and clock vectors */
	Coords center;

	private Coords clockU;

	private Coords clockV;

	/** direction from last point */
	private Coords direction;

	double length;

	/** normal (for caps) */
	private Coords normal = null;

	/** normal deviation along direction */
	private double normalDevD = 0;
	private double normalDevN = 1;

	/** thickness = radius of the section */
	private float thickness;

	/**
	 * constructor
	 */
	public PlotterBrushSection() {
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

	public void set(Coords point, float thickness, Coords clockU, Coords clockV) {
		this.center.set(point);
		this.thickness = thickness;
		this.clockU.set(clockU);
		this.clockV.set(clockV);

		direction.setUndefined();
		normal.setUndefined();
		normalDevD = 0;
	}

	public void set(Coords point, float thickness) {
		this.center.set(point);
		this.thickness = thickness;

		clockU.setUndefined();
		clockV.setUndefined();
		direction.setUndefined();
		normal.setUndefined();
		normalDevD = 0;
	}

	public void set(PlotterBrushSection s, Coords point, float thickness,
			boolean updateClock) {
		this.center.set(point);
		this.thickness = thickness;

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
				// Application.debug("dt="+dt+",normalDev="+normalDevD+","+normalDevN);
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
		clockU.set(s.clockU);
		clockV.set(s.clockV);

		// Application.debug("direction=\n"+direction.toString());
	}

	private Coords tmpCoords = new Coords(3);

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
	public void getNormalAndPosition(double u, double v, Coords vn, Coords pos) {

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

	public void set(PlotterBrushSection s, Coords point, float thickness) {
		this.center.set(point);
		this.thickness = thickness;
		this.direction.setSub(center, s.getCenter());
		direction.calcNorm();
		length = direction.getNorm();
		direction.mulInside(1 / length);

		if (!s.clockU.isDefined()) {
			direction.completeOrthonormal(s.clockU, s.clockV);
		}

		clockV.setCrossProduct(direction, s.clockU);
		clockV.normalize();
		// normalize it to avoid little errors propagation
		clockU.setCrossProduct(clockV, direction);
		clockU.normalize();

		normal.setUndefined();
		normalDevD = 0;
	}

}
