package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.CursorAtom;
import com.himamis.retex.renderer.share.DefaultTeXFont;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXEnvironment;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class TeXBuilder {

	private MathSequence currentField;
	private int currentOffset;


	private Atom build(MathSequence mathFormula) {
		RowAtom ra = new RowAtom(null);
		for(int i=0;i<mathFormula.size();i++){
			if (mathFormula == currentField && i == 0 && currentOffset == 0) {
				addCursor(ra);
			}

			if (mathFormula.getArgument(i + 1) instanceof MathFunction) {
				String name = ((MathFunction) mathFormula.getArgument(i + 1))
						.getName();
				if ("^".equals(name) || "_".equals(name)) {
					continue;
				}
			}
			ra.add(build(mathFormula.getArgument(i)));

			if (mathFormula == currentField && i == currentOffset - 1) {
				addCursor(ra);
			}
		}
		return ra;

	}

	private void addCursor(RowAtom ra) {
		ra.add(new CursorAtom(FactoryProvider.getInstance().getGraphicsFactory()
				.createColor(100, 100, 255), 1));

	}

	private Atom build(MathComponent argument) {
		if (argument instanceof MathCharacter) {
			return new CharAtom(((MathCharacter) argument).getUnicode(),
					"mathnormal");
		}
		if (argument instanceof MathFunction) {
			return buildFunction((MathFunction) argument);
		}
		if (argument instanceof MathArray) {
			return buildArray((MathArray) argument);
		}
		return new CharAtom('X', "mathnormal");
	}

	private Atom buildArray(MathArray argument) {
		Atom ret = new FencedAtom(build(argument.getArgument(0)),
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
		return build(rootComponent);
	}

}
