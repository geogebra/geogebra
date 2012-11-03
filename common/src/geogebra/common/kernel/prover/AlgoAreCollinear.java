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
import geogebra.common.kernel.geos.GeoPoint;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Decides if the points are collinear.
 * Can be embedded into the Prove command to work symbolically.
 * @author Simon Weitzhofer
 *         18th April 2012
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoAreCollinear extends AlgoElement implements SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgoAre {

	private GeoPoint inputPoint1, inputPoint2, inputPoint3; //input
	
    private GeoBoolean outputBoolean; //output	
	private Polynomial[] polynomials;
	private Polynomial[][] botanaPolynomials;

    /**
     * Creates a new AlgoAreCollinear function
     * @param cons the Construction
     * @param label the name of the boolean
     * @param inputPoint1 the first point
     * @param inputPoint2 the second point
     * @param inputPoint3 the third point
     */
	public AlgoAreCollinear(final Construction cons, final String label,
			final GeoPoint inputPoint1, final GeoPoint inputPoint2,
			final GeoPoint inputPoint3) {
		super(cons);
        this.inputPoint1=inputPoint1;
        this.inputPoint2=inputPoint2;
        this.inputPoint3=inputPoint3;
               
        outputBoolean = new GeoBoolean(cons);

        setInputOutput();
        compute();
        // outputBoolean.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoAreCollinear;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = inputPoint1;
        input[1] = inputPoint2;
        input[2] = inputPoint3;

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
        double det=
        	inputPoint1.getX()*inputPoint2.getY()*inputPoint3.getZ()+
	        inputPoint2.getX()*inputPoint3.getY()*inputPoint1.getZ()+
	        inputPoint3.getX()*inputPoint1.getY()*inputPoint2.getZ()-
	        inputPoint3.getX()*inputPoint2.getY()*inputPoint1.getZ()-
	        inputPoint2.getX()*inputPoint1.getY()*inputPoint3.getZ()-
	        inputPoint1.getX()*inputPoint3.getY()*inputPoint2.getZ();
        outputBoolean.setValue(Kernel.isZero(det));
    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null) {
			inputPoint1.getFreeVariables(variables);
			inputPoint2.getFreeVariables(variables);
			inputPoint3.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null) {
			int[] degree1 = inputPoint1.getDegrees();
			int[] degree2 = inputPoint2.getDegrees();
			int[] degree3 = inputPoint3.getDegrees();
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
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null) {
			BigInteger[] coords1 = inputPoint1.getExactCoordinates(values);
			BigInteger[] coords2 = inputPoint2.getExactCoordinates(values);
			BigInteger[] coords3 = inputPoint3.getExactCoordinates(values);
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
		
		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null) {
			Polynomial[] coords1 = inputPoint1.getPolynomials();
			Polynomial[] coords2 = inputPoint2.getPolynomials();
			Polynomial[] coords3 = inputPoint3.getPolynomials();
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

		if (inputPoint1 != null && inputPoint2 != null && inputPoint3 != null) {
			
			Variable[] fv1 = new Variable[2];
			Variable[] fv2 = new Variable[2];
			Variable[] fv3 = new Variable[2];
			fv1 = inputPoint1.getBotanaVars(inputPoint1);
			fv2 = inputPoint2.getBotanaVars(inputPoint2);
			fv3 = inputPoint3.getBotanaVars(inputPoint3);

			botanaPolynomials = new Polynomial[1][1];
			botanaPolynomials[0][0] = Polynomial.collinear(fv1[0], fv1[1], fv2[0], fv2[1], fv3[0], fv3[1]); 
			return botanaPolynomials;
			
		}
		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability
  
}
