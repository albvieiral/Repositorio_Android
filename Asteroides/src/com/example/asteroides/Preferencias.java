package com.example.asteroides;

import java.util.List;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferencias extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		addPreferencesFromResource(R.xml.preferencias);
	}
	
   // public void onBuildHeaders(int resid, List<Header> target) {
   // 	   loadHeadersFromResource(R.xml.preferencias, target);
   // }
	
	
	
}
