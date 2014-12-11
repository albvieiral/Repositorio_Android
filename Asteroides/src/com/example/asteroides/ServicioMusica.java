package com.example.asteroides;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class ServicioMusica extends Service {

	MediaPlayer reproductor;
	private NotificationManager nm;
	private static final int ID_NOTIFICACION_CREAR = 1;
	
	@Override public void onCreate() {
		Toast.makeText(this, "Servicio creado", Toast.LENGTH_LONG);
		reproductor = MediaPlayer.create(this, R.raw.audio);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override public int onStartCommand(Intent intent, int flags, int IdArranque) {
		Toast.makeText(this, "Servicio arrancado", Toast.LENGTH_LONG).show();
		reproductor.start();
		Notification notificacion = new Notification(R.drawable.ic_launcher,"Creando Servicio de Música",System.currentTimeMillis());
		PendingIntent intencionpendiente = PendingIntent.getActivity(this,0, new Intent(this, Asteroides.class),0);
		notificacion.setLatestEventInfo(this,  "Reproduciendo música", "Información adicional", intencionpendiente);
		//Envía notificación al NotificationManager
		nm.notify(ID_NOTIFICACION_CREAR, notificacion);
		//Si el sistema elimina el servicio, le indica que cuando sea posible lo recree de nuevo
		return START_STICKY;
	}
	
	@Override public void onDestroy() {
		Toast.makeText(this, "Servicio destruido", Toast.LENGTH_LONG).show();
		reproductor.stop();
		//Elimina notificación del NotificationManager
		nm.cancel(ID_NOTIFICACION_CREAR);
	}
	
	@Override public IBinder onBind(Intent intent) {
		return null;
	}
}
