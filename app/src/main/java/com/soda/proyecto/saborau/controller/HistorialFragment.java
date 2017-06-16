package com.soda.proyecto.saborau.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soda.proyecto.saborau.R;
import com.soda.proyecto.saborau.dominio.ItemPedido;
import com.soda.proyecto.saborau.dominio.Pedido;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HistorialFragment extends Fragment {

    private Intent intent;
    private ListView historialListView;
    private List<Pedido> pedidos;
    private int posicion;
    private DatabaseReference ref;
    private DatabaseReference mensajeRef;
    private ValueEventListener listener;
    private SharedPreferences pref;
    private ArrayList<Pedido> pedidosDescendentes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_historial, container, false);
        pref = getActivity().getApplicationContext().getSharedPreferences("UsuarioActual", 0); // 0 - for private mode
        historialListView = (ListView) view.findViewById(R.id.historialView);
        ref = FirebaseDatabase.getInstance().getReference();
        mensajeRef = ref.child("Pedidos");

        pedidos = new ArrayList<Pedido>();

        listener = mensajeRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pedidos.clear();

                Iterator<DataSnapshot> ite = dataSnapshot.getChildren().iterator();

                while(ite.hasNext()){

                    DataSnapshot data = ite.next();
                    Pedido pedido  = data.getValue(Pedido.class);
                    pedido.setIdPedido(data.getKey());
                    if(pedido.getUsuario().getCorreo().equals(pref.getString("correoUsuario", null)))
                    {
                        pedidos.add(pedido);
                    }
                }

                if(!pedidos.isEmpty()){
                    pedidosDescendentes = new ArrayList<Pedido>();
                    for(int i=pedidos.size()-1; i>=0; i--){

                        pedidosDescendentes.add(pedidos.get(i));
                    }
                    PedidoAdapter pedidoAdapter = new PedidoAdapter(getActivity().getApplicationContext(), R.layout.row, pedidosDescendentes);
                    historialListView.setAdapter(pedidoAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(), "No se ha podido contactar con el catálogo de pedidos", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
    public class PedidoAdapter extends ArrayAdapter {

        private List<Pedido> pedidos;
        private int resources;
        private LayoutInflater inflater;
        private ImageView imagenPlatoPedido;
        private TextView fechaPedido;
        private TextView estadoPedido;
        private TextView totalPedido;
        private Button btnDetallesPedido;

        public PedidoAdapter(Context context, int resource, List<Pedido> objects) {

            super(context, resource, objects);
            pedidos = objects;
            resources = resource;
            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){

                convertView = inflater.inflate(resources, null);
            }

            imagenPlatoPedido = (ImageView) convertView.findViewById(R.id.imagenPlatoPedido);
            fechaPedido = (TextView) convertView.findViewById(R.id.fechaPedido);
            estadoPedido = (TextView) convertView.findViewById(R.id.estadoPedido);
            totalPedido = (TextView) convertView.findViewById(R.id.totalPedido);
            btnDetallesPedido = (Button) convertView.findViewById(R.id.btnDetallesPedido);

            try
            {
                Glide.with(getContext()).load(pedidos.get(position).getItems().get(0).getPlato().getImagenPlato()).into((ImageView) imagenPlatoPedido);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            fechaPedido.setText("Fecha del pedido: "+pedidos.get(position).getFechaPedido());
            estadoPedido.setText("Estados del pedido: "+pedidos.get(position).getEstado());

            float costoPedido = 0;

            for (ItemPedido item:pedidos.get(position).getItems()) {

                costoPedido += (item.getCantidad()*item.getPrecioPlato());
            }

            totalPedido.setText("Costo del pedido: "+costoPedido);

            btnDetallesPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    posicion = position;
                    detallesPedido(view);

                }
            });
            return convertView;
        }
    }
    public void detallesPedido(View view){

        DetallesPedidoFragment fragment = new DetallesPedidoFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contenedor, fragment).addToBackStack(null).commit();

        Bundle data = new Bundle();
        data.putSerializable("Pedido", pedidosDescendentes.get(posicion));
        fragment.setArguments(data);

        mensajeRef.removeEventListener(listener);
    }
}
