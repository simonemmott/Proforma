package com.k2.Proforma;

/**
 * This interface provides a mechanism to call back to get a value of a particular type
 * @author simon
 *
 * @param <T>	The type of the value to be returned by the call back
 */
public interface CallBack<T> {
	/**
	 * Get the called value
	 * @return	The called value
	 */
	T get();

}
