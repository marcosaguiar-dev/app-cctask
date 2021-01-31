package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.helper.Permissao;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.Tarefa;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

public class InformacoesTarefaActivity extends AppCompatActivity {

    private TextView campoMateria, campoData, campoInformacoes, campoArquivo;
    private Tarefa tarefa;
    private ImageView imageView;
    private StorageReference storageReference;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private int STATUS_OK = 200;
    private String idUsuario;

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_tarefas);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Informações da tarefa");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //validar permissão
        Permissao.validarPermissoes(permissoes, this, STATUS_OK);

        storageReference = ConfiguracaoFaribase.getStorage().child("imagens").child("tarefas");
        campoMateria = findViewById(R.id.textInformacaoMateria);
        campoData = findViewById(R.id.textInformacaoData);
        campoInformacoes = findViewById(R.id.textInformacaoAdicional);
        campoArquivo = findViewById(R.id.textInformacaoArquivoTarefa);
        imageView = findViewById(R.id.imageInfoTare);
        idUsuario = UsuarioFaribase.getIdUsuario();

        //recuperar tarefas
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("dadosTarefa")) {
                tarefa = (Tarefa) bundle.getSerializable("dadosTarefa");
                campoMateria.setText(tarefa.getMateria());
                campoInformacoes.setText(tarefa.getDescricao());

                if (tarefa.getData() != "") {
                    campoData.setText(tarefa.getData());
                }

                if (tarefa.getNomeArquivo() != null) {
                    campoArquivo.setText(tarefa.getNomeArquivo());
                    campoArquivo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tarefa.getCodeArquivo().equals("100")) {
                                if (idUsuario.equals(tarefa.getIdUsuario())) {
                                    Log.i("path", tarefa.getCaminhoArquivo());
                                    Intent intent = new Intent(InformacoesTarefaActivity.this, PdfActivity.class);
                                    intent.putExtra("getdata", tarefa.getCaminhoArquivo());
                                    startActivity(intent);

                                } else {
                                    verificaArquivo(tarefa.getNomeArquivo());
                                }

                            } else {
                                imageView.setVisibility(View.VISIBLE);
                                Picasso.get().load(String.valueOf(tarefa.getCaminhoArquivoFire())).into(imageView);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        imageView.setVisibility(View.GONE);
                                    }
                                });
                            }

                        }
                    });
                }
            }
        }
    }

    private void verificaArquivo(String nomeArquivo) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/Download/" + nomeArquivo);
        if (folder.exists()) {
            Toast.makeText(this, "Abrindo arquivo!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(InformacoesTarefaActivity.this, PdfActivity.class);
            intent.putExtra("getArquivo", folder);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Abrindo browser!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(tarefa.getCaminhoArquivoFire()));
            startActivity(i);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Para visualizar o anexo (caso tenha) é preciso permitir o acesso aos seus arquivos!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
