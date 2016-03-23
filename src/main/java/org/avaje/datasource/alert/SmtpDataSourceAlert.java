package org.avaje.datasource.alert;

import org.avaje.datasource.DataSourceAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple smtp email alert that sends a email message on dataSourceDown and
 * dataSourceUp etc.
 * <ul>
 * <li>alert.fromuser = the from user name
 * <li>alert.fromemail = the from email account
 * <li>alert.toemail = comma delimited list of email accounts to email
 * <li>alert.mailserver = the smpt server name
 * </ul>
 */
public class SmtpDataSourceAlert implements DataSourceAlert, MailListener {

  private static final Logger logger = LoggerFactory.getLogger(SmtpDataSourceAlert.class);

  private static final String alertMailServerName = System.getProperty("ebean.datasource.alert.mailserver");

  private static final String fromUser = System.getProperty("ebean.datasource.alert.fromUser");
  private static final String fromEmail = System.getProperty("ebean.datasource.alert.fromEmail");
  private static final String toEmail = System.getProperty("ebean.datasource.alert.toEmail");

  /**
   * Create a SimpleAlerter.
   */
  public SmtpDataSourceAlert() {
  }

  /**
   * If the email failed then log the error.
   */
  public void handleEvent(MailEvent event) {
    Throwable e = event.getError();
    if (e != null) {
      logger.error(null, e);
    }
  }

  /**
   * Send the dataSource down alert.
   */
  @Override
  public void dataSourceDown(String dataSourceName) {
    String msg = getSubject(true, dataSourceName);
    sendMessage(msg, msg);
  }

  /**
   * Send the dataSource up alert.
   */
  @Override
  public void dataSourceUp(String dataSourceName) {
    String msg = getSubject(false, dataSourceName);
    sendMessage(msg, msg);
  }

  /**
   * Send the warning message.
   */
  @Override
  public void dataSourceWarning(String subject, String msg) {
    sendMessage(subject, msg);
  }

  private String getSubject(boolean isDown, String dsName) {
    String msg = "The DataSource " + dsName;
    if (isDown) {
      msg += " is DOWN!!";
    } else {
      msg += " is UP.";
    }
    return msg;
  }

  private void sendMessage(String subject, String msg) {

    if (alertMailServerName == null) {
      return;
    }

    MailMessage data = new MailMessage();
    data.setSender(fromUser, fromEmail);
    data.addBodyLine(msg);
    data.setSubject(subject);

    String[] toList = toEmail.split(",");
    if (toList.length == 0) {
      logger.error("alert.toemail has not been set?");
    } else {
      for (int i = 0; i < toList.length; i++) {
        data.addRecipient(null, toList[i].trim());
      }
      MailSender sender = new MailSender(alertMailServerName);
      sender.setMailListener(this);
      sender.sendInBackground(data);
    }
  }

}
