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

package org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.common;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.IntList;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.Misc;
import org.geogebra.common.geogebra3D.archimedean.com.quantimegroup.solutions.archimedean.utils.ObjectList;

/**
 * Defines the known Platonic and Archimedean solids and their duals.
 * 
 * @author kasparianr
 * 
 */
public class SolidDefinition {
	private int[] signature;
	private String name;
	private boolean dual = false;
	static private ObjectList<SolidDefinition> knownSolids = new ObjectList<SolidDefinition>(0, 20);

	static public SolidDefinition

	// Platonic
			TETRAHEDRON,
			CUBE,
			OCTAHEDRON,
			DODECAHEDRON,
			ICOSAHEDRON,

			// Archimedean
			TRUNCATED_TETRAHEDRON, TRUNCATED_CUBE, TRUNCATED_OCTAHEDRON, CUBOCTAHEDRON, SMALL_RHOMBICUBOCTAHEDRON,
			GREAT_RHOMBICUBOCTAHEDRON,
			SNUB_CUBE, TRUNCATED_DODECAHEDRON, TRUNCATED_ICOSAHEDRON, ICOSIDODECAHEDRON,
			SMALL_RHOMBICOSIDODECAHEDRON,
			GREAT_RHOMBICOSIDODECAHEDRON,
			SNUB_DODECAHEDRON,

			// Dual
			TRIAKIS_TETRAHEDRON, TRIAKIS_OCTAHEDRON, TETRAKIS_HEXAHEDRON, RHOMBIC_DODECAHEDRON, TRAPEZOIDAL_ICOSITETRAHEDRON,
			HEXAKIS_OCTAHEDRON,
			PENTAGONAL_ICOSITETRAHEDRON, TRIAKIS_ICOSAHEDRON, PENTAKIS_DODECAHEDRON, RHOMBIC_TRIACONTAHEDRON,
			TRAPEZOIDAL_HEXECONTAHEDRON,
			HEXAKIS_ICOSAHEDRON, PENTAGONAL_HEXECONTAHEDRON;

	static public final int UNDEFINED = -1, OTHER = 0, PLATONIC = 1, ARCHIMEDEAN = 2, DUAL = 3;
	static {
		init();
	}

	static private void init() {
		int[][] signs = {
				{
						3, 3, 3 }, {
						4, 4, 4 }, {
						3, 3, 3, 3 }, {
						5, 5, 5 }, {
						3, 3, 3, 3, 3 },

				{
						3, 6, 6 }, {
						3, 8, 8 }, {
						4, 6, 6 }, {
						3, 4, 3, 4 }, {
						3, 4, 4, 4 }, {
						4, 6, 8 }, {
						3, 3, 3, 3, 4 }, {
						3, 10, 10 }, {
						5, 6, 6 }, {
						3, 5, 3, 5 }, {
						3, 4, 5, 4 }, {
						4, 6, 10 }, {
						3, 3, 3, 3, 5 },

				{
						3, 6, 6 }, {
						3, 8, 8 }, {
						4, 6, 6 }, {
						3, 4, 3, 4 }, {
						3, 4, 4, 4 }, {
						4, 6, 8 }, {
						3, 3, 3, 3, 4 }, {
						3, 10, 10 }, {
						5, 6, 6 }, {
						3, 5, 3, 5 }, {
						3, 4, 5, 4 }, {
						4, 6, 10 }, {
						3, 3, 3, 3, 5 } };

		String[] names = {
				"Tetrahedron", "Cube", "Octahedron", "Dodecahedron", "Icosahedron",

				"Truncated Tetrahedron", "Truncated Cube", "Truncated Octahedron", "Cuboctahedron", "Small Rhombicuboctahedron",
				"Great Rhombicuboctahedron", "Snub Cube", "Truncated Dodecahedron", "Truncated Icosohedron", "Icosidodecahedron",
				"Small Rhombicosidodecahedron", "Great Rhombicosidodecahedron", "Snub Dodecahedron",

				"Triakis Tetrahedron", "Triakis Octahedron", "Tetrakis Hexahedron", "Rhombic Dodecahedron", "Trapezoidal Icositetrahedron",
				"Hexakis Octahedron", "Pentagonal Icositetrahedron", "Triakis Icosahedron", "Pentakis Dodecahedron", "Rhombic Triacontahedron",
				"Trapezoidal Hexecontahedron", "Hexakis Icosahedron", "Pentagonal Hexecontahedron" };
		for (int i = 0; i < 18; ++i) {
			new SolidDefinition(signs[i], names[i]);
		}
		for (int i = 18; i < 31; ++i) {
			new SolidDefinition(signs[i], names[i], true);
		}

		// Platonic
		TETRAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(0);
		CUBE = (SolidDefinition) SolidDefinition.knownSolids.get(1);
		OCTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(2);
		DODECAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(3);
		ICOSAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(4);

		// Archimedean
		TRUNCATED_TETRAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(5);
		TRUNCATED_CUBE = (SolidDefinition) SolidDefinition.knownSolids.get(6);
		TRUNCATED_OCTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(7);
		CUBOCTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(8);
		SMALL_RHOMBICUBOCTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(9);
		GREAT_RHOMBICUBOCTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(10);
		SNUB_CUBE = (SolidDefinition) SolidDefinition.knownSolids.get(11);
		TRUNCATED_DODECAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(12);
		TRUNCATED_ICOSAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(13);
		ICOSIDODECAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(14);
		SMALL_RHOMBICOSIDODECAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(15);
		GREAT_RHOMBICOSIDODECAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(16);
		SNUB_DODECAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(17);

		// Dual
		TRIAKIS_TETRAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(18);
		TRIAKIS_OCTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(19);
		TETRAKIS_HEXAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(20);
		RHOMBIC_DODECAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(21);
		TRAPEZOIDAL_ICOSITETRAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(22);
		HEXAKIS_OCTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(23);
		PENTAGONAL_ICOSITETRAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(24);
		TRIAKIS_ICOSAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(25);
		PENTAKIS_DODECAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(26);
		RHOMBIC_TRIACONTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(27);
		TRAPEZOIDAL_HEXECONTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(28);
		HEXAKIS_ICOSAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(29);
		PENTAGONAL_HEXECONTAHEDRON = (SolidDefinition) SolidDefinition.knownSolids.get(30);

	}

	/**
	 * Determine whether this represents an Archimedean or Platonic solid.
	 * 
	 * @param signature
	 *          the signature
	 * @param signatureLength
	 *          the length of the signature
	 * @return SolidDefiniton.ARCHIMEDEAN or SolidDefiniton.PLATONIC
	 */
	static public int getSolidType(int[] signature, int signatureLength) {
		for (int i = 1; i < signatureLength; ++i) {
			if (signature[i] != signature[0])
				return ARCHIMEDEAN;
		}
		return PLATONIC;
	}

	/**
	 * Determine whether this represents an Archimedean or Platonic solid.
	 * 
	 * @param signature
	 *          the signature
	 * @return SolidDefiniton.ARCHIMEDEAN or SolidDefiniton.PLATONIC
	 */
	static public int getSolidType(int[] signature) {
		return getSolidType(signature, signature.length);
	}

	/**
	 * Convert the specified type to a meaningful string.
	 * 
	 * @param type
	 *          SolidDefinition.PLATONIC, SolidDefinition.ARCHIMEDEAN,
	 *          SolidDefinition.DUAL, SolidDefinition.OTHER or
	 *          SolidDefinition.UNDEFINED
	 * @return the string representation of the specified type or "no type" if the
	 *         type is unknown
	 */
	static public String typeToString(int type) {
		switch (type) {
		case PLATONIC:
			return "PLATONIC";
		case ARCHIMEDEAN:
			return "ARCHIMEDEAN";
		case DUAL:
			return "ARCHIMEDEAN DUAL";
		case OTHER:
			return "OTHER";
		case UNDEFINED:
			return "UNDEFINED";
		default:
			return "no type";
		}
	}

	protected SolidDefinition() {
	}

	/**
	 * Construct a SolidDefinition from the signature.
	 * 
	 * @param signature
	 * @param signatureLength
	 */
	public SolidDefinition(int[] signature, String signatureLength) {
		this(signature, signatureLength, false);
	}

	/**
	 * Construct a SolidDefinition from the signature.
	 * 
	 * @param signature
	 */
	public SolidDefinition(int[] signature, String signatureLength, boolean d) {
		this.signature = new int[signature.length];
		System.arraycopy(signature, 0, this.signature, 0, signature.length);
		name = new String(signatureLength);
		if (findSolidDefinition(signatureLength) == null) {
			knownSolids.add(this);
		}
		dual = d;
	}

	private boolean equals(int[] signature) {
		return Misc.arrayCompare(signature, this.signature) == 0;
	}

	private boolean equals(String name) {
		return this.name.equalsIgnoreCase(name);
	}

	private boolean equals(SolidDefinition sd) {
		return equals(sd.name) && equals(sd.signature) && dual == sd.dual;
	}

	/**
	 * Return the SolidDefinition for the specified name.
	 * 
	 * @param theName
	 * @return a SolidDefinition or null of the name is not known.
	 */
	static public SolidDefinition findSolidDefinition(String theName) {
		for (int i = 0; i < knownSolids.num; ++i) {
			SolidDefinition sd = (SolidDefinition) knownSolids.get(i);
			if (sd.equals(theName))
				return sd;
		}
		return null;
	}

	/**
	 * Return the SolidDefinition for the specified signature.
	 * 
	 * @param signature
	 * 
	 * @param isDual
	 * @return a SolidDefinition or null of the signature is not known.
	 */
	public static SolidDefinition findSolidDefinition(int[] signature, boolean dual) {
		if (signature.length != signature.length) {
			int[] temp = new int[signature.length];
			System.arraycopy(signature, 0, temp, 0, signature.length);
			signature = temp;
		}
		for (SolidDefinition sd : getKnownSolids()) {
			for (int i = 0; i < signature.length; ++i) {
				Misc.arrayRotate(signature, 1);
				if (Misc.arrayCompare(signature, sd.signature) == 0) {
					return sd;
				}
			}
		}
		Misc.arrayReverse(signature);
		for (SolidDefinition sd : getKnownSolids()) {
			for (int i = 0; i < signature.length; ++i) {
				Misc.arrayRotate(signature, 1);
				if (Misc.arrayCompare(signature, sd.signature) == 0) {
					return sd;
				}
			}
		}
		return null;
	}

	/**
	 * Return the SolidDefinition for the specified signature.
	 * 
	 * @param signature
	 *          must be in sorted order.
	 * @param isDual
	 * @return a SolidDefinition or null of the signature is not known.
	 */
	static private SolidDefinition findSolidDefinitionOld(int[] signature, boolean isDual) {
		for (int i = 0; i < knownSolids.num; ++i) {
			SolidDefinition sd = (SolidDefinition) knownSolids.get(i);
			if (sd.dual == isDual && Misc.arrayCompare(signature, sd.signature) == 0) {
				return sd;
			}
		}
		return null;
	}

	static public boolean isPrism(int[] signature, int signatureLength) {
		if (signatureLength != 3) {
			return false;
		}
		int count = 0;
		for (int i = 0; i < signatureLength; ++i) {
			if (signature[i] == 4) {
				count++;
			}
		}
		return count == 2;
	}

	static public boolean isAntiprism(int[] signature, int signatureLength) {
		if (signatureLength != 4) {
			return false;
		}
		int count = 0;
		for (int i = 0; i < signatureLength; ++i) {
			if (signature[i] == 3) {
				count++;
			}
		}
		return count == 3;
	}

	/**
	 * Determine if this represents a known solid. The signature may be any
	 * rotation of a known signature or its reflection. For example, 4, 6, 8 is
	 * equivalent to 6, 8, 4 (rotation) and 8, 6, 4 (reversal). This also returns
	 * true for prisms (4, 4, *) and antiprisms (3, 3, 3, *).
	 * 
	 * @param signature
	 * @param signatureLength
	 * @return true if this represents a known solid.
	 */
	public static boolean isKnown(int[] signature, int signatureLength) {
		// check for prisms
		if (isPrism(signature, signatureLength)) {
			return true;
		}
		if (isAntiprism(signature, signatureLength)) {
			return true;
		}
		if (signature.length != signatureLength) {
			int[] temp = new int[signatureLength];
			System.arraycopy(signature, 0, temp, 0, signatureLength);
			signature = temp;
		}
		for (SolidDefinition sd : getKnownSolids()) {
			for (int i = 0; i < signature.length; ++i) {
				Misc.arrayRotate(signature, 1);
				if (Misc.arrayCompare(signature, sd.signature) == 0) {
					return true;
				}
			}
		}
		Misc.arrayReverse(signature);
		for (SolidDefinition sd : getKnownSolids()) {
			for (int i = 0; i < signature.length; ++i) {
				Misc.arrayRotate(signature, 1);
				if (Misc.arrayCompare(signature, sd.signature) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the signature of this SolidDefinition
	 * 
	 * @return
	 */
	public int[] getSignature() {
		return signature;
	}

	/**
	 * Return true if this SolidDefinition represents a dual; false otherwise.
	 * 
	 * @return
	 */
	public boolean isDual() {
		return dual;
	}

	/**
	 * Get the name of this SolidDefinition
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	static public int[] getSignature(int[] polys, int length) {
		IntList sign = new IntList(length);
		for (int i = 0; i < length; ++i) {
			sign.add(polys[i]);
		}
		IntList bestSign = sign, temp;
		for (int i = 0; i < length; ++i) {
			temp = sign.wrapCopy(i, 1);
			if (Misc.arrayCompare(temp.ints, bestSign.ints) < 0)
				bestSign = temp;
			temp = sign.wrapCopy(i, -1);
		}
		return bestSign.ints;
	}

	/**
	 * Get a list of all SolidDefinitions.
	 * 
	 * @return
	 */
	public static List<SolidDefinition> getKnownSolids() {
		knownSolids.shrink();
		List<?> temp = Arrays.asList(knownSolids.objects);
		List<SolidDefinition> list = (List<SolidDefinition>) temp;
		return list;
	}

	public String toString() {
		return name;
	}
}
