package com.example.asteroides;

import android.app.Activity;
import android.os.Bundle;

public class Juego extends Activity {
   
	private VistaJuego vistaJuego;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);
        
        
        vistaJuego = (VistaJuego) findViewById(R.id.vistaJuego);
        //Se informa a vistaJuego de la actividad que le contiene para poder devolver la puntuación desde la vista
        vistaJuego.setPadre(this);
        
    }
	
	@Override protected void onPause() {
		super.onPause();
		vistaJuego.getThread().pausar();
	}
	
	@Override protected void onRestart() {
		super.onRestart();
		vistaJuego.getThread().reanudar();
	}
	
	@Override protected void onDestroy() {
		vistaJuego.getThread().detener();
		super.onDestroy();
	}
	
	

	
	
}
