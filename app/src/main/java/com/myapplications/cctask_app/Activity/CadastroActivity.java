package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.helper.Base64Custom;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.UsuarioAdm;
import com.myapplications.cctask_app.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.ArrayList;
import java.util.Random;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoSobrenome, campoTurma, campoSenha, campoEmail;
    private Button buttonCadastrar, buttonAlterar, buttonCancelar;
    private FirebaseAuth autenticacao;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campoNome = findViewById(R.id.editNomeCadastro);
        campoSobrenome = findViewById(R.id.editSobrenomeCadastro);
        campoEmail = findViewById(R.id.editEmailCadastro);
        campoSenha = findViewById(R.id.editSenhaCadastro);
        campoTurma = findViewById(R.id.editTurmaCadastro);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        buttonCancelar = findViewById(R.id.buttonCadastrarCancelar);
        buttonAlterar = findViewById(R.id.buttonCadastrarAlterar);
        linearLayout = findViewById(R.id.linearCadastro);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            final Usuario usuario = (Usuario) bundle.getSerializable("usuario");
            campoNome.setText(usuario.getNome());
            campoSobrenome.setText(usuario.getSobrenome());
            campoTurma.setText(usuario.getTurma());
            campoEmail.setText(usuario.getEmail());
            buttonCadastrar.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            campoSenha.setText(Base64Custom.decodificar64(usuario.getSenha()));

            final String senhaAtual = Base64Custom.decodificar64(usuario.getSenha());

            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            buttonAlterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usuario.setNome(campoNome.getText().toString());
                    usuario.setSobrenome(campoSobrenome.getText().toString());
                    usuario.setTurma(campoTurma.getText().toString());
                    usuario.setEmail(campoEmail.getText().toString());

                    String novasenha = Base64Custom.codificar64(campoSenha.getText().toString());
                    if(!senhaAtual.equals(novasenha)){
                        usuario.setSenha(novasenha);
                        UsuarioFaribase.atualizaSenha(usuario.getEmail(), senhaAtual, novasenha, CadastroActivity.this);
                    }

                    usuario.atualizar(usuario.getId());

                    Toast.makeText(CadastroActivity.this,
                            "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        }

    public void validarCadastroUsuario(View view){
        //Recuperar textos do campos
        String textoNome = campoNome.getText().toString();
        String textoSobrenome = campoSobrenome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoTurma = campoTurma.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if(!textoNome.isEmpty()){
            if(!textoSobrenome.isEmpty()){
                if(!textoTurma.isEmpty()){
                    if(!textoEmail.isEmpty()){
                        if(!textoSenha.isEmpty()){

                            Usuario usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setSobrenome(textoSobrenome);
                            usuario.setTurma(textoTurma);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(Base64Custom.codificar64(textoSenha));

                            UsuarioAdm usuarioAdm = new UsuarioAdm();
                            usuarioAdm.setNome(textoNome + " " + textoSobrenome);
                            usuarioAdm.setStatus("");

                            cadastrarUsuario(usuario, usuarioAdm);

                        }else {
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha a senha!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o Email!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a turma!",
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this,
                        "Preencha o sobrenome!",
                        Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(CadastroActivity.this,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastrarUsuario(final Usuario usuario, final UsuarioAdm usuarioAdm){
        autenticacao = ConfiguracaoFaribase.getAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(CadastroActivity.this,
                            "Cadastro realizado com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    finish();

                    try {
                        String idUsuario = Base64Custom.codificar64(usuario.getEmail());
                        usuario.setId(idUsuario);
                        codigoCor(usuario);

                        usuario.salvar();

                        usuarioAdm.setIdUsuario(idUsuario);
                        usuarioAdm.salvar();


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {

                    String excecao="";

                    try {
                        throw task.getException(); //recupera a exceção
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha maior!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite um e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Essa conta já foi cadastrada!";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void codigoCor(Usuario usuario){
        ArrayList<Integer> numerosAleatorios = new ArrayList<>();
        for(int i =0; i <=27; i++){
            numerosAleatorios.add(i);
        }

        Random random = new Random();
        int numeroSorteado = 0;
        for (int i = 0; i <=27 ; i++) {
            int num = random.nextInt(numerosAleatorios.size());
            numeroSorteado = numerosAleatorios.get(num);
            numerosAleatorios.remove(num);
//            Log.i("cores", String.valueOf(numeroSorteado));
            //getCor(numeroSorteado);
        }

        usuario.setCor(String.valueOf(numeroSorteado));

    }


}
