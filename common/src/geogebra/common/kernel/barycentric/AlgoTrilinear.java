package geogebra.common.kernel.barycentric;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * @author Darko Drakulic
 * @version 17-10-2011
 * 
 *  This class make point with given trilinear coordinates. 
 *          
 */

public class AlgoTrilinear extends AlgoElement {

	private GeoPoint P1, P2, P3; // input
	private NumberValue v1, v2, v3; // input
	private GeoPoint point; // output
	
	/**
	 * Creates new trilinear algo
	 * @param cons construction
	 * @param label label
	 * @param A first point
	 * @param B second point
	 * @param C third point
	 * @param a first trilinear coord
	 * @param b second trilinear coord
	 * @param c third trilinear coord
	 */
	public AlgoTrilinear(Construction cons, String label, GeoPoint A, GeoPoint B, GeoPoint C,
			NumberValue a, NumberValue b, NumberValue c) {
		super(cons);
		this.P1 = A;
		this.P2 = B;
		this.P3 = C;
		this.v1 = a;
		this.v2 = b;
		this.v3 = c;
		
		point = new GeoPoint(cons);
		setInputOutput();
		compute();		
		point.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoTrilinear;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[6];
		input[0] = P1;
		input[1] = P2;
		input[2] = P3;
		input[3] = v1.toGeoElement();
		input[4] = v2.toGeoElement();
		input[5] = v3.toGeoElement();
		
		setOutputLength(1);
		setOutput(0, point);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting point
	 */
	public GeoPoint getResult() {
		return point;
	}

	@Override
	public final void compute() {
		
		double p1 = P2.distance(P3);
		double p2 = P1.distance(P3);
		double p3 = P1.distance(P2);
		
		double x=0, y=0, sum=0;
		x = v1.getDouble()*p1 * P1.inhomX + v2.getDouble()*p2 * P2.inhomX + v3.getDouble()*p3 * P3.inhomX;
		y = v1.getDouble()*p1 * P1.inhomY + v2.getDouble()*p2 * P2.inhomY + v3.getDouble()*p3 * P3.inhomY;
		sum = v1.getDouble()*p1 + v2.getDouble()*p2 + v3.getDouble()*p3;
		point.setCoords(x/sum, y/sum, 1);
	}

	// TODO Consider locusequability
}