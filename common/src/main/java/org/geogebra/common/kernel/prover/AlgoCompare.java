package org.geogebra.common.kernel.prover;

import static org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;

import org.geogebra.common.cas.realgeom.RealGeomWebService;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Compares two objects geometrically by using real quantifier elimination.
 *
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoCompare extends AlgoElement {

    private GeoElement inputElement1; // input
    private GeoElement inputElement2; // input

    private GeoText outputText; // output

    /**
     * Compares two objects
     *
     * @param cons          The construction the objects depend on
     * @param inputElement1 the first object
     * @param inputElement2 the second object
     */
    public AlgoCompare(Construction cons, GeoElement inputElement1,
                       GeoElement inputElement2) {
        super(cons);
        this.inputElement1 = inputElement1;
        this.inputElement2 = inputElement2;

        outputText = new GeoText(cons);

        setInputOutput();
        compute();

    }

    /**
     * Compares two objects
     *
     * @param cons          The construction the objects depend on
     * @param label         the label for the AlgoAreCompare object
     * @param inputElement1 the first object
     * @param inputElement2 the second object
     */
    public AlgoCompare(Construction cons, String label,
                       GeoElement inputElement1, GeoElement inputElement2) {
        this(cons, inputElement1, inputElement2);
        outputText.setLabel(label);
    }

    @Override
    public Commands getClassName() {
        return Commands.Compare;
    }

    @Override
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = inputElement1;
        input[1] = inputElement2;

        super.setOutputLength(1);
        super.setOutput(0, outputText);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Gets the result of the test
     *
     * @return the result of comparison
     */

    public GeoText getResult() {
        return outputText;
    }

    @Override
    public final void compute() {
        if (inputElement1.getKernel().isSilentMode()) {
            return;
        }

        RealGeomWebService realgeomWS = cons.getApplication().getRealGeomWS();
        if (realgeomWS == null || (!realgeomWS.isAvailable())) {
            outputText.setTextString("RealGeomWS is not available");
            return;
        }

        AlgoAreCongruent aae = new AlgoAreCongruent(cons, inputElement1, inputElement2);
        GeoBoolean gb = new GeoBoolean(cons);
        gb.setParentAlgorithm(aae);
        Prover p = UtilFactory.getPrototype().newProver();
        p.setProverEngine(Prover.ProverEngine.BOTANAS_PROVER);
        AlgebraicStatement as = new AlgebraicStatement(gb, null, p);
        as.removeThesis();

        PVariable var1 = new PVariable(kernel);
        PVariable var2 = new PVariable(kernel);
        // Creating the describing polynomials:
        PPolynomial poly1 = new PPolynomial();
        PPolynomial poly2 = new PPolynomial();

        try {
            if (inputElement1 instanceof GeoSegment
                    && inputElement2 instanceof GeoSegment) {
                // Obtaining their lengths to two new variables:

                PVariable[] v1 = new PVariable[4];
                PVariable[] v2 = new PVariable[4];
                PPolynomial p1 = new PPolynomial();
                PPolynomial p2 = new PPolynomial();

                v1 = ((GeoSegment) inputElement1).getBotanaVars(inputElement1); // AB
                v2 = ((GeoSegment) inputElement2).getBotanaVars(inputElement2); // CD

                PPolynomial a1 = new PPolynomial(v1[0]);
                PPolynomial a2 = new PPolynomial(v1[1]);
                PPolynomial b1 = new PPolynomial(v1[2]);
                PPolynomial b2 = new PPolynomial(v1[3]);
                PPolynomial c1 = new PPolynomial(v2[0]);
                PPolynomial c2 = new PPolynomial(v2[1]);
                PPolynomial d1 = new PPolynomial(v2[2]);
                PPolynomial d2 = new PPolynomial(v2[3]);
                p1 = ((PPolynomial.sqr(a1.subtract(b1))
                        .add(PPolynomial.sqr(a2.subtract(b2)))).subtract(new PPolynomial(var1)
                        .multiply(new PPolynomial(var1))));
                p2 = ((PPolynomial.sqr(c1.subtract(d1))
                        .add(PPolynomial.sqr(c2.subtract(d2)))).subtract(new PPolynomial(var2)
                        .multiply(new PPolynomial(var2))));

                poly1 = p1.substitute(as.substitutions);
                poly2 = p2.substitute(as.substitutions);
            }
        } catch (NoSymbolicParametersException e) {
            return;
        }
        as.addPolynomial(poly1);
        as.addPolynomial(poly2);

        Log.debug("poly1=" + poly1);
        Log.debug("poly2=" + poly2);

        String rgCommand = "euclideansolver";
        StringBuilder rgParameters = new StringBuilder();
        rgParameters.append("lhs=" + var1 + "&" + "rhs=" + var2 + "&")
                .append("polys=");
        as.computeStrings();
        rgParameters.append(as.getPolys());
        String freeVars = as.getFreeVars();
        String elimVars = as.getElimVars();
        Log.debug("freevars=" + freeVars);
        Log.debug("elimvars=" + elimVars);

        String vars = freeVars;
        if (!"".equals(elimVars)) {
            vars += "," + elimVars;
        }

        rgParameters.append("&vars=").append(vars);
        rgParameters.append("&posvariables=").append(var1).append(",").append(var2);
        rgParameters.append("&mode=explore");

        Log.debug(rgParameters);

        String result = realgeomWS.directCommand(rgCommand, rgParameters.toString());
        if ("m > 0".equals(result)) {
            outputText.setTextString(""); // no useful information gained
            return;
        }

        String inp1 = inputElement1.getLabelSimple();
        String inp2 = inputElement2.getLabelSimple();
        // Inequality[0, Less, m, LessEqual, 2]
        result = result.replaceAll("Inequality\\[(.*?), (.*?), m, (.*?), (.*?)\\]",
                "$1 " + Unicode.CENTER_DOT + " " + inp2 +
                        " $2 " + inp1 + " $3 $4 " + Unicode.CENTER_DOT + " " + inp2);
        // Remove "0*inp2 Less" from the beginning (it's trivial)
        result = result.replaceAll("(^0 " + Unicode.CENTER_DOT + " \\w+ Less )", "");
        // m >= 1/2
        result = result.replaceAll("m >= (.*)",
                inp1 + " GreaterEqual $1 " + Unicode.CENTER_DOT + " " + inp2);
        // m >= 1/2
        result = result.replaceAll("m > (.*)",
                inp1 + " Greater $1 " + Unicode.CENTER_DOT + " " + inp2);
        // m == 1
        result = result.replaceAll("m == (.*)",
                inp1 + " = $1 " + Unicode.CENTER_DOT + " " + inp2);
        // Simplify 1*... to ...
        result = result.replaceAll("(\\s)1 " + Unicode.CENTER_DOT + " ", "$1");
        // Use math symbols instead of Mathematica notation
        result = result.replace("LessEqual", String.valueOf(Unicode.LESS_EQUAL));
        result = result.replace("Less", "<");
        result = result.replace("GreaterEqual", String.valueOf(Unicode.GREATER_EQUAL));
        result = result.replace("Greater", ">");
        // result = result.replace("==", "=");
        result = result.replace("&& m > 0", "");
        result = result.replace("m > 0", "");

        outputText.setTextString(result);

    }

}
