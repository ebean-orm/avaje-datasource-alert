package org.avaje.datasource.alert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Sends simple MailMessages via smtp.
 */
public class MailSender implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

  private static final int SMTP_PORT = 25;

  private final String server;

  private BufferedReader in;

  private OutputStreamWriter out;

  private MailMessage message;

  private MailListener listener;

  /**
   * Create for a given mail server.
   */
  public MailSender(String server) {
    this.server = server;
  }

  /**
   * Set the listener to handle MessageEvents.
   */
  public void setMailListener(MailListener listener) {
    this.listener = listener;
  }

  /**
   * Send the message.
   */
  public void run() {
    send(message);
  }

  /**
   * Send the message in a background thread.
   */
  public void sendInBackground(MailMessage message) {
    this.message = message;
    Thread thread = new Thread(this);
    thread.start();
  }

  /**
   * Send the message in the current thread.
   */
  public void send(MailMessage message) {
    try {
      for (MailAddress recipientAddress : message.getRecipientList()) {
        Socket socket = new Socket(server, SMTP_PORT);
        send(message, socket, recipientAddress);
        socket.close();

        if (listener != null) {
          MailEvent event = new MailEvent(message, null);
          listener.handleEvent(event);
        }
      }
    } catch (Exception ex) {
      if (listener != null) {
        MailEvent event = new MailEvent(message, ex);
        listener.handleEvent(event);
      } else {
        logger.error(null, ex);
      }
    }
  }

  private void send(MailMessage message, Socket sserver, MailAddress recipientAddress) throws IOException {

    // A bit convoluted, but doesn't depend on DNS in any way...
    InetAddress localhost = sserver.getLocalAddress();
    String localaddress = localhost.getHostAddress();
    MailAddress sender = message.getSender();
    message.setCurrentRecipient(recipientAddress);

    // Mandatory header fields, Date and From
    if (message.getHeader("Date") == null) {
      message.addHeader("Date", new java.util.Date().toString());
    }
    if (message.getHeader("From") == null) {
      message.addHeader("From", sender.getAlias() + " <" + sender.getEmailAddress() + ">");
    }

    message.addHeader("To", recipientAddress.getAlias() + " <" + recipientAddress.getEmailAddress() + ">");

    out = new OutputStreamWriter(sserver.getOutputStream());
    in = new BufferedReader(new InputStreamReader(sserver.getInputStream()));
    String sintro = readln();
    if (!sintro.startsWith("220")) { // 220
      logger.debug("SmtpSender: intro==" + sintro);
      return;
    }

    writeln("EHLO " + localaddress);
    if (!expect250()) {
      return;
    }

    writeln("MAIL FROM:<" + sender.getEmailAddress() + ">");
    if (!expect250()) {
      return;
    }
    writeln("RCPT TO:<" + recipientAddress.getEmailAddress() + ">");
    if (!expect250()) {
      return;
    }
    writeln("DATA");
    while (true) { // may be multiple 250 replies pending from server
      String line = readln();
      if (line.startsWith("3"))
        break; // ready to send
      if (!line.startsWith("2")) {
        logger.debug("SmtpSender.send reponse to DATA: " + line);
        return;
      }
    }
    for (String key : message.getHeaderFields()) {
      writeln(key + ": " + message.getHeader(key));
    }
    writeln(""); // end of header;
    for (String bline : message.getBodyLines()) {
      if (bline.startsWith(".")) {
        bline = "." + bline;
      }
      writeln(bline);
    }
    writeln(".");
    expect250();
    writeln("QUIT");

  }

  private boolean expect250() throws IOException {
    String line = readln();
    if (!line.startsWith("2")) {
      logger.info("SmtpSender.expect250: " + line);
      return false;
    }
    return true;
  }

  private void writeln(String s) throws IOException {
    out.write(s + "\r\n");
    out.flush();
  }

  private String readln() throws IOException {
    return in.readLine();
  }

}
