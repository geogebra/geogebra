package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;


/**
 * @author  Victor Franco Espino
 * @version 11-02-2007
 * 
 * Osculating Circle of a curve f in point A: center = A + (radius)^2 * v
 * 											  radius = 1/abs(k(x)), k(x)=curvature of f
 *                                            v = curvature vector of f in point A
 */

public class AlgoOsculatingCircleCurve extends AlgoElement {

	private GeoPoint A, R;// input A
    private GeoCurveCartesian f;// input
    private GeoVector v;//curvature vector of f in point A 
    private GeoNumeric curv;//curvature of f in point A
    private GeoConic circle; // output
    
    AlgoCurvatureCurve algo;
    AlgoCurvatureVectorCurve cv;
    
    public AlgoOsculatingCircleCurve(Construction cons, String label, GeoPoint A, GeoCurveCartesian f) {
        super(cons);
        this.A = A;
        this.f = f;
        
        R = new GeoPoint(cons);//R is the center of the circle
        circle = new GeoConic(cons);

        //Catch curvature and curvature vector
        algo = new AlgoCurvatureCurve(cons,A,f);
        cv = new AlgoCurvatureVectorCurve(cons,A,f);
        curv = algo.getResult();
        v = cv.getVector();
 
    	cons.removeFromConstructionList(algo);
		cons.removeFromConstructionList(cv);
		setInputOutput();
        compute();
        circle.setLabel(label);
    }
 
    @Override
	public Commands getClassName() {
        return Commands.OsculatingCircle;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = A;
        input[1] = f;
        
        super.setOutputLength(1);
        super.setOutput(0, circle);
        setDependencies(); // done by AlgoElement
    }
    
    //Return the resultant circle
    public GeoConic getCircle() {
    	return circle;
    }

    @Override
	public final void compute() {
    	
    	// bugfix Michael Borcherds
    	// undefined unless A is a point on f
        if (!f.isOnPath(A, Kernel.MIN_PRECISION)) {
        	circle.setUndefined();
        	return;
        }
        
    	double radius = 1/Math.abs(curv.getValue());
    	double r2 = radius*radius;
    	double x = r2 * v.x;
    	double y = r2 * v.y;
    	
    	R.setCoords(A.inhomX + x, A.inhomY + y, 1.0);
    	circle.setCircle(R, A);	
    }
    
    @Override
	public void remove() {
    	if(removed)
			return;
        super.remove();
        f.removeAlgorithm(algo);
        f.removeAlgorithm(cv);
        A.removeAlgorithm(algo);
        A.removeAlgorithm(cv);
        
        // make sure all AlgoCASDerivatives get removed
        cv.remove();
    }

	// TODO Consider locusequability

}