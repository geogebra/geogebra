package geogebra.common.kernel.algos;

import geogebra.common.euclidian.draw.DrawAngle;

/**
 * Tagging interface for algoritms producing visible angles
 * @author kondr
 *
 */
public interface AngleAlgo {
	boolean updateDrawInfo(double[]m,double[] firstVec, DrawAngle drawable);
}
