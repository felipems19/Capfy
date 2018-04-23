package capfy.threads;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;


public class ServerRecebeAudio implements Runnable {
    private ServerSocket serverSocketFoto;
    private final int SERVERPORT4 = 8084;
    private InputStream inputAudioRecebido;
    private OutputStream outputAudioRecebido;
    private String pathAudioRecebido;
    private MediaPlayer audioExecutar;

    @Override
    public void run() {
        try {
            serverSocketFoto = new ServerSocket(SERVERPORT4);

            while(true) {
                Socket recebeAudio = serverSocketFoto.accept();
                try{

                    pathAudioRecebido = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Capfy/Audios/audioRecebido.mp3";
                    inputAudioRecebido = recebeAudio.getInputStream();
                    outputAudioRecebido = new FileOutputStream(pathAudioRecebido);

                    byte[] bytes = new byte[16*1024];
                    int count;

                    while ((count = inputAudioRecebido.read(bytes)) > 0) {
                        outputAudioRecebido.write(bytes, 0, count);
                    }

                    outputAudioRecebido.close();
                    inputAudioRecebido.close();
                    recebeAudio.close();

                    startPlaying();



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void startPlaying() {
        audioExecutar = new MediaPlayer();

        try {
            audioExecutar.setDataSource(pathAudioRecebido);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            audioExecutar.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioExecutar.start();

    }



}

