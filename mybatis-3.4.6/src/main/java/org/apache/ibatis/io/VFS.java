/**
 * Copyright 2009-2017 the original author or authors.
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
package org.apache.ibatis.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * VFS表示虚拟文件系统（Virtual File System），它用来查找指定路径下的资源。
 * VFS是一个抽象类，MyBatis中提供了JBoss6VFS 和 DefaultVFS两个VFS的实现。
 *
 * @author kaifeng
 * @author Ben Gunter
 */
public abstract class VFS {
    private static final Log log = LogFactory.getLog(VFS.class);

    /**
     * 记录两个VFS实现类
     */
    public static final Class<?>[] IMPLEMENTATIONS = {JBoss6VFS.class, DefaultVFS.class};

    /**
     * 记录用户自定义的VFS实现类，通过 {@link #addImplClass(Class)}.将自定义实现类添加到USER_IMPLEMENTATIONS集合中
     */
    public static final List<Class<? extends VFS>> USER_IMPLEMENTATIONS = new ArrayList<Class<? extends VFS>>();

    /**
     * 单例模式，记录全局唯一VFS
     */
    private static class VFSHolder {
        static final VFS INSTANCE = createVFS();

        @SuppressWarnings("unchecked")
        static VFS createVFS() {
            // 优先使用用户自定义的VFS实现类，其次使用系统的
            List<Class<? extends VFS>> impls = new ArrayList<Class<? extends VFS>>();
            impls.addAll(USER_IMPLEMENTATIONS);
            impls.addAll(Arrays.asList((Class<? extends VFS>[]) IMPLEMENTATIONS));

            // 遍历实现类，依次实例化VFS对象并检测实例对象是否有效，如果获得有效实例对象则循环结束
            VFS vfs = null;
            for (int i = 0; vfs == null || !vfs.isValid(); i++) {
                Class<? extends VFS> impl = impls.get(i);
                try {
                    vfs = impl.newInstance();
                    if (vfs == null || !vfs.isValid()) {
                        if (log.isDebugEnabled()) {
                            log.debug("VFS implementation " + impl.getName() +
                                    " is not valid in this environment.");
                        }
                    }
                } catch (InstantiationException e) {
                    log.error("Failed to instantiate " + impl, e);
                    return null;
                } catch (IllegalAccessException e) {
                    log.error("Failed to instantiate " + impl, e);
                    return null;
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Using VFS adapter " + vfs.getClass().getName());
            }

            return vfs;
        }
    }

    /**
     * 获取全局唯一VFS实例化对象
     */
    public static VFS getInstance() {
        return VFSHolder.INSTANCE;
    }

    /**
     * 将用户自定义VFS实现类添加到集合USER_IMPLEMENTATIONS
     *
     * @param clazz 自定义VFS实现类
     */
    public static void addImplClass(Class<? extends VFS> clazz) {
        if (clazz != null) {
            USER_IMPLEMENTATIONS.add(clazz);
        }
    }

    /**
     * 根据类名获取类
     */
    protected static Class<?> getClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
//      return ReflectUtil.findClass(className);
        } catch (ClassNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.debug("Class not found: " + className);
            }
            return null;
        }
    }

    /**
     * 根据指定的类，方法名及方法参数获取方法对象
     *
     * @param clazz          方法所属类对象
     * @param methodName     方法名
     * @param parameterTypes 方法参数
     */
    protected static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (SecurityException e) {
            log.error("Security exception looking for method " + clazz.getName() + "." + methodName + ".  Cause: " + e);
            return null;
        } catch (NoSuchMethodException e) {
            log.error("Method not found " + clazz.getName() + "." + methodName + "." + methodName + ".  Cause: " + e);
            return null;
        }
    }

    /**
     * 调用指定对象的指定方法并返回内容
     *
     * @param method     指定的方法对象
     * @param object     实例化对象
     * @param parameters 方法参数
     * @return 返回调用方法的返回值
     * @throws IOException      If I/O errors occur
     * @throws RuntimeException If anything else goes wrong
     */
    @SuppressWarnings("unchecked")
    protected static <T> T invoke(Method method, Object object, Object... parameters)
            throws IOException, RuntimeException {
        try {
            return (T) method.invoke(object, parameters);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof IOException) {
                throw (IOException) e.getTargetException();
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据指定的路径，使用当前线程绑定的类加载器获取该路径下所有资源的URL
     *
     * @param path 指定的资源路径
     * @return 所有资源的URL集合 {@link ClassLoader#getResources(String)}.
     * @throws IOException If I/O errors occur
     */
    protected static List<URL> getResources(String path) throws IOException {
        return Collections.list(Thread.currentThread().getContextClassLoader().getResources(path));
    }

    /**
     * 检测当前VFS对象在当前环境下是否有效
     */
    public abstract boolean isValid();

    /**
     * 查找指定的资源名称列表，
     *
     * @param url     资源url地址
     * @param forPath URL标识的资源的路径
     * @return 包含资源名称的集合
     * @throws IOException If I/O errors occur
     */
    protected abstract List<String> list(URL url, String forPath) throws IOException;

    /**
     * 递归列出在指定路径中找到的所有的完整资源路径。
     *
     * @param path 资源路径
     * @return 所有的完整资源路径集合
     * @throws IOException If I/O errors occur
     */
    public List<String> list(String path) throws IOException {
        List<String> names = new ArrayList<String>();
        for (URL url : getResources(path)) {
            names.addAll(list(url, path));
        }
        return names;
    }
}
