/*
This file is part of GeoGebra.
This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.export.pstricks;
import geogebra.common.awt.GColor;
import geogebra.common.euclidian.DrawPoint;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAngleLines;
import geogebra.common.kernel.algos.AlgoAnglePoints;
import geogebra.common.kernel.algos.AlgoAngleVector;
import geogebra.common.kernel.algos.AlgoAngleVectors;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import geogebra.common.kernel.algos.AlgoIntegralFunctions;
import geogebra.common.kernel.algos.AlgoSlope;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.statistics.AlgoBoxPlot;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.main.AppD;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/*
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject; */
/**
 * @author Andy Zhu
 */

public class GeoGebraToAsymptote extends GeoGebraExport {
	
     // Use euro symbol, compact code and cse5 code, respectively; and black-and-white vs color
    private boolean eurosym = false, compact = false, compactcse5 = false, grayscale = false, dotColors = false,
     // refer to pairs by a name           
                    pairName = false;                 
     // Indexes number of parabolas and hyperbolas and arcs and functions
    private int parabolaCount     = 0,   // number of functions used by parabolas
                hyperbolaCount    = 0,   // number of functions used by hyperbolas
                arcCount          = 0,   // number of arcs drawn
                functionCount     = 0,   // number of functions drawn
                implicitFuncCount = 0,   // number of implicit functions drawn
                fillType          = 0,   // FILL_OPACITY, etc
                fontsize;                // font size
     // Code for beginning of picture, for points, for Colors, and for background fill
    private StringBuilder codeBeginPic, codePointDecl, codeColors, codeEndDoc;
     // Contains list of points
    private ArrayList<GeoPoint> pointList;
     // Maps unicode expressions to text equivalents
    private Map<String, String> unicodeTable, pairNameTable;
     // Maps function return expressions to function #
    private Map<String, Integer> functionTable, implicitPolyTable;
     // use the following packages for Asymptote and LaTeX commands
     // importContour = false, importMath = false, importGraph = false,
     // usepackage_amssymb = false, usepackage_amsmath = false, usepackage_mathrsfs = false;
    private Set<String> usepackage, importpackage;
    
    /**
     * @param app
     */
    public GeoGebraToAsymptote(final AppD app) {
        super(app);
    }
    @Override
	protected void createFrame(){
        frame = new AsymptoteFrame(this);
    }
    
    /**
     * generateAllCode: generate Asymptote output by assembling snippets and sanitizing
     */
    @Override
	public void generateAllCode() {
    	
        
        // reset global variables
        parabolaCount     = 0; 
        hyperbolaCount    = 0; 
        arcCount          = 0; 
        functionCount     = 0;
        implicitFuncCount = 0;
        fillType          = 0; 
        /* importContour = false; importMath = false; importGraph = false;
         * usepackage_amssymb  = false; usepackage_amsmath = false; usepackage_mathrsfs = false; */
        usepackage    = new TreeSet<String>();
        importpackage = new TreeSet<String>();
        pointList         = new ArrayList<GeoPoint>();     // list of pairs, for cse5
        unicodeTable      = new HashMap<String, String>(); // map of unicode -> LaTeX commands
        pairNameTable     = new HashMap<String, String>(); // map of coordinates -> point's name 
        functionTable     = new HashMap<String, Integer>(); // function(x) return value to function #
        implicitPolyTable = new HashMap<String, Integer>(); // function(x,y) return value to function #
        CustomColor       = new HashMap<geogebra.common.awt.GColor, String>();  // map of rgb -> alphabet pen names
        
        // retrieve flags from frame
        format      = frame.getFormat();
        compact     = frame.getAsyCompact() || frame.getAsyCompactCse5();
        compactcse5 = frame.getAsyCompactCse5(); 
        fillType    = frame.getFillType();
        fontsize    = frame.getFontSize();
        grayscale   = frame.isGrayscale();
        pairName    = frame.getUsePairNames();  
        dotColors   = frame.getKeepDotColors();
        
        // initialize unit variables, scale ratio = yunit/xunit;
        try {    
            xunit = frame.getXUnit();
            yunit = frame.getYUnit();
        }
        catch(NullPointerException e2) {
            xunit = 1; yunit = 1;
        }
        
        // initialize new StringBuilders for Asymptote code
        // overall output
        code             = new StringBuilder();
        // beginning statements/comments 
        codePreamble     = new StringBuilder();
        // beginning statements/comments 
        codeBeginPic     = new StringBuilder();
        // definition of pairs, for cse5 mode 
        codePointDecl    = new StringBuilder();
        // pens corresponding to certain rgb values
        codeColors       = new StringBuilder();
        // dots and labels
        codePoint        = new StringBuilder();
        // all major geometric constructions
        codeFilledObject = new StringBuilder();
        // axes, grid, and so forth
        codeBeginPic     = new StringBuilder();
        // ending code, odds and ends
        codeEndDoc       = new StringBuilder();
        
        // generate point list 
        if(pairName) {
            for (int step = 0; step < construction.steps(); step++){
                GeoElement[] geos = construction.getConstructionElement(step).getGeoElements();
                for (int j = 0; j < geos.length; j++){
                    GeoElement g = geos[j];
                    if (g.isEuclidianVisible() && g.isGeoPoint()) 
                        pointList.add((GeoPoint) g);
                }
            }
        }
        
        // In cse5, initialize pair definitions.
        initPointDeclarations();
        // Initialize Unicode Table
        initUnicodeTextTable();
        
        // get all objects from construction and "draw" by creating Asymptote code
        // **Run this before generating other code in case it causes other changes
        //  such as which packages should be imported.**
        drawAllElements();
        
        // Write preamble. If compact option unchecked, include liberal documentation.
        if (!compact) {
            codePreamble.append(" /* Geogebra to Asymptote conversion, ");
            // userscripts.org/scripts/show/72997 
            codePreamble.append("documentation at artofproblemsolving.com/Wiki, go to User:Azjps/geogebra */\n");
        }
        importpackage.add("graph");
        for(String s : importpackage) 
            codePreamble.append("import " + s + "; ");
        for(String s : usepackage)
            codePreamble.append("usepackage(\"" + s + "\"); ");
        /* if (usepackage_amssymb) codePreamble.append("usepackage(\"amssymb\"); "); 
         * if (usepackage_amsmath) codePreamble.append("usepackage(\"amsmath\"); ");
         * if (importContour) codePreamble.append("import contour; ");
         * if (importMath) codePreamble.append("import math; "); */
        codePreamble.append("size(" + format(frame.getLatexWidth()) + "cm); ");
        initUnitAndVariable();
        
        // Draw grid
        if (euclidianView.getShowGrid() && frame.getShowAxes())
            drawGrid();
        // Draw axis
        if ((euclidianView.getShowXaxis() || euclidianView.getShowYaxis()) 
                && frame.getShowAxes()) 
            drawAxis();
        
        // Clip frame
        codeEndDoc.append("\nclip((xmin,ymin)--(xmin,ymax)--(xmax,ymax)--(xmax,ymin)--cycle); ");
        // Background color
        if(!euclidianView.getBackgroundCommon().equals(GColor.WHITE)) {
            if(!compact)
                codeEndDoc.append("\n");
            codeEndDoc.append("shipout(bbox(");
            ColorCode(euclidianView.getBackgroundCommon(),codeEndDoc);
            codeEndDoc.append(",Fill)); ");
        }
        // Re-scale
        if(format(yunit).compareTo(format(xunit)) != 0) {
            if(!compact)
                codeEndDoc.append("\n /* re-scale y/x */\n");
            packSpaceBetween(codeEndDoc, "currentpicture", "=", 
                "yscale(" + format(yunit/xunit) + ")", "*", "currentpicture; ");
        }
        if(!compact)
            codeEndDoc.append("\n /* end of picture */");     
        
        // add code for Points and Labels
        code.append("\n");
        if(!compact)
            code.append(" /* dots and labels */");
        code.append(codePoint);

/*      String formatFont=resizeFont(app.getFontSize());
        if (null!=formatFont){
            codeBeginPic.insert(0,formatFont+"\n");
            code.append("}\n");
        }
*/      // Order: TODO
        // Preamble, Colors, Points, Fills, Pic, Objects, regular code, EndDoc
        if(!compact)
            code.insert(0, " /* draw figures */");
        code.insert(0, "\n");
        code.insert(0, codeBeginPic);
        code.insert(0, codeFilledObject);
        if(codeFilledObject.length() != 0)
            code.insert(0, "\n");
        code.insert(0, codePointDecl);
        if(!compact)
            code.insert(0, codeColors);
        else if(codeColors.length() != 0) // remove first comma of pen
            code.insert(0, "\npen" + codeColors.substring(1) + "; ");
        code.insert(0, codePreamble);
        code.append(codeEndDoc); // clip frame, background fill, re-scaling
        
        // code to temporarily remove pi from code, other unicode issues
        convertUnicodeToText(code);
        
        
        frame.write(code);
    }   
    
    @Override
	protected void drawLocus(GeoLocus geo){
        ArrayList<MyPoint> ll = geo.getPoints();
        Iterator<MyPoint> it = ll.iterator();
        boolean first = true, first2 = true;  // whether to write join operators afterwards
        
        if(!compact)
            code.append(" /* locus construction */\n");
        startDraw();
        while(it.hasNext()){
            MyPoint mp = it.next();
            if (mp.x > xmin && mp.x < xmax && mp.y > ymin && mp.y < ymax){
                String x = format(mp.x), 
                       y = format(mp.y);
                if (first && first2) {
                    code.append("(");
                    first = false; first2 = false;
                }
                else if (first) { // don't draw connecting line
                    code.append("^^(");
                    first = false;
                }
                else if (mp.lineTo)
                    code.append("--(");
                else
                    code.append("^^(");
                code.append(x + "," + y + ")");
            }
            else first = true;
        }
        endDraw(geo);
    }

    @Override
	protected void drawBoxPlot(GeoNumeric geo){
        AlgoBoxPlot algo = ((AlgoBoxPlot) geo.getParentAlgorithm());
        double y = algo.getA().getDouble();
        double height = algo.getB().getDouble();
        double[] lf = algo.getLeftBorders();
        double min = lf[0];
        double q1  = lf[1];
        double med = lf[2];
        double q3  = lf[3];
        double max = lf[4];

        // Min vertical bar
        drawLine(min,y-height,min,y+height,geo);
        // Max vertical bar
        drawLine(max,y-height,max,y+height,geo);
        // Med vertical bar
        drawLine(med,y-height,med,y+height,geo);
        // Min-q1 horizontal
        drawLine(min,y,q1,y,geo);
        // q3-max
        drawLine(q3,y,max,y,geo);
        
        // Rectangle q1-q3
        startTransparentFill(codeFilledObject);
        codeFilledObject.append("box(");
        addPoint(format(q1),format(y-height),codeFilledObject);
        codeFilledObject.append(",");
        addPoint(format(q3),format(y+height),codeFilledObject);
        codeFilledObject.append(")");
        endTransparentFill(geo, codeFilledObject);
    }

    @Override
	protected void drawHistogram(GeoNumeric geo){
        AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums)geo.getParentAlgorithm();
        double[] y = algo.getValues();
        double[] x = algo.getLeftBorder();

        for (int i=0; i<x.length-1; i++){
            startTransparentFill(codeFilledObject);
            codeFilledObject.append("box((");
            codeFilledObject.append(format(x[i]));
            codeFilledObject.append(",0),(");
            codeFilledObject.append(format(x[i+1]));
            codeFilledObject.append(",");
            codeFilledObject.append(format(y[i]));
            codeFilledObject.append("))");
            endTransparentFill(geo,codeFilledObject);
        }       
    }
    
    @Override
	protected void drawSumTrapezoidal(GeoNumeric geo){
        AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums) geo.getParentAlgorithm();
        int n = algo.getIntervals();
        double[] y = algo.getValues();
        double[] x = algo.getLeftBorder();
        for (int i = 0; i < n; i++){
            startTransparentFill(codeFilledObject);
            codeFilledObject.append("(");
            codeFilledObject.append(format(x[i]));
            codeFilledObject.append(",0)--(");
            codeFilledObject.append(format(x[i+1]));
            codeFilledObject.append(",0)--(");
            codeFilledObject.append(format(x[i+1]));
            codeFilledObject.append(",");
            codeFilledObject.append(format(y[i+1]));
            codeFilledObject.append(")--(");
            codeFilledObject.append(format(x[i]));
            codeFilledObject.append(",");
            codeFilledObject.append(format(y[i]));
            codeFilledObject.append(")--cycle");
            endTransparentFill(geo,codeFilledObject);
        }       
    }
    
    @Override
	protected void drawSumUpperLower(GeoNumeric geo){
        AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums)geo.getParentAlgorithm();
        int n = algo.getIntervals();
        double step = algo.getStep();
        double[] y = algo.getValues();
        double[] x = algo.getLeftBorder();

        for (int i=0; i<n; i++){
            startTransparentFill(codeFilledObject);
            codeFilledObject.append("box((");
            codeFilledObject.append(format(x[i]));
            codeFilledObject.append(",0),(");
            codeFilledObject.append(format(x[i]+step));
            codeFilledObject.append(",");
            codeFilledObject.append(format(y[i]));
            codeFilledObject.append("))");
            endTransparentFill(geo,codeFilledObject);
        }
    }
    
    @Override
	protected void drawIntegralFunctions(GeoNumeric geo){
        importpackage.add("graph");
        
        AlgoIntegralFunctions algo = (AlgoIntegralFunctions) geo.getParentAlgorithm();      
        GeoFunction f = algo.getF(), // function f
                    g = algo.getG(); // function g
        // double a and b
        double a = algo.getA().getDouble(),
               b = algo.getB().getDouble();
        // String output for a and b
        String sa = format(a),
               sb = format(b);
        // String Expression of f and g
        String valueF = f.toValueString(getStringTemplate()), valueG = g.toValueString(getStringTemplate());
        valueF = parseFunction(valueF);
        valueG = parseFunction(valueG);
        // String expressions for f(a) and g(b) 
        // String fa = format(f.evaluate(a));
        // String gb = format(g.evaluate(b));

        if(!compact)
            codeFilledObject.append("\n");
        
        // write functions for f and g if they do not already exist.
        int indexFunc = -1;
        String tempFunctionCountF = "f"+Integer.toString(functionCount+1);
        String returnCode = "(real x){return " + valueF + ";} ";
        // search for previous occurrences of function
        // TODO Hashtable rewrite?
        if(compact) {
            indexFunc = codeFilledObject.indexOf(returnCode);
            if(indexFunc != -1) {
                // retrieve name of previously used function
                int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
                tempFunctionCountF = codeFilledObject.substring(indexFuncStart+1,indexFunc);
            }
        } 
        // write function
        if(indexFunc == -1){ 
            functionCount++;
            packSpaceBetween(codeFilledObject, "real f" + functionCount, "(real x)", "{", "return " + valueF + ";", "} ");
        }

        indexFunc = -1;
        String tempFunctionCountG = "f"+Integer.toString(functionCount+1);
        returnCode = "(real x){return " + valueG + ";} ";
        // search for previous occurrences of function
        if(compact) {
            indexFunc = codeFilledObject.indexOf(returnCode);
            if(indexFunc != -1) {
                // retrieve name of previously used function
                int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
                tempFunctionCountG = codeFilledObject.substring(indexFuncStart+1,indexFunc);
            }
        } // write function
        if(indexFunc == -1){ 
            functionCount++;
            packSpaceBetween(codeFilledObject, "real f" + functionCount, "(real x)", "{", "return " + valueG + ";", "} ");
        }
        
        // draw graphs of f and g
        startTransparentFill(codeFilledObject);
        packSpaceBetween(codeFilledObject, "graph(" + tempFunctionCountF + ",", sa + ",", sb + ")", "--",
                                           "graph(" + tempFunctionCountG + ",", sb + ",", sa + ")", "--cycle");
        endTransparentFill(geo, codeFilledObject);
    }

    @Override
	protected void drawIntegral(GeoNumeric geo){
        importpackage.add("graph");
        
        AlgoIntegralDefinite algo = (AlgoIntegralDefinite) geo.getParentAlgorithm();
        GeoFunction f = algo.getFunction(); // function f between a and b
        String a = format(algo.getA().getDouble());
        String b = format(algo.getB().getDouble());    
        String value = f.toValueString(getStringTemplate());
        value = parseFunction(value);
        
        int indexFunc = -1;
        String tempFunctionCount = "f"+Integer.toString(functionCount+1);
        String returnCode = "(real x){return (" + value + ");} ";
        // search for previous occurrences of function
        if(compact) {
            indexFunc = codeFilledObject.indexOf(returnCode);
            if(indexFunc != -1) {
                // retrieve name of previously used function
                int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
                tempFunctionCount = codeFilledObject.substring(indexFuncStart+1,indexFunc);
            }
        } // write function
        if(indexFunc == -1){ 
            functionCount++;
            if(!compact)
                codeFilledObject.append("\n");
            codeFilledObject.append("real f");
            codeFilledObject.append(functionCount);
            packSpace(codeFilledObject, "(real x)");
            codeFilledObject.append("{return ");
            codeFilledObject.append(value);
            codeFilledObject.append(";} ");
        }
        
        startTransparentFill(codeFilledObject);
        codeFilledObject.append("graph(");
        codeFilledObject.append(tempFunctionCount);
        codeFilledObject.append(",");
        codeFilledObject.append(a);
        codeFilledObject.append(",");
        codeFilledObject.append(b);
        codeFilledObject.append(")--");
        addPoint(b,"0",codeFilledObject);
        codeFilledObject.append("--");
        addPoint(a,"0",codeFilledObject);
        codeFilledObject.append("--cycle");
        endTransparentFill(geo,codeFilledObject);
    }

    @Override
	protected void drawSlope(GeoNumeric geo){ // TODO: label bug? 
        int slopeTriangleSize = geo.getSlopeTriangleSize();
        double rwHeight = geo.getValue() * slopeTriangleSize;
        double height = euclidianView.getYscale() * rwHeight;
        double[] coords = new double[2];
        if (Math.abs(height) > Float.MAX_VALUE) {
            return;
        }
        // get point on line g
        GeoLine g = ((AlgoSlope)geo.getParentAlgorithm()).getg();
        g.getInhomPointOnLine(coords);
        // draw slope triangle       
        float x = (float) coords[0];
        float y = (float) coords[1];
        float xright=x+slopeTriangleSize;

        startTransparentFill(codeFilledObject);
        addPoint(format(x),format(y),codeFilledObject);
        codeFilledObject.append("--");
        addPoint(format(xright),format(y),codeFilledObject);
        codeFilledObject.append("--");
        addPoint(format(xright),format(y+rwHeight),codeFilledObject);
        codeFilledObject.append("--cycle");
        endTransparentFill(geo,codeFilledObject);
        
        // draw Label 
        float xLabelHor = (x + xright) /2;
        float yLabelHor = y - (float)(
                (euclidianView.getFont().getSize() + 2)/euclidianView.getYscale());
        geogebra.common.awt.GColor geocolor =  geo.getObjectColor();

        if(!compact)
            codePoint.append("\n");
        packSpaceAfter(codePoint, "label(\"$" + slopeTriangleSize + "$\",", 
                "(" + format(xLabelHor) + ",", format(yLabelHor) + "),", "NE", "*");
        if(compact)
            codePoint.append("lsf");
        else
            codePoint.append("labelscalefactor");
        if (!geocolor.equals(GColor.BLACK)){
            codePoint.append(",");
            ColorCode(geocolor,codePoint);
        }
        codePoint.append("); "); 
    }

    @Override
	protected void drawAngle(GeoAngle geo){
        int arcSize = geo.getArcSize();
        AlgoElement algo = geo.getParentAlgorithm();
        GeoPointND vertex, point;
        GeoVector v;
        GeoLine line, line2;
        GeoPoint tempPoint = new GeoPoint(construction);        
        tempPoint.setCoords(0.0, 0.0, 1.0);
        double[] firstVec = new double[2];
        double[] m = new double[2];
        // angle defines with three points
        if (algo instanceof AlgoAnglePoints) {
            AlgoAnglePoints pa = (AlgoAnglePoints) algo;
            vertex = pa.getB();
            point = pa.getA();
            vertex.getInhomCoords(m);
            // first vec
            Coords coords = point.getInhomCoordsInD(3);
            firstVec[0] = coords.getX() - m[0];
            firstVec[1] = coords.getY() - m[1];
        } 
        // angle between two vectors
        else if (algo instanceof AlgoAngleVectors) {
            AlgoAngleVectors va = (AlgoAngleVectors) algo;
            v = va.getv();
            // vertex
            vertex = v.getStartPoint();             
            if (vertex == null) vertex = tempPoint;
            vertex.getInhomCoords(m);
            // first vec
            v.getInhomCoords(firstVec);             
        } 
        // angle between two lines
        else if (algo instanceof AlgoAngleLines) {
            AlgoAngleLines la = (AlgoAngleLines) algo;
            line = la.getg();
            line2 = la.geth();  
            vertex = tempPoint;
            // intersect lines to get vertex
            m = GeoVec3D.cross(line, line2).get();
            // first vec
            line.getDirection(firstVec);
        }
        // angle of a single vector or a single point
        else if (algo instanceof AlgoAngleVector) {         
            AlgoAngleVector va = (AlgoAngleVector) algo;
            GeoVec3D vec = va.getVec3D();   
            if (vec instanceof GeoVector) {
                v = (GeoVector) vec;
                // vertex
                vertex = v.getStartPoint();             
                if (vertex == null) vertex = tempPoint;
                vertex.getInhomCoords(m);
            } else if (vec instanceof GeoPoint) {
                point = (GeoPoint) vec;             
                vertex = tempPoint;
                // vertex
                vertex.getInhomCoords(m);
            }           
            firstVec[0] = 1;
            firstVec[1] = 0;

        }
        tempPoint.remove(); // Michael Borcherds 2008-08-20
        
        double angSt = Math.atan2(firstVec[1], firstVec[0]);

        // Michael Borcherds 2007-10-21 BEGIN
        // double angExt = geo.getValue();
        double angExt = geo.getRawAngle();
        if (angExt > Math.PI*2) angExt -= Math.PI*2;
        
        if (geo.getAngleStyle() == GeoAngle.ANGLE_ISCLOCKWISE) {
            angSt += angExt;
            angExt = 2.0*Math.PI-angExt;
        }
        
        if (geo.getAngleStyle() == GeoAngle.ANGLE_ISNOTREFLEX) {
            if (angExt > Math.PI) {
                angSt += angExt;
                angExt = 2.0*Math.PI-angExt;
            }
        }
        
        if (geo.getAngleStyle() == GeoAngle.ANGLE_ISREFLEX) {
            if (angExt < Math.PI) {
                angSt += angExt;
                angExt = 2.0*Math.PI-angExt;
            }
        }
        // if (geo.changedReflexAngle()) {          
        //      angSt = angSt - angExt;
        // }
        // Michael Borcherds 2007-10-21 END

        angExt += angSt;
        double r = arcSize /euclidianView.getXscale();
        
        // StringBuilder tempsb = new StringBuilder();
        startTransparentFill(codeFilledObject);
        // if right angle and decoration is a little square 
        if (Kernel.isEqual(geo.getValue(), Kernel.PI_HALF) && geo.isEmphasizeRightAngle()
                && euclidianView.getRightAngleStyle() == EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE){
            r = r/Math.sqrt(2);
            double[] x = new double[8];
            x[0] = m[0] + r*Math.cos(angSt);
            x[1] = m[1] + r*Math.sin(angSt);
            x[2] = m[0] + r*Math.sqrt(2)*Math.cos(angSt+Kernel.PI_HALF/2);
            x[3] = m[1] + r*Math.sqrt(2)*Math.sin(angSt+Kernel.PI_HALF/2);
            x[4] = m[0] + r*Math.cos(angSt+Kernel.PI_HALF);
            x[5] = m[1] + r*Math.sin(angSt+Kernel.PI_HALF);
            x[6] = m[0];
            x[7] = m[1];
            
            for (int i = 0; i < 4; i++){
                addPoint(format(x[2*i]),format(x[2*i+1]),codeFilledObject);
                codeFilledObject.append("--");
            }
            codeFilledObject.append("cycle");
            
            // transparent fill options
            endTransparentFill(geo, codeFilledObject);
        }
        else {  // draw arc for the angle. 
            codeFilledObject.append("arc(");
            addPoint(format(m[0]),format(m[1]),codeFilledObject);
            codeFilledObject.append(",");
            codeFilledObject.append(format(r));
            codeFilledObject.append(",");
            codeFilledObject.append(format(Math.toDegrees(angSt)));
            codeFilledObject.append(",");
            codeFilledObject.append(format(Math.toDegrees(angExt)));
            codeFilledObject.append(")--(");
            codeFilledObject.append(format(m[0]));
            codeFilledObject.append(",");
            codeFilledObject.append(format(m[1]));
            codeFilledObject.append(")--cycle");
            // transparent fill options
            endTransparentFill(geo,codeFilledObject);
            
            // draw the [circular?] dot if right angle and decoration is dot
            if (Kernel.isEqual(geo.getValue(), Kernel.PI_HALF) && geo.isEmphasizeRightAngle() 
                    && euclidianView.getRightAngleStyle() == EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT){
                double diameter = geo.lineThickness/euclidianView.getXscale();
                double radius = arcSize/euclidianView.getXscale()/1.7;
                double labelAngle = (angSt+angExt) / 2.0;
                double x1 = m[0] + radius * Math.cos(labelAngle); 
                double x2 = m[1] + radius * Math.sin(labelAngle);
                
                startDraw();
                if(compactcse5)
                    code.append("CR(");
                else
                    code.append("circle(");
                addPoint(format(x1),format(x2),code);
                code.append(",");
                code.append(format(diameter));
                code.append(")");
                endDraw(geo);
            }
            if (geo.decorationType != GeoElement.DECORATION_NONE){ 
                markAngle(geo,r,m,angSt,angExt);
            }
        }
    }
    
    @Override
	protected void drawArrowArc(GeoAngle geo,double[] vertex,double angSt,double angEnd,double r,boolean anticlockwise){
        // The arrow head goes away from the line.
        // Arrow Winset=0.25, see PStricks spec for arrows
        double arrowHeight = (geo.lineThickness*0.8+3)*1.4*3/4;
        double angle = Math.asin(arrowHeight/2/euclidianView.getXscale()/ r);
        angEnd = angEnd-angle;
    
        startDraw();
        code.append("arc(");
        addPoint(format(vertex[0]), format(vertex[1]),code);
        code.append(",");
        code.append(format(r));
        code.append(",");
        code.append(format(Math.toDegrees(angSt)));
        code.append(",");
        code.append(format(Math.toDegrees(angEnd)));
        code.append(")");
        if(LineOptionCode(geo,true) != null) {
            packSpaceAfter(code, ",");
            code.append(LineOptionCode(geo, true));
        } // TODO: resize?
        if (anticlockwise)  code.append(",EndArcArrow(6)");
        else                code.append(",BeginArcArrow(6)");
        code.append("); ");
    }
    
    // angSt, angEnd in degrees. r = radius.
    @Override
	protected void drawArc(GeoAngle geo,double[] vertex, double angSt, double angEnd, double r){
        startDraw();
        code.append("arc(");
        addPoint(format(vertex[0]),format(vertex[1]),code);
        code.append(",");
        code.append(format(r));
        code.append(",");
        code.append(format(Math.toDegrees(angSt)));
        code.append(",");
        code.append(format(Math.toDegrees(angEnd)));
        code.append(")");
        endDraw(geo);
    }
    
    @Override
	protected void drawTick(GeoAngle geo,double[] vertex,double angle){
        angle = -angle;
        double radius = geo.getArcSize();
        double diff = 2.5 + geo.lineThickness / 4d;
        double x1=euclidianView.toRealWorldCoordX(vertex[0]+(radius-diff)*Math.cos(angle));
        double x2=euclidianView.toRealWorldCoordX(vertex[0]+(radius+diff)*Math.cos(angle));
        double y1=euclidianView.toRealWorldCoordY(vertex[1]+(radius-diff)*Math.sin(angle)*euclidianView.getScaleRatio());
        double y2=euclidianView.toRealWorldCoordY(vertex[1]+(radius+diff)*Math.sin(angle)*euclidianView.getScaleRatio());

        startDraw();
        addPoint(format(x1),format(y1),code);
        code.append("--");
        addPoint(format(x2),format(y2),code);
        endDraw(geo);
    }
    
    @Override
	protected void drawSlider(GeoNumeric geo){
        boolean horizontal = geo.isSliderHorizontal();
        double max = geo.getIntervalMax();
        double min = geo.getIntervalMin();
        double value = geo.getValue();
        double width = geo.getSliderWidth();
        double x = geo.getSliderX();
        double y = geo.getSliderY();
        
        // start point of horizontal line for slider
        if (geo.isAbsoluteScreenLocActive()) {
            x = euclidianView.toRealWorldCoordX(x);
            y = euclidianView.toRealWorldCoordY(y);
            width = horizontal ? width / euclidianView.getXscale() :
                        width / euclidianView.getYscale();
        }
        // create point for slider
        GeoPoint geoPoint = new GeoPoint(construction);
        geoPoint.setObjColor(geo.getObjectColor());
        String label=StringUtil.toLaTeXString(geo.getLabelDescription(),true);
        geoPoint.setLabel(label);
        double param =  (value - min) / (max - min);
        geoPoint.setPointSize(2 + (geo.lineThickness+1) / 3);  
        geoPoint.setLabelVisible(geo.isLabelVisible());
        if (horizontal) geoPoint.setCoords(x+width*param, y, 1.0);
        else geoPoint.setCoords(x, y+width* param, 1.0);
        DrawPoint drawPoint = new DrawPoint(euclidianView, geoPoint);
        drawPoint.setGeoElement(geo);
        if (geo.isLabelVisible()) {
            if (horizontal){
                drawPoint.xLabel -= 15;
                drawPoint.yLabel -= 5;
            }
            else {
                drawPoint.xLabel += 5;
                drawPoint.yLabel += 2*geoPoint.getPointSize() + 4;   
            }
        }
        drawGeoPoint(geoPoint);
        drawLabel(geoPoint,drawPoint);
        
        geoPoint.remove(); // Michael Borcherds 2008-08-20

        //draw Line for Slider
        startDraw();
        addPoint(format(x),format(y),code);
        code.append("--");
        if (horizontal) x += width;
        else            y += width;
        addPoint(format(x),format(y),code);
        endDraw(geo);
    }
    
    @Override
	protected void drawPolygon(GeoPolygon geo){
        GeoPointND[] points = geo.getPoints();
        // StringBuilder tempsb = new StringBuilder();
        
        startTransparentFill(codeFilledObject);
        for (int i = 0; i < points.length; i++){
        	Coords coords = points[i].getCoordsInD(2);
            double x = coords.getX(),
                   y = coords.getY(),
                   z = coords.getZ();
            x = x / z; y = y / z;
            addPoint(format(x),format(y),codeFilledObject);
            codeFilledObject.append("--");
        }
        codeFilledObject.append("cycle");
        endTransparentFill(geo,codeFilledObject);
    }
    
    @Override
	protected void drawText(GeoText geo){
        boolean isLatex = geo.isLaTeX();
        String st = geo.getTextString();
        if(isLatex)
            st = StringUtil.toLaTeXString(st, true);
        // try to replace euro symbol
        if (st.indexOf("\u20ac") != -1) {
            st = st.replaceAll("\\u20ac", "\\\\euro{}");
            if (!eurosym) codePreamble.append("usepackage(\"eurosym\"); ");
        }
        geogebra.common.awt.GColor geocolor = geo.getObjectColor();
        int style = geo.getFontStyle();
        int size = (int) (geo.getFontSizeMultiplier() * app.getFontSize());
        GeoPoint gp;
        double x,y;
          // compute location of text       
        if (geo.isAbsoluteScreenLocActive()) {
            x = geo.getAbsoluteScreenLocX();
            y = geo.getAbsoluteScreenLocY(); 
        } 
        else {
            gp = (GeoPoint) geo.getStartPoint();
            if (gp == null) {
                x = (int) euclidianView.getXZero();
                y = (int) euclidianView.getYZero();
            } 
            else {
                if (!gp.isDefined()) {
                    return;
                }
                x = euclidianView.toScreenCoordX(gp.inhomX);
                y = euclidianView.toScreenCoordY(gp.inhomY);            
            }
            x += geo.labelOffsetX;
            y += geo.labelOffsetY; 
        }
        x = euclidianView.toRealWorldCoordX(x);
        y = euclidianView.toRealWorldCoordY(y-euclidianView.getFont().getSize());
        int id = st.indexOf("\n");
        boolean comma = false;

        // One line
        if (id == -1){
            if(!compact)
                code.append("\n");
            code.append("label(\"");
            addText(st,isLatex,style);
            code.append("\",");
            addPoint(format(x),format(y),code);
            code.append(",SE*");
            if(compact)
                code.append("lsf");
            else
                code.append("labelscalefactor");
            if(!geocolor.equals(GColor.BLACK)) { // color
                code.append(","); comma = true;
                ColorCode(geocolor,code);
            }
            if(size != app.getFontSize()) { // fontsize
                if(!comma) code.append(",");
                else packSpace(code, "+");
                code.append("fontsize(");
                code.append(fontsize+(size-app.getFontSize()));
                code.append(")");
            }
            else if(compactcse5) {  // use default font pen for cse5
                if(!comma) code.append(",");
                else packSpace(code, "+");
                code.append("fp");
            }
            code.append("); ");
        }
        // MultiLine
        else {
            StringBuilder sb = new StringBuilder();
            StringTokenizer stk = new StringTokenizer(st,"\n");
            int width = 0;
            Font font = new Font(geo.isSerifFont() ? "Serif" : "SansSerif", style, size);
            FontMetrics fm = euclidianView.getFontMetrics(font);
            while (stk.hasMoreTokens()){
                String line = stk.nextToken();
                width = Math.max(width,fm.stringWidth(line));       
                sb.append(line);
                if (stk.hasMoreTokens()) sb.append(" \\\\ ");
            }
            
            if(!compact)
                code.append("\n");
            code.append("label(\"$");
            code.append("\\parbox{");
            code.append(format(width*(xmax-xmin)*xunit/euclidianView.getWidth()+1));
            code.append(" cm}{");
            addText(new String(sb),isLatex,style);
            code.append("}$\",");
            addPoint(format(x),format(y),code);
            code.append(",SE*");
            if(compact)
                code.append("lsf");
            else
                code.append("labelscalefactor");
            if(!geocolor.equals(GColor.BLACK)) { // color
                code.append(","); comma = true;
                ColorCode(geocolor,code);
            }
            if(size != app.getFontSize()) { // fontsize
                if(!comma) code.append(",");
                else packSpace(code, "+");
                code.append("fontsize(");
                code.append(fontsize+(size-app.getFontSize()));
                code.append(")");
            }
            else if(compactcse5) {  // use default font pen for cse5
                if(!comma) code.append(",");
                else packSpace(code, "+");
                code.append("fp");
            }
            code.append("); ");
        }
    }
    
    @Override
	protected void drawGeoConicPart(GeoConicPart geo){
        StringBuilder tempsb = new StringBuilder();
        double r1 = geo.getHalfAxes()[0],
               r2 = geo.getHalfAxes()[1];
        double startAngle = geo.getParameterStart();
        double endAngle = geo.getParameterEnd();
        // Get all coefficients form the transform matrix
        AffineTransform af = geogebra.awt.GAffineTransformD.getAwtAffineTransform(geo.getAffineTransform());
        double m11 = af.getScaleX();
        double m22 = af.getScaleY();
        double m12 = af.getShearX();
        double m21 = af.getShearY();
        double tx = af.getTranslateX();
        double ty = af.getTranslateY();
        
        if (startAngle > endAngle){
            startAngle -= Math.PI*2;
        }
        // Fill if: SECTOR and fill type not set to FILL_NONE
        if(m11 == 1 && m22 == 1 && m12 == 0 && m21 == 0) {
            if (geo.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR 
                    && fillType != ExportFrame.FILL_NONE)               
                startTransparentFill(tempsb);
            else
                startDraw(tempsb);
            tempsb.append("shift(");
            addPoint(format(tx),format(ty),tempsb);
            tempsb.append(")*xscale(");
            tempsb.append(format(r1));
            tempsb.append(")*yscale(");
            tempsb.append(format(r2));
            tempsb.append(")*arc((0,0),1,");
            tempsb.append(format(Math.toDegrees(startAngle)));
            tempsb.append(",");
            tempsb.append(format(Math.toDegrees(endAngle)));
            tempsb.append(")");
        }
        else {
            StringBuilder sb1=new StringBuilder(),sb2=new StringBuilder();
            sb1.append(format(r1));
            sb1.append("*cos(t)");
            sb2.append(format(r2));
            sb2.append("*sin(t)");
            
            arcCount++;
            if(!compact)
                tempsb.append("\n");
            tempsb.append("pair arc");
            tempsb.append(arcCount);
            packSpace(tempsb,"(real t)");
            tempsb.append("{return (");
            tempsb.append(format(m11));
            tempsb.append("*");
            tempsb.append(sb1);
            tempsb.append("+");
            tempsb.append(format(m12));
            tempsb.append("*");
            tempsb.append(sb2);
            tempsb.append("+");
            tempsb.append(format(tx));           
            tempsb.append(",");
            tempsb.append(format(m21));
            tempsb.append("*");
            tempsb.append(sb1);
            tempsb.append("+");
            tempsb.append(format(m22));
            tempsb.append("*");
            tempsb.append(sb2);
            tempsb.append("+");
            tempsb.append(format(ty));
            tempsb.append(");} ");
            
            if (geo.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR 
                    && fillType != ExportFrame.FILL_NONE)               
                startTransparentFill(tempsb);
            else
                startDraw(tempsb);
            tempsb.append("graph(arc");
            tempsb.append(arcCount);
            tempsb.append(",");
            tempsb.append(format(startAngle));
            tempsb.append(",");
            tempsb.append(format(endAngle));
            tempsb.append(")");
        }
        if (geo.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR){              
            tempsb.append("--");
            addPoint(format(tx),format(ty),tempsb);
            tempsb.append("--cycle");
            if(fillType == ExportFrame.FILL_NONE)
                endDraw(geo,tempsb);
            else
                endTransparentFill(geo,tempsb);
        }
        else
            endDraw(geo,tempsb);
    
        if (geo.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR 
                && fillType != ExportFrame.FILL_NONE)               
            codeFilledObject.append(tempsb);
        else
            code.append(tempsb);
    }
    
    @Override
	protected void drawCurveCartesian (GeoCurveCartesian geo){
        importpackage.add("graph");
        
        double start = geo.getMinParameter(),
                 end = geo.getMaxParameter();
//      boolean isClosed=geo.isClosedPath();
        String fx = parseFunction(geo.getFunX(getStringTemplate()));
        String fy = parseFunction(geo.getFunY(getStringTemplate()));
        String variable = parseFunction(geo.getVarString(getStringTemplate()));
        // boolean warning=!(variable.equals("t"));
        
        int indexFunc = -1;
        String tempFunctionCount = "f"+Integer.toString(functionCount+1);
        String returnCode = "(real "+variable+"){return (" + fx + "," + fy + ");} ";
        // search for previous occurrences of function
        if(compact) {
            indexFunc = codeFilledObject.indexOf(returnCode);
            if(indexFunc != -1) {
                // retrieve name of previously used function
                int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
                tempFunctionCount = codeFilledObject.substring(indexFuncStart+1,indexFunc);
            }
            else if(code.indexOf(returnCode) != -1) {
                indexFunc = code.indexOf(returnCode);
                int indexFuncStart = code.lastIndexOf(" ",indexFunc);
                tempFunctionCount = code.substring(indexFuncStart+1,indexFunc); 
                indexFunc = code.indexOf(returnCode);
            }
        } // write function
        if(indexFunc == -1){ 
            functionCount++;
            if(!compact)
                code.append("\n");
            code.append("pair f");
            code.append(functionCount);
            packSpace(code,"(real " + variable + ")");
            code.append("{return (");
            code.append(fx);
            code.append(",");
            code.append(fy);
            code.append(");} ");    
        }
        
        startDraw();
        code.append("graph(");
        code.append(tempFunctionCount);
        code.append(",");
        code.append(format(start));
        code.append(",");
        code.append(format(end));
        code.append(")");   
        endDraw(geo);
    }
    
    @Override
	protected void drawFunction(GeoFunction geo){
        importpackage.add("graph");
        
        Function f = geo.getFunction();
        if (f == null) return;
        String value = f.toValueString(getStringTemplate());
        value = parseFunction(value);
        value = value.replaceAll("\\\\pi", "pi");
        double a = xmin;
        double b = xmax;
        if (geo.hasInterval()) {
            a = Math.max(a,geo.getIntervalMin());
            b = Math.min(b,geo.getIntervalMax());
        }
        double xrangemax = a, xrangemin = a;
        while (xrangemax < b){
            xrangemin = firstDefinedValue(geo,a,b);
//          Application.debug("xrangemin "+xrangemin);
            if (xrangemin == b) break;
            xrangemax = maxDefinedValue(geo,xrangemin,b);
//          Application.debug("xrangemax "+xrangemax);

            int indexFunc = -1;
            String tempFunctionCount = "f"+Integer.toString(functionCount+1);
            String returnCode = "(real x){return " + value + ";} ";
            // search for previous occurrences of function
            if(compact) {
                indexFunc = codeFilledObject.indexOf(returnCode);
                if(indexFunc != -1) {
                    // retrieve name of previously used function
                    int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
                    tempFunctionCount = codeFilledObject.substring(indexFuncStart+1,indexFunc);
                }
                else if(code.indexOf(returnCode) != -1) {
                    indexFunc = code.indexOf(returnCode);
                    int indexFuncStart = code.lastIndexOf(" ",indexFunc);
                    tempFunctionCount = code.substring(indexFuncStart+1,indexFunc); 
                    indexFunc = code.indexOf(returnCode);
                }
            } // write function
            if(indexFunc == -1){ 
                functionCount++;
                if(!compact)
                    code.append("\n");
                code.append("real ");
                code.append(tempFunctionCount);
                packSpace(code,"(real x)");
                code.append("{return ");
                code.append(value);
                code.append(";} ");     
            }
            
            startDraw();
            code.append("graph(");
            code.append(tempFunctionCount);
            code.append(",");
            // add/subtract 0.01 to prevent 1/x, log(x) undefined behavior
            code.append(format(xrangemin+0.01));
            code.append(",");
            code.append(format(xrangemax-0.01));
            code.append(")");
            //? recycled code of sorts? 
            xrangemax += PRECISION_XRANGE_FUNCTION;
            a = xrangemax; 
            endDraw(geo);
        }
    }
  
    private static void renameFunc(StringBuilder sb, String nameFunc, String nameNew){
        int ind = sb.indexOf(nameFunc);
        while(ind > -1){
            sb.replace(ind, ind + nameFunc.length(), nameNew);
            ind = sb.indexOf(nameFunc);
        }
    }
    
    private double maxDefinedValue(GeoFunction f, double a, double b){
        double x = a;
        double step = (b-a)/100;
        while(x <= b){
            double y = f.evaluate(x);
            if (Double.isNaN(y)){
                if (step < PRECISION_XRANGE_FUNCTION) return x-step;
				return maxDefinedValue(f, x - step, x);
            }
            x += step;
        }
        return b;
    }
    
    private double firstDefinedValue(GeoFunction f, double a, double b){
        double x = a;
        double step = (b-a)/100;
        while(x <= b){
            double y = f.evaluate(x);
            if (!Double.isNaN(y)){
                if (x == a) return a;
                else if (step < PRECISION_XRANGE_FUNCTION) return x;
                else return firstDefinedValue(f, x - step, x);
            }
            x += step;
        }
        return b;
    }
    // draw vector with EndArrow(6)
    @Override
	protected void drawGeoVector(GeoVector geo){
        GeoPoint pointStart = geo.getStartPoint();
        String x1, y1;
        if (pointStart == null){
            x1 = "0"; y1 = "0";
        }
        else {
            x1 = format(pointStart.getX()/pointStart.getZ());
            y1 = format(pointStart.getY()/pointStart.getZ());
        }
        double[] coord = new double[3];
        geo.getCoords(coord);
        String x2 = format(coord[0]+Double.parseDouble(x1));
        String y2 = format(coord[1]+Double.parseDouble(y1));
        
        if(!compact)
            code.append("\n");
        if(compactcse5)
            code.append("D(");
        else
            code.append("draw(");
        addPoint(x1,y1,code);
        code.append("--");
        addPoint(x2,y2,code);
        if(LineOptionCode(geo,true) != null) {
            code.append(",");
            if(!compact)
                code.append(" ");
            code.append(LineOptionCode(geo,true));
        }
        code.append(",EndArrow(6)); ");
    }
    
    private void drawCircle(GeoConic geo){
        StringBuilder tempsb = new StringBuilder();
        boolean nofill = geo.getAlphaValue() < 0.05;
        
        if (xunit == yunit){
            // draw a circle
            double x = geo.getTranslationVector().getX();
            double y = geo.getTranslationVector().getY();
            double r = geo.getHalfAxes()[0];
            String tmpr = format(r); // removed *xunit, unsure of function
            
            if(nofill) {
                if(!compact)
                    tempsb.append("\n");
                if(compactcse5)
                    tempsb.append("D(CR(");
                else
                    tempsb.append("draw(circle(");
            }
            else {
                startTransparentFill(tempsb);
                if(compactcse5)
                    tempsb.append("CR(");
                else
                    tempsb.append("circle(");
            }
            addPoint(format(x),format(y),tempsb);
            packSpaceAfter(tempsb, ",");
            if (Double.parseDouble(tmpr)!=0) 
                tempsb.append(tmpr);
            else 
                tempsb.append(r);
            tempsb.append(")");
            if(nofill) {
                endDraw(geo, tempsb);
            }
            else
                endTransparentFill(geo,tempsb);
        }
        else {
        // draw an ellipse by scaling a circle
            double x1 = geo.getTranslationVector().getX();
            double y1 = geo.getTranslationVector().getY();
            double r1 = geo.getHalfAxes()[0];
            double r2 = geo.getHalfAxes()[1];

            if(nofill) {
                if(!compact)
                    tempsb.append("\n");
                if(compactcse5)
                    tempsb.append("D(");
                else
                    tempsb.append("draw(");
            }
            else
                startTransparentFill(tempsb);
            tempsb.append("shift(");
            addPoint(format(x1),format(y1),tempsb);
            packSpaceBetween(tempsb, ")", "*", "scale(" + format(r1) + ",", format(r2) + ")*unitcircle");
            if(nofill) 
                endDraw(geo, tempsb);
            else
                endTransparentFill(geo, tempsb);
        }
        
        if(nofill)
            code.append(tempsb);
        else
            codeFilledObject.append(tempsb);
    }
    
    @Override
	protected void drawGeoConic(GeoConic geo){  
        switch(geo.getType()){
        // if conic is a circle
            case GeoConicNDConstants.CONIC_CIRCLE:
                drawCircle(geo);
            break;
        // if conic is an ellipse
            case GeoConicNDConstants.CONIC_ELLIPSE:
                AffineTransform at=geogebra.awt.GAffineTransformD.getAwtAffineTransform(geo.getAffineTransform());
                double eigenvecX = at.getScaleX();
                double eigenvecY = at.getShearY();
                double x1 = geo.getTranslationVector().getX();
                double y1 = geo.getTranslationVector().getY();
                double r1 = geo.getHalfAxes()[0];
                double r2 = geo.getHalfAxes()[1];
                double angle = Math.toDegrees(Math.atan2(eigenvecY,eigenvecX));
                
                // use scale operator to draw ellipse
                if(compactcse5)
                    code.append("D(shift(");
                else
                    code.append("draw(shift(");
                addPoint(format(x1),format(y1),code);
                code.append(")*rotate(");
                code.append(format(angle));
                code.append(")*xscale(");
                code.append(format(r1));
                code.append(")*yscale(");
                code.append(format(r2));
                code.append(")*unitcircle");
                endDraw(geo);
            break;
            
        // if conic is a parabola 
            case GeoConicNDConstants.CONIC_PARABOLA:       
                 // parameter of the parabola
                double p = geo.p;
                at = geogebra.awt.GAffineTransformD.getAwtAffineTransform(geo.getAffineTransform());
                 // first eigenvector
                eigenvecX = at.getScaleX();
                eigenvecY = at.getShearY();
                 // vertex
                x1 = geo.getTranslationVector().getX();
                y1 = geo.getTranslationVector().getY();
                
                 // calculate the x range to draw the parabola
                double x0 = Math.max( Math.abs(x1-xmin), Math.abs(x1-xmax) );
                x0 = Math.max(x0, Math.abs(y1 - ymin));
                x0 = Math.max(x0, Math.abs(y1 - ymax));
                /*
                x0 *= 2.0d;
                // y� = 2px
                y0 = Math.sqrt(2*c.p*x0);
                */
                
                // avoid sqrt by choosing x = k*p with         
                // i = 2*k is quadratic number
                // make parabola big enough: k*p >= 2*x0 -> 2*k >= 4*x0/p
                x0 = 4 * x0 / p;
                int i = 4, k2 = 16;
                while (k2 < x0) {
                    i += 2;
                    k2 = i * i;
                }
                //x0 = k2/2 * p; // x = k*p
                x0 = i * p;    // y = sqrt(2k p^2) = i p
                angle=Math.toDegrees(Math.atan2(eigenvecY,eigenvecX))-90;
                
                 // write real parabola (real x) function
                parabolaCount++;
                if(!compact)
                    code.append("\n");
                code.append("real p");
                if(!compact)
                    code.append("arabola");
                code.append(parabolaCount);
                packSpace(code,"(real x)");
                code.append("{return x^2/2/");
                if(compact)
                    code.append(format(p));
                else
                    code.append(p);
                code.append(";} ");
                
                 // use graph to plot parabola
                if(!compact)
                    code.append("\n");
                if(compactcse5)
                    code.append("D(shift(");
                else
                    code.append("draw(shift(");
                addPoint(format(x1),format(y1),code);
                code.append(")*rotate(");
                code.append(format(angle));
                code.append(")*graph(p");
                if(!compact)
                    code.append("arabola");
                code.append(parabolaCount);
                code.append(",");
                code.append(format(-x0));
                code.append(",");
                code.append(format(x0));
                code.append(")");
                endDraw(geo);
                
                if(!compact)
                    code.append("/* parabola construction */");
            break;
            
            case GeoConicNDConstants.CONIC_HYPERBOLA:
//              parametric: (a(1+t^2)/(1-t^2), 2bt/(1-t^2))
                at = geogebra.awt.GAffineTransformD.getAwtAffineTransform(geo.getAffineTransform());
                eigenvecX = at.getScaleX();
                eigenvecY = at.getShearY();
                x1 = geo.getTranslationVector().getX();
                y1 = geo.getTranslationVector().getY();
                r1 = geo.getHalfAxes()[0];
                r2 = geo.getHalfAxes()[1];
                angle = Math.toDegrees(Math.atan2(eigenvecY,eigenvecX));
                
                hyperbolaCount++;
                if(!compact)
                    code.append("\n");
                if(!compact)
                    code.append("pair hyperbolaLeft");
                else 
                    code.append("pair hl");
                code.append(hyperbolaCount);
                packSpace(code,"(real t)");
                code.append("{return (");
                code.append(format(r1));
                code.append("*(1+t^2)/(1-t^2),");
                code.append(format(r2));
                code.append("*2*t/(1-t^2));} ");
                if(!compact)
                    code.append("pair hyperbolaRight");
                else 
                    code.append("pair hr");
                code.append(hyperbolaCount);
                packSpace(code,"(real t)");
                code.append("{return (");
                code.append(format(r1));
                code.append("*(-1-t^2)/(1-t^2),");
                code.append(format(r2));
                code.append("*(-2)*t/(1-t^2));} ");
                
                 // use graph to plot both halves of hyperbola
                if(!compact)
                    code.append("\n");
                if(compactcse5)
                    code.append("D(shift(");
                else
                    code.append("draw(shift(");
                addPoint(format(x1),format(y1),code);
                code.append(")*rotate(");
                code.append(format(angle));
                if(!compact)
                    code.append(")*graph(hyperbolaLeft");
                else 
                    code.append(")*graph(hl");
                code.append(hyperbolaCount);
                code.append(",-0.99,0.99)");    // arbitrary to approach (-1,1)
                endDraw(geo);
                
                if(compactcse5)
                    code.append("D(shift(");
                else
                    code.append("draw(shift(");
                addPoint(format(x1),format(y1),code);
                code.append(")*rotate(");
                code.append(format(angle));
                if(!compact)
                    code.append(")*graph(hyperbolaRight");
                else 
                    code.append(")*graph(hr");
                code.append(hyperbolaCount);
                code.append(",-0.99,0.99)");
                endDraw(geo);
                
                if(!compact)
                    code.append("/* hyperbola construction */");
                
                break;
        }   
    }
    // draws dot
    @Override
	protected void drawGeoPoint(GeoPoint gp){
        if (frame.getExportPointSymbol()){
            double x = gp.getX(),
                   y = gp.getY(),
                   z = gp.getZ();
            x = x/z;
            y = y/z;
            gp.getNameDescription();
            int dotstyle = gp.getPointStyle();
            if (dotstyle == -1) { // default
                dotstyle = EuclidianStyleConstants.POINT_STYLE_DOT;
            }                     // draw special dot styles
            if(dotstyle != EuclidianStyleConstants.POINT_STYLE_DOT) {
                drawSpecialPoint(gp);
            }
            else {                // plain dot style
                if(!compact)
                    codePoint.append("\n");
                if(compactcse5)
                    codePoint.append("D(");
                else
                    codePoint.append("dot(");
                addPoint(format(x),format(y),codePoint);          
                PointOptionCode(gp,codePoint);
                codePoint.append("); ");
            }   
        }
    }
    /** Draws a point with a special point style (usually uses draw() or filldraw() command).
     * @param geo GeoPoint with style not equal to the standard dot style.
     */
    protected void drawSpecialPoint(GeoPoint geo){
        // radius = dotsize (pt) * (2.54 cm)/(72 pt per inch) * XUnit / cm
        double dotsize = geo.getPointSize();
        double radius = dotsize * (2.54/72) * (frame.getXUnit());
        int dotstyle = geo.getPointStyle();
        if (dotstyle == -1) { // default
            dotstyle = EuclidianStyleConstants.POINT_STYLE_DOT;
        }
        double x = geo.getX(),
               y = geo.getY(),
               z = geo.getZ();
        x = x/z;
        y = y/z;
        geogebra.common.awt.GColor dotcolor = geo.getObjectColor();
        
        switch(dotstyle){
            case EuclidianStyleConstants.POINT_STYLE_CROSS:
                startDraw();
                code.append("shift((" + format(x) + "," + format(y) + "))*");
                code.append("scale(");
                code.append(format(radius));
                code.append(")*(expi(pi/4)--expi(5*pi/4)");
                if(compactcse5) // compromise for cse5, does not allow join operator
                    code.append("--(0,0)--");
                else
                    code.append("^^");
                code.append("expi(3*pi/4)--expi(7*pi/4))");
                endPoint(dotcolor);
            break;
            case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
                // use dot(..,UnFill(0)) command in lieu of filldraw
                if(!compactcse5) {
                    codePoint.append("dot(");
                    addPoint(format(x),format(y),codePoint);
                    // 4.0 slightly arbitrary. 6.0 should be corrective factor, but too small. 
                    PointOptionCode(geo,codePoint,geo.getPointSize()/4.0);
                    codePoint.append(",UnFill(0)); ");
                }
                // use filldraw(CR) for cse5
                else {
                    startDraw();
                    // if(compactcse5)
                        code.append("CR((");
                    // else
                    //  code.append("circle((");
                    code.append(format(x) + "," + format(y) + "),");
                    code.append(format(radius));
                    code.append(")");
                    endPoint(dotcolor);
                }
            break;
            case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
                startDraw();
                code.append("shift((" + format(x) + "," + format(y) + "))*");
                code.append("scale(");
                code.append(format(radius));
                code.append(")*((1,0)--(0,1)--(-1,0)--(0,-1)--cycle)");
                endPoint(dotcolor);
            break;
            case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
                if(!compact)
                    code.append("\n");
                packSpaceBetween("fill(shift((" + format(x) + "," + format(y) + "))", "*", 
                        "scale(" + format(radius) + ")", "*", "((1,0)--(0,1)--(-1,0)--(0,-1)--cycle)");
                endPoint(dotcolor);
            break;
            case EuclidianStyleConstants.POINT_STYLE_PLUS:
                startDraw();
                packSpaceBetween("shift((" + format(x) + "," + format(y) + "))", "*", 
                                  "scale(" + format(radius) + ")","*","((0,1)--(0,-1)");
                if(compactcse5) // compromise for cse5, does not allow join operator
                    code.append("--(0,0)--");
                else
                    code.append("^^");
                code.append("(1,0)--(-1,0))");
                endPoint(dotcolor);
            break;
            case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
                if(!compact)
                    code.append("\n");
                packSpaceBetween("fill(shift((" + format(x) + "," + format(y) + "))", "*",
                        "scale(" + format(radius) + ")", "*", 
                        "((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
                endPoint(dotcolor);
            break;
            case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:
                if(!compact)
                    code.append("\n");
                packSpaceBetween("fill(shift((" + format(x) + "," + format(y) + "))", "*",
                        "rotate(90)", "*", 
                        "scale(" + format(radius) + ")", "*", 
                        "((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
                endPoint(dotcolor);
            break;
            case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
                if(!compact)
                    code.append("\n");
                packSpaceBetween("fill(shift((" + format(x) + "," + format(y) + "))", "*",
                        "rotate(270)", "*", 
                        "scale(" + format(radius) + ")", "*", 
                        "((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
                endPoint(dotcolor);
            break;
            case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:
                if(!compact)
                    code.append("\n");
                packSpaceBetween("fill(shift((" + format(x) + "," + format(y) + "))", "*",
                        "rotate(180)", "*", 
                        "scale(" + format(radius) + ")", "*", 
                        "((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
                endPoint(dotcolor);
            break;
            default:
            break;
        }
        if(!compact)
            code.append("/* special point */");
    }
    // draws line
    @Override
	protected void drawGeoLine(GeoLine geo){
        double x = geo.getX(),
               y = geo.getY(),
               z = geo.getZ();
        
        if (y != 0){
            startDraw();
             // new evaluation: [-x/y]*[xmin or xmax]-(z/y)
            packSpaceAfter(code, "(xmin,");
            code.append(format(-x/y));
            code.append("*xmin");
            if(z/y < 0 || format(-z/y).equals("0")) 
                packSpace(code, "+");
            code.append(format(-z/y));
            code.append(")");
            // String tmpy=format(y);
            // if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
            // else code.append(y);
            packSpaceAfter(code, "--(xmax,");
            code.append(format(-x/y));
            code.append("*xmax");
            if(z/y < 0 || format(-z/y).equals("0")) 
                packSpace(code, "+");
            code.append(format(-z/y));
            // if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
            // else code.append(y);
            code.append(")");
            endDraw(geo);   
        }
        else { // vertical line
            if(!compact)
                code.append("\n");
            if(compactcse5)
                code.append("D((");
            else
                code.append("draw((");
            String s=format(-z/x);
            code.append(s);
            code.append(",ymin)--(");
            code.append(s);
            code.append(",ymax)");
            endDraw(geo);
        }
        if(!compact)
            code.append("/* line */");
    }
    // draws segment
    @Override
	protected void drawGeoSegment(GeoSegment geo){
        double[] A = new double[2],
                 B = new double[2];
        GeoPoint pointStart = geo.getStartPoint();
        GeoPoint pointEnd = geo.getEndPoint();
        pointStart.getInhomCoords(A);
        pointEnd.getInhomCoords(B);
        String x1 = format(A[0]),
               y1 = format(A[1]),
               x2 = format(B[0]),
               y2 = format(B[1]);
        int deco = geo.decorationType;
        
        if(!compact)
            code.append("\n");
        if(!compactcse5) 
            code.append("draw(");
        else
            code.append("D(");
        addPoint(x1,y1,code);
        code.append("--");
        addPoint(x2,y2,code);
        endDraw(geo);
        
        if (deco != GeoElement.DECORATION_NONE) mark(A,B,deco,geo);
    }
    
    @Override
	protected void drawLine(double x1,double y1,double x2,double y2,GeoElement geo){
        String sx1 = format(x1);
        String sy1 = format(y1);
        String sx2 = format(x2);
        String sy2 = format(y2);

        startDraw();
        addPoint(sx1,sy1,code);
        code.append("--");
        addPoint(sx2,sy2,code);
        endDraw(geo);
    }
    
    @Override
	protected void drawGeoRay(GeoRay geo){
        GeoPoint pointStart = geo.getStartPoint();
        double x1 = pointStart.getX();
        double z1 = pointStart.getZ();
        x1 = x1/z1;
        String y1 = format(pointStart.getY()/z1);
        
        double x = geo.getX(),
               y = geo.getY(),
               z = geo.getZ();
        double yEndpoint; // records explicitly y-coordinate of endpoint
        // String tmpy = format(y);
        double inf = xmin, sup = xmax;  // determine left and right bounds on x to draw ray
        if (y > 0) {
            inf = x1;
            yEndpoint = (-z - x*inf) / y;
        }
        else {
            sup = x1;
            yEndpoint = (-z - x*sup) / y;
        }
    
         // format: draw((inf,f(inf))--(xmax,f(xmax)));
         //     OR: draw((xmin,f(xmin))--(sup,f(sup)));
         // old evaluation: (-(z)-(x)*[inf or sup])/y
         // new evaluation: [-x/y]*[inf or sup]-(z/y)
        startDraw();
        if (y != 0){  // non-vertical line   
            if (y > 0) {
                addPoint(format(inf),format(yEndpoint),code);
                code.append("--");
                packSpaceAfter(code, "(xmax,");
                code.append(format(-x/y));
                code.append("*xmax");
                if(z/y < 0 || format(-z/y).equals("0")) 
                    packSpace(code, "+");
                code.append(format(-z/y));
                // code.append(")/");
                // if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
                // else code.append(y);
                code.append(")");   
            }
            else {
                addPoint(format(sup),format(yEndpoint),code);
                code.append("--");
                packSpaceAfter(code, "(xmin,");
                code.append(format(-x/y));
                code.append("*xmin");
                if(z/y < 0 || format(-z/y).equals("0")) 
                    packSpace(code, "+");
                code.append(format(-z/y));
                // code.append("/");
                // if (Double.parseDouble(tmpy) != 0) code.append(tmpy);
                // else code.append(y);
                code.append(")");
            }
            endDraw(geo);
        }
        else {
            addPoint(format(x1),y1,code);
            code.append("--(");
            code.append(format(x1));
            packSpaceAfter(code, ",");
            if (-x > 0)
                code.append("ymax");
            else
                code.append("ymin");
            code.append(")");
            endDraw(geo);
        }
        if(!compact)
            code.append("/* ray */");
    }
    
    @Override
    protected void drawImplicitPoly(GeoImplicitPoly geo) {
        // credit: help from Art of Problem Solving user fedja
        importpackage.add("contour");  // importContour = true;    flag for preamble to import contour package 
        // two-variable implicit function expression
        String polynomial = parseFunction(getImplicitExpr(geo)).replaceAll("\\\\pi", "pi");
        implicitFuncCount++;
        int implicitFuncName = implicitFuncCount;
        
        // if compact, retrieve previous instance of implicit polynomial expression, if exists
        if(!compact || !implicitPolyTable.containsKey(polynomial)) {
            if(compact) {
                if(implicitPolyTable.isEmpty())  // map polynomial to function #
                    implicitPolyTable.put(polynomial, 1);
                else {
                    implicitFuncName = implicitPolyTable.size() + 1;
                    implicitPolyTable.put(polynomial, implicitFuncName);
                }
            }
            
            // write implicitf# (real x, real y) is implicit polynomial function of two variables
            if(!compact)
                code.append("\n");
            code.append("real implicitf");
            code.append(implicitFuncName);
            packSpace("(real x, real y)", "{");
            code.append("return " + polynomial);
            packSpaceAfter(";");
            code.append("} "); 
        } 
        else 
            implicitFuncName = implicitPolyTable.get(polynomial);
        
        startDraw(); // code: draw(contour(f, (xmin,ymin), (xmax,ymax), new real[]{0}, 500));
        code.append("contour(implicitf");
        code.append(implicitFuncName);
        packSpaceBetween(code, ",", "(xmin,ymin),", "(xmax,ymax),", "new real[]{0},", "500)");
        endDraw(geo);
    }
    
    @Override
    protected void drawGeoInequalities(GeoFunctionNVar geo) { // TODO
        importpackage.add("graph");
        
        FunctionNVar function = geo.getFunction();
        int ineqCount = function.getIneqs().getSize();
        codeFilledObject.append("/* Inequalities: " + ineqCount + " */");
        
        /*     
        // take line g here, not geo this object may be used for conics too
        boolean isVisible = geo.isEuclidianVisible();
        if (!isVisible)
            return;
        boolean labelVisible = geo.isLabelVisible();
        
        int ineqCount = function.getIneqs().size();
            
        for (int i = 0; i < ineqCount; i++) {
            Inequality ineq = function.getIneqs().get(i);           
            if(drawables.size() <= i || !matchBorder(ineq.getBorder(),i)){
                Drawable draw;
                switch (ineq.getType()){
                    case Inequality.INEQUALITY_PARAMETRIC_Y: 
                        draw = new DrawParametricInequality(ineq, view, geo);
                        break;
                    case Inequality.INEQUALITY_PARAMETRIC_X: 
                        draw = new DrawParametricInequality(ineq, view, geo);
                        break;
                    case Inequality.INEQUALITY_1VAR_X: 
                        draw = new DrawInequality1Var(ineq, view, geo, false);
                        break;
                    case Inequality.INEQUALITY_1VAR_Y: 
                        draw = new DrawInequality1Var(ineq, view, geo, true);
                        break;  
                    case Inequality.INEQUALITY_CONIC: 
                        draw = new DrawConic(view, ineq.getConicBorder());                  
                        ineq.getConicBorder().setInverseFill(geo.isInverseFill() ^ ineq.isAboveBorder());   
                        break;  
                    case Inequality.INEQUALITY_IMPLICIT: 
                        draw = new DrawImplicitPoly(view, ineq.getImpBorder());
                        break;
                    default: draw = null;
                }
            
                draw.setGeoElement((GeoElement)function);
                draw.update();
                if(drawables.size() <= i)
                    drawables.add(draw);
                else
                    drawables.set(i,draw);
            }
            else {
                if(ineq.getType() == Inequality.INEQUALITY_CONIC) {                 
                    ineq.getConicBorder().setInverseFill(geo.isInverseFill() ^ ineq.isAboveBorder());
                }
                drawables.get(i).update();
            }
    */  
    }
    
    @Override
    protected void drawPolyLine(GeoPolyLine geo) {
        GeoPointND[] points = geo.getPoints();
        
        startDraw(); // connect (by join --) all points within one draw statement
        for (int i = 0; i < points.length; i++){
        	Coords coords = points[i].getInhomCoords();
            double x = coords.getX(),
                   y = coords.getY();
            addPoint(format(x),format(y),code);
            if (i != points.length - 1)
                code.append("--");
        }
        endDraw(geo);
    }
    
    private void initUnitAndVariable(){
        // Initaialze units, dot style, dot size .... 
        /* codeBeginPic.append("\\psset{xunit=");
        codeBeginPic.append(sci2dec(xunit));
        codeBeginPic.append("cm,yunit=");
        codeBeginPic.append(sci2dec(yunit));
        codeBeginPic.append("cm,algebraic=true,dotstyle=o,dotsize=");
        codeBeginPic.append(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
        codeBeginPic.append("pt 0");
        codeBeginPic.append(",linewidth=");
        codeBeginPic.append(format(EuclidianStyleConstants.DEFAULT_LINE_THICKNESS/2*0.8));
        codeBeginPic.append("pt,arrowsize=3pt 2,arrowinset=0.25}\n"); */
        
        if (!compact) {
            codePreamble.append("\nreal labelscalefactor = 0.5; /* changes label-to-point distance */");
            codePreamble.append("\npen dps = linewidth(0.7) + fontsize(");
            codePreamble.append(fontsize);
            codePreamble.append("); defaultpen(dps); /* default pen style */ ");
            if(!frame.getKeepDotColors())
                codePreamble.append("\npen dotstyle = black; /* point style */ \n");
        }
        else if (!compactcse5) {
            codePreamble.append("real lsf=0.5; pen dps=linewidth(0.7)+fontsize(");
            codePreamble.append(fontsize);
            codePreamble.append("); defaultpen(dps); ");
            if(!frame.getKeepDotColors())
                codePreamble.append("pen ds=black; ");
        }
        else {
            codePreamble.append("real lsf=0.5; pathpen=linewidth(0.7); pointpen=black; pen fp=fontsize(");
            codePreamble.append(fontsize);
            codePreamble.append("); pointfontpen=fp; ");
        }
        packSpaceBetween(codePreamble, "real xmin", "=", format(xmin) + ",", 
                                            "xmax", "=", format(xmax) + ",", 
                                            "ymin", "=", format(ymin) + ",",
                                            "ymax", "=", format(ymax) + "; ");
        if(!compact) 
            codePreamble.append(" /* image dimensions */\n");
        else { /* codePreamble.append("\n"); */ }
    }
    // Generate list of pairs for cse5 code to use
    private void initPointDeclarations(){
        if(!pairName) return;
        Iterator<GeoPoint> it = pointList.iterator();
        boolean comma = false;    // flag for determining whether to add comma
        // pre-defined pair names in base module plain. Do not re-write to save hassle
        String predefinedNames[] = {"N", "S", "E", "W", "NE", "SE", "NW", "SW",
                "NNE", "NNW", "SSE", "SSW", "ENE", "WNW", "ESE", "WSW", 
                "left", "right", "up", "down"};
        
        while(it.hasNext()) {
            GeoPoint gp = it.next();
            if(gp.getPointStyle() == EuclidianStyleConstants.POINT_STYLE_DOT
            || gp.getPointStyle() == EuclidianStyleConstants.POINT_STYLE_CIRCLE) {
                double x = gp.getX(), y = gp.getY(), z = gp.getZ();
                x /= z; y /= z;
                String pairString = "(" + format(x) + "," + format(y) + ")";
                String pointName = gp.getLabel(getStringTemplate());
                boolean isVariable = true;
                
                // Note: if problem with point name, simply discard and move on.
                // check if characters of point names are valid, namely alphanumeric or underscore
                for(int i = 0; i < pointName.length(); i++) 
                    if(!Character.isLetterOrDigit(pointName.charAt(i)) && pointName.charAt(i) != '_')
                        isVariable = false;
                
                // check that point names don't re-write basic asymptote pairs
                for(int i = 0; i < predefinedNames.length; i++) 
                    if(pointName.equals(predefinedNames[i]))
                        isVariable = false;
                
                // store pairString -> pairName, write asy declaration pair pairName = pairString;
                if(!pairNameTable.containsKey(pairString) && isVariable) {
                    if(comma)
                        codePointDecl.append(", ");
                    else
                        comma = true;
                    pairNameTable.put(pairString, pointName);
                    codePointDecl.append(pointName);
                    packSpace(codePointDecl,"=");
                    codePointDecl.append(pairString);
                }
            }
        }
        if(comma) {
            codePointDecl.insert(0, "\npair ");
            codePointDecl.append("; ");
        }
    }
    
    // if label is visible, draw it
    @Override
	protected void drawLabel(GeoElement geo,DrawableND drawGeo){
        try{
            if (geo.isLabelVisible()){
                String name;
                if (geo.getLabelMode() == GeoElement.LABEL_CAPTION) {
                   name = convertUnicodeToText(geo.getLabelDescription()).replaceAll("\\$","dollar");
                }
                else if (compactcse5) {
                   name = StringUtil.toLaTeXString(geo.getLabelDescription(),true);
                   name = convertUnicodeToLatex(name);
                }
                else {
                   name = "$"+StringUtil.toLaTeXString(geo.getLabelDescription(),true)+"$";
                   name = convertUnicodeToLatex(name);
                }
                if (name.indexOf("\u00b0") != -1){
                   name = name.replaceAll("\u00b0", "^\\\\circ");
                }
    
                if (drawGeo == null) 
                    drawGeo = euclidianView.getDrawableFor(geo);
                double xLabel = drawGeo.getxLabel();
                double yLabel = drawGeo.getyLabel();
                xLabel = euclidianView.toRealWorldCoordX(Math.round(xLabel));
                yLabel = euclidianView.toRealWorldCoordY(Math.round(yLabel));
                boolean isPointLabel = false;
                
                geogebra.common.awt.GColor geocolor = geo.getObjectColor();

                if(!compact)
                    codePoint.append("\n");
                if(compactcse5 && geo.getLabelMode() != GeoElement.LABEL_CAPTION)
                    codePoint.append("MP(\"");
                else
                    codePoint.append("label(\""); 
                codePoint.append(name);
                packSpaceBetween(codePoint, "\",", "(");
                codePoint.append(format(xLabel));
                codePoint.append(",");
                codePoint.append(format(yLabel));
                codePoint.append("),");
                if(!compact)
                    codePoint.append(" ");
                codePoint.append("NE");
                packSpace(codePoint,"*");
                if(compact)
                    codePoint.append("lsf");
                if(!compact)
                    codePoint.append("labelscalefactor");
            
                // check if label is of point
                isPointLabel = (geocolor.equals(GColor.BLUE) || ColorEquals(geocolor,geogebra.common.factories.AwtFactory.prototype.newColor(124,124,255))) // xdxdff
                                    // is of the form "A" or "$A$"
                            && ( ((name.length() == 1) && Character.isUpperCase(name.charAt(0)))
                            || ( ((name.length() == 3) && name.charAt(0) == '$' && name.charAt(2) == '$' 
                                && Character.isUpperCase(name.charAt(1)))) ); 
                isPointLabel = isPointLabel || geo.isGeoPoint();
                // replaced with pointfontpen:
                // if(compactcse5) {
                //  codePoint.append(",fp");
                // }
                if(isPointLabel && !frame.getKeepDotColors()) {
                    // configurable or default black?
                    // temp empty
                }
                else if(!geocolor.equals(GColor.BLACK)){
                    if(compactcse5)
                        codePoint.append(",fp+");
                    else
                        codePoint.append(",");
                    ColorCode(geocolor,codePoint);
                }
                codePoint.append("); ");
            }
        }
        // For GeoElement that don't have a Label
        // For example (created with geoList)
        catch(NullPointerException e){
        	App.debug(e);
        }
    }   
    
    /** Returns whether or not c1 and c2 are equivalent colors, when rounded to the nearest hexadecimal integer.
     * @param c1 The first Color object.
     * @param c2 The second Color object to compare with.
     * @return Whether c1 and c2 are equivalent colors, to rounding.
     */
    boolean ColorEquals(geogebra.common.awt.GColor c1, geogebra.common.awt.GColor c2) {
        return format(c1.getRed()  /255d).equals(format(c2.getRed()  /255d))
            && format(c1.getGreen()/255d).equals(format(c2.getGreen()/255d))
            && format(c1.getBlue() /255d).equals(format(c2.getBlue() /255d));
    }
    
    // Draw the grid 
    private void drawGrid(){
        geogebra.common.awt.GColor GridCol = euclidianView.getGridColor();
        double[] GridDist = euclidianView.getGridDistances();
        boolean GridBold = euclidianView.getGridIsBold();
        int GridLine = euclidianView.getGridLineStyle();
        
        if(!compact) {
             // draws grid using Asymptote loops
            codeBeginPic.append("\n /* draw grid of horizontal/vertical lines */");
            codeBeginPic.append("\npen gridstyle = ");
            if(GridBold)
                codeBeginPic.append("linewidth(1.0)");  
            else
                codeBeginPic.append("linewidth(0.7)");  
            codeBeginPic.append(" + ");
            ColorCode(GridCol,codeBeginPic);
            if(GridLine != EuclidianStyleConstants.LINE_TYPE_FULL) {
                codeBeginPic.append(" + ");
                LinestyleCode(GridLine, codeBeginPic);
            }
            codeBeginPic.append("; real gridx = ");
            codeBeginPic.append(format(GridDist[0]));
            codeBeginPic.append(", gridy = ");
            codeBeginPic.append(format(GridDist[1]));
            codeBeginPic.append("; /* grid intervals */"  
                              + "\nfor(real i = ceil(xmin/gridx)*gridx; "
                              + "i <= floor(xmax/gridx)*gridx; i += gridx)");
            codeBeginPic.append("\n draw((i,ymin)--(i,ymax), gridstyle);");         
            codeBeginPic.append("\nfor(real i = ceil(ymin/gridy)*gridy; "
                              + "i <= floor(ymax/gridy)*gridy; i += gridy)");
            codeBeginPic.append("\n draw((xmin,i)--(xmax,i), gridstyle);");         
            codeBeginPic.append("\n /* end grid */ \n");
            return;
        }
        else if(!compactcse5) 
            codeBeginPic.append("\n/*grid*/ "); /*
            //// COMMENTED CODE - explicitly draw grid using for loops. ////
            codeBeginPic.append("pen gs=");
            if(GridBold)
                codeBeginPic.append("linewidth(1.0)");  
            else
                codeBeginPic.append("linewidth(0.7)");  
            codeBeginPic.append("+");
            ColorCode(GridCol,codeBeginPic);
            if(GridLine != EuclidianStyleConstants.LINE_TYPE_FULL) {
               codeBeginPic.append("+");
               LinestyleCode(GridLine, codeBeginPic);
            }
            codeBeginPic.append("; ");
            codeBeginPic.append("real gx=" + format(GridDist[0])
                                  + ",gy=" + format(GridDist[1]) + "; ");
            codeBeginPic.append("\nfor(real i=ceil(xmin/gx)*gx;"
                              + "i<=floor(xmax/gx)*gx;i+=gx)");
            codeBeginPic.append(" draw((i,ymin)--(i,ymax),gs);");           
            codeBeginPic.append(" for(real i=ceil(ymin/gy)*gy;"
                              + "i<=floor(ymax/gy)*gy;i+=gy)");
            codeBeginPic.append(" draw((xmin,i)--(xmax,i),gs); ");     
            
            // USE math module defined method grid(Nx, Ny): 
            real gx=1,gy=1;
            add(scale(gx,gy)*shift(floor(xmin/gx),floor(ymin/gy))*grid(ceil(xmax-xmin)+1,ceil(ymax-ymin)+1,gridpen));
        } 
        else { // with cse5 shorthands
            if(GridBold)
                codeBeginPic.append("linewidth(1.0)");  
            else
                codeBeginPic.append("linewidth(0.7)");  
            codeBeginPic.append("+");
            ColorCode(GridCol,codeBeginPic);
            if(GridLine != EuclidianStyleConstants.LINE_TYPE_FULL) {
                codeBeginPic.append("+");
                LinestyleCode(GridLine, codeBeginPic);
            }
            codeBeginPic.append("; real gx=");
            codeBeginPic.append(format(GridDist[0]));
            codeBeginPic.append(",gy=");
            codeBeginPic.append(format(GridDist[1]));
            codeBeginPic.append(";\nfor(real i=ceil(xmin/gx)*gx;"
                              + "i<=floor(xmax/gx)*gx;i+=gx)");
            codeBeginPic.append(" D((i,ymin)--(i,ymax),gs);");          
            codeBeginPic.append(" for(real i=ceil(ymin/gy)*gy;"
                              + "i<=floor(ymax/gy)*gy;i+=gy)");
            codeBeginPic.append(" D((xmin,i)--(xmax,i),gs); "); 
        } */
        importpackage.add("math");
        codeBeginPic.append("real gx=" + format(GridDist[0])
                              + ",gy=" + format(GridDist[1]) + "; ");
        codeBeginPic.append("add(scale(gx,gy)*shift(floor(xmin/gx),floor(ymin/gy))*grid(ceil(xmax-xmin)+1,ceil(ymax-ymin)+1,");
        if(GridBold)
            codeBeginPic.append("linewidth(1.0)");  
        else
            codeBeginPic.append("linewidth(0.7)");  
        codeBeginPic.append("+");
        ColorCode(GridCol,codeBeginPic);
        if(GridLine != EuclidianStyleConstants.LINE_TYPE_FULL) {
            codeBeginPic.append("+");
            LinestyleCode(GridLine, codeBeginPic);
        }
        codeBeginPic.append(")); ");
    }
    
    // Draws Axis presuming shown
    // TODO low priority: improve modularity of this function, repeated code for xaxis/yaxis.
    // note: may shift around relative positions of certain labels. 
    private void drawAxis(){
        boolean xAxis    = euclidianView.getShowXaxis();
        boolean yAxis    = euclidianView.getShowYaxis();
        boolean bx       = euclidianView.getShowAxesNumbers()[0];
        boolean by       = euclidianView.getShowAxesNumbers()[1];
        String Dx        = format(euclidianView.getAxesNumberingDistances()[0]);
        String Dy        = format(euclidianView.getAxesNumberingDistances()[1]);
        String[] label   = euclidianView.getAxesLabels();
        String[] units   = euclidianView.getAxesUnitLabels();
        int axisStyle    = euclidianView.getAxesLineStyle();
        int[] tickStyle  = euclidianView.getAxesTickStyles();
        geogebra.common.awt.GColor axisColor  = euclidianView.getAxesColor();
        boolean axisBold =  (axisStyle == EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_BOLD) 
                         || (axisStyle == EuclidianStyleConstants.AXES_LINE_TYPE_FULL_BOLD);
        boolean axisArrow = (axisStyle == EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_BOLD) 
                         || (axisStyle == EuclidianStyleConstants.AXES_LINE_TYPE_ARROW);
        
        String lx = "", ly = "";    // axis labels
        if(label[0] != null)
            lx = "$"+StringUtil.toLaTeXString(label[0], true)+"$";
        if(label[1] != null)
            ly = "$"+StringUtil.toLaTeXString(label[1], true)+"$";
/* follow format:       
        void xaxis(picture pic=currentpicture, Label L="", axis axis=YZero,
                real xmin=-infinity, real xmax=infinity, pen p=currentpen, 
                ticks ticks=NoTicks, arrowbar arrow=None, bool above=false);
 */
       
        // Note: code for xaxis and yaxis duplicated twice.
        //  When making changes, be sure to update both. 
        if(xAxis || yAxis) {
            codeBeginPic.append("\n");   // create initial label
            codeBeginPic.append("Label laxis; laxis.p");
            packSpace(codeBeginPic, "=");
            codeBeginPic.append("fontsize(" + fontsize + "); ");
            if (!bx || !by) { // implement no number shown
                if(!compact)
                    codeBeginPic.append("\n");
                codeBeginPic.append("string blank(real x) {return \"\";} ");
            }
            if(bx || by) { // implement unit labels
                if(units[0] != null && !units[0].equals("")) {
                    codeBeginPic.append("string ");
                    if(compact)
                        codeBeginPic.append("xlbl");
                    else
                        codeBeginPic.append("xaxislabel");
                    packSpace(codeBeginPic, "(real x)");
                    packSpaceAfter(codeBeginPic, "{");
                    
                    // asymptote code for pi labels:
                    // string xlbl(real x){string s; int n=round(2*x/pi); if(abs(n-2*x/pi) > 1e-3) return string(x); 
                    // if(abs(n)>2) s=string(round((n%2+1)*x/pi)); if(n%2==0) return "$"+s+"\pi$"; return "$"+s+"\pi/2$";}
                    
                    // unit label is pi: format -1pi, -1pi/2, 0pi, 1pi/2, 1pi
                    if(units[0].equals("\u03c0")) { 
                        // create labeling function for special labels if n = -1,0,1
                        packSpaceBetween(codeBeginPic, "string s; ", "int n", "=", "round(2*x/pi); ");
                        if(!compact)
                            codeBeginPic.append("\n");
                        packSpaceBetween(codeBeginPic, "if(abs(n-2*x/pi)", ">", "1e-3) return string(x); ");
                        if(!compact)
                            codeBeginPic.append("\n");
                        packSpaceBetween(codeBeginPic, "if(abs(n)", ">", "2) s = string(round((n%2", "+", "1)*x/pi)); ");
                        if(!compact)
                            codeBeginPic.append("\n");
                        packSpaceBetween(codeBeginPic, "if(n%2", "==", "0) return \"$\"+s+\"\\pi$\"; ");
                        // codeBeginPic.append("int n=round(x/pi); ");
                        // codeBeginPic.append("if(n==-1) return \"$-\\pi$\"; ");
                        // codeBeginPic.append("if(n==1) return \"$\\pi$\"; ");
                        // codeBeginPic.append("if(n==0) return \"$0$\"; ");
                    }
                    codeBeginPic.append("return \"$\"");
                    packSpace(codeBeginPic, "+");
                    // unit label is pi
                    if(units[0].equals("\u03c0")) 
                        packSpaceBetween(codeBeginPic,"s", "+", "\"\\pi/2");
                    // unit label is degrees symbol
                    else if(units[0].equals("\u00b0")) 
                        packSpaceBetween(codeBeginPic, "string(x)", "+", "\"^\\circ");
                    else {
                        codeBeginPic.append("string(x)");
                        packSpace(codeBeginPic, "+");
                        codeBeginPic.append("\"\\,\\mathrm{"+units[0]+"}");
                    }
                    codeBeginPic.append("$\";} ");
                }
                if(units[1] != null && !units[1].equals("")) {
                    codeBeginPic.append("string ");
                    if(compact)
                        codeBeginPic.append("ylbl");
                    else
                        codeBeginPic.append("yaxislabel");
                    packSpace(codeBeginPic, "(real x)");
                    packSpaceAfter(codeBeginPic, "{");
                    
                    // asymptote code for pi labels:
                    // string ylbl(real x){string s; int n=round(2*x/pi); if(abs(n-2*x/pi) > 1e-3) return string(x); 
                    // if(abs(n)>2) s=string(round((n%2+1)*x/pi)); if(n%2==0) return "$"+s+"\pi$"; return "$"+s+"\pi/2$";}
                    
                    // unit label is pi: format -1pi, -1pi/2, 0pi, 1pi/2, 1pi
                    if(units[1].equals("\u03c0")) { 
                        // create labeling function for special labels if n = -1,0,1
                        packSpaceBetween(codeBeginPic, "string s; ", "int n", "=", "round(2*x/pi); ");
                        if(!compact)
                            codeBeginPic.append("\n");
                        packSpaceBetween(codeBeginPic, "if(abs(n-2*x/pi)", ">", "1e-3) return string(x); ");
                        if(!compact)
                            codeBeginPic.append("\n");
                        packSpaceBetween(codeBeginPic, "if(abs(n)", ">", "2) s = string(round((n%2", "+", "1)*x/pi)); ");
                        if(!compact)
                            codeBeginPic.append("\n");
                        packSpaceBetween(codeBeginPic, "if(n%2", "==", "0) return \"$\"+s+\"\\pi$\"; ");
                        // codeBeginPic.append("int n=round(x/pi); ");
                        // codeBeginPic.append("if(n==-1) return \"$-\\pi$\"; ");
                        // codeBeginPic.append("if(n==1) return \"$\\pi$\"; ");
                        // codeBeginPic.append("if(n==0) return \"$0$\"; ");
                    }
                    codeBeginPic.append("return \"$\"");
                    packSpace(codeBeginPic, "+");
                    // unit label is pi
                    if(units[1].equals("\u03c0")) 
                        packSpaceBetween(codeBeginPic,"s", "+", "\"\\pi/2");
                    // unit label is degrees symbol
                    else if(units[1].equals("\u00b0")) 
                        packSpaceBetween(codeBeginPic, "string(x)", "+", "\"^\\circ");
                    else {
                        codeBeginPic.append("string(x)");
                        packSpace(codeBeginPic,"+");
                        // put units in text form
                        codeBeginPic.append("\"\\,\\mathrm{"+units[1]+"}");
                    }
                    codeBeginPic.append("$\";} ");
                }
            }
            codeBeginPic.append("\n");
        }
        if(xAxis) {
            codeBeginPic.append("xaxis(");
            if (label[0] != null) // axis label
                packSpaceBetween(codeBeginPic, "\""+lx+"\",");
            packSpaceBetween(codeBeginPic, "xmin,", "xmax"); // non-fixed axes?
                                                             // TODO: remove if !compact? priority: minor
            // axis pen style
            if(axisColor != GColor.BLACK) {
                codeBeginPic.append(",");
                // catch for other options not changing.
                if(compactcse5)
                    codeBeginPic.append("pathpen+");
                else
                    codeBeginPic.append("defaultpen+");
                ColorCode(axisColor,codeBeginPic);
                if(axisBold) {
                    codeBeginPic.append("+linewidth(1.2)");
                }
            }
            else if(axisBold) {
                codeBeginPic.append(",linewidth(1.2)");
            } 
            packSpaceAfter(codeBeginPic, ",");
            if(tickStyle[0] == EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR) {
                packSpaceAfter(codeBeginPic, "Ticks(laxis,");
                if(!bx) // no tick labels
                    packSpaceAfter(codeBeginPic, "blank,");
                else if(units[0] != null && !units[0].equals("")) {
                    if(compact)
                        packSpaceAfter(codeBeginPic, "xlbl,");
                    else
                        packSpaceAfter(codeBeginPic, "xaxislabel,");
                } 
                // Step=Dx, Size=2, NoZero
                packSpaceBetween(codeBeginPic, "Step", "=", Dx + ",", "Size", "=", "2"); 
                if(yAxis)
                    packSpaceBetween(codeBeginPic, ",", "NoZero");
                codeBeginPic.append(")");
            }
            else if(tickStyle[0] == EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR) {
                packSpaceAfter(codeBeginPic, "Ticks(laxis,");
                if(!bx) // no tick labels
                    packSpaceAfter(codeBeginPic, "blank,");
                else if(units[0] != null && !units[0].equals("")) {
                    if(compact)
                        packSpaceAfter(codeBeginPic, "xlbl,");
                    else
                        packSpaceAfter(codeBeginPic, "xaxislabel,");
                }
                // n=2, Step=Dx, Size=2, size=1, NoZero
                packSpaceBetween(codeBeginPic, "n", "=", "2,", "Step", "=", Dx + ",", "Size", "=", "2,", "size", "=", "1");
                if(yAxis)
                    packSpaceBetween(codeBeginPic, ",", "NoZero");
                codeBeginPic.append(")");
            }
            if(axisArrow)
                packSpaceBetween(codeBeginPic, ",", "Arrows(6)");
            packSpaceBetween(codeBeginPic, ",", "above", "=", "true); ");
        }
        if(xAxis && yAxis && !compact)
            codeBeginPic.append("\n");
        if(yAxis) {
            codeBeginPic.append("yaxis(");
            if (label[1] != null) // axis label
                packSpaceAfter(codeBeginPic, "\""+ly+"\",");
            packSpaceBetween(codeBeginPic, "ymin,", "ymax"); // non-fixed axes?
            
            // axis pen style
            if(axisColor != GColor.BLACK) {
                if(compactcse5)
                    codeBeginPic.append(",pathpen+");
                else
                    codeBeginPic.append(",defaultpen+");
                ColorCode(axisColor,codeBeginPic);
                if(axisBold) {
                    codeBeginPic.append("+linewidth(1.2)");
                }
            }
            else if(axisBold) {
                codeBeginPic.append(",linewidth(1.2)");
            }
            packSpaceAfter(codeBeginPic, ",");
            if(tickStyle[1] == EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR) {
                packSpaceAfter(codeBeginPic, "Ticks(laxis,");
                if(!by) // no tick labels
                    packSpaceAfter(codeBeginPic, "blank,");
                else if(units[1] != null && !units[1].equals("")) {
                    if(compact)
                        packSpaceAfter(codeBeginPic, "ylbl,");
                    else
                        packSpaceAfter(codeBeginPic, "yaxislabel,");
                }
                // Step=Dy, Size=2, NoZero
                packSpaceBetween(codeBeginPic, "Step", "=", Dy + ",", "Size", "=", "2");
                if(xAxis)
                    packSpaceBetween(codeBeginPic, ",", "NoZero");
                codeBeginPic.append(")");
            }
            else if(tickStyle[1] == EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR) {
                packSpaceAfter(codeBeginPic, "Ticks(laxis,");
                if(!by) // no tick labels
                    packSpaceAfter(codeBeginPic, "blank,");
                else if(units[1] != null && !units[1].equals("")) {
                    if(compact)
                        packSpaceAfter(codeBeginPic, "ylbl,");
                    else
                        packSpaceAfter(codeBeginPic, "yaxislabel,");
                }
                // n=2, Step=Dy, Size=2, size=1, NoZero
                packSpaceBetween(codeBeginPic, "n", "=", "2,", "Step", "=", Dy + ",", "Size", "=", "2,", "size", "=", "1");
                if(xAxis)
                    packSpaceBetween(codeBeginPic, ",", "NoZero");
                codeBeginPic.append(")");
            }
            if(axisArrow)
                packSpaceBetween(codeBeginPic, ",", "Arrows(6)");
            packSpaceBetween(codeBeginPic, ",", "above", "=", "true); ");
        }
        if((xAxis || yAxis) && !compact)  // documentation
            codeBeginPic.append("/* draws axes; NoZero hides '0' label */ ");
    }
    // Returns point style code with size dotsize. Includes comma.
    private void PointOptionCode(GeoPoint geo, StringBuilder sb, double dotsize){
        geogebra.common.awt.GColor dotcolor = geo.getObjectColor();
        int dotstyle   = geo.getPointStyle();
        if (dotstyle == -1) { // default
            dotstyle = EuclidianStyleConstants.POINT_STYLE_DOT;
        }
        boolean comma = false; // add comma
        
        if (dotsize != EuclidianStyleConstants.DEFAULT_POINT_SIZE){
            // comma needed
            comma=true;
            sb.append(",linewidth(");
            // Note: Asymptote magnifies default dotsizes by a scale of 6 x linewidth,
            // but it does not magnify passed-in arguments. So the dotsize here
            // is approximately of the correct size. 
            sb.append(format(dotsize));
            sb.append("pt)");
        }
        if (!dotcolor.equals(GColor.BLACK) && frame.getKeepDotColors()){
            if (comma) packSpace(sb,"+");
            else sb.append(",");
            comma=true;
            
            ColorCode(dotcolor,sb);
        }
        else if (!frame.getKeepDotColors() && !compactcse5){
            if (comma) packSpace(sb,"+");
            else sb.append(",");
            comma = true;
            
            /* cse5 has pointpen attribute */
            if(!compact)
                sb.append("dotstyle");
            else if(!compactcse5)
                sb.append("ds");
        }
        // catch mistake
        if (dotstyle != EuclidianStyleConstants.POINT_STYLE_DOT) {
            if (comma) packSpace(sb,"+");
            else sb.append(",");
            comma = true;
            sb.append("invisible");
        }
    }
    // Returns point style code. Includes comma.
    private void PointOptionCode(GeoPoint geo, StringBuilder sb){
        PointOptionCode(geo, sb, geo.getPointSize());
    }
    // Line style code; does not include comma.
    private String LineOptionCode(GeoElement geo,boolean transparency){
        StringBuilder sb = new StringBuilder(); 
        geogebra.common.awt.GColor linecolor = geo.getObjectColor();
        int linethickness = geo.getLineThickness();
        int linestyle = geo.getLineType();

        boolean noPlus = true;
        if (linethickness != EuclidianStyleConstants.DEFAULT_LINE_THICKNESS){
            // first parameter
            noPlus = false;
            sb.append("linewidth(");
            sb.append(format(linethickness/2.0*0.8));
            sb.append(")");
        }
        if (linestyle != EuclidianStyleConstants.DEFAULT_LINE_TYPE){
            if (!noPlus) 
                packSpace(sb,"+");
            else noPlus = false;
            LinestyleCode(linestyle,sb);
        }
        if (!linecolor.equals(GColor.BLACK)){
            if (!noPlus) 
                packSpace(sb,"+");
            else noPlus = false;
            ColorCode(linecolor,sb);
        }
        if (transparency && geo.isFillable() && geo.getAlphaValue() > 0.0f){
            /* TODO: write opacity code?
            if (!noPlus) 
                packSpace("+",sb);
            else noPlus = false;
            sb.append("fillcolor=");
            ColorCode(linecolor,sb);
            sb.append(",fillstyle=solid,opacity=");
            sb.append(geo.getAlphaValue()); 
            */
        }
        if(noPlus)
            return null;
        return new String(sb);
    }
    
    // Append the linestyle to PSTricks code
    private static void LinestyleCode(int linestyle,StringBuilder sb) {
        // note: removed 'pt' from linetype commands, seems to work better. 
        switch(linestyle){
            case EuclidianStyleConstants.LINE_TYPE_DOTTED:
                sb.append("dotted");
            break;
            case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
                sb.append("linetype(\"");
                //int size = resizePt(3);
                int size = 2;
                sb.append(size);
                sb.append(" ");
                sb.append(size);
                sb.append("\")");
            break;
            case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
                sb.append("linetype(\"");
                // size = resizePt(6);
                size = 4;
                sb.append(size);
                sb.append(" ");
                sb.append(size);
                sb.append("\")");
            break;
            case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
                sb.append("linetype(\"");
                //int size1 = resizePt(2);
                //int size2 = resizePt(8);
                //int size3 = resizePt(10);
                int size1 = 0, size2 = 3, size3 = 4;
                sb.append(size1);
                sb.append(" ");
                sb.append(size2);
                sb.append(" ");
                sb.append(size3);
                sb.append(" ");
                sb.append(size2);
                sb.append("\")");
            break;
        }
    }
    
    // Append the name color to StringBuilder sb 
    @Override
	protected void ColorCode(geogebra.common.awt.GColor c, StringBuilder sb){
        int red = c.getRed(),
          green = c.getGreen(),
           blue = c.getBlue();
        if (grayscale){
            String colorname="";
            int grayscale = (red+green+blue)/3;
            c = geogebra.common.factories.AwtFactory.prototype.newColor(grayscale,grayscale,grayscale);
            if (CustomColor.containsKey(c)){
                colorname = CustomColor.get(c).toString();
            }
            else { 
                // Not compact:
                // "pen XXXXXX = rgb(0,0,0); pen YYYYYY = rgb(1,1,1);"
                // Compact:
                // "pen XXXXXX = rgb(0,0,0), YYYYYY = rgb(1,1,1);"
                colorname=createCustomColor(grayscale,grayscale,grayscale);
                if(!compact)
                    codeColors.append("pen ");
                else
                    codeColors.append(", ");
                codeColors.append(colorname);
                packSpace(codeColors,"=");
                codeColors.append("rgb("
                    +format(grayscale/255d)+","
                    +format(grayscale/255d)+","
                    +format(grayscale/255d)+")");
                if(!compact)
                    codeColors.append("; ");
                CustomColor.put(c,colorname);
            }
            if (c.equals(GColor.BLACK))       sb.append("black");
            //else if (c.equals(Color.DARK_GRAY)) sb.append("darkgray");
            else if (c.equals(GColor.GRAY))   sb.append("gray");
            //else if (c.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
            else if (c.equals(GColor.WHITE))  sb.append("white");
            else sb.append(colorname);
        }
        else {
            if (c.equals(GColor.BLACK))       sb.append("black");
            //else if (c.equals(Color.DARK_GRAY)) sb.append("darkgray");
            else if (c.equals(GColor.GRAY))   sb.append("gray");
            //else if (c.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
            else if (c.equals(GColor.WHITE))  sb.append("white");
            else if (c.equals(GColor.RED))    sb.append("red");
            else if (c.equals(GColor.GREEN))  sb.append("green");
            else if (c.equals(GColor.BLUE))   sb.append("blue");
            else if (c.equals(GColor.YELLOW)) sb.append("yellow");
            else {
                String colorname = "";
                if (CustomColor.containsKey(c)){
                    colorname = CustomColor.get(c).toString();
                }
                else {
                    colorname = createCustomColor(red,green,blue);
                    if(!compact)
                        codeColors.append("pen ");
                    else
                        codeColors.append(", ");
                    codeColors.append(colorname);
                    packSpace(codeColors,"=");
                    codeColors.append("rgb("
                        +format(red/255d)+","
                        +format(green/255d)+","
                        +format(blue/255d)+")");
                    if(!compact)
                        codeColors.append("; ");                    
                    CustomColor.put(c,colorname);
                }
                sb.append(colorname);
            }
        }
    }
    
    /** Equivalent to ColorCode, but dampens color based upon opacity. Appends the pen to codeColor.
     * @param c The original color before transparency.
     * @param opacity Double value from 0 to 1, with 0 being completely transparent.
     * @param sb StringBuilder to attach code to.
     */
    protected void ColorLightCode(geogebra.common.awt.GColor c, double opacity, StringBuilder sb){
        // new Color object so that c is not overriden.
        geogebra.common.awt.GColor tempc; 
        int red = c.getRed(),
          green = c.getGreen(),
           blue = c.getBlue();
        red   = (int) (255 * (1-opacity) + red * opacity);
        green = (int) (255 * (1-opacity) + green * opacity);
        blue  = (int) (255 * (1-opacity) + blue * opacity);
        if (grayscale){
            String colorname = "";
            int grayscale = (red+green+blue)/3;
            tempc = geogebra.common.factories.AwtFactory.prototype.newColor(grayscale,grayscale,grayscale);
            if (CustomColor.containsKey(tempc)){
                colorname = CustomColor.get(tempc).toString();
            }
            else {
                colorname = createCustomColor(grayscale,grayscale,grayscale);
                if(!compact)
                    codeColors.append("pen ");
                else
                    codeColors.append(", ");
                codeColors.append(colorname);
                packSpace(codeColors,"=");
                codeColors.append("rgb("
                    +format(grayscale/255d)+","
                    +format(grayscale/255d)+","
                    +format(grayscale/255d)+")");
                if(!compact)
                    codeColors.append("; ");
                CustomColor.put(tempc,colorname);
            }
            if (tempc.equals(GColor.BLACK))       sb.append("black");
            //else if (tempc.equals(Color.DARK_GRAY)) sb.append("darkgray");
            else if (tempc.equals(GColor.GRAY))   sb.append("gray");
            //else if (tempc.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
            else if (tempc.equals(GColor.WHITE))  sb.append("white");
            else sb.append(colorname);
        }
        else {
            tempc = geogebra.common.factories.AwtFactory.prototype.newColor(red,green,blue);
            if (tempc.equals(GColor.BLACK))       sb.append("black");
            //else if (tempc.equals(Color.DARK_GRAY)) sb.append("darkgray");
            else if (tempc.equals(GColor.GRAY))   sb.append("gray");
            //else if (tempc.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
            else if (tempc.equals(GColor.WHITE))  sb.append("white");
            else if (tempc.equals(GColor.RED))    sb.append("red");
            else if (tempc.equals(GColor.GREEN))  sb.append("green");
            else if (tempc.equals(GColor.BLUE))   sb.append("blue");
            else if (tempc.equals(GColor.YELLOW)) sb.append("yellow");
            else {
                String colorname = "";
                if (CustomColor.containsKey(tempc)){
                    colorname = CustomColor.get(tempc).toString();
                }
                else {
                    colorname = createCustomColor(red,green,blue);
                    if(!compact)
                        codeColors.append("pen ");
                    else
                        codeColors.append(", ");
                    codeColors.append(colorname);
                    packSpace(codeColors,"=");
                    codeColors.append("rgb("
                        +format(red/255d)+","
                        +format(green/255d)+","
                        +format(blue/255d)+")");
                    if(!compact)
                        codeColors.append("; ");
                    CustomColor.put(tempc,colorname);
                }
                sb.append(colorname);
            }
        }
    }
    
    /** Returns the LaTeX color command; \color[rgb](XX,YY,ZZ). Does not create a new pen. 
     * @param c Desired Color object.
     * @param sb Code to add the command to. 
     */
    // Adds LaTeX: 
    protected void ColorCode2(GColor c,StringBuilder sb){
        int red=c.getRed(), green=c.getGreen(), blue=c.getBlue();
        if (grayscale){
            int grayscale = (red+green+blue)/3;
            c = geogebra.common.factories.AwtFactory.prototype.newColor(grayscale,grayscale,grayscale);
            sb.append("\\color[rgb]{"
                +format(grayscale/255d)+","
                +format(grayscale/255d)+","
                +format(grayscale/255d)+"}");
            if (c.equals(GColor.BLACK))       sb.append("black");
            else if (c.equals(GColor.GRAY))   sb.append("gray");
            else if (c.equals(GColor.WHITE))  sb.append("white");
        }
        else {
            if (c.equals(GColor.BLACK))       sb.append("black");
            else if (c.equals(GColor.GRAY))   sb.append("gray");
            else if (c.equals(GColor.WHITE))  sb.append("white");
            else if (c.equals(GColor.RED))    sb.append("red");
            else if (c.equals(GColor.GREEN))  sb.append("green");
            else if (c.equals(GColor.BLUE))   sb.append("blue");
            else if (c.equals(GColor.YELLOW)) sb.append("yellow");
            else {
                sb.append("\\color[rgb]{"
                    +format(red/255d)  +","
                    +format(green/255d)+","
                    +format(blue/255d) +"}");
            }
        }
    }
/*  // Resize text Keep the ratio between font size and picture height
    private String resizeFont(int fontSize){
        int latexFont=frame.getFontSize();
        double height_geogebra=euclidianView.getHeight()/30;
        double height_latex=frame.getLatexHeight();
        double ratio=height_latex/height_geogebra;
        int theoric_size=(int)Math.round(ratio*fontSize);
        String st=null;
        switch(latexFont){
            case 10:
                if (theoric_size<=5) st="\\tiny{";
                else if (theoric_size<=7) st="\\scriptsize{";
                else if (theoric_size<=8) st="\\footnotesize{";
                else if (theoric_size<=9) st="\\small{";
                else if (theoric_size<=10) ;
                else if (theoric_size<=12) st="\\large{";
                else if (theoric_size<=14) st="\\Large{";
                else if (theoric_size<=17) st="\\LARGE{";
                else if (theoric_size<=20) st="\\huge{";
                else  st="\\Huge{";
            break;
            case 11:
                if (theoric_size<=6) st="\\tiny{";
                else if (theoric_size<=8) st="\\scriptsize{";
                else if (theoric_size<=9) st="\\footnotesize{";
                else if (theoric_size<=10) st="\\small{";
                else if (theoric_size<=11) ;
                else if (theoric_size<=12) st="\\large{";
                else if (theoric_size<=14) st="\\Large{";
                else if (theoric_size<=17) st="\\LARGE{";
                else if (theoric_size<=20) st="\\huge{";
                else  st="\\Huge{";
            break;
            case 12:
                if (theoric_size<=6) st="\\tiny{";
                else if (theoric_size<=8) st="\\scriptsize{";
                else if (theoric_size<=10) st="\\footnotesize{";
                else if (theoric_size<=11) st="\\small{";
                else if (theoric_size<=12) ;
                else if (theoric_size<=14) st="\\large{";
                else if (theoric_size<=17) st="\\Large{";
                else if (theoric_size<=20) st="\\LARGE{";
                else if (theoric_size<=25) st="\\huge{";
                else  st="\\Huge{";
            break;
        }
        return st;
    }*/
//  private void defineTransparency(){} 
    
    private void addText(String st,boolean isLatex,int style){
        if (isLatex) code.append("$");
        if (isLatex && st.charAt(0) == '$') st = st.substring(1);
        
        // use packages 
        if (isLatex) {
            /* too many commands to check, here's a partial list of more common ones:
            \begin \text \substack
            \tfrac \dfrac \cfrac
            \iint \iiint \iiiint
            \boldsymbol \pmb
            \dots \dddot \ddddot
            */
            if(st.indexOf("\\") != -1)
                usepackage.add("amsmath");
            if(st.indexOf("\\mathbb") != -1 || st.indexOf("\\mathfrak") != -1)
                usepackage.add("amssymb");
            if(st.indexOf("\\mathscr") != -1)
                usepackage.add("mathrsfs");
        }
        
        // Convert Unicode symbols
        if (isLatex) 
            st = convertUnicodeToLatex(st);
        else {
            st = convertUnicodeToText(st);
            // Strip dollar signs. Questionable! TODO
            st = st.replaceAll("\\$", "dollar ");
            // Replace all backslash symbol with \textbackslash, except for newlines
            st = st.replaceAll("\\\\", "\\\\textbackslash ")
                   .replaceAll("\\\\textbackslash \\\\textbackslash ", "\\\\\\\\ ");
        }
        switch(style){
            case 1:
                if (isLatex) code.append("\\mathbf{");
                else code.append("\\textbf{");
            break;
            case 2:
                if (isLatex) code.append("\\mathit{");
                else code.append("\\textit{");
            break;
            case 3:
                if (isLatex) code.append("\\mathit{\\mathbf{");
                else code.append("\\textit{\\textbf{");
            break;
        }
        /*if (!geocolor.equals(Color.BLACK)){
            ColorCode2(geocolor,code);
            code.append("{");
        } // Colors moved to drawText()

        if (size!=app.getFontSize()) {
            String formatFont=resizeFont(size);
            if (null!=formatFont) code.append(formatFont);
        }*/
        
        // strip final '$'
        code.append(st.substring(0,st.length() - 1));
        if(!isLatex || st.charAt(st.length() - 1) != '$')
            code.append(st.charAt(st.length() - 1));
        
        // if (size!=app.getFontSize()) code.append("}");
        // if (!geocolor.equals(Color.BLACK)) code.append("}");
        
        switch(style){
            case 1:
            case 2:
                code.append("}");
                break;
            case 3:
                code.append("}}");
                break;
        }
        if (isLatex) code.append("$");
    }
    
    /** Append spaces between list s to code if not in compact mode.
     * @param s A string which can have spaces around it.
     */
    protected void packSpaceBetween(String... s){
        packSpaceBetween(code, s);
    }
    /** Append spaces between list s to sb if not in compact mode.
     * @param sb The StringBuilder to which s is attached.
     * @param s A string which can have spaces around it.
     */
    protected void packSpaceBetween(StringBuilder sb, String... s){
        sb.append(s[0]);
        for(int i = 1; i < s.length; i++) {
            if(!compact)
                sb.append(" " + s[i]);
            else
                sb.append(s[i]);
        } 
    }
    /** Append spaces after s to code if not in compact mode.
     * @param s A string which can have spaces around it.
     */
    protected void packSpaceAfter(String... s){
        packSpaceAfter(code, s);
    }
    /** Append spaces after s to sb if not in compact mode.
     * @param sb The StringBuilder to which s is attached.
     * @param s A string which can have spaces around it.
     */
    protected void packSpaceAfter(StringBuilder sb, String... s){
        packSpaceBetween(sb, s);
        if(!compact)
            sb.append(" ");
    }
    /** Append space around s to code if not in compact mode.
     * @param s A string which can have spaces around it.
     */
    protected void packSpace(String... s){
        packSpace(code, s);
    }
    /** Append spaces about s to sb if not in compact mode.
     * @param sb The StringBuilder to which s is attached.
     * @param s A string which can have spaces around it.
     */
    protected void packSpace(StringBuilder sb, String... s){    
        if(!compact)
            sb.append(" ");
        packSpaceAfter(sb, s);
    }
    
    /** Default version of startDraw, appends the start of a draw() command to StringBuilder code.
     * 
     */
    protected void startDraw(){
        startDraw(code);
    }
    
    /** Appends the opening of a draw() command to sb.
     * @param sb Code to attach to. 
     */
    protected void startDraw(StringBuilder sb){
        if(!compact)
            sb.append("\n");
        if(compactcse5)
            sb.append("D(");
        else
            sb.append("draw(");
    }
    /** Appends line style code to end of StringBuilder code. 
     * @param geo contains line style code. 
     */
    protected void endDraw(GeoElement geo){
        endDraw(geo, code);
    }

    /** Appends line style code to end of StringBuilder code. 
     * @param geo contains line style code. 
     * @param sb code to attach to.
     */
    protected void endDraw(GeoElement geo, StringBuilder sb){
        if(LineOptionCode(geo,true) != null) {
            packSpaceAfter(sb, ",");
            sb.append(LineOptionCode(geo,true));
        }
        sb.append("); ");
    }
        
    /** Begins an object drawn by the filldraw() command.
     * @param sb StringBuilder to which code added.
     */
    protected void startTransparentFill(StringBuilder sb){
        if(!compact)
            sb.append("\n");
        if(fillType != ExportFrame.FILL_NONE) // filldraw
            sb.append("filldraw(");
        else if(compactcse5) // normal draw
            sb.append("D(");
        else
            sb.append("draw(");
    }
    
    /** Closes an object drawn by the filldraw() command.
     * @param geo Object that can be filled.
     * @param sb StringBuilder to which code added.
     */
    protected void endTransparentFill(GeoElement geo, StringBuilder sb){
        // transparent fill options
        if(fillType == ExportFrame.FILL_OPAQUE) {
            packSpaceAfter(sb, ",");
            if(geo.getAlphaValue() >= 0.9) 
                ColorCode(geo.getObjectColor(),sb);
            else
                sb.append("invisible");
        }
        // use opacity(alpha value) pen
        else if(fillType == ExportFrame.FILL_OPACITY_PEN) {
            packSpaceAfter(sb, ",");
            ColorCode(geo.getObjectColor(),sb);
            packSpace(sb,"+");
            sb.append("opacity(");
            sb.append(geo.getAlphaValue());
            sb.append(")");
        }
        else if(fillType == ExportFrame.FILL_LAYER) {
            packSpaceAfter(sb, ",");
            ColorLightCode(geo.getObjectColor(),geo.getAlphaValue(),sb);        
        }
        if(LineOptionCode(geo,true) != null) {
            packSpaceAfter(sb, ",");
            sb.append(LineOptionCode(geo,true));
        }
        sb.append("); ");
    }
    /** For use with drawSpecialPoint() function, appends dot styles
     * @param c
     */
    protected void endPoint(geogebra.common.awt.GColor c) {
        if (!c.equals(GColor.BLACK) && dotColors){
            code.append(",");
            if(!compact) code.append(" ");
            ColorCode(c,code);
        }
        code.append("); ");
    }
    /** Adds a point in the format "(s1,s2)" to sb.
     * @param s1 format(x-coordinate)
     * @param s2 format(y-coordinate)
     * @param sb StringBuilder object to append code to.
     */
    protected void addPoint(String s1, String s2, StringBuilder sb){
        String pairString = "(" + s1 + "," + s2 + ")";
        if(pairName && pairNameTable.containsKey(pairString)) 
            sb.append(pairNameTable.get(pairString));
            // retrieves point name from codePointDecl
            // using string manipulations, unsafe
            // int locPair = codePointDecl.indexOf("(" + s1 + "," + s2 + ")");
            // if(locPair != -1 && compact) {        
                // String name = codePointDecl.substring(0,locPair); 
                // int locNameStart = name.lastIndexOf(" ")+1;
                // int locNameEnd = name.lastIndexOf("=");
                // name = codePointDecl.substring(locNameStart,locNameEnd);
                // sb.append(name);
                // return;
            // }
            // else {
                // String name = codePointDecl.substring(0,locPair); // temporary re-use
                // int locNameStart = Math.max(name.lastIndexOf(", ")+2, name.lastIndexOf("pair ")+5);
                // int locNameEnd = name.lastIndexOf("=");
                // name = codePointDecl.substring(locNameStart,locNameEnd);
                // sb.append(name);
            //    return;
            // }
        else
            sb.append(pairString);
    }
    
    /** Adds a point in the format "(s1,s2)" to sb.
     * @param x real value of x-coordinate
     * @param y real value of y-coordinate
     * @param sb StringBuilder object to append code to.
     */
    protected void addPoint(double x, double y, StringBuilder sb){
        addPoint(format(x), format(y), sb);
    }
    
    /** Initializes a Hash Map mapping unicode expressions with plain text equivalents. 
     * Reads from file at directory geogebra/export/pstricks/unicodetex.
     * 
     */
    protected void initUnicodeTextTable(){ // TODO file path issue
        // read unicode symbols from unicodetex.txt
        try {
            BufferedReader br = new BufferedReader(new FileReader("geogebra/export/pstricks/unicodetex"));
            String st; 
            
            while ((st = br.readLine()) != null) {
                int indexTab = st.indexOf("\t");
                // file format:
                // \ uXXXX \t plaintext
                unicodeTable.put(st.substring(0,indexTab), 
                                 st.substring(indexTab+1,st.length()));
            }
        } catch (FileNotFoundException e) { 
            codePreamble.insert(0,"/* File unicodetex not found. */\n\n");
            e.printStackTrace();
        } catch (IOException e) {
            codePreamble.insert(0,"/* IO error. */\n\n");
            e.printStackTrace();     
        }
    }
    
    /** Converts unicode expressions ("\u03c0") to plain text ("pi").
     * @param sb StringBuilder with code.
     * @return Updated StringBuilder;
     */
    protected StringBuilder convertUnicodeToText(StringBuilder sb){
        // import unicode;
        String tempc = sb.toString();
        tempc = convertUnicodeToText(tempc);
        // override sb with tempc
        sb.delete(0, sb.length());
        sb.append(tempc);
        return sb;
    }
    
    /** Converts unicode expressions ("\u03c0") to plain text ("pi").
     * @param s Text to convert unicode symbols to text. Is not modified.
     * @return Converted string.
     */
    protected String convertUnicodeToText(String s){
        // import unicode;
        String s1 = new String(s);
        Iterator<String> it = unicodeTable.keySet().iterator();
        while(it.hasNext()) {
            String skey = it.next();
            s1 = s1.replaceAll(skey, unicodeTable.get(skey)+" ");
        }
        return s1.replaceAll("\u00b0", "o ")    // degree symbol
                 .replaceAll("\u212f", "e ")
                 .replaceAll("\u00b2", "2 ")
                 .replaceAll("\u00b3", "3 ")
                 .replaceAll("pi \\)",  "pi\\)");   // eliminate unsightly spaces
    }
    
    /** Converts unicode expressions ("\u03c0") to LaTeX expressions ("\pi").
     * @param s Text to convert unicode symbols to LaTeX. Is not modified.
     * @return Converted string.
     */
    protected String convertUnicodeToLatex(String s){
        // import unicode;
        String s1 = new String(s);
        Iterator<String> it = unicodeTable.keySet().iterator();
        // look up unicodeTable conversions and replace with LaTeX commands
        while(it.hasNext()) {
            String skey = it.next();
            s1 = s1.replaceAll(skey, "\\\\"+unicodeTable.get(skey)+" ");
        }
        
        // strip dollar signs
        /* int locDollar = 0;
        while((locDollar = s1.indexOf('$',locDollar+1)) != -1) {
            if(locDollar != 0 && locDollar != s1.length() && s1.charAt(locDollar-1) != '\\')
                s1 = s1.substring(0,locDollar) + "\\" + s1.substring(locDollar);
        } */
        
        StringBuilder sb = new StringBuilder();
        // ignore first and last characters
        // TODO check if odd number of dollar signs? No catch-all fix ..
        sb.append(s1.charAt(0));
        for(int i = 1; i < s1.length() - 1; i++) {
            if(s1.charAt(i-1) == '\\' && (i == 1 || s1.charAt(i-2) != '\\')) {
                sb.append(s1.charAt(i));
                continue;
            }
            else if(s1.charAt(i) == '$') 
                sb.append("\\$");
            else
                sb.append(s1.charAt(i));
        }
        if(s1.length() > 1)
            sb.append(s1.charAt(s1.length() - 1));
        s1 = sb.toString(); 
        
        return s1.replaceAll("\u00b0", "^\\\\circ")
                 .replaceAll("\u212f", " e")
                 .replaceAll("\u00b2", "^2")
                 .replaceAll("\u00b3", "^3")
                 .replaceAll("\\\\questeq", "\\\\stackrel{?}{=}");
    }
    
    /** Formats a function string.
     * @param s Code containing function.
     * @return Parsed function string compatible with programming languages.
     */
    protected String parseFunction(String s){
        // Unicode?
        return killSpace(StringUtil.toLaTeXString(s,true));
    }
    
/* Rewrite the function: TODO
 * Kill spaces
 * Add character * when needed: 2  x +3 ----> 2*x+3
 * Rename several functions:
           log(x)  ---> ln(x)
           ceil(x) ---> ceiling(x)
           exp(x)  ---> 2.71828^(x)
 */   
    private static String killSpace(String name){
        StringBuilder sb = new StringBuilder();
        boolean operand = false;
        boolean space = false;
        for (int i = 0; i < name.length(); i++){
            char c = name.charAt(i);
            if ("*/+-".indexOf(c) != -1){
                sb.append(c);
                operand = true;
                space = false;
            }
            else if (c == ' ') {
                if (!operand) space = true;
                else {
                    space = false;
                    operand = false;
                }
            }
            else {
                if (space) sb.append("*");
                sb.append(c);
                space = false;
                operand = false;
            }
        }

        // following needs cleanup
        // rename functions log, ceil and exp
        renameFunc(sb,"\\\\pi","pi");
        renameFunc(sb,"EXP(","exp(");
        renameFunc(sb,"ln(","log(");
        // integers
        renameFunc(sb,"ceiling(","ceil(");
        renameFunc(sb,"CEILING(","ceil(");
        renameFunc(sb,"FLOOR(","floor(");
        // de-capitalize trigonometric/hyperbolics
        renameFunc(sb,"SIN(","sin(");
        renameFunc(sb,"COS(","cos(");
        renameFunc(sb,"TAN(","tan(");
        renameFunc(sb,"ASIN(","asin(");
        renameFunc(sb,"ACOS(","acos(");
        renameFunc(sb,"ATAN(","atan(");
        renameFunc(sb,"SINH(","sinh(");
        renameFunc(sb,"COSH(","cosh(");
        renameFunc(sb,"TANH(","tanh(");
        renameFunc(sb,"ASINH(","asinh(");
        renameFunc(sb,"ACOSH(","acosh(");
        renameFunc(sb,"ATANH(","atanh(");

        // for exponential in new Geogebra version.
        renameFunc(sb,Unicode.EULER_STRING,"2.718"); /*2.718281828*/

        // temporary code: may be redundant, fail-safe
        // upper letter greek symbols
        renameFunc(sb,"\u0393","Gamma");
        renameFunc(sb,"\u0394","Delta");
        renameFunc(sb,"\u0398","Theta");
        renameFunc(sb,"\u039b","Lambda");
        renameFunc(sb,"\u039e","Xi");
        renameFunc(sb,"\u03a0","Pi");
        renameFunc(sb,"\u03a3","Sigma");
        renameFunc(sb,"\u03a6","Phi");
        renameFunc(sb,"\u03a8","Psi");
        renameFunc(sb,"\u03a9","Omega");

        // lower letter greek symbols
        renameFunc(sb,"\u03b1","alpha");
        renameFunc(sb,"\u03b2","beta");
        renameFunc(sb,"\u03b3","gamma");
        renameFunc(sb,"\u03b4","delta");
        renameFunc(sb,"\u03b5","epsilon");
        renameFunc(sb,"\u03b6","zeta");
        renameFunc(sb,"\u03b7","eta");
        renameFunc(sb,"\u03b8","theta");
        renameFunc(sb,"\u03b9","iota");
        renameFunc(sb,"\u03ba","kappa");
        renameFunc(sb,"\u03bb","lambda");
        renameFunc(sb,"\u03bc","mu");
        renameFunc(sb,"\u03be","xi");
        renameFunc(sb,"\u03c0","pi");
        renameFunc(sb,"\u03c1","rho");
        renameFunc(sb,"\u03c2","varsigma");
        renameFunc(sb,"\u03c3","sigma");
        renameFunc(sb,"\u03c4","tau");
        renameFunc(sb,"\u03c5","upsilon");
        renameFunc(sb,"\u03c6","varphi");
        renameFunc(sb,"\u03c7","chi");
        renameFunc(sb,"\u03c8","psi");
        renameFunc(sb,"\u03c9","omega");

        // remove greek letter escapes
        String greekalpha[] = {"alpha","beta","gamma","delta","epsilon","zeta","eta","theta",
                "iota","kappa","lambda","mu","xi","pi","rho","varsigma","sigma","tau",
                "upsilon","varphi","chi","psi","omega"};
        for(int i = 0; i < greekalpha.length; i++) {
            renameFunc(sb,"\\"+greekalpha[i],greekalpha[i]); // lower case 
            String temps = Character.toString(Character.toUpperCase(greekalpha[i].charAt(0)))
                         + greekalpha[i].substring(1);
            renameFunc(sb,"\\"+temps,temps); // upper case
        }

        return new String(sb);
    }    
    
    @Override
	protected StringTemplate getStringTemplate(){
    	return StringTemplate.get(StringType.PSTRICKS);
    }
}