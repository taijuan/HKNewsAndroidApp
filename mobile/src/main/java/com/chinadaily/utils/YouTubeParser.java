package com.chinadaily.utils;

import com.chinadaily.base.BaseApp;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YouTubeParser {

    private static final int BUFFER_SIZE = 8192;
    private static final String GET_VIDEO_INFO_URL_1ST_HALF = "http://www.youtube.com/get_video_info?video_id=";

    private static String downloadBinary(InputStream is) throws IOException {
        String result;
        byte[] buffer = new byte[BUFFER_SIZE];
        StringBuilder sb = new StringBuilder();
        int size;
        do {
            size = is.read(buffer);
            if (size != -1)
                sb.append(new String(buffer, 0, size));
        } while (size != -1);
        result = sb.toString();
        return result;
    }

    private static String getVideoInfoURL(String youTubeID) {
        String result;
        result = GET_VIDEO_INFO_URL_1ST_HALF + youTubeID;
        return result;
    }

    /**
     * @param queryString string
     * @return is a dictionary of arrays
     */
    private static Map<String, ArrayList<String>> dictionaryFromQueryStringComponents(String queryString) {
        Map<String, ArrayList<String>> result;
        result = new HashMap<>();
        String[] ampersandSeparated = queryString.split("&");
        for (String oneString : ampersandSeparated) {
            String[] keyValuePair = oneString.split("=");
            if (keyValuePair.length < 2) {
                continue;
            }
            try {
                String key = URLDecoder.decode(keyValuePair[0], "UTF-8");
                String value = URLDecoder.decode(keyValuePair[1], "UTF-8");

                ArrayList<String> values = result.get(key);
                if (values == null) {
                    values = new ArrayList<>();
                    result.put(key, values);
                }
                values.add(value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String youtubeIDFromYoutubeURL(URL youTubeURL) {
        String result = null;
        Map<String, ArrayList<String>> temp;
        temp = dictionaryFromQueryStringComponents(youTubeURL.getQuery());
        ArrayList<String> temp2;
        temp2 = temp.get("v");
        if (temp2 != null) {
            result = temp2.get(0);
        }
        return result;
    }

    public static Map<String, String> h264videosWithYoutubeURL(URL youTubeURL) {
        Map<String, String> result = new HashMap<>();
        String youTubeID = null;
        if (youTubeURL != null && youTubeURL.toString().startsWith("https")) {
            if (youTubeURL.getQuery() == null) {
                String urlFile = youTubeURL.getFile();
                String youtubeUrl = "https://www.youtube.com/watch?v=" + urlFile.substring(1);
                URL url;
                try {
                    url = new URL(youtubeUrl);
                    youTubeID = youtubeIDFromYoutubeURL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                youTubeID = youtubeIDFromYoutubeURL(youTubeURL);
            }
        } else {
            youTubeID = youTubeURL.getFile().substring(1);
        }
        String videoInfoURLString;
        URL videoInfoURL;
        videoInfoURLString = getVideoInfoURL(youTubeID);
        String rawResponseString = "";
        try {
            videoInfoURL = new URL(videoInfoURLString);
            HttpURLConnection http;
            http = (HttpURLConnection) videoInfoURL.openConnection();
            String userAgent = BaseApp.getUserAgent();
            http.setRequestProperty("User-Agent", userAgent);
            //java.net.SocketTimeoutException: failed to connect to www.youtube.com/69.63.176.12 (port 80) after 90000ms
            //切换地址时，设置超时时间进行优化，
            http.setConnectTimeout(4000);
            http.setReadTimeout(4000);
            InputStream is = http.getInputStream();
            int count = 0;
            String value;
            do {
                value = http.getHeaderField(count);
                count++;
            } while (value != null);
            rawResponseString = downloadBinary(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (rawResponseString.length() > 0) {
            Map<String, ArrayList<String>> parts;
            parts = dictionaryFromQueryStringComponents(rawResponseString);
            ArrayList<String> fmtStreamMapStringArray;
            fmtStreamMapStringArray = parts.get("url_encoded_fmt_stream_map");
            if (fmtStreamMapStringArray == null) {
                return null;
            }
            String fmtStreamMapString;
            fmtStreamMapString = fmtStreamMapStringArray.get(0);
            if (fmtStreamMapString == null) {
                return null;
            }
            String[] fmtStreamMapArray;
            fmtStreamMapArray = fmtStreamMapString.split(",");
            for (String oneString : fmtStreamMapArray) {
                Map<String, ArrayList<String>> videoComponents;
                videoComponents = dictionaryFromQueryStringComponents(oneString);
                if (videoComponents.get("type").get(0).contains("mp4")) {
                    String url;
                    url = videoComponents.get("url").get(0);
                    String quality;
                    quality = videoComponents.get("quality").get(0);
                    if (result.get(quality) == null) {
                        result.put(quality, url);
                    }
                }
            }
        }
        return result;
    }
}
