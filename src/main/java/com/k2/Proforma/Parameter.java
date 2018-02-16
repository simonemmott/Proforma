package com.k2.Proforma;

import com.k2.Expressions.expression.ParameterExpression;

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
public class Parameter<T> extends ParameterExpression<T>{
	/**
	 * Create a parameter with the given alias
	 * 
	 * @param alias	The alias for the parameter
	 */
	Parameter(Class<T> javaType, String alias){
		super(javaType, alias);
	}
}
