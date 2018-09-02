package com.flag;

import org.mybatis.generator.api.ShellRunner;

/**
 * @author sven.zhang
 * @since 2018/9/2
 */
public class PluginTest {
    public static void main(String[] args) {
        String config = PluginTest.class.getClassLoader().getResource("generatorConfig.xml").getFile();
        String[] arg = {"-configfile", config};
        ShellRunner.main(arg);
    }
}
