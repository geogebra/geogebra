package org.geogebra.common.kernel.discrete.tsp.impl;

import org.geogebra.common.kernel.MyPoint;

// Fast Local Search, 2-Opt "Dont look bits"
public final class FLS {

	/**
	 * 2-Opt a tour. removes 2 edges, then reconstructs a tour. in general (from
	 * http://en.wikipedia.org/wiki/2-opt): 1. take route[0] to route[i-1] and
	 * add them in order to new_route 2. take route[i] to route[k] and add them
	 * in reverse order to new_route 3. take route[k+1] to end and add them in
	 * order to new_route
	 *
	 * note that reverse is called with min(from,to)+1, max(from,to). in a 2-opt
	 * move, 2 edges are removed, leaving 2 disconnected sub-tours. either one
	 * of these subtours is then reversed and the 2 subtours reconnected in a
	 * different (shorter) way.
	 *
	 * Bentley, in his experiments on TSP heuristics paper notes that since
	 * either subtour can be reversed, it is best to reverse the shortest one,
	 * otherwise an arbitrary reversal will be N/2 array accesses.
	 *
	 * this implementation currently reverses whatever subtour does not wrap
	 * around -which could be the larger of the two.
	 */
	private static void reverse(final MyPoint[] x, final int from, final int to) {
		for (int i = from, j = to; i < j; i++, j--) {
			final MyPoint tmp = x[i];
			x[i] = x[j];
			x[j] = tmp;
		}
	}

	/**
	 * a tour is a circle. wrap around.
	 */
	private static int wrap(final int i, final int max) {
		return (max + i) % max;
	}

	/**
	 * cost of a 2-Opt. cost of replacing existing edges (ab), (cd) with new
	 * edges (ac) (bd). returns the delta of a 2-Opt move. a negative delta
	 * indicates that performing this 2-Opt will result in a shorter tour, and a
	 * positive delta indicates that this 2-Opt will result in a longer tour.
	 *
	 * this function is the main hotspot in the optimisation. it is not feasible
	 * to pre-compute a matrix (a lookup table) for a tour with N cities, since
	 * this will be O(N^2) and the most compact representation will be
	 * (N^2-N)/2.
	 *
	 * good optimisation: most of the time the algorithm is evaluating bad
	 * moves, in the obvious case where 2 edge exchanges would result in 2
	 * longer edges, avoid 4 square root operations by comparing squares. this
	 * results in a 40% speed up in this code.
	 */
	private static double moveCost(final MyPoint a, final MyPoint b, final MyPoint c,
			final MyPoint d) {

		// original edges (ab) (cd)
		final double _ab = a.distanceSq(b), _cd = c.distanceSq(d);

		// candidate edges (ac) (bd)
		final double _ac = a.distanceSq(c), _bd = b.distanceSq(d);

		// triangle of inequality: at least 1 edge will be shorter.
		// if both will be longer, there will be no improvement.
		// return a positive delta to indicate no improvement.
		if (_ab < _ac && _cd < _bd) {
			return 1;
		}

		// otherwise must calculate distance delta.
		return (Math.sqrt(_ac) + Math.sqrt(_bd))
				- (Math.sqrt(_ab) + Math.sqrt(_cd));
	}

	/**
	 * set active bits for 4 vertices making up edges ab, cd.
	 */
	private static void activate(final MyPoint a, final MyPoint b, final MyPoint c,
			final MyPoint d) {
		a.setActive(true);
		b.setActive(true);
		c.setActive(true);
		d.setActive(true);
	}

	/**
	 * try to find a move from the current city. given the current city, search
	 * for a 2-opt move that will result in an improvement to the tour length.
	 * the edge before the current city, (prevPoint,currentPoint) and after
	 * (currentPoint,nextPoint) are compared to all over edges (c,d), starting
	 * at (c=currentPoint+2, d=currentPoint+3) until an improvement is found.
	 */
	private static double findMove(final int current, final MyPoint currentPoint,
			final MyPoint[] points, final int numCities) {

		// previous and next city index and point object.
		final int prev = wrap(current - 1, numCities);
		final int next = wrap(current + 1, numCities);
		final MyPoint prevPoint = points[prev];
		final MyPoint nextPoint = points[next];

		// iterate through pairs (i,j) where i = current+2 j = current+3
		// until i = current+numCities-2, j = current+numCities-1.
		// if points = {0,1,2,3,4,5,6,7,8,9}, current = 4, this will produce:
		// (6,7) (7,8) (8,9) (9,0) (0,1) (1,2) (2,3)
		for (int i = wrap(current + 2, numCities), j = wrap(current + 3,
				numCities); j != current; i = j, j = wrap(j + 1, numCities)) {

			final MyPoint c = points[i];
			final MyPoint d = points[j];

			// previous edge:
			// see if swapping the current 2 edges:
			// (prevPoint, currentPoint) (c, d) to:
			// (prevPoint, c) (currentPoint, d)
			// will result in an improvement. if so, set active bits for
			// the 4 vertices involved and reverse everything between:
			// (currentPoint, c).
			final double delta1 = moveCost(prevPoint, currentPoint, c, d);
			if (delta1 < 0) {
				activate(prevPoint, currentPoint, c, d);
				reverse(points, Math.min(prev, i) + 1, Math.max(prev, i));
				return delta1;
			}

			// next edge:
			// see if swapping the current 2 edges:
			// (currentPoint, nextPoint) (c, d) to:
			// (currentPoint, c) (nextPoint, d)
			// will result in an improvement. if so, set active bits for
			// the 4 vertices involved and reverse everything between:
			// (nextPoint, c).
			final double delta2 = moveCost(currentPoint, nextPoint, c, d);
			if (delta2 < 0) {
				activate(currentPoint, nextPoint, c, d);
				reverse(points, Math.min(current, i) + 1, Math.max(current, i));
				return delta2;
			}

		}
		return 0.0;
	}

	/**
	 * optimise a tour.
	 * 
	 * @return a 2-Optimal tour.
	 */
	public static double optimise(final MyPoint[] points) {

		// total tour distance
		double best = distance(points);

		// total number of cities in the tour
		final int numCities = points.length;

		// numCities - visited = total number of active cities.
		// current = current city being explored.
		int visited = 0, current = 0;

		// terminate when a full rotation of of static order from city 1:N
		// has completed without making a move (when all cities are inactive).
		// the resulting tour (points) will be "2-Optimal" -that is, no further
		// imrovements are possible (local optima).
		while (visited < numCities) {
			final MyPoint currentPoint = points[current];
			if (currentPoint.isActive()) {

				// from the current city, try to find a move.
				final double modified = findMove(current, currentPoint, points,
						numCities);

				// if a move was found, go to previous city.
				// best is += modified delta.
				if (modified < 0) {
					current = wrap(current - 1, numCities);
					visited = 0;
					best += modified;
					continue;
				}
				currentPoint.setActive(false);
			}

			// if city is inactive or no moves found, go to next city.
			current = wrap(current + 1, numCities);
			visited++;
		}
		return best;
	}

	/**
	 * Euclidean distance. tour wraps around N-1 to 0.
	 */
	private static double distance(final MyPoint[] points) {
		final int len = points.length;
		double d = points[len - 1].distance(points[0]);
		for (int i = 1; i < len; i++) {
			d += points[i - 1].distance(points[i]);
		}
		return d;
	}

}
