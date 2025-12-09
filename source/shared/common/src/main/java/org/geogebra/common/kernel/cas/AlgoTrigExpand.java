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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCasBase;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Algorithm for TrigExpand
 *
 */
public class AlgoTrigExpand extends AlgoCasBase {
	private GeoFunction target;
	private ArbitraryConstantRegistry arbconst = new ArbitraryConstantRegistry(this);

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param target
	 *            target function (ie sin or cos)
	 * @param info
	 *            evaluation flags
	 */
	public AlgoTrigExpand(Construction cons, String label,
			CasEvaluableFunction f, GeoFunction target, EvalInfo info) {
		super(cons, f, Commands.TrigExpand, info);
		this.target = target;
		setInputOutput();
		compute();
		g.setLabel(label);
	}

	@Override
	public void setInputOutput() {
		if (target != null) {
			input = new GeoElement[] { f.toGeoElement(), target };

		} else {
			input = new GeoElement[] { f.toGeoElement() };
		}
		setOnlyOutput(g);
		setDependencies();
	}

	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append("TrigExpand[%");
		if (target != null) {
			sb.append(',');
			sb.append(target.toValueString(tpl));
		}
		sb.append(']');
		g.setUsingCasCommand(sb.toString(), f, true, arbconst);
	}
}
