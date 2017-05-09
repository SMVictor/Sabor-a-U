package com.soda.proyecto.saborau.dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {
	private String idPedido;
	private String estadoEntrega;
	private String fechaPedido;
	private List<ItemPedido> items;
	private UsuarioServicio usuario;

	public Pedido() {
		this.items = new ArrayList<ItemPedido>();
		this.usuario = new UsuarioServicio();
	}

	public String getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(String idPedido) {
		this.idPedido = idPedido;
	}

	public List<ItemPedido> getItems() {
		return items;
	}

	public void setItems(List<ItemPedido> items) {
		this.items = items;
	}

	public String getEstado() {
		return estadoEntrega;
	}

	public void setEstado(String estadoEntrega) {
		this.estadoEntrega = estadoEntrega;
	}

	public String getFechaPedido() {
		return fechaPedido;
	}

	public void setFechaPedido(String fechaPedido) {
		this.fechaPedido = fechaPedido;
	}

	public UsuarioServicio getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioServicio usuario) {
		this.usuario = usuario;
	}
}
