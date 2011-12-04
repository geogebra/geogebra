package geogebra.common.kernel;

//point pair (i, j, dist) stores the point 
//pair D_i, Q_j and their distance
public class PointPair {
	public int indexP;
	public boolean isPalive;
	public int indexQ;	
	public boolean isQonPath;
	public double dist;
	
	public PointPair next;
	
	public PointPair(int i, boolean isPalive, int j, boolean isQjOnPath, double distance) {
		indexP = i;
		this.isPalive = isPalive;
		indexQ = j;
		isQonPath = isQjOnPath;
		dist = distance;
	}
	
	/*
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(indexP);
		sb.append(", ");
		sb.append(isPalive);
		sb.append(", ");
		sb.append(indexQ);
		sb.append(", ");
		sb.append(isQonPath);
		sb.append(", ");
		sb.append(dist);
		sb.append(")\n");
		return sb.toString();
	}*/
} 
