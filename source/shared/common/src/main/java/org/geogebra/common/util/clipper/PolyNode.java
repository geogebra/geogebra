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

	public void addChild(PolyNode child) {
		final int cnt = children.size();
		children.add(child);
		child.parent = this;
		child.index = cnt;
	}

	public int getChildCount() {
		return children.size();
	}

	public List<PolyNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * modified to be compatible with double
	 */
	public List<DoublePoint> getContour() {
		return polygon;
	}

	public EndType getEndType() {
		return endType;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public PolyNode getNext() {
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

	public PolyNode getParent() {
		return parent;
	}

	public Path getPolygon() {
		return polygon;
	}

	public boolean isHole() {
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

	public boolean isOpen() {
		return isOpen;
	}

	public void setEndType(EndType value) {
		endType = value;
	}

	public void setJoinType(JoinType value) {
		joinType = value;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public void setParent(PolyNode n) {
		parent = n;

	}

}
