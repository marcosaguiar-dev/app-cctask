package com.myapplications.cctask_app.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode){
        if(Build.VERSION.SDK_INT >= 23){
            List<String> listaPermissoes = new ArrayList<>();

            //percorrer permissões
            for(String permissao : permissoes){
                boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao)
                        == PackageManager.PERMISSION_GRANTED;
                if(!temPermissao) listaPermissoes.add(permissao);
            }

            //Se a lista estiver vazia, não é necessário solicitar uma permissão
            if(listaPermissoes.isEmpty()) return true;
            String[] novasPermissoes = new String[listaPermissoes.size()];

            //converte para array devido a 'requestPermissions' receber um array
            listaPermissoes.toArray(novasPermissoes);

            //solicitar permissao
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);
        }

        return true;
    }

}
