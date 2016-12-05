package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPointWithZ;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.input3D.EuclidianViewInput3DCompanion;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererCheckGLVersionD;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl;

/**
 * EuclidianView3D with controller using 3D input
 * 
 * @author mathieu
 * 
 */
public class EuclidianViewInput3D extends EuclidianView3DD {

	private Input3D input3D;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            euclidian controller
	 * @param settings
	 *            settings
	 */
	public EuclidianViewInput3D(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);

	}
	
	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		return new EuclidianViewInput3DCompanion(this);
	}

	@Override
	public EuclidianViewInput3DCompanion getCompanion() {
		return (EuclidianViewInput3DCompanion) super.getCompanion();
	}

	@Override
	protected void start(){
		input3D = ((EuclidianControllerInput3D) euclidianController).input3D;
		input3D.init(this);
		getCompanion().setInput3D(input3D);
		super.start();
	}


	@Override
	public void drawMouseCursor(Renderer renderer1) {

		getCompanion().drawMouseCursor(renderer1);

	}
	

	@Override
	public boolean isPolarized() {
		return input3D.useInterlacedPolarization();
	}


	@Override
	protected void setPickPointFromMouse(GPoint mouse) {
		super.setPickPointFromMouse(mouse);

		if (input3D.currentlyUseMouse2D()) {
			return;
		}

		if (mouse instanceof GPointWithZ) {
			pickPoint.setZ(((GPointWithZ) mouse).getZ());
		}
	}


	@Override
	protected Renderer createRenderer() {
		RendererJogl.setDefaultProfile();
		// return new RendererLogicalPickingGL2(this, !app.isApplet());
		return new RendererCheckGLVersionD(this, true);
	}

	

	@Override
	public boolean isStereoBuffered() {
		return input3D.isStereoBuffered();
	}

	@Override
	public boolean wantsStereo() {
		return input3D.wantsStereo();
	}

	@Override
	public Coords getHittingDirection() {
		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			return input3D.getMouse3DDirection();
		}
		return super.getHittingDirection();
	}

	@Override
	public Coords getHittingOrigin(GPoint mouse) {
		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			return input3D.getMouse3DScenePosition();
		}
		return super.getHittingOrigin(mouse);
	}


	@Override
	public void initAxisAndPlane() {
		super.initAxisAndPlane();
		getCompanion().initAxisAndPlane();
	}

	@Override
	protected boolean decorationVisible() {
		return (!input3D.hasMouseDirection() || input3D.currentlyUseMouse2D())
				&& super.decorationVisible();
	}

	@Override
	protected boolean drawCrossForFreePoint() {
		return !input3D.hasMouseDirection() || input3D.currentlyUseMouse2D();
	}



	@Override
	public boolean useHandGrabbing() {
		return input3D.useHandGrabbing() && !input3D.currentlyUseMouse2D();
	}




}
