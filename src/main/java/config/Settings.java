package config;

public class Settings {

    public final static Integer port = 8000; // 服务器监听端口

    public final static String emailSender = "sunjitao@ncuteam.com.cn"; // 邮件发送着账号
    public final static String emailPassword = "Sun19961203"; // 邮件发送者密码

    public final static Integer BLOCK_TXNS = 5; // 一个区块中包含的交易数

    public final static Double MINE_COIN = 1.024; // 交易有效时的 coin 值
    public final static Double SCAN_COIN = 0.128; // 扫描有效时的 coin 值

    public final static Double SCAN_INTERVAL = 0.1; // 进入无矿可挖状态时，每多少分钟可以算作有效请求

}
