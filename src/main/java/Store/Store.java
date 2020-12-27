package Store;

import java.util.ArrayList;
import java.util.HashMap;


public class Store {

    public static ArrayList<User> userInfo = new ArrayList<User>(); // 订阅服务的用户信息

    public static User subscribe(String user_name, String password, String qq, HashMap<String, String> cookie) {
        User newSubscribeUser = new User(user_name, password, qq, cookie);
        Store.userInfo.add(newSubscribeUser);
        return newSubscribeUser;
    }

    /**
     * 用户是否已存在，指不能重复订阅
     * @param user_name 用户学习通账号
     * @return 用户是否已存在
     */
    public static Boolean isUserExist(String user_name) {
        for (User user : Store.userInfo) {
            if(user.user_name.equals(user_name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过公钥 get 用户信息
     * @param publicKey 用户公钥
     * @return 用户信息
     */
    public static User getUserByPublicKey(String publicKey) {
        for (User user : Store.userInfo) {
            if(user.publicKey.equals(publicKey)) {
                return user;
            }
        }
        return null;
    }

    /**
     * 通过私钥 get 用户信息
     * @param secretKey 用户私钥
     * @return 用户信息
     */
    public static User getUserBySecretKey(String secretKey) {
        for (User user : Store.userInfo) {
            if(user.secretKey.equals(secretKey)) {
                return user;
            }
        }
        return null;
    }


}
