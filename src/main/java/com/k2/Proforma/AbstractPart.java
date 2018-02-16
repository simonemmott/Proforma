package com.k2.Proforma;

import com.k2.Expressions.expression.Expression;

/**
 * The abstract part provides the conditional part handling for parameter and string parts
 * 
 * @author simon
 *
 */
public abstract class AbstractPart implements Part {

	Expression<Boolean> conditionalExpression;
		
	@Override
	public boolean isIncluded(ProformaOutput<?> po) {
		if (conditionalExpression != null) {
			return conditionalExpression.evaluate(po);
		} else {
			return true;
		}
	}



}
