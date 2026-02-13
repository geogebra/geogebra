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

package org.geogebra.common.util.clipper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.util.clipper.Clipper.EndType;
import org.geogebra.common.util.clipper.Clipper.JoinType;
import org.geogebra.common.util.clipper.Point.DoublePoint;

class PolyNode {
	enum NodeType {
		ANY, OPEN, CLOSED
	}

	private PolyNode parent;
	private final Path polygon = new Path();
	private int index;
	private JoinType joinType;
	private EndType endType;
	protected final List<PolyNode> children = new ArrayList<>();
	private boolean isOpen;

	void addChild(PolyNode child) {
		final int cnt = children.size();
		children.add(child);
		child.parent = this;
		child.index = cnt;
	}

	int getChildCount() {
		return children.size();
	}

	List<PolyNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * modified to be compatible with double
	 */
	List<DoublePoint> getContour() {
		return polygon;
	}

	EndType getEndType() {
		return endType;
	}

	JoinType getJoinType() {
		return joinType;
	}

	PolyNode getNext() {
		if (!children.isEmpty()) {
			return children.get(0);
		}
		return getNextSiblingUp();
	}

	private PolyNode getNextSiblingUp() {
		if (parent == null) {
			return null;
		} else if (index == parent.children.size() - 1) {
			return parent.getNextSiblingUp();
		} else {
			return parent.children.get(index + 1);
		}
	}

	PolyNode getParent() {
		return parent;
	}

	Path getPolygon() {
		return polygon;
	}

	boolean isHole() {
		return isHoleNode();
	}

	private boolean isHoleNode() {
		boolean result = true;
		PolyNode node = parent;
		while (node != null) {
			result = !result;
			node = node.parent;
		}
		return result;
	}

	boolean isOpen() {
		return isOpen;
	}

	void setEndType(EndType value) {
		endType = value;
	}

	void setJoinType(JoinType value) {
		joinType = value;
	}

	void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	void setParent(PolyNode n) {
		parent = n;
	}

}
