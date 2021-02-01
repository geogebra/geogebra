package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EuclidianViewCE;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionExpander;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVarCollector;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.properties.DelegateProperties;
import org.geogebra.common.kernel.geos.properties.EquationType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * Symbolic geo for CAS computations in AV
 * @author Zbynek
 */
public class GeoSymbolic extends GeoElement
		implements GeoSymbolicI, VarString, GeoEvaluatable, GeoFunctionable, DelegateProperties,
		HasArbitraryConstant, EuclidianViewCE {
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
	private MyArbitraryConstant constant;

	@Nullable
	private GeoElement twinGeo;

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

	/**
	 * @param c construction
	 */
	public GeoSymbolic(Construction c) {
		super(c);
		symbolicMode = true;
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
		GeoElementND twin = getTwinGeo();
		if (symbolicMode || twin == null) {
			if (value != null) {
				return value.toValueString(tpl);
			}
			return getDefinition().toValueString(tpl);
		} else {
			return twin.toValueString(tpl);
		}
	}

	@Override
	protected boolean showInEuclidianView() {
		GeoElementND twin = getTwinGeo();
		return isEuclidianShowable && twin != null && twin.isEuclidianShowable();
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

	@Override
	public void computeOutput() {
		ExpressionValue casInputArg = getDefinition().deepCopy(kernel)
				.traverse(FunctionExpander.getCollector());
		Command casInput = getCasInput(casInputArg);

		MyArbitraryConstant constant = getArbitraryConstant();
		constant.setSymbolic(!shouldBeEuclidianVisible(casInput));

		String s = evaluateGeoGebraCAS(casInput, constant);

		if (Commands.Solve.name().equals(casInput.getName()) && GeoFunction.isUndefined(s)) {
			getDefinition().getTopLevelCommand().setName(Commands.NSolve.name());
			casInput = getCasInput(getDefinition().deepCopy(kernel)
					.traverse(FunctionExpander.getCollector()));
			s = evaluateGeoGebraCAS(casInput, constant);
		}

		this.casOutputString = s;
		ExpressionValue casOutput = parseOutputString(s);

		computeFunctionVariables();
		setValue(casOutput);

		isTwinUpToDate = false;
		isEuclidianShowable = shouldBeEuclidianVisible(casInput);
	}

	private Command getCasInput(ExpressionValue casInputArg) {
		Command casInput;
		if (casInputArg.unwrap() instanceof  Command) {
			casInput = (Command) casInputArg.unwrap();
		} else {
			casInput = new Command(kernel, "Evaluate", false);
			casInput.addArgument(casInputArg.wrap());
		}
		return casInput;
	}

	private String evaluateGeoGebraCAS(Command command, MyArbitraryConstant constant) {
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

	private void computeFunctionVariables() {
		if (getDefinition() == null || !fVars.isEmpty()) {
			return;
		}
		ExpressionValue def = getDefinition().unwrap();
		if (def instanceof FunctionNVar) {
			setVariables(((FunctionNVar) def).getFunctionVariables());
		} else if (def instanceof Command || getDefinition().containsFreeFunctionVariable(null)) {
			FunctionVarCollector functionVarCollector = FunctionVarCollector
					.getCollector();
			getDefinition().traverse(functionVarCollector);
			fVars.addAll(
					Arrays.asList(functionVarCollector.buildVariables(kernel)));
		}
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

	private StringBuilder appendVarString(StringBuilder sb,
			final StringTemplate tpl) {
		for (int i = 0; i < fVars.size() - 1; i++) {
			sb.append(fVars.get(i).toString(tpl));
			sb.append(", ");
		}
		sb.append(fVars.get(fVars.size() - 1).toString(tpl));
		return sb;
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	/**
	 * @param functionVariables function variables
	 */
	public void setVariables(FunctionVariable[] functionVariables) {
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
		return kernel.getParser().parseGeoGebraExpression(casOutputString).wrap();
	}

	private ExpressionNode getNodeFromInput() {
		ExpressionNode node = getDefinition().deepCopy(kernel)
				.traverse(createPrepareDefinition()).wrap();
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
				}
				return ev;
			}
		};
	}

	private GeoElement process(ExpressionNode expressionNode) throws Exception {
		expressionNode.traverse(Traversing.GgbVectRemover.getInstance());
		AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
		if (algebraProcessor.hasVectorLabel(this)) {
			expressionNode.setForceVector();
		}
		GeoElement[] elements = algebraProcessor.processValidExpression(expressionNode);
		GeoElement result = elements.length > 1 ? toGeoList(elements) : elements[0];
		AlgoElement parentAlgo = elements[0].getParentAlgorithm();
		if (cons.isRegisteredEuclidianViewCE(parentAlgo)) {
			cons.unregisterEuclidianViewCE(parentAlgo);
			cons.registerEuclidianViewCE(this);
		} else {
			cons.unregisterEuclidianViewCE(this);
		}
		return result;
	}

	private GeoElement toGeoList(GeoElement[] elements) {
		GeoList geoList = new GeoList(cons);
		for (GeoElement element : elements) {
			geoList.add(element);
		}
		return geoList;
	}

	@Override
	final public void setVisualStyle(final GeoElement geo, boolean copyAux) {
		super.setVisualStyle(geo, copyAux);
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
				? twinGeo.toValueString(StringTemplate.defaultTemplate) : null;

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
		if (twinGeo != null) {
			twinGeo.setVisualStyle(this);
		}
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
			sb.append(StringUtil.encodeXML(variable.getSetVarString()));
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
		return twinGeo != null ? twinGeo.isMatrix() : hasMatrixDefinition();
	}

	private boolean hasMatrixDefinition() {
		ExpressionNode definition = getDefinition();
		if (definition == null) {
			return false;
		} else {
			ExpressionValue unwrapped = getDefinition().unwrap();
			return unwrapped instanceof MyList && ((MyList) unwrapped).isMatrix();
		}
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return twinGeo != null
				? twinGeo.toLaTeXString(symbolic || isSymbolicMode(), tpl)
				: symbolic ? getDefinition(tpl) : toValueString(tpl);
	}

	@Override
	public boolean isGeoVector() {
		return twinGeo != null
				? twinGeo.isGeoVector()
				: getDefinition() != null && getDefinition().unwrap() instanceof MyVecNDNode;
	}

	@Override
	public MyArbitraryConstant getArbitraryConstant() {
		if (constant == null) {
			constant = new MyArbitraryConstant(this);
		}
		return constant;
	}

	@Override
	public void setArbitraryConstant(MyArbitraryConstant constant) {
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
}
