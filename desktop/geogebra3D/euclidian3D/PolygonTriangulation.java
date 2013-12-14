package geogebra3D.euclidian3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Class to convert a GeoPolygon to a set of triangles
 * @author mathieu
 *
 */
public class PolygonTriangulation {
	
	private class Point implements Comparable<Point>{
		public double x, y;
		public int id;
		public String name;
		public double orientationToPrev, orientationToNext;
		Point prev, next; // previous and next point
		
		TreeSet<PointToPoint> ptpSet;
		
		public Point(double x, double y){
			this.x = x;
			this.y = y;
		}
		
		public void addPointToPoint(double orientation, Point nextPoint){
			if (ptpSet == null){
				ptpSet = new TreeSet<PointToPoint>();
			}
			
			ptpSet.add(new PointToPoint(orientation, nextPoint));
		}

		final public int compareTo(Point p2) {
			
			
			
			// smallest x
			if (Kernel.isGreater(p2.x, x)){
				return -1;
			}			
			if (Kernel.isGreater(x, p2.x)){
				return 1;
			}
			
			// then smallest y
			if (Kernel.isGreater(p2.y, y)){
				return -1;
			}			
			if (Kernel.isGreater(y, p2.y)){
				return 1;
			}
						
			// same point : add all point-to-point set to existing point
			App.error(this.name+"=="+p2.name);			
			p2.ptpSet.addAll(ptpSet);
			return 0;
		}
	}
	
	
	private class PointToPoint implements Comparable<PointToPoint>{
		double orientation;
		Point nextPoint;
		Segment segment;
		
		public PointToPoint(double orientation, Point nextPoint){
			this.orientation = orientation;
			this.nextPoint = nextPoint;
		}
		
		public int compareTo(PointToPoint ptp) {
			
			if (Kernel.isGreater(ptp.orientation, orientation)){
				return -1;
			}
			
			if (Kernel.isGreater(orientation, ptp.orientation)){
				return 1;
			}
			
			// same orientation : check next point id
			if (nextPoint.id < ptp.nextPoint.id){
				return -1;
			}
			
			if (nextPoint.id > ptp.nextPoint.id){
				return 1;
			}
			
			// same ptp
			return 0;
		}
	}
	
	
	private class Segment{
		Segment next, previous;
	}

	private GeoPolygon p;
	
	private Point firstPoint;
	
	/**
	 * Constructor
	 * @param p polygon
	 */
	public PolygonTriangulation(GeoPolygon p){
		this.p = p;
	}
	
	/**
	 * set point id
	 * @param point
	 * @param i
	 */
	private void setId(Point point, int i){
		point.id = i;
		point.name = ((GeoElement) p.getPointsND()[i]).getLabelSimple();
	}
	
	/**
	 * update points list
	 */
	public void updatePoints(){
		
		// feed the list with no successively equal points
		Point point = new Point(p.getPointX(0), p.getPointY(0));
		setId(point, 0);
		firstPoint = point;
		int n = 1;
		for (int i = 0; i < p.getPointsLength(); i++){
			double x1 = p.getPointX(i); 
			double y1 = p.getPointY(i);
			if (!Kernel.isEqual(point.x, x1) || !Kernel.isEqual(point.y, y1)){
				point.next = new Point(x1, y1);
				setId(point.next, i);
				point.next.prev = point;
				point = point.next;				
				n++;
			}
			
		}
		
		
		// check first point <> last point
		if (Kernel.isEqual(point.x, firstPoint.x) && Kernel.isEqual(point.y, firstPoint.y)){
			firstPoint = firstPoint.next;
			n--;
		}	
		point.next = firstPoint;
		firstPoint.prev = point;

		
		
		// set orientations and remove flat points
		Point prevPoint = firstPoint;
		point = prevPoint.next;
		prevPoint.orientationToNext = Math.atan2(point.y - prevPoint.y, point.x - prevPoint.x);
		
		int removedPoints = 0;
		for (int i = 0; i < n && removedPoints < n-1 ; i++){ 
			// make it n times since at each step :
			// * we remove 1 point and go on
			// * we remove 2 points and go back
			// * we go on
			// so each point is visited at least once
			Point nextPoint = point.next;
			point.orientationToNext = Math.atan2(nextPoint.y - point.y, nextPoint.x - point.x);
			// delta orientation between 0 and 2pi
			double delta = point.orientationToNext - prevPoint.orientationToNext; 
			if (delta < 0){
				delta += 2*Math.PI;
			}
			App.debug(prevPoint.name+"/"+point.name+"/"+nextPoint.name+" : "+(delta*180/Math.PI));
			if (Kernel.isZero(delta)){ // point aligned				
				// remove point
				prevPoint.next = nextPoint;
				nextPoint.prev = prevPoint;
				removedPoints++;
				point = nextPoint;
			}else if (Kernel.isEqual(delta, Math.PI)){ // U-turn
				App.debug("U-turn");
				if(Kernel.isEqual(nextPoint.x, prevPoint.x) && Kernel.isEqual(nextPoint.y, prevPoint.y)){
					// same point
					App.debug(prevPoint.name+"=="+nextPoint.name);
					// go back
					point = prevPoint; 
					prevPoint = prevPoint.prev;
					// remove point and nextPoint
					point.next = nextPoint.next;
					nextPoint.next.prev = point;
					removedPoints += 2;
				}else if (Kernel.isGreater(0, (nextPoint.x - prevPoint.x)*(point.x - prevPoint.x) + (nextPoint.y - prevPoint.y)*(point.y - prevPoint.y))){
					// next point is back old point
					App.debug(" next point is back old point - "+(prevPoint.orientationToNext*180/Math.PI));
					if (prevPoint.orientationToNext > 0){
						prevPoint.orientationToNext -= Math.PI;
					}else{
						prevPoint.orientationToNext += Math.PI;
					}
					// remove point
					prevPoint.next = nextPoint;
					nextPoint.prev = prevPoint;
					removedPoints++;
					point = nextPoint;
				}else{
					// remove point
					prevPoint.next = nextPoint;
					nextPoint.prev = prevPoint;
					point = nextPoint;
				}
				
			}else{
				prevPoint = point;	
				point = nextPoint;
			}
			
		}
		
		
		firstPoint = point; // in case old firstPoint has been removed
		
		String s = "";
		for (point = firstPoint; point.next != firstPoint; point = point.next){
			s+=point.name+"("+(point.orientationToNext*180/Math.PI)+"°), ";
		}
		s+=point.name+"("+(point.orientationToNext*180/Math.PI)+"°)";
		App.debug(s);
		
		
	}
	
	final private Point getPoint(int i){
		return null;
	}
	

	private int getPointsLength(){
		return 0;
	}
	
	
	//////////////////////////////////////
	// INTERSECTIONS
	//////////////////////////////////////
	
	/**
	 * set intersections
	 */
	public void setIntersections(){
		
		// store all points in sweep order
		TreeSet<Point> pointSet = new TreeSet<Point>();
		
		Point point;
		for (point = firstPoint; point.next != firstPoint; point = point.next){
			setPointToPoint(point);
			pointSet.add(point);
		}
		setPointToPoint(point);
		pointSet.add(point);
		
		
		String s = "";
		for (Point pt : pointSet){
			s+=pt.name+"|";
			for (PointToPoint ptp : pt.ptpSet){
				s+=((int) (ptp.orientation*180/Math.PI))+"°:"+ptp.nextPoint.name+"|";
			}
			s+=" - ";
		}
		App.debug(s);
		
		
		/*
		for (Point pt : pointSet){
			App.debug(pt.name+"--"+pt.next.name+"/"+pt.prev.name);
			SortedSet<Point> tail = pointSet.tailSet(pt, false);
			s = "";
			for (Point pt2 : tail){
				s+=pt2.name+",";
			}
			App.debug(s);
		}
		*/
		
		/*
		firstPoint = pointSet.first();
		setOrientationToPrev(firstPoint);
		setOrientationToPrev(firstPoint.next);
		*/
		
	}
	
	final static private double getReverseOrientation(double orientation){
		if (orientation > 0){
			return orientation - Math.PI;
		}
		
		return orientation + Math.PI;

	}


	
	private void setPointToPoint(Point point){
		App.debug(point.name+"-"+point.prev.name+"/"+point.next.name);
		point.addPointToPoint(point.orientationToNext, point.next);
		point.addPointToPoint(getReverseOrientation(point.prev.orientationToNext), point.prev);
		
		point.prev.addPointToPoint(point.prev.orientationToNext, point);
		
		point.next.addPointToPoint(getReverseOrientation(point.orientationToNext), point);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	/**
	 * 
	 * @return list of list of vertex indices, which forms triangle fans tessalating the polygon
	 */
	public ArrayList<ArrayList<Integer>> getTriangulation(){
		ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
		
		/*
		final int length = getPointsLength();
		
		// put 2D indices in sweep order (smaller x, then smaller y)
		TreeSet<Integer> sweepTree = new TreeSet<Integer>(this);
		for (int i = 0; i < length; i++){
			sweepTree.add(i);
		}
		
		
		
		
		
		
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
		*/
		return ret;
	}
	
	
}
