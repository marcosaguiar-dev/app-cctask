package com.myapplications.cctask_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.MyViewHolder> {

    private String idUsuario;
    private Context context;
    private List<Usuario> usuarioList;

    public UsuariosAdapter(Context context, List<Usuario> usuarioList) {
        this.context = context;
        this.usuarioList = usuarioList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_usuarios, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = usuarioList.get(position);

        int codCor = Integer.parseInt(usuario.getCor());

        UsuarioFaribase.retornaCor(holder.cicleImage, codCor);

        String nome = usuario.getNome();
        String sobrenome = usuario.getSobrenome();

        idUsuario = UsuarioFaribase.getIdUsuario();
        if(usuario.getId().equals(idUsuario)){
            holder.nome.setText("Eu");
        }else {
            holder.nome.setText(nome + " " + sobrenome);
        }

        String charNome = Character.toString(nome.charAt(0)).toUpperCase();
        String charSobrenome = Character.toString(sobrenome.charAt(0)).toUpperCase();

        holder.sigla.setText(charNome + charSobrenome);
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView cicleImage;
        private TextView nome, sigla;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cicleImage = itemView.findViewById(R.id.circleImageSiglaUsuario);
            nome = itemView.findViewById(R.id.textNomeUsuario);
            sigla = itemView.findViewById(R.id.textSiglaUsuario);
        }
    }
}
