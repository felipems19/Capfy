package capfy.begin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import felipems.capfy.R;


public class Login extends AppCompatActivity {

    private Button register;
    private TextView nomeUsuario;
    private Intent iniciarCadastro, iniciarHomePage;
    private String usuario="";
    private ImageView fotoPerfil;
    private View logar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //------------------------------------------------
        //Declaração dos elementos e seus respectivos ID's

        register = (Button) findViewById(R.id.txt_bRegistrar);
        logar = (View) findViewById(R.id.txt_logar);
        nomeUsuario = (TextView) findViewById(R.id.txt_nome);
        fotoPerfil = (ImageView) findViewById(R.id.txt_fotoPerfil);

        //-------------------------------------------------
        //Inserção dos listeners

        register.setOnClickListener(listener);
        logar.setOnClickListener(listener);


        //-------------------------------------------------
        //Declaração das Intents para abrir futuras novas paginas

        iniciarCadastro = new Intent(this, Cadastro.class);
        iniciarHomePage = new Intent(this, HomePage.class);


        //-------------------------------------------------
        //O codigo abaixo tem como objetivo verificar na memoria interna do aparelho por dados de cadastro.
        //Caso encontrado dados (dado pelo método BuscarDados) faz-se o regex definido para separar dois tipos de informação
        //O padrão definido da inclusao dos dados do usuario no arquivo é: nome_completo<;;>senha
        //Separa-se nesse codigo o nome da senha para que o nome vá para o textView e a senha ser posteriormente utilizada.
        //Logo após faz-se a leitura da imagem para o imageView

        Context context = this;
        usuario = new String(BuscarDados(context,"profileData"));

        if(!usuario.equals("")) {
            String[] parts = usuario.split("<;;>"); // String array, each element is text between dots
            String firstPartName = parts[0];
            nomeUsuario.setText(firstPartName);

            CarregarImagemDaMemoriaInterna(context,firstPartName);
        }
    }



    //--------------------------------------------------------
    //Declaracao do listener do botão. Caso a opcao seja registrar é iniciado a activity cadastro
    //Caso a opcao seja logar, verifica-se se tem um usuario cadastrado. Caso sim, abre a activity homepage
    //Caso nao, aparece um toast falando que nao tem cadastro

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.txt_bRegistrar)
            {
                startActivity(iniciarCadastro);
            }
            if (v.getId() == R.id.txt_logar)
            {
                if(usuario != "")
                {
                    iniciarHomePage.putExtra("nomeUsuario", nomeUsuario.getText().toString());
                    startActivity(iniciarHomePage);
                }
                else
                    Toast.makeText(getApplicationContext(), "Usuario nao cadastrado", Toast.LENGTH_LONG).show();
            }
        }
    };



    //-------------------------------------------------------
    //Método BuscarDados. Por default na classe cadastro definiu-se um arquivo cujo nome é profileData
    //para armazenar dados do usuário. O padrão de armazenamento é nome_completo<;;>senha
    //dessa forma é feito a leitura e o retorno com o conteúdo

    private StringBuffer BuscarDados(Context x, String nomeDoLocalInterno)
    {
        int ch;
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;
        try {
            fis = x.openFileInput(nomeDoLocalInterno);
            try {
                while( (ch = fis.read()) != -1)
                    fileContent.append((char)ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    //------------------------------------------------------

    private void CarregarImagemDaMemoriaInterna(Context contexto,String nomeFoto)
    {
        try {
            FileInputStream fis;
            fis = contexto.openFileInput(nomeFoto);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fotoPerfil.setImageBitmap(b);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
