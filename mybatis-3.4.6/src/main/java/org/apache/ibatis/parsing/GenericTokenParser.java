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
package org.apache.ibatis.parsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mybatis通用标记解析器，对xml中属性中的占位符进行解析
 *
 * @author Clinton Begin
 */
public class GenericTokenParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 开始标记符
     */
    private final String openToken;
    /**
     * 结束标记符
     */
    private final String closeToken;
    /**
     * 标记处理接口，具体的处理操作取决于它的实现方法
     */
    private final TokenHandler handler;

    /**
     * 构造函数
     */
    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    /**
     * 文本解析方法
     */
    public String parse(String text) {
        //文本空值判断
        if (text == null || text.isEmpty()) {
            return "";
        }
        // 获取开始标记符在文本中的位置
        int start = text.indexOf(openToken, 0);
        //位置索引值为-1，说明不存在该开始标记符
        if (start == -1) {
            return text;
        }
        //将文本转换成字符数组
        char[] src = text.toCharArray();
        //偏移量
        int offset = 0;
        //解析后的字符串
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            //判断开始标记符前边是否有转移字符，如果存在转义字符则移除转义字符
            if (start > 0 && src[start - 1] == '\\') {
                //移除转义字符
                builder.append(src, offset, start - offset - 1).append(openToken);
                //重新计算偏移量
                offset = start + openToken.length();
            } else {
                //开始查找结束标记符
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                //结束标记符的索引值
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    //同样判断标识符前是否有转义字符，有就移除
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        //重新计算偏移量
                        offset = end + closeToken.length();
                        //重新计算结束标识符的索引值
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }
                //没有找到结束标记符
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    //找到了一组标记符，对该标记符进行值替换
                    builder.append(handler.handleToken(expression.toString()));
                    offset = end + closeToken.length();
                }
            }
            //接着查找下一组标记符
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        logger.debug("[GenericTokenParser]-[parse]-待解析文本：{},解析结果：{}",text,builder.toString());
        return builder.toString();
    }
}
