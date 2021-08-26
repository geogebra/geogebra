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
		CommandNameFilterSet commandNameFilter = new CommandNameFilterSet(
				false);
		commandNameFilter.addCommands(Commands.Mean, Commands.mean, Commands.SD,
				Commands.stdev, Commands.SampleSD, Commands.stdevp,
				Commands.nPr, Commands.nCr, Commands.Binomial, Commands.MAD,
				Commands.mad);
		return commandNameFilter;
	}

	/**
	 * Creates a CommandFilter for the Graphing app.
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
		CommandNameFilterSet nameFilter = new CommandNameFilterSet(true);
		nameFilter.addCommands(Commands.PerpendicularVector, Commands.OrthogonalVector,
				Commands.UnitOrthogonalVector, Commands.UnitVector, Commands.Cross, Commands.Dot,
				Commands.Reflect, Commands.Mirror, Commands.AngleBisector,
				Commands.AngularBisector, Commands.Angle, Commands.ConjugateDiameter,
				Commands.Diameter, Commands.LinearEccentricity, Commands.Excentricity,
				Commands.MajorAxis, Commands.FirstAxis, Commands.MinorAxis, Commands.SecondAxis,
				Commands.SemiMajorAxisLength, Commands.FirstAxisLength,
				Commands.SemiMinorAxisLength, Commands.SecondAxisLength,
				Commands.Relation, Commands.AffineRatio, Commands.Arc, Commands.AreCollinear,
				Commands.AreConcurrent, Commands.AreConcyclic, Commands.AreCongruent,
				Commands.AreEqual, Commands.AreParallel, Commands.ArePerpendicular, Commands.Area,
				Commands.Barycenter, Commands.Centroid, Commands.CircularArc, Commands.CircleArc,
				Commands.CircularSector, Commands.CircleSector, Commands.CircumcircularArc,
				Commands.CircumcircleArc, Commands.CircumcircularSector,
				Commands.CircumcircleSector, Commands.Circumference, Commands.ClosestPoint,
				Commands.ClosestPointRegion, Commands.CrossRatio, Commands.Cubic,
				Commands.Direction, Commands.Distance, Commands.Envelope, Commands.IntersectPath,
				Commands.Locus, Commands.LocusEquation, Commands.Midpoint, Commands.Perimeter,
				Commands.PerpendicularBisector, Commands.LineBisector, Commands.PerpendicularLine,
				Commands.OrthogonalLine, Commands.Polygon,
				Commands.Prove, Commands.ProveDetails, Commands.Radius, Commands.RigidPolygon,
				Commands.Sector, Commands.Segment, Commands.Slope, Commands.Tangent,
				Commands.TriangleCenter, Commands.TriangleCurve, Commands.Trilinear,
				Commands.Vertex, Commands.Polynomial, Commands.TaylorPolynomial,
				Commands.TaylorSeries, Commands.Asymptote, Commands.OsculatingCircle,
				Commands.CommonDenominator, Commands.CompleteSquare, Commands.Div, Commands.Mod,
				Commands.Division, Commands.IsVertexForm);
		return nameFilter;
	}

	/**
	 * @return name filter for apps with no CAS
	 */
	public static CommandFilter createNoCasCommandFilter() {
		CommandNameFilterSet commandNameFilter = new CommandNameFilterSet(true);
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
				Commands.PlotSolve);
		return commandNameFilter;
	}

	/**
	 * @return name filter for apps with CAS
	 */
	public static CommandFilter createCasCommandFilter() {
		CommandNameFilterSet commandNameFilter = new CommandNameFilterSet(true);
		commandNameFilter.addCommands(
				// CAS specific command
				Commands.Delete, Commands.Poisson,
				// Function Commands
				Commands.Asymptote, Commands.CurvatureVector, Commands.DataFunction,
				Commands.Function, Commands.ImplicitCurve, Commands.IterationList,
				Commands.NSolveODE, Commands.OsculatingCircle, Commands.ParametricDerivative,
				Commands.PathParameter, Commands.RootList, Commands.Roots,
				Commands.Spline,
				// Vector And Matrix Commands
				Commands.ApplyMatrix,
				//Geometry Commands
				Commands.Angle, Commands.Centroid, Commands.CircularArc,
				Commands.CircleArc, Commands.CircularSector, Commands.CircleSector,
				Commands.CircumcircularArc, Commands.CircumcircleArc, Commands.CircumcircularSector,
				Commands.CircumcircleSector, Commands.Cubic, Commands.Direction,
				Commands.Distance, Commands.Envelope, Commands.IntersectPath,
				Commands.Locus, Commands.LocusEquation, Commands.Midpoint,
				Commands.Point, Commands.Polygon, Commands.Polyline,
				Commands.PolyLine, Commands.ProveDetails, Commands.Ray,
				Commands.RigidPolygon, Commands.Sector, Commands.Segment,
				Commands.Slope, Commands.TriangleCurve, Commands.Vertex,
				// Transformation Commands
				Commands.Rotate, Commands.Shear, Commands.Translate,
				// Statistics Commands
				Commands.Fit, Commands.SigmaXX, Commands.SigmaXY,
				Commands.SigmaYY, Commands.Spearman, Commands.SumSquaredErrors,
				// Probability commands
				Commands.Bernoulli,
				// Conic Commands
				Commands.Axes, Commands.Center, Commands.ConjugateDiameter,
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
				Commands.IndexOf, Commands.Take,
				Commands.Zip,
				// Text Commands
				Commands.ContinuedFraction, Commands.FormulaText, Commands.LaTeX,
				Commands.FractionText, Commands.SurdText, Commands.TableText,
				Commands.Text, Commands.UnicodeToLetter,
				// Logical Commands
				Commands.IsDefined, Commands.Defined,
				Commands.Relation,
				// Optimization Command
				Commands.Maximize, Commands.Minimize,
				// Scripting Commands
				Commands.AttachCopyToView, Commands.Button, Commands.Checkbox,
				Commands.CopyFreeObject, Commands.Delete, Commands.GetTime,
				Commands.InputBox, Commands.Textfield, Commands.Pan,
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
				Commands.InfiniteCone, Commands.InfiniteCylinder, Commands.IntersectConic,
				Commands.Net, Commands.Octahedron,
				Commands.Plane, Commands.PlaneBisector, Commands.Prism,
				Commands.Pyramid, Commands.Side, Commands.Sphere,
				Commands.Surface, Commands.Tetrahedron, Commands.Top,
				Commands.Volume, Commands.QuadricSide, Commands.OrthogonalPlane,
				// SpreadSheet Commands
				Commands.Cell, Commands.CellRange, Commands.Column,
				Commands.ColumnName, Commands.FillCells, Commands.FillColumn,
				Commands.FillRow, Commands.Row
			);
		return commandNameFilter;
	}

	/**
	 * @return command filter for the 3D graphing app
	 */
	public static CommandFilter create3DGraphingCommandFilter() {
		CommandNameFilterSet commandNameFilter = new CommandNameFilterSet(true);
		commandNameFilter.addCommands(Commands.PieChart);
		return commandNameFilter;
	}
}
