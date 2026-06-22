package org.eventb.theory.internal.core.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * General utilities.
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class GeneralUtilities {

	/**
	 * Returns a singleton set containing the given element.
	 * 
	 * @param <E>
	 *            the type of the element
	 * @param element
	 * @return a singleton set containing the element
	 */
	public static <E> Set<E> singletonSet(E element) {
		Set<E> set = new LinkedHashSet<E>();
		set.add(element);
		return set;
	}

	/**
	 * 
	 * Utility to check whether array <code>objs</code> contains all of the elements of array
	 * <code>os</code>.
	 * 
	 * @param objs
	 *            the container array of objects
	 * @param os
	 *            the array of objects
	 * @return whether <code>objs</code> contains all of <code>os</code>.
	 */
	public static boolean subset(Object[] objs, Object[] os) {
		for (Object o : os) {
			if (!contains(objs, o))
				return false;
		}
		return true;
	}

	/**
	 * A utility to check if an object is present in an array of objects. This
	 * method uses <code>Object.equals(Object)</code>
	 * <p>
	 * 
	 * @param objs
	 *            the container array of objects
	 * @param o
	 *            the object to check
	 * @return whether <code>o</code> is in <code>objs</code>
	 */
	public static boolean contains(Object[] objs, Object o) {
		for (Object obj : objs) {
			if (obj.equals(o))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns a string representation of the given list of objects.
	 * 
	 * @param list
	 *            the list of strings
	 * @return the representing string
	 */
	public static <E> String toString(List<E> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i).toString();
			if (i < list.size() - 1) {
				result += ", ";
			}
		}
		return result;
	}

}
