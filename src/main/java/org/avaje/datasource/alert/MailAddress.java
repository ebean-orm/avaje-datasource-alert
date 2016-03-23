package org.avaje.datasource.alert;

/**
 * An Email address with an associated alias.
 */
public class MailAddress {


  private final String alias;

  private final String emailAddress;

  /**
   * Create an address with an optional alias.
   */
  public MailAddress(String alias, String emailAddress) {
    this.alias = alias;
    this.emailAddress = emailAddress;
  }

  /**
   * Return the alias.
   * If the alias is null this returns an empty string.
   */
  public String getAlias() {
    if (alias == null) {
      return "";
    }
    return alias;
  }

  /**
   * Return the email address.
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  public String toString() {
    return getAlias() + " " + "<" + getEmailAddress() + ">";
  }

}
