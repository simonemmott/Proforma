package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import com.k2.Expressions.expression.Expression;
import com.k2.Util.StringUtil;

/**
 * This class represents a parameterized part of a proforma line
 * 
 * @author simon
 *
 */
public class ParameterPart extends AbstractPart implements Part {
	
	/**
	 * The parameter for this part
	 */
	private Parameter<?> param;
	
	/**
	 * Create a parameter part for the given parameter
	 * @param param	The parameter to use as a part of a proforma line
	 */
	ParameterPart(Parameter<?> param) {
		this.param = param;
		this.conditionalExpression = param.includeIf();
	}
	
	/**
	 * Create a parameter part as a clone of the given parameter part
	 * @param clone		The parameter part to clone into the created parameter part
	 */
	private ParameterPart(ParameterPart clone) {
		this.param = clone.param;
		this.conditionalExpression = clone.conditionalExpression;
	}
	
	/**
	 * Write the value of this parameter to the given writer
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Writer write(int indent, Writer out, ProformaOutput po) throws IOException {
		Object value = po.valueOf(param);
		if (value instanceof Collection) {
			Collection<?> c = (Collection<?>)value;
			Iterator<?> i = c.iterator();
			while (i.hasNext()) {
				out.write(StringUtil.toString(i.next()));
			}
		} else {
			out.write(StringUtil.toString(value));
		}
		return out;
	}

	@Override
	public ParameterPart includeIf(Expression<Boolean> conditionalExpression) {
		ParameterPart p = new ParameterPart(this);
		p.conditionalExpression = conditionalExpression;
		return p;
	}

}
