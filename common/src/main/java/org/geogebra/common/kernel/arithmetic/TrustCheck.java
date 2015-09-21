package org.geogebra.common.kernel.arithmetic;

public class TrustCheck {
	/** true if the expression contains only segments in even power */
	private boolean trustable = false;
	/** true if the expression contains / or * of segments */
	private boolean halfTrustable = false;

	/**
	 * @return trustable
	 */
	public boolean getTrustable() {
		return this.trustable;
	}

	/**
	 * @return halfTrustable
	 */
	public boolean getHalfTrustable() {
		return halfTrustable;
	}

	/**
	 * @param trustable
	 *            - true if expression is trustable
	 */
	public void setTrustable(boolean trustable) {
		this.trustable = trustable;
	}

	/**
	 * @param halfTrustable
	 *            - true if expression is half trustable
	 */
	public void setHalfTrustable(boolean halfTrustable) {
		this.halfTrustable = halfTrustable;
	}
}
