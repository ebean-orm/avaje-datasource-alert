package org.avaje.datasource;

import com.avaje.ebeaninternal.server.lib.sql.DataSourceAlert;
import com.avaje.ebeaninternal.server.lib.sql.SimpleDataSourceAlert;

/**
 * Service factory for creating the DataSourceAlert implementation.
 */
public class AlertFactory implements DataSourceAlertFactory {

  @Override
  public DataSourceAlert createAlert() {
    return new SimpleDataSourceAlert();
  }
}
