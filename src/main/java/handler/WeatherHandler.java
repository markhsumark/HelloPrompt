package handler;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import java.util.Scanner;
import org.json.JSONException;
import java.io.IOException;
import java.net.UnknownHostException;


public class WeatherHandler extends Handler{
    private static final String WEATHER_CENTER_FILE_PATH = "/weather_center_data.csv";
    private static final String LOCATION_NAME_URL_PATH = "/location_name_url.csv";
    private static String predictUrl;
    private static String currentUrl;
    private static String key;
    private ArrayList<Map<String, ArrayList<String>>> predict_weather;
    private Map<String, ArrayList<String>> current_weather;
    private final String location;

    public WeatherHandler(){
        ifOutput = true;
        this.location = "基隆市";   //預設
    }
    public WeatherHandler(String location){
        ifOutput = true;
        this.location = location;   
    }
    public ArrayList<Map<String, ArrayList<String>>> getPredictWeather()throws NullPointerException{
        if(predict_weather==null){
            throw new NullPointerException("predict_weather hasn't been initalize");
        }
        return predict_weather;
    }
    public Map<String, ArrayList<String>> getCurrentWeather()throws NullPointerException{
        if(current_weather==null){
            throw new NullPointerException("current_weather hasn't been initalize");
        }
        return current_weather;
    }
    public String getHttp(String url)throws Exception{
        String allData =new String();
        try{
            Scanner scanner = new Scanner(new URL(url).openStream(), StandardCharsets.UTF_8.toString());
            scanner.useDelimiter("\\A");
            allData =scanner.next();
            return allData;
        }
        catch(UnknownHostException e){
            throw new UnknownHostException("wrong url resource(check csv file)");
        }
    }
// TEMP 溫度 攝氏單位
// HUMD 空氣濕度 相對濕度(0< HUMD<1)
// WDIR 風向，單位 度，一般風向 0 表示無風
// WDSD 風速，單位 公尺/秒
// D_TX 本日最高溫，單位 攝氏
// D_TXT 本日最高溫發生時間，hhmm (小時分鐘)
// D_TN 本日最低溫，單位 攝氏
// D_TNT 本日最低溫發生時間，hhmm (小時分鐘)
    public void produceCurrentWeather(JSONObject json)throws Exception{
        List<String> elementsName_EN = Arrays.asList("TEMP", "HUMD","WDIR","WDSD","D_TX","D_TXT","D_TN","D_TNT");
        List<String> elementsName_CH = Arrays.asList("現在溫度","濕度","風向","風速","本日最高溫","本日最高溫發生時間","本日最低溫","本日最低溫發生時間");
        Map<String, ArrayList<String>> weather= new HashMap<String, ArrayList<String>>();
        String temp= new String();
        String humd= new String(); //相對濕度
        try{
            JSONArray allLocation = json.getJSONObject("records").getJSONArray("location");
            for(int i=0; i<allLocation.length(); i++){
                JSONObject J= allLocation.getJSONObject(i);
                String city = J.getJSONArray("parameter").getJSONObject(0).get("parameterValue").toString();
                if(city.equals(location)){
                    JSONArray weatherElement= J.getJSONArray("weatherElement");
                    for(int en=0; en<weatherElement.length(); en++){
                        ArrayList<String> elementList = new ArrayList<String>();
                        String elementN= weatherElement.getJSONObject(en).get("elementName").toString();
                        String elementV= weatherElement.getJSONObject(en).get("elementValue").toString();
                        elementList.add(elementV);
                        for(int k=0; k<elementsName_EN.size(); k++){
                            if(elementN.equals(elementsName_EN.get(k))){
                                weather.put(elementsName_CH.get(k),elementList);
                            }
                        }
                        
                    }
                }
            }
        }catch(Exception e){
            throw new JSONException("\nThe JSON file from current weather's url has error");
        }
        current_weather=  weather;
    }

    // 指定地區的預測天氣
    public void producePredictWeather(JSONObject json)throws Exception{
        List<String> conditons= Arrays.asList("天氣現象", "降雨機率", "最低溫度", "舒適度" ,"最高溫度");
        ArrayList<Map<String, ArrayList<String>>> weatherList= new ArrayList<Map<String, ArrayList<String>>>();
        try{
            JSONArray allLocation = json.getJSONObject("records").getJSONArray("location");
            // JSONObject jsonWeather= allLocation.getJSONObject(0);
            JSONArray elements = allLocation.getJSONObject(0).getJSONArray("weatherElement");
            JSONArray timeArray = elements.getJSONObject(0).getJSONArray("time");
            for(int k=0; k<timeArray.length(); k++){
                Map<String, ArrayList<String>> map= new HashMap<String, ArrayList<String>>();
                JSONObject sameTimes = timeArray.getJSONObject(k);
                ArrayList<String> sttlist = new ArrayList<String>();
                ArrayList<String> endtlist = new ArrayList<String>();
                sttlist.add((String)sameTimes.get("startTime"));
                endtlist.add((String)sameTimes.get("endTime"));
                map.put("startTime",sttlist);
                map.put("endTime",endtlist);

                for(int i=0; i<elements.length();i++){
                    JSONObject times =elements.getJSONObject(i).getJSONArray("time").getJSONObject(k);
                    JSONObject parameter = times.getJSONObject("parameter");
                    ArrayList<String> plist = new ArrayList<String>();
                    plist.add((String)parameter.get("parameterName"));
                    switch(i){
                        case 0:
                        plist.add((String)parameter.get("parameterValue"));
                        break;
                        case 1:
                        plist.add((String)parameter.get("parameterUnit"));
                        break;
                        case 2:
                        plist.add((String)parameter.get("parameterUnit"));
                        break;
                        case 4:
                        plist.add((String)parameter.get("parameterUnit"));
                        break;
                        default:
                        break;
                    }
                    map.put(conditons.get(i), plist);
                }
                weatherList.add(map);
            }
        }catch(Exception e){
            System.out.println(e);
            throw new JSONException("\nThe JSON file from predict weather's url has error");
        }
        predict_weather = weatherList;
    }

    // 建立資料
    public void weatherInit()throws Exception{
            WeatherAPI weatherAPI =new WeatherAPI(location);
            this.predictUrl= weatherAPI.getPredictDataUrl();
            this.currentUrl= weatherAPI.getCurrentDataUrl();

            String datafromHttp = getHttp(this.predictUrl);
            JSONObject predict_json = new JSONObject(datafromHttp);
            producePredictWeather(predict_json);

            String dataCurrentHttp = getHttp(this.currentUrl);
            JSONObject Jsonfile_C = new JSONObject(dataCurrentHttp);
            produceCurrentWeather(Jsonfile_C);
    }
    @Override
	protected void readConfig(String fileName){
	}
    @Override
    public String toString(){
        try{
            weatherInit();
            ArrayList<Map<String, ArrayList<String>>> list= new ArrayList<Map<String, ArrayList<String>>>(predict_weather);
            list.add(0, current_weather);
            return list.toString();
        }catch(Exception e){
            return "weather handler fail:\n"+e;
        }
    }

}