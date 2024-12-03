package org.geogebra.common.main;

import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.jre.headless.App3DCompanionHeadless;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.EuclidianController3DNoGui;
import org.geogebra.common.jre.headless.EuclidianView3DNoGui;
import org.geogebra.common.jre.kernel.commands.CommandDispatcher3DJre;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;

public class AppCommon3D extends AppCommon {

	private DrawEquation drawEquation;
	private boolean is3Dactive;
	private EuclidianView3DNoGui ev3d;

	public AppCommon3D(LocalizationJre loc, AwtFactory awtFactory, AppConfig appConfig) {
		super(loc, awtFactory, appConfig);
	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionHeadless(this);
	}

	@Override
	public CommandDispatcher newCommandDispatcher(Kernel cmdKernel) {
		return new CommandDispatcher3DJre(cmdKernel);
	}

	@Override
	public boolean is3D() {
		return true;
	}

	@Override
	public void setActiveView(int evID) {
		this.is3Dactive = evID == App.VIEW_EUCLIDIAN3D;
		getEuclidianView3D(); // make sure 3D exists
	}

	@Override
	public EuclidianView getActiveEuclidianView() {
		return is3Dactive && ev3d != null ? ev3d : euclidianView;
	}

	@Override
	public EuclidianView3DInterface getEuclidianView3D() {
		if (ev3d == null) {
			ev3d = new EuclidianView3DNoGui(
					new EuclidianController3DNoGui(this, kernel),
					this.getSettings().getEuclidian(3));
		}
		return ev3d;
	}

	@Override
	public DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationCommon();
		}
		return drawEquation;
	}
}
