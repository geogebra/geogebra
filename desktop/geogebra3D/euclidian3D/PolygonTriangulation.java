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

	private GeoPolygon p;
	
	/**
	 * Constructor
	 * @param p polygon
	 */
	public PolygonTriangulation(GeoPolygon p){
		this.p = p;
	}
	
	
	private double getPointX(int i){
		return p.getPointX(i);
	}
	
	private double getPointY(int i){
		return p.getPointY(i);
	}
	
	private int getPointsLength(){
		return p.getPointsLength();
	}

	//////////////////////////////////////
	// COMPARATOR
	//////////////////////////////////////
	
	public int compare(Integer i1, Integer i2) {
		
		// smallest x
		double x1 = getPointX(i1);
		double x2 = getPointX(i2);
		if (Kernel.isGreater(x2, x1)){
			return -1;
		}			
		if (Kernel.isGreater(x1, x2)){
			return 1;
		}
		
		// then smallest y
		double y1 = getPointY(i1);
		double y2 = getPointY(i2);
		if (Kernel.isGreater(y2, y1)){
			return -1;
		}			
		if (Kernel.isGreater(y1, y2)){
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
		double xMin = getPointX(min);
		double yMin = getPointY(min);
		boolean topBetween = inverseMinMax ^ Kernel.isGreater((getPointX(minN2)-xMin)*(getPointY(minN1)-yMin), (getPointX(minN1)-xMin)*(getPointY(minN2)-yMin));
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
				double xi = getPointX(vi);
				double yi = getPointY(vi);
				
				currentTriangleFan.add(vi);
				
				// first correct point
				int vk = stack.pop();
				currentTriangleFan.add(vk);
				//debugDiagonal("diagonal : ",vi,vk);
				double dx2 = getPointX(vk) - xi;
				double dy2 = getPointY(vk) - yi;
				
				boolean go = true;
				while (!stack.isEmpty() && go){
					double dx1 = dx2;
					double dy1 = dy2;
					int v = stack.pop();
					dx2 = getPointX(v) - xi;
					dy2 = getPointY(v) - yi;
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
