package org.geogebra.common.main.settings.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.factory.PropertiesFactory;
import org.geogebra.common.properties.factory.ScientificPropertiesFactory;

/**
 * Config for Scientific Calculator app
 */
public class AppConfigScientific extends AppConfigGraphing {

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
	public String getTutorialKey() {
		return "TutorialScientific";
	}

	@Override
	public boolean allowsSuggestions() {
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
	public CommandFilter getCommandFilter() {
		return CommandFilterFactory.createSciCalcCommandFilter();
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
		return null;
	}

	@CheckForNull
	@Override
	public SyntaxFilter newCommandSyntaxFilter() {
		return null;
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.SCIENTIFIC;
	}

	@Override
	public boolean hasExam() {
		return false;
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
	public int getEnforcedLineEquationForm() {
		return -1;
	}

	@Override
	public int getEnforcedConicEquationForm() {
		return -1;
	}

	@Override
	public OperationArgumentFilter createOperationArgumentFilter() {
		return null;
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
}
