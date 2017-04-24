package com.himamis.retex.editor.share.serializer;

import java.util.HashMap;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.CursorAtom;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SelectionAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Directly convert MathComponents into atoms
 * 
 * @author Zbynek
 *
 */
public class TeXBuilder {

	private MathSequence currentField;
	private int currentOffset;
	private HashMap<Atom, MathComponent> atomToComponent;
	private MathComponent selectionStart;
	private MathComponent selectionEnd;
	private TeXParser parser;

	private Atom buildSequence(MathSequence mathFormula) {
		return buildSequence(mathFormula, 0, mathFormula.size() - 1);
	}
	private Atom buildSequence(MathSequence mathFormula, int from, int to) {
		RowAtom ra = new RowAtom(null);
		int i= from;
		if (mathFormula == currentField && to < from) {
			addCursor(ra);
		}
		while (i <= to) {
			if (mathFormula == currentField && i == 0 && currentOffset == 0) {
				addCursor(ra);
			}

			if (mathFormula.getArgument(i + 1) instanceof MathFunction) {
				String name = ((MathFunction) mathFormula.getArgument(i + 1))
						.getName();
				if ("^".equals(name) || "_".equals(name)) {
					i++;
					continue;
				}
			}
			

			if (mathFormula.getArgument(i) == selectionStart) {
				selectionStart = null;
				SelectionAtom sa = new SelectionAtom(
						buildSequence(mathFormula, i,
								selectionEnd.getParentIndex()),
						ColorAtom.getColor("#CCCCFF"),
						null);
				ra.add(sa);
				i = selectionEnd.getParentIndex();
			} else {
				ra.add(build(mathFormula.getArgument(i)));
			}
			
			if (mathFormula == currentField && i == currentOffset - 1
					&& selectionEnd == null) {
				addCursor(ra);
			}
			i++;
		}
		return ra;

	}

	private static void addCursor(RowAtom ra) {
		ra.add(new CursorAtom(FactoryProvider.getInstance().getGraphicsFactory()
				.createColor(100, 100, 255), 0.9));

	}

	private Atom build(MathComponent argument) {
		Atom ret = null;
		if (argument instanceof MathCharacter) {
			ret = newCharAtom(((MathCharacter) argument).getUnicode());
		}
		else if (argument instanceof MathFunction) {
			ret = buildFunction((MathFunction) argument);
		}
		else if (argument instanceof MathArray) {
			ret = buildArray((MathArray) argument);
		}
		else if (argument instanceof MathSequence) {
			ret = buildSequence((MathSequence) argument);
		} else {
			ret = new EmptyAtom();
		}
		atomToComponent.put(ret, argument);
		return ret;
	}

	private Atom newCharAtom(char unicode) {
		if (parser == null) {
			TeXFormula tf = new TeXFormula();
			parser = new TeXParser("", tf);
		}
		Atom ret = parser.convertCharacter(unicode, false);
		if (ret instanceof SymbolAtom) {
			ret = ret.duplicate();
		}
		return ret;
	}
	private Atom buildArray(MathArray argument) {
		String leftKey = "lbrack";
		if (argument.getOpenKey() == '[') {
			leftKey = "lsqbrack";
		}
		if (argument.getOpenKey() == '{') {
			leftKey = "lbrace";
		}
		String rightKey = "rbrack";
		if (argument.getCloseKey() == ']') {
			rightKey = "rsqbrack";
		}
		if (argument.getCloseKey() == '}') {
			rightKey = "rbrace";
		}

		return buildFenced(leftKey, rightKey, argument, 0);
	}

	private Atom buildFenced(String leftKey, String rightKey,
			MathContainer argument, int offset) {
		RowAtom row = new RowAtom(null);
		for (int i = offset; i < argument.size(); i++) {
			if (i > 0) {
				row.add(newCharAtom(','));
			}
			row.add(build(argument.getArgument(i)));
		}
		return new FencedAtom(row,
				new SymbolAtom(leftKey, TeXConstants.TYPE_OPENING, true),
				new SymbolAtom(rightKey, TeXConstants.TYPE_CLOSING, true));
	}
	private Atom buildFunction(MathFunction argument) {
		if ("^".equals(argument.getName())) {
			MathSequence parent = argument.getParent();
			int idx = argument.getParentIndex();
			return new ScriptsAtom(build(parent.getArgument(idx - 1)), null,
					build(argument.getArgument(0)));
		}
		if ("_".equals(argument.getName())) {
			MathSequence parent = argument.getParent();
			int idx = argument.getParentIndex();
			return new ScriptsAtom(build(parent.getArgument(idx - 1)),
					build(argument.getArgument(0)), null);
		}
		if ("frac".equals(argument.getName())) {
			return new FractionAtom(
					build(argument.getArgument(0)),
					build(argument.getArgument(1)));
		}
		RowAtom row = new RowAtom(null);
		for (int i = 0; i < argument.getName().length(); i++) {
			row.add(newCharAtom(argument.getName().charAt(i)));
		}
		row.add(buildFenced("lbrack", "rbrack", argument, 0));
		return row;
	}

	/**
	 * @param rootComponent
	 *            root
	 * @param currentField1
	 *            selected field
	 * @param currentOffset1
	 *            cursor offset within currentField
	 * @param selectionStart1
	 *            first selected atom
	 * @param selectionEnd1
	 *            last selected atom
	 * @return atom representing the whole sequence
	 */
	public Atom build(MathSequence rootComponent, MathSequence currentField1,
			int currentOffset1, MathComponent selectionStart1,
			MathComponent selectionEnd1) {
		this.currentField = currentField1;
		this.currentOffset = currentOffset1;
		this.atomToComponent = new HashMap<Atom, MathComponent>();
		this.selectionStart = selectionStart1;
		this.selectionEnd = selectionEnd1;
		return build(rootComponent);
	}

	/**
	 * Access the internal mapping atom-&gt; component
	 * 
	 * @param atom
	 *            atom
	 * @return corresponding component
	 */
	public MathComponent getComponent(Atom atom) {
		// TODO Auto-generated method stub
		return atomToComponent.get(atom);
	}

}
