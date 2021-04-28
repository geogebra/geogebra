package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.xr.XRManagerInterface;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.geos.AnimationExportSlider;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;

/**
 *
 * Used for openGL display.
 * <p>
 * It provides:
 * <ul>
 * <li>methods for displaying {@link Drawable3D}, with painting parameters</li>
 * <li>methods for picking object</li>
 * </ul>
 *
 * @author ggb3D
 *
 */
public abstract class Renderer {

	/**
	 * used for showing depth instead of color (for testing)
	 */
	final public static boolean TEST_DRAW_DEPTH_TO_COLOR = false;

	/**
	 * renderer type (shader or not)
	 */
	public enum RendererType {
		/** use shaders */
		SHADER,
		/** use GL2 specs (no shaders) */
		GL2,
		/** not specified at start */
		NOT_SPECIFIED
	}

	private RendererType type;

	// layers
	/** shift for planes layer to avoid z-fighting */
	public static final int LAYER_PLANE_SHIFT = -1;
	/** shift for angles layer to avoid z-fighting */
	public static final int LAYER_ANGLE_SHIFT = 1;
	/** min value for layers */
	public static final int LAYER_MIN = LAYER_PLANE_SHIFT;
	/** min value for layers (string for shaders) */
	public static final String LAYER_MIN_STRING_WITH_OP = "" + LAYER_MIN;
	/** factor for coding layers (for shaders) */
	public static final int LAYER_FACTOR_FOR_CODING = 2;
	/** default layer */
	public static final int LAYER_DEFAULT = 0;
    /** layer to ensure no z-fighting between text and its background */
    public static final int LAYER_FOR_TEXTS = 5;

	/** 3D view */
	protected EuclidianView3D view3D;

	/** matrix for drawing */
	protected CoordMatrix4x4 m_drawingMatrix;

	/** geometries manager */
	protected Manager geometryManager;

	private Textures textures;

	/** ambient factor for light #0 */
	public static final float AMBIENT_0 = 0.5f;
	/** ambient factor for light #1 */
	public static final float AMBIENT_1 = 0.4f;
	/** if clipping is enabled */
	public boolean enableClipPlanes;
	private boolean waitForUpdateClipPlanes = false;
	static final private float SQRT2_DIV2 = (float) Math.sqrt(2) / 2;
	/** light position for web */
	public static final float[] LIGHT_POSITION_W = { SQRT2_DIV2, 0f,
			SQRT2_DIV2 };
	/** light position for desktop */
	static final public float[] LIGHT_POSITION_D = { SQRT2_DIV2, 0f, SQRT2_DIV2,
			0f };
	/** if needs to export image */
	protected boolean needExportImage = false;

	private boolean exportImageForThumbnail = false;

	private boolean waitForUpdateClearColor = false;

	/** screen left (in pixels) */
	protected int left = 0;
	/** screen right (in pixels) */
	protected int right = 640;
	/** screen bottom (in pixels) */
	protected int bottom = 0;
	/** screen top (in pixels) */
	protected int top = 480;
	/** eye to screen distance */
	public double[] eyeToScreenDistance = new double[2];
	/** perspective eye position */
	public Coords perspEye;

	/** eye position */
	public double[] glassesEyeX = new double[2];
	/** eye position */
	public double[] glassesEyeY = new double[2];

	/** left eye index */
	public static final int EYE_LEFT = 0;
	/** right eye index */
	public static final int EYE_RIGHT = 1;
	/** eye index */
	public int eye = EYE_LEFT;

	/** oblique projection x factor */
	public double obliqueX;
	/** oblique projection y factor */
	public double obliqueY;
	private Coords obliqueOrthoDirection; // direction "orthogonal" to the
											// screen (i.e. not visible)
	private ExportType exportType = ExportType.NONE;
	private int export_n;
	private double export_val;
	private double export_min;
	private double export_max;
	private double export_step;
	private int export_i;
	private AnimationExportSlider export_num;
	private Runnable export3DRunnable;

	// AR
    private boolean arShouldStart = false;

	/** shift for getting alpha value */
	private static final int ALPHA_SHIFT = 24;

	private RendererImpl rendererImpl;
	private Hitting hitting;

	/**
	 * background type (only for AR)
     * Order matters and corresponds to order in settings
	 */
	public enum BackgroundStyle {
		/** no background, ie we see camera image */
		NONE,
		/** transparent background like a filter */
		TRANSPARENT,
		/** opaque: camera image is not visible */
		OPAQUE
	}

	/**
	 * creates a renderer linked to an {@link EuclidianView3D}
	 *
	 * @param view
	 *            the {@link EuclidianView3D} linked to
	 * @param type
	 *            renderer type
	 */
	public Renderer(EuclidianView3D view, RendererType type) {
		this.view3D = view;
		this.type = type;
		hitting = new Hitting(view3D);
	}

	/**
	 *
	 * @return new Textures object
	 */
	protected Textures newTextures() {
		return new Textures(this);
	}

    /**
     * Start AR session
     */
	public void setARShouldStart() {
        arShouldStart = true;
    }

	/**
	 * start AR if needed
	 */
    public void mayStartAR() {
        if (arShouldStart) {
			doStartAR();
            arShouldStart = false;
        }
    }

	/**
	 * do start AR
	 */
	abstract protected void doStartAR();

	/**
     * @param ret Hitting Direction from AR. Override in RendererWithImplA
	 */
    public void getHittingDirectionAR(Coords ret) {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
            arManager.fromXRCoordsToGGBCoords(arManager.getHittingDirection(), ret);
			ret.normalize();
		}
    }

	/**
	 * @param ret
     *            Hitting Origin from AR. Override in RendererWithImplA
	 */
	public void getHittingOriginAR(Coords ret) {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
            arManager.fromXRCoordsToGGBCoords(arManager.getHittingOrigin(), ret);
		}
	}

    /**
     * @param ret Hitting floor from AR. Override in RendererWithImplA
     *
     * @return true if there is an hitting on floor
     */
    public boolean getHittingFloorAR(Coords ret) {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
			Coords hittingFloor = arManager.getHittingFloor();
			if (hittingFloor == null) {
				return false;
			}
            arManager.fromXRCoordsToGGBCoords(hittingFloor, ret);
			return true;
		}
		return false;

    }

    /**
     *
     * @return current hitting distance (in AR)
     */
    public double getHittingDistanceAR() {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
			return arManager.getHittingDistance();
		}
		return 0;
    }

    /**
     * Check if z coordinate should be changed regarding current hit (in AR)
     * @param z calculated z value
     * @return hit z value (if already computed)
     */
    public double checkHittingFloorZ(double z) {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
			return arManager.checkHittingFloorZ(z) + view3D.getARFloorShift();
		}
		return 0;
    }

	/**
	 * dummy renderer (when no GL available)
	 */
	public Renderer() {

	}

	/**
	 *
	 */
	protected void updateViewAndDrawables() {

		view3D.update();
		view3D.getDrawList3D().updateManagerBuffers(this);
		view3D.updateOwnDrawablesNow();

		// update 3D drawables
		view3D.updateDrawables();

		// say that 3D view changed has been performed
		view3D.resetViewChanged();
	}

	/**
	 * init rendering values (clear color buffer, etc.)
	 */
	protected void initRenderingValues() {
		// clear color buffer
		if (!view3D.getCompanion().isStereoBuffered()) {
			clearColorBuffer();
		}
		rendererImpl.initLighting();
		rendererImpl.initRenderingValues();
	}

	/**
	 * set runnable to do a 3D export on next frame
	 *
	 * @param runnable
	 *            export handler
	 */
	public void setExport3D(Runnable runnable) {
		export3DRunnable = runnable;
	}

	/**
	 * may update the GL clear color
	 */
	public void mayUpdateClearColor() {
		if (waitForUpdateClearColor) {
			updateClearColor();
			waitForUpdateClearColor = false;
		}
	}

	/**
	 * draw the scene
	 */
	public void drawScene() {

		// update 3D controller
		((EuclidianController3D) view3D.getEuclidianController())
				.updateInput3D();

		rendererImpl.useShaderProgram();

		// clip planes
		if (waitForUpdateClipPlanes) {
			// Application.debug(enableClipPlanes);
			if (enableClipPlanes) {
				rendererImpl.enableClipPlanes();
			} else {
				rendererImpl.disableClipPlanes();
			}
			waitForUpdateClipPlanes = false;
		}

		// update 3D controller
		((EuclidianController3D) view3D.getEuclidianController()).update();

		// long time = System.currentTimeMillis();
		// update 3D view and drawables
		updateViewAndDrawables();
		// Log.debug("======= UPDATE : "+(System.currentTimeMillis() - time));

		if (needExportImage) {
			rendererImpl.selectFBO();
		}

		mayUpdateClearColor();

		// init rendering values
		initRenderingValues();

		// time = System.currentTimeMillis();

		if (view3D
				.getProjection() == EuclidianView3DInterface.PROJECTION_GLASSES) {

			// left eye
			setDrawLeft();
			rendererImpl.clearDepthBuffer();
			setView();
			draw();

			// right eye
			setDrawRight();
			rendererImpl.clearDepthBufferForSecondAnaglyphFilter();
			setView();
			draw();

		} else {
			if (view3D.getCompanion().isStereoBuffered()) {
				// we draw the same image on both left/right buffers
				setBufferLeft();
				clearColorBuffer();
				rendererImpl.clearDepthBuffer();
				setView();
				draw();

				setBufferRight();
				clearColorBuffer();
				rendererImpl.clearDepthBuffer();
				setView();
				draw();
			} else {
				rendererImpl.clearDepthBuffer();
				if (!view3D.isXREnabled()) {
					setView();
				}
				draw();
			}
		}

		// Log.debug("======= DRAW : "+(System.currentTimeMillis() - time));

		// prepare correct color mask for next clear
		rendererImpl.setColorMask(ColorMask.ALL);

        endOfDrawScene();
	}

	/**
	 * called at end of scene drawing
	 */
	public void endOfDrawScene() {
        boolean nei = needExportImage;

        exportImage();

        if (nei) {
			rendererImpl.unselectFBO();
        }

        if (export3DRunnable != null) {
            export3DRunnable.run();
            export3DRunnable = null;
        }
    }

	/**
	 * says that an export image is needed, and call immediate display
	 */
	public void needExportImage() {

		setExportImageForThumbnail(true);
		double scale = Math.min(MyXMLio.THUMBNAIL_PIXELS_X / getWidth(),
				MyXMLio.THUMBNAIL_PIXELS_Y / getHeight());

		needExportImage(scale, (int) (getWidth() * scale),
				(int) (getHeight() * scale));

	}

	/**
	 *
	 * @param scale
	 *            scale
	 * @return export image immediately
	 */
	public GBufferedImage getExportImage(double scale) {

		setExportImageForThumbnail(true);

		needExportImage(scale, (int) (getWidth() * scale),
				(int) (getHeight() * scale));

		return getExportImage();
	}

	private void setExportImageForThumbnail(boolean flag) {
		exportImageForThumbnail = flag;
	}

	/**
	 * @return whether exported image is for thumbnail
	 */
	protected boolean getExportImageForThumbnail() {
		return exportImageForThumbnail;
	}

	/**
	 * says that an export image is needed, and call immediate display
	 *
	 * @param scale
	 *            scale for export image
	 * @param forThumbnail
	 *            whether output is thumbnail
	 */
	public void needExportImage(double scale, boolean forThumbnail) {

		setExportImageForThumbnail(forThumbnail);
		needExportImage(scale, (int) (getWidth() * scale),
				(int) (getHeight() * scale));
	}

	/**
	 * @return an image containing last export image created
	 */
	public GBufferedImage getExportImage() {
		return null;
	}

	/**
	 * start animation for gif export
	 *
	 * @param gifEncoder
	 *            gif encoder
	 * @param num
	 *            slider to anime
	 * @param n
	 *            number of images
	 * @param val
	 *            start value
	 * @param min
	 *            slider min value
	 * @param max
	 *            slider max value
	 * @param step
	 *            slider step
	 */
	public void startAnimatedGIFExport(Object gifEncoder,
			AnimationExportSlider num, int n, double val, double min,
			double max, double step) {
		exportType = ExportType.ANIMATEDGIF;

		num.setValue(val);
		num.updateRepaint();
		export_i = 0;

		this.export_n = n;
		this.export_num = num;
		this.export_val = val;
		this.export_min = min;
		this.export_max = max;
		this.export_step = step;
		setGIFEncoder(gifEncoder);

		needExportImage(1, false);

	}

	/**
	 * set gif encoder
	 *
	 * @param gifEncoder
	 *            gif encoder
	 */
	protected void setGIFEncoder(Object gifEncoder) {
		// TODO make it abstract
	}

	/**
	 * set drawing for left eye
	 */
	final protected void setDrawLeft() {
		if (view3D.getCompanion().isStereoBuffered()) {
			setBufferLeft();
			clearColorBuffer();
		}

		eye = EYE_LEFT;
		setColorMask();
	}

	/**
	 * set drawing for right eye
	 */
	final protected void setDrawRight() {
		if (view3D.getCompanion().isStereoBuffered()) {
			setBufferRight();
			clearColorBuffer();
		}

		if (view3D.getCompanion().isStereoBuffered()
				&& !view3D.getCompanion().wantsStereo()) {
			// draw again left eye if no stereo glasses detected
			eye = EYE_LEFT;
		} else {
			eye = EYE_RIGHT;
		}

		setColorMask();
	}

	private void drawTransp() {
		rendererImpl.setLight(1);

		rendererImpl.drawTranspNotCurved();

		rendererImpl.setCullFaceFront();
		rendererImpl.drawTranspClosedCurved(); // draws inside parts
		if (view3D.getDrawList3D().containsClippedSurfacesInclLists()) {
			enableClipPlanesIfNeeded();
			rendererImpl.drawTranspClipped();
			disableClipPlanesIfNeeded();
		}
		rendererImpl.setCullFaceBack();
		rendererImpl.drawTranspClosedCurved(); // draws outside parts
		if (view3D.getDrawList3D().containsClippedSurfacesInclLists()) {
			enableClipPlanesIfNeeded();
			rendererImpl.drawTranspClipped();
			disableClipPlanesIfNeeded();
		}

		rendererImpl.setLight(0);

	}

	/**
	 * draw face-to screen parts (labels, ...)
	 */
	protected void drawFaceToScreen() {
		// drawing labels and texts
		rendererImpl.drawFaceToScreenAbove();

		rendererImpl.enableAlphaTest();
		rendererImpl.disableLighting();
		enableBlending();

		enableTexturesForText();
		view3D.getDrawList3D().drawLabel(this);
		view3D.getDrawList3D().drawForAbsoluteText(this, false);

		rendererImpl.disableTextures();

		if (enableClipPlanes) {
			rendererImpl.disableClipPlanes();
		}

		view3D.drawMouseCursor(this);

		if (enableClipPlanes) {
			rendererImpl.enableClipPlanes();
		}

		rendererImpl.drawFaceToScreenBelow();
	}

	/**
	 * draw face-to screen parts at end (absolute texts, ...)
	 */
	protected void drawFaceToScreenEnd() {

		// drawing texts
		rendererImpl.drawFaceToScreenAbove();

		rendererImpl.enableAlphaTest();
		rendererImpl.disableLighting();
		enableBlending();

		enableTexturesForText();

		view3D.getDrawList3D().drawForAbsoluteText(this, true);

		rendererImpl.disableTextures();

		rendererImpl.drawFaceToScreenBelow();
	}

	/**
	 * sets if clip planes have to be enabled
	 *
	 * @param flag
	 *            flag
	 */
	public void setEnableClipPlanes(boolean flag) {
		waitForUpdateClipPlanes = true;
		enableClipPlanes = flag;
	}

	/**
	 * enable clipping if needed
	 */
	public void enableClipPlanesIfNeeded() {
		if (!enableClipPlanes) {
			rendererImpl.enableClipPlanes();
		}
	}

	/**
	 * disable clipping if needed
	 */
	public void disableClipPlanesIfNeeded() {
		if (!enableClipPlanes) {
			rendererImpl.disableClipPlanes();
		}
	}

	private void drawLabels() {
		if (enableClipPlanes) {
			rendererImpl.enableClipPlanes();
		}
        if (view3D.isXRDrawing()) {
            view3D.updateAxesDecorationPosition();
        }
		drawFaceToScreen();
	}

	private void setMatrixAndLight() {
		rendererImpl.setMatrixView();
		setLightPosition();
		rendererImpl.setLight(0);
	}

	private void drawCursor3D() {
		rendererImpl.enableLighting();
		rendererImpl.disableAlphaTest();
		enableCulling();
		if (needExportImage) {
			// we don't want mouse cursor on export image
			rendererImpl.setCullFaceBack(); // needed for further calculations
		} else {
			drawCursor();
		}
	}

	private void drawHidden() {
		rendererImpl.enableAlphaTest();
		rendererImpl.disableTextures();
		rendererImpl.drawHiddenNotTextured();
		rendererImpl.enableDashHidden();
		rendererImpl.drawHiddenTextured();
	}

	private void drawOpaqueSurfaces() {
		rendererImpl.enableShine();
		rendererImpl.enableFading();
		rendererImpl.drawOpaqueSurfaces();
		rendererImpl.disableTextures();
		rendererImpl.disableAlphaTest();
	}

	private void drawTransparentSurfaces() {
		rendererImpl.enableFading();
		rendererImpl.disableDepthMask();
		enableBlending();
		drawTransp();
		rendererImpl.enableDepthMask();
		rendererImpl.disableTextures();
	}

	private void drawHidingSurfaces(boolean cullFaceFront) {
		enableCulling();
		disableBlending();

		// drawing hiding parts
		if (!TEST_DRAW_DEPTH_TO_COLOR) {
			// no writing in color buffer
			rendererImpl.setColorMask(ColorMask.NONE);
		}
		if (cullFaceFront) {
			rendererImpl.setCullFaceFront(); // draws inside parts
		} else {
			rendererImpl.setCullFaceBack(); // draws outside parts
		}
		rendererImpl.drawClosedSurfacesForHiding();
		if (view3D.getDrawList3D().containsClippedSurfacesInclLists()) {
			enableClipPlanesIfNeeded();
			rendererImpl.drawClippedSurfacesForHiding();
			disableClipPlanesIfNeeded();
		}
		rendererImpl.disableCulling();
		rendererImpl.drawSurfacesForHiding(); // non closed surfaces
		setColorMask();

	}

	private void drawNotHidden() {
		rendererImpl.disableShine();
		rendererImpl.enableDash();
		enableCulling();
		rendererImpl.setCullFaceBack();
		rendererImpl.drawNotHidden();
	}

	private void drawCursor3DAtEnd() {
		if (enableClipPlanes) {
			rendererImpl.disableClipPlanes();
		}
		if (!needExportImage) {
			view3D.drawCursorAtEnd(this);
		}
	}

	private void drawAbsoluteTexts() {
		rendererImpl.disableLighting();
		disableDepthTest();
		rendererImpl.unsetMatrixView();
		enableTexturesForText();
		drawFaceToScreenEnd();
		enableDepthTest();
		rendererImpl.enableLighting();
	}

	private void draw() {
		rendererImpl.draw();
		drawLabels();
		setMatrixAndLight();
		drawCursor3D();
		drawHidden();
		drawOpaqueSurfaces();
		if (TEST_DRAW_DEPTH_TO_COLOR) {
			drawHidingSurfaces(true);
			drawHidingSurfaces(false);
		} else {
			drawTransparentSurfaces();
			drawHidingSurfaces(true);
			drawTransparentSurfaces();
			drawHidingSurfaces(false);
			drawTransparentSurfaces();
		}
		drawNotHidden();
		drawCursor3DAtEnd();
		drawAbsoluteTexts();
	}

	/**
	 * draw view cursor
	 * 
	 * WARNING: needs to be protected for iOS
	 */
	protected void drawCursor() {
		if (enableClipPlanes) {
			rendererImpl.disableClipPlanes();
		}
		rendererImpl.setCullFaceBack();
		view3D.drawCursor(this);
		if (enableClipPlanes) {
			rendererImpl.enableClipPlanes();
		}
	}

	// /////////////////////////////////////////////////
	//
	// pencil methods
	//
	// ///////////////////////////////////////////////////

	/**
	 * sets the color
	 *
	 * @param color
	 *            (r,g,b,a) vector
	 *
	 */
	final public void setColor(Coords color) {
		rendererImpl.setColor((float) color.getX(), (float) color.getY(),
				(float) color.getZ(), (float) color.getW());

	}

	/**
	 * sets the color
	 *
	 * @param color
	 *            (r,g,b,a)
	 */
	final public void setColor(GColor color) {
		rendererImpl.setColor(color.getRed() / 255f, color.getGreen() / 255f,
				color.getBlue() / 255f, color.getAlpha() / 255f);
	}

	/**
	 * sets the matrix in which coord sys the pencil draws.
	 *
	 * @param a_matrix
	 *            the matrix
	 */
	public void setMatrix(CoordMatrix4x4 a_matrix) {
		m_drawingMatrix = a_matrix;
	}

	/**
	 * gets the matrix describing the coord sys used by the pencil.
	 *
	 * @return the matrix
	 */
	public CoordMatrix4x4 getMatrix() {
		return m_drawingMatrix;
	}

	/**
	 * 
	 * @return geometry manager
	 */
	final public Manager getGeometryManager() {
		return geometryManager;
	}

	/**
	 * 
	 * @return textures manager
	 */
	public Textures getTextures() {
		return textures;
	}

	/**
	 * draws a 3D cross cursor
	 *
	 * @param cursorType
	 *            cursor type
	 */
	public void drawCursor(PlotterCursor.Type cursorType) {
		rendererImpl.setNormalToNone();

		if (!cursorType.useLight()) {
			rendererImpl.disableLighting();
		}

		rendererImpl.initMatrix();
		geometryManager.draw(geometryManager.cursor.getIndex(cursorType));
		rendererImpl.resetMatrix();

		if (!cursorType.useLight()) {
			rendererImpl.enableLighting();
		}
	}

	/**
	 * draws a 3D cross cursor; doesn't modify the lighting
	 * 
	 * @param dotMatrix
	 *            matrix for target dot
	 * @param circleMatrix
	 *            matrix for target circle
	 *
	 */
	public void drawTarget(CoordMatrix4x4 dotMatrix,
			CoordMatrix4x4 circleMatrix) {
		rendererImpl.setNormalToNone();
		rendererImpl.disableLighting();
		rendererImpl.disableDepthMask();
		enableBlending();
		setMatrix(dotMatrix);
		rendererImpl.initMatrix();
		geometryManager.draw(
				geometryManager.cursor.getIndex(PlotterCursor.Type.SPHERE));
		setMatrix(circleMatrix);
		rendererImpl.initMatrix();
		geometryManager.draw(geometryManager.cursor
				.getIndex(PlotterCursor.Type.TARGET_CIRCLE));
		rendererImpl.resetMatrix();
		disableBlending();
		rendererImpl.enableDepthMask();
		rendererImpl.enableLighting();
	}

	/**
	 * Draw completing cursor for 3D input.
	 *
	 * @param value
	 *            value
	 * @param out
	 *            out
	 */
	public void drawCompletingCursor(double value, boolean out) {
		rendererImpl.setNormalToNone();
		rendererImpl.initMatrix();
		setLineWidth(PlotterCompletingCursor.WIDTH);
		enableBlending();
		geometryManager.getCompletingCursor().drawCircle(out);
		geometryManager.getCompletingCursor().drawCompleting(value, out);
		disableBlending();
		rendererImpl.resetMatrix();

	}

	/**
	 * draws a view button
	 */
	final public void drawViewInFrontOf() {
		// Application.debug("ici");
		rendererImpl.initMatrix();
		disableBlending();
		geometryManager.draw(geometryManager.getViewInFrontOf().getIndex());
		enableBlending();
		rendererImpl.resetMatrix();
	}

	/**
	 * draws mouse cursor
	 */
	public void drawMouseCursor() {
		rendererImpl.setNormalToNone();
		rendererImpl.initMatrixForFaceToScreen();
		disableBlending();
		rendererImpl.disableCulling();
		geometryManager.draw(geometryManager.getMouseCursor().getIndex());
		enableCulling();
		enableBlending();
		rendererImpl.resetMatrix();
	}

	/** picking type */
	public enum PickingType {
		/** picking point or curve */
		POINT_OR_CURVE,
		/** picking surface */
		SURFACE,
		/** picking label */
		LABEL
	}

	/**
	 * set light position
	 */
	protected void setLightPosition() {
		rendererImpl.setLightPosition(rendererImpl.getLightPosition());
	}

	/**
	 * set waiting for update color
	 */
	public void setWaitForUpdateClearColor() {
		waitForUpdateClearColor = true;
	}

	final private void updateClearColor() {

		GColor c = view3D.getApplyedBackground();
		float r, g, b;
		if (view3D
				.getProjection() == EuclidianView3DInterface.PROJECTION_GLASSES
				&& !view3D.getCompanion().isStereoBuffered()) { // grayscale for
																// anaglyph
																// glasses
			r = (float) (c.getGrayScale() / 255);
			g = r;
			b = r;
		} else {
			r = (float) c.getRed() / 255;
			g = view3D.isShutDownGreen() ? 0 : (float) c.getGreen() / 255;
			b = (float) c.getBlue() / 255;
		}

		rendererImpl.setClearColor(r, g, b, 1.0f);
	}

	/**
	 * 
	 * @return screen left
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * 
	 * @return screen right
	 */
	public int getRight() {
		return right;
	}

	/**
	 * 
	 * @return screen width
	 */
	public int getWidth() {
		return right - left;
	}

	/**
	 * Used for dip density devices
	 *
	 * @return height in pixels
	 */
	public int getWidthInPixels() {
		return getWidth();
	}

	/**
	 * 
	 * @return screen bottom
	 */
	public int getBottom() {
		return bottom;
	}

	/**
	 * 
	 * @return screen top
	 */
	public int getTop() {
		return top;
	}

	/**
	 * 
	 * @return screen height
	 */
	public int getHeight() {
		return top - bottom;
	}

	/**
	 * Used for dip density devices
	 *
	 * @return height in pixels
	 */
	public int getHeightInPixels() {
		return getHeight();
	}

	/**
	 * 
	 * @return visible depth
	 */
	final public double getVisibleDepth() {
		return getWidth() * 2;
	} // keep visible objects at twice center-to-right distance

	/**
	 * 
	 * @return near distance
	 */
	public int getNear() {
		return -getWidth();
	}

	/**
	 * 
	 * @return far distance
	 */
	public int getFar() {
		return getWidth();
	}

	/**
	 * for a line described by (o,v), return the min and max parameters to draw
	 * the line
	 *
	 * @param minmax
	 *            initial interval
	 * @param o
	 *            origin of the line
	 * @param v
	 *            direction of the line
	 * @param extendedDepth
	 *            says if it looks to real depth bounds, or working depth bounds
	 * @return interval to draw the line
	 */
	public double[] getIntervalInFrustum(double[] minmax, Coords o, Coords v,
			boolean extendedDepth) {

		double left1 = (getLeft() - o.get(1)) / v.get(1);
		double right1 = (getRight() - o.get(1)) / v.get(1);
		updateIntervalInFrustum(minmax, left1, right1);

		double top1 = (getTop() - o.get(2)) / v.get(2);
		double bottom1 = (getBottom() - o.get(2)) / v.get(2);
		updateIntervalInFrustum(minmax, top1, bottom1);

		double halfDepth = getVisibleDepth() / 2.0;
		double front = (-halfDepth - o.get(3)) / v.get(3);
		double back = (halfDepth - o.get(3)) / v.get(3);
		updateIntervalInFrustum(minmax, front, back);

		return minmax;
	}

	/**
	 * return the intersection of intervals [minmax] and [v1,v2]
	 *
	 * @param minmax
	 *            initial interval
	 * @param v1
	 *            first value
	 * @param v2
	 *            second value
	 * @return intersection interval
	 */
	private static double[] updateIntervalInFrustum(double[] minmax, double v1,
			double v2) {
		double vMin = v1;
		double vMax = v2;

		if (vMin > vMax) {
			vMin = v2;
			vMax = v1;
		}

		if (vMin > minmax[0]) {
			minmax[0] = vMin;
		}

		if (vMax < minmax[1]) {
			minmax[1] = vMax;
		}

		return minmax;
	}

	/**
	 * Update projection matrix for view's projection.
	 */
	public final void setProjectionMatrix() {
		if (view3D.isXRDrawing()) {
			rendererImpl.setProjectionMatrixViewForAR();
		} else {
			switch (view3D.getProjection()) {
				default:
			case EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC:
				rendererImpl.viewOrtho();
					break;
			case EuclidianView3DInterface.PROJECTION_PERSPECTIVE:
				rendererImpl.viewPersp();
					break;
			case EuclidianView3DInterface.PROJECTION_GLASSES:
				rendererImpl.viewGlasses();
					break;
			case EuclidianView3DInterface.PROJECTION_OBLIQUE:
				rendererImpl.viewOblique();
					break;
			}
		}
	}

	/**
	 * Update perspective for eye position.
	 *
	 * @param left
	 *            left eye distance
	 * @param right
	 *            right eye distance
	 */
	final public void setNear(double left, double right) {
		eyeToScreenDistance[EYE_LEFT] = left;
		eyeToScreenDistance[EYE_RIGHT] = right;
		updatePerspValues();
		updatePerspEye();
	}

	/**
	 * update values for perspective projection
	 */
	final private void updatePerspValues() {
		if (rendererImpl != null) {
			rendererImpl.updatePerspValues();
		}
	}

	private void updatePerspEye() {
		perspEye = new Coords(glassesEyeX[1], glassesEyeY[1],
				eyeToScreenDistance[EYE_LEFT], 1); // perspFocus is negative
	}

	/**
	 *
	 * @return coords of the eye (in real coords) when perspective projection
	 */
	public Coords getPerspEye() {
		return perspEye;
	}

	/**
	 *
	 * @return eyes separation (half of, in real coords)
	 */
	public double getEyeSep() {
		return (glassesEyeX[0] - glassesEyeX[1]) / 2;
	}

	/**
	 * Update glasses coordinates.
	 */
	public void updateGlassesValues() {
		for (int i = 0; i < 2; i++) {
			// eye values
			glassesEyeX[i] = view3D.getEyeX(i);
			glassesEyeY[i] = view3D.getEyeY(i);
		}
		if (rendererImpl != null) {
			rendererImpl.updateGlassesValues();
		}
	}

	/**
	 * set the color mask
	 */
	protected void setColorMask() {
		if (view3D
				.getProjection() == EuclidianView3DInterface.PROJECTION_GLASSES
				&& !view3D.getCompanion().isStereoBuffered()) {
			if (eye == EYE_LEFT) {
				rendererImpl.setColorMask(ColorMask.RED); // cyan
			} else {
				rendererImpl.setColorMask(
						view3D.isGlassesShutDownGreen() ? ColorMask.BLUE
						: ColorMask.BLUE_AND_GREEN); // red
			}
		} else {
			rendererImpl.setColorMask(ColorMask.ALL);
		}

	}

	/**
	 * export type
	 */
	public enum ExportType {
		/** no type */
		NONE,
		/** animated gif */
		ANIMATEDGIF,
		/** producing thumbnail */
		THUMBNAIL_IN_GGBFILE,
		/** png */
		PNG,
		/** copy to clipboard */
		CLIPBOARD,
		/** upload to website */
		UPLOAD_TO_GEOGEBRATUBE
	}

	/**
	 * Update oblique projection attributes.
	 */
	public void updateProjectionObliqueValues() {
		double angle = Math.toRadians(view3D.getProjectionObliqueAngle());
		obliqueX = -view3D.getProjectionObliqueFactor() * Math.cos(angle);
		obliqueY = -view3D.getProjectionObliqueFactor() * Math.sin(angle);
		obliqueOrthoDirection = new Coords(obliqueX, obliqueY, -1, 0);
		if (rendererImpl != null) {
			rendererImpl.updateProjectionObliqueValues();
		}
	}

	/**
	 *
	 * @return x oblique factor
	 */
	public double getObliqueX() {
		return obliqueX;
	}

	/**
	 *
	 * @return y oblique factor
	 */
	public double getObliqueY() {
		return obliqueY;
	}

	/**
	 * 
	 * @return oblique orthogonal direction
	 */
	public Coords getObliqueOrthoDirection() {
		return obliqueOrthoDirection;
	}

	/**
	 * Set Up An Ortho View after setting left, right, bottom, front values
	 *
	 * @param x
	 *            left
	 * @param y
	 *            bottom
	 * @param w
	 *            width
	 * @param h
	 *            height
	 *
	 */
	public void setView(int x, int y, int w, int h) {
		left = x - w / 2;
		bottom = y - h / 2;
		right = left + w;
		top = bottom + h;

		if (needExportImage) {
			return;
		}

		switch (view3D.getProjection()) {
		default:
		case EuclidianView3DInterface.PROJECTION_ORTHOGRAPHIC:
			updateOrthoValues();
			break;
		case EuclidianView3DInterface.PROJECTION_PERSPECTIVE:
			updatePerspValues();
			updatePerspEye();
			break;
		case EuclidianView3DInterface.PROJECTION_GLASSES:
			updatePerspValues();
			updateGlassesValues();
			updatePerspEye();
			break;
		case EuclidianView3DInterface.PROJECTION_OBLIQUE:
			updateProjectionObliqueValues();
			break;
		}

		setView();

		view3D.setViewChanged();
		view3D.setWaitForUpdate();
	}

	/**
	 * Export image to clipboardd (async)
	 */
	public void exportToClipboard() {
		exportType = ExportType.CLIPBOARD;
		needExportImage(App.getMaxScaleForClipBoard(view3D), true);

	}

	/**
	 * Export image (async), start Tube upload after that.
	 */
	public void uploadToGeoGebraTube() {
		exportType = ExportType.UPLOAD_TO_GEOGEBRATUBE;
		needExportImage();
	}

	/**
	 * Double.POSITIVE_INFINITY for parallel projections
	 *
	 * @return eye to screen distance
	 */
	public double getEyeToScreenDistance() {
		if (view3D
				.getProjection() == EuclidianView3DInterface.PROJECTION_PERSPECTIVE
				|| view3D
						.getProjection() == EuclidianView3DInterface.PROJECTION_GLASSES) {
			return eyeToScreenDistance[EYE_LEFT];
		}

		return Double.POSITIVE_INFINITY;
	}

	/**
	 *
	 * @param val
	 *            value
	 * @return first power of 2 greater than val
	 */
	public static final int firstPowerOfTwoGreaterThan(int val) {

		int ret = 1;
		while (ret < val) {
			ret *= 2;
		}
		return ret;
	}

	/**
	 * init the renderer
	 */
	public void init() {

		rendererImpl.initShaders();

		textures = newTextures();
		geometryManager = rendererImpl.createManager();

		// GL_LIGHT0 & GL_LIGHT1
		float diffuse0 = 1f - AMBIENT_0;
		float diffuse1 = 1f - AMBIENT_1;

		rendererImpl.setLightAmbiantDiffuse(AMBIENT_0, diffuse0, AMBIENT_1,
				diffuse1);

		// material and light
		rendererImpl.setColorMaterial();

		// setLight(GLlocal.GL_LIGHT0);
		rendererImpl.setLightModel();
		rendererImpl.enableLightingOnInit();

		// common enabling
		enableDepthTest();
		setDepthFunc();
		enablePolygonOffsetFill();
		rendererImpl.initCulling();

		// blending
		setBlendFunc();
		enableBlending();
		updateClearColor();

		rendererImpl.setAlphaFunc();

		// normal anti-scaling
		enableNormalNormalized();

		// textures
		initTextures();

		// reset euclidian view
		view3D.reset();

		// ensure that animation is on (needed when undocking/docking 3D view)
		resumeAnimator();

	}

	/**
	 * init textures
	 */
	protected void initTextures() {
		textures.init();
	}

	/**
	 * set the depth function
	 */
	abstract protected void setDepthFunc();

	/**
	 * enable polygon offset fill
	 */
	abstract protected void enablePolygonOffsetFill();

	/**
	 * set the blend function
	 */
	abstract protected void setBlendFunc();

	/**
	 * enable text textures
	 */
	public void enableTexturesForText() {
		rendererImpl.enableTextures();
		rendererImpl.enableTexturesForText();
	}

	/**
	 *
	 * @return the 3D view attached
	 */
	final public EuclidianView3D getView() {
		return view3D;
	}

	/**
	 * 
	 * @return scene to screen matrix
	 */
	public CoordMatrix4x4 getToScreenMatrix() {
		return view3D.getToScreenMatrixForGL();
	}

	/**
	 * @param flag
	 *            image export flag
	 */
	final public void setNeedExportImage(boolean flag) {
		// Log.printStacktrace("" + flag);
		needExportImage = flag;
	}

	/**
	 * 
	 * @return current export type
	 */
	protected ExportType getExportType() {
		return exportType;
	}

	/**
	 * 
	 * @return slider used for exporting animated gifs
	 */
	protected AnimationExportSlider getExportNum() {
		return export_num;
	}

	/**
	 * 
	 * @return current slider value for exporting animated gifs
	 */
	protected double getExportVal() {
		return export_val;
	}

	/**
	 * 
	 * @return slider max value for exporting animated gifs
	 */
	protected double getExportMax() {
		return export_max;
	}

	/**
	 * 
	 * @return slider min value for exporting animated gifs
	 */
	protected double getExportMin() {
		return export_min;
	}

	/**
	 * 
	 * @return animated gifs current image id
	 */
	protected int getExportI() {
		return export_i;
	}

	/**
	 * 
	 * @return animated gifs images count
	 */
	protected double getExportN() {
		return export_n;
	}

	/**
	 * 
	 * @return slider step for exporting animated gifs
	 */
	protected double getExportStep() {
		return export_step;
	}

	/**
	 * 
	 * @return renderer type
	 */
	protected RendererType getType() {
		return type;
	}

	/**
	 * set renderer type
	 * 
	 * @param t
	 *            type
	 */
	protected void setType(RendererType t) {
		type = t;
	}

	/**
	 * set slider step for exporting animated gifs
	 * 
	 * @param step
	 *            step
	 */
	protected void setExportStep(double step) {
		export_step = step;
	}

	/**
	 * set slider value for exporting animated gifs
	 * 
	 * @param val
	 *            value
	 */
	protected void setExportVal(double val) {
		export_val = val;
	}

	/**
	 * set animated gifs image id
	 * 
	 * @param i
	 *            id
	 */
	protected void setExportI(int i) {
		export_i = i;
	}

	/**
	 * set export type
	 * 
	 * @param type
	 *            type
	 */
	protected void setExportType(ExportType type) {
		exportType = type;
	}

	/**
	 *
	 * @return true (default) if reduce "window" for clipping box
	 */
	public boolean reduceForClipping() {
		return !view3D.isXREnabled();
	}

	/**
	 * Set scale for AR
	 */
	public void setARScaleAtStart() {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
            arManager.setXRScaleAtStart();
		}
	}

	/**
	 * set background color
	 */
	public void setBackgroundColor() {
		XRManagerInterface<?> arManager = getXRManager();
	    if (arManager != null) {
            arManager.setBackgroundColor();
        }
    }

	/**
	 * set background style
	 * 
	 * @param backgroundStyle
	 *            style
	 */
	public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
		XRManagerInterface<?> arManager = getXRManager();
        if (arManager != null) {
            arManager.setBackgroundStyle(backgroundStyle);
        }
	}

	/**
	 * @return background for AR, opaque otherwise
	 */
	public BackgroundStyle getBackgroundStyle() {
		XRManagerInterface<?> arManager = getXRManager();
        if (arManager != null) {
            return arManager.getBackgroundStyle();
        }
		return BackgroundStyle.NONE;
	}

    /**
     * set z-value for first floor hit in AR
     * @param z altitude
     */
    public void setARFloorZ(double z) {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
            arManager.setFirstFloor(z);
		}
    }

	/**
	 * sets the clip planes
	 *
	 * @param minMax
	 *            min/max for x/y/z
	 */
	final public void setClipPlanes(double[][] minMax) {
		if (rendererImpl != null) {
			rendererImpl.setClipPlanes(minMax);
		}
	}

	/**
	 * set up the view
	 */
	public void setView() {
		rendererImpl.setView();
	}

	/**
	 *
	 * @return new geometry manager
	 */
	protected Manager createManager() {
		return rendererImpl.createManager();
	}

	/**
	 * for shaders : update projection matrix
	 */
	final public void updateOrthoValues() {
		if (rendererImpl != null) {
			rendererImpl.updateOrthoValues();
		}
	}

	/**
	 * set drawing to left buffer (when stereo buffered)
	 */
	protected void setBufferLeft() {
		rendererImpl.setBufferLeft();
	}

	/**
	 * set drawing to right buffer (when stereo buffered)
	 */
	protected void setBufferRight() {
		rendererImpl.setBufferRight();
	}

	/**
	 * clear color buffer
	 */
	protected void clearColorBuffer() {
		rendererImpl.glClear(rendererImpl.getGL_COLOR_BUFFER_BIT());
	}

	/**
	 * enable culling
	 */
	final public void enableCulling() {
		rendererImpl.glEnable(rendererImpl.getGL_CULL_FACE());
	}

	/**
	 * disable blending
	 */
	final public void disableBlending() {
		rendererImpl.glDisable(rendererImpl.getGL_BLEND());
	}

	/**
	 * enable blending
	 */
	final public void enableBlending() {
		rendererImpl.glEnable(rendererImpl.getGL_BLEND());
	}

	/**
	 * enable depth test
	 */
	final public void enableDepthTest() {
		rendererImpl.glEnable(rendererImpl.getGL_DEPTH_TEST());
	}

	/**
	 * disable depth test
	 */
	final public void disableDepthTest() {
		rendererImpl.glDisable(rendererImpl.getGL_DEPTH_TEST());
	}

	/**
	 * 
	 * @return true if uses shaders
	 */
	public boolean useShaders() {
		return rendererImpl.useShaders();
	}

	/**
	 * set hits for mouse location
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @param threshold
	 *            threshold
	 */
	final public void setHits(GPoint mouseLoc, int threshold) {

		if (mouseLoc == null) {
			return;
		}

		hitting.setHits(mouseLoc, threshold);

	}

	/**
	 *
	 * @return hitting
	 */
	final public Hitting getHitting() {
		return hitting;
	}

	/**
	 * set label hits for mouse location
	 * 
	 * @param mouseLoc
	 *            mouse location
	 * @return first label hitted geo
	 */
	public GeoElement getLabelHit(GPoint mouseLoc) {
		if (mouseLoc == null) {
			return null;
		}

		return hitting.getLabelHit(mouseLoc);
	}

	/**
	 * process picking for intersection curves SHOULD NOT BE CALLED OUTSIDE THE
	 * DISPLAY LOOP
	 */
	public void pickIntersectionCurves() {

		ArrayList<IntersectionCurve> curves = ((EuclidianController3D) view3D
				.getEuclidianController()).getIntersectionCurves();

		// picking objects
		for (IntersectionCurve intersectionCurve : curves) {
			Drawable3D d = intersectionCurve.drawable;
			if (!d.hit(hitting)
					|| d.getPickingType() != PickingType.POINT_OR_CURVE) {
				// we assume that hitting infos are updated from last mouse move
				d.setZPick(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
						hitting.discardPositiveHits(),
						Double.POSITIVE_INFINITY);
			}
		}
	}

	/**
	 * get alpha channel of the array ARGB description
	 * 
	 * @param label
	 *            label
	 * 
	 * @param pix
	 *            bitmap
	 * @return the alpha channel of the array ARGB description
	 */
	protected static byte[] argbToAlpha(DrawLabel3D label, int[] pix) {
		return argbToAlpha(label, label.getWidth(), label.getHeight(), pix);
	}

	/**
	 * get alpha channel of the array ARGB description
	 * 
	 * @param label
	 *            label
	 * @param labelWidthRes
	 *            width
	 * @param labelHeightRes
	 *            height
	 * @param pix
	 *            bitmap
	 * @return the alpha channel of the array ARGB description
	 */
	protected static byte[] argbToAlpha(DrawLabel3D label, int labelWidthRes,
			int labelHeightRes, int[] pix) {

		// calculates 2^n dimensions
		int w = firstPowerOfTwoGreaterThan(labelWidthRes);
		int h = firstPowerOfTwoGreaterThan(labelHeightRes);

		// Application.debug("width="+width+",height="+height+"--w="+w+",h="+h);

		// get alpha channel and extends to 2^n dimensions
		byte[] bytes = new byte[w * h];
		byte b;
		int bytesIndex = 0;
		int pixIndex = 0;
		int xmin = w, xmax = 0, ymin = h, ymax = 0;
		for (int y = 0; y < labelHeightRes; y++) {
			for (int x = 0; x < labelWidthRes; x++) {
				b = (byte) (pix[pixIndex] >> ALPHA_SHIFT);
				if (b != 0) {
					if (x < xmin) {
						xmin = x;
					}
					if (x > xmax) {
						xmax = x;
					}
					if (y < ymin) {
						ymin = y;
					}
					if (y > ymax) {
						ymax = y;
					}

				}
				bytes[bytesIndex] = b;
				bytesIndex++;
				pixIndex++;
			}
			bytesIndex += w - labelWidthRes;
		}

		// values for picking (ignore transparent bytes)
		label.setPickingDimension(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1);

		// update width and height
		label.setDimensionPowerOfTwo(w, h);

		return bytes;
	}

	/**
	 * enables normalization for normals
	 */
	protected void enableNormalNormalized() {
		// only need for non-shader renderers
	}

	/**
	 * dispaly for export image
	 */
	public void display() {
		// used in desktop and for export image
	}

	/**
	 * do export image if needed
	 */
	protected void exportImage() {
		// only in Desktop; Web uses canvas methods
	}

	/**
	 * says that we need an export image with scale, width and height
	 *
	 * @param scale
	 *            scale factor
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	protected void needExportImage(double scale, int w, int h) {
		if (rendererImpl != null) {
			rendererImpl.needExportImage(scale, w, h);
		}
	}
	
	/**
	 * @return implementation
	 */
	public RendererImpl getRendererImpl() {
		return rendererImpl;
	}

	/**
	 * @param rendererImpl
	 *            implementation
	 */
	protected void setRendererImpl(RendererImpl rendererImpl) {
		this.rendererImpl = rendererImpl;
	}

	/**
	 * create dummy texture
	 */
	public void createDummyTexture() {
		rendererImpl.createDummyTexture();
	}

	/**
	 * set AR to end
	 */
	public void setARShouldEnd() {
		resetScaleFromAR();
		killARSession();
		view3D.resetViewFromAR();
		view3D.setXRDrawing(false);
		view3D.setXREnabled(false);
	}

	/**
	 * kill AR session
	 */
	protected void killARSession() {
		// not used here
	}

	/**
	 * 
	 * @return canvas (for desktop version at least)
	 */
	abstract public Object getCanvas();

	/**
	 * re-calc the display immediately
	 */

	/**
	 * set line width
	 * 
	 * @param width
	 *            line width
	 */
	abstract public void setLineWidth(double width);

	/**
	 * enable GL textures 2D
	 */
	abstract public void enableTextures2D();

	/**
	 * disable GL textures 2D
	 */
	abstract public void disableTextures2D();

	/**
	 * 
	 * @param label
	 *            label
	 * @return buffered image for drawing label
	 */
	abstract public GBufferedImage createBufferedImage(DrawLabel3D label);

	/**
	 * create alpha texture for label from image
	 * 
	 * @param label
	 *            label
	 * @param bimg
	 *            buffered image
	 */
	abstract public void createAlphaTexture(DrawLabel3D label,
			GBufferedImage bimg);

	/**
	 * 
	 * @param sizeX
	 *            width
	 * @param sizeY
	 *            height
	 * @param buf
	 *            image data
	 * @return a texture for alpha channel
	 */
	abstract public int createAlphaTexture(int sizeX, int sizeY, byte[] buf);

	/**
	 * @param sizeX
	 *            width
	 * @param sizeY
	 *            height
	 * @param buf
	 *            image data
	 */
	abstract public void textureImage2D(int sizeX, int sizeY, byte[] buf);

	/**
	 * set texture linear parameters
	 */
	abstract public void setTextureLinear();

	/**
	 * set texture nearest parameters
	 */
	abstract public void setTextureNearest();

	/**
	 * ensure that animation is on (needed when undocking/docking 3D view)
	 */
	abstract public void resumeAnimator();

	/**
	 * Restart AR session.
	 */
	abstract public void setARShouldRestart();

    /**
     *
     * @return XR manager (can be null)
     */
	public XRManagerInterface<?> getXRManager() {
	    return null;
    }

	/**
	 * @return ArViewMatrix.
	 */
	public CoordMatrix4x4 getArViewModelMatrix() {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
			return arManager.getViewModelMatrix();
		}
		return CoordMatrix4x4.IDENTITY;
	}

	/**
	 * @return undoRotationMatrixAR.
	 */
	public CoordMatrix4x4 getUndoRotationMatrixAR() {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
			return arManager.getUndoRotationMatrix();
		}
		return CoordMatrix4x4.IDENTITY;
	}

	/**
	 * fit thickness to screen distance in AR.
	 */
	public void fitThicknessInAR() {
		XRManagerInterface<?> arManager = getXRManager();
		if (arManager != null) {
			arManager.fitThickness();
		}
    }

    /**
     * reset 3D view scale if AR has changed it
     */
    protected void resetScaleFromAR() {
        XRManagerInterface<?> arManager = getXRManager();
        if (arManager != null) {
            arManager.resetScaleFromXR();
        }
    }
}
