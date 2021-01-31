package com.myapplications.cctask_app.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplications.cctask_app.Activity.AddTarefaActivity;
import com.myapplications.cctask_app.Activity.InformacoesTarefaActivity;
import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.adapter.TarefaAdapter;
import com.myapplications.cctask_app.helper.RecyclerItemClickListener;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.Tarefa;
import com.myapplications.cctask_app.model.UsuarioAdm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class TarefasFragment extends Fragment {

    private RecyclerView recyclerTarefas;
    private List<Tarefa> tarefaList = new ArrayList<>();
    private TarefaAdapter tarefaAdapter;
    private ValueEventListener valueEventListener;
    private DatabaseReference tarefasRef;
    private DatabaseReference usuarioAdmRef;
    private ImageView imageCircle;
    private String idUsuario;
    private Tarefa tarefa;
    private FrameLayout frameLayout;
    private UsuarioAdm usuarioAdm;
    private AlertDialog dialog;
    private String idPrincipal;
    private FloatingActionButton fab;
    private TextView time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tarefas, container, false);

        fab = view.findViewById(R.id.fabAdicionar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTarefaActivity.class);
                startActivity(intent);
            }
        });


        idUsuario = UsuarioFaribase.getIdUsuario();
        idPrincipal = UsuarioFaribase.getIdUsuarioPrincipal();
        tarefasRef = ConfiguracaoFaribase.getDatabase().child("tarefas");
        usuarioAdmRef = ConfiguracaoFaribase.getDatabase().child("usuarios_adm");
        fab = view.findViewById(R.id.fabAdicionar);
        frameLayout = view.findViewById(R.id.frameTarefa);
        imageCircle = view.findViewById(R.id.imageCirculo);
        time = view.findViewById(R.id.time);

        recyclerTarefas = view.findViewById(R.id.recyclerTarefas);

        //configurando adapter
        tarefaAdapter = new TarefaAdapter(getActivity(), tarefaList);


        //configurar recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerTarefas.setLayoutManager(layoutManager);
        recyclerTarefas.setHasFixedSize(true);
        recyclerTarefas.setAdapter(tarefaAdapter);

        //evento de clique no recyclerView
        recyclerTarefas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(), recyclerTarefas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                List<Tarefa> listaTarefasAtualizadas = tarefaAdapter.getTarefas();

                                Tarefa tarefaSelecionada = listaTarefasAtualizadas.get(position);

                                if (tarefaSelecionada != null) {
                                    tarefaSelecionada.setTarefaVisualizacao(idUsuario);
                                    tarefaSelecionada.atualizarVisualizacao(tarefaSelecionada.getId(), idUsuario);

                                    Intent i = new Intent(getActivity(), InformacoesTarefaActivity.class);
                                    i.putExtra("dadosTarefa", tarefaSelecionada);
                                    startActivity(i);

                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                final Tarefa tarefaSelecionada = tarefaList.get(position);

                                AlertDialog.Builder dialogTarefa = new AlertDialog.Builder(getContext());

                                if (tarefaSelecionada != null) {
                                    if (idUsuario.equals(tarefaSelecionada.getIdUsuario())) {

                                        dialogTarefa.setTitle("Alterar ou excluir tarefa");
                                        //dialogNome.setMessage("Informe o nome do arquivo para salvar!");

                                        dialogTarefa.setPositiveButton("Alterar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent(getActivity(), AddTarefaActivity.class);
                                                i.putExtra("atualizarTarefa", tarefaSelecionada);
                                                startActivity(i);
                                            }
                                        });

                                        dialogTarefa.setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                tarefaSelecionada.remover();
                                            }
                                        });

                                    }

                                }

                                AlertDialog dialog = dialogTarefa.create();
                                dialog.show();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        return view;
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void retornaHora() {
//        LocalDateTime agora = LocalDateTime.now();
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
//        String dataFormatada = formatter.format(agora);
//
//        time.setText(dataFormatada);
//    }

    private void recuperarTarefas() {
        valueEventListener = tarefasRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                limparListaTarefas();

                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (dados.getValue() != null) {
                        recyclerTarefas.setVisibility(View.VISIBLE);
                        tarefa = dados.getValue(Tarefa.class);
                        tarefaList.add(tarefa);

                        if (dados.child(idUsuario).child("tarefaVisualizacao").exists()) {
                            String usuarioId = dados.child(idUsuario).child("tarefaVisualizacao").getValue().toString();
                            tarefa.setTarefaVisualizacao(usuarioId);
                        }
                    }
                }
                tarefaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarSolicitacaoAdm() {
        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference databaseReference = usuarioAdmRef.child(idUsuario);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    usuarioAdm = snapshot.getValue(UsuarioAdm.class);

                    if(idUsuario.equals(UsuarioFaribase.getIdUsuarioPrincipal())){
                        fab.setVisibility(View.VISIBLE);
                    }else {
                        if (usuarioAdm.getStatus().equals("aceito")) {
                            fab.setVisibility(View.VISIBLE);
                        }
                    }
                }
                recuperarTarefas();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void limparListaTarefas() {
        tarefaList.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarSolicitacaoAdm();
    }

    @Override
    public void onStop() {
        super.onStop();
        tarefasRef.removeEventListener(valueEventListener);
    }
}

