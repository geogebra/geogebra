package geogebra.common.adapters;

public abstract class Complex {
	public static Complex prototype = null;

	public abstract double getReal();
	public abstract double getImaginary();
	public abstract Complex divide(Complex rhs);
	public abstract Complex multiply(Complex rhs);
	public abstract Complex pow(Complex rhs);
	public abstract Complex sqrt();
	public abstract Complex conjugate();
	public abstract Complex exp();
	public abstract Complex log();
	public abstract double abs();
	public abstract Complex construct(double r, double i);
}
