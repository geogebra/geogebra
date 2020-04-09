package org.geogebra.common.kernel.prover;

import static org.geogebra.common.kernel.prover.ProverBotanasMethod.AlgebraicStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.geogebra.common.cas.GeoGebraCAS;
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
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.RealGeomWSSettings;
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

    private String cachedEqualityStatement = null;

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

    private double startTime;
    private String retval = "";

    private void debugElapsedTime() {
        int elapsedTime = (int) (UtilFactory.getPrototype().getMillisecondTime()
                - startTime);

        /*
         * Don't remove this. It is needed for automated testing. (String match
         * is assumed.)
         */
        Log.debug("Benchmarking: " + elapsedTime + " ms");
        Log.debug("COMPARISON RESULT IS " + retval);
    }

    @Override
    public final void compute() {

        if (inputElement1.getKernel().isSilentMode()) {
            return;
        }

        // setInputOutput();
        do {
            cons.removeFromAlgorithmList(this);
        } while (cons.getAlgoList().contains(this));
        // Adding this again:
        cons.addToAlgorithmList(this);
        cons.removeFromConstructionList(this);
        // Adding this again:
        cons.addToConstructionList(this, true);
        // TODO: consider moving setInputOutput() out from compute()

        String lhs_var = "";
        String rhs_var = "";
        rewrites = new TreeMap<>(Collections.reverseOrder());

        RealGeomWebService realgeomWS = cons.getApplication().getRealGeomWS();

        AlgoAreCongruent aae = new AlgoAreCongruent(cons, inputElement1, inputElement2);
        GeoBoolean gb = new GeoBoolean(cons);
        gb.setParentAlgorithm(aae);
        Prover p = UtilFactory.getPrototype().newProver();
        p.setProverEngine(Prover.ProverEngine.BOTANAS_PROVER);
        as = new AlgebraicStatement(gb, null, p);
        as.removeThesis();
        ArrayList<String> extraPolys = new ArrayList<>();
        ArrayList<String> extraVars = new ArrayList<>();
        aae.remove();
        gb.remove();

        String inp1 = "";
        String inp2 = "";

        String currentEqualityStatement = p.getTextFormat(p.getStatement());
        Log.debug("currentEqualityStatement = " + currentEqualityStatement);
        Log.debug("cachedEqualityStatement = " + cachedEqualityStatement);
        if (cachedEqualityStatement != null && currentEqualityStatement.equals(cachedEqualityStatement)) {
            return;
        }
        cachedEqualityStatement = currentEqualityStatement;

        // Adding benchmarking:
        startTime = UtilFactory.getPrototype().getMillisecondTime();

        if (inputElement1 instanceof GeoSegment) {
            lhs_var = (processSegment((GeoSegment) inputElement1)).getName();
            if (htmlMode) {
                inp1 = inputElement1.getColoredLabel();
            } else {
                inp1 = inputElement1.getLabelSimple();
            }
        }

        if (inputElement2 instanceof GeoSegment) {
            rhs_var = (processSegment((GeoSegment) inputElement2)).getName();
            if (htmlMode) {
                inp2 = inputElement2.getColoredLabel();
            } else {
                inp2 = inputElement2.getLabelSimple();
            }
        }

        if (inputElement1 instanceof GeoNumeric) {
            processExpr((GeoNumeric) inputElement1);
            lhs_var = "w1";
            extraPolys.add("-w1+" + rewrite(inputElement1.getDefinition(portableFormat)));
            extraVars.add("w1");
            if (htmlMode) {
                inp1 += inputElement1.getColoredLabel();
            } else {
                if (inputElement1.getLabelSimple() != null) {
                    inp1 += inputElement1.getLabelSimple();
                }
                else {
                    inp1 += "(" + inputElement1.getDefinition(fancyFormat) + ")";
                }
            }
        }

        if (inputElement2 instanceof GeoNumeric) {
            processExpr((GeoNumeric) inputElement2);
            rhs_var = "w2";
            extraPolys.add("-w2+" + rewrite(inputElement2.getDefinition(portableFormat)));
            extraVars.add("w2");
            if (htmlMode) {
                inp2 += inputElement2.getColoredLabel();
            } else {
                if (inputElement2.getLabelSimple() != null) {
                    inp2 += inputElement2.getLabelSimple();
                }
                else {
                    inp2 += "(" + inputElement2.getDefinition(fancyFormat) + ")";
                }
            }
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

        /*
        Set<Set<PPolynomial>> eliminationIdeal;
        eliminationIdeal = PPolynomial.eliminate(
                as.getPolynomials()
                        .toArray(new PPolynomial[as.getPolynomials()
                                .size()]),
                as.substitutions, kernel, 0, true, false,
                as.freeVariables);
        */

        String vars = freeVars;
        if (!"".equals(elimVars)) {
            vars += "," + elimVars;
        }
        for (String v : extraVars) {
            vars += "," + v;
        }

        // Start of direct Giac computation.
        /* Example code:
           [assume(m>0),solve(eliminate(subst([-v6+v4+v3-v1,-v5-v4+v3+v2,v7+v4-v2-v1,v8-v3-v2+v1,
           -v9^2+v8^2+v7^2-2*v8*v4+v4^2-2*v7*v3+v3^2,-v10^2+v4^2+v3^2-2*v4*v2+v2^2-2*v3*v1+v1^2,-w1+v10+v10,w1*m-(v9)],
           [v1=0,v2=0,v3=0,v4=1]),[v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,w1])[0],m)][1]
         */
        StringBuilder gc = new StringBuilder();
        gc.append("[assume(m>0),solve(eliminate(subst([");
        gc.append(as.getPolys());
        for (String po : extraPolys) {
            gc.append(",").append(po);
        }
        gc.append(",").append(rhs_var).append("*m-(").append(lhs_var).append(")],[");

        StringBuilder varsubst = new StringBuilder();
        int i = 0;
        for (PVariable v : as.freeVariables) {
            if (i<4) {
                int value = 0;
                if (i == 2)
                    value = 1;
                // 0,0,1,0 according to (0,0) and (1,0)
                if (i > 0)
                    varsubst.append(",");
                varsubst.append(v).append("=").append(value);
                ++i;
            }
        }

        Localization loc = kernel.getLocalization();
        String or = loc.getMenu("Symbol.Or").toLowerCase();

        gc.append(varsubst).append("]),[");
        gc.append(vars).append("])[0],m)][1]");
        GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
        boolean useGiac = RealGeomWSSettings.isUseGiacElimination();
        boolean useRealGeom = false;
        outputText.setTextString(retval); // retval == "" here

        if (useGiac) {
            try {
                String elimSol = cas.getCurrentCAS().evaluateRaw(gc.toString());
                if (!elimSol.equals("?") && !elimSol.equals("{}")) {
                    elimSol = elimSol.substring(1, elimSol.length() - 1);
                    String[] cases = elimSol.split(",");
                    for (String result : cases) {
                        if (!"".equals(retval)) {
                            retval += " " + or + " ";
                            // Multiple results found, so let the situation be clarified via RealGeom
                            useRealGeom = true;
                        }
                        result = result.replace("m=", "");
                        result = result.replace("*", "" + Unicode.CENTER_DOT);
                        retval += inp1 + " = " + result + " " + Unicode.CENTER_DOT + " " + inp2;
                    }
                    outputText.setTextString(retval);
                    debugElapsedTime();
                    if (!useRealGeom) {
                        return;
                    }
                }
                // The result is not just a number. (Or a set of numbers.)
            } catch (Throwable throwable) {
                Log.debug("Error when trying elimination");
            }
        }
        // End of direct Giac computation.

        if (realgeomWS == null || (!realgeomWS.isAvailable())) {
            // outputText.setTextString("RealGeomWS is not available");
            Log.debug("RealGeomWS is not available");
            debugElapsedTime();
            return;
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

        String rgResult = realgeomWS.directCommand(rgCommand, rgParameters.toString());

        if ("$Aborted".equals(rgResult)) {
            Log.debug("Timeout in RealGeom");
            rgResult = "";
        }

        if (rgResult != null && !rgResult.equals("")) {
            // If there was some useful result in RealGeom, then use it and forget the previous results from Giac.
            retval = "";
            String[] cases = rgResult.split("\\|\\|");

            for (String result : cases) {

                if ("m > 0".equals(result)) {
                    continue;
                }

                if (!"".equals(retval)) {
                    retval += " " + or + " ";
                }

                String oldResult = "";
                while (!oldResult.equals(result)) {
                    oldResult = result;
                    // This is just a workaround. E.g. "m == Sqrt[40 - 6*Sqrt[3]/3]" is converted to
                    // "m == √(40 - 6*Sqrt[3)/3]" which is syntactically wrong, and also incomplete.
                    // So we repeat this step as many times as it is required.
                    result = result.replaceAll("Sqrt\\[(.*?)\\]", Unicode.SQUARE_ROOT + "$1");
                }
                // Inequality[0, Less, m, LessEqual, 2]
                result = result.replaceAll("Inequality\\[(.*?), (.*?), m, (.*?), (.*?)\\]",
                        "($1) " + Unicode.CENTER_DOT + " " + inp2 +
                                " $2 " + inp1 + " $3 ($4) " + Unicode.CENTER_DOT + " " + inp2);
                // Remove "(0)*inp2 Less" from the beginning (it's trivial)
                result = result.replaceAll("^\\(0\\) " + Unicode.CENTER_DOT + " .*? Less ", "");
                // m >= 1/2
                result = result.replaceAll("m >= (.*)",
                        inp1 + " GreaterEqual ($1) " + Unicode.CENTER_DOT + " " + inp2);
                // m >= 1/2
                result = result.replaceAll("m > (.*)",
                        inp1 + " Greater ($1) " + Unicode.CENTER_DOT + " " + inp2);
                // m == 1
                result = result.replaceAll("m == (.*)",
                        inp1 + " = ($1) " + Unicode.CENTER_DOT + " " + inp2);

                // remove spaces at parentheses
                result = result.replaceAll("\\(\\s", "(");
                result = result.replaceAll("\\s\\)", ")");

                // Simplify (1)*... to ...
                result = result.replaceAll("\\(1\\)(\\s)" + Unicode.CENTER_DOT + "\\s", "");            // Use math symbols instead of Mathematica notation
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
                result = result.replace("*", "" + Unicode.CENTER_DOT);

                // Root[1 - #1 - 2*#1^2 + #1^3 & , 2, 0]
                result = result.replaceAll("Root\\[(.*?) \\& , (.*?), 0\\]", "$2. root of $1");
                result = result.replaceAll("[^\\&]#1", "x");

                retval += result;
            }
        }

        debugElapsedTime();
        outputText.setTextString(retval);
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
