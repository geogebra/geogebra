package org.geogebra.common.awt;

public interface GAffineTransform {
	void setTransform(GAffineTransform a);

	/**
	 * Set transform from matrix entries.
	 */
	void setTransform(double m00, double m10, double m01, double m11,
			double m02, double m12);

	void concatenate(GAffineTransform a);

	double getScaleX();

	double getScaleY();

	double getShearX();

	double getShearY();

	GShape createTransformedShape(GShape shape);

	GPoint2D transform(GPoint2D src, GPoint2D dest);

	/**
	 * @param srcCoordinates source coordinates x1, y1, x2, y2 ...
	 * @param srcOffset offset in source array
	 * @param destCoordinates output array
	 * @param destOffset offset in destination array
	 * @param nPoints number of points to transform
	 */
	void transform(double[] srcCoordinates, int srcOffset, double[] destCoordinates,
			int destOffset, int nPoints);

	GAffineTransform createInverse() throws Exception;

	void scale(double xscale, double yscale);

	void translate(double ax, double ay);

	double getTranslateX();

	double getTranslateY();

	// void transform(float[] pointCoords, int pointIdx, double[] coords,
	// int j, int k);

	void rotate(double theta);

	boolean isIdentity();

	void setToTranslation(double tx, double ty);

	void setToScale(double sx, double sy);

	void getMatrix(double[] m);

	/**
	 * Sets this transform to a rotation transformation.
	 * The matrix representing this transform becomes:
	 * <pre>
	 *          [   cos(theta)    -sin(theta)    0   ]
	 *          [   sin(theta)     cos(theta)    0   ]
	 *          [       0              0         1   ]
	 * </pre>
	 * Rotating by a positive angle theta rotates points on the positive
	 * X axis toward the positive Y axis.
	 * Note also the discussion of
	 * <a href="#quadrantapproximation">Handling 90-Degree Rotations</a>
	 * above.
	 * @param theta the angle of rotation measured in radians
	 */
	void setToRotation(double theta);

	/**
	 * Sets this transform to a translated rotation transformation.
	 * This operation is equivalent to translating the coordinates so
	 * that the anchor point is at the origin (S1), then rotating them
	 * about the new origin (S2), and finally translating so that the
	 * intermediate origin is restored to the coordinates of the original
	 * anchor point (S3).
	 * <p>
	 * This operation is equivalent to the following sequence of calls:
	 * <pre>
	 *     setToTranslation(anchorx, anchory); // S3: final translation
	 *     rotate(theta);                      // S2: rotate around anchor
	 *     translate(-anchorx, -anchory);      // S1: translate anchor to origin
	 * </pre>
	 * The matrix representing this transform becomes:
	 * <pre>
	 *          [   cos(theta)    -sin(theta)    x-x*cos+y*sin  ]
	 *          [   sin(theta)     cos(theta)    y-x*sin-y*cos  ]
	 *          [       0              0               1        ]
	 * </pre>
	 * Rotating by a positive angle theta rotates points on the positive
	 * X axis toward the positive Y axis.
	 * Note also the discussion of
	 * <a href="#quadrantapproximation">Handling 90-Degree Rotations</a>
	 * above.
	 *
	 * @param theta the angle of rotation measured in radians
	 * @param anchorx the X coordinate of the rotation anchor point
	 * @param anchory the Y coordinate of the rotation anchor point
	 */
	void setToRotation(double theta, double anchorx, double anchory);

}
