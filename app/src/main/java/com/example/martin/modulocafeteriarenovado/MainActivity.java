package com.example.martin.modulocafeteriarenovado;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity {

    private TextView txt1 ,txt2;
    private ListView lvw;
    private Spinner spinner , spinner2;

    String[] valores = {"Platillos de otros dias.","Lunes","Martes","Miercoles","Jueves","Viernes","Sábado"};
    String[] valores2 = {"Elija el Tipo de Menú","Universitario => S:/7.50 + REFRESCO","Ejecutivo => S:/8.50 + REFRESCO"};

    public String TipoMenu = "";
    public String FechaNueva="";
    public int Estado = 0;

    ArrayList nombre = new ArrayList();
    ArrayList descripcion = new ArrayList();
    ArrayList imagen = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt1 = (TextView) findViewById(R.id.lbl1);
        txt2 = (TextView)findViewById(R.id.lbl2);
        lvw = (ListView)findViewById(R.id.lvw);
        spinner = (Spinner) findViewById(R.id.spn1);
        spinner2 = (Spinner) findViewById(R.id.spn2);

        diaSemana();


        //Primer algoritmo en comenzar
        if(diaSemana().equals("domingo")){

            txt1.setText("BIENVENIDOS A LA CAFETERIA UTP");
            txt2.setText("HOY '"+String.valueOf("DOMINGO".toUpperCase())+"' NO HABRA ATENCION.");

            TipoMenu = "Universitario".toLowerCase();

            Estado = 2;

            lvw.setAdapter(new ImagenAdapter2(getApplicationContext()));

        }else{

            TipoMenu = "Universitario".toLowerCase();
            //metodo aun no implementado...
            cargarImagen(diaSemana().toString(),TipoMenu.toString());
            Estado = 0;
        }


        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valores));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                if(position!=0) {

                    Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

                   FechaNueva = String.valueOf((String) adapterView.getItemAtPosition(position)).toLowerCase();

                    cargarImagen(FechaNueva.toString(),TipoMenu.toString());
                    Estado = 1;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // vacio

            }
        });

        spinner2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valores2));
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                if(position!=0 && Estado == 0) {

                    //Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

                    String Msg = String.valueOf((String) adapterView.getItemAtPosition(position)).toLowerCase();

                            if(Msg.toUpperCase().contains("UNIVERSITARIO")){

                        //TipoMenu = String.valueOf((String) adapterView.getItemAtPosition(position)).toLowerCase();
                        TipoMenu = "universitario";

                        cargarImagen(diaSemana().toString(),TipoMenu.toString());

                    }else
                            if(Msg.toUpperCase().contains("EJECUTIVO")){

                        //TipoMenu = String.valueOf((String) adapterView.getItemAtPosition(position)).toLowerCase();

                        TipoMenu = "ejecutivo";

                        cargarImagen(diaSemana().toString(),TipoMenu.toString());

                    }else {

                               // Toast.makeText(getApplicationContext(),"AKI SUCEDE ALGO RARO COMPA :V",Toast.LENGTH_LONG).show();

                            }

                }else
                        if(position!=0 && Estado == 1){

                            //Toast.makeText(getApplicationContext(),"AKI SUCEDE ALGO RARO COMPA :V",Toast.LENGTH_LONG).show();

                    String Msg2 = String.valueOf((String) adapterView.getItemAtPosition(position)).toLowerCase();

                    if(Msg2.toUpperCase().contains("UNIVERSITARIO")){

                        //TipoMenu = String.valueOf((String) adapterView.getItemAtPosition(position)).toLowerCase();
                        TipoMenu = "universitario";

                        cargarImagen(FechaNueva.toString(),TipoMenu.toString());

                    }else
                    if(Msg2.toUpperCase().contains("EJECUTIVO")){

                        //TipoMenu = String.valueOf((String) adapterView.getItemAtPosition(position)).toLowerCase();

                        TipoMenu = "ejecutivo";

                        cargarImagen(FechaNueva.toString(),TipoMenu.toString());

                    }else {

                       // Toast.makeText(getApplicationContext(),"AKI SUCEDE ALGO RARO COMPA :V",Toast.LENGTH_LONG).show();

                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // vacio

            }
        });


    }

    public String diaSemana() {

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String fecha = sdf.format(d);
        //Toast.makeText(getApplicationContext(), "" + fecha, Toast.LENGTH_LONG).show();
        // txt1.setText("BIENVENIDO AL MENÚ DEL :"+fecha.toUpperCase());
        txt1.setText("BIENVENIDOS A LA CAFETERIA UTP");
        txt2.setText("HOY '"+String.valueOf(fecha.toUpperCase())+"' TENEMOS: ");

        Estado = 0;

        return fecha;
    }

    private void cargarImagen( String diasemana, String tipomenu){

        nombre.clear();
        descripcion.clear();
        imagen.clear();

        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setMessage("Cargando Datos...");
        progress.show();


        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://examenfinal2016.esy.es/ModuloCafeteriaSimple/index.php";

        //String fecha = String.valueOf(diaSemana());

        RequestParams requestParams = new RequestParams();
        requestParams.add("dia",diasemana.toString());
        requestParams.add("tipo",tipomenu.toString());

        RequestHandle post = client.post(url, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if(statusCode==200){
                    progress.dismiss();

                    try {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));

                        for(int i=0; i<jsonArray.length();i++){

                            nombre.add(jsonArray.getJSONObject(i).getString("Nombre"));
                            descripcion.add(jsonArray.getJSONObject(i).getString("Descripcion"));
                            imagen.add(jsonArray.getJSONObject(i).getString("Foto"));

                        }

                        lvw.setAdapter(new ImagenAdaptar(getApplicationContext()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(getApplicationContext(),"ESTATUS CODE != 200 ",Toast.LENGTH_LONG).show();

            }
        });


    }

    private class ImagenAdaptar extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater;
        SmartImageView smartImageView;
        TextView txtNombre, txtDescripcion;

        public ImagenAdaptar(Context applicationContext) {

            this.context=applicationContext;
            layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagen.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewGroup viewGroup = (ViewGroup)layoutInflater.inflate(R.layout.activity_main_item,null);
            smartImageView = (SmartImageView)viewGroup.findViewById(R.id.imagen1);
            txtNombre = (TextView)viewGroup.findViewById(R.id.tvNombre);
            txtDescripcion = (TextView)viewGroup.findViewById(R.id.tvDescripcion);

            //Revisar codigo para traer imagenes de servidor local
            //Sobre que nombre utiliza para traerlo

            String urlFinal="http://examenfinal2016.esy.es/ModuloCafeteriaSimple/imagenes/"+imagen.get(position).toString();
            Rect rect = new Rect(smartImageView.getLeft(),smartImageView.getTop(),smartImageView.getRight(),smartImageView.getBottom());

            smartImageView.setImageUrl(urlFinal,rect);
            txtNombre.setText(nombre.get(position).toString());

            txtDescripcion.setText(" "+descripcion.get(position).toString());

            return viewGroup ;
        }
    }

    private class ImagenAdapter2 extends BaseAdapter{

        Context context;
        LayoutInflater layoutInflater;
        SmartImageView smartImageView;


        public ImagenAdapter2(Context applicationContext) {

            this.context = applicationContext;
            layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {return 1;}

        @Override
        public Object getItem(int position) {return position;}

        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewGroup viewGroup = (ViewGroup)layoutInflater.inflate(R.layout.activity_main_item2,null);
            smartImageView = (SmartImageView)viewGroup.findViewById(R.id.imagen2);

            String urlFinal="http://examenfinal2016.esy.es/ModuloCafeteriaSimple/imagenes/domingo.jpg";
            Rect rect = new Rect(smartImageView.getLeft(),smartImageView.getTop(),smartImageView.getRight(),smartImageView.getBottom());

            smartImageView.setImageUrl(urlFinal,rect);

            return viewGroup;
        }
    }

}


