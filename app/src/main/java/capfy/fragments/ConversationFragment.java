package capfy.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


import capfy.threads.ClienteEnviarAudio;
import felipems.capfy.R;

public class ConversationFragment extends Fragment {

    ImageView fotoDoContato;
    ImageButton fotoBotaoAudio;
    TextView nomeDoContato, statusDoContato;
    private Button bLigar;
    private MediaRecorder mediaGravacao;
    private View view;
    private String pathAudioLocal,nomeContato, ipContato,fotoContato,statusContato;
    private int ID;
    private long tStart, tEnd, tempoDecorrido;
    private Bundle valoresRecebidos;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pathAudioLocal = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Capfy/Audios/audioGravadoLocalmente.mp3";


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_conversation, container, false);

        fotoDoContato = (ImageView) view.findViewById(R.id.fotoContato);
        fotoBotaoAudio = (ImageButton) view.findViewById(R.id.botaoAudio);
        nomeDoContato = (TextView) view.findViewById(R.id.nomeContato);
        statusDoContato = (TextView) view.findViewById(R.id.statusContato);
        bLigar = (Button) view.findViewById(R.id.ligar);

        fotoBotaoAudio.setOnTouchListener(Objgravacao);
        bLigar.setOnTouchListener(Objgravacao);

        valoresRecebidos = getArguments();
        if (valoresRecebidos != null) {

            nomeContato = valoresRecebidos.getString("nome");
            ipContato = valoresRecebidos.getString("ip");
            fotoContato = valoresRecebidos.getString("foto");
            statusContato = valoresRecebidos.getString("status");
            ID = 1;

            nomeDoContato.setText(nomeContato);
            statusDoContato.setText(statusContato);
            File imgFile = new File(fotoContato);

            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                fotoDoContato.setImageBitmap(myBitmap);
            }

        }
        return view;
    }


    private View.OnTouchListener Objgravacao = new View.OnTouchListener() {
        @Override

        public boolean onTouch(View v, MotionEvent event) {
            if(v.getId() == R.id.ligar){
                if(!nomeDoContato.getText().toString().equals("")) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ID = 0;
                        Thread cThread = new Thread(new ClienteEnviarAudio(pathAudioLocal, ipContato, ID, view.getContext()));
                        Toast.makeText(view.getContext(), "Fazendo ligacao", Toast.LENGTH_SHORT).show();
                        cThread.start();
                        return true;
                    }
                }
            }


            if(v.getId() == R.id.botaoAudio){
                if(!nomeDoContato.getText().toString().equals("")) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startRecording();
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        tEnd = System.nanoTime();
                        tempoDecorrido = tEnd - tStart;
                        if (TimeUnit.NANOSECONDS.toMillis(tempoDecorrido) >= 500) stopRecording();
                        else mediaGravacao.reset();
                        return true;
                    }
                }
            }
            return false;
        }

    };

    protected void startRecording() {

        mediaGravacao = new MediaRecorder();
        mediaGravacao.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaGravacao.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaGravacao.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaGravacao.setOutputFile(pathAudioLocal);

        try {
            mediaGravacao.prepare();
            mediaGravacao.start();
            tStart = System.nanoTime();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Toast.makeText(view.getContext(), "Gravacao Iniciada", Toast.LENGTH_SHORT).show();

    }

    protected void stopRecording() {
        mediaGravacao.stop();
        mediaGravacao.release();
        mediaGravacao = null;

        ID = 1;
        Thread cThread = new Thread(new ClienteEnviarAudio(pathAudioLocal,ipContato,ID, view.getContext()));
        cThread.start();

        Toast.makeText(view.getContext(), "Gravacao Terminada com sucesso",Toast.LENGTH_SHORT).show();

    }


}
