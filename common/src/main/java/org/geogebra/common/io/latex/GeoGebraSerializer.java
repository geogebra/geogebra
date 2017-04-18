package org.geogebra.common.io.latex;

import java.util.HashMap;

import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.Serializer;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.NthRoot;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.SymbolAtom;

/**
 * Serializes internal formulas representation into GeoGebra string
 *
 */
public class GeoGebraSerializer implements Serializer {

	private static HashMap<String, String> mappings;

	@Override
	public String serialize(MathFormula formula) {
		MathSequence sequence = formula.getRootComponent();
		StringBuilder stringBuilder = new StringBuilder();
		serialize(sequence, stringBuilder);
		return stringBuilder.toString();
	}

	private static void serialize(MathComponent mathComponent,
			MathSequence parent, int index,
			StringBuilder stringBuilder) {
		if (mathComponent instanceof MathCharacter) {
			serialize((MathCharacter) mathComponent, parent, index,
					stringBuilder);
		} else if (mathComponent instanceof MathFunction) {
			serialize((MathFunction) mathComponent, stringBuilder);
		} else if (mathComponent instanceof MathArray) {
			serialize((MathArray) mathComponent, stringBuilder);
		} else if (mathComponent instanceof MathSequence) {
			serialize((MathSequence) mathComponent, stringBuilder);
		}
	}

	/**
	 * @param c
	 *            math formula fragment
	 * @return string
	 */
	public static String serialize(MathComponent c) {
		StringBuilder sb = new StringBuilder();
		GeoGebraSerializer.serialize(c, null, 0, sb);
		return sb.toString();
	}

	private static void serialize(MathCharacter mathCharacter,
			MathSequence parent, int index,
			StringBuilder stringBuilder) {
		if (mathCharacter.getUnicode() == MathCharacter.ZERO_SPACE) {

			if (parent != null && index + 1 < parent.size()) {
				if (parent.getArgument(index + 1) instanceof MathArray) {
					stringBuilder.append(" ");
				}
			}
			return;
		}
		stringBuilder.append(mathCharacter.getUnicode());
	}

	private static void serialize(MathFunction mathFunction,
			StringBuilder stringBuilder) {
		String mathFunctionName = mathFunction.getName();
		if ("^".equals(mathFunctionName)) {
			stringBuilder.append(mathFunctionName + '(');
			serialize(mathFunction.getArgument(0), stringBuilder);
			stringBuilder.append(')');
		} else if ("_".equals(mathFunction.getName())) {
			stringBuilder.append(mathFunctionName + '{');
			serialize(mathFunction.getArgument(0), stringBuilder);
			// a_{1}sin(x) should be a_{1} sin(x)
			stringBuilder.append("} ");
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
		} // Strict control of available functions is needed, so that SUM/ and
			// Prod doesn't work
		else if ("sum".equals(mathFunctionName)) {
			stringBuilder.append("Sum");
			serializeArgs(mathFunction, stringBuilder,
					new int[] { 3, 0, 1, 2 });
		} else if ("prod".equals(mathFunctionName)) {
			stringBuilder.append("Product");
			serializeArgs(mathFunction, stringBuilder,
					new int[] { 3, 0, 1, 2 });
		} else if ("int".equals(mathFunctionName)) {
			stringBuilder.append("Integral");
			serializeArgs(mathFunction, stringBuilder,
					new int[] { 2, 0, 1 });
		} else if ("lim".equals(mathFunctionName)) {
			stringBuilder.append("Limit");
			serializeArgs(mathFunction, stringBuilder,
					new int[] { 2, 3, 0, 1 });
		}

		/*
		 * else if ("nsum".equals(function.getName()) ||
		 * "nprod".equals(function.getName())) {
		 * buffer.append(function.getTexName()); buffer.append("_{");
		 * serialize(function.getArgument(0), buffer); buffer.append('=');
		 * serialize(function.getArgument(1), buffer); buffer.append('}');
		 * boolean addBraces = currentBraces ||
		 * (function.getArgument(2).hasOperator()); if (addBraces) {
		 * buffer.append('('); } serialize(function.getArgument(2), buffer); if
		 * (addBraces) { buffer.append(')'); }
		 * 
		 * } else if ("int".equals(function.getName())) {
		 * buffer.append(function.getTexName()); buffer.append('_');
		 * serialize(function.getArgument(0), buffer); buffer.append('^');
		 * serialize(function.getArgument(1), buffer); buffer.append('{');
		 * boolean addBraces = currentBraces; if (addBraces) {
		 * buffer.append('('); } serialize(function.getArgument(2), buffer); //
		 * jmathtex v0.7: incompatibility buffer.append(" " + (jmathtex ?
		 * "\\nbsp" : "\\ ") + " d"); serialize(function.getArgument(3),
		 * buffer); if (addBraces) { buffer.append(')'); } buffer.append('}');
		 * 
		 * } else if ("nint".equals(function.getName())) {
		 * buffer.append(function.getTexName()); buffer.append((jmathtex ?
		 * "_{\\nbsp}" : "") + "{"); boolean addBraces = currentBraces; if
		 * (addBraces) { buffer.append('('); }
		 * serialize(function.getArgument(0), buffer); // jmathtex v0.7:
		 * incompatibility buffer.append(" " + (jmathtex ? "\\nbsp" : "\\ ") +
		 * " d"); serialize(function.getArgument(1), buffer); if (addBraces) {
		 * buffer.append(')'); } buffer.append('}');
		 * 
		 * } else if ("lim".equals(function.getName())) { // lim not implemented
		 * in jmathtex if (!jmathtex) { buffer.append("\\"); }
		 * buffer.append(function.getTexName()); buffer.append("_{");
		 * serialize(function.getArgument(0), buffer); buffer.append(
		 * " \\rightarrow "); serialize(function.getArgument(1), buffer); //
		 * jmathtex v0.7: incompatibility buffer.append("} " + (jmathtex ?
		 * "\\nbsp" : "\\ ") + " {"); boolean addBraces = currentBraces ||
		 * (function.getArgument(2).hasOperator() && function
		 * .getParent().hasOperator()); if (addBraces) { buffer.append('('); }
		 * serialize(function.getArgument(2), buffer); if (addBraces) {
		 * buffer.append(')'); } buffer.append('}');
		 * 
		 * }
		 */
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

	private static void serializeArgs(MathFunction mathFunction,
			StringBuilder stringBuilder, int[] order) {
		for (int i = 0; i < order.length; i++) {
			stringBuilder.append(i == 0 ? "((" : ",(");
			serialize(mathFunction.getArgument(order[i]), stringBuilder);
			stringBuilder.append(")");
		}
		stringBuilder.append(")");
	}

	private static void maybeInsertTimes(MathFunction mathFunction,
			StringBuilder stringBuilder) {
		MathSequence mathSequence = mathFunction.getParent();
		if (mathSequence != null && mathFunction.getParentIndex() > 0) {
			MathComponent mathComponent = mathSequence
					.getArgument(mathFunction.getParentIndex() - 1);
			if (mathComponent instanceof MathCharacter) {
				MathCharacter mathCharacter = (MathCharacter) mathComponent;
				if (mathCharacter.isCharacter() && mathCharacter
						.getUnicode() != MathCharacter.ZERO_SPACE) {
					stringBuilder.append("*");
				}
			}
		}
	}

	private static void serialize(MathArray mathArray,
			StringBuilder stringBuilder) {
		String open = mathArray.getOpen().getKey() + "";
		String close = mathArray.getClose().getKey() + "";
		String field = mathArray.getField().getKey() + "";
		String row = mathArray.getRow().getKey() + "";
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

	private static void serialize(MathSequence mathSequence,
			StringBuilder stringBuilder) {
		if (mathSequence == null) {
			return;
		}
		for (int i = 0; i < mathSequence.size(); i++) {
			serialize(mathSequence.getArgument(i), mathSequence, i,
					stringBuilder);
		}
	}

	/**
	 * @param formula
	 *            original formula
	 * @return formula after stringify + parse
	 */
	public static MathFormula reparse(MathFormula formula) {
		Parser parser = new Parser(formula.getMetaModel());
		MathFormula formula1 = null;
		try {
			formula1 = parser.parse(serialize(formula.getRootComponent()));

		} catch (ParseException e) {
			Log.warn("Problem parsing: " + formula.getRootComponent());
			e.printStackTrace();
		}
		return formula1 == null ? formula : formula1;
	}

	public static String serialize(Atom root) {
		if(root instanceof FractionAtom){
			FractionAtom frac = (FractionAtom) root;
			return "(" + serialize(frac.getNumerator()) + ")/("
					+ serialize(frac.getDenominator()) + ")";
		}
		if (root instanceof NthRoot) {
			NthRoot frac = (NthRoot) root;
			if (frac.getRoot() instanceof EmptyAtom) {
				return "sqrt(" + serialize(frac.getBase()) + ")";
			}
			return "nroot(" + serialize(frac.getBase()) + ","
					+ serialize(frac.getRoot()) + ")";
			// return "+";
		}
		if (root instanceof CharAtom) {
			CharAtom ch = (CharAtom) root;
			return ch.getCharacter() + "";
		}
		if (root instanceof ScriptsAtom) {
			ScriptsAtom ch = (ScriptsAtom) root;
			return subSup(ch);
		}
		if (root instanceof FencedAtom) {
			FencedAtom ch = (FencedAtom) root;
			return serialize(ch.getLeft()) + serialize(ch.getBase())
					+ serialize(ch.getRight());
		}
		if (root instanceof SpaceAtom) {
			return " ";
		}
		if (root instanceof SymbolAtom) {
			if (mappings == null) {
				initMappings();
			}
			SymbolAtom ch = (SymbolAtom) root;
			return mappings.get(ch.getName()) == null ? ch.getName()
					: mappings.get(ch.getName());
			// return "+";
		}
		if (root instanceof RowAtom) {
			RowAtom row = (RowAtom) root;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; row.getElement(i) != null; i++) {
				sb.append(serialize(row.getElement(i)));
			}
			return sb.toString();
		}
		return root.getClass().getName();
	}


	private static String subSup(ScriptsAtom ch) {
		StringBuilder sb = new StringBuilder(serialize(ch.getBase()));
		if (ch.getSub() != null) {
			String sub = serialize(ch.getSub());
			if (sub.length() > 1) {
				sb.append("_{");
				sb.append(sub);
				sb.append("}");
			} else {
				sb.append("_");
				sb.append(sub);
			}
		}
		if (ch.getSup() != null) {
			sb.append("^(");
			sb.append(serialize(ch.getSup()));
			sb.append(")");
		}
		return sb.toString();
	}

	private static void initMappings() {
		mappings = new HashMap<String, String>();
		mappings.put("plus", "+");
		mappings.put("minus", "-");
		mappings.put("equals", "=");
		mappings.put("lbrack", "(");
		mappings.put("rbrack", ")");
		mappings.put("lsqbrack", "[");
		mappings.put("rsqbrack", "]");
		mappings.put("normaldot", ".");
		mappings.put("comma", ",");
	}
}
