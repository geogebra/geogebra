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
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

public class AlgoCasLoaded extends AlgoElement implements UsesCAS {
	private final boolean casEnabled;
	private GeoBoolean output;

	/**
	 *
	 * @param c the construction.
	 */
	public AlgoCasLoaded(Construction c) {
		super(c);
		casEnabled = kernel.getApplication().getConfig().isCASEnabled();
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		output = new GeoBoolean(cons, false);
		setOnlyOutput(output);
		input = new GeoElement[0];
		setDependencies();
	}

	@Override
	public void compute() {
		output.setValue(casEnabled
				&& kernel.getGeoGebraCAS().getCurrentCAS().isLoaded());
	}

	@Override
	public GetCommand getClassName() {
		return Commands.CASLoaded ;
	}

	public GeoBoolean getResult() {
		return output;
	}
}
