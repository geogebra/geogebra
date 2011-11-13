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
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;

import java.util.ArrayList;

import org.apache.commons.math.distribution.BinomialDistribution;
import org.apache.commons.math.distribution.BinomialDistributionImpl;
import org.apache.commons.math.distribution.CauchyDistribution;
import org.apache.commons.math.distribution.CauchyDistributionImpl;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.distribution.ExponentialDistribution;
import org.apache.commons.math.distribution.ExponentialDistributionImpl;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.distribution.GammaDistribution;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.distribution.HypergeometricDistribution;
import org.apache.commons.math.distribution.HypergeometricDistributionImpl;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.distribution.PascalDistribution;
import org.apache.commons.math.distribution.PascalDistributionImpl;
import org.apache.commons.math.distribution.PoissonDistribution;
import org.apache.commons.math.distribution.PoissonDistributionImpl;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.distribution.WeibullDistribution;
import org.apache.commons.math.distribution.WeibullDistributionImpl;
import org.apache.commons.math.distribution.ZipfDistribution;
import org.apache.commons.math.distribution.ZipfDistributionImpl;

/**
 * 
 * @author Michael Borcherds
 * @version 20090730
 */

public abstract class AlgoDistribution extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected NumberValue a,b,c,d; //input
	protected GeoBoolean isCumulative; //input
	protected GeoNumeric num; //output	
	private TDistribution t = null;
	private ChiSquaredDistribution chisquared = null;
	private FDistribution f = null;
	private GammaDistribution gamma = null;
	private CauchyDistribution cauchy = null;
	private ExponentialDistribution exponential = null;
	private HypergeometricDistribution hypergeometric = null;
	private PascalDistribution pascal = null;
	private BinomialDistribution binomial = null;
	private WeibullDistribution weibull = null;
	private ZipfDistribution zipf = null;
	private NormalDistribution normal = null;
	private PoissonDistribution poisson = null;

	public AlgoDistribution(Construction cons, String label, NumberValue a, NumberValue b, NumberValue c, NumberValue d) {
		super(cons);
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();
		num.setLabel(label);
	}

	public AlgoDistribution(Construction cons, String label, NumberValue a, NumberValue b, NumberValue c, 
			GeoBoolean isCumulative) {
		this(cons, a, b, c, isCumulative);
		num.setLabel(label);
	}
	
	public AlgoDistribution(Construction cons, NumberValue a, NumberValue b, NumberValue c, 
			GeoBoolean isCumulative) {
		super(cons);
		this.a = a;
		this.b = b;
		this.c = c;
		this.isCumulative = isCumulative;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();		
	}
	
	
	public AlgoDistribution(Construction cons, String label, NumberValue a, NumberValue b, NumberValue c, NumberValue d,
			GeoBoolean isCumulative) {
		this(cons, a, b, c, d, isCumulative);
		num.setLabel(label);
	}
	
	public AlgoDistribution(Construction cons, NumberValue a, NumberValue b, NumberValue c, NumberValue d,
			GeoBoolean isCumulative) {
		super(cons);
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.isCumulative = isCumulative;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}
	
	
	public abstract String getClassName();

	protected void setInputOutput(){

		// build array list of possible arguments
		ArrayList<GeoElement> inputList = new ArrayList<GeoElement>();
		inputList.add(a.toGeoElement());
		inputList.add(b.toGeoElement());
		if(c!=null)
		inputList.add(c.toGeoElement());
		if(d!=null)
			inputList.add(d.toGeoElement());		
		if (isCumulative != null) 
			inputList.add(isCumulative.toGeoElement());

		// convert to array	
		input = new GeoElement[inputList.size()];
		inputList.toArray(input);

		output = new GeoElement[1];
		output[0] = num;
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return num;
	}

	abstract protected void compute();     



	TDistribution getTDistribution(double param) {
		if (t == null) 
			t = new TDistributionImpl(param);
		else t.setDegreesOfFreedom(param);
		return t;
	}

	FDistribution getFDistribution(double param, double param2) {
		if (f == null) 
			f = new FDistributionImpl(param, param2);
		else {
				f.setNumeratorDegreesOfFreedom(param);
				f.setDenominatorDegreesOfFreedom(param2);
		}
		return f;
	}

	GammaDistribution getGammaDistribution(double param, double param2) {
		if (gamma == null) 
			gamma = new GammaDistributionImpl(param, param2);
		else {
				gamma.setAlpha(param);
				gamma.setBeta(param2);
		}
		return gamma;
	}

	CauchyDistribution getCauchyDistribution(double param, double param2) {
		if (cauchy == null) 
			cauchy = new CauchyDistributionImpl(param, param2);
		else {
				cauchy.setMedian(param);
				cauchy.setScale(param2);
		}
		return cauchy;
	}

	ChiSquaredDistribution getChiSquaredDistribution(double param) {
		if (chisquared == null) 
			chisquared = new ChiSquaredDistributionImpl(param);
		else {
				chisquared.setDegreesOfFreedom(param);
		}
		return chisquared;
	}

	ExponentialDistribution getExponentialDistribution(double param) {
		if (exponential == null) 
			exponential = new ExponentialDistributionImpl(1.0 / param);
		else {
				exponential.setMean(1.0 / param);
		}
		return exponential;
	}

	
	HypergeometricDistribution getHypergeometricDistribution(int param, int param2, int param3) {
		if (hypergeometric == null) 
			hypergeometric = new HypergeometricDistributionImpl(param, param2, param3);
		else {
				hypergeometric.setPopulationSize(param);
				hypergeometric.setNumberOfSuccesses(param2);
				hypergeometric.setSampleSize(param3);
		}
		return hypergeometric;
	}

	PascalDistribution getPascalDistribution(int param, double param2) {
		if (pascal == null) 
			pascal = new PascalDistributionImpl(param, param2);
		else {
				pascal.setNumberOfSuccesses(param);
				pascal.setProbabilityOfSuccess(param2);
		}
		return pascal;
	}
	
	PoissonDistribution getPoissonDistribution(double param) {
		if (poisson == null) 
			poisson = new PoissonDistributionImpl(param);
		else {
			poisson.setMean(param);
		}
		return poisson;
	}
	
	
	BinomialDistribution getBinomialDistribution(int param, double param2) {
		if (binomial == null) 
			binomial = new BinomialDistributionImpl(param, param2);
		else {
			binomial.setNumberOfTrials(param);
			binomial.setProbabilityOfSuccess(param2);
		}
		return binomial;
	}
	

	WeibullDistribution getWeibullDistribution(double param, double param2) {
		if (weibull == null) 
			weibull = new WeibullDistributionImpl(param, param2);
		else {
				weibull.setShape(param);
				weibull.setScale(param2);
		}
		return weibull;
	}

	NormalDistribution getNormalDistribution(double param, double param2) {
		if (normal == null) 
			normal = new NormalDistributionImpl(param, param2);
		else {
			normal.setMean(param);
			normal.setStandardDeviation(param2);
		}
		return normal;
	}

	ZipfDistribution getZipfDistribution(int param, double param2) {
		if (zipf == null) 
			zipf = new ZipfDistributionImpl(param, param2);
		else {
			zipf.setNumberOfElements(param);
			zipf.setExponent(param2);
		}
		return zipf;
	}
	
	

}


