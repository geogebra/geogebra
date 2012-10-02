package geogebra.kernel.discrete.signalprocesser.voronoi.shapegeneration;
import geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;

public class ShapeGeneration {
    
    /* ********************************************************* */
    // Constants
    
    private static final int POINTSLEFT_BEFORE_CUTOFF = 100;
    
    private static final FontRenderContext FONT_RENDER = new FontRenderContext(null, true, true);
    
    /* ********************************************************* */
    // Helpful Methods (for debugging)
    
    public static Shape createShape(Area area) {
        return new PathIteratorWrapper(area.getPathIterator(null));
    }
    public static Shape createShape(PathIterator pathiter) {
        return new PathIteratorWrapper(pathiter);
    }
    public static Shape createShape(ArrayList<VPoint> points) {
        return new PathIteratorWrapper(new ListPathIterator(points));
    }
    
    public static Area createArea(ArrayList<VPoint> points) {
        return new Area(new PathIteratorWrapper(new ListPathIterator(points)));
    }
    
    
    /* ********************************************************* */
    // Fill with points
    
    public static ArrayList<VPoint> addRandomPoints(ArrayList<VPoint> points_original,
            boolean splitlonglines,
            int shapepoints, int shapepoint_mindensity,
            int internalpoints, int internal_mindensity) throws ShapeGenerationException {
        // Protect the original border points - we don't want them to be changed
        ArrayList<VPoint> points = new ArrayList<VPoint>();
        points.addAll( points_original );
        
        // Create a new array that contains the same boundary points as before
        ArrayList<VPoint> randompoints = new ArrayList<VPoint>();
        
        // Can't add points to a line, just return
        if ( points.size()<=2 ) {
            // Add points to random points
            randompoints.addAll(points);
            
            // Return list
            return randompoints;
        }
        
        // Determine boundary of points
        VPoint first = points.get(0);
        int min_x = (int)first.x, max_x = (int)first.x;
        int min_y = (int)first.y, max_y = (int)first.y;
        for ( VPoint point : points ) {
            // Check x value
            if ( point.x<min_x ) {
                min_x = (int)point.x;
            } else if ( point.x>max_x ) {
                max_x = (int)point.x;
            }
            
            // Check y value
            if ( point.y<min_y ) {
                min_y = (int)point.y;
            } else if ( point.y>max_y ) {
                max_y = (int)point.y;
            }
        }
        
        // Calculate size of image
        int width  = max_x - min_x + 1;
        int height = max_y - min_y + 1;
        
        // Verify width/height (just in case! - below will fail badly if not positive)
        if ( width<=0 ) {
            throw new ShapeGenerationException("Width of shape is zero - cannot add random points");
        } else if ( height<=0 ) {
            throw new ShapeGenerationException("Height of shape is zero - cannot add random points");
        }
        
        // Initialise array entirely to TRUE values
        int x=0, y=0;
        boolean[][] array = new boolean[width][height];
        for ( x=0 ; x<width ; x++ ) {
            for ( y=0 ; y<height ; y++ ) {
                array[x][y] = true;
            }
        }
        
        // Initialise circle definition
        boolean[][] shapepoint_circle, internal_circle;
        if ( shapepoint_mindensity==internal_mindensity ) {
            shapepoint_circle = createCircle(shapepoint_mindensity);
            internal_circle   = shapepoint_circle;
        } else {
            shapepoint_circle = createCircle(shapepoint_mindensity);
            internal_circle   = createCircle(internal_mindensity  );
        }
        
        // Remove points from points array until points is less than the number
        //  of shapepoints required
        while ( points.size()>shapepoints ) {
            // Determine a random point to remove
            int index = (int) ( Math.random() * (points.size() - 1) );
            
            // Remove that index
            points.remove(index);
        }
        
        // Clear around the radius of shape points - set radius around corner
        //  points to FALSE, everything else at the end will be TRUE (including
        //  those points inside AND outside of the shape required)
        int index;
        VPoint currpoint = null;
        for ( index=0 ; index<points.size() ; index++ ) {
            // Set previous point/get current point
            currpoint = points.get(index);
            
            // Get x/y coordinates of shape point
            x = (int)currpoint.x - min_x;
            y = (int)currpoint.y - min_y;
            
            // Check position is not already taken (if so continue)
            if (!( array[x][y] )) {
                // Remove the point from the arraylist
                points.remove(index);
                index--;
                
                // Continue
                continue;
            }
            
            // Add to random points
            randompoints.add( currpoint );
            
            // Otherwise, unset the position and the pieces around the position
            unsetCircle(-1, x, y,
                    array, width, height, shapepoint_circle, shapepoint_mindensity);
        }
        
        // Split long lines
        if ( splitlonglines && points.size()>=2 ) {
            currpoint = points.get(0);
            VPoint prevpoint;
            for ( index=1 ; index<points.size() ; index++ ) {
                // Set previous point/get current point
                prevpoint = currpoint;
                currpoint = points.get(index);
                
                if ( prevpoint.distanceTo(currpoint)>2*shapepoint_mindensity ) {
                    index = addPointsToLine(index, points, randompoints, prevpoint, currpoint, min_x, min_y,
                            array, width, height, shapepoint_circle, shapepoint_mindensity);
                }
            }
            
            // As we've been removing points from the points list we may not have
            //  a complete shape - make sure the point is closed, if not close it
            VPoint firstpoint = points.get(0);
            VPoint lastpoint  = points.get(points.size()-1);
            if (!( firstpoint.x==lastpoint.x && firstpoint.y==lastpoint.y )) {
                // Check if the line needs to be split
                if ( splitlonglines && lastpoint.distanceTo(firstpoint)>2*shapepoint_mindensity ) {
                    addPointsToLine(points.size(), points, randompoints, lastpoint, firstpoint, min_x, min_y,
                            array, width, height, shapepoint_circle, shapepoint_mindensity);
                }
                
                // Add in the final line segment
                points.add( first );
            }
        }
        
        // Reset array back to true and put on circles at internal_mindensity
        //  rather than shapepoint_mindensity
        for ( x=0 ; x<width ; x++ ) {
            for ( y=0 ; y<height ; y++ ) {
                array[x][y] = true;
            }
        }
        for ( VPoint point : points ) {
            // Get x/y coordinates of shape point
            x = (int)point.x - min_x;
            y = (int)point.y - min_y;
            
            // Unset circle
            unsetCircle(-1, x, y,
                    array, width, height, internal_circle, internal_mindensity);
        }
        
        // Create new Buffered Image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D g = (Graphics2D) image.getGraphics();
        
        // Get color constants to be used
        IndexColorModel colormodel = (IndexColorModel) image.getColorModel();
        Color BACKGROUND = new Color( colormodel.getRGB(1) );
        Color SHAPE      = new Color( colormodel.getRGB(2) );
        
        // Draw the backgound
        g.setPaint( BACKGROUND );
        g.drawRect(0, 0, width, height);
        
        // Draw the shape (using the __original__ points list; i.e. the unmodified border points)
        g.setPaint( SHAPE );
        g.fill( new PathIteratorWrapper(new ListPathIterator(points_original, min_x, min_y)) );
        
        // Set the shape into a matrix
        int SHAPE_RGB = SHAPE.getRGB();
        int pointsleft = 0;
        for ( x=0 ; x<width ; x++ ) {
            for ( y=0 ; y<height ; y++ ) {
                // If position is already false, leave it false
                if ( array[x][y]==false ) continue;
                
                // Otherwise consider turning it false if it
                //  lies outside the shape
                if ( image.getRGB(x, y)==SHAPE_RGB ) {
                    pointsleft++;
                } else {
                    array[x][y] = false;
                }
            }
        }
        
        // Dispose of the graphic, we don't need it anymore
        g.dispose();
        image = null;
        g = null;
        
        // Define the random points
        int x2=0, y2=0;
        int index_x=0, index_y=0;
        for (  ; internalpoints>0 ; internalpoints-- ) {
            // Return if no more positions left
            if ( pointsleft<=POINTSLEFT_BEFORE_CUTOFF ) {
                return randompoints;
            }
            
            // Decide on the position of the next point
            int point = (int)( Math.random() * (pointsleft - 1) + 1 );
            {foundxycoord:
                 for ( x=0 ; x<width ; x++ ) {
                     for ( y=0 ; y<height ; y++ ) {
                         if ( array[x][y] ) {
                             if ( point<=0 ) {
                                 break foundxycoord;
                             }
                             point--;
                         }
                     }
                 }
            }
            
            // Add the point to the list of points
            randompoints.add( new VPoint(x+min_x, y+min_y) );
            
            // Unset the position and the pieces around the position
            pointsleft = unsetCircle(pointsleft, x, y,
                    array, width, height, internal_circle, internal_mindensity);
        }
        
        return randompoints;
    }
    
    static private boolean[][] createCircle(int radius) {
        boolean[][] circle = new boolean[radius*2][radius*2];
        for ( int x=0 ; x<radius*2 ; x++ ) {
            for ( int y=0 ; y<radius*2 ; y++ ) {
                if ( Math.sqrt((radius-x)*(radius-x)+(radius-y)*(radius-y)) <= radius ) {
                    circle[x][y] = true;
                } else {
                    circle[x][y] = false;
                }
            }
        }
        return circle;
    }
    
    static private int addPointsToLine(int index, java.util.List<VPoint> points, ArrayList<VPoint> randompoints, VPoint prevpoint, VPoint currpoint, int min_x, int min_y, boolean[][] array, int width, int height, boolean[][] circle, int maxdensity) {
        // Determine skip
        double length = prevpoint.distanceTo(currpoint);
        int pointstoadd = (int)( length/(maxdensity+1) );
        
        // Otherwise consider whether x/y axis has the lower gradient
        double grad = (double)(prevpoint.y-currpoint.y) / (double)(prevpoint.x-currpoint.x);
        double skip_x = Math.sqrt( ( (length*length) / (pointstoadd*pointstoadd) ) / ( 1 + grad*grad ) );
        double skip_y = Math.sqrt( ( (length*length) / (pointstoadd*pointstoadd) ) / ( 1 + 1/(grad*grad) ) );
        if ( prevpoint.x>currpoint.x ) skip_x *= -1;
        if ( prevpoint.y>currpoint.y ) skip_y *= -1;
        
        // Consider pixel by pixel
        int x, y;
        for ( int pointnum=1 ; pointnum<=pointstoadd ; pointnum++ ) {
            // Determine coordinates
            x = (int)prevpoint.x-min_x + (int)( skip_x * (double)pointnum );
            y = (int)prevpoint.y-min_y + (int)( skip_y * (double)pointnum );
            
            // If position is not taken then take it
            if ( array[x][y] ) {
                // Unset the position and the pieces around this position
                unsetCircle(-1, x, y,
                        array, width, height, circle, maxdensity);
                
                // Add this point to both the points list and the
                //  random points list
                VPoint point = new VPoint(x+min_x, y+min_y);
                points.add(index, point );
                randompoints.add( point );
                
                // Increment index so as to skip over this new point
                index++;
            }
        }
        
        // Return final index
        return index;
    }
    
    static private int unsetCircle(int pointsleft, int x, int y, boolean[][] array, int width, int height, boolean[][] circle, int maxdensity ) {
        int index_x, index_y;
        for ( int x2=0 ; x2<maxdensity*2 ; x2++ ) {
            for ( int y2=0 ; y2<maxdensity*2 ; y2++ ) {
                if ( circle[x2][y2] ) {
                    index_x = x + x2 - maxdensity;
                    index_y = y + y2 - maxdensity;
                    if ( index_x>=width ) {
                        return pointsleft;
                    } else if ( index_x<0 || index_y>=height ) {
                        break;
                    } else if ( index_y<0 ) {
                        continue;
                    } else if ( array[index_x][index_y] ) {
                        pointsleft--;
                        array[index_x][index_y] = false;
                    }
                }
            }
        }
        return pointsleft;
    }
    
    /* ********************************************************* */
// Create Shape Outline
    
    /**
     * Note: shape returning is not always exactly inside the bounds - for example,
     *   if the text requested is a "g" - the foot of the g may over hang the lower
     *   bounds.
     */
    public static ArrayList<VPoint> createShapeOutline(String text, Rectangle bounds, Font font) throws ShapeGenerationException {
        // Create text layout
        TextLayout textlayout = new TextLayout(text, font, FONT_RENDER);
        
        // Get the bounds of the shape
        Rectangle2D shapebounds = textlayout.getBounds();
        
        // Tranform to the correct location
        double scale_x = (double)bounds.width / (double)shapebounds.getWidth();
        double scale_y = (double)bounds.height / (double)shapebounds.getHeight();
        double translate_x = bounds.x/scale_x - shapebounds.getX();
        double translate_y = bounds.y/scale_y - shapebounds.getY();
        AffineTransform transform = AffineTransform.getScaleInstance(scale_x, scale_y);
        transform.translate(translate_x, translate_y);
        
        // Get the outline of the shape
        Shape outline = textlayout.getOutline(transform);
        PathIterator pathiter = outline.getPathIterator(null, 0.0);
        
        // Collect the points that form the shape
        ArrayList<VPoint> points = new ArrayList<VPoint>();
        double currpoint[] = new double[2];
        while (!( pathiter.isDone() )) {
            // Consider the current element
            int type = pathiter.currentSegment(currpoint);
            if ( type==PathIterator.SEG_MOVETO ) {
                points.add( new VPoint((int)currpoint[0], (int)currpoint[1]) );
            } else if ( type==PathIterator.SEG_LINETO ) {
                points.add( new VPoint((int)currpoint[0], (int)currpoint[1]) );
            } else if ( type==PathIterator.SEG_CLOSE ) {
                break;
            } else {
                throw new RuntimeException("Unexpected type " + type + " returned");
            }
            
            // Go to the next element
            pathiter.next();
        }
        
        // Return points
        return points;
    }
    
    /* ********************************************************* */
    // Classes used by the above
    
    private static class PathIteratorWrapper implements Shape {
        private PathIterator iter;
        
        public PathIteratorWrapper(PathIterator _iter) {
            this.iter = _iter;
        }
        
        public PathIterator getPathIterator(AffineTransform at) { return iter; }
        public PathIterator getPathIterator(AffineTransform at, double flatness) { return iter; }
        
        public boolean contains(double x, double y) { throw new RuntimeException("Unimplemented method"); }
        public boolean contains(double x, double y, double w, double h) { throw new RuntimeException("Unimplemented method"); }
        public boolean contains(Point2D p) { throw new RuntimeException("Unimplemented method"); }
        public boolean contains(Rectangle2D r) { throw new RuntimeException("Unimplemented method"); }
        public Rectangle getBounds() { throw new RuntimeException("Unimplemented method"); }
        public Rectangle2D getBounds2D() { throw new RuntimeException("Unimplemented method"); }
        public boolean intersects(double x, double y, double w, double h) { throw new RuntimeException("Unimplemented method"); }
        public boolean intersects(Rectangle2D r) { throw new RuntimeException("Unimplemented method"); }
    }
    
    private static class ListPathIterator implements PathIterator {
        private int index = 0;
        private java.util.List<VPoint> points;
        
        private int offset_x;
        private int offset_y;
        
        public ListPathIterator(java.util.List<VPoint> points) {
            this(points, 0, 0);
        }
        public ListPathIterator(java.util.List<VPoint> _points, int _offset_x, int _offset_y) {
            this.points = _points;
            this.offset_x = _offset_x;
            this.offset_y = _offset_y;
        }
        
        public void resetIterator() {
            index = 0;
        }
        
        public boolean isDone() { return ( index>=points.size() ); }
        public void next() { index++; }
        
        public int currentSegment(double[] coords) {
            VPoint point = points.get(index);
            coords[0] = point.x - offset_x;
            coords[1] = point.y - offset_y;
            return getReturnValue();
        }
        public int currentSegment(float[] coords) {
            VPoint point = points.get(index);
            coords[0] = (int)point.x - offset_x;
            coords[1] = (int)point.y - offset_y;
            return getReturnValue();
        }
        private int getReturnValue() {
            if ( index==0 ) {
                return SEG_MOVETO;
            } else if ( index<(points.size()-1) ) {
                return SEG_LINETO;
            } else {
                return SEG_CLOSE;
            }
        }
        
        /**
         * WIND_NON_ZERO appears to be the only value ever returned
         *   - but their may be instances where this should not be the case
         */
        public int getWindingRule() {
            return WIND_NON_ZERO;
        }
    }
    
    /* ********************************************************* */
}
