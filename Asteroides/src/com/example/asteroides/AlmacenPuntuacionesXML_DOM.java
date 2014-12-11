package com.example.asteroides;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import android.content.Context;
import android.util.Log;

public class AlmacenPuntuacionesXML_DOM implements AlmacenPuntuaciones {

	private static String FICHERO = "puntuaciones.xml";
	private Context context;
	private Document documento;
	private boolean cargadoDocumento;
	
	public AlmacenPuntuacionesXML_DOM(Context context) {
		this.context = context;
		cargadoDocumento = false;
	}
	
	@Override
	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		try {	if (!cargadoDocumento) {
			        //La siguiente linea generaba la excepción SAXParserException "Unexpected end of document"
			        //Esto debía ser porque el fichero estaba vacío
			        //leerXML(context.openFileInput(FICHERO));
			        InputStream is = context.openFileInput(FICHERO);
			        if (is.read()==-1) 
			        	crearXML();
			        else 
			        	leerXML(is);
				}
		} catch (FileNotFoundException e) {			
			crearXML();
		} catch (Exception e) {
			Log.e("Asteroides", e.getMessage(), e);
		}
		nuevo(puntos, nombre, fecha);
		try {
			escribirXML(context.openFileOutput(FICHERO, Context.MODE_PRIVATE));
		} catch (Exception e) {
			Log.e("Asteroides", e.getMessage(), e);
		}	
	}

	@Override
	public Vector<String> listaPuntuaciones(int cantidad) {
		try { if (!cargadoDocumento) {
				leerXML(context.openFileInput(FICHERO));		
			  }
		} catch (FileNotFoundException e) {
			crearXML();
		} catch (Exception e) {Log.e("Asteroides", e.getMessage(), e);}
		return aVectorString(); 
	}

	public void crearXML() {
		try { 
				DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
				DocumentBuilder constructor = fabrica.newDocumentBuilder();
				documento = constructor.newDocument();
				Element raiz = documento.createElement("lista_puntuaciones");
				documento.appendChild(raiz);
				cargadoDocumento = true;
		} catch (Exception e) {Log.e("Asteroides", e.getMessage(), e);}	
	}
	
	public void leerXML(InputStream entrada) throws Exception {
		DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructor = fabrica.newDocumentBuilder();
		documento = constructor.parse(entrada);
		cargadoDocumento = true;
	}
	
	public void nuevo(int puntos, String nombre, long fecha) {
		Element puntuacion = documento.createElement("puntuacion");
		puntuacion.setAttribute("fecha", String.valueOf(fecha));
		Element e_nombre = documento.createElement("nombre");
		Text texto = documento.createTextNode(nombre);
		e_nombre.appendChild(texto);
		puntuacion.appendChild(e_nombre);
		Element e_puntos = documento.createElement("puntos");
		texto = documento.createTextNode(String.valueOf(puntos));
		e_puntos.appendChild(texto);
		puntuacion.appendChild(e_puntos);
		Element raiz = documento.getDocumentElement();
		raiz.appendChild(puntuacion);
	}
	
	public Vector<String> aVectorString() {
		Vector<String> result = new Vector<String>();
		String nombre = "", puntos = "";
		Element raiz = documento.getDocumentElement();
		NodeList puntuaciones = raiz.getElementsByTagName("puntuacion");
		for (int i = 0; i<puntuaciones.getLength(); i++) {
			Node puntuacion = puntuaciones.item(i);
			NodeList propiedades = puntuacion.getChildNodes();
			for (int j = 0; j<propiedades.getLength(); j++) {
				Node propiedad = propiedades.item(j);
				String etiqueta = propiedad.getNodeName();
				if (etiqueta.equals("nombre")) {
					nombre = propiedad.getFirstChild().getNodeValue();
				} else if (etiqueta.equals("puntos")) {
					puntos = propiedad.getFirstChild().getNodeValue();
				} 
			}
			result.add(nombre + " " + puntos);
		}
		return result;
	}
	
	public void escribirXML(OutputStream salida) throws Exception {
		String s = serializa(documento.getDocumentElement());
		salida.write(s.getBytes("UTF-8"));
	}
	
	public String serializa(Node raiz) throws IOException {	
		
		StringBuilder resultado = new StringBuilder();
		if (raiz.getNodeType() == Node.TEXT_NODE) 
			resultado.append(raiz.getNodeValue());
		else {
			if (raiz.getNodeType() != Node.DOCUMENT_NODE) {
				StringBuffer atributos = new StringBuffer();
				for (int i = 0; i<raiz.getAttributes().getLength(); i++) {
					atributos.append(" ")
					.append(raiz.getAttributes().item(i).getNodeName())
					.append("=\"")
					.append(raiz.getAttributes().item(i).getNodeValue())
					.append("\" ");
				}
				resultado.append("<").append(raiz.getNodeName()).append(" ").append(atributos).append(">");
			} else {
				resultado.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			}
			NodeList listaNodos = raiz.getChildNodes();
			for (int i = 0; i<listaNodos.getLength(); i++) {
				Node nodo = listaNodos.item(i);
				resultado.append(serializa(nodo));
			}
			if (raiz.getNodeType() != Node.DOCUMENT_NODE) {
				resultado.append("</").append(raiz.getNodeName()).append(">");
			}
		}		
		return resultado.toString();		
	}
}