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
		public double orientationToNext;
		Point prev, next; // previous and next point

		TreeSet<Segment> toRight, toLeft;

		public Point(double x, double y){
			this.x = x;
			this.y = y;
		}

		public void removeSegmentToRight(Segment segment){
			toRight.remove(segment);
		}


		public boolean addSegmentToRight(Segment segment){
			if (toRight == null){
				toRight = new TreeSet<Segment>();
			}
			return toRight.add(segment);
		}

		public void removeSegmentToLeft(Segment segment){
			toLeft.remove(segment);
		}


		public void addSegmentToLeft(Segment segment){
			if (toLeft == null){
				toLeft = new TreeSet<Segment>();
			}
			toLeft.add(segment);
		}

		public boolean hasNoSegment(){
			return (toLeft == null || toLeft.isEmpty()) && (toRight == null || toRight.isEmpty()); 
		}

		final public int compareTo(Point p2) {

			if (id == p2.id){
				return 0;
			}

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


			if (toRight != null){
				if (p2.toRight == null){
					p2.toRight = new TreeSet<Segment>();
				}
				for (Segment seg : toRight){
					seg.leftPoint = p2;
					p2.toRight.add(seg);
					cutAfterComparison(seg);
				}
			}

			if (toLeft != null){
				if (p2.toLeft == null){
					p2.toLeft = new TreeSet<Segment>();
				}
				for (Segment seg : toLeft){
					seg.rightPoint = p2;
					p2.toLeft.add(seg);
					/*
					if (comparedSameOrientationSegment!=null){
						App.debug(seg+","+comparedSameOrientationSegment+" : "+comparedSameOrientationValue);
					}
					*/
				}
			}


			return 0;
		}


		final public int compareToOnly(Point p2) {

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

			return 0;

		}
	}
	
	protected Segment comparedSameOrientationSegment;
	protected int comparedSameOrientationValue;

	
	private class Segment implements Comparable<Segment>{
		double orientation;
		Point leftPoint, rightPoint;
		Segment above, below;
		Segment next;
		
		int usable;
				

		// equation vector
		double x, y, z;
		private boolean equationNeedsUpdate = true;

		public Segment(){
			// dummy constructor
		}

		public boolean isDummy(){
			return leftPoint == null;
		}

		public Segment(double orientation, Point leftPoint, Point rightPoint){
			this.orientation = orientation;
			this.leftPoint = leftPoint;
			this.rightPoint = rightPoint;
			
			// first usable once
			usable = 1;
		}


		public void setEquation(){
			if (equationNeedsUpdate){
				y =  rightPoint.x - leftPoint.x;
				x = -rightPoint.y + leftPoint.y;
				z = -x * rightPoint.x - y * rightPoint.y; 
				equationNeedsUpdate = false;
			}
		}

		public void equationNeedsUpdate(){
			equationNeedsUpdate = true;
		}

		@Override
		public String toString(){
			if (leftPoint != null){
				return leftPoint.name+rightPoint.name;
			}

			return "dummy";
		}

		/**
		 * remove this segment from left and right points
		 */
		public void removeFromPoints(){
			leftPoint.removeSegmentToRight(this);
			rightPoint.removeSegmentToLeft(this);
		}
		
		
		/**
		 * add this segment to left and right points
		 */
		public void addToPoints(){
			if (leftPoint == rightPoint){
				App.printStacktrace("ICI : "+leftPoint.name);
			}
			leftPoint.addSegmentToRight(this);
			rightPoint.addSegmentToLeft(this);
		}
		

		public int compareTo(Segment seg) {
			
			if (this==seg){
				return 0;
			}

			if (Kernel.isGreater(seg.orientation, orientation)){
				return -1;
			}

			if (Kernel.isGreater(orientation, seg.orientation)){
				return 1;
			}


			comparedSameOrientationSegment = seg;
			comparedSameOrientationValue = rightPoint.compareToOnly(seg.rightPoint);			
			//App.error(this+","+seg+" : "+c);		
			/*
			if (c > 0){ 
				seg.rightPoint.removeSegmentToLeft(seg);
				rightPoint.addSegmentToLeft(seg);
				seg.rightPoint = rightPoint;
			}else{			
				rightPoint.removeSegmentToLeft(this);
			}
				*/		
						
						
			
			// same orientation : check next point id
			if (rightPoint.id < seg.rightPoint.id){
				return -1;
			}

			if (rightPoint.id > seg.rightPoint.id){
				return 1;
			}
			
			/*
			// same right point : augment usability
			if (rightPoint.id == seg.rightPoint.id){
				seg.usable += usable/2; // usable is always multiple of 2, and will be add twice (from left and from right)
				App.debug(seg+": "+seg.usable);
			}
			*/
			
			
			

			// same ptp
			return 0;
		}
	}



	private GeoPolygon polygon;

	private int maxPointIndex;

	private Point firstPoint;

	/**
	 * Constructor
	 * @param p polygon
	 */
	public PolygonTriangulation(GeoPolygon p){
		this.polygon = p;
	}

	/**
	 * set point id
	 * @param point
	 * @param i
	 */
	private void setId(Point point, int i){
		point.id = i;
		point.name = ((GeoElement) polygon.getPointsND()[i]).getLabelSimple();
	}

	/**
	 * update points list
	 */
	public void updatePoints(){

		maxPointIndex = polygon.getPointsLength();

		// feed the list with no successively equal points
		Point point = new Point(polygon.getPointX(0), polygon.getPointY(0));
		setId(point, 0);
		firstPoint = point;
		int n = 1;
		for (int i = 0; i < polygon.getPointsLength(); i++){
			double x1 = polygon.getPointX(i); 
			double y1 = polygon.getPointY(i);
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
					// set correct orientation
					//App.error(prevPoint.orientationToNext*180/Math.PI+"/"+nextPoint.orientationToNext*180/Math.PI);
					prevPoint.orientationToNext = nextPoint.orientationToNext;
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
					// next point is in same direction as old point
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
	
	
	private void cut(Segment segment, Point pt){
		// cut the segment
		segment.removeFromPoints();
		Segment segment2 = new Segment(segment.orientation, pt, segment.rightPoint);
		segment2.usable = segment.usable;
		segment.rightPoint = pt;		
		segment.addToPoints();
		comparedSameOrientationSegment = null;
		segment2.addToPoints();
		cutAfterComparison(segment2);
	}
	
	
	protected void cutAfterComparison(Segment segment2){
		if (comparedSameOrientationSegment!=null){
			App.debug(segment2+","+comparedSameOrientationSegment+" : "+comparedSameOrientationValue);
			if (comparedSameOrientationValue < 0){
				//remove segment2 and segment2 part from comparedSameOrientationSegment		
				Segment s = comparedSameOrientationSegment;
				comparedSameOrientationSegment = null;				
				s.removeFromPoints();
				s.leftPoint = segment2.rightPoint;
				segment2.removeFromPoints();
				comparedSameOrientationSegment = null;
				s.addToPoints();
				cutAfterComparison(s);
			}else if (comparedSameOrientationValue > 0){
				//remove comparedSameOrientationSegment and comparedSameOrientationSegment part from segment2
				Segment s = comparedSameOrientationSegment;
				comparedSameOrientationSegment = null;	
				segment2.removeFromPoints();
				segment2.leftPoint = s.rightPoint;
				s.removeFromPoints();							
				comparedSameOrientationSegment = null;
				segment2.addToPoints();
				cutAfterComparison(segment2);
			}else{
				// same segment : can remove it
				comparedSameOrientationSegment = null;
				segment2.removeFromPoints();
			}
		}
	}

	/**
	 * set intersections
	 */
	public void setIntersections(){

		// create segments
		Point point;
		for (point = firstPoint; point.next != firstPoint; point = point.next){
			createSegment(point);
		}
		createSegment(point);

		// store all points in sweep order
		TreeSet<Point> pointSet = new TreeSet<Point>();

		for (point = firstPoint; point.next != firstPoint; point = point.next){
			pointSet.add(point);
		}
		pointSet.add(point);


		String s = "";
		for (Point pt : pointSet){
			s+="\n"+pt.name;
			if (pt.toRight != null){
				s+="\nto right : ";
				for (Segment segment : pt.toRight){
					s+=((int) (segment.orientation*180/Math.PI))+"°:"+segment.rightPoint.name+", ";
				}
			}
			if (pt.toLeft != null){
				s+="\nto left : ";
				for (Segment segment : pt.toLeft){
					s+=((int) (segment.orientation*180/Math.PI))+"°:"+segment.leftPoint.name+", ";
				}
			}
		}
		App.debug(s);


		// top and bottom (dummy) segments
		Segment top = new Segment();
		Segment bottom = new Segment();
		bottom.above = top;
		top.below = bottom;

		for (Point pt = pointSet.first() ; pt != pointSet.last() ; pt = pointSet.higher(pt) ){
			s=pt.name+" : ";
			Segment above = null;
			Segment below = null;

			App.debug(s);
			

			
			// remove to-left segments
			if (pt.toLeft!=null && !pt.toLeft.isEmpty()){ // will put to-right segments in place of to-left segments
				App.error(pt.toLeft.first()+"/"+pt.toLeft.last());
				above = pt.toLeft.first().above;
				below = pt.toLeft.last().below;
				below.above = above;
				above.below = below;
				checkIntersection(below, above, pointSet);
				
				
				// check if new point is aligned with existing segment			
				boolean go = true;
				for (Segment segment = bottom.above ; segment != top && go ; segment = segment.above){
					double orientation = Math.atan2(pt.y - segment.leftPoint.y, pt.x - segment.leftPoint.x);
					//App.error(segment.leftPoint.name+pt.name+" : "+orientation);
					if (Kernel.isEqual(orientation, segment.orientation)){
						App.error("(1)"+pt.name+" aligned with "+segment);
						
						// cut the segment
						cut(segment, pt);
						
						// remove new left segment
						above = segment.above;
						below = segment.below;
						below.above = above;
						above.below = below;
						segment = below;
												
					}else if (orientation < segment.orientation){
						go = false;
						
					}
				}
				
			}else{ // search for the correct place for to-right segments
				//App.error(pt.name);
				boolean go = true;
				for (Segment segment = bottom.above ; segment != top && go ; segment = segment.above){
					//App.error(segment.leftPoint.name+segment.rightPoint.name+" : "+segment.orientation);
					double orientation = Math.atan2(pt.y - segment.leftPoint.y, pt.x - segment.leftPoint.x);
					//App.error(segment.leftPoint.name+pt.name+" : "+orientation);
					if (Kernel.isEqual(orientation, segment.orientation)){
						App.error("(2)"+pt.name+" aligned with "+segment);
						
						// cut the segment
						cut(segment, pt);
						
						// remove new left segment
						above = segment.above;
						below = segment.below;
						below.above = above;
						above.below = below;
						
						//go = false;
						
					}else if (orientation < segment.orientation){ // found the place
						go = false;
						above = segment;
						below = above.below;
						App.error(below+"<"+pt.name+"<"+above);
					}		
				}
				if (go){ // when there are no segment between top and bottom
					above = top;
					below = above.below;
				}
			}
			

			// put to-right segments
			if (pt.toRight!=null){
				Segment oldBelow = below;
				for (Segment seg : pt.toRight){
					below.above = seg;
					seg.below = below;
					below = seg;
				}
				below.above = above;
				above.below = below;
				checkIntersection(oldBelow, oldBelow.above, pointSet);
				checkIntersection(below, below.above, pointSet);

			}


			for (Segment seg = bottom.above ; seg != top; seg = seg.above){
				s+=seg.toString()+",";
			}
			App.debug(s);
		}




		

		//if (1==1){ return; }
		
		App.debug("=========== non self-intersecting polygons ==============");

		while (!pointSet.isEmpty()){
			App.debug("=========================");
			Point start = pointSet.first();
			Point currentPoint; 
			Point nextPoint = start;
			Segment segStart = start.toRight.first();
			Segment segment = segStart;
			Segment next = null;

			running = Running.RIGHT;

			while (running != Running.STOP){
				currentPoint = nextPoint;
				//App.debug(segment+"");
				if (running == Running.RIGHT){
					nextPoint = segment.rightPoint;
					if (nextPoint == start){
						running = Running.STOP;
					}else{
						next = nextPoint.toLeft.lower(segment);
						if (next == null){
							if (nextPoint.toRight != null && !nextPoint.toRight.isEmpty()){
								next = nextPoint.toRight.last();
							}
							if (next == null){ // no to-right segment
								next = nextPoint.toLeft.higher(segment);
								running = Running.LEFT;
							}
						}else{
							running = Running.LEFT;
						}

					}
				}else{ // running == Running.LEFT
					nextPoint = segment.leftPoint;
					if (nextPoint == start){
						running = Running.STOP;
					}else{
						next = nextPoint.toRight.lower(segment);
						if (next == null){
							if (nextPoint.toLeft != null && !nextPoint.toLeft.isEmpty()){
								next = nextPoint.toLeft.last();
							}
							if (next == null){ // no to-left segment
								next = nextPoint.toRight.higher(segment);
								running = Running.RIGHT;
							}
						}else{
							running = Running.RIGHT;
						}

					}
				}

				// remove this segment from left and right points if not usable anymore
				segment.usable -- ;
				if (segment.usable == 0){
					segment.removeFromPoints();	
				}
				segment = next;

				if (currentPoint.hasNoSegment()){
					App.debug(currentPoint.name+" : remove");
					pointSet.remove(currentPoint);
				}else{
					App.debug(currentPoint.name+" : keep");
				}
			}

			// remove this segment from left and right points
			segment.removeFromPoints();
			if (start.hasNoSegment()){
				App.debug(start.name+" : remove");
				pointSet.remove(start);
			}else{
				App.debug(start.name+" : keep");
			}
		}



	}

	private enum Running  { RIGHT, LEFT, STOP };
	private Running running;



	final private void checkIntersection(Segment a, Segment b, TreeSet<Point> pointSet){

		//App.error("check intersection : "+a+"-"+b);

		if (a.isDummy() || b.isDummy()){
			return;
		}


		if (a.rightPoint == b.rightPoint){
			return;
		}		

		// ensure a and b have correct equation
		a.setEquation();
		b.setEquation();

		// calculate possible intersection point
		double x = a.y * b.z - a.z * b.y;
		double y = a.z * b.x - a.x * b.z;
		double z = a.x * b.y - a.y * b.x;

		//App.debug(x+","+y+","+z);

		if (!Kernel.isZero(z)){
			// create intersection point
			Point pt = new Point(x/z, y/z);

			
			// check intersection point is inside segments		
			int al, ar, bl, br;
			if ((al = pt.compareToOnly(a.leftPoint)) < 0 
					|| (ar = pt.compareToOnly(a.rightPoint)) > 0 
					|| (bl = pt.compareToOnly(b.leftPoint)) < 0 
					|| (br = pt.compareToOnly(b.rightPoint)) > 0){
				// point outside the segments : no intersection
				
				
			}else if(al == 0){ // happen only after some aligned points and a.leftPoint is current point in sweep line
				/*
				pt = a.leftPoint;
				App.debug("al : "+pt.name);
				
				
				// remove segment b from left point
				b.leftPoint.removeSegmentToRight(b);
				
				// new left point for b
				b.leftPoint = pt;
				
				// add segment b to new left point
				pt.addSegmentToRight(b);
				*/
			}else if(ar == 0){
				pt = a.rightPoint;
				App.error("ar : "+pt.name);
				
				cut(b,pt);
				
				/*
				// remove old segments from old right point
				b.rightPoint.removeSegmentToLeft(b);

				pt.addSegmentToLeft(b);

				// create new segment
				Segment b2 = new Segment(b.orientation, pt, b.rightPoint);
				if(pt.addSegmentToRight(b2)){ // if orientation already exists don't add the new half-segment
					b.rightPoint.addSegmentToLeft(b2);
				}

				// set old segment right point
				b.rightPoint = pt;
				*/
				
			}else if(bl == 0){ // happen only after some aligned points and b.leftPoint is current point in sweep line
				/*
				pt = b.leftPoint;
				App.debug("bl : "+pt.name+" "+a+"/"+b);
				
				
				// remove segment a from left point
				a.leftPoint.removeSegmentToRight(a);
				
				// new left point for a
				a.leftPoint = pt;
				
				// add segment a to new left point
				pt.addSegmentToRight(a);
				
				*/
			}else if(br == 0){
				pt = b.rightPoint;
				App.error("br : "+pt.name);
					
				
				cut(a,pt);
				
				/*
				// remove old segments from old right point
				a.rightPoint.removeSegmentToLeft(a);

				pt.addSegmentToLeft(a);

				// create new segment
				Segment a2 = new Segment(a.orientation, pt, a.rightPoint);
				if(pt.addSegmentToRight(a2)){ // if orientation already exists don't add the new half-segment
					a.rightPoint.addSegmentToLeft(a2);
				}

				// set old segment right point
				a.rightPoint = pt;
				*/
			}else{ // point strictly inside the segments 	
			
			/*
			// check if point is strictly inside the segments 
			if (pt.compareToOnly(a.leftPoint) > 0 
					&& pt.compareToOnly(a.rightPoint) < 0 
					&& pt.compareToOnly(b.leftPoint) > 0 
					&& pt.compareToOnly(b.rightPoint) < 0){
				
			*/
				pt.id = maxPointIndex;
				pt.name = Integer.toString(pt.id);
				maxPointIndex++;

				App.debug("inter : "+pt.name+" : "+pt.x+","+pt.y);

				// remove old segments 
				a.removeFromPoints();
				b.removeFromPoints();

				// create new segments
				Segment a2 = new Segment(a.orientation, pt, a.rightPoint);
				Segment b2 = new Segment(b.orientation, pt, b.rightPoint);
				a2.addToPoints();
				b2.addToPoints();
				a2.usable = a.usable;
				b2.usable = b.usable;

				// set old segments right point
				a.rightPoint = pt;
				b.rightPoint = pt;
				
				// re-add old segments (with correct right points)
				a.addToPoints();
				b.addToPoints();

				// says that old segments need an update for equation
				//a.equationNeedsUpdate();
				//b.equationNeedsUpdate();
				
				// add point to set
				pointSet.add(pt);
			}
		}
		
	}



	final private void createSegment(Point point){
		
		App.debug(point.name+", "+((int) (point.orientationToNext*180/Math.PI))+"°, "+point.next.name);
		Segment segment;
		if (Kernel.isGreater(point.orientationToNext, -Math.PI/2) && Kernel.isGreaterEqual(Math.PI/2,point.orientationToNext)){ // point is left point
			segment = new Segment(point.orientationToNext, point, point.next);
		}else{ // point is right point
			segment = new Segment(getReverseOrientation(point.orientationToNext), point.next, point);			
		}
		
		segment.addToPoints();
	}

	final static private double getReverseOrientation(double orientation){
		if (orientation > 0){
			return orientation - Math.PI;
		}

		return orientation + Math.PI;

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
