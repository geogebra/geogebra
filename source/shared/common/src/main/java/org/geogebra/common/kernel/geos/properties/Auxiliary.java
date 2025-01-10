package org.geogebra.common.kernel.geos.properties;

/** enum for auxiliary state */
public enum Auxiliary {
	/** is auxiliary */
	YES_DEFAULT(true, false) {
		@Override
		public Auxiliary toggle() {
			return Auxiliary.NO_SAVE;
		}
	},
	/** is not auxiliary */
	NO_DEFAULT(false, false) {
		@Override
		public Auxiliary toggle() {
			return Auxiliary.YES_SAVE;
		}
	},
	/** is not auxiliary, needs to save to XML */
	NO_SAVE(false, true) {
		@Override
		public Auxiliary toggle() {
			return YES_DEFAULT;
		}
	},
	/** is auxiliary, needs to save to XML */
	YES_SAVE(true, true) {
		@Override
		public Auxiliary toggle() {
			return NO_DEFAULT;
		}
	};
	
	private boolean isOn;
	private boolean needsSaveToXML;

	private Auxiliary(boolean isOn, boolean needsSaveToXML) {
		this.isOn = isOn;
		this.needsSaveToXML = needsSaveToXML;
	}
	
	/**
	 * 
	 * @return true if is auxiliary
	 */
	public boolean isOn() {
		return isOn;
	}

	/**
	 * 
	 * @return true if it needs save to XML
	 */
	public boolean needsSaveToXML() {
		return needsSaveToXML;
	}

	/**
	 * 
	 * @return the opposite value
	 */
	abstract public Auxiliary toggle();
}