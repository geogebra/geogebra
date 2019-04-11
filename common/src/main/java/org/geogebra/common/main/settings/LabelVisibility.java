package org.geogebra.common.main.settings;

public enum LabelVisibility {
	NotSet(-1), Automatic(0), AlwaysOn(1), AlwaysOff(2), PointsOnly(3), UseDefaults(4);

	private final int value;

	LabelVisibility(int value) {
		this.value = value;
	}

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

	public int getValue() {
		return value;
	}
}
