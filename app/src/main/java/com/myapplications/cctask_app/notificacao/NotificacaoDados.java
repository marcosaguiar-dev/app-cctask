package com.myapplications.cctask_app.notificacao;

public class NotificacaoDados {
    //Estrutura de dados para enviar ao firebase
    /*
        "to": "topicos ou token,
        "notificacao" : {
            "title" : "titulo",
            "body" : "corpo
           }

     */

    private String  to;
    private Notificacao notification;

    public NotificacaoDados(String to, Notificacao notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notificacao getNotification() {
        return notification;
    }

    public void setNotification(Notificacao notification) {
        this.notification = notification;
    }
}
