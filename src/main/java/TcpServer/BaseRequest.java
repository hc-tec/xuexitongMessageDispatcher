package TcpServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class BaseRequest {

    public HttpExchange exchange;
    public HashMap<String, String> headers;
    public HashMap<String, String> cookies;
    public HashMap<String, String> queryParams;
    public HashMap<String, String> data;

    public BaseRequest(HttpExchange exchange) {
        this.exchange = exchange;
        this.parse();
    }

    /**
     * 对 HttpExchange 中未解析的数据进行解析
     */
    public void parse() {
        Headers rawHeaders = this.exchange.getRequestHeaders();
        this.queryParams = this.parseQueryParams();
        this.data = this.parseData();
        this.headers = this.parseHeaders(rawHeaders);
        this.cookies = this.parseCookies(this.headers.get("cookie"));
    }

    /**
     * 解析请求头
     * @return 解析之后的结果
     */
    private HashMap<String, String> parseHeaders(Headers rawHeaders) {
        HashMap<String, String> headers = new HashMap<String, String>();
        for (String key : rawHeaders.keySet()) {
            headers.put(key.toLowerCase(), rawHeaders.getFirst(key));
        }
        return headers;
    }

    /**
     * get 等请求时的参数解析
     * @return 解析之后的数据
     */
    private HashMap<String, String> parseQueryParams() {
        String rawQuery = this.exchange.getRequestURI().getQuery();
        return this._parseQuery(rawQuery, "&");
    }

    /**
     * post 等请求时的 form-data 及 x-www-form-urlencoded 参数解析
     * rawQuery 值为一串特殊形式的文字，e.g:
     * ----------------------------974425475039832803051050
     * Content-Disposition: form-data; name="user_name"titto
     * ----------------------------974425475039832803051050
     * Content-Disposition: form-data; name="password"123456
     * ----------------------------974425475039832803051050
     * @return 解析之后的数据
     */
    private HashMap<String, String> parseData() {
        try {
            InputStream body = this.exchange.getRequestBody();
            InputStreamReader isr = new InputStreamReader(body, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder rawQuery = new StringBuilder();
            String tmp = br.readLine();
            while(tmp != null) {
                rawQuery.append(tmp);
                tmp = br.readLine();
            }
            if(rawQuery.length() == 0) return null;
            if(this.isFormData(rawQuery.toString())) {
                return this._parseFormData(rawQuery.toString());
            } else {
                return this._parseXWwwFormUrlencoded(rawQuery.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在 post 等请求中，使用 form-data 传参时数据以特殊形式呈现，需要
     * 单独的一个函数对其解析
     * @param rawQuery 原生数据
     * @return 解析之后的数据
     */
    private String getBoundary(String rawQuery) {
        int boundary_end = rawQuery.indexOf('C');
        String boundary = rawQuery.substring(0, boundary_end);
        return boundary;
    }

    /**
     * 参数是否由 form-data 携带
     * @param rawQuery 原生数据
     * @return 返回上述目标真假值
     */
    private Boolean isFormData(String rawQuery) {
        return rawQuery.contains("form-data");
    }

    /**
     * 对 "user_name=titto&password=123456" 格式数据进行解析
     * @param rawQuery 原生数据
     * @return 解析之后的数据
     */
    private HashMap<String, String> _parseQuery(String rawQuery, String splitChar) {
        splitChar = splitChar != null ? splitChar : "&";
        HashMap<String, String> queryParams = new HashMap<String, String>();
        if (rawQuery == null) return null;
        String[] querys = rawQuery.split(splitChar);
        for (int i = 0; i < querys.length; i++) {
            String[] tmp = querys[i].split("=");
            try {
                queryParams.put(tmp[0], tmp[1]);
            } catch (IndexOutOfBoundsException e) {
                queryParams.put(tmp[0], "");
            }
        }
        return queryParams;
    }

    /**
     * 对 form-data 格式参数解析
     * @param rawQuery 原生数据
     * @return 解析之后的数据
     */
    private HashMap<String, String> _parseFormData(String rawQuery) {
        HashMap<String, String> queryParams = new HashMap<String, String>();
        // 获取边界，在之后的解析中用于分割数据
        int rawQueryLen = rawQuery.length();
        rawQuery = rawQuery.substring(0, rawQueryLen - 2);
        String boundary = this.getBoundary(rawQuery);
        String[] matches = Pattern.compile(boundary).split(rawQuery);
        for (String match : matches) {
            String key, val;
            int len = match.length();
            if(len == 0) continue;
            int key_start = match.indexOf("\"");
            String cur_match = match.substring(key_start+1);
            int key_end = cur_match.indexOf("\"")+key_start;
            key = match.substring(key_start, key_end+1).replace("\"", "");
            val = match.substring(key_end+1, len).replace("\"", "");
            queryParams.put(key, val);
        }
        return queryParams;
    }

    /**
     * 对 x-www-form-urlencoded 参数解析
     * @param rawQuery 原生数据
     * @return 解析之后的结果
     */
    private HashMap<String, String> _parseXWwwFormUrlencoded(String rawQuery) {
        return this._parseQuery(rawQuery, "&");
    }

    /**
     * 对 cookie 进行解析
     * @param rawCookies 原生 cookie 数据
     * @return 解析之后的结果
     */
    private HashMap<String, String> parseCookies(String rawCookies) {
        return this._parseQuery(rawCookies, ";");
    }


}
