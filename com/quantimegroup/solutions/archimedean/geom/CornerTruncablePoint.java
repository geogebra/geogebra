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

package com.quantimegroup.solutions.archimedean.geom;import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;public class CornerTruncablePoint extends TrajectoryPoint {	public CornerTruncablePoint(OrderedTriple pp, int p) {		super(pp);		start = p;	}	void setGoals(OrderedTriple[] gp, int[] g) {		goals = new int[g.length];		double[] speeds = new double[g.length];		truncPercents = new double[g.length];		OrderedTriple pole = this.negative();		double radius = this.length();		goals[0] = g[0];		speeds[0] = 1 / (gp[0].minus(this).cos(pole));		truncPercents[0] = (distance(gp[0]) / speeds[0]) / radius * 100;		goals[1] = g[1];		speeds[1] = 1 / (gp[1].minus(gp[0]).cos(pole));		truncPercents[1] = (gp[0].distance(gp[1]) / speeds[1]) / radius * 100 + truncPercents[0];		goals[2] = g[2];		speeds[2] = 1 / (gp[2].minus(gp[1]).cos(pole));		truncPercents[2] = 100;	}	public void locate(double truncPercent) {		if (truncPercent < 0) truncPercent = 0;		if (truncPercent >= truncPercents[2]) truncPercent = truncPercents[2];		if (truncPercent <= truncPercents[0]) become(getStartPoint().mid(getGoalPoint(0), truncPercent * 100 / (truncPercents[0] - 0)));		else if (truncPercent <= truncPercents[1]){			truncPercent -= truncPercents[0];			become(getGoalPoint(0).mid(getGoalPoint(1), truncPercent * 100 / (truncPercents[1] - truncPercents[0])));		}else if (truncPercent <= truncPercents[2]){			truncPercent -= truncPercents[1];			become(getGoalPoint(1).mid(getGoalPoint(2), truncPercent * 100 / (truncPercents[2] - truncPercents[1])));		}	}}