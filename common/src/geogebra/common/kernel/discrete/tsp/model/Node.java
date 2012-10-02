package geogebra.common.kernel.discrete.tsp.model;

public class Node {
	private double x;
	
	private double y;

	public Node(double x, double y) {
		this.x = x;
		this.y = y;
	}

	

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node node = (Node) obj;
			return this.x == node.x && this.y == node.y;
		}
		return false;
	}
	
	/**
	 * é ‚ç‚¹é–“ã�®ç›´ç·šè·�é›¢ã‚’è¨ˆç®—ã�—ã�¾ã�™ã€‚
	 * @param node è·�é›¢ã‚’è¨ˆç®—ã�™ã‚‹é ‚ç‚¹
	 * @return é ‚ç‚¹é–“ã�®ç›´ç·šè·�é›¢
	 */
	public double getDistance(Node node) {
		double dx = this.x - node.x;
		double dy = this.y - node.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public double getX() {
		return this.x;
	}
	
	
	public double getY() {
		return this.y;
	}

	@Override
	public String toString() {
		return this.x + "," + this.y;
	}
}
