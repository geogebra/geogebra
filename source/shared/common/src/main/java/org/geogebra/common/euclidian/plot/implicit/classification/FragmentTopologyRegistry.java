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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;

class FragmentTopologyRegistry {
	private List<ClippedFragment> lastFragments = List.of();
	private final Map<Integer, List<Integer>> forwardContourEdgesByFragmentId = new HashMap<>();

	List<ClippedFragment> lastFragments() {
		return lastFragments;
	}

	void setLastFragments(List<ClippedFragment> fragments) {
		this.lastFragments = fragments;
	}

	boolean hasOpenViewportFragments() {
		return lastFragments.stream().anyMatch(this::isOpenViewportFragment);
	}

	boolean isOpenViewportFragment(ClippedFragment fragment) {
		return !fragment.closed()
				&& fragment.start() != null
				&& fragment.end() != null
				&& fragment.start().getEdge() != null
				&& fragment.end().getEdge() != null;
	}

	List<OpenViewportFragment> openViewportFragments() {
		List<OpenViewportFragment> openFragments = new ArrayList<>();
		for (int fragmentId = 0; fragmentId < lastFragments.size(); fragmentId++) {
			ClippedFragment fragment = lastFragments.get(fragmentId);
			if (isOpenViewportFragment(fragment)) {
				openFragments.add(new OpenViewportFragment(fragmentId, fragment));
			}
		}
		return openFragments;
	}

	void reset() {
		lastFragments = List.of();
		forwardContourEdgesByFragmentId.clear();
	}

	List<Integer> getForwardContourEdgesBy(int fragmentId) {
		return forwardContourEdgesByFragmentId.get(fragmentId);
	}

	void recordForwardContourEdgesBy(int fragmentId, List<Integer> list) {
		forwardContourEdgesByFragmentId.put(fragmentId, list);
	}

	List<Integer> getForwardContourEdgesBy(int fragmentId, List<Integer> defaultValue) {
		return forwardContourEdgesByFragmentId.getOrDefault(fragmentId, defaultValue);
	}
}
