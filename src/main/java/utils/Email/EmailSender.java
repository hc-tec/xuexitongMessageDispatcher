package utils.Email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    private final String senderEmail;
    private final String senderPassword;
    private final String smtpType = "smtp.exmail.qq.com";

    public EmailSender(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    public void send(String title, String text, String receiver) {
        Session session = this.getSession();
        MimeMessage message = this.getMessage(session, title, text, receiver);
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect(this.smtpType, this.senderEmail, this.senderPassword);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Session getSession() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", this.smtpType);
        props.put("mail.smtp.host", this.smtpType);
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);
        return session;
    }

    private MimeMessage getMessage(Session session, String title, String text, String receiver) {
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(this.senderEmail);
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(receiver));
            message.setSubject(title);
            message.setText(text);
            message.saveChanges();
        } catch (Exception e) {
        }
        return message;
    }


}
