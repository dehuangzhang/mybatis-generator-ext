package com.flag.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;


/**
 * @author sven.zhang
 * @since 2018/9/2
 */
public class GeneratorUtil {
    public static String getShortName(String className) {
        if (className == null) {
            return className;
        }
        return className.substring(className.lastIndexOf(".") + 1);
    }

    /**
     * 在接口中创建方法
     * <p>
     *
     * @param methodName 方法名
     * @param returnType 返回类型
     * @param params     Map<参数名, 参数类型>
     * @param inter      接口
     */
    public static void createInterfaceMethod(String methodName, FullyQualifiedJavaType returnType,
                                             Map<String, FullyQualifiedJavaType> params,
                                             Interface inter) {
        Method method = new Method(methodName);
        if (returnType != null) {
            method.setReturnType(returnType);
        }
        addMethodParamters(method, params);
        inter.addMethod(method);

    }

    public static void addImportedType(Class clazz, Set<FullyQualifiedJavaType> types) {
        types.add(new FullyQualifiedJavaType(clazz.getName()));
    }

    /**
     * 在接口中创建一个方法
     * <p>
     * 只支持1个参数
     *
     * @param name        方法名
     * @param rsType      返回类型
     * @param params      Map<参数名, 参数类型>
     * @param annotations 注解 集合
     * @param body        方法体
     * @param clazz
     */
    public static void createClassMethod(String name, FullyQualifiedJavaType rsType,
                                         Map<String, FullyQualifiedJavaType> params,
                                         List<String> annotations, String body, TopLevelClass clazz) {
        Method method = new Method(name);
        method.setVisibility(JavaVisibility.PUBLIC);
        if (rsType != null) {
            method.setReturnType(rsType);
            clazz.addImportedType(rsType);
        }
        addMethodParamters(method, params);
        annotations.forEach(annotation -> method.addAnnotation(annotation));
        method.addBodyLine(body);
        clazz.addMethod(method);
    }

    private static void addMethodParamters(Method method, Map<String, FullyQualifiedJavaType> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        for (Entry<String, FullyQualifiedJavaType> entry : params.entrySet()) {
            Parameter param = new Parameter(entry.getValue(), entry.getKey());
            method.addParameter(param);
        }
    }

}
