package TcpServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import TcpServer.Response.BaseResponse;
import TcpServer.Response.ErrorResponse;
import config.StatusCode;

public class BaseServerHandler implements HttpHandler {

    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod().toLowerCase();
        try {
            // 反射，分发特定的请求方法
            Method requestMethod = this.getClass().getMethod(method, BaseRequest.class);
            // 执行反射，获取到相应结果
            BaseResponse response = (BaseResponse) requestMethod.invoke(
                    this, new BaseRequest(exchange)
            );
            // 向请求方发送响应结果
            BaseResponse.write(exchange, response.responseString);
            this.logger(exchange, method.toUpperCase());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            BaseResponse.write(exchange,
                    new ErrorResponse(exchange,
                            StatusCode.NonsupportMethod.code,
                            StatusCode.NonsupportMethod.msg)
                            .responseString);
            this.logger(exchange, method.toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            BaseResponse.write(exchange,
                    new ErrorResponse(exchange,
                            StatusCode.UnknownCondition.code,
                            StatusCode.UnknownCondition.msg.concat(e.getMessage()))
                            .responseString);
            this.logger(exchange, method.toUpperCase());
        }
    }

    public void logger(HttpExchange exchange, String requestMethod) {
        // [Date String] host:port method /urlPath/
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        StringBuilder log = new StringBuilder();
        log.append("[").append(df.format(new Date())).append("] ");
        URI requestUri = exchange.getRequestURI();
        InetSocketAddress addr = exchange.getLocalAddress();
        String host = addr.getHostName();
        int port = addr.getPort();
        String path = requestUri.getPath();
        log.append(host).append(":").append(port).append(" ");
        log.append(requestMethod).append(" ");
        log.append(path);
        System.out.println(log);
    }

}
