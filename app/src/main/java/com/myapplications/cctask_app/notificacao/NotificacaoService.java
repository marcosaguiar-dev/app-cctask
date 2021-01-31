package com.myapplications.cctask_app.notificacao;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificacaoService {

    @Headers({
            "Authorization:key=chave-do-firebase-cloud",
            "Content-Type:application/json"
    })
    @POST("send")
    Call<NotificacaoDados> salvarNotificacao(@Body NotificacaoDados notificacaoDados);
}
