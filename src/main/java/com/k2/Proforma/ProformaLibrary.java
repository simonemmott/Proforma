package com.k2.Proforma;

import java.util.Collection;

public interface ProformaLibrary {
	
	public Proforma getProforma(String name);
	
	public void include(ProformaLibrary ... libraries);

	public Collection<Proforma> getAllProfomas();

}
