// GenericsNote: Converted.
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections15.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * <p>A customized implementation of <code>java.util.ArrayList</code> designed
 * to operate in a multithreaded environment where the large majority of
 * method calls are read-only, instead of structural changes.  When operating
 * in "fast" mode, read calls are non-synchronized and write calls perform the
 * following steps:</p>
 * <ul>
 * <li>Clone the existing collection
 * <li>Perform the modification on the clone
 * <li>Replace the existing collection with the (modified) clone
 * </ul>
 * <p>When first created, objects of this class default to "slow" mode, where
 * all accesses of any type are synchronized but no cloning takes place.  This
 * is appropriate for initially populating the collection, followed by a switch
 * to "fast" mode (by calling <code>setFast(true)</code>) after initialization
 * is complete.</p>
 * <p/>
 * <p><strong>NOTE</strong>: If you are creating and accessing an
 * <code>ArrayList</code> only within a single thread, you should use
 * <code>java.util.ArrayList</code> directly (with no synchronization), for
 * maximum performance.</p>
 * <p/>
 * <p><strong>NOTE</strong>: <i>This class is not cross-platform.
 * Using it may cause unexpected failures on some architectures.</i>
 * It suffers from the same problems as the double-checked locking idiom.
 * In particular, the instruction that clones the internal collection and the
 * instruction that sets the internal reference to the clone can be executed
 * or perceived out-of-order.  This means that any read operation might fail
 * unexpectedly, as it may be reading the state of the internal collection
 * before the internal collection is fully formed.
 * For more information on the double-checked locking idiom, see the
 * <a href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">
 * Double-Checked Locking Idiom Is Broken Declaration</a>.</p>
 *
 * @author Matt Hall, John Watkinson, Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 1.0
 */
public class FastArrayList <E> extends ArrayList<E> {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a an empty list.
     */
    public FastArrayList() {

        super();
        this.list = new ArrayList<E>();

    }


    /**
     * Construct an empty list with the specified capacity.
     *
     * @param capacity The initial capacity of the empty list
     */
    public FastArrayList(int capacity) {

        super();
        this.list = new ArrayList<E>(capacity);

    }


    /**
     * Construct a list containing the elements of the specified collection,
     * in the order they are returned by the collection's iterator.
     *
     * @param collection The collection whose elements initialize the contents
     *                   of this list
     */
    public FastArrayList(Collection<E> collection) {

        super();
        this.list = new ArrayList<E>(collection);

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The underlying list we are managing.
     */
    protected ArrayList<E> list = null;


    // ------------------------------------------------------------- Properties


    /**
     * Are we operating in "fast" mode?
     */
    protected boolean fast = false;


    /**
     * Returns true if this list is operating in fast mode.
     *
     * @return true if this list is operating in fast mode
     */
    public boolean getFast() {
        return (this.fast);
    }

    /**
     * Sets whether this list will operate in fast mode.
     *
     * @param fast true if the list should operate in fast mode
     */
    public void setFast(boolean fast) {
        this.fast = fast;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Appends the specified element to the end of this list.
     *
     * @param element The element to be appended
     */
    public boolean add(E element) {

        if (fast) {
            synchronized (this) {
                ArrayList<E> temp = (ArrayList<E>) list.clone();
                boolean result = temp.add(element);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.add(element));
            }
        }

    }


    /**
     * Insert the specified element at the specified position in this list,
     * and shift all remaining elements up one position.
     *
     * @param index   Index at which to insert this element
     * @param element The element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void add(int index, E element) {

        if (fast) {
            synchronized (this) {
                ArrayList<E> temp = (ArrayList<E>) list.clone();
                temp.add(index, element);
                list = temp;
            }
        } else {
            synchronized (list) {
                list.add(index, element);
            }
        }

    }


    /**
     * Append all of the elements in the specified Collection to the end
     * of this list, in the order that they are returned by the specified
     * Collection's Iterator.
     *
     * @param collection The collection to be appended
     */
    public boolean addAll(Collection<? extends E> collection) {

        if (fast) {
            synchronized (this) {
                ArrayList<E> temp = (ArrayList<E>) list.clone();
                boolean result = temp.addAll(collection);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.addAll(collection));
            }
        }

    }


    /**
     * Insert all of the elements in the specified Collection at the specified
     * position in this list, and shift any previous elements upwards as
     * needed.
     *
     * @param index      Index at which insertion takes place
     * @param collection The collection to be added
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public boolean addAll(int index, Collection<? extends E> collection) {

        if (fast) {
            synchronized (this) {
                ArrayList<E> temp = (ArrayList<E>) list.clone();
                boolean result = temp.addAll(index, collection);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.addAll(index, collection));
            }
        }

    }


    /**
     * Remove all of the elements from this list.  The list will be empty
     * after this call returns.
     *
     * @throws UnsupportedOperationException if <code>clear()</code>
     *                                       is not supported by this list
     */
    public void clear() {

        if (fast) {
            synchronized (this) {
                ArrayList<E> temp = (ArrayList<E>) list.clone();
                temp.clear();
                list = temp;
            }
        } else {
            synchronized (list) {
                list.clear();
            }
        }

    }


    /**
     * Return a shallow copy of this <code>FastArrayList</code> instance.
     * The elements themselves are not copied.
     */
    public Object clone() {

        FastArrayList<E> results = null;
        if (fast) {
            results = new FastArrayList<E>(list);
        } else {
            synchronized (list) {
                results = new FastArrayList<E>(list);
            }
        }
        results.setFast(getFast());
        return (results);

    }


    /**
     * Return <code>true</code> if this list contains the specified element.
     *
     * @param element The element to test for
     */
    public boolean contains(Object element) {

        if (fast) {
            return (list.contains(element));
        } else {
            synchronized (list) {
                return (list.contains(element));
            }
        }

    }


    /**
     * Return <code>true</code> if this list contains all of the elements
     * in the specified Collection.
     *
     * @param collection Collection whose elements are to be checked
     */
    public boolean containsAll(Collection<?> collection) {

        if (fast) {
            return (list.containsAll(collection));
        } else {
            synchronized (list) {
                return (list.containsAll(collection));
            }
        }

    }


    /**
     * Increase the capacity of this <code>ArrayList</code> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param capacity The new minimum capacity
     */
    public void ensureCapacity(int capacity) {

        if (fast) {
            synchronized (this) {
                ArrayList<E> temp = (ArrayList<E>) list.clone();
                temp.ensureCapacity(capacity);
                list = temp;
            }
        } else {
            synchronized (list) {
                list.ensureCapacity(capacity);
            }
        }

    }


    /**
     * Compare the specified object with this list for equality.  This
     * implementation uses exactly the code that is used to define the
     * list equals function in the documentation for the
     * <code>List.equals</code> method.
     *
     * @param o Object to be compared to this list
     */
    public boolean equals(Object o) {

        // Simple tests that require no synchronization
        if (o == this)
            return (true);
        else if (!(o instanceof List))
            return (false);
        List lo = (List) o;

        // Compare the sets of elements for equality
        if (fast) {
            ListIterator li1 = list.listIterator();
            ListIterator li2 = lo.listIterator();
            while (li1.hasNext() && li2.hasNext()) {
                Object o1 = li1.next();
                Object o2 = li2.next();
                if (!(o1 == null ? o2 == null : o1.equals(o2)))
                    return (false);
            }
            return (!(li1.hasNext() || li2.hasNext()));
        } else {
            synchronized (list) {
                ListIterator li1 = list.listIterator();
                ListIterator li2 = lo.listIterator();
                while (li1.hasNext() && li2.hasNext()) {
                    Object o1 = li1.next();
                    Object o2 = li2.next();
                    if (!(o1 == null ? o2 == null : o1.equals(o2)))
                        return (false);
                }
                return (!(li1.hasNext() || li2.hasNext()));
            }
        }

    }


    /**
     * Return the element at the specified position in the list.
     *
     * @param index The index of the element to return
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public E get(int index) {

        if (fast) {
            return (list.get(index));
        } else {
            synchronized (list) {
                return (list.get(index));
            }
        }

    }


    /**
     * Return the hash code value for this list.  This implementation uses
     * exactly the code that is used to define the list hash function in the
     * documentation for the <code>List.hashCode</code> method.
     */
    public int hashCode() {

        if (fast) {
            int hashCode = 1;
            java.util.Iterator i = list.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
            }
            return (hashCode);
        } else {
            synchronized (list) {
                int hashCode = 1;
                java.util.Iterator i = list.iterator();
                while (i.hasNext()) {
                    Object o = i.next();
                    hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
                }
                return (hashCode);
            }
        }

    }


    /**
     * Search for the first occurrence of the given argument, testing
     * for equality using the <code>equals()</code> method, and return
     * the corresponding index, or -1 if the object is not found.
     *
     * @param element The element to search for
     */
    public int indexOf(Object element) {

        if (fast) {
            return (list.indexOf(element));
        } else {
            synchronized (list) {
                return (list.indexOf(element));
            }
        }

    }


    /**
     * Test if this list has no elements.
     */
    public boolean isEmpty() {

        if (fast) {
            return (list.isEmpty());
        } else {
            synchronized (list) {
                return (list.isEmpty());
            }
        }

    }


    /**
     * Return an iterator over the elements in this list in proper sequence.
     * <br><br>
     * <strong>IMPLEMENTATION NOTE</strong> - If the list is operating in fast
     * mode, an Iterator is returned, and a structural modification to the
     * list is made, then the Iterator will continue over the previous contents
     * of the list (at the time that the Iterator was created), rather than
     * failing due to concurrent modifications.
     */
    public Iterator<E> iterator() {
        if (fast) {
            return new ListIter(0);
        } else {
            return list.iterator();
        }
    }


    /**
     * Search for the last occurrence of the given argument, testing
     * for equality using the <code>equals()</code> method, and return
     * the corresponding index, or -1 if the object is not found.
     *
     * @param element The element to search for
     */
    public int lastIndexOf(Object element) {

        if (fast) {
            return (list.lastIndexOf(element));
        } else {
            synchronized (list) {
                return (list.lastIndexOf(element));
            }
        }

    }


    /**
     * Return an iterator of the elements of this list, in proper sequence.
     * See the implementation note on <code>iterator()</code>.
     */
    public ListIterator<E> listIterator() {
        if (fast) {
            return new ListIter(0);
        } else {
            return list.listIterator();
        }
    }


    /**
     * Return an iterator of the elements of this list, in proper sequence,
     * starting at the specified position.
     * See the implementation note on <code>iterator()</code>.
     *
     * @param index The starting position of the iterator to return
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public ListIterator<E> listIterator(int index) {
        if (fast) {
            return new ListIter(index);
        } else {
            return list.listIterator(index);
        }
    }


    /**
     * Remove the element at the specified position in the list, and shift
     * any subsequent elements down one position.
     *
     * @param index Index of the element to be removed
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public E remove(int index) {

        if (fast) {
            synchronized (this) {
                ArrayList<E> temp = (ArrayList<E>) list.clone();
                E result = temp.remove(index);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.remove(index));
            }
        }

    }


    /**
     * Remove the first occurrence of the specified element from the list,
     * and shift any subsequent elements down one position.
     *
     * @param element Element to be removed
     */
    public boolean remove(Object element) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                boolean result = temp.remove(element);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.remove(element));
            }
        }

    }


    /**
     * Remove from this collection all of its elements that are contained
     * in the specified collection.
     *
     * @param collection Collection containing elements to be removed
     * @throws UnsupportedOperationException if this optional operation
     *                                       is not supported by this list
     */
    public boolean removeAll(Collection<?> collection) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                boolean result = temp.removeAll(collection);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.removeAll(collection));
            }
        }

    }


    /**
     * Remove from this collection all of its elements except those that are
     * contained in the specified collection.
     *
     * @param collection Collection containing elements to be retained
     * @throws UnsupportedOperationException if this optional operation
     *                                       is not supported by this list
     */
    public boolean retainAll(Collection<?> collection) {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                boolean result = temp.retainAll(collection);
                list = temp;
                return (result);
            }
        } else {
            synchronized (list) {
                return (list.retainAll(collection));
            }
        }

    }


    /**
     * Replace the element at the specified position in this list with
     * the specified element.  Returns the previous object at that position.
     * <br><br>
     * <strong>IMPLEMENTATION NOTE</strong> - This operation is specifically
     * documented to not be a structural change, so it is safe to be performed
     * without cloning.
     *
     * @param index   Index of the element to replace
     * @param element The new element to be stored
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public E set(int index, E element) {

        if (fast) {
            return (list.set(index, element));
        } else {
            synchronized (list) {
                return (list.set(index, element));
            }
        }

    }


    /**
     * Return the number of elements in this list.
     */
    public int size() {

        if (fast) {
            return (list.size());
        } else {
            synchronized (list) {
                return (list.size());
            }
        }

    }


    /**
     * Return a view of the portion of this list between fromIndex
     * (inclusive) and toIndex (exclusive).  The returned list is backed
     * by this list, so non-structural changes in the returned list are
     * reflected in this list.  The returned list supports
     * all of the optional list operations supported by this list.
     *
     * @param fromIndex The starting index of the sublist view
     * @param toIndex   The index after the end of the sublist view
     * @throws IndexOutOfBoundsException if an index is out of range
     */
    public List<E> subList(int fromIndex, int toIndex) {
        if (fast) {
            return new SubList(fromIndex, toIndex);
        } else {
            return list.subList(fromIndex, toIndex);
        }
    }


    /**
     * Return an array containing all of the elements in this list in the
     * correct order.
     */
    public Object[] toArray() {

        if (fast) {
            return (list.toArray());
        } else {
            synchronized (list) {
                return (list.toArray());
            }
        }

    }


    /**
     * Return an array containing all of the elements in this list in the
     * correct order.  The runtime type of the returned array is that of
     * the specified array.  If the list fits in the specified array, it is
     * returned therein.  Otherwise, a new array is allocated with the
     * runtime type of the specified array, and the size of this list.
     *
     * @param array Array defining the element type of the returned list
     * @throws ArrayStoreException if the runtime type of <code>array</code>
     *                             is not a supertype of the runtime type of every element in this list
     */
    public <T> T[] toArray(T[] array) {

        if (fast) {
            return (list.toArray(array));
        } else {
            synchronized (list) {
                return (list.toArray(array));
            }
        }

    }


    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("FastArrayList[");
        sb.append(list.toString());
        sb.append("]");
        return (sb.toString());

    }


    /**
     * Trim the capacity of this <code>ArrayList</code> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <code>ArrayList</code> instance.
     */
    public void trimToSize() {

        if (fast) {
            synchronized (this) {
                ArrayList temp = (ArrayList) list.clone();
                temp.trimToSize();
                list = temp;
            }
        } else {
            synchronized (list) {
                list.trimToSize();
            }
        }

    }


    private class SubList implements List<E> {

        private int first;
        private int last;
        private List<E> expected;


        public SubList(int first, int last) {
            this.first = first;
            this.last = last;
            this.expected = list;
        }

        private List<E> get(List<E> l) {
            if (list != expected) {
                throw new ConcurrentModificationException();
            }
            return l.subList(first, last);
        }

        public void clear() {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList<E> temp = (ArrayList<E>) list.clone();
                    get(temp).clear();
                    last = first;
                    list = temp;
                    expected = temp;
                }
            } else {
                synchronized (list) {
                    get(expected).clear();
                }
            }
        }

        public boolean remove(Object o) {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList temp = (ArrayList) list.clone();
                    boolean r = get(temp).remove(o);
                    if (r) last--;
                    list = temp;
                    expected = temp;
                    return r;
                }
            } else {
                synchronized (list) {
                    return get(expected).remove(o);
                }
            }
        }

        public boolean removeAll(Collection<?> o) {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList temp = (ArrayList) list.clone();
                    List sub = get(temp);
                    boolean r = sub.removeAll(o);
                    if (r) last = first + sub.size();
                    list = temp;
                    expected = temp;
                    return r;
                }
            } else {
                synchronized (list) {
                    return get(expected).removeAll(o);
                }
            }
        }

        public boolean retainAll(Collection<?> o) {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList temp = (ArrayList) list.clone();
                    List sub = get(temp);
                    boolean r = sub.retainAll(o);
                    if (r) last = first + sub.size();
                    list = temp;
                    expected = temp;
                    return r;
                }
            } else {
                synchronized (list) {
                    return get(expected).retainAll(o);
                }
            }
        }

        public int size() {
            if (fast) {
                return get(expected).size();
            } else {
                synchronized (list) {
                    return get(expected).size();
                }
            }
        }


        public boolean isEmpty() {
            if (fast) {
                return get(expected).isEmpty();
            } else {
                synchronized (list) {
                    return get(expected).isEmpty();
                }
            }
        }

        public boolean contains(Object o) {
            if (fast) {
                return get(expected).contains(o);
            } else {
                synchronized (list) {
                    return get(expected).contains(o);
                }
            }
        }

        public boolean containsAll(Collection<?> o) {
            if (fast) {
                return get(expected).containsAll(o);
            } else {
                synchronized (list) {
                    return get(expected).containsAll(o);
                }
            }
        }

        public <T> T[] toArray(T[] o) {
            if (fast) {
                return get(expected).toArray(o);
            } else {
                synchronized (list) {
                    return get(expected).toArray(o);
                }
            }
        }

        public Object[] toArray() {
            if (fast) {
                return get(expected).toArray();
            } else {
                synchronized (list) {
                    return get(expected).toArray();
                }
            }
        }


        public boolean equals(Object o) {
            if (o == this) return true;
            if (fast) {
                return get(expected).equals(o);
            } else {
                synchronized (list) {
                    return get(expected).equals(o);
                }
            }
        }

        public int hashCode() {
            if (fast) {
                return get(expected).hashCode();
            } else {
                synchronized (list) {
                    return get(expected).hashCode();
                }
            }
        }

        public boolean add(E o) {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList temp = (ArrayList) list.clone();
                    boolean r = get(temp).add(o);
                    if (r) last++;
                    list = temp;
                    expected = temp;
                    return r;
                }
            } else {
                synchronized (list) {
                    return get(expected).add(o);
                }
            }
        }

        public boolean addAll(Collection<? extends E> o) {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList temp = (ArrayList) list.clone();
                    boolean r = get(temp).addAll(o);
                    if (r) last += o.size();
                    list = temp;
                    expected = temp;
                    return r;
                }
            } else {
                synchronized (list) {
                    return get(expected).addAll(o);
                }
            }
        }

        public void add(int i, E o) {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList temp = (ArrayList) list.clone();
                    get(temp).add(i, o);
                    last++;
                    list = temp;
                    expected = temp;
                }
            } else {
                synchronized (list) {
                    get(expected).add(i, o);
                }
            }
        }

        public boolean addAll(int i, Collection<? extends E> o) {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList temp = (ArrayList) list.clone();
                    boolean r = get(temp).addAll(i, o);
                    list = temp;
                    if (r) last += o.size();
                    expected = temp;
                    return r;
                }
            } else {
                synchronized (list) {
                    return get(expected).addAll(i, o);
                }
            }
        }

        public E remove(int i) {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList temp = (ArrayList) list.clone();
                    E o = get(temp).remove(i);
                    last--;
                    list = temp;
                    expected = temp;
                    return o;
                }
            } else {
                synchronized (list) {
                    return get(expected).remove(i);
                }
            }
        }

        public E set(int i, E a) {
            if (fast) {
                synchronized (FastArrayList.this) {
                    ArrayList temp = (ArrayList) list.clone();
                    E o = get(temp).set(i, a);
                    list = temp;
                    expected = temp;
                    return o;
                }
            } else {
                synchronized (list) {
                    return get(expected).set(i, a);
                }
            }
        }


        public Iterator<E> iterator() {
            return new SubListIter(0);
        }

        public ListIterator<E> listIterator() {
            return new SubListIter(0);
        }

        public ListIterator<E> listIterator(int i) {
            return new SubListIter(i);
        }


        public E get(int i) {
            if (fast) {
                return get(expected).get(i);
            } else {
                synchronized (list) {
                    return get(expected).get(i);
                }
            }
        }

        public int indexOf(Object o) {
            if (fast) {
                return get(expected).indexOf(o);
            } else {
                synchronized (list) {
                    return get(expected).indexOf(o);
                }
            }
        }


        public int lastIndexOf(Object o) {
            if (fast) {
                return get(expected).lastIndexOf(o);
            } else {
                synchronized (list) {
                    return get(expected).lastIndexOf(o);
                }
            }
        }


        public List<E> subList(int f, int l) {
            if (list != expected) {
                throw new ConcurrentModificationException();
            }
            return new SubList(first + f, f + l);
        }


        private class SubListIter implements ListIterator<E> {

            private List<E> expected1;
            private ListIterator<E> iter;
            private int lastReturnedIndex = -1;


            public SubListIter(int i) {
                this.expected1 = list;
                this.iter = SubList.this.get(expected1).listIterator(i);
            }

            private void checkMod() {
                if (list != expected1) {
                    throw new ConcurrentModificationException();
                }
            }

            List<E> get() {
                return SubList.this.get(expected1);
            }

            public boolean hasNext() {
                checkMod();
                return iter.hasNext();
            }

            public E next() {
                checkMod();
                lastReturnedIndex = iter.nextIndex();
                return iter.next();
            }

            public boolean hasPrevious() {
                checkMod();
                return iter.hasPrevious();
            }

            public E previous() {
                checkMod();
                lastReturnedIndex = iter.previousIndex();
                return iter.previous();
            }

            public int previousIndex() {
                checkMod();
                return iter.previousIndex();
            }

            public int nextIndex() {
                checkMod();
                return iter.nextIndex();
            }

            public void remove() {
                checkMod();
                if (lastReturnedIndex < 0) {
                    throw new IllegalStateException();
                }
                get().remove(lastReturnedIndex);
                last--;
                expected1 = list;
                iter = get().listIterator(previousIndex());
                lastReturnedIndex = -1;
            }

            public void set(E o) {
                checkMod();
                if (lastReturnedIndex < 0) {
                    throw new IllegalStateException();
                }
                get().set(lastReturnedIndex, o);
                expected1 = list;
                iter = get().listIterator(previousIndex() + 1);
            }

            public void add(E o) {
                checkMod();
                int i = nextIndex();
                get().add(i, o);
                last++;
                iter = get().listIterator(i + 1);
                lastReturnedIndex = 1;
            }

        }


    }


    private class ListIter implements ListIterator<E> {

        private List<E> expected;
        private ListIterator<E> iter;
        private int lastReturnedIndex = -1;


        public ListIter(int i) {
            this.expected = list;
            this.iter = get().listIterator(i);
        }

        private void checkMod() {
            if (list != expected) {
                throw new ConcurrentModificationException();
            }
        }

        List get() {
            return expected;
        }

        public boolean hasNext() {
            checkMod();
            return iter.hasNext();
        }

        public E next() {
            checkMod();
            lastReturnedIndex = iter.nextIndex();
            return iter.next();
        }

        public boolean hasPrevious() {
            checkMod();
            return iter.hasPrevious();
        }

        public E previous() {
            checkMod();
            lastReturnedIndex = iter.previousIndex();
            return iter.previous();
        }

        public int previousIndex() {
            checkMod();
            return iter.previousIndex();
        }

        public int nextIndex() {
            checkMod();
            return iter.nextIndex();
        }

        public void remove() {
            checkMod();
            if (lastReturnedIndex < 0) {
                throw new IllegalStateException();
            }
            get().remove(lastReturnedIndex);
            expected = list;
            iter = get().listIterator(previousIndex());
            lastReturnedIndex = -1;
        }

        public void set(E o) {
            checkMod();
            if (lastReturnedIndex < 0) {
                throw new IllegalStateException();
            }
            get().set(lastReturnedIndex, o);
            expected = list;
            iter = get().listIterator(previousIndex() + 1);
        }

        public void add(E o) {
            checkMod();
            int i = nextIndex();
            get().add(i, o);
            iter = get().listIterator(i + 1);
            lastReturnedIndex = -1;
        }

    }
}
