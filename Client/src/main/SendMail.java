package main;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import logfilename.NewLogForEachRunFileAppender;

 

import ui.URLConfiguration;

public class SendMail {
    public static void sendMail() {

        URLConfiguration cfg = new URLConfiguration();
        cfg = Utility25_10.getConfiguration();
        String msg =
            "\n-------------------------------- Statistics --------------------------------------------------" +
            "\nRows processed    : " + Main25_10.totalEmployee + "\nPosted to fusion  : " + Main25_10.postedEmployee +
            "\nNot Posted        : " + (Main25_10.totalEmployee - Main25_10.postedEmployee) +
            "\n----------------------------------------------------------------------------------------------\n";

        String username = cfg.getFromMailId();
        String password = cfg.getFromMailPassword();
        String toRecipient = cfg.getToRecipient();
        String ccRecipient = cfg.getCcRecipient();

        Properties props = new Properties();
        props.put("mail.smtp.host", cfg.getSmtpHost());
        props.put("mail.smtp.port", cfg.getSmtpPort());
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toRecipient));
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccRecipient));

            message.setSubject("Error Log File");

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hii\nPlease resolve the errors Which is in the error log file\n" + msg + "\nPFA");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            String filename = NewLogForEachRunFileAppender.logFileName;
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("All Done");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
