package geogebra.common.gui.inputfield;

import geogebra.common.kernel.Kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Class for coloring the labels in the input bar
 * 
 * @author bencze
 */
public class IntervalProvider {

	private Kernel kernel;
	private Set<String> labels;
	private List<Integer[]> intervals;
	private List<RegExp> patterns;
	private String text;
	private boolean isCasInput;

	/**
	 * @param kernel1
	 *            kernel providing us labels from the construction
	 * @param isCasInput1
	 *            whether we are providing labels for cas input
	 */
	public IntervalProvider(Kernel kernel1, boolean isCasInput1) {
		kernel = kernel1;
		isCasInput = isCasInput1;
		labels = null;
		patterns = new ArrayList<RegExp>();
		intervals = new ArrayList<Integer[]>();
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
	 * @return true if i points to a label in the text false otherwise
	 */
	public boolean isInInterval(int i) {
		for (Integer[] in : intervals) {
			if (in[0] <= i && in[1] > i) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sets the flags for algebra or cas input
	 * 
	 * @param isCasInput1 false means it is algebra input
	 */
	public void setIsCasInput(boolean isCasInput1) {
		isCasInput = isCasInput1;
	}

	private void getIntervals() {
		Set<String> labels1 = null;
		if (isCasInput) {
			labels1 = kernel.getConstruction().getAllLabels();
		} else {
			labels1 = kernel.getConstruction().getAllGeoLabels();
		}
		// recompile patterns only, if the labels have changed
		if (!labels1.equals(labels)) {
			labels = labels1;
			compilePatterns();
		}
		intervals.clear();
		for (RegExp pattern : patterns) {
			MatchResult res;
			while ((res = pattern.exec(text)) != null) {
				int len = res.getGroup(2).length();
				int i = res.getIndex() + res.getGroup(1).length();
				intervals.add(new Integer[] { i, i + len });
				int lastIndex = pattern.getLastIndex();
				if (lastIndex > 0 && lastIndex < text.length()) {
					pattern.setLastIndex(lastIndex - 1);
				}
			}
			pattern.setLastIndex(0);
		}
	}

	private void compilePatterns() {
		patterns.clear();
		for (String label : labels) {
			// we need to escape special characters in the label to be a valid
			// regular expression
			patterns.add(RegExp.compile("(^|\\W)(" + label.replaceAll(
									"([\\[\\]\\^\\$\\|\\(\\)\\\\\\+\\*\\?\\{\\}\\=\\!])",
									"\\\\$1") + ")($|\\W)", "g"));
		}
	}

}
