/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*********************************************************************************
 *  SuperWaba Virtual Machine, version 4                                         *
 *  Copyright (C) 2000-2003 Guilherme Campos Hazan <support@superwaba.com.br>    *
 *  Copyright (C) 1998, 1999 Wabasoft <www.wabasoft.com>                         *
 *  All Rights Reserved                                                          *
 *                                                                               *
 *  This library and virtual machine is free software; you can redistribute      *
 *  it and/or modify it under the terms of the Amended GNU Lesser General        *
 *  Public License distributed with this software.                               *
 *                                                                               *
 *  This library and virtual machine is distributed in the hope that it will     *
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                         *
 *                                                                               *
 *  For the purposes of the SuperWaba software we request that software using    *
 *  or linking to the SuperWaba virtual machine or its libraries display the     *
 *  following notice:                                                            *
 *                                                                               *
 *                   Created with SuperWaba                                      *
 *                  http://www.superwaba.org                                     *
 *                                                                               *
 *  Please see the software license located at SuperWabaSDK/license.txt          *
 *  for more details.                                                            *
 *                                                                               *
 *  You should have received a copy of the License along with this software;     *
 *  if not, write to                                                             *
 *                                                                               *
 *     Guilherme Campos Hazan                                                    *
 *     Av. Nossa Senhora de Copacabana 728 apto 605 - Copacabana                 *
 *     Rio de Janeiro / RJ - Brazil                                              *
 *     Cep: 22050-000                                                            *
 *     E-mail: support@superwaba.com.br                                          *
 *                                                                               *
 *********************************************************************************/

package org.concord.sensor.impl;

/**
 * A vector is an array of object references. The vector grows and shrinks
 * dynamically as objects are added and removed.
 * <p>
 * Here is an example showing a vector being used:
 *
 * <pre>
 * ...
 * Vector vec = new Vector();
 * vec.add(obj1);
 * vec.add(obj2);
 * ...
 * vec.insert(3, obj3);
 * vec.del(2);
 * if (vec.getCount() > 5)
 * ...
 * </pre>
 */
public final class Vector
{
    public static void copyArray(Object [] srcArray, int srcStart, 
            Object [] dstArray, int dstStart, int length)
    {
        int srcIndex = srcStart;
        int dstIndex = dstStart;
        for(int i=0; i<length; i++) {
            dstArray[dstIndex] = srcArray[srcIndex];
            srcIndex++;
            dstIndex++;
        }        
    }
   /** This member is public for fast access. Always use the correct methods
     * for add and remove, otherwise you'll be in trouble. */
   public Object items[];
   private int count;

   /** Constructs an empty vector. */
   public Vector()
   {
      this(8);
   }

   /**
   * Constructs an empty vector with a given initial size. The size is
   * the initial size of the vector's internal object array. The vector
   * will grow as needed when objects are added. SIZE CANNOT BE 0!
   */
   public Vector(int size)
   {
      items = new Object[size==0?1:size];
   }

   /** Constructs a vector starting with the given elements. The vector can grow after this */
   public Vector(Object []startingWith) // guich@200b4_31
   {
      if (startingWith != null)
      {
         items = startingWith;
         count = startingWith.length;
      } else items = new Object[1];
   }

   /** Adds an object to the end of the vector. */
   public void add(Object obj)
   {
      if (count < items.length)
         items[count++] = obj;
      else
         insert(count, obj);
   }

   /** Inserts an object at the given index. */
   public void insert(int index, Object obj)
   {
      if (index < 0 || index > count) index = count; // guich@200b3: check if index is valid
      if (count == items.length)
      {
         // double size of items array
         Object newItems[] = new Object[(items.length * 12) / 10 + 1];
         copyArray(items, 0, newItems, 0, count);
         items = newItems;
      }
      if (index != count)
         copyArray(items, index, items, index + 1, count - index);
      items[index] = obj;
      count++;
   }

   /** Deletes the object reference at the given index. */
   public void del(int index)
   {
      if (index != count - 1)
         copyArray(items, index + 1, items, index, count - index - 1);
      items[count - 1] = null;
      count--;
   }

   /** Deletes the object */
   public boolean del(Object obj)
   {
	   int i = find(obj);
   	if (i >= 0)
   	{
	      del(i);
	      return true;
  	   }
	   return false;
   }

   /** Returns the object at the given index. */
   public Object get(int index)
   {
      if (index >= count)
         index = items.length; // force an out of range error
      return items[index];
   }

   /** Sets the object at the given index. */
   public void set(int index, Object obj)
   {
      if (index >= count)
         index = items.length; // force an out of range error
      items[index] = obj;
   }

   /**
   * Finds the index of the given object. The list is searched using a O(n) linear
   * search through all the objects in the vector.
   */
   public int find(Object obj)
   {
      return find(obj,0);
   }
   /**
   * Finds the index of the given object. The list is searched using a O(n) linear
   * search starting in startIndex up through all the objects in the vector.
   */
   public int find(Object obj, int startIndex)
   {
      for (int i = startIndex; i < count; i++)
         if (items[i].equals(obj))
            return i;
      return -1;
   }


   /** Returns the number of objects in the vector. */
   public int getCount()
   {
      return count;
   }

   /** Converts the vector to an array of objects.
   * Because of a bug in the SuperWaba VM about zero-sized arrays,
   * if there are no elements in this vector, returns null.
   * Note that if the elements are Strings, you can cast the result to a String[] array.
   */
   public Object []toObjectArray()
   {
      if (count == 0) return null; // guich@200b2
      Object objs[];
      if (items[0] instanceof String) // guich@220_32
         objs = new String[count];
      else
         objs = new Object[count];
      if (count > 0)  // guich
         copyArray(items, 0, objs, 0, count);
      return objs;
   }

   // methods to let the vector act like a stack

   /** pushes a object. simply calls add. */ // guich@102
   public void push(Object obj)
   {
      add(obj);
   }
   /** returns the last object, removing it. returns null if no more elements. */ // guich@102
   public Object pop()
   {
      Object o=null;
      if (count > 0)
         o = items[--count];
      items[count] = null; // let gc do their work
      return o;
   }
   /** returns the last object, without removing it. returns null if no more elements. */ // guich@102
   public Object peek()
   {
      return (count > 0)?items[count-1]:null;
   }

   /** clears all elements in this vector and sets its length to 0 */
   public void clear()
   {
      for (int i =0; i < count; i++)
         items[i] = null;
      count = 0;
   }

   //// guich@200b2: ok, i hate to do this, but here they are! the methods to make the vector class compatible with JDK. This incompatibility is very annoying!
   /** same of getCount() */
   public int size()
   {
      return count;
   }
   /** same of find(Object) */
   public int indexOf(Object elem)
   {
	   return find(elem, 0);
   }
   /** same of find(Object, index) */
   public int indexOf(Object elem, int index)
   {
	   return find(elem, index);
   }
   /** same of get(index) */
   public Object elementAt(int index)
   {
      return get(index);
   }
   /** same of set(index, Object) */
   public void setElementAt(Object obj, int index)
   {
      set(index,obj);
   }
   /** same of del(index) */
   public void removeElementAt(int index)
   {
      del(index);
   }
   /** same of insert(index, Object) */
   public void insertElementAt(Object obj, int index)
   {
      insert(index,obj);
   }
   /** same of del(Object) */
   public boolean removeElement(Object obj)
   {
      return del(obj);
   }
   /** same of clear() */
   public void removeAllElements()
   {
      clear();
   }
   
}