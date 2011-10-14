package geogebra3D.archimedean.support;

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
	 */
	public static IArchimedeanSolid create(SolidDefinition sd) {
		int[] polys = sd.getSignature();
		boolean isDual = sd.isDual();
		if (isDual) {
			throw new IllegalArgumentException("Duals not handled yet. Will be handled when/if necessary.");
		}
		return new AbstractArchimedeanSolid(polys, polys.length, false) {
		};
	}
}
