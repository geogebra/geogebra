package com.himamis.retex.editor.share.serializer;

import java.util.HashMap;

import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.NthRoot;
import com.himamis.retex.renderer.share.PhantomAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.ScaleAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SpaceAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.UnderOverArrowAtom;
import com.himamis.retex.renderer.share.commands.CommandOpName;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;

/**
 * Directly convert MathComponents into atoms
 * 
 * @author Zbynek&Ágoston
 *
 */
public class TeXBuilder {

	public static final MathComponent SELECTION = new MathSequence() {
		@Override
		public String toString() {
			return "SELECTION";
		}
	};

	private MathSequence currentField;
	private HashMap<Atom, MathComponent> atomToComponent;
	private TeXParser parser;

	private final Color placeholderColor = FactoryProvider.getInstance().getGraphicsFactory()
			.createColor(0xDCDCDC);

	private Atom buildSequence(MathSequence mathFormula) {
		RowAtom ra = new RowAtom();

		if (mathFormula.size() == 0) {
			Atom a;
			if (mathFormula == currentField) {
				a = newCharAtom('\0');
				atomToComponent.put(a, SELECTION);
			} else {
				a = new ColorAtom(
						new ScaleAtom(new PhantomAtom(new CharAtom('g')), 1, 1.6),
						placeholderColor,
						null
				);
			}
			ra.add(a);
			return ra;
		}

		for (int i = 0; i < mathFormula.size(); i++) {
			if (mathFormula.getArgument(i).hasTag(Tag.SUPERSCRIPT)) {
				Atom sup = build(((MathFunction) mathFormula.getArgument(i)).getArgument(0));
				Atom tmp = addToSup(ra.getLastAtom(), sup);
				atomToComponent.put(tmp, mathFormula.getArgument(i));
				ra.add(tmp);
				continue;
			} else if (mathFormula.getArgument(i).hasTag(Tag.SUBSCRIPT)) {
				Atom sub = build(((MathFunction) mathFormula.getArgument(i)).getArgument(0));
				Atom tmp = addToSub(ra.getLastAtom(), sub);
				atomToComponent.put(tmp, mathFormula.getArgument(i));
				ra.add(tmp);
				continue;
			}

			Atom argument = build(mathFormula.getArgument(i));
			ra.add(argument);
		}

		return ra;
	}

	private Atom addToSub(Atom lastAtom, Atom sub) {
		if (lastAtom instanceof ScriptsAtom) {
			((ScriptsAtom) lastAtom).addToSub(sub);
			return lastAtom;
		}
		return new ScriptsAtom(lastAtom, sub, null);
	}

	private Atom addToSup(Atom lastAtom, Atom sup) {
		if (lastAtom instanceof ScriptsAtom) {
			((ScriptsAtom) lastAtom).addToSup(sup);
			return lastAtom;
		}
		return new ScriptsAtom(lastAtom, null, sup);
	}

	private Atom build(MathComponent argument) {
		Atom ret = null;
		if (argument instanceof MathCharacter) {
			ret = newCharAtom(((MathCharacter) argument).getUnicode());
		} else if (argument instanceof MathFunction) {
			ret = buildFunction((MathFunction) argument);
		} else if (argument instanceof MathArray) {
			ret = buildArray((MathArray) argument);
		} else if (argument instanceof MathSequence) {
			ret = buildSequence((MathSequence) argument);
		} else {
			ret = new EmptyAtom();
		}

		if (!atomToComponent.containsKey(ret)) {
			atomToComponent.put(ret, argument);
		}
		return ret;
	}

	private Atom newCharAtom(char unicode) {
		if (parser == null) {
			parser = new TeXParser("");
		}
		Atom ret = parser.getAtomFromUnicode(unicode, false);
		if (ret instanceof SymbolAtom) {
			ret = ((SymbolAtom) ret).duplicate();
		}
		if (unicode == '=') {
			return new RowAtom(space(), ret, space());
		}

		return ret;
	}

	private Atom space() {
		return new SpaceAtom(TeXConstants.Muskip.THIN);
	}

	private Atom buildArray(MathArray argument) {
		return buildFenced(argument.getOpenKey(), argument.getCloseKey(), argument, 0);
	}

	private Atom buildString(String str) {
		RowAtom atom = new RowAtom();
		for (char c : str.toCharArray()) {
			atom.add(newCharAtom(c));
		}
		return atom;
	}

	private Atom buildFenced(char leftKey, char rightKey,
			MathContainer argument, int offset) {
		RowAtom row = new RowAtom((Atom) null);
		for (int i = offset; i < argument.size(); i++) {
			if (i > offset) {
				row.add(newCharAtom(','));
			}
			row.add(build(argument.getArgument(i)));
		}
		return new FencedAtom(row,
				new SymbolAtom(lookupBracket(leftKey), TeXConstants.TYPE_OPENING,
						leftKey),
				new SymbolAtom(lookupBracket(rightKey), TeXConstants.TYPE_CLOSING,
						rightKey));
	}

	private static String lookupBracket(char bracket) {
		switch (bracket) {
		case '(':
			return "lbrack";
		case ')':
			return "rbrack";
		case '{':
			return "lbrace";
		case '}':
			return "rbrace";
		case '[':
			return "lsqbrack";
		case ']':
			return "rsqbrack";
		case '\u3008':
			return "langle";
		case '\u3009':
			return "rangle";
		case '\u2308':
			return "lceil";
		case '\u2309':
			return "rceil";
		case '\u230A':
			return "lfloor";
		case '\u230B':
			return "rfloor";
		case '|':
			return "vert";
		default:
			FactoryProvider.debugS("missing case in lookupBracket()");
			return "";
		}
	}

	private Atom buildFunction(MathFunction argument) {
		switch (argument.getName()) {
		case SUPERSCRIPT:
			return new ScriptsAtom(
					new EmptyAtom(),
					null,
					build(argument.getArgument(0))
			);
		case SUBSCRIPT:
			return new ScriptsAtom(
					new EmptyAtom(),
					build(argument.getArgument(0)),
					null
			);
		case FRAC:
			return new FractionAtom(build(argument.getArgument(0)),
					build(argument.getArgument(1)));
		case SQRT:
			return new NthRoot(build(argument.getArgument(0)), new EmptyAtom());
		case NROOT:
			return new NthRoot(build(argument.getArgument(1)),
					build(argument.getArgument(0)));
		case LOG:
			Atom log = buildString("log");
			if (argument.getArgument(0).size() > 0) {
				log = new ScriptsAtom(log, build(argument.getArgument(0)), null);
			}

			return new RowAtom(
					log,
					buildFenced('(', ')', argument, 1)
			);
		case ABS:
			return buildFenced('|', '|', argument, 0);
		case FLOOR:
			return buildFenced('\u230A', '\u230B', argument, 0);
		case CEIL:
			return buildFenced('\u2308', '\u2309', argument, 0);
		case DEF_INT:
			return new ScriptsAtom(
					Symbols.INT.duplicate(),
					build(argument.getArgument(0)),
					build(argument.getArgument(1))
			);
		case SUM_EQ:
			return new ScriptsAtom(
					newCharAtom('\u2211'),
					build(argument.getArgument(0)),
					build(argument.getArgument(1))
			);
		case PROD_EQ:
			return new ScriptsAtom(
					newCharAtom('\u220F'),
					build(argument.getArgument(0)),
					build(argument.getArgument(1))
			);
		case LIM_EQ:
			return new ScriptsAtom(
					CommandOpName.createOperation("lim", null, true),
					build(argument.getArgument(0)),
					null
			);
		case VEC:
			return new UnderOverArrowAtom(build(argument.getArgument(0)), false, true);
		default:
			return new RowAtom(
				build(argument.getArgument(0)),
				buildFenced('(', ')', argument, 1)
			);
		}
	}

	/**
	 * @param rootComponent
	 *            root
	 * @param currentField1
	 *            selected field
	 * @return atom representing the whole sequence
	 */
	public Atom build(MathSequence rootComponent, MathSequence currentField1) {
		this.currentField = currentField1;
		this.atomToComponent = new HashMap<>();
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
