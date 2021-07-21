package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.input3D.EuclidianViewInput3DCompanion;
import org.geogebra.common.geogebra3D.input3D.Input3D;
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

	private EuclidianViewInput3DCompanion companionInput3D;

	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		companionInput3D = new EuclidianViewInput3DCompanion(this);
		return companionInput3D;
	}

	@Override
	public EuclidianViewInput3DCompanion getCompanion() {
		return companionInput3D;
	}

	@Override
	protected void start() {
		input3D = ((EuclidianControllerInput3D) euclidianController).input3D;
		input3D.init(this);
		getCompanion().setInput3D(input3D);
		super.start();
	}

	@Override
	protected Renderer createRenderer() {
		RendererJogl.setDefaultProfile();
		// return new RendererLogicalPickingGL2(this, !app.isApplet());
		return new RendererCheckGLVersionD(this, true);
	}
}
