package capfy.begin;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import capfy.fragments.ConfiguracoesFragment;
import capfy.fragments.ConversationFragment;
import capfy.fragments.usersConnectedFragment;
import capfy.threads.ServerRecebeAudio;
import capfy.threads.ServerEnviaFoto;
import felipems.capfy.R;

public class HomePage extends AppCompatActivity {

    private BottomNavigationView navigation;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private String nomeRecebidoLogin;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            Boolean testeDeMudancaFragment = false;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    if (!(currentFragment instanceof usersConnectedFragment)) {
                        testeDeMudancaFragment = true;
                        fragment = new usersConnectedFragment();
                    }
                    break;

                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    if (!(currentFragment instanceof ConversationFragment)) {
                        testeDeMudancaFragment = true;
                        fragment = new ConversationFragment();
                    }
                    break;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    if (!(currentFragment instanceof ConfiguracoesFragment)) {
                        testeDeMudancaFragment = true;
                        fragment = new ConfiguracoesFragment();
                    }
                    break;
            }
            //final FragmentTransaction transaction = fragmentManager.beginTransaction();
            //transaction.replace(R.id.main_container, fragment).commit();
            if(testeDeMudancaFragment == true) getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment, "TAG").commit();
            return true;


            //return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        usersConnectedFragment fragmentInicial = new usersConnectedFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_container, fragmentInicial);
        ft.commit();


        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Intent recebimentoDado = getIntent();
        nomeRecebidoLogin = recebimentoDado.getExtras().getString("nomeUsuario");

        Context contextoEnviaParaThread = this;
        Thread serverFotoThread = new Thread(new ServerEnviaFoto(nomeRecebidoLogin, contextoEnviaParaThread));
        serverFotoThread.start();

        Thread sThread = new Thread(new ServerRecebeAudio());
        sThread.start();

    }


}
