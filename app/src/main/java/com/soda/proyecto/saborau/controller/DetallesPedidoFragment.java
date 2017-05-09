package com.soda.proyecto.saborau.controller;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.soda.proyecto.saborau.presenter.SolicitudPresenter;

import java.util.Iterator;
import java.util.List;

import com.soda.proyecto.saborau.R;

public class DetallesPedidoFragment extends Fragment {

    private ListView historialListView;
    private TextView tvFechaPedido;
    private TextView tvEstadoPedido;
    private Pedido pedido;
    private DatabaseReference ref;
    private DatabaseReference mensajeRef;
    private Button btnModificarPedido;
    private Button btnCancelarPedido;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_detalles_pedido, container, false);

        historialListView = (ListView) view.findViewById(R.id.historialView);
        tvFechaPedido = (TextView) view.findViewById(R.id.tvFechaPedido);
        tvEstadoPedido = (TextView) view.findViewById(R.id.tvEstadoPedido);
        btnCancelarPedido = (Button) view.findViewById(R.id.btnCancelarPedido);
        btnModificarPedido = (Button) view.findViewById(R.id.btnModificarPedido);
        ref = FirebaseDatabase.getInstance().getReference();
        mensajeRef = ref.child("Platos");
        pedido = (Pedido) getArguments().getSerializable("Pedido");

        btnCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarPedido(view);
            }
        });

        btnModificarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarPedido(view);
            }
        });

        mensajeRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> ite = dataSnapshot.getChildren().iterator();

                while(ite.hasNext()){

                    DataSnapshot data =ite.next();
                    Plato plato  = data.getValue(Plato.class);
                    plato.setIdPlato(data.getKey());

                    for (ItemPedido itemLista:pedido.getItems()) {

                        if(itemLista.getPlato().getIdPlato().equals(plato.getIdPlato())){

                            itemLista.setPlato(plato);
                        }

                    }
                }
                ItemPedidoAdapter itemPedidoAdapteredidoAdapter = new ItemPedidoAdapter(getActivity().getApplicationContext(), R.layout.item_platillo_pedido_row, pedido.getItems());
                historialListView.setAdapter(itemPedidoAdapteredidoAdapter);
                tvFechaPedido.setText("Fecha del pedido: "+pedido.getFechaPedido());
                tvEstadoPedido.setText("Estado del pedido: "+pedido.getEstado());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(), "No se ha podido contactar con el catálogo de platos", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
    public void modificarPedido(View view){

        ModificarPedidoFragment fragment = new ModificarPedidoFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contenedor, fragment).addToBackStack(null).commit();
        Bundle data = new Bundle();
        data.putSerializable("Pedido", pedido);
        fragment.setArguments(data);
    }
    public void cancelarPedido(View view){

        if(pedido.getEstado().compareTo("Pendiente") != 0){
            Toast.makeText(getActivity(), "¡Lo sentimos! Este pedido no puede ser cancelado", Toast.LENGTH_SHORT).show();
        }
        else{
            AlertDialog.Builder mensajeConfirmacion = new AlertDialog.Builder(getContext());
            mensajeConfirmacion.setTitle("Importante");
            mensajeConfirmacion.setMessage("¿De verdad desea cancelar el pedido?");
            mensajeConfirmacion.setCancelable(false);
            mensajeConfirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast t = Toast.makeText(getActivity(), "¡Perfecto! Excelente decisión", Toast.LENGTH_SHORT);
                    t.show();
                }
            });
            mensajeConfirmacion.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PedidoData pedidoData = new PedidoDataFirebase(getActivity());
                    SolicitudPresenter sPresenter = new SolicitudPresenter(pedidoData);
                    sPresenter.cancelarPedido(pedido);

                    Toast t = Toast.makeText(getActivity(), "Pedido cancelado exitosamente", Toast.LENGTH_SHORT);
                    t.show();

                    HistorialFragment fragment = new HistorialFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contenedor, fragment).addToBackStack(null).commit();
                }
            });

            mensajeConfirmacion.show();
        }

    }
    public class ItemPedidoAdapter extends ArrayAdapter {

        private List<ItemPedido> itemsPedido;
        private int resources;
        private LayoutInflater inflater;
        private TextView tvNombrePlato;
        private TextView tvComponentes;
        private TextView tvCantidad;
        private TextView tvPrecioPlato;
        private TextView tvComentarios;

        public ItemPedidoAdapter(Context context, int resource, List<ItemPedido> objects) {

            super(context, resource, objects);
            itemsPedido = objects;
            resources = resource;
            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){

                convertView = inflater.inflate(resources, null);
            }

            tvComentarios = (TextView) convertView.findViewById(R.id.tvComentarios);
            tvComponentes = (TextView) convertView.findViewById(R.id.tvComponentes);
            tvCantidad = (TextView) convertView.findViewById(R.id.tvCantidad);
            tvNombrePlato = (TextView) convertView.findViewById(R.id.tvNombrePlato);
            tvPrecioPlato = (TextView) convertView.findViewById(R.id.tvPrecioPlato);

            tvComentarios.setText("Comentarios adicionales: "+itemsPedido.get(position).getComentarios());
            tvPrecioPlato.setText("Precio: "+itemsPedido.get(position).getPrecioPlato());
            tvNombrePlato.setText("Platillo: "+itemsPedido.get(position).getPlato().getNombrePlato());
            tvCantidad.setText("Cantidad: "+itemsPedido.get(position).getCantidad());

            String componentes = "Componentes del plato: ";

            for (ItemPlatoComponente item:itemsPedido.get(position).getPlato().getItems()) {

                componentes += (item.getComponente().getNombreComponentePlato()+", ");
            }

            tvComponentes.setText(componentes);

            return convertView;
        }
    }
}
