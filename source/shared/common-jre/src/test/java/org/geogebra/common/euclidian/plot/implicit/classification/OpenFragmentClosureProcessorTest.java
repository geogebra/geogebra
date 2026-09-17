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

package org.geogebra.common.euclidian.plot.implicit.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.plot.implicit.ClipEdge;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;
import org.geogebra.common.euclidian.plot.implicit.FragmentEndpoint;
import org.geogebra.common.euclidian.plot.implicit.classification.OpenFragmentClosureResult.FailureReason;
import org.geogebra.common.euclidian.plot.implicit.classification.OpenFragmentClosureResult.Status;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.junit.jupiter.api.Test;

class OpenFragmentClosureProcessorTest {
	private static final EpsilonPolicy EPSILON_POLICY = EpsilonPolicy.defaults();

	@Test
	void simpleOpenFragmentShouldReturnSuccess() {
		Fixture fixture = new Fixture();
		fixture.addContourChain(0);
		fixture.state.setLastFragments(List.of(fixture.fragment(0)));

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.SUCCESS, result.status());
		assertEquals(1, result.openFragments());
		assertEquals(1, result.acceptedClosures());
		assertEquals(0, result.skippedClosures());
	}

	@Test
	void missingEndpointShouldReturnIncomplete() {
		Fixture fixture = new Fixture();
		fixture.state.setLastFragments(List.of(fixture.missingEndpointFragment(0)));

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.INCOMPLETE, result.status());
		assertEquals(1, result.missingEndpoints());
		assertHasFailure(result, FailureReason.MISSING_ENDPOINT);
	}

	@Test
	void missingContourChainShouldReturnIncomplete() {
		Fixture fixture = new Fixture();
		fixture.cacheEndpointVertices();
		fixture.state.setLastFragments(List.of(fixture.fragment(0)));

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.INCOMPLETE, result.status());
		assertEquals(1, result.missingChains());
		assertHasFailure(result, FailureReason.MISSING_CONTOUR_CHAIN);
	}

	@Test
	void endpointOutsideViewportShouldReturnIncomplete() {
		Fixture fixture = new Fixture();
		fixture.state.setLastFragments(List.of(fixture.offViewportEndpointFragment(0)));

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.INCOMPLETE, result.status());
		assertHasFailure(result, FailureReason.MISSING_VIEWPORT_ENDPOINT);
	}

	@Test
	void nonContiguousContourChainShouldReturnIncomplete() {
		Fixture fixture = new Fixture();
		fixture.addNonContiguousContourChain(0);
		fixture.state.setLastFragments(List.of(fixture.fragment(0)));

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.INCOMPLETE, result.status());
		assertHasFailure(result, FailureReason.NON_CONTIGUOUS_CONTOUR_CHAIN);
	}

	@Test
	void invalidInputShouldNotMutateGraphLinks() {
		Fixture fixture = new Fixture();
		fixture.addNonContiguousContourChain(0);
		fixture.state.setLastFragments(List.of(fixture.fragment(0)));
		List<Integer> nextLinks = fixture.nextLinks();
		List<Integer> previousLinks = fixture.previousLinks();

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.INCOMPLETE, result.status());
		assertEquals(nextLinks, fixture.nextLinks());
		assertEquals(previousLinks, fixture.previousLinks());
	}

	@Test
	void conflictingClosureShouldReturnIncomplete() {
		Fixture fixture = new Fixture();
		List<Integer> contourChain = fixture.addContourChain(0);
		fixture.state.recordForwardContourEdgesBy(1, contourChain);
		fixture.state.setLastFragments(List.of(fixture.fragment(0), fixture.fragment(1)));

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.INCOMPLETE, result.status());
		assertEquals(0, result.acceptedClosures());
		assertEquals(1, result.conflicts());
		assertHasFailure(result, FailureReason.CONFLICT);
	}

	@Test
	void conflictingClosureShouldNotMutateGraphLinks() {
		Fixture fixture = new Fixture();
		List<Integer> contourChain = fixture.addContourChain(0);
		fixture.state.recordForwardContourEdgesBy(1, contourChain);
		fixture.state.setLastFragments(List.of(fixture.fragment(0), fixture.fragment(1)));
		List<Integer> nextLinks = fixture.nextLinks();
		List<Integer> previousLinks = fixture.previousLinks();

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.INCOMPLETE, result.status());
		assertEquals(nextLinks, fixture.nextLinks());
		assertEquals(previousLinks, fixture.previousLinks());
	}

	@Test
	void conflictingClosureShouldBeDeterministicForReorderedFragments() {
		Fixture firstFixture = new Fixture();
		List<Integer> firstContourChain = firstFixture.addContourChain(0);
		firstFixture.state.recordForwardContourEdgesBy(1, firstContourChain);
		firstFixture.state.setLastFragments(
				List.of(firstFixture.fragment(0), firstFixture.fragment(1)));

		Fixture secondFixture = new Fixture();
		List<Integer> secondContourChain = secondFixture.addContourChain(0);
		secondFixture.state.recordForwardContourEdgesBy(1, secondContourChain);
		secondFixture.state.setLastFragments(
				List.of(secondFixture.fragment(1), secondFixture.fragment(0)));

		OpenFragmentClosureResult first = firstFixture.process();
		OpenFragmentClosureResult second = secondFixture.process();

		assertEquals(first.status(), second.status());
		assertEquals(first.conflicts(), second.conflicts());
		assertEquals(first.acceptedClosures(), second.acceptedClosures());
	}

	@Test
	void globalSelectionShouldResolveGreedyViewportConflict() {
		SquareFixture fixture = new SquareFixture();
		fixture.addTopLeftToBottomRightContour(0);
		fixture.addTopRightToBottomLeftContour(1);
		fixture.state.setLastFragments(
				List.of(fixture.topLeftToBottomRightFragment(0), fixture.topRightToBottomLeftFragment(1)));

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.SUCCESS, result.status());
		assertEquals(0, result.conflicts());
		assertEquals(2, result.acceptedClosures());
	}

	@Test
	void globalSelectionShouldBeDeterministicForReorderedFragments() {
		SquareFixture firstFixture = new SquareFixture();
		firstFixture.addTopLeftToBottomRightContour(0);
		firstFixture.addTopRightToBottomLeftContour(1);
		firstFixture.state.setLastFragments(List.of(
				firstFixture.topLeftToBottomRightFragment(0),
				firstFixture.topRightToBottomLeftFragment(1)));

		SquareFixture secondFixture = new SquareFixture();
		secondFixture.addTopRightToBottomLeftContour(0);
		secondFixture.addTopLeftToBottomRightContour(1);
		secondFixture.state.setLastFragments(List.of(
				secondFixture.topRightToBottomLeftFragment(0),
				secondFixture.topLeftToBottomRightFragment(1)));

		OpenFragmentClosureResult first = firstFixture.process();
		OpenFragmentClosureResult second = secondFixture.process();

		assertEquals(first.status(), second.status());
		assertEquals(first.conflicts(), second.conflicts());
		assertEquals(first.acceptedClosures(), second.acceptedClosures());
	}

	@Test
	void missingForwardClosureArcShouldReturnIncomplete() {
		Fixture fixture = new Fixture();
		fixture.addContourChain(0);
		fixture.removeViewportEdge(fixture.start, fixture.end);
		fixture.removeViewportEdge(fixture.end, fixture.secondStart);
		fixture.state.setLastFragments(List.of(fixture.fragment(0)));

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.INCOMPLETE, result.status());
		assertHasFailure(result, FailureReason.MISSING_FORWARD_CLOSURE_ARC);
	}

	@Test
	void missingReverseClosureArcShouldReturnIncomplete() {
		Fixture fixture = new Fixture();
		fixture.addContourChain(0);
		fixture.removeViewportEdge(fixture.end, fixture.secondStart);
		fixture.removeViewportEdge(fixture.secondEnd, fixture.start);
		fixture.state.setLastFragments(List.of(fixture.fragment(0)));

		OpenFragmentClosureResult result = fixture.process();

		assertEquals(Status.INCOMPLETE, result.status());
		assertHasFailure(result, FailureReason.MISSING_REVERSE_CLOSURE_ARC);
	}

	@Test
	void failedMergedComplementShouldReturnIncomplete() {
		OpenFragmentClosureResult.Builder builder = new OpenFragmentClosureResult.Builder();
		builder.setOpenFragments(2);
		builder.acceptClosure();
		builder.acceptClosure();
		builder.addFailure(0, FailureReason.FAILED_COMPLEMENT_WIRING);

		OpenFragmentClosureResult result = builder.build();

		assertEquals(Status.INCOMPLETE, result.status());
		assertEquals(2, result.acceptedClosures());
		assertEquals(1, result.failedComplementWirings());
		assertHasFailure(result, FailureReason.FAILED_COMPLEMENT_WIRING);
	}

	private static void assertHasFailure(OpenFragmentClosureResult result, FailureReason reason) {
		assertTrue(
				result.failures().stream().anyMatch(failure -> failure.reason() == reason),
				"Expected failure reason " + reason + " in " + result.failures());
	}

	private static MyPoint point(double x, double y) {
		MyPoint point = new MyPoint(x, y, SegmentType.LINE_TO);
		point.setLineTo(true);
		return point;
	}

	private static class AbstractFixture {
		final PlanarGraph graph = new PlanarGraph();
		final FragmentTopologyRegistry state = new FragmentTopologyRegistry();
		final VertexCache vertexCache = new VertexCache(graph, EPSILON_POLICY.getVertexMerge());
		final OpenFragmentClosureProcessor processor =
				new OpenFragmentClosureProcessor(graph, state, EPSILON_POLICY, vertexCache);
		int start;
		int end;
		int bottomRight;
		int bottomLeft;
		int topLeft;
		int topRight;

		OpenFragmentClosureResult process() {
			return processor.process(viewportInfo());
		}

		ViewportInfo viewportInfo() {
			return ViewportInfo.ofBounds(start, end, bottomLeft, bottomRight, 0, 10, 0, 10);
		}
	}

	private static final class Fixture extends AbstractFixture {
		private final int secondStart;
		private final int secondEnd;

		private Fixture() {
			start = addViewportVertex(5, 10);
			end = addViewportVertex(10, 5);
			secondStart = addViewportVertex(10, 2);
			secondEnd = addViewportVertex(2, 10);
			bottomRight = addViewportVertex(10, 0);
			bottomLeft = addViewportVertex(0, 0);
			topLeft = addViewportVertex(0, 10);
			addViewportEdge(start, end);
			addViewportEdge(end, secondStart);
			addViewportEdge(secondStart, bottomRight);
			addViewportEdge(bottomRight, bottomLeft);
			addViewportEdge(bottomLeft, topLeft);
			addViewportEdge(topLeft, secondEnd);
			addViewportEdge(secondEnd, start);
			processor.replaceOrderedVertexIds(
					List.of(start, end, secondStart, bottomRight, bottomLeft, topLeft, secondEnd));
		}

		private int addViewportVertex(double x, double y) {
			int vertex = graph.addVertex(x, y);
			vertexCache.put(vertex, x, y);
			return vertex;
		}

		private int addViewportEdge(int from, int to) {
			return graph.addViewportEdge(from, to);
		}

		private void removeViewportEdge(int from, int to) {
			graph.removeViewportEdge(from, to);
		}

		private List<Integer> addContourChain(int fragmentId) {
			int middle = addGraphVertex(6, 8);
			int first = graph.addContourEdge(start, middle, 0);
			int second = graph.addContourEdge(middle, end, 0);
			List<Integer> chain = List.of(first, second);
			state.recordForwardContourEdgesBy(fragmentId, chain);
			return chain;
		}

		private void addNonContiguousContourChain(int fragmentId) {
			int firstMiddle = addGraphVertex(6, 8);
			int secondMiddle = addGraphVertex(8, 6);
			int first = graph.addContourEdge(start, firstMiddle, 0);
			int second = graph.addContourEdge(secondMiddle, end, 0);
			state.recordForwardContourEdgesBy(fragmentId, List.of(first, second));
		}

		private int addGraphVertex(double x, double y) {
			int vertex = graph.addVertex(x, y);
			vertexCache.put(vertex, x, y);
			return vertex;
		}

		private void cacheEndpointVertices() {
			vertexCache.put(start, 5, 10);
			vertexCache.put(end, 10, 5);
		}

		private List<Integer> nextLinks() {
			List<Integer> links = new ArrayList<>();
			for (int i = 0; i < graph.getHalfEdges().size(); i++) {
				links.add(graph.halfEdge(i).getNextHalfEdgeId());
			}
			return links;
		}

		private List<Integer> previousLinks() {
			List<Integer> links = new ArrayList<>();
			for (int i = 0; i < graph.getHalfEdges().size(); i++) {
				links.add(graph.halfEdge(i).getPrevHalfEdgeId());
			}
			return links;
		}

		private ClippedFragment fragment(int fragmentId) {
			MyPoint startPoint = point(5, 10);
			MyPoint endPoint = point(10, 5);
			return new ClippedFragment(
					0,
					List.of(startPoint, point(7, 7), endPoint),
					false,
					endpoint(startPoint, ClipEdge.TOP, 0.5, fragmentId),
					endpoint(endPoint, ClipEdge.RIGHT, 1.5, fragmentId));
		}

		private ClippedFragment offViewportEndpointFragment(int fragmentId) {
			MyPoint startPoint = point(4, 9);
			MyPoint endPoint = point(10, 5);
			addGraphVertex(4, 9);
			return new ClippedFragment(
					0,
					List.of(startPoint, point(7, 7), endPoint),
					false,
					endpoint(startPoint, ClipEdge.TOP, 0.4, fragmentId),
					endpoint(endPoint, ClipEdge.RIGHT, 1.5, fragmentId));
		}

		private ClippedFragment missingEndpointFragment(int fragmentId) {
			MyPoint startPoint = point(4, 10);
			MyPoint endPoint = point(9, 5);
			return new ClippedFragment(
					0,
					List.of(startPoint, point(7, 7), endPoint),
					false,
					endpoint(startPoint, ClipEdge.TOP, 0.4, fragmentId),
					endpoint(endPoint, ClipEdge.RIGHT, 1.4, fragmentId));
		}

		private FragmentEndpoint endpoint(
				MyPoint point, ClipEdge edge, double perimeter, int fragmentId) {
			return new FragmentEndpoint(point, edge, perimeter, 0, fragmentId, 0);
		}
	}

	private static final class SquareFixture extends AbstractFixture {

		private SquareFixture() {
			topLeft = addVertex(0, 10);
			topRight = addVertex(10, 10);
			bottomRight = addVertex(10, 0);
			bottomLeft = addVertex(0, 0);
			graph.addViewportEdge(topLeft, topRight);
			graph.addViewportEdge(topRight, bottomRight);
			graph.addViewportEdge(bottomRight, bottomLeft);
			graph.addViewportEdge(bottomLeft, topLeft);
			processor.replaceOrderedVertexIds(List.of(topLeft, topRight, bottomRight, bottomLeft));
		}

		private int addVertex(double x, double y) {
			int vertex = graph.addVertex(x, y);
			vertexCache.put(vertex, x, y);
			return vertex;
		}

		private void addTopLeftToBottomRightContour(int fragmentId) {
			int middle = addVertex(9, 1);
			int first = graph.addContourEdge(topLeft, middle, 0);
			int second = graph.addContourEdge(middle, bottomRight, 0);
			state.recordForwardContourEdgesBy(fragmentId, List.of(first, second));
		}

		private void addTopRightToBottomLeftContour(int fragmentId) {
			int middle = addVertex(1, 1);
			int first = graph.addContourEdge(topRight, middle, 0);
			int second = graph.addContourEdge(middle, bottomLeft, 0);
			state.recordForwardContourEdgesBy(fragmentId, List.of(first, second));
		}

		private ClippedFragment topLeftToBottomRightFragment(int fragmentId) {
			MyPoint startPoint = point(0, 10);
			MyPoint endPoint = point(10, 0);
			return new ClippedFragment(
					0,
					List.of(startPoint, point(9, 1), endPoint),
					false,
					endpoint(startPoint, ClipEdge.TOP, 0.0, fragmentId),
					endpoint(endPoint, ClipEdge.RIGHT, 2.0, fragmentId));
		}

		private ClippedFragment topRightToBottomLeftFragment(int fragmentId) {
			MyPoint startPoint = point(10, 10);
			MyPoint endPoint = point(0, 0);
			return new ClippedFragment(
					0,
					List.of(startPoint, point(1, 1), endPoint),
					false,
					endpoint(startPoint, ClipEdge.TOP, 1.0, fragmentId),
					endpoint(endPoint, ClipEdge.LEFT, 3.0, fragmentId));
		}

		private FragmentEndpoint endpoint(
				MyPoint point, ClipEdge edge, double perimeter, int fragmentId) {
			return new FragmentEndpoint(point, edge, perimeter, 0, fragmentId, 0);
		}
	}
}
