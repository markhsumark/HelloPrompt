
package handler;

import java.util.Date;
import java.util.Formatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.*;
import java.util.List;
import java.util.Arrays;
import java.lang.StringBuilder;


import java.util.Scanner;
import org.json.JSONException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class WeatherAPI {
    private static final String AUTHORITY_FILE_PATH = "/authority_key.csv";

    // url : apiUrl+ 授權碼+ 想搜尋的類別+ 時間+ 地點
    private final String apiUrl = "https://opendata.cwb.gov.tw/api/v1/rest/datastore";
    private final String current = "/O-A0001-001"; // 最近一次觀測資料
    private final String predict = "/F-C0032-001"; // 後36hrs預報

    private static String key;
    private static List<String> weatherElements= Arrays.asList("WDIR","WDSD", "D_TX", "D_TXT", "D_TN", "D_TNT", "TEMP", "HUMD");                 
    private final String now;
    private final String location;
    private final String locationUrl;

    public WeatherAPI(String location) throws Exception {
        this.location = location;
        now = getTime();
        locationUrl = "&locationName=" + location;
        if (locationUrl == null) {
            throw new IllegalArgumentException("Unknown location");
        }
        produceDateFromFile(AUTHORITY_FILE_PATH);
    }

    public WeatherAPI() throws Exception {
        this("基隆市"); // Default location
    }

    public String getCurrentDataUrl() {
        StringBuilder elements= new StringBuilder();
        for(int i=0; i<weatherElements.size(); i++) {
            if(i==0){
                elements.append(weatherElements.get(i));
            }
            elements.append(","+ weatherElements.get(i));
        }
        return apiUrl + current + "?Authorization=" + key + "&elementName=" + elements + "&parameterName%EF%BC%8C=CITY";
    }

    public String getPredictDataUrl() {
        return apiUrl + predict + "?Authorization=" + key + "&timeFrom=" + now + locationUrl;
    }

    public String[] openCsvFile(String path) throws IOException {
        String data = new String();
        try {
            InputStream is = WeatherHandler.class.getResourceAsStream(path);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append(",");
                line = buf.readLine();
            }
            buf.close();
            data = sb.toString();
            return data.split(",");
        } catch (Exception e) {
            throw new IOException("files in " + path + " not exist");
        }
    }

    public void produceDateFromFile(String fileName) throws Exception {

        String[] str = openCsvFile(fileName);
        for (int i = 0; i < str.length; i++) {

            if (str[i].contains("授權碼")) {
                this.key = str[i + 1];
            }
        }
    }

    public String getTime() {
        Date current = new Date();// 取時間
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh'%3A'mm'%3A'ss");
        return formatter.format(current);
    }

    // 回傳後36hrs的url (String)
    public String toString() {
        return apiUrl + predict + "?Authorization=" + key + "&timeFrom=" + now + locationUrl;

    }

}
