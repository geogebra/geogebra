package org.geogebra.common.awt;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Affine transform.
 */
public interface GAffineTransform {

	/**
	 * Sets this transform to a copy of the transform in the specified
	 * <code>AffineTransform</code> object.
	 * @param Tx0 the <code>AffineTransform</code> object from which to
	 * copy the transform
	 */
	void setTransform(GAffineTransform Tx0);

	/**
	 * Set transform from matrix entries.
	 */
	void setTransform(double m00, double m10, double m01, double m11,
			double m02, double m12);

	/**
	 * Concatenates an <code>AffineTransform</code> <code>Tx</code> to
	 * this <code>AffineTransform</code> Cx in the most commonly useful
	 * way to provide a new user space
	 * that is mapped to the former user space by <code>Tx</code>.
	 * Cx is updated to perform the combined transformation.
	 * Transforming a point p by the updated transform Cx' is
	 * equivalent to first transforming p by <code>Tx</code> and then
	 * transforming the result by the original transform Cx like this:
	 * Cx'(p) = Cx(Tx(p))
	 * In matrix notation, if this transform Cx is
	 * represented by the matrix [this] and <code>Tx</code> is represented
	 * by the matrix [Tx] then this method does the following:
	 * <pre>
	 *          [this] = [this] x [Tx]
	 * </pre>
	 * @param Tx0 the <code>AffineTransform</code> object to be
	 * concatenated with this <code>AffineTransform</code> object.
	 */
	void concatenate(GAffineTransform Tx0);

	/**
	 * Returns the X coordinate scaling element (m00) of the 3x3
	 * affine transformation matrix.
	 * @return a double value that is the X coordinate of the scaling
	 *  element of the affine transformation matrix.
	 * @see #getMatrix
	 */
	double getScaleX();

	/**
	 * Returns the Y coordinate scaling element (m11) of the 3x3
	 * affine transformation matrix.
	 * @return a double value that is the Y coordinate of the scaling
	 *  element of the affine transformation matrix.
	 * @see #getMatrix
	 */
	double getScaleY();

	/**
	 * Returns the X coordinate shearing element (m01) of the 3x3
	 * affine transformation matrix.
	 * @return a double value that is the X coordinate of the shearing
	 *  element of the affine transformation matrix.
	 * @see #getMatrix
	 */
	double getShearX();

	/**
	 * Returns the Y coordinate shearing element (m10) of the 3x3
	 * affine transformation matrix.
	 * @return a double value that is the Y coordinate of the shearing
	 *  element of the affine transformation matrix.
	 * @see #getMatrix
	 */
	double getShearY();

	/**
	 * Returns a new {@link GShape} object defined by the geometry of the
	 * specified <code>Shape</code> after it has been transformed by
	 * this transform.
	 * @param pSrc the specified <code>Shape</code> object to be
	 * transformed by this transform.
	 * @return a new <code>Shape</code> object that defines the geometry
	 * of the transformed <code>Shape</code>, or null if {@code pSrc} is null.
	 */
	GShape createTransformedShape(GShape pSrc);

	/**
	 * Transforms the specified <code>ptSrc</code> and stores the result
	 * in <code>ptDst</code>.
	 * If <code>ptDst</code> is <code>null</code>, a new {@link GPoint2D}
	 * object is allocated and then the result of the transformation is
	 * stored in this object.
	 * In either case, <code>ptDst</code>, which contains the
	 * transformed point, is returned for convenience.
	 * If <code>ptSrc</code> and <code>ptDst</code> are the same
	 * object, the input point is correctly overwritten with
	 * the transformed point.
	 * @param src the specified <code>Point2D</code> to be transformed
	 * @param dest the specified <code>Point2D</code> that stores the
	 * result of transforming <code>ptSrc</code>
	 * @return the <code>ptDst</code> after transforming
	 * <code>ptSrc</code> and storing the result in <code>ptDst</code>.
	 */
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

	@MissingDoc
	GAffineTransform createInverse() throws Exception;

	/**
	 * Concatenates this transform with a scaling transformation.
	 * This is equivalent to calling concatenate(S), where S is an
	 * <code>AffineTransform</code> represented by the following matrix:
	 * <pre>
	 *          [   sx   0    0   ]
	 *          [   0    sy   0   ]
	 *          [   0    0    1   ]
	 * </pre>
	 * @param sx the factor by which coordinates are scaled along the
	 * X axis direction
	 * @param sy the factor by which coordinates are scaled along the
	 * Y axis direction
	 */
	void scale(double sx, double sy);

	/**
	 * Concatenates this transform with a translation transformation.
	 * This is equivalent to calling concatenate(T), where T is an
	 * <code>AffineTransform</code> represented by the following matrix:
	 * <pre>
	 *          [   1    0    tx  ]
	 *          [   0    1    ty  ]
	 *          [   0    0    1   ]
	 * </pre>
	 * @param tx the distance by which coordinates are translated in the
	 * X axis direction
	 * @param ty the distance by which coordinates are translated in the
	 * Y axis direction
	 */
	void translate(double tx, double ty);

	@MissingDoc
	double getTranslateX();

	@MissingDoc
	double getTranslateY();

	/**
	 * Concatenates this transform with a rotation transformation.
	 * This is equivalent to calling concatenate(R), where R is an
	 * <code>AffineTransform</code> represented by the following matrix:
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
	void rotate(double theta);

	@MissingDoc
	boolean isIdentity();

	/**
	 * Sets this transform to a translation transformation.
	 * The matrix representing this transform becomes:
	 * <pre>
	 *          [   1    0    tx  ]
	 *          [   0    1    ty  ]
	 *          [   0    0    1   ]
	 * </pre>
	 * @param tx the distance by which coordinates are translated in the
	 * X axis direction
	 * @param ty the distance by which coordinates are translated in the
	 * Y axis direction
	 */
	void setToTranslation(double tx, double ty);

	/**
	 * Sets this transform to a scaling transformation.
	 * The matrix representing this transform becomes:
	 * <pre>
	 *          [   sx   0    0   ]
	 *          [   0    sy   0   ]
	 *          [   0    0    1   ]
	 * </pre>
	 * @param sx the factor by which coordinates are scaled along the
	 * X axis direction
	 * @param sy the factor by which coordinates are scaled along the
	 * Y axis direction
	 */
	void setToScale(double sx, double sy);

	/**
	 * Retrieves the 6 specifiable values in the 3x3 affine transformation
	 * matrix and places them into an array of double precisions values.
	 * The values are stored in the array as
	 * {&nbsp;m00&nbsp;m10&nbsp;m01&nbsp;m11&nbsp;m02&nbsp;m12&nbsp;}.
	 * An array of 4 doubles can also be specified, in which case only the
	 * first four elements representing the non-transform
	 * parts of the array are retrieved and the values are stored into
	 * the array as {&nbsp;m00&nbsp;m10&nbsp;m01&nbsp;m11&nbsp;}
	 * @param flatmatrix the double array used to store the returned
	 * values.
	 * @see #getScaleX
	 * @see #getScaleY
	 * @see #getShearX
	 * @see #getShearY
	 * @see #getTranslateX
	 * @see #getTranslateY
	 */
	void getMatrix(double[] flatmatrix);

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
