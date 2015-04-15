package org.geogebra.common.kernel.discrete.tsp.method.tsp;

import org.geogebra.common.kernel.discrete.tsp.model.Node;
import org.geogebra.common.kernel.discrete.tsp.util.Heap;
import org.geogebra.common.kernel.discrete.tsp.util.IntegerSet;
import org.geogebra.common.util.Unicode;

public class BranchBound {

	class Circuit {
		double cost;
		int[] route;

		public Circuit(int[] route, double cost) {
			this.route = route;
			this.cost = cost;
		}

		public double getCost() {
			return this.cost;
		}

		public int[] getRoute() {
			return this.route;
		}

		public void set(int[] route, double cost) {
			this.cost = cost;
			this.route = route;
		}
	}

	private final int limit;
	private TspImprovement opt;

	public BranchBound(int limit, TspImprovement opt) {
		this.limit = limit;
		this.opt = opt;
	}

	public Circuit branch(Object panel, double[][] table, double[] multipliers,
			boolean[][] edges, boolean[][] connect, boolean[][] disconnect,
			double circuitLowerBound, int depth, double percent) {
		// if (panel != null) panel.set(connect, disconnect);
		for (int i = 0; i < multipliers.length; i++) {
			multipliers[i] = 0;
		}
		int count = 0;
		if (Double.POSITIVE_INFINITY > circuitLowerBound) {

		}
		double multiplier = 1000 / edges.length;
		if (circuitLowerBound < Double.POSITIVE_INFINITY) {
			multiplier = circuitLowerBound / edges.length;
		}
		double lowerBound = 0;
		boolean[][] betterEdges = new boolean[edges.length][edges.length];
		do {
			this.getOneTree(edges, table, multipliers, connect, disconnect);
			double cost = this.getLowerCost(table, multipliers, edges);
			if (circuitLowerBound < cost) {
				// å®Ÿè¡Œå�¯èƒ½è§£ã‚ˆã‚Šã‚‚éƒ¨åˆ†å•�é¡Œã�®ä¸‹ç•Œã�Œå¤§ã��ã�„ã�®ã�§æž�åˆˆã‚Šã�—ã�¾ã�™ã€‚
				// System.out.println((int) (percent * 100)+ "% / bound: "+
				// count + ", depth: "+ depth);
				return null;
			} else if (lowerBound < cost) {
				lowerBound = cost;
				this.copy(edges, betterEdges);
			}
			if (this.updateMulipliers(multiplier, edges, multipliers)) {
				int[] route = BranchBound.compressTableToArray(edges);
				if (this.opt != null) {
					while (this.opt.method(route, table))
						;
					cost = this.getCost(route, table);
				}
				return new Circuit(route, cost);
			}
			multiplier *= 0.95;
		} while (count++ < this.limit);
		this.copy(betterEdges, edges);
		for (int i = 0; i < edges.length; i++) {
			int connection = 0;
			int fix = 0;
			for (int j = 0; j < edges.length; j++) {
				if (edges[i][j]) {
					connection++;
					if (connect[i][j]) {
						fix++;
					}
				}
			}
			if (connection > 2) {
				boolean[][][] connects = new boolean[3 - fix][edges.length][edges.length];
				boolean[][][] disconnects = new boolean[3 - fix][edges.length][edges.length];
				if (fix < 2) {
					for (int l = 0; l < connects.length; l++) {
						for (int j = 0; j < edges.length; j++) {
							for (int k = 0; k < connects[l][j].length; k++) {
								connects[l][j][k] = connect[j][k];
								disconnects[l][j][k] = disconnect[j][k];
							}
						}
					}
					int edge = 0;
					for (int j = 0; j < edges.length; j++) {
						if (i != j && !connect[i][j]) {
							if (edges[i][j]) {
								if (edge < connects.length) {
									disconnects[edge][i][j] = true;
									disconnects[edge][j][i] = true;
									for (int k = edge + 1; k < connects.length; k++) {
										connects[k][i][j] = true;
										connects[k][j][i] = true;
									}
								} else {
									disconnects[disconnects.length - 1][i][j] = true;
									disconnects[disconnects.length - 1][j][i] = true;
								}
								edge++;
							} else {
								disconnects[disconnects.length - 1][i][j] = true;
								disconnects[disconnects.length - 1][j][i] = true;
							}
						}
					}
					for (int j = 0; j < connects.length; j++) {
						this.updateConstraint(connects[j], disconnects[j]);
					}
				}
				Circuit bestCase = null;
				for (int j = connects.length - 1; j >= 0; j--) {
					if (this.hasCircuitPossibility(connects[j], disconnects[j])) {
						Circuit circuit = branch(
								panel,
								table,
								multipliers,
								edges,
								connects[j],
								disconnects[j],
								circuitLowerBound,
								depth + 1,
								percent
										+ (1 / Math.pow(3, depth + 1) * (connects.length
												- j - 1)));
						if (circuit != null) {
							if (circuitLowerBound > circuit.getCost()) {
								circuitLowerBound = circuit.getCost();
								// if (panel != null)
								// panel.set(this.extractArrayToTable(route));
								// if (panel != null) panel.set(connects[j],
								// disconnects[j]);
								// if (panel != null)
								// panel.setCost(circuitLowerBound);
								if (circuitLowerBound < lowerBound) {
									// System.out.println((int) (percent + (1 /
									// Math.pow(3, depth + 1) * (connects.length
									// - j)) * 100) +
									// "% / bound circuit, depth: "+ depth);
									return circuit;
								}
								bestCase = circuit;
							}
						}
					}
				}
				if (bestCase != null) {
					return bestCase;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private static int[] compressTableToArray(boolean[][] table) {
		int[] route = new int[table.length];
		int before = -1;
		int index = 0;
		int count = 0;
		for (int i = 0; count < table.length; i++) {
			if (index != i && table[index][i]) {
				if (before != i) {
					route[count++] = index;
					before = index;
					index = i;
					i = -1;
				}
			}
		}
		return route;
	}

	public void copy(boolean[][] arrays, boolean[][] copy) {
		for (int i = 0; i < copy.length; i++) {
			for (int j = 0; j < copy[i].length; j++) {
				copy[i][j] = arrays[i][j];
			}
		}
	}

	public double[][] createTable(Node[] nodes) {
		final double[][] table = new double[nodes.length][nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			for (int j = i + 1; j < nodes.length; j++) {
				double distance = nodes[i].getDistance(nodes[j]);
				table[i][j] = distance;
				table[j][i] = distance;
			}
		}
		return table;
	}

	public String EdgetoString(boolean[][] edges) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < edges.length; i++) {
			for (int j = 0; j < edges[i].length; j++) {
				if (edges[i][j]) {
					sb.append("* ");
				} else {
					sb.append("- ");
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private double getCost(int[] route, double[][] table) {
		int previous = route[route.length - 1];
		int next;
		double cost = 0;
		for (int i = 0; i < route.length; i++) {
			next = route[i];
			cost += table[previous][next];
			previous = next;
		}
		return cost;
	}

	private double getLowerCost(double[][] table, double[] multiplier,
			boolean[][] edges) {
		double cost = 0;
		int edge = 0;
		for (int i = 1; i < table.length; i++) {
			for (int j = 0; j < i; j++) {
				if (edges[i][j]) {
					cost += table[i][j] + multiplier[i] + multiplier[j];
					edge++;
				}
			}
		}
		if (edge != edges.length) {
			return Double.POSITIVE_INFINITY;
		}
		double sigma = 0;
		for (double m : multiplier) {
			sigma += m;
		}
		return cost - 2 * sigma;
	}

	// private Heap<Entry> open = new Heap<Entry>();

	private void getOneTree(final boolean[][] edges, final double[][] table,
			final double[] multipliers, boolean[][] connect,
			boolean[][] disconnect) {
		for (int i = 0; i < edges.length; i++) {
			for (int j = 0; j < edges.length; j++) {
				edges[i][j] = false;
			}
		}
		Heap<Entry> open = new Heap<Entry>(11);
		// HashSet<Integer> notClose = new HashSet<Integer>(edges.length);
		IntegerSet notClose = new IntegerSet(edges.length);
		for (int i = 0; i < edges.length; i++) {
			notClose.add(i);
		}
		// int s = (int) (Math.random() * edges.length);
		int s = 0;
		notClose.remove(s);
		int index = (s + 1) % edges.length;
		notClose.remove(index);
		Entry edge;
		// Iterator<Integer> itr = notClose.iterator();
		for (int i = 0; i < notClose.size(); i++) {
			// int a = itr.next();
			int a = notClose.get(i);
			if (!disconnect[index][a]) {
				open.add(new Entry(index, a,
						connect[index][a] ? Double.NEGATIVE_INFINITY
								: table[index][a] + multipliers[index]
										+ multipliers[a]));
			}
		}
		do {
			edge = open.poll();
			if (edge == null) {
				break;
			}
			edges[edge.s][edge.t] = true;
			edges[edge.t][edge.s] = true;
			index = edge.t;
			notClose.remove(index);
			// itr = notClose.iterator();
			for (int i = 0; i < notClose.size(); i++) {
				// int a = itr.next();
				int a = notClose.get(i);
				if (!disconnect[index][a]) {
					open.add(new Entry(index, a,
							connect[index][a] ? Double.NEGATIVE_INFINITY
									: table[index][a] + multipliers[index]
											+ multipliers[a]));
				}
			}
		} while (open.size() > 0);
		open.clear();
		for (int i = 0; i < edges.length; i++) {
			if (s != i && !disconnect[s][i]) {
				open.add(new Entry(s, i,
						connect[s][i] ? Double.NEGATIVE_INFINITY : table[s][i]
								+ multipliers[s] + multipliers[i]));
			}
		}
		edge = open.poll();
		edges[edge.s][edge.t] = true;
		edges[edge.t][edge.s] = true;
		edge = open.poll();
		edges[edge.s][edge.t] = true;
		edges[edge.t][edge.s] = true;
	}

	class Entry implements Comparable<Entry> {
		double cost;
		int s;
		int t;

		public Entry(int s, int t, double cost) {
			this.s = s;
			this.t = t;
			this.cost = cost;
		}

		public int compareTo(Entry e) {
			double diff = this.cost - e.cost;
			if (diff < 0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			}
			// TODO -- implement this in GWT compatible way if we really need
			// such precision
			/*
			 * long thisBits = Double.doubleToLongBits(this.cost); long
			 * anotherBits = Double.doubleToLongBits(e.cost); return (thisBits
			 * == anotherBits ? 0 : (thisBits < anotherBits ? -1 : // (-0.0,
			 * 0.0) or (!NaN, NaN) 1)); // (0.0, -0.0) or (NaN, !NaN)
			 */
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			return this.hashCode() == obj.hashCode();
		}

		@Override
		public int hashCode() {
			return this.t;
		}
	}

	private boolean hasCircuitPossibility(boolean[][] connect,
			boolean[][] disconnect) {
		for (int i = 0; i < connect.length; i++) {
			int connection = 0;
			int disconnection = 0;
			for (int j = 0; j < connect[i].length; j++) {
				if (i != j) {
					if (connect[i][j]) {
						connection++;
					}
					if (disconnect[i][j]) {
						disconnection++;
					}
				}
			}
			if (connection > 2) {
				return false;
			}
			if (disconnect.length < disconnection + 3) {
				return false;
			}
		}
		return true;
	}

	public int[] method(Node[] nodes) {
		if (nodes.length > 2) {
			boolean[][] edges = new boolean[nodes.length][nodes.length];
			double[][] table = createTable(nodes);
			double[] multipliers = new double[nodes.length];
			boolean[][] connect = new boolean[nodes.length][nodes.length];
			boolean[][] disconnect = new boolean[nodes.length][nodes.length];
			int depth = 0;
			double percent = 0;
			Circuit betterCase = branch(null, table, multipliers, edges,
					connect, disconnect, Double.POSITIVE_INFINITY, depth,
					percent);
			// System.out.println("[" + this.toString() + "]");
			// System.out.println("node: "+ nodes.length);
			// System.out.println("cost: "+ ((int) (betterCase.getCost() * 1000)
			// / 1000D));
			// System.out.println("time: "+ (end - start) + "ms");
			// panel.set(this.extractArrayToTable(betterCase.getRoute()));
			// panel.setCost(betterCase.getCost());
			// System.out.println();
			return betterCase.getRoute();
		}

		return null;
	}

	@Override
	public String toString() {
		if (this.opt != null) {
			return "Branch and Bound - " + this.opt + " " + Unicode.multiply + " " + this.limit;
		} else {
			return "Branch and Bound " + Unicode.multiply + " " + this.limit;
		}
	}

	private void updateConstraint(boolean[][] connect, boolean[][] disconnect) {
		for (int i = 0; i < connect.length; i++) {
			int connection = 0;
			int disconnection = 0;
			for (int j = 0; j < connect.length; j++) {
				if (connect[i][j]) {
					connection++;
				}
				if (disconnect[i][j]) {
					disconnection++;
				}
			}
			if (connection == 2
					&& connection + disconnection + 1 != connect.length) {
				for (int j = 0; j < connect[i].length; j++) {
					if (i != j) {
						if (connect[i][j]) {
							disconnect[i][j] = false;
							disconnect[j][i] = false;
						} else {
							disconnect[i][j] = true;
							disconnect[j][i] = true;
						}
					}
				}
			}
		}
	}

	private boolean updateMulipliers(double multiple, boolean[][] edges,
			double[] multipliers) {
		boolean ret = true;
		for (int i = 0; i < edges.length; i++) {
			int connection = 0;
			for (int j = 0; j < edges[i].length; j++) {
				if (edges[i][j]) {
					connection++;
				}
			}
			if (connection < 2) {
				multipliers[i] -= multiple;
				ret = false;
			} else if (connection > 2) {
				multipliers[i] += multiple;
				ret = false;
			}
		}
		return ret;
	}
}