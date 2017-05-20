package com.kotlarz.mail.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.kotlarz.domain.AbstractJobOffer;
import com.kotlarz.translator.Translator;

@Service
public class MailSenderService {
	private final String EXTERNAL_IP_URL = "http://checkip.amazonaws.com/";

	public List<String> getEmailsToSend(String propFilePath) throws FileNotFoundException, IOException {
		Properties props = loadProperties(propFilePath);

		if (!props.containsKey("destination"))
			return null;

		String[] mailArray = props.getProperty("destination").split(";");
		return Arrays.asList(mailArray);
	}

	public Properties loadProperties(String filePath) throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(new File(filePath)));
		return props;
	}

	private String readClassPathFile(String path) throws IOException {
		String filePath = path;

		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}

	public void sendOffers(Map<String, List<AbstractJobOffer>> jobOffers, String emailAddress, Properties emailProps)
			throws MessagingException, IOException {
		String emailTemplate = emailProps.getProperty("emailTemplate");
		String mailContent = parseEmail(jobOffers, readClassPathFile(emailTemplate));

		sendMail(mailContent, emailAddress, emailProps);
	}

	public String parseEmail(Map<String, List<AbstractJobOffer>> jobOffers, String mailTemplate) throws IOException {
		Document document = Jsoup.parse(mailTemplate);
		document.getElementById("externalAddress").attr("href", "http://" + getExternalIp());

		StringBuilder builder = new StringBuilder();
		for (Entry<String, List<AbstractJobOffer>> jobEntries : jobOffers.entrySet()) {
			if(jobEntries.getValue().isEmpty())
				continue;
			
			builder.append("<tr><td><h1>");
			builder.append(Translator.getMessage(jobEntries.getKey()));
			builder.append("</h1></td></tr>");

			for (AbstractJobOffer jobOffer : jobEntries.getValue()) {
				builder.append("<tr><td>");
				builder.append("<a href=\"" + jobOffer.getLink() + "\" style=\"width: 100%\">" + jobOffer.getName() + "</a>");
				builder.append("</td></tr>");
			}
		}
		
		Element tableElement = document.getElementById("jobOffers");
		tableElement.html(builder.toString());
		
		return document.toString();
	}

	private String getExternalIp() throws IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(EXTERNAL_IP_URL);
		HttpResponse response = client.execute(get);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}

	public void sendMail(String mailContent, String emailAddress, Properties emailProps)
			throws AddressException, MessagingException {
		String host = emailProps.getProperty("host");
		String from = emailProps.getProperty("from");
		String user = emailProps.getProperty("user");
		String password = emailProps.getProperty("password");
		Integer port = Integer.parseInt(emailProps.getProperty("port"));
		String subject = emailProps.getProperty("subject");

		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.socketFactory.port", port.toString());
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", port.toString());

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));
		message.setSubject(subject);
		message.setContent(mailContent, "text/html; charset=utf-8");

		Transport.send(message);
	}
}
