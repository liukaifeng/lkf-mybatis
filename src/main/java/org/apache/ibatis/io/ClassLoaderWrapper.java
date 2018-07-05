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
package org.apache.ibatis.io;

import java.io.InputStream;
import java.net.URL;

/**
 * 该类封装了多个类加载器，统一了使用入口
 *
 * @author Clinton Begin
 */
public class ClassLoaderWrapper {

    //默认类加载器
    ClassLoader defaultClassLoader;
    //系统类加载器
    ClassLoader systemClassLoader;

    /**
     * 无参构造函数
     */
    ClassLoaderWrapper() {
        try {
            //初始化系统类加载器
            systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException ignored) {
            // AccessControlException on Google App Engine
        }
    }

    /**
     * 使用当前类路径获取资源作为URL
     *
     * @param resource 要定位的资源
     * @return 返回URL或null
     */
    public URL getResourceAsURL(String resource) {
        return getResourceAsURL(resource, getClassLoaders(null));
    }

    /**
     * Get a resource from the classpath, starting with a specific class loader
     *
     * @param resource    - 要定位的资源
     * @param classLoader - 指定的类加载器
     * @return 返回URL或null
     */
    public URL getResourceAsURL(String resource, ClassLoader classLoader) {
        return getResourceAsURL(resource, getClassLoaders(classLoader));
    }

    /**
     * 从指定路径获取资源
     *
     * @param resource - 要定位的资源
     * @return 返回流格式资源或null
     */
    public InputStream getResourceAsStream(String resource) {
        return getResourceAsStream(resource, getClassLoaders(null));
    }

    /**
     * 根据指定路径和类加载器查找资源
     *
     * @param resource    - 要定位的资源
     * @param classLoader - 指定的类加载器
     * @return 返回流格式资源或null
     */
    public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
        return getResourceAsStream(resource, getClassLoaders(classLoader));
    }

    /**
     * 根据类名查找类对象
     *
     * @param name - 要查找的类对象
     * @return - 返回找到的类对象
     * @throws ClassNotFoundException 不存在抛出异常
     */
    public Class<?> classForName(String name) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(null));
    }

    /**
     * 根据指定的类名，优先使用指定的类加载器查找类对象
     *
     * @param name        - 要查找的类对象
     * @param classLoader - 优先使用指定的类加载器
     * @return - 返回找到的类对象
     * @throws ClassNotFoundException 不存在抛出异常
     */
    public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(classLoader));
    }

    /**
     * 根据指定的资源路径，尝试使用各种类加载器查找资源
     *
     * @param resource    - 要查找的资源
     * @param classLoader - 类加载器数组
     * @return 返回找到的资源或null
     */
    InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
        for (ClassLoader cl : classLoader) {
            if (null != cl) {

                // 通过使用不同的类加载器查找资源
                InputStream returnValue = cl.getResourceAsStream(resource);

                // 如果没有找到资源，通过在指定路径前面加上“/”，重新尝试一次
                if (null == returnValue) {
                    returnValue = cl.getResourceAsStream("/" + resource);
                }
                // 找到资源并返回
                if (null != returnValue) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    /**
     * 根据指定的资源路径，尝试使用多种类加载器查找资源
     *
     * @param resource    - 指定的资源路径
     * @param classLoader - 多种类加载器组成的数组
     * @return 返回找到的资源或null
     */
    URL getResourceAsURL(String resource, ClassLoader[] classLoader) {

        URL url;

        for (ClassLoader cl : classLoader) {

            if (null != cl) {

                // 通过使用不同的类加载器查找资源
                url = cl.getResource(resource);

                // 如果没有找到资源，通过在指定路径前面加上“/”，重新尝试一次
                if (null == url) {
                    url = cl.getResource("/" + resource);
                }

                // 找到资源并返回
                if (null != url) {
                    return url;
                }

            }

        }

        // 没找到返回null
        return null;

    }

    /**
     * 根据指定的类名，尝试使用各种类加载器查找类对象
     *
     * @param name        - 要定位的类名
     * @param classLoader - 类加载器组成的数组
     * @return the class
     * @throws ClassNotFoundException - 抛出找不到异常
     */
    Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {

        for (ClassLoader cl : classLoader) {

            if (null != cl) {

                try {

                    Class<?> c = Class.forName(name, true, cl);

                    if (null != c) {
                        return c;
                    }

                } catch (ClassNotFoundException e) {
                    // we'll ignore this until all classloaders fail to locate the class
                }

            }

        }

        throw new ClassNotFoundException("Cannot find class: " + name);

    }

    /**
     * 构造类加载器数组
     *
     * @param classLoader 类加载器
     */
    ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        return new ClassLoader[]{
                classLoader,
                defaultClassLoader,
                Thread.currentThread().getContextClassLoader(),
                getClass().getClassLoader(),
                systemClassLoader};
    }

}
