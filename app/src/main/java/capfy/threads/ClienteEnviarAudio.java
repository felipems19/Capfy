package capfy.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClienteEnviarAudio implements Runnable {

    private String ipContato;
    private String pathAudioLocal;
    private final int SERVERPORT4 = 8084;


    public ClienteEnviarAudio (String ipRecebido, String pathRecebido)
    {
        ipContato = ipRecebido;
        pathAudioLocal = pathRecebido;
    }

    public void run() {
    try {

        InetAddress serverAddr = InetAddress.getByName(ipContato);
        Socket socketEnviaAudio = new Socket(serverAddr, SERVERPORT4);

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

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
