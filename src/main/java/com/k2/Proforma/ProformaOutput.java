package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import com.k2.Expressions.ParameterEvaluator;
import com.k2.Expressions.evaluators.ParamterOrObjectEvaluator;
import com.k2.Proforma.Proforma.CarriageReturn;

/**
 * The proforma output is an implementation of the ParamterEvaluator interface and as such can provide values for the parameters
 * in a proforma.
 * 
 * This class extends the ParramterOrObjectEvaluator consequently the set values for parameters will take precedence over values
 * extracted from the object that the proforma output has been set to get it's value from by calling the with(...) method.
 * 
 * Profoma output are created automatically when any of the methods of the proforma are called that set the output parameters of the 
 * proforma
 * 
 * @author simon
 *
 * @param <E>	The type of the object that can be set on the proforma output to provide values for the proforma's parameters.
 */
public class ProformaOutput<E> extends ParamterOrObjectEvaluator<E> implements ParameterEvaluator{

	private Proforma proforma;
	private String cr = String.format("%n");
	private String indent = "  ";
	Collection<E> valueSources;
	private boolean autoFlush = false;
	
	/**
	 * Create a proforma output for the given proforma
	 * @param proforma	The proforma for which the proforma output is required
	 */
	public ProformaOutput(Proforma proforma) {
		super(null);
		this.proforma = proforma;
	}

	/**
	 * Create a proforma output for the given proforma cloning the values from an existing profoma output
	 * @param proforma	The proforma for which a profoma output is required
	 * @param po		The proforma output whose values are to be cloned into the new proforma output
	 */
	private ProformaOutput(Proforma proforma, ProformaOutput<E> po) {
		super(po.valueSource);
		this.cr = po.cr;
		this.indent = po.indent;
		this.valueSources = po.valueSources;
		this.parameterValues = po.parameterValues;
		this.proforma = proforma;
	}
	
	public ProformaOutput<E> clone(Proforma p) {
		ProformaOutput<E> po = new ProformaOutput<E>(p);
		po.cr = this.cr;
		po.indent = this.indent;
		po.valueSources = this.valueSources;
		po.parameterValues = this.parameterValues;
		po.autoFlush = this.autoFlush;
		return po;

	}

	/**
	 * Create a proforma output for the given proforma with the given collection of value sources.
	 * 
	 * When such a proforma is output the output will be repeated for each object in the collection
	 * @param proforma		The proforma defining the format of the output
	 * @param valueSources	A collection of objects to use a sources of values for the proforma
	 */
	public ProformaOutput(Proforma proforma, Collection<E> valueSources) {
		super(null);
		this.valueSources = valueSources;
		this.proforma = proforma;
	}

	/**
	 * Create a proforma output for the given proforma drawing its values from the given object
	 * @param proforma		The proforma defining the format of the output
	 * @param valueSource	The source of values for this proforma output
	 */
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
	
	/**
	 * Set the output of this proforma output to flush it output after each line
	 * @param autoFlush	Whether or not to flush the output at the end of each line 
	 * @return	This proforma output for method chaining
	 */
	public ProformaOutput<E> setAutoFlush(boolean autoFlush) {
		this.autoFlush = autoFlush;
		return this;
	}

	/**
	 * Set the output of this proforma output to flush it output after each line
	 * @return	This proforma output for method chaining
	 */
	public boolean autoFlush() {
		return autoFlush;
	}

	/**
	 * Get the indent string for this proforma output
	 * @return	The indent string that will be used by this proforma output
	 */
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
	
	/**
	 * Get the carriage return used by this proforma output
	 * @return	The string that will be used by the proforma output as the carriage return
	 */
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
	 * @param i		The indent level at which to write the output
	 * @param out	The writer on which to write the output
	 * @return	The given writer after the proforma has been written
	 * 
	 * IO Exceptions thrown by the writer are converted into the unchecked ProformaError
	 */
	public Writer write(int i, Writer out) {
		return write(i, out, this);
		
	}
	
	/**
	 * Write out the proforma for this proforma output with the specified indent, on the specified writer using the specified evaluator
	 * @param i		The indent level for which this proforma should be output
	 * @param out	The writer onto which this proforma will be written
	 * @param po		The proforma output poviding values for this proformas paramters
	 * @return		The writer having output the proforma for method chaining
	 */
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
			if (line.isIncluded(po)) {
				try {
					if (i > 0) {
						if (l>0 || !proforma.embedded()) {
							for (int j=0; j<i; j++) {
								out.write(po.getIndent());
							}
						}
					}
					
					line.write((proforma.autoIncrementIndent()) ? i+1: i, out, po);
					
					if (i >= 0) {
						if (l<proforma.getLines().size()-1 || !proforma.embedded()) {
							out.write(cr);
							try {
								if (autoFlush) out.flush();
							} catch (IOException e) {
								throw new ProformaError("Unable to flush, message {}", e, e.getMessage());
							}
						}
					}
				} catch (IOException e) {
					throw new ProformaError(e);
				}
			}
		}
		try {
			if (autoFlush) out.flush();
		} catch (IOException e) {
			throw new ProformaError("Unable to flush, message {}", e, e.getMessage());
		}
		return out;
	}


}
