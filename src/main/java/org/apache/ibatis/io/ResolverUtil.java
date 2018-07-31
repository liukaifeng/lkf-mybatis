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
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * ResolverUtil可以根据指定的条件查找指定包下的类，其中使用的条件由Test接口表示。
 *
 * @author kaifeng
 * @author Tim Fennell
 */
public class ResolverUtil<T> {
    /**
     * 为当前类实例化一个日志对象
     */
    private static final Log log = LogFactory.getLog(ResolverUtil.class);

    /**
     * 一个简单的接口，指定如何检测类，以确定它们是否包含在ResolverUtil生成的结果中。
     */
    public interface Test {
        /**
         * 如果待检测的类符合条件，则返回True，否则返回false。
         *
         * @param type 待检测的类
         */
        boolean matches(Class<?> type);
    }

    /**
     * 用于检测类是否继承了指定的类或接口，
     */
    public static class IsA implements Test {
        private Class<?> parent;

        /**
         * 构造函数指定父类或实现的接口
         */
        public IsA(Class<?> parentType) {
            this.parent = parentType;
        }

        /**
         * 如果继承了构造函数中指定的父类则返回true
         */
        @Override
        public boolean matches(Class<?> type) {
            return type != null && parent.isAssignableFrom(type);
        }

        @Override
        public String toString() {
            return "is assignable to " + parent.getSimpleName();
        }
    }

    /**
     * 检测指定类是否添加了指定的注解
     */
    public static class AnnotatedWith implements Test {
        private Class<? extends Annotation> annotation;

        /**
         * 构造函数指定注解类型
         */
        public AnnotatedWith(Class<? extends Annotation> annotation) {
            this.annotation = annotation;
        }

        /**
         * 如果检测的类使用了构造函数中指定的注解，则返回true
         */
        @Override
        public boolean matches(Class<?> type) {
            return type != null && type.isAnnotationPresent(annotation);
        }

        @Override
        public String toString() {
            return "annotated with @" + annotation.getSimpleName();
        }
    }

    /**
     * The set of matches being accumulated.
     */
    private Set<Class<? extends T>> matches = new HashSet<Class<? extends T>>();

    /**
     * 类加载器，默认使用Thread.currentThread().getContextClassLoader() will be used.
     */
    private ClassLoader classloader;

    /**
     * 获取匹配的类集合
     *
     * @return 匹配的类集合
     */
    public Set<Class<? extends T>> getClasses() {
        return matches;
    }

    /**
     * 获取类加载器，默认使用当前线程绑定的类加载器
     *
     * @return 用来加载类的类加载器
     */
    public ClassLoader getClassLoader() {
        return classloader == null ? Thread.currentThread().getContextClassLoader() : classloader;
    }

    /**
     * 修改类加载器
     *
     * @param classloader 指定的类加载器
     */
    public void setClassLoader(ClassLoader classloader) {
        this.classloader = classloader;
    }

    /**
     * 查找指定包中所有继承了指定父类的类
     *
     * @param parent       父类或接口
     * @param packageNames 一个或多个包名
     */
    public ResolverUtil<T> findImplementations(Class<?> parent, String... packageNames) {
        if (packageNames == null) {
            return this;
        }

        Test test = new IsA(parent);
        for (String pkg : packageNames) {
            find(test, pkg);
        }

        return this;
    }

    /**
     * 查找指定包下所有使用指定注解的类
     *
     * @param annotation   指定的注解对象
     * @param packageNames 一个或多个包名
     */
    public ResolverUtil<T> findAnnotated(Class<? extends Annotation> annotation, String... packageNames) {
        if (packageNames == null) {
            return this;
        }

        Test test = new AnnotatedWith(annotation);
        for (String pkg : packageNames) {
            find(test, pkg);
        }

        return this;
    }

    /**
     * 扫描指定包及子包下的所有类，检测包下所有的类
     *
     * @param test        检测类的实例对象
     * @param packageName 包名 e.g. {@code net.sourceforge.stripes}
     */
    public ResolverUtil<T> find(Test test, String packageName) {
        //根据包名获取包的路径
        String path = getPackagePath(packageName);

        try {
            List<String> children = VFS.getInstance().list(path);
            for (String child : children) {
                if (child.endsWith(".class")) {
                    //检测类是否符合test条件
                    addIfMatching(test, child);
                }
            }
        } catch (IOException ioe) {
            log.error("Could not read package: " + packageName, ioe);
        }

        return this;
    }

    /**
     * 根据包名转换成包路径
     *
     * @param packageName 包名
     */
    protected String getPackagePath(String packageName) {
        return packageName == null ? null : packageName.replace('.', '/');
    }

    /**
     * 检测类是否符合指定的test条件
     *
     * @param test 检测条件
     * @param fqn  一个类的完整限定名，包括所在包的路径
     */
    @SuppressWarnings("unchecked")
    protected void addIfMatching(Test test, String fqn) {
        try {
            String externalName = fqn.substring(0, fqn.indexOf('.')).replace('/', '.');
            ClassLoader loader = getClassLoader();
            if (log.isDebugEnabled()) {
                log.debug("Checking to see if class " + externalName + " matches criteria [" + test + "]");
            }

            Class<?> type = loader.loadClass(externalName);
            if (test.matches(type)) {
                matches.add((Class<T>) type);
            }
        } catch (Throwable t) {
            log.warn("Could not examine class '" + fqn + "'" + " due to a " +
                    t.getClass().getName() + " with message: " + t.getMessage());
        }
    }
}