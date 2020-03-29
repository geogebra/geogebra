package org.geogebra.common.kernel.geos.symbolic;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoSymbolic;

import javax.annotation.Nullable;

/**
 * Creates and handles the GeoElement twin of the GeoSymbolic.
 */
public class Twin {

	private GeoSymbolic symbolic;
	private Construction construction;
	private Kernel kernel;
	private boolean isUpToDate;

	@Nullable
	private GeoElement twin;

	/**
	 * @param symbolic symbolic
	 */
	public Twin(GeoSymbolic symbolic) {
		this.symbolic = symbolic;
		construction = symbolic.getConstruction();
		kernel = symbolic.getKernel();
	}

	@Nullable
	public GeoElement getElement() {
		return twin;
	}

	/**
	 * @return geo for drawing
	 */
	public GeoElement createAndGetElement() {
		if (isUpToDate) {
			return twin;
		}

		GeoElement newTwin = createTwinGeo();

		if (newTwin instanceof EquationValue) {
			((EquationValue) newTwin).setToUser();
		}

		if (newTwin instanceof GeoList) {
			newTwin.setEuclidianVisible(true);
		}

		if (twin != null && newTwin != null) {
			newTwin.setVisualStyle(symbolic);
			updateTwin(newTwin.toGeoElement());
		} else if (newTwin == null) {
			updateTwin(null);
		} else {
			updateTwin(newTwin.toGeoElement());
			symbolic.setVisualStyle(twin);
		}
		isUpToDate = true;

		return twin;
	}

	private GeoElement createTwinGeo() {
		if (symbolic.getDefinition() == null) {
			return null;
		}
		boolean isSuppressLabelsActive = construction.isSuppressLabelsActive();
		ExpressionNode node;
		try {
			construction.setSuppressLabelCreation(true);
			node = symbolic
					.getDefinition()
					.deepCopy(kernel)
					.traverse(createPrepareDefinition())
					.wrap();
			node.setLabel(null);
			return process(node);
		} catch (Throwable exception) {
			try {
				node = symbolic.parseOutput().wrap();
				return process(node);
			} catch (Throwable t) {
				return null;
			}
		} finally {
			construction.setSuppressLabelCreation(isSuppressLabelsActive);
		}
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
		if (algebraProcessor.hasVectorLabel(symbolic)) {
			expressionNode.setForceVector();
		}
		GeoElement[] elements = algebraProcessor.processValidExpression(expressionNode);
		return elements[0];
	}

	private void updateTwin(GeoElement twin) {
		this.twin = twin;
		AlgoElement parentAlgoOfSymbolic = symbolic.getParentAlgorithm();
		if (twin != null && parentAlgoOfSymbolic != null) {
			twin.setParentAlgorithm(parentAlgoOfSymbolic);
		}
	}

	public void setUpToDate(boolean upToDate) {
		isUpToDate = upToDate;
	}
}
