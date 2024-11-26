/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.commands;

import java.util.HashMap;

import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

@SuppressWarnings("javadoc")
public enum Commands implements CommandsConstants,

		GetCommand {

	// Subtables are separated by comment lines here.

	// =================================================================
	// Algebra & Numbers
	// =============================================================
	Mod(TABLE_ALGEBRA),

	Div(TABLE_ALGEBRA),

	Min(TABLE_ALGEBRA),

	Max(TABLE_ALGEBRA),

	LCM(TABLE_ALGEBRA),

	GCD(TABLE_ALGEBRA),

	Expand(TABLE_ALGEBRA),

	Factor(TABLE_ALGEBRA),

	Simplify(TABLE_ALGEBRA),

	PrimeFactors(TABLE_ALGEBRA),

	CompleteSquare(TABLE_ALGEBRA),

	ToBase(TABLE_ALGEBRA),

	FromBase(TABLE_ALGEBRA),

	Division(TABLE_ALGEBRA),

	Divisors(TABLE_ALGEBRA),

	DivisorsList(TABLE_ALGEBRA),

	DivisorsSum(TABLE_ALGEBRA),

	IsPrime(TABLE_ALGEBRA),

	LeftSide(TABLE_ALGEBRA),

	NextPrime(TABLE_ALGEBRA),

	RightSide(TABLE_ALGEBRA),

	PreviousPrime(TABLE_ALGEBRA),

	IsFactored(TABLE_ALGEBRA),

	// =================================================================
	// Geometry
	// =============================================================
	Line(TABLE_GEOMETRY),

	Ray(TABLE_GEOMETRY),

	AngularBisector(TABLE_GEOMETRY),

	OrthogonalLine(TABLE_GEOMETRY),

	Tangent(TABLE_GEOMETRY),

	Segment(TABLE_GEOMETRY),

	Slope(TABLE_GEOMETRY),

	Angle(TABLE_GEOMETRY),

	InteriorAngles(TABLE_GEOMETRY),

	Direction(TABLE_GEOMETRY),

	Point(TABLE_GEOMETRY),

	Midpoint(TABLE_GEOMETRY),

	LineBisector(TABLE_GEOMETRY),

	Intersect(TABLE_GEOMETRY),

	IntersectPath(TABLE_GEOMETRY),

	IntersectRegion(TABLE_ENGLISH),

	Distance(TABLE_GEOMETRY),

	Length(TABLE_GEOMETRY),

	Radius(TABLE_GEOMETRY),

	Type(TABLE_GEOMETRY),

	CircleArc(TABLE_GEOMETRY),

	Arc(TABLE_GEOMETRY),

	Sector(TABLE_GEOMETRY),

	CircleSector(TABLE_GEOMETRY),

	CircumcircleSector(TABLE_GEOMETRY),

	CircumcircleArc(TABLE_GEOMETRY),

	Polygon(TABLE_GEOMETRY),

	RigidPolygon(TABLE_GEOMETRY),

	Area(TABLE_GEOMETRY),

	Circumference(TABLE_GEOMETRY),

	Perimeter(TABLE_GEOMETRY),

	Locus(TABLE_GEOMETRY),

	Centroid(TABLE_GEOMETRY),

	TriangleCenter(TABLE_GEOMETRY),

	Barycenter(TABLE_GEOMETRY),

	Trilinear(TABLE_GEOMETRY),

	Cubic(TABLE_GEOMETRY),

	TriangleCurve(TABLE_GEOMETRY),

	Vertex(TABLE_GEOMETRY),

	PolyLine(TABLE_GEOMETRY),

	PenStroke(TABLE_GEOMETRY),

	PointIn(TABLE_GEOMETRY),

	AffineRatio(TABLE_GEOMETRY),

	CrossRatio(TABLE_GEOMETRY),

	ClosestPoint(TABLE_GEOMETRY),

	ClosestPointRegion(TABLE_GEOMETRY),

	Prove(TABLE_GEOMETRY),

	ProveDetails(TABLE_GEOMETRY),

	AreCollinear(TABLE_GEOMETRY),

	AreParallel(TABLE_GEOMETRY),

	AreConcyclic(TABLE_GEOMETRY),

	ArePerpendicular(TABLE_GEOMETRY),

	AreEqual(TABLE_GEOMETRY),

	AreConcurrent(TABLE_GEOMETRY),

	AreCongruent(TABLE_GEOMETRY),

	IsTangent(TABLE_GEOMETRY),

	LocusEquation(TABLE_GEOMETRY),

	Envelope(TABLE_GEOMETRY),

	Volume(TABLE_3D),

	Difference(TABLE_GEOMETRY),

	// =============================================================
	// text
	// =============================================================
	Text(TABLE_TEXT),

	LaTeX(TABLE_TEXT),

	LetterToUnicode(TABLE_TEXT),

	TextToUnicode(TABLE_TEXT),

	UnicodeToText(TABLE_TEXT),

	UnicodeToLetter(TABLE_TEXT),

	FractionText(TABLE_TEXT),

	SurdText(TABLE_TEXT),

	ScientificText(TABLE_TEXT),

	TableText(TABLE_TEXT),

	VerticalText(TABLE_TEXT),

	RotateText(TABLE_TEXT),

	Ordinal(TABLE_TEXT),

	ContinuedFraction(TABLE_TEXT),

	ReplaceAll(TABLE_TEXT),

	Split(TABLE_TEXT),

	// =============================================================
	// logical
	// =============================================================
	If(TABLE_LOGICAL),

	CountIf(TABLE_LOGICAL),

	IsInteger(TABLE_LOGICAL),

	KeepIf(TABLE_LOGICAL),

	Relation(TABLE_LOGICAL),

	Defined(TABLE_LOGICAL),

	IsInRegion(TABLE_LOGICAL),

	// =============================================================
	// functions & calculus
	// =============================================================
	Root(TABLE_FUNCTION),

	Roots(TABLE_FUNCTION),

	/**
	 * bad translation, actually InflectionPoint
	 * name just used internally and in XML
	 */
	TurningPoint(TABLE_FUNCTION),

	Polynomial(TABLE_FUNCTION),

	Function(TABLE_FUNCTION),

	Extremum(TABLE_FUNCTION),

	RemovableDiscontinuity(TABLE_FUNCTION),

	CurveCartesian(TABLE_FUNCTION),

	ParametricDerivative(TABLE_FUNCTION),

	Derivative(TABLE_FUNCTION),

	NDerivative(TABLE_FUNCTION),

	Integral(TABLE_FUNCTION),

	IntegralBetween(TABLE_FUNCTION),

	LowerSum(TABLE_FUNCTION),

	LeftSum(TABLE_FUNCTION),

	RectangleSum(TABLE_FUNCTION),

	TaylorSeries(TABLE_FUNCTION),

	UpperSum(TABLE_FUNCTION),

	TrapezoidalSum(TABLE_FUNCTION),

	Limit(TABLE_FUNCTION),

	LimitBelow(TABLE_FUNCTION),

	LimitAbove(TABLE_FUNCTION),

	Factors(TABLE_FUNCTION),

	Degree(TABLE_FUNCTION),

	Coefficients(TABLE_FUNCTION),

	PartialFractions(TABLE_FUNCTION),

	Numerator(TABLE_FUNCTION),

	Denominator(TABLE_FUNCTION),

	ComplexRoot(TABLE_FUNCTION),

	SolveODE(TABLE_FUNCTION),

	SlopeField(TABLE_FUNCTION),

	Iteration(TABLE_FUNCTION),

	PathParameter(TABLE_FUNCTION),

	Asymptote(TABLE_FUNCTION),

	CurvatureVector(TABLE_FUNCTION),

	Curvature(TABLE_FUNCTION),

	OsculatingCircle(TABLE_FUNCTION),

	IterationList(TABLE_FUNCTION),

	RootList(TABLE_FUNCTION),

	ImplicitCurve(TABLE_FUNCTION),

	ImplicitDerivative(TABLE_FUNCTION),

	NSolveODE(TABLE_FUNCTION),

	Spline(TABLE_FUNCTION),

	// see
	// Feature.IMPLICIT_CURVES
	ImplicitSurface(TABLE_ENGLISH),

	Normalize(TABLE_FUNCTION),

	SVD(TABLE_FUNCTION),

	// =============================================================
	// conics
	// =============================================================
	Ellipse(TABLE_CONIC),

	Hyperbola(TABLE_CONIC),

	SecondAxisLength(TABLE_CONIC),

	SecondAxis(TABLE_CONIC),

	Directrix(TABLE_CONIC),

	Diameter(TABLE_CONIC),

	Conic(TABLE_CONIC),

	FirstAxis(TABLE_CONIC),

	Circle(TABLE_CONIC),

	Incircle(TABLE_CONIC),

	Semicircle(TABLE_CONIC),

	FirstAxisLength(TABLE_CONIC),

	Parabola(TABLE_CONIC),

	Focus(TABLE_CONIC),

	Parameter(TABLE_CONIC),

	Center(TABLE_CONIC),

	Polar(TABLE_CONIC),

	// linear eccentricity
	Excentricity(TABLE_CONIC),

	Eccentricity(TABLE_CONIC),

	Axes(TABLE_CONIC),

	// =============================================================
	// lists
	// =============================================================
	Sort(TABLE_LIST),

	First(TABLE_LIST),

	Last(TABLE_LIST),

	Take(TABLE_LIST),

	RemoveUndefined(TABLE_LIST),

	Reverse(TABLE_LIST),

	Element(TABLE_LIST),

	IndexOf(TABLE_LIST),

	Append(TABLE_LIST),

	Join(TABLE_LIST),

	Flatten(TABLE_LIST),

	Insert(TABLE_LIST),

	Sequence(TABLE_LIST),

	SelectedElement(TABLE_LIST),

	SelectedIndex(TABLE_LIST),

	RandomElement(TABLE_LIST),

	Product(TABLE_LIST),

	Frequency(TABLE_LIST),

	Unique(TABLE_LIST),

	Classes(TABLE_LIST),

	Zip(TABLE_LIST),

	Intersection(TABLE_LIST),

	PointList(TABLE_LIST),

	OrdinalRank(TABLE_LIST),

	TiedRank(TABLE_LIST),

	Union(TABLE_LIST),

	Remove(TABLE_LIST),

	// =============================================================
	// charts
	// =============================================================
	BarChart(TABLE_CHARTS),

	BoxPlot(TABLE_CHARTS),

	Histogram(TABLE_CHARTS),

	HistogramRight(TABLE_CHARTS),

	DotPlot(TABLE_CHARTS),

	StemPlot(TABLE_CHARTS),

	ResidualPlot(TABLE_CHARTS),

	FrequencyPolygon(TABLE_CHARTS),

	NormalQuantilePlot(TABLE_CHARTS),

	FrequencyTable(TABLE_CHARTS),

	StickGraph(TABLE_CHARTS),

	StepGraph(TABLE_CHARTS),

	LineGraph(TABLE_CHARTS),

	PieChart(TABLE_CHARTS),

	ContingencyTable(TABLE_CHARTS),

	// =============================================================
	// statistics
	// =============================================================
	Sum(TABLE_STATISTICS),

	Mean(TABLE_STATISTICS),

	mean(TABLE_STATISTICS),

	Variance(TABLE_STATISTICS),

	SD(TABLE_STATISTICS),

	/* alias for SD */
	stdev(TABLE_STATISTICS),

	MAD(TABLE_STATISTICS),

	mad(TABLE_STATISTICS),

	SampleVariance(TABLE_STATISTICS),

	SampleSD(TABLE_STATISTICS),

	/* alias for SampleSD */
	stdevp(TABLE_STATISTICS),

	Median(TABLE_STATISTICS),

	Q1(TABLE_STATISTICS),

	Quartile1(TABLE_ENGLISH),

	Q3(TABLE_STATISTICS),

	Quartile3(TABLE_ENGLISH),

	Mode(TABLE_STATISTICS),

	SigmaXX(TABLE_STATISTICS),

	SigmaXY(TABLE_STATISTICS),

	SigmaYY(TABLE_STATISTICS),

	Covariance(TABLE_STATISTICS),

	SXY(TABLE_STATISTICS),

	SXX(TABLE_STATISTICS),

	SYY(TABLE_STATISTICS),

	MeanX(TABLE_STATISTICS),

	MeanY(TABLE_STATISTICS),

	PMCC(TABLE_STATISTICS),

	SampleSDX(TABLE_STATISTICS),

	SampleSDY(TABLE_STATISTICS),

	SDX(TABLE_STATISTICS),

	SDY(TABLE_STATISTICS),

	FitLineY(TABLE_STATISTICS),

	FitLineX(TABLE_STATISTICS),

	FitPoly(TABLE_STATISTICS),

	FitExp(TABLE_STATISTICS),

	FitLog(TABLE_STATISTICS),

	FitPow(TABLE_STATISTICS),

	Fit(TABLE_STATISTICS),

	FitGrowth(TABLE_STATISTICS),

	FitSin(TABLE_STATISTICS),

	FitLogistic(TABLE_STATISTICS),

	SumSquaredErrors(TABLE_STATISTICS),

	RSquare(TABLE_STATISTICS),

	Sample(TABLE_STATISTICS),

	Shuffle(TABLE_STATISTICS),

	FitImplicit(TABLE_STATISTICS),

	Spearman(TABLE_STATISTICS),

	TTest(TABLE_STATISTICS),

	ZProportionTest(TABLE_STATISTICS),

	ZProportion2Test(TABLE_STATISTICS),

	ZProportionEstimate(TABLE_STATISTICS),

	ZProportion2Estimate(TABLE_STATISTICS),

	ZMeanEstimate(TABLE_STATISTICS),

	ZMean2Estimate(TABLE_STATISTICS),

	ZMeanTest(TABLE_STATISTICS),

	ZMean2Test(TABLE_STATISTICS),

	TTestPaired(TABLE_STATISTICS),

	TTest2(TABLE_STATISTICS),

	TMeanEstimate(TABLE_STATISTICS),

	TMean2Estimate(TABLE_STATISTICS),

	ChiSquaredTest(TABLE_STATISTICS),

	ANOVA(TABLE_STATISTICS),

	Percentile(TABLE_STATISTICS),

	GeometricMean(TABLE_STATISTICS),

	HarmonicMean(TABLE_STATISTICS),

	RootMeanSquare(TABLE_STATISTICS),

	// =============================================================
	// probability
	// =============================================================
	Random(TABLE_PROBABILITY),

	RandomNormal(TABLE_PROBABILITY),

	RandomUniform(TABLE_PROBABILITY),

	RandomBinomial(TABLE_PROBABILITY),

	RandomPoisson(TABLE_PROBABILITY),

	Normal(TABLE_PROBABILITY),

	LogNormal(TABLE_PROBABILITY),

	Logistic(TABLE_PROBABILITY),

	InverseLogistic(TABLE_PROBABILITY),

	InverseNormal(TABLE_PROBABILITY),

	nCr(TABLE_PROBABILITY),

	Binomial(TABLE_PROBABILITY),

	BinomialDist(TABLE_PROBABILITY),

	Bernoulli(TABLE_PROBABILITY),

	InverseBinomial(TABLE_PROBABILITY),

	InverseBinomialMinimumTrials(TABLE_PROBABILITY),

	TDistribution(TABLE_PROBABILITY),

	InverseTDistribution(TABLE_PROBABILITY),

	FDistribution(TABLE_PROBABILITY),

	InverseFDistribution(TABLE_PROBABILITY),

	BetaDist(TABLE_PROBABILITY),

	InverseBeta(TABLE_PROBABILITY),

	Gamma(TABLE_PROBABILITY),

	InverseGamma(TABLE_PROBABILITY),

	Cauchy(TABLE_PROBABILITY),

	InverseCauchy(TABLE_PROBABILITY),

	ChiSquared(TABLE_PROBABILITY),

	InverseChiSquared(TABLE_PROBABILITY),

	Exponential(TABLE_PROBABILITY),

	InverseExponential(TABLE_PROBABILITY),

	HyperGeometric(TABLE_PROBABILITY),

	InverseHyperGeometric(TABLE_PROBABILITY),

	Pascal(TABLE_PROBABILITY),

	InversePascal(TABLE_PROBABILITY),

	Poisson(TABLE_PROBABILITY),

	InversePoisson(TABLE_PROBABILITY),

	Weibull(TABLE_PROBABILITY),

	InverseWeibull(TABLE_PROBABILITY),

	Zipf(TABLE_PROBABILITY),

	InverseZipf(TABLE_PROBABILITY),

	Triangular(TABLE_PROBABILITY),

	Uniform(TABLE_PROBABILITY),

	Erlang(TABLE_PROBABILITY),

	InverseLogNormal(TABLE_PROBABILITY),

	RandomPolynomial(TABLE_PROBABILITY),

	RandomDiscrete(TABLE_PROBABILITY),

	RandomPointIn(TABLE_PROBABILITY),

	// =============================================================
	// vector & matrix
	// =============================================================
	ApplyMatrix(TABLE_VECTOR),

	UnitVector(TABLE_VECTOR),

	Vector(TABLE_VECTOR),

	UnitOrthogonalVector(TABLE_VECTOR),

	OrthogonalVector(TABLE_VECTOR),

	Invert(TABLE_VECTOR),

	Transpose(TABLE_VECTOR),

	ReducedRowEchelonForm(TABLE_VECTOR),

	Determinant(TABLE_VECTOR),

	Identity(TABLE_VECTOR),

	Dimension(TABLE_VECTOR),

	MatrixRank(TABLE_VECTOR),

	// =============================================================
	// transformations
	// =============================================================
	Mirror(TABLE_TRANSFORMATION),

	Dilate(TABLE_TRANSFORMATION),

	Rotate(TABLE_TRANSFORMATION),

	Translate(TABLE_TRANSFORMATION),

	Shear(TABLE_TRANSFORMATION),

	Stretch(TABLE_TRANSFORMATION),

	// =============================================================
	// spreadsheet
	// =============================================================
	CellRange(TABLE_SPREADSHEET),

	Row(TABLE_SPREADSHEET),

	Column(TABLE_SPREADSHEET),

	ColumnName(TABLE_SPREADSHEET),

	FillRow(TABLE_SPREADSHEET),

	FillColumn(TABLE_SPREADSHEET),

	FillCells(TABLE_SPREADSHEET),

	Cell(TABLE_SPREADSHEET),

	// =============================================================
	// financial
	// =============================================================
	Rate(TABLE_FINANCIAL),

	Periods(TABLE_FINANCIAL),

	Payment(TABLE_FINANCIAL),

	FutureValue(TABLE_FINANCIAL),

	PresentValue(TABLE_FINANCIAL),

	// =============================================================
	// scripting
	// =============================================================
	CopyFreeObject(TABLE_SCRIPTING),

	DataFunction(TABLE_SCRIPTING),

	SetColor(TABLE_SCRIPTING),

	SetBackgroundColor(TABLE_SCRIPTING),

	SetDecoration(TABLE_SCRIPTING),

	SetDynamicColor(TABLE_SCRIPTING),

	SetConditionToShowObject(TABLE_SCRIPTING),

	SetFilling(TABLE_SCRIPTING),

	SetLevelOfDetail(TABLE_SCRIPTING),

	SetLineThickness(TABLE_SCRIPTING),

	SetLineStyle(TABLE_SCRIPTING),

	SetPointStyle(TABLE_SCRIPTING),

	SetPointSize(TABLE_SCRIPTING),

	SetFixed(TABLE_SCRIPTING),

	SetTrace(TABLE_SCRIPTING),

	Rename(TABLE_SCRIPTING),

	HideLayer(TABLE_SCRIPTING),

	ShowLayer(TABLE_SCRIPTING),

	SetCoords(TABLE_SCRIPTING),

	Pan(TABLE_SCRIPTING),

	CenterView(TABLE_SCRIPTING),

	ZoomIn(TABLE_SCRIPTING),

	SetSeed(TABLE_SCRIPTING),

	ZoomOut(TABLE_SCRIPTING),

	SetActiveView(TABLE_SCRIPTING),

	SelectObjects(TABLE_SCRIPTING),

	SetLayer(TABLE_SCRIPTING),

	SetCaption(TABLE_SCRIPTING),

	SetLabelMode(TABLE_SCRIPTING),

	SetTooltipMode(TABLE_SCRIPTING),

	UpdateConstruction(TABLE_SCRIPTING),

	SetValue(TABLE_SCRIPTING),

	PlaySound(TABLE_SCRIPTING),

	ReadText(TABLE_SCRIPTING),

	ParseToNumber(TABLE_SCRIPTING),

	ParseToFunction(TABLE_SCRIPTING),

	StartAnimation(TABLE_SCRIPTING),

	Delete(TABLE_SCRIPTING),

	Slider(TABLE_SCRIPTING),

	Checkbox(TABLE_SCRIPTING),

	Textfield(TABLE_SCRIPTING),

	Button(TABLE_SCRIPTING),

	Execute(TABLE_SCRIPTING),

	GetTime(TABLE_SCRIPTING),

	ShowLabel(TABLE_SCRIPTING),

	SetAxesRatio(TABLE_SCRIPTING),

	SetVisibleInView(TABLE_SCRIPTING),

	ShowAxes(TABLE_SCRIPTING),

	ShowGrid(TABLE_SCRIPTING),

	AttachCopyToView(TABLE_SCRIPTING),

	RunClickScript(TABLE_SCRIPTING),

	RunUpdateScript(TABLE_SCRIPTING),

	SetPerspective(TABLE_SCRIPTING),

	StartRecord(TABLE_SCRIPTING),

	Repeat(TABLE_SCRIPTING),

	SetImage(TABLE_SCRIPTING),

	// =============================================================
	// discrete math
	// =============================================================
	Voronoi(TABLE_DISCRETE),

	// command removed, now falls back to ConvexHull
	// don't want in autocomplete
	Hull(TABLE_ENGLISH),

	ConvexHull(TABLE_DISCRETE),

	MinimumSpanningTree(TABLE_DISCRETE),

	DelauneyTriangulation(TABLE_DISCRETE),

	TravelingSalesman(TABLE_DISCRETE),

	ShortestDistance(TABLE_DISCRETE),

	// =================================================================
	// GeoGebra
	// =============================================================
	Corner(TABLE_GEOGEBRA),

	AxisStepX(TABLE_GEOGEBRA),

	AxisStepY(TABLE_GEOGEBRA),

	ConstructionStep(TABLE_GEOGEBRA),

	Object(TABLE_GEOGEBRA),

	Name(TABLE_GEOGEBRA),

	SlowPlot(TABLE_GEOGEBRA),

	ToolImage(TABLE_GEOGEBRA),

	DynamicCoordinates(TABLE_GEOGEBRA),

	SetConstructionStep(TABLE_GEOGEBRA),

	// =================================================================
	// Optimization
	// =============================================================
	Maximize(TABLE_OPTIMIZATION),

	Minimize(TABLE_OPTIMIZATION),

	ExportImage(TABLE_SCRIPTING),

	// =================================================================
	// commands that have been renamed so we want the new name to work
	// in other languages eg Curve used to be CurveCartesian
	// =============================================================
	Curve(TABLE_ENGLISH),

	FormulaText(TABLE_ENGLISH),

	IsDefined(TABLE_ENGLISH),

	ConjugateDiameter(TABLE_ENGLISH),

	LinearEccentricity(TABLE_ENGLISH),

	MajorAxis(TABLE_ENGLISH),

	SemiMajorAxisLength(TABLE_ENGLISH),

	PerpendicularBisector(TABLE_ENGLISH),

	PerpendicularLine(TABLE_ENGLISH),

	PerpendicularVector(TABLE_ENGLISH),

	MinorAxis(TABLE_ENGLISH),

	SemiMinorAxisLength(TABLE_ENGLISH),

	UnitPerpendicularVector(TABLE_ENGLISH),

	CorrelationCoefficient(TABLE_ENGLISH),

	FitLine(TABLE_ENGLISH),

	BinomialCoefficient(TABLE_ENGLISH),

	RandomBetween(TABLE_ENGLISH),

	TaylorPolynomial(TABLE_ENGLISH),

	AngleBisector(TABLE_ENGLISH),

	CircumcircularSector(TABLE_ENGLISH),

	CircumcircularArc(TABLE_ENGLISH),

	CircularSector(TABLE_ENGLISH),

	CircularArc(TABLE_ENGLISH),

	Polyline(TABLE_ENGLISH),

	Sxx(TABLE_ENGLISH),

	Syy(TABLE_ENGLISH),

	Sxy(TABLE_ENGLISH),

	Side(TABLE_ENGLISH),

	DelaunayTriangulation(TABLE_ENGLISH),

	InflectionPoint(TABLE_ENGLISH),

	/* alias for Variance */
	var(TABLE_ENGLISH),

	/* alias for Covariance */
	cov(TABLE_ENGLISH),

	// =================================================================
	// 3D
	// =============================================================

	Bottom(TABLE_3D),

	Cone(TABLE_3D),

	Cube(TABLE_3D),

	Cylinder(TABLE_3D),

	Dodecahedron(TABLE_3D),

	Ends(TABLE_3D),

	Icosahedron(TABLE_3D),

	Octahedron(TABLE_3D),

	Plane(TABLE_3D),

	QuadricSide(TABLE_3D),

	Surface(TABLE_3D),

	Tetrahedron(TABLE_3D),

	Top(TABLE_3D),

	Sphere(TABLE_3D),

	Prism(TABLE_3D),

	Pyramid(TABLE_3D),

	PlaneBisector(TABLE_3D),

	IntersectionPaths(TABLE_ENGLISH),

	/** internal name */
	OrthogonalPlane(TABLE_3D),

	/** English name */
	PerpendicularPlane(TABLE_ENGLISH),

	/** internal name */
	ConeInfinite(TABLE_3D),

	/** English name */
	InfiniteCone(TABLE_ENGLISH),

	/** internal name */
	CylinderInfinite(TABLE_3D),

	/** English name */
	InfiniteCylinder(TABLE_ENGLISH),

	IntersectCircle(TABLE_ENGLISH),

	IntersectConic(TABLE_3D),

	Height(TABLE_3D),

	CornerThreeD(TABLE_ENGLISH),

	Net(TABLE_3D),

	// =============================================================
	// scripting 3D
	// =============================================================

	SetViewDirection(TABLE_SCRIPTING),

	SetSpinSpeed(TABLE_SCRIPTING),

	// ================================================================
	// Turtle
	// =============================================================

	Turtle(TABLE_SCRIPTING),

	TurtleForward(TABLE_SCRIPTING),

	TurtleBack(TABLE_SCRIPTING),

	TurtleLeft(TABLE_SCRIPTING),

	TurtleRight(TABLE_SCRIPTING),

	TurtleUp(TABLE_SCRIPTING),

	TurtleDown(TABLE_SCRIPTING),

	// these are currently disabled (unfinished)
	// change TABLE_ENGLISH when adding
	Polyhedron(TABLE_ENGLISH),

	// ==

	Reflect(TABLE_ENGLISH),

	Assume(TABLE_CAS),

	CFactor(TABLE_CAS),

	CIFactor(TABLE_CAS),

	IFactor(TABLE_ALGEBRA),

	IntegralSymbolic(TABLE_CAS),

	CommonDenominator(TABLE_ALGEBRA),

	Cross(TABLE_ALGEBRA),

	CSolutions(TABLE_CAS),

	CSolve(TABLE_CAS),

	Dot(TABLE_ALGEBRA),

	Eliminate(TABLE_CAS),

	GroebnerLex(TABLE_CAS),

	GroebnerDegRevLex(TABLE_CAS),

	GroebnerLexDeg(TABLE_CAS),

	NIntegral(TABLE_FUNCTION),

	NInvert(TABLE_FUNCTION),

	NSolve(TABLE_ALGEBRA),

	NSolutions(TABLE_ALGEBRA),

	Numeric(TABLE_CAS),

	Evaluate(TABLE_ENGLISH),

	MixedNumber(TABLE_CAS),

	Rationalize(TABLE_CAS),

	Solutions(TABLE_ALGEBRA),

	Solve(TABLE_ALGEBRA),

	PlotSolve(TABLE_FUNCTION),

	SolveCubic(TABLE_CAS),

	SolveQuartic(TABLE_CAS),

	JordanDiagonalization(TABLE_CAS),

	Eigenvectors(TABLE_CAS),

	Eigenvalues(TABLE_CAS),

	Laplace(TABLE_CAS),

	InverseLaplace(TABLE_CAS),

	Substitute(TABLE_CAS),

	ToComplex(TABLE_GEOGEBRA),

	ToExponential(TABLE_CAS),

	InputBox(TABLE_ENGLISH),

	ToPolar(TABLE_GEOGEBRA),

	ToPoint(TABLE_GEOGEBRA),

	TrigExpand(TABLE_FUNCTION),

	TrigSimplify(TABLE_FUNCTION),

	TrigCombine(TABLE_FUNCTION),

	nPr(TABLE_ENGLISH),

	CASLoaded(TABLE_GEOGEBRA),

	IsVertexForm(TABLE_FUNCTION),

	TableToChart(TABLE_ENGLISH),

	ExtendedGCD(TABLE_CAS),

	ModularExponent(TABLE_CAS),

	CharacteristicPolynomial(TABLE_CAS),

	MinimalPolynomial(TABLE_CAS),

	LUDecomposition(TABLE_CAS),

	QRDecomposition(TABLE_CAS);

	private static final Commands[] RENAMED = {
			Commands.Binomial, Commands.BinomialCoefficient, Commands.Mean,
			Commands.SD, Commands.SampleSD, Commands.MAD
	};

	private int table;

	Commands(int table) {
		this.table = table;
	}

	/**
	 * Case-insensitive lookup of commands
	 * @param key user-specified name
	 * @return name with normalized capitalization, null if command not found
	 */
	public static String lookupInternal(String key) {
		// if that fails check internal commands
		for (Commands c : Commands.values()) {
			if (c.name().equalsIgnoreCase(key)) {
				return Commands.englishToInternal(c).name();
			}
		}
		return null;
	}

	public int getTable() {
		return table;
	}

	/**
	 * @param comm
	 *            english command, e.g. FormulaText
	 * @return internal command, e.g. LaTeX
	 */
	public static Commands englishToInternal(Commands comm) {
		switch (comm) {
		case Quartile1:
			return Q1;
		case Quartile3:
			return Q3;
		case Polyline:
			return PolyLine;
		case Sxx:
			return SXX;
		case Syy:
			return SYY;
		case Sxy:
			return SXY;
		case CircularArc:
			return CircleArc;
		case CircularSector:
			return CircleSector;
		case CircumcircularArc:
			return CircumcircleArc;
		case CircumcircularSector:
			return CircumcircleSector;
		case AngleBisector:
			return AngularBisector;
		case Reflect:
			return Mirror;
		case Curve:
			return CurveCartesian;
		case FormulaText:
			return LaTeX;
		case IsDefined:
			return Defined;
		case ConjugateDiameter:
			return Diameter;
		case LinearEccentricity:
			return Excentricity;
		case MajorAxis:
			return FirstAxis;
		case SemiMajorAxisLength:
			return FirstAxisLength;
		case PerpendicularBisector:
			return LineBisector;
		case PerpendicularLine:
			return OrthogonalLine;
		case PerpendicularVector:
			return OrthogonalVector;
		case MinorAxis:
			return SecondAxis;
		case SemiMinorAxisLength:
			return SecondAxisLength;
		case UnitPerpendicularVector:
			return UnitOrthogonalVector;
		case InflectionPoint:
			return TurningPoint;
		case CorrelationCoefficient:
			return PMCC;
		case FitLine:
			return FitLineY;
		case BinomialCoefficient:
		case Binomial:
			return nCr;
		case RandomBetween:
			return Random;
		case TaylorPolynomial:
			return TaylorSeries;
		case InfiniteCylinder:
			return CylinderInfinite;
		case InfiniteCone:
			return ConeInfinite;
		case PerpendicularPlane:
			return OrthogonalPlane;
		case InputBox:
			return Textfield;
		case IntersectCircle:
			return IntersectConic;
		case Side:
			return QuadricSide;
		case DelaunayTriangulation:
			return DelauneyTriangulation;
		case Mean:
			return mean;
		case SD:
			return stdevp;
		case SampleSD:
			return stdev;
		case MAD:
			return mad;
		default:
			break;
		}
		return comm;
	}

	@Override
	public String getCommand() {
		return name();
	}

	/**
	 * Like valueOf(), but no error is thrown
	 *
	 * @param str
	 *            command nam
	 * @return command with that name
	 */
	public static Commands stringToCommand(String str) {
		for (Commands c : Commands.values()) {
			if (c.getCommand().equals(str)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * @param cmdLower
	 *            lower-case command name
	 * @param loc
	 *            localization
	 * @return internal command if it was renamed
	 */
	public static String getRenamed(String cmdLower, Localization loc) {
		for (Commands c : Commands.RENAMED) {
			if (StringUtil.toLowerCaseUS(loc.getCommand(c.name()))
					.equals(cmdLower)) {
				return Commands.englishToInternal(c).name();
			}
		}
		return null;
	}

	/**
	 * @param revTranslateCommandTable
	 *            reverse lookup table
	 * @param loc
	 *            localization
	 */
	public static void addRenamed(
			HashMap<String, String> revTranslateCommandTable,
			Localization loc) {
		for (Commands c : Commands.RENAMED) {
			String lowerCaseCmd = StringUtil
					.toLowerCaseUS(loc.getCommand(c.name()));
			revTranslateCommandTable.put(lowerCaseCmd,
					Commands.englishToInternal(c).name());
		}
	}

}