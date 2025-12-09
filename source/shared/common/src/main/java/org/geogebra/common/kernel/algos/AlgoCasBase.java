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
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Base class for algorithms using the CAS.
 * 
 * @author Markus Hohenwarter
 */
public abstract class AlgoCasBase extends AlgoElement implements UsesCAS {
	/** Input function */
	protected CasEvaluableFunction f;
	/** Output function */
	protected CasEvaluableFunction g;
	private Commands cmd;

	/**
	 * Creates CAS algo and sets input, output and label. Do not use if
	 * compute() or setInputOutput() are overridden.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            input function
	 * @param cmd
	 *            command name
	 * @param info
	 *            evaluation flags
	 */
	protected AlgoCasBase(Construction cons, String label,
			CasEvaluableFunction f, Commands cmd, EvalInfo info) {
		this(cons, f, cmd, info);

		setInputOutput(); // for AlgoElement
		compute();
		g.toGeoElement().setLabel(label);
	}

	/**
	 * Creates CAS algo, doesn't set any input, output or label
	 * 
	 * @param cons
	 *            construction
	 * @param f
	 *            input function
	 * @param cmd
	 *            command name
	 * @param info
	 *            evaluation flags
	 */
	protected AlgoCasBase(Construction cons, CasEvaluableFunction f,
			Commands cmd, EvalInfo info) {
		super(cons);
		f.updateCASEvalMap(info.getCASMap());
		this.f = f;
		this.cmd = cmd;
		g = (CasEvaluableFunction) f.toGeoElement().copyInternal(cons);
	}

	@Override
	public final Commands getClassName() {
		return cmd;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f.toGeoElement();

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting function
	 */
	public GeoElement getResult() {
		return g.toGeoElement();
	}

	@Override
	public final void compute() {
		if (!f.toGeoElement().isDefined()) {
			g.toGeoElement().setUndefined();
			return;
		}

		applyCasCommand(StringTemplate.prefixedDefault);
	}

	/**
	 * Build a GeoGebraCAS command, send it to CAS and use the output to update
	 * result of this algo.
	 * 
	 * @param tpl
	 *            template for serialization of the command
	 */
	protected abstract void applyCasCommand(StringTemplate tpl);

	/**
	 * Clears the cache (needed in Web when the CAS loads)
	 */
	public final void clearCasEvalMap() {
		f.clearCasEvalMap();
	}
}
