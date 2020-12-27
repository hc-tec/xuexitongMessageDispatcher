package TcpServer.Response;

import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class BaseResponse {

    public HttpExchange exchange;
    public String responseString;

    public BaseResponse(HttpExchange exchange, String responseString) {
        this.exchange = exchange;
        this.responseString = responseString;
    }

    public static void write(HttpExchange exchange, String responseString) {
        try {
            // 设置传输数据长度
            int length = responseString.getBytes("UTF-8").length;
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, length);
            OutputStream responseBody = exchange.getResponseBody();
            OutputStreamWriter writer = new OutputStreamWriter(responseBody, "UTF-8");
            // 向请求方发送数据
            writer.write(responseString);
            writer.close();
        } catch (Exception ignored) {

        }
    }
}