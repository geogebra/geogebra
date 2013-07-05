package geogebra.common.kernel.prover;

import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.polynomial.Polynomial;
import geogebra.common.kernel.prover.polynomial.Variable;
import geogebra.common.main.App;
import geogebra.common.util.Prover;
import geogebra.common.util.Prover.NDGCondition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Detects NDG conditions
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 */
public class NDGDetector {

	/**
	 * Returns the NDG condition (as a GeoGebra object) if the input polynomial is detected as a
	 * recognizable geometrically meaningful condition (parallelism, collinearity etc.).
	 * @param p input polynomial
	 * @param prover input prover
	 * @return the NDG condition
	 */
	public static NDGCondition detect(Polynomial p, Prover prover) {

		List<GeoElement> freePoints = ProverBotanasMethod.getFreePoints(prover.getStatement());
		HashSet<GeoElement >freePointsSet = new HashSet<GeoElement>(freePoints);
		
		Combinations triplets = new Combinations(freePointsSet,3);
		
		while (triplets.hasNext()) {
			HashSet<Object> triplet = (HashSet<Object>) triplets.next();
			Iterator<Object> it = triplet.iterator();
			// GeoElement[] points = (GeoElement[]) triplet.toArray();
			// This is not working directly, so we have to do it manually:
			int i = 0;
			GeoElement[] points = new GeoElement[triplet.size()];
			while (it.hasNext()) {
				points[i] = (GeoElement) it.next();
				i++;
			}
			Variable[] fv1 = ((SymbolicParametersBotanaAlgo)points[0]).getBotanaVars(points[0]);
			Variable[] fv2 = ((SymbolicParametersBotanaAlgo)points[1]).getBotanaVars(points[1]);
			Variable[] fv3 = ((SymbolicParametersBotanaAlgo)points[2]).getBotanaVars(points[2]);
			// Creating the polynomial for collinearity:
			Polynomial coll = Polynomial.collinear(fv1[0], fv1[1], fv2[0], fv2[1], fv3[0], fv3[1]);
			if (coll.add(p).isZero() || coll.equals(p)) { // coll == +p or -p
				App.debug(p + " means collinearity for " + triplet);
				NDGCondition ndgc = new NDGCondition();
				ndgc.setGeos(points);
				Arrays.sort(ndgc.getGeos());
				ndgc.setCondition("AreCollinear");
				return ndgc;
			}

		}

		return null;

		
	}	
}
