package org.geogebra.web.test;

import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcherCommands3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcherAdvanced;
import org.geogebra.common.kernel.commands.CommandDispatcherCAS;
import org.geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;
import org.geogebra.common.kernel.commands.CommandDispatcherProver;
import org.geogebra.common.kernel.commands.CommandDispatcherScripting;
import org.geogebra.common.kernel.commands.CommandDispatcherStats;
import org.geogebra.common.kernel.commands.CommandDispatcherSteps;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.web.kernel3D.commands.CommandDispatcher3DW;
import org.geogebra.web.geogebra3D.web.main.AppWapplet3D;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;
import org.geogebra.web.html5.util.ArticleElementInterface;

/**
 * Like production, mock async loading of commands
 *
 */
public class AppWapplet3DTest extends AppWapplet3D {

	/**
	 * @param ae
	 *            settings
	 * @param gf
	 *            frame
	 * @param laf
	 *            laf
	 * @param device
	 *            device
	 */
	public AppWapplet3DTest(ArticleElementInterface ae, GeoGebraFrameFull gf,
			GLookAndFeel laf, GDevice device) {
		super(ae, gf, laf, device);
	}

	@Override
	public CommandDispatcher3D getCommand3DDispatcher(Kernel cmdKernel) {
		return new CommandDispatcher3DW(cmdKernel) {
			@Override
			public CommandDispatcherInterface get3DDispatcher() {
				return new CommandDispatcherCommands3D();
			}
		};
	}

	@Override
	public CommandDispatcherW getCommandDispatcher(Kernel cmdKernel) {
		return new CommandDispatcherW(cmdKernel) {

			@Override
			public CommandDispatcherInterface getStatsDispatcher() {
				return new CommandDispatcherStats();
			}

			@Override
			public CommandDispatcherInterface getDiscreteDispatcher() {
				return new CommandDispatcherDiscrete();
			}

			@Override
			public CommandDispatcherInterface getCASDispatcher() {
				return new CommandDispatcherCAS();
			}

			@Override
			public CommandDispatcherInterface getScriptingDispatcher() {
				return new CommandDispatcherScripting();
			}

			@Override
			public CommandDispatcherInterface getAdvancedDispatcher() {
				return new CommandDispatcherAdvanced();
			}

			@Override
			public CommandDispatcherInterface getStepsDispatcher() {
				return new CommandDispatcherSteps();
			}

			@Override
			public CommandDispatcherInterface getProverDispatcher() {
				return new CommandDispatcherProver();
			}
		};
	}

}
