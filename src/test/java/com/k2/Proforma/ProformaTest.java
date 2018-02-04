package com.k2.Proforma;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProformaTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	@Test
	public void basicTest()
    {
		
		Proforma proforma = new Proforma()
							.add("This is ", Proforma.param("name"), " template")
							.add("This is line 2")
							.add("This line has another ", Proforma.param("parameter"))
							.add("This line had two parameters ", Proforma.param("name"), " and ", Proforma.param("parameter"));
		
		proforma.set("name", "MY")
				.set("parameter", "PARAMETER");
		
		proforma.setIndent("\t");
		
		String exprected = 	"\t\t\tThis is MY template\n" + 
							"\t\t\tThis is line 2\n" + 
							"\t\t\tThis line has another PARAMETER\n" + 
							"\t\t\tThis line had two parameters MY and PARAMETER\n";
		
		StringWriter sw = new StringWriter();
		
		proforma.write(3, sw);
		
		assertEquals(exprected, sw.toString());
		
	}
	
	@Test
	public void parameterTest()
    {
		
		Parameter name = Proforma.param("name").setValue("MY");
		Parameter paramter = Proforma.param("parameter").setValue("PARAMETER");
		
		Proforma proforma = new Proforma()
							.add("This is ", name, " template")
							.add("This is line 2")
							.add("This line has another ", paramter)
							.add("This line had two parameters ", name, " and ", paramter);
				
		proforma.setIndent("\t");
		
		String exprected = 	"\t\t\tThis is MY template\n" + 
							"\t\t\tThis is line 2\n" + 
							"\t\t\tThis line has another PARAMETER\n" + 
							"\t\t\tThis line had two parameters MY and PARAMETER\n";
		
		StringWriter sw = new StringWriter();
		
		proforma.write(3, sw);
		
		assertEquals(exprected, sw.toString());
		
	}
	
	@Test
	public void proformaTest()
    {
		
		Parameter name = Proforma.param("name").setValue("MY");
		Parameter parameter = Proforma.param("parameter").setValue("PARAMETER");

		Proforma embedded = new Proforma().setIndent("\t").setEmbedded(true)
				.add("This is an embedded proforma called ", name)
				.add("With twp parameters ", name, " and ", parameter);

		Proforma proforma = new Proforma().setIndent("\t")
							.add("This proforma '", embedded, "' is embedded")
							.add("With another line");
				
		String exprected = 	"\t\t\tThis proforma 'This is an embedded proforma called MY\n" + 
							"\t\t\t\tWith twp parameters MY and PARAMETER' is embedded\n" + 
							"\t\t\tWith another line\n";
		
		StringWriter sw = new StringWriter();
		
		proforma.write(3, sw);
		System.out.println(sw.toString());
		assertEquals(exprected, sw.toString());
		
	}

	@Test
	public void callbackTest()
    {
		
		Parameter name = Proforma.param("name").setCallback(new CallBack<String>() { int i=0; public String get() {return "MY"+i++;}});
		Parameter paramter = Proforma.param("parameter").setContinuousCallback(new CallBack<String>() { int i=0; public String get() {return "PARAMETER"+i++;}});
		
		Proforma proforma = new Proforma()
							.add("This is ", name, " template")
							.add("This is line 2")
							.add("This line has another ", paramter)
							.add("This line had two parameters ", name, " and ", paramter);
				
		proforma.setIndent("\t");
		
		String exprected = 	"\t\t\tThis is MY0 template\n" + 
							"\t\t\tThis is line 2\n" + 
							"\t\t\tThis line has another PARAMETER0\n" + 
							"\t\t\tThis line had two parameters MY0 and PARAMETER1\n";
		
		StringWriter sw = new StringWriter();
		
		proforma.write(3, sw);
		
		assertEquals(exprected, sw.toString());
		
    }

	@Test
	public void callback2Test()
    {
		
		Proforma proforma = new Proforma()
							.add("This is ", Proforma.param("name"), " template")
							.add("This is line 2")
							.add("This line has another ", Proforma.param("parameter"))
							.add("This line had two parameters ", Proforma.param("name"), " and ", Proforma.param("parameter"));
		
		proforma.set("name", new CallBack<String>() { int i=0; public String get() {return "MY"+i++;}})
				.set("parameter", new CallBack<String>() { int i=0; public String get() {return "PARAMETER"+i++;}}, true);
		
		proforma.setIndent("\t");
		
		String exprected = 	"\t\t\tThis is MY0 template\n" + 
							"\t\t\tThis is line 2\n" + 
							"\t\t\tThis line has another PARAMETER0\n" + 
							"\t\t\tThis line had two parameters MY0 and PARAMETER1\n";
		
		StringWriter sw = new StringWriter();
		
		proforma.write(3, sw);
		
		assertEquals(exprected, sw.toString());
		
	}
	
	@Test
	public void outputTest() throws IOException
    {
		
		Proforma proforma = new Proforma()
							.add("This is ", Proforma.param("name"), " template")
							.add("This is line 2")
							.add("This line has another ", Proforma.param("parameter"))
							.add("This line had two parameters ", Proforma.param("name"), " and ", Proforma.param("parameter"));
		
		proforma.set("name", new CallBack<String>() { int i=0; public String get() {return "MY"+i++;}})
				.set("parameter", new CallBack<String>() { int i=0; public String get() {return "PARAMETER"+i++;}}, true);
		
		proforma.setIndent("\t");
				
		proforma.write(3, new PrintWriter(System.out)).flush();
		
		
	}
	

}
