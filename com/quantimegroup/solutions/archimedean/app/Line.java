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

package com.quantimegroup.solutions.archimedean.app;import com.quantimegroup.solutions.archimedean.utils.OrderedTriple;public class Line{	OrderedTriple[] originalSects, originalOuts;	SpacePoint[] sects, outs;		public Line( OrderedTriple L1, OrderedTriple L2, SpacePoly poly, double length ){		//assumes that it passes through the origin		//since we are assumming that L is also an axis of symmetry, l must be perpendicular to the		//two sides that it crosses.  This means that any sect to its out is a normal of that side.  		//Therefore when determining whether the side that the line is coming out of is visible,		//you need not know the side.  Just use sect->out in place of the sides normal.  But since		//the line passes through the origin, this is equivalent to as outs (or sects)		//Also assumes that L1 and L2 are originalSects[1] and [2]		sects = new SpacePoint[2];		outs = new SpacePoint[2];		originalSects = new OrderedTriple[2];		originalOuts = new OrderedTriple[2];		 		originalSects[0] = new OrderedTriple( L1 );		originalSects[1] = new OrderedTriple( L2 );		for( int i = 0; i < 2; ++i ){			originalOuts[i] = originalSects[i].unit().times( length/2 );			outs[i] = new SpacePoint();			sects[i] = new SpacePoint();		}	}	public boolean visible( int whichHalf ){		return SpacePoint.viewer.minus( sects[whichHalf] ).dot( sects[whichHalf] ) > 0;	}}