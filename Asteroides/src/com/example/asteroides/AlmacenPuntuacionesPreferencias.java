package com.example.asteroides;

import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;

public class AlmacenPuntuacionesPreferencias implements AlmacenPuntuaciones {

	private static String PREFERENCIAS = "puntuaciones";
	private Context context;
	
	public AlmacenPuntuacionesPreferencias(Context context) {
		this.context = context;		
	}
	
	@Override
	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		//editor.putString("puntuacion", puntos + " " + nombre);
		for (int n=9; n>=1; n--) {
			editor.putString("puntuacion" + n, pref.getString("puntuacion" + (n-1),"") + " " + nombre);
		}
		editor.putString("puntuacion0", puntos + " " + nombre);
        editor.commit();
	}

	@Override
	public Vector<String> listaPuntuaciones(int cantidad) {
        Vector<String> result = new Vector<String>();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        for (int n=0; n<=9;n++) {
            String s = pref.getString("puntuacion"+n, "");
            if (s!="") {
            	result.add(s);
            }        
        	
        }
		return result;
	}

}
