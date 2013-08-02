package geogebra.common.gui.inputfield;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

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
public class ColorProvider {
	
	private Kernel kernel;
	private Set<String> labels;
	private List<Integer[]> definedObjectsIntervals;
	private List<Integer[]> undefinedObjectsIntervals;
	private List<Integer[]> ignoreIntervals;
	private String text;
	private boolean isCasInput;
	private RegExp labelReg = RegExp.compile("(\\p{L}\\p{M}*)(\\p{L}\\p{M}*|\\'|\\p{Nd})*(\\_\\{+(\\P{M}\\p{M}*)+\\}|\\_(\\P{M}\\p{M})?)?(\\p{L}\\p{M}|\\'|\\p{Nd})*", "g");
	private RegExp commandReg = RegExp.compile("((\\p{L}\\p{M}*)*)\\[(\\P{M}\\p{M}*)*\\]", "g");
	private RegExp commandParamReg = RegExp.compile("<(\\p{L}\\p{M}*| |\\-)*>", "g");
	
	// defined object color = blue
	private static GColor COLOR_DEFINED = GColor.BLUE;
		
	// undefined object color = orange
	private static GColor COLOR_UNDEFINED = GColor.GRAY;
	
	// default color
	private static GColor COLOR_DEFAULT = GColor.BLACK;

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
		for (Integer[] in : ignoreIntervals) {
			if (in[0] <= i && in[1] > i) {
				return COLOR_DEFAULT;
			}
		}
		for (Integer[] in : definedObjectsIntervals) {
			if (in[0] <= i && in[1] > i) {
				return COLOR_DEFINED;
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
		
		MatchResult res;
		while ((res = commandReg.exec(text)) != null) {
			int i = res.getIndex();
			ignoreIntervals.add(new Integer[] {i, i + res.getGroup(1).length()} );
		}
		while ((res = commandParamReg.exec(text)) != null) {
			int i = res.getIndex();
			ignoreIntervals.add(new Integer[] {i, i + res.getGroup(0).length()} );
		}
		
		while ((res = labelReg.exec(text)) != null) {
			String match = res.getGroup(0);
			
			int i = res.getIndex();
			int len = match.length();
			if (labels.contains(match)) {
				definedObjectsIntervals.add(new Integer[] {i, i + len} );
			} else {
				undefinedObjectsIntervals.add(new Integer[] {i, i + len});				
			}
		}
	}
}
