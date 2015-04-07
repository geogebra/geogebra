/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCasBase;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;

/**
 * Process a function using single argument command
 * 
 * @author Markus Hohenwarter
 */
public class AlgoCasBaseSingleArgument extends AlgoCasBase {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 */
	public AlgoCasBaseSingleArgument(Construction cons, String label,
			CasEvaluableFunction f, Commands cmd) {
		super(cons, label, f, cmd);
	}

	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		// factor value form of f
		Commands cmd = this.getClassName();
		g.setUsingCasCommand(cmd.name() + "[%]", f, false,
				arbconst);
		if (f.isDefined() && !g.isDefined()) {
			g.toGeoElement().set(f.toGeoElement());
		}
	}

	// TODO Consider locusequability
}