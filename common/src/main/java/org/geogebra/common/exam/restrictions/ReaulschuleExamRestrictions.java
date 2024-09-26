package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.kernel.commands.Commands.*;

import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandNotFoundError;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.filter.BaseCommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

final class ReaulschuleExamRestrictions extends ExamRestrictions {

	ReaulschuleExamRestrictions() {
		super(ExamType.REALSCHULE,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.GEOMETRY, SuiteSubApp.G3D,
						SuiteSubApp.PROBABILITY, SuiteSubApp.SCIENTIFIC),
				SuiteSubApp.GRAPHING,
				null,
				null,
				createCommandFilters(),
				createCommandArgumentFilters(),
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

	private static class RealschuleCommandArgumentFilter extends BaseCommandArgumentFilter {

		@Override
		public void checkAllowed(Command command, CommandProcessor commandProcessor) throws MyError {
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
}
