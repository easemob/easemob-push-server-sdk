# Easemob PUSH SDK for Java

提供便捷的推送能力。

## 依赖

- Java 1.8
- [Reactor](https://projectreactor.io)(io.projectreactor:reactor-bom:2020.0.4)

## 安装

如果你的项目使用 Maven 构建，在 pom.xml 中添加下面代码即可：

``` xml
<dependency>
    <groupId>com.easemob.im</groupId>
    <artifactId>push-sdk-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

如果你的项目使用 Gradle 构建，可以在 build.grade 中添加下面代码：

``` gradle
implementation 'com.easemob.im:push-sdk-core:0.0.1-SNAPSHOT'
```

## 准备

### 获取环信 appKey, clientId, clientSecret

如果您有环信管理后台账号并创建过应用，请进入 [这里](https://console.easemob.com/user/login) 进行登录。
![图片](https://user-images.githubusercontent.com/15087647/114996679-a34cb980-9ed1-11eb-89ae-a22c1af7d69d.png)

如图点击查看后，可以看到自己的 appkey、Client ID、ClientSecret，用于 SDK 的初始化。

如果您没有环信管理后台账号，请进入 [这里](https://console.easemob.com/user/register) 进行注册账号，注册成功后请进行登录。
![图片](https://user-images.githubusercontent.com/15087647/114997381-59180800-9ed2-11eb-968a-a29406c78021.png)

如图先添加应用(也就是创建 appkey，自动生成 Client ID、ClientSecret)，添加成功后在应用列表中可以看到应用信息，点击查看可以看到自己的 appkey、Client ID、ClientSecret，用于 SDK 的初始化。

## 使用

[EMPushService]() 是所有 API 的入口，可以这样初始化：

### 1. 使用 Easemob App Credentials 进行设备推送
``` java
        EMProperties emProperties = EMProperties.builder()
                .setProtocol(EMProperties.Protocol.HTTPS)
                .setCredentials(EasemobAppCredentials.of("YXA6jfsWQbbYR","YXA6c9HDAfuiQN"))
                .setAppKey("easemob-demo#easeim")
                .build();

        EMPushService emPushService = new EMPushService(emProperties);
        
       
```
