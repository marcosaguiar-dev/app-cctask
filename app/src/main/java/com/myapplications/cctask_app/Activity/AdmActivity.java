package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.UsuarioAdm;
import com.myapplications.cctask_app.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class AdmActivity extends AppCompatActivity {

    private EditText campoKey;
    private TextView textInformeChave;
    private Button buttonValidarKey;
    private DatabaseReference keyRef;
    private String idUsuarioLogado;
    private String idPrincipal;
    private Usuario usuario;
    private UsuarioAdm usuarioAdm;
    private DatabaseReference firebase;
    private AlertDialog dialog;
    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Usuário Adm");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebase = ConfiguracaoFaribase.getDatabase();
        idUsuarioLogado = UsuarioFaribase.getIdUsuario();
        idPrincipal = UsuarioFaribase.getIdUsuarioPrincipal();

        textInformeChave = findViewById(R.id.textView7);
        campoKey = findViewById(R.id.editCampoKey);
        buttonValidarKey = findViewById(R.id.buttonValidarKey);
        keyRef = ConfiguracaoFaribase.getDatabase();

        recuperarDadosUsuarioAdm();
    }

    private void recuperarDadosUsuarioAdm() {
        if (!idUsuarioLogado.equals(idPrincipal)) {

            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Aguarde")
                    .setCancelable(false)
                    .build();
            dialog.show();


            DatabaseReference usuarioRef = firebase
                    .child("usuarios_adm").child(idUsuarioLogado);

            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        usuarioAdm = snapshot.getValue(UsuarioAdm.class);
                        verificaStatus(usuarioAdm.getStatus());
                    }
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void verificaChave() {
        String chave = campoKey.getText().toString();
        if (!chave.isEmpty()) {
            if (chave.equals("dXN1YXJpb2FkbQ==")) {
                textInformeChave.setText("Aguarde a solicitação");
                buttonValidarKey.setText("Chave enviada");
                buttonValidarKey.setClickable(false);
                campoKey.setHint("dXN1YXJpb2FkbQ==");
                campoKey.setFocusable(false);
                UsuarioAdm usuarioAdm1 = new UsuarioAdm();
                usuarioAdm1.setStatus("aguardando");
                usuarioAdm1.atualizarStatus(idUsuarioLogado);

                Toast.makeText(AdmActivity.this,
                        "Solicitação enviada!", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(AdmActivity.this,
                        "Chave inválida", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void verificaStatus(String status) {
        if (status.equals("aguardando")) {
            textInformeChave.setText("Aguarde a solicitação");
            buttonValidarKey.setText("Chave enviada");
            buttonValidarKey.setClickable(false);
            campoKey.setHint("dXN1YXJpb2FkbQ==");
            campoKey.setFocusable(false);
        } else if (status.equals("aceito")) {
            textInformeChave.setText("Solicitação para adm aceito");
            campoKey.setFocusable(false);
            campoKey.setHint("Você agora é um Administrador");
            buttonValidarKey.setText("Chave enviada");
            buttonValidarKey.setClickable(false);
        }else {
            buttonValidarKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    verificaChave();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (idUsuarioLogado.equals(idPrincipal)) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_solicitacao, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuSolicitacao) {
            Intent intent = new Intent(this, SolicitacoesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
