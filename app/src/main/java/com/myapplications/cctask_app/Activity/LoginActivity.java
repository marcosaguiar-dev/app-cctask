package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.helper.Base64Custom;
import com.myapplications.cctask_app.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button buttonAcessar;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFaribase.getAuth();

        campoEmail = findViewById(R.id.editEmailAcessar);
        campoSenha = findViewById(R.id.editSenhaAcessar);
        buttonAcessar = findViewById(R.id.buttonAcessar);

    }

    public void abrirTelaCadastro(View view){
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    public void validarAutenticacaoUsuario(View view){
        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();

        if(!email.isEmpty()){
            if(!senha.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setSenha(Base64Custom.codificar64(senha));

                logarUsuario(usuario);

            }else {
                Toast.makeText(LoginActivity.this,
                        "Preencha a senha!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(LoginActivity.this,
                    "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
        }
    }

    public void logarUsuario(Usuario usuario){
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaPrincipal();
                }else {

                    String excecao="";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não está cadastrado!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail ou senha inválidos!";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,
                            excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, TarefaAvisoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null){
            abrirTelaPrincipal();
        }
    }
}
