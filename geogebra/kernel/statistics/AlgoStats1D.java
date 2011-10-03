/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;


/**
 * Mean, variance, sum, sum of squares, standard deviation of a list
 * adapted from AlgoListMin
 * to replace AlgoMean, AlgoSum
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public abstract class AlgoStats1D extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
	public GeoNumeric Truncate; //input	
	public GeoNumeric result; //output	

	private int stat;

	final static int STATS_MEAN = 0;
	final static int STATS_VARIANCE = 1;
	final static int STATS_SIGMAX = 2;
	final static int STATS_SIGMAXX = 3;
	final static int STATS_SD = 4;
	final static int STATS_PRODUCT = 5;
	final static int STATS_SXX = 6;
	final static int STATS_SAMPLE_VARIANCE = 7;
	final static int STATS_SAMPLE_SD = 8;

	public AlgoStats1D(Construction cons, String label, GeoList geoList, int stat) {
		this(cons, label, geoList, null, stat);
	}

	AlgoStats1D(Construction cons, String label, GeoList geoList, GeoNumeric Truncate, int stat) {
		this(cons, geoList, Truncate, stat);
		result.setLabel(label);
	}

	AlgoStats1D(Construction cons, GeoList geoList, GeoNumeric Truncate, int stat) {
		super(cons);
		this.geoList = geoList;
		this.stat=stat;
		this.Truncate=Truncate;

		if (geoList.size() > 0 && geoList.get(0).isAngle())
			result = new GeoAngle(cons);
		else
			result = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	public AlgoStats1D(Construction cons, GeoList geoList, int stat) {
		this(cons, geoList, null, stat);
	}

	public abstract String getClassName();

	protected void setInputOutput(){
		if (Truncate == null) {
			input = new GeoElement[1];
			input[0] = geoList;
		}
		else {
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = Truncate;
		}

		output = new GeoElement[1];
		output[0] = result;
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return result;
	}


	protected final void compute() {

		// TODO: remove
		//Application.debug("compute: " + geoList);
		if (!geoList.isDefined()) {
			result.setUndefined();
			return;
		}

		int truncate;
		int size = geoList.size();

		if (Truncate != null)
		{
			truncate=(int)Truncate.getDouble();
			if (truncate == 0) {
				result.setValue(0);
				return;
			}
			if (truncate < 1 || truncate > size)
			{
				result.setUndefined();
				return;
			}
			size = truncate; // truncate the list
		}

		if (size == 0) {
			switch (stat)
			{
			case STATS_SIGMAX:
			case STATS_SIGMAXX:
				result.setValue(0);
				return;
			case STATS_PRODUCT:
				result.setValue(1);
				return;
			default:
				result.setUndefined();
			return;
			}
		}


			double sumVal = 0;
			double sumSquares = 0;
			double product = 1;
			double val;
			for (int i=0; i < size; i++) {
				GeoElement geo = geoList.get(i);
				if (geo.isNumberValue()) {
					NumberValue num = (NumberValue) geo;
					val=num.getDouble();
					sumVal += val;
					sumSquares += val*val;
					product *= val;
				} else {
					result.setUndefined();
					return;
				}    		    		
			}   

			double mu=sumVal/(double)size;
			double var;

			switch (stat)
			{
			case STATS_MEAN:
				result.setValue(mu);
				break;
			case STATS_SD:
				var=sumSquares/(double)size-mu*mu;
				result.setValue(Math.sqrt(var));
				break;
			case STATS_SAMPLE_SD:
				var=(sumSquares - sumVal * sumVal / size) / (size -1);
				result.setValue(Math.sqrt(var));
				break;
			case STATS_VARIANCE:
				var=sumSquares/(double)size-mu*mu;
				result.setValue(var);
				break;
			case STATS_SAMPLE_VARIANCE:
				var=(sumSquares - sumVal * sumVal / size) / (size -1);
				result.setValue(var);
				break;
			case STATS_SXX:
				var=sumSquares - (sumVal * sumVal) / (double)size;
				result.setValue(var);
				break;
			case STATS_SIGMAX:
				result.setValue(sumVal);
				break;
			case STATS_SIGMAXX:
				result.setValue(sumSquares);
				break;
			case STATS_PRODUCT:
				result.setValue(product);
				break;
			}
		}

	}


