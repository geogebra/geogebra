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
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;

import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.IntList;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.OrderedTriple;

/**
 * @author kasparianr
 * 
 */
class BspTree {
	private Facet plane;
	private List<Facet> facets = new ArrayList<Facet>();
	private BspTree behinders;
	private BspTree infronters;

	private static final boolean DEBUG = false;
	private boolean fake = false;
	private boolean interrupted;

	private IdentityHashMap<Facet, Integer> facetToIndex = null;
	private List<Facet> masterFacetList = new ArrayList<Facet>();

	enum Relationship {
		COINCIDING,
		INFRONT,
		BEHIND,
		SPANNING
	};

	private BspTree(boolean fake) {
		this.fake = fake;
	}

	private static void build(BspTree tree, List<Facet> facets, BspTree root) {
		List<Facet> frontList = new ArrayList<Facet>();
		List<Facet> behindList = new ArrayList<Facet>();

		tree.plane = facets.remove(0);
		tree.facets.add(tree.plane);
		root.masterFacetList.add(tree.plane);
		while (!facets.isEmpty()) {
			if(!root.fake){
				if(/*Thread.interrupted() ||*/ root.interrupted){
					//root.interrupted = true;
					if(tree == root){
						System.out.println("interrupted");
					}
					return;
				}
			}
			Facet sf = facets.remove(0);
			Relationship rel = calculateSide(tree.plane, sf, root.fake);
			if (rel.equals(Relationship.COINCIDING)) {
				tree.facets.add(sf);
				root.masterFacetList.add(sf);
			} else if (rel.equals(Relationship.BEHIND)) {
				behindList.add(sf);
			} else if (rel.equals(Relationship.INFRONT)) {
				frontList.add(sf);
			} else {
				try {
					Facet[] halves = sf.split(tree.plane);
					if (halves == null) {
						throw new Exception("Not expecting spanning.");
					} else {
						facets.add(halves[0]);
						facets.add(halves[1]);
					}
				} catch (Exception e) {
					if (DEBUG) {
						Facet[] halves = sf.split(tree.plane);
						System.out.println("Plane");
						for (int i = 0; i < tree.plane.getVertexCount(); ++i) {
							System.out.println(tree.plane.getPoint(i));
						}
						System.out.println("Facet");
						for (int i = 0; i < sf.getVertexCount(); ++i) {
							System.out.println(sf.getPoint(i));
						}
					}
					rel = forceCalculateSide(tree.plane, sf);
					if (rel.equals(Relationship.COINCIDING)) {
						rel = forceCalculateSide(sf, tree.plane);
						if (rel.equals(Relationship.COINCIDING)) {
							tree.facets.add(sf);
							root.masterFacetList.add(sf);
						} else if (rel.equals(Relationship.BEHIND)) {
							frontList.add(sf);
						} else if (rel.equals(Relationship.INFRONT)) {
							behindList.add(sf);
						}
					} else if (rel.equals(Relationship.BEHIND)) {
						behindList.add(sf);
					} else if (rel.equals(Relationship.INFRONT)) {
						frontList.add(sf);
					}
				}
			}
		}
		if (!frontList.isEmpty()) {
			tree.infronters = new BspTree(root.fake);
			build(tree.infronters, frontList, root);
		}
		if (!behindList.isEmpty()) {
			tree.behinders = new BspTree(root.fake);
			build(tree.behinders, behindList, root);
		}
		sortCoinciders(tree.facets);
	}

	public static BspTree build(List<Facet> facets) {
		return build(facets, false);
	}

	public static BspTree build(List<Facet> facets, boolean fake) {
		BspTree tree = new BspTree(fake);
		if (facets.isEmpty()) {
			return tree;
		}
		tree.facetToIndex = new IdentityHashMap<Facet, Integer>();
		List<Facet> buildSpaceFacets = new ArrayList<Facet>(facets);
		build(tree, buildSpaceFacets, tree);
		tree.redoIndices(facets);
		return tree;
	}

	private static Relationship classifyPoint(Facet facet, OrderedTriple p) {
		double minDistance2 = Double.POSITIVE_INFINITY;
		OrderedTriple bestSpaceFacetPoint = null;
		for (int i = 0; i < facet.getVertexCount(); ++i) {
			OrderedTriple sfp = facet.getPoint(i);
			double distance2 = sfp.distanceSquared(p);
			if (distance2 < minDistance2) {
				bestSpaceFacetPoint = sfp;
				minDistance2 = distance2;
			}
		}
		OrderedTriple v = p.minus(bestSpaceFacetPoint);
		double dot = v.dot(facet.getNormal());

		// smaller epsilons fail for 4,6,10 edge trunc
		if (OrderedTriple.isApprox(dot, 0, 1e-5)) {
			// if (OrderedTriple.isApprox(dot, 0, 1e-3)) {
			dot = 0;
		}
		if (dot > 0) {
			return Relationship.INFRONT;
		} else if (dot < 0) {
			return Relationship.BEHIND;
		} else {
			return Relationship.COINCIDING;
		}
	}

	private static Relationship forceCalculateSide(Facet main, Facet other) {
		int numInfront = 0;
		int numBehind = 0;
		for (int i = 0; i < other.getVertexCount(); ++i) {
			OrderedTriple p = other.getPoint(i);
			Relationship classify = classifyPoint(main, p);
			if (classify == Relationship.INFRONT) {
				numInfront++;
			} else if (classify == Relationship.BEHIND) {
				numBehind++;
			}
		}
		if (numInfront > numBehind) {
			return Relationship.INFRONT;
		} else if (numBehind > numInfront) {
			return Relationship.BEHIND;
		} else {
			return Relationship.COINCIDING;
		}
	}

	private static Relationship calculateSide(Facet main, Facet other, boolean fake) {
		if (fake) {
			return Relationship.COINCIDING;
		}
		int numInfront = 0;
		int numBehind = 0;
		for (int i = 0; i < other.getVertexCount(); ++i) {
			OrderedTriple p = other.getPoint(i);
			Relationship classify = classifyPoint(main, p);
			if (classify == Relationship.INFRONT) {
				numInfront++;
			} else if (classify == Relationship.BEHIND) {
				numBehind++;
			}
		}
		if (numInfront > 0 && numBehind == 0) {
			return Relationship.INFRONT;
		} else if (numInfront == 0 && numBehind > 0) {
			return Relationship.BEHIND;
		} else if (numInfront == 0 && numBehind == 0) {
			return Relationship.COINCIDING;
		} else {
			return Relationship.SPANNING;
		}
	}

	private void sort(List<Facet> sorted, OrderedTriple viewer) {
		Relationship rel = classifyPoint(plane, viewer);
		if (rel.equals(Relationship.BEHIND)) {
			if (infronters != null) {
				infronters.sort(sorted, viewer);
			}
			sorted.addAll(facets);
			if (behinders != null) {
				behinders.sort(sorted, viewer);
			}
		} else if (rel.equals(Relationship.INFRONT)) {
			if (behinders != null) {
				behinders.sort(sorted, viewer);
			}
			sorted.addAll(facets);
			if (infronters != null) {
				infronters.sort(sorted, viewer);
			}
		} else {
			if (infronters != null) {
				infronters.sort(sorted, viewer);
			}
			if (behinders != null) {
				behinders.sort(sorted, viewer);
			}
		}

	}

	private static void sortCoinciders(List<Facet> coinciders) {
		Comparator<Facet> comp = new Comparator<Facet>() {
			public int compare(Facet f1, Facet f2) {
				return f1.getShape().getVertexCount() - f2.getShape().getVertexCount();
			}
		};
		Collections.sort(coinciders, comp);
	}

	public int[] sort(OrderedTriple viewer) {
		List<Facet> sorted = new ArrayList<Facet>();
		sort(sorted, viewer);
		IntList indices = new IntList(sorted.size());
		for (Facet sf : sorted) {
			indices.add(facetToIndex.get(sf));
		}
		return indices.toArray();
	}

	private void redoIndices(List<Facet> buildSpaceFacets) {
		// diagnostic
		if (DEBUG) {
			IdentityHashMap<Facet, Integer> temp = new IdentityHashMap<Facet, Integer>();
			for (Facet f : masterFacetList) {
				temp.put(f, null);
			}
			int count = temp.size();
			boolean containsAll = masterFacetList.containsAll(buildSpaceFacets);
		}
		facetToIndex.clear();
		for (Facet f : masterFacetList) {
			facetToIndex.put(f, facetToIndex.size());
		}

	}

	public List<Facet> getMasterFacetList() {
		return masterFacetList;
	}

}
