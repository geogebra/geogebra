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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadric extends AlgoElement3D {

	private GeoQuadric3D quadric;
	private GeoElementND secondInput;
	private GeoNumberValue number;

	private AlgoQuadricComputer computer;

	/**
	 * @param c
	 *            construction
	 * @param secondInput
	 *            second input
	 * @param number
	 *            radius or angle
	 * @param computer
	 *            quadric computer
	 */
	public AlgoQuadric(Construction c, GeoElementND secondInput,
			GeoNumberValue number, AlgoQuadricComputer computer) {
		this(c, secondInput, number, computer, true);
	}

	/**
	 * @param c
	 *            construction
	 * @param secondInput
	 *            second input
	 * @param number
	 *            radius or angle
	 * @param computer
	 *            quadric computer
	 * @param addToConstructionList
	 *            whether to add this to construction
	 */
	public AlgoQuadric(Construction c, GeoElementND secondInput,
			GeoNumberValue number, AlgoQuadricComputer computer,
			boolean addToConstructionList) {
		super(c, addToConstructionList);
		quadric = computer.newQuadric(c);
		this.number = number;

		this.secondInput = secondInput;

		this.computer = computer;

	}

	protected AlgoQuadricComputer getComputer() {
		return computer;
	}

	/**
	 * 
	 * @return second input
	 */
	protected GeoElementND getSecondInput() {
		return secondInput;
	}

	/**
	 * 
	 * @return radius or angle
	 */
	protected GeoNumberValue getNumber() {
		return number;
	}

	/**
	 * 
	 * @return direction of the axis
	 */
	protected abstract Coords getDirection();

	/**
	 * @return the cone
	 */
	public GeoQuadric3D getQuadric() {
		return quadric;
	}

	/*
	 * This should apply to every subclass. In case it does not, a case per case
	 * should be used.
	 */

}
