/*
 * Copyright 2008 the original author or authors.
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package geogebra.common.kernel.discrete.alds;

/**
 * Sortable Object is used in all of the Sorting Classes and also in some of the
 * Data Structures, User will need to use this class in order to use this
 * package.
 * <p>
 * It needs both the object and that object's value in order to sort, if the
 * object value is not provided it uses the hashcode of the provided object.
 * <p>
 * All of the algorithms and datastructures in this package will only rely on
 * the Value and will not use the object itself.
 * 
 * @author Devender Gollapally
 * 
 */
public class SortableObject<T> {
	private final T object;
	private final int value;

	/**
	 * Constructs a Sortable Object whose value is the hash code of the provided
	 * object. It is preferred that the user provide the value, this should only
	 * be used if you are providing a custom hash code for your object.
	 * 
	 * @param object
	 */
	public SortableObject(T object) {
		this.object = object;
		this.value = object.hashCode();
	}

	/**
	 * Constructs a Sortable Object whose value is the provided value.
	 * 
	 * @param object
	 * @param value
	 */
	public SortableObject(T object, int value) {
		this.object = object;
		this.value = value;
	}

	/**
	 * Returns the Object Stored
	 * 
	 * @return Object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Returns the value of the object, which is used for comparisons and
	 * sorting. If you provided a value when creating the object that value will
	 * be used or the hashcode of that object will be used.
	 * 
	 * See the constructor.
	 * 
	 * @return int
	 */
	public int getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SortableObject)) {
			return false;
		} else {
			@SuppressWarnings("unchecked")
			SortableObject other = (SortableObject) obj;
			if (this.getValue() == other.getValue()) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public int hashCode() {
		return value + 17;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}