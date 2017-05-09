package com.soda.proyecto.saborau.dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TiempoDeComida implements Serializable {
	private String nombreTiempoDeComida;
	private List<Plato> platos;

	public TiempoDeComida() {
		super();
		this.platos = new ArrayList<Plato>();
	}

	public String getNombreTiempoDeComida() {
		return nombreTiempoDeComida;
	}

	public void setNombreTiempoDeComida(String nombreTiempoDeComida) {
		this.nombreTiempoDeComida = nombreTiempoDeComida;
	}

	public List<Plato> getPlatos() {
		return platos;
	}

	public void setPlatos(List<Plato> platos) {
		this.platos = platos;
	}


}
