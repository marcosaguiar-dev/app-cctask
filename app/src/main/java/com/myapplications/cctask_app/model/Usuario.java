package com.myapplications.cctask_app.model;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String sobrenome;
    private String turma;
    private String email;
    private String senha;
    private String cor;

    public Usuario() {
    }

    public void salvar() {

        DatabaseReference firebaseRef = ConfiguracaoFaribase.getDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child(getId());

        usuario.setValue(this);
    }

    public void atualizar(String id) {
        DatabaseReference database = ConfiguracaoFaribase.getDatabase();

        DatabaseReference avisoRef = database.child("usuarios")
                .child(id);

        Map<String, Object> valoresTarefas = converterParaMap();
        avisoRef.updateChildren(valoresTarefas);
    }

    @Exclude
    public Map<String, Object> converterParaMap() {
        HashMap<String, Object> UsuarioMap = new HashMap<>();
        UsuarioMap.put("nome", getNome());
        UsuarioMap.put("sobrenome", getSobrenome());
        UsuarioMap.put("turma", getTurma());
        UsuarioMap.put("email", getEmail());
        UsuarioMap.put("senha", getSenha());

        return UsuarioMap;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
