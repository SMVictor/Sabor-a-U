package com.soda.proyecto.saborau.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.soda.proyecto.saborau.R;
import com.soda.proyecto.saborau.Utilities.MiFirebaseInstanceIdService;
import com.soda.proyecto.saborau.dataAccess.PedidoDataFirebase;
import com.soda.proyecto.saborau.dominio.Control;
import com.soda.proyecto.saborau.dominio.Plato;
import com.soda.proyecto.saborau.dominio.UsuarioServicio;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Intent intent;
    private TextView tvEmail;
    private TextView tvPassword;
    private boolean session;
    private NavigationView navigationView;
    private DatabaseReference ref;
    private DatabaseReference mensajeRef;
    private ValueEventListener listener;
    private UsuarioServicio usuarioServicio;
    private SharedPreferences pref;
    private String control;
    private Plato plato;
    private int diaCorrespondiente;
    private int semanaCorrespondiente;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ref = FirebaseDatabase.getInstance().getReference();
        pref = getApplicationContext().getSharedPreferences("UsuarioActual", 0); // 0 - for private mode

        fragmentManager.beginTransaction().replace(R.id.contenedor, new PrincipalFragment()).addToBackStack(null).commit();
    }
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.home:
                fragmentManager.beginTransaction().replace(R.id.contenedor, new PrincipalFragment()).addToBackStack(null).commit();
                break;

            case R.id.registro:
                fragmentManager.beginTransaction().replace(R.id.contenedor, new RegistrarFragment()).addToBackStack(null).commit();
                break;

            case R.id.almuerzo:
                if(!session)// Represent that the session variable is "false", because the user has not logged in.
                {
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new LoginFragment()).addToBackStack(null).commit();
                    Toast.makeText(getApplicationContext(), "Debes iniciar sesión", Toast.LENGTH_LONG).show();
                }
                else
                {
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new SolicitarServicioAlimentacionFragment()).addToBackStack(null).commit();
                }
                break;

            case R.id.hitorial:

                if(!session)// Represent that the session variable is "false", because the user has not logged in.
                {
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new LoginFragment()).addToBackStack(null).commit();
                    Toast.makeText(getApplicationContext(), "Debes iniciar sesión", Toast.LENGTH_LONG).show();
                }
                else
                {
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new HistorialFragment()).addToBackStack(null).commit();
                }
                break;

            case R.id.iniciarSesion:
                if(!this.session)
                {
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new LoginFragment()).addToBackStack(null).commit();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Ya tienes una sesión iniciada ", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.cerrarSesion:
                if(this.session)
                {
                    /*
                    *In case that the user logs out, we set the profile photo and username with
                    * the default, and we change the session variable to false.
                    */
                    View hView = navigationView.getHeaderView(0);
                    TextView tvUserNameHeader = (TextView)hView.findViewById(R.id.tvUserNameHeader);
                    ImageView ivUserNameHeader = (ImageView) hView.findViewById(R.id.ivUserNameHeader);
                    ivUserNameHeader.setImageResource(R.drawable.logo);
                    tvUserNameHeader.setText("Usuario");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("correoUsuario"); // Storing long
                    editor.remove("usuario");
                    editor.commit(); // commit changes
                    session = false;
                    fragmentManager.beginTransaction().replace(R.id.contenedor, new PrincipalFragment()).addToBackStack(null).commit();
                    Toast.makeText(getApplicationContext(), "Hasta Luego", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Aún no has iniciado sesión ", Toast.LENGTH_LONG).show();
                }
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * This method is responsible for verify that the username and password supplied are congruent
     * with the data stored in the database. Further, it is responsible for set the profil photo and
     * username whit correspondig data to the current user, and change the session variable to "true".
     */
    public void authenticateUser(View view)
    {
        if(listener!=null)
        {
            mensajeRef.removeEventListener(listener);
        }
        mensajeRef = ref.child("UsuarioServicio");
        this.tvEmail = (TextView) findViewById(R.id.tvEmail);
        this.tvPassword = (TextView) findViewById(R.id.tvPassword);
        String correo = tvEmail.getText()+"";

        // It is obtained the user with email address supplied
        listener = mensajeRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Iterator<DataSnapshot> ite = dataSnapshot.getChildren().iterator();

                while(ite.hasNext())
                {

                    DataSnapshot data = ite.next();
                    UsuarioServicio usuario  = data.getValue(UsuarioServicio.class);
                    usuario.setIdUsuario(data.getKey());

                    if(usuario.getCorreo().equals(tvEmail.getText()+""))
                    {
                        usuarioServicio = usuario;
                    }
                }
                //It is verified that the user address exists.
                if(usuarioServicio!=null)
                {
                    //It is verified that the user password is ok.
                    if(usuarioServicio.getContrasena().equals(tvPassword.getText()+""))
                    {
                        View hView = navigationView.getHeaderView(0);
                        TextView tvUserNameHeader = (TextView)hView.findViewById(R.id.tvUserNameHeader);
                        ImageView ivUserNameHeader = (ImageView) hView.findViewById(R.id.ivUserNameHeader);

                        //it is changed the profile photo and username in the navigation bar.
                        try {
                            byte [] encodeByte= Base64.decode(usuarioServicio.getFotoPerfil(),Base64.DEFAULT);
                            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                            ivUserNameHeader.setImageBitmap(bitmap);
                        } catch(Exception e) {
                            e.getMessage();
                        }
                        tvUserNameHeader.setText(usuarioServicio.getNombre());

                        // it is changed the session variable to true
                        session = true;
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("correoUsuario", usuarioServicio.getCorreo()); // Storing long
                        editor.putString("usuario", usuarioServicio.getNombre()+" "
                                +usuarioServicio.getPrimerApellido()+" "+usuarioServicio.getSegundoApellido());
                        editor.commit(); // commit changes
                        fragmentManager.popBackStack();
                        Toast.makeText(getApplicationContext(), "Bienvenido "+usuarioServicio.getNombre(), Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrecto", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Correo electrónico no encontrado", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "No se ha podido contactar con el catálogo de usuarios", Toast.LENGTH_LONG).show();
            }
        });
    }//Fin del método
    public long difDiasEntre2fechas(Calendar date1, Calendar date2)
    {
        long difms=date2.getTimeInMillis() - date1.getTimeInMillis();
        long difd=difms / (1000 * 60 * 60 * 24);
        return difd;
    }
    public void solicitudPlatoDia(View view)
    {
        calcularPlatoCorrespondiente();
        if(listener!=null)
        {
            mensajeRef.removeEventListener(listener);
        }
        mensajeRef = ref.child("Platos");
        listener = mensajeRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Iterator<DataSnapshot> ite = dataSnapshot.getChildren().iterator();

                while(ite.hasNext())
                {

                    DataSnapshot data = ite.next();
                    Plato platoLista= data.getValue(Plato.class);
                    platoLista.setIdPlato(data.getKey());

                    if(platoLista.getSemana().getNumeroSemana()==semanaCorrespondiente &&
                            platoLista.getDia().getNumeroDia()==diaCorrespondiente)
                    {
                        plato=platoLista;
                    }
                }
                if(plato!=null)
                {
                    SolicitarServicioPlatoFragment fragment = new SolicitarServicioPlatoFragment();
                    fragmentManager.beginTransaction().replace(R.id.contenedor, fragment).addToBackStack(null).commit();
                    Bundle data = new Bundle();
                    data.putBoolean("tipoPlatoSeleccionado", false);
                    data.putString("platoCorrespondiente", plato.getIdPlato());
                    fragment.setArguments(data);
                } else{

                    Toast.makeText(getApplicationContext(), "El menú no está disponible", Toast.LENGTH_LONG).show();
                }
                mensajeRef.removeEventListener(listener);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Objeto de control no encontrado", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void solicitudPlatoOpcional(View view)
    {
        calcularPlatoCorrespondiente();
        if(diaCorrespondiente!=6 && diaCorrespondiente!=7)
        {
            if(listener!=null)
            {
                mensajeRef.removeEventListener(listener);
            }
            mensajeRef = ref.child("control");
            listener = mensajeRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    Iterator<DataSnapshot> ite = dataSnapshot.getChildren().iterator();

                    while(ite.hasNext())
                    {

                        DataSnapshot data = ite.next();
                        control = data.getValue(String.class);
                    }
                    if(control!=null)
                    {
                        SolicitarServicioPlatoFragment fragment = new SolicitarServicioPlatoFragment();
                        fragmentManager.beginTransaction().replace(R.id.contenedor, fragment).addToBackStack(null).commit();
                        Bundle data = new Bundle();
                        data.putBoolean("tipoPlatoSeleccionado", true);
                        data.putString("platoCorrespondiente", control);
                        fragment.setArguments(data);
                    } else{

                        Toast.makeText(getApplicationContext(), "El menú no está disponible", Toast.LENGTH_LONG).show();
                    }
                    mensajeRef.removeEventListener(listener);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Toast.makeText(getApplicationContext(), "Objeto de control no encontrado", Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "El menú no está disponible", Toast.LENGTH_LONG).show();
        }
    }
    public void calcularPlatoCorrespondiente()
    {
        Calendar calendar2 = Calendar.getInstance();
        Calendar calendar1 = new GregorianCalendar(2017,5,19);

        long diasTrascurridos = difDiasEntre2fechas(calendar1, calendar2);

        diaCorrespondiente = (int) (diasTrascurridos%7)+1;
        semanaCorrespondiente = (int) ((diasTrascurridos/7)%3)+1;
    }
}
