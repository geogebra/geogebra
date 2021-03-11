package com.himamis.retex.editor.share.serializer;

import java.util.HashMap;

import com.himamis.retex.editor.share.editor.SyntaxAdapter;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.ArrayOfAtoms;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.CharAtom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.EmptyAtom;
import com.himamis.retex.renderer.share.EnvArray;
import com.himamis.retex.renderer.share.FencedAtom;
import com.himamis.retex.renderer.share.FractionAtom;
import com.himamis.retex.renderer.share.NthRoot;
import com.himamis.retex.renderer.share.PhantomAtom;
import com.himamis.retex.renderer.share.RomanAtom;
import com.himamis.retex.renderer.share.RowAtom;
import com.himamis.retex.renderer.share.SMatrixAtom;
import com.himamis.retex.renderer.share.ScaleAtom;
import com.himamis.retex.renderer.share.ScriptsAtom;
import com.himamis.retex.renderer.share.SymbolAtom;
import com.himamis.retex.renderer.share.Symbols;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.TextStyle;
import com.himamis.retex.renderer.share.TextStyleAtom;
import com.himamis.retex.renderer.share.UnderOverArrowAtom;
import com.himamis.retex.renderer.share.commands.CommandOpName;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

/**
 * Directly convert MathComponents into atoms
 * 
 * @author Zbynek & Agoston
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
	private final TeXParser parser;
	private final TeXSerializer teXSerializer;

	private final static HashMap<Character, String> replacements = new HashMap<>();

	static {
		replacements.put('*', "cdot");
		replacements.put('%', "textpercent");
		replacements.put('$', "textdollar");
		replacements.put('&', "textampersand");
	}

	public TeXBuilder() {
		parser = new TeXParser("");
		teXSerializer = new TeXSerializer();
	}

	private Atom buildSequence(MathSequence mathFormula) {
		RowAtom ra = new RowAtom();

		if (mathFormula.size() == 0) {
			Atom a;
			if (mathFormula == currentField) {
				Atom placeholder = new CharAtom('1');
				atomToComponent.put(placeholder, SELECTION);
				a = new ScaleAtom(new PhantomAtom(placeholder), 0.1, 1);
			} else {
				a = getPlaceholder(mathFormula);
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

			// same ugly hack for ln as we have in TeXSerializer
			if (argument instanceof CharAtom
					&& ((CharAtom) argument).getCharacter() == 'n'
					&& ra.last() instanceof CharAtom
					&& ((CharAtom) ra.last()).getCharacter() == 'l') {
				Atom last = ra.getLastAtom();
				ra.add(new RomanAtom(new RowAtom(last, argument)));
				continue;
			}

			ra.add(argument);
		}

		return ra;
	}

	private Atom getPlaceholder(MathSequence sequence) {
		MathContainer parent = sequence.getParent();
		if (parent == null
				|| (parent instanceof MathArray	&& parent.size() == 1)) {
			return getInvisiblePlaceholder();
		}
		if (parent instanceof MathFunction) {
			Tag fn = ((MathFunction) parent).getName();
			if (fn == Tag.APPLY || fn == Tag.LOG) {
				return getInvisiblePlaceholder();
			}
		}

		return getPlaceholderBox();
	}

	private Atom getInvisiblePlaceholder() {
		return new PhantomAtom(new CharAtom('1'));
	}

	private Atom getPlaceholderBox() {
		return new ColorAtom(
				new ScaleAtom(new PhantomAtom(new CharAtom('g')), 1, 1.6),
				FactoryProvider.getInstance().getGraphicsFactory().createColor(0xDCDCDC),
				null
		);
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
		String replacement = replacements.get(unicode);
		if (replacement != null) {
			return SymbolAtom.get(replacement).duplicate();
		}

		Atom ret = parser.getAtomFromUnicode(unicode, true);
		if (ret instanceof SymbolAtom) {
			ret = ((SymbolAtom) ret).duplicate();
		}

		return ret;
	}

	private Atom buildArray(MathArray array) {
		if (array.isMatrix()) {
			final SymbolAtom op = SymbolAtom.get(lookupBracket('('));
			final SymbolAtom cl = SymbolAtom.get(lookupBracket(')'));

			ArrayOfAtoms atoms = new ArrayOfAtoms();
			for (int i = 0; i < array.rows(); i++) {
				for (int j = 0; j < array.columns(); j++) {
					if (j != 0) {
						atoms.add(EnvArray.ColSep.get());
					}
					atoms.add(build(array.getArgument(i, j)));
				}
				atoms.add(EnvArray.RowSep.get());
			}
			atoms.checkDimensions();

			final Atom mat = new SMatrixAtom(atoms, false);
			return new FencedAtom(mat, op, cl);
		} else if (array.getOpenKey() == '"') {
			Atom argument = new RowAtom(newCharAtom(Unicode.OPEN_DOUBLE_QUOTE),
					build(array.getArgument(0)), newCharAtom(Unicode.CLOSE_DOUBLE_QUOTE));

			return new RomanAtom(new TextStyleAtom(argument, TextStyle.MATHNORMAL));
		} else {
			return buildFenced(array.getOpenKey(), array.getCloseKey(), array, 0);
		}
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
		case CBRT:
			return new NthRoot(build(argument.getArgument(0)), newCharAtom('3'));
		case NROOT:
			return new NthRoot(build(argument.getArgument(1)),
					build(argument.getArgument(0)));
		case LOG:
			Atom log = new RomanAtom(buildString("log"));
			if (argument.getArgument(0).size() > 0
					|| currentField == argument.getArgument(0)) {
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
			StringBuilder functionName = new StringBuilder();
			teXSerializer.serialize(argument.getArgument(0), functionName);
			Atom function = build(argument.getArgument(0));

			if (teXSerializer.isFunction(functionName.toString())) {
				function = new RomanAtom(function);
			}

			return new RowAtom(
				function,
				buildFenced(argument.getOpeningBracket(), argument.getClosingBracket(),
						argument, 1)
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

	public void setSyntaxAdapter(SyntaxAdapter syntaxAdapter) {
		teXSerializer.setSyntaxAdapter(syntaxAdapter);
	}
}
