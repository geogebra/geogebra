package geogebra.common.kernel.prover;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.algos.SymbolicParameters;
import geogebra.common.kernel.algos.SymbolicParametersAlgo;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoSegment;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Simon Weitzhofer
 *         18th of May 2012
 *
 */
public class AlgoAreConcurrent extends AlgoElement implements SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgoAre {

	private GeoLine inputLine1, inputLine2, inputLine3; //input
	
    private GeoBoolean outputBoolean; //output	
	private Polynomial[] polynomials;
	private Polynomial[][] botanaPolynomials;


    /**
     * Creates a new AlgoAreConcurrent function
     * @param cons the Construction
     * @param label the name of the boolean
     * @param inputLine1 the first line
     * @param inputLine2 the second line
     * @param inputLine3 the third line
     */
	public AlgoAreConcurrent(final Construction cons, final String label,
			final GeoLine inputLine1, final GeoLine inputLine2,
			final GeoLine inputLine3) {
		super(cons);
        this.inputLine1=inputLine1;
        this.inputLine2=inputLine2;
        this.inputLine3=inputLine3;
               
        outputBoolean = new GeoBoolean(cons);

        setInputOutput();
        compute();
        // outputBoolean.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoAreConcurrent;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = inputLine1;
        input[1] = inputLine2;
        input[2] = inputLine3;

        super.setOutputLength(1);
        super.setOutput(0, outputBoolean);
        setDependencies(); // done by AlgoElement
    }
    																								
    /**
     * Returns the result of the test
     * @return true if the three lines are concurrent i.e. they intersect in a common point, false otherwise
     */
    public GeoBoolean getResult() {
        return outputBoolean;
    }

    @Override
	public final void compute() {
        double det=
        	inputLine1.getX()*inputLine2.getY()*inputLine3.getZ()+
	        inputLine2.getX()*inputLine3.getY()*inputLine1.getZ()+
	        inputLine3.getX()*inputLine1.getY()*inputLine2.getZ()-
	        inputLine3.getX()*inputLine2.getY()*inputLine1.getZ()-
	        inputLine2.getX()*inputLine1.getY()*inputLine3.getZ()-
	        inputLine1.getX()*inputLine3.getY()*inputLine2.getZ();
        outputBoolean.setValue(Kernel.isZero(det));
    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if ((inputLine1 instanceof GeoSegment)
				|| (inputLine2 instanceof GeoSegment)
				|| (inputLine3 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {
			inputLine1.getFreeVariables(variables);
			inputLine2.getFreeVariables(variables);
			inputLine3.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if ((inputLine1 instanceof GeoSegment)
				|| (inputLine2 instanceof GeoSegment)
				|| (inputLine3 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {
			int[] degree1 = inputLine1.getDegrees();
			int[] degree2 = inputLine2.getDegrees();
			int[] degree3 = inputLine3.getDegrees();
			int[] result = new int[1];
			result[0] = Math.max(degree1[0] + degree2[1] + degree3[2],
					Math.max(degree2[0] + degree3[1] + degree1[2],
					Math.max(degree3[0] + degree1[1] + degree2[2],
					Math.max(degree3[0] + degree2[1] + degree1[2],
					Math.max(degree2[0] + degree1[1] + degree3[2],
							degree1[0] + degree3[1] + degree2[2])))));
			return result;
		}
		throw new NoSymbolicParametersException();
	}
	
	public BigInteger[] getExactCoordinates(
			final HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if ((inputLine1 instanceof GeoSegment)
				|| (inputLine2 instanceof GeoSegment)
				|| (inputLine3 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {
			BigInteger[] coords1 = inputLine1.getExactCoordinates(values);
			BigInteger[] coords2 = inputLine2.getExactCoordinates(values);
			BigInteger[] coords3 = inputLine3.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[1];
			coords[0] = coords1[0].multiply(coords2[1]).multiply(coords3[2]).add(
					coords2[0].multiply(coords3[1]).multiply(coords1[2])).add(
					coords3[0].multiply(coords1[1]).multiply(coords2[2])).subtract(
							
					coords3[0].multiply(coords2[1]).multiply(coords1[2]).add(
					coords2[0].multiply(coords1[1]).multiply(coords3[2])).add(
					coords1[0].multiply(coords3[1]).multiply(coords2[2])));
			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if ((inputLine1 instanceof GeoSegment)
				|| (inputLine2 instanceof GeoSegment)
				|| (inputLine3 instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {
			Polynomial[] coords1 = inputLine1.getPolynomials();
			Polynomial[] coords2 = inputLine2.getPolynomials();
			Polynomial[] coords3 = inputLine3.getPolynomials();
			polynomials = new Polynomial[1];
			polynomials[0] = coords1[0].multiply(coords2[1]).multiply(coords3[2]).add(
					coords2[0].multiply(coords3[1]).multiply(coords1[2])).add(
					coords3[0].multiply(coords1[1]).multiply(coords2[2])).subtract(
							
					coords3[0].multiply(coords2[1]).multiply(coords1[2]).add(
					coords2[0].multiply(coords1[1]).multiply(coords3[2])).add(
					coords1[0].multiply(coords3[1]).multiply(coords2[2])));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[][] getBotanaPolynomials() throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (inputLine1 != null && inputLine2 != null && inputLine3 != null) {
			
			Variable[][] v = new Variable[3][4];
			v[0] = inputLine1.getBotanaVars(inputLine1);
			v[1] = inputLine2.getBotanaVars(inputLine2);
			v[2] = inputLine3.getBotanaVars(inputLine3);
			
			Variable[] nv = new Variable[2]; // new point for collinearity
			nv[0]=new Variable();
			nv[1]=new Variable();
			
			// We need three collinearities with an extra point.
			botanaPolynomials = new Polynomial[1][3];
			for (int i=0; i<3; ++i)
				botanaPolynomials[0][i] = Polynomial.collinear(v[i][0],v[i][1],v[i][2],v[i][3],nv[0],nv[1]);
			
			return botanaPolynomials;
			
		}
		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability
  
}
