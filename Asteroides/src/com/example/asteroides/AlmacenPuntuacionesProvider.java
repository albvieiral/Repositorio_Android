package com.example.asteroides;

import java.util.Vector;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class AlmacenPuntuacionesProvider implements AlmacenPuntuaciones {

	private Context context;
	
	public AlmacenPuntuacionesProvider (Context context) {
		this.context = context;
	}	
	
	@Override
	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		Uri uri = Uri.parse("content://com.example.puntuacionesprovider/puntuaciones");
		ContentValues valores = new ContentValues();
		valores.put("puntos", puntos);
		valores.put("nombre", nombre);
		valores.put("fecha", fecha);
		try {
			context.getContentResolver().insert(uri,valores);
		} catch (Exception e) {
			Toast.makeText(context, "Verificar que está instalado com.example.puntuacionesprovider", Toast.LENGTH_LONG).show();
			Log.e("Asteroides", "Error: " + e.toString(), e);
		}
		
        
	}

	@Override
	public Vector<String> listaPuntuaciones(int cantidad) {
		Vector<String> result = new Vector<String>();
		Uri uri = Uri.parse("content://com.example.puntuacionesprovider/puntuaciones");
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, "fecha DESC");		
		if (cursor != null) {
			while(cursor.moveToNext()) {
				String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
				int puntos = cursor.getInt(cursor.getColumnIndex("puntos"));
				result.add(puntos + " " + nombre);
			}
		}
		return result;
	}

}
