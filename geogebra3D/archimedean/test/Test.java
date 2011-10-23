package geogebra3D.archimedean.test;

import geogebra3D.archimedean.support.ArchimedeanSolidFactory;
import geogebra3D.archimedean.support.IArchimedeanSolid;

import comold.quantimegroup.solutions.archimedean.common.SolidDefinition;

/**
 * Use this class for testing GeoGebra / Archimedean collaboration.
 * 
 * @author kasparianr
 * 
 */
public class Test {
	public static void main(String[] arg) {
		SolidDefinition[] testSolids = {
				SolidDefinition.CUBE, SolidDefinition.TETRAHEDRON, SolidDefinition.TRUNCATED_CUBE, SolidDefinition.GREAT_RHOMBICOSIDODECAHEDRON };
		for (SolidDefinition sd : testSolids) {
			IArchimedeanSolid solid = ArchimedeanSolidFactory.create(sd);
			System.out.println(sd.getName());
			System.out.println(solid);
			System.out.println();

		}
	}
}
