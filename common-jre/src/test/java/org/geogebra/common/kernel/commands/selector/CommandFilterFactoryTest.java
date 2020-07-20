package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.junit.Assert;
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
			Commands.Div, Commands.Mod, Commands.Division
	};

	@Test
	public void testGraphingCommandFilter() {
		CommandFilter filter = CommandFilterFactory.createGraphingCommandFilter();
		for (Commands command : FILTERED) {
			Assert.assertFalse(filter.isCommandAllowed(command));
		}
		for (Commands command : Commands.values()) {
			int table = command.getTable();
			if (table == CommandsConstants.TABLE_TRANSFORMATION
					|| table == CommandsConstants.TABLE_CONIC) {
				Assert.assertFalse(filter.isCommandAllowed(command));
			}
		}
	}

	@Test
	public void testCasCommandFilterForVectorCommands() {
		Commands[] allowedVectorCommands = {Commands.PerpendicularVector,
				Commands.UnitPerpendicularVector, Commands.UnitVector};
		CommandFilter filter = CommandFilterFactory.createCasCommandFilter();
		for (Commands command : allowedVectorCommands) {
			Assert.assertTrue(filter.isCommandAllowed(command));
		}
	}
}
