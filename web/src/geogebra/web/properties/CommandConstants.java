package geogebra.web.properties;

import com.google.gwt.i18n.client.ConstantsWithLookup;


@SuppressWarnings("javadoc")
/**
 * CommandConstants and interface that represents the Command properties file
 * @author Rana
 *
 */
public interface CommandConstants extends ConstantsWithLookup {
	
	@DefaultStringValue("[ <Object>, <Condition> ]")
	String SetConditionToShowObject_Syntax();

	@DefaultStringValue("Insert")
	String Insert();

	@DefaultStringValue("Arc")
	String Arc();

	@DefaultStringValue("ConstructionStep")
	String ConstructionStep();

	@DefaultStringValue("[ <Mean> ]")
	String RandomPoisson_Syntax();

	@DefaultStringValue("[ <Polygon> ]")
	String Centroid_Syntax();

	@DefaultStringValue("[ <Object>, <Number> ]")
	String SetLabelMode_Syntax();

	@DefaultStringValue("[ <Expression> ]")
	String FractionalPart_SyntaxCAS();

	@DefaultStringValue("Rationalize")
	String Rationalize();

	@DefaultStringValue("NRoot")
	String NRoot();

	@DefaultStringValue("[ <Point>, <Radius Number> ]")
	String Sphere_Syntax();

	@DefaultStringValue("[ <Expression> ]\n[ <Expression>, <Variable> ]\n[ <Expression>, <Variable>, <Number> ]")
	String Derivative_SyntaxCAS();

	@DefaultStringValue("CSolutions")
	String CSolutions();

	@DefaultStringValue("Point")
	String Point();

	@DefaultStringValue("[ <Object>, <Vector> ]\n[ <Vector>, <Start Point> ]")
	String Translate_Syntax();

	@DefaultStringValue("[ <Dependent Number>, <Free Number> ]")
	String Minimize_Syntax();

	@DefaultStringValue("[ <Number of Trials>, <Probability of Success>, <Variable Value>, <Boolean Cumulative> ]")
	String BinomialDist_SyntaxCAS();

	@DefaultStringValue("Union")
	String Union();

	@DefaultStringValue("[ <Conic> ]")
	String SecondAxis_Syntax();

	@DefaultStringValue("Rename")
	String Rename();

	@DefaultStringValue("SetColor")
	String SetColor();

	@DefaultStringValue("[ <Min>, <Max> ]")
	String RandomUniform_Syntax();

	@DefaultStringValue("RotateText")
	String RotateText();

	@DefaultStringValue("[ <Object>, \"<Color>\" ]\n[ <Object>, <Red>, <Green>, <Blue> ]")
	String SetBackgroundColor_Syntax();

	@DefaultStringValue("[ <Point>, <Point>, <Point> ]")
	String AffineRatio_Syntax();

	@DefaultStringValue("SampleSDY")
	String SampleSDY();

	@DefaultStringValue("OrdinalRank")
	String OrdinalRank();

	@DefaultStringValue("SampleSDX")
	String SampleSDX();

	@DefaultStringValue("[ <Number> ]")
	String Identity_SyntaxCAS();

	@DefaultStringValue("[ <List of Points> ]\n[ <List of x-Coordinates>, <List of y-Coordinates> ]")
	String SigmaXY_Syntax();

	@DefaultStringValue("[ <Line> ]\n[ <Segment> ]\n[ <Vector> ]")
	String OrthogonalVector_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]")
	String SampleVariance_Syntax();

	@DefaultStringValue("[ <Object>, <Red>, <Green>, <Blue> ]\n[ <Object>, <Red>, <Green>, <Blue>, <Opacity> ]")
	String SetDynamicColor_Syntax();

	@DefaultStringValue("Normal")
	String Normal();

	@DefaultStringValue("[ <Alpha>, <Beta>, <Variable Value> ]")
	String Gamma_SyntaxCAS();

	@DefaultStringValue("[ <List of Numbers> ]")
	String Median_SyntaxCAS();

	@DefaultStringValue("[ <Expression>, <Expression> ]")
	String CommonDenominator_SyntaxCAS();

	@DefaultStringValue("Rotate")
	String Rotate();

	@DefaultStringValue("Barycenter")
	String Barycenter();

	@DefaultStringValue("Zip")
	String Zip();

	@DefaultStringValue("[ <Number of Corner> ]\n[ <Image>, <Number of Corner> ]\n[ <Text>, <Number of Corner> ]\n[ <Graphics View>, <Number of Corner> ]")
	String Corner_Syntax();

	@DefaultStringValue("InverseExponential")
	String InverseExponential();

	@DefaultStringValue("RandomNormal")
	String RandomNormal();

	@DefaultStringValue("[ <Function> ]\n[ <Function>, <Variable> ]")
	String PartialFractions_SyntaxCAS();

	@DefaultStringValue("[ \"<Letter>\" ]")
	String LetterToUnicode_Syntax();

	@DefaultStringValue("[ <Matrix> ]")
	String Transpose_SyntaxCAS();

	@DefaultStringValue("[ <Point>, <Line> ]\n[ <Point>, <Segment> ]\n[ <Point>, <Vector> ]")
	String OrthogonalLine_Syntax();

	@DefaultStringValue("Pascal")
	String Pascal();

	@DefaultStringValue("[ <Number of Successes>, <Probability of Success>, <Variable Value>, <Boolean Cumulative> ]")
	String Pascal_SyntaxCAS();

	@DefaultStringValue("Reflect")
	String Mirror();

	@DefaultStringValue("PathParameter")
	String PathParameter();

	@DefaultStringValue("[ <Polynomial> ]\n[ <Function>, <Initial x-Value> ]\n[ <Function>, <Start x-Value>, <End x-Value> ]")
	String Root_Syntax();

	@DefaultStringValue("[ <List> ]")
	String OrdinalRank_Syntax();

	@DefaultStringValue("Polygon")
	String Polygon();

	@DefaultStringValue("PerpendicularPlane")
	String PerpendicularPlane();

	@DefaultStringValue("RootMeanSquare")
	String RootMeanSquare();

	@DefaultStringValue("[ <Point>, <Point>, <Point>, <Number> ]")
	String TriangleCubic_Syntax();

	@DefaultStringValue("Degree")
	String Degree();

	@DefaultStringValue("[ <List of Numbers> ]")
	String SampleSD_SyntaxCAS();

	@DefaultStringValue("[ <Lower Bound>, <Upper Bound>, <Mode>, x ]\n[ <Lower Bound>, <Upper Bound>, <Mode>, <Variable Value> ]\n[ <Lower Bound>, <Upper Bound>, <Mode>, x, <Boolean Cumulative> ]")
	String Triangular_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String FitExp_SyntaxCAS();

	@DefaultStringValue("[ <Expression> ]")
	String Expand_SyntaxCAS();

	@DefaultStringValue("InputBox")
	String Textfield();

	@DefaultStringValue("[ <Point On Path> ]")
	String PathParameter_Syntax();

	@DefaultStringValue("[ <List> ]\n[ <List>, <Number of Elements> ]")
	String First_SyntaxCAS();

	@DefaultStringValue("TriangleCenter")
	String TriangleCenter();

	@DefaultStringValue("[ <Polynomial> ]\n[ <Number> ]")
	String Factors_SyntaxCAS();

	@DefaultStringValue("Coefficients")
	String Coefficients();

	@DefaultStringValue("[ <List of Points>, <Percentage> ]")
	String Hull_Syntax();

	@DefaultStringValue("[ <Equation> ]\n[ { <Equation> } ]")
	String RightSide_SyntaxCAS();

	@DefaultStringValue("Solve")
	String Solve();

	@DefaultStringValue("[ <Point>, <Point>, <Radius> ]")
	String Cylinder_Syntax();

	@DefaultStringValue("[ <Function>, <Function> ]")
	String Intersect_SyntaxCAS();

	@DefaultStringValue("[ <yOffset>, <yScale>, <List of Raw Data> ]\n[ <yOffset>, <yScale>, <Start Value>, <Q1>, <Median>, <Q3>, <End Value> ]")
	String BoxPlot_Syntax();

	@DefaultStringValue("[ <Line> ]\n[ <Segment> ]\n[ <Vector> ]")
	String UnitOrthogonalVector_Syntax();

	@DefaultStringValue("[ <List of Sample Data 1>, <List of Sample Data 2>, <Tail> ]")
	String TTestPaired_Syntax();

	@DefaultStringValue("TriangleCurve")
	String TriangleCurve();

	@DefaultStringValue("[ <List>, <List>, <Position> ]\n[ <Object>, <List>, <Position> ]")
	String Insert_Syntax();

	@DefaultStringValue("InversePoisson")
	String InversePoisson();

	@DefaultStringValue("TTestPaired")
	String TTestPaired();

	@DefaultStringValue("InverseBinomial")
	String InverseBinomial();

	@DefaultStringValue("[ <Mean>, <Standard Deviation>, <Variable Value> ]")
	String Normal_SyntaxCAS();

	@DefaultStringValue("DynamicCoordinates")
	String DynamicCoordinates();

	@DefaultStringValue("[ <Point>, <Point>, <Point> ]")
	String Incircle_Syntax();

	@DefaultStringValue("[ <Object>, <Number> ]")
	String SetTooltipMode_Syntax();

	@DefaultStringValue("[ <Conic> ]")
	String Circumference_Syntax();

	@DefaultStringValue("[ <List> ]")
	String Unique_SyntaxCAS();

	@DefaultStringValue("[ ]\n[ <Caption> ]\n[ <List> ]\n[ <Caption>, <List> ]")
	String Checkbox_Syntax();

	@DefaultStringValue("[ <Conic> ]")
	String Radius_Syntax();

	@DefaultStringValue("Command")
	String Command();

	@DefaultStringValue("UnicodeToText")
	String UnicodeToText();

	@DefaultStringValue("Substitute")
	String Substitute();

	@DefaultStringValue("[ <Median>, <Scale>, x ]\n[ <Median>, <Scale>, <Variable Value> ]\n[ <Median>, <Scale>, x, <Boolean Cumulative> ]")
	String Cauchy_Syntax();

	@DefaultStringValue("Octahedron")
	String Octahedron();

	@DefaultStringValue("[ <List> ]")
	String Flatten_Syntax();

	@DefaultStringValue("TrigExpand")
	String TrigExpand();

	@DefaultStringValue("[ <List of Class Boundaries>, <List of Heights> ]\n[ <List of Class Boundaries>, <List of Raw Data>, <Boolean Use Density>, <Density Scale Factor (optional)> ]\n[ <Boolean Cumulative>, <List of Class Boundaries>, <List of Raw Data>, <Boolean Use Density>, <Density Scale Factor (optional)> ]")
	String FrequencyPolygon_Syntax();

	@DefaultStringValue("MeanY")
	String MeanY();

	@DefaultStringValue("MeanX")
	String MeanX();

	@DefaultStringValue("[ <Matrix> ]\n[ <Function> ]")
	String Invert_Syntax();

	@DefaultStringValue("TravelingSalesman")
	String TravelingSalesman();

	@DefaultStringValue("FitPoly")
	String FitPoly();

	@DefaultStringValue("Sum")
	String Sum();

	@DefaultStringValue("Angle")
	String Angle();

	@DefaultStringValue("[ <List>, <Size> ]\n[ <List>, <Size>, <With Replacement> ]")
	String Sample_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String FitLog_SyntaxCAS();

	@DefaultStringValue("Parameter")
	String Parameter();

	@DefaultStringValue("[ <List of Numbers> ]")
	String Variance_Syntax();

	@DefaultStringValue("ToPoint")
	String ToPoint();

	@DefaultStringValue("[ <Function>, <Start x-Value>, <End x-Value>, <Number of Rectangles>, <Position for rectangle start> ]")
	String RectangleSum_Syntax();

	@DefaultStringValue("PointList")
	String PointList();

	@DefaultStringValue("[ <Function>, <Start x-Value>, <End x-Value> ]")
	String Roots_Syntax();

	@DefaultStringValue("[ <Expression>, <N> ]")
	String NRoot_SyntaxCAS();

	@DefaultStringValue("Join")
	String Join();

	@DefaultStringValue("Division")
	String Division();

	@DefaultStringValue("Fit")
	String Fit();

	@DefaultStringValue("Icosahedron")
	String Icosahedron();

	@DefaultStringValue("[ <List of Points> ]")
	String MinimumSpanningTree_Syntax();

	@DefaultStringValue("CommonDenominator")
	String CommonDenominator();

	@DefaultStringValue("[ <List of Points> ]")
	String FitPow_SyntaxCAS();

	@DefaultStringValue("Hyperbola")
	String Hyperbola();

	@DefaultStringValue("FrequencyPolygon")
	String FrequencyPolygon();

	@DefaultStringValue("[ <Point>, <x>, <y> ]")
	String SetCoords_Syntax();

	@DefaultStringValue("[ <Degree>, <Minimum for Coefficients>, <Maximum for Coefficients> ]\n[ <Variable>, <Degree>, <Minimum for Coefficients>, <Maximum for Coefficients> ]")
	String RandomPolynomial_SyntaxCAS();

	@DefaultStringValue("[ <Point>, <Number> ]")
	String SetPointStyle_Syntax();

	@DefaultStringValue("[ <Number> ]")
	String DivisorsList_SyntaxCAS();

	@DefaultStringValue("HistogramRight")
	String HistogramRight();

	@DefaultStringValue("SetLabelMode")
	String SetLabelMode();

	@DefaultStringValue("Covariance")
	String Covariance();

	@DefaultStringValue("[ <List of Polynomials> ]\n[ <List of Polynomials>, <List of Variables> ]\n[ <List of Polynomials>, <List of Variables>, <Type of Variable Ordering> ]")
	String Groebner_SyntaxCAS();

	@DefaultStringValue("[ <List of Numbers> ]")
	String HarmonicMean_Syntax();

	@DefaultStringValue("[ <Population Size>, <Number of Successes>, <Sample Size>, <Probability> ]")
	String InverseHyperGeometric_Syntax();

	@DefaultStringValue("[ <Equation> ]\n[ <Equation>, <Variable> ]\n[ <List of Equations>, <List of Variables> ]")
	String CSolve_SyntaxCAS();

	@DefaultStringValue("TrigSimplify")
	String TrigSimplify();

	@DefaultStringValue("[ <Number>, <Number> ]\n[ <Number>, <Number>, <Number> ]")
	String SetAxesRatio_Syntax3D();

	@DefaultStringValue("[ <Point>, <Plane> ]\n[ <Point>, <Point>, <Point> ]")
	String Plane_Syntax();

	@DefaultStringValue("[ <Point>, <Vector>, <Angle> ]\n[ <Point>, <Point>, <Angle> ]\n[ <Point>, <Line>, <Angle> ]")
	String InfiniteCone_Syntax();

	@DefaultStringValue("Groebner")
	String Groebner();

	@DefaultStringValue("FitSin")
	String FitSin();

	@DefaultStringValue("HideLayer")
	String HideLayer();

	@DefaultStringValue("[ <Expression> ]\n[ <Expression>, <Variable> ]")
	String CFactor_SyntaxCAS();

	@DefaultStringValue("RandomPoisson")
	String RandomPoisson();

	@DefaultStringValue("SemiMinorAxisLength")
	String SecondAxisLength();

	@DefaultStringValue("[ <Function> ]\n[ <Function>, <Variable> ]\n[ <Function>, <Start x-Value>, <End x-Value> ]\n[ <Function>, <Variable>, <Start Value>, <End Value> ]")
	String Integral_SyntaxCAS();

	@DefaultStringValue("[ <List of Points> ]")
	String FitSin_SyntaxCAS();

	@DefaultStringValue("[ <Segment> ]\n[ <Conic> ]\n[ <Interval> ]\n[ <Point>, <Point> ]")
	String Midpoint_Syntax();

	@DefaultStringValue("[ <List> ]")
	String RandomElement_SyntaxCAS();

	@DefaultStringValue("Centroid")
	String Centroid();

	@DefaultStringValue("[ <Expression> ]")
	String Decimal_SyntaxCAS();

	@DefaultStringValue("[ <List of Points> ]")
	String TravelingSalesman_Syntax();

	@DefaultStringValue("SetActiveView")
	String SetActiveView();

	@DefaultStringValue("SetLineStyle")
	String SetLineStyle();

	@DefaultStringValue("[ <Point>, <Function> ]\n[ <Point>, <Curve> ]")
	String Curvature_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]")
	String Variance_SyntaxCAS();

	@DefaultStringValue("[ <Object>, <Name> ]")
	String Rename_Syntax();

	@DefaultStringValue("Roots")
	String Roots();

	@DefaultStringValue("[ <Region> ]")
	String PointIn_Syntax();

	@DefaultStringValue("RightSide")
	String RightSide();

	@DefaultStringValue("[ <Number> ]")
	String IsInteger_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String MeanY_Syntax();

	@DefaultStringValue("Tangent")
	String Tangent();

	@DefaultStringValue("[ <Variable> ]")
	String Delete_SyntaxCAS();

	@DefaultStringValue("[ <Degrees of Freedom>, <Probability> ]")
	String InverseTDistribution_Syntax();

	@DefaultStringValue("RandomUniform")
	String RandomUniform();

	@DefaultStringValue("[ \"<Text>\" ]")
	String TextToUnicode_Syntax();

	@DefaultStringValue("[ <Point>, <Point> ]\n[ <Point>, <Length> ]")
	String Segment_Syntax();

	@DefaultStringValue("[ <Point>, <Line> ]")
	String Parabola_Syntax();

	@DefaultStringValue("[ <Mean>, <Scale>, x ]\n[ <Mean>, <Scale>, <Variable Value> ]\n[ <Mean>, <Scale>, x, <Boolean Cumulative> ]")
	String Logistic_Syntax();

	@DefaultStringValue("[ <Mean>, <Scale>, <Probability> ]")
	String InverseLogistic_Syntax();

	@DefaultStringValue("[ <Dividend Number>, <Divisor Number> ]\n[ <Dividend Polynomial>, <Divisor Polynomial> ]")
	String Mod_Syntax();

	@DefaultStringValue("[ <List of Raw Data> ]\n[ <Boolean Cumulative>, <List of Raw Data> ]\n[ <List of Class Boundaries>, <List of Raw Data> ]\n[ <Boolean Cumulative>, <List of Class Boundaries>, <List of Raw Data> ]\n[ <List of Class Boundaries>, <List of Raw Data>, <Use Density>, <Density Scale Factor (optional)> ]\n[ <Boolean Cumulative>, <List of Class Boundaries>, <List of Raw Data>, <Use Density>, <Density Scale Factor (optional)> ]")
	String FrequencyTable_Syntax();

	@DefaultStringValue("MinorAxis")
	String SecondAxis();

	@DefaultStringValue("ParseToNumber")
	String ParseToNumber();

	@DefaultStringValue("[ <Function>, <Value> ]")
	String LimitBelow_Syntax();

	@DefaultStringValue("CountIf")
	String CountIf();

	@DefaultStringValue("[ <Equation> ]\n[ <Equation>, <Variable> ]\n[ <Equation>, <Variable = starting value> ]\n[ <List of Equations>, <List of Variables> ]")
	String NSolutions_SyntaxCAS();

	@DefaultStringValue("NSolve")
	String NSolve();

	@DefaultStringValue("[ <x>, <y> ]")
	String Pan_Syntax();

	@DefaultStringValue("[ <Vector> ]")
	String Dimension_SyntaxCAS();

	@DefaultStringValue("GetTime")
	String GetTime();

	@DefaultStringValue("Cube")
	String Cube();

	@DefaultStringValue("Sector")
	String Sector();

	@DefaultStringValue("[ <List of Numbers> ]")
	String RootMeanSquare_Syntax();

	@DefaultStringValue("[ <List>, <Position of Element> ]\n[ <Matrix>, <Row>, <Column> ]")
	String Element_SyntaxCAS();

	@DefaultStringValue("Column")
	String Column();

	@DefaultStringValue("Maximize")
	String Maximize();

	@DefaultStringValue("Stretch")
	String Stretch();

	@DefaultStringValue("ResidualPlot")
	String ResidualPlot();

	@DefaultStringValue("[ <List of Numbers> ]")
	String GeometricMean_Syntax();

	@DefaultStringValue("Pan")
	String Pan();

	@DefaultStringValue("[ <Condition>, <List> ]")
	String CountIf_Syntax();

	@DefaultStringValue("[ <Point3D>, <Line3D> ]")
	String PerpendicularPlane_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String FitLogistic_Syntax();

	@DefaultStringValue("BinomialDist")
	String BinomialDist();

	@DefaultStringValue("Factor")
	String Factor();

	@DefaultStringValue("UnitVector")
	String UnitVector();

	@DefaultStringValue("Cauchy")
	String Cauchy();

	@DefaultStringValue("PerpendicularBisector")
	String LineBisector();

	@DefaultStringValue("[ <Object>, <Boolean> ]")
	String ShowLabel_Syntax();

	@DefaultStringValue("[ <Function>, <Start x-Value>, <End x-Value>, <Number of Trapezoids> ]")
	String TrapezoidalSum_Syntax();

	@DefaultStringValue("[ <Number> ]")
	String ShowLayer_Syntax();

	@DefaultStringValue("Axes")
	String Axes();

	@DefaultStringValue("[ ]\n[ <Boolean> ]\n[ <Slider or Point>, <Slider or Point>, ... ]\n[ <Slider or Point>, <Slider or Point>, ..., <Boolean> ]")
	String StartAnimation_Syntax();

	@DefaultStringValue("TiedRank")
	String TiedRank();

	@DefaultStringValue("[ <Probability>, <Boolean Cumulative> ]")
	String Bernoulli_Syntax();

	@DefaultStringValue("If")
	String If();

	@DefaultStringValue("IsPrime")
	String IsPrime();

	@DefaultStringValue("ShortestDistance")
	String ShortestDistance();

	@DefaultStringValue("Vector")
	String Vector();

	@DefaultStringValue("[ <Line>, <Radius> ]\n[ <Point>, <Vector>, <Radius> ]\n[ <Point>, <Point>, <Radius> ]")
	String InfiniteCylinder_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]")
	String SD_Syntax();

	@DefaultStringValue("Locus")
	String Locus();

	@DefaultStringValue("[ <List of Numbers> ]")
	String Mode_Syntax();

	@DefaultStringValue("[ <Mean>, <Variable Value>, <Boolean Cumulative> ]")
	String Poisson_SyntaxCAS();

	@DefaultStringValue("[ <List> ]\n[ <Text> ]\n[ <List>, <Number of Elements> ]\n[ <Text>, <Number of Elements> ]")
	String Last_Syntax();

	@DefaultStringValue("[ <List of Points> ]\n[ <List of Numbers>, <List of Numbers> ]")
	String SXY_Syntax();

	@DefaultStringValue("[ <Expression>, <x-Value>, <Order Number> ]\n[ <Expression>, <Variable>, <Variable-Value>, <Order Number> ]")
	String TaylorSeries_SyntaxCAS();

	@DefaultStringValue("[ <List> ]")
	String SelectedElement_Syntax();

	@DefaultStringValue("[ <Point>, <Point> ]")
	String Semicircle_Syntax();

	@DefaultStringValue("[ <List> ]")
	String Reverse_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]")
	String SampleSD_Syntax();

	@DefaultStringValue("[ <Matrix> ]")
	String Transpose_Syntax();

	@DefaultStringValue("[ <List of Points>, <List of Functions> ]\n[ <List of Points>, <Function> ]")
	String Fit_Syntax();

	@DefaultStringValue("Side")
	String QuadricSide();

	@DefaultStringValue("Dilate")
	String Dilate();

	@DefaultStringValue("FitLogistic")
	String FitLogistic();

	@DefaultStringValue("[ <Point Creating Locus Line>, <Point> ]\n[ <Point Creating Locus Line>, <Slider> ]")
	String Locus_Syntax();

	@DefaultStringValue("GCD")
	String GCD();

	@DefaultStringValue("[ <Mean>, <Standard Deviation>, x ]\n[ <Mean>, <Standard Deviation>, <Variable Value> ]\n[ <Mean>, <Standard Deviation>, x, <Boolean Cumulative> ]")
	String LogNormal_Syntax();

	@DefaultStringValue("[ <Mean>, <Standard Deviation>, <Probability> ]")
	String InverseLogNormal_Syntax();

	@DefaultStringValue("[ <Mean>, <Standard Deviation> ]")
	String RandomNormal_Syntax();

	@DefaultStringValue("InverseHyperGeometric")
	String InverseHyperGeometric();

	@DefaultStringValue("Derivative")
	String Derivative();

	@DefaultStringValue("FractionalPart")
	String FractionalPart();

	@DefaultStringValue("[ <Point>, <Radius Number> ]\n[ <Point>, <Segment> ]\n[ <Point>, <Point> ]\n[ <Point>, <Point>, <Point> ]")
	String Circle_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String SDY_Syntax();

	@DefaultStringValue("[ <Mean> ]")
	String RandomPoisson_SyntaxCAS();

	@DefaultStringValue("Expand")
	String Expand();

	@DefaultStringValue("Voronoi")
	String Voronoi();

	@DefaultStringValue("[ <List of Points> ]\n[ <List of Numbers>, <List of Numbers> ]")
	String Spearman_Syntax();

	@DefaultStringValue("[ <List> ]")
	String Shuffle_SyntaxCAS();

	@DefaultStringValue("[ <Text>, <Angle> ]")
	String RotateText_Syntax();

	@DefaultStringValue("Classes")
	String Classes();

	@DefaultStringValue("[ <Point>, <Point>, <Radius> ]")
	String Cone_Syntax();

	@DefaultStringValue("Dodecahedron")
	String Dodecahedron();

	@DefaultStringValue("[ <Expression> ]")
	String IntegerPart_SyntaxCAS();

	@DefaultStringValue("[ <Number> ]")
	String Divisors_SyntaxCAS();

	@DefaultStringValue("[ <Expression> ]")
	String Expand_Syntax();

	@DefaultStringValue("Focus")
	String Focus();

	@DefaultStringValue("LowerSum")
	String LowerSum();

	@DefaultStringValue("[ ]")
	String AxisStepY_Syntax();

	@DefaultStringValue("Frequency")
	String Frequency();

	@DefaultStringValue("[ <Function>, <Start Value>, <Number of Iterations> ]")
	String IterationList_Syntax();

	@DefaultStringValue("Invert")
	String Invert();

	@DefaultStringValue("PrimeFactors")
	String PrimeFactors();

	@DefaultStringValue("Curve")
	String CurveCartesian();

	@DefaultStringValue("SelectedElement")
	String SelectedElement();

	@DefaultStringValue("[ <Function> ]")
	String SlowPlot_Syntax();

	@DefaultStringValue("Pyramid")
	String Pyramid();

	@DefaultStringValue("[ <Dividend Number>, <Divisor Number> ]\n[ <Dividend Polynomial>, <Divisor Polynomial> ]")
	String Mod_SyntaxCAS();

	@DefaultStringValue("RootList")
	String RootList();

	@DefaultStringValue("FitPow")
	String FitPow();

	@DefaultStringValue("FitLog")
	String FitLog();

	@DefaultStringValue("[ <Object>, <View Number 1|2>, <Boolean> ]")
	String SetVisibleInView_Syntax();

	@DefaultStringValue("PerpendicularLine")
	String OrthogonalLine();

	@DefaultStringValue("[ <Number of Elements>, <Exponent>, <Probability> ]")
	String InverseZipf_Syntax();

	@DefaultStringValue("SlowPlot")
	String SlowPlot();

	@DefaultStringValue("BarCode")
	String BarCode();

	@DefaultStringValue("Prove")
	String Prove();
	
	@DefaultStringValue("ProveDetails")
	String ProveDetails();
	
	@DefaultStringValue("AffineRatio")
	String AffineRatio();

	@DefaultStringValue("Percentile")
	String Percentile();

	@DefaultStringValue("Direction")
	String Direction();

	@DefaultStringValue("[ <List of Points> ]")
	String DelauneyTriangulation_Syntax();

	@DefaultStringValue("[ <List>, <Size> ]\n[ <List>, <Size>, <With Replacement> ]")
	String Sample_SyntaxCAS();

	@DefaultStringValue("[ <Polynomial> ]")
	String Factor_Syntax();

	@DefaultStringValue("[ <Conic> ]")
	String Excentricity_Syntax();

	@DefaultStringValue("IsDefined")
	String Defined();

	@DefaultStringValue("[ <List>, <Start Position>, <End Position> ]\n[ <Text>, <Start Position>, <End Position> ]")
	String Take_Syntax();

	@DefaultStringValue("FillCells")
	String FillCells();

	@DefaultStringValue("[ <List of Points> ]\n[ <List of Numbers>, <List of Numbers> ]")
	String Covariance_Syntax();

	@DefaultStringValue("[ <Line>, <Number> ]")
	String SetLineThickness_Syntax();

	@DefaultStringValue("[ <Function>, <Start Value>, <Number of Iterations> ]")
	String Iteration_Syntax();

	@DefaultStringValue("[ <Lambda>, <Probability> ]")
	String InverseExponential_Syntax();

	@DefaultStringValue("[ <Point3D>, <Point3D>, ... ]")
	String Pyramid_Syntax();

	@DefaultStringValue("Function")
	String Function();

	@DefaultStringValue("[ <Dividend Number>, <Divisor Number> ]\n[ <Dividend Polynomial>, <Divisor Polynomial> ]")
	String Div_SyntaxCAS();

	@DefaultStringValue("Flatten")
	String Flatten();

	@DefaultStringValue("[ <Conic> ]")
	String FirstAxisLength_Syntax();

	@DefaultStringValue("[ <Column>, <List> ]")
	String FillColumn_Syntax();

	@DefaultStringValue("Median")
	String Median();

	@DefaultStringValue("SetDynamicColor")
	String SetDynamicColor();

	@DefaultStringValue("[ <List>, <Position of Element> ]\n[ <Matrix>, <Row>, <Column> ]\n[ <List>, <Index1>, <Index2>, ... ]")
	String Element_Syntax();

	@DefaultStringValue("[ <Expression> ]\n[ <Expression>, <Target Function> ]\n[ <Expression>, <Target Function>, <Target Variable> ]\n[ <Expression>, <Target Function>, <Target Variable>, <Target Variable> ]")
	String TrigExpand_SyntaxCAS();

	@DefaultStringValue("[ <Point>, <Function> ]\n[ <Point>, <Curve> ]")
	String CurvatureVector_Syntax();

	@DefaultStringValue("LeftSum")
	String LeftSum();

	@DefaultStringValue("[ <Conic> ]")
	String Eccentricity_Syntax();

	@DefaultStringValue("TMean2Estimate")
	String TMean2Estimate();

	@DefaultStringValue("[ <Conic> ]\n[ <Polygon> ]\n[ <Point>, ..., <Point> ]")
	String Area_Syntax();

	@DefaultStringValue("[ <Number> ]")
	String FractionText_Syntax();

	@DefaultStringValue("TableText")
	String TableText();

	@DefaultStringValue("Plane")
	String Plane();

	@DefaultStringValue("[ <Min>, <Max>, <Increment>, <Speed>, <Width>, <Is Angle>, <Horizontal>, <Animating>, <Random> ]")
	String Slider_Syntax();

	@DefaultStringValue("[ <Alpha>, <Beta>, <Probability> ]")
	String InverseGamma_Syntax();

	@DefaultStringValue("SetPointSize")
	String SetPointSize();

	@DefaultStringValue("[ <Matrix> ]")
	String Determinant_Syntax();

	@DefaultStringValue("[ <Point>, <Point>, <Direction> ]")
	String Octahedron_Syntax();

	@DefaultStringValue("SolveODE")
	String SolveODE();

	@DefaultStringValue("[ <List> ]")
	String RandomElement_Syntax();

	@DefaultStringValue("InverseTDistribution")
	String InverseTDistribution();

	@DefaultStringValue("[ <End Value> ]\n[ <Expression>, <Variable>, <Start Value>, <End Value> ]\n[ <Expression>, <Variable>, <Start Value>, <End Value>, <Increment> ]")
	String Sequence_SyntaxCAS();

	@DefaultStringValue("IsInteger")
	String IsInteger();

	@DefaultStringValue("[ <Expression>, <Var1>, <List1>, <Var2>, <List2>, ... ]")
	String Zip_Syntax();

	@DefaultStringValue("[ <Number> ]")
	String HideLayer_Syntax();

	@DefaultStringValue("[ <Vector>, <Vector> ]")
	String Dot_SyntaxCAS();

	@DefaultStringValue("ToolImage")
	String ToolImage();

	@DefaultStringValue("SelectedIndex")
	String SelectedIndex();

	@DefaultStringValue("[ <Object>, <List> ]\n[ <Text>, <Text> ]\n[ <Object>, <List>, <Start Index> ]\n[ <Text>, <Text>, <Start Index> ]")
	String IndexOf_Syntax();

	@DefaultStringValue("Q1")
	String Q1();

	@DefaultStringValue("Q3")
	String Q3();

	@DefaultStringValue("[ <Shape>, <Scale>, <Probability> ]")
	String InverseWeibull_Syntax();

	@DefaultStringValue("ToExponential")
	String ToExponential();

	@DefaultStringValue("[ <List of Points> ]\n[ <f(x, y)> ]")
	String ImplicitCurve_Syntax();

	@DefaultStringValue("[ <Point>, <Point>, <Direction> ]")
	String Tetrahedron_Syntax();

	@DefaultStringValue("[ <Function>, <String> ]")
	String ParseToFunction_Syntax();

	@DefaultStringValue("TriangleCubic")
	String TriangleCubic();

	@DefaultStringValue("Polar")
	String Polar();

	@DefaultStringValue("ApplyMatrix")
	String ApplyMatrix();

	@DefaultStringValue("[ <List of Numbers> ]")
	String Q1_Syntax();

	@DefaultStringValue("Variance")
	String Variance();

	@DefaultStringValue("ReducedRowEchelonForm")
	String ReducedRowEchelonForm();

	@DefaultStringValue("CircularSector")
	String CircleSector();

	@DefaultStringValue("[ <Number of Elements>, <Exponent> ]\n[ <Number of Elements>, <Exponent>, <Boolean Cumulative> ]\n[ <Number of Elements>, <Exponent>, <Variable Value>, <Boolean Cumulative> ]")
	String Zipf_Syntax();

	@DefaultStringValue("Logistic")
	String Logistic();

	@DefaultStringValue("InverseLogistic")
	String InverseLogistic();

	@DefaultStringValue("[ <List of Numbers> ]")
	String Q3_Syntax();

	@DefaultStringValue("[ <List of Texts> ]\n[ <List of Texts>, <Parameter>, <Parameter>, ... ]")
	String Execute_Syntax();

	@DefaultStringValue("AngleBisector")
	String AngularBisector();

	@DefaultStringValue("[ <List> ]\n[ <List>, <Number of Elements> ]")
	String Last_SyntaxCAS();

	@DefaultStringValue("[ <List> ]")
	String Unique_Syntax();

	@DefaultStringValue("Polynomial")
	String Polynomial();

	@DefaultStringValue("SD")
	String SD();

	@DefaultStringValue("SetFixed")
	String SetFixed();

	@DefaultStringValue("[ <Line> ]")
	String Slope_Syntax();

	@DefaultStringValue("IndexOf")
	String IndexOf();

	@DefaultStringValue("InversePascal")
	String InversePascal();

	@DefaultStringValue("[ ]\n[ <Object> ]")
	String ConstructionStep_Syntax();

	@DefaultStringValue("Execute")
	String Execute();

	@DefaultStringValue("[ <Point>, <Point>, <Point>, <Number>, <Number>, <Number> ]")
	String Trilinear_Syntax();

	@DefaultStringValue("[ <Object>, <Vector> ]\n[ <Object>, <Line>, <Ratio> ]")
	String Stretch_Syntax();

	@DefaultStringValue("[ <Numerator Degrees of Freedom>, <Denominator Degrees of Freedom>, <Probability> ]")
	String InverseFDistribution_Syntax();

	@DefaultStringValue("[ <Focal Point>, <Focal Point>, <Semimajor Axis Length> ]\n[ <Focal Point>, <Focal Point>, <Segment> ]\n[ <Point>, <Point>, <Point> ]")
	String Ellipse_Syntax();

	@DefaultStringValue("[ <Equation> ]\n[ <Equation>, <Variable> ]\n[ <List of Equations>, <List of Variables> ]")
	String CSolutions_SyntaxCAS();

	@DefaultStringValue("[ <Mean>, <Standard Deviation>, <Probability> ]")
	String InverseNormal_Syntax();

	@DefaultStringValue("SetValue")
	String SetValue();

	@DefaultStringValue("[ <List> ]\n[ <Interval> ]\n[ <Number>, <Number> ]\n[ <Function>, <Start x-Value>, <End x-Value> ]")
	String Max_Syntax();

	@DefaultStringValue("ConjugateDiameter")
	String Diameter();

	@DefaultStringValue("Shuffle")
	String Shuffle();

	@DefaultStringValue("[ <Number>, <Number> ]")
	String nPr_SyntaxCAS();

	@DefaultStringValue("Cone")
	String Cone();

	@DefaultStringValue("Decimal")
	String Decimal();

	@DefaultStringValue("[ <Population Size>, <Number of Successes>, <Sample Size>, <Variable Value>, <Boolean Cumulative> ]")
	String HyperGeometric_SyntaxCAS();

	@DefaultStringValue("NextPrime")
	String NextPrime();

	@DefaultStringValue("[ <List> ]")
	String SelectedIndex_Syntax();

	@DefaultStringValue("Prism")
	String Prism();

	@DefaultStringValue("CompetitionRank")
	String CompetitionRank();

	@DefaultStringValue("MatrixRank")
	String MatrixRank();

	@DefaultStringValue("[ <Number n>, <Number r> ]")
	String Binomial_Syntax();

	@DefaultStringValue("TextToUnicode")
	String TextToUnicode();

	@DefaultStringValue("[ <List of Numbers> ]")
	String Mean_Syntax();

	@DefaultStringValue("Bottom")
	String Bottom();

	@DefaultStringValue("[ <CellRange>, <Object> ]\n[ <Cell>, <List> ]\n[ <Cell>, <Matrix> ]")
	String FillCells_Syntax();

	@DefaultStringValue("[ <List of Sample Data>, <Hypothesized Mean>, <Tail> ]\n[ <Sample Mean>, <Sample Standard Deviation>, <Sample Size>, <Hypothesized Mean>, <Tail> ]")
	String TTest_Syntax();

	@DefaultStringValue("[ <Object>, <true | false> ]")
	String SetFixed_Syntax();

	@DefaultStringValue("BinomialCoefficient")
	String Binomial();

	@DefaultStringValue("[ <Vector>, <Vector> ]")
	String Cross_SyntaxCAS();

	@DefaultStringValue("[ <Column>, <Row> ]")
	String Cell_Syntax();

	@DefaultStringValue("[ <List>, <Object> ]\n[ <Object>, <List> ]")
	String Append_Syntax();

	@DefaultStringValue("Directrix")
	String Directrix();

	@DefaultStringValue("[ <Function>, <Start x-Value>, <End x-Value>, <Number of Rectangles> ]")
	String LowerSum_Syntax();

	@DefaultStringValue("InfiniteCone")
	String InfiniteCone();

	@DefaultStringValue("Slope")
	String Slope();

	@DefaultStringValue("[ <Matrix> ]")
	String Invert_SyntaxCAS();

	@DefaultStringValue("Erlang")
	String Erlang();

	@DefaultStringValue("[ <List of Sample Data>, <Level> ]\n[ <Sample Mean>, <Sample Standard Deviation>, <Sample Size>, <Level> ]")
	String TMeanEstimate_Syntax();

	@DefaultStringValue("[ <List of Raw Data> ]")
	String NormalQuantilePlot_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]")
	String Median_Syntax();
	
	@DefaultStringValue("Surface")
	String Surface();
	
	@DefaultStringValue("[ <Expression>, <Expression>, <Expression>, <Parameter Variable 1>, <Start Value>, <End Value>, <Parameter Variable 2>, <Start Value>, <End Value> ]")
	String Surface_Syntax();

	@DefaultStringValue("Surface")
	String SurfaceCartesian();

	@DefaultStringValue("[ <Object>, <Point> ]\n[ <Object>, <Line> ]\n[ <Object>, <Circle> ]")
	String Mirror_Syntax();

	@DefaultStringValue("[ <Equation> ]\n[ <Equation>, <Dependent Variable>, <Independent Variable> ]")
	String SolveODE_SyntaxCAS();

	@DefaultStringValue("ImplicitDerivative")
	String ImplicitDerivative();

	@DefaultStringValue("[ <Vector>, <Conic> ]\n[ <Line>, <Conic> ]")
	String Diameter_Syntax();

	@DefaultStringValue("SampleVariance")
	String SampleVariance();

	@DefaultStringValue("Numerator")
	String Numerator();

	@DefaultStringValue("[ <Conic> ]")
	String Axes_Syntax();

	@DefaultStringValue("[ <List of Raw Data> ]")
	String DotPlot_Syntax();

	@DefaultStringValue("Length")
	String Length();

	@DefaultStringValue("ToComplex")
	String ToComplex();

	@DefaultStringValue("FractionText")
	String FractionText();

	@DefaultStringValue("[ <Point>, <Point>, <Point>, <Equation> ]")
	String TriangleCurve_Syntax();

	@DefaultStringValue("[ <Expression> ]\n[ <Expression>, <Target Function> ]")
	String TrigCombine_SyntaxCAS();

	@DefaultStringValue("StartAnimation")
	String StartAnimation();

	@DefaultStringValue("UpperSum")
	String UpperSum();

	@DefaultStringValue("Element")
	String Element();

	@DefaultStringValue("[ <Conic> ]")
	String Focus_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String ConvexHull_Syntax();

	@DefaultStringValue("[ <Object> ]")
	String Delete_Syntax();

	@DefaultStringValue("SelectObjects")
	String SelectObjects();

	@DefaultStringValue("PointIn")
	String PointIn();

	@DefaultStringValue("[ <Number> ]")
	String NextPrime_SyntaxCAS();

	@DefaultStringValue("InverseFDistribution")
	String InverseFDistribution();

	@DefaultStringValue("[ <Expression>, <Value> ]\n[ <Expression>, <Variable>, <Value> ]")
	String LimitBelow_SyntaxCAS();

	@DefaultStringValue("SetAxesRatio")
	String SetAxesRatio();

	@DefaultStringValue("[ <List> ]\n[ <Number>, <Number> ]")
	String Max_SyntaxCAS();

	@DefaultStringValue("Sphere")
	String Sphere();

	@DefaultStringValue("[ <List of Integers> ]")
	String UnicodeToText_Syntax();

	@DefaultStringValue("CopyFreeObject")
	String CopyFreeObject();

	@DefaultStringValue("RandomElement")
	String RandomElement();

	@DefaultStringValue("Sort")
	String Sort();

	@DefaultStringValue("[ <List of Class Boundaries>, <List of Heights> ]\n[ <List of Class Boundaries>, <List of Raw Data>, <Use Density>, <Density Scale Factor (optional)> ]\n[ <Boolean Cumulative>, <List of Class Boundaries>, <List of Raw Data>, <Use Density>, <Density Scale Factor (optional)> ]")
	String Histogram_Syntax();

	@DefaultStringValue("[ <Polygon>, <Point> ]\n[ <Polygon>, <Height value> ]\n[ <Point>, <Point>, ... ]")
	String Prism_Syntax();

	@DefaultStringValue("[ <Object> ]\n[ <Object>, <Boolean for Substitution of Variables> ]\n[ <Object>, <Boolean for Substitution of Variables>, <Boolean Show Name> ]")
	String LaTeX_Syntax();

	@DefaultStringValue("Append")
	String Append();

	@DefaultStringValue("[ ]\n[ <Image> ]\n[ <Text or Number>, \"<Format (optional)>\" , \"<Error Correction (optional)>\", <Width (optional)>, <Height (optional)>]")
	String BarCode_Syntax();

	@DefaultStringValue("[ <Boolean Expression> ]")
	String Prove_Syntax();
	
	@DefaultStringValue("[ <Boolean Expression> ]")
	String ProveDetails_Syntax();
	
	@DefaultStringValue("[ <List> ]")
	String RootList_Syntax();

	@DefaultStringValue("[ <Object>, <Object> ]")
	String Relation_Syntax();

	@DefaultStringValue("[ <Expression>, <Value> ]\n[ <Expression>, <Variable>, <Value> ]")
	String Limit_SyntaxCAS();

	@DefaultStringValue("[ <Degrees of Freedom>, <Variable> ]")
	String ChiSquared_SyntaxCAS();

	@DefaultStringValue("Minimize")
	String Minimize();

	@DefaultStringValue("UnitPerpendicularVector")
	String UnitOrthogonalVector();

	@DefaultStringValue("[ <Function> ]")
	String Numerator_Syntax();

	@DefaultStringValue("BoxPlot")
	String BoxPlot();

	@DefaultStringValue("Semicircle")
	String Semicircle();

	@DefaultStringValue("Curvature")
	String Curvature();

	@DefaultStringValue("PlaneBisector")
	String PlaneBisector();

	@DefaultStringValue("HarmonicMean")
	String HarmonicMean();

	@DefaultStringValue("UpdateConstruction")
	String UpdateConstruction();

	@DefaultStringValue("[ <List of Numbers> ]\n[ <List of Points> ]")
	String SXX_Syntax();

	@DefaultStringValue("InflectionPoint")
	String TurningPoint();

	@DefaultStringValue("Histogram")
	String Histogram();

	@DefaultStringValue("Relation")
	String Relation();

	@DefaultStringValue("[ <Matrix> ]")
	String ReducedRowEchelonForm_Syntax();

	@DefaultStringValue("[ <List> ]\n[ <Interval> ]\n[ <Number>, <Number> ]\n[ <Function>, <Start x-Value>, <End x-Value> ]")
	String Min_Syntax();

	@DefaultStringValue("InverseZipf")
	String InverseZipf();

	@DefaultStringValue("[ <View Number 1|2> ]")
	String SetActiveView_Syntax();

	@DefaultStringValue("[ <Boolean>, <0|1> ]\n[ <Object>, <Object> ]\n[ <List>, <Number>, <Object> ]")
	String SetValue_Syntax();

	@DefaultStringValue("[ <Point>, <Point> ]\n[ <Point>, <Parallel Line> ]\n[ <Point>, <Direction Vector> ]")
	String Line_Syntax();

	@DefaultStringValue("[ <Vector> ]")
	String ToComplex_SyntaxCAS();

	@DefaultStringValue("[ <Matrix>, <Object> ]")
	String ApplyMatrix_Syntax();

	@DefaultStringValue("[ <List> ]\n[ <List>, <Number of Elements> ]")
	String Sum_Syntax();

	@DefaultStringValue("Name")
	String Name();

	@DefaultStringValue("BarChart")
	String BarChart();

	@DefaultStringValue("Identity")
	String Identity();

	@DefaultStringValue("[ <Number> ]")
	String Rationalize_SyntaxCAS();

	@DefaultStringValue("[ <Object>, <Object> ]\n[ <Object>, <Object>, <Index of Intersection Point> ]\n[ <Object>, <Object>, <Initial Point> ]\n[ <Function>, <Function>, <Start x-Value>, <End x-Value> ]")
	String Intersect_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]\n[ <List of Polynomials> ]\n[ <Number>, <Number> ]\n[ <Polynomial>, <Polynomial> ]")
	String GCD_SyntaxCAS();

	@DefaultStringValue("[ <List>, <List> ]\n[ <Polygon>, <Polygon> ]")
	String Union_Syntax();

	@DefaultStringValue("Divisors")
	String Divisors();

	@DefaultStringValue("[ <Midpoint>, <Point>, <Point> ]")
	String CircleSector_Syntax();

	@DefaultStringValue("Syy")
	String SYY();

	@DefaultStringValue("Cross")
	String Cross();

	@DefaultStringValue("[ <Function> ]")
	String Simplify_SyntaxCAS();

	@DefaultStringValue("[ <Mean> ]\n[ <Mean>, <Boolean Cumulative> ]\n[ <Mean>, <Variable Value>, <Boolean Cumulative> ]")
	String Poisson_Syntax();

	@DefaultStringValue("[ <List> ]\n[ <Text> ]\n[ <List>, <Number of Elements> ]\n[ <Text>, <Number of Elements> ]\n[ <Locus>, <Number of Elements> ]")
	String First_Syntax();

	@DefaultStringValue("SetConditionToShowObject")
	String SetConditionToShowObject();

	@DefaultStringValue("[ <Alpha>, <Beta>, x ]\n[ <Alpha>, <Beta>, <Variable Value> ]\n[ <Alpha>, <Beta>, x, <Boolean Cumulative> ]")
	String Gamma_Syntax();

	@DefaultStringValue("RigidPolygon")
	String RigidPolygon();

	@DefaultStringValue("Ordinal")
	String Ordinal();

	@DefaultStringValue("RandomBinomial")
	String RandomBinomial();

	@DefaultStringValue("PolyLine")
	String PolyLine();

	@DefaultStringValue("Transpose")
	String Transpose();

	@DefaultStringValue("RSquare")
	String RSquare();

	@DefaultStringValue("Sxx")
	String SXX();

	@DefaultStringValue("Mode")
	String Mode();

	@DefaultStringValue("Sxy")
	String SXY();

	@DefaultStringValue("[ ]\n[ <Object>, <Object>, ... ]")
	String SelectObjects_Syntax();

	@DefaultStringValue("[ <Number> ]")
	String PrimeFactors_SyntaxCAS();

	@DefaultStringValue("[ <Complex Number> ]\n[ <Vector> ]")
	String ToPolar_SyntaxCAS();

	@DefaultStringValue("[ <Function>, <Start x-Value>, <End x-Value>, <Number of Rectangles> ]")
	String UpperSum_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String FitLog_Syntax();

	@DefaultStringValue("TaylorPolynomial")
	String TaylorSeries();

	@DefaultStringValue("[ <Number> ]")
	String MixedNumber_SyntaxCAS();

	@DefaultStringValue("Limit")
	String Limit();

	@DefaultStringValue("[ <Shape>, <Rate>, x ]\n[ <Shape>, <Rate>, <Variable Value> ]\n[ <Shape>, <Rate>, x, <Boolean Cumulative> ]")
	String Erlang_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String SYY_Syntax();

	@DefaultStringValue("Cell")
	String Cell();

	@DefaultStringValue("[ <Object> ]\n[ <Object>, <Parameter> ]\n[ <Point>, <Vector> ]")
	String Point_Syntax();

	@DefaultStringValue("[ <List> ]\n[ <expression>, <variable>, <start value>, <end value> ]")
	String Sum_SyntaxCAS();

	@DefaultStringValue("[ <Lambda>, <Variable Value> ]")
	String Exponential_SyntaxCAS();

	@DefaultStringValue("[ <Object>, <Dilation Factor> ]\n[ <Object>, <Dilation Factor>, <Dilation Center Point> ]")
	String Dilate_Syntax();

	@DefaultStringValue("KeepIf")
	String KeepIf();

	@DefaultStringValue("ImplicitCurve")
	String ImplicitCurve();

	@DefaultStringValue("[ <Line>, <Line> ]\n[ <Point>, <Point>, <Point> ]")
	String AngularBisector_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String FitLineY_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String SigmaYY_Syntax();

	@DefaultStringValue("[ <Function>, <Start x-Value>, <End x-Value> ]")
	String Function_Syntax();

	@DefaultStringValue("[ <Polynomial> ]")
	String ComplexRoot_Syntax();

	@DefaultStringValue("[ <List of Class Boundaries>, <List of Heights> ]\n[ <List of Class Boundaries>, <List of Raw Data>, <Use Density>, <Density Scale Factor> (optional) ]\n[ <Boolean Cumulative>, <List of Class Boundaries>, <List of Raw Data>, <Use Density>, <Density Scale Factor> (optional) ]")
	String HistogramRight_Syntax();

	@DefaultStringValue("ChiSquared")
	String ChiSquared();

	@DefaultStringValue("Line")
	String Line();

	@DefaultStringValue("OsculatingCircle")
	String OsculatingCircle();

	@DefaultStringValue("[ <List of Sample Data 1>, <List of Sample Data 2>, <Tail>, <Boolean Pooled> ]\n[ <Sample Mean 1>, <Sample Standard Deviation 1>, <Sample Size 1>, <Sample Mean 2>, <Sample Standard Deviation 2>, <Sample Size 2>, <Tail>, <Boolean Pooled> ]")
	String TTest2_Syntax();

	@DefaultStringValue("[ <Number of Successes>, <Probability of Success> ]\n[ <Number of Successes>, <Probability of Success>, <Boolean Cumulative> ]\n[ <Number of Successes>, <Probability of Success>, <Variable Value>, <Boolean Cumulative> ]")
	String Pascal_Syntax();

	@DefaultStringValue("[ <Expression> ]")
	String TrigSimplify_SyntaxCAS();

	@DefaultStringValue("[ <Median>, <Scale>, <Probability> ]")
	String InverseCauchy_Syntax();

	@DefaultStringValue("Checkbox")
	String Checkbox();

	@DefaultStringValue("[ <Numerator Degrees of Freedom>, <Denominator Degrees of Freedom>, x ]\n[ <Numerator Degrees of Freedom>, <Denominator Degrees of Freedom>, <Variable Value> ]\n[ <Numerator Degrees of Freedom>, <Denominator Degrees of Freedom>, x, <Boolean Cumulative> ]")
	String FDistribution_Syntax();

	@DefaultStringValue("[ <Point> ]\n[ <Start Point>, <End Point> ]")
	String Vector_Syntax();

	@DefaultStringValue("[ <List> ]")
	String Shuffle_Syntax();

	@DefaultStringValue("[ <Point>, <Point>, <Point>, <Point> ]")
	String CrossRatio_Syntax();

	@DefaultStringValue("[ <Polynomial> ]")
	String TurningPoint_Syntax();

	@DefaultStringValue("Sequence")
	String Sequence();

	@DefaultStringValue("Circle")
	String Circle();

	@DefaultStringValue("[ <Number>, <Number> ]")
	String Binomial_SyntaxCAS();

	@DefaultStringValue("CSolve")
	String CSolve();

	@DefaultStringValue("[ <Number>, <String> ]")
	String ParseToNumber_Syntax();

	@DefaultStringValue("SetFilling")
	String SetFilling();

	@DefaultStringValue("Last")
	String Last();

	@DefaultStringValue("ZoomIn")
	String ZoomIn();

	@DefaultStringValue("[ <List of Points> ]\n[ <List of Numbers>, <List of Numbers> ]")
	String Covariance_SyntaxCAS();

	@DefaultStringValue("SurdText")
	String SurdText();

	@DefaultStringValue("InverseCauchy")
	String InverseCauchy();

	@DefaultStringValue("[ <List of Numbers> ]")
	String Mean_SyntaxCAS();

	@DefaultStringValue("SigmaXY")
	String SigmaXY();

	@DefaultStringValue("SigmaXX")
	String SigmaXX();

	@DefaultStringValue("[ <Conic> ]")
	String FirstAxis_Syntax();

	@DefaultStringValue("SetLayer")
	String SetLayer();

	@DefaultStringValue("Conic")
	String Conic();

	@DefaultStringValue("[ ]")
	String GetTime_Syntax();

	@DefaultStringValue("[ <List of Points>, <Function> ]")
	String ResidualPlot_Syntax();

	@DefaultStringValue("[ <Point>, <Point>, <Direction> ]")
	String Cube_Syntax();

	@DefaultStringValue("SigmaYY")
	String SigmaYY();

	@DefaultStringValue("[ <List of Points>, <Function> ]")
	String SumSquaredErrors_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]\n[ <Number>, <Number> ]")
	String LCM_Syntax();

	@DefaultStringValue("InverseNormal")
	String InverseNormal();

	@DefaultStringValue("[ <List> ]")
	String RemoveUndefined_Syntax();

	@DefaultStringValue("MinimumSpanningTree")
	String MinimumSpanningTree();

	@DefaultStringValue("Determinant")
	String Determinant();

	@DefaultStringValue("[ <Number> ]")
	String IsPrime_SyntaxCAS();

	@DefaultStringValue("CFactor")
	String CFactor();

	@DefaultStringValue("Eccentricity")
	String Eccentricity();

	@DefaultStringValue("[ <List> ]")
	String TiedRank_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]")
	String SampleVariance_SyntaxCAS();

	@DefaultStringValue("MixedNumber")
	String MixedNumber();

	@DefaultStringValue("Mean")
	String Mean();

	@DefaultStringValue("[ <List of Points> ]")
	String SDX_Syntax();

	@DefaultStringValue("Corner")
	String Corner();

	@DefaultStringValue("[ <Vector> ]")
	String OrthogonalVector_SyntaxCAS();

	@DefaultStringValue("[ <Function>, <Value> ]")
	String LimitAbove_Syntax();

	@DefaultStringValue("[ <Conic>, <Point>, <Point> ]\n[ <Conic>, <Parameter Value>, <Parameter Value> ]")
	String Sector_Syntax();

	@DefaultStringValue("[ <Object> ]\n[ <Vector>, <Vector> ]\n[ <Line>, <Line> ]\n[ <Point>, <Apex>, <Point> ]\n[ <Point>, <Apex>, <Angle> ]")
	String Angle_Syntax();

	@DefaultStringValue("HyperGeometric")
	String HyperGeometric();

	@DefaultStringValue("[ <Complex Number> ]")
	String ToExponential_SyntaxCAS();

	@DefaultStringValue("ComplexRoot")
	String ComplexRoot();

	@DefaultStringValue("Circumference")
	String Circumference();

	@DefaultStringValue("[ <Point>, <Point>, <Point> ]")
	String CircumcircleSector_Syntax();

	@DefaultStringValue("Center")
	String Center();

	@DefaultStringValue("[ <Equation in x> ]\n[ <Equation>, <Variable> ]\n[ <Equation>, <Variable = starting value> ]\n[ <List of Equations>, <List of Variables> ]")
	String Solve_SyntaxCAS();

	@DefaultStringValue("[ <Function>, <x-Value>, <Order Number> ]")
	String TaylorSeries_Syntax();

	@DefaultStringValue("[ <Number>, <Number> ]")
	String SetAxesRatio_Syntax();

	@DefaultStringValue("[ <Point>, <Number>, <Number> ]")
	String DynamicCoordinates_Syntax();

	@DefaultStringValue("[ <Point>, <Object> ]")
	String Distance_Syntax();

	@DefaultStringValue("SetPointStyle")
	String SetPointStyle();

	@DefaultStringValue("Sample")
	String Sample();

	@DefaultStringValue("[ <Line>, <Polygon> ]\n[ <Line>, <Conic> ]\n[ <Plane>, <Polygon> ]\n[ <Plane>, <Quadric> ]")
	String IntersectionPaths_Syntax3D();

	@DefaultStringValue("[ <List of Points>, <List of Numbers> ]")
	String Barycenter_Syntax();

	@DefaultStringValue("[ <Function>, <Function>, <Start x-Value>, <End x-Value> ]\n[ <Function>, <Function>, <Variable>, <Start Value>, <End Value> ]")
	String IntegralBetween_SyntaxCAS();

	@DefaultStringValue("[ <Point>, <Point>, <Point>, <Point>, <Point> ]\n[ <Number>, <Number>, <Number>, <Number>, <Number>, <Number> ]")
	String Conic_Syntax();

	@DefaultStringValue("[ <File> ]\n[ <Boolean Play> ]\n[ <Note Sequence>, <Instrument> ]\n[ <Note>, <Duration>, <Instrument> ]\n[ <Function>, <Min Value>, <Max Value> ]\n[ <Function>, <Min Value>, <Max Value>, <Sample Rate>, <Sample Depth> ]")
	String PlaySound_Syntax();

	@DefaultStringValue("[ <Integer> ]")
	String UnicodeToLetter_Syntax();

	@DefaultStringValue("Bernoulli")
	String Bernoulli();

	@DefaultStringValue("SampleSD")
	String SampleSD();

	@DefaultStringValue("Perimeter")
	String Perimeter();

	@DefaultStringValue("[ <Mean>, <Standard Deviation> ]")
	String RandomNormal_SyntaxCAS();

	@DefaultStringValue("[ <Function>, <x-start>, <x-end> ]\n[ <Function>, <Variable>, <Start Point>, <End Point> ]")
	String Length_SyntaxCAS();

	@DefaultStringValue("[ <Name of Object as Text> ]")
	String Object_Syntax();

	@DefaultStringValue("[ <Spreadsheet Cell> ]")
	String ColumnName_Syntax();

	@DefaultStringValue("[ <Line>, <Number> ]")
	String SetLineStyle_Syntax();

	@DefaultStringValue("[ <Conic> ]")
	String Directrix_Syntax();

	@DefaultStringValue("FrequencyTable")
	String FrequencyTable();

	@DefaultStringValue("[ <Polygon>, <Polygon> ]")
	String IntersectRegion_Syntax();

	@DefaultStringValue("[ <Complex Number> ]")
	String ToPoint_SyntaxCAS();

	@DefaultStringValue("[ <Polynomial> ]\n[ <Number> ]")
	String Factors_Syntax();

	@DefaultStringValue("[ <Vector> ]\n[ <Point> ]\n[ <List> ]\n[ <Text> ]\n[ <Locus> ]\n[ <Segment> ]\n[ <Function>, <Start x-Value>, <End x-Value> ]\n[ <Function>, <Start Point>, <End Point> ]\n[ <Curve>, <Start x-Value>, <End x-Value> ]\n[ <Curve>, <Start Point>, <End Point> ]")
	String Length_Syntax();

	@DefaultStringValue("Parabola")
	String Parabola();

	@DefaultStringValue("[ <Object>, <Text> ]")
	String SetCaption_Syntax();

	@DefaultStringValue("SumSquaredErrors")
	String SumSquaredErrors();

	@DefaultStringValue("Cylinder")
	String Cylinder();

	@DefaultStringValue("[ <List of Lists> ]\n[ <List>, <List>, ... ]")
	String Join_Syntax();

	@DefaultStringValue("[ <Focal Point>, <Focal Point>, <Semimajor Axis Length> ]\n[ <Focal Point>, <Focal Point>, <Segment> ]\n[ <Point>, <Point>, <Point> ]")
	String Hyperbola_Syntax();

	@DefaultStringValue("[ <List of Points>, <Function> ]")
	String RSquare_Syntax();

	@DefaultStringValue("[ <Point>, <Point>, <Point> ]")
	String CircumcircleArc_Syntax();

	@DefaultStringValue("Segment")
	String Segment();

	@DefaultStringValue("[ <List>, <Start Position>, <End Position> ]")
	String Take_SyntaxCAS();

	@DefaultStringValue("InverseChiSquared")
	String InverseChiSquared();

	@DefaultStringValue("Imaginary")
	String Imaginary();

	@DefaultStringValue("CircumcircularArc")
	String CircumcircleArc();

	@DefaultStringValue("CircumcircularSector")
	String CircumcircleSector();

	@DefaultStringValue("[ <List of Sample Data 1>, <List of Sample Data 2>, <Level>, <Boolean Pooled> ]\n[ <Sample Mean 1>, <Sample Standard Deviation 1>, <Sample Size 1>, <Sample Mean 2>, <Sample Standard Deviation 2>, <Sample Size 2>, <Level>, <Boolean Pooled> ]")
	String TMean2Estimate_Syntax();

	@DefaultStringValue("CurvatureVector")
	String CurvatureVector();

	@DefaultStringValue("InverseWeibull")
	String InverseWeibull();

	@DefaultStringValue("[ <Complex Number> ]")
	String Imaginary_SyntaxCAS();

	@DefaultStringValue("[ <Degrees of Freedom>, x ]\n[ <Degrees of Freedom>, <Variable Value> ]\n[ <Degrees of Freedom>, x, <Boolean Cumulative> ]")
	String TDistribution_Syntax();

	@DefaultStringValue("TrigCombine")
	String TrigCombine();

	@DefaultStringValue("CompleteSquare")
	String CompleteSquare();

	@DefaultStringValue("IsInRegion")
	String IsInRegion();

	@DefaultStringValue("FitLineX")
	String FitLineX();

	@DefaultStringValue("FitExp")
	String FitExp();

	@DefaultStringValue("FitLine")
	String FitLineY();

	@DefaultStringValue("[ <Number> ]")
	String PreviousPrime_SyntaxCAS();

	@DefaultStringValue("FitGrowth")
	String FitGrowth();

	@DefaultStringValue("CellRange")
	String CellRange();

	@DefaultStringValue("[ <Degrees of Freedom>, x ]\n[ <Degrees of Freedom>, <Variable Value> ]\n[ <Degrees of Freedom>, x, <Boolean Cumulative> ]")
	String ChiSquared_Syntax();

	@DefaultStringValue("Intersection")
	String Intersection();

	@DefaultStringValue("[ <Matrix> ]")
	String Determinant_SyntaxCAS();

	@DefaultStringValue("MajorAxis")
	String FirstAxis();

	@DefaultStringValue("PartialFractions")
	String PartialFractions();

	@DefaultStringValue("Delete")
	String Delete();

	@DefaultStringValue("[ <Degrees of Freedom>, <Probability> ]")
	String InverseChiSquared_Syntax();

	@DefaultStringValue("SetCaption")
	String SetCaption();

	@DefaultStringValue("[ <Dividend Number>, <Divisor Number> ]\n[ <Dividend Polynomial>, <Divisor Polynomial> ]")
	String Division_SyntaxCAS();

	@DefaultStringValue("[ <Dependent Number>, <Free Number> ]")
	String Maximize_Syntax();

	@DefaultStringValue("Factors")
	String Factors();

	@DefaultStringValue("Product")
	String Product();

	@DefaultStringValue("Midpoint")
	String Midpoint();

	@DefaultStringValue("ShowLayer")
	String ShowLayer();

	@DefaultStringValue("LCM")
	String LCM();

	@DefaultStringValue("Polyhedron")
	String Polyhedron();

	@DefaultStringValue("NIntegral")
	String NIntegral();

	@DefaultStringValue("[ <Population Size>, <Number of Successes>, <Sample Size> ]\n[ <Population Size>, <Number of Successes>, <Sample Size>, <Boolean Cumulative> ]\n[ <Population Size>, <Number of Successes>, <Sample Size>, <Variable Value>, <Boolean Cumulative> ]")
	String HyperGeometric_Syntax();

	@DefaultStringValue("[ <Matrix> ]")
	String MatrixRank_SyntaxCAS();

	@DefaultStringValue("IntegerPart")
	String IntegerPart();

	@DefaultStringValue("[ <List of Numbers>, <Percent> ]")
	String Percentile_Syntax();

	@DefaultStringValue("[ <Point>, <Point>, <Direction> ]")
	String Dodecahedron_Syntax();

	@DefaultStringValue("RemoveUndefined")
	String RemoveUndefined();

	@DefaultStringValue("Slider")
	String Slider();

	@DefaultStringValue("Simplify")
	String Simplify();

	@DefaultStringValue("[ <Minimum Integer>, <Maximum Integer> ]")
	String Random_Syntax();

	@DefaultStringValue("[ <Function>, <Start x-Value>, <End x-Value> ]\n[ <Function>, <Variable>, <Start Value>, <End Value> ]")
	String NIntegral_SyntaxCAS();

	@DefaultStringValue("[ <List of Points> ]")
	String FitLineX_Syntax();

	@DefaultStringValue("Max")
	String Max();

	@DefaultStringValue("ClosestPoint")
	String ClosestPoint();

	@DefaultStringValue("[ <Polynomial> ]\n[ <Expression>, <Variable> ]")
	String Factor_SyntaxCAS();

	@DefaultStringValue("[ <Number of Trials>, <Probability> ]")
	String RandomBinomial_SyntaxCAS();

	@DefaultStringValue("[ <f'(x, y)>, <Start x>, <Start y>, <End x>, <Step> ]\n[ <y'>, <x'>, <Start x>, <Start y>, <End t>, <Step> ]\n[ <b(x)>, <c(x)>, <f(x)>, <Start x>, <Start y>, <Start y'>, <End x>, <Step> ]")
	String SolveODE_Syntax();

	@DefaultStringValue("LetterToUnicode")
	String LetterToUnicode();

	@DefaultStringValue("[ <Circle>, <Point>, <Point> ]\n[ <Ellipse>, <Point>, <Point> ]\n[ <Circle>, <Parameter Value>, <Parameter Value> ]\n[ <Ellipse>, <Parameter Value>, <Parameter Value> ]")
	String Arc_Syntax();

	@DefaultStringValue("IntersectionPaths")
	String IntersectionPaths();

	@DefaultStringValue("LimitBelow")
	String LimitBelow();

	@DefaultStringValue("LeftSide")
	String LeftSide();

	@DefaultStringValue("[ <Point>, <Region> ]")
	String IsInRegion_Syntax();

	@DefaultStringValue("VerticalText")
	String VerticalText();

	@DefaultStringValue("[ <Free Point>, ..., <Free Point> ]")
	String RigidPolygon_Syntax();

	@DefaultStringValue("Vertex")
	String Vertex();

	@DefaultStringValue("PlaySound")
	String PlaySound();

	@DefaultStringValue("[ <List of Points> ]\n[ <Point>, ..., <Point> ]\n[ <Point>, <Point>, <Number of Vertices> ]")
	String Polygon_Syntax();

	@DefaultStringValue("RandomPolynomial")
	String RandomPolynomial();

	@DefaultStringValue("Denominator")
	String Denominator();

	@DefaultStringValue("[ <Degrees of Freedom>, <Variable Value> ]")
	String TDistribution_SyntaxCAS();

	@DefaultStringValue("Root")
	String Root();

	@DefaultStringValue("[ <Lambda>, x ]\n[ <Lambda>, <Variable Value> ]\n[ <Lambda>, x, <Boolean Cumulative> ]")
	String Exponential_Syntax();

	@DefaultStringValue("[ <Function> ]\n[ <List of Points> ]")
	String Polynomial_Syntax();

	@DefaultStringValue("[ <Minimum Integer>, <Maximum Integer> ]")
	String Random_SyntaxCAS();

	@DefaultStringValue("[ <Shape>, <Scale>, x ]\n[ <Shape>, <Scale>, <Variable Value> ]\n[ <Shape>, <Scale>, x, <Boolean Cumulative> ]")
	String Weibull_Syntax();

	@DefaultStringValue("Incircle")
	String Incircle();

	@DefaultStringValue("[ <Object>, <Line>, <Ratio> ]")
	String Shear_Syntax();

	@DefaultStringValue("StemPlot")
	String StemPlot();

	@DefaultStringValue("DivisorsList")
	String DivisorsList();

	@DefaultStringValue("[ <Vector> ]")
	String UnitOrthogonalVector_SyntaxCAS();

	@DefaultStringValue("[ <List> ]")
	String CompetitionRank_Syntax();

	@DefaultStringValue("AxisStepX")
	String AxisStepX();

	@DefaultStringValue("FDistribution")
	String FDistribution();

	@DefaultStringValue("AxisStepY")
	String AxisStepY();

	@DefaultStringValue("CorrelationCoefficient")
	String PMCC();

	@DefaultStringValue("Translate")
	String Translate();

	@DefaultStringValue("Zipf")
	String Zipf();

	@DefaultStringValue("[ <List of Points> ]")
	String SampleSDY_Syntax();

	@DefaultStringValue("[ <Function> ]")
	String PartialFractions_Syntax();

	@DefaultStringValue("UnicodeToLetter")
	String UnicodeToLetter();

	@DefaultStringValue("NSolutions")
	String NSolutions();

	@DefaultStringValue("[ <List of Points> ]\n[ <List of Numbers> ]")
	String SigmaXX_Syntax();

	@DefaultStringValue("nPr")
	String nPr();

	@DefaultStringValue("[ <Condition>, <List> ]")
	String KeepIf_Syntax();

	@DefaultStringValue("SetBackgroundColor")
	String SetBackgroundColor();

	@DefaultStringValue("[ <Function> ]")
	String Denominator_Syntax();

	@DefaultStringValue("[ <Expression>, <Value> ]\n[ <Expression>, <Variable>, <Value> ]")
	String LimitAbove_SyntaxCAS();

	@DefaultStringValue("[ <List of Points>, <Degree of Polynomial> ]")
	String FitPoly_SyntaxCAS();

	@DefaultStringValue("[ <Line> ]")
	String Direction_Syntax();

	@DefaultStringValue("Mod")
	String Mod();

	@DefaultStringValue("[ <Polynomial> ]")
	String Degree_Syntax();

	@DefaultStringValue("[ <Expression>, <Substitution List> ]\n[ <Expression>, <from>, <to> ]")
	String Substitute_SyntaxCAS();

	@DefaultStringValue("SemiMajorAxisLength")
	String FirstAxisLength();

	@DefaultStringValue("IntegralBetween")
	String IntegralBetween();

	@DefaultStringValue("[ <Point>, <Number> ]")
	String SetPointSize_Syntax();

	@DefaultStringValue("FillColumn")
	String FillColumn();

	@DefaultStringValue("SetVisibleInView")
	String SetVisibleInView();

	@DefaultStringValue("[ <List of Points> ]\n[ <Point>, ..., <Point> ]")
	String PolyLine_Syntax();

	@DefaultStringValue("[ <Spreadsheet Cell> ]")
	String Row_Syntax();

	@DefaultStringValue("Solutions")
	String Solutions();

	@DefaultStringValue("Ellipse")
	String Ellipse();

	@DefaultStringValue("[ <Point>, <Point>, ... ]")
	String Polyhedron_Syntax();

	@DefaultStringValue("[ <Point>, <Function> ]\n[ <Point>, <Curve> ]")
	String OsculatingCircle_Syntax();

	@DefaultStringValue("Integral")
	String Integral();

	@DefaultStringValue("DivisorsSum")
	String DivisorsSum();

	@DefaultStringValue("[ <List of Points>, <Degree of Polynomial> ]")
	String FitPoly_Syntax();

	@DefaultStringValue("ConvexHull")
	String ConvexHull();

	@DefaultStringValue("[ <Function>, <Start x-Value>, <End x-Value>, <Number of Rectangles> ]")
	String LeftSum_Syntax();

	@DefaultStringValue("Hull")
	String Hull();

	@DefaultStringValue("[ <Number of Successes>, <Probability of Success>, <Probability> ]")
	String InversePascal_Syntax();

	@DefaultStringValue("ZoomOut")
	String ZoomOut();

	@DefaultStringValue("TMeanEstimate")
	String TMeanEstimate();

	@DefaultStringValue("[ <Text> ]")
	String VerticalText_Syntax();

	@DefaultStringValue("[ <Function> ]\n[ <Function>, <Start x-Value>, <End x-Value> ]\n[ <Function>, <Start x-Value>, <End x-Value>, <Boolean Evaluate> ]")
	String Integral_Syntax();

	@DefaultStringValue("[ <List of expressions> ]\n[ <Expression>, <variable>, <start index>, <end index> ]")
	String Product_SyntaxCAS();

	@DefaultStringValue("[ <Number> ]")
	String DivisorsSum_SyntaxCAS();

	@DefaultStringValue("DelaunayTriangulation")
	String DelauneyTriangulation();

	@DefaultStringValue("Radius")
	String Radius();

	@DefaultStringValue("FormulaText")
	String LaTeX();

	@DefaultStringValue("[ <List of Numbers> ]")
	String SD_SyntaxCAS();

	@DefaultStringValue("[ <Expression> ]")
	String Denominator_SyntaxCAS();

	@DefaultStringValue("Unique")
	String Unique();

	@DefaultStringValue("RectangleSum")
	String RectangleSum();

	@DefaultStringValue("Weibull")
	String Weibull();

	@DefaultStringValue("Gamma")
	String Gamma();

	@DefaultStringValue("PreviousPrime")
	String PreviousPrime();

	@DefaultStringValue("Button")
	String Button();

	@DefaultStringValue("[ <Vector> ]")
	String UnitVector_SyntaxCAS();

	@DefaultStringValue("Min")
	String Min();

	@DefaultStringValue("[ <Object>, <Number> ]")
	String SetFilling_Syntax();

	@DefaultStringValue("Ray")
	String Ray();

	@DefaultStringValue("Distance")
	String Distance();

	@DefaultStringValue("[ <Spreadsheet Cell> ]")
	String Column_Syntax();

	@DefaultStringValue("[ <List> ]\n[ <Number>, <Number> ]")
	String Min_SyntaxCAS();

	@DefaultStringValue("[ <List of Points> ]")
	String MeanX_Syntax();

	@DefaultStringValue("[ <Expression>, <Dependent Variable>, <Independent Variable> ]")
	String ImplicitDerivative_SyntaxCAS();

	@DefaultStringValue("[ <List>, <List>, ... ]")
	String ANOVA_Syntax();

	@DefaultStringValue("Uniform")
	String Uniform();

	@DefaultStringValue("[ <Conic> ]")
	String SecondAxisLength_Syntax();

	@DefaultStringValue("[ <Conic> ]\n[ <Polygon> ]\n[ <Polygon>, <Index> ]")
	String Vertex_Syntax();

	@DefaultStringValue("LimitAbove")
	String LimitAbove();

	@DefaultStringValue("[ <Numerator Degrees of Freedom>, <Denominator Degrees of Freedom>, <Variable Value> ]")
	String FDistribution_SyntaxCAS();

	@DefaultStringValue("SDX")
	String SDX();

	@DefaultStringValue("Real")
	String Real();

	@DefaultStringValue("ParseToFunction")
	String ParseToFunction();

	@DefaultStringValue("[ <Number of Elements>, <Exponent>, <Variable Value>, <Boolean Cumulative> ]")
	String Zipf_SyntaxCAS();

	@DefaultStringValue("[ <List of Points> ]")
	String SampleSDX_Syntax();

	@DefaultStringValue("[ <End Value> ]\n[ <Expression>, <Variable>, <Start Value>, <End Value> ]\n[ <Expression>, <Variable>, <Start Value>, <End Value>, <Increment> ]")
	String Sequence_Syntax();

	@DefaultStringValue("[ <List> ]\n[ <List>, <Adjustment -1|0|1> ]")
	String StemPlot_Syntax();

	@DefaultStringValue("SDY")
	String SDY();

	@DefaultStringValue("Div")
	String Div();

	@DefaultStringValue("[ <List of Data>, <List of Frequencies> ]\n[ <List of Raw Data>, <Width of Bars> ]\n[ <List of Data>, <List of Frequencies>, <Width of Bars> ]\n[ <Start Value>, <End Value>, <List of Heights> ]\n[ <Start Value>, <End Value>, <Expression>, <Variable>, <From Number>, <To Number> ]\n[ <Start Value>, <End Value>, <Expression>, <Variable>, <From Number>, <To Number>, <Step Width> ]")
	String BarChart_Syntax();

	@DefaultStringValue("[ <Start Cell>, <End Cell> ]")
	String CellRange_Syntax();

	@DefaultStringValue("[ <Object> ]")
	String CopyFreeObject_Syntax();

	@DefaultStringValue("ToPolar")
	String ToPolar();

	@DefaultStringValue("[ <Function>, <Function>, <Start x-Value>, <End x-Value> ]\n[ <Function>, <Function>, <Start x-Value>, <End x-Value>, <Boolean Evaluate> ]")
	String IntegralBetween_Syntax();

	@DefaultStringValue("[ <Point>, <Point>, <Point>, <Number> ]")
	String TriangleCenter_Syntax();

	@DefaultStringValue("[ <Number> ]")
	String ToolImage_Syntax();

	@DefaultStringValue("SetLineThickness")
	String SetLineThickness();

	@DefaultStringValue("[ <Integer> ]")
	String Ordinal_Syntax();

	@DefaultStringValue("[ <Polygon> ]\n[ <Conic> ]\n[ <Locus> ]")
	String Perimeter_Syntax();

	@DefaultStringValue("SetCoords")
	String SetCoords();

	@DefaultStringValue("[ <Complex Number> ]")
	String Real_SyntaxCAS();

	@DefaultStringValue("[ <Vector> ]\n[ <Line> ]\n[ <Segment> ]")
	String UnitVector_Syntax();

	@DefaultStringValue("[ <Equation> ]\n[ <Equation>, <Variable> ]\n[ <Equation>, <Variable = starting value> ]\n[ <List of Equations>, <List of Variables)> ]")
	String NSolve_SyntaxCAS();

	@DefaultStringValue("Shear")
	String Shear();

	@DefaultStringValue("[ <List of Numbers> ]\n[ <List of Numbers>, <Number of Elements> ]")
	String Product_Syntax();

	@DefaultStringValue("[ <Mean>, <Probability> ]")
	String InversePoisson_Syntax();

	@DefaultStringValue("[ <List of Raw Data> ]\n[ <Boolean Cumulative>, <List of Raw Data> ]\n[ <List of Class Boundaries>, <List of Raw Data>, ]\n[ <Boolean Cumulative>, <List of Class Boundaries>, <List of Raw Data> ]\n[ <List of Class Boundaries>, <List of Raw Data>, <Use Density>, <Density Scale Factor (optional)> ]\n[ <Boolean Cumulative>, <List of Class Boundaries>, <List of Raw Data>, <Use Density>, <Density Scale Factor (optional)> ]")
	String Frequency_Syntax();

	@DefaultStringValue("[ <Path>, <Point> ]")
	String ClosestPoint_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]\n[ <Number>, <Number> ]")
	String GCD_Syntax();

	@DefaultStringValue("[ <Equation> ]\n[ <Equation>, <Variable> ]\n[ <List of Equations>, <List of Variables> ]")
	String Solutions_SyntaxCAS();

	@DefaultStringValue("[ <List> ]")
	String Sort_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String FitPow_Syntax();

	@DefaultStringValue("[ <Line>, <Polygon> ]\n[ <Line>, <Conic> ]")
	String IntersectionPaths_Syntax();

	@DefaultStringValue("LinearEccentricity")
	String Excentricity();
	
//	@DefaultStringValue("Excentricity")
//	String LinearEccentricity();

	@DefaultStringValue("[ <Polynomial> ]")
	String Coefficients_SyntaxCAS();

	@DefaultStringValue("First")
	String First();

	@DefaultStringValue("TTest2")
	String TTest2();

	@DefaultStringValue("[ <Point>, <Point>, <Direction> ]")
	String Icosahedron_Syntax();

	@DefaultStringValue("[ <Polynomial> ]\n[ <Conic> ]")
	String Coefficients_Syntax();

	@DefaultStringValue("[ <Expression> ]")
	String Numerator_SyntaxCAS();

	@DefaultStringValue("[ <Number> ]")
	String PrimeFactors_Syntax();

	@DefaultStringValue("ColumnName")
	String ColumnName();

	@DefaultStringValue("Tetrahedron")
	String Tetrahedron();

	@DefaultStringValue("Top")
	String Top();

	@DefaultStringValue("[ <Expression>, <Expression>, <Parameter Variable>, <Start Value>, <End Value> ]")
	String CurveCartesian_Syntax();

	@DefaultStringValue("[ <Number of Trials>, <Probability of Success> ]\n[ <Number of Trials>, <Probability of Success>, <Boolean Cumulative> ]\n[ <Number of Trials>, <Probability of Success>, <Variable Value>, <Boolean Cumulative> ]")
	String BinomialDist_Syntax();

	@DefaultStringValue("[ <List of Numbers> ]\n[ <List of Polynomials> ]\n[ <Number>, <Number> ]\n[ <Polynomial>, <Polynomial> ]")
	String LCM_SyntaxCAS();

	@DefaultStringValue("[ ]\n[ <Caption> ]")
	String Button_Syntax();

	@DefaultStringValue("[ <Matrix> ]")
	String ReducedRowEchelonForm_SyntaxCAS();

	@DefaultStringValue("Take")
	String Take();

	@DefaultStringValue("[ <Equation> ]\n[ { <Equation> } ]")
	String LeftSide_SyntaxCAS();

	@DefaultStringValue("Row")
	String Row();

	@DefaultStringValue("[ <Function>, <Value> ]")
	String Limit_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String Voronoi_Syntax();

	@DefaultStringValue("[ ]\n[ <Linked Object> ]")
	String Textfield_Syntax();

	@DefaultStringValue("[ <List of Points> ]")
	String FitSin_Syntax();

	@DefaultStringValue("Iteration")
	String Iteration();

	@DefaultStringValue("[ <Object>, \"<Color>\" ]\n[ <Object>, <Red>, <Green>, <Blue> ]")
	String SetColor_Syntax();

	@DefaultStringValue("[ <Expression>, <Expression>, <Parameter Variable>, <Start Value>, <End Value> ]\n[ <Expression>, <Expression>, <Expression>, <Parameter Variable>, <Start Value>, <End Value> ]")
	String CurveCartesian_Syntax3D();

	@DefaultStringValue("TTest")
	String TTest();

	@DefaultStringValue("[ <List of Segments>, <Start Point>, <End Point>, <Boolean Weighted> ]")
	String ShortestDistance_Syntax();

	@DefaultStringValue("Ends")
	String Ends();

	@DefaultStringValue("ANOVA")
	String ANOVA();

	@DefaultStringValue("DotPlot")
	String DotPlot();

	@DefaultStringValue("[ <List of Points> ]")
	String FitExp_Syntax();

	@DefaultStringValue("Object")
	String Object();

	@DefaultStringValue("[ <Number> ]\n[ <Point> ]")
	String SurdText_Syntax();

	@DefaultStringValue("[ <Object>, <Angle> ]\n[ <Object>, <Angle>, <Point> ]")
	String Rotate_Syntax();

	@DefaultStringValue("[ <Condition>, <Then> ]\n[ <Condition>, <Then>, <Else> ]")
	String If_Syntax();

	@DefaultStringValue("[ <Midpoint>, <Point>, <Point> ]")
	String CircleArc_Syntax();

	@DefaultStringValue("[ <List>, <List> ]")
	String Intersection_Syntax();

	@DefaultStringValue("IntersectRegion")
	String IntersectRegion();

	@DefaultStringValue("Intersect")
	String Intersect();

	@DefaultStringValue("[ <Object>, <Layer> ]")
	String SetLayer_Syntax();

	@DefaultStringValue("CrossRatio")
	String CrossRatio();

	@DefaultStringValue("GeometricMean")
	String GeometricMean();

	@DefaultStringValue("[ <Polynomial> ]")
	String Degree_SyntaxCAS();

	@DefaultStringValue("[ <List of Points> ]\n[ <List of x-Coordinates>, <List of y-Coordinates> ]")
	String PMCC_Syntax();

	@DefaultStringValue("Reverse")
	String Reverse();

	@DefaultStringValue("SetTooltipMode")
	String SetTooltipMode();

	@DefaultStringValue("[ <Function> ]\n[ <Curve> ]\n[ <Function>, <Number> ]\n[ <Curve>, <Number> ]")
	String Derivative_Syntax();

	@DefaultStringValue("TDistribution")
	String TDistribution();

	@DefaultStringValue("[ <Scale Factor> ]\n[ <Scale Factor>, <Center Point> ]\n[ <Min x>, <Min y>, <Max x>, <Max y> ]")
	String ZoomIn_Syntax();

	@DefaultStringValue("[ <Number of Trials>, <Probability> ]")
	String RandomBinomial_Syntax();

	@DefaultStringValue("Triangular")
	String Triangular();

	@DefaultStringValue("[ <Point>, <Conic> ]\n[ <Point>, <Function> ]\n[ <Point on Curve>, <Curve> ]\n[ <x-Value>, <Function> ]\n[ <Line>, <Conic> ]\n[ <Circle>, <Circle> ]")
	String Tangent_Syntax();

	@DefaultStringValue("Area")
	String Area();

	@DefaultStringValue("[ <Median>, <Scale>, <Variable Value> ]")
	String Cauchy_SyntaxCAS();

	@DefaultStringValue("Spearman")
	String Spearman();

	@DefaultStringValue("[ <Conic> ]")
	String Center_Syntax();

	@DefaultStringValue("[ <Polynomial> ]")
	String Root_SyntaxCAS();

	@DefaultStringValue("ShowLabel")
	String ShowLabel();

	@DefaultStringValue("[ <Object> ]\n[ <Object>, <Boolean for Substitution of Variables> ]\n[ <Object>, <Point> ]\n[ <Object>, <Point>, <Boolean for Substitution of Variables> ]\n[ <Object>, <Point>, <Boolean for Substitution of Variables>, <Boolean for LaTeX formula> ]")
	String Text_Syntax();

	@DefaultStringValue("[ <Polynomial> ]\n[ <Function>, <Start x-Value>, <End x-Value> ]")
	String Extremum_Syntax();

	@DefaultStringValue("[ <Conic> ]\n[ <Function> ]\n[ <Implicit Curve> ]")
	String Asymptote_Syntax();

	@DefaultStringValue("Asymptote")
	String Asymptote();

	@DefaultStringValue("IterationList")
	String IterationList();

	@DefaultStringValue("[ ]")
	String AxisStepX_Syntax();

	@DefaultStringValue("Numeric")
	String Numeric();

	@DefaultStringValue("Poisson")
	String Poisson();

	@DefaultStringValue("[ <Shape>, <Scale>, <Variable Value> ]")
	String Weibull_SyntaxCAS();

	@DefaultStringValue("FillRow")
	String FillRow();

	@DefaultStringValue("[ <Expression> ]\n[ <Expression>, <significant figures> ]")
	String Numeric_SyntaxCAS();

	@DefaultStringValue("[ <List of Data>, <Number of Classes> ]\n[ <List of Data>, <Start>, <Width of Classes> ]")
	String Classes_Syntax();

	@DefaultStringValue("[ ]")
	String UpdateConstruction_Syntax();

	@DefaultStringValue("[ <Dividend Number>, <Divisor Number> ]\n[ <Dividend Polynomial>, <Divisor Polynomial> ]")
	String Div_Syntax();

	@DefaultStringValue("[ <Segment> ]\n[ <Point>, <Point> ]")
	String LineBisector_Syntax();

	@DefaultStringValue("[ <Object> ]")
	String Defined_Syntax();

	@DefaultStringValue("PerpendicularVector")
	String OrthogonalVector();

	@DefaultStringValue("[ <List>, <List>, ... ]\n[ <List>, <List>, ..., <Alignment of Text> ]")
	String TableText_Syntax();

	@DefaultStringValue("TrapezoidalSum")
	String TrapezoidalSum();

	@DefaultStringValue("Exponential")
	String Exponential();

	@DefaultStringValue("[ <Mean>, <Standard Deviation>, x ]\n[ <Mean>, <Standard Deviation>, <Variable Value> ]\n[ <Mean>, <Standard Deviation>, x, <Boolean Cumulative> ]")
	String Normal_Syntax();

	@DefaultStringValue("NormalQuantilePlot")
	String NormalQuantilePlot();

	@DefaultStringValue("InverseGamma")
	String InverseGamma();

	@DefaultStringValue("[ <Number of Trials>, <Probability of Success>, <Probability> ]")
	String InverseBinomial_Syntax();

	@DefaultStringValue("LogNormal")
	String LogNormal();

	@DefaultStringValue("InverseLogNormal")
	String InverseLogNormal();

	@DefaultStringValue("Trilinear")
	String Trilinear();

	@DefaultStringValue("[ <Scale Factor> ]\n[ <Scale Factor>, <Center Point> ]")
	String ZoomOut_Syntax();

	@DefaultStringValue("[ <Row>, <List> ]")
	String FillRow_Syntax();

	@DefaultStringValue("[ <Quadratic Function> ]")
	String CompleteSquare_Syntax();

	@DefaultStringValue("InfiniteCylinder")
	String InfiniteCylinder();

	@DefaultStringValue("Extremum")
	String Extremum();

	@DefaultStringValue("[ <Start Point>, <Point> ]\n[ <Start Point>, <Direction Vector> ]")
	String Ray_Syntax();

	@DefaultStringValue("[ <List> ]")
	String PointList_Syntax();

	@DefaultStringValue("[ <Parabola> ]")
	String Parameter_Syntax();

	@DefaultStringValue("Dimension")
	String Dimension();

	@DefaultStringValue("[ <List of Points> ]")
	String FitGrowth_Syntax();

	@DefaultStringValue("[ <Lower Bound>, <Upper Bound>, x ]\n[ <Lower Bound>, <Upper Bound>, <Variable Value> ]\n[ <Lower Bound>, <Upper Bound>, x, <Boolean Cumulative> ]")
	String Uniform_Syntax();

	@DefaultStringValue("[ <Object> ]")
	String Name_Syntax();

	@DefaultStringValue("Dot")
	String Dot();

	@DefaultStringValue("Text")
	String Text();

	@DefaultStringValue("[ <Number> ]")
	String Identity_Syntax();

	@DefaultStringValue("CircularArc")
	String CircleArc();

	@DefaultStringValue("[ <Point>, <Conic> ]")
	String Polar_Syntax();

	@DefaultStringValue("RandomBetween")
	String Random();

	@DefaultStringValue("[ <Function> ]\n[ <Text> ]")
	String Simplify_Syntax();
	
	@DefaultStringValue("ShowAxes")
	String ShowAxes();
	
	@DefaultStringValue("[]\n[ <Boolean> ]\n[ <View>, <Boolean> ]")
	String ShowAxes_Syntax();
	
	@DefaultStringValue("ShowGrid")
	String ShowGrid();
	
	@DefaultStringValue("[]\n[ <Boolean> ]\n[ <View>, <Boolean> ]")
	String ShowGrid_Syntax();

	@DefaultStringValue("AreCollinear")
	String AreCollinear();
	
	@DefaultStringValue("[ <Point>, <Point>, <Point> ]")
	String AreCollinear_Syntax();

	@DefaultStringValue("AreParallel")
	String AreParallel();
	
	@DefaultStringValue("[ <Line>, <Line> ]")
	String AreParallel_Syntax();
	
	@DefaultStringValue("AreConcyclic")
	String AreConcyclic();
	
	@DefaultStringValue("[ <Point>, <Point>, <Point>, <Point> ]")
	String AreConcyclic_Syntax();

	@DefaultStringValue("CenterView")
	String CenterView();
	
	@DefaultStringValue("[ <Center Point> ]")
	String CenterView_Syntax();

	@DefaultStringValue("ScientificText")
	String ScientificText();
	
	@DefaultStringValue("[ <Number> ]\n[ <Number>, <Precision> ]")
	String ScientificText_Syntax();

	@DefaultStringValue("SlopeField")
	String SlopeField();
	
	@DefaultStringValue("[ <f(x,y)> ]\n[ <f(x,y)>, <Number n> ]\n[ <f(x,y)>, <Number n>, <Length Multiplier a> ]\n[ <f(x,y)>, <Number n>, <Length Multiplier a>, <Min x>, <Min y>, <Max x>, <Max y> ]")
	String SlopeField_Syntax();

	 @DefaultStringValue("ArePerpendicular")
	 String ArePerpendicular();

	 @DefaultStringValue("[ <Line>, <Line> ]")
	 String ArePerpendicular_Syntax();
	 
	 @DefaultStringValue("AreEqual")
	 String AreEqual();
	 
	 @DefaultStringValue("[ <Object>, <Object> ]")
	 String AreEqual_Syntax();
	 
	 @DefaultStringValue("AreConcurrent")
	 String AreConcurrent();
	 
	 @DefaultStringValue("[ <Line>, <Line>, <Line> ]")
	 String AreConcurrent_Syntax();
	 
	 @DefaultStringValue("SetTrace")
	 String SetTrace();
	 
	 @DefaultStringValue("[ <Object>, <true | false> ]")
	 String SetTrace_Syntax();
	 
	 @DefaultStringValue("FromBase")
	 String FromBase();

	 @DefaultStringValue("[ <Number as Text>, <Base> ]")
	 String FromBase_Syntax();
	 
	 @DefaultStringValue("ToBase")
	 String ToBase();

	 @DefaultStringValue("[ <Number>, <Base> ]")
	 String ToBase_Syntax();
	 
	 @DefaultStringValue("ContinuedFraction")
	 String ContinuedFraction();

	 @DefaultStringValue("[ <Number> ]\n[ <Number>, <Level> ]\n[ <Number>, <Level (optional)>, <Shorthand> ]")
	 String ContinuedFraction_Syntax();

	 @DefaultStringValue("AttachCopyToView")
	 String AttachCopyToView();

	 @DefaultStringValue("[ <Object>, <View 0|1|2> ]\n[ <Object>, <View 0|1|2>, <Point 1>, <Point 2>,<Screen Point 1>, <Screen Point 2>]")
	 String AttachCopyToView_Syntax();
	 
	 
}
