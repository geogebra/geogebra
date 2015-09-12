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
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 * Decides if the point lies on a path. Currently only point/line check is
 * implemented. TODO: consider adding this as a command (and add a numerical
 * check in compute()). Then embedding into the Prove/ProveDetails commands will
 * give symbolic functionality automatically.
 *
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoIsOnPath extends AlgoElement implements
		SymbolicParametersBotanaAlgoAre {

	private GeoPoint inputPoint;
	private Path inputPath;

	private GeoBoolean outputBoolean; // output
	private Polynomial[][] botanaPolynomials;
	private Variable[] botanaVars;

	/**
	 * Creates a new AlgoIsOnPath function
	 * 
	 * @param cons
	 *            the Construction
	 * @param label
	 *            the name of the boolean
	 * @param inputPoint
	 *            the point
	 * @param inputLine
	 *            the line
	 */
	public AlgoIsOnPath(final Construction cons,
			final GeoPoint inputPoint, final Path inputPath) {
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

		super.setOutputLength(1);
		super.setOutput(0, outputBoolean);
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

	public Variable[] getBotanaVars() {
		return botanaVars;
	}

	public Polynomial[][] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (inputPoint != null && inputPath != null) {
			if (inputPath instanceof GeoLine) {

				Variable[] fv1 = new Variable[2];
				Variable[] fv2 = new Variable[4];
				fv1 = inputPoint.getBotanaVars(inputPoint);
				fv2 = ((GeoLine) inputPath).getBotanaVars((GeoLine) inputPath);

				botanaPolynomials = new Polynomial[1][1];
				botanaPolynomials[0][0] = Polynomial.collinear(fv1[0], fv1[1],
						fv2[0], fv2[1], fv2[2], fv2[3]);
				return botanaPolynomials;
			} else if (inputPath instanceof GeoConic) {
				if (((GeoConic) inputPath).isCircle()) {
					Variable[] fv1 = new Variable[2];
					Variable[] fv2 = new Variable[4];
					fv1 = inputPoint.getBotanaVars(inputPoint);
					fv2 = ((GeoConic) inputPath)
							.getBotanaVars((GeoConic) inputPath);

					botanaPolynomials = new Polynomial[1][1];
					botanaPolynomials[0][0] = Polynomial.equidistant(fv1[0],
							fv1[1], fv2[0], fv2[1], fv2[2], fv2[3]);
					return botanaPolynomials;
				}
				if (((GeoConic) inputPath).isParabola()) {
					if (botanaVars == null) {
						botanaVars = new Variable[2];
						// T - projection of P to directrix
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
					}

					Variable[] fv1 = new Variable[2];
					Variable[] fv2 = new Variable[10];
					fv1 = inputPoint.getBotanaVars(inputPoint);
					fv2 = ((GeoConic) inputPath)
							.getBotanaVars((GeoConic) inputPath);

					botanaPolynomials = new Polynomial[1][3];

					// |FP| = |PT|
					botanaPolynomials[0][0] = Polynomial.equidistant(fv2[8],
							fv2[9], fv1[0], fv1[1], botanaVars[0],
							botanaVars[1]);

					// A,T,B collinear
					botanaPolynomials[0][1] = Polynomial.collinear(fv2[4],
							fv2[5], botanaVars[0], botanaVars[1], fv2[6],
							fv2[7]);

					// PT orthogonal AB
					botanaPolynomials[0][2] = Polynomial.perpendicular(fv1[0],
							fv1[1], botanaVars[0], botanaVars[1], fv2[4],
							fv2[5], fv2[6], fv2[7]);

					return botanaPolynomials;
				}
				if (((GeoConic) inputPath).isEllipse()
						|| ((GeoConic) inputPath).isHyperbola()) {

					if (botanaVars == null
							&& ((GeoElement) inputPoint).getParentAlgorithm() != null) {
						botanaVars = new Variable[4];
						botanaVars = ((SymbolicParametersBotanaAlgo) ((GeoElement) inputPoint)
								.getParentAlgorithm())
								.getBotanaVars(inputPoint);
					}

					Variable[] fv1 = new Variable[4];
					Variable[] fv2 = new Variable[12];
					// botana variables of input point
					fv1 = inputPoint.getBotanaVars(inputPoint);
					// botana variables of input path
					fv2 = ((GeoConic) inputPath)
							.getBotanaVars((GeoConic) inputPath);

					botanaPolynomials = new Polynomial[1][3];
					
					Polynomial e_1 = new Polynomial();
					Polynomial e_2 = new Polynomial();
					AlgoElement algoParent = ((GeoElement) inputPoint)
							.getParentAlgorithm();
					// case input point is point on ellipse/hyperbola
					if (algoParent instanceof AlgoPointOnPath
							&& (((GeoConic) ((AlgoPointOnPath) algoParent)
									.getPath()).isEllipse() || ((GeoConic) ((AlgoPointOnPath) algoParent)
									.getPath()).isHyperbola())) {
						e_1 = new Polynomial(botanaVars[2]);
						e_2 = new Polynomial(botanaVars[3]);
					}
					// case input point is point from ellipses definition
					else if (fv1[0].equals(fv2[10]) && fv1[1].equals(fv2[11])) {
						e_1 = new Polynomial(fv2[2]);
						e_2 = new Polynomial(fv2[3]);
					} else {
						e_1 = new Polynomial(new Variable());
						e_2 = new Polynomial(new Variable());
					}
					Polynomial d1 = new Polynomial(fv2[2]);
					Polynomial d2 = new Polynomial(fv2[3]);

					// d1+d2 = e1'+e2'
					botanaPolynomials[0][0] = d1.add(d2).subtract(e_1)
							.subtract(e_2);

					// e1'^2=Polynomial.sqrDistance(a1,a2,p1,p2)
					botanaPolynomials[0][1] = Polynomial.sqrDistance(fv2[6],
							fv2[7], fv1[0], fv1[1]).subtract(e_1.multiply(e_1));

					// e2'^2=Polynomial.sqrDistance(b1,b2,p1,p2)
					botanaPolynomials[0][2] = Polynomial.sqrDistance(fv2[8],
							fv2[9], fv1[0], fv1[1]).subtract(e_2.multiply(e_2));

					return botanaPolynomials;
				}
			}

		}
		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability

}
