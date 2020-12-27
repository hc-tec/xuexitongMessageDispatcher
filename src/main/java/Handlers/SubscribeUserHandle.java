package Handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import config.StatusCode;

import Store.Store;
import Store.User;

import TcpServer.BaseRequest;
import TcpServer.BaseServerHandler;
import TcpServer.Response.JsonResponse;
import TcpServer.Response.ErrorResponse;

import utils.Crypto;
import utils.Request.Request;
import utils.Serializer.Serializer;
import utils.Exception.AuthenticateErrorException;

public class SubscribeUserHandle extends BaseServerHandler {

    /**
     * 订阅(注册)
     * 传来的参数为 e.g
     * {
     *     "user_name": "13177666570", // 学习通账号
     *     "password": "xxxxxx", // 学习通密码
     *     "qq": "2598772546" // 用于信息接收的 QQ
     * }
     * 返回的响应值为 e.g
     * {
     *     "qq": "2598772546",
     *     "password": "cxxxxxxxxxxxxxxx", // 经过 base64 加密后的密码
     *     "cookie": {
     *         "fid": "2xx",
     *         "UID": "103194xxx",
     *         "_d": "160908408xxxx",
     *         "vc3": "WrxxxxxxWn1miuyNgJ3fpWAsy50XJwR6OVu0PHFN0D9%2B00HtXnkS131RwjpgNUL%2ByrAYPWEdEDgWzfVUNgzTgTKWqYJ6ujOL7e1j2pBSaAlZ2sNf6E%2BTYFTfxB%2FFahV6XoBv%2FYJpPwO0gxvHl3bkIXYBftdlZWWq5pXLK1b%2FZA%3De9f7a6d73f9e863ffbf70a8721e3613d"
     *     },
     *     "secretKey": "3c5027ba3adb40c3a2f737097baa3e8e",
     *     "user_name": "13177666570",
     *     "publicKey": "a9d548792999424ea653349e900db5de",
     *     "lastMineTs": 0,
     *     "account": 0.0
     * }
     */
    public JsonResponse post(BaseRequest request) {
        HashMap<String, String> data = request.data;
        String user_name = data.get("user_name");
        String password = data.get("password");
        String qq = data.get("qq");
        // 判断是否早已存在此用户
        final Boolean isExist = Store.isUserExist(user_name);
        if(isExist) {
            return new ErrorResponse(
                    request.exchange,
                    StatusCode.UserAlreadyExist.code,
                    StatusCode.UserAlreadyExist.msg
            );
        }
        if(!this.isValid(user_name, password, qq)) {
            return new ErrorResponse(
                    request.exchange,
                    StatusCode.InvalidParams.code,
                    StatusCode.InvalidParams.msg);
        }
        // base64 加密用户密码
        password = Crypto.BASE64(password);
        // 通过 Cookie 获取登录认证
        HashMap<String, String> cookie = new HashMap<>();
        try {
            cookie = this.getCookies(user_name, password);
        } catch (AuthenticateErrorException e) {
            return new ErrorResponse(
                    request.exchange,
                    StatusCode.UsernameOrPasswordWrong.code,
                    StatusCode.UsernameOrPasswordWrong.msg);
        }
        // 储存用户信息
        User newSubscribeUser = Store.subscribe(user_name, password, qq, cookie);
        // 序列化，获取结果
        Serializer ser = new Serializer(newSubscribeUser);
        JSONObject userJsonObj = ser.serializeToObject();
        return new JsonResponse(request.exchange, userJsonObj);
    }

    /**
     * 获取 cookie
     * @param user_name 用户名
     * @param password 密码
     * @return HashMap<String, String>, cookie
     */
    private HashMap<String, String> getCookies(String user_name, String password)
            throws AuthenticateErrorException {
        HashMap<String, String> cookies = new HashMap<>();
        Map<String, String> params = (Map<String, String>) new HashMap<String, String>();
        params.put("uname", user_name);
        params.put("password", password);
        params.put("fid", "-1");
        params.put("refer", "http%3A%2F%2Fi.chaoxing.com");
        params.put("t", "true");
        // 向登录接口发送请求，获取临时登录凭证
        Map<String, Object> request = Request.post("https://passport2.chaoxing.com/fanyalogin", params);
        Map<String, List<String>> headers = (Map<String, List<String>>) request.get("headers");
        List<String> rawSetCookie = headers.get("Set-Cookie");
        HashMap<String, HashMap<String, String>> setCookies = this.parseSetCookies(rawSetCookie);
        // 四个登录凭证必需字段
        String[] needFields = {"fid", "_d", "UID", "vc3"};
        for (String field : needFields) {
            HashMap<String, String> targetObj = setCookies.get(field);
            if(targetObj == null)
                throw new AuthenticateErrorException("用户名或密码错误");
            cookies.put(field, targetObj.get(field));
        }
        return cookies;
    }

    /**
     * 判断用户名以及密码是否有效
     * @param user_name 用户名
     * @param password 密码
     * @return Boolean，是否有效
     */
    private Boolean isValid(String user_name, String password, String qq) {
        return user_name != null &&
                user_name.length() != 0 &&
                password != null &&
                password.length() != 0 &&
                qq != null &&
                qq.length() != 0;
    }

    /**
     * 对 set-cookie 进行解析
     * @param rawSetCookies 原生 set-cookie 数据
     * @return 解析之后的结果
     */
    private HashMap<String, HashMap<String, String>> parseSetCookies(List<String> rawSetCookies) {
        if(rawSetCookies == null) return null;
        HashMap<String, HashMap<String, String>> setCookies = new HashMap<>();
        for (String cookie : rawSetCookies) {
            String key = cookie.split("=")[0];
            HashMap<String, String> result = this._parseQuery(cookie, ";");
            setCookies.put(key, result);
        }
        return setCookies;
    }

    /**
     * 对 "user_name=titto&password=123456" 格式数据进行解析
     * @param rawQuery 原生数据
     * @param splitChar 用以分割不同字段的字符
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

}
