package com.soda.proyecto.saborau.dominio;

import java.io.Serializable;

public class ItemPlatoComponente implements Serializable {

	private ComponentePlato componente;

	public ItemPlatoComponente() {

	}

	public ComponentePlato getComponente() {
		return componente;
	}

	public void setComponente(ComponentePlato componente) {
		this.componente = componente;
	}

}
