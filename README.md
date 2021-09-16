**具体spring boot gradle快速上手及配置说明，可以参考我的这篇博客文章进行学习：**

[https://blog.csdn.net/JustinQin/article/details/120328896](https://blog.csdn.net/JustinQin/article/details/120328896)

**基于官方Springboot的Gradle改造demo工程，实现定制化多模块集中管理配置内容，主结构及说明如下：**<br/>
```javascript
springboot-gradle-demo
  ├─template
    ├─gradle
	  ├─v1
	    ├─build_java.gradle //bootjar、jar、war等构建方式定义
		├─buildscript.gradle //统一加载需要的插件
		├─functions.gradle //gralde定制化主函数配置
		├─junit.gradle //单元测试依赖的jdk版本、源码目录等
  ├─app_common
  ├─app_one
  ├─app_two
├─build.gradle //仓库定义、引用外部函数、插件以及依赖各个子模块定制化等
├─dependencies.gradle //根据名字可以看出主要用来进行依赖jar包用途
├─settings.gradle //主模块及子模块之间的include依赖
```

**核心配置文件build.gradle、dependencies.gradle、settings.gradle及template配置内容及用途说明如下：**<br/>
**（1）`settings.gradle`配置及说明：**<br/>
```javascript
//Gradle主工程名
rootProject.name = 'springboot-gradle-demo'

//Gradle子模块名
include 'app_common','app_one','app_two'
```

**（2）`build.gradle`配置及说明：**<br/>

```javascript
//==================定制化1：仓库、编译、插件、公共依赖start==================
buildscript {
	//全局声明变量
	ext {
		//maven公共仓库
		maven_public_repo_url="https://maven.aliyun.com/repository/public/"

		//gradle插件中心
		gradle_repo_url="https://plugins.gradle.org/m2/"

		//maven应用系统私有仓库
		//maven_private_dev_repo_url="https://maven.aliyun.com/repository/spring/"

		//DEV SNAPSHOTS依赖仓库
		//maven_snapshots_dev_repo_url="https://oss.sonatype.org/content/repositories/snapshots/"

		//插件目录(放在工程目录下，也可以单独存放到资源服务器，指定访问文件的具体url)
		template_dir="/template/gradle/v1"

		//编译目录（相对路径）
		output_dir="build"
	}

	//全局仓库: 自上而下寻找依赖
	repositories {
		//maven本地仓库: 优先加载
		mavenLocal()

		//maven仓库中心
		mavenCentral()

		//jcenter仓库中心
		//jcenter()和mavenCentral()是两个独立的仓库，两者毫无关系，jcenter()有的mavenCentral()可能没有，反之亦然。
		jcenter()

		//google仓库中心
		//google()

		//maven远程仓库: 阿里云或公司私服仓库
		maven {
			url = maven_public_repo_url
			url = gradle_repo_url
			//url = maven_private_dev_repo_url
			//url = maven_snapshots_dev_repo_url
		}
	}

	//全局公共依赖
	apply from: template_dir+'/buildscript.gradle',to: buildscript
}
//==================定制化1：仓库、编译、插件、公共依赖end==================

//Java插件: 未配置时api、implementation无法加载
plugins {
	id 'java'
	id 'java-library'
}

//==================定制化2：配置构建主入口、全局功能函数start==================
//自定义全局工具定制化: 自动构建主入口+配置全局功能函数
apply from: template_dir+'/functions.gradle'
//==================定制化2：配置构建主入口、全局功能函数end==================


//=======================定制化3：gradle子项目模块start=========================
//common project
project(':app_common'){
	apply from: '..'+template_dir+'/build_java.gradle'
	apply from: '..'+template_dir+'/junit.gradle'
}

//app_one project
project(':app_one'){
	apply from: '..'+template_dir+'/build_java.gradle'
	apply from: '..'+template_dir+'/junit.gradle'
}

//app_two project
project(':app_two'){
	apply from: '..'+template_dir+'/build_java.gradle'
	apply from: '..'+template_dir+'/junit.gradle'
}
//=======================定制化3：gradle子项目模块end=========================

//=======================定制化4：子项目定义依赖start=========================
apply from: 'dependencies.gradle'
//=======================定制化4：子项目定义依赖end=========================
//setSysSnapshotsRepo
//待扩展...


```

**（3）`dependencies.gradle`配置及说明：**<br/>

```javascript
//========================Gradle工程子模块依赖jar包及版本控制=========================
//===========参数说明start=========
//api: 依赖暴露，其他模块都可见
//implementation: 表示依赖屏蔽，仅自己模块内可见
//===========参数说明end===========



//公共子模块依赖定义：app_common
project(':app_common'){//app_common公共项目子模块: 作为jar工程供其他子模块引用，其他无须重复定义相关jar包
	dependencies{
		//依赖仓库jar包
		api 'org.springframework.boot:spring-boot:2.4.3'
		api 'org.springframework.boot:spring-boot-starter-web:2.4.3'
		api 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.1.3'
		api 'mysql:mysql-connector-java:5.1.47'

		//依赖第三方jar包:指定工程资源lib目录下所有依赖包
		api fileTree(dir:'src/main/resources/lib',includes: ['*.jar'])

		//引入 mybatis-generator 插件
		//apply plugin: "com.arenagod.gradle.MybatisGenerator"

		//测试依赖：仅供测试用，仅当前模块生效，不会加载到打包版本中，PS: 根据实际需要定义
		testImplementation 'junit:junit:4.11'
	}
}

//独立子模块依赖定义: app_one
project(':app_one'){
	dependencies{
		//模块依赖: 指定某个模块进行依赖
		implementation project(':app_common')

		//依赖第三方jar包：指定工程资源lib目录下所有依赖包
	    compile fileTree(dir:'src/main/resources/lib',includes: ['*.jar'])

		//依赖排除(包级别)：exclude的内容将不会被该jar包引用，PS: 根据实际需要定义
		testImplementation (('org.springframework:spring-test:5.3.3')) {
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
		}

		//依赖排除(模块级别)：exclude的内容将不会被该jar包引用，PS: 根据实际需要定义
		implementation (project(':app_common')) {
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
		}

		//测试依赖：仅供测试用，仅当前模块生效，不会加载到打包版本中，PS: 根据实际需要定义
		testImplementation 'junit:junit:4.11'
		testImplementation 'org.mybatis.generator:mybatis-generator-core:1.3.7'
	}

	//依赖排除(全局级别)：exclude的内容将不会被该jar包引用，PS: 当要排除的依赖范围很广时，可采用该方式
	configurations.all {
		exclude group: 'org.springframework', module: 'spring-core'
	}
}

//独立子模块依赖定义: app_two
project(':app_two'){
	dependencies{
		//模块依赖: 指定某个模块进行依赖
		implementation project(':app_common')

		//依赖第三方jar包：指定工程资源lib目录下所有依赖包
		compile fileTree(dir:'src/main/resources/lib',includes: ['*.jar'])

		//依赖排除(包级别)：exclude的内容将不会被该jar包引用，PS: 根据实际需要定义
		testImplementation (('org.springframework:spring-test:5.3.3')) {
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
		}

		//依赖排除(模块级别)：exclude的内容将不会被该jar包引用，PS: 根据实际需要定义
		implementation (project(':app_common')) {
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
			exclude group: 'org.springframework', module: 'mock'
		}

		//测试依赖：仅供测试用，仅当前模块生效，不会加载到打包版本中，PS: 根据实际需要定义
		testImplementation 'junit:junit:4.11'
		testImplementation 'org.mybatis.generator:mybatis-generator-core:1.3.7'
	}

}


```

**（4）template下的相关组件及函数**<br/>
**除了template/gradle/v1/`functions.gradle`这段内容中的包名`com.justin`需要改成自己的之外，其他可以可以不需要动**<br/>
```java
		//deploy package dir of the project (sub-dir of output-dir)
		setSysPrivateRepo = { sys_id ->
			allprojects{
				repositories{
					maven {
						url maven_private_dev_repo_url
						content { includeGroupByRegx "com\\.justin\\."+sys_id+".*" }
					}
				}
			}
		}
```
