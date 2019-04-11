package org.geogebra.common.main;

import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.jre.headless.App3DCompanionHeadless;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.kernel.commands.CommandDispatcher3DJre;
import org.geogebra.common.jre.main.LocalizationJre;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;

public final class AppCommon3D extends AppCommon {

	public AppCommon3D(LocalizationJre loc, AwtFactory awtFactory) {
		super(loc, awtFactory);
	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionHeadless(this);
	}

	@Override
	public CommandDispatcher getCommand3DDispatcher(Kernel cmdKernel) {
		return new CommandDispatcher3DJre(cmdKernel);
	}
	
	@Override
	public boolean is3D(){
		return true;
	}
}
