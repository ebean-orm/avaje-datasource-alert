package org.avaje.datasource.alert;

import org.testng.annotations.Test;


public class MailSenderTest {

  @Test(enabled = false)
  public void send() throws Exception {

    MailSender sender = new MailSender("localhost");
    MailMessage message = new MailMessage();
    message.setSubject("hello");
    message.addBodyLine("some body");
    message.addRecipient("Rob", "robin.bygrave@gmail.com");

    sender.send(message);
  }
}