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

package com.quantimegroup.solutions.archimedean.app;import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;public abstract class TP extends OrderedTriple {	static ArchiBuilder boss;	protected int start;	protected int[] goals;	protected double[] truncPercents;	TP(OrderedTriple p) {		super(p);	}	static public void init(ArchiBuilder a) {		boss = a;	}	OrderedTriple getStartPoint() {		return (OrderedTriple) boss.nonUpdatingPoints.get(start);	}	OrderedTriple getGoalPoint(int i) {		return (OrderedTriple) boss.nonUpdatingPoints.get(goals[i]);	}	public boolean equals(OrderedTriple T) {		Class tClass = T.getClass();		if (getClass() != tClass) return false;		TruncablePoint t = (TruncablePoint) T;		if (super.equals(t) && start == t.start && boss == t.boss){			if (goals.length == t.goals.length && truncPercents.length == t.truncPercents.length){				for (int i = 0; i < goals.length; ++i)					if (goals[i] != t.goals[i]) return false;				for (int i = 0; i < truncPercents.length; ++i)					if (truncPercents[i] != t.truncPercents[i]) return false;				return true;			}		}		return false;	}	abstract void locate(double truncPercent);}