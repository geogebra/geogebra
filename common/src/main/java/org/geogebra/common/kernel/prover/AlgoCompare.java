package org.geogebra.common.kernel.prover;

import static org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.geogebra.common.cas.realgeom.RealGeomWebService;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
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
    private boolean htmlMode;

    private GeoText outputText; // output

    /**
     * Compares two objects
     *
     * @param cons          The construction the objects depend on
     * @param inputElement1 the first object
     * @param inputElement2 the second object
     */
    public AlgoCompare(Construction cons, GeoElement inputElement1,
                       GeoElement inputElement2, boolean htmlMode) {
        super(cons);
        this.inputElement1 = inputElement1;
        this.inputElement2 = inputElement2;
        this.htmlMode = htmlMode;

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
                       GeoElement inputElement1, GeoElement inputElement2, boolean htmlMode) {
        this(cons, inputElement1, inputElement2, htmlMode);
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

    AlgebraicStatement as;
    SortedMap<GeoSegment, PVariable> rewrites;
    StringTemplate portableFormat = StringTemplate.casCopyTemplate;
    StringTemplate fancyFormat = StringTemplate.algebraTemplate;

    @Override
    public final void compute() {

        if (inputElement1.getKernel().isSilentMode()) {
            return;
        }

        String lhs_var = "";
        String rhs_var = "";
        rewrites = new TreeMap<>(Collections.reverseOrder());

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
        as = new AlgebraicStatement(gb, null, p);
        as.removeThesis();
        ArrayList<String> extraPolys = new ArrayList<>();
        ArrayList<String> extraVars = new ArrayList<>();

        String inp1 = "";
        String inp2 = "";

        if (inputElement1 instanceof GeoSegment) {
            lhs_var = (processSegment((GeoSegment) inputElement1)).getName();
            if (htmlMode) {
                inp1 = inputElement1.getLabelTextOrHTML(false);
            } else {
                inp1 = inputElement1.getLabelSimple();
            }
        }

        if (inputElement2 instanceof GeoSegment) {
            rhs_var = (processSegment((GeoSegment) inputElement2)).getName();
            if (htmlMode) {
                inp2 = inputElement2.getLabelTextOrHTML(false);
            } else {
                inp2 = inputElement2.getLabelSimple();
            }
        }

        if (inputElement1 instanceof GeoNumeric) {
            processExpr((GeoNumeric) inputElement1);
            lhs_var = "w1";
            extraPolys.add("-w1+" + rewrite(inputElement1.getDefinition(portableFormat)));
            extraVars.add("w1");
            inp1 = "(";
            if (htmlMode) {
                inp1 += inputElement1.getDefinitionHTML(false);
            } else {
                inp1 += inputElement1.getDefinition(fancyFormat);
            }
            inp1 += ")";
        }

        if (inputElement2 instanceof GeoNumeric) {
            processExpr((GeoNumeric) inputElement2);
            rhs_var = "w2";
            extraPolys.add("-w2+" + rewrite(inputElement2.getDefinition(portableFormat)));
            extraVars.add("w2");
            inp2 = "(";
            if (htmlMode) {
                inp2 += inputElement2.getDefinitionHTML(false);
            } else {
                inp2 += inputElement2.getDefinition(fancyFormat);
            }
            inp2 += ")";
        }

        String rgCommand = "euclideansolver";
        StringBuilder rgParameters = new StringBuilder();
        rgParameters.append("lhs=" + lhs_var + "&" + "rhs=" + rhs_var + "&")
                .append("polys=");
        as.computeStrings();
        rgParameters.append(as.getPolys());
        for (String po : extraPolys) {
            rgParameters.append(",").append(po);
        }

        String freeVars = as.getFreeVars();
        String elimVars = as.getElimVars();
        Log.debug("freevars=" + freeVars);
        Log.debug("elimvars=" + elimVars);

        String vars = freeVars;
        if (!"".equals(elimVars)) {
            vars += "," + elimVars;
        }
        for (String v : extraVars) {
            vars += "," + v;
        }
        rgParameters.append("&vars=").append(vars);
        rgParameters.append("&posvariables=");
        Iterator it = rewrites.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            GeoSegment s = (GeoSegment) me.getKey();
            PVariable v = (PVariable) me.getValue();
            rgParameters.append(v.getName()).append(",");
        }
        rgParameters.deleteCharAt(rgParameters.length() - 1);
        rgParameters.append("&mode=explore");

        Log.debug(rgParameters);

        String result = realgeomWS.directCommand(rgCommand, rgParameters.toString());
        if ("m > 0".equals(result)) {
            outputText.setTextString(""); // no useful information gained
            return;
        }

        result = result.replaceAll("Sqrt\\[(.*?)\\]", Unicode.SQUARE_ROOT + "$1");
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
        String repl = "<";
        if (htmlMode) {
            repl = "&lt;";
        }
        result = result.replace("Less", repl);
        result = result.replace("GreaterEqual", String.valueOf(Unicode.GREATER_EQUAL));
        repl = ">";
        if (htmlMode) {
            repl = "&gt;";
        }
        result = result.replace("Greater", repl);
        // result = result.replace("==", "=");
        result = result.replace("&& m > 0", "");
        result = result.replace("m > 0", "");

        outputText.setTextString(result);

        aae.remove();
        gb.remove();
    }

    private PVariable processSegment(GeoSegment s) {

        if (rewrites.containsKey(s)) {
            return rewrites.get(s);
        }

        PVariable var = new PVariable(kernel);
        // Creating the describing polynomial:
        PPolynomial poly = new PPolynomial();
        try {

            PVariable[] v = new PVariable[4];
            PPolynomial p = new PPolynomial();

            v = (s.getBotanaVars(s)); // AB

            PPolynomial a1 = new PPolynomial(v[0]);
            PPolynomial a2 = new PPolynomial(v[1]);
            PPolynomial b1 = new PPolynomial(v[2]);
            PPolynomial b2 = new PPolynomial(v[3]);
            p = ((PPolynomial.sqr(a1.subtract(b1))
                    .add(PPolynomial.sqr(a2.subtract(b2)))).subtract(new PPolynomial(var)
                    .multiply(new PPolynomial(var))));

            poly = p.substitute(as.substitutions);
            as.addPolynomial(poly);

        } catch (NoSymbolicParametersException e) {
            return null;
        }
        rewrites.put(s, var);
        return var;
    }

    private void processExpr(GeoNumeric n) {
        AlgoElement ae = n.getParentAlgorithm();
        if (ae instanceof AlgoDependentNumber) {
            for (GeoElement ge : ae.getInput()) {
                if (!(ge instanceof GeoSegment)) {
                    // this is an expression that contains a non-segment object, unimplemented
                    outputText.setTextString("");
                    return;
                }
                PVariable v = processSegment((GeoSegment) ge);
            }
        }
    }

    private String rewrite(String exp) {
        // https://stackoverflow.com/questions/1326682/java-replacing-multiple-different-substring-in-a-string-at-once-or-in-the-most
        Iterator it = rewrites.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            GeoSegment s = (GeoSegment) me.getKey();
            PVariable v = (PVariable) me.getValue();
            exp = exp.replace(s.getLabel(portableFormat), v.getName());
        }
        exp = exp.replace(" ", "");
        return exp;
    }

}
