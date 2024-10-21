package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.CommandsConstants;

/**
 * Creates CommandFilters for various apps.
 * @author laszlo
 */
public final class CommandFilterFactory {
	/**
	 * @return Returns the CommandFilter that allows only the Scientific
	 * Calculator commands
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
	 * @return command filter
	 */
	public static CommandFilter createGraphingCommandFilter() {
		CommandFilter noCasCommandFilter = createNoCasCommandFilter();
		CommandFilter tableFilter = new CommandTableFilter(CommandsConstants.TABLE_CONIC,
				CommandsConstants.TABLE_TRANSFORMATION, CommandsConstants.TABLE_3D);
		CommandFilter nameFilter = createGraphingNameFilter();
		return new CompositeCommandFilter(noCasCommandFilter,
				tableFilter, nameFilter);
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

	/**
	 * Add boolean commands to a filter
	 * @param nameFilter filter
	 */
	public static void addBooleanCommands(CommandNameFilter nameFilter) {
		nameFilter.addCommands(Commands.AreCollinear, Commands.IsTangent,
				Commands.AreConcurrent, Commands.AreConcyclic, Commands.AreCongruent,
				Commands.AreEqual, Commands.AreParallel, Commands.ArePerpendicular);
	}

	/**
	 * @return filer for Bayern CAS exam
	 */
	@Deprecated // replaced by BayernCasExamRestrictions
	public static CommandFilter createBayernCasFilter() {
		CommandNameFilter nameFilter = new CommandNameFilter(true);
		nameFilter.addCommands(Commands.Plane);
		return nameFilter;
	}

	/**
	 * @return filter for Vlaanderen exam
	 */
	@Deprecated // replaced by VlaanderenExamRestrictions
	public static CommandFilter createVlaanderenFilter() {
		CommandNameFilter nameFilter = new CommandNameFilter(true);
		nameFilter.addCommands(Commands.Derivative, Commands.NDerivative, Commands.Integral,
				Commands.IntegralSymbolic, Commands.IntegralBetween, Commands.NIntegral,
				Commands.Solve, Commands.SolveQuartic, Commands.SolveODE, Commands.SolveCubic,
				Commands.Solutions, Commands.NSolve, Commands.NSolveODE, Commands.NSolutions);
		return nameFilter;
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
				Commands.CurveCartesian, Commands.Curve,
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
				Commands.ParseToFunction, Commands.PlaySound,
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
		addBooleanCommands(commandNameFilter);
		return new CompositeCommandFilter(commandNameFilter,
				new CommandTableFilter(Commands.TABLE_DISCRETE));
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
