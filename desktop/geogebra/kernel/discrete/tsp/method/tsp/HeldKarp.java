package geogebra.kernel.discrete.tsp.method.tsp;

import geogebra.kernel.discrete.tsp.gui.DemoPanel;
import geogebra.kernel.discrete.tsp.method.GraphDemonstration;
import geogebra.kernel.discrete.tsp.model.Node;
import geogebra.kernel.discrete.tsp.util.Heap;

/**
 * Held and Karpã�®æ‰‹æ³•ã�«ã‚ˆã‚‹å·¡å›žã‚»ãƒ¼ãƒ«ã‚¹ãƒžãƒ³å•�é¡Œã�®åŽ³å¯†è§£æ³•
 * ãƒ©ã‚°ãƒ©ãƒ³ã‚¸ãƒ¥ç·©å’Œã€�ãƒ©ã‚°ãƒ©ãƒ³ã‚¸ãƒ¥ä¹—æ•°ã�¯é�©å½“
 * @author ma38su
 */
public class HeldKarp implements GraphDemonstration {
	private final int limit;
	public HeldKarp(int limit) {
		this.limit = limit;
	}
	/**
	 * ä¸€æ™‚çš„ã�«è¾ºã‚’è¡¨ç�¾ã�™ã‚‹ã�Ÿã‚�ã�®ã‚¯ãƒ©ã‚¹
	 * @author ma38su
	 */
	class Edge {
		int s;
		int t;
		double cost;
		public Edge(int s, int t, double cost) {
			this.s = s;
			this.t = t;
			this.cost = cost;
		}

		@Override
		public boolean equals(Object obj) {
			return this.hashCode() == obj.hashCode();
		}

		public int compareTo(Edge e) {
			double diff = this.cost - e.cost;
			if (diff < 0) {
				return -1;
			} else if (diff > 0){
				return 1;
			}
	        long thisBits = Double.doubleToLongBits(this.cost);
	        long anotherBits = Double.doubleToLongBits(e.cost);
	        return (thisBits == anotherBits ? 0 : 
	                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
	                 1));                          // (0.0, -0.0) or (NaN, !NaN)
		}
		@Override
		public int hashCode() {
			return this.t;
		}
	}

	/**
	 * 1ã�¤ã�®é ‚ç‚¹ã�«2ã�¤ã�®è¾ºã�Œã�¤ã�ªã�Œã�£ã�¦ã�„ã‚‹ã�‹ã�©ã�†ã�‹ç¢ºèª�ã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * 1-treeã�Œã�“ã‚Œã‚’æº€ã�Ÿã�›ã�°ã€�å·¡å›žè·¯ã�¨ã�„ã�ˆã‚‹ã€‚
	 * @param dcost æ“�ä½œé‡�
	 * @param edges è¾ºã�®æŽ¥ç¶š
	 * @param adjustment ãƒ©ã‚°ãƒ©ãƒ³ã‚¸ãƒ¥ç·©å’Œã�«ã‚ˆã‚‹è¾ºã�®è£œæ­£
	 * @return å·¡å›žè·¯ã�§ã�‚ã‚Œã�°trueã€�å·¡å›žè·¯ã�§ã�ªã�‘ã‚Œã�°falseã‚’è¿”ã�™ã€‚
	 */
	private boolean checkCircuit(double dcost, boolean[][] edges, double[] adjustment) {
		boolean ret = true;
		for (int i = 0; i < edges.length; i++) {
			int connection = 0;
			for (int j = 0; j < edges[i].length; j++) {
				if (edges[i][j]) {
					connection++;
				}
			}
			if (connection < 2) {
				adjustment[i] -= dcost;
				ret = false;
			} else if (connection > 2) {
				adjustment[i] += dcost;
				ret = false;
			}
		}
		return ret;
	}

	/**
	 * å·¡å›žè·¯ã�®ã‚³ã‚¹ãƒˆã�®ä¸‹ç•Œã‚’æ±‚ã‚�ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param table è·�é›¢ãƒ†ãƒ¼ãƒ–ãƒ«
	 * @param multiplier ãƒ©ã‚°ãƒ©ãƒ³ã‚¸ãƒ¥ä¹—æ•°
	 * @param edges è¾ºã�®æŽ¥ç¶šé–¢ä¿‚
	 * @return å·¡å›žè·¯ã�®ã‚³ã‚¹ãƒˆã�®ä¸‹ç•Œ
	 */
	private double getLowerCost(double[][] table, double[] multiplier, boolean[][] edges) {
		double cost = 0;
		for (int i = 0; i < table.length; i++) {
			for (int j = i + 1; j < table[i].length; j++) {
				if (edges[i][j]) {
					cost += table[i][j] + multiplier[i] + multiplier[j];
				}
			}
		}
		double sigma = 0;
		for (double m : multiplier) {
			sigma += m;
		}
		return cost - 2 * sigma;
	}

	/**
	 * 1-treeã‚’æ±‚ã‚�ã‚‹ã€‚
	 * @param panel ãƒ‘ãƒ�ãƒ«
	 * @param edges è¾ºé…�åˆ—
	 * @param nodes é ‚ç‚¹é…�åˆ—
	 * @param table è·�é›¢ãƒ†ãƒ¼ãƒ–ãƒ«
	 * @param multipliers ãƒ©ã‚°ãƒ©ãƒ³ã‚¸ãƒ¥ç·©å’Œã�«ã‚ˆã‚‹è·�é›¢ã�®è£œæ­£
	 */
	private void getOneTree(DemoPanel panel, final boolean[][] edges, final double[][] table, final double[] multipliers) {
		for (int i = 0; i < edges.length; i++) {
			for (int j = 0; j < edges.length; j++) {
				edges[i][j] = false;
			}
		}
		if (edges.length > 1) {
			Edge e;
			boolean[] close = new boolean[edges.length];
			Heap<Edge> open = new Heap<Edge>();
			int s = (int) (Math.random() * edges.length);
			close[s] = true;
			int index = (s + 1) % edges.length;
			close[index] = true;
			do {
				for (int i = 0; i < edges.length; i++) {
					if (i != index && !close[i]) {
						open.add(new Edge(index, i, table[index][i] + multipliers[index] + multipliers[i]));
					}
				}
				e = open.poll();
				if (e == null) {
					break;
				}
				edges[e.s][e.t] = true;
				edges[e.t][e.s] = true;
				index = e.t;
				close[index] = true;
				panel.set(edges);
			} while (open.size() > 0);
			open.clear();
			for (int i = 0; i < edges.length; i++) {
				if (s != i) {
					open.add(new Edge(s, i, table[index][i] + multipliers[index] + multipliers[i]));
				}
			}
			e = open.poll();
			edges[e.s][e.t] = true;
			edges[e.t][e.s] = true;
			e = open.poll();
			edges[e.s][e.t] = true;
			edges[e.t][e.s] = true;
		}
	}

	/**
	 * è·�é›¢ãƒ†ãƒ¼ãƒ–ãƒ«ã‚’ä½œæˆ�ã�—ã�¾ã�™ã€‚
	 * @param nodes é ‚ç‚¹é…�åˆ—
	 * @return è·�é›¢ãƒ†ãƒ¼ãƒ–ãƒ«
	 */
	public double[][] createTable(Node[] nodes) {
		final double[][] table = new double[nodes.length][nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes.length; j++) {
				table[i][j] = nodes[i].getDistance(nodes[j]);
			}
		}
		return table;
	}

	public void method(DemoPanel panel) {
		final Node[] nodes = panel.getNodes().toArray(new Node[]{});
		double[][] table = createTable(nodes);
		boolean[][] edges = new boolean[nodes.length][nodes.length];
		double[] multipliers = new double[nodes.length];
		BetterCase betterCase = new BetterCase(nodes.length);
		double lowerBound = 0;
		int count = 0;
		double multiplier = 100;
		do {
			this.getOneTree(panel, edges, table, multipliers);
			panel.set(edges);
			double cost = this.getLowerCost(table, multipliers, edges);
			if (lowerBound < cost) {
				lowerBound = cost;
				betterCase.set(edges, multipliers, lowerBound);
				panel.setCost(lowerBound);
			}
			multiplier *= 0.9;
		} while (count++ < this.limit && !this.checkCircuit(multiplier, edges, multipliers));
		panel.set(betterCase.getEdges());
	}

	@Override
	public String toString() {
		return "Held and Karp";
	}
	class BetterCase {
		boolean[][] edges;
		double[] multipliers;
		double lowerBound;
		public BetterCase(int nodes) {
			this.edges = new boolean[nodes][nodes];
			this.multipliers = new double[nodes];
		}
		public void set(boolean[][] edges, double[] multipliers, double lowerBound) {
			for (int i = 0; i < edges.length; i++) {
				for (int j = 0; j < edges[i].length; j++) {
					this.edges[i][j] = edges[i][j];
				}
			}
			for (int i = 0; i < multipliers.length; i++) {
				this.multipliers[i] = multipliers[i];
			}
			this.lowerBound = lowerBound;
		}
		public boolean[][] getEdges() {
			return this.edges;
		}
		public double[] getMultipliers() {
			return this.multipliers;
		}
		public double getLowerBound() {
			return this.lowerBound;
		}
	}
}