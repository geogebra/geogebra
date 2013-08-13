package geogebra.common.gui.inputfield;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;

/**
 * Class for coloring the labels in the input bar
 * 
 * @author bencze
 */
public class ColorProvider {
	
	private Kernel kernel;
	private Set<String> labels;
	private List<Integer[]> definedObjectsIntervals;
	private List<Integer[]> undefinedObjectsIntervals;
	private List<Integer[]> ignoreIntervals;
	private SplitResult localVariables; 
	private String text;
	private boolean isCasInput;
	
	/* Regular expression object matching any label and function */
	private RegExp labelReg = RegExp.compile(LABEL_REGEX_STRING + "(\\((" + WS + LABEL_REGEX_STRING + WS + ",)*" + WS + "(" + LABEL_REGEX_STRING + WS + ")?\\))?", "g");
	
	/* Regular expression object matching commands */
	private RegExp commandReg = RegExp.compile(LABEL_REGEX_STRING + "\\[(\\P{M}\\p{M}*)*\\]", "g");
	
	/* Regular expression object matching command parameters */
	private RegExp commandParamReg = RegExp.compile("<(\\p{L}\\p{M}*| |\\-)*>", "g");
	
	/* Regular expression object for splitting variable names */
	private RegExp splitter = RegExp.compile("(\\(" + WS + ")|(" + WS + "," + WS + ")|(" + WS + "\\))");
	
	/* Regular expression string matching a full label */
	private static String LABEL_REGEX_STRING = "((\\p{L}\\p{M}*)(\\p{L}\\p{M}*|\\'|\\p{Nd})*(\\_\\{+(\\P{M}\\p{M}*)+\\}|\\_(\\P{M}\\p{M})?)?(\\p{L}\\p{M}|\\'|\\p{Nd})*)";
	
	/* Regular expression string matching any number of whitespace */
	private static String WS = "((\\p{Z})*)";
	
	/* Defined object color */
	private static GColor COLOR_DEFINED = GeoGebraColorConstants.DEFINED_OBJECT_COLOR;
	
	/* Undefined object color */
	private static GColor COLOR_UNDEFINED = GeoGebraColorConstants.UNDEFINED_OBJECT_COLOR;
	
	/* Default color */
	private static GColor COLOR_DEFAULT = GeoGebraColorConstants.BLACK;

	/**
	 * @param app
	 *            for getting kernel and command dictionary
	 * @param isCasInput1
	 *            whether we are providing labels for cas input
	 */
	public ColorProvider(App app, boolean isCasInput1) {
		kernel = app.getKernel();
		isCasInput = isCasInput1;
		labels = null;
		definedObjectsIntervals = new ArrayList<Integer[]>();
		undefinedObjectsIntervals = new ArrayList<Integer[]>();
		ignoreIntervals = new ArrayList<Integer[]>();
		localVariables = null;
		text = "";
	}

	/**
	 * @param text1
	 *            text in we are looking for labels
	 */
	public void setText(String text1) {
		text = text1;
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
		isCasInput = isCasInput1;
	}

	private void getIntervals() {
		if (isCasInput) {
			labels = kernel.getConstruction().getAllLabels();
		} else {
			labels = kernel.getConstruction().getAllGeoLabels();
		}
		definedObjectsIntervals.clear();
		undefinedObjectsIntervals.clear();
		ignoreIntervals.clear();
		localVariables = null;
		
		MatchResult res;
		/* only for algebra input */
		if (!isCasInput) {
			while ((res = commandReg.exec(text)) != null) {
				int i = res.getIndex();
				ignoreIntervals.add(new Integer[] { i,
						i + res.getGroup(1).length() });
			}
			while ((res = commandParamReg.exec(text)) != null) {
				int i = res.getIndex();
				ignoreIntervals.add(new Integer[] { i,
						i + res.getGroup(0).length() });
			}
		}
		boolean isAssignment = (text.contains(":=") || (!isCasInput && text.contains("=")));
		
		while ((res = labelReg.exec(text)) != null) {
			String label = res.getGroup(1);
			String labelvar = res.getGroup(0);
			String vars = res.getGroup(8);
			if (isAssignment && localVariables == null && vars != null) {
				localVariables = getVariables(vars);
				/* set isAssignment to false so we don't fall in if again */
				isAssignment = false;
				for (int i = 0; i < localVariables.length(); i++) {
					labels.add(localVariables.get(i));
				}
			}
			int i = res.getIndex();
			int len = labelvar.length();
			if (labels.contains(label)) {
				definedObjectsIntervals.add(new Integer[] {i, i + len} );
			} else if (!isCasInput){
				/* we only color undefined objects in algebra input */
				undefinedObjectsIntervals.add(new Integer[] {i, i + len});				
			}
		}
	}
	
	private SplitResult getVariables(String vars) {
		return vars == null ? null : splitter.split(vars);
	}
}
