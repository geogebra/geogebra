package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.HittingSphere;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.kernel.geos.GeoElement;

public class RendererLogicalPickingGL2 extends RendererGL2 {

	public RendererLogicalPickingGL2(EuclidianView3D view, boolean useCanvas) {

		super(view, useCanvas);

		if (((EuclidianController3D) view3D.getEuclidianController())
				.useInputDepthForHitting()) {
			hitting = new HittingSphere(view3D);
		} else {
			hitting = new Hitting(view3D);
		}
	}

	private Hitting hitting;

	@Override
	public Hitting getHitting() {
		return hitting;
	}

	@Override
	public void setHits(GPoint mouseLoc, int threshold) {

		if (mouseLoc == null) {
			return;
		}

		hitting.setHits(mouseLoc, threshold);

	}

	@Override
	public GeoElement getLabelHit(GPoint mouseLoc) {
		if (mouseLoc == null) {
			return null;
		}

		// return hitting.getLabelHit(mouseLoc);

		return null;
	}

	@Override
	public void pickIntersectionCurves() {

		ArrayList<IntersectionCurve> curves = ((EuclidianController3D) view3D
				.getEuclidianController()).getIntersectionCurves();

		// picking objects
		for (IntersectionCurve intersectionCurve : curves) {
			Drawable3D d = intersectionCurve.drawable;
			d.updateForHitting(); // we may need an update
			if (!d.hit(hitting)
					|| d.getPickingType() != PickingType.POINT_OR_CURVE) { // we
																			// assume
																			// that
																			// hitting
																			// infos
																			// are
																			// updated
																			// from
																			// last
																			// mouse
																			// move
				d.setZPick(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
			}

		}

	}

	@Override
	protected void doPick() {
		// no need here
	}

	@Override
	public boolean useLogicalPicking() {
		return true;
	}


	// private int background;
	//
	// private void createBackground() {
	//
	// enableTextures2D();
	//
	// int[] index = new int[1];
	// genTextures2D(1, index);
	//
	// background = index[0];
	//
	// updateBackGround();
	// }
	//
	// private void updateBackGround() {
	//
	// enableTextures2D();
	//
	// bindTexture(background);
	//
	// int sizeX = 512;
	// int sizeY = sizeX;
	// // double factor = 100000;
	// // double pulseX = factor / (sizeX * getWidth());
	// // double pulseY = factor / (sizeY * getHeight());
	// // int amplitude = 255;
	//
	// ByteBuffer buf = ByteBuffer.allocate(sizeX * sizeY);
	// for (int x = 0; x < sizeX; x++) {
	// for (int y = 0; y < sizeY; y++) {
	// // buf.put((byte) (255 + amplitude
	// // * (Math.sin(pulseX * x) * Math.sin(pulseY * y) - 1)));
	// // buf.put((byte) (255 - amplitude * Math.random()));
	// buf.put((byte) (Math.random() > 0.5 ? 255 : 0));
	// }
	//
	// }
	// buf.rewind();
	//
	// getGL().glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_ALPHA, sizeX, sizeY, 0,
	// GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, buf);
	//
	// disableTextures2D();
	//
	// }
	//
	// @Override
	// protected void initTextures() {
	// super.initTextures();
	// createBackground();
	// }
	//
	// @Override
	// protected void clearColorBuffer() {
	// super.clearColorBuffer();
	//
	// jogl.getGL2().glViewport(0, 0, right - left, top - bottom);
	// jogl.getGL2().glMatrixMode(GLlocal.GL_PROJECTION);
	// jogl.getGL2().glLoadIdentity();
	//
	// jogl.getGL2().glMatrixMode(GLlocal.GL_MODELVIEW);
	// jogl.getGL2().glLoadIdentity();
	//
	// disableDepthTest();
	//
	// disableCulling();
	//
	// enableBlending();
	// enableTextures2D();
	// bindTexture(background);
	// // setTextureLinear();
	// setTextureNearest();
	//
	// // disableTextures2D();
	// disableLighting();
	//
	// jogl.getGL2().glBegin(GLlocal.GL_QUADS);
	// jogl.getGL2().glColor3f(1f, 1f, 1f);
	// jogl.getGL2().glTexCoord2f(0.0f, 0.0f);
	// jogl.getGL2().glVertex2f(-1f, -1f);
	// jogl.getGL2().glTexCoord2f(0.0f, 1.0f);
	// jogl.getGL2().glVertex2f(-1.0f, 1.0f);
	// jogl.getGL2().glTexCoord2f(1.0f, 1.0f);
	// jogl.getGL2().glVertex2f(1.0f, 1.0f);
	// jogl.getGL2().glTexCoord2f(1.0f, 0.0f);
	// jogl.getGL2().glVertex2f(1.0f, -1.0f);
	// jogl.getGL2().glEnd();
	//
	// disableTextures2D();
	// enableDepthTest();
	//
	// }

}
