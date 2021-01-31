package com.myapplications.cctask_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.myapplications.cctask_app.R;
import com.google.firebase.database.DatabaseReference;

public class AdmFragment extends Fragment {

    private TextView campoKey;
    private Button buttonValidarKey;
    private DatabaseReference keyRef;
    private String idUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adm, container, false);

        return view;
    }

}
