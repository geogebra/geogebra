package geogebra.kernel;

import geogebra.kernel.kernelND.GeoPointND;

/**
 * Normalized path that uses a path parameter in range [0,1].
 * @author Markus Hohenwarter
 */
public class PathNormalizer implements Path {
	
	private Path parentPath;
	
	/**
	 * Creates a normalized path with parameter range [0,1] for the given parent path 
	 * with an arbitrary parameter range.
	 * @param parentPath
	 */
	public PathNormalizer(Path parentPath) {
		this.parentPath = parentPath;
	}
	
	/**
	 * Converts path parameter value tn from range [0, 1] to [min, max].
	 * 
	 * @param tn parameter value in [0,1]
	 * @param min of range [min, max]
	 * @param max of range [min, max]
	 * @return parameter value in [min, max]
	 */
	public static double toParentPathParameter(double tn, double min, double max) {
		
		// for Points as Paths (min=max=0)
		if (min == max) return min;

		if (tn < 0) tn = 0; 
		else if (tn > 1) tn = 1;

		if (min == Double.NEGATIVE_INFINITY) { 
			if (max == Double.POSITIVE_INFINITY) {
				// [0,1] -> (-infinite, +infinite)
				// first: (0,1) -> (-1,1), then use infFunction(-1 ... 1)
				return infFunction(2*tn - 1);									
			}
			else { 
				// [0,1] -> (-infinite, max_param]
				// max_param + infFunction(-1 ... 0)	
				return max + infFunction(tn - 1);
			}
		} 
		else {
			if (max == Double.POSITIVE_INFINITY){
				// [0,1] -> [min_param, +infinite)
				// min_param + infFunction(0 ... 1)	
				return min + infFunction(tn);			
			}
			else { 
				// [0,1] -> [min_param, max_param]
				return (1-tn) * min + tn * max;				
			}
		}
	}
	
	/**
	 * Converts path parameter value t from range [min, max] to [0, 1].
	 * @param t parameter to be normalized
	 * 
	 * @param min of range [min, max]
	 * @param max of range [min, max]
	 * @return parameter value in [0,1] 
	 */
	public static double toNormalizedPathParameter(double t, double min, double max) {
		
		// for Points as Paths (min=max=0)
		if (min == max) return 0;
		
		if (t < min) t = min; else if (t > max) t = max;

		if (min == Double.NEGATIVE_INFINITY) { 
			if (max == Double.POSITIVE_INFINITY) {
				// (-infinite, +infinite) -> [0,1]
				if (t == Double.NEGATIVE_INFINITY) 
					return 0;
				else if (t == Double.POSITIVE_INFINITY) 
					return 1;
				else {
					// (-infinite, +infinite) -> (0,1)
					// solve for tn: t = infFunction(2*tn - 1);
					return 0.5 + 0.5 * inverseInfFunction(t);	
				}
			}
			else { 
				// (-infinite, max] -> [0,1]
				if (t == Double.NEGATIVE_INFINITY) 
					return 0;
				else {
					// solve for tn: t = max + infFunction(tn - 1);
					return 1 + inverseInfFunction(t - max);
				}
			}
		} 
		else {
			if (max == Double.POSITIVE_INFINITY){
				// [min, +infinite) -> [0,1]
				// solve for tn: t = min + infFunction(tn)	
				return inverseInfFunction(t - min);			
			}
			else { 
				// [min, max] -> [0,1]
				// solve for tn: t = (1 - tn) * min + tn * max;
				return (t - min) / (max - min);
			}
		}
	}
	
	/**
	 * Converts path parameter from range [0, 1] to [parentPath.min, parentPath.max].
	 * 
	 * @param pp path parameter value in [parentPath.min, parentPath.max]
	 */
	private void toParentPathParameter(PathParameter pp) {
		pp.setT(toParentPathParameter(pp.getT(), parentPath.getMinParameter(), parentPath.getMaxParameter()));
	}
	
	/**
	 * Converts path parameter from range [parentPath.min, parentPath.max] to [0, 1].
	 * 
	 * @param pp path parameter to be normalized
	 */
	private void toNormalizedPathParameter(PathParameter pp) {
		pp.setT(toNormalizedPathParameter(pp.getT(), parentPath.getMinParameter(), parentPath.getMaxParameter()));
	}
	
	/**
	 * Function t: (-1, 1) -> (-inf, +inf)
	 * @param t
	 * @return parameter in (-1,1) to be mapped into all reals
	 */
	public static double infFunction(double t) {		
		return  t /  (1 - Math.abs(t));
	}
	
	/**
	 * Function z: (-inf, +inf) -> (-1, 1)
	 * @param z
	 * @return arbitrary parameter to be mapped into (-1,1)
	 */
	public static double inverseInfFunction(double z) {
		if (z >= 0) {
			return z / (1 + z);
		} else {
			return z / (1 - z);
		}
	}

	public boolean isOnPath(GeoPointND PI, double eps) {
		// path parameter from [0,1] -> [parentPath.min, parentPath.max]
		toParentPathParameter(PI.getPathParameter());
		
		// delegate to parent path
		boolean result = parentPath.isOnPath(PI, eps);
		
		// path parameter from [parentPath.min, parentPath.max] -> [0,1]
		toNormalizedPathParameter(PI.getPathParameter());
		return result;
	}

	public void pathChanged(GeoPointND PI) {
		// path parameter from [0,1] -> [parentPath.min, parentPath.max]
		toParentPathParameter(PI.getPathParameter());
		
		// delegate to parent path
		parentPath.pathChanged(PI);
		
		// path parameter from [parentPath.min, parentPath.max] -> [0,1]
		toNormalizedPathParameter(PI.getPathParameter());	
	}

	public void pointChanged(GeoPointND PI) {
		// path parameter from [0,1] -> [parentPath.min, parentPath.max]
		toParentPathParameter(PI.getPathParameter());
	
		// delegate to parent path
		parentPath.pointChanged(PI);
		
		// path parameter from [parentPath.min, parentPath.max] -> [0,1]
		toNormalizedPathParameter(PI.getPathParameter());	
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public double getMaxParameter() {
		return 1;
	}

	public double getMinParameter() {
		return 0;
	}

	public boolean isClosedPath() {
		return parentPath.isClosedPath();
	}

	public GeoElement toGeoElement() {
		return parentPath.toGeoElement();
	}
	
	/*
	 * TEST
	 */
//	public static void main(String[] args) {
//		
//		for (int i=0; i<10; i++) {
//			double t = 2*Math.random()-1;
//			System.out.println("t = " + t);
//			
//			double tn = toNormalizedPathParameter(t, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
//			double t2= toParentPathParameter(tn, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
//			System.out.println("\ttn = " + tn + ", error: " + (t2-t));
//			
//			tn = toNormalizedPathParameter(t, Double.NEGATIVE_INFINITY, 3);
//			t2= toParentPathParameter(tn, Double.NEGATIVE_INFINITY, 3);
//			System.out.println("\ttn = " + tn + ", error: " + (t2-t));
//				
//			tn = toNormalizedPathParameter(t, -7, Double.POSITIVE_INFINITY);
//			t2= toParentPathParameter(tn, -7, Double.POSITIVE_INFINITY);
//			System.out.println("\ttn = " + tn + ", error: " + (t2-t));
//			
//			tn = toNormalizedPathParameter(t, -5, 9);
//			t2= toParentPathParameter(tn, -5, 9);
//			System.out.println("\ttn = " + tn + ", error: " + (t2-t));
//		}
//		
//	}

}
