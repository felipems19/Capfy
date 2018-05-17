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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

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
    private static Context contexto;
    public Thread serverFotoThread,sThread;

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

                        String[] lerDadosDoUltimoContato = null;
                        FileInputStream inStream = null;

                        try {
                            inStream = contexto.openFileInput("todosOsDadosContato");
                            ObjectInputStream din = new ObjectInputStream(inStream);
                            lerDadosDoUltimoContato = (String[]) din.readObject();
                            din.close();
                            inStream.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        catch (IOException a)
                        {
                            a.printStackTrace();
                        }
                        catch (ClassNotFoundException z)
                        {
                            z.printStackTrace();
                        }


                        if(lerDadosDoUltimoContato != null) {

                            Bundle variaveisParaEnviar = new Bundle();
                            variaveisParaEnviar.putString("nome", lerDadosDoUltimoContato[0]);
                            variaveisParaEnviar.putString("ip", lerDadosDoUltimoContato[1]);
                            variaveisParaEnviar.putString("foto", lerDadosDoUltimoContato[2]);
                            variaveisParaEnviar.putString("status", lerDadosDoUltimoContato[3]);

                            fragment.setArguments(variaveisParaEnviar);
                        }
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

        Intent recebimentoDado = getIntent();

        nomeRecebidoLogin = recebimentoDado.getExtras().getString("nomeUsuario");
        String escolhaFragmentInicial = recebimentoDado.getExtras().getString("fragmentInicial");
        String[] vetorDeDadosContato = recebimentoDado.getExtras().getStringArray("dadosContato");

        if (escolhaFragmentInicial.equals("chamarConversation"))
        {
            ConversationFragment fragmentChamadoPorNotification = new ConversationFragment();

            Bundle variaveisParaEnviar = new Bundle();
            variaveisParaEnviar.putString("nome", vetorDeDadosContato[0]);
            variaveisParaEnviar.putString("ip", vetorDeDadosContato[1]);
            variaveisParaEnviar.putString("foto", vetorDeDadosContato[2]);
            variaveisParaEnviar.putString("status", vetorDeDadosContato[3]);

            fragmentChamadoPorNotification.setArguments(variaveisParaEnviar);



            fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.main_container, fragmentChamadoPorNotification);
            ft.commit();
        }
        else {

            usersConnectedFragment fragmentInicial = new usersConnectedFragment();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.main_container, fragmentInicial);
            ft.commit();
        }

        contexto = getApplication();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Context contextoEnviaParaThread = this;
        serverFotoThread = new Thread(new ServerEnviaFoto(nomeRecebidoLogin, contextoEnviaParaThread));
        serverFotoThread.start();

        sThread = new Thread(new ServerRecebeAudio(contextoEnviaParaThread, this));
        sThread.start();

    }




}
