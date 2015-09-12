package org.geogebra.common.kernel.prover;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAnglePoints;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 * Decides if the objects are equal. Can be embedded into the Prove command to
 * work symbolically.
 * 
 * @author Simon Weitzhofer 17 May 2012
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoAreEqual extends AlgoElement implements
		SymbolicParametersBotanaAlgoAre {

	private GeoElement inputElement1; // input
	private GeoElement inputElement2; // input

	private GeoBoolean outputBoolean; // output
	private Polynomial[][] botanaPolynomials;

	/**
	 * Tests if two objects are equal
	 * 
	 * @param cons
	 *            The construction the objects depend on
	 * @param label
	 *            the name of the resulting boolean
	 * @param inputElement1
	 *            the first object
	 * @param inputElement2
	 *            the second object
	 */
	public AlgoAreEqual(Construction cons,
			GeoElement inputElement1, GeoElement inputElement2) {
		super(cons);
		this.inputElement1 = inputElement1;
		this.inputElement2 = inputElement2;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();

	}

	public AlgoAreEqual(Construction cons, String label,
			GeoElement inputElement1, GeoElement inputElement2) {
		this(cons, inputElement1, inputElement2);
		outputBoolean.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.AreEqual;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputElement1;
		input[1] = inputElement2;

		super.setOutputLength(1);
		super.setOutput(0, outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Gets the result of the test
	 * 
	 * @return true if the objects are equal and false otherwise
	 */

	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		// Formerly we used this:
		// outputBoolean.setValue(ExpressionNodeEvaluator.evalEquals(kernel,
		// inputElement1, inputElement2).getBoolean());
		// But this way is more useful eg for segments, polygons
		// ie compares endpoints NOT just length

		// #5331
		// The formerly used computation is now implemented in AlgoAreCongruent.
		outputBoolean.setValue(inputElement1.isEqual(inputElement2));
	}


	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoPoint
				&& inputElement2 instanceof GeoPoint) {
			botanaPolynomials = new Polynomial[2][1];

			Variable[] v1 = new Variable[2];
			Variable[] v2 = new Variable[2];
			v1 = ((GeoPoint) inputElement1).getBotanaVars(inputElement1); // A=(x1,y1)
			v2 = ((GeoPoint) inputElement2).getBotanaVars(inputElement2); // B=(x2,y2)

			// We want to prove: 1) x1-x2==0, 2) y1-y2==0
			botanaPolynomials[0][0] = new Polynomial(v1[0])
					.subtract(new Polynomial(v2[0]));
			botanaPolynomials[1][0] = new Polynomial(v1[1])
					.subtract(new Polynomial(v2[1]));
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoSegment
				&& inputElement2 instanceof GeoSegment) {
			// currently unimplemented
			throw new NoSymbolicParametersException();
		}
		
		if (inputElement1 instanceof GeoLine
				&& inputElement2 instanceof GeoLine) {
			botanaPolynomials = new Polynomial[2][1];

			Variable[] v1 = new Variable[4];
			Variable[] v2 = new Variable[4];
			v1 = ((GeoLine) inputElement1).getBotanaVars(inputElement1); // AB
			v2 = ((GeoLine) inputElement2).getBotanaVars(inputElement2); // CD

			// We want to prove: 1) ABC collinear, 2) ABD collinear
			botanaPolynomials[0][0] = Polynomial.collinear(v1[0], v1[1], v1[2],
					v1[3], v2[0], v2[1]);
			botanaPolynomials[1][0] = Polynomial.collinear(v1[0], v1[1], v1[2],
					v1[3], v2[2], v2[3]);
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoConic
				&& inputElement2 instanceof GeoConic) {
			if (((GeoConic) inputElement1).isCircle()
					&& ((GeoConic) inputElement2).isCircle()) {
				botanaPolynomials = new Polynomial[2][1];

				Variable[] v1 = new Variable[4];
				Variable[] v2 = new Variable[4];
				// circle with center A and point B
				v1 = ((GeoConic) inputElement1).getBotanaVars(inputElement1);
				// circle with center C and point D
				v2 = ((GeoConic) inputElement2).getBotanaVars(inputElement2);

				// We want to prove: 1) |AC|^2 = 0, 2) |AB|^2 = |CD|^2
				botanaPolynomials[0][0] = Polynomial.sqrDistance(v1[0], v1[1],
						v2[0], v2[1]);
				botanaPolynomials[1][0] = Polynomial.sqrDistance(v1[0], v1[1],
						v1[2], v1[3]).subtract(
						Polynomial.sqrDistance(v2[0], v2[1], v2[2], v2[3]));
				return botanaPolynomials;
			}
			
			if (((GeoConic) inputElement1).isParabola()
					&& ((GeoConic) inputElement2).isParabola()) {
				botanaPolynomials = new Polynomial[4][1];

				Variable[] v1 = new Variable[10];
				Variable[] v2 = new Variable[10];

				v1 = ((GeoConic) inputElement1).getBotanaVars(inputElement1);
				v2 = ((GeoConic) inputElement2).getBotanaVars(inputElement2);

				// We want to prove: 1) A, B, A' coll. 2) A, B, B' coll. 3) F=F'
				// f1 = f'1
				botanaPolynomials[0][0] = new Polynomial(v1[8])
						.subtract(new Polynomial(v2[8]));

				// f2 = f'2
				botanaPolynomials[1][0] = new Polynomial(v1[9])
						.subtract(new Polynomial(v2[9]));

				// A, B, A'
				botanaPolynomials[2][0] = Polynomial.collinear(v1[4], v1[5],
						v1[6], v1[7], v2[4], v2[5]);

				// A, B, B'
				botanaPolynomials[3][0] = Polynomial.collinear(v1[4], v1[5],
						v1[6], v1[7], v2[6], v2[7]);

				return botanaPolynomials;

			}

		}

		if (inputElement1 instanceof GeoAngle
				&& inputElement2 instanceof GeoAngle) {
			AlgoAnglePoints algo1 = (AlgoAnglePoints) inputElement1
					.getParentAlgorithm();
			// get points of first angle
			GeoPoint A = (GeoPoint) algo1.input[0];
			GeoPoint B = (GeoPoint) algo1.input[1];
			GeoPoint C = (GeoPoint) algo1.input[2];
			Variable[] vA = A.getBotanaVars(A);
			Variable[] vB = B.getBotanaVars(B);
			Variable[] vC = C.getBotanaVars(C);

			AlgoAnglePoints algo2 = (AlgoAnglePoints) inputElement2
					.getParentAlgorithm();
			// get points of second angle
			GeoPoint D = (GeoPoint) algo2.input[0];
			GeoPoint E = (GeoPoint) algo2.input[1];
			GeoPoint F = (GeoPoint) algo2.input[2];
			Variable[] vD = D.getBotanaVars(D);
			Variable[] vE = E.getBotanaVars(E);
			Variable[] vF = F.getBotanaVars(F);

			botanaPolynomials = new Polynomial[3][1];
			// We want to prove: 1) A = D 2) B = E 3) C = F
			botanaPolynomials[0][0] = Polynomial.sqrDistance(vA[0], vA[1],
					vD[0], vD[0]);
			botanaPolynomials[1][0] = Polynomial.sqrDistance(vB[0], vB[1],
					vE[0], vE[1]);
			botanaPolynomials[2][0] = Polynomial.sqrDistance(vC[0], vC[1],
					vF[0], vF[1]);
			return botanaPolynomials;

		}

		// distance between 2 point without segment
		if (inputElement1 instanceof GeoNumeric
				&& inputElement2 instanceof GeoNumeric
				&& (inputElement1.getParentAlgorithm()).getRelatedModeID() == EuclidianConstants.MODE_DISTANCE
				&& (inputElement2.getParentAlgorithm()).getRelatedModeID() == EuclidianConstants.MODE_DISTANCE) {
			// We check whether their length are equal.
			botanaPolynomials = new Polynomial[1][1];

			Variable[] v1 = new Variable[4];
			Variable[] v2 = new Variable[4];
			// get coordinates of the start and end points
			v1 = ((SymbolicParametersBotanaAlgo) inputElement1
					.getParentAlgorithm()).getBotanaVars(inputElement1); // AB
			v2 = ((SymbolicParametersBotanaAlgo) inputElement2
					.getParentAlgorithm()).getBotanaVars(inputElement2); // CD

			// We want to prove: d(AB)=d(CD) =>
			// (a1-b1)^2+(a2-b2)^2=(c1-d1)^2+(c2-d2)^2
			// => (a1-b1)^2+(a2-b2)^2-(c1-d1)^2-(c2-d2)^2
			Polynomial a1 = new Polynomial(v1[0]);
			Polynomial a2 = new Polynomial(v1[1]);
			Polynomial b1 = new Polynomial(v1[2]);
			Polynomial b2 = new Polynomial(v1[3]);
			Polynomial c1 = new Polynomial(v2[0]);
			Polynomial c2 = new Polynomial(v2[1]);
			Polynomial d1 = new Polynomial(v2[2]);
			Polynomial d2 = new Polynomial(v2[3]);
			botanaPolynomials[0][0] = ((Polynomial.sqr(a1.subtract(b1))
					.add(Polynomial.sqr(a2.subtract(b2)))).subtract(Polynomial
					.sqr(c1.subtract(d1)))).subtract(Polynomial.sqr(c2
					.subtract(d2)));

			return botanaPolynomials;
		}

		// area of two polygons
		// area of polygon is the sum of areas of triangles in polygon
		if (inputElement1 instanceof GeoNumeric
				&& inputElement2 instanceof GeoNumeric
				&& (inputElement1.getParentAlgorithm()).getRelatedModeID() == EuclidianConstants.MODE_AREA
				&& (inputElement2.getParentAlgorithm()).getRelatedModeID() == EuclidianConstants.MODE_AREA) {

			// get botanaVars of points of first polygon
			Variable[] v1 = ((SymbolicParametersBotanaAlgo) inputElement1
						.getParentAlgorithm()).getBotanaVars(inputElement1);
			// get botanaVars of points of first polygon
			Variable[] v2 = ((SymbolicParametersBotanaAlgo) inputElement2
						.getParentAlgorithm()).getBotanaVars(inputElement2);

			// add areas of triangles in first polygon
			Polynomial det1sum = Polynomial.area(v1[0], v1[1], v1[2], v1[3],
					v1[4], v1[5]);
			for (int i = 4; i < v1.length - 3; i = i + 2) {
				det1sum = det1sum.add(Polynomial.area(v1[0], v1[1], v1[i],
						v1[i + 1], v1[i + 2], v1[i + 3]));
			}

			// add areas of triangles in second polygon
			Polynomial det2sum = Polynomial.area(v2[0], v2[1], v2[2], v2[3],
					v2[4], v2[5]);
			for (int i = 4; i < v2.length - 3; i = i + 2) {
				det2sum = det2sum.add(Polynomial.area(v2[0], v2[1], v2[i],
						v2[i + 1], v2[i + 2], v2[i + 3]));
			}

			botanaPolynomials = new Polynomial[1][1];
			botanaPolynomials[0][0] = (Polynomial.sqr(det1sum))
					.subtract(Polynomial.sqr(det2sum));

			return botanaPolynomials;
		}
		// TODO: Implement circles etc.

		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability
}
