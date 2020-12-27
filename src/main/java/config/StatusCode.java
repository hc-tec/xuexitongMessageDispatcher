package config;

public class StatusCode {

    public final static Status InvalidParams = new Status(
            "400", "Invalid Params");

    public final static Status NonsupportMethod = new Status(
            "401", "Nonsupport utils.Request Method");

    public final static Status IncorrectProof = new Status(
            "402", "Invalid Proof");

    public final static Status MsgIdAlreadyExist = new Status(
            "403", "Message Id is already exist");

    public final static Status UnknownCondition = new Status(
            "404", "Unknown Condition");

    public final static Status UsernameOrPasswordWrong = new Status(
            "405", "Username or Password Wrong");

    public final static Status UserAlreadyExist = new Status(
            "406", "The user is already register");

    public final static Status IntervalTooShort = new Status(
            "407", "Interval must longer than 10 minutes when no receiver");


}
