package com.himamis.retex.editor.share.serializer;

import java.util.HashMap;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.CursorAtom;
import com.himamis.retex.renderer.share.DefaultTeXFont;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SelectionAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXEnvironment;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class TeXBuilder {

	private MathSequence currentField;
	private int currentOffset;
	private HashMap<Atom, MathComponent> atomToComponent;
	private MathComponent selectionStart;
	private MathComponent selectionEnd;

	private Atom buildSequence(MathSequence mathFormula) {
		return buildSequence(mathFormula, 0, mathFormula.size() - 1);
	}
	private Atom buildSequence(MathSequence mathFormula, int from, int to) {
		RowAtom ra = new RowAtom(null);
		int i= from;
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

	private void addCursor(RowAtom ra) {
		ra.add(new CursorAtom(FactoryProvider.getInstance().getGraphicsFactory()
				.createColor(100, 100, 255), 1));

	}

	private Atom build(MathComponent argument) {
		Atom ret = null;
		if (argument instanceof MathCharacter) {
			ret = new CharAtom(((MathCharacter) argument).getUnicode(),
					"mathnormal");
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

	private Atom buildArray(MathArray argument) {
		Atom ret = new FencedAtom(buildSequence(argument.getArgument(0)),
				new SymbolAtom("lbrack", TeXConstants.TYPE_OPENING, true),
				new SymbolAtom("rbrack", TeXConstants.TYPE_CLOSING, true));
		System.out.println(
				ret.createBox(new TeXEnvironment(0, new DefaultTeXFont(12))));
		return ret;
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
		return new CharAtom('F', "mathnormal");
	}

	public Atom build(MathSequence rootComponent, MathSequence currentField,
			int currentOffset, MathComponent selectionStart,
			MathComponent selectionEnd) {
		this.currentField = currentField;
		this.currentOffset = currentOffset;
		this.atomToComponent = new HashMap<Atom, MathComponent>();
		this.selectionStart = selectionStart;
		this.selectionEnd = selectionEnd;
		return build(rootComponent);
	}

	public MathComponent getComponent(Atom atom) {
		// TODO Auto-generated method stub
		return atomToComponent.get(atom);
	}

}
