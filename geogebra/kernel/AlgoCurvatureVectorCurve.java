package geogebra.kernel;

import geogebra.kernel.cas.AlgoDerivative;


/**
 * @author Victor Franco Espino
 * @version 11-02-2007
 * 
 * Calculate Curvature Vector for curve: c(t) = ((a'(t)b''(t)-a''(t)b'(t))/T^4) *
 * (-b'(t),a'(t)) T = sqrt(a'(t)^2+b'(t)^2)
 */

public class AlgoCurvatureVectorCurve extends AlgoElement {

	private static final long serialVersionUID = 1L;

	private GeoPoint A; // input
	private GeoCurveCartesian f, f1, f2; // f = f(x), f1 is f'(x), f2 is f''(x)										
	private GeoVector v; // output

	private double f1eval[] = new double[2];
	private double f2eval[] = new double[2];
	
    AlgoDerivative algoCAS, algoCAS2;

	AlgoCurvatureVectorCurve(Construction cons, String label, GeoPoint A,
			GeoCurveCartesian f) {
		this(cons, A, f);

		if (label != null) {
			v.setLabel(label);
		} else {
			// if we don't have a label we could try c
			v.setLabel("c");
		}
	}

	AlgoCurvatureVectorCurve(Construction cons, GeoPoint A, GeoCurveCartesian f) {
		super(cons);
		this.A = A;
		this.f = f;

		// create new vector
		v = new GeoVector(cons);
		try {
			v.setStartPoint(A);
		} catch (CircularDefinitionException e) {
		}

		// First derivative of curve f
		algoCAS = new AlgoDerivative(cons, f);
		cons.removeFromConstructionList(algoCAS);
		this.f1 = (GeoCurveCartesian) algoCAS.getResult();

		// Second derivative of curve f
		algoCAS2 = new AlgoDerivative(cons, f1);
		cons.removeFromConstructionList(algoCAS2);
		this.f2 = (GeoCurveCartesian) algoCAS2.getResult();
		
		setInputOutput();
		compute();
	}

	public String getClassName() {
		return "AlgoCurvatureVectorCurve";
	}

	// for AlgoElement
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A;
		input[1] = f;

		output = new GeoElement[1];
		output[0] = v;
		setDependencies(); // done by AlgoElement
	}

	// Return the resultant vector
	GeoVector getVector() {
		return v;
	}

	protected final void compute() {
		try {			
			double t, t4, x, y, evals, tvalue;

			tvalue = f.getClosestParameter(A, f.getMinParameter());
			f1.evaluateCurve(tvalue, f1eval);
			f2.evaluateCurve(tvalue, f2eval);
			t = Math.sqrt(f1eval[0] * f1eval[0] + f1eval[1] * f1eval[1]);
			t4 = t * t * t * t;
			evals = f1eval[0] * f2eval[1] - f2eval[0] * f1eval[1];

			x = A.inhomX + ((evals / t4) * (-f1eval[1]));
			y = A.inhomY + ((evals / t4) * f1eval[0]);
			
			v.x = x - A.inhomX;
			v.y = y - A.inhomY;
			v.z = 0.0;
		} 
		catch (Exception e) {
			// in case something went wrong, e.g. derivatives not defined
			v.setUndefined();
		}
	}
	
	public void remove() {  
    	super.remove();  
   		A.removeAlgorithm(algoCAS);
   		f.removeAlgorithm(algoCAS);
   		A.removeAlgorithm(algoCAS2);
   		f.removeAlgorithm(algoCAS2);
    }

}