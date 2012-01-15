package geogebra.common.kernel.commands;

public enum Commands {
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
  	// Optimization
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
   	RandomBetween,
   	//=================================================================
  	// 3D
	//=============================================================
   	
   	Bottom,Cone,Cube,Cylinder,Dodecahedron,Ends,Icosahedron,InfiniteCone,InfiniteCylinder,
   	Octahedron,Plane,QuadricSide,SurfaceCartesian,Tetrahedron,Top
}