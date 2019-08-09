package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Creates CommandNameFilters for various apps.
 * 
 * @author laszlo
 *
 */
public final class CommandNameFilterFactory {
	/**
	 *
	 * @return Returns the CommandNameFilter that allows only the Scientific
	 *         Calculator commands
	 */
	public static CommandNameFilter createSciCalcCommandNameFilter() {
		CommandNameFilterSet commandNameFilter = new CommandNameFilterSet(
				false);
		commandNameFilter.addCommands(Commands.Mean, Commands.mean, Commands.SD,
				Commands.stdev, Commands.SampleSD, Commands.stdevp,
				Commands.nPr, Commands.nCr, Commands.Binomial, Commands.MAD,
				Commands.mad);
		return commandNameFilter;
	}

	/**
	 * @return name filter for apps with no CAS
	 */
	public static CommandNameFilter createNoCasCommandNameFilter() {
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
				Commands.Solutions, Commands.NSolutions, Commands.NSolve);
		return commandNameFilter;
	}

	/**
	 * @return name filter for apps with CAS
	 */
	public static CommandNameFilter createCasCommandNameFilter() {
		CommandNameFilterSet commandNameFilter = new CommandNameFilterSet(true);
		commandNameFilter.addCommands(
				// CAS specific command
				Commands.Delete, Commands.Invert, Commands.Max,
				Commands.Min, Commands.NSolutions, Commands.PerpendicularVector,
				Commands.OrthogonalVector, Commands.Poisson, Commands.ReducedRowEchelonForm,
				Commands.Sequence, Commands.Substitute, Commands.Sum,
				Commands.Transpose, Commands.UnitPerpendicularVector, Commands.UnitOrthogonalVector,
				Commands.UnitVector,
				// Function Commands
				Commands.Asymptote, Commands.CurvatureVector, Commands.DataFunction,
				Commands.Function, Commands.ImplicitCurve, Commands.IterationList,
				Commands.NSolveODE, Commands.OsculatingCircle, Commands.ParametricDerivative,
				Commands.PathParameter, Commands.RootList, Commands.Roots,
				Commands.Spline,
				// Vector And Matrix Commands
				Commands.ApplyMatrix, Commands.Identity, Commands.Vector,
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
				Commands.Slope, Commands.Tangent, Commands.TriangleCurve,
				Commands.Vertex,
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
				Commands.StickGraph,
				// Discrete Math Commands
				Commands.ShortestDistance,
				// GeoGebra Commands
				Commands.DynamicCoordinates, Commands.Object, Commands.SlowPlot,
				Commands.ToolImage,
				// List Commands
				Commands.IndexOf, Commands.RemoveUndefined, Commands.Take,
				Commands.Zip,
				// Text Commands
				Commands.ContinuedFraction, Commands.FormulaText, Commands.LaTeX,
				Commands.FractionText, Commands.SurdText, Commands.TableText,
				Commands.Text, Commands.UnicodeToLetter,
				// Logical Commands
				Commands.If, Commands.IsDefined, Commands.Defined,
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
				Commands.SetVisibleInView, Commands.ShowLabel, Commands.Slider,
				Commands.StartLogging, Commands.StartRecord, Commands.StopLogging,
				Commands.Turtle, Commands.TurtleBack, Commands.TurtleDown,
				Commands.TurtleForward, Commands.TurtleLeft, Commands.TurtleRight,
				Commands.TurtleUp, Commands.UpdateConstruction,
				// 3D Commands
				Commands.Bottom, Commands.Cone, Commands.Cube,
				Commands.Cylinder, Commands.Dodecahedron, Commands.Ends,
				Commands.Height, Commands.Icosahedron,
				Commands.InfiniteCone, Commands.InfiniteCylinder, Commands.IntersectConic,
				Commands.Net, Commands.Octahedron, Commands.PerpendicularPlane,
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
}
