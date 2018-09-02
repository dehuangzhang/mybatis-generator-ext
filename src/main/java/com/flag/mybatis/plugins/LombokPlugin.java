package com.flag.mybatis.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.flag.base.BaseEntity;
import com.flag.base.BaseExample;
import com.flag.base.BaseMapper;
import com.flag.util.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * @author sven.zhang
 * @since 2018/9/1
 */
public class LombokPlugin extends PluginAdapter {
    private static String MAPPER_SUFFIX = "Ext";
    private static List<String> DEFAULT_FIELD_NAMES = Arrays.asList("id", "is_deleted", "modifier", "creator",
        "gmtCreate", "gmtModied");

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Iterator<Field> it = topLevelClass.getFields().iterator();
        while (it.hasNext()) {
            if (DEFAULT_FIELD_NAMES.contains(it.next().getName())) {
                it.remove();
            }
        }
        topLevelClass.addImportedType(new FullyQualifiedJavaType(Getter.class.getName()));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(Setter.class.getName()));
        FullyQualifiedJavaType baseEntityType = new FullyQualifiedJavaType(BaseEntity.class.getName());
        topLevelClass.addAnnotation("@Getter");
        topLevelClass.addAnnotation("@Setter");
        // 基本属性
        topLevelClass.addImportedType(baseEntityType);
        topLevelClass.setSuperClass(baseEntityType);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        return false;
    }

    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        String baseExample = BaseExample.class.getName();
        topLevelClass.setSuperClass(baseExample);
        topLevelClass.addImportedType(baseExample);
        return true;
    }

    // 生成XXExt.java
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()
            + MAPPER_SUFFIX);
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(interfaze);

        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType("javax.annotation.Resource");
        interfaze.addAnnotation("@Resource");
        interfaze.addImportedType(annotation);

        String doName = introspectedTable.getBaseRecordType();
        String exmpName = introspectedTable.getExampleType();
        interfaze.addSuperInterface(new FullyQualifiedJavaType(
            BaseMapper.class.getSimpleName() + "<" + doName + ","
                + exmpName + ">"));
        interfaze.addImportedType(new FullyQualifiedJavaType(BaseMapper.class.getName()));

        CompilationUnit compilationUnits = interfaze;
        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(compilationUnits,
            context.getJavaModelGeneratorConfiguration().getTargetProject(),
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());

        if (FileUtils.isExistExtFile(generatedJavaFile.getTargetProject(), generatedJavaFile.getTargetPackage(),
            generatedJavaFile.getFileName())) {
            return null;
        }
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>(1);
        generatedJavaFile.getFileName();
        generatedJavaFiles.add(generatedJavaFile);
        return generatedJavaFiles;
    }

}
