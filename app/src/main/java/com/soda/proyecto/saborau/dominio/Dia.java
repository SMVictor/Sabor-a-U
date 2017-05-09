package com.soda.proyecto.saborau.dominio;

import java.io.Serializable;

/**
 * Created by victo on 5/11/2016.
 */

public class Dia implements Serializable {
    private int numeroDia;

    public Dia(){

    }

    public int getNumeroDia() {
        return numeroDia;
    }

    public void setNumeroDia(int numeroDia) {
        this.numeroDia = numeroDia;
    }
}