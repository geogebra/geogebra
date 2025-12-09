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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.Rotatable;

/**
 *
 * @author Markus
 */
public class AlgoRotate extends AlgoTransformation {

	private GeoNumberValue angle;
	private GeoElement angleGeo;

	/**
	 * Creates new generic rotation algo
	 */
	AlgoRotate(Construction cons, String label, GeoElement A,
			GeoNumberValue angle) {
		this(cons, A, angle);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new unlabeled rotation algo
	 */
	public AlgoRotate(Construction cons, GeoElement A, GeoNumberValue angle) {
		super(cons);
		this.angle = angle;

		angleGeo = angle.toGeoElement();
		inGeo = A;

		// create output object
		outGeo = getResultTemplate(inGeo);

		setInputOutput();
		compute();
		if (inGeo.isGeoFunction()) {
			cons.registerEuclidianViewCE(this);
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.Rotate;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inGeo;
		input[1] = angle.toGeoElement();

		setOnlyOutput(outGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the rotated object
	 * 
	 * @return rotated object
	 */
	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	// calc rotated point
	@Override
	public final void compute() {
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}
		if (inGeo instanceof GeoFunction) {
			((GeoFunction) inGeo)
					.toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else {
			setOutGeo();
		}
		if (!outGeo.isDefined()) {
			return;
		}

		if (outGeo instanceof Rotatable) {
			((Rotatable) outGeo).rotate(angle);
		}
		if (inGeo.isLimitedPath()) {
			this.transformLimitedPath(inGeo, outGeo);
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("ARotatedByAngleB",
				"%0 rotated by angle %1",
				inGeo.getLabel(tpl), angleGeo.getLabel(tpl));
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoFunction) {
			return new GeoCurveCartesian(cons);
		}
		return super.getResultTemplate(geo);
	}

	@Override
	public double getAreaScaleFactor() {
		return 1;
	}

}
