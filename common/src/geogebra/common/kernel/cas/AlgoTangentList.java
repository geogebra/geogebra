package geogebra.common.kernel.cas;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.GetCommand;
import geogebra.common.kernel.algos.TangentAlgo;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSpline;

/**
 * @author Giuliano Bellucci 15/05/2013
 * 
 *         For tangents of splines
 */
public class AlgoTangentList extends AlgoElement implements TangentAlgo {

	private GeoPoint P;
	private GeoSpline list;
	private int size;
	private GeoLine tangent = null;
	private AlgoTangentCurve algot;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param P
	 *            point on function
	 * @param list
	 *            - list of GeoCurvecartesian functions
	 */
	public AlgoTangentList(Construction cons, String label, GeoPoint P,
			GeoSpline list) {
		super(cons);
		this.P = P;
		this.list = list;
		size=list.size();
		GeoCurveCartesian minF = list.curveOfDistance(P);
		algot = new AlgoTangentCurve(cons, label, P, minF);
		cons.removeFromConstructionList(algot);
		tangent = algot.getTangent();
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
		if (size != 0 && P.isFinite()) {
			algot.initialize(list.curveOfDistance(P));
			algot.compute();
		}
	}

	

	@Override
	public GetCommand getClassName() {
		return Commands.Tangent;
	}

	public GeoPoint getTangentPoint(GeoElement geo, GeoLine line) {
		if (geo == list && line == tangent) {
			return P;
		}
		return null;
	}
}
