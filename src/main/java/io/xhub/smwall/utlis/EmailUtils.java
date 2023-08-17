package io.xhub.smwall.utlis;

import io.xhub.smwall.config.EmailProperties;
import io.xhub.smwall.domains.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
@Component
public class EmailUtils {
    private final EmailProperties emailProperties;

    public void sendEmail(User user, String htmlTemplatePath) throws MessagingException, IOException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", String.valueOf(emailProperties.getSmtp().isAuth()));
        prop.put("mail.smtp.host", emailProperties.getSmtp().getHost());
        prop.put("mail.smtp.port", String.valueOf(emailProperties.getSmtp().getPort()));
        prop.setProperty("mail.smtp.starttls.enable", String.valueOf(emailProperties.getSmtp().isStarttlsEnable()));
        prop.setProperty("mail.smtp.ssl.protocols", emailProperties.getSmtp().getSslProtocols());

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProperties.getUsername(), emailProperties.getPassword());
            }
        });
        String authoritiesNames = user.getAuthorities()
                .stream()
                .map(authority -> authority.getName().toString().substring(5))
                .collect(Collectors.joining(", "));

        String personalizedTitle = String.format(" %s %s %s",
                authoritiesNames, user.getFirstName(), user.getLastName());
        String customEmail = String.format(" %s ", user.getEmail());


        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailProperties.getUsername()));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(user.getEmail())
        );

        message.setSubject("welcome " + user.getLastName() + user.getFirstName());

        String htmlContent = loadHtmlTemplate(htmlTemplatePath);

        htmlContent = htmlContent.replace("{{ personalizedTitle }}", personalizedTitle);
        htmlContent = htmlContent.replace("{email}", customEmail);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        Transport.send(message);

        log.info("email sent successfully");

    }

    private static String loadHtmlTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(bytes, StandardCharsets.UTF_8);
    }
}