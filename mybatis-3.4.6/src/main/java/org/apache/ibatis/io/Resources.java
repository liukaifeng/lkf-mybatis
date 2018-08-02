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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 通过类加载器简化对资源的访问的类
 *
 * @author kaifeng
 * @author Clinton Begin
 */
public class Resources {

    /**
     * 多种类加载器的包装对象
     */
    private static ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

    /**
     * 字符集，null值默认使用系统字符集
     */
    private static Charset charset;

    Resources() {
    }

    /**
     * 获取默认类加载器，可能为null
     *
     * @return 默认类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        return classLoaderWrapper.defaultClassLoader;
    }

    /**
     * 设置默认类加载器
     *
     * @param defaultClassLoader - 新的默认类加载器
     */
    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        classLoaderWrapper.defaultClassLoader = defaultClassLoader;
    }

    /**
     * 返回指定资源的URL
     *
     * @param resource 需要查找的资源
     * @return 资源的url
     * @throws java.io.IOException 如果资源没有找到抛出异常
     */
    public static URL getResourceURL(String resource) throws IOException {
        // issue #625
        return getResourceURL(null, resource);
    }

    /**
     * 返回指定资源的URL
     *
     * @param loader   查找资源的类加载器
     * @param resource 需要查找的资源
     * @return 资源的URL
     * @throws java.io.IOException 如果资源没有找到抛出异常
     */
    public static URL getResourceURL(ClassLoader loader, String resource) throws IOException {
        URL url = classLoaderWrapper.getResourceAsURL(resource, loader);
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return url;
    }

    /**
     * 返回指定资源的流对象
     *
     * @param resource 指定查找的资源
     * @return 资源流对象
     * @throws java.io.IOException 如果资源没有找到抛出异常
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(null, resource);
    }

    /**
     * 返回指定资源的流对象
     *
     * @param loader   用于获取资源的类加载器
     * @param resource 需要获取的资源
     * @return 资源流对象
     * @throws java.io.IOException 如果资源没有找到抛出异常
     */
    public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
        InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    /**
     * 以Properties对象的格式返回资源
     *
     * @param resource 需要获取的资源
     * @return 资源的Properties对象格式
     * @throws java.io.IOException 如果资源没有找到抛出异常
     */
    public static Properties getResourceAsProperties(String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(resource);
        //将流对象转换为Properties对象
        props.load(in);
        in.close();
        return props;
    }

    /**
     * 以Properties对象的格式返回资源
     *
     * @param loader   获取资源的类加载器
     * @param resource 需要获取的资源
     * @return 资源的Properties对象格式
     * @throws java.io.IOException 如果资源没有找到抛出异常
     */
    public static Properties getResourceAsProperties(ClassLoader loader, String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(loader, resource);
        props.load(in);
        in.close();
        return props;
    }

    /**
     * 以Reader对象的格式返回资源
     *
     * @param resource 需要获取的资源
     * @return 资源的Reader对象格式
     * @throws java.io.IOException 如果资源没有找到抛出异常
     */
    public static Reader getResourceAsReader(String resource) throws IOException {
        Reader reader;
        //如果字符集为null,则使用系统字符集
        if (charset == null) {
            reader = new InputStreamReader(getResourceAsStream(resource));
        } else {
            reader = new InputStreamReader(getResourceAsStream(resource), charset);
        }
        return reader;
    }

    /**
     * 以Reader对象的格式返回资源
     *
     * @param loader   获取资源的类加载器
     * @param resource 需要获取的资源
     * @return 资源的Reader对象格式
     * @throws java.io.IOException 如果资源没有找到抛出异常
     */
    public static Reader getResourceAsReader(ClassLoader loader, String resource) throws IOException {
        Reader reader;
        if (charset == null) {
            reader = new InputStreamReader(getResourceAsStream(loader, resource));
        } else {
            reader = new InputStreamReader(getResourceAsStream(loader, resource), charset);
        }
        return reader;
    }

    /**
     * 以File对象的格式返回资源
     *
     * @param resource 需要被加载的资源
     * @return 返回资源
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static File getResourceAsFile(String resource) throws IOException {
        return new File(getResourceURL(resource).getFile());
    }

    /**
     * 返回指定classpath的资源并将它转换为 File 对象
     *
     * @param loader   - 加载资源的类加载器
     * @param resource - 需要被加载的资源
     * @return 返回 File 对象资源
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static File getResourceAsFile(ClassLoader loader, String resource) throws IOException {
        return new File(getResourceURL(loader, resource).getFile());
    }

    /**
     * 根据资源URL地址获取该资源的InputStream对象
     *
     * @param urlString - 资源URL地址
     * @return URL地址资源的InputStream对象
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static InputStream getUrlAsStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getInputStream();
    }

    /**
     * 根据资源URL地址获取该资源的Reader对象
     *
     * @param urlString - 资源的URL地址
     * @return URL地址资源的Reader对象
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static Reader getUrlAsReader(String urlString) throws IOException {
        Reader reader;
        if (charset == null) {
            reader = new InputStreamReader(getUrlAsStream(urlString));
        } else {
            reader = new InputStreamReader(getUrlAsStream(urlString), charset);
        }
        return reader;
    }

    /**
     * 根据资源URL地址获取该资源的 Properties 对象
     *
     * @param urlString - 资源的URL地址
     * @return URL地址资源转换成 Properties 对象
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static Properties getUrlAsProperties(String urlString) throws IOException {
        Properties props = new Properties();
        InputStream in = getUrlAsStream(urlString);
        props.load(in);
        in.close();
        return props;
    }

    /*
     * 加载一个类
     *
     * @param className - 类名
     * @return 加载的class对象
     * @throws ClassNotFoundException If the class cannot be found (duh!)
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return classLoaderWrapper.classForName(className);
    }

    /**
     * 获取字符集
     */
    public static Charset getCharset() {
        return charset;
    }

    /**
     * 设置字符集
     */
    public static void setCharset(Charset charset) {
        Resources.charset = charset;
    }

}
