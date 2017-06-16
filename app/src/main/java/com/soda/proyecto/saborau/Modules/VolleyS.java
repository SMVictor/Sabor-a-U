package com.soda.proyecto.saborau.Modules;

//Importaciones necesarias

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by yunen on 8/5/2017.
 */

public class VolleyS
{
    //Declaración de variables globales
    private static VolleyS mVolleyS = null;
    private RequestQueue mRequestQueue;

    /**
     * Método constructor de la aplicación
     * */
    public VolleyS(Context context)
    {
        this.mRequestQueue = Volley.newRequestQueue(context);
    }//Fin del método

    /**
     * Retorna la instancia
     * */
    public static VolleyS getInstance(Context context)
    {
        if(mVolleyS == null)
        {
            mVolleyS = new VolleyS(context);
        }//Fin del if
        return mVolleyS;
    }//Fin del método

    /**
     * Obtener el RequestQueue
     * */
    public RequestQueue getRequestQueue()
    {
        return this.mRequestQueue;
    }//Fin del método
}//Fin de la clase