package org.geogebra.common.main.settings;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.parser.function.ParserFunctions;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;

/**
 * Config for CAS Calculator app
 */
public class AppConfigCas extends AppConfigGraphing {

	@Override
	public String getAppTitle() {
		return "CASCalculator";
	}

	@Override
	public String getAppName() {
		return "GeoGebraCASCalculator";
	}

	@Override
	public String getAppNameShort() {
		return "CasCalculator.short";
	}

	@Override
	public String getTutorialKey() {
		return "cas_tutorials";
	}

	@Override
	public boolean isCASEnabled() {
		return true;
	}

	@Override
	public String getPreferencesKey() {
		return "_cas";
	}

	@Override
	public SymbolicMode getSymbolicMode() {
		return SymbolicMode.SYMBOLIC_AV;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return CommandFilterFactory.createCasCommandFilter();
	}

	@Override
	public boolean hasAutomaticLabels() {
		return false;
	}

	@Override
	public boolean hasAutomaticSliders() {
		return false;
	}

	@Override
	public boolean showToolsPanel() {
		return false;
	}

	@Override
	public String getAppCode() {
		return "cas";
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.CAS;
	}

	@Override
	public String getExamMenuItemText() {
		return "ExamCAS.short";
	}

	@Override
	public OperationArgumentFilter createOperationArgumentFilter() {
		return null;
	}

	@Override
	public ParserFunctions createParserFunctions() {
		return ParserFunctionsFactory.createParserFunctions();
	}
}
