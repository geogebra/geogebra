package org.geogebra.suite;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;

public class BaseSuiteTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AlgebraTest.createApp(new AppConfigUnrestrictedGraphing());
	}
}
