package com.myapplications.cctask_app.model;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Aviso implements Serializable {

    private String id;
    private String titulo;
    private String data;
    private String descricao;
    private String caminhoArquivoFire;
    private String caminhoArquivo;
    private String idUsuario;
    private String nomeArquivo;
    private String nomeUsuario;
    private String codeArquivo;
    private String avisoVisualizacao;

    public Aviso() {
        DatabaseReference avisoRef = ConfiguracaoFaribase.getDatabase().child("avisos");
        setId(avisoRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference avisoRef = ConfiguracaoFaribase.getDatabase().child("avisos");

        avisoRef.child(getId())
                .setValue(this);
    }

    public void remover(){
        DatabaseReference avisoRef = ConfiguracaoFaribase.getDatabase().child("avisos");

        avisoRef.child(getId())
                .removeValue();
    }

//    public void atualizar(String id){
//        DatabaseReference database = ConfiguracaoFaribase.getDatabase();
//
//        DatabaseReference avisoRef = database.child("avisos")
//                .child(id);
//
//        Map<String, Object> valoresAvisos = converterParaMap();
//        avisoRef.updateChildren(valoresAvisos);
//    }

    @Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> avisoMap = new HashMap<>();
        avisoMap.put("titulo", getTitulo());
        avisoMap.put("data", getData());
        avisoMap.put("descricao", getDescricao());
        avisoMap.put("caminhoArquivoFire", getCaminhoArquivoFire());
        avisoMap.put("nomeArquivo", getNomeArquivo());
        avisoMap.put("codeArquivo", getCodeArquivo());
        avisoMap.put("caminhoArquivo", getCaminhoArquivo());
        avisoMap.put("avisoVisualizacao", getAvisoVisualizacao());

        return avisoMap;
    }

    public void atualizarVisualizacao(String id, String idu){

        HashMap<String, Object> idVisu = new HashMap<>();
        idVisu.put("avisoVisualizacao", getAvisoVisualizacao());

        DatabaseReference firebaseRef = ConfiguracaoFaribase.getDatabase();
        DatabaseReference avisoRef = firebaseRef
                .child("avisos")
                .child(id)
                .child(idu);
        avisoRef.updateChildren(idVisu);
    }

    public String getAvisoVisualizacao() {
        return avisoVisualizacao;
    }

    public void setAvisoVisualizacao(String avisoVisualizacao) {
        this.avisoVisualizacao = avisoVisualizacao;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getCodeArquivo() {
        return codeArquivo;
    }

    public void setCodeArquivo(String codeArquivo) {
        this.codeArquivo = codeArquivo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoArquivoFire() {
        return caminhoArquivoFire;
    }

    public void setCaminhoArquivoFire(String caminhoArquivoFire) {
        this.caminhoArquivoFire = caminhoArquivoFire;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
}
