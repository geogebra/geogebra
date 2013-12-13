package geogebra3D.euclidian3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.App;

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
		public double orientation;
		Point left, right;
		
		public Point(double x, double y){
			this.x = x;
			this.y = y;
		}
	}

	private GeoPolygon p;
	
	
	/**
	 * Constructor
	 * @param p polygon
	 */
	public PolygonTriangulation(GeoPolygon p){
		this.p = p;
	}
	
	/**
	 * @deprecated used only for debug
	 * @param point
	 * @param i
	 */
	private void setName(Point point, int i){
		point.name = ((GeoElement) p.getPointsND()[i]).getLabelSimple();
	}
	
	/**
	 * update points list
	 */
	public void updatePoints(){
		
		// feed the list with no successively equal points
		Point point = new Point(p.getPointX(0), p.getPointY(0));
		setName(point, 0);
		Point firstPoint = point;
		int n = 1;
		for (int i = 0; i < p.getPointsLength(); i++){
			double x1 = p.getPointX(i); 
			double y1 = p.getPointY(i);
			if (!Kernel.isEqual(point.x, x1) || !Kernel.isEqual(point.y, y1)){
				point.right = new Point(x1, y1);
				setName(point.right, i);
				point.right.left = point;
				point = point.right;				
				n++;
			}
			
		}
		
		
		// check first point <> last point
		if (Kernel.isEqual(point.x, firstPoint.x) && Kernel.isEqual(point.y, firstPoint.y)){
			firstPoint = firstPoint.right;
			n--;
		}	
		point.right = firstPoint;
		firstPoint.left = point;

		
		
		// set orientations and remove flat points
		Point oldPoint = firstPoint;
		point = oldPoint.right;
		oldPoint.orientation = Math.atan2(point.y - oldPoint.y, point.x - oldPoint.x);
		
		for (int i = 0; i < n ; i++){
			Point nextPoint = point.right;
			point.orientation = Math.atan2(nextPoint.y - point.y, nextPoint.x - point.x);
			// delta orientation between 0 and 2pi
			double delta = point.orientation - oldPoint.orientation; 
			if (delta < 0){
				delta += 2*Math.PI;
			}
			App.debug(oldPoint.name+"/"+point.name+"/"+nextPoint.name+" : "+(delta*180/Math.PI));
			if (Kernel.isZero(delta)){ // point aligned				
				// right is next point
				oldPoint.right = nextPoint;
				nextPoint.left = oldPoint;
				point = nextPoint;
			}else if (Kernel.isEqual(delta, Math.PI)){ // U-turn
				App.debug("U-turn");
				if(Kernel.isEqual(nextPoint.x, oldPoint.x) && Kernel.isEqual(nextPoint.y, oldPoint.y)){
					// same point : ignore next, update orientation
					App.debug(oldPoint.name+"=="+nextPoint.name);
					/*
					nextPoint = nextPoint.right;
					oldPoint.orientation = Math.atan2(nextPoint.y - oldPoint.y, nextPoint.x - oldPoint.x);	
					*/
					// right is next point
					oldPoint.right = nextPoint;
					nextPoint.left = oldPoint;
					point = nextPoint;
				}else if (Kernel.isGreater(0, (nextPoint.x - oldPoint.x)*(point.x - oldPoint.x) + (nextPoint.y - oldPoint.y)*(point.y - oldPoint.y))){
					// next point is back old point
					App.debug(" next point is back old point - "+(oldPoint.orientation*180/Math.PI));
					if (oldPoint.orientation > 0){
						oldPoint.orientation -= Math.PI;
					}else{
						oldPoint.orientation += Math.PI;
					}
					// right is next point
					oldPoint.right = nextPoint;
					nextPoint.left = oldPoint;
					point = nextPoint;
				}else{
					// right is next point
					oldPoint.right = nextPoint;
					nextPoint.left = oldPoint;
					point = nextPoint;
				}
				
			}else{
				oldPoint = point;	
				point = nextPoint;
			}
			
			

			
		}
		
		
		/*
		ArrayList<Point> noFlatPoints = new ArrayList<Point>();
		
		Point oldPoint = pointsList.get(n-2);
		point = pointsList.get(n-1);
		oldPoint.orientation = Math.atan2(point.y - oldPoint.y, point.x - oldPoint.x);
		
		for (int i = 0; i < n ; i++){
			Point nextPoint = pointsList.get(i);
			point.orientation = Math.atan2(nextPoint.y - point.y, nextPoint.x - point.x);
			// delta orientation between 0 and 2pi
			double delta = point.orientation - oldPoint.orientation; 
			if (delta < 0){
				delta += 2*Math.PI;
			}
			App.debug(oldPoint.name+"/"+point.name+"/"+nextPoint.name+" : "+(delta*180/Math.PI));
			if (Kernel.isZero(delta)){ // point aligned				
				// no need to change oldPoint 
			}else if (Kernel.isEqual(delta, Math.PI)){ // U-turn
				App.debug("U-turn");
				if(Kernel.isEqual(nextPoint.x, oldPoint.x) && Kernel.isEqual(nextPoint.y, oldPoint.y)){
					// same point : ignore next, update orientation
					App.debug(oldPoint.name+"=="+nextPoint.name);
					i++;
					if (i < n){ // check if there are still points
						nextPoint = pointsList.get(i);
						oldPoint.orientation = Math.atan2(nextPoint.y - oldPoint.y, nextPoint.x - oldPoint.x);
					}else{
						App.debug("last");
						if(noFlatPoints.get(0) == nextPoint){
							App.debug("remove");
							oldPoint.orientation = nextPoint.orientation;
							noFlatPoints.remove(0);
							//nextPoint = noFlatPoints.get(0);
							//oldPoint.orientation = Math.atan2(nextPoint.y - oldPoint.y, nextPoint.x - oldPoint.x);
						}
						
					}
				}else if (Kernel.isGreater(0, (nextPoint.x - oldPoint.x)*(point.x - oldPoint.x) + (nextPoint.y - oldPoint.y)*(point.y - oldPoint.y))){
					// next point is back old point
					App.debug(" next point is back old point - "+(oldPoint.orientation*180/Math.PI));
					if (oldPoint.orientation > 0){
						oldPoint.orientation -= Math.PI;
					}else{
						oldPoint.orientation += Math.PI;
					}
				}
			}else{
				noFlatPoints.add(point);
				oldPoint = point;			
			}
			
			point = nextPoint;

			
		}
		
		
		pointsList = noFlatPoints;

		if (Kernel.isEqual(point.x, x0) && Kernel.isEqual(point.y, y0)){
			pointsList.remove(pointsList.size()-1);
		}
		*/
		
		/*
		String s = "";
		for (int i = 0 ; i < pointsList.size() ; i++){
			s+="\n"+pointsList.get(i).name+"("+(pointsList.get(i).orientation*180/Math.PI)+"°), ";
		}
		App.debug(s);
		*/
		
		String s = "========";
		point = firstPoint;
		for (point = firstPoint; point.right != firstPoint; point = point.right){
			s+="\n"+point.name+"("+(point.orientation*180/Math.PI)+"°), ";
		}
		s+="\n"+point.name+"("+(point.orientation*180/Math.PI)+"°) **";
		App.debug(s);
		
		
		s = "========";
		point = firstPoint;
		for (point = firstPoint; point.left != firstPoint; point = point.left){
			s+="\n"+point.name+"("+(point.orientation*180/Math.PI)+"°), ";
		}
		s+="\n"+point.name+"("+(point.orientation*180/Math.PI)+"°) **";
		App.debug(s);


		
	}
	
	final private Point getPoint(int i){
		return null;
	}
	

	private int getPointsLength(){
		return 0;
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
