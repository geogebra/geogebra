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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Two tangents through point P to conic section c
 */
public abstract class AlgoCommonTangentsND extends AlgoElement
		implements TangentAlgo {
	protected GeoConicND c;
	protected GeoConicND d;

	/**
	 * @param c
	 *            construction
	 */
	public AlgoCommonTangentsND(Construction c) {
		super(c);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("CommonTangentOfCirclesAandB",
				"Common tangent of conics %0 and %1",
				c.getLabel(tpl), d.getLabel(tpl));
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Tangent;
	}

}