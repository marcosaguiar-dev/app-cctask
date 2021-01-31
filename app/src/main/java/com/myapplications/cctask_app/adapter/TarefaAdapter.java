package com.myapplications.cctask_app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.Tarefa;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.MyViewHolder> {

    private Context context;
    private List<Tarefa> tarefas;

    public TarefaAdapter(Context c, List<Tarefa> listaTarefas) {
        this.context = c;
        this.tarefas = listaTarefas;
    }

    public List<Tarefa> getTarefas() {
        return this.tarefas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_tarefas, parent, false);

        return new MyViewHolder(itemLista);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Tarefa tarefa = tarefas.get(position);
        String idUsuario = UsuarioFaribase.getIdUsuario();

        if (tarefa.getTarefaVisualizacao() != null) {
            if (tarefa.getTarefaVisualizacao().equals(idUsuario)) {
                holder.imageCircle.setVisibility(View.GONE);
            }
        }

        holder.nomeMateria.setText(tarefa.getMateria().toUpperCase());

        if (!tarefa.getData().isEmpty()) {
            holder.data.setText(tarefa.getData());
        }

        holder.nomeUsuario.setText("por: " + tarefa.getNomeUsuario().toUpperCase());

        switch (tarefa.getMateria()) {
            case "Aspec Teor da Computação":
                tarefa.setSigla("ATC");
                break;
            case "APS":
                tarefa.setSigla("APS");
                break;
            case "Estudos Disciplinares":
                tarefa.setSigla("ED");
                break;
            case "Administração":
                tarefa.setSigla("AD");
                break;
            case "Trabalho de curso I":
                tarefa.setSigla("TCI");
                break;
            case "Engenharia de Software":
                tarefa.setSigla("ES");
                break;
            case "Sistemas Distribuidos":
                tarefa.setSigla("SD");
                break;
            case "Ciência da Computação Integrada":
                tarefa.setSigla("CCI");
                break;
        }

        holder.siglaMateria.setText(tarefa.getSigla());
    }

    @Override
    public int getItemCount() {
        return tarefas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //CircleImageView siglaMateria;
        TextView nomeMateria, data, nomeUsuario, siglaMateria;
        ConstraintLayout teste;
        CircleImageView circleImageView;
        ImageView imageCircle;

        public MyViewHolder(View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.circleImageSigla);
            teste = itemView.findViewById(R.id.teste);
            siglaMateria = itemView.findViewById(R.id.textSiglaMateria);
            nomeMateria = itemView.findViewById(R.id.textMateria);
            data = itemView.findViewById(R.id.textData);
            nomeUsuario = itemView.findViewById(R.id.textUsuario);
            imageCircle = itemView.findViewById(R.id.imageCirculo);
        }
    }
}
