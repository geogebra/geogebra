package geogebra3D.euclidian3D;

import geogebra.common.awt.GPoint2D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
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
		
		boolean needsDiagonal = false;
		
		public int usable = 1;

		public Point(double x, double y, int id){
			this.x = x;
			this.y = y;
			this.id = id;
		}
		
		@Override
		public Point clone(){
			Point ret = new Point(x,y,id);
			ret.name = name;
			return ret;
		}
		
		public String debugSegments(){
			String s = name+" ";
			if (toLeft != null){
				s+="/ to left : ";
				for (Segment segment : toLeft){
					s+=((int) (segment.orientation*180/Math.PI))+"°:"+segment.leftPoint.name+", ";
				}
			}
			if (toRight != null){
				
				s+="/ to right : ";
				for (Segment segment : toRight){
					s+=((int) (segment.orientation*180/Math.PI))+"°:"+segment.rightPoint.name+", ";
				}
			}
			
			return s;
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
		
		final public int compareTo(double x1, double y1){
			// smallest x
			if (Kernel.isGreater(x1, x)){
				return -1;
			}			
			if (Kernel.isGreater(x, x1)){
				return 1;
			}

			// then smallest y
			if (Kernel.isGreater(y1, y)){
				return -1;
			}			
			if (Kernel.isGreater(y, y1)){
				return 1;
			}

			return 0;
		}
		
		/**
		 * convert it to GPoint2D.Double
		 * @return (x,y) GPoint2D.Double
		 */
		public GPoint2D.Double toDouble(){
			return new GPoint2D.Double(x, y);
		}
	}
	
	protected Segment comparedSameOrientationSegment;
	protected int comparedSameOrientationValue;

	
	private class Segment implements Comparable<Segment>{
		double orientation;
		Point leftPoint, rightPoint;
		Segment above, below;
		Segment next;
		boolean isDiagonal;
		
		Running running = Running.STOP;
		
		

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
			this(leftPoint, rightPoint);
			this.orientation = orientation;
		}
		
		@Override
		public Segment clone(){
			return new Segment(orientation, leftPoint, rightPoint);
		}

		public Segment(Point leftPoint, Point rightPoint){
			this.leftPoint = leftPoint;
			this.rightPoint = rightPoint;
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
				/*
				if (running == Running.LEFT){
					return rightPoint.name+leftPoint.name;
				}
				*/
				return leftPoint.name+rightPoint.name;
			}
			return "dummy";
		}
		
		
		public Point getFirstPoint(){
			if (running == Running.LEFT){
				return rightPoint;
			}
			
			//running == Running.RIGHT
			return leftPoint;
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
	
	
	public class TriangleFan extends LinkedList<Integer>{
		
		private boolean isClockWise;		
		private int apex;
		
		private Iterator<Integer> iterator;
		
		/**
		 * 
		 * @param apex of the fan
		 * @param isClockWise orientation
		 */
		public TriangleFan(int apex, boolean isClockWise){
			this.apex = apex;
			this.isClockWise = isClockWise;
		}

		/**
		 * 
		 * @return apex point and sets the iterator regarding clockwise/anti clockwise orientation
		 */
		public int getApexPoint() {
			if (isClockWise){
				iterator = this.descendingIterator();
			}else{
				iterator = this.iterator();
			}
			return apex;
		}
		
		/**
		 * 
		 * @return true if the iterator has a next point
		 */
		public boolean hasNext(){
			return iterator.hasNext();
		}
		
		/**
		 * 
		 * @return next point
		 */
		public int next(){
			return iterator.next();
		}
	}

	

	private GeoPolygon polygon;

	private int maxPointIndex;

	private Point firstPoint;
	
	private ArrayList<TreeSet<Point>> polygonPointsList;
	
	private ArrayList<TriangleFan> fansList;
	
	private GPoint2D.Double[] pointsArray;


	/**
	 * Constructor
	 * @param p polygon
	 */
	public PolygonTriangulation(GeoPolygon p){
		this.polygon = p;
		
		polygonPointsList = new ArrayList<TreeSet<Point>>();
		fansList = new ArrayList<TriangleFan>();
	}

	/**
	 * set point id
	 * @param point
	 * @param i
	 */
	private void setName(Point point, int i){
		point.name = ((GeoElement) polygon.getPointsND()[i]).getLabelSimple();
	}

	/**
	 * update points list:
	 * creates a chain from firstPoint to next points ; 
	 * two consecutive points can't be equal ; 
	 * three consecutive points can't be aligned.
	 * For each point orientation to the next (angle about Ox) is stored.
	 */
	public void updatePoints(){

		maxPointIndex = polygon.getPointsLength();

		// feed the list with no successively equal points
		Point point = new Point(polygon.getPointX(0), polygon.getPointY(0), 0);
		setName(point, 0);
		firstPoint = point;
		int n = 1;
		for (int i = 0; i < polygon.getPointsLength(); i++){
			double x1 = polygon.getPointX(i); 
			double y1 = polygon.getPointY(i);
			if (!Kernel.isEqual(point.x, x1) || !Kernel.isEqual(point.y, y1)){
				point.next = new Point(x1, y1, i);
				setName(point.next, i);
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


	//////////////////////////////////////
	// INTERSECTIONS
	//////////////////////////////////////
	
	/**
	 * cut a segment in two by this point
	 * @param segment segment
	 * @param pt cutting point
	 */
	private void cut(Segment segment, Point pt){
		// cut the segment
		segment.removeFromPoints();
		Segment segment2 = new Segment(segment.orientation, pt, segment.rightPoint);
		segment.rightPoint = pt;		
		segment.addToPoints();
		comparedSameOrientationSegment = null;
		segment2.addToPoints();
		cutAfterComparison(segment2);
	}
	
	/**
	 * After adding a segment to the points, it may be redundant with an already existing segment in the left point
	 * @param segment2 segment
	 */
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
		// same points are merged
		// aligned segments to right are cut 
		// aligned segments to left are ignored
		TreeSet<Point> pointSet = new TreeSet<Point>();

		for (point = firstPoint; point.next != firstPoint; point = point.next){
			pointSet.add(point);
		}
		pointSet.add(point);
		
		// at this time, pointSet only contains different points, each points have to-left / to-right segments with different orientations


		
		for (Point pt : pointSet){
			App.debug(pt.debugSegments());
		}


		// now compute intersections
		// TODO use a better storage than linear chained segments
		
		// top and bottom (dummy) segments
		Segment top = new Segment();
		Segment bottom = new Segment();
		bottom.above = top;
		top.below = bottom;

		for (Point pt = pointSet.first() ; pt != pointSet.last() ; pt = pointSet.higher(pt) ){
			String s=pt.name+" : ";
			Segment above = null;
			Segment below = null;

			//App.debug(s);
			

			
			// remove to-left segments
			if (pt.toLeft!=null && !pt.toLeft.isEmpty()){ // will put to-right segments in place of to-left segments
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



		setNonSelfIntersecting(pointSet);
	}
	
	
		
		
		
	private void setNonSelfIntersecting(TreeSet<Point> pointSet){
		
		// prepare points as an array
		pointsArray = new GPoint2D.Double[maxPointIndex];

		// now all intersections are computed, and points are correctly chained by oriented segments
		// we can divide the polygon turning e.g. counter clock-wise 
		
		polygonPointsList.clear();
		
		//App.debug("=========== non self-intersecting polygons ==============");
		

		while (!pointSet.isEmpty()){
			TreeSet<Point> polygonPoints = new TreeSet<Point>();
			Point start = pointSet.first();
			Point currentPoint; 
			Point currentPointNew;
			Point nextPoint = start;
			Point nextPointNew = nextPoint.clone();
			Point startPointNew = nextPointNew;
			//polygonPoints.add(nextPointNew);
			Segment segStart = start.toRight.first();
			Segment segment = segStart;
			Segment next = null;

			Running running = Running.RIGHT;

			while (running != Running.STOP){
				segment.running = running;
				currentPoint = nextPoint;
				currentPointNew = nextPointNew;
				boolean needsDiagonal = false;
				//App.debug(segment+"");
				if (running == Running.RIGHT){
					nextPoint = segment.rightPoint;
					if (nextPoint == start){
						running = Running.STOP;
						next = segStart;
					}else{
						next = nextPoint.toLeft.lower(segment);
						if (next == null){
							if (nextPoint.toRight != null && !nextPoint.toRight.isEmpty()){
								next = nextPoint.toRight.last();
							}
							if (next == null){ // no to-right segment
								next = nextPoint.toLeft.last();
								running = Running.LEFT;
								needsDiagonal = needsDiagonal(segment, next);
							}
						}else{
							running = Running.LEFT;
							needsDiagonal = needsDiagonal(segment, next);
						}

					}
				}else{ // running == Running.LEFT
					nextPoint = segment.leftPoint;
					if (nextPoint == start){
						running = Running.STOP;
						next = segStart;
					}else{
						next = nextPoint.toRight.lower(segment);
						if (next == null){
							if (nextPoint.toLeft != null && !nextPoint.toLeft.isEmpty()){
								next = nextPoint.toLeft.last();
							}
							if (next == null){ // no to-left segment
								next = nextPoint.toRight.last();
								running = Running.RIGHT;
								needsDiagonal = needsDiagonal(segment, next);
							}
						}else{
							running = Running.RIGHT;
							needsDiagonal = needsDiagonal(segment, next);
						}

					}
				}

				// remove this segment from left and right points
				segment.removeFromPoints();	
				
				
				// reconfigure segment to new points
				if (running != Running.STOP){
					nextPointNew = nextPoint.clone();
				}else{					
					nextPointNew = startPointNew;
				}
				if (segment.running == Running.RIGHT){
					segment.leftPoint = currentPointNew;
					segment.rightPoint = nextPointNew;
				}else{
					segment.leftPoint = nextPointNew;
					segment.rightPoint = currentPointNew;					
				}
				segment.addToPoints();
				
				// says if the point needs a diagonal
				nextPointNew.needsDiagonal = needsDiagonal;

				// add current point to current polygon
				polygonPoints.add(nextPointNew);
				
				// remove current point if no more segment
				if (currentPoint.hasNoSegment()){
					//App.debug(currentPoint.name+" : remove");
					pointSet.remove(currentPoint);
					pointsArray[currentPoint.id] = new GPoint2D.Double(currentPoint.x, currentPoint.y);
				}else{
					//App.debug(currentPoint.name+" : keep");
				}


				// go on with next segment
				segment = next;
				 
			}

			if (start.hasNoSegment()){
				//App.debug(start.name+" : remove");
				pointSet.remove(start);
				pointsArray[start.id] = new GPoint2D.Double(start.x, start.y);
			}else{
				//App.debug(start.name+" : keep");
			}
			
			// add current polygon to list
			polygonPointsList.add(polygonPoints);
			

		}

		

	}
	
	
	

	private enum Running  { RIGHT, LEFT, STOP };



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
			//Point pt = new Point(x/z, y/z);
			double x1 = x/z;
			double y1 = y/z;

			
			// check intersection point is inside segments		
			int al, ar, bl, br;
			if ((al = a.leftPoint.compareTo(x1,y1)) > 0 
					|| (ar = a.rightPoint.compareTo(x1,y1)) < 0 
					|| (bl = b.leftPoint.compareTo(x1,y1)) > 0 
					|| (br = b.rightPoint.compareTo(x1,y1)) < 0){
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
				Point pt = a.rightPoint;
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
				Point pt = b.rightPoint;
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
				Point pt = new Point(x/z, y/z, maxPointIndex);
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
		
		//App.debug(point.name+", "+((int) (point.orientationToNext*180/Math.PI))+"°, "+point.next.name);
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





	
	




	////////////////////////////////////////////////
	// TRIANGULATION
	////////////////////////////////////////////////


	

	private enum Chain {BOTH, BELOW, ABOVE}

	
	/**
	 * triangulate since polygon has been cut into non-self-intersecting pieces
	 */
	public void triangulate(){
		
		fansList.clear();
		
		for (TreeSet<Point> polygonPoints : polygonPointsList){
			triangulate(polygonPoints);
		}
	}
	
	/**
	 * triangulate a polygon : cut it into monotone pieces, then feed the fans list
	 * @param polygonPoints
	 */
	private void triangulate(TreeSet<Point> polygonPoints){
		String s = "set diagonals of ";
		for (Point pt : polygonPoints){
			s+=pt.name;
			if (pt.needsDiagonal){
				s+="(*)";
			}
		}
		
		App.debug(s);
		
		
		// top and bottom (dummy) segments
		Segment top = new Segment();
		Segment bottom = new Segment();
		bottom.above = top;
		top.below = bottom;

		
		//////////////////////////////////////////////
		// set diagonals
		
		for (Point pt : polygonPoints){
			
			s = pt.name + " : ";
			
			Segment above = null;
			Segment below = null;

		
			// remove to-left segments
			if (pt.toLeft!=null && !pt.toLeft.isEmpty()){ // will put to-right segments in place of to-left segments
				above = pt.toLeft.first().above;
				below = pt.toLeft.last().below;
				below.above = above;
				above.below = below;
				

				if (pt.needsDiagonal){
					//App.error("diagonal to right : "+below+"<"+pt.name+"<"+above);
					Point pt2;
					if (below.rightPoint.compareToOnly(above.rightPoint) < 0){
						pt2 = below.rightPoint;
					}else{
						pt2 = above.rightPoint;
					}					
					
					Segment diagonal = new Segment( Math.atan2(pt2.y - pt.y, pt2.x - pt.x), pt, pt2);
					diagonal.addToPoints();
					diagonal.isDiagonal = true;
					pt.usable++;
					pt2.usable++;
									
					//App.error("diagonal to right : "+diagonal);
				}
				
			}else{ // search for the correct place for to-right segments
				//App.error(pt.name);
				boolean go = true;
				for (Segment segment = bottom.above ; segment != top && go ; segment = segment.above){
					double orientation = Math.atan2(pt.y - segment.leftPoint.y, pt.x - segment.leftPoint.x);
					if (orientation < segment.orientation){ // found the place
						go = false;
						above = segment;
						below = above.below;					
					}		
				}
				if (go){ // when there are no segment between top and bottom
					above = top;
					below = above.below;
				}
				
				if (pt.needsDiagonal){
					//App.error("diagonal to left : "+below+"<"+pt.name+"<"+above);
					if (below.isDiagonal){
						below.removeFromPoints();
						below.rightPoint.usable--;
						pt.usable++;
						below.rightPoint = pt;
						below.addToPoints();
						//App.error("below is diagonal, replace : "+below);
						// remove below
						below = below.below;
						below.above = above;
						above.below = below;
					}else if (above.isDiagonal){
						above.removeFromPoints();
						above.rightPoint.usable--;
						pt.usable++;
						above.rightPoint = pt;
						above.addToPoints();
						//App.error("above is diagonal, replace : "+above);
						// remove above
						above = above.above;
						below.above = above;
						above.below = below;
					}else{
						Point pt2;
						if (below.leftPoint.compareToOnly(above.leftPoint) < 0){
							pt2 = above.leftPoint;
						}else{
							pt2 = below.leftPoint;
						}			
						Segment diagonal = new Segment( Math.atan2(pt.y - pt2.y, pt.x - pt2.x), pt2, pt);
						diagonal.addToPoints();
						diagonal.isDiagonal = true;
						pt.usable++;
						pt2.usable++;

						//App.error("diagonal to left : "+diagonal);
					}
				}
				
			}
			
			
			// put to-right segments
			if (pt.toRight!=null){
				for (Segment seg : pt.toRight){
					below.above = seg;
					seg.below = below;
					below = seg;
				}
				below.above = above;
				above.below = below;
			}

			

			for (Segment seg = bottom.above ; seg != top; seg = seg.above){
				s+=seg.toString()+",";
			}
			//App.debug(s);

		}
		
		//////////////////////////////////////////////
		// cut in monotone pieces
		
		while (!polygonPoints.isEmpty()){
			s="Monotone piece : ";

			Point start = polygonPoints.first();
			Point currentPoint = start;
			Point nextPoint;
			Segment segStart = start.toRight.first();
			Segment segment = segStart;
			Segment next = null;

			Running running = Running.RIGHT;
			Running oldRunning;
			
			
			while (running != Running.STOP){
				oldRunning = running;
				if (running == Running.RIGHT){
					nextPoint = segment.rightPoint;
					if (nextPoint == start){
						running = Running.STOP;
						//next = segStart;
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
						//next = segStart;
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

				
				
				s+=currentPoint.name;


				segment.removeFromPoints();
				if (oldRunning == Running.LEFT){		
					if (segment.isDiagonal){
						//App.debug("segment "+segment+" is diagonal, running left, keep point : "+nextPoint.name);
						segment.isDiagonal = false ; // no more a diagonal, clone it
						Segment clone = segment.clone();
						clone.addToPoints();	
					}
					
					if (running == Running.LEFT){
						next.next = segment;
					}

				}else{ // oldRunning == Running.RIGHT					
					if (segment.isDiagonal){
						//App.debug("segment "+segment+" is diagonal, running right, keep point : "+currentPoint.name);
						segment.isDiagonal = false ; // no more a diagonal, clone it
						Segment clone = segment.clone();
						clone.addToPoints();
					}
					
					if (running == Running.RIGHT){
						segment.next = next;
					}
				}
				
				currentPoint.usable--;
				if (currentPoint.usable == 0){
					polygonPoints.remove(currentPoint);
				}
					
				
			
				
				segment = next;
				currentPoint = nextPoint;

			}

			/*
			s+="\nabove : ";
			for (Segment seg = segment ; seg != null ; seg = seg.next ){
				s += seg+",";
			}
			s+="\nbelow : ";
			for (Segment seg = segStart ; seg != null ; seg = seg.next ){
				s += seg+",";
			}
			*/
			
			App.debug(s);
			
			triangulate(segStart, segment);
		}
		
		
		
		

	
	}
	
	private boolean needsDiagonal(Segment seg1, Segment seg2){
		//App.debug(seg1+"("+((int) (seg1.orientation*180/Math.PI))+"°)"+","+seg2+"("+((int) (seg2.orientation*180/Math.PI))+"°)");
		if (seg1.orientation < seg2.orientation){
			return true;
		}
		return false;
	}
	
	
	
	
	public void triangulate(Segment firstBelow, Segment firstAbove){
		
		
		
		// init stack
		Chain chain;
		Stack<Point> stack = new Stack<Point>();
		stack.push(firstAbove.leftPoint);
		
		Point pAbove = firstAbove.rightPoint;
		Point pBelow = firstBelow.rightPoint;
		if (pAbove.compareToOnly(pBelow) < 0){
			//App.debug("above : "+pAbove.name);
			chain = Chain.ABOVE;
			stack.push(pAbove);
			firstAbove = firstAbove.next;
		}else{
			//App.debug("below : "+pBelow.name);
			chain = Chain.BELOW;
			stack.push(pBelow);
			firstBelow = firstBelow.next;
		}
		
		
		
		

		// loop
		while (firstAbove != null && firstBelow != null){
			String s = "fan : ";
			//ArrayList<Integer> currentTriangleFan = new ArrayList<Integer>();
			TriangleFan currentTriangleFan;
			Point top = stack.peek();
			Point vi;
			Chain viChain;
			//App.debug(firstAbove+"/"+firstBelow);
			if (chain == Chain.ABOVE){ // top point is pAbove
				//if (firstAbove != null){
					pAbove = firstAbove.rightPoint;
					if (pAbove.compareToOnly(pBelow) < 0){ // next point is above
						vi = pAbove;
						viChain = Chain.ABOVE;
						firstAbove = firstAbove.next;
					}else{ // next point is below
						vi = pBelow;
						viChain = Chain.BELOW;
						firstBelow = firstBelow.next;
					}
					/*
				}else{ // next point is below
					vi = pBelow;
					viChain = Chain.BELOW;
					firstBelow = firstBelow.next;					
				}
				*/				
			}else{ // (chain == Chain.BELOW){ // top point is pBelow
				//if (firstBelow != null){
					pBelow = firstBelow.rightPoint;
					if (pBelow.compareToOnly(pAbove) < 0){ // next point is below
						vi = pBelow;
						viChain = Chain.BELOW;
						firstBelow = firstBelow.next;
					}else{ // next point is above
						vi = pAbove;
						viChain = Chain.ABOVE;
						firstAbove = firstAbove.next;
					}
					/*
				}else{ // next point is above
					vi = pAbove;
					viChain = Chain.ABOVE;
					firstAbove = firstAbove.next;					
				}	
				*/			
			}
			
			boolean clockWise = false;
			
			//boolean viBetween = vi > min && vi < max;
			//debugDiagonal("(vi > min && vi < max) , (top > min && top < max) : "+(vi > min && vi < max)+","+(top > min && top < max),vi,top);
			if (viChain != chain){ // vi and top are not on the same chain
				debugDiagonal("case 2 ",top,vi);
				//App.debug("case 2, "+viChain+" : "+vi.name);
				if (viChain == Chain.ABOVE){
					clockWise = true;
				}
				currentTriangleFan = new TriangleFan(vi.id, clockWise);
				s+=vi.name;
				while (!stack.isEmpty()){
					Point v = stack.pop();
					currentTriangleFan.add(v.id);
					s+=v.name;
					debugDiagonal("diagonal : ",vi,v);
				}
				stack.push(top);
				stack.push(vi);

			}else{ // vi and top are on the same chain
				debugDiagonal("case 1 ",top,vi);
				//App.debug("case 1, "+viChain+" : "+vi.name);
				if (viChain == Chain.BELOW){
					clockWise = true;
				}				
				currentTriangleFan = new TriangleFan(vi.id, clockWise);
				
				s+=vi.name;

				// first correct point
				Point vk = stack.pop();
				currentTriangleFan.add(vk.id);
				s+=vk.name;
				debugDiagonal("diagonal ",vi,vk);
				double dx2 = vk.x - vi.x;
				double dy2 = vk.y - vi.y;

				boolean go = true;
				while (!stack.isEmpty() && go){
					double dx1 = dx2;
					double dy1 = dy2;
					Point v = stack.pop();
					dx2 = v.x - vi.x;
					dy2 = v.y - vi.y;
					if (Kernel.isGreater(dx1*dy2, dx2*dy1) ^ (viChain != Chain.BELOW)){ // not same orientation
						stack.push(v); //re-push v in stack
						go = false;
					}else{
						vk = v;
						currentTriangleFan.add(vk.id);
						s+=vk.name;
						debugDiagonal("diagonal ",vi,vk);
					}
				}
				stack.push(vk);
				stack.push(vi);
				
			}

			if (currentTriangleFan.size()>1){ // add fan only if at least 3 points
				fansList.add(currentTriangleFan);
				if (clockWise){
					App.error(s);
				}else{
					App.debug(s);
				}
			}
			
			chain = viChain;

		}
		
		/*
		String s="fans: ";
		for (ArrayList<Point> fan : ret){
			for (Point p : fan){
				s+=p.name;
			}
			s+=", ";
		}
		App.debug(s);
		*/
		
		 

	}

	
	private void debugDiagonal(String s, Point p1, Point p2){
		//App.debug(s+": "+p1.name+","+p2.name);
	}



	/**
	 * 
	 * @return list of list of points indices, which constitute triangle fans covering the polygon
	 */
	public ArrayList<TriangleFan> getTriangleFans(){
		return fansList;

	}
	
	/**
	 * 
	 * @param vertices
	 * @param cs
	 * @return complete vertex array (with intersections)
	 */
	public Coords[] getCompleteVertices(Coords[] vertices, CoordSys cs){
		if (pointsArray.length == vertices.length){
			return vertices;
		}
		
		Coords[] ret = new Coords[pointsArray.length];
		for (int i = 0 ; i < vertices.length; i++){
			ret[i] = vertices[i];
		}
		for (int i = vertices.length ; i < pointsArray.length; i++){
			GPoint2D.Double point = pointsArray[i];
			if (point!=null){
				ret[i] = cs.getPoint(point.x, point.y);
			}
		}
	
		
		return ret;
	}


}
