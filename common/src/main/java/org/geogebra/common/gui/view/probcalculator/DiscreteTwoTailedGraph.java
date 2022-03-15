package org.geogebra.common.gui.view.probcalculator;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public class DiscreteTwoTailedGraph {
	private final GeoElement left;
	private final GeoElement right;

	/**
	 *
	 * @param left part of the graph.
	 * @param right part of the graph.
	 */
	public DiscreteTwoTailedGraph(GeoElement left, GeoElement right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * Add the graph to a list
	 * @param list to add the whole graph to
	 */
	public void addTo(List<GeoElementND> list) {
		list.add(left);
		list.add(right);
	}

	/**
	 * Remove the graph from a list
	 * @param list to remove the whole graph from
	 */
	public void removeFrom(List<GeoElementND> list) {
		list.remove(left);
		list.remove(right);
	}

	/**
	 *
	 * @return line thickness of the graph.
	 */
	public int getLineThickness() {
		return left.getLineThickness();
	}

	/**
	 * Update the graph in a cascade way.
	 */
	public void updateCascade() {
		left.updateCascade();
		right.updateCascade();
	}

	/**
	 * Update the graph.
	 */
	public void update() {
		left.update();
		right.update();
	}
}