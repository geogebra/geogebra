package org.geogebra.common.util;

/**
 * A class that encapsulates a value and its error. Primarily for use with
 * ScientificFormat
 * 
 * @see ScientificFormatAdapter
 * 
 * @author Tony Johnson
 * @author Mark Donszelmann
 * @version $Id: DoubleWithError.java,v 1.4 2008-09-01 14:39:45 murkle Exp $
 */
public class DoubleWithError {
	public DoubleWithError(double value, double error) {
		this.value = value;
		this.error = error;
		this.asymmetricError = false;
	}

	public DoubleWithError(double value, double plusError, double minError) {
		this.value = value;
		this.error = plusError;
		this.minError = minError;
		this.asymmetricError = true;
	}

	public void setError(double error) {
		this.error = error;
		this.asymmetricError = false;
	}

	public void setError(double plusError, double minError) {
		this.error = plusError;
		this.minError = minError;
		this.asymmetricError = true;
	}

	public double getError() {
		// FIXME: what do we return here if this has an asymmetric error
		return error;
	}

	public double getPlusError() {
		return error;
	}

	public double getMinError() {
		return (asymmetricError) ? minError : error;
	}

	public boolean hasAsymmetricError() {
		return asymmetricError;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		if (asymmetricError) {
			return String.valueOf(value) + plus + error + minus + minError;
		}
		return String.valueOf(value) + plusorminus + error;
	}

	// Not private because used by scientific format
	final static public char plusorminus = '\u00b1';
	final static public char plus = '+';
	final static public char minus = '-';
	private double value;

	private double error;
	private boolean asymmetricError;
	private double minError;
}
