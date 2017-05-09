package com.soda.proyecto.saborau.dataAccess;

import android.content.Context;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.soda.proyecto.saborau.dominio.Pedido;

public class PedidoDataFirebase implements PedidoData{
	public static final String FIREBASE_URL = "https://pedi-tu-almuerzo-c34ef.firebaseio.com/";
	final public static String PLATILLO_REFERENCE="Platos";
	final public static String CONTROL_REFERENCE="control";
	final public static String PEDIDOS_REFERENCE="Pedidos";

	private final Context context;
	String estadoTransaccion = "Pedido solicitado con éxito!";

	public PedidoDataFirebase(Context context){
		this.context = context;
	}

	@Override
	public String solicitarPedido(Pedido pedido) {

		Firebase.setAndroidContext(context);
		Firebase ref = new Firebase(PedidoDataFirebase.FIREBASE_URL);

		ref.child("Pedidos").push().setValue(pedido, new Firebase.CompletionListener() {
			@Override
			public void onComplete(FirebaseError firebaseError, Firebase firebase) {
				if (firebaseError != null) {
					estadoTransaccion = "Data could not be saved. " + firebaseError.getMessage();
				} else {
					estadoTransaccion = "Data saved successfully.";
				}
			}

		});

		return  estadoTransaccion;
	}

	@Override
	public void modificarPedido(Pedido pedido) {

		Firebase.setAndroidContext(context);
		Firebase ref = new Firebase(PedidoDataFirebase.FIREBASE_URL);

		ref.child("Pedidos").child(pedido.getIdPedido()).setValue(pedido);
	}

	@Override
	public void cancelarPedido(Pedido pedido) {
		Firebase.setAndroidContext(context);
		Firebase ref = new Firebase(PedidoDataFirebase.FIREBASE_URL);

		ref.child("Pedidos").child(pedido.getIdPedido()).child("estado").setValue("cancelado");
	}
}
