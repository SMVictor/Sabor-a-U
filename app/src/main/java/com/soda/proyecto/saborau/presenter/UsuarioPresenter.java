package com.soda.proyecto.saborau.presenter;

import com.soda.proyecto.saborau.dataAccess.UsuarioDataFireBase;
import com.soda.proyecto.saborau.dominio.UsuarioServicio;

/**
 * Created by victo on 20/11/2016.
 */

public class UsuarioPresenter {

    private UsuarioDataFireBase usuarioData;

    public UsuarioPresenter(UsuarioDataFireBase usuarioData) {
        this.usuarioData = usuarioData;
    }

    public String nuevoUsuario(UsuarioServicio usuarioServicio){

        return usuarioData.nuevoUsuario(usuarioServicio);
    }
}
