package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EuclidianViewCE;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ConditionalSerializer;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ExpressionValueType;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionExpander;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVarCollector;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Functional;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.cas.AlgoComplexSolve;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.commands.SymbolicProcessor;
import org.geogebra.common.kernel.geos.properties.DelegateProperties;
import org.geogebra.common.kernel.geos.properties.EquationType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.SymbolicUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Symbolic geo for CAS computations in AV
 * @author Zbynek
 */
public class GeoSymbolic extends GeoElement
		implements GeoSymbolicI, VarString, GeoEvaluatable, GeoFunctionable, DelegateProperties,
		HasArbitraryConstant, EuclidianViewCE, Functional {
	private ExpressionValue value;
	private final ArrayList<FunctionVariable> fVars = new ArrayList<>();
	private String casOutputString;
	private boolean isTwinUpToDate = false;
	private boolean isEuclidianShowable = true;
	private int tableColumn = -1;
	private boolean pointsVisible = true;
	private GeoFunction asFunction;
	private int pointStyle;
	private int pointSize;
	private boolean symbolicMode;
	private ArbitraryConstantRegistry constant;
	private boolean wrapInNumeric = false;

	private @CheckForNull GeoElement twinGeo;

	private @CheckForNull ExpressionValue numericValue;
	private int numericPrintFigures;
	private int numericPrintDecimals;
	private ConditionalSerializer conditionalSerializer;
	private ExpressionNode excludedEquation;

	/**
	 * @param c construction
	 * @param definition definition
	 */
	public GeoSymbolic(Construction c, @Nonnull ExpressionNode definition) {
		super(c);
		setDefinition(definition);
		symbolicMode = true;
		fixed = true;
	}

	/**
	 * @return output expression
	 */
	@Override
	public ExpressionValue getValue() {
		return value;
	}

	/**
	 * @param value output expression
	 */
	private void setValue(ExpressionValue value) {
		this.value = value;
	}

	@Override
	public ExpressionValueType getValueType() {
		if (value != null && ExpressionNode.isDefined(value)) {
			return value.getValueType();
		}
		if (getDefinition() != null) {
			return getDefinition().getValueType();
		}
		return ValueType.UNKNOWN;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SYMBOLIC;
	}

	@Override
	public GeoElement copy() {
		GeoSymbolic copy = new GeoSymbolic(cons, getDefinition().deepCopy(kernel));
		copy.setFromSymbolic(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		reuseDefinition(geo);
		fVars.clear();
		if (geo instanceof GeoSymbolic) {
			setFromSymbolic((GeoSymbolic) geo);
		}
	}

	private void setFromSymbolic(GeoSymbolic symbolic) {
		fVars.addAll(symbolic.fVars);
		value = symbolic.getValue();
		casOutputString = symbolic.casOutputString;
		numericValue = symbolic.numericValue;
		numericPrintFigures = symbolic.numericPrintFigures;
		numericPrintDecimals = symbolic.numericPrintDecimals;
		isTwinUpToDate = false;
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if ((symbolicMode || tpl.getStringType().isGiac()) || !hasNumericValue()
				|| isParametricTwin()) {
			if (value != null) {
				return value.toValueString(tpl);
			}
			return getDefinition().toValueString(tpl);
		} else {
			return getNumericValueString(tpl);
		}
	}

	private boolean isParametricTwin() {
		Equation eqn = twinGeo != null
				&& twinGeo.getDefinition() != null
				&& twinGeo.getDefinition().unwrap() instanceof Equation
				? (Equation) twinGeo.getDefinition().unwrap() : null;
		return eqn != null && "X".equals(eqn.getLHS().toString(StringTemplate.defaultTemplate));
	}

	/**
	 * Retrieves the output expression of this {@code GeoSymbolic}
	 * in a format visible to the user in the Algebra view.
	 * <p>
	 * Unlike {@link GeoSymbolic#getValue()}, this method takes into account the format
	 * of the output (e.g., for an input of {@code Normal(2, 0.5, 1)}, the output in the default format
	 * would be {@code (erf(-√2) + 1) / 2}, whereas after switching
	 * to the approximated output format, it would be {@code 0.0227501319482}).
	 * @return the output expression of {@code GeoSymbolic}
	 */
	public @Nonnull ExpressionValue getOutputExpression() {
		if (symbolicMode || !hasNumericValue()) {
			if (value != null) {
				return value;
			}
			return getDefinition();
		}
		return getNumericValueExpression();
	}

	/**
	 * If this represents explicit equation, return the right hand side, otherwise return this.
	 * @return right hand side or this
	 */
	public ExpressionValue getImplicitEquationRHSOrSelf() {
		if (value.unwrap() instanceof Equation) {
			Equation equation = (Equation) value.unwrap();
			if ("y".equals(equation.getLHS().toString(StringTemplate.defaultTemplate))
					&& containsOnlyX(equation.getRHS())) {
				return equation.getRHS();
			}
		}
		return this;
	}

	private boolean containsOnlyX(ExpressionNode rhs) {
		return !rhs.any(v -> v instanceof FunctionVariable
				&& !((FunctionVariable) v).getSetVarString().equals("x"));
	}

	private boolean hasNumericValue() {
		return numericValue != null || getTwinGeo() != null;
	}

	private ExpressionValue getNumericValueExpression() {
		assert hasNumericValue();
		GeoElementND twin = getTwinGeo();
		if (twin != null && twin.isGeoAngle()) {
			return twin;
		} else if (numericValue != null) {
			return numericValue;
		} else {
			assert twin != null;
			return twin;
		}
	}

	private String getNumericValueString(StringTemplate tpl) {
		assert hasNumericValue();
		GeoElementND twin = getTwinGeo();
		if (twin != null && twin.isGeoAngle()) {
			return twin.toValueString(tpl);
		} else if (numericValue != null) {
			return numericValue.toValueString(tpl);
		} else {
			assert twin != null;
			return twin.toValueString(tpl.isLatex() ? tpl.deriveWithDisplayStyle() : tpl);
		}
	}

	@Override
	protected boolean showInEuclidianView() {
		GeoElementND twin = getTwinGeo();
		return isEuclidianShowable && twin != null && twin.isEuclidianShowable()
				&& !twin.isLabelSet();
	}

	@Override
	public HitType getLastHitType() {
		return HitType.NONE;
	}

	@Override
	public void setError(String key) {
		// TODO deal with errors on parsing
	}

	@Override
	public void setAssignmentType(AssignmentType assignmentType) {
		// compatibility with CAS view, do nothing
	}

	@Override
	public void resetDefinition() {
		super.resetDefinition();
		fVars.clear();
	}

	private ExpressionValue fixMatrixInput(ExpressionValue casInputArg) {
		// neglect dummy variable lhs if rhs is matrix
		ExpressionValue ret = casInputArg;
		if (((ExpressionNode) casInputArg).getLeft() instanceof Equation) {
			Equation eq = (Equation) ((ExpressionNode) casInputArg).getLeft();
			boolean lIsDummy = eq.getLHS().getLeft() instanceof GeoDummyVariable;
			boolean rIsMatrix = eq.getRHS().getLeft() instanceof MyList
					&& ((MyList) (eq.getRHS().getLeft())).isMatrix();
			if (lIsDummy && rIsMatrix) {
				ret = eq.getRHS().getLeft();
			}
		}
		return ret;
	}

	@Override
	public void computeOutput() {
		ExpressionValue casInputArg = getDefinition().deepCopy(kernel)
				.traverse(FunctionExpander.newFunctionExpander(this));

		Command casInput = getCasInput(fixMatrixInput(casInputArg));
		if (casInput.getName().equals(Commands.Solve.name()) && casInput.getArgumentNumber() == 1) {
			SymbolicProcessor.autoCompleteVariables(casInput);
		}
		String casResult = calculateCasResult(casInput);

		casOutputString = casResult;
		ExpressionValue casOutput = parseOutputString(casResult);
		setValue(casOutput);

		setSymbolicMode();
		setFunctionVariables();

		isTwinUpToDate = false;
		isEuclidianShowable = shouldBeEuclidianVisible(casInput);
		numericValue = maybeComputeNumericValue(casOutput);
	}

	private String calculateCasResult(Command casInput) {
		ArbitraryConstantRegistry constant = getArbitraryConstant();
		constant.setSymbolic(!shouldBeEuclidianVisible(casInput));

		if (isUndefined(casInput)) {
			return "?";
		}

		String casResult = evaluateGeoGebraCAS(casInput, constant);
		if (GeoFunction.isUndefined(casResult) && argumentsDefined(casInput)) {
			casResult = tryNumericCommand(casInput, casResult);
		}

		if (casInput.getName().equals(Commands.SolveODE.name())) {
			return normalizeSolveODE(casResult, casInput);
		}
		return casResult;
	}

	private boolean isUndefined(Command command) {
		return isLengthOfCurve(command);
	}

	private boolean isLengthOfCurve(Command command) {
		if (Commands.Length.name().equals(command.getName())
				&& command.getArgumentNumber() == 1) {
			ExpressionValue arg = command.getArgument(0).unwrap();
			if (arg instanceof GeoSymbolic) {
				GeoElementND twinGeo = ((GeoSymbolic) arg).getTwinGeo();
				return twinGeo != null && twinGeo.isGeoConic();
			}
		}
		return false;
	}

	private String normalizeSolveODE(String casResult, Command casInput) {
		try {
			ExpressionValue parsed = kernel.getParser()
					.parseGeoGebraExpression(casResult).unwrap();
			if (parsed instanceof Equation) {
				Function fn = ((Equation) parsed).asFunction();
				if (fn != null) {
					return fn.toString(getStringTemplate(casInput));
				}
			}
		} catch (Throwable t) {
			Log.debug(t);
		}
		return casResult;
	}

	private boolean argumentsDefined(Command casInput) {
		return casInput.any(v ->
				!v.toValueString(StringTemplate.defaultTemplate).contains("?"));
	}

	private String tryNumericCommand(Command casInput, String casResult) {
		String result = casResult;
		if (Commands.Integral.name().equals(casInput.getName())) {
			casInput.setName(Commands.NIntegral.name());
			result = evaluateGeoGebraCAS(casInput, constant);
			return result;
		}

		Command numericVersion = new Command(kernel, "Numeric", false);
		numericVersion.addArgument(casInput.wrap());
		String numResult = evaluateGeoGebraCAS(numericVersion, constant);

		if (!GeoFunction.isUndefined(numResult)) {
			return numResult;
		}

		return result;
	}

	public void setWrapInNumeric(boolean wrapInNumeric) {
		this.wrapInNumeric = wrapInNumeric;
	}

	/**
	 * @return whether this should be wrapped in numeric command for symbolic toggle
	 */
	public boolean shouldWrapInNumeric() {
		return wrapInNumeric;
	}

	private boolean isTopLevelCommandNumeric() {
		Command topCommand = getDefinition().getTopLevelCommand();
		return topCommand != null && (isNSolve(topCommand) || isNumericWrapOfSolve(topCommand));
	}

	private boolean isNSolve(Command command) {
		return Commands.NSolve.name().equals(command.getName());
	}
	
	private boolean isNumericWrapOfSolve(Command command) {
		if (!Commands.Numeric.name().equals(command.getName())) {
			return false;
		}
		ExpressionNode arg = command.getArgument(0);
		if (arg.getTopLevelCommand() != null) {
			return Commands.Solve.name().equals(arg.getTopLevelCommand().getName());
		}
		return false;
	}

	private Command getCasInput(ExpressionValue casInputArg) {
		Command casInput;
		if (casInputArg.unwrap() instanceof Command) {
			casInput = (Command) casInputArg.unwrap();
		} else {
			casInput = new Command(kernel, "Evaluate", false);
			casInput.addArgument(casInputArg.wrap());
		}
		return casInput;
	}

	private String evaluateGeoGebraCAS(Command command, ArbitraryConstantRegistry constant) {
		return kernel.getGeoGebraCAS().evaluateGeoGebraCAS(
				command.wrap(), constant, getStringTemplate(command), null, kernel);
	}

	private boolean shouldBeEuclidianVisible(Command input) {
		String inputName = input.getName();
		return !Commands.Solve.name().equals(inputName)
				&& !Commands.NSolve.name().equals(inputName)
				&& !Commands.IntegralSymbolic.name().equals(inputName)
				&& !Commands.IsInteger.name().equals(inputName);
	}

	private ExpressionValue maybeComputeNumericValue(ExpressionValue casOutput) {
		if (!SymbolicUtil.shouldComputeNumericValue(casOutput)) {
			return null;
		}
		Log.debug("GeoSymbolic is a number value, calculating numeric result");
		try {
			return computeNumericValue(casOutput);
		} catch (Exception e) {
			return null;
		}
	}

	private ExpressionValue computeNumericValue(ExpressionValue casOutput) {
		Command command;
		numericPrintFigures = kernel.getPrintFigures();
		numericPrintDecimals = kernel.getPrintDecimals();
		if (numericPrintFigures == -1) {
			command = new Command(kernel, "Round", false);
			command.addArgument(casOutput.wrap());
			command.addArgument(new MyDouble(kernel, numericPrintDecimals).wrap());
		} else {
			command = new Command(kernel, "Numeric", false);
			command.addArgument(casOutput.wrap());
			command.addArgument(new MyDouble(kernel, numericPrintFigures).wrap());
		}

		String casResult = evaluateGeoGebraCAS(command, constant);
		return parseOutputString(casResult);
	}

	private void maybeRecomputeNumericValue() {
		if (numericValue == null) {
			return;
		}
		if (numericPrintFigures != kernel.getPrintFigures()
				|| numericPrintDecimals != kernel.getPrintFigures()) {
			numericValue = maybeComputeNumericValue(value);
		}
	}

	private StringTemplate getStringTemplate(Command input) {
		String inputName = input.getName();
		return Commands.Numeric.name().equals(inputName) && input.getArgumentNumber() == 2
				? StringTemplate.numericNoLocal : StringTemplate.prefixedDefault;
	}

	private ExpressionValue parseOutputString(String output) {
		ExpressionValue value = kernel.getGeoGebraCAS().parseOutput(output, this, kernel);
		checkCASVector(value);
		return value;
	}

	private void checkCASVector(ExpressionValue value) {
		AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
		if (value != null
				&& value.unwrap() instanceof MyVecNDNode
				&& algebraProcessor.hasVectorLabel(this)) {
			((MyVecNDNode) value.unwrap()).setupCASVector();
		}
	}

	private void setSymbolicMode() {
		// if user preference is symbolic, but symbolic mode is not available, turn it off
		// non-symbolic mode is always possible -- keep user preference
		if (kernel.getGeoGebraCAS().getCurrentCAS().isLoaded() && symbolicMode) {
			boolean isValueDefined = isCasValueDefined();
			setSymbolicMode(!isTopLevelCommandNumeric() && isValueDefined, false);
		}
	}

	private void setFunctionVariables() {
		if (!fVars.isEmpty() || SymbolicUtil.containsUndefinedOrIsEmpty(this)) {
			return;
		}
		Iterable<FunctionVariable> variables = computeFunctionVariables();
		setVariables(variables);
	}

	private Iterable<FunctionVariable> computeFunctionVariables() {
		if (getDefinition() == null) {
			return Collections.emptyList();
		}
		ExpressionValue def = getDefinition().unwrap();
		if (def instanceof FunctionNVar) {
			return Arrays.asList(((FunctionNVar) def).getFunctionVariables());
		} else if (getDefinition().getLocalVariables().size() > 0) {
			List<String> localVariables = getDefinition().getLocalVariables();
			return localVariables.stream().map((var) -> new FunctionVariable(kernel, var))
					.collect(Collectors.toList());
		} else if (def instanceof Command && shouldShowFunctionVariablesInOutputFor((Command) def)
				&& !valueIsListOrPoint()) {
			return collectVariables();
		} else if (getDefinition().containsFreeFunctionVariable(null)) {
			return collectVariables();
		} else {
			return variablesFromOutput();
		}
	}

	protected List<FunctionVariable> collectVariables() {
		FunctionVarCollector functionVarCollector = FunctionVarCollector
				.getCollector();
		getDefinition().traverse(functionVarCollector);
		List<FunctionVariable> vars = Arrays.asList(functionVarCollector.buildVariables(kernel));
		if (vars.isEmpty()) {
			return variablesFromOutput();
		} else {
			return vars;
		}
	}

	private List<FunctionVariable> variablesFromOutput() {
		FunctionVarCollector functionVarCollector = FunctionVarCollector
				.getCollector();
		try {
			ExpressionNode nodeFromOutput = getNodeFromOutput();
			if (nodeFromOutput.any(ex -> ex instanceof Equation)) {
				return List.of();
			}
			nodeFromOutput.traverse(functionVarCollector);
			return Arrays.asList(functionVarCollector.buildVariables(kernel));
		} catch (ParseException e) {
			return List.of();
		}
	}

	private static boolean shouldShowFunctionVariablesInOutputFor(Command command) {
		return Stream.of(Commands.Solutions, Commands.SolveODE).map(Commands::getCommand)
				.noneMatch(command.getName()::equals); // APPS-1821, APPS-2190
	}

	private boolean valueIsListOrPoint() {
		return value.unwrap() instanceof MyList || value.unwrap() instanceof MyVecNode; // APPS-4396
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (value == null) {
			return "?";
		}
		StringBuilder sb = new StringBuilder();
		appendAssignmentLHS(sb, tpl);
		sb.append(getLabelDelimiterWithSpace(tpl));
		sb.append(value.toString(tpl));
		return sb.toString();
	}

	@Override
	public String getAssignmentLHS(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		appendAssignmentLHS(sb, tpl);
		return sb.toString();
	}

	private void appendAssignmentLHS(StringBuilder sb, StringTemplate tpl) {
		sb.append(getLabelSimple());
		if (!fVars.isEmpty()) {
			sb.append(tpl.leftBracket());
			appendVarString(sb, tpl);
			sb.append(tpl.rightBracket());
		}
	}

	private void appendVarString(StringBuilder sb,
			final StringTemplate tpl) {
		for (int i = 0; i < fVars.size() - 1; i++) {
			sb.append(fVars.get(i).toString(tpl));
			sb.append(", ");
		}
		sb.append(fVars.get(fVars.size() - 1).toString(tpl));
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	/**
	 * @param functionVariables function variables
	 */
	public void setVariables(Iterable<FunctionVariable> functionVariables) {
		fVars.clear();
		for (FunctionVariable fv : functionVariables) {
			fVars.add(fv.deepCopy(kernel));
		}
	}

	/**
	 * @return function variables
	 */
	@Override
	public FunctionVariable[] getFunctionVariables() {
		return fVars.toArray(new FunctionVariable[0]);
	}

	/**
	 * @return geo for drawing, null if the output contains variables
	 */
	public @CheckForNull GeoElementND getTwinGeo() {
		if (isTwinUpToDate) {
			return twinGeo;
		}
		GeoElementND newTwin = ensureNotInConstruction(createTwinGeo());
		if (newTwin instanceof GeoList) {
			newTwin.setEuclidianVisible(true);
		}
		if (twinGeo != null) {
			twinGeo.remove();
		}

		if (twinGeo != null && newTwin != null) {
			newTwin.setVisualStyle(this);
			twinGeo = newTwin.toGeoElement();
		} else if (newTwin == null) {
			twinGeo = null;
		} else {
			twinGeo = newTwin.toGeoElement();
			setVisualStyle(twinGeo);
		}
		isTwinUpToDate = true;

		return twinGeo;
	}

	private GeoElementND ensureNotInConstruction(GeoElementND computed) {
		GeoElementND newTwin = computed;
		if (newTwin != null && newTwin.isLabelSet()) {
			newTwin = newTwin.copyInternal(cons);
			if (newTwin.isGeoNumeric()) {
				((GeoNumeric) newTwin).setDrawable(false);
			}
			computed.setFixed(newTwin.isLocked());
		}
		return newTwin;
	}

	private GeoElementND createTwinGeo() {
		if (getDefinition() == null) {
			return null;
		}
		boolean isSuppressLabelsActive = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		try {
			return process(getTwinInput());
		} catch (CommandNotLoadedError err) {
			// by failing the whole twin creation we make sure this uses the same path
			// in web and other platforms
			if (!isLabelSet()) {
				remove();
			}
			throw err;
		} catch (Throwable throwable) {
			try {
				return process(getTwinFallbackInput());
			} catch (Throwable throwable2) {
				return null;
			}
		} finally {
			cons.setSuppressLabelCreation(isSuppressLabelsActive);
		}
	}

	private ExpressionNode getTwinInput() throws ParseException {
		if (useOutputAsMainTwin()) {
			return getNodeFromOutput();
		}
		return getNodeFromInput();
	}

	private ExpressionNode getTwinFallbackInput() throws ParseException {
		if (useOutputAsMainTwin()) {
			return getNodeFromInput();
		}
		return getNodeFromOutput();
	}

	private boolean useOutputAsMainTwin() {
		return (constant != null && constant.getTotalNumberOfConsts() > 0)
				|| (getDefinition() != null && isFullyComputedByCAS(getDefinition().unwrap()));
	}

	/**
	 * @param unwrappedDefinition unwrapped definition of this element
	 * @return whether running the input through AlgebraProcessor brings no value compared to
	 * just processing output of the CAS computation
	 */
	private boolean isFullyComputedByCAS(ExpressionValue unwrappedDefinition) {
		if (unwrappedDefinition instanceof Equation) {
			return true;
		}
		Commands cmd = unwrappedDefinition instanceof Command
				? Commands.stringToCommand(((Command) unwrappedDefinition).getName()) : null;
		if (cmd == null) {
			return false;
		}
		switch (cmd) {
		case Simplify:
		case Expand:
		case Factor:
		case TrigSimplify:
		case TrigCombine:
		case TrigExpand:
		case Min:
		case Max:
		case Point:
		case Distance:
			return true;
		default: return false;
		}
	}

	private ExpressionNode getNodeFromOutput() throws ParseException {
		ValidExpression validExpression =
				kernel.getParser().parseGeoGebraExpression(LabelManager.HIDDEN_PREFIX + ":"
						+ casOutputString);
		validExpression.setLabels(null);
		return validExpression.wrap();
	}

	private ExpressionNode getNodeFromInput() {
		ExpressionNode node = getDefinition().deepCopy(kernel)
				.traverse(new FunctionExpander())
				.traverse(createPrepareDefinition())
				.wrap();
		node.setLabel(null);
		return node;
	}

	@Override
	public void setLabelSimple(String lab) {
		super.setLabelSimple(lab);
		checkCASVector(value);
	}

	private Traversing createPrepareDefinition() {
		return new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev instanceof GeoSymbolic) {
					ExpressionValue symbolicValue = ((GeoSymbolic) ev).getValue();
					if (symbolicValue != null) {
						ExpressionValue symbolicValueCopy = symbolicValue.deepCopy(kernel);
						return symbolicValueCopy.traverse(this);
					}
				}
				if (ev instanceof GeoDummyVariable) {
					GeoDummyVariable variable = (GeoDummyVariable) ev;
					return new Variable(variable.getKernel(), variable.getVarName());
				}
				if (ev instanceof Command) {
					Command command = (Command) ev;
					command = checkIntegralCommand(command);
					return command;
				}
				return ev;
			}

			private Command checkIntegralCommand(Command command) {
				if (command.getName().equals(Commands.Integral.name())
						&& command.getArgumentNumber() == 4) {
					ExpressionNode function = command.getArgument(0);
					ExpressionNode lowerLimit = command.getArgument(2);
					ExpressionNode upperLimit = command.getArgument(3);

					Command newCommand = new Command(kernel, command.getName(), false);
					newCommand.addArgument(function);
					newCommand.addArgument(lowerLimit);
					newCommand.addArgument(upperLimit);
					return newCommand;
				}
				return command;
			}
		};
	}

	private GeoElement process(ExpressionNode expressionNode) throws CircularDefinitionException {
		registerFunctionVariablesIfHasFunction(expressionNode);
		expressionNode.traverse(Traversing.GgbVectRemover.getInstance());
		AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
		if (algebraProcessor.hasVectorLabel(this)) {
			expressionNode.setForceVector();
		}
		EvalInfo twinInfo = new EvalInfo(false, true)
				.withAssignments(false).withAutocreate(false);
		GeoElement[] elements = algebraProcessor.processValidExpression(expressionNode, twinInfo);
		GeoElement result = processResult(elements);
		AlgoElement parentAlgo = elements[0].getParentAlgorithm();
		if (cons.isRegisteredEuclidianViewCE(parentAlgo)) {
			cons.unregisterEuclidianViewCE(parentAlgo);
			cons.registerEuclidianViewCE(this);
		} else {
			cons.unregisterEuclidianViewCE(this);
		}
		result.setFixed(true);
		return result;
	}

	private GeoElement processResult(GeoElement[] elements) {
		GeoElement result = elements.length > 1 || needsListWrapping(elements[0])
				? toGeoList(elements) : elements[0];
		if (isOutputOfCSolveCommand()) {
			handleOutputOfCSolveCommand(result);
		} else if (algoParent instanceof AlgoComplexSolve && result instanceof GeoLine) {
			return createPointFromEquation((GeoLine) result);
		}
		return result;
	}

	private boolean needsListWrapping(GeoElement geo) {
		// in AV these may return 1 or more points, in CAS they always return a list
		// forcing list wrapping makes the style and behavior independent on number of results
		GetCommand cmd = geo.getParentAlgorithm() == null
				? null : geo.getParentAlgorithm().getClassName();
		return cmd == Commands.Root || cmd == Commands.Extremum || cmd == Commands.Intersect
				|| cmd == Commands.Asymptote;
	}

	private void registerFunctionVariablesIfHasFunction(ExpressionNode functionExpression) {
		Function function =
				functionExpression.isLeaf() && functionExpression.getLeft() instanceof Function
						? (Function) functionExpression.getLeft()
						: null;
		FunctionVariable[] variables = function != null ? function.getFunctionVariables() : null;
		if (variables != null) {
			for (FunctionVariable functionVariable : variables) {
				cons.registerFunctionVariable(functionVariable.getSetVarString());
			}
		}
	}

	private GeoElement toGeoList(GeoElement[] elements) {
		GeoList geoList = new GeoList(cons);
		for (GeoElement element : elements) {
			geoList.add(element);
		}
		return geoList;
	}

	/**
	 * In case the created, complex GeoPoint(s) are the outcome of the CSolve command. We want to
	 * make sure they are printed in the form x = a + b*i. If the result is a list containing both
	 * GeoPoints and GeoLines, this method ensures the created GeoLines are replaced by a GeoPoint.
	 * @param result GeoElement
	 */
	private void handleOutputOfCSolveCommand(GeoElement result) {
		if (result instanceof GeoList) {
			unifySolutionFormatForCSolveCommand((GeoList) result);
		}
	}

	/**
	 * @return Whether this is an output of the CSolve command. Also checks if it
	 * is a nested command.
	 */
	private boolean isOutputOfCSolveCommand() {
		if (getDefinition() == null || getDefinition().getTopLevelCommand() == null) {
			return false;
		}
		Command command = getDefinition().getTopLevelCommand();
		if (command.getName().equals(Commands.CSolve.name())) {
			return true;
		}
		return Arrays.stream(command.getArguments())
				.map(ExpressionNode::getLeft)
				.filter(arg -> arg instanceof GeoSymbolic)
				.map(left -> (GeoSymbolic) left)
				.anyMatch(GeoSymbolic::isOutputOfCSolveCommand);
	}

	/**
	 * Ensures the output of the CSolve command is resulting in GeoPoints.
	 * @param list GeoList
	 */
	private void unifySolutionFormatForCSolveCommand(GeoList list) {
		list.replaceAll(element -> {
			if (element instanceof GeoLine || element instanceof GeoPlaneND) {
				return createPointFromEquation((EquationValue) element);
			}
			return element;
		});
	}

	private GeoPoint createPointFromEquation(EquationValue equation) {
		GeoPoint point = new GeoPoint(getConstruction(), 0, 0, 1);
		ExpressionValue lhs = equation.getEquation().getLHS().unwrap();
		ExpressionValue rhs = equation.getEquation().getRHS().unwrap();
		if (lhs instanceof FunctionVariable) {
			String varStr = ((FunctionVariable) lhs).getSetVarString();
			point.setComplexSolutionVar(varStr);
			if (rhs.isNumberValue()) {
				point.setX(rhs.evaluateDouble());
			}
		}
		point.setComplex();
		point.updateCoords();
		return point;
	}

	@Override
	final public void setBasicVisualStyle(final GeoElement geo) {
		super.setBasicVisualStyle(geo);
		if (geo instanceof PointProperties) {
			setPointSize(((PointProperties) geo).getPointSize());
			setPointStyle(((PointProperties) geo).getPointStyle());
		}
	}

	@Override
	public char getLabelDelimiter() {
		return getDefinition().unwrap() instanceof Equation ? ':' : '=';
	}

	@Override
	public String getVarString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fVars.size() - 1; i++) {
			sb.append(fVars.get(i).toString(tpl));
			sb.append(", ");
		}
		sb.append(fVars.get(fVars.size() - 1).toString(tpl));
		return sb.toString();
	}

	@Override
	public String getDefaultLabel() {
		GeoElementND twin = getTwinGeo();
		if (twin != null) {
			return twin.getDefaultLabel();
		}
		if (getEquationTypeForLabeling() == EquationType.EXPLICIT) {
			return getLabelManager()
					.getNextIndexedLabel(LabelType.functionLabels);
		}
		return super.getDefaultLabel();
	}

	@Override
	public Function getFunction() {
		GeoElementND twin = getTwinGeo();
		if (twin instanceof GeoFunctionable) {
			return ((GeoFunctionable) twin).getFunction();
		}
		ExpressionNode alwaysUndefined = new ExpressionNode(kernel, Double.NaN);
		return new Function(kernel, alwaysUndefined);
	}

	/**
	 * Still called from multiple places, see APPS-801
	 */
	@Override
	public GeoFunction getGeoFunction() {
		if (asFunction != null) {
			return asFunction;
		}
		GeoFunction ret = kernel.getGeoFactory().newFunction(this);
		if (!ret.isIndependent()) {
			asFunction = ret;
		}

		return ret;
	}

	@Override
	public GeoFunction getGeoDerivative(int order, boolean fast) {
		return getGeoFunction().getGeoDerivative(order, fast);
	}

	@Override
	public double value(double x) {
		GeoElementND twin = getTwinGeo();
		if (twin instanceof GeoFunctionable) {
			double val =  ((GeoFunctionable) twin).value(x);
			if (!Double.isNaN(val)) {
				return val;
			}
		}
		if (getFunctionVariables().length == 1) {
			ExpressionNode expressionNode =
					new ExpressionNode(kernel, this, Operation.FUNCTION, new MyDouble(kernel, x))
							.traverse(new FunctionExpander()).wrap();
			Command numeric = new Command(kernel, "Numeric", false);
			numeric.addArgument(expressionNode);
			String casResult = evaluateGeoGebraCAS(numeric, constant);
			ExpressionValue casOutput = parseOutputString(casResult);
			return casOutput.evaluateDouble();
		}
		return Double.NaN;
	}

	@Override
	public int getTableColumn() {
		return this.tableColumn;
	}

	@Override
	public void setTableColumn(int column) {
		this.tableColumn = column;
	}

	@Override
	public void setPointsVisible(boolean pointsVisible) {
		this.pointsVisible = pointsVisible;
	}

	@Override
	public boolean isRealValuedFunction() {
		GeoElementND twin = getTwinGeo();
		return twin != null && twin.isRealValuedFunction();
	}

	@Override
	public boolean isPointsVisible() {
		return pointsVisible;
	}

	@Override
	public Function getFunctionForRoot() {
		return getFunction();
	}

	@Override
	public boolean isPolynomialFunction(boolean forRoot) {
		GeoElementND twin = getTwinGeo();
		if (twin instanceof GeoFunctionable) {
			return ((GeoFunctionable) twin).isPolynomialFunction(forRoot);
		}
		return false;
	}

	@Override
	public boolean hasTableOfValues() {
		GeoElementND twin = getTwinGeo();
		return twin != null && twin.hasTableOfValues();
	}

	@Override
	public DescriptionMode getDescriptionMode() {
		GeoElementND twinGeo = getTwinGeo();
		boolean symbolicMode = isSymbolicMode();
		setSymbolicMode(true, false);

		String def = getDefinition(StringTemplate.defaultTemplate);
		String val = getValueForInputBar();
		String twin = twinGeo != null
				? twinGeo.toValueString(StringTemplate.algebraTemplate) : null;

		setSymbolicMode(symbolicMode, false);
		if (def.equals(val) && (twin == null || twin.equals(val))) {
			return DescriptionMode.VALUE;
		} else {
			return DescriptionMode.DEFINITION_VALUE;
		}
	}

	@Override
	public void initSymbolicMode() {
		setSymbolicMode(true, false);
	}

	@Override
	public void setSymbolicMode(boolean mode, boolean updateParent) {
		this.symbolicMode = mode;
	}

	@Override
	public boolean isSymbolicMode() {
		return symbolicMode;
	}

	@Override
	public boolean supportsEngineeringNotation() {
		return false;
	}

	@Override
	public void setEngineeringNotationMode(boolean mode) {
		// Not needed
	}

	@Override
	public boolean isEngineeringNotationMode() {
		return false;
	}

	@Override
	public void setPointSize(int pointSize) {
		this.pointSize = pointSize;
	}

	@Override
	public int getPointSize() {
		return pointSize;
	}

	@Override
	public void setPointStyle(int pointStyle) {
		this.pointStyle = pointStyle;
	}

	@Override
	public int getPointStyle() {
		return pointStyle;
	}

	@Override
	public boolean showPointProperties() {
		getTwinGeo();
		return twinGeo instanceof PointProperties
				&& ((PointProperties) twinGeo).showPointProperties();
	}

	@Override
	public boolean showLineProperties() {
		getTwinGeo();
		return twinGeo != null && twinGeo.showLineProperties();
	}

	@Override
	public void update(boolean drag) {
		asFunction = null;
		if (twinGeo != null) {
			twinGeo.setVisualStyle(this);
		}
		maybeRecomputeNumericValue();
		super.update(drag);
	}

	@Override
	public void updateVisualStyle(GProperty property) {
		if (twinGeo != null) {
			twinGeo.setVisualStyle(this);
		}
		super.updateVisualStyle(property);
	}

	@Override
	public void getXMLtags(StringBuilder builder) {
		super.getXMLtags(builder);
		getFVarsXML(builder);
	}

	@Override
	protected void getStyleXML(StringBuilder builder) {
		super.getStyleXML(builder);
		getLineStyleXML(builder);
		XMLBuilder.appendPointProperties(builder, this);
		XMLBuilder.appendSymbolicMode(builder, this, true);
	}

	private void getFVarsXML(StringBuilder sb) {
		if (fVars.isEmpty()) {
			return;
		}
		String prefix = "";
		sb.append("\t<variables val=\"");
		for (FunctionVariable variable : fVars) {
			sb.append(prefix);
			StringUtil.encodeXML(sb, variable.getSetVarString());
			prefix = ",";
		}
		sb.append("\"/>\n");
	}

	@Override
	public boolean hasLineOpacity() {
		getTwinGeo();
		return twinGeo != null && twinGeo.hasLineOpacity();
	}

	@Override
	public boolean evaluatesToList() {
		return value != null && value.evaluatesToList();
	}

	@Override
	public int getListDepth() {
		return value != null ? value.getListDepth() : 0;
	}

	@Override
	public boolean evaluatesTo3DVector() {
		return value != null && value.evaluatesTo3DVector();
	}

	@Override
	public boolean evaluatesToNDVector() {
		return value != null && value.evaluatesToNDVector();
	}

	@Override
	public boolean evaluatesToNonComplex2DVector() {
		return value != null && value.evaluatesToNonComplex2DVector();
	}

	@Override
	public boolean evaluatesToText() {
		return value != null && value.evaluatesToText();
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		return value != null && value.evaluatesToVectorNotPoint();
	}

	@Override
	public boolean evaluatesToNumber(boolean def) {
		return value != null && value.evaluatesToNumber(def);
	}

	@Override
	public double evaluateDouble() {
		return value != null ? value.evaluateDouble() : Double.NaN;
	}

	@Override
	public @CheckForNull GeoElementND unwrapSymbolic() {
		return getTwinGeo();
	}

	@Override
	public boolean isDrawable() {
		return twinGeo != null ? twinGeo.isDrawable() : super.isDrawable();
	}

	@Override
	public boolean isMatrix() {
		return twinGeo != null ? twinGeo.isMatrix() : hasMatrixValue();
	}

	private boolean hasMatrixValue() {
		ExpressionValue expr = value == null ? getDefinition() : value;
		if (expr == null) {
			return false;
		} else {
			ExpressionValue unwrapped = expr.unwrap();
			return unwrapped instanceof MyList && ((MyList) unwrapped).isMatrix();
		}
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		if (symbolicMode) {
			return symbolic ? getDefinition(tpl) : toValueString(tpl);
		} else if (twinGeo != null) {
			return twinGeo.toLaTeXString(symbolic, tpl);
		} else {
			return getDefinition(tpl);
		}
	}

	@Override
	public boolean isGeoVector() {
		return twinGeo != null
				? twinGeo.isGeoVector()
				: getDefinition() != null && getDefinition().unwrap() instanceof MyVecNDNode;
	}

	@Override
	public ArbitraryConstantRegistry getArbitraryConstant() {
		if (constant == null) {
			constant = new ArbitraryConstantRegistry(this);
		}
		return constant;
	}

	@Override
	public void setArbitraryConstant(ArbitraryConstantRegistry constant) {
		this.constant = constant;
	}

	@Override
	public boolean isFixable() {
		return false;
	}

	@Override
	public boolean euclidianViewUpdate() {
		isTwinUpToDate = false;
		return true;
	}

	@Override
	public void doRemove() {
		super.doRemove();
		cons.unregisterEuclidianViewCE(this);
	}

	@Override
	protected void getDefinitionXML(StringBuilder sb) {
		ExpressionValue unwrapped = getDefinition().unwrap();
		if (label != null && unwrapped instanceof Equation) {
			StringBuilder builder = new StringBuilder();
			super.getDefinitionXML(builder);
			if (builder.toString().contains("=")) {
				sb.append(label);
				sb.append(": ");
			}
		} else if (label != null && unwrapped instanceof Function) {
			sb.append(label);
			sb.append("(");
			sb.append(((Function) unwrapped).getFunctionVariable());
			sb.append(") = ");
		}
		super.getDefinitionXML(sb);
	}

	/**
	 * @param value value
	 * @return True iff the value can be unwrapped to a ListValue,
	 *     using both unwrap and unwrapSymbolic
	 */
	public static boolean isWrappedList(ExpressionValue value) {
		if (value == null) {
			return false;
		}
		ExpressionValue unwrapped = value.unwrap();
		if (unwrapped instanceof GeoSymbolic) {
			unwrapped = ((GeoSymbolic) unwrapped).unwrapSymbolic();
		}
		return unwrapped instanceof ListValue;
	}

	private boolean isCasValueDefined() {
		return !value.any(Inspecting::isUndefined);
	}

	@Override
	public String getFormulaString(StringTemplate tpl,
			boolean substituteNumbers) {
		if (substituteNumbers && tpl.isLatex()) {
			if (value != null && value.wrap().isTopLevelCommand("If")
					&& !fVars.isEmpty()) {
				FunctionVariable fv = fVars.get(0);
				ArrayList<ExpressionNode> cases = new ArrayList<>();
				ArrayList<Bounds> conditions = new ArrayList<>();
				ExpressionNode[] arguments = ((Command) value.unwrap()).getArguments();
				boolean complete = Bounds.collectFromCommand(kernel,
						fv, arguments, cases, conditions);
				return getConditionalSerializer().appendConditionalLaTeX(cases, conditions,
						complete, true, tpl);
			}
		}
		return super.getFormulaString(tpl, substituteNumbers);
	}

	@Override
	protected void appendObjectColorXML(StringBuilder sb) {
		if (isDefaultGeo() || isColorSet()) {
			super.appendObjectColorXML(sb);
		}
	}

	private ConditionalSerializer getConditionalSerializer() {
		if (conditionalSerializer == null) {
			conditionalSerializer = new ConditionalSerializer(kernel, this);
		}
		return conditionalSerializer;
	}

	public void setExcludedEquation(ExpressionNode excludedEquation) {
		this.excludedEquation = excludedEquation;
	}

	public ExpressionNode getExcludedEquation() {
		return this.excludedEquation;
	}

	@Override
	public void setZero() {
		setValue(new ExpressionNode(kernel, new MyDouble(kernel, 0.0)));
	}
}
