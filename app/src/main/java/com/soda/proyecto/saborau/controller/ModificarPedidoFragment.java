package com.soda.proyecto.saborau.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.soda.proyecto.saborau.R;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soda.proyecto.saborau.dataAccess.PedidoData;
import com.soda.proyecto.saborau.dataAccess.PedidoDataFirebase;
import com.soda.proyecto.saborau.dominio.ItemPlatoComponente;
import com.soda.proyecto.saborau.dominio.Pedido;
import com.soda.proyecto.saborau.presenter.SolicitudPresenter;

import java.util.ArrayList;

public class ModificarPedidoFragment extends Fragment {

    private Intent intent;
    private TextView tipoPlato, precio, cantidad, observaciones;
    private ListView listaIngredientes;
    private Pedido pedido;
    private int posicion;
    private DatabaseReference ref;
    private DatabaseReference mensajeRef;
    private ValueEventListener listener;
    private Button btnModificar;
    private EditText inCantidad, inObservaciones;
    private ImageView imagenPlato;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Toast.makeText(getActivity(), "Imágenes de referencia", Toast.LENGTH_SHORT).show();

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_modificar_pedido, container, false);

        tipoPlato = (TextView) view.findViewById(R.id.lbTipoPlato);
        precio = (TextView) view.findViewById(R.id.lbPrecio);
        cantidad = (TextView) view.findViewById(R.id.lbCantidad);
        observaciones = (TextView) view.findViewById(R.id.lbObservaciones);
        listaIngredientes = (ListView) view.findViewById(R.id.lvListaIngredientes);
        inCantidad = (EditText) view.findViewById(R.id.etCantidad);
        inObservaciones = (EditText) view.findViewById(R.id.etObservaciones);
        btnModificar = (Button) view.findViewById(R.id.btnModificarPedido);
        imagenPlato = (ImageView) view.findViewById(R.id.ivImagenPlato);
        ref = FirebaseDatabase.getInstance().getReference();
        mensajeRef = ref.child("Pedidos");
        pedido = (Pedido) getArguments().getSerializable("Pedido");

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarPedido(view);
            }
        });

        if(!pedido.getItems().get(0).getPlato().isOpcional()){
            tipoPlato.setText(pedido.getItems().get(0).getPlato().getNombrePlato());
        }
        else{
            tipoPlato.setText(pedido.getItems().get(0).getPlato().getNombrePlato());

        }
        precio.setText("Precio: "+pedido.getItems().get(0).getPlato().getPrecioPlato()+"");
        inCantidad.setText(pedido.getItems().get(0).getCantidad()+"");
        inObservaciones.setText(pedido.getItems().get(0).getComentarios());

        ArrayList<String> componentes = new ArrayList<String>();

        for (ItemPlatoComponente item:pedido.getItems().get(0).getPlato().getItems()) {

            componentes.add(item.getComponente().getNombreComponentePlato());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, componentes);
        listaIngredientes.setAdapter(adapter);

        return view;
    }
    public void modificarPedido(View view){
        if(inCantidad.getText().toString().equals("")){
            inCantidad.setError("Debe ingresar una cantidad");
            inCantidad.requestFocus();
        }
        else if(pedido.getEstado().compareTo("Pendiente")!= 0){
            Toast.makeText(getActivity(), "¡Lo sentimos! Este pedido no puede ser modificado", Toast.LENGTH_SHORT).show();
        }
        else{
            pedido.getItems().get(0).setCantidad(Integer.parseInt(inCantidad.getText().toString()));
            pedido.getItems().get(0).setComentarios(inObservaciones.getText().toString());
            PedidoData pedidoData = new PedidoDataFirebase(getContext());
            SolicitudPresenter modificarPedidoPresenter = new SolicitudPresenter(pedidoData);
            modificarPedidoPresenter.modificarPedido(pedido);
            Toast.makeText(getActivity(), "¡Excelente! Pedido modificado", Toast.LENGTH_SHORT).show();
            }


        }
    }
