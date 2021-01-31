package com.myapplications.cctask_app.notificacao;

import android.content.Context;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EnviarNotificacao {


    public static void enviar(final Context context, Retrofit retrofit, String titulo, String corpo) {
        //monta objeto notificação

        Notificacao notificacao = new Notificacao(titulo, corpo);

        String to = "/topics/grupoCC";
        NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao);

        NotificacaoService service = retrofit.create(NotificacaoService.class);

        Call<NotificacaoDados> call = service.salvarNotificacao(notificacaoDados);

        call.enqueue(new Callback<NotificacaoDados>() {
            @Override
            public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                if (response.isSuccessful()) {
//                    Toast.makeText(context,
//                            "codigo: " + response.code(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(context,
                            "Notificação enviada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NotificacaoDados> call, Throwable t) {

            }
        });
    }
}
