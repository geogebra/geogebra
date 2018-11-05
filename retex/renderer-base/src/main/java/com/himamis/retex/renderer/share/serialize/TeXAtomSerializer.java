package com.himamis.retex.renderer.share.serialize;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.BreakMarkAtom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.NthRoot;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.TypedAtom;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Converts parsed LaTeX to GGB syntax
 * 
 * @author Zbynek
 *
 */
public class TeXAtomSerializer {
	private BracketsAdapter adapter;

	/**
	 * @param ad
	 *            adapter
	 */
	public TeXAtomSerializer(BracketsAdapter ad) {
		this.adapter = ad;
	}

	/**
	 * @param root
	 *            tex formula
	 * @return expression in GeoGebra syntax
	 */
	public String serialize(Atom root) {
		if (root instanceof FractionAtom) {
			FractionAtom frac = (FractionAtom) root;
			return "(" + serialize(frac.getNumerator()) + ")/("
					+ serialize(frac.getDenominator()) + ")";
		}
		if (root instanceof NthRoot) {
			NthRoot frac = (NthRoot) root;
			if (frac.getRoot() == null) {
				return "sqrt(" + serialize(frac.getBase()) + ")";
			}
			return "nroot(" + serialize(frac.getBase()) + ","
					+ serialize(frac.getRoot()) + ")";
		}
		if (root instanceof CharAtom) {
			CharAtom ch = (CharAtom) root;
			return ch.getCharacter() + "";
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
			String left = serialize(ch.getLeft());
			String right = serialize(ch.getRight());
			String base = serialize(ch.getTrueBase());
			if (adapter == null) {
				return left + base + right;
			}
			return adapter.transformBrackets(left, base, right);
		}
		if (root instanceof SpaceAtom) {
			return " ";
		}
		if (root instanceof EmptyAtom || root instanceof BreakMarkAtom) {
			return "";
		}
		if (root instanceof SymbolAtom) {

			SymbolAtom ch = (SymbolAtom) root;
			String out = ch.getUnicode() + "";
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

		// BoldAtom, ItAtom, TextStyleAtom, StyleAtom, RomanAtom
		// TODO: probably more atoms need to implement HasTrueBase
		if (root instanceof HasTrueBase) {
			return serialize(((HasTrueBase) root).getTrueBase());
		}

		FactoryProvider.getInstance().debug("Unknown atom:" + root.getClass());
		// FactoryProvider.getInstance().printStacktrace();
		return "?";
	}

	private String subSup(ScriptsAtom ch) {
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

}
