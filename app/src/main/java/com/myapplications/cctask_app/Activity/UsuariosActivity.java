package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.adapter.UsuariosAdapter;
import com.myapplications.cctask_app.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsuariosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UsuariosAdapter usuariosAdapter;
    private List<Usuario> usuarioList = new ArrayList<>();
    private ValueEventListener valueEventListener;
    private DatabaseReference firebase;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Usuários");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebase = ConfiguracaoFaribase.getDatabase().child("usuarios");

        recyclerView = findViewById(R.id.recyclerUsuarios);

        //configurando adapter
        usuariosAdapter = new UsuariosAdapter(this, usuarioList);


        //configurar recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(usuariosAdapter);

        recuperarTarefas();
    }

    private void recuperarTarefas(){
        valueEventListener = firebase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioList.clear();

                for(DataSnapshot dados : snapshot.getChildren()){
                    usuario = dados.getValue(Usuario.class);
                    usuarioList.add(usuario);
                }

               usuariosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
