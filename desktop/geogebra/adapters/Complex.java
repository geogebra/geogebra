package geogebra.adapters;

public class Complex implements geogebra.common.adapters.Complex {

	private org.apache.commons.math.complex.Complex comp;

	public Complex(double r, double i) {
		comp = new org.apache.commons.math.complex.Complex(r, i);
	}

	public Complex(org.apache.commons.math.complex.Complex c) {
		comp = c;
	}

	org.apache.commons.math.complex.Complex getImpl() {
		return comp;
	}

	public double getReal() {
		return comp.getReal();
	}

	public double getImaginary() {
		return comp.getImaginary();
	}

	public geogebra.common.adapters.Complex divide(geogebra.common.adapters.Complex rhs) {
		return new Complex(comp.divide(((Complex)rhs).getImpl()));
	}

	public geogebra.common.adapters.Complex multiply(geogebra.common.adapters.Complex rhs) {
		return new Complex(comp.multiply(((Complex)rhs).getImpl()));
	}

	public geogebra.common.adapters.Complex pow(geogebra.common.adapters.Complex rhs) {
		return new Complex(comp.pow(((Complex)rhs).getImpl()));
	}

	public geogebra.common.adapters.Complex sqrt() {
		return new Complex(comp.sqrt());
	}

	public geogebra.common.adapters.Complex conjugate() {
		return new Complex(comp.conjugate());
	}

	public geogebra.common.adapters.Complex exp() {
		return new Complex(comp.exp());
	}

	public geogebra.common.adapters.Complex log() {
		return new Complex(comp.log());
	}

	public double abs() {
		return comp.abs();
	}
}
