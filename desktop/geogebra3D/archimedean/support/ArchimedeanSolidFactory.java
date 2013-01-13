package geogebra3D.archimedean.support;

import geogebra.common.kernel.commands.Commands;

import com.quantimegroup.solutions.archimedean.common.SolidDefinition;

/**
 * Factory for creating Archimedean solids.
 * 
 * @author kasparianr
 * 
 */
public class ArchimedeanSolidFactory {
	private ArchimedeanSolidFactory() {
	}

	/**
	 * Create an Archimedean solid for the specified type.
	 * 
	 * @param sd
	 * @return
	 * @throws Exception
	 */
	public static IArchimedeanSolid create(SolidDefinition sd) throws Exception {
		int[] polys = sd.getSignature();
		boolean isDual = sd.isDual();
		if (isDual) {
			throw new IllegalArgumentException(
					"Duals not handled yet. Will be handled when/if necessary.");
		}
		return new AbstractArchimedeanSolid(polys, polys.length, false) {
			public IFace createFace() {
				return new Face();
			}
		};
	}

	/**
	 * Create an Archimedean solid for the specified type.
	 * 
	 * @param sd
	 * @return
	 * @throws Exception
	 */
	public static IArchimedeanSolid create(int[] polys) throws Exception {
		return new AbstractArchimedeanSolid(polys, polys.length, false) {
			public IFace createFace() {
				return new Face();
			}
		};
	}

	public static IArchimedeanSolid create(Commands name) {
		try {
			return create(getSolidDefinition(name));
		} catch (Exception e) {
			return null;
		}
	}

	private static final SolidDefinition getSolidDefinition(Commands name) {

		switch (name) {
		case Tetrahedron:
				return SolidDefinition.TETRAHEDRON;

		case Cube:
				return SolidDefinition.CUBE;

		case Octahedron:
				return SolidDefinition.OCTAHEDRON;

		case Dodecahedron:
				return SolidDefinition.DODECAHEDRON;

		case Icosahedron:
				return SolidDefinition.ICOSAHEDRON;

		}
		return null;
	}
}
