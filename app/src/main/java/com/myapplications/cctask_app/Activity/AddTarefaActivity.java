package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.adapter.TarefaAdapter;
import com.myapplications.cctask_app.helper.Permissao;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.notificacao.EnviarNotificacao;
import com.myapplications.cctask_app.model.Tarefa;
import com.myapplications.cctask_app.model.Usuario;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
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

public class AddTarefaActivity extends AppCompatActivity {

    private ImageView imageAnexo, pdfAnexo;
    private int STATUS_OK = 200;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private DatabaseReference firebaseRef;
    private String idUsuario, urlImagemFire, urlimage = null, urlPdfFire, pdfUrl = null;
    private TextView textNomeArquivo, textMateria, textSelecione;
    private Tarefa tarefa;
    private Button buttonTarefa, buttonAlterar, buttonCancelar;
    private Spinner spinnerMateria;
    private MaskEditText textData;
    private MultiAutoCompleteTextView multDescricao;
    private static final int SELECAO_PDF = 100;
    private static final int SELECAO_GALERIA = 200;
    private Usuario usuario;
    private AlertDialog dialog, dialog1;
    private List<Tarefa> tarefaList = new ArrayList<>();
    private TarefaAdapter tarefaAdapter;
    private ImageView image, imageClose;
    private StorageReference storageReference;
    private LinearLayout linearLayout, linearArquivo;
    private String baseUrl;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tarefa);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Adicionar tarefa");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //validar permissão
        Permissao.validarPermissoes(permissoes, this, STATUS_OK);

        idUsuario = UsuarioFaribase.getIdUsuario();
        storageReference = ConfiguracaoFaribase.getStorage();
        firebaseRef = ConfiguracaoFaribase.getDatabase().child("usuarios").child(idUsuario);
        tarefaAdapter = new TarefaAdapter(this, tarefaList);

        image = findViewById(R.id.imageArquivoTarefa);
        imageAnexo = findViewById(R.id.imageAnexo);
        pdfAnexo = findViewById(R.id.pdfAnexo);
        textNomeArquivo = findViewById(R.id.textNomeArquivo);
        buttonTarefa = findViewById(R.id.buttonSalvarTarefa);
        buttonCancelar = findViewById(R.id.buttonCancelarTaref);
        buttonAlterar = findViewById(R.id.buttonAlterarTaref);
        spinnerMateria = findViewById(R.id.spinnerTarefaMateria);
        textData = findViewById(R.id.textData);
        multDescricao = findViewById(R.id.MultiDescricaoTarefa);
        textMateria = findViewById(R.id.textMateria);
        textSelecione = findViewById(R.id.textSelecione);
        imageClose = findViewById(R.id.imageCloseTarefa);
        linearLayout = findViewById(R.id.linearAddTarefa);
        linearArquivo = findViewById(R.id.linearTareArquivo);

        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        usuario = new Usuario();

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

        image.setVisibility(View.GONE);
        tarefa = new Tarefa();
        getMaterias();
        alterarTarefa();
    }


//    public void openDiretorio(Uri uri){
//
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//        //Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
//        startActivityForResult(intent, STATUS_OK);
//
//    }


    public void getMaterias() {
        String[] materias = getResources().getStringArray(R.array.materias);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, materias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMateria.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //if(requestCode == STATUS_OK && resultCode == Activity.RESULT_OK) {

        if (resultCode == RESULT_OK) {
            Bitmap pdf = null;
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
                                    textNomeArquivo.setVisibility(View.GONE);
                                    tarefa.setCaminhoArquivoFire(null);
                                    imageClose.setVisibility(View.GONE);
                                }
                            });

                            textNomeArquivo.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("ResourceAsColor")
                                @Override
                                public void onClick(View v) {

                                    if (urlPdfFire != null) {
                                        Intent intent = new Intent(AddTarefaActivity.this, PdfActivity.class);
                                        intent.putExtra("getdata", pdfUrl);
                                        startActivity(intent);
                                    } else {
                                        salvarArquivo(SELECAO_PDF);
                                        pdfAnexo.setClickable(false);
                                        pdfAnexo.setImageResource(R.drawable.ic_description2_24dp);
                                        imageAnexo.setClickable(false);
                                        imageAnexo.setImageResource(R.drawable.ic_foto2_24dp);
                                        Intent intent = new Intent(AddTarefaActivity.this, PdfActivity.class);
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
                            Bitmap imagemBitmap;

                            nomeArquivo(SELECAO_GALERIA);

                            urlimage = urlImagem.toString();

                            imagemBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), urlImagem);

                            final Bitmap finalImagemBitmap = imagemBitmap;

                            imageClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    textNomeArquivo.setVisibility(View.GONE);
                                    tarefa.setCaminhoArquivoFire(null);
                                    imageClose.setVisibility(View.GONE);
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

    public void nomeArquivo(int code) {
        final AlertDialog.Builder dialogNome = new AlertDialog.Builder(this);
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
                            tarefa.setNomeArquivo(nome);
                            textNomeArquivo.setText(tarefa.getNomeArquivo());
                            linearArquivo.setVisibility(View.VISIBLE);

                            imageClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    excluirArquivo(tarefa.getNomeArquivo());

                                }
                            });

                        } else {
                            tarefa.setNomeArquivo(nome + ".pdf");
                            textNomeArquivo.setText(tarefa.getNomeArquivo());
                            linearArquivo.setVisibility(View.VISIBLE);

                            imageClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    excluirArquivo(tarefa.getNomeArquivo());
                                }
                            });
                        }
                    } else {
                        Toast.makeText(AddTarefaActivity.this,
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
                        tarefa.setNomeArquivo(nome + ".jpeg");
                        textNomeArquivo.setText(tarefa.getNomeArquivo());
                        linearArquivo.setVisibility(View.VISIBLE);

                        imageClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                excluirArquivo(tarefa.getNomeArquivo());
                            }
                        });

                    } else {
                        Toast.makeText(AddTarefaActivity.this,
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

    private void excluirArquivo(String arquivoNome) {

        if (urlPdfFire != null && urlImagemFire == null) {
            dialog1 = new SpotsDialog.Builder()
                    .setContext(AddTarefaActivity.this)
                    .setMessage("Excluindo")
                    .setCancelable(false)
                    .build();
            dialog1.show();

            StorageReference arquivo = storageReference.child("PDFs").child("tarefas").child(arquivoNome);

            arquivo.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddTarefaActivity.this, "Arquivo excluido!", Toast.LENGTH_SHORT).show();
                    linearArquivo.setVisibility(View.GONE);
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
                    Toast.makeText(AddTarefaActivity.this, "Erro ao excluir arquivo!", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (urlPdfFire == null && urlImagemFire != null) {
            dialog1 = new SpotsDialog.Builder()
                    .setContext(AddTarefaActivity.this)
                    .setMessage("Excluindo")
                    .setCancelable(false)
                    .build();
            dialog1.show();

            StorageReference arquivo = storageReference.child("imagens").child("tarefas").child(arquivoNome);

            arquivo.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddTarefaActivity.this, "Arquivo excluido!", Toast.LENGTH_SHORT).show();
                    linearArquivo.setVisibility(View.GONE);
                    textNomeArquivo.setText(null);
                    urlimage = null;
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
                    Toast.makeText(AddTarefaActivity.this, "Erro ao excluir arquivo!", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            linearArquivo.setVisibility(View.GONE);
            textNomeArquivo.setText(null);
            pdfAnexo.setClickable(true);
            pdfAnexo.setImageResource(R.drawable.ic_description_24dp);
            imageAnexo.setImageResource(R.drawable.ic_foto_24dp);
            imageAnexo.setClickable(true);

            if (pdfUrl != null) {
                urlPdfFire = null;
                pdfUrl = null;
            } else {
                urlimage = null;
                urlImagemFire = null;
            }
        }
    }

    public void salvarDadosTarefa(View view) {
        tarefa = configuraTarefa();

        if (!tarefa.getMateria().isEmpty()) {
            //if(!date.isEmpty()){
            if (!tarefa.getDescricao().isEmpty()) {

                firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Usuario usuario = snapshot.getValue(Usuario.class);
                            tarefa.setNomeUsuario(usuario.getNome() + " " + usuario.getSobrenome());
                            tarefa.setIdUsuario(idUsuario);

                            if (textNomeArquivo.getText() != "") {
                                if (urlImagemFire != null && urlPdfFire == null) {
                                    tarefa.setCaminhoArquivo(urlimage);
                                    tarefa.setCodeArquivo(String.valueOf(SELECAO_GALERIA));
                                    tarefa.setCaminhoArquivoFire(urlImagemFire);

                                    EnviarNotificacao.enviar(AddTarefaActivity.this, retrofit, "Nova tarefa foi adicionada", tarefa.getMateria());

                                    tarefa.salvar();
                                    tarefaList.add(tarefa);
                                    tarefaAdapter.notifyDataSetChanged();
                                    finish();

                                    Toast.makeText(AddTarefaActivity.this,
                                            "Tarefa adicionada!",
                                            Toast.LENGTH_SHORT).show();

                                } else if (urlImagemFire == null && urlPdfFire != null) {
                                    tarefa.setCaminhoArquivo(pdfUrl);
                                    tarefa.setCodeArquivo(String.valueOf(SELECAO_PDF));
                                    tarefa.setCaminhoArquivoFire(urlPdfFire);

                                    EnviarNotificacao.enviar(AddTarefaActivity.this, retrofit, "Nova tarefa foi adicionada", tarefa.getMateria());

                                    tarefa.salvar();
                                    tarefaList.add(tarefa);
                                    tarefaAdapter.notifyDataSetChanged();
                                    finish();

                                    Toast.makeText(AddTarefaActivity.this,
                                            "Tarefa adicionada!",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(AddTarefaActivity.this,
                                            "É necessário carregar os dados do arquivo primeiro!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                tarefa.setNomeArquivo(null);
                                tarefa.salvar();
                                tarefaList.add(tarefa);
                                tarefaAdapter.notifyDataSetChanged();
                                finish();

                                EnviarNotificacao.enviar(AddTarefaActivity.this, retrofit, "Nova tarefa foi adicionada", tarefa.getMateria());

                                Toast.makeText(AddTarefaActivity.this,
                                        "Tarefa adicionada!",
                                        Toast.LENGTH_SHORT).show();
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
//            }else {
//                Toast.makeText(this,
//                        "Informe uma data!"
//                        , Toast.LENGTH_SHORT).show();
//            }
        } else {
            Toast.makeText(this,
                    "Selecione uma matéria!"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private Tarefa configuraTarefa() {
        String materia = spinnerMateria.getSelectedItem().toString();
        String data = textData.getText().toString();
        String descricao = multDescricao.getText().toString();

        tarefa.setMateria(materia);
        tarefa.setData(data);
        tarefa.setDescricao(descricao);

        return tarefa;
    }

    private void alterarTarefa() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("atualizarTarefa")) {
                final Tarefa taref = (Tarefa) bundle.getSerializable("atualizarTarefa");

                textSelecione.setText("Matéria selecionada");
                textMateria.setVisibility(View.VISIBLE);
                textMateria.setText(taref.getMateria());
                spinnerMateria.setVisibility(View.GONE);
                buttonTarefa.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                multDescricao.setText(taref.getDescricao());

                if (taref.getNomeArquivo() != null) {
                    linearArquivo.setVisibility(View.VISIBLE);
                    textNomeArquivo.setText(taref.getNomeArquivo());

                    pdfAnexo.setClickable(false);
                    pdfAnexo.setImageResource(R.drawable.ic_description2_24dp);
                    imageAnexo.setClickable(false);
                    imageAnexo.setImageResource(R.drawable.ic_foto2_24dp);

                    if (taref.getCodeArquivo().equals("100")) {
                        urlPdfFire = taref.getCaminhoArquivoFire();
                    } else {
                        urlImagemFire = taref.getCaminhoArquivoFire();
                    }
                }

                if (taref.getData() != null) {
                    textData.setText(taref.getData());
                }

                imageClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder dialogtaref = new AlertDialog.Builder(AddTarefaActivity.this);
                        dialogtaref.setTitle("Tem certeza que deseja excluir este arquivo?");
                        dialogtaref.setMessage("Excluindo o arquivo não terá como desfazer a ação...");

                        dialogtaref.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                excluirArquivo(taref.getNomeArquivo());
                                taref.setCaminhoArquivo(null);
                                taref.setCaminhoArquivoFire(null);
                                taref.setNomeArquivo(null);
                                taref.setCodeArquivo(null);
                                buttonCancelar.setVisibility(View.GONE);
                            }
                        });

                        dialogtaref.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog dialog = dialogtaref.create();
                        dialog.show();
                    }
                });

                buttonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                taref.setMateria(textMateria.getText().toString());

                buttonAlterar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!multDescricao.getText().toString().equals("")) {
                            taref.setDescricao(multDescricao.getText().toString());

                            if (textNomeArquivo.getText() != "") {
                                if (urlImagemFire != null && urlPdfFire == null) {
                                    taref.setNomeArquivo(textNomeArquivo.getText().toString());
                                    taref.setCodeArquivo(String.valueOf(SELECAO_GALERIA));
                                    taref.setCaminhoArquivoFire(urlImagemFire);
                                    taref.setCaminhoArquivo(urlimage);
                                    taref.setTarefaVisualizacao(null);
                                    if (textData.getText() != null) {
                                        taref.setData(textData.getText().toString());
                                    }
                                    taref.remover();
                                    taref.salvar();
                                    EnviarNotificacao.enviar(AddTarefaActivity.this, retrofit, "Uma tarefa foi alterada", taref.getMateria());

                                    Toast.makeText(AddTarefaActivity.this,
                                            "Tarefa alterada!",
                                            Toast.LENGTH_SHORT).show();

                                    finish();
                                } else if (urlImagemFire == null && urlPdfFire != null) {
                                    taref.setNomeArquivo(textNomeArquivo.getText().toString());
                                    taref.setCodeArquivo(String.valueOf(SELECAO_PDF));
                                    taref.setCaminhoArquivoFire(urlPdfFire);
                                    taref.setCaminhoArquivo(pdfUrl);
                                    taref.setTarefaVisualizacao(null);
                                    if (textData.getText() != null) {
                                        taref.setData(textData.getText().toString());
                                    }
                                    taref.remover();
                                    taref.salvar();
                                    EnviarNotificacao.enviar(AddTarefaActivity.this, retrofit, "Uma tarefa foi alterada", taref.getMateria());

                                    Toast.makeText(AddTarefaActivity.this,
                                            "Tarefa alterada!",
                                            Toast.LENGTH_SHORT).show();

                                    finish();
                                } else {
                                    Toast.makeText(AddTarefaActivity.this, "É necessário carregar os dados do arquivo primeiro", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                taref.setTarefaVisualizacao(null);
                                if (textData.getText() != null) {
                                    taref.setData(textData.getText().toString());
                                }
                                taref.remover();
                                taref.salvar();
                                EnviarNotificacao.enviar(AddTarefaActivity.this, retrofit, "Uma tarefa foi alterada", taref.getMateria());

                                Toast.makeText(AddTarefaActivity.this,
                                        "Tarefa alterada!",
                                        Toast.LENGTH_SHORT).show();

                                finish();
                            }
                        } else {
                            Toast.makeText(AddTarefaActivity.this, "Informe uma descrição!", Toast.LENGTH_SHORT).show();
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
                    .child("tarefas")
                    .child(tarefa.getNomeArquivo());

            if (urlimage != null) {

                dialog = new SpotsDialog.Builder()
                        .setContext(AddTarefaActivity.this)
                        .setMessage("Aguarde")
                        .setCancelable(false)
                        .build();
                dialog.show();

                UploadTask uploadTask = imagemRef.putFile(Uri.parse(urlimage));
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddTarefaActivity.this,
                                "Erro ao carregar os dados do arquivo!",
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
                    .child("tarefas")
                    .child(tarefa.getNomeArquivo());

            dialog = new SpotsDialog.Builder()
                    .setContext(AddTarefaActivity.this)
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
}
