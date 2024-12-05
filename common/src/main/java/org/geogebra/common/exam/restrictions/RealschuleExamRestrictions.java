package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Regression;
import static org.geogebra.common.kernel.commands.Commands.*;

import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.filter.BaseCommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelector;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;

final class RealschuleExamRestrictions extends ExamRestrictions {

	RealschuleExamRestrictions() {
		super(ExamType.REALSCHULE,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.GEOMETRY, SuiteSubApp.G3D,
						SuiteSubApp.PROBABILITY, SuiteSubApp.SCIENTIFIC),
				SuiteSubApp.GRAPHING,
				null,
				null,
				null,
				createCommandFilters(),
				createCommandArgumentFilters(),
				null,
				createContextMenuItemFilters(),
				createSyntaxFilter(),
				createToolsFilter(),
				null,
				null,
				null);
	}

	private static Set<CommandFilter> createCommandFilters() {
		return Set.of(new CommandNameFilter(true,
				Volume, Bottom, Cone, Cube, Cylinder, Dodecahedron, Ends, Icosahedron, Octahedron,
				Plane, QuadricSide, Surface, Tetrahedron, Top, Sphere, Prism, Pyramid,
				PlaneBisector, OrthogonalPlane, ConeInfinite, CylinderInfinite, IntersectConic,
				Height, Net, Assume, CFactor, CIFactor, IntegralSymbolic, CSolutions, CSolve,
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
				Div, Mod, Division));
	}

	private static Set<CommandArgumentFilter> createCommandArgumentFilters() {
		return Set.of(new RealschuleCommandArgumentFilter());
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
				EuclidianConstants.MODE_TEXTFIELD_ACTION

		);
	}

	@Override
	protected RestorableSettings createSavedSettings() {
		return new RealschuleSettings();
	}

	@Override
	public void applySettingsRestrictions(@Nonnull Settings settings) {
		super.applySettingsRestrictions(settings);
		EuclidianSettings euclidian = settings.getEuclidian(1);
		settings.getGeneral().setCoordFormat(Kernel.COORD_STYLE_AUSTRIAN);
		euclidian.beginBatch();
		euclidian.setAxisLabel(0, "x");
		euclidian.setAxisLabel(1, "y");
		euclidian.setGridType(EuclidianView.GRID_CARTESIAN);
		euclidian.setAxisNumberingDistance(0, 0.5);
		euclidian.setAxisNumberingDistance(1, 0.5);
		euclidian.endBatch();
	}

	private static class RealschuleCommandArgumentFilter extends BaseCommandArgumentFilter {
		@Override
		public void checkAllowed(Command command, CommandProcessor commandProcessor)
				throws MyError {
			GeoElement[] arguments = commandProcessor.resArgs(command);
			if (isCommand(command, Length)) {
				switch (command.getArgumentNumber()) {
					case 1:
						// Length(<Vector>) or Length(<Point>)
						if (arguments[0].isGeoVector() || arguments[0].isGeoPoint()) {
							throw commandProcessor.argErr(command, arguments[0]);
						}
						break;
					case 3:
						if (
								// Length(<Function>, <Start x-Value>, <End x-Value>)
								(arguments[0].isRealValuedFunction()
								&& arguments[1].isGeoNumeric()
								&& arguments[2].isGeoNumeric())

								// Length(<Function>, <Start point>, <End point>)
								|| (arguments[0].isRealValuedFunction()
								&& arguments[1].isGeoPoint()
								&& arguments[2].isGeoPoint())

								// Length(<Curve>, <Start t-Value>, <End t-Value>)
								|| (arguments[0].isGeoCurveCartesian()
								&& arguments[1].isGeoNumeric()
								&& arguments[2].isGeoNumeric())

								// Length(<Curve>, <Start point>, <End point>)
								|| (arguments[0].isGeoCurveCartesian()
								&& arguments[1].isGeoPoint()
								&& arguments[2].isGeoPoint())
						) {
							throw commandProcessor.argNumErr(command, 3);
						}
						break;
				}
			} else if (isCommand(command, Line)) {
				// Line(<Point>, <Parallel line>)
				if (command.getArgumentNumber() == 2
						&& arguments[0].isGeoPoint() && arguments[1].isGeoLine()) {
					throw commandProcessor.argNumErr(command, 2);
				}
			}
		}
	}

	private static class RealschuleSyntaxFilter implements SyntaxFilter {
		@Override
		public String getFilteredSyntax(String internalCommandName, String syntax) {
			if (Length.name().equals(internalCommandName)) {
				// Allow only Length(<Object>)
				return LineSelector.select(syntax, 0);
			} else if (Line.name().equals(internalCommandName)) {
				// Allow only Line(<Point>, <Point>) and Line(<Point>, <Direction Vector>)
				return LineSelector.select(syntax, 0, 2);
			}
			return syntax;
		}
	}
}
