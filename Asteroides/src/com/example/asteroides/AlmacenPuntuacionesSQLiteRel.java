package com.example.asteroides;

import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlmacenPuntuacionesSQLiteRel extends SQLiteOpenHelper implements AlmacenPuntuaciones {

	Context context;
	
	public AlmacenPuntuacionesSQLiteRel(Context context) {
		super(context,"puntuaciones", null, 2);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE usuarios (" + 
	               "usu_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				   "nombre TEXT, correo TEXT)");
		db.execSQL("CREATE TABLE puntuaciones2 (" + 
	               "pun_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				   "puntos INTEGER," +
	               "fecha LONG," +
				   "usuario INTEGER, FOREIGN KEY (usuario) REFERENCES usuarios (usu_id))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if (oldVersion == 1 && newVersion ==2) {
			//crea tablas nuevas
			onCreate(db);
			Cursor cursor = db.rawQuery("SELECT puntos, nombre, fecha FROM puntuaciones",null);
			while (cursor.moveToNext()) {
				guardarPuntuacion(db,cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
			}
			cursor.close();
			db.execSQL("DROP TABLE puntuaciones");
		}
		
		
	}

	public void guardarPuntuacion(SQLiteDatabase db, int puntos, String nombre, long fecha) {		
		int idUsuario;
		idUsuario = getUsuario(db, nombre);
		db.execSQL("INSERT INTO puntuaciones2 VALUES (null, " + puntos + "," + fecha + "," + idUsuario + ")");        
	}
	
	@Override
	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		SQLiteDatabase db = getWritableDatabase();
		int idUsuario;
		idUsuario = getUsuario(db, nombre);
		db.execSQL("INSERT INTO puntuaciones2 VALUES (null, " + puntos + "," + fecha + "," + idUsuario + ")");        
	}

	private int getUsuario(SQLiteDatabase db, String nombre) {
		Cursor cursor = db.rawQuery("SELECT usu_id FROM usuarios WHERE nombre = '" + nombre + "'", null);
		if (cursor.moveToNext()) {
			return cursor.getInt(0);
		}
		//Si no existe usuario, lo inserta
		else { 	db.execSQL("INSERT INTO usuarios VALUES (null,'" + nombre + "','')");
				return getUsuario(db, nombre);
		}	
	}
	
	@Override
	public Vector<String> listaPuntuaciones(int cantidad) {
		Vector<String> result = new Vector<String>();
		SQLiteDatabase db = getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT puntos, nombre FROM puntuaciones2, usuarios ORDER BY puntos DESC LIMIT " + cantidad, null);
	    while (cursor.moveToNext()) {
	    	result.add(cursor.getInt(0) + " " + cursor.getString(1));	    	
	    }
	    cursor.close();
		return result;
	}
	
	
	
}
