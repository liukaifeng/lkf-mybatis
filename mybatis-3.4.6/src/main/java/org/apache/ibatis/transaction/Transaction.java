/**
 * Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务抽象接口
 * 处理数据库连接的生命周期: 创建、准备、提交/回滚、关闭
 *
 * @author kaifeng
 * @author Clinton Begin
 */
public interface Transaction {

    /**
     * 获取数据库连接对象
     *
     * @return DataBase connection
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交
     *
     * @throws SQLException
     */
    void commit() throws SQLException;

    /**
     * 回滚
     *
     * @throws SQLException
     */
    void rollback() throws SQLException;

    /**
     * 关闭数据库连接
     *
     * @throws SQLException
     */
    void close() throws SQLException;

    /**
     * 获取事务超时时间
     *
     * @throws SQLException
     */
    Integer getTimeout() throws SQLException;

}
