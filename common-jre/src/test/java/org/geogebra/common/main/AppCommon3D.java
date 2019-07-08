package org.geogebra.common.main;

import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.jre.headless.App3DCompanionHeadless;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.kernel.commands.CommandDispatcher3DJre;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;

public class AppCommon3D extends AppCommon {

	private DrawEquation drawEquation;

	public AppCommon3D(LocalizationJre loc, AwtFactory awtFactory) {
		super(loc, awtFactory);
	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionHeadless(this);
	}

	@Override
	public CommandDispatcher newCommand3DDispatcher(Kernel cmdKernel) {
		return new CommandDispatcher3DJre(cmdKernel);
	}
	
	@Override
	public boolean is3D(){
		return true;
	}

	public DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationCommon();
		}
		return drawEquation;

	}
}
