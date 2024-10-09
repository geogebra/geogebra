package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.CommandsConstants;

/**
 * Creates CommandFilters for various apps.
 * 
 * @author laszlo
 *
 */
public final class CommandFilterFactory {
	/**
	 *
	 * @return Returns the CommandFilter that allows only the Scientific
	 *         Calculator commands
	 */
	public static CommandFilter createSciCalcCommandFilter() {
		CommandNameFilter commandNameFilter = new CommandNameFilter(
				false);
		commandNameFilter.addCommands(Commands.Mean, Commands.mean, Commands.SD,
				Commands.stdev, Commands.SampleSD, Commands.stdevp,
				Commands.nPr, Commands.nCr, Commands.Binomial, Commands.BinomialDist,
				Commands.MAD, Commands.mad, Commands.Normal);
		return commandNameFilter;
	}

	/**
	 * Creates a CommandFilter for the Graphing app (no cas: e.g. Solve).
	 *
	 * @return command filter
	 */
	public static CommandFilter createGraphingCommandFilter() {
		CommandFilter noCasCommandFilter = createNoCasCommandFilter();
		CommandFilter tableFilter = new CommandTableFilter(CommandsConstants.TABLE_CONIC,
				CommandsConstants.TABLE_TRANSFORMATION);
		CommandFilter nameFilter = createGraphingNameFilter();
		CommandFilter composite = new CompositeCommandFilter(noCasCommandFilter,
				tableFilter, nameFilter);
		return new EnglishCommandFilter(composite);
	}

	private static CommandFilter createGraphingNameFilter() {
		CommandNameFilter nameFilter = new CommandNameFilter(true);
		nameFilter.addCommands(Commands.OrthogonalVector,
				Commands.UnitOrthogonalVector, Commands.UnitVector, Commands.Cross, Commands.Dot,
				Commands.Mirror, Commands.AngularBisector, Commands.Angle,
				Commands.Diameter, Commands.Excentricity,
				Commands.FirstAxis, Commands.SecondAxis,
				Commands.FirstAxisLength, Commands.SecondAxisLength,
				Commands.Relation, Commands.AffineRatio, Commands.Arc, Commands.Area,
				Commands.Barycenter, Commands.Centroid, Commands.CircleArc,
				Commands.CircleSector, Commands.CircumcircleArc, Commands.CircumcircleSector,
				Commands.Circumference, Commands.ClosestPoint,
				Commands.ClosestPointRegion, Commands.CrossRatio, Commands.Cubic,
				Commands.Direction, Commands.Distance, Commands.Envelope, Commands.IntersectPath,
				Commands.Locus, Commands.LocusEquation, Commands.Midpoint, Commands.Perimeter,
				Commands.LineBisector, Commands.OrthogonalLine, Commands.Polygon,
				Commands.Prove, Commands.ProveDetails, Commands.Radius, Commands.RigidPolygon,
				Commands.Sector, Commands.Segment, Commands.Slope, Commands.Tangent,
				Commands.TriangleCenter, Commands.TriangleCurve, Commands.Trilinear,
				Commands.Vertex, Commands.Polynomial,
				Commands.TaylorSeries, Commands.Asymptote, Commands.OsculatingCircle,
				Commands.CommonDenominator, Commands.CompleteSquare, Commands.Div, Commands.Mod,
				Commands.Division, Commands.IsVertexForm);
		addBooleanCommands(nameFilter);
		return nameFilter;
	}

	private static void addBooleanCommands(CommandNameFilter nameFilter) {
		nameFilter.addCommands(Commands.AreCollinear,
				Commands.AreConcurrent, Commands.AreConcyclic, Commands.AreCongruent,
				Commands.AreEqual, Commands.AreParallel, Commands.ArePerpendicular);
	}

	/**
	 * @return filer for IQB MMS exam
	 */
	public static CommandFilter createMmsFilter() {
		CommandNameFilter nameFilter = new CommandNameFilter(true);
		nameFilter.addCommands(Commands.Axes, Commands.Focus,
				Commands.DelauneyTriangulation, Commands.Difference,
				Commands.Rotate, Commands.TriangleCenter,
				Commands.Envelope, Commands.Ends,
				Commands.FirstAxisLength, Commands.SecondAxisLength,
				Commands.FirstAxis, Commands.Height, Commands.InteriorAngles,
				Commands.InverseBinomial, Commands.InverseBinomialMinimumTrials,
				Commands.InverseCauchy, Commands.InverseChiSquared,
				Commands.InverseExponential, Commands.InverseFDistribution,
				Commands.InverseGamma, Commands.InverseHyperGeometric,
				Commands.InverseLaplace, Commands.InverseLogistic, Commands.InverseLogNormal,
				Commands.InverseNormal, Commands.InversePascal, Commands.InversePoisson,
				Commands.InverseTDistribution, Commands.InverseWeibull, Commands.InverseZipf,
				Commands.Diameter, Commands.ConvexHull, Commands.ShortestDistance,
				Commands.CurveCartesian, Commands.Barycenter, Commands.MinimumSpanningTree,
				Commands.Center, Commands.SecondAxis, Commands.Net, Commands.Top,
				Commands.Surface, Commands.Prove, Commands.ProveDetails,
				Commands.Point, Commands.PointIn, Commands.IntersectPath,
				Commands.IntersectConic, Commands.Centroid, Commands.QuadricSide,
				Commands.Sector, Commands.Mirror, Commands.Spline,
				Commands.Stretch, Commands.Dilate, Commands.Bottom,
				Commands.Translate, Commands.Shear, Commands.Polygon,
				Commands.Arc, Commands.Circle, Commands.CircleSector,
				Commands.CircleArc, Commands.OsculatingCircle, Commands.Cubic,
				Commands.Polygon, Commands.Line, Commands.Segment, Commands.Ray,
				Commands.Ellipse, Commands.LineBisector, Commands.OrthogonalLine,
				Commands.Asymptote, Commands.RigidPolygon, Commands.Tangent,
				Commands.AngularBisector,
				Commands.Pyramid, Commands.Prism, Commands.Cone, Commands.Cylinder,
				Commands.Sphere, Commands.TriangleCurve, Commands.Semicircle,
				Commands.ImplicitCurve, Commands.Conic, Commands.Icosahedron, Commands.Hyperbola,
				Commands.Parabola, Commands.Incircle, Commands.Directrix, Commands.Octahedron,
				Commands.Locus, Commands.LocusEquation, Commands.Polar,
				Commands.PolyLine, Commands.ConeInfinite, Commands.CylinderInfinite,
				Commands.Tetrahedron, Commands.CircumcircleArc, Commands.CircumcircleSector,
				Commands.Cube, Commands.Roots, Commands.ComplexRoot, Commands.Root,
				Commands.RootList, Commands.Volume, Commands.Plane,
				Commands.OrthogonalPlane,
				Commands.PlaneBisector, Commands.Angle, Commands.Distance, Commands.Relation,
				Commands.IsInRegion, Commands.AffineRatio, Commands.Angle,
				Commands.AngleBisector, Commands.ANOVA, Commands.ApplyMatrix, Commands.Arc,
				Commands.Area, Commands.AreCollinear, Commands.AreConcurrent,
				Commands.AreConcyclic, Commands.AreCongruent, Commands.AreEqual,
				Commands.AreParallel, Commands.ArePerpendicular, Commands.Asymptote,
				Commands.AttachCopyToView, Commands.Axes, Commands.AxisStepX, Commands.AxisStepY,
				Commands.Barycenter, Commands.Bernoulli, Commands.Bottom, Commands.Button,
				Commands.CASLoaded, Commands.Cauchy, Commands.Cell, Commands.CellRange,
				Commands.CenterView, Commands.Centroid, Commands.Checkbox, Commands.ChiSquared,
				Commands.ChiSquaredTest, Commands.Circle, Commands.CircularArc,
				Commands.CircleSector, Commands.CircumcircularArc, Commands.CircumcircularSector,
				Commands.Classes, Commands.ClosestPoint, Commands.ClosestPointRegion,
				Commands.Column, Commands.ColumnName, Commands.CompleteSquare, Commands.ComplexRoot,
				Commands.Cone, Commands.Conic, Commands.ConjugateDiameter,
				Commands.ConstructionStep, Commands.ContingencyTable, Commands.ContinuedFraction,
				Commands.ConvexHull, Commands.CopyFreeObject, Commands.Corner, Commands.CrossRatio,
				Commands.Cube, Commands.Cubic, Commands.Curvature, Commands.CurvatureVector,
				Commands.Curve, Commands.Cylinder, Commands.DelauneyTriangulation,
				Commands.Difference, Commands.Dilate, Commands.Direction, Commands.Directrix,
				Commands.Distance, Commands.Div, Commands.Divisors, Commands.DivisorsList,
				Commands.DivisorsSum, Commands.Dodecahedron, Commands.DotPlot,
				Commands.DynamicCoordinates, Commands.Eccentricity, Commands.Ellipse,
				Commands.Ends, Commands.Envelope, Commands.Erlang, Commands.Execute,
				Commands.Exponential, Commands.ExportImage, Commands.Extremum,
				Commands.FDistribution, Commands.FillCells, Commands.FillColumn, Commands.FillRow,
				Commands.Fit, Commands.FitExp, Commands.FitGrowth, Commands.FitImplicit,
				Commands.FitLineY, Commands.FitLineX, Commands.FitLog, Commands.FitLogistic,
				Commands.FitPoly, Commands.FitPow, Commands.FitSin, Commands.Focus,
				Commands.FormulaText, Commands.FractionText, Commands.FrequencyTable,
				Commands.FromBase, Commands.Function, Commands.FutureValue, Commands.Gamma,
				Commands.GeometricMean, Commands.GetTime, Commands.GroebnerDegRevLex,
				Commands.GroebnerLex, Commands.GroebnerLexDeg, Commands.HarmonicMean,
				Commands.Height, Commands.HideLayer, Commands.Hyperbola, Commands.Icosahedron,
				Commands.ImplicitCurve, Commands.ImplicitDerivative, Commands.Incircle,
				Commands.InfiniteCone, Commands.TurningPoint, Commands.InputBox,
				Commands.IntegralBetween, Commands.InteriorAngles, Commands.Intersect,
				Commands.IntersectConic, Commands.IntersectPath, Commands.InverseBinomial,
				Commands.InverseCauchy, Commands.InverseChiSquared, Commands.InverseExponential,
				Commands.InverseFDistribution, Commands.InverseGamma,
				Commands.InverseHyperGeometric, Commands.InverseLogistic,
				Commands.InverseLogNormal, Commands.InverseNormal, Commands.InversePascal,
				Commands.InversePoisson, Commands.InverseTDistribution, Commands.InverseWeibull,
				Commands.InverseZipf, Commands.IsDefined, Commands.IsFactored, Commands.IsInRegion,
				Commands.IsInteger, Commands.IsPrime, Commands.IsVertexForm,
				Commands.Iteration, Commands.IterationList, Commands.Laplace, Commands.LeftSum,
				Commands.Length, Commands.LetterToUnicode, Commands.Line,
				Commands.Excentricity, Commands.Locus, Commands.LocusEquation,
				Commands.Logistic, Commands.LogNormal, Commands.LowerSum, Commands.mad,
				Commands.MajorAxis, Commands.Maximize, Commands.Midpoint, Commands.Minimize,
				Commands.MinimumSpanningTree, Commands.MinorAxis, Commands.Name, Commands.Net,
				Commands.NextPrime, Commands.NormalQuantilePlot, Commands.NSolveODE,
				Commands.Object, Commands.Octahedron, Commands.Ordinal, Commands.OsculatingCircle,
				Commands.Pan, Commands.Parabola, Commands.Parameter, Commands.ParametricDerivative,
				Commands.ParseToFunction, Commands.ParseToNumber, Commands.Pascal,
				Commands.PathParameter, Commands.Payment, Commands.Perimeter, Commands.Periods,
				Commands.PerpendicularBisector, Commands.PerpendicularLine,
				Commands.OrthogonalVector, Commands.Plane, Commands.PlaneBisector,
				Commands.PlaySound, Commands.PlotSolve, Commands.Point, Commands.PointIn,
				Commands.PointList, Commands.Poisson, Commands.Polar, Commands.Polygon,
				Commands.Polyline, Commands.Polynomial, Commands.PresentValue,
				Commands.PreviousPrime, Commands.Prism, Commands.Prove, Commands.ProveDetails,
				Commands.Pyramid, Commands.Radius, Commands.RandomBinomial,
				Commands.RandomDiscrete, Commands.RandomNormal, Commands.RandomPointIn,
				Commands.RandomPoisson, Commands.RandomPolynomial, Commands.RandomUniform,
				Commands.Rate, Commands.Rationalize, Commands.Ray, Commands.ReadText,
				Commands.RectangleSum, Commands.ReducedRowEchelonForm, Commands.Reflect,
				Commands.Relation, Commands.RemovableDiscontinuity, Commands.Rename,
				Commands.Repeat, Commands.ResidualPlot, Commands.RigidPolygon, Commands.Root,
				Commands.RootList, Commands.RootMeanSquare, Commands.Roots, Commands.Rotate,
				Commands.RotateText, Commands.Row, Commands.RunClickScript,
				Commands.RunUpdateScript, Commands.SampleSD, Commands.stdev, Commands.SampleSDX,
				Commands.SampleSDY, Commands.SampleVariance, Commands.ScientificText, Commands.SD,
				Commands.stdevp, Commands.SDX, Commands.SDY, Commands.Sector, Commands.Segment,
				Commands.SelectedElement, Commands.SelectedIndex, Commands.Semicircle,
				Commands.SemiMajorAxisLength, Commands.SemiMinorAxisLength, Commands.SetActiveView,
				Commands.SetAxesRatio, Commands.SetBackgroundColor, Commands.SetCaption,
				Commands.SetColor, Commands.SetConditionToShowObject, Commands.SetConstructionStep,
				Commands.SetCoords, Commands.SetDecoration, Commands.SetDynamicColor,
				Commands.SetFilling, Commands.SetFixed, Commands.SetImage, Commands.SetLabelMode,
				Commands.SetLayer, Commands.SetLevelOfDetail, Commands.SetLineStyle,
				Commands.SetLineThickness, Commands.SetPerspective, Commands.SetPointSize,
				Commands.SetPointStyle, Commands.SetSeed, Commands.SetSpinSpeed,
				Commands.SetTooltipMode, Commands.SetTrace, Commands.SetValue,
				Commands.SetViewDirection, Commands.SetVisibleInView, Commands.Shear,
				Commands.ShortestDistance, Commands.ShowAxes, Commands.ShowGrid,
				Commands.ShowLabel, Commands.ShowLayer, Commands.Side, Commands.SigmaXX,
				Commands.SigmaXY, Commands.SigmaYY, Commands.Slope, Commands.SlopeField,
				Commands.SlowPlot, Commands.SolveCubic, Commands.SolveODE, Commands.SolveQuartic,
				Commands.Spearman, Commands.Sphere, Commands.Spline, Commands.Split,
				Commands.StartAnimation, Commands.StartRecord, Commands.Stretch,
				Commands.SumSquaredErrors, Commands.SurdText, Commands.Surface, Commands.SVD,
				Commands.SXX, Commands.SXY, Commands.SYY, Commands.TableText, Commands.Tangent,
				Commands.TaylorSeries, Commands.TDistribution, Commands.Tetrahedron,
				Commands.TextToUnicode, Commands.TMean2Estimate, Commands.TMeanEstimate,
				Commands.ToBase, Commands.ToolImage, Commands.Top, Commands.ToPoint,
				Commands.ToPolar, Commands.Translate, Commands.TrapezoidalSum,
				Commands.TravelingSalesman, Commands.TriangleCenter, Commands.TriangleCurve,
				Commands.Triangular, Commands.TrigCombine, Commands.TrigExpand,
				Commands.TrigSimplify, Commands.Trilinear, Commands.TTest, Commands.TTest2,
				Commands.TTestPaired, Commands.Turtle, Commands.TurtleBack, Commands.TurtleDown,
				Commands.TurtleForward, Commands.TurtleLeft, Commands.TurtleRight,
				Commands.TurtleUp, Commands.Type, Commands.UnicodeToLetter, Commands.UnicodeToText,
				Commands.Uniform, Commands.UnitOrthogonalVector, Commands.UnitVector,
				Commands.UpperSum, Commands.Variance, Commands.Vector, Commands.Vertex,
				Commands.VerticalText, Commands.Volume, Commands.Voronoi, Commands.Weibull,
				Commands.Zip, Commands.Zipf, Commands.ZMean2Estimate, Commands.ZMean2Test,
				Commands.ZMeanEstimate, Commands.ZMeanTest, Commands.ZoomIn, Commands.ZoomOut,
				Commands.ZProportion2Estimate, Commands.ZProportion2Test,
				Commands.ZProportionEstimate, Commands.ZProportionTest);
		addBooleanCommands(nameFilter);
		return new EnglishCommandFilter(nameFilter);
	}

	/**
	 * @return filer for Bayern CAS exam
	 */
	public static CommandFilter createBayernCasFilter() {
		CommandNameFilter nameFilter = new CommandNameFilter(true);
		nameFilter.addCommands(Commands.Plane);
		return new EnglishCommandFilter(nameFilter);
	}

	/**
	 * @return name filter for apps with no CAS
	 */
	public static CommandFilter createNoCasCommandFilter() {
		CommandNameFilter commandNameFilter = new CommandNameFilter(true);
		commandNameFilter.addCommands(Commands.LocusEquation, Commands.Envelope,
				Commands.Expand, Commands.Factor, Commands.Factors,
				Commands.IFactor, Commands.CFactor, Commands.Simplify,
				Commands.SurdText, Commands.ParametricDerivative,
				Commands.TrigExpand, Commands.TrigCombine,
				Commands.TrigSimplify, Commands.Limit, Commands.LimitBelow,
				Commands.LimitAbove, Commands.Degree, Commands.Coefficients,
				Commands.CompleteSquare, Commands.PartialFractions,
				Commands.SolveODE, Commands.ImplicitDerivative,
				Commands.NextPrime, Commands.PreviousPrime, Commands.Solve,
				Commands.Solutions, Commands.NSolutions, Commands.NSolve,
				Commands.IntegralSymbolic, Commands.RemovableDiscontinuity,
				Commands.PlotSolve, Commands.ExtendedGCD, Commands.ModularExponent,
				Commands.CharacteristicPolynomial, Commands.MinimalPolynomial,
				Commands.LUDecomposition, Commands.QRDecomposition);
		return commandNameFilter;
	}

	/**
	 * @return name filter for apps with CAS
	 */
	public static CommandFilter createCasCommandFilter() {
		CommandNameFilter commandNameFilter = new CommandNameFilter(true);
		commandNameFilter.addCommands(
				// CAS specific command
				Commands.Delete, Commands.Poisson,
				// Function Commands
				Commands.Asymptote, Commands.CurveCartesian, Commands.Curve,
				Commands.CurvatureVector, Commands.DataFunction, Commands.Function,
				Commands.ImplicitCurve, Commands.IterationList, Commands.NSolveODE,
				Commands.OsculatingCircle, Commands.ParametricDerivative, Commands.PathParameter,
				Commands.RootList, Commands.Roots, Commands.Spline,
				// Vector And Matrix Commands
				Commands.ApplyMatrix,
				//Geometry Commands
				Commands.Angle, Commands.Centroid,
				Commands.CircleArc, Commands.CircleSector,
				Commands.CircumcircleArc,
				Commands.CircumcircleSector, Commands.Cubic, Commands.Direction,
				Commands.Envelope, Commands.IntersectPath,
				Commands.Locus, Commands.LocusEquation, Commands.Midpoint,
				Commands.Point, Commands.Polygon,
				Commands.PolyLine, Commands.ProveDetails, Commands.Ray,
				Commands.RigidPolygon, Commands.Sector, Commands.Segment,
				Commands.Slope, Commands.TriangleCurve, Commands.Vertex,
				// Transformation Commands
				Commands.Rotate, Commands.Shear, Commands.Translate,
				// Statistics Commands
				Commands.Spearman, Commands.SumSquaredErrors,
				// Probability commands
				Commands.Bernoulli,
				// Conic Commands
				Commands.Axes, Commands.Center,
				Commands.Diameter, Commands.Focus,
				// Chart Commands
				Commands.BarChart, Commands.BoxPlot, Commands.ContingencyTable,
				Commands.DotPlot, Commands.FrequencyPolygon, Commands.FrequencyTable,
				Commands.Histogram, Commands.HistogramRight, Commands.NormalQuantilePlot,
				Commands.ResidualPlot, Commands.StemPlot, Commands.StepGraph,
				Commands.StickGraph, Commands.LineGraph, Commands.PieChart,
				// Discrete Math Commands
				Commands.ShortestDistance,
				// GeoGebra Commands
				Commands.DynamicCoordinates, Commands.Object, Commands.SlowPlot,
				Commands.ToolImage,
				// List Commands
				Commands.Zip,
				// Text Commands
				Commands.ContinuedFraction, Commands.LaTeX,
				Commands.FractionText, Commands.SurdText, Commands.TableText,
				Commands.Text, Commands.UnicodeToLetter,
				// Logical Commands
				Commands.Defined, Commands.Relation,
				// Optimization Command
				Commands.Maximize, Commands.Minimize,
				// Scripting Commands
				Commands.AttachCopyToView, Commands.Button, Commands.Checkbox,
				Commands.CopyFreeObject, Commands.Delete, Commands.GetTime,
				Commands.Textfield, Commands.Pan,
				Commands.ParseToFunction, Commands.ParseToNumber, Commands.PlaySound,
				Commands.Rename, Commands.Repeat, Commands.RunClickScript,
				Commands.RunUpdateScript, Commands.SelectObjects, Commands.SetActiveView,
				Commands.SetConditionToShowObject, Commands.SetConstructionStep, Commands.SetCoords,
				Commands.SetDynamicColor, Commands.SetFilling, Commands.SetFixed,
				Commands.SetLabelMode, Commands.SetLayer, Commands.SetLineStyle,
				Commands.SetLineThickness, Commands.SetPerspective, Commands.SetPointStyle,
				Commands.SetSeed, Commands.SetSpinSpeed, Commands.SetTooltipMode,
				Commands.SetTrace, Commands.SetValue, Commands.SetViewDirection,
				Commands.SetVisibleInView, Commands.ShowLabel,
				Commands.StartRecord,
				Commands.Turtle, Commands.TurtleBack, Commands.TurtleDown,
				Commands.TurtleForward, Commands.TurtleLeft, Commands.TurtleRight,
				Commands.TurtleUp, Commands.UpdateConstruction,
				// 3D Commands
				Commands.Bottom, Commands.Cone, Commands.Cube,
				Commands.Cylinder, Commands.Dodecahedron, Commands.Ends,
				Commands.Height, Commands.Icosahedron,
				Commands.ConeInfinite, Commands.CylinderInfinite, Commands.IntersectConic,
				Commands.Net, Commands.Octahedron,
				Commands.Plane, Commands.PlaneBisector, Commands.Prism,
				Commands.Pyramid, Commands.Sphere,
				Commands.Surface, Commands.Tetrahedron, Commands.Top,
				Commands.Volume, Commands.QuadricSide, Commands.OrthogonalPlane,
				// SpreadSheet Commands
				Commands.Cell, Commands.CellRange, Commands.Column,
				Commands.ColumnName, Commands.FillCells, Commands.FillColumn,
				Commands.FillRow, Commands.Row
			);
		return new EnglishCommandFilter(commandNameFilter);
	}

	/**
	 * @return command filter for the 3D graphing app
	 */
	public static CommandFilter create3DGraphingCommandFilter() {
		CommandNameFilter commandNameFilter = new CommandNameFilter(true);
		commandNameFilter.addCommands(Commands.PieChart);
		return commandNameFilter;
	}
}
