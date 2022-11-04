# Easemob PUSH SDK for Java

为使用环信服务端推送提供便捷。

## 依赖

### Maven 

如果你的项目使用 Maven 构建，在 pom.xml 中添加下面代码即可：
[版本链接](https://search.maven.org/search?q=g:com.easemob.im%20AND%20a:push-sdk-reactor-api)
``` xml
<dependency>
    <groupId>com.easemob.im</groupId>
    <artifactId>push-sdk-reactor-api</artifactId>
    <version>${version}</version>
</dependency>
```

### Gradle

如果你的项目使用 Gradle 构建，可以在 build.grade 中添加下面代码：

``` gradle
implementation 'com.easemob.im:push-sdk-reactor-api:${version}'
```

## 使用

#### 1、初始化EmPushService

<font color="red">注意: 无特殊需求 EMPushService 上下文中可为单例</font>

``` java
        EmPushProperties emPushProperties=EmPushProperties.builder()
            .setProtocol(EmPushProperties.Protocol.HTTPS)
            .setCredentials(EasemobAppCredentials.of("client_id","client_secret"))
            .setAppKey("appkey")
            .build();

        EMPushService emPushService=new EMPushService(emPushProperties); 
```

#### 2、API 调用

``` java

        //推送特性配置内容，详见：https://docs-im.easemob.com/push/apppush/pushkv
        HashMap<String, Object> pushMesssage = new HashMap<String, Object>() {
            {
                put("title", "环信");
                put("content", "欢迎使用环信推送服务");
            }
        };
        
        EMPushHttpResponse eMPushHttpResponse = emPushService.push().single(
                PushRequest.builder()
                        .setTargets(Collections.singletonList("username"))
                        .setPushMessage(pushMesssage)
                        .build()
        ).block;       
```
