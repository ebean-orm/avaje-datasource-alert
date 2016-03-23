package org.avaje.datasource;

import org.avaje.datasource.alert.SmtpDataSourceAlert;

/**
 * Service factory for creating the DataSourceAlert implementation.
 */
public class AlertFactory implements DataSourceAlertFactory {

  @Override
  public DataSourceAlert createAlert() {
    return new SmtpDataSourceAlert();
  }
}
