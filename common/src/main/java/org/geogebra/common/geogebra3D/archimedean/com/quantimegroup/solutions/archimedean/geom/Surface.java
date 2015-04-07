/*
Archimedean 1.1, a 3D applet/application for visualizing, building, 
transforming and analyzing Archimedean solids and their derivatives.
Copyright 1998, 2011 Raffi J. Kasparian, www.raffikasparian.com.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.geom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.IntList;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.ObjectList;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.OrderedTriple;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.Quick3X3Matrix;

public class Surface extends Geometry {
	private List<Facet> rootFacets = new ArrayList<Facet>();
	private Set<Integer> excludedRootFacetIndices = new HashSet<Integer>();

	private List<Facet> facets = new ArrayList<Facet>();
	private List<IntList> boundaries = null;
	private double transformPercent;
	private BspTree bspTree;
	
	// initial value to signal not counted yet
	private int nonTransientPointCount = -1;


	public Surface(ObjectList<OrderedTriple> points) {
		super(points);
	}

	public void square() {
		ObjectList<OrderedTriple> ots = getPoints();
		Facet firstSide = getFacet(0);
		OrderedTriple firstSideNormal = firstSide.getNormal().unit();
		OrderedTriple desiredFirstSideNormal = new OrderedTriple(0, 0, -1);
		OrderedTriple firstEdgeVector = ots.get(1).minus(ots.get(0)).unit();
		OrderedTriple desiredFirstEdgeVector = new OrderedTriple(1, 0, 0);

		Quick3X3Matrix m = Quick3X3Matrix.findRotationMatrix(firstSideNormal, desiredFirstSideNormal, firstEdgeVector, desiredFirstEdgeVector);
		for (OrderedTriple ot : ots) {
			ot.become(m.times(ot));
		}
		for (Facet facet : facets) {
			facet.calcNormal();
		}
	}

	public void addRootFacet(Facet f) {
		rootFacets.add(f);
		boundaries = null;
	}

	public void excludeRootFacet(int index) {
		excludedRootFacetIndices.add(index);
		boundaries = null;
	}

	public void excludeAllRootFacets() {
		for (int i = 0; i < rootFacets.size(); ++i) {
			excludeRootFacet(i);
		}
		boundaries = null;
	}

	public void includeRootFacet(int index) {
		excludedRootFacetIndices.remove(index);
		boundaries = null;
	}

	public void includeAllRootFacets() {
		excludedRootFacetIndices.clear();
		boundaries = null;
	}

	public Facet getRootFacet(int index) {
		return rootFacets.get(index);
	}

	public Facet getFacet(int index) {
		return facets.get(index);
	}

	public List<Facet> getFacets() {
		return facets;
	}

	public int getFacetCount() {
		return facets.size();
	}

	public List<IntList> getBoundaries() {
		if (boundaries == null) {
			calcBoundary();
		}
		return boundaries;
	}

	public void bspBuild() {
		if (nonTransientPointCount < 0) {
			nonTransientPointCount = getPointCount();
		}
		super.getPoints().setSize(nonTransientPointCount);
		List<Facet> includedFacets = new ArrayList<Facet>();
		for (int i = 0; i < rootFacets.size(); ++i) {
			Facet rootFacet = rootFacets.get(i);
			rootFacet.clearChildren();
			if (excludedRootFacetIndices.contains(i)) {
				continue;
			}
			if (rootFacets.get(i).getState().isReal()) {
				includedFacets.add(rootFacet);
			}
		}
		bspTree = BspTree.build(includedFacets);
		// saveBspTree();
		facets.clear();
		facets.addAll(bspTree.getMasterFacetList());
		System.out.println("Points: " + getPointCount());
	}

	public int[] bspSort(OrderedTriple viewer) {
		if (facets.isEmpty()) {
			return new int[0];
		}
		return bspTree.sort(viewer);
	}

	public boolean calcBoundary() {
		boundaries = new ArrayList<IntList>();
		IntList edges1 = new IntList(0, 10);
		IntList edges2 = new IntList(0, 10);
		for (Facet sa : facets) {
			IntList pointIndicesA = sa.getPointIndices();
			A: for (int a = 0; a < pointIndicesA.size(); ++a) {
				int ai1 = pointIndicesA.get(a);
				int ai2 = pointIndicesA.wrapget(a + 1);
				for (Facet sb : facets) {
					if (sb == sa) {
						continue;
					}
					IntList pointIndicesB = sb.getPointIndices();
					for (int b = 0; b < pointIndicesB.size(); ++b) {
						int bi1 = pointIndicesB.get(b);
						int bi2 = pointIndicesB.wrapget(b + 1);
						if (bi2 == ai1 && bi1 == ai2) {
							continue A;
						}
					}
				}
				edges1.add(ai1);
				edges2.add(ai2);
			}
		}
		if (edges1.isEmpty()) {
			return false;
		}
		while (!edges1.isEmpty()) {
			IntList boundary = new IntList(0, 1);
			boundaries.add(boundary);
			int b1 = edges1.get(0);
			int b2 = edges2.get(0);
			boundary.add(b1);
			boundary.add(b2);
			edges1.orderedRemoveIndex(0);
			edges2.orderedRemoveIndex(0);
			while (true) {
				int bi = edges1.find(boundary.get(boundary.size() - 1));
				if (bi == -1) {
					break;
				}
				int newBi = edges2.get(bi);
				if (newBi != boundary.get(0)) {
					boundary.add(newBi);
				}
				edges1.orderedRemoveIndex(bi);
				edges2.orderedRemoveIndex(bi);
				b2 = bi;
			}
		}
		return true;
	}

	public double getTransformPercent() {
		return transformPercent;
	}

	public void transform(double percent) {
		this.transformPercent = percent;
		for (OrderedTriple p : getPoints()) {
			if (p instanceof TransformablePoint) {
				TransformablePoint tp = (TransformablePoint) p;
				tp.transform(percent);
			}
		}
		for (Facet facet : rootFacets) {
			facet.setTransformPercent(percent);
		}
	}

	public int getTotalFacetCount() {
		return facets.size();
	}

	public int getRootFacetCount() {
		return rootFacets.size();
	}

	public int getExcludedRootFacetCount() {
		return excludedRootFacetIndices.size();
	}

	public Iterator<Facet> rootFacetsIterator() {
		return rootFacets.iterator();
	}

	private TreeMap<Double, BspTree> savedBspTrees = new TreeMap<Double, BspTree>();

	private void saveBspTree() {
		savedBspTrees.put(transformPercent, bspTree);
	}

	private BspTree getBestBspTreeNew() {
		if (savedBspTrees.isEmpty()) {
			return bspTree;
		}
		Double d = null;
		Double prevD = null;
		double minDelta = Double.POSITIVE_INFINITY;
		for (Map.Entry<Double, BspTree> entry : savedBspTrees.entrySet()) {
			d = entry.getKey();
			double delta = transformPercent - d;
			minDelta = Math.min(minDelta, delta);
			if (d >= transformPercent) {
				if (delta == minDelta) {
					return entry.getValue();
				} else {
					return savedBspTrees.get(prevD);
				}
			}
			prevD = d;
		}
		return savedBspTrees.get(d);
	}

	private BspTree getBestBspTree() {
		if (savedBspTrees.isEmpty()) {
			return bspTree;
		}
		Double d = null;
		for (Map.Entry<Double, BspTree> entry : savedBspTrees.entrySet()) {
			d = entry.getKey();
			if (d >= transformPercent) {
				return entry.getValue();
			}
		}
		return savedBspTrees.get(d);
	}

	public boolean useBestBspTree() {
		BspTree best = getBestBspTree();
		if (bspTree != best) {
			bspTree = best;
			facets.clear();
			facets.addAll(bspTree.getMasterFacetList());
			return true;
		}
		return false;
	}

	public static Surface createTestSurface1() {
		ObjectList<OrderedTriple> points = new ObjectList<OrderedTriple>(10, 10);
		points.add(new OrderedTriple(-100, -50, 100));
		points.add(new OrderedTriple(-100, -50, -100));
		points.add(new OrderedTriple(100, -50, -100));
		points.add(new OrderedTriple(100, -50, 100));

		points.add(new OrderedTriple(50, -100, -100));
		points.add(new OrderedTriple(50, -100, 100));
		points.add(new OrderedTriple(50, 100, 100));
		points.add(new OrderedTriple(50, 100, -100));

		points.add(new OrderedTriple(100, 100, -50));
		points.add(new OrderedTriple(-100, 100, -50));
		points.add(new OrderedTriple(-100, -100, -50));
		points.add(new OrderedTriple(100, -100, -50));

		Surface surface = new Surface(points);
		surface.addRootFacet(new Facet(surface, new int[] {
				0, 1, 2, 3 }));
		surface.addRootFacet(new Facet(surface, new int[] {
				4, 5, 6, 7 }));
		surface.addRootFacet(new Facet(surface, new int[] {
				8, 9, 10, 11 }));

		for (Facet facet : surface.getFacets()) {
			facet.calcNormal();
		}

		return surface;
	}

	static interface Listener {
		public void surfaceChanged();
	}
}
