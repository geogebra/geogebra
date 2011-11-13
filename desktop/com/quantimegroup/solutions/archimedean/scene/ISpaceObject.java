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

package com.quantimegroup.solutions.archimedean.scene;

import java.awt.Color;
import java.awt.Graphics;

import com.quantimegroup.solutions.archimedean.geom.ArchiBuilder;
import com.quantimegroup.solutions.archimedean.utils.Axes;
import com.quantimegroup.solutions.archimedean.utils.IntList;
import com.quantimegroup.solutions.archimedean.utils.ObjectList;
import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;
import com.quantimegroup.solutions.archimedean.utils.Rotater;



public interface ISpaceObject {

	public ObjectList<Rotater> getRotaters();

	public void setRotaters(ObjectList<Rotater> rotaters);

	public ObjectList<SpaceSide> getSides();

	public void setSides(ObjectList<SpaceSide> sides);

	public ObjectList<Line> getLines();

	public ObjectList<OrderedTriple> getPoints();

	public ObjectList<OrderedTriple> getVectors();

	public int getVertexCount();

	public IntList getCornerIndices();

	public void setCornerIndices(IntList corderIndices);

	public void setCornersToShow(boolean[] cornersToShow);

	public ArchiBuilder getBuilder();

	public boolean isDirtyPoints();

	public void setDirtyPoints(boolean dirtyPoints);

	public Color getEdgeColor();

	public void setEdgeColor(Color color);

	public boolean isHighlightCorners();

	public void setHighlightCorners(boolean highlightCorners);

	public OrderedTriple getOriginalPoint(int index);

	public void addLine(OrderedTriple p1, OrderedTriple p2, double length);

	public Axes getPersonalAxes();

	public void setShowBackSides(boolean show);

	public boolean isCorrectAxes();

	public int getType();

	public void setType(int type);

	public double getMaxRadius();

	public double getMinRadius();

	public void rotate(OrderedTriple p0, OrderedTriple n0);

	public void sizeToFit(int width, int height);

	public void toggleCircumscribedSphere();

	public void toggleInscribedSphere();

	public boolean isShowCircumscribedSphere();

	public void setShowCircumscribedSphere(boolean showCircumscribedSphere);

	public boolean isShowInscribedSphere();

	public void setShowInscribedSphere(boolean showInscribedSphere);

	public void update();

	public void draw(Graphics g);

	public SpaceSide getSide(int i);

	public SpacePoint getBoundaryPoint(int whichBoundary, int i);

	public SpacePoint getPoint(int i);

	public void render(Graphics g);

	/**
	 * rotate around origin to bring p0 -> n0 and p1 -> n1
	 * 
	 * @param p0
	 * @param n0
	 * @param p1
	 * @param n1
	 */
	public void rotate(OrderedTriple p0, OrderedTriple n0, OrderedTriple p1, OrderedTriple n1);

	public void toScreenCoord();

	public OrderedTriple getOriginalBoundaryPoint(int whichBoundary, int i);

}
