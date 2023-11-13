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
