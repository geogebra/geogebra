package org.rosuda.REngine;

/** REXPFactor represents a factor in R. It is an integer vector with levels for each contained category. */
// FIXME: this is currently somehow screwed - the concept of RFactor and REXPFactor is duplicate - we need to remove this historical baggage
public class REXPFactor extends REXPInteger {
	private String[] levels;
	private RFactor factor;
	
	/** create a new factor REXP
	 *  @param ids indices (one-based!)
	 *  @param levels levels */
	public REXPFactor(int[] ids, String[] levels) {
		super(ids);
		this.levels = (levels==null)?(new String[0]):levels;
		factor = new RFactor(this.payload, this.levels, false, 1);
		attr = new REXPList(
							new RList(
									  new REXP[] {
										  new REXPString(this.levels), new REXPString("factor")
									  }, new String[] { "levels", "class" }));
	}

	/** create a new factor REXP
	 *  @param ids indices (one-based!)
	 *  @param levels levels
	 *  @param attr attributes */
	public REXPFactor(int[] ids, String[] levels, REXPList attr) {
		super(ids, attr);
		this.levels = (levels==null)?(new String[0]):levels;
		factor = new RFactor(this.payload, this.levels, false, 1);
	}
	
	/** create a new factor REXP from an existing RFactor
	 *  @param factor factor object (can be of any index base, the contents will be pulled with base 1) */
	public REXPFactor(RFactor factor) {
		super(factor.asIntegers(1));
		this.factor = factor;
		this.levels = factor.levels();
		attr = new REXPList(
							new RList(
									  new REXP[] {
										  new REXPString(this.levels), new REXPString("factor")
									  }, new String[] { "levels", "class" }));
	}
	
	/** create a new factor REXP from an existing RFactor
	 *  @param factor factor object (can be of any index base, the contents will be pulled with base 1)
	 *  @param attr attributes */
	public REXPFactor(RFactor factor, REXPList attr) {
		super(factor.asIntegers(1), attr);
		this.factor = factor;
		this.levels = factor.levels();
	}

	public boolean isFactor() { return true; }

	/** return the contents as a factor - the factor is guaranteed to have index base 1
	 *  @return the contents as a factor */
	public RFactor asFactor() {
		return factor;
	}

	public String[] asStrings() {
		return factor.asStrings();
	}

	public Object asNativeJavaObject() {
		return asStrings();
	}
	
	public String toString() {
		return super.toString()+"["+levels.length+"]";
	}
}
