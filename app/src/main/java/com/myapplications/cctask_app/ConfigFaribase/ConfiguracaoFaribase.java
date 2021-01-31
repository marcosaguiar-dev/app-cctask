package com.myapplications.cctask_app.ConfigFaribase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFaribase {

    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //retorna a instância do FirebaseDatabase
    public static DatabaseReference getDatabase(){
        if(database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //retorna a instância do FirebaseAuth
    public static FirebaseAuth getAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static StorageReference getStorage(){
        if(storage == null){
            storage= FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
