package com.himamis.retex.renderer.share.serialize;

import com.himamis.retex.renderer.share.AccentedAtom;
import com.himamis.retex.renderer.share.ArrayAtom;
import com.himamis.retex.renderer.share.ArrayOfAtoms;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.BigDelimiterAtom;
import com.himamis.retex.renderer.share.BigOperatorAtom;
import com.himamis.retex.renderer.share.BreakMarkAtom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.HlineAtom;
import com.himamis.retex.renderer.share.JavaFontRenderingAtom;
import com.himamis.retex.renderer.share.NthRoot;
import com.himamis.retex.renderer.share.PhantomAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TypedAtom;
import com.himamis.retex.renderer.share.VRowAtom;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Converts parsed LaTeX to GGB syntax
 * 
 * @author Zbynek
 *
 */
public class TeXAtomSerializer {
	private SerializationAdapter adapter;

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
			if (nRoot.getRoot() == null) {
				return adapter.sqrt(serialize(nRoot.getTrueBase()));
			}
			return adapter.nroot(serialize(nRoot.getTrueBase()), serialize(nRoot.getRoot()));
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
			String base = serialize(ch.getTrueBase());
			if (isBinomial(ch.getTrueBase())) {
				return base;
			}
			String left = serialize(ch.getLeft());
			String right = serialize(ch.getRight());
			return adapter.transformBrackets(left, base, right);
		}
		if (root instanceof SpaceAtom) {
			return " ";
		}
		if (root instanceof EmptyAtom || root instanceof BreakMarkAtom
				|| root instanceof PhantomAtom) {
			return "";
		}
		if (root instanceof SymbolAtom) {

			SymbolAtom ch = (SymbolAtom) root;
			String out = adapter.convertCharacter(ch.getUnicode());
			if ("\u00b7".equals(out)) {
				return "*";
			}
			return out;

		}
		if (root instanceof RowAtom) {
			RowAtom row = (RowAtom) root;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; row.getElement(i) != null; i++) {
				sb.append(serialize(row.getElement(i)));
			}
			return sb.toString();
		}
		if (root instanceof AccentedAtom) {
			SymbolAtom accent = ((AccentedAtom) root).getAccent();
			String content = serialize(((AccentedAtom) root).getBase());
			if (accent == Symbols.VEC) {
				return " vector " + content;
			}
			return content + " with " + accent.getUnicode();
		}
		if (root instanceof VRowAtom) {
			VRowAtom row = (VRowAtom) root;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; row.getElement(i) != null; i++) {
				sb.append(serialize(row.getElement(i)));
				sb.append(' ');
			}
			return sb.toString();
		}

		if (root instanceof HlineAtom) {
			return "";
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
			ArrayAtom atom = (ArrayAtom) root;
			ArrayOfAtoms matrix = atom.getMatrix();
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

		if (root instanceof BigOperatorAtom) {
			return serializeBigOperator((BigOperatorAtom) root);
		}
		
		// BoldAtom, ItAtom, TextStyleAtom, StyleAtom, RomanAtom
		// TODO: probably more atoms need to implement HasTrueBase
		if (root instanceof HasTrueBase) {
			return serialize(((HasTrueBase) root).getTrueBase());
		}
		if (root instanceof BigDelimiterAtom) {
			return serialize(((BigDelimiterAtom) root).getDelimiter());
		}

		FactoryProvider.debugS("Unhandled atom:"
				+ (root == null ? "null" : (root.getClass() + " " + root.toString())));
		// FactoryProvider.getInstance().printStacktrace();

		return "?";
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
		String op = serialize(bigOp.getTrueBase());

		if ("log".equals(op)) {
			return "log_" + serialize(bigOp.getBottom());
		}

		// eg sum/product
		return serialize(bigOp.getTrueBase()) + " from " + serialize(bigOp.getBottom()) + " to "
				+ serialize(bigOp.getTop());
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
