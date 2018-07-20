/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.datasource.unpooled;

import org.apache.ibatis.io.Resources;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 不使用连接池的数据源
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class UnpooledDataSource implements DataSource {
    /**
     * 数据源驱动类加载器
     */
    private ClassLoader driverClassLoader;
    /**
     * 驱动连接属性
     */
    private Properties driverProperties;

    /**
     * 已注册的驱动集合
     */
    private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<String, Driver>();

    /**
     * 当前使用的驱动
     */
    private String driver;
    /**
     * 数据源地址
     */
    private String url;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 是否自动提交事务
     */
    private Boolean autoCommit;

    /**
     * 默认事务隔离级别
     */
    private Integer defaultTransactionIsolationLevel;

    // 静态代码块，当类加载的时候，就从DriverManager中获取所有的驱动信息，放到当前维护的Map中
    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            registeredDrivers.put(driver.getClass().getName(), driver);
        }
    }

    public UnpooledDataSource() {
    }

    public UnpooledDataSource( String driver, String url, String username, String password ) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UnpooledDataSource( String driver, String url, Properties driverProperties ) {
        this.driver = driver;
        this.url = url;
        this.driverProperties = driverProperties;
    }

    public UnpooledDataSource( ClassLoader driverClassLoader, String driver, String url, String username, String password ) {
        this.driverClassLoader = driverClassLoader;
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UnpooledDataSource( ClassLoader driverClassLoader, String driver, String url, Properties driverProperties ) {
        this.driverClassLoader = driverClassLoader;
        this.driver = driver;
        this.url = url;
        this.driverProperties = driverProperties;
    }

    /**
     * 获取数据源连接对象
     */
    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(username, password);
    }

    /**
     * 获取数据源连接对象
     *
     * @param username 用户名
     * @param password 密码
     */
    @Override
    public Connection getConnection( String username, String password ) throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public void setLoginTimeout( int loginTimeout ) throws SQLException {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public void setLogWriter( PrintWriter logWriter ) throws SQLException {
        DriverManager.setLogWriter(logWriter);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    public ClassLoader getDriverClassLoader() {
        return driverClassLoader;
    }

    public void setDriverClassLoader( ClassLoader driverClassLoader ) {
        this.driverClassLoader = driverClassLoader;
    }

    public Properties getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties( Properties driverProperties ) {
        this.driverProperties = driverProperties;
    }

    public String getDriver() {
        return driver;
    }

    public synchronized void setDriver( String driver ) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public Boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit( Boolean autoCommit ) {
        this.autoCommit = autoCommit;
    }

    public Integer getDefaultTransactionIsolationLevel() {
        return defaultTransactionIsolationLevel;
    }

    public void setDefaultTransactionIsolationLevel( Integer defaultTransactionIsolationLevel ) {
        this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
    }

    /**
     * 根据指定用户名和密码获取数据源连接对象
     *
     * @param username 用户名
     * @param password 密码
     */
    private Connection doGetConnection( String username, String password ) throws SQLException {
        Properties props = new Properties();
        if (driverProperties != null) {
            props.putAll(driverProperties);
        }
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        return doGetConnection(props);
    }

    /**
     * 根据指定属性获取数据源连接对象
     *
     * @param properties 配置属性
     */
    private Connection doGetConnection( Properties properties ) throws SQLException {
        //初始化数据源连接驱动
        initializeDriver();
        //从DriverManager中获取数据库连接
        Connection connection = DriverManager.getConnection(url, properties);
        //设置连接对象
        configureConnection(connection);
        return connection;
    }

    /**
     * 初始化数据源连接驱动
     */
    private synchronized void initializeDriver() throws SQLException {
        //没有注册的驱动，需要加载到registeredDrivers集合中
        if (!registeredDrivers.containsKey(driver)) {
            Class<?> driverType;
            try {
                // 加载数据库连接驱动
                if (driverClassLoader != null) {
                    driverType = Class.forName(driver, true, driverClassLoader);
                } else {
                    driverType = Resources.classForName(driver);
                }
                // DriverManager requires the driver to be loaded via the system ClassLoader.
                // http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
                //创建驱动实例
                Driver driverInstance = (Driver) driverType.newInstance();
                //注册到DriverManager中，用于创建数据库连接,代理模式实例化driver对象
                DriverManager.registerDriver(new DriverProxy(driverInstance));
                registeredDrivers.put(driver, driverInstance);
            } catch (Exception e) {
                throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
            }
        }
    }

    /**
     * 设置连接对象（事务隔离级别及是否自动提交事务）
     *
     * @param conn 数据源连接对象
     */
    private void configureConnection( Connection conn ) throws SQLException {
        //设置是否自动提交事务
        if (autoCommit != null && autoCommit != conn.getAutoCommit()) {
            conn.setAutoCommit(autoCommit);
        }
        //设置默认事务隔离级别
        if (defaultTransactionIsolationLevel != null) {
            conn.setTransactionIsolation(defaultTransactionIsolationLevel);
        }
    }

    /**
     * 具体Driver对象的代理类
     */
    private static class DriverProxy implements Driver {
        private Driver driver;

        DriverProxy( Driver d ) {
            this.driver = d;
        }

        @Override
        public boolean acceptsURL( String u ) throws SQLException {
            return this.driver.acceptsURL(u);
        }

        @Override
        public Connection connect( String u, Properties p ) throws SQLException {
            return this.driver.connect(u, p);
        }

        @Override
        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo( String u, Properties p ) throws SQLException {
            return this.driver.getPropertyInfo(u, p);
        }

        @Override
        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        // @Override only valid jdk7+
        public Logger getParentLogger() {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }

    @Override
    public <T> T unwrap( Class<T> iface ) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor( Class<?> iface ) throws SQLException {
        return false;
    }

    // @Override only valid jdk7+
    public Logger getParentLogger() {
        // requires JDK version 1.6
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

}
