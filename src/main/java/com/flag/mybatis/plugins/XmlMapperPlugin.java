package com.flag.mybatis.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.flag.util.FileUtils;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellRunner;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;

/**
 * @author sven.zhang
 */
public class XmlMapperPlugin extends PluginAdapter {

    private static String XMLFILE_POSTFIX = "Ext";

    private static String JAVAFILE_POTFIX = "Ext";

    private static String SQLMAP_COMMON_POTFIX = "and is_deleted = 'n'";

    // 添删改Document的sql语句及属性
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement parentElement = document.getRootElement();

        updateDocumentNameSpace(introspectedTable, parentElement);

        moveDocumentInsertSql(parentElement);

        updateDocumentInsertSelective(parentElement);

        moveDocumentUpdateByPrimaryKeySql(parentElement);

        generateMysqlPageSql(parentElement, introspectedTable);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private void generateMysqlPageSql(XmlElement parentElement, IntrospectedTable introspectedTable) {
        // mysql分页语句前半部分
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        XmlElement paginationPrefixElement = new XmlElement("sql");
        context.getCommentGenerator().addComment(paginationPrefixElement);
        paginationPrefixElement.addAttribute(new Attribute("id", "MysqlDialectPrefix"));
        XmlElement pageStart = new XmlElement("if");
        pageStart.addAttribute(new Attribute("test", "page != null"));
        pageStart.addElement(new TextElement("from " + tableName + " where id in ( select id from ( select id "));
        paginationPrefixElement.addElement(pageStart);
        parentElement.addElement(paginationPrefixElement);

        // mysql分页语句后半部分
        XmlElement paginationSuffixElement = new XmlElement("sql");
        context.getCommentGenerator().addComment(paginationSuffixElement);
        paginationSuffixElement.addAttribute(new Attribute("id", "MysqlDialectSuffix"));
        XmlElement pageEnd = new XmlElement("if");
        pageEnd.addAttribute(new Attribute("test", "page != null"));
        pageEnd.addElement(new TextElement("<![CDATA[ limit #{page.begin}, #{page.length} ) as temp_page_table) ]]>"));
        paginationSuffixElement.addElement(pageEnd);
        parentElement.addElement(paginationSuffixElement);
    }

    private void moveDocumentUpdateByPrimaryKeySql(XmlElement parentElement) {
        XmlElement updateElement = null;
        for (Element element : parentElement.getElements()) {
            XmlElement xmlElement = (XmlElement)element;
            if (xmlElement.getName().equals("update")) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if (attribute.getValue().equals("updateByPrimaryKey")) {
                        updateElement = xmlElement;
                        break;
                    }
                }

            }

        }
        parentElement.getElements().remove(updateElement);
    }

    private void updateDocumentInsertSelective(XmlElement parentElement) {
        XmlElement oldElement = null;
        XmlElement newElement = null;
        for (Element element : parentElement.getElements()) {
            XmlElement xmlElement = (XmlElement)element;
            if (xmlElement.getName().equals("insert")) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if (attribute.getValue().equals("insertSelective")) {
                        oldElement = xmlElement;
                        newElement = xmlElement;
                        newElement.addAttribute(new Attribute("useGeneratedKeys", "true"));
                        newElement.addAttribute(new Attribute("keyProperty", "id"));
                        break;
                    }
                }
            }
        }
        parentElement.getElements().remove(oldElement);
        parentElement.getElements().add(newElement);
    }

    private void moveDocumentInsertSql(XmlElement parentElement) {
        XmlElement insertElement = null;
        for (Element element : parentElement.getElements()) {
            XmlElement xmlElement = (XmlElement)element;
            if (xmlElement.getName().equals("insert")) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if (attribute.getValue().equals("insert")) {
                        insertElement = xmlElement;
                        break;
                    }
                }
            }
        }
        parentElement.getElements().remove(insertElement);
    }

    private void updateDocumentNameSpace(IntrospectedTable introspectedTable, XmlElement parentElement) {
        Attribute namespaceAttribute = null;
        for (Attribute attribute : parentElement.getAttributes()) {
            if (attribute.getName().equals("namespace")) {
                namespaceAttribute = attribute;
            }
        }
        parentElement.getAttributes().remove(namespaceAttribute);
        parentElement.getAttributes().add(new Attribute("namespace", introspectedTable.getMyBatis3JavaMapperType()
            + JAVAFILE_POTFIX));
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        TextElement text = new TextElement(SQLMAP_COMMON_POTFIX);
        element.addElement(text);
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        List<Element> elements = element.getElements();
        XmlElement setItem = null;
        int modifierItemIndex = -1;
        int gmtModifiedItemIndex = -1;
        for (Element e : elements) {
            if (e instanceof XmlElement) {
                setItem = (XmlElement)e;
                for (int i = 0; i < setItem.getElements().size(); i++) {
                    XmlElement xmlElement = (XmlElement)setItem.getElements().get(i);
                    for (Attribute att : xmlElement.getAttributes()) {
                        if (att.getValue().equals("modifier != null")) {
                            modifierItemIndex = i;
                            break;
                        }

                        if (att.getValue().equals("gmtModified != null")) {
                            gmtModifiedItemIndex = i;
                            break;
                        }
                    }
                }
            }
        }

        if (modifierItemIndex != -1 && setItem != null) {
            addXmlElementModifier(setItem, modifierItemIndex);
        }

        if (gmtModifiedItemIndex != -1 && setItem != null) {
            addGmtModifiedXmlElement(setItem, gmtModifiedItemIndex);
        }

        TextElement text = new TextElement(SQLMAP_COMMON_POTFIX);
        element.addElement(text);
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    private void addGmtModifiedXmlElement(XmlElement setItem, int gmtModifiedItemIndex) {
        XmlElement defaultGmtModified = new XmlElement("if");
        defaultGmtModified.addAttribute(new Attribute("test", "gmtModified == null"));
        defaultGmtModified.addElement(new TextElement("GMT_MODIFIED = current_timestamp,"));
        setItem.getElements().add(gmtModifiedItemIndex + 1, defaultGmtModified);
    }

    private void addXmlElementModifier(XmlElement setItem, int modifierItemIndex) {
        XmlElement defaultmodifier = new XmlElement("if");
        defaultmodifier.addAttribute(new Attribute("test", "modifier == null"));
        defaultmodifier.addElement(new TextElement("MODIFIER = 'system',"));
        setItem.getElements().add(modifierItemIndex + 1, defaultmodifier);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element,
                                                                        IntrospectedTable introspectedTable) {
        TextElement text = new TextElement(SQLMAP_COMMON_POTFIX);
        element.addElement(text);
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.setName("update");
        int replaceInd = -1;
        for (int i = 0; i < element.getAttributes().size(); i++) {
            Attribute attr = element.getAttributes().get(i);
            if (attr.getName().equals("parameterType")) {
                replaceInd = i;
                break;
            }
        }
        if (replaceInd >= 0) {
            element.getAttributes().remove(replaceInd);
            element.getAttributes().add(replaceInd,
                new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        }
        List<Element> removeElement = new ArrayList<Element>();
        for (int i = 5; i < element.getElements().size(); i++) {
            removeElement.add(element.getElements().get(i));

        }
        element.getElements().removeAll(removeElement);
        element.getElements().add(new TextElement("update "
            + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()
            + " set is_deleted = 'y',modifier=#{modifier,jdbcType=VARCHAR},gmt_Modified=current_timestamp where ID = "
            + "#{id,jdbcType=BIGINT}"));
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    // insert
    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Element> elements = element.getElements();
        XmlElement fieldItem = null;
        XmlElement valueItem = null;
        for (Element e : elements) {
            if (e instanceof XmlElement) {
                XmlElement xmlElement = (XmlElement)e;
                if (xmlElement.getName().equals("trim")) {
                    for (Attribute arr : xmlElement.getAttributes()) {
                        if (arr.getValue().equals("(")) {
                            fieldItem = xmlElement;
                            break;
                        }
                        if (arr.getValue().equals("values (")) {
                            valueItem = xmlElement;
                            break;
                        }
                    }
                }
            }
        }

        if (fieldItem != null) {
            XmlElement defaultGmtCreate = new XmlElement("if");
            defaultGmtCreate.addAttribute(new Attribute("test", "gmtCreate == null"));
            defaultGmtCreate.addElement(new TextElement("GMT_CREATE,"));
            fieldItem.addElement(1, defaultGmtCreate);

            XmlElement defaultGmtModified = new XmlElement("if");
            defaultGmtModified.addAttribute(new Attribute("test", "gmtModified == null"));
            defaultGmtModified.addElement(new TextElement("GMT_MODIFIED,"));
            fieldItem.addElement(1, defaultGmtModified);

            XmlElement defaultmodifier = new XmlElement("if");
            defaultmodifier.addAttribute(new Attribute("test", "modifier == null"));
            defaultmodifier.addElement(new TextElement("MODIFIER,"));
            fieldItem.addElement(1, defaultmodifier);

            XmlElement defaultCreator = new XmlElement("if");
            defaultCreator.addAttribute(new Attribute("test", "creator == null"));
            defaultCreator.addElement(new TextElement("CREATOR,"));
            fieldItem.addElement(1, defaultCreator);

            XmlElement defaultIsDeleted = new XmlElement("if");
            defaultIsDeleted.addAttribute(new Attribute("test", "isDeleted == null"));
            defaultIsDeleted.addElement(new TextElement("IS_DELETED,"));
            fieldItem.addElement(1, defaultIsDeleted);
        }

        if (valueItem != null) {
            XmlElement defaultGmtCreate = new XmlElement("if");
            defaultGmtCreate.addAttribute(new Attribute("test", "gmtCreate == null"));
            defaultGmtCreate.addElement(new TextElement("current_timestamp,"));
            valueItem.addElement(1, defaultGmtCreate);

            XmlElement defaultGmtModified = new XmlElement("if");
            defaultGmtModified.addAttribute(new Attribute("test", "gmtModified == null"));
            defaultGmtModified.addElement(new TextElement("current_timestamp,"));
            valueItem.addElement(1, defaultGmtModified);

            XmlElement defaultmodifier = new XmlElement("if");
            defaultmodifier.addAttribute(new Attribute("test", "modifier == null"));
            defaultmodifier.addElement(new TextElement("'system',"));
            valueItem.addElement(1, defaultmodifier);

            XmlElement defaultCreator = new XmlElement("if");
            defaultCreator.addAttribute(new Attribute("test", "creator == null"));
            defaultCreator.addElement(new TextElement("'system',"));
            valueItem.addElement(1, defaultCreator);

            XmlElement defaultIsDeleted = new XmlElement("if");
            defaultIsDeleted.addAttribute(new Attribute("test", "isDeleted == null"));
            defaultIsDeleted.addElement(new TextElement("'n',"));
            valueItem.addElement(1, defaultIsDeleted);
        }

        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
                                                           IntrospectedTable introspectedTable) {
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()),
            "record");
        method.getParameters().clear();
        method.addParameter(parameter);
        return super.clientDeleteByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        // 不再需要Mapper.java类，直接使用mapperExt.java
        return false;
    }

    // selectByExample
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        XmlElement lastXmlE = (XmlElement)element.getElements().remove(element.getElements().size() - 1);
        XmlElement pageStart = new XmlElement("include");
        pageStart.addAttribute(new Attribute("refid", "MysqlDialectPrefix"));
        element.getElements().add(8, pageStart);
        XmlElement isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test", "oredCriteria.size != 0"));
        XmlElement isdeletedChooseElement = new XmlElement("choose");
        XmlElement isdeletedWhenElement = new XmlElement("when");
        isdeletedWhenElement.addAttribute(new Attribute("test", "oredCriteria.size == 1 and !oredCriteria[0].valid"));
        isdeletedWhenElement.addElement(new TextElement("where is_deleted = 'n'"));
        isdeletedChooseElement.addElement(isdeletedWhenElement);
        XmlElement isdeletedOtherwiseElement = new XmlElement("otherwise");
        isdeletedOtherwiseElement.addElement(new TextElement(SQLMAP_COMMON_POTFIX));
        isdeletedChooseElement.addElement(isdeletedOtherwiseElement);
        isdeletedElement.addElement(isdeletedChooseElement);
        element.addElement(isdeletedElement);

        isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test", "oredCriteria.size == 0"));
        isdeletedElement.addElement(new TextElement("where is_deleted = 'n'"));
        element.addElement(isdeletedElement);

        XmlElement fullOrgPath = new XmlElement("include");
        fullOrgPath.addAttribute(new Attribute("refid", "fullOrgPath"));
        element.addElement(fullOrgPath);

        XmlElement owner = new XmlElement("include");
        owner.addAttribute(new Attribute("refid", "owner"));
        element.addElement(owner);

        element.addElement(lastXmlE);
        XmlElement isNotNullElement = new XmlElement("include"); //$NON-NLS-1$
        isNotNullElement.addAttribute(new Attribute("refid", "MysqlDialectSuffix"));
        element.getElements().add(isNotNullElement);

        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        // 添加is_deleted = 'n'
        XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
        isNotNullElement.addAttribute(new Attribute("test", "oredCriteria.size != 0")); //$NON-NLS-1$ //$NON-NLS-2$
        XmlElement isdeletedChooseElement = new XmlElement("choose");
        XmlElement isdeletedWhenElement = new XmlElement("when");
        isdeletedWhenElement.addAttribute(new Attribute("test", "oredCriteria.size == 1 and !oredCriteria[0].valid"));
        isdeletedWhenElement.addElement(new TextElement("where is_deleted = 'n'"));
        isdeletedChooseElement.addElement(isdeletedWhenElement);
        XmlElement isdeletedOtherwiseElement = new XmlElement("otherwise");
        isdeletedOtherwiseElement.addElement(new TextElement(SQLMAP_COMMON_POTFIX));
        isdeletedChooseElement.addElement(isdeletedOtherwiseElement);
        isNotNullElement.addElement(isdeletedChooseElement);
        element.addElement(isNotNullElement);

        isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
        isNotNullElement.addAttribute(new Attribute("test", "oredCriteria.size == 0")); //$NON-NLS-1$ //$NON-NLS-2$
        isNotNullElement.addElement(new TextElement("where is_deleted = 'n'"));
        element.addElement(isNotNullElement);
        XmlElement fullOrgPath = new XmlElement("include");
        fullOrgPath.addAttribute(new Attribute("refid", "fullOrgPath"));
        element.addElement(fullOrgPath);
        // 添加
        XmlElement owner = new XmlElement("include");
        owner.addAttribute(new Attribute("refid", "owner"));
        element.addElement(owner);
        return super.sqlMapCountByExampleElementGenerated(element, introspectedTable);
    }

    // 生成XXExt.xml
    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {

        String[] splitFile = introspectedTable.getMyBatis3XmlMapperFileName().split("\\.");
        String fileNameExt = null;
        if (splitFile[0] != null) {
            fileNameExt = splitFile[0] + XMLFILE_POSTFIX + ".xml";
        }

        if (FileUtils.isExistExtFile(context.getSqlMapGeneratorConfiguration().getTargetProject(),
            introspectedTable.getMyBatis3XmlMapperPackage(), fileNameExt)) {
            return super.contextGenerateAdditionalXmlFiles(introspectedTable);
        }

        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
            XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        XmlElement root = new XmlElement("mapper");
        document.setRootElement(root);

        String namespace = introspectedTable.getMyBatis3SqlMapNamespace() + XMLFILE_POSTFIX;
        root.addAttribute(new Attribute("namespace", namespace));
        root.addElement(createInsertBatch(introspectedTable));
        GeneratedXmlFile gxf = new GeneratedXmlFile(document, fileNameExt,
            introspectedTable.getMyBatis3XmlMapperPackage(),
            context.getSqlMapGeneratorConfiguration().getTargetProject(), false,
            context.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(1);
        answer.add(gxf);

        return answer;
    }

    /**
     * 创建批量插入xml方法
     *
     * @param introspectedTable
     * @return
     */
    private Element createInsertBatch(IntrospectedTable introspectedTable) {
        XmlElement insert = new XmlElement("insert");
        insert.addAttribute(new Attribute("id", "insertBatch"));
        insert.addAttribute(new Attribute("parameterType", "java.util.List"));
        insert.addElement(new TextElement(
            " INSERT INTO " + introspectedTable.getTableConfiguration().getTableName() + "("));
        StringBuilder builder = new StringBuilder();
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            builder.append(column.getActualColumnName() + ",");
        }
        XmlElement trimElement = addTrimElement();
        trimElement.addElement(new TextElement(builder.toString()));
        insert.addElement(trimElement);
        insert.addElement(new TextElement(")values"));
        addForeachElement(introspectedTable, insert);
        return insert;
    }

    private void addIfElement(XmlElement parent, IntrospectedColumn column) {
        String property = column.getJavaProperty();
        String type = column.getFullyQualifiedJavaType().getShortName();
        String text = column.getActualColumnName() + " = #{item." + property + "},";
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", property + " != null"));
        ifElement.addElement(new TextElement(text));
        parent.addElement(ifElement);
        if (Objects.equals(property, "id")) {
            return;
        }
        XmlElement element = new XmlElement("if");
        element.addAttribute(new Attribute("test", property + " == null"));
        String defalutValue;
        switch (type) {
            case "Integer":
                defalutValue = "0";
                break;
            case "Long":
                defalutValue = "0";
                break;
            case "BigDecimal":
                defalutValue = "0.00";
                break;
            case "Date":
                defalutValue = "now()";
                break;
            default:
                defalutValue = "\'\'";
        }
        element.addElement(new TextElement(column.getActualColumnName() + " = " + defalutValue));
        parent.addElement(element);
    }

    private XmlElement addTrimElement() {
        XmlElement element = new XmlElement("trim");
        element.addAttribute(new Attribute("suffix", ""));
        element.addAttribute(new Attribute("suffixOverrides", ","));
        return element;
    }

    /**
     * 添加foreach 元素
     *
     * @param introspectedTable
     * @param insertBatch
     */
    private void addForeachElement(IntrospectedTable introspectedTable, XmlElement insertBatch) {
        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "list"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("separator", ","));
        foreach.addElement(new TextElement("("));
        XmlElement trimElement = addTrimElement();
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            addIfElement(trimElement, column);
        }
        foreach.addElement(trimElement);
        foreach.addElement(new TextElement(")"));
        insertBatch.addElement(foreach);
    }

    /**
     * This plugin is always valid - no properties are required
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    public static void main(String[] args) {
        String config = XmlMapperPlugin.class.getClassLoader().getResource("generatorConfig.xml").getFile();
        String[] arg = {"-configfile", config};
        ShellRunner.main(arg);
    }
}
