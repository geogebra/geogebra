package org.geogebra.common.kernel.commands.selector;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.junit.Test;

public class CommandFilterFactoryTest {

	private static final Commands[] FILTERED = {
			Commands.PerpendicularVector, Commands.UnitPerpendicularVector, Commands.UnitVector,
			Commands.Cross, Commands.Dot, Commands.Dilate, Commands.Reflect, Commands.Rotate,
			Commands.Shear, Commands.Stretch, Commands.Translate, Commands.AngleBisector,
			Commands.Angle, Commands.Axes, Commands.Center, Commands.Circle, Commands.Conic,
			Commands.ConjugateDiameter, Commands.Directrix, Commands.Eccentricity,
			Commands.Ellipse, Commands.Focus, Commands.Hyperbola, Commands.Incircle,
			Commands.LinearEccentricity, Commands.MajorAxis, Commands.MinorAxis,
			Commands.Parabola, Commands.Parameter, Commands.Polar, Commands.SemiMajorAxisLength,
			Commands.SemiMinorAxisLength, Commands.Semicircle, Commands.Relation,
			Commands.AffineRatio, Commands.Arc, Commands.AreCollinear, Commands.AreConcurrent,
			Commands.AreConcyclic, Commands.AreCongruent, Commands.AreEqual, Commands.AreParallel,
			Commands.ArePerpendicular, Commands.Area, Commands.Barycenter, Commands.Centroid,
			Commands.CircularArc, Commands.CircularSector, Commands.CircumcircularArc,
			Commands.CircumcircularSector, Commands.Circumference, Commands.ClosestPoint,
			Commands.ClosestPointRegion, Commands.CrossRatio, Commands.Cubic, Commands.Direction,
			Commands.Distance, Commands.Envelope, Commands.IntersectPath, Commands.Locus,
			Commands.LocusEquation, Commands.Midpoint, Commands.Perimeter,
			Commands.PerpendicularBisector, Commands.PerpendicularLine, Commands.Polygon,
			Commands.Prove, Commands.ProveDetails, Commands.Radius,
			Commands.RigidPolygon, Commands.Sector, Commands.Segment, Commands.Slope,
			Commands.Tangent, Commands.TriangleCenter, Commands.TriangleCurve, Commands.Trilinear,
			Commands.Vertex, Commands.Polynomial, Commands.TaylorPolynomial, Commands.Asymptote,
			Commands.OsculatingCircle, Commands.CommonDenominator, Commands.CompleteSquare,
			Commands.Div, Commands.Mod, Commands.Division, Commands.Tetrahedron, Commands.Surface
	};
	private CommandFilter filter;

	@Test
	public void testGraphingCommandFilter() {
		filter = CommandFilterFactory.createGraphingCommandFilter();
		for (Commands command : FILTERED) {
			assertAllowed(false, command);
		}
		for (Commands command : Commands.values()) {
			int table = command.getTable();
			if (table == CommandsConstants.TABLE_TRANSFORMATION
					|| table == CommandsConstants.TABLE_CONIC) {
				assertAllowed(false, command);
			}
		}
	}

	@Test
	public void testCasCommandFilterForVectorCommands() {
		Commands[] allowedVectorCommands = {Commands.PerpendicularVector,
				Commands.UnitPerpendicularVector, Commands.UnitVector, Commands.ParseToNumber};
		filter = CommandFilterFactory.createCasCommandFilter();
		for (Commands command : allowedVectorCommands) {
			assertAllowed(true, command);
		}
	}

	@Test
	public void testCasCommandFilterForAsymptoteCommand() {
		filter = CommandFilterFactory.createCasCommandFilter();
		assertAllowed(true, Commands.Asymptote);
	}

	@Test
	public void testMmsCommandFilter() {
		filter = CommandFilterFactory.createMmsFilter();
		assertAllowed(false, Commands.AreEqual);
		assertAllowed(false, Commands.Hyperbola);
		assertAllowed(false, Commands.Slope);
		assertAllowed(false, Commands.Intersect);
		// disabled commands that have different english and internal name
		assertAllowed(false, Commands.DelaunayTriangulation);
		assertAllowed(false, Commands.SemiMajorAxisLength);
		assertAllowed(false, Commands.SemiMinorAxisLength);
		assertAllowed(false, Commands.MajorAxis);
		assertAllowed(false, Commands.ConjugateDiameter);
		assertAllowed(false, Commands.Curve);
		assertAllowed(false, Commands.MinorAxis);
		assertAllowed(false, Commands.Side);
		assertAllowed(false, Commands.Reflect);
		assertAllowed(false, Commands.Asymptote);
	}

	@Test
	public void testBayernCasCommandFilter() {
		filter = CommandFilterFactory.createBayernCasFilter();
		assertAllowed(false, Commands.Plane);
	}

	@Test
	public void testVlaanderenCommandFilter() {
		filter = CommandFilterFactory.createVlaanderenFilter();
		assertAllowed(false, Commands.Derivative);
		assertAllowed(false, Commands.NDerivative);
		assertAllowed(false, Commands.Integral);
		assertAllowed(false, Commands.IntegralSymbolic);
		assertAllowed(false, Commands.IntegralBetween);
		assertAllowed(false, Commands.NIntegral);
		assertAllowed(false, Commands.Solve);
		assertAllowed(false, Commands.SolveQuartic);
		assertAllowed(false, Commands.SolveODE);
		assertAllowed(false, Commands.SolveCubic);
		assertAllowed(false, Commands.Solutions);
		assertAllowed(false, Commands.NSolve);
		assertAllowed(false, Commands.NSolveODE);
		assertAllowed(false, Commands.NSolutions);
	}

	private void assertAllowed(boolean shouldAllow, Commands command) {
		assertEquals(command + (shouldAllow ? " should" : " should not") + " be allowed",
				shouldAllow, filter.isCommandAllowed(command));
	}
}
