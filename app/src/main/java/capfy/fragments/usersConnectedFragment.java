package capfy.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



import felipems.capfy.R;


public class usersConnectedFragment extends Fragment {
    public static List<Item> items = new ArrayList<Item>();
    public static MyAdapter adapter;
    android.os.Handler customHandler = new android.os.Handler();
    public GridView mGridView;


    public ExecutorService es = Executors.newFixedThreadPool(20);
    public int SERVERPORT3 = 8083;
    public int timeout = 300;
    public String x;

    public View view;

    public static String nomeArquivo = "";
    public static InputStream inFotoRecebida;


    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private ProgressBar loading;

    private static Context contexto;


    public usersConnectedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customHandler.postDelayed(updateTimerThread, 0);

        fragment = new ConversationFragment();
        fragmentManager = getActivity().getSupportFragmentManager();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_users_connected,container,false);
        mGridView = (GridView) view.findViewById(R.id.gridview);

        adapter = new MyAdapter(view.getContext());
        mGridView.setAdapter(adapter);

        loading = (ProgressBar) view.findViewById(R.id.progresso);
        loading.setIndeterminate(true);

        contexto = getActivity();


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {

                String nomeEnvia = items.get(position).descricao;
                String ipEnvia = items.get(position).ip;
                String fotoEnvia = items.get(position).fotoContato.getPath();
                String statusEnvia = items.get(position).status;


                Bundle variaveisParaEnviar = new Bundle();
                variaveisParaEnviar.putString("nome", nomeEnvia);
                variaveisParaEnviar.putString("ip", ipEnvia);
                variaveisParaEnviar.putString("foto", fotoEnvia);
                variaveisParaEnviar.putString("status", statusEnvia);
                fragment.setArguments(variaveisParaEnviar);


                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });


        return view;

    }
    

    private Runnable updateTimerThread = new Runnable(){


            String tx = getLocalIpAddress(), IP = "";

            public void run() {
                loading.setVisibility(View.VISIBLE);

                int i;
                for (i = tx.length() - 1; i >= 0; i--) {
                    if (tx.charAt(i) == '.') break;
                }

                x = tx.substring(0, i + 1);

                for (int a = 0; a <= 245; a++) {
                    IP = x + a;
                    if(!IP.equals(tx)) findSocket(es, IP, SERVERPORT3, timeout);

                }

                /*
                for (int a = 123; a <= 245; a++) {
                    IP = x + a;
                    if(IP.equals(tx) == false) findSocket(es, IP, SERVERPORT3, timeout);
                }
                */
                //loading.setVisibility(View.GONE);
                
                try {
                    adapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                customHandler.postDelayed(this, 10000);
            }
    };



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


    public static Future<Boolean> findSocket(final ExecutorService es, final String ip, final int port, final int timeout) {
        return es.submit(new Callable<Boolean>() {
            Boolean testeExistenciaIP = false;

            @Override
            public Boolean call() {
                try {

                    Socket socketRecebeFoto = new Socket();
                    //socketRecebeFoto.connect(new InetSocketAddress(ip, port), timeout);
                    socketRecebeFoto.connect(new InetSocketAddress(ip, port));

                    //InetAddress serverAddr = InetAddress.getByName(ip);
                    //Socket socketRecebeFoto = new Socket(serverAddr, port);

                    if(socketRecebeFoto.isConnected()) {
                        for (int aux = 0; aux < items.size(); aux++) {
                            if (items.get(aux).ip.equals(ip)) {
                                testeExistenciaIP = true;
                                break;
                            } else testeExistenciaIP = false;
                        }


                        if (testeExistenciaIP == false) {
                            inFotoRecebida = socketRecebeFoto.getInputStream();
                            DataInputStream in = new DataInputStream(inFotoRecebida);
                            nomeArquivo = in.readUTF();

                            String[] divisorDeDadosNomeEStatus = nomeArquivo.split("<;;>"); // String array, each element is text between dots
                            String nomeDaFotoRecebida = divisorDeDadosNomeEStatus[0];
                            String statusDoUsuarioRecebido = divisorDeDadosNomeEStatus[1];





                            FileOutputStream outputStreamFotoRecebida = null;
                            try {
                                outputStreamFotoRecebida = contexto.openFileOutput(nomeDaFotoRecebida, contexto.MODE_PRIVATE);
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

                            }


                            inFotoRecebida.close();
                            socketRecebeFoto.close();


                            File fotoDoContato = new File(contexto.getFilesDir(), nomeDaFotoRecebida);
                            items.add(new Item(nomeDaFotoRecebida, fotoDoContato, ip,statusDoUsuarioRecebido));

                            return true;
                        }
                        else return true;
                    }
                    else return  true;

                } catch (Exception ex) {
                    for (int aux = 0; aux < items.size(); aux++)
                    {
                        if (items.get(aux).ip.equals(ip))
                        {
                            items.remove(aux);

                            File fotoParaTestarExistencia = new File(contexto.getFilesDir(), items.get(aux).descricao);
                            if (fotoParaTestarExistencia.exists()) contexto.deleteFile(items.get(aux).descricao);
                            //items.remove(new Item(items.get(aux).descricao, items.get(aux).fotoContato, ip));
                            testeExistenciaIP = false;
                        }
                        else testeExistenciaIP = false;
                    }
                    return false;
                }
            }
        });
    }


    static class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
        }

        public String getName(int i){
            return items.get(i).descricao;
        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i)
        {
            return items.get(i);
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView fotoUsuarioEncontrado;
            TextView descricaoUsuarioEncontrado;

            if(v == null)
            {
                v = inflater.inflate(R.layout.item_lista_usuariosdisponiveis, viewGroup, false);
                v.setTag(R.id.imagemUsuario, v.findViewById(R.id.imagemUsuario));
                v.setTag(R.id.descricaoUsuario, v.findViewById(R.id.descricaoUsuario));
            }

            fotoUsuarioEncontrado = (ImageView)v.getTag(R.id.imagemUsuario);
            descricaoUsuarioEncontrado = (TextView)v.getTag(R.id.descricaoUsuario);

            Item item = (Item)getItem(i);

            try{
                FileInputStream streamIn = new FileInputStream(item.fotoContato);
                Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
                Drawable e = new BitmapDrawable(bitmap);

                fotoUsuarioEncontrado.setImageDrawable(e);
                descricaoUsuarioEncontrado.setText(item.descricao);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return v;
        }

    }

    public static class Item
    {
        final String descricao;
        final File fotoContato;
        final String ip;
        final String status;

        public Item(String descricao, File fotoContato, String ip, String status)
        {
            this.descricao = descricao;
            this.fotoContato = fotoContato;
            this.ip = ip;
            this.status = status;
        }
    }
}










