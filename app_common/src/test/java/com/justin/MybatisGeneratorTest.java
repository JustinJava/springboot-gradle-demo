package com.justin;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc：mybatis-generator自动生成mapper.xml、entity、dao测试类
 * 
 * @author JustinQin
 *
 */
public class MybatisGeneratorTest {
	
	public static void main(String[] args) throws Exception {
		MybatisGeneratorTest generatorTest = new MybatisGeneratorTest();
		generatorTest.generator();
	}

	private void generator() throws Exception {
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		File configFile = new File("app_two/generatorConfig.xml");
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
	}
}
