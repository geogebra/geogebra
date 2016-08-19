package org.geogebra.common.io.latex;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.Serializer;

public class GeoGebraSerializer implements Serializer {

    @Override
    public String serialize(MathFormula formula) {
        MathSequence sequence = formula.getRootComponent();
        StringBuilder stringBuilder = new StringBuilder();
        serialize(sequence, stringBuilder);
        return stringBuilder.toString();
    }

	private void serialize(MathComponent mathComponent,
			StringBuilder stringBuilder) {
        if (mathComponent instanceof MathCharacter) {
            serialize((MathCharacter) mathComponent, stringBuilder);
        } else if (mathComponent instanceof MathFunction) {
            serialize((MathFunction) mathComponent, stringBuilder);
        } else if (mathComponent instanceof MathArray) {
            serialize((MathArray) mathComponent, stringBuilder);
        } else if (mathComponent instanceof MathSequence) {
            serialize((MathSequence) mathComponent, stringBuilder);
        }
    }

	public static String serialize(MathComponent c) {
		GeoGebraSerializer ser = new GeoGebraSerializer();
		StringBuilder sb = new StringBuilder();
		ser.serialize(c, sb);
		return sb.toString();
	}

    private void serialize(MathCharacter mathCharacter, StringBuilder stringBuilder) {
        stringBuilder.append(mathCharacter.getUnicode());
    }

    private void serialize(MathFunction mathFunction, StringBuilder stringBuilder) {
        String mathFunctionName = mathFunction.getName();
        if ("^".equals(mathFunctionName)) {
            stringBuilder.append(mathFunctionName + '(');
            serialize(mathFunction.getArgument(0), stringBuilder);
            stringBuilder.append(')');
        } else if ("_".equals(mathFunction.getName())) {
            stringBuilder.append(mathFunctionName + '{');
            serialize(mathFunction.getArgument(0), stringBuilder);
            stringBuilder.append('}');
        } else if ("frac".equals(mathFunctionName)) {
            stringBuilder.append('(');
            serialize(mathFunction.getArgument(0), stringBuilder);
            stringBuilder.append(")/(");
            serialize(mathFunction.getArgument(1), stringBuilder);
            stringBuilder.append(")");
        } else if ("sqrt".equals(mathFunctionName)) {
            maybeInsertTimes(mathFunction, stringBuilder);
            stringBuilder.append("sqrt(");
            serialize(mathFunction.getArgument(0), stringBuilder);
            stringBuilder.append(')');
        } else if ("nroot".equals(mathFunctionName)) {
            maybeInsertTimes(mathFunction, stringBuilder);
            stringBuilder.append("nroot(");
            serialize(mathFunction.getArgument(1), stringBuilder);
            stringBuilder.append(",");
            serialize(mathFunction.getArgument(0), stringBuilder);
            stringBuilder.append(')');
        }  // Strict control of available functions is needed, so that SUM/ and Prod doesn't work
        /*else if ("sum".equals(function.getName())
                || "prod".equals(function.getName())) {
            buffer.append(function.getTexName());
            buffer.append("_{");
            serialize(function.getArgument(0), buffer);
            buffer.append('=');
            serialize(function.getArgument(1), buffer);
            buffer.append("}^");
            serialize(function.getArgument(2), buffer);
            boolean addBraces = currentBraces
                    || (function.getArgument(3).hasOperator());
            if (addBraces) {
                buffer.append('(');
            }
            serialize(function.getArgument(3), buffer);
            if (addBraces) {
                buffer.append(')');
            }

        else if ("nsum".equals(function.getName())
                || "nprod".equals(function.getName())) {
            buffer.append(function.getTexName());
            buffer.append("_{");
            serialize(function.getArgument(0), buffer);
            buffer.append('=');
            serialize(function.getArgument(1), buffer);
            buffer.append('}');
            boolean addBraces = currentBraces
                    || (function.getArgument(2).hasOperator());
            if (addBraces) {
                buffer.append('(');
            }
            serialize(function.getArgument(2), buffer);
            if (addBraces) {
                buffer.append(')');
            }

        } else if ("int".equals(function.getName())) {
            buffer.append(function.getTexName());
            buffer.append('_');
            serialize(function.getArgument(0), buffer);
            buffer.append('^');
            serialize(function.getArgument(1), buffer);
            buffer.append('{');
            boolean addBraces = currentBraces;
            if (addBraces) {
                buffer.append('(');
            }
            serialize(function.getArgument(2), buffer);
            // jmathtex v0.7: incompatibility
            buffer.append(" " + (jmathtex ? "\\nbsp" : "\\ ") + " d");
            serialize(function.getArgument(3), buffer);
            if (addBraces) {
                buffer.append(')');
            }
            buffer.append('}');

        } else if ("nint".equals(function.getName())) {
            buffer.append(function.getTexName());
            buffer.append((jmathtex ? "_{\\nbsp}" : "") + "{");
            boolean addBraces = currentBraces;
            if (addBraces) {
                buffer.append('(');
            }
            serialize(function.getArgument(0), buffer);
            // jmathtex v0.7: incompatibility
            buffer.append(" " + (jmathtex ? "\\nbsp" : "\\ ") + " d");
            serialize(function.getArgument(1), buffer);
            if (addBraces) {
                buffer.append(')');
            }
            buffer.append('}');

        } else if ("lim".equals(function.getName())) {
            // lim not implemented in jmathtex
            if (!jmathtex) {
                buffer.append("\\");
            }
            buffer.append(function.getTexName());
            buffer.append("_{");
            serialize(function.getArgument(0), buffer);
            buffer.append(" \\rightarrow ");
            serialize(function.getArgument(1), buffer);
            // jmathtex v0.7: incompatibility
            buffer.append("} " + (jmathtex ? "\\nbsp" : "\\ ") + " {");
            boolean addBraces = currentBraces
                    || (function.getArgument(2).hasOperator() && function
                    .getParent().hasOperator());
            if (addBraces) {
                buffer.append('(');
            }
            serialize(function.getArgument(2), buffer);
            if (addBraces) {
                buffer.append(')');
            }
            buffer.append('}');

        } */
        else if ("factorial".equals(mathFunctionName)) {
            MathSequence argument = mathFunction.getArgument(0);
            boolean addBraces = argument.hasOperator();
            if (addBraces) {
                stringBuilder.append('(');
            }
            serialize(argument, stringBuilder);
            if (addBraces) {
                stringBuilder.append(')');
            }
            stringBuilder.append("!");
        } else {
            // some general function
            maybeInsertTimes(mathFunction, stringBuilder);
            stringBuilder.append(mathFunctionName);
            stringBuilder.append('(');
            for (int i = 0; i < mathFunction.size(); i++) {
                serialize(mathFunction.getArgument(i), stringBuilder);
                stringBuilder.append(',');
            }
            if (mathFunction.size() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            stringBuilder.append(')');
        }
    }

    private void maybeInsertTimes(MathFunction mathFunction, StringBuilder stringBuilder) {
        MathSequence mathSequence = mathFunction.getParent();
        if (mathSequence != null && mathFunction.getParentIndex() > 0) {
            MathComponent mathComponent = mathSequence.getArgument(mathFunction.getParentIndex() - 1);
            if (mathComponent instanceof MathCharacter) {
                MathCharacter mathCharacter = (MathCharacter) mathComponent;
                if (mathCharacter.isCharacter()) {
                    stringBuilder.append("*");
                }
            }
        }
    }

    private void serialize(MathArray mathArray, StringBuilder stringBuilder) {
        String open = mathArray.getOpen().getCasName();
        String close = mathArray.getClose().getCasName();
        String field = mathArray.getField().getCasName();
        String row = mathArray.getRow().getCasName();
        if (mathArray.isMatrix()) {
            stringBuilder.append(open);
        }
        for (int i = 0; i < mathArray.rows(); i++) {
            stringBuilder.append(open);
            for (int j = 0; j < mathArray.columns(); j++) {
                serialize(mathArray.getArgument(i, j), stringBuilder);
                stringBuilder.append(field);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - field.length());
            stringBuilder.append(close);
            stringBuilder.append(row);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - row.length());
        if (mathArray.isMatrix()) {
            stringBuilder.append(close);
        }
    }

    private void serialize(MathSequence mathSequence, StringBuilder stringBuilder) {
		if (mathSequence == null) {
			return;
		}
        for (int i = 0; i < mathSequence.size(); i++) {
            serialize(mathSequence.getArgument(i), stringBuilder);
        }
    }
}
