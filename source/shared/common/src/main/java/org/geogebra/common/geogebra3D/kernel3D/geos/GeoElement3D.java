/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoElement.java
 *
 * Created on 30. August 2001, 17:10
 */

package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.debug.Log;

/**
 * Class for describing GeoElement in 3D version.
 *
 * <h3>How to create a new Element</h3>
 * <p>
 * We'll call here our new element "GeoNew3D"
 * </p><p>
 * 
 * <b> In GeoElement3D (this class), create an new constant to identify GeoNew3D
 * </b>
 * </p>
 * <code> public static final int GEO_CLASS_NEW3D = 30??; </code>
 * <b> Create an new class GeoNew3D </b>
 * <ul>
 * <li>It will eventually extend another class (at least GeoElement3D) :<br>
 * <code>
   final public class GeoNew3D extends ??? {
   </code></li>
 * <li>Your IDE will add auto-generated methods; modify it :<br>
 * <pre><code>
    public GeoElement copy() {
        return null;
    }

    public int getGeoClassType() {
       return GEO_CLASS_NEW3D;
    }

    protected String getTypeString() {
        return "New3D";
    }

    public boolean isDefined() {
       return true;
    }

    public boolean isEqual(GeoElementND Geo) {
       return false;
    }

    public void set(GeoElement geo) {

    }

    public void setUndefined() {

    }

    protected boolean showInAlgebraView() {
        return true;
    }

    protected boolean showInEuclidianView() {
       return true;
    }

    public String toValueString() {
        return "todo";
    }

    protected String getClassName() {
        return "GeoNew3D";
    }
  </code></pre></li>
 * <li>Create a constructor <br>
 * <code>
    public GeoNew3D(Construction c, ?? args) { <br> &nbsp;&nbsp;
        super(c); // eventually + args <br> &nbsp;&nbsp;
        + stuff <br>
    }
   </code></li>
 * </ul>
 * 
 * <h3>See</h3>
 * <ul>
 * <li>{@link Drawable3D} to create a drawable linked to this new element.</li>
 * <li>{@link Kernel3D} to add a method to create this new element</li>
 * <li>
 * {@link TestGeo} Kernel3D, geogebra.euclidian.EuclidianView,
 * geogebra3D.euclidian3D.EuclidianView3D, geogebra3D.Application3D)} to test it
 * </li>
 * </ul>
 * 
 */
public abstract class GeoElement3D extends GeoElement {
	private StringBuilder sbToString;
	private StringBuilder sbBuildValueString = new StringBuilder(50);

	/**
	 * Creates new GeoElement for given construction
	 * 
	 * @param c
	 *            construction
	 */
	public GeoElement3D(Construction c) {
		super(c);
	}

	/**
	 * it's a 3D GeoElement.
	 * 
	 * @return true
	 */
	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	@Override
	abstract public Coords getLabelPosition();

	/**
	 * set the alpha value to alpha for openGL
	 * 
	 * @param alpha
	 *            alpha value
	 */
	@Override
	public void setAlphaValue(double alpha) {
		if (alpha < 0.0 || alpha > 1.0) {
			return;
		}
		alphaValue = alpha;
	}

	// //////////////////////////
	// for toString()

	/**
	 * @return builder for toString
	 */
	protected StringBuilder getSbToString() {
		if (sbToString == null) {
			sbToString = new StringBuilder(50);
		}
		return sbToString;
	}

	/**
	 * @return builder for toValueString
	 */
	protected StringBuilder getSbBuildValueString() {
		if (sbBuildValueString == null) {
			sbBuildValueString = new StringBuilder();
		} else {
			sbBuildValueString.setLength(0);
		}
		return sbBuildValueString;
	}

	@Override
	public boolean isWhollyIn2DView(EuclidianView ev) {
		Log.debug("isWhollyIn2DView unimplemented for " + this.getClass() + " "
				+ this.getGeoClassType());
		return false;
	}

}