package com.soda.proyecto.saborau.dataAccess;

import com.soda.proyecto.saborau.dominio.Pedido;

public interface PedidoData {
	public String solicitarPedido(Pedido pedido);
	public void modificarPedido(Pedido pedido);
	public void cancelarPedido(Pedido pedido);
}
