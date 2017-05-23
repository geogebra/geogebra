package org.geogebra.common.io.latex;

import java.util.HashMap;

import org.geogebra.common.util.lang.Unicode;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.NthRoot;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.TypedAtom;

/**
 * Converts parsed LaTeX to GGB syntax
 * 
 * @author Zbynek
 *
 */
public class TeXAtomSerializer {
	private static HashMap<String, String> mappings;
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
		if (root instanceof TypedAtom) {
			TypedAtom ch = (TypedAtom) root;
			return serialize(ch.getBase());
		}
		if (root instanceof RomanAtom) {
			RomanAtom ch = (RomanAtom) root;
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
			String base = serialize(ch.getBase());
			if (adapter == null) {
				return left + base + right;
			}
			return adapter.transformBrackets(left, base, right);
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

	private static void initMappings() {
		mappings = new HashMap<String, String>();
		mappings.put("plus", "+");
		mappings.put("minus", "-");
		mappings.put("equals", "=");
		mappings.put("lbrack", "(");
		mappings.put("rbrack", ")");
		mappings.put("lsqbrack", "[");
		mappings.put("rsqbrack", "]");
		mappings.put("lbrace", "{");
		mappings.put("rbrace", "}");
		mappings.put("normaldot", ".");
		mappings.put("comma", ",");
		mappings.put("ge", ">=");
		mappings.put("le", "<=");
		mappings.put("geq", ">=");
		mappings.put("leq", "<=");
		mappings.put("cdot", "*");
		mappings.put("times", "*");
		mappings.put("theta", Unicode.thetaStr);
	}
}
