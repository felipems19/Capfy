package capfy.threads;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;


public class ServerEnviaFoto implements Runnable {
    private ServerSocket serverSocketFoto;
    private final int SERVERPORT3 = 8083;
    private String nomeDaFotoParaEnviar;
    private OutputStream outFoto;
    private Context contexto;

    public ServerEnviaFoto(String nomeFoto, Context contextoRecebeDeHomepage)
    {
        nomeDaFotoParaEnviar = nomeFoto;
        contexto = contextoRecebeDeHomepage;
    }

    @Override
    public void run() {
        try {
            serverSocketFoto = new ServerSocket(SERVERPORT3);

            while(true) {
                Socket sEnviaFoto = serverSocketFoto.accept();
                try{


                    //Pega dados Referente à foto. Em que o DataInputStream é exatamente o arquivo local. Depois o output vai ser a socket
                    File file = new File(contexto.getFilesDir().getAbsolutePath()+ "/" + nomeDaFotoParaEnviar);
                    byte[] bytes = new byte[(int)file.length()];

                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    DataInputStream dis = new DataInputStream(bis);
                    dis.readFully(bytes, 0, bytes.length);

                    //------------------------------

                    outFoto = sEnviaFoto.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(outFoto);


                    //Instrucao para pegar status do usuario e enviar por um UTF
                    String statusAtualUsuario = new String(BuscarDados(contexto,"profileStatus"));

                    dos.writeUTF(nomeDaFotoParaEnviar+"<;;>"+statusAtualUsuario);
                    dos.writeLong(bytes.length); //pega o tamanho do arquivo e manda
                    dos.write(bytes, 0, bytes.length); //faz a escrita em si

                    //outFoto.write(bytes, 0, bytes.length);
                    //outFoto.flush();
                    dos.flush();
                    outFoto.close();
                    sEnviaFoto.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getLocalIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (enumNetworkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Algo deu errado! " + e.toString() + "\n";
            return null;
        }
        return ip;
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


}

