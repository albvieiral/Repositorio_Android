package com.example.asteroides;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class VistaJuego extends View implements SensorEventListener{

    ////// ASTEROIDES //////
    private Vector<Grafico> Asteroides; // Vector con los Asteroides
    private int numAsteroides= 5; // Número inicial de asteroides
    private int numFragmentos= 3; // Fragmentos en que se divide

    // //// NAVE //////
    private Grafico nave;// Gráfico de la nave
    private int giroNave; // Incremento de dirección
    private float aceleracionNave; // aumento de velocidad
    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;
    
 // //// MISIL //////
    private Grafico misil;// Gráfico del misil
    private static final int PASO_VELOCIDAD_MISIL = 12;
    private boolean misilActivo= false;
    private int tiempoMisil;
    
    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;
    // Para controlar que no exceda la velocidad
    private static int MAX_VELOCIDAD_NAVE = 500000;
	
    private float mX=0, mY=0;
    private boolean disparo=false;
    
    private boolean sensorOrientacionActivado;
    private boolean hayValorInicial= false;
    private float valorInicial;
    private int numCambios = 0;
    
    private int puntuacion = 0;
    
    
    //Reproducción de sonidos  
    private SoundPool soundPool;
    private int idDisparo,idExplosion;
    
    //Se guarda la actividad que muestra la vista para facilitar devolver la puntuación a la actividad principal
    private Activity padre;
    
    public VistaJuego(Context context, AttributeSet attrs) {
          super(context, attrs);

          soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
          idDisparo = soundPool.load(context, R.raw.disparo,1);
          idExplosion = soundPool.load(context, R.raw.explosion,1);
          
          //mpDisparo = MediaPlayer.create(context, R.raw.disparo);
          
          Drawable drawableNave, drawableAsteroide, drawableMisil;
          
          //recupera de las preferencias el tipo de gráfico a visualizar (bitmap(=1), vectorial(=0))
          SharedPreferences pref = context.getSharedPreferences("com.example.asteroides_preferences", Context.MODE_PRIVATE);
          if (pref.getString("graficos", "1").equals("0")) {
        	  Path pathAsteroide = new Path();
              pathAsteroide.moveTo((float) 0.3, (float) 0.0);
              pathAsteroide.lineTo((float) 0.6, (float) 0.0);
              pathAsteroide.lineTo((float) 0.6, (float) 0.3);
              pathAsteroide.lineTo((float) 0.8, (float) 0.2);
              pathAsteroide.lineTo((float) 1.0, (float) 0.4);
              pathAsteroide.lineTo((float) 0.8, (float) 0.6);
              pathAsteroide.lineTo((float) 0.9, (float) 0.9);
              pathAsteroide.lineTo((float) 0.8, (float) 1.0);
              pathAsteroide.lineTo((float) 0.4, (float) 1.0);
              pathAsteroide.lineTo((float) 0.0, (float) 0.6);
              pathAsteroide.lineTo((float) 0.0, (float) 0.2);
              pathAsteroide.lineTo((float) 0.3, (float) 0.0);
              ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 1, 1));
              dAsteroide.getPaint().setColor(Color.WHITE);
              dAsteroide.getPaint().setStyle(Style.STROKE);
              dAsteroide.setIntrinsicWidth(50);
              dAsteroide.setIntrinsicHeight(50);
              drawableAsteroide = dAsteroide;
              
              ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
              dMisil.getPaint().setColor(Color.WHITE);
              dMisil.getPaint().setStyle(Style.STROKE);
              dMisil.setIntrinsicWidth(15);
              dMisil.setIntrinsicHeight(3);
              drawableMisil = dMisil;
                            
        	  Path pathNave = new Path();
        	  pathNave.moveTo((float) 0.0, (float) 0.0);
        	  pathNave.lineTo((float) 1, (float) 0.5);
        	  pathNave.lineTo((float) 0.0, (float) 1.0);
              pathNave.lineTo((float) 0.0, (float) 0.0);
              ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 1, 1));
              dNave.getPaint().setColor(Color.WHITE);
              dNave.getPaint().setStyle(Style.STROKE);
              dNave.setIntrinsicWidth(20);
              dNave.setIntrinsicHeight(15);
              drawableNave = dNave;
              
              setBackgroundColor(Color.BLACK);                            
        	  }
          else {
        	  int numAsteroide = Math.abs((int) Math.random()*3)+1; 
        	  switch (numAsteroide) {        	      
        	  case 1: 
        		  drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
        		  break;
        	  case 2:
        		  drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide2);
        		  break;        		  
        	  default:
        		  drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide3);
        		  break;        		          		  
        	  }
              drawableNave = context.getResources().getDrawable(R.drawable.nave);    
              drawableMisil = context.getResources().getDrawable(R.drawable.misil1);
          }
          
          nave = new Grafico(this, drawableNave);
          misil = new Grafico(this, drawableMisil);
          
          Asteroides = new Vector<Grafico>();
          for (int i = 0; i < numAsteroides; i++) {
                Grafico asteroide = new Grafico(this, drawableAsteroide);
                asteroide.setIncY(Math.random() * 4 - 2);
                asteroide.setIncX(Math.random() * 4 - 2);
                asteroide.setAngulo((int) (Math.random() * 360));
                asteroide.setRotacion((int) (Math.random() * 8 - 4));
                Asteroides.add(asteroide);
          }
          
          sensorOrientacionActivado = pref.getBoolean("orientacion", false);
          if (sensorOrientacionActivado) {
        	  SensorManager mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        	  Sensor orientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        	  mSensorManager.registerListener((SensorEventListener) this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
          }	  
          
    }

    @Override protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
          super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);

          // Una vez que conocemos nuestro ancho y alto.
          for (Grafico asteroide: Asteroides) {
                asteroide.setPosX(Math.random()*(ancho-asteroide.getAncho()));
                asteroide.setPosY(Math.random()*(alto-asteroide.getAlto()));
          }
          nave.setPosX((ancho/2)-(nave.getAncho()/2));
          nave.setPosY(alto/2-(nave.getAlto()/2));
          
          ultimoProceso = System.currentTimeMillis();
          thread.start();
    }

    @Override protected void onDraw(Canvas canvas) {
          super.onDraw(canvas);

          for (Grafico asteroide: Asteroides) {
              asteroide.dibujaGrafico(canvas);
          }
          nave.dibujaGrafico(canvas);
          if (misilActivo)  {
        	  misil.dibujaGrafico(canvas);
          }
          

    }
    
    @Override public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
    	super.onKeyDown(codigoTecla, evento);
    	
    	boolean procesada = true;
    	Toast.makeText(null, "He pulsado tecla" + codigoTecla, Toast.LENGTH_LONG).show();
    	switch(codigoTecla) {
    	case KeyEvent.KEYCODE_DPAD_UP:
    		aceleracionNave = +PASO_ACELERACION_NAVE;
    		break;
    	    	
    	case KeyEvent.KEYCODE_DPAD_LEFT:
    		giroNave = -PASO_GIRO_NAVE;
    		break;

    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		giroNave = +PASO_GIRO_NAVE;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_CENTER:

    	case KeyEvent.KEYCODE_ENTER:
    		ActivarMisil();
    		break;
    		
    	default:
    		//si no es ninguna tecla esperada por la aplicación 
    		procesada = false;
    		break;
    	}
    	
    	return procesada;
    }
    
    
    @Override public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
    	super.onKeyUp(codigoTecla, evento);
    	
    	boolean procesada = true;
    	Toast.makeText(null, "He pulsado tecla" + codigoTecla, Toast.LENGTH_LONG).show();
    	switch(codigoTecla) {
    	case KeyEvent.KEYCODE_DPAD_UP:
    		aceleracionNave = 0;
    		break;
    	    	
    	case KeyEvent.KEYCODE_DPAD_LEFT:

    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		giroNave = 0;
    		break;
    		
    	case KeyEvent.KEYCODE_DPAD_CENTER:

    	default:
    		//si no es ninguna tecla esperada por la aplicación 
    		procesada = false;
    		break;
    	}
    	
    	return procesada;
    }
    
    @Override
    public boolean onTouchEvent (MotionEvent event) {
       super.onTouchEvent(event);
    	   float x = event.getX();
    	   float y = event.getY();
    	   switch (event.getAction()) {
    	   case MotionEvent.ACTION_DOWN:
    		  disparo=true;
              break;
    	   case MotionEvent.ACTION_MOVE:
    	      //Si está activado el sensor de orientación, solo se puede utilizar la pantalla táctil para disparar
              if (!sensorOrientacionActivado) {
    	   
	              float dx = Math.abs(x - mX);
    	          float dy = Math.abs(y - mY);
        	      if (dy<6 && dx>6){
            	      giroNave = Math.round((x - mX) / 2);
                     disparo = false;
              	} else if (dx<6 && dy>6){
                     aceleracionNave = Math.round((mY - y) / 25);
                     disparo = false;
              		}
              }		
              break;
    	   case MotionEvent.ACTION_UP:
              giroNave = 0;
              aceleracionNave = 0;
              if (disparo){
                ActivarMisil();
                disparo = false;
              }
              break;
    	   }
    	   mX=x; mY=y;
       return true;
    }
    
    @Override 
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        
    @Override 
    public void onSensorChanged(SensorEvent event) {
        //Se comprueba que esté activado el sensor de orientación. Si está activado, solo se puede utilizar la pantalla táctil para disparar
    	if (sensorOrientacionActivado) {
    		float x = event.values[0];
    		float y = event.values[1];
    		if (!hayValorInicial) {
    			mX = x;
    			mY = y;
    			hayValorInicial = true;
    		}
    		if (numCambios == 5000) {
    		   numCambios = 0;
    		  } 
    		aceleracionNave = Math.round((mX - x) /25);
    		giroNave = (int) (y-mY)/3;
    		mX=x; mY=y;
    		numCambios += 1;
    	}
    }
    
    protected void setPadre(Activity padre) {
    	this.padre = padre;
    };
    
            
    public void ActivarMisil()  {
    	//Activa sonido de disparo desde MediaPlayer
    	//mpDisparo.start();
    	//Activa sonido de disparo desde SoundPool
    	soundPool.play(idDisparo,1,1,0,2,0.5f);
    	misil.setPosX(nave.getPosX() + nave.getAncho()/2 - misil.getAncho()/2);
    	misil.setPosY(nave.getPosY() + nave.getAlto()/2 - misil.getAlto()/2);
    	misil.setAngulo(nave.getAngulo());
    	misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo()))*PASO_VELOCIDAD_MISIL);
    	misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo()))*PASO_VELOCIDAD_MISIL);
    	tiempoMisil = (int) Math.min(this.getWidth()/ Math.abs(misil.getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2;
    	misilActivo = true;
    }
    
    private void destruyeAsteriode(int i) {
    	Asteroides.remove(i);
    	puntuacion += 1000;
    	misilActivo = false;
    	if (Asteroides.isEmpty()) {
    		salir();
    	}
    }
    
    protected void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        // No hagas nada si el período de proceso no se ha cumplido.
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
              return;
        }
        // Para una ejecución en tiempo real calculamos retardo           
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
        // Actualizamos velocidad y dirección de la nave a partir de 
        // giroNave y aceleracionNave (según la entrada del jugador)
        nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));
        double nIncX = nave.getIncX() + aceleracionNave *
                             Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
        double nIncY = nave.getIncY() + aceleracionNave * 
                            Math.sin(Math.toRadians(nave.getAngulo())) * retardo;
        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX,nIncY) <= MAX_VELOCIDAD_NAVE){
              nave.setIncX(nIncX);
              nave.setIncY(nIncY);
        }
        // Actualizamos posiciones X e Y
        nave.incrementaPos(retardo);
        for (Grafico asteroide : Asteroides) {
              asteroide.incrementaPos(retardo);
        }
        
        //Actualizamos posición de misil
        if (misilActivo) {
        	misil.incrementaPos(retardo);
        	tiempoMisil -= retardo;
        	if (tiempoMisil<0) {
        		misilActivo = false;
        	} 
        	else  {
        		     for (int i = 0; i < Asteroides.size(); i++)  
        		    	 if (misil.verificaColision(Asteroides.elementAt(i))) {
        		    		 destruyeAsteriode(i);
        		    		 break;
        		    	 }        		    		 
        	}        
        }
        
        for (Grafico asteroide : Asteroides) {
              if (asteroide.verificaColision(nave)) {
              	salir();
              }
        }
        
    }
    
    
    private void salir() {
    	Bundle bundle = new Bundle();
    	bundle.putInt("puntuacion", puntuacion);
    	Intent intent = new Intent();
    	intent.putExtras(bundle);
    	padre.setResult(Activity.RESULT_OK, intent);
    	padre.finish();
    }
    
    
    class ThreadJuego extends Thread {
    	   private boolean pausa,corriendo;
    	   
    	   public synchronized void pausar() {
    	          pausa = true;
    	   }
    	 
    	   public synchronized void reanudar() {
    	          pausa = false;
    	          notify();
    	   }
    	 
    	   public void detener() {
    	          corriendo = false;
    	          if (pausa) reanudar();
    	   }
    	  
    	   @Override public void run() {
    	          corriendo = true;
    	          while (corriendo) {
    	             actualizaFisica();
    	             synchronized (this) {
    	                while (pausa)
    	                   try {
    	                      wait();
    	                   } catch (Exception e) {
    	                   }
    	                }
    	             }
   	          }  
    }  
   

	public ThreadJuego getThread() {
		return thread;
	}
    
    

}