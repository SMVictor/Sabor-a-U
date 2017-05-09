package com.soda.proyecto.saborau.dominio;


import java.io.Serializable;

public class ComponentePlato implements Serializable {

	private String nombreComponentePlato;

	public ComponentePlato() {
	}

	public String getNombreComponentePlato() {
		return nombreComponentePlato;
	}

	public void setNombreComponentePlato(String nombreComponentePlato) {
		this.nombreComponentePlato = nombreComponentePlato;
	}

}
