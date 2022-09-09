# Easemob PUSH Reactor SDK

## 依赖

如果你的项目使用 Maven 构建，在 pom.xml 中添加下面代码即可：

``` xml
<dependency>
    <groupId>com.easemob.im</groupId>
    <artifactId>push-sdk-reactor</artifactId>
    <version>${version}</version>
</dependency>
```

如果你的项目使用 Gradle 构建，可以在 build.grade 中添加下面代码：

``` gradle
implementation 'com.easemob.im:push-sdk-reactor:${version}'
```

## 使用

### 1. 使用 Easemob App Credentials 进行推送

``` java
        EMProperties emProperties = EMProperties.builder()
                .setProtocol(EMProperties.Protocol.HTTPS)
                .setCredentials(EasemobAppCredentials.of("YXA6jfsWQbb", "YXA6c9HDA"))
                .setAppKey("easemob-demo#easeim")
                .build();

        EMPushService emPushService = new EMPushService(emProperties);

        //推送特性配置内容，详见：https://docs-im.easemob.com/push/apppush/pushkv
        HashMap<String, Object> pushMesssage = new HashMap<String, Object>() {
            {
                put("title", "环信");
                put("content", "欢迎使用环信推送服务");
            }
        };
        
        EMPushHttpResponse block = emPushService.push().single(
                PushRequest.builder()
                        .setTargets(Collections.singletonList("username"))
                        .setPushMessage(pushMesssage)
                        .build()
        ).block();       
```
