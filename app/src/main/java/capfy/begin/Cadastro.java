package capfy.begin;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import felipems.capfy.R;

public class Cadastro extends AppCompatActivity {

    Button bCadastra, bFoto;
    EditText eTNome, eTSenha;
    CheckBox esconderSenha;
    ImageView IFoto;
    Intent enviaLogin;
    File rootDirectoty,perfilDirectory,contatosDirectory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //------------------------------------------------
        //Declaração dos elementos e seus respectivos ID's

        bCadastra = (Button) findViewById(R.id.act_cadastrar);
        bFoto = (Button) findViewById(R.id.act_selecionarImg);

        eTNome = (EditText) findViewById(R.id.act_nome);
        eTSenha = (EditText) findViewById(R.id.act_senha);

        esconderSenha = (CheckBox) findViewById(R.id.act_checkBox);
        IFoto = (ImageView) findViewById(R.id.act_fotoPerfil);

        //-------------------------------------------------
        //Inserção do listener no botão

        bCadastra.setOnClickListener(listener);
        bFoto.setOnClickListener(listener);

        //--------------------------------------------------
        //Intent para retorno

        enviaLogin = new Intent(this, Login.class);


        //--------------------------------------------------
        //Criando esquema de pastas


        rootDirectoty = new File(Environment.getExternalStorageDirectory()+ File.separator + "Capfy");
        if(!rootDirectoty.exists()) rootDirectoty.mkdirs();


        contatosDirectory = new File(Environment.getExternalStorageDirectory()+File.separator +"Capfy", "Audios");
        if(!contatosDirectory.exists()) contatosDirectory.mkdirs();
        //--------------------------------------------------


    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            //Método recomendado pela google para abrir galeria. startActivityForResult está declarada lá
            //em baixo. RequestCode=1. Ou seja, cairá no case 1 do onActivityResult

            if (v.getId() == R.id.act_selecionarImg) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }


            //--------------------------------------------------------------------------------------------------
            //Caso a intencao do usuario seja cadastrar, verifica anteriormente se os campos foram preenchidos e
            //caso positivo, cria um arquivo com nome profileData na memoria interna do celular, que é setada
            //como sendo private, ou seja, não acessivel pelo usuário, somente pela aplicação. Dai o método
            //faz uma escrita por outputStream. Logo após ele cria o arquivo imagem do ImageView no root definido pelo File cachPath

            if(v.getId() == R.id.act_cadastrar){

                if((eTNome.getText().toString().equals(""))||(eTSenha.getText().toString().equals(""))){
                    if((eTNome.getText().toString().equals(""))&&(!eTSenha.getText().toString().equals(""))){
                        Toast.makeText(getApplicationContext(), "Nome nao inserido", Toast.LENGTH_LONG).show();
                    }
                    if((!eTNome.getText().toString().equals(""))&&(eTSenha.getText().toString().equals(""))){
                        Toast.makeText(getApplicationContext(), "Senha nao inserida", Toast.LENGTH_LONG).show();
                    }
                    if((eTNome.getText().toString().equals(""))&&(eTSenha.getText().toString().equals(""))){
                        Toast.makeText(getApplicationContext(), "Nenhuma informacao inserida", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    String fileName = "profileData";
                    String content = eTNome.getText() + "<;;>" + eTSenha.getText();

                    FileOutputStream outputStream = null;
                    try {
                        outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                        outputStream.write(content.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    String nomeArquivoStatus = "profileStatus";
                    String conteudoStatus = "Disponivel";

                    FileOutputStream outputStreamStatus = null;
                    try {
                        outputStreamStatus = openFileOutput(nomeArquivoStatus, Context.MODE_PRIVATE);
                        outputStreamStatus.write(conteudoStatus.getBytes());
                        outputStreamStatus.close();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    IFoto.setDrawingCacheEnabled(true);
                    Bitmap bitmap = IFoto.getDrawingCache();

                    FileOutputStream fos = null;
                    try {
                        fos = openFileOutput(eTNome.getText().toString(), Context.MODE_PRIVATE);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                    startActivity(enviaLogin);
                }

            }
        }
    };

    //-----------------------------------------------------------------------------------------------------------
    //Resultado do elemento clicado pelo usuário no Intent pickPhoto. No caso ele pega a foto e
    //seta o imageView

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    IFoto.setImageURI(selectedImage);
                }
                break;
        }
    }

    //--------------------------------------------------------------------------------------------------------------------

}
