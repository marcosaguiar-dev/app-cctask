package com.myapplications.cctask_app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplications.cctask_app.Activity.AddAvisoActivity;
import com.myapplications.cctask_app.Activity.InformacoesAvisosActivity;
import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.adapter.AvisoAdapter;
import com.myapplications.cctask_app.helper.RecyclerItemClickListener;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.Aviso;
import com.myapplications.cctask_app.model.UsuarioAdm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AvisosFragment extends Fragment {

    private RecyclerView recyclerAviso;
    private List<Aviso> avisoList = new ArrayList<>();
    private AvisoAdapter avisoAdapter;
    private ValueEventListener valueEventListener;
    private DatabaseReference avisosRef;
    private DatabaseReference usuarioAdmRef;
    private String idUsuario;
    private FrameLayout frameLayout;
    private String idPrincipal;
    private UsuarioAdm usuarioAdm;
    private AlertDialog dialog;
    private FloatingActionButton fab;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avisos, container, false);

        fab = view.findViewById(R.id.fabAdicionarAviso);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddAvisoActivity.class);
                startActivity(intent);
            }
        });

        idUsuario = UsuarioFaribase.getIdUsuario();
        idPrincipal = UsuarioFaribase.getIdUsuarioPrincipal();
        avisosRef = ConfiguracaoFaribase.getDatabase().child("avisos");
        usuarioAdmRef = ConfiguracaoFaribase.getDatabase();
        frameLayout = view.findViewById(R.id.frameAviso);
        recyclerAviso = view.findViewById(R.id.recyclerAvisos);

        //configurando adapter
        avisoAdapter = new AvisoAdapter(getActivity(), avisoList);

        //configurar recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerAviso.setLayoutManager(layoutManager);
        recyclerAviso.setHasFixedSize(true);
        recyclerAviso.setAdapter(avisoAdapter);

        //evento de clique no recyclerView
        recyclerAviso.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(), recyclerAviso,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                List<Aviso> listaAvisosAtualizadas = avisoAdapter.getAvisos();

                                Aviso avisoSelecionado = listaAvisosAtualizadas.get(position);

                                if (avisoSelecionado != null) {
                                    avisoSelecionado.setAvisoVisualizacao(idUsuario);
                                    avisoSelecionado.atualizarVisualizacao(avisoSelecionado.getId(), idUsuario);

                                    Intent i = new Intent(getActivity(), InformacoesAvisosActivity.class);
                                    i.putExtra("dadosAvisos", avisoSelecionado);
                                    startActivity(i);
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                final Aviso avisoSelecionado = avisoList.get(position);

                                AlertDialog.Builder dialogAviso = new AlertDialog.Builder(getContext());

                                if (avisoSelecionado != null) {
                                    if (idUsuario.equals(avisoSelecionado.getIdUsuario())) {

                                        dialogAviso.setTitle("Alterar ou excluir aviso");

                                        dialogAviso.setPositiveButton("Alterar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent(getActivity(), AddAvisoActivity.class);
                                                i.putExtra("atualizarAviso", avisoSelecionado);
                                                startActivity(i);
                                            }
                                        });

                                        dialogAviso.setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                avisoSelecionado.remover();
                                            }
                                        });

                                    }

                                }
                                AlertDialog dialog = dialogAviso.create();
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

    public void recuperarAvisos() {
        valueEventListener = avisosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                limparListaAviso();

                for (DataSnapshot dados : snapshot.getChildren()) {
                    if (dados.getValue() != null) {
                        recyclerAviso.setVisibility(View.VISIBLE);
                        Aviso aviso = dados.getValue(Aviso.class);
                        avisoList.add(aviso);

                        if (dados.child(idUsuario).child("avisoVisualizacao").exists()) {
                            String usuarioId = dados.child(idUsuario).child("avisoVisualizacao").getValue().toString();
                            aviso.setAvisoVisualizacao(usuarioId);
                        }
                    }
                }
                avisoAdapter.notifyDataSetChanged();

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

        DatabaseReference databaseReference = usuarioAdmRef.child("usuarios_adm").child(idUsuario);

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
                recuperarAvisos();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void limparListaAviso() {
        avisoList.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarSolicitacaoAdm();
    }

    @Override
    public void onStop() {
        super.onStop();
        avisosRef.removeEventListener(valueEventListener);

    }
}
