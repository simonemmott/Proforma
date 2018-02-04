package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a line of proforma parts
 * 
 * @author simon
 *
 */
public class Line {
	List<Part> parts = new ArrayList<Part>();
	
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
	Line add(Parameter param) {
		parts.add(new ParameterPart(param));
		return this;
	}

	/**
	 * Output the parts of this line as a string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Part part : parts) {
			sb.append(part.toString());
		}
		return sb.toString();
	}

	/**
	 * Write the parts of this line to the given writer
	 * @param indent		The amount of indent to prepend to each new line
	 * @param out		The writer on which to output the line
	 * @throws IOException	If there is an error writing to the writer
	 */
	public void write(int indent, Writer out) throws IOException {
		for (Part part : parts) {
			part.write(indent, out);
		}
	}

}
