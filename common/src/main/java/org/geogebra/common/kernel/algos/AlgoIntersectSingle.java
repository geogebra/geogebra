package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.RestrictionAlgoForLocusEquation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 * Single intersection point
 */
public class AlgoIntersectSingle extends AlgoIntersect implements
		RestrictionAlgoForLocusEquation, SymbolicParametersBotanaAlgo {

	// input
	private AlgoIntersect algo;
	private GeoNumberValue index; // index of point in algo, can be input
									// directly or be
	// calculated from refPoint
	private GeoPoint refPoint; // reference point in algo to calculate index;
								// can be null or undefined

	// output
	private GeoPoint point;

	private GeoPoint[] parentOutput;
	private int idx;

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

	/**
	 * Creates algo for single intersection close to given point
	 * 
	 * @param label
	 *            label for output
	 * @param algo
	 *            intersection algo with multiple outputs
	 * @param refPoint
	 *            point close to desired intersection
	 */
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

	/**
	 * Creates algo for single intersection with given index
	 * 
	 * @param label
	 *            label for output
	 * @param algo
	 *            intersection algo with multiple outputs
	 * @param index
	 *            index, starting with 1
	 */
	public AlgoIntersectSingle(String label, AlgoIntersect algo,
			GeoNumberValue index) {
		super(algo.cons);
		this.algo = algo;
		algo.addUser(); // this algorithm is a user of algo

		// check index
		this.index = index;

		refPoint = null;

		point = new GeoPoint(algo.cons);

		setInputOutput();
		initForNearToRelationship();
		compute();
		// setIncidence();
		point.setLabel(label);
		addIncidence();
	}

	/**
	 * Creates algo for single intersection with given index
	 * 
	 * @param label
	 *            label for output
	 * @param algo
	 *            intersection algo with multiple outputs
	 * @param index
	 *            index, starting with 0
	 */
	public AlgoIntersectSingle(String label, AlgoIntersect algo, int index) {
		this(label, algo, new GeoNumeric(algo.getConstruction(), index + 1));
	}

	private void addIncidence() {
		// point's incidence with parent algo's two intersectable objects
		if (algo instanceof AlgoIntersectConics) {
			point.addIncidence(((AlgoIntersectConics) algo).getA(), false);
			point.addIncidence(((AlgoIntersectConics) algo).getB(), false);

			// these two lines are already done in point.addIncidence()
			// ((GeoConic)
			// ((AlgoIntersectConics)algo).getA()).addPointOnConic(point);
			// ((GeoConic)
			// ((AlgoIntersectConics)algo).getB()).addPointOnConic(point);

		} else if (algo instanceof AlgoIntersectLineConic) {
			point.addIncidence(((AlgoIntersectLineConic) algo).getLine(), false);
			point.addIncidence(((AlgoIntersectLineConic) algo).getConic(),
					false);

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
	public Commands getClassName() {
		return Commands.Intersect;
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
			input[2] = index.toGeoElement();
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
	 * 
	 * @return inner algo.
	 */
	public AlgoIntersect getAlgo() {
		return this.algo;
	}

	/**
	 * @return resulting point
	 */
	public GeoPoint getPoint() {
		return point;
	}

	@Override
	public GeoPoint[] getIntersectionPoints() {
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
		algo.setIntersectionPoint(idx, point);
		algo.compute();
	}

	@Override
	public void compute() {
		if (index != null)
			idx = Math.max(0, (int) index.getDouble() - 1);
		parentOutput = algo.getIntersectionPoints();

		if (kernel.getLoadingMode() && point.hasUpdatePrevilege) { // for
			// backward
			// compatability
			algo.setIntersectionPoint(idx, point);
			point.hasUpdatePrevilege = false;
		}

		// update index if reference point has been defined
		if (refPoint != null)
			if (refPoint.isDefined())
				idx = algo.getClosestPointIndex(refPoint);

		if (input[0].isDefined() && input[1].isDefined()
				&& idx < parentOutput.length) {
			// get coordinates from helper algorithm
			point.setCoords(parentOutput[idx]);

			if (point.getIncidenceList() != null) {
				for (int i = 0; i < parentOutput.length; ++i) {
					if (!parentOutput[idx].contains(parentOutput[i]))
						point.getIncidenceList().remove(parentOutput[i]);
				}
			}
			point.addIncidence(parentOutput[idx], false);
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
			return getLoc().getPlain("IntersectionPointOfAB",
					input[0].getLabel(tpl), input[1].getLabel(tpl));
		}
		return getLoc().getPlain("IntersectionPointOfABNearC",
				input[0].getLabel(tpl), input[1].getLabel(tpl),
				input[2].getLabel(tpl));
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnIntersectSingle(geo, this, scope);
	}

	public Variable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (algo != null) {
			if (algo instanceof AlgoIntersectLineConic) {
				botanaPolynomials = ((SymbolicParametersBotanaAlgo) algo)
						.getBotanaPolynomials(geo);
				if (botanaVars == null) {
					botanaVars = ((SymbolicParametersBotanaAlgo) algo)
							.getBotanaVars(geo);
				}
			}
			if (algo instanceof AlgoIntersectConics) {
				botanaPolynomials = ((SymbolicParametersBotanaAlgo) algo)
						.getBotanaPolynomials(geo);
				if (botanaVars == null) {
					botanaVars = ((SymbolicParametersBotanaAlgo) algo)
							.getBotanaVars(geo);
				}
			}
			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}
}
