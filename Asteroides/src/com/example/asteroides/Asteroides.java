package com.example.asteroides;


import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



public class Asteroides extends Activity implements OnGesturePerformedListener {

	private Button btnJugar;
	private Button btnConfigurar;
	private Button btnAcercaDe;
	private Button btnSalir;
	//public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesArray();
	public static AlmacenPuntuaciones almacen;	
	private Boolean reproducirmusica; 
	private GestureLibrary libreria;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //almacen = new AlmacenPuntuacionesPreferencias(this);
        //almacen = new AlmacenPuntuacionesFicheroInterno(this);
        //almacen = new AlmacenPuntuacionesFicheroExterno(this);
        //almacen = new AlmacenPuntuacionesXML_DOM(this);
        //almacen = new AlmacenPuntuacionesXML_SAX(this);
        //almacen = new AlmacenPuntuacionesSQLite(this);
        almacen = new AlmacenPuntuacionesSQLiteRel(this);
        //almacen = new AlmacenPuntuacionesProvider(this);
        
        //crea evento onClick sobre bot�n Jugar
        btnJugar = (Button) findViewById(R.id.btnJugar);
        btnJugar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//mostrarPreferencias(v);
            	//lanzarPuntuaciones(v);
            	lanzarJuego(v);
            }
        });
                
        //crea evento onClick sobre bot�n Configurar        
        btnConfigurar = (Button) findViewById(R.id.btnConfigurar);
        btnConfigurar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	lanzarPreferencias(v);
            }
        });
        
        //crea evento onClick sobre bot�n Acercade        
        btnAcercaDe = (Button) findViewById(R.id.btnAcercaDe);
        btnAcercaDe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	lanzarAcercaDe(v);
            }
        });        
        
        btnSalir = (Button) findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               finish();
            }
        });
        
        //Si en las preferencias el usuario ha seleccionado que desea reproducir m�sica 
        //se arranca el servicio
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        reproducirmusica = pref.getBoolean("musica", false);
        if (reproducirmusica) {
          //Arranca el servicio de reproducci�n de m�sica
          startService(new Intent(Asteroides.this, ServicioMusica.class));
        }
                
        //Carga librer�a de gestures
        libreria = GestureLibraries.fromRawResource(this, R.raw.gestures);		
		if (!libreria.load()) {
			finish();
		}
		
		GestureOverlayView gesturesView = (GestureOverlayView) findViewById(R.id.gestures);
		gesturesView.addOnGesturePerformedListener((GestureOverlayView.OnGesturePerformedListener) this);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.localizacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
        case R.id.acercade:
        	lanzarAcercaDe(null);
        	break;
        case R.id.config:
        	lanzarPreferencias(null);
        	break;
        }
        return true;

    }
    
    @Override 
    public void onDestroy() {
        super.onDestroy();
    	//Si se sale de la aplicaci�n y se est� reproduciendo m�sica, se para el servicio 
    	//if (reproducirmusica) {    		
    		stopService(new Intent(Asteroides.this, ServicioMusica.class));
    	//}
    }
    
        
    public void lanzarJuego (View view) {
    	Intent i = new Intent(this, Juego.class);
    	startActivityForResult(i, 1234);    	
    }
    
    public void lanzarAcercaDe (View view) {
    	Intent i = new Intent(this, AcercaDe.class);
    	startActivity(i);    	
    }
    
    public void lanzarPreferencias (View view) {
    	Intent i = new Intent(this, Preferencias.class);
    	startActivity(i);    	
    }
    
    public void lanzarPuntuaciones () {
    	Intent i = new Intent(this, Puntuaciones.class);
    	startActivity(i);
    }
    
    public void mostrarPreferencias (View view) {
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    	String s = "m�sica: " + pref.getBoolean("musica", true)
    			+ ",graficos: " +  pref.getString("graficos", "?")
    			+ ",fragmentos: " + "" +  String.valueOf(pref.getInt("fragmentos", 3))
    			+ ",multijugador: " + pref.getBoolean("activar", false)
    			+ ",jugadores: " + "" + String.valueOf(pref.getInt("jugadores", 0)) 
    			+ ",conexi�n: " + pref.getString("conexion", "?");
    	Toast.makeText(this,s,Toast.LENGTH_SHORT).show();    	
    }
    
    @Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    
    	//Recupera la puntuaci�n obtenida, la guarda y muestra lista hist�rica de puntuaciones
    	if (requestCode == 1234 & resultCode == RESULT_OK & data != null) {
    		int puntuacion = data.getExtras().getInt("puntuacion");
    		String nombre = "Yo";
    	    almacen.guardarPuntuacion(puntuacion, nombre, System.currentTimeMillis());
    	    lanzarPuntuaciones();
    	}
    	
    }
    
    @Override
    public void onGesturePerformed(GestureOverlayView ov, Gesture gesture) {
		ArrayList<Prediction> predictions = libreria.recognize(gesture);
		
		//Selecciona opci�n seg�n la gesture
		if (predictions.size()>0) {
			String comando = predictions.get(0).name;
			if (comando.equals("play")) {
				lanzarJuego(null);
			} else if (comando.equals("configurar")) {
				lanzarPreferencias(null);
			} else if (comando.equals("acerca")) {
				lanzarAcercaDe(null);
			} else if (comando.equals("salir")) {
				finish();
			}				
		}		
    }
    
    
}
