package geogebra.common.kernel.discrete.delaunay;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * This class represents a Delaunay Triangulation. The class was written for a
 * large scale triangulation (1000 - 200,000 vertices). The application main use is 3D surface (terrain) presentation.
 * <br>
 * The class main properties are the following:<br>
 * - fast point location. (O(n^0.5)), practical runtime is often very fast. <br>
 * - handles degenerate cases and none general position input (ignores duplicate points). <br>
 * - save & load from\to text file in TSIN format. <br>
 * - 3D support: including z value approximation. <br>
 * - standard java (1.5 generic) iterators for the vertices and triangles. <br>
 * - smart iterator to only the updated triangles - for terrain simplification <br>
 * <br>
 *
 * Testing (done in early 2005): Platform java 1.5.02 windows XP (SP2), AMD laptop 1.6G sempron CPU
 * 512MB RAM. Constructing a triangulation of 100,000 vertices takes ~ 10
 * seconds. point location of 100,000 points on a triangulation of 100,000
 * vertices takes ~ 5 seconds.
 *
 * Note: constructing a triangulation with 200,000 vertices and more requires
 * extending java heap size (otherwise an exception will be thrown).<br>
 *
 * Bugs: if U find a bug or U have an idea as for how to improve the code,
 * please send me an email to: benmo@ariel.ac.il
 *
 * @author Boaz Ben Moshe 5/11/05 <br>
 * The project uses some ideas presented in the VoroGuide project, written by Klasse f?r Kreise (1996-1997),
 * For the original applet see: http://www.pi6.fernuni-hagen.de/GeomLab/VoroGlide/ . <br>
 */

public class Delaunay_Triangulation {

	// the first and last points (used only for first step construction)
	private Point_dt firstP;
	private Point_dt lastP;

	// for degenerate case!
	public boolean allCollinear;

	// the first and last triangles (used only for first step construction)
	private Triangle_dt firstT, lastT, currT;

	// the triangle the fond (search start from
	private Triangle_dt startTriangle;

	// the triangle the convex hull starts from
	public Triangle_dt startTriangleHull;

	private int nPoints = 0; // number of points
	// additional data 4/8/05 used by the iterators
	private Set<Point_dt> _vertices;
	private Vector<Triangle_dt> _triangles;
	
	// The triangles that were deleted in the last deletePoint iteration.
	private Vector<Triangle_dt> deletedTriangles;
        // The triangles that were added in the last deletePoint iteration.
	private Vector<Triangle_dt> addedTriangles;

	private int _modCount = 0, _modCount2 = 0;

	// the Bounding Box, {{x0,y0,z0} , {x1,y1,z1}}
	private Point_dt _bb_min, _bb_max;

	/**
	 * creates an empty Delaunay Triangulation.
	 */
	public Delaunay_Triangulation() {
		this(new Point_dt[] {});
	}

	/**
	 * creates a Delaunay Triangulation from all the points. Note: duplicated
	 * points are ignored.
	 */
	public Delaunay_Triangulation(Point_dt[] ps) {
		_modCount = 0;
		_modCount2 = 0;
		_bb_min = null;
		_bb_max = null;
		this._vertices = new TreeSet<Point_dt>(Point_dt.getComparator());
		_triangles = new Vector<Triangle_dt>();
		deletedTriangles = null;
		addedTriangles = new Vector<Triangle_dt>();
		allCollinear = true;
		for (int i = 0; ps != null && i < ps.length && ps[i] != null; i++) {
			this.insertPoint(ps[i]);
		}
	}

	/**
	 * the number of (different) vertices in this triangulation.
	 *
	 * @return the number of vertices in the triangulation (duplicates are
	 *         ignore - set size).
	 */
	public int size() {
		if (_vertices == null)
		{
			return 0;
		}
		return _vertices.size();
	}

	/**
	 * @return the number of triangles in the triangulation. <br />
	 * Note: includes infinife faces!!.
	 */
	public int trianglesSize() {
		this.initTriangles();
		return _triangles.size();
	}

	/**
	 * returns the changes counter for this triangulation
	 */
	public int getModeCounter() {
		return this._modCount;
	}

    /**
     * insert the point to this Delaunay Triangulation. Note: if p is null or
     * already exist in this triangulation p is ignored.
     *
     * @param p
     *            new vertex to be inserted the triangulation.
     */
    public void insertPoint(Point_dt p) {
     if (this._vertices.contains(p))
      return;
     _modCount++;
     updateBoundingBox(p);
     this._vertices.add(p);
     Triangle_dt t = insertPointSimple(p);
     if (t == null) //
      return;
     Triangle_dt tt = t;
     currT = t; // recall the last point for - fast (last) update iterator.
     do {
      flip(tt, _modCount);
      tt = tt.canext;
     } while (tt != t && !tt.halfplane);
     
    }
	
	/**
	 * Deletes the given point from this.
	 * @param pointToDelete The given point to delete.
	 * 
	 * Implementation of the Mostafavia, Gold & Dakowicz algorithm (2002).
	 * 
	 * By Eyal Roth & Doron Ganel (2009).
	 */
	public void deletePoint(Point_dt pointToDelete) {
                
		// Finding the triangles to delete.
		Vector<Point_dt> pointsVec = findConnectedVertices(pointToDelete, true);
		if (pointsVec == null) {
			return;
		}
		
		while(pointsVec.size()>=3)
		{
			// Getting a triangle to add, and saving it.
			Triangle_dt triangle = findTriangle(pointsVec, pointToDelete);
			addedTriangles.add(triangle);
			
			// Finding the point on the diagonal (pointToDelete,p)
			Point_dt p = findDiagonal(triangle,pointToDelete);
			
			for(Point_dt tmpP : pointsVec)
			{
				if(tmpP.equals(p))
				{
					pointsVec.removeElement(tmpP);
					break;
				}
			}
		}
                //updating the trangulation
                deleteUpdate(pointToDelete);
                for(Triangle_dt t:deletedTriangles) {
                    if(t == startTriangle) {
                        startTriangle = addedTriangles.elementAt(0);
                        break;
                    }
                }
                _triangles.removeAll(deletedTriangles);
                _triangles.addAll(addedTriangles);
                _vertices.remove(pointToDelete);
                nPoints = nPoints+addedTriangles.size()-deletedTriangles.size();
                addedTriangles.removeAllElements();
                deletedTriangles.removeAllElements();             
	}

    /** return a point from the trangulation that is close to pointToDelete
     * @param pointToDelete the point that the user wants to delete
     * @return a point from the trangulation that is close to pointToDelete
     * By Eyal Roth & Doron Ganel (2009).
     */
    public Point_dt findClosePoint(Point_dt pointToDelete) {
            Triangle_dt triangle = find(pointToDelete);
            Point_dt p1 = triangle.p1();
            Point_dt p2 = triangle.p2();
            double d1 = p1.distance(pointToDelete);
            double d2 = p2.distance(pointToDelete);
            if(triangle.isHalfplane()) {
                if(d1<=d2) {
                    return p1;
                }
                else {
                    return p2;
                }
            }
            else {
                Point_dt p3 = triangle.p3();
                
                double d3 = p3.distance(pointToDelete);
                Point_dt p;
                if(d1<=d2 && d1<=d3) {
                    return p1;
                }
                else if(d2<=d1 && d2<=d3) {
                    return p2;
                }
                else {
                    return p3;
                }
            }
        }
        
        //updates the trangulation after the triangles to be deleted and
        //the triangles to be added were found
        //by Doron Ganel & Eyal Roth(2009)
        private void deleteUpdate(Point_dt pointToDelete) {
            for(Triangle_dt addedTriangle1 : addedTriangles) {
                //update between addedd triangles and deleted triangles
                for(Triangle_dt deletedTriangle : deletedTriangles) {
                    if(shareSegment(addedTriangle1,deletedTriangle)) {
                        updateNeighbor(addedTriangle1,deletedTriangle,pointToDelete);
                    }
                }
            }
            for(Triangle_dt addedTriangle1 : addedTriangles) {
                //update between added triangles
                for(Triangle_dt addedTriangle2 : addedTriangles) {
                    if((addedTriangle1!=addedTriangle2)&&(shareSegment(addedTriangle1,addedTriangle2))) {
                        updateNeighbor(addedTriangle1,addedTriangle2);
                    }
                }
            }
            
            
        }
        
        //checks if the 2 triangles shares a segment
        //by Doron Ganel & Eyal Roth(2009)
        private boolean shareSegment(Triangle_dt t1,Triangle_dt t2) {
            int counter = 0;
            Point_dt t1P1 = t1.p1();
            Point_dt t1P2 = t1.p2();
            Point_dt t1P3 = t1.p3();
            Point_dt t2P1 = t2.p1();
            Point_dt t2P2 = t2.p2();
            Point_dt t2P3 = t2.p3();
                         
            if(t1P1.equals(t2P1)){
                counter++;
            }
            if(t1P1.equals(t2P2)){
                counter++;
            }
            if(t1P1.equals(t2P3)){
                counter++;
            }
            if(t1P2.equals(t2P1)){
                counter++;
            }
            if(t1P2.equals(t2P2)){
                counter++;
            }
            if(t1P2.equals(t2P3)){
                counter++;
            }
            if(t1P3.equals(t2P1)){
                counter++;
            }
            if(t1P3.equals(t2P2)){
                counter++;
            }
            if(t1P3.equals(t2P3)){
                counter++;
            }
            if(counter>=2)
                return true;
            else
                return false;
        }
        
        //update the neighbors of the addedTriangle and deletedTriangle
        //we assume the 2 triangles share a segment
        //by Doron Ganel & Eyal Roth(2009)
        private void updateNeighbor(Triangle_dt addedTriangle,Triangle_dt deletedTriangle,Point_dt pointToDelete) {
            Point_dt delA = deletedTriangle.p1();
            Point_dt delB = deletedTriangle.p2();
            Point_dt delC = deletedTriangle.p3();
            Point_dt addA = addedTriangle.p1();
            Point_dt addB = addedTriangle.p2();
            Point_dt addC = addedTriangle.p3();
            
            //updates the neighbor of the deleted triangle to point to the added triangle
            //setting the neighbor of the added triangle
            if(pointToDelete.equals(delA)) {
                deletedTriangle.next_23().switchneighbors(deletedTriangle,addedTriangle);
                //AB-BC || BA-BC
                if((addA.equals(delB)&&addB.equals(delC)) || (addB.equals(delB)&&addA.equals(delC))) {                    
                        addedTriangle.abnext = deletedTriangle.next_23();                
                }
                //AC-BC || CA-BC
                else if((addA.equals(delB)&&addC.equals(delC)) || (addC.equals(delB)&&addA.equals(delC))) {                    
                        addedTriangle.canext = deletedTriangle.next_23();                
                }
                //BC-BC || CB-BC
                else {
                    addedTriangle.bcnext = deletedTriangle.next_23();
                }
            }
            else if(pointToDelete.equals(delB)) {
                deletedTriangle.next_31().switchneighbors(deletedTriangle,addedTriangle);
                //AB-AC || BA-AC
                if((addA.equals(delA)&&addB.equals(delC)) || (addB.equals(delA)&&addA.equals(delC))) {                    
                        addedTriangle.abnext = deletedTriangle.next_31();                
                }
                //AC-AC || CA-AC
                else if((addA.equals(delA)&&addC.equals(delC)) || (addC.equals(delA)&&addA.equals(delC))) {                    
                        addedTriangle.canext = deletedTriangle.next_31();                
                }
                //BC-AC || CB-AC
                else {
                    addedTriangle.bcnext = deletedTriangle.next_31();
                }
            }
            //equals c
            else {
                deletedTriangle.next_12().switchneighbors(deletedTriangle,addedTriangle);
                //AB-AB || BA-AB
                if((addA.equals(delA)&&addB.equals(delB)) || (addB.equals(delA)&&addA.equals(delB))) {                    
                        addedTriangle.abnext = deletedTriangle.next_12();                
                }
                //AC-AB || CA-AB
                else if((addA.equals(delA)&&addC.equals(delB)) || (addC.equals(delA)&&addA.equals(delB))) {                    
                        addedTriangle.canext = deletedTriangle.next_12();                
                }
                //BC-AB || CB-AB
                else {
                    addedTriangle.bcnext = deletedTriangle.next_12();
                }
            }
        }
        
        //update the neighbors of the 2 added Triangle s
        //we assume the 2 triangles share a segment
        //by Doron Ganel & Eyal Roth(2009)
        private void updateNeighbor(Triangle_dt addedTriangle1,Triangle_dt addedTriangle2){
            Point_dt A1 = addedTriangle1.p1();
            Point_dt B1 = addedTriangle1.p2();
            Point_dt C1 = addedTriangle1.p3();
            Point_dt A2 = addedTriangle2.p1();
            Point_dt B2 = addedTriangle2.p2();
            Point_dt C2 = addedTriangle2.p3();
            
            //A1-A2
            if(A1.equals(A2)){
                //A1B1-A2B2
                if(B1.equals(B2)) {
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //A1B1-A2C2
                else if(B1.equals(C2)){
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
                //A1C1-A2B2
                else if(C1.equals(B2)) {
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //A1C1-A2C2
                else {
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
            }
            //A1-B2
            else if(A1.equals(B2)) {
                //A1B1-B2A2
                if(B1.equals(A2)) {
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //A1B1-B2C2
                else if(B1.equals(C2)) {
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
                //A1C1-B2A2
                else if(C1.equals(A2)) {
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //A1C1-B2C2
                else {
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
            }
            //A1-C2
            else if(A1.equals(C2)) {
                //A1B1-C2A2
                if(B1.equals(A2)){
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
                //A1B1-C2B2
                if(B1.equals(B2)){
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
                //A1C1-C2A2
                if(C1.equals(A2)){
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
                //A1C1-C2B2
                else {
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
            }
            //B1-A2
            else if(B1.equals(A2)){
                //B1A1-A2B2
                if(A1.equals(B2)) {
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //B1A1-A2C2
                else if(A1.equals(C2)){
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
                //B1C1-A2B2
                else if(C1.equals(B2)) {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //B1C1-A2C2
                else {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
            }
            //B1-B2
            else if(B1.equals(B2)) {
                //B1A1-B2A2
                if(A1.equals(A2)) {
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //B1A1-B2C2
                else if(A1.equals(C2)){
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
                //B1C1-B2A2
                else if(C1.equals(A2)) {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //B1C1-B2C2
                else {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
            }
            //B1-C2
            else if(B1.equals(C2)) {
                //B1A1-C2A2
                if(A1.equals(A2)){
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
                //B1A1-C2B2
                if(A1.equals(B2)){
                    addedTriangle1.abnext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
                //B1C1-C2A2
                if(C1.equals(A2)){
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
                //B1C1-C2B2
                else {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
            }
            //C1-A2
            else if(C1.equals(A2)) {
                //C1A1-A2B2
                if(A1.equals(B2)) {
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //C1A1-A2C2
                else if(A1.equals(C2)){
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
                //C1B1-A2B2
                else if(B1.equals(B2)) {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //C1B1-A2C2
                else {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
            }
            //C1-B2
            else if(C1.equals(B2)) {
                //C1A1-B2A2
                if(A1.equals(A2)) {
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //C1A1-B2C2
                else if(A1.equals(C2)){
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
                //C1B1-B2A2
                else if(B1.equals(A2)) {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.abnext = addedTriangle1;
                }
                //C1B1-B2C2
                else {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
            }
            //C1-C2
            else if(C1.equals(C2)) {
                //C1A1-C2A2
                if(A1.equals(A2)){
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
                //C1A1-C2B2
                if(A1.equals(B2)){
                    addedTriangle1.canext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
                //C1B1-C2A2
                if(B1.equals(A2)){
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.canext = addedTriangle1;
                }
                //C1B1-C2B2
                else {
                    addedTriangle1.bcnext = addedTriangle2;
                    addedTriangle2.bcnext = addedTriangle1;
                }
            }
        }
	
	//finds the a point on the triangle that if connect it to "point" (creating a segment)
        //the other two points of the triangle will be to the left and to the right of the segment
	//by Doron Ganel & Eyal Roth(2009)
	private Point_dt findDiagonal(Triangle_dt triangle, Point_dt point) {
		Point_dt p1 = triangle.p1();
		Point_dt p2 = triangle.p2();
		Point_dt p3 = triangle.p3();
		
		if((p1.pointLineTest(point,p3) == point.LEFT)&&
		   (p2.pointLineTest(point,p3) == point.RIGHT))
			return p3;
		if((p3.pointLineTest(point,p2) == point.LEFT)&&
		   (p1.pointLineTest(point,p2) == point.RIGHT))
			return p2;
		if((p2.pointLineTest(point,p1) == point.LEFT)&&
		   (p3.pointLineTest(point,p1) == point.RIGHT))
			return p1;
		return null;		
	}

	/**
	 * Calculates a Voronoi cell for a given neighborhood
	 * in this triangulation. A neighborhood is defined by a triangle
	 * and one of its corner points.
	 *  
	 * By Udi Schneider
	 * 
	 * @param triangle a triangle in the neighborhood  
	 * @param p corner point whose surrounding neighbors will be checked
	 * @return set of Points representing the cell polygon
	 */
	public Point_dt[] calcVoronoiCell(Triangle_dt triangle, Point_dt p)
	{	
		// handle any full triangle		 
		if (!triangle.isHalfplane()) {
			
			// get all neighbors of given corner point
			Vector<Triangle_dt> neighbors = findTriangleNeighborhood(triangle, p);
						
			Iterator<Triangle_dt> itn = neighbors.iterator();
			Point_dt[] vertices = new Point_dt[neighbors.size()];
			
			// for each neighbor, including the given triangle, add
			// center of circumscribed circle to cell polygon
			int index = 0;
			while (itn.hasNext()) {
				Triangle_dt tmp = itn.next();									
				vertices[index++] = tmp.circumcircle().Center();				
			}			
			
			return vertices;
		}
		
		// handle half plane
		// in this case, the cell is a single line
		// which is the perpendicular bisector of the half plane line
		else {
			// local friendly alias			
			Triangle_dt halfplane = triangle;
			// third point of triangle adjacent to this half plane
			// (the point not shared with the half plane)
			Point_dt third = null;
			// triangle adjacent to the half plane
			Triangle_dt neighbor = null;
			
			// find the neighbor triangle
			if (!halfplane.next_12().isHalfplane())
			{
				neighbor = halfplane.next_12();				
			}
			else if (!halfplane.next_23().isHalfplane())
			{
				neighbor = halfplane.next_23();				
			}
			else if (!halfplane.next_23().isHalfplane())
			{
				neighbor = halfplane.next_31();				
			}
				
			// find third point of neighbor triangle
			// (the one which is not shared with current half plane)
			// this is used in determining half plane orientation
			if (!neighbor.p1().equals(halfplane.p1()) && !neighbor.p1().equals(halfplane.p2()) ) 
				third = neighbor.p1();  
			if (!neighbor.p2().equals(halfplane.p1()) && !neighbor.p2().equals(halfplane.p2()) ) 
				third = neighbor.p2();
			if (!neighbor.p3().equals(halfplane.p1()) && !neighbor.p3().equals(halfplane.p2()) ) 
				third = neighbor.p3();
						
			// delta (slope) of half plane edge
			double halfplane_delta = (halfplane.p1().y() - halfplane.p2().y()) /
							(halfplane.p1().x() - halfplane.p2().x());
			
			// delta of line perpendicular to current half plane edge
			double perp_delta = (1.0 / halfplane_delta) * (-1.0);
		 
			// determine orientation: find if the third point of the triangle
			// lies above or below the half plane
			// works by finding the matching y value on the half plane line equation
			// for the same x value as the third point
			double y_orient =  halfplane_delta * (third.x() - halfplane.p1().x()) + halfplane.p1().y();
			boolean above = true;
			if (y_orient > third.y())
				above = false;
			
			// based on orientation, determine cell line direction
			// (towards right or left side of window)
			double sign = 1.0;
			if ((perp_delta < 0 && !above) || (perp_delta > 0 && above))
				sign = -1.0;
						
			// the cell line is a line originating from the circumcircle to infinity
			// x = 500.0 is used as a large enough value
			Point_dt circumcircle = neighbor.circumcircle().Center();
			double x_cell_line = (circumcircle.x() + (500.0 * sign));			
			double y_cell_line = perp_delta * (x_cell_line - circumcircle.x()) + circumcircle.y();
			
			Point_dt[] result = new Point_dt[2];
			result[0] = circumcircle;
			result[1] = new Point_dt(x_cell_line, y_cell_line);
						
			return result;
		}
	}
	
	/**
	 * returns an iterator object involved in the last update.
	 * @return iterator to all triangles involved in the last update of the
	 *         triangulation NOTE: works ONLY if the are triangles (it there is
	 *         only a half plane - returns an empty iterator
	 */
	public Iterator<Triangle_dt> getLastUpdatedTriangles() {
		Vector<Triangle_dt> tmp = new Vector<Triangle_dt>();
		if (this.trianglesSize() > 1) {
			Triangle_dt t = currT;
			allTriangles(t, tmp, this._modCount);
		}
		return tmp.iterator();
	}

	private void allTriangles(Triangle_dt curr, Vector<Triangle_dt> front, int mc) {
		if (curr != null && curr._mc == mc && !front.contains(curr)) {
			front.add(curr);
			allTriangles(curr.abnext, front, mc);
			allTriangles(curr.bcnext, front, mc);
			allTriangles(curr.canext, front, mc);
		}
	}

	private Triangle_dt insertPointSimple(Point_dt p) {
		nPoints++;
		if (!allCollinear) {
			Triangle_dt t = find(startTriangle, p);
			if (t.halfplane)
				startTriangle = extendOutside(t, p);
			else
				startTriangle = extendInside(t, p);
			return startTriangle;
		}

		if (nPoints == 1) {
			firstP = p;
			return null;
		}

		if (nPoints == 2) {
			startTriangulation(firstP, p);
			return null;
		}

		switch (p.pointLineTest(firstP, lastP)) {
		case Point_dt.LEFT:
			startTriangle = extendOutside(firstT.abnext, p);
			allCollinear = false;
			break;
		case Point_dt.RIGHT:
			startTriangle = extendOutside(firstT, p);
			allCollinear = false;
			break;
		case Point_dt.ONSEGMENT:
			insertCollinear(p, Point_dt.ONSEGMENT);
			break;
		case Point_dt.INFRONTOFA:
			insertCollinear(p, Point_dt.INFRONTOFA);
			break;
		case Point_dt.BEHINDB:
			insertCollinear(p, Point_dt.BEHINDB);
			break;
		}
		return null;
	}

	private void insertCollinear(Point_dt p, int res) {
		Triangle_dt t, tp, u;

		switch (res) {
		case Point_dt.INFRONTOFA:
			t = new Triangle_dt(firstP, p);
			tp = new Triangle_dt(p, firstP);
			t.abnext = tp;
			tp.abnext = t;
			t.bcnext = tp;
			tp.canext = t;
			t.canext = firstT;
			firstT.bcnext = t;
			tp.bcnext = firstT.abnext;
			firstT.abnext.canext = tp;
			firstT = t;
			firstP = p;
			break;
		case Point_dt.BEHINDB:
			t = new Triangle_dt(p, lastP);
			tp = new Triangle_dt(lastP, p);
			t.abnext = tp;
			tp.abnext = t;
			t.bcnext = lastT;
			lastT.canext = t;
			t.canext = tp;
			tp.bcnext = t;
			tp.canext = lastT.abnext;
			lastT.abnext.bcnext = tp;
			lastT = t;
			lastP = p;
			break;
		case Point_dt.ONSEGMENT:
			u = firstT;
			while (p.isGreater(u.a))
				u = u.canext;
			t = new Triangle_dt(p, u.b);
			tp = new Triangle_dt(u.b, p);
			u.b = p;
			u.abnext.a = p;
			t.abnext = tp;
			tp.abnext = t;
			t.bcnext = u.bcnext;
			u.bcnext.canext = t;
			t.canext = u;
			u.bcnext = t;
			tp.canext = u.abnext.canext;
			u.abnext.canext.bcnext = tp;
			tp.bcnext = u.abnext;
			u.abnext.canext = tp;
			if (firstT == u) {
				firstT = t;
			}
			break;
		}
	}

	private void startTriangulation(Point_dt p1, Point_dt p2) {
		Point_dt ps, pb;
		if (p1.isLess(p2)) {
			ps = p1;
			pb = p2;
		} else {
			ps = p2;
			pb = p1;
		}
		firstT = new Triangle_dt(pb, ps);
		lastT = firstT;
		Triangle_dt t = new Triangle_dt(ps, pb);
		firstT.abnext = t;
		t.abnext = firstT;
		firstT.bcnext = t;
		t.canext = firstT;
		firstT.canext = t;
		t.bcnext = firstT;
		firstP = firstT.b;
		lastP = lastT.a;
		startTriangleHull = firstT;
	}

	private Triangle_dt extendInside(Triangle_dt t, Point_dt p) {

		Triangle_dt h1, h2;
		h1 = treatDegeneracyInside(t, p);
		if (h1 != null)
			return h1;

		h1 = new Triangle_dt(t.c, t.a, p);
		h2 = new Triangle_dt(t.b, t.c, p);
		t.c = p;
		t.circumcircle();
		h1.abnext = t.canext;
		h1.bcnext = t;
		h1.canext = h2;
		h2.abnext = t.bcnext;
		h2.bcnext = h1;
		h2.canext = t;
		h1.abnext.switchneighbors(t, h1);
		h2.abnext.switchneighbors(t, h2);
		t.bcnext = h2;
		t.canext = h1;
		return t;
	}

	private Triangle_dt treatDegeneracyInside(Triangle_dt t, Point_dt p) {

		if (t.abnext.halfplane
				&& p.pointLineTest(t.b, t.a) == Point_dt.ONSEGMENT)
			return extendOutside(t.abnext, p);
		if (t.bcnext.halfplane
				&& p.pointLineTest(t.c, t.b) == Point_dt.ONSEGMENT)
			return extendOutside(t.bcnext, p);
		if (t.canext.halfplane
				&& p.pointLineTest(t.a, t.c) == Point_dt.ONSEGMENT)
			return extendOutside(t.canext, p);
		return null;
	}

	private Triangle_dt extendOutside(Triangle_dt t, Point_dt p) {

		if (p.pointLineTest(t.a, t.b) == Point_dt.ONSEGMENT) {
			Triangle_dt dg = new Triangle_dt(t.a, t.b, p);
			Triangle_dt hp = new Triangle_dt(p, t.b);
			t.b = p;
			dg.abnext = t.abnext;
			dg.abnext.switchneighbors(t, dg);
			dg.bcnext = hp;
			hp.abnext = dg;
			dg.canext = t;
			t.abnext = dg;
			hp.bcnext = t.bcnext;
			hp.bcnext.canext = hp;
			hp.canext = t;
			t.bcnext = hp;
			return dg;
		}
		Triangle_dt ccT = extendcounterclock(t, p);
		Triangle_dt cT = extendclock(t, p);
		ccT.bcnext = cT;
		cT.canext = ccT;
		startTriangleHull = cT;
		return cT.abnext;
	}

	private Triangle_dt extendcounterclock(Triangle_dt t, Point_dt p) {

		t.halfplane = false;
		t.c = p;
		t.circumcircle();

		Triangle_dt tca = t.canext;

		if (p.pointLineTest(tca.a, tca.b) >= Point_dt.RIGHT) {
			Triangle_dt nT = new Triangle_dt(t.a, p);
			nT.abnext = t;
			t.canext = nT;
			nT.canext = tca;
			tca.bcnext = nT;
			return nT;
		}
		return extendcounterclock(tca, p);
	}

	private Triangle_dt extendclock(Triangle_dt t, Point_dt p) {

		t.halfplane = false;
		t.c = p;
		t.circumcircle();

		Triangle_dt tbc = t.bcnext;

		if (p.pointLineTest(tbc.a, tbc.b) >= Point_dt.RIGHT) {
			Triangle_dt nT = new Triangle_dt(p, t.b);
			nT.abnext = t;
			t.bcnext = nT;
			nT.bcnext = tbc;
			tbc.canext = nT;
			return nT;
		}
		return extendclock(tbc, p);
	}

	private void flip(Triangle_dt t, int mc) {

		Triangle_dt u = t.abnext, v;
		t._mc = mc;
		if (u.halfplane || !u.circumcircle_contains(t.c))
			return;

		if (t.a == u.a) {
			v = new Triangle_dt(u.b, t.b, t.c);
			v.abnext = u.bcnext;
			t.abnext = u.abnext;
		} else if (t.a == u.b) {
			v = new Triangle_dt(u.c, t.b, t.c);
			v.abnext = u.canext;
			t.abnext = u.bcnext;
		} else if (t.a == u.c) {
			v = new Triangle_dt(u.a, t.b, t.c);
			v.abnext = u.abnext;
			t.abnext = u.canext;
		} else {
			throw new RuntimeException("Error in flip.");
		}

		v._mc = mc;
		v.bcnext = t.bcnext;
		v.abnext.switchneighbors(u, v);
		v.bcnext.switchneighbors(t, v);
		t.bcnext = v;
		v.canext = t;
		t.b = v.a;
		t.abnext.switchneighbors(u, t);
		t.circumcircle();

		currT = v;
		flip(t, mc);
		flip(v, mc);
	}

	/**
	 * compute the number of vertices in the convex hull. <br />
	 * NOTE: has a 'bug-like' behavor: <br />
	 * in cases of colinear - not on a asix parallel rectangle,
	 * colinear points are reported
	 *
	 * @return the number of vertices in the convex hull.
	 */
	public int CH_size() {
		int ans = 0;
		Iterator<Point_dt> it = this.CH_vertices_Iterator();
		while (it.hasNext()) {
			ans++;
			it.next();
		}
		return ans;
	}

	/**
	 * finds the triangle the query point falls in, note if out-side of this
	 * triangulation a half plane triangle will be returned (see contains), the
	 * search has expected time of O(n^0.5), and it starts form a fixed triangle
	 * (this.startTriangle),
	 *
	 * @param p
	 *            query point
	 * @return the triangle that point p is in.
	 */
	public Triangle_dt find(Point_dt p) {

		// If triangulation has a spatial index try to use it as the starting triangle
		Triangle_dt searchTriangle = startTriangle;

		// Search for the point's triangle starting from searchTriangle
		return find(searchTriangle, p);
	}

	/**
	 * finds the triangle the query point falls in, note if out-side of this
	 * triangulation a half plane triangle will be returned (see contains). the
	 * search starts from the the start triangle
	 *
	 * @param p
	 *            query point
	 * @param start
	 *            the triangle the search starts at.
	 * @return the triangle that point p is in..
	 */
	public Triangle_dt find(Point_dt p, Triangle_dt start) {
		if (start == null)
			start = this.startTriangle;
		Triangle_dt T = find(start, p);
		return T;
	}


	private static Triangle_dt find(Triangle_dt curr, Point_dt p) {
		if (p == null)
			return null;
		Triangle_dt next_t;
		if (curr.halfplane) {
			next_t = findnext2(p, curr);
			if (next_t == null || next_t.halfplane)
				return curr;
			curr = next_t;
		}
		while (true) {
			next_t = findnext1(p, curr);
			if (next_t == null)
				return curr;
			if (next_t.halfplane)
				return next_t;
			curr = next_t;
		}
	}

	/*
	 * assumes v is NOT an halfplane!
	 * returns the next triangle for find.
	 */
	private static Triangle_dt findnext1(Point_dt p, Triangle_dt v) {
		if (p.pointLineTest(v.a, v.b) == Point_dt.RIGHT && !v.abnext.halfplane)
			return v.abnext;
		if (p.pointLineTest(v.b, v.c) == Point_dt.RIGHT && !v.bcnext.halfplane)
			return v.bcnext;
		if (p.pointLineTest(v.c, v.a) == Point_dt.RIGHT && !v.canext.halfplane)
			return v.canext;
		if (p.pointLineTest(v.a, v.b) == Point_dt.RIGHT)
			return v.abnext;
		if (p.pointLineTest(v.b, v.c) == Point_dt.RIGHT)
			return v.bcnext;
		if (p.pointLineTest(v.c, v.a) == Point_dt.RIGHT)
			return v.canext;
		return null;
	}

	/** assumes v is an halfplane! - returns another (none halfplane) triangle */
	private static Triangle_dt findnext2(Point_dt p, Triangle_dt v) {
		if (v.abnext != null && !v.abnext.halfplane)
			return v.abnext;
		if (v.bcnext != null && !v.bcnext.halfplane)
			return v.bcnext;
		if (v.canext != null && !v.canext.halfplane)
			return v.canext;
		return null;
	}
	
	/* 
	 * Receives a point and returns all the points of the triangles that
	 * shares point as a corner (Connected vertices to this point).
	 * 
	 * By Doron Ganel & Eyal Roth
	 */
	private Vector<Point_dt> findConnectedVertices(Point_dt point) {
		return findConnectedVertices(point, false);
	}
	
	/* 
	 * Receives a point and returns all the points of the triangles that
	 * shares point as a corner (Connected vertices to this point).
	 * 
	 * Set saveTriangles to true if you wish to save the triangles that were found.
	 * 
	 * By Doron Ganel & Eyal Roth
	 */
	private Vector<Point_dt> findConnectedVertices(Point_dt point, boolean saveTriangles) {
		Set<Point_dt> pointsSet = new HashSet<Point_dt>();
		Vector<Point_dt> pointsVec = new Vector<Point_dt>();
		Vector<Triangle_dt> triangles = null;
		// Getting one of the neigh
		Triangle_dt triangle = find(point);
		
		// Validating find result.
		if (!triangle.isCorner(point)) {
			System.err.println("findConnectedVertices: Could not find connected vertices since the first found triangle doesn't" +
					" share the given point.");
			return null;
		}
		
		triangles = findTriangleNeighborhood(triangle, point);
                if(triangles == null) {
                    System.err.println("Error: can't delete a point on the perimeter");
                    return null;
                }
		if (saveTriangles) {
			deletedTriangles = triangles;
		}
		
		for (Triangle_dt tmpTriangle : triangles) {
			Point_dt point1 = tmpTriangle.p1();
			Point_dt point2 = tmpTriangle.p2();
			Point_dt point3 = tmpTriangle.p3();
			
			if (point1.equals(point) && !pointsSet.contains(point2)) {
				pointsSet.add(point2);
				pointsVec.add(point2);
			}
			
			if (point2.equals(point) && !pointsSet.contains(point3)) {
				pointsSet.add(point3);
				pointsVec.add(point3);
			}
			
			if (point3.equals(point)&& !pointsSet.contains(point1)) {
				pointsSet.add(point1);
				pointsVec.add(point1);
			}
		}
		
		return pointsVec;
	}
        
        private boolean onPerimeter(Vector<Triangle_dt> triangles) {
            for(Triangle_dt t : triangles) {
                if(t.isHalfplane())
                    return true;
            }
            return false;
        }
	
	// Walks on a consistent side of triangles until a cycle is achieved.
	//By Doron Ganel & Eyal Roth
    // changed to public by Udi
	public Vector<Triangle_dt> findTriangleNeighborhood(Triangle_dt firstTriangle, Point_dt point) {
		Vector<Triangle_dt> triangles = new Vector<Triangle_dt>(30);
		triangles.add(firstTriangle);
		
		Triangle_dt prevTriangle = null;
		Triangle_dt currentTriangle = firstTriangle;
		Triangle_dt nextTriangle = currentTriangle.nextNeighbor(point, prevTriangle);
		
		while (nextTriangle != firstTriangle) {
                        //the point is on the perimeter
                        if(nextTriangle.isHalfplane()) {                            
                            return null;
                        }
			triangles.add(nextTriangle);
			prevTriangle = currentTriangle;
			currentTriangle = nextTriangle;
			nextTriangle = currentTriangle.nextNeighbor(point, prevTriangle);
		}
		
		return triangles;
	}	
	
	/*
	 * find triangle to be added to the triangulation
	 * 
	 * By: Doron Ganel & Eyal Roth
	 * 
	 */
	private Triangle_dt findTriangle(Vector<Point_dt> pointsVec,Point_dt p)
	{
		Point_dt[] arrayPoints = new Point_dt[pointsVec.size()];
		pointsVec.toArray(arrayPoints);
		
		int size = arrayPoints.length;
		if(size < 3)
		{
			return null;
		}
		// if we left with 3 points we return the triangle
		else if(size==3)
		{
			return new Triangle_dt(arrayPoints[0],arrayPoints[1],arrayPoints[2]);
		}
		else
		{
		        for(int i=0;i<=size-1;i++)
		        {
		                Point_dt p1 = arrayPoints[i];
                                int j=i+1;                            
                                int k=i+2;
                                if(j>=size)
                                {
                                    j=0;
                                    k=1;
                                }
                                //check IndexOutOfBound
                                else if(k>=size)
                                    k=0;
                                Point_dt p2 = arrayPoints[j];
                                Point_dt p3 = arrayPoints[k];
                                //check if the triangle is not re-entrant and not encloses p
                                Triangle_dt t = new Triangle_dt(p1,p2,p3);
                                if((calcDet(p1, p2, p3) >= 0) && !t.contains(p)){                                                       
                                    if(!t.fallInsideCircumcircle(arrayPoints))
                                            return t;
                                }
                                //if there are only 4 points use contains that refers to point
                                //on boundary as outside
                                if(size==4&&(calcDet(p1, p2, p3) >= 0) && !t.contains_BoundaryIsOutside(p)){                                                       
                                    if(!t.fallInsideCircumcircle(arrayPoints))
                                            return t;
                                }
                        }                        
		}
		return null;
	}
	
	// TODO: Move this to triangle.
	//checks if the triangle is not re-entrant
	private double calcDet(Point_dt A ,Point_dt B, Point_dt P)
	{
		return (A.x()*(B.y()-P.y())) - (A.y()*(B.x()-P.x())) + (B.x()*P.y()-B.y()*P.x());  
	}
	


	/**
	 *
	 * @param p
	 *            query point
	 * @return true iff p is within this triangulation (in its 2D convex hull).
	 */

	public boolean contains(Point_dt p) {
		Triangle_dt tt = find(p);
		return !tt.halfplane;
	}

	/**
	 *
	 * @param x
	 *            - X cordination of the query point
	 * @param y
	 *            - Y cordination of the query point
	 * @return true iff (x,y) falls inside this triangulation (in its 2D convex
	 *         hull).
	 */
	public boolean contains(double x, double y) {
		return contains(new Point_dt(x, y));
	}

	/**
	 *
	 * @param q
	 *            Query point
	 * @return the q point with updated Z value (z value is as given the
	 *         triangulation).
	 */
	public Point_dt z(Point_dt q) {
		Triangle_dt t = find(q);
		return t.z(q);
	}

	/**
	 *
	 * @param x
	 *            - X cordination of the query point
	 * @param y
	 *            - Y cordination of the query point
	 * @return the q point with updated Z value (z value is as given the
	 *         triangulation).
	 */
	public double z(double x, double y) {
		Point_dt q = new Point_dt(x, y);
		Triangle_dt t = find(q);
		return t.z_value(q);
	}

	private void updateBoundingBox(Point_dt p) {
		double x = p.x(), y = p.y(), z = p.z();
		if (_bb_min == null) {
			_bb_min = new Point_dt(p);
			_bb_max = new Point_dt(p);
		} else {
			if (x < _bb_min.x())
				_bb_min.x = x;
			else if (x > _bb_max.x())
				_bb_max.x = x;
			if (y < _bb_min.y)
				_bb_min.y = y;
			else if (y > _bb_max.y())
				_bb_max.y = y;
			if (z < _bb_min.z)
				_bb_min.z = z;
			else if (z > _bb_max.z())
				_bb_max.z = z;
		}
	}
     /**
      * @return  The bounding rectange between the minimum and maximum coordinates
      */
     public BoundingBox getBoundingBox() {
      return new BoundingBox(_bb_min, _bb_max);
     }

	/**
	 * return the min point of the bounding box of this triangulation
	 * {{x0,y0,z0}}
	 */
	public Point_dt minBoundingBox() {
		return _bb_min;
	}

	/**
	 * return the max point of the bounding box of this triangulation
	 * {{x1,y1,z1}}
	 */
	public Point_dt maxBoundingBox() {
		return _bb_max;
	}

	/**
	 * computes the current set (vector) of all triangles and
	 * return an iterator to them.
	 *
	 * @return an iterator to the current set of all triangles.
	 */
	public Iterator<Triangle_dt> trianglesIterator() {
		if (this.size() <= 2)
			_triangles = new Vector<Triangle_dt>();
		initTriangles();
		return _triangles.iterator();
	}

	/**
	 * returns an iterator to the set of all the points on the XY-convex hull
	 * @return iterator to the set of all the points on the XY-convex hull.
	 */
	public Iterator<Point_dt> CH_vertices_Iterator() {
		Vector<Point_dt> ans = new Vector<Point_dt>();
		Triangle_dt curr = this.startTriangleHull;
		boolean cont = true;
		double x0 = _bb_min.x(), x1 = _bb_max.x();
		double y0 = _bb_min.y(), y1 = _bb_max.y();
		boolean sx, sy;
		while (cont) {
			sx = curr.p1().x() == x0 || curr.p1().x() == x1;
			sy = curr.p1().y() == y0 || curr.p1().y() == y1;
			if ((sx & sy) | (!sx & !sy)) {
				ans.add(curr.p1());
			}
			if (curr.bcnext != null && curr.bcnext.halfplane)
				curr = curr.bcnext;
			if (curr == this.startTriangleHull)
				cont = false;
		}
		return ans.iterator();
	}

	/**
	 * returns an iterator to the set of points compusing this triangulation.
	 * @return iterator to the set of points compusing this triangulation.
	 */
	public Iterator<Point_dt> verticesIterator() {
		return this._vertices.iterator();
	}

	private void initTriangles() {
		if (_modCount == _modCount2)
			return;
		if (this.size() > 2) {
			_modCount2 = _modCount;
			Vector<Triangle_dt> front = new Vector<Triangle_dt>();
			_triangles = new Vector<Triangle_dt>();
			front.add(this.startTriangle);
			while (front.size() > 0) {
				Triangle_dt t = front.remove(0);
				if (t._mark == false) {
					t._mark = true;
					_triangles.add(t);
					if (t.abnext != null && !t.abnext._mark) {
						front.add(t.abnext);
					}
					if (t.bcnext != null && !t.bcnext._mark) {
						front.add(t.bcnext);
					}
					if (t.canext != null && !t.canext._mark) {
						front.add(t.canext);
					}
				}
			}
			// _triNum = _triangles.size();
			for (int i = 0; i < _triangles.size(); i++) {
				_triangles.elementAt(i)._mark = false;
			}
		}
	}

}
