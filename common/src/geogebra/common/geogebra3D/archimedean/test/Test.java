package geogebra.common.geogebra3D.archimedean.test;

import geogebra.common.geogebra3D.archimedean.support.ArchimedeanSolidFactory;
import geogebra.common.geogebra3D.archimedean.support.IArchimedeanSolid;

import com.quantimegroup.solutions.archimedean.common.SolidDefinition;

/**
 * Use this class for testing GeoGebra / Archimedean collaboration.
 * 
 * @author kasparianr
 * 
 */
public class Test {
	public static void main(String[] arg) throws Exception {
		SolidDefinition[] testSolids = {
				SolidDefinition.CUBE, SolidDefinition.ICOSAHEDRON, SolidDefinition.TETRAHEDRON, SolidDefinition.TRUNCATED_CUBE, SolidDefinition.GREAT_RHOMBICOSIDODECAHEDRON };
		for (SolidDefinition sd : testSolids) {
			System.out.println(sd.getName());
			IArchimedeanSolid solid = ArchimedeanSolidFactory.create(sd);
			System.out.println(solid);
			System.out.println();

		}

		int[][] signatures = new int[][] {
				{
						3, 4, 5, 4 }, {
						3, 3, 3, 13 }, {
						8, 4, 6 } };
		for (int[] signature : signatures) {
			IArchimedeanSolid solid = null;
			try {
				solid = ArchimedeanSolidFactory.create(signature);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			SolidDefinition sd = SolidDefinition.findSolidDefinition(signature, false);
			if (sd != null) {
				System.out.println(sd.getName());
			} else {
				System.out.println("Unnamed solid.");
			}
			System.out.println(solid);
			System.out.println();

		}
	}
}
