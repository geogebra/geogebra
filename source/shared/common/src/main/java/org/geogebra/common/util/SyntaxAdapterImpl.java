package org.geogebra.common.util;

import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictable;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;

/**
 * Convert expressions from Presentation MathML / LaTeX to simple ggb syntax
 * Compared to parent class, this adds awareness of existing GeoGebra objects
 * and built-in functions.
 */
public class SyntaxAdapterImpl extends AbstractSyntaxAdapter implements ExamRestrictable {

	private final Kernel kernel;
	private boolean supportsMixedNumbers = true;

	/**
	 * @param kernel
	 *            Kernel
	 */
	public SyntaxAdapterImpl(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	protected boolean mightBeLaTeXSyntax(String expression) {
		try {
			kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(expression);
			String[] parts = expression.split("\\\\");
			// a\b is set difference: allow it
			for (int i = 1; i < parts.length; i++) {
				String command = parts[i].contains(" ")
						? parts[i].substring(0, parts[i].indexOf(' ')) : parts[i];
				if (kernel.lookupLabel(command) == null) {
					return true;
				}
			}
			// parses OK as GGB, not LaTeX
			return false;
		} catch (Throwable e) {
			// fall through
		}

		return super.mightBeLaTeXSyntax(expression);
	}

	@Override
	public String convertLaTeXtoGGB(String latexExpression) {
		kernel.getApplication().getDrawEquation()
				.checkFirstCall();
		return super.convertLaTeXtoGGB(latexExpression);
	}

	@Override
	public boolean isFunction(String casName) {
		Operation operation = kernel.getApplication().getParserFunctions(true).get(casName, 1);
		return operation != null && casName.length() > 1;
	}

	@Override
	public boolean supportsMixedNumbers() {
		return supportsMixedNumbers;
	}

	protected Kernel getKernel() {
		return kernel;
	}

	@Override
	public void applyRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType) {
		if (featureRestrictions.contains(ExamFeatureRestriction.DISABLE_MIXED_NUMBERS)) {
			supportsMixedNumbers = false;
		}
	}

	@Override
	public void removeRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType) {
		supportsMixedNumbers = true;
	}
}
