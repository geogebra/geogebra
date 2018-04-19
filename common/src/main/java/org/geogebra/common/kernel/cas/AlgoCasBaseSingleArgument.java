/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCasBase;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

/**
 * Process a function using single argument command
 * 
 * @author Markus Hohenwarter
 */
public class AlgoCasBaseSingleArgument extends AlgoCasBase implements HasSteps {
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param cmd
	 *            command
	 * @param info
	 *            evaluation flags
	 */
	public AlgoCasBaseSingleArgument(Construction cons, String label, CasEvaluableFunction f, 
			Commands cmd, EvalInfo info) {
		super(cons, label, f, cmd, info);
	}

	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		// factor value form of f
		Commands cmd = this.getClassName();
		g.setUsingCasCommand(cmd.name() + "[%]", f, false, arbconst);
		if (f.isDefined() && !g.isDefined()) {
			g.toGeoElement().set(f.toGeoElement());
		}
	}

	@Override
	public void getSteps(StepGuiBuilder builder) {
		App app = kernel.getApplication();

		SolutionBuilder sb = new SolutionBuilder();
		String definitionNoLabel = f.toGeoElement()
				.getDefinitionNoLabel(StringTemplate.defaultTemplate);
		StepExpression sn = (StepExpression) StepNode.getStepTree(
				definitionNoLabel, app.getKernel().getParser());
		switch (getClassName()) {
		case Expand:
			sn.expand(sb);
			break;
		case Factor:
			sn.factor(sb);
			break;
		case Simplify:
			sn.regroup(sb);
			break;
		default:
			Log.warn("Not supported for steps: " + getClassName());
		}

		SolutionStep steps = sb.getSteps();
		steps.getListOfSteps(builder, app.getLocalization());
	}

	@Override
	public boolean canShowSteps() {
		return getClassName() == Commands.Simplify || getClassName() == Commands.Expand
				|| getClassName() == Commands.Factor;
	}

}
