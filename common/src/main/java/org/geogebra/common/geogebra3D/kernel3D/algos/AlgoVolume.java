/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.HasVolume;

/**
 * Computes the area of a polygon
 * 
 * @author mathieu
 */
public class AlgoVolume extends AlgoElement {

	private HasVolume hasVolume; // input
	private GeoNumeric volume; // output

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param hasVolume
	 *            geo that has a volume
	 */
	public AlgoVolume(Construction cons, String label, HasVolume hasVolume) {
		super(cons);
		this.hasVolume = hasVolume;
		volume = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();

		volume.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Volume;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_VOLUME;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) hasVolume;

		setOutputLength(1);
		setOutput(0, volume);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * 
	 * @return volume output
	 */
	public GeoNumeric getVolume() {
		return volume;
	}

	@Override
	public final void compute() {
		volume.setValue(hasVolume.getVolume());
	}

}
