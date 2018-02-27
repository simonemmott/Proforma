package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;

import com.k2.Expressions.expression.K2Expression;

/**
 * This interface defines parts of a proforma line
 * 
 * @author simon
 *
 */
public interface Part {
	/**
	 * Output the part as a String converting any parameters to their parameter value
	 * @return	The part as a string
	 */
	public String toString();
	
	/**
	 * This method sets the boolean expression that defines whether the part should be included in the output
	 * @param conditionalExpression		The boolean expression controlling whether or not this part should be included in the output
	 * @return		A clone of this part with it's output controlled by the given boolean expression
	 */
	public Part includeIf(K2Expression<Boolean> conditionalExpression);
	
	/**
	 * Evaluate using the given ProformaOutput implementing Evaluator whther or not this part should be included in the output
	 * @param po		The Proforma output evaluating the values for this profoma
	 * @return		True if this part should be included in the output
	 */
	public boolean isIncluded(ProformaOutput<?> po);
	
	/**
	 * Write the part to the given writer
	 * @param indent		The amount of indent to prepend to proforma parts
	 * @param out		The writer on which to write the part
	 * @param po			The proforma output that will supply values for the proformas parameters
	 * @return		The output writer for method chaining
	 * @throws IOException	If there is an error writing to the writer
	 */
	public Writer write(int indent, Writer out, ProformaOutput<?> po) throws IOException;

}
