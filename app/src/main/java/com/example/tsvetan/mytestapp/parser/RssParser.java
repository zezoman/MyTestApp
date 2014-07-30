package com.example.tsvetan.mytestapp.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tsvetan on 25.7.2014 г..
 */
public class RssParser {
    private static final String dateFormat = "EEE, d MMM yyyy HH:mm:ss Z";

    public List<Item> parse(String urlString){
        ArrayList<Item> itemsArr = new ArrayList<Item>();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            URL url = new URL(urlString);
            HttpURLConnection httpUrlCon = (HttpURLConnection)url.openConnection();
            httpUrlCon.setReadTimeout(10000);
            httpUrlCon.setConnectTimeout(15000);
            httpUrlCon.setRequestMethod("GET");
//            httpUrlCon.setUseCaches(false);
//            httpUrlCon.setInstanceFollowRedirects(true);
            httpUrlCon.connect();
            // null causes the encoding declared in the XML to be used
            parser.setInput(httpUrlCon.getInputStream(), null);

            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG && parser.getName().equalsIgnoreCase("item")){
                    itemsArr.add(readItem(parser));
                }
                eventType = parser.next();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return itemsArr;
    }

    private Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        Item item = new Item();
        int eventType = parser.getEventType();
        while (true) {
            if (eventType == XmlPullParser.END_TAG && parser.getName().equals("item")) {
                return item;
            } else if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("title")) {
                    eventType = parser.next();
                    item.setTitle(parser.getText());
                } else if (parser.getName().equals("link")) {
                    eventType = parser.next();
                    item.setLink(parser.getText());
                } else if (parser.getName().equals("description")) {
                    eventType = parser.next();
                    item.setDescription(parser.getText());
                } else if (parser.getName().equals("guid")) {
                    eventType = parser.next();
                    item.setGuid(parser.getText());
                } else if (parser.getName().equals("pubDate")) {
                    eventType = parser.next();
                    SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                    Date date = format.parse(parser.getText());
                    item.setDate(date.getTime() / 1000L);
                }
            }
            eventType = parser.next();
        }
    }

}
