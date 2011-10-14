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

package com.quantimegroup.solutions.archimedean.app;import com.quantimegroup.solutions.archimedean.common.SolidDefinition;public class ExtendedSolidDefinition extends SolidDefinition {	static public SolidDefinition	// RATIONAL			RATIONAL_3_3_4,			RATIONAL_3_4_5, RATIONAL_3_4_6, RATIONAL_3_5_5, RATIONAL_3_5_10, RATIONAL_3_6_10, RATIONAL_4_5_6, RATIONAL_4_5_10;	static public final int RATIONAL = 4;	static {		init();	}	static private void init() {		int[][] signs = {				{						3, 3, 4 }, {						3, 4, 5 }, {						3, 4, 6 }, {						3, 5, 5 }, {						3, 5, 10 }, {						3, 6, 10 }, {						4, 5, 6 }, {						4, 5, 10 } };		String[] names = {				"Rational_3_3_4", "Rational_3_4_5", "Rational_3_4_6", "Rational_3_5_5", "Rational_3_5_10", "Rational_3_6_10", "Rational_4_5_6",				"Rational_4_5_10"		};		if (Archimedean.PRIVILEGED) {			for (int i = 0; i < names.length; ++i) {				new SolidDefinition(signs[i], names[i]);			}		}		if (Archimedean.PRIVILEGED) {			RATIONAL_3_3_4 = SolidDefinition.getKnownSolids().get(31);			RATIONAL_3_4_5 = SolidDefinition.getKnownSolids().get(32);			RATIONAL_3_4_6 = SolidDefinition.getKnownSolids().get(33);			RATIONAL_3_5_5 = SolidDefinition.getKnownSolids().get(34);			RATIONAL_3_5_10 = SolidDefinition.getKnownSolids().get(35);			RATIONAL_3_6_10 = SolidDefinition.getKnownSolids().get(36);			RATIONAL_4_5_6 = SolidDefinition.getKnownSolids().get(37);			RATIONAL_4_5_10 = SolidDefinition.getKnownSolids().get(38);		}	}	static public String typeToString(int type) {		if (type == RATIONAL) {			return "RATIONAL";		} else {			return SolidDefinition.typeToString(type);		}	}	private ExtendedSolidDefinition() {		throw new UnsupportedOperationException();	}}