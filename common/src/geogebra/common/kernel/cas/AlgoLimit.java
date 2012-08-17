/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Find a limit
 * 
 * @author Michael Borcherds
 */
public class AlgoLimit extends AlgoElement implements AsynchronousCommand{

	protected GeoFunction f;
	protected NumberValue num; // input
	protected GeoNumeric outNum; // output

	protected StringBuilder sb = new StringBuilder();
	private String limitString;

	public AlgoLimit(Construction cons, String label, GeoFunction f,
			NumberValue num) {
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
	public Algos getClassName() {
		return Algos.AlgoLimit;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = f;
		input[1] =  num.toGeoElement();

		setOutputLength(1);
		setOutput(0, outNum);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return outNum;
	}

	// over-ridden in LimitAbove/Below
	@Override
	public void compute() {
		if (!f.isDefined() || !input[1].isDefined()) {
			outNum.setUndefined();
			return;
		}
		limitString = f.getLimit(num.getDouble(), getDirection());
		if(f==null){
			outNum.setUndefined();
			return;
		}
		outNum.setUndefined();
		kernel.evaluateGeoGebraCASAsync(this);
	}
	
	public String getCasInput(){
		return limitString;
	}
	/**
	 * 
	 * @return direction -- 0 default, -1 above, +1 below
	 */
	protected int getDirection(){
		return 0;
	}
	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}
	
	

	public void handleCASoutput(String output, int requestID) {
		
		NumberValue nv = kernel.getAlgebraProcessor().evaluateToNumeric(
					output, true);
		outNum.setValue(nv.getDouble());
		if(USE_ASYNCHRONOUS)
			outNum.updateCascade();
		
	}

	public void handleException(Throwable exception,int id) {
		outNum.setUndefined();
		
	}

	public boolean useCacheing() {
		return true;
	}

	// TODO Consider locusequability

}
