package com.soda.proyecto.saborau.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.soda.proyecto.saborau.Modules.VolleyS;
import com.soda.proyecto.saborau.R;
import com.soda.proyecto.saborau.dataAccess.PedidoData;
import com.soda.proyecto.saborau.dataAccess.PedidoDataFirebase;
import com.soda.proyecto.saborau.dominio.UsuarioServicio;
import com.soda.proyecto.saborau.presenter.SolicitudPresenter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegistrarFragment extends Fragment
{
    private TextView nombre;
    private TextView primerApellido;
    private TextView segundoApellido;
    private TextView correo;
    private TextView password;
    private TextView confirmarPassword;
    private Button btnLogin;
    private  UsuarioServicio usuarioServicio;
    private ImageView imgLogo;
    private VolleyS volley;
    protected RequestQueue fRequestQueue;
    public static final String FIREBASE_URL = "https://pedi-tu-almuerzo-c34ef.firebaseio.com/";
    final public static String USUARIOS_REFERENCE="UsuarioServicio";
    private static final int PICK_IMAGE = 100;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar, container, false);

        nombre = (TextView) view.findViewById(R.id.tvNombre);
        primerApellido = (TextView) view.findViewById(R.id.tvApellido1);
        segundoApellido = (TextView) view.findViewById(R.id.tvApellido2);
        correo = (TextView) view.findViewById(R.id.tvEmail);
        password = (TextView) view.findViewById(R.id.tvPassword);
        confirmarPassword = (TextView) view.findViewById(R.id.tvConfirmarPassword);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        imgLogo = (ImageView) view.findViewById(R.id.ivLogo);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });

        this.volley = VolleyS.getInstance(this.getContext());
        this.fRequestQueue = volley.getRequestQueue();

        return view;
    }

    public void registrar()
    {
        if((password.getText()+"").equals((confirmarPassword.getText()+"")))
        {
            usuarioServicio = new UsuarioServicio();
            usuarioServicio.setNombre(nombre.getText()+"");
            usuarioServicio.setPrimerApellido(primerApellido.getText()+"");
            usuarioServicio.setSegundoApellido(segundoApellido.getText()+"");
            usuarioServicio.setCorreo(correo.getText()+"");
            usuarioServicio.setContrasena(password.getText()+"");

            AlertDialog.Builder mensajeConfirmacion = new AlertDialog.Builder(getContext());
            mensajeConfirmacion.setTitle("Importante");
            mensajeConfirmacion.setMessage("¿Desea agregar una foto de perfil?");
            mensajeConfirmacion.setCancelable(false);
            mensajeConfirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Bitmap foto = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                    ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                    foto.compress(Bitmap.CompressFormat.PNG,100, baos);
                    byte [] b=baos.toByteArray();
                    String temp=Base64.encodeToString(b, Base64.DEFAULT);

                    usuarioServicio.setFotoPerfil(temp);
                    guardarUsuario();
                }
            });
            mensajeConfirmacion.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try
                    {
                        Toast.makeText(getActivity(), "Seleccione una foto de perfil", Toast.LENGTH_SHORT).show();
                        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, PICK_IMAGE);

                    }catch (ActivityNotFoundException ex){}

                }
            });

            mensajeConfirmacion.show();
        } else {
                Toast.makeText(getActivity(), "Las contraseñas suministradas no coinciden", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE)
        {
            try
            {
                Uri uriImagen = data.getData();
                Bitmap foto = BitmapFactory.decodeFile(obtenerPathReal(uriImagen));
                ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                foto.compress(Bitmap.CompressFormat.PNG,100, baos);
                byte [] b=baos.toByteArray();
                baos.close();
                String temp=Base64.encodeToString(b, Base64.DEFAULT);

                usuarioServicio.setFotoPerfil(temp);
                guardarUsuario();

            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void guardarUsuario()
    {
        Firebase.setAndroidContext(getActivity());
        Firebase ref = new Firebase(PedidoDataFirebase.FIREBASE_URL);

        Toast.makeText(getActivity(), "Procesando Solicitud...", Toast.LENGTH_SHORT).show();

        ref.child("UsuarioServicio").push().setValue(usuarioServicio, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null)
                {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new LoginFragment()).addToBackStack(null).commit();
                    Toast.makeText(getActivity(), "Registro Exitoso!!!!, Procesa a Iniciar Sesión", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Su solicitud no pudo ser realizada", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    public String obtenerPathReal(Uri uri)
    {
        Cursor cursor = null;
        try
        {
            String [] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getApplicationContext().getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }finally {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }
    public void addToQueue(Request request)
    {
        if (request != null)
        {
            request.setTag(this);
            if (fRequestQueue == null)
                fRequestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            fRequestQueue.add(request);
        }//Fin del if
    }//Fin del método
}
