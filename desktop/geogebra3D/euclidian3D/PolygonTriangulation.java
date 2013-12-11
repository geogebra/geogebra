package geogebra3D.euclidian3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoPolygon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Class to convert a GeoPolygon to a set of triangles
 * @author mathieu
 *
 */
public class PolygonTriangulation implements Comparator<Integer> {
	
	private class Point{
		public double x, y;
		public String name;
		public int orientation;
		
		public Point(double x, double y){
			this.x = x;
			this.y = y;
		}
	}

	private GeoPolygon p;
	
	private ArrayList<Point> pointsList;
	
	/**
	 * Constructor
	 * @param p polygon
	 */
	public PolygonTriangulation(GeoPolygon p){
		this.p = p;
		pointsList = new ArrayList<Point>();
	}
	
	public void updatePoints(){
		pointsList.clear();
		for (int i = 0; i < p.getPointsLength(); i++){
			Point point = new Point(p.getPointX(i), p.getPointY(i));
			pointsList.add(point);
		}
	}
	
	final private Point getPoint(int i){
		return pointsList.get(i);
	}

	/*
	private double getPointX(int i){
		return p.getPointX(i);
	}
	
	private double getPointY(int i){
		return p.getPointY(i);
	}
	*/
	
	private int getPointsLength(){
		return pointsList.size();
	}

	//////////////////////////////////////
	// COMPARATOR
	//////////////////////////////////////
	
	public int compare(Integer i1, Integer i2) {
		
		// smallest x
		Point p1 = getPoint(i1);
		Point p2 = getPoint(i2);
		if (Kernel.isGreater(p2.x, p1.x)){
			return -1;
		}			
		if (Kernel.isGreater(p1.x, p2.x)){
			return 1;
		}
		
		// then smallest y
		if (Kernel.isGreater(p2.y, p1.y)){
			return -1;
		}			
		if (Kernel.isGreater(p1.y, p2.y)){
			return 1;
		}
		
		// then smallest index
		if (i1 < i2){
			return -1;
		}
		if (i1 > i2){
			return 1;
		}
		
		// same point
		return 0;
	}

	
	
	/**
	 * 
	 * @return list of list of vertex indices, which forms triangle fans tessalating the polygon
	 */
	public ArrayList<ArrayList<Integer>> getTriangulation(){
		ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
		
		final int length = getPointsLength();
		
		// put 2D indices in sweep order (smaller x, then smaller y)
		TreeSet<Integer> sweepTree = new TreeSet<Integer>(this);
		for (int i = 0; i < length; i++){
			sweepTree.add(i);
		}
		/*
		App.error("sweepTree:");
		for (int i : sweepTree){
			App.debug(i+": "+getPointsND()[i]);
		}
		*/
		
		
		
		/*
		// find points with edges both on right or both on left
		double x0 = getPointX(length - 2);
		double x1 = getPointX(length - 1);
		double x2;
		for (int i = 0 ; i < length ; i++){
			x2 = getPointX(i);
			if (Kernel.isGreater(x1, x0) && Kernel.isGreater(x1, x2)){
				App.debug((i-1)+" : both left");
			}else if (Kernel.isGreater(x0, x1) && Kernel.isGreater(x2, x1)){
				App.debug((i-1)+" : both right");
			}
			x0 = x1;
			x1 = x2;
		}
		*/
		
		int[] sweepArray = new int[length];
		int index = 0;
		for (int i : sweepTree){
			sweepArray[index] = i;
			index++;
		}
		
		int min = sweepArray[0];
		int max = sweepArray[length - 1];
		
		boolean inverseMinMax = false;
		if (min > max){
			int tmp = min;
			min = max;
			max = tmp;
			inverseMinMax = true;
		}	
		//App.debug(min+","+max);
		
		//check if top chain is indices between min and max or not
		int minN1 = (min + 1) % length;
		int minN2 = (min - 1) % length;
		Point pMin = getPoint(min);
		Point pMinN1 = getPoint(minN1);
		Point pMinN2 = getPoint(minN2);
		boolean topBetween = inverseMinMax ^ Kernel.isGreater((pMinN2.x-pMin.x)*(pMinN1.y-pMin.y), (pMinN1.x-pMin.x)*(pMinN2.y-pMin.y));
		//App.error(""+topBetween);

		
		// init stack
		Stack<Integer> stack = new Stack<Integer>();
		stack.push(sweepArray[0]);
		stack.push(sweepArray[1]);
		
		// loop
		for (int i = 2 ; i < length ; i++){
			ArrayList<Integer> currentTriangleFan = new ArrayList<Integer>();
			int top = stack.peek();
			int vi = sweepArray[i];
			boolean viBetween = vi > min && vi < max;
			//debugDiagonal("(vi > min && vi < max) , (top > min && top < max) : "+(vi > min && vi < max)+","+(top > min && top < max),vi,top);
			if (viBetween ^ (top > min && top < max)){ // vi and top are not on the same chain
				//debugDiagonal("case 2 ",top,vi);
				currentTriangleFan.add(vi);
				while (!stack.isEmpty()){
					int v = stack.pop();
					currentTriangleFan.add(v);
					//debugDiagonal("diagonal : ",vi,v);
				}
				stack.push(top);
				stack.push(vi);
				
			}else{ // vi and top are on the same chain
				//debugDiagonal("case 1 ",top,vi);
				Point pi = getPoint(vi);
				
				currentTriangleFan.add(vi);
				
				// first correct point
				int vk = stack.pop();
				currentTriangleFan.add(vk);
				//debugDiagonal("diagonal : ",vi,vk);
				Point pk = getPoint(vk);
				double dx2 = pk.x - pi.x;
				double dy2 = pk.y - pi.y;
				
				boolean go = true;
				while (!stack.isEmpty() && go){
					double dx1 = dx2;
					double dy1 = dy2;
					int v = stack.pop();
					Point pv = getPoint(v);
					dx2 = pv.x - pi.x;
					dy2 = pv.y - pi.y;
					if (Kernel.isGreater(dx1*dy2, dx2*dy1) ^ (viBetween ^ !topBetween)){ // not same orientation
						stack.push(v); //re-push v in stack
						go = false;
					}else{
						vk = v;
						currentTriangleFan.add(vk);
						//debugDiagonal("diagonal : ",vi,vk);
					}
				}
				stack.push(vk);
				stack.push(vi);
			}
			
			if (currentTriangleFan.size()>2){ // add fan only if at least 3 points
				ret.add(currentTriangleFan);
			}
			
		}
		
		return ret;
	}
	
	
}
