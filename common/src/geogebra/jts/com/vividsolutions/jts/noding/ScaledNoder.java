package com.vividsolutions.jts.noding;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.util.*;

/**
 * Wraps a {@link Noder} and transforms its input
 * into the integer domain.
 * This is intended for use with Snap-Rounding noders,
 * which typically are only intended to work in the integer domain.
 * Offsets can be provided to increase the number of digits of available precision.
 *
 * @version 1.7
 */
public class ScaledNoder
    implements Noder
{
  private Noder noder;
  private double scaleFactor;
  private double offsetX;
  private double offsetY;
  private boolean isScaled = false;

  public ScaledNoder(Noder noder, double scaleFactor) {
    this(noder, scaleFactor, 0, 0);
  }

  public ScaledNoder(Noder noder, double scaleFactor, double offsetX, double offsetY) {
    this.noder = noder;
    this.scaleFactor = scaleFactor;
    // no need to scale if input precision is already integral
    isScaled = ! isIntegerPrecision();
  }

  public boolean isIntegerPrecision() { return scaleFactor == 1.0; }

  public Collection getNodedSubstrings()
  {
    Collection splitSS = noder.getNodedSubstrings();
    if (isScaled) rescale(splitSS);
    return splitSS;
  }

  public void computeNodes(Collection inputSegStrings)
  {
    Collection intSegStrings = inputSegStrings;
    if (isScaled)
      intSegStrings = scale(inputSegStrings);
    noder.computeNodes(intSegStrings);
  }

  private Collection scale(Collection segStrings)
  {
//  	System.out.println("Scaled: scaleFactor = " + scaleFactor);
    return CollectionUtil.transform(segStrings,
                                    new CollectionUtil.Function() {
      public Object execute(Object obj) {
        SegmentString ss = (SegmentString) obj;
        return new NodedSegmentString(scale(ss.getCoordinates()), ss.getData());
      }
                                    }
      );
  }

  private Coordinate[] scale(Coordinate[] pts)
  {
    Coordinate[] roundPts = new Coordinate[pts.length];
    for (int i = 0; i < pts.length; i++) {
      roundPts[i] = new Coordinate(
          Math.round((pts[i].x - offsetX) * scaleFactor),
          Math.round((pts[i].y - offsetY) * scaleFactor)
        );
    }
    Coordinate[] roundPtsNoDup = CoordinateArrays.removeRepeatedPoints(roundPts);
    return roundPtsNoDup;
  }

  //private double scale(double val) { return (double) Math.round(val * scaleFactor); }

  private void rescale(Collection segStrings)
  {
//  	System.out.println("Rescaled: scaleFactor = " + scaleFactor);
  	CollectionUtil.apply(segStrings,
  			new CollectionUtil.Function() {
  		public Object execute(Object obj) {
  			SegmentString ss = (SegmentString) obj;
  			rescale(ss.getCoordinates());
  			return null;
  		}
  	}
  	);
  }

  private void rescale(Coordinate[] pts)
  {
    for (int i = 0; i < pts.length; i++) {
      pts[i].x = pts[i].x / scaleFactor + offsetX;
      pts[i].y = pts[i].y / scaleFactor + offsetY;
    }
  }

  //private double rescale(double val) { return val / scaleFactor; }
}