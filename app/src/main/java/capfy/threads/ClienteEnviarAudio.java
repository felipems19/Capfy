package capfy.threads;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class ClienteEnviarAudio implements Runnable {

    private String pathAudioLocal, nomeMeu,ipContato, ipMeu, fotoContato, statusMeu;
    private Context contexto;
    private int ID;
    private final int SERVERPORT4 = 8084;


    public ClienteEnviarAudio (String pathAudioLocal,String ipContato, int ID, Context contexto)
    {
        this.pathAudioLocal = pathAudioLocal;
        this.ipContato = ipContato;
        this.ID = ID;
        this.contexto = contexto;
    }

    public void run() {
    try {

        InetAddress serverAddr = InetAddress.getByName(ipContato);
        Socket socketEnviaAudio = new Socket(serverAddr, SERVERPORT4);

        if(ID == 0)
        {

            ipMeu = getLocalIpAddress();
            statusMeu = new String(BuscarDados(contexto,"profileStatus"));



            String meusDadosProfile = new String(BuscarDados(contexto,"profileData"));
            String[] parts = meusDadosProfile.split("<;;>"); // String array, each element is text between dots
            nomeMeu = parts[0];

            File arquivoMinhaFoto = new File(contexto.getFilesDir(), nomeMeu);
            byte[] bytes = new byte[(int)arquivoMinhaFoto.length()];

            FileInputStream fis = new FileInputStream(arquivoMinhaFoto);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(bytes, 0, bytes.length);


            OutputStream outDados = socketEnviaAudio.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outDados);
            dos.writeUTF(nomeMeu+"<;;>"+ipMeu+"<;;>"+statusMeu);
            dos.writeLong(bytes.length); //pega o tamanho do arquivo e manda
            dos.write(bytes, 0, bytes.length); //faz a escrita em si


            dos.flush();
            outDados.close();
            socketEnviaAudio.close();

        }

        if (ID == 1)
        {
            OutputStream outDados2 = socketEnviaAudio.getOutputStream();
            DataOutputStream dos2 = new DataOutputStream(outDados2);
            dos2.writeUTF("n");

            File pathAudioParaEnviar = new File(pathAudioLocal);

            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(pathAudioParaEnviar);
            OutputStream out = socketEnviaAudio.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
            socketEnviaAudio.close();

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

}
