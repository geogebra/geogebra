package com.vividsolutions.jts.operation.overlay.snap;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.overlay.OverlayOp;
import com.vividsolutions.jts.operation.overlay.validate.OverlayResultValidator;


/**
 * Performs an overlay operation using snapping and enhanced precision
 * to improve the robustness of the result.
 * This class only uses snapping
 * if an error is detected when running the standard JTS overlay code.
 * Errors detected include thrown exceptions 
 * (in particular, {@link TopologyException})
 * and invalid overlay computations.
 *     
 * @author Martin Davis
 * @version 1.7
 */
public class SnapIfNeededOverlayOp
{
  public static Geometry overlayOp(Geometry g0, Geometry g1, int opCode)
  {
  	SnapIfNeededOverlayOp op = new SnapIfNeededOverlayOp(g0, g1);
  	return op.getResultGeometry(opCode);
  }

  public static Geometry intersection(Geometry g0, Geometry g1)
  {
     return overlayOp(g0, g1, OverlayOp.INTERSECTION);
  }

  public static Geometry union(Geometry g0, Geometry g1)
  {
     return overlayOp(g0, g1, OverlayOp.UNION);
  }

  public static Geometry difference(Geometry g0, Geometry g1)
  {
     return overlayOp(g0, g1, OverlayOp.DIFFERENCE);
  }

  public static Geometry symDifference(Geometry g0, Geometry g1)
  {
     return overlayOp(g0, g1, OverlayOp.SYMDIFFERENCE);
  }
  
  private Geometry[] geom = new Geometry[2];

  public SnapIfNeededOverlayOp(Geometry g1, Geometry g2)
  {
    geom[0] = g1;
    geom[1] = g2;
  }

  public Geometry getResultGeometry(int opCode)
  {
    Geometry result = null;
    boolean isSuccess = false;
    try {
      result = OverlayOp.overlayOp(geom[0], geom[1], opCode); 
      boolean isValid = true;
      // not needed if noding validation is used
//      boolean isValid = OverlayResultValidator.isValid(geom[0], geom[1], OverlayOp.INTERSECTION, result);
      if (isValid)
      	isSuccess = true;
    
    }
    catch (Exception ex) {
    	// ignore this exception, since the operation will be rerun
//    	System.out.println(ex.getMessage());
//    	ex.printStackTrace();
    }
    if (! isSuccess) {
    	// this may still throw an exception - just let it go if it does
      result = SnapOverlayOp.overlayOp(geom[0], geom[1], opCode);
    }
    return result;
  }
}