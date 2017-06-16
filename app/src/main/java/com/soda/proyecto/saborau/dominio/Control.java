package com.soda.proyecto.saborau.dominio;

import java.io.Serializable;

/**
 * Created by victo on 14/6/2017.
 */

public class Control implements Serializable
{
    private int dia;
    private String platoOpcional;
    private String platoPrincipal;
    private int Semana;

    public Control() {}

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public String getPlatoOpcional() {
        return platoOpcional;
    }

    public void setPlatoOpcional(String platoOpcional) {
        this.platoOpcional = platoOpcional;
    }

    public String getPlatoPrincipal() {
        return platoPrincipal;
    }

    public void setPlatoPrincipal(String platoPrincipal) {
        this.platoPrincipal = platoPrincipal;
    }

    public int getSemana() {
        return Semana;
    }

    public void setSemana(int semana) {
        Semana = semana;
    }
}
