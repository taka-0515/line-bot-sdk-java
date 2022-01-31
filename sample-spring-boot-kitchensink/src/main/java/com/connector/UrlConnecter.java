package com.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UrlConnecter {
	    private Logger log = LoggerFactory.getLogger(UrlConnecter.class);
	public String getResponseStr(String urlString) {
	   	String results = "";
    	//try-catchで囲む    	
		try {
			InputStream stream;
			stream = new URL(urlString).openStream();

    		//文字列のバッファを構築
    		StringBuffer sb = new StringBuffer();
    		String line = "";
    		//文字型入力ストリームを作成
    		BufferedReader br = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
    		//読めなくなるまでwhile文で回す
    		while((line = br.readLine()) != null) {
    			sb.append(line);
    		}
    		stream.close();
    		results = sb.toString();
		} catch (MalformedURLException e) {
			log.error("URL error",e);
		} catch (IOException e) {
			log.error("IO error",e);
		}
		return results;
	}
}
