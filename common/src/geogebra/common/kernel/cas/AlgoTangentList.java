package geogebra.common.kernel.cas;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.GetCommand;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * @author Giuliano Bellucci 15/05/2013
 *
 * For tangents of splines 
 */
public class AlgoTangentList extends AlgoElement {

	private GeoPoint P;
	private GeoList list;
	private int size;
	private GeoLine tangent=null;
	private AlgoTangentCurve algot;

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param P point on function
	 * @param list - list of GeoCurvecartesian functions
	 */
	public AlgoTangentList(Construction cons, String label, GeoPoint P,
			GeoList list) {
		super(cons);
		this.P=P;
		this.list = list;
		size = list.size();
		double min=Double.MAX_VALUE;
		GeoCurveCartesian minF=null;
		for (int i = 0; i < size; i++) {
			GeoCurveCartesian f = (GeoCurveCartesian) list.get(i);		
			if (f.distance(P)< min) {
				min=f.distance(P);
				minF=f;
			}
		}
		algot=new AlgoTangentCurve(cons,label,P,minF);
		tangent=algot.getTangent();
		setInputOutput();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = P;
		input[1] = list;
		setOutputLength(1);		
		setOutput(0, tangent);
		setDependencies();
	}
	
	/**
	 * @return tangent
	 */
	public GeoLine getTangent() {
		return tangent;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TANGENTS;
	}

	@Override
	public void compute() {
		double min=Double.MAX_VALUE;
		GeoCurveCartesian minF=null;
		for (int i = 0; i < size; i++) {
			GeoCurveCartesian f = (GeoCurveCartesian) list.get(i);		
			if (f.distance(P)< min) {
				min=f.distance(P);
				minF=f;
			}
		}
		tangent.setParentAlgorithm(algot);
		algot.initialize(minF);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Tangent;
	}
}
