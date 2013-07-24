package geogebra.common.gui.inputfield;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * @author bencze
 * Class for coloring the labels in the input bar
 */
public class IntervalProvider {
	
	private Kernel kernel; 
	private String[] labels;
	private List<Integer[]> intervals;	
	private List<RegExp> patterns;
	
	/**
	 * @param kernel1 kernel providing us labels from the construction
	 */
	public IntervalProvider(Kernel kernel1) {
		kernel = kernel1;
		labels = null;
		patterns = new ArrayList<RegExp>();
		intervals = new ArrayList<Integer[]>();
	}
	
	private List<Integer[]> getIntervals(String text) {
		String[] labels1 = kernel.getConstruction().getAllLabels().toArray(new String[0]);
		if (labels != null) {
			Arrays.sort(labels);
		}
		Arrays.sort(labels1);
		if (!Arrays.equals(labels, labels1)) {
			labels = labels1;
			compilePatterns();
		}
		intervals.clear();
		for (RegExp pattern : patterns) {
			MatchResult res = pattern.exec(text);
			if (res != null) {
				int len = res.getGroup(2).length();
				int i = text.indexOf(res.getGroup(2));
				intervals.add(new Integer[] {i, i + len});
			}
		}
		return intervals;
	}
	
	private void compilePatterns() {
		App.debug("compiling patterns");
		patterns.clear();
		for (String label : labels) {
			patterns.add(RegExp.compile("(^|\\W)(" + label + ")($|\\W)"));
		}
	}
	
	/**
	 * @param i the cursor in the text
	 * @param text text
	 * @return true if i points to a label in the text false otherwise
	 */
	public boolean isInInterval(int i, String text) {
		List<Integer[]> inter = getIntervals(text);
		for (Integer[] in : inter) {
			if (in[0] <= i && in[1] > i) {
				return true;
			}
		}
		return false;
	}
}
