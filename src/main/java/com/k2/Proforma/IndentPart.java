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
public class IndentPart extends AbstractPart implements Part {
	
	/**
	 * Create the string part for the given string
	 * @param value	The string to use for the static string part of a proforma line
	 */
	IndentPart() {
	}
	
	/**
	 * Create a new String part as a clone of the given string part
	 * @param clone		The string part to clone
	 */
	private IndentPart(IndentPart clone) {
	}
	
	@Override
	public Writer write(int indent, Writer out, ProformaOutput<?> po) throws IOException {
		out.write(po.getIndent());
		if (po.autoFlush()) out.flush();
		return out;
	}

	@Override
	public IndentPart includeIf(Expression<Boolean> conditionalExpression) {
		IndentPart p = new IndentPart(this);
		p.conditionalExpression = conditionalExpression;
		return p;
	}

}
