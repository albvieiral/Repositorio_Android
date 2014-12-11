package com.example.asteroides;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class AlmacenPuntuacionesFicheroExterno implements AlmacenPuntuaciones {

	private String FICHERO = Environment.getExternalStorageDirectory() +  "/puntuaciones";
	private String estadoMemoriaExterna;
	private Context context;
	
	public AlmacenPuntuacionesFicheroExterno (Context context) {
		this.context = context;
	}
	
	@Override
	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		try {
			    estadoMemoriaExterna = Environment.getExternalStorageState();
				if (!estadoMemoriaExterna.equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(context, "No se puede escribir en la memoria externa", Toast.LENGTH_LONG).show();
					return;			
				}

				FileOutputStream f = new FileOutputStream(FICHERO, true);
				String texto =  + puntos + " " + nombre + "\n";
				f.write(texto.getBytes());
				f.close();
				 
			} catch (Exception e) {	Log.e("Asteriodes", e.getMessage(), e); }
	}

	@Override
	public Vector<String> listaPuntuaciones(int cantidad) {
		Vector<String> result = new Vector<String>();
		try {
			
		    estadoMemoriaExterna = Environment.getExternalStorageState();
			if (!estadoMemoriaExterna.equals(Environment.MEDIA_MOUNTED) && !estadoMemoriaExterna.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
				Toast.makeText(context, "No se puede escribir en la memoria externa", Toast.LENGTH_LONG).show();
				return result;
			}

			FileInputStream f = new FileInputStream(FICHERO);
			BufferedReader entrada = new BufferedReader(new InputStreamReader(f));
			int n=0 ;
		    String linea;		    
		    do {
		    	linea = entrada.readLine();
		    	if (linea!=null) {
		    		result.add(linea);
		    		n++;
		    	}	
		    } while (n < cantidad && linea != null);
		    f.close();
		} catch (Exception e) {		    
		    	Log.e("Asteriodes", e.getMessage(), e);
		}
		return result;
	}

}
