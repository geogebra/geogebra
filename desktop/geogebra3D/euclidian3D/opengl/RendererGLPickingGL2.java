package geogebra3D.euclidian3D.opengl;

import geogebra.common.awt.GPoint;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.Hits3D;
import geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.geos.GeoElement;
import geogebra3D.awt.GPointWithZ;
import geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

import java.nio.IntBuffer;
import java.util.ArrayList;

public class RendererGLPickingGL2 extends RendererGL2 {

	public RendererGLPickingGL2(EuclidianView3D view, boolean useCanvas) {
		super(view, useCanvas);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setHits(GPoint mouseLoc, int threshold) {

		// sets the flag and mouse location for openGL picking
		setMouseLoc(mouseLoc, Renderer.PICKING_MODE_LABELS);

	}

	@Override
	public GeoElement getLabelHit(GPoint mouseLoc) {

		return view3D.getHits3D().getLabelHit();
	}

	@Override
	public void pickIntersectionCurves() {

		ArrayList<IntersectionCurve> curves = ((EuclidianController3D) view3D
				.getEuclidianController()).getIntersectionCurves();

		int bufSize = curves.size();
		// IntBuffer selectBuffer=createSelectBufferForPicking(bufSize);
		// Drawable3D[] drawHits=createDrawableListForPicking(bufSize);
		if (bufSize > geoToPickSize) {
			selectBuffer = createSelectBufferForPicking(bufSize);
			drawHits = createDrawableListForPicking(bufSize);
			oldGeoToPickSize = -1;
		}

		setGLForPicking();
		pushSceneMatrix();

		// picking objects
		for (IntersectionCurve intersectionCurve : curves) {
			Drawable3D d = intersectionCurve.drawable;
			d.setZPick(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
			pick(d, true, PickingType.POINT_OR_CURVE);
		}

		// set off the scene matrix
		jogl.getGL2().glPopMatrix();

		storePickingInfos(null, 0, 0); // 0, 0 will be ignored since hits are
		// passed as null

		enableLighting();
	}

	private IntBuffer createSelectBufferForPicking(int bufSize) {
		// Set Up the Selection Buffer
		// Application.debug(bufSize);
		IntBuffer ret = RendererJogl.newIntBuffer(bufSize);
		jogl.getGL2().glSelectBuffer(bufSize, ret); // Tell OpenGL To Use Our
													// Array For Selection
		return ret;
	}

	@Override
	protected void doPick() {

		// App.debug("geoToPickSize = "+geoToPickSize);
		if (geoToPickSize != oldGeoToPickSize || needsNewPickingBuffer) {
			int bufSize = geoToPickSize * 2 + 1 + 20; // TODO remove "+20" due
														// to intersection curve
			selectBuffer = createSelectBufferForPicking(bufSize);
			drawHits = createDrawableListForPicking(bufSize);
			oldGeoToPickSize = geoToPickSize;
			needsNewPickingBuffer = false;
		}

		setGLForPicking();
		pushSceneMatrix();

		// picking surfaces
		drawable3DLists.drawForPickingSurfaces(this);

		// picking points and curves
		int pointAndCurvesLoop = pickingLoop;
		drawable3DLists.drawForPickingPointsAndCurves(this);

		// set off the scene matrix
		jogl.getGL2().glPopMatrix();

		// picking labels
		int labelLoop = pickingLoop;

		if (pickingMode == PICKING_MODE_LABELS) {
			// picking labels
			drawable3DLists.drawLabelForPicking(this);
		}

		// end picking

		// hits are stored
		// Hits3D hits3D = new Hits3D();
		Hits3D hits3D = view3D.getHits3D();
		hits3D.init();
		storePickingInfos(hits3D, pointAndCurvesLoop, labelLoop);

		// sets the GeoElements in view3D
		hits3D.sort();
		/*
		 * DEBUG / StringBuilder sbd = new StringBuilder();
		 * sbd.append("hits~~~"+hits3D.toString()); for (int i = 0;
		 * i<drawHits.length; i++) { if (drawHits[i]!=null &&
		 * drawHits[i].getGeoElement()!=null) { if
		 * (hits3D.contains(drawHits[i].getGeoElement())) { sbd.append("\n" +
		 * drawHits[i].getGeoElement().getLabel()+ "~~~ zPickMin=" +
		 * drawHits[i].zPickMin + "  zPickMax=" + drawHits[i].zPickMax);}} }
		 * Application.debug(sbd.toString()); / END DEBUG
		 */
		// view3D.setHits(hits3D);

		// App.debug(hits3D);

		waitForPick = false;

		enableLighting();
	}

	private void storePickingInfos(Hits3D hits3D, int pointAndCurvesLoop,
			int labelLoop) {

		int hits = jogl.getGL2().glRenderMode(GLlocal.GL_RENDER); // Switch To
																	// Render
																	// Mode,
																	// Find Out
																	// How Many

		int names, ptr = 0;
		double zFar, zNear;
		int num;

		// App.error("");

		for (int i = 0; i < hits; i++) {

			names = selectBuffer.get(ptr);
			ptr++; // min z
			zNear = getScreenZFromPickingDepth(getDepth(ptr, selectBuffer));
			ptr++; // max z
			zFar = getScreenZFromPickingDepth(getDepth(ptr, selectBuffer));

			ptr++;

			for (int j = 0; j < names; j++) {
				num = selectBuffer.get(ptr);

				if (hits3D == null) { // just update z min/max values for the
										// drawable
					drawHits[num].setZPick(zNear, zFar);
				} else { // if for hits array, some checks are done
							// App.debug("\n"+drawHits[num].getGeoElement());
					if (!((EuclidianController3D) view3D
							.getEuclidianController())
							.useInputDepthForHitting()// (mouse instanceof
														// GPointWithZ)
							|| intersectsMouse3D(zNear, zFar,
									((GPointWithZ) mouse).getZ())) { // check if
																		// mouse
																		// is
																		// nearer
																		// than
																		// objet
																		// (for
																		// 3D
																		// input)
						PickingType type;
						if (num >= labelLoop) {
							type = PickingType.LABEL;
						} else if (num >= pointAndCurvesLoop) {
							type = PickingType.POINT_OR_CURVE;
						} else {
							type = PickingType.SURFACE;
						}
						hits3D.addDrawable3D(drawHits[num], type, zNear, zFar);
						// App.debug("\n"+drawHits[num].getGeoElement()+"\nzFar = "+zFar+"\nmouse z ="+((GPointWithZ)
						// mouse).getZ());
					}
				}

				// Application.debug(drawHits[num]+"\nzMin="+zMin+", zMax="+zMax);
				ptr++;
			}
		}
	}

	/**
	 * returns the depth between 0 and 2, in double format, from an integer
	 * offset lowest is depth, nearest is the object
	 * 
	 * @param ptr
	 *            the integer offset
	 * */
	private final static float getDepth(int ptr, IntBuffer selectBuffer) {

		return (float) (selectBuffer.get(ptr) & 0xffffffffL) / 0x7fffffff;
	}

	@Override
	public boolean useLogicalPicking() {
		return false;
	}

}
