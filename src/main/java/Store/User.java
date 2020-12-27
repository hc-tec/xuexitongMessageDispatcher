package Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class User {
    public String user_name; // 学习通账号
    public String password; // md5 加密后的学习通密码
    public String qq; // 接收消息的 qq
    public String secretKey; // 私钥
    public String publicKey; // 公钥
    public Double account; // 拥有的币值
    public long lastMineTs; // 上一个挖矿时记录的时间戳
    public HashMap<String, String> cookie; // 认证 cookie
    private ArrayList<String> visitedMsgIdList; // 用户已接收到的消息 ID

    public User(String user_name, String password, String qq, HashMap<String, String> cookie) {
        this.user_name = user_name;
        this.password = password;
        this.qq = qq;
        this.cookie = cookie;
        this.account = 0.0;
        this.publicKey = this.generateKey();
        this.secretKey = this.generateKey();
        this.visitedMsgIdList = new ArrayList<String>();
    }

    public String generateKey() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public void addVisMsgId(String msgId) {
        this.visitedMsgIdList.add(msgId);
    }

    public Double addAccount(Double coin) {
        // 溢出检测
        if(this.account+coin > this.account) {
            this.account += coin;
        }
        return this.account;
    }

    public void logTimestamp() {
        this.lastMineTs = System.currentTimeMillis();
    }

    public Boolean isMsgIdExist(String msgId) {
        return this.visitedMsgIdList.contains(msgId);
    }

}

