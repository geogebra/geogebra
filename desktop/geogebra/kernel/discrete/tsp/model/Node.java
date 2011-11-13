package geogebra.kernel.discrete.tsp.model;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.StringTokenizer;

public class Node {
	private static final int SIZE = 10;
	private double x;
	
	private double y;

	public Node(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Node(String data) {
		StringTokenizer st = new StringTokenizer(data, ",");
		this.x = Integer.parseInt(st.nextToken());
		this.y = Integer.parseInt(st.nextToken());
	}

	/**
	 * é ‚ç‚¹ã‚’æ��ç”»ã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param g
	 * @param color è‰²
	 */
	public void draw(Graphics2D g, Color color) {
		g.setColor(color);
		int centerX = (int)this.x - SIZE / 2;
		int centerY = (int)this.y - SIZE / 2;
		g.fillOval(centerX, centerY, SIZE, SIZE);
		g.setColor(Color.BLACK);
		g.drawOval(centerX, centerY, SIZE, SIZE);
	}

	/**
	 * é ‚ç‚¹ã‚’æ��ç”»ã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param g 
	 * @param index é ‚ç‚¹ç•ªå�·
	 */
	public void draw(Graphics2D g, int index) {
		g.drawString(Integer.toString(index), (int)this.x + SIZE / 2, (int)this.y - SIZE / 2);
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
