package org.avaje.datasource.alert;

/**
 * Listens to see if the message was successfully sent.
 */
public interface MailListener {

  /**
   * Handle the message event.
   */
  void handleEvent(MailEvent event);

}
