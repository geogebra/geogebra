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

package org.geogebra.common.kernel.statistics;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PascalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;

/**
 * 
 * @author Michael Borcherds
 * @version 20090730
 */

public abstract class AlgoDistribution extends AlgoElement {
	/** input: dist. parameter */
	protected GeoNumberValue a;
	/** input: dist. parameter or variable value */
	protected GeoNumberValue b;
	/** input: dist. parameter or variable value */
	protected GeoNumberValue c;
	/** input: variable value */
	protected GeoNumberValue d;
	/** input: flag for cumulative function */
	protected GeoBoolean isCumulative;
	/** output number */
	protected GeoNumeric num;
	private RealDistribution realDistribution = null;

	// discrete distributions
	private HypergeometricDistribution hypergeometric = null;
	private PascalDistribution pascal = null;
	private BinomialDistribution binomial = null;
	private ZipfDistribution zipf = null;
	private PoissonDistribution poisson = null;
	protected GeoList list;
	private double lastParam;
	private double lastParam2;

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            first input element
	 * @param b
	 *            second input element
	 * @param c
	 *            third input element
	 * @param d
	 *            fourth input element
	 */
	public AlgoDistribution(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoNumberValue d) {
		super(cons);
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            first input element
	 * @param b
	 *            second input element
	 * @param c
	 *            third input element
	 * @param isCumulative
	 *            flag for cumulative
	 */
	public AlgoDistribution(Construction cons, GeoBoolean isCumulative, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c) {
		super(cons);
		this.a = a;
		this.b = b;
		this.c = c;
		this.isCumulative = isCumulative;

		num = new GeoNumeric(cons);

		setInputOutput();
	}

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            first input element
	 * @param b
	 *            second input element
	 * @param c
	 *            third input element
	 * @param isCumulative
	 *            flag for cumulative
	 */
	public AlgoDistribution(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoBoolean isCumulative) {
		this(cons, isCumulative, a, b, c);
		compute();
	}

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            first input element
	 * @param b
	 *            second input element
	 * @param c
	 *            third input element
	 * @param d
	 *            fourth input element
	 * @param isCumulative
	 *            flag for cumulative
	 */
	public AlgoDistribution(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoNumberValue d,
			GeoBoolean isCumulative) {
		super(cons);
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.isCumulative = isCumulative;

		num = new GeoNumeric(cons);

		setInputOutput();
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param a
	 *            parameter
	 * @param b
	 *            parameter
	 * @param list
	 *            list of values (for binomial)
	 */
	public AlgoDistribution(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoList list) {
		super(cons);
		this.a = a;
		this.b = b;
		this.list = list;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param a
	 *            parameter
	 * @param list
	 *            list of values (for poisson)
	 */
	public AlgoDistribution(Construction cons, GeoNumberValue a,
			GeoList list) {
		super(cons);
		this.a = a;
		this.list = list;

		num = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {

		// build array list of possible arguments
		ArrayList<GeoElement> inputList = new ArrayList<>();
		inputList.add(a.toGeoElement());
		inputList.add(b.toGeoElement());
		if (c != null) {
			inputList.add(c.toGeoElement());
		}
		if (d != null) {
			inputList.add(d.toGeoElement());
		}
		if (isCumulative != null) {
			inputList.add(isCumulative.toGeoElement());
		}

		// convert to array
		input = new GeoElement[inputList.size()];
		inputList.toArray(input);

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting number
	 */
	public GeoNumeric getResult() {
		return num;
	}

	@Override
	public abstract void compute();

	/**
	 * @param param
	 *            population size
	 * @param param2
	 *            number of successes
	 * @param param3
	 *            sample size
	 * @return hypergeometric distribution
	 */
	protected HypergeometricDistribution getHypergeometricDistribution(
			int param, int param2, int param3) {
		if (hypergeometric == null
				|| hypergeometric.getNumberOfSuccesses() != param2
				|| hypergeometric.getPopulationSize() != param
				|| hypergeometric.getSampleSize() != param3) {
			hypergeometric = new HypergeometricDistribution(param, param2,
					param3);
		}

		return hypergeometric;
	}

	/**
	 * @param param
	 *            number of successes
	 * @param param2
	 *            prob. of success
	 * @return Pascal distribution
	 */
	protected PascalDistribution getPascalDistribution(int param,
			double param2) {
		if (pascal == null || pascal.getNumberOfSuccesses() != param
				|| pascal.getProbabilityOfSuccess() != param2) {
			pascal = new PascalDistribution(param, param2);
		}

		return pascal;
	}

	/**
	 * @param param
	 *            mean
	 * @return Poisson distribution
	 */
	protected PoissonDistribution getPoissonDistribution(double param) {
		if (poisson == null || poisson.getMean() != param) {
			poisson = new PoissonDistribution(param);
		}
		return poisson;
	}

	/**
	 * @param param
	 *            number of trials
	 * @param param2
	 *            prob. of success
	 * @return binomial distribution
	 */
	protected BinomialDistribution getBinomialDistribution(int param,
			double param2) {
		if (binomial == null || binomial.getNumberOfTrials() != param
				|| binomial.getProbabilityOfSuccess() != param2) {
			binomial = new BinomialDistribution(param, param2);
		}

		return binomial;
	}

	/**
	 * @param param
	 *            number of elements
	 * @param param2
	 *            exponent
	 * @return Zipf distribution
	 */
	ZipfDistribution getZipfDistribution(int param, double param2) {
		if (zipf == null || zipf.getNumberOfElements() != param
				|| zipf.getExponent() != param2) {
			zipf = new ZipfDistribution(param, param2);
		}
		return zipf;
	}

	/**
	 * @param dist
	 *            real distribution
	 * @param c
	 *            variable value
	 */
	protected void setFromRealDist(RealDistribution dist, GeoNumberValue c) {
		if (this.isCumulative == null || this.isCumulative.getBoolean()) {
			num.setValue(dist.cumulativeProbability(c.getDouble()));
		} else {
			num.setValue(dist.density(c.getDouble()));
		}
	}

	protected RealDistribution getDist(Dist command, double param, double param2) {
		if (realDistribution != null && param == lastParam && param2 == lastParam2) {
			return realDistribution;
		}
		lastParam = param;
		lastParam2 = param2;
		realDistribution = newDistribution(command, param, param2);
		return realDistribution;
	}

	private RealDistribution newDistribution(Dist dist, double param, double param2) {
		switch (dist) {
		case GAMMA:
			return new GammaDistribution(param, param2);
		case BETA:
			return new BetaDistribution(param, param2);
		case CAUCHY:
			return new CauchyDistribution(param, param2);
		case F:
			return new FDistribution(param, param2) {

				@Override
				public double density(double d) {
					return d < 0 ? 0 : super.density(d);
				}
			};
		case WEIBULL:
			return new WeibullDistribution(param, param2);
		case NORMAL:
			return new NormalDistribution(param, param2);
		case CHISQUARE:
			return new ChiSquaredDistribution(param);
		case STUDENT:
			return new TDistribution(param);
		case EXPONENTIAL:
			return new ExponentialDistribution(1.0 / param);
		default:
			throw new IllegalStateException("Invalid distribution");
		}
	}

}
