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
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.IntList;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.Misc;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.ObjectList;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.OrderedTriple;

public class Facet implements IFacet {
	private final Surface surface;
	private IntList pointIndices = new IntList();
	private OrderedTriple normal;
	private final static FacetState defaultState = new FacetState(true, true);
	private FacetStateCalculator stateCalculator;
	private FacetShapeCalculator shapeCalculator;
	private double transformPercent;
	private List<Facet> children = null;
	private Facet parent = null;

	void setStateCalculator(FacetStateCalculator stateCalculator) {
		this.stateCalculator = stateCalculator;
	}

	void setShapeCalculator(FacetShapeCalculator shapeCalculator) {
		this.shapeCalculator = shapeCalculator;
	}

	public void init(int[] pointIndices) {
		init(pointIndices, null);
	}

	public void init(int[] pointIndices, OrderedTriple normal) {
		this.pointIndices = new IntList(pointIndices);
		if (normal == null) {
			calcNormal();
		} else {
			this.normal = normal;
		}
	}

	public Facet(Surface surface) {
		this.surface = surface;
	}

	public Facet(Surface surface, int[] pointIndices, OrderedTriple normal) {
		this.surface = surface;
		this.pointIndices = new IntList(pointIndices);
		this.normal = normal;
	}

	public Facet(Surface surface, int[] pointIndices) {
		this.surface = surface;
		this.pointIndices = new IntList(pointIndices);
		calcNormal();
	}

	private void addChild(Facet facet) {
		if (children == null) {
			children = new ArrayList<Facet>();
		}
		children.add(facet);
		facet.parent = this;
	}

	void clearChildren() {
		children = null;
	}

	public OrderedTriple getPoint(int i) {
		return surface.getPoints().get(pointIndices.get(i));
	}

	protected void calcNormal() {
		OrderedTriple p0 = getPoint(0), p1 = getPoint(1), p2 = getPoint(2);
		OrderedTriple v1 = p0.minus(p1);
		OrderedTriple v2 = p2.minus(p1);
		normal = v1.cross(v2);

	}

	public IntList getPointIndices() {
		return pointIndices;
	}

	public int getPointIndex(int i) {
		return pointIndices.get(i);
	}

	public OrderedTriple getNormal() {
		return normal;
	}

	public String toString() {
		return String.valueOf(pointIndices);
	}

	public FacetState getState() {
		if (stateCalculator == null) {
			return defaultState;
		} else {
			return stateCalculator.calcState(transformPercent);
		}
	}

	public FacetShape getShape() {
		if (shapeCalculator == null) {
			return new FacetShape(pointIndices.size(), true);
		} else {
			return shapeCalculator.calcShape(transformPercent);
		}
	}

	public double getTransformPercent() {
		return transformPercent;
	}

	public void setTransformPercent(double truncPercent) {
		if (children != null) {
			for (Facet child : children) {
				child.setTransformPercent(truncPercent);
			}
		}
		this.transformPercent = truncPercent;
	}

	public int getVertexCount() {
		return pointIndices.size();
	}

	/**
	 * Return null if range doesn't exist, otherwise return an array of two
	 * points. If the first point is null, it means that p1 is in range, otherwise
	 * it means that p1 is out of range and the range should begin at the first
	 * point. Likewise, if the second point is null, it means that p2 is in range,
	 * otherwise it means that p2 is out of range and the range should end at the
	 * second point.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	private OrderedTriple[] calcSplitRange(OrderedTriple p1, OrderedTriple p2) {
		OrderedTriple[] range = new OrderedTriple[2];
		OrderedTriple p3 = p1.minus(p2).cross(getNormal());
		double epsilon = 1e-8;
		IntList sectIndices = new IntList(2);
		IntList exactMatches = new IntList(2);
		ObjectList<OrderedTriple> sects = new ObjectList<OrderedTriple>(2);
		for (int i = 0; i < pointIndices.size(); ++i) {
			OrderedTriple e1 = getPoint(i);
			OrderedTriple e2 = wrapgetPoint(i + 1);
			OrderedTriple sect = OrderedTriple.sectPlaneLine(p1, p2, p3, e1, e2);
			if (sect == null) {
				continue;
			}
			if (OrderedTriple.Relationship.BETWEEN.equals(sect.classify(e1, e2, epsilon))) {
				if (sects.size() == 1 && sects.get(0).equals(sect)) {
					continue;
				}
				if (sect.isApprox(e1, 1e-4)) {
					exactMatches.addBoolean(true);// means exact
					sectIndices.add(i);
				} else if (sect.isApprox(e2, 1e-4)) {
					continue;
				} else {
					exactMatches.addBoolean(false);// means in between
					sectIndices.add(i);
				}
				sects.add(sect);
				if (sects.size() == 2) {
					break;
				}
			}
		}
		if (sects.size() == 2) {
			OrderedTriple sect1 = sects.get(0);
			OrderedTriple sect2 = sects.get(1);

			// make sure that sect1 is less than sect2
			if (sect1.compareTo(sect2) > 0) {
				OrderedTriple temp = sect1;
				sect1 = sect2;
				sect2 = temp;
			}
			// make sure that p1 is less than p2
			boolean inputSwitch = p1.compareTo(p2) > 0;
			if (inputSwitch) {
				OrderedTriple temp = p1;
				p1 = p2;
				p2 = temp;
			}

			OrderedTriple.Relationship rel1 = sect1.classify(p1, p2, epsilon);
			OrderedTriple.Relationship rel2 = sect2.classify(p1, p2, epsilon);
			if (OrderedTriple.Relationship.GREATER_THAN.equals(rel1) || OrderedTriple.Relationship.LESS_THAN.equals(rel2)) {
				return null;
			}
			if (OrderedTriple.Relationship.BETWEEN.equals(rel1)) {
				range[0] = sect1;
			}
			if (OrderedTriple.Relationship.BETWEEN.equals(rel2)) {
				range[1] = sect2;
			}
			if (inputSwitch) {
				range[0] = sect2;
				range[1] = sect1;
			}
		} else {
			return null;
		}

		return range;
	}

	Facet[] split(Facet splitter) {
		if (children != null) {
			children.clear();
		}
		double epsilon = 1e-8;
		OrderedTriple[] p = Misc.threeDistinctPoints(splitter);
		OrderedTriple p1 = p[0];
		OrderedTriple p2 = p[1];
		OrderedTriple p3 = p[2];
		IntList sectIndices = new IntList(2);
		IntList exactMatches = new IntList(2);
		ObjectList<OrderedTriple> sects = new ObjectList<OrderedTriple>(2);
		for (int i = 0; i < pointIndices.size(); ++i) {
			OrderedTriple e1 = getPoint(i);
			OrderedTriple e2 = wrapgetPoint(i + 1);
			OrderedTriple sect = OrderedTriple.sectPlaneLine(p1, p2, p3, e1, e2);
			if (sect == null) {
				continue;
			}
			if (OrderedTriple.Relationship.BETWEEN.equals(sect.classify(e1, e2, epsilon))) {
				if (sects.size() == 1 && sects.get(0).equals(sect)) {
					continue;
				}
				if (sect.isApprox(e1, 1e-4)) {
					exactMatches.addBoolean(true);// means exact
					sectIndices.add(i);
				} else if (sect.isApprox(e2, 1e-4)) {
					continue;
				} else {
					exactMatches.addBoolean(false);// means in between
					sectIndices.add(i);
				}
				sects.add(sect);
				if (sects.size() == 2) {
					break;
				}
			}
		}
		if (sectIndices.size() == 2) {
			// OrderedTriple[] splitRange = splitter.calcSplitRange(sects.get(0),
			// sects.get(1));
			if (shapeCalculator == null) {
				shapeCalculator = FacetShapeCalculator.create(new FacetShape(getVertexCount(), true));
			}
			IntList sectPointIndices = new IntList(2);
			if (exactMatches.getBoolean(0)) {
				sectPointIndices.add(pointIndices.get(sectIndices.get(0)));
			} else {
				sectPointIndices.add(surface.getPoints().addReturnIndex(sects.get(0)));
			}
			if (exactMatches.getBoolean(1)) {
				sectPointIndices.add(pointIndices.get(sectIndices.get(1)));
			} else {
				sectPointIndices.add(surface.getPoints().addReturnIndex(sects.get(1)));
			}
			IntList half1Indices = new IntList(10, 10);
			IntList half2Indices = new IntList(10, 10);
			boolean firstHalf = true;
			IntList currentHalf;
			for (int i = 0; i < pointIndices.size(); ++i) {
				currentHalf = firstHalf ? half1Indices : half2Indices;
				currentHalf.add(pointIndices.get(i));
				int foundMatch = sectIndices.find(i);
				if (foundMatch >= 0) {
					if (!exactMatches.getBoolean(foundMatch)) {
						currentHalf.add(sectPointIndices.get(foundMatch));
					}
					firstHalf = !firstHalf;
					currentHalf = firstHalf ? half1Indices : half2Indices;
					if (exactMatches.getBoolean(foundMatch)) {
						currentHalf.add(pointIndices.get(i));
					} else {
						currentHalf.add(sectPointIndices.get(foundMatch));
					}
				}
			}

			if (half1Indices.size() < 3 || half2Indices.size() < 3) {
				throw new NullPointerException();
			}
			Facet halfFacet1 = new Facet(surface);
			halfFacet1.pointIndices = half1Indices;
			halfFacet1.normal = normal;
			halfFacet1.stateCalculator = stateCalculator;
			halfFacet1.shapeCalculator = shapeCalculator;
			halfFacet1.transformPercent = transformPercent;
			addChild(halfFacet1);

			Facet halfFacet2 = new Facet(surface);
			halfFacet2.pointIndices = half2Indices;
			halfFacet2.normal = normal;
			halfFacet2.stateCalculator = stateCalculator;
			halfFacet2.shapeCalculator = shapeCalculator;
			halfFacet2.transformPercent = transformPercent;
			addChild(halfFacet2);

			return new Facet[] {
					halfFacet1, halfFacet2 };

		} else {
			return null;
		}

	}

	public OrderedTriple wrapgetPoint(int i) {
		return surface.getPoints().get(pointIndices.wrapget(i));
	}

	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}

	public boolean hasParent() {
		return parent != null && parent != this;
	}

	public Iterator<Facet> childIterator() {
		return children.iterator();
	}

	public Facet findRootFacet() {
		if (parent == null || parent == this) {
			return this;
		} else {
			return parent.findRootFacet();
		}
	}

	private void getAllChildren(List<Facet> childrenList) {
		if (hasChildren()) {
			for (Facet child : children) {
				child.getAllChildren(childrenList);
			}
		}
		childrenList.add(this);
	}

	public boolean isRoot() {
		return parent == null || parent == this;
	}

	public List<Facet> getAllChildren() {
		List<Facet> childrenList = new ArrayList<Facet>();
		getAllChildren(childrenList);
		return childrenList;
	}
}
