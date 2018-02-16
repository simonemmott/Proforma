package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;

import com.k2.Expressions.expression.Expression;

/**
 * This class represents a static string on a proforma line
 * 
 * @author simon
 *
 */
public class StringPart extends AbstractPart implements Part {
	
	/**
	 * The static string value of this part of the proforma line
	 */
	private final String value;
	/**
	 * Create the string part for the given string
	 * @param value	The string to use for the static string part of a proforma line
	 */
	StringPart(String value) {
		this.value = value;
	}
	
	/**
	 * Create a new String part as a clone of the given string part
	 * @param clone		The string part to clone
	 */
	private StringPart(StringPart clone) {
		this.value = clone.value;
	}
	
	@Override
	public Writer write(int indent, Writer out, ProformaOutput<?> po) throws IOException {
		out.write(value);
		return out;
	}

	@Override
	public StringPart includeIf(Expression<Boolean> conditionalExpression) {
		StringPart p = new StringPart(this);
		p.conditionalExpression = conditionalExpression;
		return p;
	}

}
