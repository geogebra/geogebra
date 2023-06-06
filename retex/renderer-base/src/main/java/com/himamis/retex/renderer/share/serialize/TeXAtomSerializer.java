package com.himamis.retex.renderer.share.serialize;

import com.himamis.retex.renderer.share.ArrayAtom;
import com.himamis.retex.renderer.share.ArrayOfAtoms;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.BigDelimiterAtom;
import com.himamis.retex.renderer.share.BigOperatorAtom;
import com.himamis.retex.renderer.share.BreakMarkAtom;
import com.himamis.retex.renderer.share.CedillaAtom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.CursorAtom;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.GraphicsAtom;
import com.himamis.retex.renderer.share.GraphicsAtomBase64;
import com.himamis.retex.renderer.share.HVruleAtom;
import com.himamis.retex.renderer.share.HasUnderOver;
import com.himamis.retex.renderer.share.HlineAtom;
import com.himamis.retex.renderer.share.JavaFontRenderingAtom;
import com.himamis.retex.renderer.share.MHeightAtom;
import com.himamis.retex.renderer.share.NthRoot;
import com.himamis.retex.renderer.share.OgonekAtom;
import com.himamis.retex.renderer.share.OverlinedAtom;
import com.himamis.retex.renderer.share.PhantomAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.RuleAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SetLengthAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TextCircledAtom;
import com.himamis.retex.renderer.share.TheAtom;
import com.himamis.retex.renderer.share.TypedAtom;
import com.himamis.retex.renderer.share.VlineAtom;
import com.himamis.retex.renderer.share.mhchem.CEEmptyAtom;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Converts parsed LaTeX to GGB syntax
 * 
 * @author Zbynek
 *
 */
public class TeXAtomSerializer {
	public static final String DEGREE = "\u2218";
	public static final String HYPERBOLICS = "sinh cosh tanh coth sech csch";
	public static final String TRIGONOMETRICS = "sin cos tan cot sec csc" + HYPERBOLICS;
	private final SerializationAdapter adapter;

	private enum DegreePlural {
		None,
		Degree,
		Degrees
	}

	/**
	 * @param ad
	 *            adapter
	 */
	public TeXAtomSerializer(SerializationAdapter ad) {
		this.adapter = ad == null ? new DefaultSerializationAdapter() : ad;
	}

	/**
	 * @param root
	 *            tex formula
	 * @return expression in GeoGebra syntax
	 */
	public String serialize(Atom root) {
		if (root instanceof FractionAtom) {
			return serializeFractionAtom((FractionAtom) root);
		}
		if (root instanceof NthRoot) {
			NthRoot nRoot = (NthRoot) root;
			String index = nRoot.getRoot() == null ? "" : serialize(nRoot.getRoot());
			if (index.isEmpty()) {
				return adapter.sqrt(serialize(nRoot.getTrueBase()));
			}
			return adapter.nroot(serialize(nRoot.getTrueBase()), index);
		}
		if (root instanceof CharAtom) {
			CharAtom ch = (CharAtom) root;
			return adapter.convertCharacter(ch.getCharacter());
		}
		if (root instanceof TypedAtom) {
			TypedAtom ch = (TypedAtom) root;
			return serialize(ch.getBase());
		}
		if (root instanceof ScriptsAtom) {
			ScriptsAtom ch = (ScriptsAtom) root;
			return subSup(ch);
		}
		if (root instanceof FencedAtom) {
			FencedAtom ch = (FencedAtom) root;
			Atom bracketsContent = ch.getTrueBase();
			String base = serialize(bracketsContent);
			if (isBinomial(bracketsContent)) {
				return base;
			}
			String left = serialize(ch.getLeft());
			String right = serialize(ch.getRight());
			if (bracketsContent instanceof ArrayAtom) {
				return adapter.transformMatrix(left, base, right);
			}
			return adapter.transformBrackets(left, base, right);
		}
		if (root instanceof SpaceAtom) {
			return " ";
		}
		if (root instanceof EmptyAtom || root instanceof BreakMarkAtom
				|| root instanceof PhantomAtom || root instanceof HlineAtom
				|| root instanceof SetLengthAtom || root instanceof CursorAtom
				|| root instanceof VlineAtom || root instanceof CEEmptyAtom
				|| root instanceof RuleAtom || root instanceof GraphicsAtom
				|| root instanceof GraphicsAtomBase64 || root instanceof HVruleAtom
				|| root instanceof MHeightAtom || root instanceof TheAtom) {
			return "";
		}
		if (root instanceof SymbolAtom) {
			return serializeSymbol((SymbolAtom) root);
		}
		if (root instanceof RowAtom) {
			RowAtom row = (RowAtom) root;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; row.getElement(i) != null; i++) {
				sb.append(serialize(row.getElement(i)));
			}
			return adapter.getLigature(sb.toString());
		}
		if (root instanceof IsAccentedAtom) {
			Atom accent = ((IsAccentedAtom) root).getAccent();
			String content = serialize(((IsAccentedAtom) root).getTrueBase());
			if (accent == Symbols.VEC) {
				return " vector " + content;
			}
			return content + " with " + serialize(accent);
		}
		if (root instanceof TextCircledAtom) {
			return "circled " + serialize(((TextCircledAtom) root).getTrueBase());
		}
		if (root instanceof HasUnderOver) {
			return serialize(((HasUnderOver) root).getUnderOver())
					+ (((HasUnderOver) root).isUnder() ? " under " : " over ")
					+ serialize(((HasUnderOver) root).getTrueBase());
		}
		if (root instanceof HasElements) {
			HasElements row = (HasElements) root;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; row.getElement(i) != null; i++) {
				sb.append(serialize(row.getElement(i)));
				sb.append(' ');
			}
			return sb.toString();
		}
		if (root instanceof HasCharacter) {
			return ((HasCharacter) root).getCharacter();
		}

		if (root instanceof ColorAtom) {
			ColorAtom row = (ColorAtom) root;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; row.getElement(i) != null; i++) {
				sb.append(serialize(row.getElement(i)));
			}
			return sb.toString();
		}

		if (root instanceof JavaFontRenderingAtom) {
			return ((JavaFontRenderingAtom) root).getString();
		}

		// serialise table to eg {{1,2,3},{3,4,5}}
		if (root instanceof ArrayAtom) {
			return serializeArray(((ArrayAtom) root).getMatrix());
		}

		if (root instanceof BigOperatorAtom) {
			return serializeBigOperator((BigOperatorAtom) root);
		}

		if (root instanceof OverlinedAtom) {
			String base = serialize(((OverlinedAtom) root).getTrueBase());

			// Only serialize to a recurring decimal if there are only digits
			if (base.matches("\\d+")) {
				return serializeOverLine(base);
			}
			return "Segment " + base;
		}

		// BoldAtom, ItAtom, TextStyleAtom, StyleAtom, RomanAtom
		if (root instanceof HasTrueBase) {
			Atom trueBase = ((HasTrueBase) root).getTrueBase();
			DegreePlural degreePlural = checkDegrees(trueBase);
			if (degreePlural != DegreePlural.None) {
				return serialize(trueBase) + serializeDegrees(degreePlural);
			}
			return serialize(trueBase);
		}

		if (root instanceof BigDelimiterAtom) {
			return serialize(((BigDelimiterAtom) root).getDelimiter());
		}
		if (root == null) {
			return "";
		}
		FactoryProvider.debugS("Unhandled atom:"
				+ (root.getClass() + " " + root.toString()));

		return "?";
	}

	private String serializeArray(ArrayOfAtoms matrix) {
		int rows = matrix.getRows();
		int cols = matrix.getCols();

		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (int row = 0; row < rows; row++) {
			sb.append('{');
			for (int col = 0; col < cols; col++) {
				sb.append(serialize(matrix.get(row, col)));

				if (col < cols - 1) {
					sb.append(",");
				}
			}
			sb.append("}");

			if (row < rows - 1) {
				sb.append(",");
			}
		}
		sb.append('}');

		return sb.toString();
	}

	private String serializeSymbol(SymbolAtom symbol) {
		if (symbol == CedillaAtom.CEDILLA) {
			return "cedilla";
		}
		if (symbol == OgonekAtom.OGONEK) {
			return "ogonek";
		}
		return adapter.convertCharacter(symbol.getUnicode());
	}

	private DegreePlural checkDegrees(Atom trueBase) {
		if (!(trueBase instanceof RowAtom)) {
			return DegreePlural.None;
		}
		RowAtom row = (RowAtom) trueBase;
		if (!(row.last() instanceof ScriptsAtom)) {
			return DegreePlural.None;
		}

		ScriptsAtom scripts = (ScriptsAtom) (row.last());
		boolean degree = DEGREE.equals(serialize(scripts.getSup()));
		if (degree && "1".equals(serialize(row.getBase())) && noNumberIn(row)) {
			return DegreePlural.Degree;
		}
		return degree ? DegreePlural.Degrees : DegreePlural.None;
	}

	private boolean noNumberIn(RowAtom row) {
		for (int i = 0; i < row.size(); i++) {
			if (row.getElement(i) instanceof CharAtom) {
				return false;
			}
		}
		return true;
	}

	private String serializeDegrees(DegreePlural plural) {
		return plural == DegreePlural.Degree ? "degree" : "degrees";
	}

	private String serializeFractionAtom(FractionAtom frac) {
		if (isBinomial(frac)) {
			return "nCr(" + serialize(frac.getNumerator()) + ","
					+ serialize(frac.getDenominator()) + ")";
		}
		return adapter.fraction(serialize(frac.getNumerator()),
				serialize(frac.getDenominator()));
	}

	private boolean isBinomial(Atom frac) {
		return frac instanceof FractionAtom && ((FractionAtom) frac).isRuleHidden();
	}

	private String serializeBigOperator(BigOperatorAtom bigOp) {
		Atom trueBase = bigOp.getTrueBase();
		String op = serialize(trueBase);

		if ("log".equals(op)) {
			return "log_" + serialize(bigOp.getBottom());
		}

		if (isTrigonometric(trueBase)) {
			if (isInverse(bigOp.getTop())) {
				return " arc" + getFunctionName(trueBase);
			} else {
				return adapter.subscriptContent(
						serialize(trueBase),
						null, serialize(bigOp.getTop()));
			}
		}

		// eg sum/product
		return serialize(trueBase) + " from " + serialize(bigOp.getBottom()) + " to "
				+ serialize(bigOp.getTop());
	}

	private String serializeOverLine(String base) {
		String ret = "";
		for (int i = 0; i < base.length(); i++) {
			ret += base.charAt(i) + "\u0305";
		}
		return ret;
	}

	private boolean isTrigonometric(Atom trueBase) {
		return TRIGONOMETRICS.contains(serialize(trueBase));
	}

	private boolean isInverse(Atom top) {
		return " minus 1".equals(serialize(top));
	}

	private String getFunctionName(Atom trueBase) {
		String name = serialize(trueBase);
		if (isHyperbolic(name)) {
			return " hyperbolic " + name.substring(0, name.length() - 1);
		}
		return " " + name;
	}

	private boolean isHyperbolic(String name) {
		return name.endsWith("h");
	}

	private String subSup(ScriptsAtom script) {
		String base = serialize(script.getTrueBase());
		String sub = null;
		String sup = null;
		serialize(script.getTrueBase());
		if (script.getSub() != null) {
			sub = serialize(script.getSub());
		}
		if (script.getSup() != null) {
			sup = serialize(script.getSup());
		}
		return adapter.subscriptContent(base, sub, sup);
	}

}
