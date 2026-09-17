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

import static org.geogebra.common.euclidian.plot.implicit.classification.GraphLogger.TOPOLOGY_DEBUG_LOGGING;
import static org.geogebra.common.euclidian.plot.implicit.classification.GraphLogger.TOPOLOGY_FAILURE_LOGGING;
import static org.geogebra.common.euclidian.plot.implicit.classification.GraphLogger.formatPoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;
import org.geogebra.common.euclidian.plot.implicit.FragmentEndpoint;
import org.geogebra.common.euclidian.plot.implicit.classification.OpenFragmentClosureInput.OpenFragmentChain;
import org.geogebra.common.euclidian.plot.implicit.classification.OpenFragmentClosureInput.ViewportEndpoint;
import org.geogebra.common.euclidian.plot.implicit.classification.OpenFragmentClosureInput.ViewportTopology;
import org.geogebra.common.euclidian.plot.implicit.classification.OpenFragmentClosureResult.Builder;
import org.geogebra.common.euclidian.plot.implicit.classification.OpenFragmentClosureResult.EdgeKind;
import org.geogebra.common.euclidian.plot.implicit.classification.OpenFragmentClosureResult.FailureReason;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.HalfEdge;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Vertex;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.util.debug.Log;

class OpenFragmentClosureProcessor {

	private static final double AREA_EPSILON_SCALE = Kernel.MAX_PRECISION;
	private static final double VIEWPORT_AREA_TOLERANCE_SCALE = 1e-9;
	private static final double DIRECTION_ERROR_TOLERANCE = 1e-6;
	private final PlanarGraph graph;
	private final FragmentTopologyRegistry fragmentRegistry;
	private final EpsilonPolicy epsilonPolicy;
	private final VertexCache vertexCache;
	private final List<Integer> orderedViewportVertexIds = new ArrayList<>();
	private ViewportInfo viewportInfo;

	void replaceOrderedVertexIds(List<Integer> orderedVertexIds) {
		this.orderedViewportVertexIds.clear();
		this.orderedViewportVertexIds.addAll(orderedVertexIds);
	}

	void clear() {
		orderedViewportVertexIds.clear();
	}

	private record OpenFragmentClosure(
			int fragmentId,
			ClippedFragment fragment,
			int startVertex,
			int endVertex,
			List<Integer> forwardChain,
			List<Integer> reverseChain,
			ClosureCandidate forwardCandidate,
			ClosureCandidate reverseCandidate) {}

	private record OpenFragmentClosureCandidate(
			OpenFragmentChain chain,
			ClosureCandidate forwardCandidate,
			ClosureCandidate reverseCandidate,
			ClosureCandidateScore score,
			Set<Integer> claimedHalfEdges) {}

	private record OpenFragmentClosureSelection(
			List<OpenFragmentClosureCandidate> candidates,
			OpenFragmentClosureCandidate conflictCandidate,
			OpenFragmentClosureCandidate conflictOwner,
			int conflictHalfEdge) {
		private boolean isComplete() {
			return conflictCandidate == null;
		}
	}

	private record ClosureCandidate(
			List<Integer> arcEdges,
			double signedArea,
			double absArea,
			boolean viewportLike,
			boolean forwardArc) {
		private boolean isBounded(double epsilon) {
			return absArea > epsilon && !viewportLike;
		}
	}

	private record ClosureCandidateScore(
			boolean bounded, boolean viewportLike, double absArea, boolean forwardArc) {}

	OpenFragmentClosureProcessor(
			PlanarGraph graph,
			FragmentTopologyRegistry fragmentRegistry,
			EpsilonPolicy epsilonPolicy,
			VertexCache vertexCache) {
		this.graph = graph;
		this.fragmentRegistry = fragmentRegistry;
		this.vertexCache = vertexCache;
		this.epsilonPolicy = epsilonPolicy;
	}

	OpenFragmentClosureResult process(ViewportInfo viewportInfo) {
		if (orderedViewportVertexIds.isEmpty() || fragmentRegistry.lastFragments().isEmpty()) {
			return OpenFragmentClosureResult.successEmpty();
		}
		this.viewportInfo = viewportInfo;
		Builder resultBuilder = new Builder();
		OpenFragmentClosureInput input = buildClosureInput(resultBuilder);
		if (input == null) {
			return resultBuilder.build();
		}
		ViewportTopology viewportTopology = input.viewportTopology();
		List<List<OpenFragmentClosureCandidate>> candidateGroups = new ArrayList<>();
		for (OpenFragmentChain chain : input.chains()) {
			List<OpenFragmentClosureCandidate> candidates =
					createOpenFragmentClosureCandidates(chain, viewportTopology, resultBuilder);
			candidateGroups.add(candidates);
		}
		if (resultBuilder.hasFailures()) {
			return resultBuilder.build();
		}
		sortClosureCandidateGroups(candidateGroups);
		OpenFragmentClosureSelection selection = selectClosureCandidates(candidateGroups);
		if (!selection.isComplete()) {
			resultBuilder.addFailure(
					selection.conflictCandidate().chain().fragmentId(),
					FailureReason.CONFLICT,
					selection.conflictHalfEdge(),
					selection.conflictOwner().chain().fragmentId(),
					edgeKind(selection.conflictHalfEdge()));
			logOpenFragmentClosureConflict(
					selection.conflictCandidate(), selection.conflictOwner(), selection.conflictHalfEdge());
			return resultBuilder.build();
		}
		List<OpenFragmentClosure> acceptedClosures = new ArrayList<>();
		for (OpenFragmentClosureCandidate candidate : selection.candidates()) {
			acceptedClosures.add(toOpenFragmentClosure(candidate));
		}
		for (OpenFragmentClosure closure : acceptedClosures) {
			wireClosedWalk(closure.forwardChain(), closure.forwardCandidate().arcEdges());
			resultBuilder.acceptClosure();
			logOpenFragmentClosure("chosen-forward", closure);
		}
		if (acceptedClosures.size() == 1) {
			OpenFragmentClosure closure = acceptedClosures.get(0);
			wireClosedWalk(closure.reverseChain(), closure.reverseCandidate().arcEdges());
		} else if (acceptedClosures.size() > 1) {
			if (!wireMergedComplementWalk(acceptedClosures, viewportTopology)) {
				resultBuilder.addFailure(
						acceptedClosures.get(0).fragmentId(), FailureReason.FAILED_COMPLEMENT_WIRING);
			}
		}
		return resultBuilder.build();
	}

	private OpenFragmentClosureInput buildClosureInput(Builder resultBuilder) {
		ViewportTopology viewportTopology = new ViewportTopology(graph, orderedViewportVertexIds);
		List<OpenViewportFragment> openFragments = fragmentRegistry.openViewportFragments();
		resultBuilder.setOpenFragments(openFragments.size());
		List<OpenFragmentChain> chains = new ArrayList<>();
		for (OpenViewportFragment openFragment : openFragments) {
			OpenFragmentChain chain = createOpenFragmentChain(
					openFragment.fragmentId(), openFragment.fragment(), viewportTopology, resultBuilder);
			if (chain != null) {
				chains.add(chain);
			}
		}
		if (resultBuilder.hasFailures()) {
			return null;
		}
		return new OpenFragmentClosureInput(
				List.copyOf(orderedViewportVertexIds), viewportTopology, chains, openFragments.size());
	}

	private EdgeKind edgeKind(int halfEdgeId) {
		HalfEdge halfEdge = graph.halfEdge(halfEdgeId);
		if (halfEdge.isContourEdge()) {
			return EdgeKind.CONTOUR;
		}
		if (halfEdge.isViewportEdge()) {
			return EdgeKind.VIEWPORT;
		}
		return EdgeKind.OTHER;
	}

	private void addAll(Set<Integer> target, List<Integer> source) {
		target.addAll(source);
	}

	private OpenFragmentChain createOpenFragmentChain(
			int fragmentId,
			ClippedFragment fragment,
			ViewportTopology viewportTopology,
			Builder resultBuilder) {
		int startVertex = endpointVertexId(fragment.start());
		int endVertex = endpointVertexId(fragment.end());
		if (startVertex < 0 || endVertex < 0) {
			logOpenFragmentClosureSkip(
					"missing or identical endpoint vertices", fragment, startVertex, endVertex);
			resultBuilder.addFailure(fragmentId, FailureReason.MISSING_ENDPOINT);
			return null;
		}
		if (startVertex == endVertex) {
			logOpenFragmentClosureSkip(
					"missing or identical endpoint vertices", fragment, startVertex, endVertex);
			resultBuilder.addFailure(fragmentId, FailureReason.IDENTICAL_ENDPOINTS);
			return null;
		}
		int startViewportIndex = viewportTopology.indexOf(startVertex);
		int endViewportIndex = viewportTopology.indexOf(endVertex);
		if (startViewportIndex < 0 || endViewportIndex < 0) {
			logOpenFragmentClosureSkip(
					"endpoint vertex is not on viewport", fragment, startVertex, endVertex);
			logMissingViewportEndpoint(
					fragmentId, fragment, startVertex, endVertex, startViewportIndex, endViewportIndex);
			resultBuilder.addFailure(fragmentId, FailureReason.MISSING_VIEWPORT_ENDPOINT);
			return null;
		}

		List<Integer> forwardChain = contourChainFor(fragmentId);
		if (forwardChain.isEmpty()) {
			forwardChain = fallbackContourChain(fragment, startVertex, endVertex);
		}
		if (forwardChain.isEmpty()) {
			logOpenFragmentClosureSkip("missing contour chain", fragment, startVertex, endVertex);
			resultBuilder.addFailure(fragmentId, FailureReason.MISSING_CONTOUR_CHAIN);
			return null;
		}
		if (!isContiguousContourChain(forwardChain)) {
			logOpenFragmentClosureSkip("non-contiguous contour chain", fragment, startVertex, endVertex);
			resultBuilder.addFailure(fragmentId, FailureReason.NON_CONTIGUOUS_CONTOUR_CHAIN);
			return null;
		}
		if (graph.halfEdge(forwardChain.get(0)).getOriginVertexId() != startVertex
				|| graph.halfEdge(forwardChain.get(forwardChain.size() - 1)).getTargetVertexId()
						!= endVertex) {
			logOpenFragmentClosureSkip(
					"contour chain endpoint mismatch", fragment, startVertex, endVertex);
			resultBuilder.addFailure(fragmentId, FailureReason.CONTOUR_CHAIN_ENDPOINT_MISMATCH);
			return null;
		}
		List<Integer> reverseChain = reverseTwinChain(forwardChain);
		return new OpenFragmentChain(
				fragmentId,
				fragment,
				new ViewportEndpoint(startVertex, startViewportIndex),
				new ViewportEndpoint(endVertex, endViewportIndex),
				forwardChain,
				reverseChain);
	}

	private List<OpenFragmentClosureCandidate> createOpenFragmentClosureCandidates(
			OpenFragmentChain chain, ViewportTopology viewportTopology, Builder resultBuilder) {
		int fragmentId = chain.fragmentId();
		ClippedFragment fragment = chain.fragment();
		int startVertex = chain.start().vertexId();
		int endVertex = chain.end().vertexId();
		List<Integer> forwardChain = chain.forwardChain();
		List<Integer> reverseChain = chain.reverseChain();
		ClosureCandidate forwardA =
				closureCandidate(forwardChain, endVertex, startVertex, true, viewportTopology);
		ClosureCandidate forwardB =
				closureCandidate(forwardChain, endVertex, startVertex, false, viewportTopology);
		List<OpenFragmentClosureCandidate> candidates = new ArrayList<>();
		addOpenFragmentClosureCandidate(chain, forwardA, viewportTopology, candidates);
		addOpenFragmentClosureCandidate(chain, forwardB, viewportTopology, candidates);
		if (candidates.isEmpty()
				&& !hasValidClosureCandidate(forwardA)
				&& !hasValidClosureCandidate(forwardB)) {
			logOpenFragmentClosureSkip(
					"no non-degenerate viewport closure arc", fragment, startVertex, endVertex);
			resultBuilder.addFailure(fragmentId, FailureReason.MISSING_FORWARD_CLOSURE_ARC);
			return candidates;
		}
		if (candidates.isEmpty()) {
			logOpenFragmentClosureSkip(
					"missing reverse viewport closure arc", fragment, startVertex, endVertex);
			resultBuilder.addFailure(fragmentId, FailureReason.MISSING_REVERSE_CLOSURE_ARC);
			return candidates;
		}
		sortClosureCandidates(candidates);
		logOpenFragmentClosureCandidate(
				fragmentId,
				fragment,
				startVertex,
				endVertex,
				forwardA,
				forwardB,
				candidates.get(0).forwardCandidate(),
				candidates.get(0).reverseCandidate(),
				forwardChain.size(),
				reverseChain.size());
		return candidates;
	}

	private void addOpenFragmentClosureCandidate(
			OpenFragmentChain chain,
			ClosureCandidate forwardCandidate,
			ViewportTopology viewportTopology,
			List<OpenFragmentClosureCandidate> candidates) {
		if (!hasValidClosureCandidate(forwardCandidate)) {
			return;
		}
		ClosureCandidate reverseCandidate = closureCandidate(
				chain.reverseChain(),
				chain.start().vertexId(),
				chain.end().vertexId(),
				forwardCandidate.forwardArc(),
				viewportTopology);
		if (!hasValidClosureCandidate(reverseCandidate)) {
			return;
		}
		candidates.add(new OpenFragmentClosureCandidate(
				chain,
				forwardCandidate,
				reverseCandidate,
				score(forwardCandidate),
				claimedForwardHalfEdges(chain, forwardCandidate)));
	}

	private boolean hasValidClosureCandidate(ClosureCandidate candidate) {
		return candidate != null
				&& !candidate.arcEdges().isEmpty()
				&& candidate.absArea() > closureAreaEpsilon();
	}

	private ClosureCandidateScore score(ClosureCandidate candidate) {
		return new ClosureCandidateScore(
				candidate.isBounded(closureAreaEpsilon()),
				candidate.viewportLike(),
				candidate.absArea(),
				candidate.forwardArc());
	}

	private Set<Integer> claimedForwardHalfEdges(
			OpenFragmentChain chain, ClosureCandidate forwardCandidate) {
		Set<Integer> claimed = new HashSet<>();
		addAll(claimed, chain.forwardChain());
		addAll(claimed, forwardCandidate.arcEdges());
		return claimed;
	}

	private void sortClosureCandidates(List<OpenFragmentClosureCandidate> candidates) {
		candidates.sort(this::compareClosureCandidates);
	}

	private void sortClosureCandidateGroups(
			List<List<OpenFragmentClosureCandidate>> candidateGroups) {
		candidateGroups.sort(Comparator.comparingInt(openFragmentClosureCandidates ->
				openFragmentClosureCandidates.get(0).chain().fragmentId()));
	}

	private int compareClosureCandidates(
			OpenFragmentClosureCandidate first, OpenFragmentClosureCandidate second) {
		int scoreComparison = compareClosureCandidateScores(first.score(), second.score());
		if (scoreComparison != 0) {
			return scoreComparison;
		}
		return Boolean.compare(
				!first.forwardCandidate().forwardArc(), !second.forwardCandidate().forwardArc());
	}

	private int compareClosureCandidateScores(
			ClosureCandidateScore first, ClosureCandidateScore second) {
		int boundedComparison = Boolean.compare(second.bounded(), first.bounded());
		if (boundedComparison != 0) {
			return boundedComparison;
		}
		int viewportComparison = Boolean.compare(first.viewportLike(), second.viewportLike());
		if (viewportComparison != 0) {
			return viewportComparison;
		}
		int areaComparison = Double.compare(first.absArea(), second.absArea());
		if (areaComparison != 0) {
			return areaComparison;
		}
		return Boolean.compare(!first.forwardArc(), !second.forwardArc());
	}

	private OpenFragmentClosureSelection selectClosureCandidates(
			List<List<OpenFragmentClosureCandidate>> candidateGroups) {
		List<OpenFragmentClosureCandidate> selected = new ArrayList<>();
		Map<Integer, OpenFragmentClosureCandidate> claimed = new HashMap<>();
		ConflictTracker conflictTracker = new ConflictTracker();
		if (selectClosureCandidates(candidateGroups, 0, selected, claimed, conflictTracker)) {
			return new OpenFragmentClosureSelection(List.copyOf(selected), null, null, -1);
		}
		return new OpenFragmentClosureSelection(
				List.of(), conflictTracker.candidate, conflictTracker.owner, conflictTracker.halfEdgeId);
	}

	private boolean selectClosureCandidates(
			List<List<OpenFragmentClosureCandidate>> candidateGroups,
			int groupIndex,
			List<OpenFragmentClosureCandidate> selected,
			Map<Integer, OpenFragmentClosureCandidate> claimed,
			ConflictTracker conflictTracker) {
		if (groupIndex == candidateGroups.size()) {
			return true;
		}
		for (OpenFragmentClosureCandidate candidate : candidateGroups.get(groupIndex)) {
			int conflictHalfEdge = firstConflictHalfEdge(candidate, claimed);
			if (conflictHalfEdge >= 0) {
				conflictTracker.record(candidate, claimed.get(conflictHalfEdge), conflictHalfEdge);
				continue;
			}
			claim(candidate, claimed);
			selected.add(candidate);
			if (selectClosureCandidates(
					candidateGroups, groupIndex + 1, selected, claimed, conflictTracker)) {
				return true;
			}
			selected.remove(selected.size() - 1);
			unclaim(candidate, claimed);
		}
		return false;
	}

	private int firstConflictHalfEdge(
			OpenFragmentClosureCandidate candidate, Map<Integer, OpenFragmentClosureCandidate> claimed) {
		for (int halfEdgeId : candidate.claimedHalfEdges()) {
			if (claimed.containsKey(halfEdgeId)) {
				return halfEdgeId;
			}
		}
		return -1;
	}

	private void claim(
			OpenFragmentClosureCandidate candidate, Map<Integer, OpenFragmentClosureCandidate> claimed) {
		for (int halfEdgeId : candidate.claimedHalfEdges()) {
			claimed.put(halfEdgeId, candidate);
		}
	}

	private void unclaim(
			OpenFragmentClosureCandidate candidate, Map<Integer, OpenFragmentClosureCandidate> claimed) {
		for (int halfEdgeId : candidate.claimedHalfEdges()) {
			claimed.remove(halfEdgeId);
		}
	}

	private OpenFragmentClosure toOpenFragmentClosure(OpenFragmentClosureCandidate candidate) {
		OpenFragmentChain chain = candidate.chain();
		return new OpenFragmentClosure(
				chain.fragmentId(),
				chain.fragment(),
				chain.start().vertexId(),
				chain.end().vertexId(),
				chain.forwardChain(),
				chain.reverseChain(),
				candidate.forwardCandidate(),
				candidate.reverseCandidate());
	}

	private static final class ConflictTracker {
		private OpenFragmentClosureCandidate candidate;
		private OpenFragmentClosureCandidate owner;
		private int halfEdgeId = -1;

		private void record(
				OpenFragmentClosureCandidate candidate,
				OpenFragmentClosureCandidate owner,
				int halfEdgeId) {
			if (this.candidate == null) {
				this.candidate = candidate;
				this.owner = owner;
				this.halfEdgeId = halfEdgeId;
			}
		}
	}

	private List<Integer> contourChainFor(int fragmentId) {
		return fragmentRegistry.getForwardContourEdgesBy(fragmentId, List.of());
	}

	private List<Integer> fallbackContourChain(
			ClippedFragment fragment, int startVertex, int endVertex) {
		int forwardStart = outgoingContourHalfEdgeAlongFragment(
				startVertex, fragment.sourceContourId(), fragment.points(), true);
		int reverseStart = outgoingContourHalfEdgeAlongFragment(
				endVertex, fragment.sourceContourId(), fragment.points(), false);
		if (forwardStart < 0 || reverseStart < 0) {
			return List.of();
		}
		int forwardEnd = graph.halfEdge(reverseStart).getTwinHalfEdgeId();
		if (forwardStart != forwardEnd) {
			return List.of();
		}
		return List.of(forwardStart);
	}

	private boolean isContiguousContourChain(List<Integer> chain) {
		if (chain.isEmpty()) {
			return false;
		}
		for (int i = 0; i < chain.size() - 1; i++) {
			HalfEdge current = graph.halfEdge(chain.get(i));
			HalfEdge next = graph.halfEdge(chain.get(i + 1));
			if (current.getTargetVertexId() != next.getOriginVertexId()) {
				return false;
			}
		}
		return true;
	}

	private List<Integer> reverseTwinChain(List<Integer> forwardChain) {
		List<Integer> reverse = new ArrayList<>(forwardChain.size());
		for (int i = forwardChain.size() - 1; i >= 0; i--) {
			reverse.add(graph.halfEdge(forwardChain.get(i)).getTwinHalfEdgeId());
		}
		return reverse;
	}

	private ClosureCandidate closureCandidate(
			List<Integer> contourChain,
			int fromVertex,
			int toVertex,
			boolean forward,
			ViewportTopology viewportTopology) {
		List<Integer> arc = viewportTopology.arc(fromVertex, toVertex, forward);
		if (arc.isEmpty()) {
			return null;
		}
		double signedArea = closedArea(contourChain, arc);
		double absArea = Math.abs(signedArea);
		return new ClosureCandidate(arc, signedArea, absArea, isViewportLikeArea(absArea), forward);
	}

	private double closureAreaEpsilon() {
		return Math.max(AREA_EPSILON_SCALE, Math.abs(viewportInfo.absArea()) * AREA_EPSILON_SCALE);
	}

	private boolean isViewportLikeArea(double absArea) {
		double viewportAbsArea = viewportInfo.absArea();
		if (!Double.isFinite(viewportAbsArea)) {
			return false;
		}
		double tolerance =
				Math.max(VIEWPORT_AREA_TOLERANCE_SCALE, viewportAbsArea * VIEWPORT_AREA_TOLERANCE_SCALE);
		return Math.abs(absArea - viewportAbsArea) <= tolerance;
	}

	private void logOpenFragmentClosureSkip(
			String reason, ClippedFragment fragment, int startVertex, int endVertex) {
		if (!TOPOLOGY_DEBUG_LOGGING) {
			return;
		}
		Log.debug("[GraphBuilder] open fragment closure skipped reason=" + reason
				+ " contour=" + fragment.sourceContourId()
				+ " startVertex=" + startVertex
				+ " endVertex=" + endVertex
				+ " start=" + fragment.start()
				+ " end=" + fragment.end());
	}

	private void logOpenFragmentClosure(String label, OpenFragmentClosure closure) {
		if (!TOPOLOGY_DEBUG_LOGGING) {
			return;
		}
		Log.debug("[GraphBuilder] open fragment closure " + label
				+ " fragment=" + closure.fragmentId()
				+ " startVertex=" + closure.startVertex()
				+ " endVertex=" + closure.endVertex()
				+ " startS=" + normalizedEndpointPerimeter(closure.fragment().start())
				+ " endS=" + normalizedEndpointPerimeter(closure.fragment().end())
				+ " contourEdges=" + closure.forwardChain().size()
				+ " forwardArcEdges=" + closure.forwardCandidate().arcEdges().size()
				+ " forwardArea=" + closure.forwardCandidate().signedArea()
				+ " forwardArc=" + describeArc(closure.forwardCandidate())
				+ " reverseArcEdges=" + closure.reverseCandidate().arcEdges().size()
				+ " reverseArea=" + closure.reverseCandidate().signedArea()
				+ " reverseArc=" + describeArc(closure.reverseCandidate()));
	}

	private void logOpenFragmentClosureCandidate(
			int fragmentId,
			ClippedFragment fragment,
			int startVertex,
			int endVertex,
			ClosureCandidate forwardA,
			ClosureCandidate forwardB,
			ClosureCandidate chosenForward,
			ClosureCandidate reverse,
			int forwardChainSize,
			int reverseChainSize) {
		if (!TOPOLOGY_DEBUG_LOGGING) {
			return;
		}
		Log.debug("[GraphBuilder] open fragment closure candidate"
				+ " fragment=" + fragmentId
				+ " contour=" + fragment.sourceContourId()
				+ " startVertex=" + startVertex
				+ " endVertex=" + endVertex
				+ " startEdge=" + fragment.start().getEdge()
				+ " endEdge=" + fragment.end().getEdge()
				+ " startS=" + normalizedEndpointPerimeter(fragment.start())
				+ " endS=" + normalizedEndpointPerimeter(fragment.end())
				+ " forwardChainEdges=" + forwardChainSize
				+ " reverseChainEdges=" + reverseChainSize
				+ " candidateA=" + describeArc(forwardA)
				+ " candidateB=" + describeArc(forwardB)
				+ " chosen=" + describeArc(chosenForward)
				+ " reverse=" + describeArc(reverse));
	}

	private void logOpenFragmentClosureConflict(
			OpenFragmentClosureCandidate candidate, OpenFragmentClosureCandidate owner, int halfEdgeId) {
		if (!TOPOLOGY_DEBUG_LOGGING) {
			return;
		}
		HalfEdge halfEdge = graph.halfEdge(halfEdgeId);
		Log.debug("[GraphBuilder] open fragment closure conflict"
				+ " fragment=" + candidate.chain().fragmentId()
				+ " ownerFragment=" + owner.chain().fragmentId()
				+ " halfEdge=" + halfEdgeId
				+ " edgeKind=" + (halfEdge.isViewportEdge() ? "viewport" : "contour")
				+ " edge=" + edgeDescription(halfEdgeId)
				+ " candidateArc=" + describeArc(candidate.forwardCandidate())
				+ " ownerArc=" + describeArc(owner.forwardCandidate()));
	}

	private void logMissingViewportEndpoint(
			int fragmentId,
			ClippedFragment fragment,
			int startVertex,
			int endVertex,
			int startViewportIndex,
			int endViewportIndex) {
		if (!TOPOLOGY_FAILURE_LOGGING) {
			return;
		}
		Log.debug("[GraphBuilder] missing viewport endpoint"
				+ " fragment=" + fragmentId
				+ " contour=" + fragment.sourceContourId()
				+ " startVertex=" + vertexDescription(startVertex)
				+ " endVertex=" + vertexDescription(endVertex)
				+ " startViewportIndex=" + startViewportIndex
				+ " endViewportIndex=" + endViewportIndex
				+ " startEndpoint=" + endpointDescription(fragment.start())
				+ " endEndpoint=" + endpointDescription(fragment.end())
				+ " nearestStartViewport=" + nearestViewportVertex(fragment.start())
				+ " nearestEndViewport=" + nearestViewportVertex(fragment.end())
				+ " viewportVertices=" + summarizeViewportVertices());
	}

	private String endpointDescription(FragmentEndpoint endpoint) {
		if (endpoint == null || endpoint.getPoint() == null) {
			return "null";
		}
		return "{edge=" + endpoint.getEdge()
				+ ",s=" + normalizedEndpointPerimeter(endpoint)
				+ ",point=" + formatPoint(endpoint.getPoint().x, endpoint.getPoint().y)
				+ "}";
	}

	private String nearestViewportVertex(FragmentEndpoint endpoint) {
		if (endpoint == null || endpoint.getPoint() == null || orderedViewportVertexIds.isEmpty()) {
			return "n/a";
		}
		double bestDistanceSquared = Double.POSITIVE_INFINITY;
		int bestIndex = -1;
		int bestVertexId = -1;
		for (int i = 0; i < orderedViewportVertexIds.size(); i++) {
			int vertexId = orderedViewportVertexIds.get(i);
			Vertex vertex = graph.vertex(vertexId);
			double dx = vertex.getX() - endpoint.getPoint().x;
			double dy = vertex.getY() - endpoint.getPoint().y;
			double distanceSquared = dx * dx + dy * dy;
			if (distanceSquared < bestDistanceSquared) {
				bestDistanceSquared = distanceSquared;
				bestIndex = i;
				bestVertexId = vertexId;
			}
		}
		return vertexDescription(bestVertexId) + "@index=" + bestIndex + ":distanceSquared="
				+ bestDistanceSquared;
	}

	private String summarizeViewportVertices() {
		if (orderedViewportVertexIds.size() <= 10) {
			return viewportVertexRange(0, orderedViewportVertexIds.size());
		}
		return viewportVertexRange(0, 5) + "...+"
				+ (orderedViewportVertexIds.size() - 10)
				+ "..."
				+ viewportVertexRange(orderedViewportVertexIds.size() - 5, orderedViewportVertexIds.size());
	}

	private String viewportVertexRange(int from, int to) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = from; i < to; i++) {
			if (i > from) {
				sb.append(',');
			}
			sb.append(i).append(':').append(vertexDescription(orderedViewportVertexIds.get(i)));
		}
		return sb.append(']').toString();
	}

	private String vertexDescription(int vertexId) {
		if (vertexId < 0 || vertexId >= graph.getVertices().size()) {
			return String.valueOf(vertexId);
		}
		Vertex vertex = graph.vertex(vertexId);
		return vertexId + "@" + formatPoint(vertex.getX(), vertex.getY());
	}

	private String describeArc(ClosureCandidate candidate) {
		if (candidate == null) {
			return "null";
		}
		return "{direction=" + (candidate.forwardArc() ? "forward" : "reverse")
				+ ", edges=" + candidate.arcEdges().size()
				+ ", signedArea=" + candidate.signedArea()
				+ ", absArea=" + candidate.absArea()
				+ ", viewportLike=" + candidate.viewportLike()
				+ ", edgeIds=" + summarizeEdgeIds(candidate.arcEdges()) + "}";
	}

	private String summarizeEdgeIds(List<Integer> edgeIds) {
		if (edgeIds.size() <= 8) {
			return edgeIds.toString();
		}
		return edgeIds.subList(0, 4) + "...+" + (edgeIds.size() - 8) + "..."
				+ edgeIds.subList(edgeIds.size() - 4, edgeIds.size());
	}

	private double normalizedEndpointPerimeter(FragmentEndpoint endpoint) {
		return normalizePerimeter(endpoint.getSPerimeter());
	}

	private double normalizePerimeter(double perimeter) {
		double normalized = perimeter % 4.0;
		if (normalized < 0) {
			normalized += 4.0;
		}
		if (Math.abs(4.0 - normalized) <= epsilonPolicy.getSnap()) {
			return 0.0;
		}
		return normalized;
	}

	private String edgeDescription(int halfEdgeId) {
		HalfEdge halfEdge = graph.halfEdge(halfEdgeId);
		Vertex origin = graph.vertex(halfEdge.getOriginVertexId());
		Vertex target = graph.vertex(halfEdge.getTargetVertexId());
		return halfEdge.getOriginVertexId() + "->" + halfEdge.getTargetVertexId()
				+ "@" + formatPoint(origin.getX(), origin.getY())
				+ "->" + formatPoint(target.getX(), target.getY());
	}

	private void overrideClosedCycleLinks(List<Integer> cycleEdges) {
		for (int i = 0; i < cycleEdges.size(); i++) {
			overrideNext(cycleEdges.get(i), cycleEdges.get((i + 1) % cycleEdges.size()));
		}
	}

	private void overrideCycleLinks(int entryEdge, List<Integer> arcEdges, int exitEdge) {
		overrideNext(entryEdge, arcEdges.get(0));
		for (int i = 0; i < arcEdges.size() - 1; i++) {
			overrideNext(arcEdges.get(i), arcEdges.get(i + 1));
		}
		overrideNext(arcEdges.get(arcEdges.size() - 1), exitEdge);
	}

	private void wireClosedWalk(List<Integer> contourChain, List<Integer> arcEdges) {
		for (int i = 0; i < contourChain.size() - 1; i++) {
			overrideNext(contourChain.get(i), contourChain.get(i + 1));
		}
		overrideCycleLinks(contourChain.get(contourChain.size() - 1), arcEdges, contourChain.get(0));
	}

	private boolean wireMergedComplementWalk(
			List<OpenFragmentClosure> closures, ViewportTopology viewportTopology) {
		Map<Integer, OpenFragmentClosure> byEndVertex = new HashMap<>();
		for (OpenFragmentClosure closure : closures) {
			byEndVertex.put(closure.endVertex(), closure);
		}
		OpenFragmentClosure first = closures.get(0);
		boolean forward = first.reverseCandidate().forwardArc();
		List<Integer> cycle = new ArrayList<>();
		appendAll(cycle, first.reverseChain());
		byEndVertex.remove(first.endVertex());
		int currentVertex = first.startVertex();
		int guard = graph.getHalfEdges().size() + orderedViewportVertexIds.size() + closures.size();
		while ((currentVertex != first.endVertex() || !byEndVertex.isEmpty()) && guard-- > 0) {
			int viewportEdge = viewportTopology.step(currentVertex, forward);
			if (viewportEdge < 0) {
				return false;
			}
			cycle.add(viewportEdge);
			currentVertex = graph.halfEdge(viewportEdge).getTargetVertexId();
			OpenFragmentClosure nextClosure = byEndVertex.remove(currentVertex);
			if (nextClosure != null) {
				appendAll(cycle, nextClosure.reverseChain());
				currentVertex = nextClosure.startVertex();
			}
		}
		if (currentVertex == first.endVertex() && byEndVertex.isEmpty()) {
			overrideClosedCycleLinks(cycle);
			return true;
		}
		return false;
	}

	private void appendAll(List<Integer> target, List<Integer> source) {
		for (int halfEdgeId : source) {
			target.add(halfEdgeId);
		}
	}

	private int endpointVertexId(FragmentEndpoint endpoint) {
		if (endpoint == null || endpoint.getPoint() == null) {
			return -1;
		}
		return vertexCache.find(endpoint.getPoint().x, endpoint.getPoint().y);
	}

	private int outgoingContourHalfEdgeAlongFragment(
			int vertexId, int contourId, List<MyPoint> points, boolean atStart) {
		ExpectedDirection expected = expectedEndpointDirection(points, atStart);
		if (expected == null) {
			return -1;
		}
		int best = -1;
		double bestError = Double.POSITIVE_INFINITY;
		double bestLength = Double.POSITIVE_INFINITY;
		Vertex origin = graph.vertex(vertexId);
		for (int halfEdgeId : graph.vertex(vertexId).getOutgoingHalfEdges()) {
			HalfEdge halfEdge = graph.halfEdge(halfEdgeId);
			if (!isMatchingContourHalfEdge(halfEdge, contourId)) {
				continue;
			}
			Vertex target = graph.vertex(halfEdge.getTargetVertexId());
			double dx = target.getX() - origin.getX();
			double dy = target.getY() - origin.getY();
			double length = Math.hypot(dx, dy);
			if (length <= epsilonPolicy.getIntersection()) {
				continue;
			}
			double dot = expected.dx() * dx + expected.dy() * dy;
			if (dot <= 0) {
				continue;
			}
			double error =
					Math.abs(expected.dx() * dy - expected.dy() * dx) / (expected.length() * length);
			if (error > DIRECTION_ERROR_TOLERANCE) {
				continue;
			}
			if (error < bestError
					|| (Math.abs(error - bestError) <= AREA_EPSILON_SCALE && length < bestLength)) {
				best = halfEdgeId;
				bestError = error;
				bestLength = length;
			}
		}
		return best >= 0 ? best : uniqueOutgoingContourHalfEdge(vertexId, contourId);
	}

	private ExpectedDirection expectedEndpointDirection(List<MyPoint> points, boolean atStart) {
		if (points.size() < 2) {
			return null;
		}
		int originIndex = atStart ? 0 : points.size() - 1;
		MyPoint origin = points.get(originIndex);
		int step = atStart ? 1 : -1;
		for (int index = originIndex + step; index >= 0 && index < points.size(); index += step) {
			MyPoint adjacent = points.get(index);
			double dx = adjacent.x - origin.x;
			double dy = adjacent.y - origin.y;
			double length = Math.hypot(dx, dy);
			if (length > epsilonPolicy.getIntersection()) {
				return new ExpectedDirection(dx, dy, length);
			}
		}
		return null;
	}

	private boolean isMatchingContourHalfEdge(HalfEdge halfEdge, int contourId) {
		return halfEdge.isActive()
				&& halfEdge.isContourEdge()
				&& halfEdge.getSourceContourId() == contourId;
	}

	private int uniqueOutgoingContourHalfEdge(int vertexId, int contourId) {
		int match = -1;
		for (int halfEdgeId : graph.vertex(vertexId).getOutgoingHalfEdges()) {
			HalfEdge halfEdge = graph.halfEdge(halfEdgeId);
			if (!isMatchingContourHalfEdge(halfEdge, contourId)) {
				continue;
			}
			if (match != -1) {
				return -1;
			}
			match = halfEdgeId;
		}
		return match;
	}

	private record ExpectedDirection(double dx, double dy, double length) {}

	private double closedArea(List<Integer> contourChain, List<Integer> arcHalfEdges) {
		List<GPoint2D> polygon = new ArrayList<>();
		for (int halfEdgeId : contourChain) {
			Vertex origin = graph.vertex(graph.halfEdge(halfEdgeId).getOriginVertexId());
			polygon.add(new GPoint2D(origin.getX(), origin.getY()));
		}
		Vertex contourEnd =
				graph.vertex(graph.halfEdge(contourChain.get(contourChain.size() - 1)).getTargetVertexId());
		polygon.add(new GPoint2D(contourEnd.getX(), contourEnd.getY()));
		for (int halfEdgeId : arcHalfEdges) {
			Vertex target = graph.vertex(graph.halfEdge(halfEdgeId).getTargetVertexId());
			polygon.add(new GPoint2D(target.getX(), target.getY()));
		}
		double area = 0;
		for (int i = 0; i < polygon.size(); i++) {
			GPoint2D current = polygon.get(i);
			GPoint2D next = polygon.get((i + 1) % polygon.size());
			area += current.x * next.y - current.y * next.x;
		}
		return 0.5 * area;
	}

	private void overrideNext(int halfEdgeId, int nextHalfEdgeId) {
		graph.halfEdge(halfEdgeId).setNextHalfEdgeId(nextHalfEdgeId);
		graph.halfEdge(nextHalfEdgeId).setPrevHalfEdgeId(halfEdgeId);
	}
}
