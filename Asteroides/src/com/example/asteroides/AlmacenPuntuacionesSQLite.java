package com.example.asteroides;

import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlmacenPuntuacionesSQLite extends SQLiteOpenHelper implements AlmacenPuntuaciones {

	Context context;
	
	public AlmacenPuntuacionesSQLite(Context context) {
		super(context,"puntuaciones", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE puntuaciones (" + 
	               "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				   "puntos INTEGER, nombre TEXT, fecha LONG)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	@Override
	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("INSERT INTO puntuaciones VALUES (null, " + puntos + ",'" + nombre + "'," +  fecha + ")");        
	}

	@Override
	public Vector<String> listaPuntuaciones(int cantidad) {
		Vector<String> result = new Vector<String>();
		SQLiteDatabase db = getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT puntos, nombre FROM puntuaciones ORDER BY puntos DESC LIMIT " + cantidad, null);
	    while (cursor.moveToNext()) {
	    	result.add(cursor.getInt(0) + " " + cursor.getString(1));	    	
	    }
	    cursor.close();
		return result;
	}
	
	
	
}
