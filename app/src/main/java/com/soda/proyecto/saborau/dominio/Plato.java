package com.soda.proyecto.saborau.dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Plato implements Serializable {

	private String idPlato;
	private String nombrePlato;
	private boolean opcional;
	private float precioPlato;
	private TiempoDeComida tiempoDeComida;
	private Dia dia;
	private Semana semana;
	private List<ItemPlatoComponente> items;

	public Plato() {

		tiempoDeComida = new TiempoDeComida();
		dia = new Dia();
		semana = new Semana();
		items = new ArrayList<ItemPlatoComponente>();
	}

	public String getIdPlato() {
		return idPlato;
	}

	public void setIdPlato(String idPlato) {
		this.idPlato = idPlato;
	}

	public boolean isOpcional() {
		return opcional;
	}

	public void setOpcional(boolean opcional) {
		this.opcional = opcional;
	}

	public String getNombrePlato() {
		return nombrePlato;
	}

	public void setNombrePlato(String nombrePlatillo) {
		this.nombrePlato = nombrePlatillo;
	}

	public float getPrecioPlato() {
		return precioPlato;
	}

	public void setPrecioPlato(float precioPlato) {
		this.precioPlato = precioPlato;
	}

	public List<ItemPlatoComponente> getItems() {
		return items;
	}

	public void setItems(List<ItemPlatoComponente> items) {
		this.items = items;
	}

	public TiempoDeComida getTiempoDeComida() {
		return tiempoDeComida;
	}

	public void setTiempoDeComida(TiempoDeComida tiempoDeComida) {
		this.tiempoDeComida = tiempoDeComida;
	}

	public Dia getDia() {
		return dia;
	}

	public void setDia(Dia dia) {
		this.dia = dia;
	}

	public Semana getSemana() {
		return semana;
	}

	public void setSemana(Semana semana) {
		this.semana = semana;
	}
}
