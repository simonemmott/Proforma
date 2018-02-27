package com.k2.Proforma;

import com.k2.Expressions.expression.K2Expression;
import com.k2.Expressions.expression.K2ParameterExpression;

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
public class Parameter<T> extends K2ParameterExpression<T>{
	/**
	 * Create a parameter with the given alias
	 * 
	 * @param alias	The alias for the parameter
	 */
	Parameter(Class<T> javaType, String alias){
		super(javaType, alias);
	}
	
	/**
	 * The conditional expression which determines whether the parameter part generated from this parameter should be included in the output
	 */
	private K2Expression<Boolean> conditionalExpression;
	
	/**
	 * Identify that the parameter part for this parameter should be conditionally included according to the given boolean expression
	 * @param conditionalExpression		The boolean expression controlling whether this parameters parameter part is included in the output
	 * @return		This parameter for method chaining
	 */
	public Parameter<T> includeIf(K2Expression<Boolean> conditionalExpression) {
		this.conditionalExpression = conditionalExpression;
		return this;
	}
	
	/**
	 * Get the boolean expression that controls whether the parameter part for this parameter should be included in the output
	 * @return	The conditional expression that determines whether the parameter should be included or not
	 */
	public K2Expression<Boolean> includeIf() {
		return conditionalExpression;
	}
}
