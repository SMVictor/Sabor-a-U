package com.soda.proyecto.saborau.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soda.proyecto.saborau.R;
import com.soda.proyecto.saborau.dataAccess.PedidoData;
import com.soda.proyecto.saborau.dataAccess.PedidoDataFirebase;
import com.soda.proyecto.saborau.dominio.ItemPedido;
import com.soda.proyecto.saborau.dominio.ItemPlatoComponente;
import com.soda.proyecto.saborau.dominio.Pedido;
import com.soda.proyecto.saborau.dominio.Plato;
import com.soda.proyecto.saborau.dominio.UsuarioServicio;
import com.soda.proyecto.saborau.presenter.SolicitudPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class SolicitarServicioPlatoFragment extends Fragment {

    private Intent intent;
    private boolean tipoPlatoSeleccionado;
    private TextView tipoPlato, precio, cantidad, observaciones;
    ListView listaIngredientes;
    private Plato plato;
    private int posicion;
    private DatabaseReference ref;
    private DatabaseReference mensajeRef;
    private ValueEventListener listener;
    private Button btnSolicitar;
    private String platoCorrespondiente;
    private EditText inCantidad, inObservaciones;
    private SolicitudPresenter presenter;
    private SharedPreferences pref;
    private ImageView ivImagenPlato;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        tipoPlatoSeleccionado = getArguments().getBoolean("tipoPlatoSeleccionado");
        platoCorrespondiente = getArguments().getString("platoCorrespondiente");

        View view=inflater.inflate(R.layout.fragment_solicitar_servicio_plato, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("UsuarioActual", 0); // 0 - for private mode

        tipoPlato = (TextView) view.findViewById(R.id.lbTipoPlato);
        precio = (TextView) view.findViewById(R.id.lbPrecio);
        cantidad = (TextView) view.findViewById(R.id.lbCantidad);
        observaciones = (TextView) view.findViewById(R.id.lbObservaciones);
        listaIngredientes = (ListView) view.findViewById(R.id.lvListaIngredientes);
        inCantidad = (EditText) view.findViewById(R.id.etCantidad);
        inObservaciones = (EditText) view.findViewById(R.id.etObservaciones);
        btnSolicitar = (Button) view.findViewById(R.id.btnSolicitar);
        ref = FirebaseDatabase.getInstance().getReference();
        mensajeRef = ref.child("Platos");
        ivImagenPlato = (ImageView) view.findViewById(R.id.ivImagenPlato);

        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarPedido(plato);
            }
        });

        listener = mensajeRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> ite = dataSnapshot.getChildren().iterator();

                while(ite.hasNext()){

                    DataSnapshot data = ite.next();
                    Plato platoNuevo  = data.getValue(Plato.class);
                    platoNuevo.setIdPlato(data.getKey());

                    if(platoNuevo.getIdPlato().equals(platoCorrespondiente)){

                        plato = new Plato();

                        plato = platoNuevo;
                        tipoPlato.setText(""+plato.getNombrePlato());
                        precio.setText("Precio: "+plato.getPrecioPlato());
                        try
                        {
                            Glide.with(getContext()).load(plato.getImagenPlato()).into((ImageView) ivImagenPlato);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        ArrayList<String> componentes = new ArrayList<String>();

                        for (ItemPlatoComponente item:plato.getItems()) {

                            componentes.add(item.getComponente().getNombreComponentePlato());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, componentes);
                        listaIngredientes.setAdapter(adapter);
                    }
                }
                if(plato == null){
                    Toast.makeText(getActivity(), "No se ha podido contactar con el catálogo de platos", Toast.LENGTH_LONG).show();
                    platoNoEncontrado(getView());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(), "No se ha podido contactar con el catálogo de platos", Toast.LENGTH_LONG).show();
                platoNoEncontrado(getView());
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void solicitarPedido(Plato plato){

        mensajeRef.removeEventListener(listener);

        PedidoData solicitudAlimentacion = new PedidoDataFirebase(getActivity());
        presenter = new SolicitudPresenter(solicitudAlimentacion);

        String cantidad = inCantidad.getText().toString().trim();
        String comentarios = inObservaciones.getText().toString();

        if(cantidad.equalsIgnoreCase("")){
            inCantidad.setError("Debe ingresar una cantidad");
            inCantidad.requestFocus();

        }
        else{
            UsuarioServicio user = new UsuarioServicio();
            user.setCorreo(pref.getString("correoUsuario", null)); // getting String);

            Pedido pedido = new Pedido();
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setCantidad(Integer.parseInt(inCantidad.getText().toString()));
            itemPedido.setComentarios(inObservaciones.getText().toString());
            itemPedido.setPlato(plato);
            itemPedido.setPrecioPlato(plato.getPrecioPlato());
            pedido.getItems().add(itemPedido);
            pedido.setEstado("Pendiente");
            pedido.setUsuario(user);
            pedido.setFechaPedido(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

            String resultado = presenter.solicitarServicio(pedido);

            Toast.makeText(getActivity(), resultado, Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Cancelalo, en caso de no poder retirarlo. ;)", Toast.LENGTH_LONG).show();

            PrincipalFragment fragment = new PrincipalFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contenedor, fragment).addToBackStack(null).commit();

        }
    }
    public void platoNoEncontrado(View view){

        SolicitarServicioAlimentacionFragment fragment = new SolicitarServicioAlimentacionFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contenedor, fragment).addToBackStack(null).commit();
    }
}
