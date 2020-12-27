package utils.Request;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Request {
    /**
     * 建立与目标网址的连接
     * @param target 目标网址: str
     * @param method 请求方法: str
     * @return
     */
    private static HttpURLConnection conn(String target, String method) {
        try {
            URL url = new URL(target);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            switch (method.toUpperCase()) {
                case "GET":
                case "PATCH":
                    break;
                case "POST":
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    conn.setInstanceFollowRedirects(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                    break;
                case "DELETE":
                    conn.setRequestMethod("DELETE");
                    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    break;
                case "PUT":
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("PUT");
                    conn.setInstanceFollowRedirects(true);
                    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
                    break;
                default:
                    break;
            }
            return conn;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    private static void set_headers(HttpURLConnection conn, HashMap<String, String> headers) {

        if(headers != null && !headers.isEmpty()) {
            headers.forEach((key, value) -> {
               conn.setRequestProperty(key, value);
            });
        }
    }
    /**
     * 	无参数 Get 请求
     * @param target 目标路由
     * @return context 响应上下文 {"url": "xxx", "data": {...}, "headers": {"content-type":"xxx" ...}}
     */
    public static Map<String, Object> get(String target) {
        return Request.getRequest(target);
    }
    /**
     *	有参数时的 Get 请求
     * @param target 目标路由
     * @param params 参数 {"id": "1"}
     * @return 同无参数 Get 请求
     */
    public static Map<String, Object> get(String target, Map<String, String> params) {
        target = Request.addParams("get", target, params);
        return Request.getRequest(target);

    }
    /**
     *	无参数 Post 请求
     * @param target 目标路由
     * @return 同无参数 Get 请求
     */
    public static Map<String, Object> post(String target) {
        return Request.postRequest(target, "", new HashMap<>());
    }
    /**
     *	有参数 Post 请求
     * @param target 目标路由
     * @param params 参数 {"id": "1"}
     * @return 同无参数 Get 请求
     */
    public static Map<String, Object> post(String target, Map<String, String> params) {
        String results = Request.addParams("post", "", params);
        return Request.postRequest(target, results, new HashMap<>());
    }
    /**
     *	带请求头 无参数的 Post 请求
     * @param target 目标路由
     * @return 同无参数 Get 请求
     */
    public static Map<String, Object> post(String target, HashMap<String, String> headers) {
        return Request.postRequest(target, "", headers);
    }
    /**
     *	带请求头且带有参数的 Post 请求
     * @param target 目标路由
     * @param params 参数 {"id": "1"}
     * @return 同无参数 Get 请求
     */
    public static Map<String, Object> post(String target, Map<String, String> params, HashMap<String, String> headers) {
        String results = Request.addParams("post", "", params);
        return Request.postRequest(target, results, headers);
    }
    /**
     * 	无参数 Delete 请求
     * @param target 目标路由
     * @return 同无参数 Get 请求
     */
    public static Map<String, Object> delete(String target) {
        return Request.deleteRequest(target);
    }
    /**
     *	有参数时的 Delete 请求
     * @param target 目标路由
     * @param params 参数 {"id": "1"}
     * @return 同无参数 Get 请求
     */
    public static Map<String, Object> delete(String target, Map<String, String> params) {
        target = Request.addParams("delete", target, params);
        return Request.deleteRequest(target);

    }
    /**
     *	无参数 Patch 请求
     * @param target 目标路由
     * @return 同无参数 Get 请求
     */
    public static Map<String, Object> patch(String target) {
        return Request.patchRequest(target, "");
    }
    /**
     *	有参数 Patch 请求
     * @param target 目标路由
     * @param params 参数 {"id": "1"}
     * @return
     */
    public static Map<String, Object> patch(String target, Map<String, String> params) {
        String results = Request.addParams("patch", "", params);
        return Request.patchRequest(target, results);
    }
    /**
     *	无参数 Put 请求
     * @param target 目标路由
     * @return 同无参数 Get 请求
     */
    public static Map<String, Object> put(String target) {
        return Request.putRequest(target, "");
    }
    /**
     *	有参数 Put 请求
     * @param target 目标路由
     * @param params 参数 {"id": "1"}
     * @return
     */
    public static Map<String, Object> put(String target, Map<String, String> params) {
        String results = Request.addParams("PUT", "", params);
        return Request.putRequest(target, results);
    }
    /**
     *	实际上的 Get 请求发送函数
     * @param target 目标路由
     * @return
     */
    private static Map<String, Object> getRequest(String target) {
        try {
            HttpURLConnection conn = Request.conn(target, "get");

            Map<String, Object> context = Request.context(conn);

            conn.disconnect();


            return context;

        } catch (Exception e) {
            System.out.print(e.getMessage());
            return new HashMap<>();
        }
    }
    /**
     * 	实际上的 Post 请求发送函数
     * @param target 目标路由
     * @param params
     * @return
     */
    private static Map<String, Object> postRequest(String target, String params, HashMap<String, String> headers) {

        try {

            HttpURLConnection conn = Request.conn(target, "post");
            Request.set_headers(conn, headers);
            //POST请求

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(conn.getOutputStream(), "UTF-8")
            );

            out.write(params);
            out.flush();
            out.close();

            conn.connect();

            Map<String, Object> context = Request.context(conn);

            conn.disconnect();

            return context;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new HashMap<>();
        }
    }
    /**
     *	实际上的 Delete 请求发送函数
     * @param target 目标路由
     * @return
     */
    private static Map<String, Object> deleteRequest(String target) {
        try {

            HttpURLConnection conn = Request.conn(target, "delete");

            Map<String, Object> context = Request.context(conn);

            conn.disconnect();


            return context;

        } catch (Exception e) {
            System.out.print(e.getMessage());
            return new HashMap<>();
        }
    }
    /**
     * 	实际上的 Put 请求发送函数
     * @param target 目标路由
     * @param params
     * @return
     */
    private static Map<String, Object> patchRequest(String target, String params) {

        try {
            URL url = new URL(target);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            conn.connect();
            //POST请求

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(conn.getOutputStream(), "UTF-8")
            );

            out.write(params);
            out.flush();
            out.close();



            Map<String, Object> context = Request.context(conn);

            conn.disconnect();

            return context;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new HashMap<>();
        }
    }
    /**
     * 	实际上的 Put 请求发送函数
     * @param target 目标路由
     * @param params
     * @return
     */
    private static Map<String, Object> putRequest(String target, String params) {

        try {
            HttpURLConnection conn = Request.conn(target, "put");

            //POST请求

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(conn.getOutputStream(), "UTF-8")
            );

            out.write(params);
            out.flush();
            out.close();

            conn.connect();

            Map<String, Object> context = Request.context(conn);

            conn.disconnect();

            return context;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new HashMap<>();
        }
    }
    /**
     * 	响应上下文处理函数，将 url,headers,data整合起来
     * @param conn
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private static Map<String, Object> context(HttpURLConnection conn)
            throws UnsupportedEncodingException, IOException {

        // 上下文-字典
        Map<String, Object> context = new HashMap<>();

        // 请求的 url
        String url = conn.getURL().toString();

        // 请求头
        Map<String, List<String>> headers = conn.getHeaderFields();

        // 读取的 json 数据
        String result = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
        in.close();
        Object data = JSON.parse(result);

        // 全部存放至上下文中
        context.put("url", url);
        context.put("headers", headers);
        context.put("data", data);

        return context;
    }
    /**
     * 	请求方法的参数处理
     * @param method 请求方法
     * @param target 目标路由
     * @param params 参数，为一个 Map
     * @return 返回处理好的字符串形式参数
     */
    private static String addParams(String method, String target, Map<String, String> params) {

        Set<String> keys = params.keySet();
        String result = "";
        for(String key : keys) {
            result += MessageFormat.format("{0}={1}&", key, params.get(key));
        }
        result = result.substring(0, result.length()-1);

        switch(method.toUpperCase()) {
            case "GET":
            case "DELETE":
                target += "?" + result;
                break;
            case "POST":
            case "PUT":
            case "PATCH":
                return result;
            default:
                break;
        }

        return target;
    }

}
