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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author Markus + Mathieu
 */
public abstract class AlgoSphereNDTwoPoints extends AlgoElement {

	private GeoPointND M; // input
	private GeoPointND P; // input
	private GeoQuadricND sphereND; // output

	/**
	 * @param cons
	 *            construction
	 * @param M
	 *            center
	 * @param P
	 *            point on sphere
	 */
	public AlgoSphereNDTwoPoints(Construction cons, GeoPointND M,
			GeoPointND P) {
		super(cons);
		this.M = M;
		this.P = P;
		sphereND = createSphereND(cons);
		setInputOutput(); // for AlgoElement

		compute();

		sphereND.setToSpecificForm();
	}

	abstract protected GeoQuadricND createSphereND(Construction cons1);

	protected AlgoSphereNDTwoPoints(Construction cons, String label,
			GeoPointND M, GeoPointND P) {
		this(cons, M, P);
		sphereND.setLabel(label);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) M;
		input[1] = (GeoElement) P;

		setOnlyOutput(sphereND);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting sphere
	 */
	public GeoQuadricND getSphereND() {
		return sphereND;
	}

	/**
	 * Method added for LocusEqu project.
	 * 
	 * @return center of sphere.
	 */
	public GeoPointND getCenter() {
		return this.getM();
	}

	protected GeoPointND getM() {
		return M;
	}

	/**
	 * Method added for LocusEqu project.
	 * 
	 * @return external point of sphere.
	 */
	public GeoPointND getExternalPoint() {
		return this.getP();
	}

	protected GeoPointND getP() {
		return P;
	}

	// compute circle with midpoint M and radius r
	@Override
	public final void compute() {
		sphereND.setSphereND(M, P);
	}

}
