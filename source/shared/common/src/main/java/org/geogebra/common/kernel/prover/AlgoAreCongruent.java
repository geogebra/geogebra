package org.geogebra.common.kernel.prover;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAnglePoints;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.debug.Log;

/**
 * Decides if two objects are congruent. Currently only just a few special cases
 * are implemented. The other cases return undefined at the moment.
 *
 * @author Zoltan Kovacs
 */
public class AlgoAreCongruent extends AlgoElement
		implements SymbolicParametersBotanaAlgoAre, SymbolicParametersAlgo {

	private GeoElement inputElement1; // input
	private GeoElement inputElement2; // input

	private GeoBoolean outputBoolean; // output
	private PPolynomial[] polynomials;
	private PPolynomial[][] botanaPolynomials;

	/**
	 * Creates a new AlgoAreCongruent function
	 * 
	 * @param cons
	 *            the Construction
	 * @param a
	 *            the first object
	 * @param b
	 *            the second object
	 */
	public AlgoAreCongruent(final Construction cons, final GeoElement a,
			final GeoElement b) {
		super(cons);
		this.inputElement1 = a;
		this.inputElement2 = b;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
	}

	/**
	 * Creates a new AlgoAreCongruent function
	 * 
	 * @param cons
	 *            the Construction
	 * @param label
	 *            the label for the AlgoAreCongruent object
	 * @param a
	 *            the first object
	 * @param b
	 *            the second object
	 */
	public AlgoAreCongruent(final Construction cons, final String label,
			final GeoElement a, final GeoElement b) {
		this(cons, a, b);
		outputBoolean.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.AreCongruent;
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
	 * Returns the result of the test
	 * 
	 * @return true if the two objects are congruent, false if not, undefined if
	 *         testing is unimplemented in that case
	 */
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {

		ExtendedBoolean congruent = inputElement1.isCongruent(inputElement2);
		if (!ExtendedBoolean.UNKNOWN.equals(congruent)) {
			outputBoolean.setDefined();
			outputBoolean.setValue(congruent.boolVal());
		} else {
			outputBoolean.setUndefinedProverOnly();
		}
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint)
					&& (inputElement2 instanceof GeoPoint))
					|| ((inputElement1 instanceof GeoLine)
							&& (inputElement2 instanceof GeoLine))
					|| ((inputElement1 instanceof GeoVector)
							&& (inputElement2 instanceof GeoVector))) {
				((SymbolicParametersAlgo) inputElement1)
						.getFreeVariables(variables);
				((SymbolicParametersAlgo) inputElement2)
						.getFreeVariables(variables);
				return;
			}
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint)
					&& (inputElement2 instanceof GeoPoint))
					|| ((inputElement1 instanceof GeoLine)
							&& (inputElement2 instanceof GeoLine))
					|| ((inputElement1 instanceof GeoVector)
							&& (inputElement2 instanceof GeoVector))) {
				int[] degrees1 = ((SymbolicParametersAlgo) inputElement1)
						.getDegrees(a);
				int[] degrees2 = ((SymbolicParametersAlgo) inputElement2)
						.getDegrees(a);
				int[] degrees = new int[1];
				degrees[0] = Math.max(
						Math.max(degrees1[0] + degrees2[2],
								degrees2[0] + degrees1[2]),
						Math.max(degrees1[1] + degrees2[2],
								degrees2[1] + degrees1[2]));
				return degrees;
			}
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint)
					&& (inputElement2 instanceof GeoPoint))
					|| ((inputElement1 instanceof GeoLine)
							&& (inputElement2 instanceof GeoLine))
					|| ((inputElement1 instanceof GeoVector)
							&& (inputElement2 instanceof GeoVector))) {
				BigInteger[] coords1 = ((SymbolicParametersAlgo) inputElement1)
						.getExactCoordinates(values);
				BigInteger[] coords2 = ((SymbolicParametersAlgo) inputElement2)
						.getExactCoordinates(values);
				BigInteger[] coords = new BigInteger[1];
				coords[0] = coords1[0].multiply(coords2[2])
						.subtract(coords2[0].multiply(coords1[2])).abs()
						.add(coords1[1].multiply(coords2[2])
								.subtract(coords2[1].multiply(coords1[2]))
								.abs());
				return coords;
			}
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		Log.debug(polynomials);
		if (polynomials != null) {
			return polynomials;
		}
		if ((inputElement1 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint)
					&& (inputElement2 instanceof GeoPoint))
					|| ((inputElement1 instanceof GeoLine)
							&& (inputElement2 instanceof GeoLine))
					|| ((inputElement1 instanceof GeoVector)
							&& (inputElement2 instanceof GeoVector))) {
				PPolynomial[] coords1 = ((SymbolicParametersAlgo) inputElement1)
						.getPolynomials();
				PPolynomial[] coords2 = ((SymbolicParametersAlgo) inputElement2)
						.getPolynomials();
				polynomials = new PPolynomial[2];
				polynomials[0] = coords1[0].multiply(coords2[2])
						.subtract(coords2[0].multiply(coords1[2]));
				polynomials[1] = coords1[1].multiply(coords2[2])
						.subtract(coords2[1].multiply(coords1[2]));
				return polynomials;
			}
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoPoint
				&& inputElement2 instanceof GeoPoint) {
			// Same as in AreEqual.
			botanaPolynomials = new PPolynomial[2][1];

			PVariable[] v1 = new PVariable[2];
			PVariable[] v2 = new PVariable[2];
			v1 = ((GeoPoint) inputElement1).getBotanaVars(inputElement1); // A=(x1,y1)
			v2 = ((GeoPoint) inputElement2).getBotanaVars(inputElement2); // B=(x2,y2)

			// We want to prove: 1) x1-x2==0, 2) y1-y2==0
			botanaPolynomials[0][0] = new PPolynomial(v1[0])
					.subtract(new PPolynomial(v2[0]));
			botanaPolynomials[1][0] = new PPolynomial(v1[1])
					.subtract(new PPolynomial(v2[1]));
			return botanaPolynomials;
		}

		// Order is important here: a GeoSegment is also a GeoLine!
		if (inputElement1 instanceof GeoSegment
				&& inputElement2 instanceof GeoSegment) {
			// We check whether their length are equal.
			botanaPolynomials = new PPolynomial[1][1];

			PVariable[] v1 = new PVariable[4];
			PVariable[] v2 = new PVariable[4];
			v1 = ((GeoSegment) inputElement1).getBotanaVars(inputElement1); // AB
			v2 = ((GeoSegment) inputElement2).getBotanaVars(inputElement2); // CD

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

		if (inputElement1 instanceof GeoLine
				&& inputElement2 instanceof GeoLine) {
			// Same as in AreEqual.
			botanaPolynomials = new PPolynomial[2][1];

			PVariable[] v1 = new PVariable[4];
			PVariable[] v2 = new PVariable[4];
			v1 = ((GeoLine) inputElement1).getBotanaVars(inputElement1); // AB
			v2 = ((GeoLine) inputElement2).getBotanaVars(inputElement2); // CD

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
				botanaPolynomials = new PPolynomial[1][1];

				PVariable[] v1 = new PVariable[4];
				PVariable[] v2 = new PVariable[4];
				// circle with center A and point B
				v1 = ((GeoConic) inputElement1).getBotanaVars(inputElement1);
				// circle with center C and point D
				v2 = ((GeoConic) inputElement2).getBotanaVars(inputElement2);

				// We want to prove: |AB|^2 = |CD|^2
				botanaPolynomials[0][0] = PPolynomial
						.sqrDistance(v1[0], v1[1], v1[2], v1[3])
						.subtract(PPolynomial.sqrDistance(v2[0], v2[1], v2[2],
								v2[3]));
				return botanaPolynomials;
			}

			if (((GeoConic) inputElement1).isParabola()
					&& ((GeoConic) inputElement2).isParabola()) {
				botanaPolynomials = new PPolynomial[1][5];

				PVariable[] v1 = new PVariable[10];
				PVariable[] v2 = new PVariable[10];
				v1 = ((GeoConic) inputElement1).getBotanaVars(inputElement1);
				v2 = ((GeoConic) inputElement2).getBotanaVars(inputElement2);

				// auxiliary points
				PVariable[] auxVars = new PVariable[4];
				// P - first auxiliary point
				auxVars[0] = new PVariable(kernel);
				auxVars[1] = new PVariable(kernel);
				// P' - second auxiliary point
				auxVars[2] = new PVariable(kernel);
				auxVars[3] = new PVariable(kernel);

				// We want to prove, that the distance between foci points and
				// directrixes are equal
				// FP orthogonal to AB
				botanaPolynomials[0][0] = PPolynomial.perpendicular(v1[8], v1[9],
						auxVars[0], auxVars[1], v1[4], v1[5], v1[6], v1[7]);

				// A, B, P collinear
				botanaPolynomials[0][1] = PPolynomial.collinear(auxVars[0],
						auxVars[1], v1[4], v1[5], v1[6], v1[7]);

				// F'P' orthogonal to A'B'
				botanaPolynomials[0][2] = PPolynomial.perpendicular(v2[8], v2[9],
						auxVars[2], auxVars[3], v2[4], v2[5], v2[6], v2[7]);

				// A', B', P' collinear
				botanaPolynomials[0][3] = PPolynomial.collinear(auxVars[2],
						auxVars[3], v2[4], v2[5], v2[6], v2[7]);

				// |FP|^2 = |F'P'|^2
				botanaPolynomials[0][4] = PPolynomial
						.sqrDistance(v1[8], v1[9], auxVars[0], auxVars[1])
						.subtract(PPolynomial.sqrDistance(v2[8], v2[9],
								auxVars[2], auxVars[3]));
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

			PPolynomial a1 = new PPolynomial(vB[0]);
			PPolynomial a2 = new PPolynomial(vB[1]);
			PPolynomial b1 = new PPolynomial(vA[0]);
			PPolynomial b2 = new PPolynomial(vA[1]);
			PPolynomial c1 = new PPolynomial(vC[0]);
			PPolynomial c2 = new PPolynomial(vC[1]);
			PPolynomial d1 = new PPolynomial(vE[0]);
			PPolynomial d2 = new PPolynomial(vE[1]);
			PPolynomial e1 = new PPolynomial(vD[0]);
			PPolynomial e2 = new PPolynomial(vD[1]);
			PPolynomial f1 = new PPolynomial(vF[0]);
			PPolynomial f2 = new PPolynomial(vF[1]);

			PPolynomial p1 = a1.subtract(c1).multiply(b1.subtract(a1));
			PPolynomial p2 = a2.subtract(c2).multiply(b2.subtract(a2));
			// (CA*AB)^2
			PPolynomial numerator1 = PPolynomial.sqr(p1.add(p2));
			PPolynomial p3 = PPolynomial.sqr(a1.subtract(c1))
					.add(PPolynomial.sqr(a2.subtract(c2)));
			PPolynomial p4 = PPolynomial.sqr(b1.subtract(a1))
					.add(PPolynomial.sqr(b2.subtract(a2)));
			// ||CA||^2 * ||AB||^2
			PPolynomial denominator1 = p3.multiply(p4);

			PPolynomial p5 = d1.subtract(f1).multiply(e1.subtract(d1));
			PPolynomial p6 = d2.subtract(f2).multiply(e2.subtract(d2));
			// (FD*DE)^2
			PPolynomial numerator2 = PPolynomial.sqr(p5.add(p6));
			PPolynomial p7 = PPolynomial.sqr(d1.subtract(f1))
					.add(PPolynomial.sqr(d2.subtract(f2)));
			PPolynomial p8 = PPolynomial.sqr(e1.subtract(d1))
					.add(PPolynomial.sqr(e2.subtract(d2)));
			// ||FD||^2 * ||DE||^2
			PPolynomial denominator2 = p7.multiply(p8);

			// We want to prove: (CA*AB)^2 / (||CA||^2 * ||AB||^2) = (FD*DE)^2 /
			// (||FD||^2 * ||DE||^2)
			botanaPolynomials = new PPolynomial[1][1];
			botanaPolynomials[0][0] = numerator1.multiply(denominator2)
					.subtract(denominator1.multiply(numerator2));

			return botanaPolynomials;

		}

		throw new NoSymbolicParametersException();
	}

}
