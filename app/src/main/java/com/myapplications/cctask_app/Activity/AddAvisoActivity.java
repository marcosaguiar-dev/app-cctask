package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.adapter.AvisoAdapter;
import com.myapplications.cctask_app.helper.Permissao;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.Aviso;
import com.myapplications.cctask_app.notificacao.EnviarNotificacao;
import com.myapplications.cctask_app.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddAvisoActivity extends AppCompatActivity {

    private ImageView imageAnexo, pdfAnexo;
    private int STATUS_OK = 200;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private DatabaseReference firebaseRef;
    private String idUsuario, urlImagemFire, urlFire = null, urlPdfFire, pdfUrl = null;
    private TextView textNomeArquivo;
    private Aviso aviso;
    private Button buttonSalvar, buttonAlterar, buttonCancelar;
    private MaskEditText textData;
    private EditText editTitulo;
    private MultiAutoCompleteTextView multiDescricao;
    private static final int SELECAO_PDF = 100;
    private static final int SELECAO_GALERIA = 200;
    private Usuario usuario;
    private AlertDialog dialog, dialog1;
    private List<Aviso> avisoList = new ArrayList<>();
    private AvisoAdapter avisoAdapter;
    private ImageView image, imageClose;
    private StorageReference storageReference;
    private LinearLayout linearLayout, linearAviso;
    private String baseUrl;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_aviso);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Adicionar aviso");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //validar permissão
        Permissao.validarPermissoes(permissoes, this, STATUS_OK);

        idUsuario = UsuarioFaribase.getIdUsuario();
        firebaseRef = ConfiguracaoFaribase.getDatabase().child("usuarios").child(idUsuario);
        avisoAdapter = new AvisoAdapter(this, avisoList);
        storageReference = ConfiguracaoFaribase.getStorage();

        image = findViewById(R.id.imageArquivoAviso);
        imageClose = findViewById(R.id.imageCloseAviso);
        imageAnexo = findViewById(R.id.imageAnexoAviso);
        pdfAnexo = findViewById(R.id.pdfAnexoAviso);
        textNomeArquivo = findViewById(R.id.textNomeArquivoAviso);
        editTitulo = findViewById(R.id.editTitulo);
        buttonSalvar = findViewById(R.id.buttonSalvarAviso);
        buttonCancelar = findViewById(R.id.buttonCancelarAviso);
        buttonAlterar = findViewById(R.id.buttonAlterarAviso);
        textData = findViewById(R.id.textDataAviso);
        multiDescricao = findViewById(R.id.MultiDescricaoAviso);
        linearLayout = findViewById(R.id.linearAddAviso);
        linearAviso = findViewById(R.id.linearAviArquivo);
        usuario = new Usuario();

        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pdfAnexo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "PDF"), SELECAO_PDF);
            }
        });

        imageAnexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {//verifica se realmente abriu a câmera do usuario
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        aviso = new Aviso();
        alterarAviso();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //if(requestCode == STATUS_OK && resultCode == Activity.RESULT_OK) {

        if (resultCode == RESULT_OK) {
            try {
                switch (requestCode) {
                    case SELECAO_PDF:

                        Uri uri = data.getData();
                        if (uri != null) {

                            nomeArquivo(SELECAO_PDF);

                            pdfUrl = uri.toString();

                            imageClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    aviso.setCaminhoArquivoFire(null);
                                    linearAviso.setVisibility(View.VISIBLE);
                                }
                            });

                            textNomeArquivo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (urlPdfFire != null) {
                                        Intent intent = new Intent(AddAvisoActivity.this, PdfActivity.class);
                                        intent.putExtra("getdata", pdfUrl);
                                        startActivity(intent);
                                    } else {
                                        salvarArquivo(SELECAO_PDF);
                                        pdfAnexo.setClickable(false);
                                        pdfAnexo.setImageResource(R.drawable.ic_description2_24dp);
                                        imageAnexo.setClickable(false);
                                        imageAnexo.setImageResource(R.drawable.ic_foto2_24dp);
                                        Intent intent = new Intent(AddAvisoActivity.this, PdfActivity.class);
                                        intent.putExtra("getdata", pdfUrl);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        break;

                    case SELECAO_GALERIA:
                        Uri urlImagem = data.getData();
                        if (urlImagem != null) {
                            Bitmap imagemBitmap = null;

                            nomeArquivo(SELECAO_GALERIA);

                            urlFire = urlImagem.toString();

                            imagemBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), urlImagem);

                            final Bitmap finalImagemBitmap = imagemBitmap;

                            imageClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    aviso.setCaminhoArquivoFire(null);
                                    linearAviso.setVisibility(View.VISIBLE);
                                }
                            });

                            textNomeArquivo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (urlImagemFire != null) {
                                        image.setImageBitmap(finalImagemBitmap);
                                        image.setVisibility(View.VISIBLE);
                                    } else {
                                        salvarArquivo(SELECAO_GALERIA);
                                        image.setImageBitmap(finalImagemBitmap);
                                        image.setVisibility(View.VISIBLE);
                                        pdfAnexo.setClickable(false);
                                        pdfAnexo.setImageResource(R.drawable.ic_description2_24dp);
                                        imageAnexo.setClickable(false);
                                        imageAnexo.setImageResource(R.drawable.ic_foto2_24dp);
                                    }

                                    image.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            image.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void excluirArquivo(String arquivoNome) {

        if (urlPdfFire != null && urlImagemFire == null) {
            dialog1 = new SpotsDialog.Builder()
                    .setContext(AddAvisoActivity.this)
                    .setMessage("Excluindo")
                    .setCancelable(false)
                    .build();
            dialog1.show();

            StorageReference arquivo = storageReference.child("PDFs").child("avisos").child(arquivoNome);

            arquivo.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddAvisoActivity.this, "Arquivo excluido!", Toast.LENGTH_SHORT).show();
                    linearAviso.setVisibility(View.GONE);
                    textNomeArquivo.setText(null);
                    pdfAnexo.setClickable(true);
                    urlPdfFire = null;
                    pdfUrl = null;
                    pdfAnexo.setImageResource(R.drawable.ic_description_24dp);
                    imageAnexo.setImageResource(R.drawable.ic_foto_24dp);
                    imageAnexo.setClickable(true);
                    dialog1.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddAvisoActivity.this, "Erro ao excluir arquivo!", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (urlPdfFire == null && urlImagemFire != null) {
            dialog1 = new SpotsDialog.Builder()
                    .setContext(AddAvisoActivity.this)
                    .setMessage("Excluindo")
                    .setCancelable(false)
                    .build();
            dialog1.show();

            StorageReference arquivo = storageReference.child("imagens").child("avisos").child(arquivoNome);

            arquivo.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddAvisoActivity.this, "Arquivo excluido!", Toast.LENGTH_SHORT).show();
                    linearAviso.setVisibility(View.GONE);
                    textNomeArquivo.setText(null);
                    urlFire = null;
                    urlImagemFire = null;
                    pdfAnexo.setClickable(true);
                    pdfAnexo.setImageResource(R.drawable.ic_description_24dp);
                    imageAnexo.setImageResource(R.drawable.ic_foto_24dp);
                    imageAnexo.setClickable(true);
                    dialog1.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddAvisoActivity.this, "Erro ao excluir arquivo!", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            linearAviso.setVisibility(View.GONE);
            textNomeArquivo.setText(null);
            pdfAnexo.setClickable(true);
            pdfAnexo.setImageResource(R.drawable.ic_description_24dp);
            imageAnexo.setImageResource(R.drawable.ic_foto_24dp);
            imageAnexo.setClickable(true);

            if (pdfUrl != null) {
                urlPdfFire = null;
                pdfUrl = null;
            } else {
                urlFire = null;
                urlImagemFire = null;
            }
        }
    }

    public void nomeArquivo(int code) {
        AlertDialog.Builder dialogNome = new AlertDialog.Builder(this);
        dialogNome.setTitle("Nome");
        dialogNome.setMessage("Informe o nome do arquivo para salvar!");

        final EditText ediNomeArquivo = new EditText(this);
        dialogNome.setView(ediNomeArquivo);

        if (code == SELECAO_PDF) {
            ediNomeArquivo.setHint("Ex: Nome.pdf");

            dialogNome.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String nome = ediNomeArquivo.getText().toString();

                    if (!nome.isEmpty()) {
                        if (nome.contains(".pdf")) {
                            aviso.setNomeArquivo(nome);
                            textNomeArquivo.setText(aviso.getNomeArquivo());
                            linearAviso.setVisibility(View.VISIBLE);

                            imageClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    excluirArquivo(aviso.getNomeArquivo());
                                }
                            });
                        } else {
                            aviso.setNomeArquivo(nome + ".pdf");
                            textNomeArquivo.setText(aviso.getNomeArquivo());
                            linearAviso.setVisibility(View.VISIBLE);

                            imageClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    excluirArquivo(aviso.getNomeArquivo());
                                }
                            });
                        }
                    } else {
                        Toast.makeText(AddAvisoActivity.this,
                                "É necessário informar o nome do arquivo!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialogNome.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        } else {
            ediNomeArquivo.setHint("Nome");

            dialogNome.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String nome = ediNomeArquivo.getText().toString();

                    if (!nome.isEmpty()) {
                        aviso.setNomeArquivo(nome + ".jpeg");
                        textNomeArquivo.setText(aviso.getNomeArquivo());
                        linearAviso.setVisibility(View.VISIBLE);

                        imageClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                excluirArquivo(aviso.getNomeArquivo());
                            }
                        });

                    } else {
                        Toast.makeText(AddAvisoActivity.this,
                                "É necessário informar o nome do arquivo!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialogNome.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        AlertDialog dialog = dialogNome.create();
        dialog.show();
    }

    public void salvarDadosAviso(View view) {
        aviso = configuraAviso();

        if (!aviso.getTitulo().isEmpty()) {
            if (!aviso.getDescricao().isEmpty()) {

                firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Usuario usuario = snapshot.getValue(Usuario.class);
                            aviso.setNomeUsuario(usuario.getNome() + " " + usuario.getSobrenome());
                            aviso.setIdUsuario(idUsuario);

                            if (textNomeArquivo.getText() != "") {
                                if (urlImagemFire != null && urlPdfFire == null) {
                                    aviso.setCaminhoArquivo(urlFire);
                                    aviso.setCodeArquivo(String.valueOf(SELECAO_GALERIA));
                                    aviso.setCaminhoArquivoFire(urlImagemFire);

                                    EnviarNotificacao.enviar(AddAvisoActivity.this, retrofit, "Um novo aviso foi adicionado", aviso.getTitulo());

                                    aviso.salvar();
                                    avisoList.add(aviso);
                                    avisoAdapter.notifyDataSetChanged();
                                    finish();

                                    Toast.makeText(AddAvisoActivity.this,
                                            "Aviso adicionado!",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if (urlImagemFire == null && urlPdfFire != null) {
                                    aviso.setCaminhoArquivo(pdfUrl);
                                    aviso.setCodeArquivo(String.valueOf(SELECAO_PDF));
                                    aviso.setCaminhoArquivoFire(urlPdfFire);

                                    EnviarNotificacao.enviar(AddAvisoActivity.this, retrofit, "Um novo aviso foi adicionado", aviso.getTitulo());

                                    aviso.salvar();
                                    avisoList.add(aviso);
                                    avisoAdapter.notifyDataSetChanged();
                                    finish();

                                    Toast.makeText(AddAvisoActivity.this,
                                            "Aviso adicionado!",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(AddAvisoActivity.this, "É necessário carregar os dados do arquivo primeiro", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                aviso.setNomeArquivo(null);
                                aviso.salvar();
                                avisoList.add(aviso);
                                avisoAdapter.notifyDataSetChanged();
                                finish();

                                EnviarNotificacao.enviar(AddAvisoActivity.this, retrofit, "Um novo aviso foi adicionado", aviso.getTitulo());

                                Toast.makeText(AddAvisoActivity.this,
                                        "Aviso adicionado!",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                Toast.makeText(this,
                        "Informe uma descrição!"
                        , Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this,
                    "Informe um título!"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private Aviso configuraAviso() {
        String titulo = editTitulo.getText().toString();
        String data = textData.getText().toString();
        String descricao = multiDescricao.getText().toString();

        aviso.setTitulo(titulo);
        aviso.setData(data);
        aviso.setDescricao(descricao);

        return aviso;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Para anexar um documento é preciso permitir o acesso aos seus arquivos!", Toast.LENGTH_LONG).show();
                pdfAnexo.setClickable(false);
                pdfAnexo.setImageResource(R.drawable.ic_description2_24dp);
                imageAnexo.setClickable(false);
                imageAnexo.setImageResource(R.drawable.ic_foto2_24dp);
            }
        }
    }

    private void alterarAviso() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("atualizarAviso")) {
                final Aviso aviso1 = (Aviso) bundle.getSerializable("atualizarAviso");

                editTitulo.setText(aviso1.getTitulo());
                textData.setText(aviso1.getData());
                multiDescricao.setText(aviso1.getDescricao());
                buttonSalvar.setVisibility(View.GONE);
                imageClose.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                if (aviso1.getData() == null) {
                    textData.setText("00/00/0000");
                }

                if (aviso1.getNomeArquivo() != null) {
                    linearAviso.setVisibility(View.VISIBLE);
                    textNomeArquivo.setText(aviso1.getNomeArquivo());

                    pdfAnexo.setClickable(false);
                    pdfAnexo.setImageResource(R.drawable.ic_description2_24dp);
                    imageAnexo.setClickable(false);
                    imageAnexo.setImageResource(R.drawable.ic_foto2_24dp);

                    if (aviso1.getCodeArquivo().equals("100")) {
                        urlPdfFire = aviso1.getCaminhoArquivoFire();
                    } else {
                        urlImagemFire = aviso1.getCaminhoArquivoFire();
                    }
                }

                imageClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        pdfAnexo.setClickable(true);
//                        pdfAnexo.setImageResource(R.drawable.ic_description_24dp);
//                        imageAnexo.setClickable(true);
//                        imageAnexo.setImageResource(R.drawable.ic_foto_24dp);
//
//                        textNomeArquivo.setText(null);
//                        textNomeArquivo.setVisibility(View.GONE);
//                        imageClose.setVisibility(View.GONE);

                        final AlertDialog.Builder dialogaviso = new AlertDialog.Builder(AddAvisoActivity.this);
                        dialogaviso.setTitle("Tem certeza que deseja excluir este arquivo?");
                        dialogaviso.setMessage("Excluindo o arquivo não terá como desfazer a ação...");

                        dialogaviso.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                excluirArquivo(aviso1.getNomeArquivo());
                                aviso1.setCaminhoArquivo(null);
                                aviso1.setCaminhoArquivoFire(null);
                                aviso1.setCodeArquivo(null);
                                aviso1.setNomeArquivo(null);
                                buttonCancelar.setVisibility(View.GONE);
                            }
                        });

                        dialogaviso.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog dialog = dialogaviso.create();
                        dialog.show();
                    }
                });

                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                aviso1.setTitulo(editTitulo.getText().toString());

                buttonAlterar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!multiDescricao.getText().toString().equals("")) {

                            if (textNomeArquivo.getText() != "") {
                                if (urlImagemFire != null && urlPdfFire == null) {
                                    aviso1.setNomeArquivo(textNomeArquivo.getText().toString());
                                    aviso1.setCodeArquivo(String.valueOf(SELECAO_GALERIA));
                                    aviso1.setCaminhoArquivoFire(urlImagemFire);
                                    aviso1.setCaminhoArquivo(urlFire);
                                    aviso1.setAvisoVisualizacao(null);
                                    if (textData.getText() != null) {
                                        aviso1.setData(textData.getText().toString());
                                    }
                                    aviso1.remover();
                                    aviso1.salvar();

                                    EnviarNotificacao.enviar(AddAvisoActivity.this, retrofit, "Um aviso foi alterado", aviso1.getTitulo());
                                    Toast.makeText(AddAvisoActivity.this,
                                            "Aviso alterado!",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if (urlImagemFire == null && urlPdfFire != null) {
                                    aviso1.setNomeArquivo(textNomeArquivo.getText().toString());
                                    aviso1.setCodeArquivo(String.valueOf(SELECAO_PDF));
                                    aviso1.setCaminhoArquivoFire(urlPdfFire);
                                    aviso1.setCaminhoArquivo(pdfUrl);
                                    aviso1.setAvisoVisualizacao(null);
                                    if (textData.getText() != null) {
                                        aviso1.setData(textData.getText().toString());
                                    }
                                    aviso1.remover();
                                    aviso1.salvar();

                                    EnviarNotificacao.enviar(AddAvisoActivity.this, retrofit, "Um aviso foi alterado", aviso1.getTitulo());
                                    Toast.makeText(AddAvisoActivity.this,
                                            "Aviso alterado!",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(AddAvisoActivity.this, "É necessário carregar os dados do arquivo primeiro", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                aviso1.setAvisoVisualizacao(null);
                                if (textData.getText() != null) {
                                    aviso1.setData(textData.getText().toString());
                                }
                                aviso1.remover();
                                aviso1.salvar();
                                EnviarNotificacao.enviar(AddAvisoActivity.this, retrofit, "Um aviso foi alterado", aviso1.getTitulo());

                                Toast.makeText(AddAvisoActivity.this,
                                        "Aviso alterado!",
                                        Toast.LENGTH_SHORT).show();

                                finish();
                            }
                        } else {
                            Toast.makeText(AddAvisoActivity.this, "Informe uma descrição!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }
    }

    private void salvarArquivo(int code) {
        if (code == SELECAO_GALERIA) {
            //salvar no firebase
            final StorageReference imagemRef = storageReference.child("imagens")
                    .child("avisos")
                    .child(aviso.getNomeArquivo());

            if (urlFire != null) {

                dialog = new SpotsDialog.Builder()
                        .setContext(AddAvisoActivity.this)
                        .setMessage("Aguarde")
                        .setCancelable(false)
                        .build();
                dialog.show();

                UploadTask uploadTask = imagemRef.putFile(Uri.parse(urlFire));
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddAvisoActivity.this,
                                "Erro ao fazer upload da imagem",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri ulrteste = task.getResult();
                                urlImagemFire = ulrteste.toString();
                            }
                        });
                        dialog.dismiss();
                    }
                });
            }
        } else {
            final StorageReference pdfRef = storageReference.child("PDFs")
                    .child("avisos")
                    .child(aviso.getNomeArquivo());

            dialog = new SpotsDialog.Builder()
                    .setContext(AddAvisoActivity.this)
                    .setMessage("Aguarde")
                    .setCancelable(false)
                    .build();
            dialog.show();

            UploadTask uploadTask = pdfRef.putFile(Uri.parse(pdfUrl));
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pdfRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri uri = task.getResult();
                            urlPdfFire = uri.toString();
                        }
                    });
                    dialog.dismiss();
                }
            });
        }
    }
}
