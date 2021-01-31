package com.myapplications.cctask_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.Aviso;

import java.util.List;

public class AvisoAdapter extends RecyclerView.Adapter<AvisoAdapter.MyViewHolder> {

    private Context context;
    private List<Aviso> avisos;

    public AvisoAdapter(Context c, List<Aviso> listaAvisos){
        this.context = c;
        this.avisos = listaAvisos;
    }

    public List<Aviso> getAvisos(){
        return this.avisos;
    }

    @NonNull
    @Override
    public AvisoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_avisos, parent, false);

        return new AvisoAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AvisoAdapter.MyViewHolder holder, int position) {
        Aviso aviso = avisos.get(position);
        String idUsuario = UsuarioFaribase.getIdUsuario();

        if(aviso.getAvisoVisualizacao() != null){
            if(aviso.getAvisoVisualizacao().equals(idUsuario)){
                holder.imageCirculo.setVisibility(View.GONE);
            }
        }

        holder.titulo.setText(aviso.getTitulo().toUpperCase());

        if(!aviso.getData().isEmpty()){
            holder.data.setText(aviso.getData());
        }

        holder.nomeUsuario.setText("por: " + aviso.getNomeUsuario().toUpperCase());

    }

    @Override
    public int getItemCount() {
        return avisos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        //CircleImageView siglaMateria;
        TextView titulo, data, nomeUsuario;
        ImageView imageCirculo;

        public MyViewHolder(View itemView){
            super(itemView);

            imageCirculo = itemView.findViewById(R.id.imageCirculoAviso);
            titulo = itemView.findViewById(R.id.textTitulo);
            data = itemView.findViewById(R.id.textDataAviso);
            nomeUsuario = itemView.findViewById(R.id.textUsuarioAviso);
        }
    }

}
