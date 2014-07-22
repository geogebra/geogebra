package geogebra.common.kernel.prover;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.prover.polynomial.Polynomial;
import geogebra.common.kernel.prover.polynomial.Variable;

/**
 * Decides if the point lies on a path.
 * Currently only point/line check is implemented.
 * TODO: consider adding this as a command (and add a numerical check
 * in compute()). Then embedding into the Prove/ProveDetails commands will give
 * symbolic functionality automatically.
 *
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoIsOnPath extends AlgoElement implements
	SymbolicParametersBotanaAlgoAre {

	private GeoPoint inputPoint;
	private Path inputPath;
	
    private GeoBoolean outputBoolean; //output	
	private Polynomial[][] botanaPolynomials;

    /**
     * Creates a new AlgoIsOnPath function
     * @param cons the Construction
     * @param label the name of the boolean
     * @param inputPoint the point
     * @param inputLine the line
     */
	public AlgoIsOnPath(final Construction cons, final String label,
			final GeoPoint inputPoint, final Path inputPath) {
		super(cons);
        this.inputPoint=inputPoint;
        this.inputPath=inputPath;
               
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
	protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = inputPoint;
        input[1] = (GeoElement) inputPath;

        super.setOutputLength(1);
        super.setOutput(0, outputBoolean);
        setDependencies(); // done by AlgoElement
    }
    																								
    /**
     * Returns the result of the test
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

	public Polynomial[][] getBotanaPolynomials() throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (inputPoint != null && inputPath != null) {
			if (inputPath instanceof GeoLine) {
			
			Variable[] fv1 = new Variable[2];
			Variable[] fv2 = new Variable[4];
			fv1 = inputPoint.getBotanaVars(inputPoint);
			fv2 = ((GeoLine) inputPath).getBotanaVars((GeoLine)inputPath);

			botanaPolynomials = new Polynomial[1][1];
			botanaPolynomials[0][0] = Polynomial.collinear(fv1[0], fv1[1], fv2[0], fv2[1], fv2[2], fv2[3]); 
			return botanaPolynomials;
			} else if (inputPath instanceof GeoConic) {
				if (((GeoConic)inputPath).isCircle()) {
					Variable[] fv1 = new Variable[2];
					Variable[] fv2 = new Variable[4];
					fv1 = inputPoint.getBotanaVars(inputPoint);
					fv2 = ((GeoConic) inputPath).getBotanaVars((GeoConic)inputPath);

					botanaPolynomials = new Polynomial[1][1];
					botanaPolynomials[0][0] = Polynomial.equidistant(fv1[0], fv1[1], fv2[0], fv2[1], fv2[2], fv2[3]); 
					return botanaPolynomials;
				}
			}
			
		}
		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability
  
}
