package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.controller.ExpressionReader;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * Utility class for screen reader serialization.
 *
 */
public class ScreenReaderSerializer {

	/**
	 * @param er
	 *            expression reader
	 * @param expr
	 *            part of editor content
	 * @return expression description
	 */
	public static String fullDescription(ExpressionReader er,
			MathComponent expr) {
		String ggb = GeoGebraSerializer.serialize(expr);
		try {
			return er.mathExpression(ggb);
		} catch (Exception e) {
			if (expr instanceof MathSequence) {
				return describeContainer(expr, " ", er);
			}
			if (expr instanceof MathArray) {
				String content = describeContainer(expr, " ", er);
				return er.inParentheses(content);
			}
			if (expr instanceof MathFunction) {
				switch (((MathFunction) expr).getName()) {
				case FRAC:
					return er.fraction(
							fullDescription(er,
									((MathFunction) expr).getArgument(0)),
							fullDescription(er,
									((MathFunction) expr).getArgument(1)));
				case SQRT:
					return er.squareRoot(fullDescription(er,
							((MathFunction) expr).getArgument(0)));
				case CBRT:
					return er.nroot(fullDescription(er,
							((MathFunction) expr).getArgument(0)), "3");
				case SUPERSCRIPT:
					return er.power("", fullDescription(er,
							((MathFunction) expr).getArgument(0)));
				case NROOT:
					return er.nroot(
							fullDescription(er,
									((MathFunction) expr).getArgument(1)),
							fullDescription(er,
									((MathFunction) expr).getArgument(0)));
				default:
					break;
				}
			}
			return ggb.replace("+", "plus").replace("-", "minus").replace("*",
					"times");
		}
	}

	private static String describeContainer(MathComponent expr,
			String separator, ExpressionReader er) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ((MathContainer) expr).size(); i++) {
			sb.append(
					fullDescription(er, ((MathContainer) expr).getArgument(i)));
			sb.append(separator);
		}
		return sb.toString();
	}

}
