package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

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
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.commands.SymbolicProcessor;
import org.geogebra.common.kernel.geos.properties.DelegateProperties;
import org.geogebra.common.kernel.geos.properties.EquationType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.GeoClass;
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
	private ArrayList<FunctionVariable> fVars = new ArrayList<>();
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

	@Nullable
	private GeoElement twinGeo;

	@Nullable
	private ExpressionValue numericValue;
	private int numericPrintFigures;
	private int numericPrintDecimals;
	private ConditionalSerializer conditionalSerializer;

	/**
	 * @param c construction
	 */
	public GeoSymbolic(Construction c) {
		super(c);
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
	public ValueType getValueType() {
		if (value != null) {
			return value.getValueType();
		}
		return ValueType.UNKNOWN;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SYMBOLIC;
	}

	@Override
	public GeoElement copy() {
		GeoSymbolic copy = new GeoSymbolic(cons);
		copy.set(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		reuseDefinition(geo);
		fVars.clear();
		if (geo instanceof GeoSymbolic) {
			GeoSymbolic symbolic = (GeoSymbolic) geo;
			fVars.addAll(symbolic.fVars);
			value = symbolic.getValue();
			casOutputString = symbolic.casOutputString;
			numericValue = symbolic.numericValue;
			numericPrintFigures = symbolic.numericPrintFigures;
			numericPrintDecimals = symbolic.numericPrintDecimals;
			isTwinUpToDate = false;
		}
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
		if ((symbolicMode || tpl.getStringType().isGiac()) || !hasNumericValue()) {
			if (value != null) {
				return value.toValueString(tpl);
			}
			return getDefinition().toValueString(tpl);
		} else {
			return getNumericValueString(tpl);
		}
	}

	private boolean hasNumericValue() {
		return numericValue != null || getTwinGeo() != null;
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
				ret = (ExpressionValue) (eq.getRHS().getLeft());
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
		boolean argsDefined = casInput.inspect(new Inspecting() {
			@Override
			public boolean check(ExpressionValue v) {
				return !v.toValueString(StringTemplate.defaultTemplate).contains("?");
			}
		});
		return argsDefined;
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

	public void setWrapInNumeric(boolean input) {
		wrapInNumeric = input;
	}

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
		if (kernel.getGeoGebraCAS().getCurrentCAS().isLoaded()) {
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
			return Collections.emptyList();
		}
	}

	protected List<FunctionVariable> collectVariables() {
		FunctionVarCollector functionVarCollector = FunctionVarCollector
				.getCollector();
		getDefinition().traverse(functionVarCollector);
		List<FunctionVariable> vars = Arrays.asList(functionVarCollector.buildVariables(kernel));
		if (vars.size() > 0) {
			return vars;
		} else {
			try {
				getNodeFromOutput().traverse(functionVarCollector);
				return Arrays.asList(functionVarCollector.buildVariables(kernel));
			} catch (ParseException e) {
				return vars;
			}
		}
	}

	private static boolean shouldShowFunctionVariablesInOutputFor(Command command) {
		return !Commands.Solutions.getCommand().equals(command.getName()); // APPS-1821, APPS-2190
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
	 * @return geo for drawing
	 */
	public GeoElementND getTwinGeo() {
		if (isTwinUpToDate) {
			return twinGeo;
		}
		GeoElementND newTwin = createTwinGeo();

		if (newTwin instanceof EquationValue) {
			((EquationValue) newTwin).setToUser();
		}

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
		return constant != null && constant.getTotalNumberOfConsts() > 0;
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
					GeoSymbolic symbolic = (GeoSymbolic) ev;
					ExpressionValue value = symbolic.getValue().deepCopy(kernel);
					return value.traverse(this);
				} else if (ev instanceof GeoDummyVariable) {
					GeoDummyVariable variable = (GeoDummyVariable) ev;
					return new Variable(variable.getKernel(), variable.getVarName());
				} else if (ev instanceof Command) {
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
		EvalInfo twinInfo = new EvalInfo(false, true).withAssignments(false);
		GeoElement[] elements = algebraProcessor.processValidExpression(expressionNode, twinInfo);
		GeoElement result = elements.length > 1 || needsListWrapping(elements[0])
				? toGeoList(elements) : elements[0];
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
			return ((GeoFunctionable) twin).value(x);
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
	public GeoElementND unwrapSymbolic() {
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
		return unwrapped instanceof ListValue || (unwrapped instanceof GeoSymbolic
				&& ((GeoSymbolic) unwrapped).unwrapSymbolic().isGeoList()) ;
	}

	private boolean isCasValueDefined() {
		return !value.inspect(Inspecting.isUndefinedInspector);
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
}
