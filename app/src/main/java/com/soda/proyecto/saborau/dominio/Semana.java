package com.soda.proyecto.saborau.dominio;

import java.io.Serializable;

/**
 * Created by victo on 5/11/2016.
 */

public class Semana implements Serializable {

    private int numeroSemana;

    public Semana() {
    }

    public int getNumeroSemana() {
        return numeroSemana;
    }

    public void setNumeroSemana(int numeroSemana) {
        this.numeroSemana = numeroSemana;
    }
}

