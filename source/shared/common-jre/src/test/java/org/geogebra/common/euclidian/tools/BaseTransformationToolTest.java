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

package org.geogebra.common.euclidian.tools;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.jspecify.annotations.NonNull;

/** Shared geometric, output and dependency assertions for transformation tool tests. */
abstract class BaseTransformationToolTest extends BaseToolTest {

	/** Checks that a point is defined and has the expected inhomogeneous coordinates. */
	protected void assertPoint(@NonNull GeoPointND point, double x, double y) {
		assertAll(
				() -> assertTrue(point.isDefined()),
				() -> assertEquals(x, point.getInhomX(), 1E-8),
				() -> assertEquals(y, point.getInhomY(), 1E-8));
	}

	/** Checks that a transformation creates a distinct object with the expected input identities. */
	protected void assertTransformationInputs(
			@NonNull Class<? extends AlgoTransformation> algorithmType,
			@NonNull GeoElementND transformed,
			@NonNull GeoElement source,
			@NonNull GeoElement parameter,
			@NonNull GeoElement... additionalParameters) {
		AlgoTransformation algorithm =
				assertInstanceOf(algorithmType, transformed.getParentAlgorithm());
		assertEquals(
				2 + additionalParameters.length,
				algorithm.getInput().length,
				"Transformation should have the expected inputs");
		assertAll(
				() -> assertNotSame(source, transformed.toGeoElement()),
				() -> assertSame(transformed.toGeoElement(), algorithm.getResult()),
				() -> assertSame(source, algorithm.getInput()[0]),
				() -> assertSame(parameter, algorithm.getInput()[1]));
		assertRegisteredInput(source);
		assertRegisteredInput(parameter);
		for (int index = 0; index < additionalParameters.length; index++) {
			assertSame(additionalParameters[index], algorithm.getInput()[2 + index]);
			assertRegisteredInput(additionalParameters[index]);
		}
	}

	/** Checks the entire set of labeled objects, including outputs of other types. */
	protected void assertOnlyNewObjects(
			@NonNull Set<String> existingObjects, @NonNull GeoElementND... expectedObjects) {
		Set<String> expectedNames = new HashSet<>(existingObjects);
		for (GeoElementND object : expectedObjects) {
			assertTrue(object.isLabelSet(), "Expected output should have a label");
			assertTrue(expectedNames.add(object.getLabelSimple()), "Expected output should be new");
			assertSame(
					object.toGeoElement(),
					lookup(object.getLabelSimple()),
					"Expected output should be the object registered under its label");
		}
		assertEquals(expectedNames, getObjectNames());
	}

	/** Checks all transformed vertex dependencies and the connections between polygon edges. */
	protected void assertPolygonStructure(
			@NonNull Class<? extends AlgoTransformation> algorithmType,
			@NonNull GeoPolygon transformed,
			@NonNull GeoPolygon source,
			@NonNull GeoElement parameter,
			@NonNull GeoElement... additionalParameters) {
		assertTrue(transformed.isDefined());
		int vertexCount = source.getPointsLength();
		assertEquals(vertexCount, transformed.getPointsLength());
		GeoSegmentND[] segments = transformed.getSegments();
		assertEquals(vertexCount, segments.length);
		for (int index = 0; index < vertexCount; index++) {
			assertTransformationInputs(
					algorithmType,
					transformed.getPoint(index),
					source.getPoint(index).toGeoElement(),
					parameter,
					additionalParameters);
			assertSame(transformed.getPoint(index), segments[index].getStartPoint());
			assertSame(transformed.getPoint((index + 1) % vertexCount), segments[index].getEndPoint());
		}
	}

	/** Checks polygon outputs together with any additionally created transformation inputs. */
	protected void assertOnlyNewPolygon(
			@NonNull Set<String> existingObjects,
			@NonNull GeoPolygon transformed,
			@NonNull GeoElementND... additionalOutputs) {
		int vertexCount = transformed.getPointsLength();
		int polygonOutputCount = 1 + vertexCount + transformed.getSegments().length;
		GeoElementND[] outputs = new GeoElementND[polygonOutputCount + additionalOutputs.length];
		outputs[0] = transformed;
		for (int index = 0; index < vertexCount; index++) {
			outputs[1 + index] = transformed.getPoint(index);
		}
		System.arraycopy(
				transformed.getSegments(), 0, outputs, 1 + vertexCount, transformed.getSegments().length);
		System.arraycopy(additionalOutputs, 0, outputs, polygonOutputCount, additionalOutputs.length);
		assertOnlyNewObjects(existingObjects, outputs);
	}

	/** Checks incidence using a residual independent of the scale of the line coefficients. */
	protected void assertLineThrough(@NonNull GeoLine line, double x, double y) {
		assertTrue(line.isDefined());
		double scale = Math.max(Math.abs(line.getX()), Math.abs(line.getY()));
		assertTrue(Double.isFinite(scale) && scale > 0, "Line should have a finite nonzero normal");
		double normalX = line.getX() / scale;
		double normalY = line.getY() / scale;
		assertEquals(
				0, (normalX * x + normalY * y + line.getZ() / scale) / Math.hypot(normalX, normalY), 1E-8);
	}

	private void assertRegisteredInput(@NonNull GeoElement input) {
		if (input.isLabelSet()) {
			assertSame(
					input,
					lookup(input.getLabelSimple()),
					"Labeled input should remain registered under its label");
		}
	}
}
