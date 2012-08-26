package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.RestrictionAlgoForLocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.elements.EquationIntersectSingleRestriction;

import java.util.ArrayList;

/**
 * Single intersection point
 */
public class AlgoIntersectSingle extends AlgoIntersect implements RestrictionAlgoForLocusEquation {

	// input
	private AlgoIntersect algo;
	private int index; // index of point in algo, can be input directly or be
						// calculated from refPoint
	private GeoPoint refPoint; // reference point in algo to calculate index;
								// can be null or undefined

	// output
	private GeoPoint point;

	private GeoPoint[] parentOutput;

	// intersection point is the (a) nearest to refPoint
	public AlgoIntersectSingle(String label, AlgoIntersect algo,
			GeoPoint refPoint) {
		super(algo.cons);
		this.algo = algo;
		algo.addUser(); // this algorithm is a user of algo
		this.refPoint = refPoint;

		point = new GeoPoint(algo.cons);

		setInputOutput();
		initForNearToRelationship();
		compute();
		

		point.setLabel(label);
		addIncidence();
	}

	// intersection point is index-th intersection point of algo
	public AlgoIntersectSingle(String label, AlgoIntersect algo, int index) {
		super(algo.cons);
		this.algo = algo;
		algo.addUser(); // this algorithm is a user of algo

		// check index
		if (index < 0)
			index = 0;
		else
			this.index = index;

		refPoint = null;

		point = new GeoPoint(algo.cons);

		setInputOutput();
		initForNearToRelationship();
		compute();
		//setIncidence();
		point.setLabel(label);
		addIncidence();
	}

	private void addIncidence() {
		// point's incidence with parent algo's two intersectable objects
		if (algo instanceof AlgoIntersectConics) {
			point.addIncidence(((AlgoIntersectConics) algo).getA());
			point.addIncidence(((AlgoIntersectConics) algo).getB());

			// these two lines are already done in point.addIncidence()
			// ((GeoConic)
			// ((AlgoIntersectConics)algo).getA()).addPointOnConic(point);
			// ((GeoConic)
			// ((AlgoIntersectConics)algo).getB()).addPointOnConic(point);

		} else if (algo instanceof AlgoIntersectLineConic) {
			point.addIncidence(((AlgoIntersectLineConic) algo).getLine());
			point.addIncidence(((AlgoIntersectLineConic) algo).getConic());

			// this is already done in point.addIncidence()
			// ((AlgoIntersectLineConic)algo).getConic().addPointOnConic(point);
		}

		// points's incidence with one of the intersection points --
		// this is done in compute(), because it depends on the index, which can
		// be changed dynamically
	}

	@Override
	protected boolean showUndefinedPointsInAlgebraView() {
		return true;
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntersectSingle;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {

		if (refPoint == null) {
			input = new GeoElement[3];
			input[0] = algo.input[0];
			input[1] = algo.input[1];
			// dummy value to store the index of the intersection point
			// index + 1 is used here to let numbering start at 1
			input[2] = new GeoNumeric(cons, index + 1);
		} else {
			input = new GeoElement[3];
			input[0] = algo.input[0];
			input[1] = algo.input[1];
			input[2] = refPoint;
		}

		super.setOutputLength(1);
		super.setOutput(0, point);

		setDependencies(); // done by AlgoElement
	}
	
	/**
	 * Added for LocusEqu
	 * @return inner algo.
	 */
	public AlgoIntersect getAlgo() {
		return this.algo;
	}

	public GeoPoint getPoint() {
		return point;
	}

	@Override
	protected GeoPoint[] getIntersectionPoints() {
		return (GeoPoint[]) super.getOutput();
	}

	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {
		return null;
	}

	@Override
	public boolean isNearToAlgorithm() {
		return true;
	}

	@Override
	public final void initForNearToRelationship() {
		parentOutput = algo.getIntersectionPoints();

		// tell parent algorithm about the loaded position;
		// this is needed for initing the intersection algo with
		// the intersection point stored in XML files
		algo.initForNearToRelationship();
		algo.setIntersectionPoint(index, point);
		algo.compute();
	}

	@Override
	public void compute() {

		parentOutput = algo.getIntersectionPoints();

		if (point != null) {
			if (kernel.getLoadingMode() && point.hasUpdatePrevilege) { // for
																		// backward
																		// compatability
				algo.setIntersectionPoint(index, point);
				point.hasUpdatePrevilege = false;
			} 
		}

		// update index if reference point has been defined
		if (refPoint != null)
			if (refPoint.isDefined())
				index = algo.getClosestPointIndex(refPoint);

		if (input[0].isDefined() && input[1].isDefined()
				&& index < parentOutput.length) {
			// get coordinates from helper algorithm
			point.setCoords(parentOutput[index]);

			if (point.getIncidenceList() != null) {
				for (int i = 0; i < parentOutput.length; ++i) {
					if (!parentOutput[index].contains(parentOutput[i]))
						point.getIncidenceList().remove(parentOutput[i]);
				}
			}
			point.addIncidence(parentOutput[index]);
		} else {
			point.setUndefined();
			ArrayList<GeoElement> al = point.getIncidenceList();
			if (al != null)
				for (int i = 0; i < parentOutput.length; ++i) {
					al.remove(parentOutput[i]);
				}
		}
	}

	@Override
	public void remove() {
		super.remove();
		algo.removeUser(); // this algorithm was a user of algo
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		if (refPoint == null) {
			return app.getPlain("IntersectionPointOfAB", input[0].getLabel(tpl),
					input[1].getLabel(tpl));
		}
		return app.getPlain("IntersectionPointOfABNearC", input[0].getLabel(tpl),
				input[1].getLabel(tpl), input[2].getLabel(tpl));
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

}
