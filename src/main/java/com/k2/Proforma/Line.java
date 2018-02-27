package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.k2.Expressions.expression.K2Expression;
import com.k2.Util.StringUtil;

/**
 * This class represents a line of proforma parts
 * 
 * @author simon
 *
 */
public class Line {
	
	/**
	 * The parts on this line
	 */
	List<Part> parts = new ArrayList<Part>();
	
	/**
	 * Create a line converting the given objects into parts
	 * 
	 * Parameters in the line are converted to parameter parts
	 * Proformas implement the part interface so are added to the line directs
	 * Other objects are converted to String parts using the StringUtil.toString(...) methods.
	 * 
	 * @param parts
	 */
	Line(Object ...parts) {
		for (Object part : parts) {
			if (part instanceof Part) {
				add((Part)part);
			} else if (part instanceof Parameter) {
				add((Parameter<?>)part);
			} else {
				add(StringUtil.toString(part));
			}
		}		
	}
	
	/**
	 * Add a part to the line
	 * @param part	The part to add to the line
	 * @return	This line for method chaining
	 */
	public Line add(Part part) {
		parts.add(part);
		return this;
	}

	/**
	 * Add the given string to the line as a StringPart
	 * @param string		The string to add to the line
	 * @return	This line for method chaining
	 */
	Line add(String string) {
		parts.add(new StringPart(string));
		return this;
	}
	
	/**
	 * Add the given parameter to the line as a ParameterPart
	 * @param param	The parameter to add to the line
	 * @return	This line for method chaining
	 */
	Line add(Parameter<?> param) {
		parts.add(new ParameterPart(param));
		return this;
	}

	/**
	 * Write the parts of this line to the given writer if there conditional expression implies that they should be included
	 * @param indent		The amount of indent to prepend to each new line
	 * @param out		The writer on which to output the line
	 * @param po			The proforma output providing values for the parameters 
	 * @throws IOException	If there is an error writing to the writer
	 */
	public void write(int indent, Writer out, ProformaOutput<?> po) throws IOException {
		for (Part part : parts) {
			if (part.isIncluded(po)) part.write(indent, out, po);
		}
		if (po.autoFlush()) out.flush();
	}
	
	/**
	 * The boolean expression that defines whether this line should be included in the output
	 * If this field is null then the line is included in the output
	 */
	K2Expression<Boolean> conditionalExpression;
	
	/**
	 * This method set the conditional expression which determines whether this line should be included in the output
	 * @param conditionalExpression		The conditional expression that determines whether this line should be included in the output
	 * @return	This line for method chaining
	 */
	public Line includeIf(K2Expression<Boolean> conditionalExpression) {
		this.conditionalExpression = conditionalExpression;
		return this;
	}

	/**
	 * Evaluate using the implementation of Evaluator whether or not this line should be included in the output
	 * @param po		The Proforma output (implements the Evaluator interface) to use to evaluate the conditional expression
	 * @return		True if this line should be included in the output
	 */
	public boolean isIncluded(ProformaOutput<?> po) {
		if (conditionalExpression != null) {
			return conditionalExpression.evaluate(po);
		} else {
			return true;
		}
	}


}
