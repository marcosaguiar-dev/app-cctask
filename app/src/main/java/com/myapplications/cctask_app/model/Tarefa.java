package com.myapplications.cctask_app.model;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Tarefa implements Serializable {

    private String id;
    private String materia;
    private String sigla;
    private String data;
    private String descricao;
    private String caminhoArquivoFire;
    private String caminhoArquivo;
    private String nomeArquivo;
    private String nomeUsuario;
    private String idUsuario;
    private String codeArquivo;
    private String teste;
    private String tarefaVisualizacao;


    public Tarefa() {
        DatabaseReference tarefaRef = ConfiguracaoFaribase.getDatabase().child("tarefas");
        setId(tarefaRef.push().getKey());
    }

    public void salvar() {
        DatabaseReference tarefaRef = ConfiguracaoFaribase.getDatabase().child("tarefas");

        tarefaRef.child(getId())
                .setValue(this);

    }

    public void remover() {
        DatabaseReference tarefaRef = ConfiguracaoFaribase.getDatabase().child("tarefas");

        tarefaRef.child(getId())
                .removeValue();
    }

//    public void atualizar(String id) {
//        DatabaseReference database = ConfiguracaoFaribase.getDatabase();
//
//        DatabaseReference avisoRef = database.child("tarefas")
//                .child(id);
//
//        Map<String, Object> valoresTarefas = converterParaMap();
//        avisoRef.updateChildren(valoresTarefas);
//    }

    @Exclude
    public Map<String, Object> converterParaMap() {
        HashMap<String, Object> tarefaMap = new HashMap<>();
        tarefaMap.put("materia", getMateria());
        tarefaMap.put("data", getData());
        tarefaMap.put("descricao", getDescricao());
        tarefaMap.put("caminhoArquivoFire", getCaminhoArquivoFire());
        tarefaMap.put("nomeArquivo", getNomeArquivo());
        tarefaMap.put("codeArquivo", getCodeArquivo());
        tarefaMap.put("caminhoArquivo", getCaminhoArquivo());
        tarefaMap.put("tarefaVisualizacao", getTarefaVisualizacao());

        return tarefaMap;
    }

    public void atualizarVisualizacao(String id, String idu) {

        HashMap<String, Object> idVisu = new HashMap<>();
        idVisu.put("tarefaVisualizacao", getTarefaVisualizacao());

        DatabaseReference firebaseRef = ConfiguracaoFaribase.getDatabase();
        DatabaseReference tarefaRef = firebaseRef
                .child("tarefas")
                .child(id)
                .child(idu);
        tarefaRef.updateChildren(idVisu);
    }

    public String getTeste() {
        return teste;
    }

    public void setTeste(String teste) {
        this.teste = teste;
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

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getTarefaVisualizacao() {
        return tarefaVisualizacao;
    }

    public void setTarefaVisualizacao(String tarefaVisualizacao) {
        this.tarefaVisualizacao = tarefaVisualizacao;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
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
}
