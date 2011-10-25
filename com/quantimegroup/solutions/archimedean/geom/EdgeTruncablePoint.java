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

package com.quantimegroup.solutions.archimedean.geom;import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;public class EdgeTruncablePoint extends TrajectoryPoint {	EdgeTruncablePoint(OrderedTriple p) {		super(p);		truncPercents = new double[2];		goals = new int[2];	}	EdgeTruncablePoint(OrderedTriple p, int theStart, int sideCenter, int origin, double[] speeds, double totalDist) {		this(p);		start = theStart;		goals[0] = sideCenter;		double poleDistance = getStartPoint().distance(getGoalPoint(0)) / speeds[0];		truncPercents[0] = poleDistance / totalDist * 100;		goals[1] = origin;		poleDistance = this.distance(getGoalPoint(1));		truncPercents[1] = 100;	}	EdgeTruncablePoint(OrderedTriple p, int theStart, int origin) {		this(p);		start = theStart;		goals[0] = origin;		truncPercents[0] = truncPercents[1] = 100;	}	public void locate(double truncPercent) {		if (truncPercent < 0)			truncPercent = 0;		if (truncPercent >= truncPercents[1])			truncPercent = truncPercents[1];		if (truncPercent <= truncPercents[0])			become(getStartPoint().mid(getGoalPoint(0), truncPercent * 100 / (truncPercents[0] - 0)));		else if (truncPercent <= truncPercents[1]) {			truncPercent -= truncPercents[0];			become(getGoalPoint(0).mid(getGoalPoint(1), truncPercent * 100 / (truncPercents[1] - truncPercents[0])));		}	}}