/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@SpringBootApplication
@LineMessageHandler
public class EchoApplication {
    private final Logger log = LoggerFactory.getLogger(EchoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        log.info("event: " + event);
        final String originalMessageText = event.getMessage().getText();
        

        	String area = "&large_area=Z063";
        	String keyword = "&keyword="+originalMessageText;
        
        
        String subText = shopAndKuchikomiSearch(area+keyword);
        if (subText == "") {
        	subText = "not";
        }
        	
        return new TextMessage(subText);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
    
    // 試しにリクエスト用のメソッド
    public String shopAndKuchikomiSearch(String str) {

    	// クライアントから受け取ったパラメータをキーに、Hotpepper APIから店舗情報を取得する。(変数とかは自分で設定）
    	String url = "http://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=";
    	String key = "361d7b4b2da2a9f5";
    	String urlString = url+key
    	+ str
    	+ "&format=json";

    	String results = "";

    	//try-catchで囲む
    	InputStream stream;
		try {
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
    		String script = sb.toString();

    		//ObjectMapperオブジェクトの宣言
    		ObjectMapper mapper = new ObjectMapper();

    		//JSON形式をクラスオブジェクトに変換
    		JsonNode node = mapper.readTree(script).get("results").get("shop");

    		//クラスオブジェクトの中から必要なものだけを取りだす
    		for (int i = 0; i < node.size(); i++) {
    			String name = node.get(i).get("name").asText();
    			results = results + name + "\n";
    		}
    		return results;
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return results;
    }
}
