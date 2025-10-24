package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.SuiteSubApp.CAS;
import static org.geogebra.common.SuiteSubApp.G3D;
import static org.geogebra.common.SuiteSubApp.GEOMETRY;
import static org.geogebra.common.SuiteSubApp.GRAPHING;
import static org.geogebra.common.SuiteSubApp.PROBABILITY;
import static org.geogebra.common.SuiteSubApp.SCIENTIFIC;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Regression;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.HIDE;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.IGNORE;
import static org.geogebra.common.kernel.commands.Commands.AffineRatio;
import static org.geogebra.common.kernel.commands.Commands.Angle;
import static org.geogebra.common.kernel.commands.Commands.AngleBisector;
import static org.geogebra.common.kernel.commands.Commands.Arc;
import static org.geogebra.common.kernel.commands.Commands.AreCollinear;
import static org.geogebra.common.kernel.commands.Commands.AreConcurrent;
import static org.geogebra.common.kernel.commands.Commands.AreConcyclic;
import static org.geogebra.common.kernel.commands.Commands.AreCongruent;
import static org.geogebra.common.kernel.commands.Commands.AreEqual;
import static org.geogebra.common.kernel.commands.Commands.AreParallel;
import static org.geogebra.common.kernel.commands.Commands.ArePerpendicular;
import static org.geogebra.common.kernel.commands.Commands.Area;
import static org.geogebra.common.kernel.commands.Commands.Assume;
import static org.geogebra.common.kernel.commands.Commands.Asymptote;
import static org.geogebra.common.kernel.commands.Commands.Axes;
import static org.geogebra.common.kernel.commands.Commands.Barycenter;
import static org.geogebra.common.kernel.commands.Commands.Bottom;
import static org.geogebra.common.kernel.commands.Commands.CFactor;
import static org.geogebra.common.kernel.commands.Commands.CIFactor;
import static org.geogebra.common.kernel.commands.Commands.CSolutions;
import static org.geogebra.common.kernel.commands.Commands.CSolve;
import static org.geogebra.common.kernel.commands.Commands.Center;
import static org.geogebra.common.kernel.commands.Commands.Centroid;
import static org.geogebra.common.kernel.commands.Commands.CharacteristicPolynomial;
import static org.geogebra.common.kernel.commands.Commands.Circle;
import static org.geogebra.common.kernel.commands.Commands.CircularArc;
import static org.geogebra.common.kernel.commands.Commands.CircularSector;
import static org.geogebra.common.kernel.commands.Commands.CircumcircularArc;
import static org.geogebra.common.kernel.commands.Commands.CircumcircularSector;
import static org.geogebra.common.kernel.commands.Commands.Circumference;
import static org.geogebra.common.kernel.commands.Commands.ClosestPoint;
import static org.geogebra.common.kernel.commands.Commands.ClosestPointRegion;
import static org.geogebra.common.kernel.commands.Commands.Coefficients;
import static org.geogebra.common.kernel.commands.Commands.CommonDenominator;
import static org.geogebra.common.kernel.commands.Commands.CompleteSquare;
import static org.geogebra.common.kernel.commands.Commands.Cone;
import static org.geogebra.common.kernel.commands.Commands.ConeInfinite;
import static org.geogebra.common.kernel.commands.Commands.Conic;
import static org.geogebra.common.kernel.commands.Commands.ConjugateDiameter;
import static org.geogebra.common.kernel.commands.Commands.Cross;
import static org.geogebra.common.kernel.commands.Commands.CrossRatio;
import static org.geogebra.common.kernel.commands.Commands.Cube;
import static org.geogebra.common.kernel.commands.Commands.Cubic;
import static org.geogebra.common.kernel.commands.Commands.Cylinder;
import static org.geogebra.common.kernel.commands.Commands.CylinderInfinite;
import static org.geogebra.common.kernel.commands.Commands.Degree;
import static org.geogebra.common.kernel.commands.Commands.Dilate;
import static org.geogebra.common.kernel.commands.Commands.Direction;
import static org.geogebra.common.kernel.commands.Commands.Directrix;
import static org.geogebra.common.kernel.commands.Commands.Distance;
import static org.geogebra.common.kernel.commands.Commands.Div;
import static org.geogebra.common.kernel.commands.Commands.Division;
import static org.geogebra.common.kernel.commands.Commands.Dodecahedron;
import static org.geogebra.common.kernel.commands.Commands.Dot;
import static org.geogebra.common.kernel.commands.Commands.Eccentricity;
import static org.geogebra.common.kernel.commands.Commands.Eigenvectors;
import static org.geogebra.common.kernel.commands.Commands.Eliminate;
import static org.geogebra.common.kernel.commands.Commands.Ellipse;
import static org.geogebra.common.kernel.commands.Commands.Ends;
import static org.geogebra.common.kernel.commands.Commands.Envelope;
import static org.geogebra.common.kernel.commands.Commands.Expand;
import static org.geogebra.common.kernel.commands.Commands.ExtendedGCD;
import static org.geogebra.common.kernel.commands.Commands.Factor;
import static org.geogebra.common.kernel.commands.Commands.Factors;
import static org.geogebra.common.kernel.commands.Commands.Focus;
import static org.geogebra.common.kernel.commands.Commands.Function;
import static org.geogebra.common.kernel.commands.Commands.GroebnerDegRevLex;
import static org.geogebra.common.kernel.commands.Commands.GroebnerLex;
import static org.geogebra.common.kernel.commands.Commands.GroebnerLexDeg;
import static org.geogebra.common.kernel.commands.Commands.Height;
import static org.geogebra.common.kernel.commands.Commands.Hyperbola;
import static org.geogebra.common.kernel.commands.Commands.IFactor;
import static org.geogebra.common.kernel.commands.Commands.Icosahedron;
import static org.geogebra.common.kernel.commands.Commands.ImplicitDerivative;
import static org.geogebra.common.kernel.commands.Commands.Incircle;
import static org.geogebra.common.kernel.commands.Commands.IntegralSymbolic;
import static org.geogebra.common.kernel.commands.Commands.IntersectConic;
import static org.geogebra.common.kernel.commands.Commands.IntersectPath;
import static org.geogebra.common.kernel.commands.Commands.InverseLaplace;
import static org.geogebra.common.kernel.commands.Commands.IsTangent;
import static org.geogebra.common.kernel.commands.Commands.IsVertexForm;
import static org.geogebra.common.kernel.commands.Commands.JordanDiagonalization;
import static org.geogebra.common.kernel.commands.Commands.LUDecomposition;
import static org.geogebra.common.kernel.commands.Commands.Laplace;
import static org.geogebra.common.kernel.commands.Commands.Length;
import static org.geogebra.common.kernel.commands.Commands.Limit;
import static org.geogebra.common.kernel.commands.Commands.LimitAbove;
import static org.geogebra.common.kernel.commands.Commands.LimitBelow;
import static org.geogebra.common.kernel.commands.Commands.Line;
import static org.geogebra.common.kernel.commands.Commands.LinearEccentricity;
import static org.geogebra.common.kernel.commands.Commands.Locus;
import static org.geogebra.common.kernel.commands.Commands.LocusEquation;
import static org.geogebra.common.kernel.commands.Commands.MajorAxis;
import static org.geogebra.common.kernel.commands.Commands.Midpoint;
import static org.geogebra.common.kernel.commands.Commands.MinimalPolynomial;
import static org.geogebra.common.kernel.commands.Commands.MinorAxis;
import static org.geogebra.common.kernel.commands.Commands.MixedNumber;
import static org.geogebra.common.kernel.commands.Commands.Mod;
import static org.geogebra.common.kernel.commands.Commands.ModularExponent;
import static org.geogebra.common.kernel.commands.Commands.NSolutions;
import static org.geogebra.common.kernel.commands.Commands.NSolve;
import static org.geogebra.common.kernel.commands.Commands.Net;
import static org.geogebra.common.kernel.commands.Commands.NextPrime;
import static org.geogebra.common.kernel.commands.Commands.Numeric;
import static org.geogebra.common.kernel.commands.Commands.Octahedron;
import static org.geogebra.common.kernel.commands.Commands.OrthogonalPlane;
import static org.geogebra.common.kernel.commands.Commands.OsculatingCircle;
import static org.geogebra.common.kernel.commands.Commands.Parabola;
import static org.geogebra.common.kernel.commands.Commands.Parameter;
import static org.geogebra.common.kernel.commands.Commands.ParametricDerivative;
import static org.geogebra.common.kernel.commands.Commands.PartialFractions;
import static org.geogebra.common.kernel.commands.Commands.Perimeter;
import static org.geogebra.common.kernel.commands.Commands.PerpendicularBisector;
import static org.geogebra.common.kernel.commands.Commands.PerpendicularLine;
import static org.geogebra.common.kernel.commands.Commands.PerpendicularVector;
import static org.geogebra.common.kernel.commands.Commands.Plane;
import static org.geogebra.common.kernel.commands.Commands.PlaneBisector;
import static org.geogebra.common.kernel.commands.Commands.PlotSolve;
import static org.geogebra.common.kernel.commands.Commands.Polar;
import static org.geogebra.common.kernel.commands.Commands.Polygon;
import static org.geogebra.common.kernel.commands.Commands.Polyline;
import static org.geogebra.common.kernel.commands.Commands.Polynomial;
import static org.geogebra.common.kernel.commands.Commands.PreviousPrime;
import static org.geogebra.common.kernel.commands.Commands.Prism;
import static org.geogebra.common.kernel.commands.Commands.Prove;
import static org.geogebra.common.kernel.commands.Commands.ProveDetails;
import static org.geogebra.common.kernel.commands.Commands.Pyramid;
import static org.geogebra.common.kernel.commands.Commands.QRDecomposition;
import static org.geogebra.common.kernel.commands.Commands.QuadricSide;
import static org.geogebra.common.kernel.commands.Commands.Radius;
import static org.geogebra.common.kernel.commands.Commands.Rationalize;
import static org.geogebra.common.kernel.commands.Commands.Reflect;
import static org.geogebra.common.kernel.commands.Commands.Relation;
import static org.geogebra.common.kernel.commands.Commands.RemovableDiscontinuity;
import static org.geogebra.common.kernel.commands.Commands.RigidPolygon;
import static org.geogebra.common.kernel.commands.Commands.Rotate;
import static org.geogebra.common.kernel.commands.Commands.Sector;
import static org.geogebra.common.kernel.commands.Commands.Segment;
import static org.geogebra.common.kernel.commands.Commands.SemiMajorAxisLength;
import static org.geogebra.common.kernel.commands.Commands.SemiMinorAxisLength;
import static org.geogebra.common.kernel.commands.Commands.Semicircle;
import static org.geogebra.common.kernel.commands.Commands.Shear;
import static org.geogebra.common.kernel.commands.Commands.Simplify;
import static org.geogebra.common.kernel.commands.Commands.Slope;
import static org.geogebra.common.kernel.commands.Commands.Solutions;
import static org.geogebra.common.kernel.commands.Commands.Solve;
import static org.geogebra.common.kernel.commands.Commands.SolveCubic;
import static org.geogebra.common.kernel.commands.Commands.SolveODE;
import static org.geogebra.common.kernel.commands.Commands.SolveQuartic;
import static org.geogebra.common.kernel.commands.Commands.Sphere;
import static org.geogebra.common.kernel.commands.Commands.Stretch;
import static org.geogebra.common.kernel.commands.Commands.Substitute;
import static org.geogebra.common.kernel.commands.Commands.SurdText;
import static org.geogebra.common.kernel.commands.Commands.Surface;
import static org.geogebra.common.kernel.commands.Commands.Tangent;
import static org.geogebra.common.kernel.commands.Commands.TaylorPolynomial;
import static org.geogebra.common.kernel.commands.Commands.Tetrahedron;
import static org.geogebra.common.kernel.commands.Commands.ToExponential;
import static org.geogebra.common.kernel.commands.Commands.Top;
import static org.geogebra.common.kernel.commands.Commands.Translate;
import static org.geogebra.common.kernel.commands.Commands.TriangleCenter;
import static org.geogebra.common.kernel.commands.Commands.TriangleCurve;
import static org.geogebra.common.kernel.commands.Commands.TrigCombine;
import static org.geogebra.common.kernel.commands.Commands.TrigExpand;
import static org.geogebra.common.kernel.commands.Commands.TrigSimplify;
import static org.geogebra.common.kernel.commands.Commands.Trilinear;
import static org.geogebra.common.kernel.commands.Commands.UnitPerpendicularVector;
import static org.geogebra.common.kernel.commands.Commands.UnitVector;
import static org.geogebra.common.kernel.commands.Commands.Vertex;
import static org.geogebra.common.kernel.commands.Commands.Volume;
import static org.geogebra.common.plugin.Operation.ALT;
import static org.geogebra.common.plugin.Operation.ARG;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.realschule.RealschuleEquationBehaviour;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationFilter;
import org.geogebra.common.kernel.arithmetic.filter.graphing.GraphingExpressionFilterFactory;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.Syntax;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelectorSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.common.properties.impl.objects.QuadraticEquationFormProperty;

public final class RealschuleExamRestrictions extends ExamRestrictions {

	RealschuleExamRestrictions() {
		super(ExamType.BAYERN_GR,
				Set.of(CAS, GEOMETRY, G3D, PROBABILITY, SCIENTIFIC),
				GRAPHING,
				createFeatureRestrictions(),
				getInputExpressionFilter(),
				null,
				createCommandFilters(),
				createCommandArgumentFilters(),
				createOperationFilter(),
				createContextMenuItemFilters(),
				createSyntaxFilter(),
				createToolsFilter(),
				createPropertyRestrictions(),
				createVisibilityRestrictions(),
				createEquationBehaviour(),
				null,
				null,
				null);
	}

	private static Set<ExamFeatureRestriction> createFeatureRestrictions() {
		return Set.of(ExamFeatureRestriction.HIDE_CALCULATED_EQUATION,
				ExamFeatureRestriction.RESTRICT_CHANGING_EQUATION_FORM);
	}

	private static Set<CommandFilter> createCommandFilters() {
		return Set.of(new CommandNameFilter(true,
				Volume, Bottom, Cone, Cube, Cylinder, Dodecahedron, Ends, Icosahedron, Octahedron,
				Plane, QuadricSide, Surface, Tetrahedron, Top, Sphere, Prism, Pyramid,
				PlaneBisector, OrthogonalPlane, ConeInfinite, CylinderInfinite, IntersectConic,
				Height, Net, Assume, CFactor, CIFactor, IntegralSymbolic,
				Eliminate, GroebnerLex, GroebnerDegRevLex, GroebnerLexDeg, Numeric, MixedNumber,
				Rationalize, SolveCubic, SolveQuartic, JordanDiagonalization, Eigenvectors, Laplace,
				InverseLaplace, Substitute, ToExponential, ExtendedGCD, ModularExponent,
				CharacteristicPolynomial, MinimalPolynomial, LUDecomposition, QRDecomposition,
				PerpendicularVector, UnitPerpendicularVector, UnitVector, Cross, Dot, Dilate,
				Reflect, Rotate, Shear, Stretch, Translate, AngleBisector, Angle, Axes, Center,
				Circle, Conic, ConjugateDiameter, Directrix, Eccentricity, Ellipse, Focus,
				Hyperbola, Incircle, LinearEccentricity, MajorAxis, MinorAxis, Parabola, Parameter,
				Polar, SemiMajorAxisLength, SemiMinorAxisLength, Semicircle, Relation, AffineRatio,
				Arc, AreCollinear, AreConcurrent, AreConcyclic, AreCongruent, AreEqual, AreParallel,
				ArePerpendicular, Area, Barycenter, Centroid, CircularArc, CircularSector,
				CircumcircularArc, CircumcircularSector, Circumference, ClosestPoint,
				ClosestPointRegion, CrossRatio, Cubic, Direction, Distance, Envelope, IntersectPath,
				Locus, LocusEquation, Midpoint, Perimeter, PerpendicularBisector, PerpendicularLine,
				Polygon, Polyline, Prove, ProveDetails, Radius, RigidPolygon, Sector, Segment,
				Slope, Tangent, TriangleCenter, TriangleCurve, Trilinear, Vertex, Polynomial,
				TaylorPolynomial, Asymptote, OsculatingCircle, CommonDenominator, CompleteSquare,
				Div, Mod, Division, Function,
				Solve, NSolve, Solutions, NSolutions, CSolve, CSolutions,
				Coefficients, Degree, Expand, Factor, Factors, IFactor, ImplicitDerivative,
				IsTangent, IsVertexForm, Limit, LimitAbove, LimitBelow, NextPrime,
				ParametricDerivative, PartialFractions, PlotSolve, PreviousPrime,
				RemovableDiscontinuity, Simplify, SolveODE, SurdText, TrigCombine, TrigExpand,
				TrigSimplify));
	}

	private static Set<CommandArgumentFilter> createCommandArgumentFilters() {
		return Set.of(new RealschuleCommandArgumentFilter());
	}

	private static OperationFilter createOperationFilter() {
		Set<Operation> restrictedOperations = Set.of(ALT, ARG);
		return operation -> !restrictedOperations.contains(operation);
	}

	private static Set<ContextMenuItemFilter> createContextMenuItemFilters() {
		return Set.of(contextMenuItem -> !Regression.isSameItemAs(contextMenuItem));
	}

	private static SyntaxFilter createSyntaxFilter() {
		return new RealschuleSyntaxFilter();
	}

	private static ToolCollectionFilter createToolsFilter() {
		return new ToolCollectionSetFilter(
				EuclidianConstants.MODE_FITLINE,
				EuclidianConstants.MODE_IMAGE,
				EuclidianConstants.MODE_TEXT,
				EuclidianConstants.MODE_ANGLE,
				EuclidianConstants.MODE_DISTANCE,
				EuclidianConstants.MODE_AREA,
				EuclidianConstants.MODE_ANGLE_FIXED,
				EuclidianConstants.MODE_SLOPE,
				EuclidianConstants.MODE_COMPLEX_NUMBER,
				EuclidianConstants.MODE_MIDPOINT,
				EuclidianConstants.MODE_ORTHOGONAL,
				EuclidianConstants.MODE_LINE_BISECTOR,
				EuclidianConstants.MODE_PARALLEL,
				EuclidianConstants.MODE_ANGULAR_BISECTOR,
				EuclidianConstants.MODE_TANGENTS,
				EuclidianConstants.MODE_LOCUS,
				EuclidianConstants.MODE_SEGMENT,
				EuclidianConstants.MODE_SEGMENT_FIXED,
				EuclidianConstants.MODE_POLAR_DIAMETER,
				EuclidianConstants.MODE_POLYLINE,
				EuclidianConstants.MODE_POLYGON,
				EuclidianConstants.MODE_REGULAR_POLYGON,
				EuclidianConstants.MODE_VECTOR_POLYGON,
				EuclidianConstants.MODE_RIGID_POLYGON,
				EuclidianConstants.MODE_CIRCLE_TWO_POINTS,
				EuclidianConstants.MODE_COMPASSES,
				EuclidianConstants.MODE_SEMICIRCLE,
				EuclidianConstants.MODE_CIRCLE_POINT_RADIUS,
				EuclidianConstants.MODE_CIRCLE_THREE_POINTS,
				EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS,
				EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
				EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS,
				EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS,
				EuclidianConstants.MODE_ELLIPSE_THREE_POINTS,
				EuclidianConstants.MODE_CONIC_FIVE_POINTS,
				EuclidianConstants.MODE_PARABOLA,
				EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS,
				EuclidianConstants.MODE_MIRROR_AT_LINE,
				EuclidianConstants.MODE_MIRROR_AT_POINT,
				EuclidianConstants.MODE_TRANSLATE_BY_VECTOR,
				EuclidianConstants.MODE_ROTATE_BY_ANGLE,
				EuclidianConstants.MODE_DILATE_FROM_POINT,
				EuclidianConstants.MODE_MIRROR_AT_CIRCLE,
				EuclidianConstants.MODE_RELATION,
				EuclidianConstants.MODE_BUTTON_ACTION,
				EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX,
				EuclidianConstants.MODE_TEXTFIELD_ACTION);
	}

	@Override
	protected RestorableSettings createSavedSettings() {
		return new RealschuleSettings();
	}

	@Override
	public void applySettingsRestrictions(@Nonnull Settings settings,
			@Nonnull ConstructionDefaults defaults) {
		super.applySettingsRestrictions(settings, defaults);
		EuclidianSettings euclidian = settings.getEuclidian(1);
		settings.getGeneral().setCoordFormat(Kernel.COORD_STYLE_AUSTRIAN);
		euclidian.beginBatch();
		euclidian.setAxisLabel(0, "x");
		euclidian.setAxisLabel(1, "y");
		euclidian.setGridType(EuclidianView.GRID_CARTESIAN);
		euclidian.endBatch();
		settings.getAlgebra().setEquationChangeByDragRestricted(true);
		settings.getAlgebra().setEngineeringNotationEnabled(true);
		for (int index: ConstructionDefaults.POINT_INDICES) {
			GeoPointND point = (GeoPointND) defaults.getDefaultGeo(index);
			if (point != null) {
				point.setPointStyle(EuclidianStyleConstants.POINT_STYLE_CROSS);
			}
		}
	}

	private static final class RealschuleCommandArgumentFilter implements CommandArgumentFilter {
		private final Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands = Map.of(
				Length, Set.of(
						Syntax.of(Length, GeoElement::isGeoList),
						Syntax.of(Length, GeoElement::isGeoText)),
				Line, Set.of(
						Syntax.of(Line, GeoElement::isGeoPoint, GeoElement::isGeoPoint),
						Syntax.of(Line, GeoElement::isGeoPoint, GeoElement::isGeoVector)));

		@Override
		public void checkAllowed(Command command, CommandProcessor commandProcessor)
				throws MyError {
			Syntax.checkRestrictedSyntaxes(
					allowedSyntaxesForRestrictedCommands, command, commandProcessor);
		}
	}

	private static final class RealschuleSyntaxFilter extends LineSelectorSyntaxFilter {

		private RealschuleSyntaxFilter() {
			// Allow only Length(<Object>)
			addSelector(Length, 0);
			// Allow only Line(<Point>, <Point>) and Line(<Point>, <Direction Vector>)
			addSelector(Line, 0, 2);
		}
	}

	private static Map<String, PropertyRestriction> createPropertyRestrictions() {
		return Map.of(LinearEquationFormProperty.NAME_KEY, new PropertyRestriction(true, null),
				QuadraticEquationFormProperty.NAME_KEY, new PropertyRestriction(true, null));
	}

	private static Set<VisibilityRestriction> createVisibilityRestrictions() {
		return Set.of(new HiddenSingleVariableEquationVisibilityRestriction());
	}

	private static EquationBehaviour createEquationBehaviour() {
		return new RealschuleEquationBehaviour();
	}

	/**
	 * Restricts the visibility of equations with a single variable x.
	 * <p>Examples: </p>
	 * <ul>
	 *     <li>x = 0</li>
	 *     <li>x^2 = 0</li>
	 *     <li>sin(x) = 0</li>
	 * </ul>
	 */
	private static final class HiddenSingleVariableEquationVisibilityRestriction
			implements VisibilityRestriction {
		@Override
		public @Nonnull Effect getEffect(GeoElement geoElement) {
			return (geoElement instanceof EquationValue
					&& isOnlyX(((EquationValue) geoElement).getEquationVariables())
			) ? HIDE : IGNORE;
		}

		private boolean isOnlyX(String[] equationVariables) {
			return equationVariables.length == 1 && "x".equals(equationVariables[0]);
		}
	}

	private static Set<ExpressionFilter> getInputExpressionFilter() {
		return Set.of(GraphingExpressionFilterFactory.createFilter());
	}
}
