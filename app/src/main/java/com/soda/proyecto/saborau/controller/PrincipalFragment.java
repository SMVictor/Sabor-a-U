package com.soda.proyecto.saborau.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.soda.proyecto.saborau.dominio.Plato;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PrincipalFragment extends Fragment
{
    private View view;
    private int posicion;
    private ListView lvMenu;
    private List<Plato> platos;
    private DatabaseReference ref;
    private DatabaseReference mensajeRef;
    private ValueEventListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_principal, container, false);
        lvMenu = (ListView) view.findViewById(R.id.lvMenu);
        ref = FirebaseDatabase.getInstance().getReference();
        mensajeRef = ref.child("Platos");

        platos = new ArrayList<Plato>();

        listener = mensajeRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                platos.clear();

                Iterator<DataSnapshot> ite = dataSnapshot.getChildren().iterator();

                while(ite.hasNext()){

                    DataSnapshot data = ite.next();
                    Plato plato  = data.getValue(Plato.class);
                    plato.setIdPlato(data.getKey());
                    platos.add(plato);
                }

                if(platos.isEmpty()){
                    Toast.makeText(getActivity(), "No se ha registrado ningún plato", Toast.LENGTH_SHORT).show();
                }
                else{

                    PlatoAdapter platoAdapter = new PlatoAdapter(getActivity().getApplicationContext(), R.layout.dia_menu, platos);
                    lvMenu.setAdapter(platoAdapter);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(), "No se ha podido contactar con el catálogo de pedidos", Toast.LENGTH_LONG).show();
            }
        });

        return  view;
    }

    public class PlatoAdapter extends ArrayAdapter {

        private List<Plato> platos;
        private int resources;
        private LayoutInflater inflater;
        private ImageView ivImagenPlato;
        private TextView tvDiaSemana;
        private TextView tvNombrePlato;
        private TextView tvSemanaPlato;

        public PlatoAdapter(Context context, int resource, List<Plato> objects) {

            super(context, resource, objects);
            platos = objects;
            resources = resource;
            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){

                convertView = inflater.inflate(resources, null);
            }
            ivImagenPlato = (ImageView) convertView.findViewById(R.id.ivImagenPlato);
            tvDiaSemana = (TextView) convertView.findViewById(R.id.tvDiaSemana);
            tvNombrePlato = (TextView) convertView.findViewById(R.id.tvNombrePlato);
            tvSemanaPlato = (TextView) convertView.findViewById(R.id.tvSemanaPlato);

            try
            {
                Glide.with(getContext()).load(platos.get(position).getImagenPlato()).into((ImageView) ivImagenPlato);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            tvNombrePlato.setText(platos.get(position).getNombrePlato());
            tvSemanaPlato.setText("Semana: "+platos.get(position).getSemana().getNumeroSemana());
            switch (platos.get(position).getDia().getNumeroDia())
            {
                case 1:
                    tvDiaSemana.setText("Lunes");
                    break;
                case 2:
                    tvDiaSemana.setText("Martes");
                    break;
                case 3:
                    tvDiaSemana.setText("Miécoles");
                    break;
                case 4:
                    tvDiaSemana.setText("Jueves");
                    break;
                default:
                    tvDiaSemana.setText("Viernes");
                    break;
            }
            return convertView;
        }
    }
}
