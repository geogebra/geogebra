package org.geogebra.common.geogebra3D.euclidian3D.xr;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Interface for Mixed Reality managers and Augmented Reality managers
 * 
 * @param <T>
 *            touch event type
 */
public interface XRManagerInterface<T> {

    /** desk max distance is 50 cm */
    double DESK_DISTANCE_MAX = 0.5;

    /** 1 pixel thickness in ggb == 0.25 mm (for distance smaller than DESK_DISTANCE_MAX) */
    double THICKNESS_MIN = 0.00025;

    /** average distance for desk */
    double DESK_DISTANCE_AVERAGE = 0.25;

    /** 1 thickness in ggb &gt;&gt; THICKNESS_MIN_FACTOR;
     * this is for to get the visible thickness (on screen) quite the same as in regular 3D view */
    float THICKNESS_MIN_FACTOR = 0.001f;

    /** when setting scale at start, the ratio can't be emphasized more than 40% */
    float MAX_FACTOR_TO_EMPHASIZE = 1.40f;

    /** in projection matrix, we don't trust factors more than 10% precision */
    float PROJECT_FACTOR_RELATIVE_PRECISION = 0.10f;

	@MissingDoc
	Coords getHittingDirection();

	@MissingDoc
	Coords getHittingOrigin();

	@MissingDoc
	Coords getHittingFloor();

    /**
     * @param event event
     */
    void proceed(T event);

    /**
     * @param z z-coordinate
     * @return whether floor was hit
     */
    double checkHittingFloorZ(double z);

	@MissingDoc
	double getHittingDistance();

	@MissingDoc
	void setBackgroundColor();

    /**
     * @param backgroundStyle background style
     */
    void setBackgroundStyle(Renderer.BackgroundStyle backgroundStyle);

	@MissingDoc
	Renderer.BackgroundStyle getBackgroundStyle();

    /**
     * @param z z-coordinate of first floor
     */
    void setFirstFloor(double z);

	@MissingDoc
	void setXRScaleAtStart();

	@MissingDoc
	CoordMatrix4x4 getUndoRotationMatrix();

	@MissingDoc
	CoordMatrix4x4 getViewModelMatrix();

    /**
     * Convert XR coordinates to construction coordinates.
     * @param coords XR coordinates
     * @param ret output coordinates
     */
    void fromXRCoordsToGGBCoords(Coords coords, Coords ret);

    /**
     * @param projectionMatrix projection matrix
     */
    void setProjectionMatrixViewForXR(CoordMatrix4x4 projectionMatrix);

	@MissingDoc
	void fitThickness();

	@MissingDoc
	float getXRScaleFactor();

	@MissingDoc
	void resetScaleFromXR();

	@MissingDoc
	String getXRRatioInString();

    /**
     * @param ratio XR ratio
     */
    void setXRRatio(double ratio);

    /**
     * @param ratioIsShown whether to show the ratio
     */
    void setRatioIsShown(boolean ratioIsShown);

	@MissingDoc
	void calculateAndShowRatio();
}
