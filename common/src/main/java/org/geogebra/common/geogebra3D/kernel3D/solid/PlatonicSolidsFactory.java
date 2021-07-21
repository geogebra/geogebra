package org.geogebra.common.geogebra3D.kernel3D.solid;

import org.geogebra.common.kernel.matrix.Coords;

/**
 * Creates the five platonic solids.
 * 
 * @author Mathieu
 *
 */
public class PlatonicSolidsFactory {

	static private volatile PlatonicSolid tetrahedron;
	static private volatile PlatonicSolid cube;
	static private volatile PlatonicSolid octahedron;
	static private volatile PlatonicSolid dodecahedron;
	static private volatile PlatonicSolid icosahedron;

	/**
	 * 
	 * @return tetrahedron description
	 */
	static public PlatonicSolid getTetrahedron() {

		if (tetrahedron == null) {
			PlatonicSolid tetrahedron0 = new PlatonicSolid();
			tetrahedron0.set(
					new Coords[] { new Coords(0.0, 0.0, 0.0, 1.0),
							new Coords(1.0, 0.0, 0.0, 1.0),
							new Coords(0.5, 0.8660254037844398, 0.0, 1.0),
							new Coords(0.5, 0.28867513459481,
									0.8164965809277199, 1.0) },
					new int[][] { { 2, 0, 1 }, { 3, 0, 2 }, { 1, 0, 3 },
							{ 2, 1, 3 } });
			tetrahedron = tetrahedron0;
		}

		return tetrahedron;

	}

	/**
	 * 
	 * @return cube description
	 */
	static public PlatonicSolid getCube() {

		if (cube == null) {
			PlatonicSolid cube0 = new PlatonicSolid();
			cube0.set(
					new Coords[] { new Coords(0.0, 0.0, 0.0, 1.0),
							new Coords(1.0, 0.0, 0.0, 1.0),
							new Coords(1.0, 1.0, 0.0, 1.0),
							new Coords(0.0, 1.0, 0.0, 1.0),
							new Coords(0.0, 0.0, 1.0, 1.0),
							new Coords(1.0, 0.0, 1.0, 1.0),
							new Coords(1.0, 1.0, 1.0, 1.0),
							new Coords(0.0, 1.0, 1.0, 1.0) },
					new int[][] { { 3, 0, 1, 2 }, { 4, 0, 3, 7 },
							{ 1, 0, 4, 5 }, { 2, 1, 5, 6 }, { 3, 2, 6, 7 },
							{ 4, 7, 6, 5 } });

			cube = cube0;
		}

		return cube;

	}

	/**
	 * 
	 * @return octahedron description
	 */
	static public PlatonicSolid getOctahedron() {

		if (octahedron == null) {
			PlatonicSolid octahedron0 = new PlatonicSolid();
			octahedron0.set(
					new Coords[] { new Coords(0.0, 0.0, 0.0, 1.0),
							new Coords(1.0, 0.0, 0.0, 1.0),
							new Coords(0.5, 0.8660254037844398, 0.0, 1.0),
							new Coords(0.5,
									-0.28867513459481986, 0.81649658092772,
									1.0),
							new Coords(
									1.0, 0.57735026918962, 0.81649658092772,
									1.0),
							new Coords(0.0, 0.57735026918962, 0.81649658092772,
									1.0) },
					new int[][] { { 2, 0, 1 }, { 5, 0, 2 }, { 3, 0, 5 },
							{ 1, 0, 3 }, { 4, 1, 3 }, { 2, 1, 4 }, { 5, 2, 4 },
							{ 5, 4, 3 } });

			octahedron = octahedron0;
		}

		return octahedron;

	}

	/**
	 * 
	 * @return dodecahedron description
	 */
	static public PlatonicSolid getDodecahedron() {

		if (dodecahedron == null) {
			PlatonicSolid dodecahedron0 = new PlatonicSolid();
			dodecahedron0.set(
					new Coords[] { new Coords(0.0, 0.0, 0.0, 1.0),
							new Coords(1.0, 0.0, 0.0, 1.0),
							new Coords(
									1.3090169943749501, 0.9510565162951601, 0.0,
									1.0),
							new Coords(0.5, 1.5388417685876306, 0.0, 1.0),
							new Coords(-0.30901699437495, 0.9510565162951601,
									0.0, 1.0),
							new Coords(1.3090169943749501,
									-0.42532540417601983, 0.85065080835204,
									1.0),
							new Coords(
									1.8090169943749501, 1.1135163644116104,
									0.85065080835204, 1.0),
							new Coords(0.5, 2.06457288070676, 0.85065080835204,
									1.0),
							new Coords(-0.8090169943749501, 1.1135163644116104,
									0.85065080835204, 1.0),
							new Coords(-0.30901699437495, -0.42532540417601983,
									0.85065080835204, 1.0),
							new Coords(0.5, -0.6881909602355799,
									1.3763819204711698, 1.0),
							new Coords(1.8090169943749501, 0.26286555605956996,
									1.37638192047118, 1.0),
							new Coords(1.3090169943749501, 1.8017073246472002,
									1.3763819204711698, 1.0),
							new Coords(-0.30901699437495, 1.8017073246472002,
									1.3763819204711698, 1.0),
							new Coords(-0.8090169943749501,
									0.26286555605956996, 1.3763819204711698,
									1.0),
							new Coords(0.5, -0.1624598481164502,
									2.22703272882321, 1.0),
							new Coords(
									1.3090169943749501, 0.42532540417602016,
									2.22703272882321, 1.0),
							new Coords(
									1.0, 1.37638192047117, 2.22703272882321,
									1.0),
							new Coords(0.0, 1.37638192047117, 2.22703272882321,
									1.0),
							new Coords(-0.30901699437495, 0.42532540417602016,
									2.22703272882321, 1.0) },
					new int[][] { { 4, 0, 1, 2, 3 }, { 9, 0, 4, 8, 14 },
							{ 1, 0, 9, 10, 5 }, { 2, 1, 5, 11, 6 },
							{ 3, 2, 6, 12, 7 }, { 4, 3, 7, 13, 8 },
							{ 14, 8, 13, 18, 19 }, { 9, 14, 19, 15, 10 },
							{ 5, 10, 15, 16, 11 }, { 6, 11, 16, 17, 12 },
							{ 7, 12, 17, 18, 13 }, { 19, 18, 17, 16, 15 } });

			dodecahedron = dodecahedron0;
		}

		return dodecahedron;

	}

	/**
	 * 
	 * @return icosahedron description
	 */
	static public PlatonicSolid getIcosahedron() {

		if (icosahedron == null) {
			PlatonicSolid icosahedron0 = new PlatonicSolid();
			icosahedron0.set(
					new Coords[] { new Coords(0.0, 0.0, 0.0, 1.0),
							new Coords(1.0, 0.0, 0.0, 1.0),
							new Coords(0.5, 0.8660254037844398, 0.0, 1.0),
							new Coords(0.5, -0.6454972243679103,
									0.5773502691896201, 1.0),
							new Coords(1.3090169943749501, 0.7557613140761701,
									0.5773502691896302, 1.0),
							new Coords(-0.30901699437495, 0.7557613140761701,
									0.5773502691896201, 1.0),
							new Coords(-0.30901699437495, -0.17841104488655019,
									0.93417235896271, 1.0),
							new Coords(1.3090169943749501,
									-0.17841104488655019, 0.9341723589627201,
									1.0),
							new Coords(0.5, 1.2228474935575302,
									0.93417235896271, 1.0),
							new Coords(0.5,
									-0.28867513459481986, 1.5115226281523402,
									1.0),
							new Coords(1.0, 0.57735026918962,
									1.5115226281523402, 1.0),
							new Coords(0.0, 0.57735026918962,
									1.5115226281523402, 1.0) },
					new int[][] { { 2, 0, 1 }, { 5, 0, 2 }, { 6, 0, 5 },
							{ 3, 0, 6 }, { 1, 0, 3 }, { 7, 1, 3 }, { 4, 1, 7 },
							{ 2, 1, 4 }, { 8, 2, 4 }, { 5, 2, 8 }, { 10, 4, 7 },
							{ 8, 4, 10 }, { 11, 8, 10 }, { 5, 8, 11 },
							{ 9, 10, 7 }, { 11, 10, 9 }, { 6, 11, 9 },
							{ 5, 11, 6 }, { 3, 9, 7 }, { 6, 9, 3 } });

			icosahedron = icosahedron0;
		}

		return icosahedron;

	}

	// code below used to create solids}

}
