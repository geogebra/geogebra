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

		setOnlyOutput(volume);
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
