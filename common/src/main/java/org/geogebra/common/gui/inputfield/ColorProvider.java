package org.geogebra.common.gui.inputfield;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.parser.function.ParserFunctions;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.gwtproject.regexp.shared.MatchResult;
import org.gwtproject.regexp.shared.RegExp;

/**
 * Class for coloring the labels in input bar
 * 
 * @author bencze
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ColorProvider {

	private static final int TEXT_LENGHT_LIMIT = 1000;

	/** Regular expression strings */
	private static final String LABEL_REGEX_STRING = "((\\p{L}\\p{M}*)(\\p{L}\\p{M}*|\\p{Nd})*'?"
			+ "(\\_\\{+(\\P{M}\\p{M}*)+\\}|\\_(\\P{M}\\p{M})?)?(\\p{L}\\p{M}|\\p{Nd})*)";
	private static final String LABEL_PARAM = LABEL_REGEX_STRING + "(\\(|\\[)?";
	private static final String STRING = "((\\P{M}\\p{M}*)*)";
	private static final String WHITESPACE = "\\p{Z}*";

	/** Colors */
	private static final GColor COLOR_DEFINED = GeoGebraColorConstants.DEFINED_OBJECT_COLOR;
	private static final GColor COLOR_UNDEFINED = GeoGebraColorConstants.UNDEFINED_OBJECT_COLOR;
	private static final GColor COLOR_LOCAL = GeoGebraColorConstants.LOCAL_OBJECT_COLOR;
	private static final GColor COLOR_DEFAULT = GColor.BLACK;

	private Kernel kernel;
	private Set<String> labels;
	private Set<String> locals;
	private ParserFunctions pf;
	private List<Integer[]> definedObjectsIntervals;
	private List<Integer[]> undefinedObjectsIntervals;
	private List<Integer[]> ignoreIntervals;
	private List<Integer[]> localVariableIntervals;
	private String text;
	private boolean isCasInput;

	/** Regular expression objects */
	private RegExp commandReg = RegExp
			.compile(LABEL_REGEX_STRING + "\\[(" + STRING + "|,)\\]", "g");
	private RegExp commandParamReg = RegExp.compile("<(\\p{L}\\p{M}*| |\\-)*>",
			"g");
	private RegExp assignmentReg;

	/**
	 * @param app
	 *            for getting kernel and command dictionary
	 * @param isCasInput1
	 *            whether we are coloring CAS input labels
	 */
	public ColorProvider(App app, boolean isCasInput1) {
		kernel = app.getKernel();
		setIsCasInput(isCasInput1);
		assignmentReg = createAssignmentRegExp(isCasInput);
		labels = null;
		locals = null;
		definedObjectsIntervals = new ArrayList<>();
		undefinedObjectsIntervals = new ArrayList<>();
		ignoreIntervals = new ArrayList<>();
		localVariableIntervals = new ArrayList<>();
		pf = app.getParserFunctions();
		text = "";
	}

	/**
	 * @param text1
	 *            text in we are looking for labels
	 */
	public void setText(String text1) {
		text = text1;
		if (text1.length() > TEXT_LENGHT_LIMIT) {
			return;
		}
		getIntervals();
	}

	/**
	 * Every time the text changes, setText(String) must be called
	 * 
	 * @param i
	 *            the cursor in the text
	 * @return Color
	 */
	public GColor getColor(int i) {
		for (Integer[] in : definedObjectsIntervals) {
			if (in[0] <= i && in[1] > i) {
				return COLOR_DEFINED;
			}
		}
		for (Integer[] in : localVariableIntervals) {
			if (in[0] <= i && in[1] > i) {
				return COLOR_LOCAL;
			}
		}
		if (isCasInput) {
			return COLOR_DEFAULT;
		}
		for (Integer[] in : ignoreIntervals) {
			if (in[0] <= i && in[1] > i) {
				return COLOR_DEFAULT;
			}
		}
		for (Integer[] in : undefinedObjectsIntervals) {
			if (in[0] <= i && in[1] > i) {
				return COLOR_UNDEFINED;
			}
		}
		return COLOR_DEFAULT;
	}

	/**
	 * Sets the flags for algebra or CAS input
	 * 
	 * @param isCasInput1
	 *            true if it is CAS input false if algebra input
	 */
	public void setIsCasInput(boolean isCasInput1) {
		if (isCasInput != isCasInput1) {
			isCasInput = isCasInput1;
			assignmentReg = createAssignmentRegExp(isCasInput);
		}
	}

	private static RegExp createAssignmentRegExp(boolean isCasInput) {
		return RegExp.compile("^" + WHITESPACE + LABEL_REGEX_STRING + // f -
																		// function
																		// label
				"(\\(" + WHITESPACE + "((" + LABEL_REGEX_STRING + WHITESPACE
				+ "," + WHITESPACE + ")*)" + LABEL_REGEX_STRING + WHITESPACE
				+ "\\))" + // ( x1 , x2 , x3 , ... ) - function parameters
				WHITESPACE + (!isCasInput ? "(\\:\\=|\\=)" : "(\\:\\=)")); // :=/=
																			// -
																			// assignment
																			// operator
	}

	private void getIntervals() {
		if (isCasInput) {
			labels = kernel.getConstruction().getAllLabels();
		} else {
			labels = kernel.getConstruction().getAllGeoLabels();
		}
		locals = new HashSet();
		definedObjectsIntervals.clear();
		undefinedObjectsIntervals.clear();
		ignoreIntervals.clear();
		localVariableIntervals.clear();

		MatchResult res;
		// Only for algebra input
		if (!isCasInput) {
			while ((res = commandReg.exec(text)) != null) {
				int i = res.getIndex();
				ignoreIntervals
						.add(new Integer[] { i, i + res.getGroup(1).length() });
			}
			while ((res = commandParamReg.exec(text)) != null) {
				int i = res.getIndex();
				ignoreIntervals
						.add(new Integer[] { i, i + res.getGroup(0).length() });
			}
		}

		res = assignmentReg.exec(text);
		if (res != null) {
			// It is a function assignment
			// We add the parameters to the locals set
			// so we can color them differently
			String label = res.getGroup(1);
			if (labels.contains(label)) {
				addTo(definedObjectsIntervals, 0, label.length());
			}
			String[] split = getVariables(res.getGroup(8));
			for (String var: split) {
				String trimmedVar = trimVar(var);
				locals.add(trimmedVar);
			}
		}
		getIntervalsRecursively(text, 0);

	}

	private void getIntervalsRecursively(String text1, int startIndex) {
		MyLabelParamRegExp labelParam = new MyLabelParamRegExp(text1);
		MyMatchResult res = null;
		// While we get matches against text
		while ((res = labelParam.exec()) != null) {
			String label = res.getGroup(0);
			// Params is null if we got a label
			String params = res.getGroup(1);
			// We don't color commands
			if (!res.isCommand()) {
				addToInterval(label, startIndex + res.getIndex(),
						label.length());
			}
			String[] split = getVariables(params);
			int j = startIndex + res.getIndex() + label.length();
			if (split != null) {
				for (String sub: split) {
					// For every parameter we call this function recursively
					// this way we can color inner commands and function calls
					// as sin(cos(f(x)))
					getIntervalsRecursively(sub, j);
					j += sub.length() + 1;
				}
			}
		}
	}

	private String[] getVariables(String vars) {
		return vars == null ? null : vars.split(",");
	}

	private static String trimVar(String var) {
		String ret = var;
		if (ret.charAt(0) == '(') {
			ret = ret.substring(1);
		}
		if (ret.charAt(ret.length() - 1) == ')') {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret.trim();
	}

	private static void addTo(List list, int s, int e) {
		list.add(new Integer[] { s, e });
	}

	private void addToInterval(String label, int s, int len) {
		if (locals.contains(label)) {
			addTo(localVariableIntervals, s, s + len);
		} else if (labels.contains(label)) {
			addTo(definedObjectsIntervals, s, s + len);
		} else if (!isCasInput && !pf.isReserved(label)) {
			addTo(undefinedObjectsIntervals, s, s + len);
		}
	}

	// MyMatchResult and MyLabelParamRegExp are
	// inner classes used for matching labels/functions/commands
	private static class MyMatchResult {

		int index;
		List<String> groups;
		private boolean isCommand;

		public MyMatchResult(int index, List<String> groups,
				boolean isCommand) {
			this.index = index;
			this.groups = groups;
			setCommand(isCommand);
		}

		public boolean isCommand() {
			return isCommand;
		}

		public void setCommand(boolean isCommand) {
			this.isCommand = isCommand;
		}

		public int getIndex() {
			return index;
		}

		public String getGroup(int i) {
			return groups.get(i);
		}

	}

	private static class MyLabelParamRegExp {

		RegExp regExp = RegExp.compile(LABEL_PARAM);
		String text;
		int index;

		public MyLabelParamRegExp(String text) {
			setText(text);
		}

		public MyMatchResult exec() {
			MatchResult res = regExp.exec(text);
			if (res == null) {
				return null;
			}

			String label = res.getGroup(1);
			String openingBracket = res.getGroup(8);
			List groups = new ArrayList(2);
			groups.add(label);
			MyMatchResult ret;
			int step = 0;
			String params = null;

			if (openingBracket == null) {
				// this is a label without parameters
				step = res.getIndex() + label.length();
			} else {
				// we have a label and parameters
				// we look for the closing parentheses
				int paramsStart = res.getIndex() + label.length();
				int i = paramsStart + 1;
				int nrOfBrackets = 1;
				char closingBracket = getClosingBracket(openingBracket);
				for (; i < text.length() && nrOfBrackets != 0; i++) {
					if (text.charAt(i) == openingBracket.charAt(0)) {
						nrOfBrackets++;
					} else if (text.charAt(i) == closingBracket) {
						nrOfBrackets--;
					}
				}
				params = text.substring(paramsStart, i);
				step = paramsStart + params.length();
			}
			// Set the second parameter and create return value
			groups.add(params);
			ret = new MyMatchResult(index + res.getIndex(), groups,
					"[".equals(openingBracket));

			index += step;
			text = text.substring(step);
			return ret;
		}

		public void setText(String text) {
			this.text = text;
			index = 0;
		}

		private static char getClosingBracket(String openingBracket) {
			if ("[".equals(openingBracket)) {
				return ']';
			}
			// default
			return ')';
		}

	}
}
