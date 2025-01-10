package org.geogebra.common.main.settings.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.ScientificOperationArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.ScientificCommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.settings.config.equationforms.DefaultEquationBehaviour;
import org.geogebra.common.main.syntax.suggestionfilter.ScientificSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.factory.PropertiesFactory;
import org.geogebra.common.properties.factory.ScientificPropertiesFactory;

/**
 * Config for Scientific Calculator app
 */
public class AppConfigScientific extends AppConfigGraphing {

	public AppConfigScientific(String appCode) {
		super(appCode, GeoGebraConstants.SCIENTIFIC_APPCODE);
	}

	public AppConfigScientific() {
		super(GeoGebraConstants.SCIENTIFIC_APPCODE, null);
	}

	@Override
	public String getAppTitle() {
		return "ScientificCalculator";
	}

	@Override
	public String getAppName() {
		return "GeoGebraScientificCalculator";
	}

	@Override
	public String getAppNameShort() {
		return "ScientificCalculator.short";
	}

	@Override
	public String getAppNameWithoutCalc() {
		return  "Scientific";
	}

	@Override
	public String getTutorialKey() {
		return "TutorialScientific";
	}

	@Override
	public boolean allowsSuggestions() {
		return false;
	}

	@Override
	public boolean showToolsPanel() {
		return false;
	}

	@Override
	public boolean isGreekAngleLabels() {
		return false;
	}

	@Override
	public String getForcedPerspective() {
		return Perspective.SCIENTIFIC + "";
	}

	@Override
	public boolean isEnableStructures() {
		return false;
	}

	@Override
	public boolean hasSlidersInAV() {
		return false;
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
	public CommandFilter createCommandFilter() {
		return CommandFilterFactory.createSciCalcCommandFilter();
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
		return new ScientificCommandArgumentFilter();
	}

	@CheckForNull
	@Override
	public SyntaxFilter newCommandSyntaxFilter() {
		return new ScientificSyntaxFilter();
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.SCIENTIFIC;
	}

	@Override
	public boolean hasExam() {
		return getSubAppCode() != null; // only suite scicalc has exam
	}

	@Override
	public String getExamMenuItemText() {
		return "";
	}

	@Override
	public Set<FillType> getAvailableFillTypes() {
		return new HashSet<>(Arrays.asList(FillType.values()));
	}

	@Override
	public boolean isObjectDraggingRestricted() {
		return false;
	}

	@Override
	public EquationBehaviour getEquationBehaviour() {
		return new DefaultEquationBehaviour();
	}

	@Override
	public ExpressionFilter createOperationArgumentFilter() {
		return ScientificOperationArgumentFilter.INSTANCE;
	}

	@Override
	public ParserFunctionsFactory createParserFunctionsFactory() {
		return ParserFunctionsFactory.createParserFunctionsFactory();
	}

	@Override
	public boolean shouldHideEquations() {
		return false;
	}

	@Override
	public PropertiesFactory createPropertiesFactory() {
		return new ScientificPropertiesFactory();
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		return AppKeyboardType.SCIENTIFIC;
	}

	@Override
	public boolean hasLabelForDescription() {
		return true;
	}

	@Override
	public boolean hasEuclidianView() {
		return false;
	}

	@Override
	public boolean hasOneVarStatistics() {
		return false;
	}

	@Override
	public boolean hasSpreadsheetView() {
		return false;
	}

	@Override
	public boolean hasDataImport() {
		return false;
	}
}
