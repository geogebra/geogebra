package geogebra.common.kernel.commands;

import java.util.HashMap;
import java.util.Set;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MacroInterface;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;


public abstract class AbstractCommandDispatcher {
	/** kernel **/
	protected AbstractKernel kernel;
    private Construction cons;
    private AbstractApplication app;
    
    private boolean isCasActive = false;
    
    /** stores public (String name, CommandProcessor cmdProc) pairs
     * 
     * NB: Do not put CAS-specific commands in this table! If you ever want to,
     * call Markus, so he can give you one million reasons why this is a
     * terribly bad idea!
     **/   
    protected HashMap<String,CommandProcessor> cmdTable;
    
    /**
     * Same info as cmdTable, but separated for each command type.
     * Used only in {@link geogebra.gui.inputbar.InputBarHelpPanel}
     */
    protected HashMap<String,CommandProcessor>[] cmdSubTable;
    public static final int TABLE_GEOMETRY = 0;
    public static final int TABLE_ALGEBRA = 1;
    public static final int TABLE_TEXT = 2;
    public static final int TABLE_LOGICAL = 3;
    public static final int TABLE_FUNCTION = 4;
    public static final int TABLE_CONIC = 5;
    public static final int TABLE_LIST = 6;
    public static final int TABLE_VECTOR = 7;
    public static final int TABLE_TRANSFORMATION = 8;
    public static final int TABLE_CHARTS = 9;
    public static final int TABLE_STATISTICS = 10;
    public static final int TABLE_PROBABILITY = 11;
    public static final int TABLE_SPREADSHEET = 12;
    public static final int TABLE_SCRIPTING = 13;
    public static final int TABLE_DISCRETE_MATH = 14;
    public static final int TABLE_GEOGEBRA = 15;
    public static final int TABLE_OPTIMIZATION = 16;
    public static final int TABLE_ENGLISH = 17;
    
    public static final int TABLE_CAS=18;   
    private int tableCount = GeoGebraConstants.CAS_VIEW_ENABLED ? 19 : 18;
    
    
    public String getSubCommandSetName(int index){
    	switch (index) {
    	case TABLE_GEOMETRY: return app.getMenu("Type.Geometry");
    	case TABLE_ALGEBRA: return app.getMenu("Type.Algebra");
    	case TABLE_TEXT: return app.getMenu("Type.Text");
    	case TABLE_LOGICAL: return app.getMenu("Type.Logic");
    	case TABLE_FUNCTION: return app.getMenu("Type.FunctionsAndCalculus");
    	case TABLE_CONIC: return app.getMenu("Type.Conic");
    	case TABLE_LIST: return app.getMenu("Type.List");
    	case TABLE_VECTOR:return app.getMenu("Type.VectorAndMatrix");
    	case TABLE_TRANSFORMATION: return app.getMenu("Type.Transformation");
    	case TABLE_CHARTS: return app.getMenu("Type.Chart");
    	case TABLE_STATISTICS: return app.getMenu("Type.Statistics");
    	case TABLE_PROBABILITY: return app.getMenu("Type.Probability");
    	case TABLE_SPREADSHEET: return app.getMenu("Type.Spreadsheet");
    	case TABLE_SCRIPTING: return app.getMenu("Type.Scripting");
    	case TABLE_DISCRETE_MATH: return app.getMenu("Type.DiscreteMath");
    	case TABLE_GEOGEBRA: return app.getMenu("Type.GeoGebra");
    	case TABLE_OPTIMIZATION: return app.getMenu("Type.OptimizationCommands");
    	case TABLE_CAS: return app.getMenu("Type.CAS");
    	// TABLE_ENGLISH:
    	default: return null;
    	}
    }

    public static enum Commands {
    	// Please put each command to its appropriate place only!
    	// Please do not change the first command in a subtable,
    	// only change it if you change it in the initCmdTable as well!
    	// Subtables are separated by comment lines here. 

       	//=================================================================
       	// Algebra & Numbers
    	//=============================================================
       	Mod, Div, Min, Max,
       	LCM, GCD, Expand, Factor,
       	Simplify, PrimeFactors, CompleteSquare,

      	//=================================================================
      	// Geometry
    	//=============================================================
       	Line, Ray, AngularBisector, OrthogonalLine,
       	Tangent, Segment, Slope, Angle,
       	Direction, Point, Midpoint, LineBisector,
       	Intersect, IntersectRegion, Distance, Length,
       	Radius, CircleArc, Arc, Sector,
       	CircleSector, CircumcircleSector, CircumcircleArc, Polygon,
       	RigidPolygon, Area, Union, Circumference,
       	Perimeter, Locus, Centroid, TriangleCenter, Barycenter, Trilinear, TriangleCubic, 
       	TriangleCurve,Vertex, PolyLine, PointIn, AffineRatio,
       	CrossRatio, ClosestPoint,

      	//=============================================================
      	// text
    	//=============================================================
       	Text, LaTeX, LetterToUnicode, TextToUnicode,
       	UnicodeToText, UnicodeToLetter, FractionText, SurdText,
       	TableText, VerticalText, RotateText, Ordinal,

      	//=============================================================
      	// logical	
    	//=============================================================
       	If, CountIf, IsInteger, KeepIf,
       	Relation, Defined, IsInRegion,

    	//=============================================================
    	// functions & calculus
    	//=============================================================
       	Root, Roots, TurningPoint, Polynomial,
       	Function, Extremum, CurveCartesian, Derivative,
       	Integral, IntegralBetween, LowerSum, LeftSum,
       	RectangleSum, TaylorSeries, UpperSum, TrapezoidalSum,
       	Limit, LimitBelow, LimitAbove, Factors,
       	Degree, Coefficients, PartialFractions, Numerator,
       	Denominator, ComplexRoot, SolveODE, Iteration,
       	PathParameter, Asymptote, CurvatureVector, Curvature,
       	OsculatingCircle, IterationList, RootList,
       	ImplicitCurve,

    	//=============================================================
    	// conics
    	//=============================================================
       	Ellipse, Hyperbola, SecondAxisLength, SecondAxis,
       	Directrix, Diameter, Conic, FirstAxis,
       	Circle, Incircle, Semicircle, FirstAxisLength,
       	Parabola, Focus, Parameter,
       	Center, Polar, Excentricity, Eccentricity,
       	Axes,
       	
    	//=============================================================
    	// lists
    	//=============================================================
       	Sort, First, Last, Take,
       	RemoveUndefined, Reverse, Element, IndexOf,
       	Append, Join, Flatten, Insert,
       	Sequence, SelectedElement, SelectedIndex, RandomElement,
       	Product, Frequency, Unique, Classes,
       	Zip, Intersection,
       	PointList, OrdinalRank, TiedRank,
       	
    	//=============================================================
    	// charts
    	//=============================================================	
       	BarChart, BoxPlot, Histogram, HistogramRight,
       	DotPlot, StemPlot, ResidualPlot, FrequencyPolygon,
       	NormalQuantilePlot, FrequencyTable,
       	
    	//=============================================================
    	// statistics
    	//=============================================================
       	Sum, Mean, Variance, SD,
       	SampleVariance, SampleSD, Median, Q1,
       	Q3, Mode, SigmaXX, SigmaXY,
       	SigmaYY, Covariance, SXY, SXX,
       	SYY, MeanX, MeanY, PMCC,
       	SampleSDX, SampleSDY, SDX, SDY,
       	FitLineY, FitLineX, FitPoly, FitExp,
       	FitLog, FitPow, Fit, FitGrowth,
       	FitSin, FitLogistic, SumSquaredErrors, RSquare,
       	Sample, Shuffle,
       	Spearman, TTest,
       	TTestPaired, TTest2, TMeanEstimate, TMean2Estimate,
       	ANOVA, Percentile, GeometricMean, HarmonicMean,
       	RootMeanSquare,
       	
    	//=============================================================
    	// probability
    	//=============================================================
       	Random, RandomNormal, RandomUniform, RandomBinomial,
       	RandomPoisson, Normal, LogNormal, Logistic,
       	InverseNormal, Binomial, BinomialDist, Bernoulli,
       	InverseBinomial, TDistribution, InverseTDistribution, FDistribution,
       	InverseFDistribution, Gamma, InverseGamma, Cauchy,
       	InverseCauchy, ChiSquared, InverseChiSquared, Exponential,
       	InverseExponential, HyperGeometric, InverseHyperGeometric, Pascal,
       	InversePascal, Poisson, InversePoisson, Weibull,
       	InverseWeibull, Zipf, InverseZipf, Triangular,
       	Uniform, Erlang,
       	
    	//=============================================================
    	// vector & matrix
    	//=============================================================
       	ApplyMatrix, UnitVector, Vector, UnitOrthogonalVector,
       	OrthogonalVector, Invert, Transpose, ReducedRowEchelonForm,
       	Determinant, Identity,
       	
    	//=============================================================
    	// transformations
    	//=============================================================
       	Mirror, Dilate, Rotate, Translate,
       	Shear, Stretch,
       	
    	//=============================================================
    	// spreadsheet
    	//=============================================================
       	CellRange, Row, Column, ColumnName,
       	FillRow, FillColumn, FillCells, Cell,
       	
      	//=============================================================	
      	// scripting
    	//=============================================================
       	CopyFreeObject, SetColor, SetBackgroundColor, SetDynamicColor,
       	SetConditionToShowObject, SetFilling, SetLineThickness, SetLineStyle,
       	SetPointStyle, SetPointSize, SetFixed, Rename,
       	HideLayer, ShowLayer, SetCoords, Pan,
       	ZoomIn, ZoomOut, SetActiveView, SelectObjects,
       	SetLayer, SetCaption, SetLabelMode, SetTooltipMode,
       	UpdateConstruction, SetValue, PlaySound, ParseToNumber,
       	ParseToFunction, StartAnimation, Delete, Slider,
       	Checkbox, Textfield, Button, Execute,
       	GetTime, ShowLabel, SetAxesRatio, SetVisibleInView,
       	
    	//=============================================================	
      	// discrete math
    	//=============================================================
       	Voronoi, Hull, ConvexHull, MinimumSpanningTree,
       	DelauneyTriangulation, TravelingSalesman, ShortestDistance,
       	
    	//=================================================================
      	// GeoGebra
    	//=============================================================
       	Corner, AxisStepX, AxisStepY, ConstructionStep,
       	Object, Name, SlowPlot, ToolImage, BarCode,
       	DynamicCoordinates,
       	
    	//=================================================================
      	// Other ???
    	//=============================================================
       	Maximize, Minimize,
       	
    	//=================================================================
      	// commands that have been renamed so we want the new name to work
    	// in other languages eg Curve used to be CurveCartesian
    	//=============================================================
       	Curve, FormulaText, IsDefined, ConjugateDiameter,
       	LinearEccentricity, MajorAxis, SemiMajorAxisLength, PerpendicularBisector,
       	PerpendicularLine, PerpendicularVector, MinorAxis, SemiMinorAxisLength,
       	UnitPerpendicularVector, CorrelationCoefficient, FitLine, BinomialCoefficient,
       	RandomBetween
    };

    
    /** stores internal (String name, CommandProcessor cmdProc) pairs*/
    protected HashMap<String,CommandProcessor>internalCmdTable;
    private MacroProcessor macroProc;
    
    /**
     * Creates new command dispatcher
     * @param kernel2 Kernel of current application
     */
    public AbstractCommandDispatcher(AbstractKernel kernel2) {             
    	this.kernel = kernel2;
    	cons = kernel2.getConstruction();  
    	app = kernel2.getApplication();                    
    }
    
    /**
     * Returns a set with all command names available
     * in the GeoGebra input field.
     * @return Set of all command names
     */
    public Set<String> getPublicCommandSet() {
    	if (cmdTable == null) {
    		initCmdTable();
    	}  
    	
    	return cmdTable.keySet();
    }
    
	/**
	 * Returns whether the given command name is supported in GeoGebra.
	 */
	public boolean isCommandAvailable(String cmd) {
		return cmdTable.containsKey(cmd);
	}
    
    
    /**
     * Returns an array of sets containing the command names 
     * found in each table of the array cmdSubTable.
     */
    public Set[] getPublicCommandSubSets() {
    	
    	if (cmdTable == null) {
    		initCmdTable();
    	}  
    	
    	Set[] subSet = new Set[tableCount];  	
        for(int i = 0; i < tableCount; i++){
        	subSet[i] = cmdSubTable[i].keySet();
        }
  
    	return subSet;
    }
    
    
    
    /**
     * @param c Command to be executed
     * @param labelOutput specifies if output GeoElements of this command should get labels
     * @throws MyError in case command execution fails
     * @return Geos created by the command
     */
    final public GeoElement[] processCommand(Command c, boolean labelOutput)
        throws MyError {
    	
    	if (cmdTable == null) {
    		initCmdTable();
    	}    	        
  
        // cmdName
        String cmdName = c.getName();
        CommandProcessor cmdProc;
//        
//        // remove CAS variable prefix from command name if present
//        cmdName = cmdName.replace(ExpressionNode.GGBCAS_VARIABLE_PREFIX, "");
        
        // MACRO: is there a macro with this command name?        
        MacroInterface macro = kernel.getMacro(cmdName);
        if (macro != null) {    
        	c.setMacro(macro);
        	cmdProc = macroProc;
        } 
        // STANDARD CASE
        else {
        	// get CommandProcessor object for command name from command table
        	cmdProc = cmdTable.get(cmdName);    

        	if (cmdProc == null) {
        		cmdProc = commandTableSwitch(cmdName);
        		if (cmdProc != null) {
        			cmdTable.put(cmdName, cmdProc);
        		}
        	}

        	if (cmdProc == null && internalCmdTable != null) {
        		// try internal command
        		cmdProc = internalCmdTable.get(cmdName);
        	}
        }

        if(cmdProc == null)
        	throw new MyError(app, app.getError("UnknownCommand") + " : " + 
        		app.getCommand(c.getName()));
                
        // switch on macro mode to avoid labeling of output if desired
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        if (!labelOutput)
            cons.setSuppressLabelCreation(true);
      
        GeoElement[] ret = null;
        try {            
        	ret = cmdProc.process(c);	                       	        	        
        } 
        catch (MyError e) {
            throw e;
        } catch (Exception e) {        	  
            cons.setSuppressLabelCreation(oldMacroMode);        	  
            e.printStackTrace();
           	throw new MyError(app, app.getError("CAS.GeneralErrorMessage"));
        }
        finally {
        	cons.setSuppressLabelCreation(oldMacroMode);
        }
        
        // remember macro command used:
        // this is needed when a single tool A[] is exported to find
        // all other tools that are needed for A[]
        if (macro != null)
        	cons.addUsedMacro(macro);
        
        return ret;
    }
    
    protected abstract CommandProcessor commandTableSwitch(String cmdName);

	/**
     * Fills the string-command map
     */
    protected void initCmdTable() {    	 
    	macroProc = new MacroProcessor(kernel);    	    	
    	
    	// external commands: visible to users    
    	cmdTable = new HashMap<String,CommandProcessor>(500);
    	
    	cmdSubTable = new HashMap[tableCount];
    	for(int i = 0; i<tableCount; i++)
    		cmdSubTable[i] = new HashMap<String,CommandProcessor>(500);

    	// Here we doesn't instantiate CommandProcessor object as before,
    	// in order to speedup the initial loading of the Application
    	// we instantiate CommandProcessor objects when needed and
    	// store them in this command table afterwards

    	// ... in order to change or add a command,
    	// please change the enum "Commands" and the method
    	// "commandTableSwitch", and if the first command in a subtable
    	// changed, the following switch as well
    	// Arpad Fekete, 2011-09-29

    	for (Commands comm : Commands.values()) {
    		switch (comm) {
    			case Line:
    		    	cmdSubTable[TABLE_ALGEBRA].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Text:
    		    	cmdSubTable[TABLE_GEOMETRY].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case If:
    		    	cmdSubTable[TABLE_TEXT].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Root:
    		      	cmdSubTable[TABLE_LOGICAL].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Ellipse:
    		    	cmdSubTable[TABLE_FUNCTION].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Sort:
    		    	cmdSubTable[TABLE_CONIC].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case BarChart:
    		    	cmdSubTable[TABLE_LIST].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Sum:
    		    	cmdSubTable[TABLE_CHARTS].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Random:
    		    	cmdSubTable[TABLE_STATISTICS].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case ApplyMatrix:
    		    	cmdSubTable[TABLE_PROBABILITY].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Mirror:
    		    	cmdSubTable[TABLE_VECTOR].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case CellRange:
    		    	cmdSubTable[TABLE_TRANSFORMATION].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case CopyFreeObject:
    		      	cmdSubTable[TABLE_SPREADSHEET].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Voronoi:
    		       	cmdSubTable[TABLE_SCRIPTING].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Corner:
    		    	cmdSubTable[TABLE_DISCRETE_MATH].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Maximize:
    		    	cmdSubTable[TABLE_GEOGEBRA].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    			case Curve:
    		    	cmdSubTable[TABLE_OPTIMIZATION].putAll(cmdTable);
    		    	cmdTable.clear();
    		    	break;
    		}
    		cmdTable.put(comm.name(), null);
    	}
    	cmdSubTable[TABLE_ENGLISH].putAll(cmdTable);
    	cmdTable.clear();

    	//=================================================================
      	// Put all of the sub Tables together to create cmdTable
    	
    	for(int i = 0; i < tableCount; i++) {
    		if (i != TABLE_CAS) {
	    		cmdTable.putAll(cmdSubTable[i]);
    		}
    	}
    	
    	//=============================================================	
      	// CAS
    	// do *after* above loop as we must add only those CAS commands without a ggb equivalent
    	//=============================================================
    	
    	if (GeoGebraConstants.CAS_VIEW_ENABLED && app.isUsingFullGui() && isCasActive)
    		initCASCommands();

    }

    /**
     * Loads CAS commands into the cmdSubTable.
     */
    public void initCASCommands() {
    	
    	if (!GeoGebraConstants.CAS_VIEW_ENABLED) return;
    	
    	isCasActive = true;
    	
    	// this method might get called during initialization. In that case
    	// this method will be called again during the normal initCmdTable
    	// since isCasActive is now true.
    	if (cmdTable == null)
    		return;
    	for (String cmd : kernel.getGeoGebraCAS().getCurrentCAS().getAvailableCommandNames()) {
    		
    		// add commands that are in the cas ONLY
    		if (!cmdTable.containsKey(cmd))
    			cmdSubTable[TABLE_CAS].put(cmd, null); 
    	}
		fillInternalCmdTable();
    }

	protected void fillInternalCmdTable(){
		
	}

}
