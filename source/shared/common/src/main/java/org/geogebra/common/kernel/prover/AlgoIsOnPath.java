package org.geogebra.common.kernel.prover;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Decides if the point lies on a path. Currently only point/line check is
 * implemented. TODO: consider adding this as a command (and add a numerical
 * check in compute()). Then embedding into the Prove/ProveDetails commands will
 * give symbolic functionality automatically.
 *
 * @author Zoltan Kovacs
 */
public class AlgoIsOnPath extends AlgoElement
		implements SymbolicParametersBotanaAlgoAre {

	private GeoPoint inputPoint;
	private Path inputPath;

	private GeoBoolean outputBoolean; // output
	private PPolynomial[][] botanaPolynomials;
	private PVariable[] botanaVars;

	/**
	 * Creates a new AlgoIsOnPath function
	 * 
	 * @param cons
	 *            the Construction
	 * @param inputPoint
	 *            the point
	 * @param inputPath
	 *            the line
	 */
	public AlgoIsOnPath(final Construction cons, final GeoPoint inputPoint,
			final Path inputPath) {
		super(cons);
		this.inputPoint = inputPoint;
		this.inputPath = inputPath;

		outputBoolean = new GeoBoolean(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		// TODO: change this
		// return Commands.IsOnPath;
		return null;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputPoint;
		input[1] = (GeoElement) inputPath;

		setOnlyOutput(outputBoolean);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the result of the test
	 * 
	 * @return true if the three points lie on one line, false otherwise
	 */
	public GeoBoolean getResult() {
		return outputBoolean;
	}

	@Override
	public final void compute() {
		// TODO: implement this
		outputBoolean.setUndefined();
	}

	/**
	 * @return botana vars
	 */
	public PVariable[] getBotanaVars() {
		return botanaVars;
	}

	@Override
	public PPolynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (inputPoint != null && inputPath != null) {
			if (inputPath instanceof GeoLine) {

				PVariable[] fv1 = inputPoint.getBotanaVars(inputPoint);
				PVariable[] fv2 = ((GeoLine) inputPath)
						.getBotanaVars(inputPath);

				botanaPolynomials = new PPolynomial[1][1];
				botanaPolynomials[0][0] = PPolynomial.collinear(fv1[0], fv1[1],
						fv2[0], fv2[1], fv2[2], fv2[3]);
				return botanaPolynomials;
			} else if (inputPath instanceof GeoConic) {
				return getPolynomialsConic();
			}

		}
		throw new NoSymbolicParametersException();
	}

	private PPolynomial[][] getPolynomialsConic()
			throws NoSymbolicParametersException {
		if (((GeoConic) inputPath).isCircle()) {
			PVariable[] fv1 = inputPoint.getBotanaVars(inputPoint);
			PVariable[] fv2 = ((GeoConic) inputPath).getBotanaVars(inputPath);

			botanaPolynomials = new PPolynomial[1][1];
			botanaPolynomials[0][0] = PPolynomial.equidistant(fv1[0], fv1[1],
					fv2[0], fv2[1], fv2[2], fv2[3]);
			return botanaPolynomials;
		}
		if (((GeoConic) inputPath).isParabola()) {
			if (botanaVars == null) {
				botanaVars = new PVariable[2];
				// T - projection of P to directrix
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
			}

			PVariable[] fv1 = inputPoint.getBotanaVars(inputPoint);
			PVariable[] fv2 = ((GeoConic) inputPath).getBotanaVars(inputPath);

			botanaPolynomials = new PPolynomial[1][3];

			// |FP| = |PT|
			botanaPolynomials[0][0] = PPolynomial.equidistant(fv2[8], fv2[9],
					fv1[0], fv1[1], botanaVars[0], botanaVars[1]);

			// A,T,B collinear
			botanaPolynomials[0][1] = PPolynomial.collinear(fv2[4], fv2[5],
					botanaVars[0], botanaVars[1], fv2[6], fv2[7]);

			// PT orthogonal AB
			botanaPolynomials[0][2] = PPolynomial.perpendicular(fv1[0], fv1[1],
					botanaVars[0], botanaVars[1], fv2[4], fv2[5], fv2[6],
					fv2[7]);

			return botanaPolynomials;
		}
		if (((GeoConic) inputPath).isEllipse()
				|| ((GeoConic) inputPath).isHyperbola()) {

			if (botanaVars == null
					&& inputPoint.getParentAlgorithm() != null) {
				botanaVars = new PVariable[4];
				botanaVars = ((SymbolicParametersBotanaAlgo) inputPoint
						.getParentAlgorithm()).getBotanaVars(inputPoint);
			}
			// botana variables of input point
			PVariable[] fv1 = inputPoint.getBotanaVars(inputPoint);
			// botana variables of input path
			PVariable[] fv2 = ((GeoConic) inputPath).getBotanaVars(inputPath);

			botanaPolynomials = new PPolynomial[1][3];

			PPolynomial e_1 = new PPolynomial();
			PPolynomial e_2 = new PPolynomial();
			AlgoElement algoParent = inputPoint
					.getParentAlgorithm();
			// case input point is point on ellipse/hyperbola
			if (algoParent instanceof AlgoPointOnPath
					&& (((GeoConic) ((AlgoPointOnPath) algoParent).getPath())
							.isEllipse() || ((GeoConic) ((AlgoPointOnPath) algoParent)
							.getPath()).isHyperbola())) {
				e_1 = new PPolynomial(botanaVars[2]);
				e_2 = new PPolynomial(botanaVars[3]);
			}
			// case input point is point from ellipses definition
			else if (fv1[0].equals(fv2[10]) && fv1[1].equals(fv2[11])) {
				e_1 = new PPolynomial(fv2[2]);
				e_2 = new PPolynomial(fv2[3]);
			} else {
				e_1 = new PPolynomial(new PVariable(kernel));
				e_2 = new PPolynomial(new PVariable(kernel));
			}
			PPolynomial d1 = new PPolynomial(fv2[2]);
			PPolynomial d2 = new PPolynomial(fv2[3]);

			// d1+d2 = e1'+e2'
			botanaPolynomials[0][0] = d1.add(d2).subtract(e_1).subtract(e_2);

			// e1'^2=Polynomial.sqrDistance(a1,a2,p1,p2)
			botanaPolynomials[0][1] = PPolynomial.sqrDistance(fv2[6], fv2[7],
					fv1[0], fv1[1]).subtract(e_1.multiply(e_1));

			// e2'^2=Polynomial.sqrDistance(b1,b2,p1,p2)
			botanaPolynomials[0][2] = PPolynomial.sqrDistance(fv2[8], fv2[9],
					fv1[0], fv1[1]).subtract(e_2.multiply(e_2));

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

}
