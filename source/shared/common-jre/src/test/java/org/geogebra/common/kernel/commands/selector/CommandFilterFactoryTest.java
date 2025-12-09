/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.commands.selector;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.test.annotation.Issue;
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
				Commands.UnitPerpendicularVector, Commands.UnitVector, Commands.ParseToNumber,
				Commands.Poisson};
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

	@Issue("APPS-6314")
	@Test
	public void testCasCommandFilterAllowsChartCommands() {
		filter = CommandFilterFactory.createCasCommandFilter();
		assertAllowed(true, Commands.BarChart);
		assertAllowed(true, Commands.StepGraph);
		assertAllowed(true, Commands.StickGraph);
	}

	private void assertAllowed(boolean shouldAllow, Commands command) {
		assertEquals(command + (shouldAllow ? " should" : " should not") + " be allowed",
				shouldAllow, filter.isCommandAllowed(command));
	}
}
