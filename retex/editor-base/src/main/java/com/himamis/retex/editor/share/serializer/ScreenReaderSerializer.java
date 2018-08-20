package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.controller.ExpressionReader;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public class ScreenReaderSerializer {
	public static String fullDescription(ExpressionReader er,
			MathComponent prev) {
		String ggb = GeoGebraSerializer.serialize(prev);
		try {
			return er.mathExpression(ggb);
		} catch (Exception e) {
			if (prev instanceof MathSequence) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < ((MathSequence) prev).size(); i++) {
					sb.append(fullDescription(er,
							((MathSequence) prev).getArgument(i)));
					sb.append(" ");
				}
				return sb.toString();
			}
			if (prev instanceof MathFunction) {
				switch (((MathFunction) prev).getName()) {
				case FRAC:
					return er.fraction(
							fullDescription(er,
									((MathFunction) prev).getArgument(0)),
							fullDescription(er,
									((MathFunction) prev).getArgument(1)));
				case SQRT:
					return er.squareRoot(fullDescription(er,
							((MathFunction) prev).getArgument(0)));
				case SUPERSCRIPT:
					return er.power("", fullDescription(er,
							((MathFunction) prev).getArgument(0)));
				default:
					break;
				}
			}
			return ggb.replace("+", "plus");
		}
	}
	


}
