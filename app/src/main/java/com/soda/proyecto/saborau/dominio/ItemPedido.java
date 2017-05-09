package com.soda.proyecto.saborau.dominio;

import java.io.Serializable;

public class ItemPedido implements Serializable {
	private int cantidad;
	private String comentarios;
	private Plato plato;
	private float precioPlato;

	public float getPrecioPlato() {
		return precioPlato;
	}

	public void setPrecioPlato(float precioPlato) {
		this.precioPlato = precioPlato;
	}

	public ItemPedido() {
		this.plato = new Plato();
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public Plato getPlato() {
		return plato;
	}

	public void setPlato(Plato plato) {
		this.plato = plato;
	}
}

