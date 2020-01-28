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
