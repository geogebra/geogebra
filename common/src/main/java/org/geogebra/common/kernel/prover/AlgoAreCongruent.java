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
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.util.debug.Log;

/**
 * Decides if two objects are congruent. Currently only just a few special cases are
 * implemented. The other cases return undefined at the moment.
 *
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoAreCongruent extends AlgoElement implements
		SymbolicParametersBotanaAlgoAre, SymbolicParametersAlgo {

	private GeoElement inputElement1; // input
	private GeoElement inputElement2; // input

	private GeoBoolean outputBoolean; // output
	private Polynomial[] polynomials;
	private Polynomial[][] botanaPolynomials;

	/**
	 * Creates a new AlgoAreCongruent function
	 * 
	 * @param cons
	 *            the Construction
	 * @param label
	 *            the name of the boolean
	 * @param a
	 *            the first object
	 * @param b
	 *            the second object
	 */
	public AlgoAreCongruent(final Construction cons,
			final GeoElement a, final GeoElement b) {
		super(cons);
		this.inputElement1 = a;
		this.inputElement2 = b;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
	}

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

		super.setOutputLength(1);
		super.setOutput(0, outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the result of the test
	 * 
	 * @return true if the two objects are congruent, false if not,
	 * 		undefined if testing is unimplemented in that case 
	 */
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		// Segments are congruent if they are of equal length:
		if (inputElement1 instanceof GeoSegment && inputElement2 instanceof GeoSegment) {
			outputBoolean.setValue(ExpressionNodeEvaluator.evalEquals(kernel,
				inputElement1, inputElement2).getBoolean());
			return;
		}
		// Lines/points are always congruent:
		if ((inputElement1 instanceof GeoLine && inputElement2 instanceof GeoLine) ||
				(inputElement1 instanceof GeoPoint && inputElement2 instanceof GeoPoint)) {
			outputBoolean.setValue(true);
			return;
		}
		// Conics: 
		if (inputElement1 instanceof GeoConic && inputElement2 instanceof GeoConic) {
			outputBoolean.setValue(((GeoConic) inputElement1)
					.isCongruent((GeoConic) inputElement2));
			return;
		}
		// Polygons:
		if (inputElement1.isGeoPolygon() && inputElement2.isGeoPolygon()) {
			outputBoolean.setValue(((GeoPolygon) inputElement1)
					.isCongruent((GeoPolygon) inputElement2));
			return;
		}

		if (inputElement1.isEqual(inputElement2)) {
			outputBoolean.setValue(true);
			return;
		}
		outputBoolean.setUndefinedProverOnly(); // Don't use this.
		// FIXME: It seems once outputBoolean is changing to undefined,
		// it remains undefined even if meanwhile it shouldn't.
		// FIXME: Implement all missing cases.
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint) && (inputElement2 instanceof GeoPoint))
					|| ((inputElement1 instanceof GeoLine) && (inputElement2 instanceof GeoLine))
					|| ((inputElement1 instanceof GeoVector) && (inputElement2 instanceof GeoVector))) {
				((SymbolicParametersAlgo) inputElement1)
						.getFreeVariables(variables);
				((SymbolicParametersAlgo) inputElement2)
						.getFreeVariables(variables);
				return;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public int[] getDegrees() throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint) && (inputElement2 instanceof GeoPoint))
					|| ((inputElement1 instanceof GeoLine) && (inputElement2 instanceof GeoLine))
					|| ((inputElement1 instanceof GeoVector) && (inputElement2 instanceof GeoVector))) {
				int[] degrees1 = ((SymbolicParametersAlgo) inputElement1)
						.getDegrees();
				int[] degrees2 = ((SymbolicParametersAlgo) inputElement2)
						.getDegrees();
				int[] degrees = new int[1];
				degrees[0] = Math.max(
						Math.max(degrees1[0] + degrees2[2], degrees2[0]
								+ degrees1[2]),
						Math.max(degrees1[1] + degrees2[2], degrees2[1]
								+ degrees1[2]));
				return degrees;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if ((inputElement1 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint) && (inputElement2 instanceof GeoPoint))
					|| ((inputElement1 instanceof GeoLine) && (inputElement2 instanceof GeoLine))
					|| ((inputElement1 instanceof GeoVector) && (inputElement2 instanceof GeoVector))) {
				BigInteger[] coords1 = ((SymbolicParametersAlgo) inputElement1)
						.getExactCoordinates(values);
				BigInteger[] coords2 = ((SymbolicParametersAlgo) inputElement2)
						.getExactCoordinates(values);
				BigInteger[] coords = new BigInteger[1];
				coords[0] = coords1[0]
						.multiply(coords2[2])
						.subtract(coords2[0].multiply(coords1[2]))
						.abs()
						.add(coords1[1].multiply(coords2[2])
								.subtract(coords2[1].multiply(coords1[2]))
								.abs());
				return coords;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		Log.debug(polynomials);
		if (polynomials != null) {
			return polynomials;
		}
		if ((inputElement1 instanceof GeoSegment)
				|| (inputElement2 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputElement1 != null && inputElement2 != null) {
			if (((inputElement1 instanceof GeoPoint) && (inputElement2 instanceof GeoPoint))
					|| ((inputElement1 instanceof GeoLine) && (inputElement2 instanceof GeoLine))
					|| ((inputElement1 instanceof GeoVector) && (inputElement2 instanceof GeoVector))) {
				Polynomial[] coords1 = ((SymbolicParametersAlgo) inputElement1)
						.getPolynomials();
				Polynomial[] coords2 = ((SymbolicParametersAlgo) inputElement2)
						.getPolynomials();
				polynomials = new Polynomial[2];
				polynomials[0] = coords1[0].multiply(coords2[2]).subtract(
						coords2[0].multiply(coords1[2]));
				polynomials[1] = coords1[1].multiply(coords2[2]).subtract(
						coords2[1].multiply(coords1[2]));
				return polynomials;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (inputElement1 instanceof GeoPoint
				&& inputElement2 instanceof GeoPoint) {
			// Same as in AreEqual.
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

		// Order is important here: a GeoSegment is also a GeoLine!
		if (inputElement1 instanceof GeoSegment
				&& inputElement2 instanceof GeoSegment) {
			// We check whether their length are equal.
			botanaPolynomials = new Polynomial[1][1];

			Variable[] v1 = new Variable[4];
			Variable[] v2 = new Variable[4];
			v1 = ((GeoSegment) inputElement1).getBotanaVars(inputElement1); // AB
			v2 = ((GeoSegment) inputElement2).getBotanaVars(inputElement2); // CD

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

		if (inputElement1 instanceof GeoLine
				&& inputElement2 instanceof GeoLine) {
			// Same as in AreEqual.
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
		
		if (inputElement1 instanceof GeoConic && inputElement2 instanceof GeoConic) {
			if (((GeoConic) inputElement1).isCircle()
					&& ((GeoConic) inputElement2).isCircle()) {
				botanaPolynomials = new Polynomial[1][1];

				Variable[] v1 = new Variable[4];
				Variable[] v2 = new Variable[4];
				// circle with center A and point B
				v1 = ((GeoConic) inputElement1).getBotanaVars(inputElement1);
				// circle with center C and point D
				v2 = ((GeoConic) inputElement2).getBotanaVars(inputElement2);

				// We want to prove: |AB|^2 = |CD|^2
				botanaPolynomials[0][0] = Polynomial.sqrDistance(v1[0], v1[1],
						v1[2], v1[3]).subtract(
						Polynomial.sqrDistance(v2[0], v2[1], v2[2], v2[3]));
				return botanaPolynomials;
			}

			if (((GeoConic) inputElement1).isParabola()
					&& ((GeoConic) inputElement2).isParabola()) {
				botanaPolynomials = new Polynomial[1][5];

				Variable[] v1 = new Variable[10];
				Variable[] v2 = new Variable[10];
				v1 = ((GeoConic) inputElement1).getBotanaVars(inputElement1);
				v2 = ((GeoConic) inputElement2).getBotanaVars(inputElement2);

				// auxiliary points
				Variable[] auxVars = new Variable[4];
				// P - first auxiliary point
				auxVars[0] = new Variable();
				auxVars[1] = new Variable();
				// P' - second auxiliary point
				auxVars[2] = new Variable();
				auxVars[3] = new Variable();

				// We want to prove, that the distance between foci points and
				// directrixes are equal
				// FP orthogonal to AB
				botanaPolynomials[0][0] = Polynomial.perpendicular(v1[8],
						v1[9], auxVars[0], auxVars[1], v1[4], v1[5], v1[6],
						v1[7]);

				// A, B, P collinear
				botanaPolynomials[0][1] = Polynomial.collinear(auxVars[0],
						auxVars[1], v1[4], v1[5], v1[6], v1[7]);

				// F'P' orthogonal to A'B'
				botanaPolynomials[0][2] = Polynomial.perpendicular(v2[8],
						v2[9], auxVars[2], auxVars[3], v2[4], v2[5], v2[6],
						v2[7]);

				// A', B', P' collinear
				botanaPolynomials[0][3] = Polynomial.collinear(auxVars[2],
						auxVars[3], v2[4], v2[5], v2[6], v2[7]);

				// |FP|^2 = |F'P'|^2
				botanaPolynomials[0][4] = Polynomial.sqrDistance(v1[8], v1[9],
						auxVars[0], auxVars[1]).subtract(
						Polynomial.sqrDistance(v2[8], v2[9], auxVars[2],
								auxVars[3]));
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

			Polynomial a1 = new Polynomial(vB[0]);
			Polynomial a2 = new Polynomial(vB[1]);
			Polynomial b1 = new Polynomial(vA[0]);
			Polynomial b2 = new Polynomial(vA[1]);
			Polynomial c1 = new Polynomial(vC[0]);
			Polynomial c2 = new Polynomial(vC[1]);
			Polynomial d1 = new Polynomial(vE[0]);
			Polynomial d2 = new Polynomial(vE[1]);
			Polynomial e1 = new Polynomial(vD[0]);
			Polynomial e2 = new Polynomial(vD[1]);
			Polynomial f1 = new Polynomial(vF[0]);
			Polynomial f2 = new Polynomial(vF[1]);

			Polynomial p1 = a1.subtract(c1).multiply(b1.subtract(a1));
			Polynomial p2 = a2.subtract(c2).multiply(b2.subtract(a2));
			// (CA*AB)^2
			Polynomial nominator1 = Polynomial.sqr(p1.add(p2));
			Polynomial p3 = Polynomial.sqr(a1.subtract(c1)).add(
					Polynomial.sqr(a2.subtract(c2)));
			Polynomial p4 = Polynomial.sqr(b1.subtract(a1)).add(
					Polynomial.sqr(b2.subtract(a2)));
			// ||CA||^2 * ||AB||^2
			Polynomial denominator1 = p3.multiply(p4);

			Polynomial p5 = d1.subtract(f1).multiply(e1.subtract(d1));
			Polynomial p6 = d2.subtract(f2).multiply(e2.subtract(d2));
			// (FD*DE)^2
			Polynomial nominator2 = Polynomial.sqr(p5.add(p6));
			Polynomial p7 = Polynomial.sqr(d1.subtract(f1)).add(
					Polynomial.sqr(d2.subtract(f2)));
			Polynomial p8 = Polynomial.sqr(e1.subtract(d1)).add(
					Polynomial.sqr(e2.subtract(d2)));
			// ||FD||^2 * ||DE||^2
			Polynomial denominator2 = p7.multiply(p8);

			// We want to prove: (CA*AB)^2 / (||CA||^2 * ||AB||^2) = (FD*DE)^2 /
			// (||FD||^2 * ||DE||^2)
			botanaPolynomials = new Polynomial[1][1];
			botanaPolynomials[0][0] = nominator1.multiply(denominator2)
					.subtract(denominator1.multiply(nominator2));

			return botanaPolynomials;

		}

		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability

}
