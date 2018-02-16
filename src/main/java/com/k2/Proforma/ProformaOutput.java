package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.k2.Expressions.ParameterEvaluator;
import com.k2.Expressions.evaluators.ParamterOrObjectEvaluator;
import com.k2.Expressions.evaluators.SimpleParameterEvaluator;
import com.k2.Expressions.expression.CurrentTime;
import com.k2.Expressions.expression.ParameterExpression;
import com.k2.Expressions.predicate.Predicate;
import com.k2.Proforma.Proforma.CarriageReturn;
import com.k2.Util.CallBack;
import com.k2.Util.ObjectUtil;
import com.k2.Util.tuple.Pair;

public class ProformaOutput<E> extends ParamterOrObjectEvaluator<E> implements ParameterEvaluator{

	private Proforma proforma;
	private String cr = String.format("%n");
	private String indent = "  ";
	Collection<E> valueSources;
	
	public ProformaOutput(Proforma proforma) {
		super(null);
		this.proforma = proforma;
	}

	public ProformaOutput(Proforma proforma, Collection<E> valueSources) {
		super(null);
		this.valueSources = valueSources;
		this.proforma = proforma;
	}

	@SuppressWarnings("unchecked")
	public ProformaOutput(Proforma proforma, E valueSource) {
		super(valueSource);
		if (valueSource instanceof Collection) {
			valueSources = (Collection<E>)valueSource;
		}
		this.proforma = proforma;
	}

	/**
	 * Set the string to be repeated for the indent.
	 * 
	 * The default is '  ' (two spaces)
	 * @param indent		The string to be repeated for the indent
	 * @return	This proforma for method chaining
	 */
	public ProformaOutput<E> setIndent(String indent) {
		this.indent = indent;
		return this;
	}

	public String getIndent() {
		return indent;
	}


	/**
	 * Set the carriage return to the given string
	 * @param cr		The string to use as the carriage return
	 * @return		This proforma for method chaining
	 */
	public ProformaOutput<E> setCarriageReturn(String cr) {
		this.cr = cr;
		return this;
	}
	
	public String getCarriageReturn() {
		return cr;
	}

	/**
	 * Set the carriage return to the given carriage return format
	 * @param cr		The carriage return to use
	 * @return		This proforma for method chaining
	 */
	public ProformaOutput<E> setCarriageReturn(CarriageReturn cr) {
		this.cr = cr.cr();
		return this;
	}
	
	@Override
	public <T> ProformaOutput<E> set(Class<T> cls, String alias, T value) {
		super.set(cls, alias, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProformaOutput<E> with(Object source) {
		super.with((E)source);
		return this;
	}

	/**
	 * This method writes the proforma out to the output writer starting with a 0 indent
	 * @param out	The output writer
	 * @return		The output writer for method chaining
	 */
	public Writer write(Writer out) {
		return write(0, out);
	}
	/**
	 * Write the proforma out onto the given writer with the given indent
	 * 
	 * IO Exceptions thrown by the writer are converted into the unchecked ProformaError
	 */
	public Writer write(int i, Writer out) {
		return write(i, out, this);
		
	}

	public Writer write(int i, Writer out, ProformaOutput<?> po) {
		
		if (valueSources != null) {
			ProformaOutput<E> rPo = new ProformaOutput<E>(proforma).setIndent(indent).setCarriageReturn(cr);
			for (E source : valueSources) {
				((ProformaOutput<E>) rPo.with(source)).write(i, out);
			}
			return out;
		}

		for (int l=0; l<proforma.getLines().size(); l++) {
			Line line = proforma.getLines().get(l);
			try {
				if (i > 0) {
					if (l>0 || !proforma.embedded()) {
						for (int j=0; j<i; j++) {
							out.write(indent);
						}
					}
				}

				line.write((proforma.autoIncrementIndent()) ? i+1: i, out, po);

				if (i >= 0) {
					if (l<proforma.getLines().size()-1 || !proforma.embedded()) {
						out.write(cr);
					}
				}
			} catch (IOException e) {
				throw new ProformaError(e);
			}
		}
		return out;
	}


}
