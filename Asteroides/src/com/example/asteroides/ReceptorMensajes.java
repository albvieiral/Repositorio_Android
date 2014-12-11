package com.example.asteroides;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ReceptorMensajes extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, AcercaDe.class);
		//i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(i); 
	}

}
