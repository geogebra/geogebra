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

package com.quantimegroup.solutions.archimedean.geom;import com.quantimegroup.solutions.archimedean.utils.Misc;
import com.quantimegroup.solutions.archimedean.utils.ObjectList;
import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;
class TransformablePoint extends OrderedTriple {	protected ObjectList<OrderedTriple> points;	private int[] goalIndices;	private double[] transformPercents;	private State[] states;	TransformablePoint(ObjectList<OrderedTriple> points, int firstGoalIndex, State state) {		super(points.get(firstGoalIndex));		this.points = points;		addStage(firstGoalIndex, 0, state);	}	TransformablePoint(ObjectList<OrderedTriple> points, int firstGoalIndex) {		super(points.get(firstGoalIndex));		this.points = points;		addStage(firstGoalIndex, 0, new State());	}	OrderedTriple getGoalPoint(int i) {		return points.get(goalIndices[i]);	}	public boolean equals(OrderedTriple T) {		Class tClass = T.getClass();		if (getClass() != tClass)			return false;		TransformablePoint t = (TransformablePoint) T;		if (super.equals(t) && points == t.points) {			if (goalIndices.length == t.goalIndices.length && transformPercents.length == t.transformPercents.length) {				for (int i = 0; i < goalIndices.length; ++i)					if (goalIndices[i] != t.goalIndices[i])						return false;				for (int i = 0; i < transformPercents.length; ++i)					if (transformPercents[i] != t.transformPercents[i])						return false;				return true;			}		}		return false;	}	public final State getState(double truncPercent) {		State state = null;		if (truncPercent < 0) {			truncPercent = 0;		} else if (truncPercent >= transformPercents[transformPercents.length - 1]) {			truncPercent = transformPercents[transformPercents.length - 1];		}		if (truncPercent < transformPercents[0]) {			state = states[0];		} else {			for (int i = transformPercents.length - 1; i >= 0; --i) {				if (truncPercent >= transformPercents[i]) {					state = states[i];					break;				}			}		}		return state;	}	public final void transform(double truncPercent) {		truncPercent = Misc.clamp(truncPercent, transformPercents[0], transformPercents[transformPercents.length - 1]);		if (truncPercent == transformPercents[0]) {			become(getGoalPoint(0));			return;		}		for (int i = 1; i < transformPercents.length; ++i) {			if (truncPercent <= transformPercents[i]) {				truncPercent -= transformPercents[i - 1];				become(getGoalPoint(i - 1).mid(getGoalPoint(i), truncPercent * 100 / (transformPercents[i] - transformPercents[i - 1])));				return;			}		}	}	public void addStage(int goalIndex, double truncPercent) {		addStage(goalIndex, truncPercent, new State());	}	public void addStage(int goalIndex, double truncPercent, State state) {		if (goalIndices == null) {			goalIndices = new int[] { goalIndex };			transformPercents = new double[] { truncPercent };		} else {			int[] tempGoals = new int[goalIndices.length + 1];			System.arraycopy(goalIndices, 0, tempGoals, 0, goalIndices.length);			tempGoals[tempGoals.length - 1] = goalIndex;			goalIndices = tempGoals;			double[] tempTruncPercents = new double[transformPercents.length + 1];			System.arraycopy(transformPercents, 0, tempTruncPercents, 0, transformPercents.length);			tempTruncPercents[tempTruncPercents.length - 1] = truncPercent;			transformPercents = tempTruncPercents;		}	}	public static class State {		private boolean real = true;		public boolean isReal() {			return real;		}	}}