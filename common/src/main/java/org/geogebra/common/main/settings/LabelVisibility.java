package org.geogebra.common.main.settings;

/**
 * This enum contains the visibility options for the label
 * and it replaces the old integer indexes in the ConstructionDefaults.
 */
public enum LabelVisibility {
	NotSet(-1, null),
	Automatic(0, "Labeling.automatic"),
	AlwaysOn(1, "Labeling.on"),
	AlwaysOff(2, "Labeling.off"),
	PointsOnly(3, "Labeling.pointsOnly"),
	UseDefaults(4, null);

	private final int value;
	private final String transKey;

	LabelVisibility(int value, String transKey) {
		this.value = value;
		this.transKey = transKey;
	}

	/**
	 * Returns the label visibility option associated with the old index.
	 * This method should be removed once the label visibility options are removed
	 * from the ConstructionDefaults.
	 * @param index The old index of the visibility option.
	 * @return The label visibility associated with the old index.
	 */
	public static LabelVisibility get(int index) {
		switch (index) {
			case -1:
				return NotSet;
			case 0:
				return Automatic;
			case 1:
				return AlwaysOn;
			case 2:
				return AlwaysOff;
			case 3:
				return PointsOnly;
			case 4:
				return UseDefaults;
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * Returns the old index of the visibility options.
	 * This method should be removed once the label visibility options are removed
	 * from the ConstructionDefaults.
	 * @return The old index of the visibility option.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return translation key
	 */
	public String getTransKey() {
		return transKey;
	}
}
