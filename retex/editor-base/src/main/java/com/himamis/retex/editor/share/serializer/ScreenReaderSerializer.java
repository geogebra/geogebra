package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.controller.ExpressionReader;
import com.himamis.retex.editor.share.model.MathComponent;
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
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < ((MathSequence) expr).size(); i++) {
					sb.append(fullDescription(er,
							((MathSequence) expr).getArgument(i)));
					sb.append(" ");
				}
				return sb.toString();
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
				case SUPERSCRIPT:
					return er.power("", fullDescription(er,
							((MathFunction) expr).getArgument(0)));
				default:
					break;
				}
			}
			return ggb.replace("+", "plus");
		}
	}

}
