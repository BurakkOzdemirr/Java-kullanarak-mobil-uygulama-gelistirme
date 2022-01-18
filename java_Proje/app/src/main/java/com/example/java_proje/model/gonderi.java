package com.example.java_proje.model;

public class gonderi {

    private String gonderiId;
    private String gonderiResmi;
    private String gonderiSehir;
    private String gonderiYorum;
    private String gonderen;


    public gonderi() {

    }

    public gonderi(String gonderiId, String gonderiResmi, String gonderiSehir, String gonderiYorum, String gonderen) {
        this.gonderiId = gonderiId;
        this.gonderiResmi = gonderiResmi;
        this.gonderiSehir = gonderiSehir;
        this.gonderiYorum = gonderiYorum;
        this.gonderen = gonderen;
    }

    public String getGonderiId() {
        return gonderiId;
    }

    public void setGonderiId(String gonderiId) {
        this.gonderiId = gonderiId;
    }

    public String getGonderiResmi() {
        return gonderiResmi;
    }

    public void setGonderiResmi(String gonderiResmi) {
        this.gonderiResmi = gonderiResmi;
    }

    public String getGonderiSehir() {
        return gonderiSehir;
    }

    public void setGonderiSehir(String gonderiSehir) {
        this.gonderiSehir = gonderiSehir;
    }

    public String getGonderiYorum() {
        return gonderiYorum;
    }

    public void setGonderiYorum(String gonderiYorum) {
        this.gonderiYorum = gonderiYorum;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }
}
