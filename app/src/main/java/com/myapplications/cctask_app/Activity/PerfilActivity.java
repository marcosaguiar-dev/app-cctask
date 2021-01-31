package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.fragments.AdmFragment;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PerfilActivity extends AppCompatActivity {

    private TextView editCharNome, campoNome, campoTurma, campoEmail;
    private FirebaseUser usuarioAtual;
    private CircleImageView perfil;
    private DatabaseReference usuariofirebase;
    private String idUsuarioAtual;
    private Button buttonAlterar;
    private AlertDialog dialog;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idUsuarioAtual = UsuarioFaribase.getIdUsuario();

        usuarioAtual = UsuarioFaribase.getDadosUsuario();
        usuariofirebase = ConfiguracaoFaribase.getDatabase();

        buttonAlterar = findViewById(R.id.buttonAlterarPerfil);
        campoNome = findViewById(R.id.textNomePerfil);
        campoTurma = findViewById(R.id.textTurmaPerfil);
        campoEmail = findViewById(R.id.textEmailPerfil);
        editCharNome = findViewById(R.id.textCharNome);
        perfil = findViewById(R.id.circleImageViewFotoPerfil);

        recuperaDadosUsuario();

        buttonAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PerfilActivity.this, CadastroActivity.class);
                i.putExtra("usuario", usuario);
                startActivity(i);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void charNome(String nome, String sobrenome) {
        String charNome = Character.toString(nome.charAt(0)).toUpperCase();
        String charSobrenome = Character.toString(sobrenome.charAt(0)).toUpperCase();
        String charConcarternado = charNome + charSobrenome;
        editCharNome.setText(charConcarternado);
    }

    public void recuperaDadosUsuario() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = usuariofirebase
                .child("usuarios")
                .child(idUsuarioAtual);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Usuario usuario1 = snapshot.getValue(Usuario.class);
                    int codeCor = Integer.parseInt(usuario1.getCor());
                    UsuarioFaribase.retornaCor(perfil, codeCor);
                    charNome(usuario1.getNome(), usuario1.getSobrenome());

                    usuario = new Usuario();
                    usuario.setId(usuario1.getId());
                    usuario.setNome(usuario1.getNome());
                    usuario.setSobrenome(usuario1.getSobrenome());
                    usuario.setEmail(usuario1.getEmail());
                    usuario.setTurma(usuario1.getTurma());
                    usuario.setSenha(usuario1.getSenha());

                    campoNome.setText(usuario1.getNome() + " " + usuario1.getSobrenome());
                    campoEmail.setText(usuario1.getEmail());
                    campoTurma.setText(usuario1.getTurma());
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAdm) {
            Intent intent = new Intent(PerfilActivity.this, AdmFragment.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}


