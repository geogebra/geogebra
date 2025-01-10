/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.AsynchronousCommand;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.debug.Log;

/**
 * Find a limit
 * 
 * @author Michael Borcherds
 */
public class AlgoLimit extends AlgoElement
		implements AsynchronousCommand, UsesCAS {
	/** function whose limit we are finding */
	protected GeoFunction f;
	/** input number */
	protected GeoNumberValue num;
	/** result */
	protected GeoNumeric outNum;
	private String limitString;
	private ArbitraryConstantRegistry arbconst = new ArbitraryConstantRegistry(this);

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param num
	 *            number
	 */
	public AlgoLimit(Construction cons, String label, GeoFunction f,
			GeoNumberValue num) {
		super(cons);
		this.f = f;
		this.num = num;

		init(label);
	}

	private void init(String label) {
		outNum = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement
		compute();
		outNum.setLabel(label);

	}

	@Override
	public Commands getClassName() {
		return Commands.Limit;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = f;
		input[1] = num.toGeoElement();

		setOnlyOutput(outNum);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return limit result
	 */
	public GeoNumeric getResult() {
		return outNum;
	}

	@Override
	public void compute() {
		if (f == null || !f.isDefined() || !input[1].isDefined()) {
			outNum.setUndefined();
			return;
		}
		limitString = f.getLimit(num.getDouble(), getDirection());

		try {
			String numStr = kernel.evaluateCachedGeoGebraCAS(limitString,
					arbconst);

			// handles Infinity, ?
			outNum.setValue(kernel.getAlgebraProcessor()
					.evaluateToNumeric(numStr, ErrorHelper.silent())
					.getDouble());
		} catch (Throwable e) {
			Log.debug(e);
			outNum.setUndefined();
			return;
		}

	}

	@Override
	public String getCasInput() {
		return limitString;
	}

	/**
	 * 
	 * @return direction -- 0 default, -1 above, +1 below
	 */
	protected int getDirection() {
		return 0;
	}

	@Override
	public void handleCASoutput(String output, int requestID) {

		NumberValue nv = kernel.getAlgebraProcessor().evaluateToNumeric(output,
				ErrorHelper.silent());
		outNum.setValue(nv.getDouble());
		if (USE_ASYNCHRONOUS) {
			outNum.updateCascade();
		}

	}

	@Override
	public void handleException(Throwable exception, int id) {
		outNum.setUndefined();

	}

	@Override
	public boolean useCaching() {
		return true;
	}

}
