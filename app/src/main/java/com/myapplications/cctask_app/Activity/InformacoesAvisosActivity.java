package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.helper.Permissao;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.Aviso;
import com.squareup.picasso.Picasso;

import java.io.File;

public class InformacoesAvisosActivity extends AppCompatActivity {

    private TextView campoTitulo, campoData, campoInformacoes, campoArquivo;
    private ImageView imageView;
    private Aviso aviso;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private int STATUS_OK = 200;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_avisos);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Informações do aviso");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //validar permissão
        Permissao.validarPermissoes(permissoes, this, STATUS_OK);

        campoTitulo = findViewById(R.id.textInformacaoTitulo);
        campoData = findViewById(R.id.textInformacaoDataAviso);
        campoInformacoes = findViewById(R.id.textInfoAvisoDescricao);
        campoArquivo = findViewById(R.id.textInfoAvisoArquivo);
        imageView = findViewById(R.id.imageInfoAviso);
        idUsuario = UsuarioFaribase.getIdUsuario();

        //recuperar tarefas
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("dadosAvisos")) {
                aviso = (Aviso) bundle.getSerializable("dadosAvisos");
                campoTitulo.setText(aviso.getTitulo());
                campoInformacoes.setText(aviso.getDescricao());

                String arquivo = aviso.getNomeArquivo();
                if (aviso.getData() != "") {
                    campoData.setText(aviso.getData());
                }

                if (arquivo != null) {
                    campoArquivo.setText(arquivo);

                    campoArquivo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (aviso.getCodeArquivo().equals("100")) {
                                if (idUsuario.equals(aviso.getIdUsuario())) {
                                    Intent intent = new Intent(InformacoesAvisosActivity.this, PdfActivity.class);
                                    intent.putExtra("getdata", aviso.getCaminhoArquivo());
                                    startActivity(intent);
                                } else {
                                    verificaArquivo(aviso.getNomeArquivo());
                                }

                            } else {
                                imageView.setVisibility(View.VISIBLE);
                                Picasso.get().load(String.valueOf(aviso.getCaminhoArquivoFire())).into(imageView);
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
            Intent intent = new Intent(InformacoesAvisosActivity.this, PdfActivity.class);
            intent.putExtra("getArquivo", folder);
            startActivity(intent);
        } else {
            Toast.makeText(this, "abrindo browser!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(aviso.getCaminhoArquivoFire()));
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
