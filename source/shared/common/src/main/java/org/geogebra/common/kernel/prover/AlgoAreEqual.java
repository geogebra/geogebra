package org.geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoAnglePoints;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.NumberFormatAdapter;

/**
 * Decides if the objects are equal. Can be embedded into the Prove command to
 * work symbolically.
 * 
 * @author Simon Weitzhofer 17 May 2012
 * @author Zoltan Kovacs
 */
public class AlgoAreEqual extends AlgoElement
		implements SymbolicParametersBotanaAlgoAre {

	private static final NumberFormatAdapter formatter =
			FormatFactory.getPrototype().getNumberFormat(8);

	private GeoElement inputElement1; // input
	private GeoElement inputElement2; // input

	private GeoBoolean outputBoolean; // output
	private PPolynomial[][] botanaPolynomials;

	/**
	 * Tests if two objects are equal
	 * 
	 * @param cons
	 *            The construction the objects depend on
	 * @param inputElement1
	 *            the first object
	 * @param inputElement2
	 *            the second object
	 */
	public AlgoAreEqual(Construction cons, GeoElement inputElement1,
			GeoElement inputElement2) {
		super(cons);
		this.inputElement1 = inputElement1;
		this.inputElement2 = inputElement2;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();

	}

	/**
	 * Tests if two objects are equal
	 * 
	 * @param cons
	 *            The construction the objects depend on
	 * @param label
	 *            the label for the AlgoAreEqual object
	 * @param inputElement1
	 *            the first object
	 * @param inputElement2
	 *            the second object
	 */
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

		setOnlyOutput(outputBoolean);
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
		if (inputElement1 instanceof GeoInputBox) {
			// For GeoInputBoxes the AreEqual command has a special meaning, see
			// https://jira.geogebra.org/browse/WLY-83
			// this is used to heuristically compare the user input to a given expression
			// without invoking the CAS
			GeoInputBox inputBox = (GeoInputBox) inputElement1;
			outputBoolean.setValue(compareInputBoxContent(inputBox.getLinkedGeo(), inputElement2));
		} else {
			// Formerly we used this:
			// outputBoolean.setValue(ExpressionNodeEvaluator.evalEquals(kernel,
			// inputElement1, inputElement2).getBoolean());
			// But this way is more useful eg for segments, polygons
			// ie compares endpoints NOT just length

			// #5331
			// The formerly used computation is now implemented in AlgoAreCongruent.
			outputBoolean.setValue(inputElement1.isEqual(inputElement2));
		}
	}

	private boolean compareInputBoxContent(GeoElementND actual, GeoElementND expected) {
		if (!actual.isDefined() || actual.getDefinition() == null) {
			return false;
		}

		double value1 = actual.evaluateDouble();
		double value2 = expected.evaluateDouble();

		// First we compare if the numerical value of the expression in the
		// input box matches the expected
		if (!DoubleUtil.isRatioEqualTo1(value1, value2, Kernel.MAX_PRECISION)) {
			return false;
		}

		// Then we check if none of the numbers used in the input exceed the
		// precision we used when comparing the numerical result
		return actual.getDefinition().isConstant()
				&& !actual.getDefinition().inspect(v -> {
					if (v instanceof MyDouble) {
						// Special numbers, such as E, Pi, and 1 degree are allowed
						double d = ((MyDouble) v).getDouble();
						if (DoubleUtil.isEqual(d, Math.PI, Kernel.MAX_PRECISION)
							|| DoubleUtil.isEqual(d, Math.E, Kernel.MAX_PRECISION)
							|| DoubleUtil.isEqual(d, Kernel.PI_180, Kernel.MAX_PRECISION)) {
							return false;
						}

						// Integers between -10^8 and 10^8 are allowed
						if (MyDouble.exactEqual(d, Math.round(d))) {
							return d <= -1E8 || 1E8 <= d;
						}

						// Decimal numbers with less than 8 significant digits are allowed
						return countSignificantDigits(d) > 8;
					}

					return false;
				});
	}

	private int countSignificantDigits(double d) {
		String s = formatter.format(d);
		if (s.contains(".")) {
			return s.length() - 1;
		} else {
			return s.length();
		}
	}

	@Override
	public PPolynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoPoint
				&& inputElement2 instanceof GeoPoint) {
			botanaPolynomials = new PPolynomial[2][1];

			PVariable[] v1 = ((GeoPoint) inputElement1).getBotanaVars(inputElement1); // A=(x1,y1)
			PVariable[] v2 = ((GeoPoint) inputElement2).getBotanaVars(inputElement2); // B=(x2,y2)

			// We want to prove: 1) x1-x2==0, 2) y1-y2==0
			botanaPolynomials[0][0] = new PPolynomial(v1[0])
					.subtract(new PPolynomial(v2[0]));
			botanaPolynomials[1][0] = new PPolynomial(v1[1])
					.subtract(new PPolynomial(v2[1]));
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoSegment
				&& inputElement2 instanceof GeoSegment) {
			botanaPolynomials = new PPolynomial[1][1];
			// The segments are AB and CD. AB is the same as CD iff
			// (a1=c1 and a2=c2 and b1=d1 and b2=d2) or
			// (a1=c2 and a2=c1 and b1=d1 and b2=d2) or
			// (a1=c1 and a2=c2 and b1=d2 and b2=d1) or
			// (a1=c2 and a2=c1 and b1=d2 and b2=d1) or
			// currently unimplemented
			throw new NoSymbolicParametersException();
		}

		if (inputElement1 instanceof GeoLine
				&& inputElement2 instanceof GeoLine) {
			botanaPolynomials = new PPolynomial[2][1];

			PVariable[] v1 = ((GeoLine) inputElement1).getBotanaVars(inputElement1); // AB
			PVariable[] v2 = ((GeoLine) inputElement2).getBotanaVars(inputElement2); // CD

			// We want to prove: 1) A,B,C collinear, 2) A,B,D collinear
			botanaPolynomials[0][0] = PPolynomial.collinear(v1[0], v1[1], v1[2],
					v1[3], v2[0], v2[1]);
			botanaPolynomials[1][0] = PPolynomial.collinear(v1[0], v1[1], v1[2],
					v1[3], v2[2], v2[3]);
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoConic
				&& inputElement2 instanceof GeoConic) {
			if (((GeoConic) inputElement1).isCircle()
					&& ((GeoConic) inputElement2).isCircle()) {
				botanaPolynomials = new PPolynomial[2][1];

				// circle with center A and point B
				PVariable[] v1 = ((GeoConic) inputElement1).getBotanaVars(inputElement1);
				// circle with center C and point D
				PVariable[] v2 = ((GeoConic) inputElement2).getBotanaVars(inputElement2);

				// We want to prove: 1) |AC|^2 = 0, 2) |AB|^2 = |CD|^2
				botanaPolynomials[0][0] = PPolynomial.sqrDistance(v1[0], v1[1],
						v2[0], v2[1]);
				botanaPolynomials[1][0] = PPolynomial
						.sqrDistance(v1[0], v1[1], v1[2], v1[3])
						.subtract(PPolynomial.sqrDistance(v2[0], v2[1], v2[2],
								v2[3]));
				return botanaPolynomials;
			}

			if (((GeoConic) inputElement1).isParabola()
					&& ((GeoConic) inputElement2).isParabola()) {
				botanaPolynomials = new PPolynomial[4][1];

				PVariable[] v1 = ((GeoConic) inputElement1).getBotanaVars(inputElement1);
				PVariable[] v2 = ((GeoConic) inputElement2).getBotanaVars(inputElement2);

				// We want to prove: 1) A, B, A' coll. 2) A, B, B' coll. 3) F=F'
				// f1 = f'1
				botanaPolynomials[0][0] = new PPolynomial(v1[8])
						.subtract(new PPolynomial(v2[8]));

				// f2 = f'2
				botanaPolynomials[1][0] = new PPolynomial(v1[9])
						.subtract(new PPolynomial(v2[9]));

				// A, B, A'
				botanaPolynomials[2][0] = PPolynomial.collinear(v1[4], v1[5],
						v1[6], v1[7], v2[4], v2[5]);

				// A, B, B'
				botanaPolynomials[3][0] = PPolynomial.collinear(v1[4], v1[5],
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
			PVariable[] vA = A.getBotanaVars(A);
			PVariable[] vB = B.getBotanaVars(B);
			PVariable[] vC = C.getBotanaVars(C);

			AlgoAnglePoints algo2 = (AlgoAnglePoints) inputElement2
					.getParentAlgorithm();
			// get points of second angle
			GeoPoint D = (GeoPoint) algo2.input[0];
			GeoPoint E = (GeoPoint) algo2.input[1];
			GeoPoint F = (GeoPoint) algo2.input[2];
			PVariable[] vD = D.getBotanaVars(D);
			PVariable[] vE = E.getBotanaVars(E);
			PVariable[] vF = F.getBotanaVars(F);

			botanaPolynomials = new PPolynomial[3][1];
			// We want to prove: 1) A = D 2) B = E 3) C = F
			botanaPolynomials[0][0] = PPolynomial.sqrDistance(vA[0], vA[1],
					vD[0], vD[0]);
			botanaPolynomials[1][0] = PPolynomial.sqrDistance(vB[0], vB[1],
					vE[0], vE[1]);
			botanaPolynomials[2][0] = PPolynomial.sqrDistance(vC[0], vC[1],
					vF[0], vF[1]);
			return botanaPolynomials;

		}

		// area of two polygons
		// area of polygon is the sum of areas of triangles in polygon
		if (inputElement1 instanceof GeoNumeric
				&& inputElement2 instanceof GeoNumeric
				&& (inputElement1.getParentAlgorithm())
				.getRelatedModeID() == EuclidianConstants.MODE_AREA
				&& (inputElement2.getParentAlgorithm())
				.getRelatedModeID() == EuclidianConstants.MODE_AREA) {

			// get botanaVars of points of first polygon
			PVariable[] v1 = ((SymbolicParametersBotanaAlgo) inputElement1
					.getParentAlgorithm()).getBotanaVars(inputElement1);
			// get botanaVars of points of first polygon
			PVariable[] v2 = ((SymbolicParametersBotanaAlgo) inputElement2
					.getParentAlgorithm()).getBotanaVars(inputElement2);

			// add areas of triangles in first polygon
			PPolynomial det1sum = PPolynomial.area(v1[0], v1[1], v1[2], v1[3],
					v1[4], v1[5]);
			for (int i = 4; i < v1.length - 3; i = i + 2) {
				det1sum = det1sum.add(PPolynomial.area(v1[0], v1[1], v1[i],
						v1[i + 1], v1[i + 2], v1[i + 3]));
			}

			// add areas of triangles in second polygon
			PPolynomial det2sum = PPolynomial.area(v2[0], v2[1], v2[2], v2[3],
					v2[4], v2[5]);
			for (int i = 4; i < v2.length - 3; i = i + 2) {
				det2sum = det2sum.add(PPolynomial.area(v2[0], v2[1], v2[i],
						v2[i + 1], v2[i + 2], v2[i + 3]));
			}

			botanaPolynomials = new PPolynomial[1][1];
			botanaPolynomials[0][0] = (PPolynomial.sqr(det1sum))
					.subtract(PPolynomial.sqr(det2sum));

			return botanaPolynomials;
		}

		// distance between 2 point without segment
		if (inputElement1 instanceof GeoNumeric
				&& inputElement2 instanceof GeoNumeric
				&& (inputElement1.getParentAlgorithm())
						.getRelatedModeID() == EuclidianConstants.MODE_DISTANCE
				&& (inputElement2.getParentAlgorithm())
						.getRelatedModeID() == EuclidianConstants.MODE_DISTANCE) {
			// We check whether their length are equal.
			botanaPolynomials = new PPolynomial[1][1];

			// get coordinates of the start and end points
			PVariable[] v1 = ((SymbolicParametersBotanaAlgo) inputElement1
					.getParentAlgorithm()).getBotanaVars(inputElement1); // AB
			PVariable[] v2 = ((SymbolicParametersBotanaAlgo) inputElement2
					.getParentAlgorithm()).getBotanaVars(inputElement2); // CD

			// We want to prove: d(AB)=d(CD) =>
			// (a1-b1)^2+(a2-b2)^2=(c1-d1)^2+(c2-d2)^2
			// => (a1-b1)^2+(a2-b2)^2-(c1-d1)^2-(c2-d2)^2
			PPolynomial a1 = new PPolynomial(v1[0]);
			PPolynomial a2 = new PPolynomial(v1[1]);
			PPolynomial b1 = new PPolynomial(v1[2]);
			PPolynomial b2 = new PPolynomial(v1[3]);
			PPolynomial c1 = new PPolynomial(v2[0]);
			PPolynomial c2 = new PPolynomial(v2[1]);
			PPolynomial d1 = new PPolynomial(v2[2]);
			PPolynomial d2 = new PPolynomial(v2[3]);
			botanaPolynomials[0][0] = ((PPolynomial.sqr(a1.subtract(b1))
					.add(PPolynomial.sqr(a2.subtract(b2))))
							.subtract(PPolynomial.sqr(c1.subtract(d1))))
									.subtract(PPolynomial.sqr(c2.subtract(d2)));

			return botanaPolynomials;
		}

		/*
		 * Equality of two expressions, one of them is a segment, or both are
		 * expressions.
		 */
		if ((inputElement1 instanceof GeoNumeric
				&& inputElement2 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoNumeric
						&& inputElement1 instanceof GeoSegment)
				|| (inputElement1 instanceof GeoNumeric
						&& inputElement2 instanceof GeoNumeric)) {

			GeoNumeric n1, n2 = null;
			GeoSegment s = null;
			if (inputElement1 instanceof GeoNumeric) {
				n1 = (GeoNumeric) inputElement1;
				if (inputElement2 instanceof GeoNumeric) {
					n2 = (GeoNumeric) inputElement2;
				} else {
					s = (GeoSegment) inputElement2;
				}
			} else {
				n1 = (GeoNumeric) inputElement2;
				s = (GeoSegment) inputElement1;
			}

			GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();

			ValidExpression resultVE;
			if ((inputElement1 instanceof GeoNumeric
					&& inputElement2 instanceof GeoNumeric)) {
				// Create n1-n2=var as a ValidExpression
				resultVE = cas.getCASparser()
						.parseGeoGebraCASInputAndResolveDummyVars(
								n1.getDefinition() + "-(" + n2.getDefinition()
										+ ")",
								kernel, null);
			} else {
				// Create n1-s=var as a ValidExpression
				resultVE = cas.getCASparser()
						.parseGeoGebraCASInputAndResolveDummyVars(
								n1.getDefinition() + "-" + s.getLabelSimple(),
								kernel, null);
			}
			// Convert the ValidExpression to ExpressionNode
			ExpressionNode en = new ExpressionNode(kernel, resultVE);
			// Silently create an AlgoDependentNumber from the ExpressionNode
			AlgoDependentNumber algoDepNumber = new AlgoDependentNumber(
					n1.getConstruction(), en, false, null, false, false);
			// Obtain the polynomials
			PPolynomial[] result = algoDepNumber.getBotanaPolynomials(n1); // n1 unused
			int no = result.length;
			botanaPolynomials = new PPolynomial[1][no];
			for (int i = 0; i < no; ++i) {
				botanaPolynomials[0][(i - 1 + no) % no] = result[i];
				// use the first equation as last (it will be denied),
				// the order of the other eqs remains the same
			}
			PVariable[] botanaVars = algoDepNumber.getBotanaVars(n1); // n1 unused
			// Add the equation var=0 to the polynomial list
			Map<PVariable, BigInteger> m = new HashMap<>();
			m.put(botanaVars[0], BigInteger.ZERO);
			botanaPolynomials[0][no - 1] = botanaPolynomials[0][no - 1]
					.substitute(m);
			// This AlgoDependentNumber is not needed anymore
			n1.getConstruction().removeFromAlgorithmList(algoDepNumber);
			return botanaPolynomials;

		}
		// TODO: Implement circles etc.

		throw new NoSymbolicParametersException();
	}

}
