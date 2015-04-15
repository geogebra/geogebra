package org.geogebra.common.kernel.discrete.tsp.model;

/**
 * @author Masayasu Fujiwara
 */
public class Edge {
	private Node start;
	private Node end;

	public Edge(Node start, Node end) {
		this.start = start;
		this.end = end;
	}
	
	public Node getStart() {
		return this.start;
	}
	
	public Node getEnd() {
		return this.end;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			Edge entry = (Edge) obj;
			return (this.start.equals(entry.start) && this.end.equals(entry.end)) || (this.start.equals(entry.end) && this.end.equals(entry.start));
//			return this.start.equals(entry.start) && this.terminal.equals(entry.terminal);
		}
		return false;
	}

	public double getDistance() {
		double dx = this.start.getX() - this.end.getX();
		double dy = this.start.getY() - this.end.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public int hashCode() {
		return this.start.hashCode() + this.end.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Edge[");
		sb.append(this.start);
		sb.append("][");
		sb.append(this.end);
		sb.append("]");
		return sb.toString();
	}
}
