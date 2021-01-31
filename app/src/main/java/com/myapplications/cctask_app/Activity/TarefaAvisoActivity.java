package com.myapplications.cctask_app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.myapplications.cctask_app.fragments.AvisosFragment;
import com.myapplications.cctask_app.fragments.TarefasFragment;
import com.myapplications.cctask_app.helper.UsuarioFaribase;
import com.myapplications.cctask_app.model.UsuarioAdm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class TarefaAvisoActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private String idUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa_aviso);

        autenticacao = ConfiguracaoFaribase.getAuth();
        idUsuario = UsuarioFaribase.getIdUsuario();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("CCTask");
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic("grupoCC");

        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Tarefas", TarefasFragment.class)
                .add("Avisos", AvisosFragment.class)
                .create()
        );

        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuPerfil:
                abrirPerfil();
                break;
            case R.id.menuSair:
                deslogarUsuario();
                finish();
                break;
            case R.id.menuUsuarios:
                abrirTelaUsuarios();
                break;
            case R.id.menuAdm:
                abrirTelaAdm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario() {
        try {
            autenticacao.signOut();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirPerfil() {
        Intent intent = new Intent(TarefaAvisoActivity.this, PerfilActivity.class);
        startActivity(intent);
    }

    public void abrirTelaAdm() {
        Intent intent = new Intent(TarefaAvisoActivity.this, AdmActivity.class);
        intent.putExtra("id", idUsuario);
        startActivity(intent);
    }

    public void abrirTelaUsuarios() {
        Intent intent = new Intent(TarefaAvisoActivity.this, UsuariosActivity.class);
        startActivity(intent);
    }

//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            ConnectivityManager conmag;
//            conmag = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);
//            conmag.getActiveNetworkInfo();
//            //Verifica o WIFI
//            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
//

}
