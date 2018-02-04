package com.k2.Proforma;

/**
 * This class represents a parameterized String value
 * 
 * The value of the parameter can be set to a literal string value or can be set to the value returned from a CallBack of type string.
 * 
 * The call back can be called once to set the parameter and the returned value reused each time the parameter value is subsequently 
 * requested or the call back can be called each time the parameter value is requested
 * 
 * @author simon
 *
 */
public class Parameter {
	private String alias;
	private String value;
	private CallBack<String> callBack;
	private boolean continuousCallback = false;
	
	/**
	 * Create a parameter with the given alias
	 * 
	 * @param alias	The alias for the parameter
	 */
	Parameter(String alias){
		this.alias = alias;
	}
	
	/**
	 * Get the alias of the parameter
	 * @return	The alias of the parameter
	 */
	public String getAlias() { return alias; }
	
	/**
	 * Set the value of the parameter to the given literal value
	 * @param value	The literal value for the parameter
	 * @return	This parameter for method chaining
	 */
	Parameter setValue(String value) {
		this.value = value;
		return this;
	}
	
	/**
	 * set this parameter to get its value from the given call back the first time the paramter value is requested
	 * 
	 * @param callBack	The string callback from which to get the value for this parameter
	 * @return	This parameter for method chaining
	 */
	Parameter setCallback(CallBack<String> callBack) {
		this.callBack = callBack;
		return this;
	}
	
	/**
	 * set this parameter to get its value from the given call back each time the parameter value is requested
	 * @param callBack	The call back from which to get this parameters value
	 * @return	This parameter for method chaining
	 */
	Parameter setContinuousCallback(CallBack<String> callBack) {
		this.callBack = callBack;
		this.continuousCallback = true;
		return this;
	}
	
	/**
	 * Get the value for this parameter
	 */
	@Override
	public String toString() {
		if (continuousCallback) return callBack.get();
		if (value == null) {
			value = callBack.get();
		}
		return value;
	}

}
