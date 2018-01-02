package com.example.administrator.readxml;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    HttpURLConnection httpConn=null;
    Button btn_find=null;
    EditText et_cityname=null;
    InputStream din=null;
    ArrayList<WeatherInfo> weatherInfos=new ArrayList<>();
    String cityname="广州";
    LinearLayout tv_show=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("天气查询xml");
        btn_find=(Button) findViewById(R.id.btn_find);
        et_cityname=(EditText)findViewById(R.id.et_cityname);
        tv_show=(LinearLayout) findViewById(R.id.tv_show);

        btn_find.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tv_show.removeAllViews();
                Toast.makeText(MainActivity.this,"正在查询天气信息...",Toast.LENGTH_SHORT).show();
                GetXml gx=new GetXml(et_cityname.getText().toString());
                gx.start();
            }
        });
    }
    /*private void Xmlparse(InputStream data){
        XmlPullParser xp= Xml.newPullParser();
        StringBuffer buffer=new StringBuffer();
        try{
            xp.setInput(data,"utf_8");
            int type=xp.getEventType();
            while (type!=XmlPullParser.END_DOCUMENT){
                switch (type){
                    case XmlPullParser.START_TAG:
                        if ("day".equals(xp.getName())){
                            weatherInfos=new ArrayList<>();
                        }else if ("night".equals(xp.getName())){
                            weatherInfos=new ArrayList<>();
                        }else if ("date".equals(xp.getName())){
                            String date=xp.nextText();
                        }else if ("high".equals(xp.getName())){
                            String high=xp.nextText();
                        }else if ("low".equals(xp.getName())){
                            String low=xp.nextText();

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                type=xp.next();
            }

        }catch (Exception ex){
            ex.printStackTrace();

        }

    }*/

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    showdata();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    class GetXml extends Thread{

        private String urlstr =  "http://wthrcdn.etouch.cn/WeatherApi?city=";
        public GetXml(String cityname){
            try{
                urlstr = urlstr+ URLEncoder.encode(cityname,"UTF-8");

            }catch (Exception ee){

            }
        }
        @Override
        public void run() {
            for (int i=0;i<weatherInfos.size();i++){
                weatherInfos.clear();
            }
            try {
                URL url = new URL(urlstr);
                httpConn = (HttpURLConnection)url.openConnection();
                httpConn.setRequestMethod("GET");
                //httpConn.setConnectTimeout(5000);
                din = httpConn.getInputStream();
                XmlPullParser xp=Xml.newPullParser();
                xp.setInput(din,"utf-8");
                WeatherInfo line = null;
                W w=null;
                int type=xp.getEventType();
                while(type!=XmlPullParser.END_DOCUMENT){
                    if (type==XmlPullParser.START_TAG) {
                        String tag = xp.getName();
                        if (tag.equalsIgnoreCase("weather")) {
                            line = new WeatherInfo();
                        }
                        if (tag.equalsIgnoreCase("date")) {
                            if (line != null) {
                                line.date = xp.nextText();
                            }
                        }
                        if (tag.equalsIgnoreCase("high")){
                            if (line!=null){
                                line.high=xp.nextText();
                            }
                        }
                        if (tag.equalsIgnoreCase("low")){
                            if (line!=null){
                                line.low=xp.nextText();
                            }
                        }
                        if (tag.equalsIgnoreCase("day")){
                            w=new W();
                        }
                        if (tag.equalsIgnoreCase("night")){
                            w=new W();
                        }
                        if (tag.equalsIgnoreCase("type")){
                            if (w!=null){
                                w.type=xp.nextText();
                            }
                        }
                        if (tag.equalsIgnoreCase("fengxiang")){
                            if (w!=null){
                                w.fengxiang=xp.nextText();
                            }
                        }
                        if (tag.equalsIgnoreCase("fengli")){
                            if (w!=null){
                                w.fengli=xp.nextText();
                            }
                        }
                    }
                    else if (type==XmlPullParser.END_TAG){
                        String tag=xp.getName();
                        if (tag.equalsIgnoreCase("weather")){
                            weatherInfos.add(line);
                            line=null;
                        }
                        if (tag.equalsIgnoreCase("day")){
                            line.day=w;
                            w=null;
                        }
                        if (tag.equalsIgnoreCase("night")){
                            line.night=w;
                            w=null;
                        }
                    }
                    type=xp.next();
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
                Looper.prepare(); //在线程中调用Toast，要使用此方法，这里纯粹演示用:)
                Toast.makeText(MainActivity.this,"获取数据成功",Toast.LENGTH_LONG).show();
                Looper.loop(); //在线程中调用Toast，要使用此方法
            }catch (Exception ee){
                Looper.prepare(); //在线程中调用Toast，要使用此方法
                Toast.makeText(MainActivity.this,"获取数据失败，网络连接失败或输入有误",Toast.LENGTH_LONG).show();
                Looper.loop(); //在线程中调用Toast，要使用此方法
                ee.printStackTrace();
            }finally {
                try{
                    httpConn.disconnect();
                    din.close();

                }catch (Exception ee){
                    ee.printStackTrace();
                }
            }

        }
    }
    public void showdata(){
        tv_show.removeAllViews();
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i=0;i<weatherInfos.size();i++){
            TextView dateView=new TextView(this);
            dateView.setGravity(Gravity.CENTER_HORIZONTAL);
            dateView.setLayoutParams(params);
            dateView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            dateView.setText("日期:"+weatherInfos.get(i).date);
            tv_show.addView(dateView);

            TextView view=new TextView(this);
            view.setLayoutParams(params);
            String str = "高温：" + weatherInfos.get(i).high+",低温：" + weatherInfos.get(i).low + "\n";
            str = str + "白天：" + weatherInfos.get(i).day.info() + "\n";
            str = str + "夜间：" +weatherInfos.get(i).night.info();
            view.setText(str);
            tv_show.addView(view);
        }

    }


}