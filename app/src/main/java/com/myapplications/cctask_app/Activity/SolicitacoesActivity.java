package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.adapter.SolicitacoesAdapter;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.UsuarioAdm;
import com.myapplications.cctask_app.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class SolicitacoesActivity extends AppCompatActivity {

    private RecyclerView recyclerSolicitacoes;
    private SolicitacoesAdapter solicitacoesAdapter;
    private Usuario usuario;
    private DatabaseReference firebase;
    private List<UsuarioAdm> usuarioAdmList = new ArrayList<>();
    private String idPrincipal;
    private TextView textNome;
    private Button button;
    private UsuarioAdm usuarioAdm;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitacoes);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Solicitações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idPrincipal = UsuarioFaribase.getIdUsuarioPrincipal();
        firebase = ConfiguracaoFaribase.getDatabase();



        recuperarSolicitacaoAdm();

//        Bundle bundle = getIntent().getExtras();
//        if(bundle != null) {
//            if (bundle.containsKey("idUsuarioAdm")) {
//                UsuarioAdm usuarioTeste = new UsuarioAdm();
//                usuarioTeste = (UsuarioAdm) bundle.getSerializable("solicitacaoUsuario");
//                //String nomeCompleto = usuario.getNome() + " " + usuario.getSobrenome();
//                usuarioAdm.setIdUsuario(usuarioTeste.getIdUsuario());
//                usuarioAdm.setNome(usuarioTeste.getNome());
//            }
//        }

        //textNome = findViewById(R.id.textNomeAdm1);

        textNome = findViewById(R.id.textNomeAdm);
        button = findViewById(R.id.buttonPermitirAdm);

        //recuperaDados();
        recyclerSolicitacoes = findViewById(R.id.recyclerSolicitacoes);

        //configurando adapter
        solicitacoesAdapter = new SolicitacoesAdapter(this, usuarioAdmList);

        //configurar recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerSolicitacoes.setLayoutManager(layoutManager);
        recyclerSolicitacoes.setHasFixedSize(true);
        recyclerSolicitacoes.setAdapter(solicitacoesAdapter);

    }

    private void recuperarSolicitacaoAdm(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

            DatabaseReference databaseReference = firebase.child("usuarios_adm");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usuarioAdmList.clear();

                    for (DataSnapshot ds : snapshot.getChildren()){
                        usuarioAdm = ds.getValue(UsuarioAdm.class);
                        if(!usuarioAdm.getStatus().equals("")){
                            usuarioAdmList.add(usuarioAdm);
                        }

                    }

                    solicitacoesAdapter.notifyDataSetChanged();
                    dialog.dismiss();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
}
