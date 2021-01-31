package com.myapplications.cctask_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.model.UsuarioAdm;

import java.util.List;

public class SolicitacoesAdapter extends RecyclerView.Adapter<SolicitacoesAdapter.MyViewHolder> {

    private Context context;
    private List<UsuarioAdm> usuarioAdmList;

    public SolicitacoesAdapter(Context c, List<UsuarioAdm> usuarioAdmList) {
        this.context = c;
        this.usuarioAdmList = usuarioAdmList;
    }

    public List<UsuarioAdm> getUsuarioAdmList() {
        return this.usuarioAdmList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_solicitacoes, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final UsuarioAdm usuarioAdm = usuarioAdmList.get(position);

        if (usuarioAdm.getStatus().equals("aguardando")) {
            holder.nomeUsuario.setText(usuarioAdm.getNome());
            holder.buttonPermitir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usuarioAdm.setStatus("aceito");
                    holder.buttonPermitir.setText("Cancelar");
                    usuarioAdm.atualizarStatus(usuarioAdm.getIdUsuario());
                }
            });
        }  else if(usuarioAdm.getStatus().equals("aceito")) {
            holder.nomeUsuario.setText(usuarioAdm.getNome());
            holder.buttonPermitir.setText("Cancelar");
            holder.buttonPermitir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usuarioAdm.setStatus("aguardando");
                    holder.buttonPermitir.setText("Permitir");
                    usuarioAdm.atualizarStatus(usuarioAdm.getIdUsuario());
                }
            });
        }


//            holder.buttonPermitir.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(usuarioAdm.getStatus().equals("aguardando")){
//                        usuarioAdm.setStatus("aceito");
//                        holder.buttonPermitir.setText("Cancelar");
//                        usuarioAdm.atualizarStatus(usuarioAdm.getIdUsuario());
//                    }else {
//                        usuarioAdm.setStatus("aguardando");
//                        holder.buttonPermitir.setText("Permitir");
//                        usuarioAdm.atualizarStatus(usuarioAdm.getIdUsuario());
//                    }
//
//                }
//            });

    }

    @Override
    public int getItemCount() {
        return usuarioAdmList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //CircleImageView siglaMateria;
        TextView nomeUsuario, siglaNome;
        Button buttonPermitir;

        public MyViewHolder(View itemView) {
            super(itemView);

            buttonPermitir = itemView.findViewById(R.id.buttonPermitirAdm);
            nomeUsuario = itemView.findViewById(R.id.textNomeAdm);
        }
    }
}
