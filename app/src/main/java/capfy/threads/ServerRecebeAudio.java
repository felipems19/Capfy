package capfy.threads;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import capfy.begin.HomePage;
import capfy.begin.Login;
import felipems.capfy.R;


public class ServerRecebeAudio implements Runnable {
    private ServerSocket serverSocketFoto;
    private final int SERVERPORT4 = 8084;
    private InputStream inputAudioRecebido;
    private OutputStream outputAudioRecebido;
    private String pathAudioRecebido;
    private MediaPlayer audioExecutar;
    private String nomeContato, ipContato, statusContato;
    private Context contexto;

    private static HomePage parent;


    public ServerRecebeAudio(Context contexto, HomePage parent)
    {
        this.contexto = contexto;
        this.parent = parent;
    }


    @Override
    public void run() {
        try {
            serverSocketFoto = new ServerSocket(SERVERPORT4);

            while(true) {
                Socket recebeAudio = serverSocketFoto.accept();
                try{
                    inputAudioRecebido = recebeAudio.getInputStream();
                    DataInputStream in = new DataInputStream(inputAudioRecebido);
                    String dadosLigacao = "";
                    //recebendo dados
                    dadosLigacao = in.readUTF();

                    if(!dadosLigacao.equals("n"))
                    {

                        //Separando dados
                        String[] splitDados = dadosLigacao.split("<;;>"); // String array, each element is text between dots
                        nomeContato = splitDados[0];
                        ipContato = splitDados[1];
                        statusContato = splitDados[2];

                        parent.runOnUiThread(new Runnable() {
                            public void run() {

                                Toast.makeText(parent.getBaseContext(), "Dados Recebidos. Nome: "+nomeContato+". Ip: "+ipContato+ ". Status: "+statusContato, Toast.LENGTH_LONG).show();
                            }
                        });


                        //receber foto
                        FileOutputStream outputStreamFotoRecebida = null;
                        try {
                            outputStreamFotoRecebida = contexto.openFileOutput(nomeContato, contexto.MODE_PRIVATE); //Apagar depois o lixo de memória
                            byte[] buffer = new byte[1024]; //valor minimo de bytes -> Reads up to len bytes of data from the input stream into an array of bytes. An attempt is made to read as many as len bytes, but a smaller number may be read. The number of bytes actually read is returned as an integer. […]

                            int bytesRead;
                            long size = in.readLong();
                            while (size > 0 && (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                outputStreamFotoRecebida.write(buffer, 0, bytesRead);
                                size -= bytesRead;
                            }
                            outputStreamFotoRecebida.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            parent.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(contexto, "Arquivo falhou em receber", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        parent.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(contexto, "Arquivo Recebido com sucesso", Toast.LENGTH_LONG).show();
                            }
                        });

                        inputAudioRecebido.close();
                        recebeAudio.close();


                        parent.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(contexto, "Conexao Fechada", Toast.LENGTH_SHORT).show();
                            }
                        });


                        String buscarMeuNome = new String(BuscarDados(contexto,"profileData"));
                        String[] partes = buscarMeuNome.split("<;;>"); // String array, each element is text between dots
                        String meuNome = partes[0];


                        File caminhoFotoContato = new File(contexto.getFilesDir(), nomeContato);
                        String[] dadosContato = {nomeContato,ipContato,caminhoFotoContato.getPath(),statusContato};

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(contexto)
                                        .setSmallIcon(R.mipmap.ic_cf_launcher)
                                        .setContentTitle(nomeContato + " ligando.")
                                        .setContentText("Deseja atender?");
// Creates an explicit intent for an Activity in your app
                        Intent resultIntent = new Intent(contexto, HomePage.class);
                        resultIntent.putExtra("nomeUsuario", meuNome);
                        resultIntent.putExtra("fragmentInicial", "chamarConversation");
                        resultIntent.putExtra("dadosContato", dadosContato);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
// Adds the back stack for the Intent (but not the Intent itself)
                        stackBuilder.addParentStack(HomePage.class);
// Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotificationManager =
                                (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                        mNotificationManager.notify(123, mBuilder.build());



















                    }

                    else
                        {
                        pathAudioRecebido = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Capfy/Audios/audioRecebido.mp3";
                        outputAudioRecebido = new FileOutputStream(pathAudioRecebido);

                        byte[] bytes = new byte[16 * 1024];
                        int count;

                        while ((count = inputAudioRecebido.read(bytes)) > 0) {
                            outputAudioRecebido.write(bytes, 0, count);
                        }

                        outputAudioRecebido.close();
                        inputAudioRecebido.close();
                        recebeAudio.close();


                        parent.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(contexto, "Audio recebido com sucesso", Toast.LENGTH_SHORT).show();                          }
                        });


                        startPlaying();


                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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


        parent.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(contexto, "Audio executando", Toast.LENGTH_SHORT).show();                          }
        });

    }



}

