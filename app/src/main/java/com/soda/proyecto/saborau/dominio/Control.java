package com.soda.proyecto.saborau.dominio;

import java.io.Serializable;

/**
 * Created by victo on 14/6/2017.
 */

public class Control implements Serializable
{
    private String platoOpcional;

    public Control() {}


    public String getPlatoOpcional() {
        return platoOpcional;
    }

    public void setPlatoOpcional(String platoOpcional) {
        this.platoOpcional = platoOpcional;
    }
}
