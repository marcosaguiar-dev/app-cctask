package com.myapplications.cctask_app.model;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;

public class UsuarioAdm implements Serializable {

    private String idUsuario;
    private String status;
    private String nome;
    private String siglaNome;

    public UsuarioAdm() {
    }

    public void salvar() {
        DatabaseReference database = ConfiguracaoFaribase.getDatabase();
        DatabaseReference databaseReference = database.child("usuarios_adm")
                .child(getIdUsuario());

        databaseReference.setValue(this);
    }

    public void atualizarStatus(String id) {

        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus());

        DatabaseReference firebaseRef = ConfiguracaoFaribase.getDatabase();
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios_adm").child(id);

        usuarioRef.updateChildren(status);

    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
