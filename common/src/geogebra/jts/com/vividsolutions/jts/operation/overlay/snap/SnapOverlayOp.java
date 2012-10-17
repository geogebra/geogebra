package com.vividsolutions.jts.operation.overlay.snap;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.overlay.OverlayOp;
import com.vividsolutions.jts.precision.CommonBitsRemover;

/**
 * Performs an overlay operation using snapping and enhanced precision
 * to improve the robustness of the result.
 * This class <i>always</i> uses snapping.  
 * This is less performant than the standard JTS overlay code, 
 * and may even introduce errors which were not present in the original data.
 * For this reason, this class should only be used 
 * if the standard overlay code fails to produce a correct result. 
 *  
 * @author Martin Davis
 * @version 1.7
 */
public class SnapOverlayOp
{
  public static Geometry overlayOp(Geometry g0, Geometry g1, int opCode)
  {
  	SnapOverlayOp op = new SnapOverlayOp(g0, g1);
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
  private double snapTolerance;

  public SnapOverlayOp(Geometry g1, Geometry g2)
  {
    geom[0] = g1;
    geom[1] = g2;
    computeSnapTolerance();
  }
  private void computeSnapTolerance() 
  {
		snapTolerance = GeometrySnapper.computeOverlaySnapTolerance(geom[0], geom[1]);

		// System.out.println("Snap tol = " + snapTolerance);
	}

  public Geometry getResultGeometry(int opCode)
  {
    Geometry[] prepGeom = snap();
    Geometry result = OverlayOp.overlayOp(prepGeom[0], prepGeom[1], opCode);
    return prepareResult(result);	
  }
  
  private Geometry[] snap()
  {
    Geometry[] remGeom = removeCommonBits(geom);
  	
  	// MD - testing only
//  	Geometry[] remGeom = geom;
    
    Geometry[] snapGeom = GeometrySnapper.snap(remGeom[0], remGeom[1], snapTolerance);
    // MD - may want to do this at some point, but it adds cycles
//    checkValid(snapGeom[0]);
//    checkValid(snapGeom[1]);

    /*
    System.out.println("Snapped geoms: ");
    System.out.println(snapGeom[0]);
    System.out.println(snapGeom[1]);
    */
    return snapGeom;
  }

  private Geometry prepareResult(Geometry geom)
  {
    cbr.addCommonBits(geom);
    return geom;
  }

  private CommonBitsRemover cbr;

  private Geometry[] removeCommonBits(Geometry[] geom)
  {
    cbr = new CommonBitsRemover();
    cbr.add(geom[0]);
    cbr.add(geom[1]);
    Geometry remGeom[] = new Geometry[2];
    remGeom[0] = cbr.removeCommonBits((Geometry) geom[0].clone());
    remGeom[1] = cbr.removeCommonBits((Geometry) geom[1].clone());
    return remGeom;
  }
  
  private void checkValid(Geometry g)
  {
  	if (! g.isValid()) {
  		System.out.println("Snapped geometry is invalid");
  	}
  }
}