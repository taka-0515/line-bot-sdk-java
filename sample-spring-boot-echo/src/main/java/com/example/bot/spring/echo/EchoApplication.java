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
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.connector.UrlConnecter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
        	log.info(originalMessageText);
        String subText = shopAndKuchikomiSearch(originalMessageText);
        
        	
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
    	String area = "&large_area=Z063";
    	String keyword = "&keyword="+str;
    	String urlString = url+key
    	+ area
    	+ keyword
    	+ "&format=json";

    	String results = "";
    	UrlConnecter urlCon = new UrlConnecter();
    	String script = urlCon.getResponseStr(urlString);
    	//ObjectMapperオブジェクトの宣言
    	ObjectMapper mapper = new ObjectMapper();

    	//JSON形式をクラスオブジェクトに変換
    	JsonNode node;
		try {
			node = mapper.readTree(script).get("results").get("shop");
			Random random = new Random();
			int randomValue = random.nextInt(node.size() - 1);
			//クラスオブジェクトの中から必要なものだけを取りだす
    		String name = node.get(randomValue).get("name").asText();
    		if (name != null || name != "") {
    			results = results + name + "とかどうけ？\n";
    			String genre = node.get(randomValue).get("genre").get("catch").asText();
    			results = results + genre + "なんやけど";
    		}
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if (results == "") {
        	results = "探したけどないわ〜";
        } 
    	return results;
    }
}
