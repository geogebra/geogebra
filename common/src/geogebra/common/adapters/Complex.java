package geogebra.common.adapters;

public interface Complex {
	public double getReal();
	public double getImaginary();
	public Complex divide(Complex rhs);
	public Complex multiply(Complex rhs);
	public Complex pow(Complex rhs);
	public Complex sqrt();
	public Complex conjugate();
	public Complex exp();
	public Complex log();
	public double abs();
}
