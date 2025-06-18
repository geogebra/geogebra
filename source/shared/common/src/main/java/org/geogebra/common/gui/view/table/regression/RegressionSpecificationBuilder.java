package org.geogebra.common.gui.view.table.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictable;
import org.geogebra.common.kernel.statistics.Regression;

import com.himamis.retex.editor.share.util.Unicode;

public class RegressionSpecificationBuilder implements ExamRestrictable {

	private final ArrayList<RegressionSpecification> specs = new ArrayList<>();
	private RegressionModelGroup modelGroup = RegressionModelGroup.STANDARD;

	private enum RegressionModelGroup {
		STANDARD, MMS
	}

	/**
	 * @param listSize size of the selected list
	 * @return all available regression types
	 */
	public List<RegressionSpecification> getForListSize(int listSize) {
		if (specs.isEmpty()) {
			if (modelGroup == RegressionModelGroup.MMS) {
				addMMSCustomSpecs();
			} else {
				addStandardSpecs();
			}
		}
		return specs.stream().filter(spec -> spec.getCoeffOrdering().length() <= listSize)
				.collect(Collectors.toList());
	}

	private void addMMSCustomSpecs() {
		specs.add(new CustomRegressionSpecification("a x + b", 1, 0));
		specs.add(new CustomRegressionSpecification("a x", 1));
		specs.add(new CustomRegressionSpecification("a x^2 + b x + c", 2, 1, 0));
		specs.add(new CustomRegressionSpecification("a x^2 + b x", 2, 1));
		specs.add(new CustomRegressionSpecification("a x^2 + c", 2, 0));
		specs.add(new CustomRegressionSpecification("a x^2", 2, 1, 0));
		specs.add(new CustomRegressionSpecification("a * exp(b x) + c",
				CustomRegressionSpecification.Type.EXP_PLUS_CONSTANT));
		specs.add(new CustomRegressionSpecification("a * exp(b x)",
				CustomRegressionSpecification.Type.EXPONENTIAL));
		specs.add(new CustomRegressionSpecification("a / x + b", -1, 0));
		specs.add(new CustomRegressionSpecification("a / x", -1));
		specs.add(new CustomRegressionSpecification("a / x^2 + b", -2, 0));
		specs.add(new CustomRegressionSpecification("a / x^2", -2));
		specs.add(new CustomRegressionSpecification("a " + Unicode.CENTER_DOT
				+ Unicode.SQUARE_ROOT + "x", .5));
	}

	private void addStandardSpecs() {
		addSpec(Regression.LINEAR, 1, null, "ba");
		addSpec(Regression.LOG, 0, "y = a + b\\cdot \\log(x)", "ab");
		addSpec(Regression.POW, 0, "y = a \\cdot x^b", "ab");
		addSpec(Regression.POLY, 2, null, "cba");
		addSpec(Regression.POLY, 3, null, "dcba");
		addSpec(Regression.POLY, 4, null, "edcba");
		addSpec(Regression.EXP, 0, "y = a \\cdot e^{b\\ x}", "ab");
		addSpec(Regression.GROWTH, 0, "y = a \\cdot b^x", "ab");
		addSpec(Regression.SIN, 0, "y = a \\cdot \\sin(b\\ x + c) + d", "dabc");
		addSpec(Regression.LOGISTIC, 0, "y = \\frac{a}{1 + b\\cdot e^{-c\\ x}}", "bca");
	}

	private void setModelGroup(RegressionModelGroup modelGroup) {
		this.modelGroup = modelGroup;
		specs.clear();
	}

	private void addSpec(Regression regression, int polynomialDegree, String formula,
			String coefficientOrdering) {
		specs.add(new StandardRegressionSpecification(regression, polynomialDegree, formula,
				coefficientOrdering));
	}

	@Override
	public void applyRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType) {
		if (featureRestrictions.contains(
				ExamFeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS)) {
			setModelGroup(RegressionModelGroup.MMS);
		}
	}

	@Override
	public void removeRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType) {
		setModelGroup(RegressionModelGroup.STANDARD);
	}
}
