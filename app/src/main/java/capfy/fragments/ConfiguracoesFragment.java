package capfy.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import capfy.begin.HomePage;
import capfy.begin.Login;
import capfy.threads.ServerEnviaFoto;
import felipems.capfy.R;

public class ConfiguracoesFragment extends Fragment {

    private String nomePerfil;
    private TextView nomeTxtView;
    private ImageView fotoDePerfil;
    private Spinner disponibilidade;
    private View sair;
    private Intent backToLogin;
    private Context context;

    private String[] opcoesDeDisponibilidade = new String[]{"Disponivel", "Ocupado", "Ausente"};


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracoes, container, false);

        nomeTxtView = (TextView) view.findViewById(R.id.nomeConfiguracoes);
        fotoDePerfil = (ImageView) view.findViewById(R.id.fotoConfiguracoes);
        disponibilidade = (Spinner) view.findViewById(R.id.disponibilidadeConfiguracoes) ;
        sair = (View) view.findViewById(R.id.viewSair);

        sair.setOnClickListener(listener);
        context = view.getContext();

        backToLogin = new Intent(context, Login.class);
        backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);


        nomePerfil = new String(BuscarDados(context, "profileData"));

        if(!nomePerfil.equals("")) {
            String[] parts = nomePerfil.split("<;;>"); // String array, each element is text between dots
            String firstPartName = parts[0];
            nomeTxtView.setText(firstPartName);

            CarregarImagemDaMemoriaInterna(context,firstPartName);


        }


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item,opcoesDeDisponibilidade);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);




        disponibilidade.setAdapter(adapter);
        disponibilidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int count = 0;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(count == 0)
                {
                    String elementoSpinner = new String(BuscarDados(getActivity(),"profileStatus"));
                    if (elementoSpinner.equals("Disponivel")) disponibilidade.setSelection(0);
                    if (elementoSpinner.equals("Ocupado")) disponibilidade.setSelection(1);
                    if (elementoSpinner.equals("Ausente")) disponibilidade.setSelection(2);
                }
                if(count >= 1) {

                    String fileName = "profileStatus";
                    String content = opcoesDeDisponibilidade[position];
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = getActivity().openFileOutput(fileName, getActivity().MODE_PRIVATE);
                        outputStream.write(content.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    //Toast.makeText(getActivity(), "status selecionado: " + opcoesDeDisponibilidade[position], Toast.LENGTH_LONG).show();
                }
                count++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        return view;

    }



    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.viewSair)
            {
                try {
                    trimCache(context);



                    startActivity(backToLogin);
                    getActivity().finish();

                    android.os.Process.killProcess(android.os.Process.myPid());

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };




    public static void trimCache(Context context) {
        try {
            File dir = context.getFilesDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }







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


    private void CarregarImagemDaMemoriaInterna(Context contexto,String nomeFoto)
    {
        try {
            FileInputStream fis;
            fis = contexto.openFileInput(nomeFoto);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fotoDePerfil.setImageBitmap(b);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}







