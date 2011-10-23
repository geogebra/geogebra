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
			public IFace createFace() {
				return new Face();
			}
		};
	}
	

	public static IArchimedeanSolid create(String name) {
		return create(getSolidDefinition(name));
	}
	
	
	
	private static final SolidDefinition getSolidDefinition(String name){
		
		switch(name.charAt(0)){
		case 'T':
			if (name.equals("Tetrahedron"))
				return SolidDefinition.TETRAHEDRON;
			break;
			
		case 'C':
			if (name.equals("Cube"))
				return SolidDefinition.CUBE;
			break;

		case 'O':
			if (name.equals("Octahedron"))
				return SolidDefinition.OCTAHEDRON;
			break;

		case 'D':
			if (name.equals("Dodecahedron"))
				return SolidDefinition.DODECAHEDRON;
			break;

		case 'I':
			if (name.equals("Icosahedron"))
				return SolidDefinition.ICOSAHEDRON;
			break;

		
	}
		
		return null;
	}
}
