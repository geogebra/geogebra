package geogebra.common.gui.inputfield;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bencze
 * Class for coloring the labels in the input bar
 */
public class IntervalProvider {
	
	private Kernel kernel; 
	private String[] labels;
	private List<Integer[]> intervals;	
	private List<Pattern> patterns;
	
	/**
	 * @param kernel1 kernel providing us labels from the construction
	 */
	public IntervalProvider(Kernel kernel1) {
		kernel = kernel1;
		labels = null;
		patterns = new ArrayList<Pattern>();
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
		for (Pattern pattern : patterns) {
			Matcher m = pattern.matcher(text);
			if (m.find()) {
				intervals.add(new Integer[] {m.start(2), m.end(2)});
			}
		}
		return intervals;
	}
	
	private void compilePatterns() {
		App.debug("compiling patterns");
		patterns.clear();
		for (String label : labels) {
			patterns.add(Pattern.compile("(^|\\W)(" + label + ")($|\\W)"));
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
