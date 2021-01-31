package com.myapplications.cctask_app.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codificar64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT)
                .replaceAll("(\\n|\\r)", "");
    }

    public static String decodificar64(String textoCodificado){
        return new String(Base64.decode(textoCodificado, Base64.DEFAULT));
    }
}
