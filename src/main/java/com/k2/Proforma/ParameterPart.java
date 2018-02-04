package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;

/**
 * This class represents a parameterized part of a proforma line
 * 
 * @author simon
 *
 */
public class ParameterPart implements Part {
	
	/**
	 * The parameter for this part
	 */
	private Parameter param;
	
	/**
	 * Create a parameter part for the given parameter
	 * @param param	The parameter to use as a part of a proforma line
	 */
	ParameterPart(Parameter param) {
		this.param = param;
	}
	
	/**
	 * Output the value of this parameter as a string
	 */
	@Override
	public String toString() {
		return param.toString();
	}

	/**
	 * Write the value of this parameter to the given writer
	 */
	@Override
	public Writer write(int indent, Writer out) throws IOException {
		out.write(param.toString());
		return out;
	}

}
