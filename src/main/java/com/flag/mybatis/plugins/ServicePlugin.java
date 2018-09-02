package com.flag.mybatis.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.flag.base.AbstractBaseServiceImpl;
import com.flag.base.BaseService;
import com.flag.base.DataResult;
import com.flag.util.FileUtils;
import com.flag.util.GeneratorUtil;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;

import static com.flag.util.GeneratorUtil.addImportedType;
import static com.flag.util.GeneratorUtil.createClassMethod;
import static com.flag.util.GeneratorUtil.createInterfaceMethod;

/**
 * @author sven.zhang
 */
public class ServicePlugin extends PluginAdapter {

    private static FullyQualifiedJavaType longType = new FullyQualifiedJavaType("java.lang.Long");

    public static String JAVAFILE_POTFIX = "Service";
    public static String JAVAFILE_IMPL_POTFIX = "Impl";

    /**
     * 生成XXBo.java和**BoImpl.java
     * <p>
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        // service路径
        String servicePath = this.properties.getProperty("basePath");
        String packagePath = introspectedTable.getTableConfiguration().getProperties().getProperty("servicePackage");
        String entityName = introspectedTable.getBaseRecordType();
        String exampleName = introspectedTable.getExampleType();
        String mapperName = introspectedTable.getMyBatis3JavaMapperType() + "Ext";
        String serviceImplName = GeneratorUtil.getShortName(entityName) + JAVAFILE_POTFIX;

        // 建立service接口
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(packagePath + "." + serviceImplName);
        GeneratedJavaFile serviceInterfaceFile = generateServiceInterfaceFile(serviceType, exampleName, entityName,
            servicePath);

        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>(2);
        generatedJavaFiles.add(serviceInterfaceFile);
        // 建立serviceImpl类
        FullyQualifiedJavaType implType = new FullyQualifiedJavaType(packagePath + ".impl." + serviceImplName
            + JAVAFILE_IMPL_POTFIX);
        TopLevelClass clazz = new TopLevelClass(implType);
        FullyQualifiedJavaType supperType = new FullyQualifiedJavaType(
            AbstractBaseServiceImpl.class.getSimpleName() + "<"
                + GeneratorUtil.getShortName(entityName) + "," + GeneratorUtil.getShortName(mapperName) + ","
                + GeneratorUtil.getShortName(exampleName)
                + ">");
        clazz.addAnnotation("@Service");
        clazz.addImportedType("org.springframework.stereotype.Service");
        clazz.addImportedType(exampleName);
        clazz.addImportedType(mapperName);
        clazz.addImportedType(entityName);
        clazz.addImportedType(AbstractBaseServiceImpl.class.getName());
        clazz.addImportedType(supperType);
        clazz.setSuperClass(supperType);
        clazz.addImportedType(serviceType);
        clazz.addSuperInterface(serviceType);
        clazz.setVisibility(JavaVisibility.PUBLIC);
        Set<FullyQualifiedJavaType> set = new HashSet();
        set.add(new FullyQualifiedJavaType(mapperName));
        set.add(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        clazz.addImportedTypes(set);
        Map<String, FullyQualifiedJavaType> params = new HashMap<>();
        params.put("mapper", new FullyQualifiedJavaType(mapperName));
        createClassMethod("setMapper", null, params, Arrays.asList(" @Autowired "), "setMapper(mapper);", clazz);

        GeneratedJavaFile implFile = new GeneratedJavaFile(clazz, servicePath,
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());
        generatedJavaFiles.add(implFile);
        return generatedJavaFiles;
    }

    /**
     * 生成 service 接口类
     *
     * @param serviceType
     * @param entityName
     * @param exampleName
     * @param basePath
     * @return
     */
    private GeneratedJavaFile generateServiceInterfaceFile(FullyQualifiedJavaType serviceType, String exampleName,
                                                           String entityName, String basePath) {
        // 为接口添加默认方法
        //addDefaultMethods(doName, exmpName, boInterfaze);

        Interface boInterfaze = new Interface(serviceType);
        boInterfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(boInterfaze);

        // 继承BaseService
        FullyQualifiedJavaType supperInterface = new FullyQualifiedJavaType(
            BaseService.class.getSimpleName() + "<" + GeneratorUtil.getShortName(entityName)
                + "," + GeneratorUtil.getShortName(exampleName) + ">");
        boInterfaze.addImportedType(new FullyQualifiedJavaType(exampleName));
        boInterfaze.addImportedType(new FullyQualifiedJavaType(entityName));
        boInterfaze.addImportedType(new FullyQualifiedJavaType(BaseService.class.getName()));
        boInterfaze.addSuperInterface(supperInterface);
        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(boInterfaze, basePath,
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());

        if (FileUtils.isExistExtFile(basePath, generatedJavaFile.getTargetPackage(), generatedJavaFile.getFileName())) {
            return null;
        }
        return generatedJavaFile;
    }

    /**
     * 添加默认方法
     * <p>
     * 包括：<br>
     * public int insert(Do dataObject);<br>
     * public int delete(Long id);<br>
     * public List<Do> selectByExample(Example example);<br>
     * public Do selectByPrimaryKey(Long id);<br>
     * public int update(Do dataObject);<br>
     * public DataResult<Do> getPageByExample(Example example);<br>
     * public boolean isValidDo(Do dataObject);<br>
     *
     * @param entityName
     * @param exampleName
     * @param interfaze
     */
    protected void addDefaultMethods(String entityName, String exampleName, Interface interfaze) {
        FullyQualifiedJavaType intType = FullyQualifiedJavaType.getIntInstance();
        FullyQualifiedJavaType doType = new FullyQualifiedJavaType(entityName);
        FullyQualifiedJavaType examType = new FullyQualifiedJavaType(exampleName);
        Set<FullyQualifiedJavaType> importTypes = new HashSet<>();
        addImportedType(List.class, importTypes);
        addImportedType(DataResult.class, importTypes);
        Map<String, FullyQualifiedJavaType> params = new HashMap<>(4);
        params.put("dataObject", doType);
        createInterfaceMethod("insert", intType, params, interfaze);
        createInterfaceMethod("update", intType, params, interfaze);

        params.clear();
        params.put("id", longType);
        GeneratorUtil.createInterfaceMethod("delete", intType, params, interfaze);
        createInterfaceMethod("selectByPrimaryKey", doType, params, interfaze);

        params.clear();
        params.put("example", examType);
        createInterfaceMethod("selectByExample", new FullyQualifiedJavaType("List<" + entityName + ">"), params,
            interfaze);
        createInterfaceMethod("getPageByExample",
            new FullyQualifiedJavaType(DataResult.class.getSimpleName() + "<" + entityName + ">"),
            params, interfaze);
    }

    /**
     * This plugin is always valid - no properties are required
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

}
