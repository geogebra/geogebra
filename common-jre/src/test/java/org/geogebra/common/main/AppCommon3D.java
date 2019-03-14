package org.geogebra.common.main;

import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.main.App3DCompanion;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.jre.kernel.commands.CommandDispatcher3DJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.settings.EuclidianSettings;

public class AppCommon3D extends AppCommon {

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanion(this) {

			@Override
			protected EuclidianViewForPlaneCompanion createEuclidianViewForPlane(
					ViewCreator plane, EuclidianSettings evSettings,
					boolean panelSettings) {
				return null;
			}

			@Override
			public DockPanel getPanelForPlane() {
				return null;
			}
		};
	}

	@Override
	public CommandDispatcher getCommand3DDispatcher(Construction construction) {
		return new CommandDispatcher3DJre(construction);
	}
	
	public boolean is3D(){
		return true;
	}
}
