package com.odk.connect.service;

import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import static javax.mail.Message.RecipientType.*;
import org.springframework.stereotype.Service;
import com.sun.mail.smtp.SMTPTransport;
import static com.odk.connect.constants.EmailConstant.*;

@Service
public class EmailService {
	public void sendNewPasswordEmail(String prenom, String password, String email) throws MessagingException {
		Message message = createEmail(prenom, password, email);
		SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFERT_PROTOCOL);
		smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
		smtpTransport.sendMessage(message, message.getAllRecipients());
		smtpTransport.close();
	}

	public void sendNewSubscribeEmail(URL url, String email) throws MessagingException {
		Message subscribeMessage = createSubcribeEmail(url, email);
		SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFERT_PROTOCOL);
		smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
		smtpTransport.sendMessage(subscribeMessage, subscribeMessage.getAllRecipients());
		smtpTransport.close();
	}

	private Message createEmail(String prenom, String password, String email) throws MessagingException {
		Message message = new MimeMessage(getEmailSession());
		message.setFrom(new InternetAddress(FROM_EMAIL));
		message.setRecipients(TO, InternetAddress.parse(email, false));
		message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
		message.setSubject(EMAIL_SUBJECT);
		message.setText("Bonjour " + prenom + ", \n \n le nouveau mot de passe de votre compte est : " + password
				+ "\n \n OdkConnect team");
		message.setSentDate(new Date());
		message.saveChanges();
		return message;
	}

	private Message createSubcribeEmail(URL url, String email) throws MessagingException {
		Message subscribeMessage = new MimeMessage(getEmailSession());
		subscribeMessage.setFrom(new InternetAddress(FROM_EMAIL));
		subscribeMessage.setRecipients(TO, InternetAddress.parse(email, false));
		subscribeMessage.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
		subscribeMessage.setSubject(EMAIL_SUBJECTS);
		subscribeMessage.setText("Bonjour "
				+ ", \n \n cliquez sur le lien pour vous inscrire sur la plateforme  des alumnis de Orange digital Kalanso : "
				+ url + "\n \n OdkConnect team");
		subscribeMessage.setSentDate(new Date());
		subscribeMessage.saveChanges();
		return subscribeMessage;
	}

	private Session getEmailSession() {
		Properties properties = System.getProperties();
		properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
		properties.put(SMTP_AUTH, true);
		properties.put(SMTP_PORT, DEFAULT_PORT);
		properties.put(SMTP_STARTTLS_ENABLE, true);
		properties.put(SMTP_STARTTLS_REQUIRED, true);
		return Session.getInstance(properties, null);
	}

}
