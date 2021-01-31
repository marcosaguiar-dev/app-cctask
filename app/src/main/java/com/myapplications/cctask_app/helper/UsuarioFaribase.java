package com.myapplications.cctask_app.helper;

import android.content.Context;

import androidx.annotation.NonNull;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioFaribase {

    public static String getIdUsuario() {
        FirebaseAuth usuario = ConfiguracaoFaribase.getAuth();
        String email = usuario.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificar64(email);

        return idUsuario;
    }

    public static String getIdUsuarioPrincipal() {
        String idUsuario = "id-do-administrador";

        return idUsuario;
    }

    public static FirebaseUser getDadosUsuario() {
        FirebaseAuth usuario = ConfiguracaoFaribase.getAuth();
        return usuario.getCurrentUser();
    }

    public static CircleImageView retornaCor(CircleImageView imageView, int num) {

        if (num == 0) {
            imageView.setImageResource(R.color.darkGray);
        } else if (num == 1) {
            imageView.setImageResource(R.color.Coral);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 2) {
            imageView.setImageResource(R.color.stateBlue);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 3) {
            imageView.setImageResource(R.color.brown);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 4) {
            imageView.setImageResource(R.color.LawnGreen);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 5) {
            imageView.setImageResource(R.color.YellowGreen);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 6) {
            imageView.setImageResource(R.color.DarkOliveGreen);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 7) {
            imageView.setImageResource(R.color.Goldenrod);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 8) {
            imageView.setImageResource(R.color.DarkKhaki);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 9) {
            imageView.setImageResource(R.color.Red);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 10) {
            imageView.setImageResource(R.color.NavajoWhite);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 11) {
            imageView.setImageResource(R.color.BlueViolet);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 12) {
            imageView.setImageResource(R.color.DarkMagenta);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 13) {
            imageView.setImageResource(R.color.Plum);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 14) {
            imageView.setImageResource(R.color.DeepSkyBlue);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 15) {
            imageView.setImageResource(R.color.FireBrick);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 16) {
            imageView.setImageResource(R.color.Orange);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 17) {
            imageView.setImageResource(R.color.DodgerBlue);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 18) {
            imageView.setImageResource(R.color.Aquamarine);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 19) {
            imageView.setImageResource(R.color.SpringGreen);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 20) {
            imageView.setImageResource(R.color.Green);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 21) {
            imageView.setImageResource(R.color.Chocolate);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 22) {
            imageView.setImageResource(R.color.Tomato);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 23) {
            imageView.setImageResource(R.color.Yellow);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 24) {
            imageView.setImageResource(R.color.MediumTurquoise);
            //editCharNome.setTextColor(R.color.white);
        } else if (num == 25) {
            imageView.setImageResource(R.color.Purple);
            //editCharNome.setTextColor(R.color.white);
        } else {
            imageView.setImageResource(R.color.Bisque);
        }

        return imageView;
    }


    public static void atualizaSenha(String email, String senha, final String novaSenha, final Context context) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(email, senha);


        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                user.updatePassword(novaSenha).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(context,
//                                "Senha atualizada", Toast.LENGTH_SHORT).show();
//                    }
//                });
                user.updatePassword(novaSenha).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(context,
//                                "Senha atualizada", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
