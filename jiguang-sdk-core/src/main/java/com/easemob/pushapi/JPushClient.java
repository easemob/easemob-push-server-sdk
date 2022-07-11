package com.easemob.pushapi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.easemob.push.common.ClientConfig;
import com.easemob.push.common.connection.HttpProxy;
import com.easemob.push.common.response.APIConnectionException;
import com.easemob.push.common.response.APIRequestException;
import com.easemob.push.common.response.BooleanResult;
import com.easemob.push.common.response.DefaultResult;
import com.easemob.pushapi.push.*;
import com.easemob.pushapi.device.*;
import com.easemob.pushapi.push.model.BatchPushResult;
import com.easemob.pushapi.push.model.Message;
import com.easemob.pushapi.push.model.Platform;
import com.easemob.pushapi.push.model.PushPayload;
import com.easemob.pushapi.push.model.SMS;
import com.easemob.pushapi.push.model.audience.Audience;
import com.easemob.pushapi.push.model.notification.IosAlert;
import com.easemob.pushapi.push.model.notification.Notification;
import com.google.gson.JsonObject;

/**
 * The global entrance of JPush API library.
 */
public class JPushClient {
    private final PushClient _pushClient;
    private final DeviceClient _deviceClient;

    /**
     * Create a JPush Client.
     *
     * @param masterSecret API access secret of the appKey.
     * @param appKey       The KEY of one application on JPush.
     */
    public JPushClient(String masterSecret, String appKey) {
        _pushClient = new PushClient(masterSecret, appKey);
        _deviceClient = new DeviceClient(masterSecret, appKey);
    }

    /**
     * Create a JPush Client by custom Client configuration.
     *
     * @param masterSecret API access secret of the appKey.
     * @param appKey       The KEY of one application on JPush.
     * @param proxy        The proxy, if there is no proxy, should be null.
     * @param conf         The client configuration. Can use ClientConfig.getInstance() as default.
     */
    public JPushClient(String masterSecret, String appKey, HttpProxy proxy, ClientConfig conf) {
        _pushClient = new PushClient(masterSecret, appKey, proxy, conf);
        _deviceClient = new DeviceClient(masterSecret, appKey, proxy, conf);
    }

    /**
     * This will be removed in the future. Please use ClientConfig{jiguang-common com.easemob.push.common.ClientConfig#setMaxRetryTimes} instead of this constructor.
     *
     * @param masterSecret  API access secret of the appKey.
     * @param appKey        The KEY of one application on JPush.
     * @param maxRetryTimes The max retry times.
     */
    @Deprecated
    public JPushClient(String masterSecret, String appKey, int maxRetryTimes) {
        _pushClient = new PushClient(masterSecret, appKey, maxRetryTimes);
        _deviceClient = new DeviceClient(masterSecret, appKey, maxRetryTimes);
    }

    /**
     * This will be removed in the future. Please use ClientConfig{jiguang-common com.easemob.push.common.ClientConfig#setMaxRetryTimes} instead of this constructor.
     *
     * @param masterSecret  API access secret of the appKey.
     * @param appKey        The KEY of one application on JPush.
     * @param maxRetryTimes The max retry times.
     * @param proxy         The proxy, if there is no proxy, should be null.
     */
    @Deprecated
    public JPushClient(String masterSecret, String appKey, int maxRetryTimes, HttpProxy proxy) {
        _pushClient = new PushClient(masterSecret, appKey, maxRetryTimes, proxy);
        _deviceClient = new DeviceClient(masterSecret, appKey, maxRetryTimes, proxy);
    }

    /**
     * Create a JPush Client by custom Client configuration.
     * <p>
     * If you are using JPush privacy cloud, maybe this constructor is what you needed.
     * This will be removed in the future. Please use ClientConfig{jiguang-common com.easemob.push.common.ClientConfig#setMaxRetryTimes} instead of this constructor.
     *
     * @param masterSecret  API access secret of the appKey.
     * @param appKey        The KEY of one application on JPush.
     * @param maxRetryTimes Client request retry times.
     * @param proxy         The proxy, if there is no proxy, should be null.
     * @param conf          The client configuration. Can use ClientConfig.getInstance() as default.
     */
    @Deprecated
    public JPushClient(String masterSecret, String appKey, int maxRetryTimes, HttpProxy proxy,
            ClientConfig conf) {
        conf.setMaxRetryTimes(maxRetryTimes);

        _pushClient = new PushClient(masterSecret, appKey, proxy, conf);
        _deviceClient = new DeviceClient(masterSecret, appKey, proxy, conf);
    }

    /**
     * Create a JPush Client by custom Client configuration with global settings.
     * <p>
     * If you are using JPush privacy cloud, and you want different settings from default globally,
     * maybe this constructor is what you needed.
     * This will be removed in the future. Please use ClientConfig{jiguang-common com.easemob.push.common.ClientConfig#setGlobalPushSetting} instead of this constructor.
     *
     * @param masterSecret   API access secret of the appKey.
     * @param appKey         The KEY of one application on JPush.
     * @param maxRetryTimes  Client request retry times.
     * @param proxy          The proxy, if there is no proxy, should be null.
     * @param conf           The client configuration. Can use ClientConfig.getInstance() as default.
     * @param apnsProduction Global APNs environment setting. It will override PushPayload Options.
     * @param timeToLive     Global time_to_live setting. It will override PushPayload Options.
     */
    @Deprecated
    public JPushClient(String masterSecret, String appKey, int maxRetryTimes, HttpProxy proxy,
            ClientConfig conf,
            boolean apnsProduction, long timeToLive) {
        conf.setMaxRetryTimes(maxRetryTimes);
        conf.setApnsProduction(apnsProduction);
        conf.setTimeToLive(timeToLive);
        _pushClient = new PushClient(masterSecret, appKey, proxy, conf);
        _deviceClient = new DeviceClient(masterSecret, appKey, proxy, conf);
    }

    /**
     * Create a JPush Client with global settings.
     * <p>
     * If you want different settings from default globally, this constructor is what you needed.
     * This will be removed in the future. Please use ClientConfig{jiguang-common com.easemob.push.common.ClientConfig#setGlobalPushSetting} instead of this constructor.
     *
     * @param masterSecret   API access secret of the appKey.
     * @param appKey         The KEY of one application on JPush.
     * @param apnsProduction Global APNs environment setting. It will override PushPayload Options.
     * @param timeToLive     Global time_to_live setting. It will override PushPayload Options.
     */
    @Deprecated
    public JPushClient(String masterSecret, String appKey, boolean apnsProduction,
            long timeToLive) {
        ClientConfig conf = ClientConfig.getInstance();
        conf.setApnsProduction(apnsProduction);
        conf.setTimeToLive(timeToLive);
        _pushClient = new PushClient(masterSecret, appKey);
        _deviceClient = new DeviceClient(masterSecret, appKey);
    }

    public PushClient getPushClient() {
        return _pushClient;
    }

    // ----------------------------- Push API

    /**
     * Send a push with PushPayload object.
     *
     * @param pushPayload payload object of a push.
     * @return PushResult The result object of a Push. Can be printed to a JSON.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendPush(PushPayload pushPayload)
            throws APIConnectionException, APIRequestException {
        return _pushClient.sendPush(pushPayload);
    }

    /**
     * Send a push with JSON string.
     * <p>
     * You can send a push JSON string directly with this method.
     * <p>
     * Attention: globally settings cannot be affect this type of Push.
     *
     * @param payloadString payload of a push.
     * @return PushResult. Can be printed to a JSON.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendPush(String payloadString)
            throws APIConnectionException, APIRequestException {
        return _pushClient.sendPush(payloadString);
    }

    /**
     * Send a file push with PushPayload object.
     *
     * @param pushPayload payload object of a push.
     * @return PushResult The result object of a Push. Can be printed to a JSON.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendFilePush(PushPayload pushPayload)
            throws APIConnectionException, APIRequestException {
        return _pushClient.sendFilePush(pushPayload);
    }

    /**
     * Validate a push action, but do NOT send it actually.
     *
     * @param payload payload of a push.
     * @return PushResult. Can be printed to a JSON.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendPushValidate(PushPayload payload)
            throws APIConnectionException, APIRequestException {
        return _pushClient.sendPushValidate(payload);
    }

    public PushResult sendPushValidate(String payloadString)
            throws APIConnectionException, APIRequestException {
        return _pushClient.sendPushValidate(payloadString);
    }

    public BatchPushResult batchSendPushByRegId(List<PushPayload> pushPayloadList)
            throws APIConnectionException, APIRequestException {
        return _pushClient.batchSendPushByRegId(pushPayloadList);
    }

    public BatchPushResult batchSendPushByAlias(List<PushPayload> pushPayloadList)
            throws APIConnectionException, APIRequestException {
        return _pushClient.batchSendPushByAlias(pushPayloadList);
    }

    /**
     * Get cid list, the data form of cid is appKey-uuid.
     *
     * @param count the count of cid list, from 1 to 1000. default is 1.
     * @param type  default is push, option: schedule
     * @return CIDResult, an array of cid
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public CIDResult getCidList(int count, String type)
            throws APIConnectionException, APIRequestException {
        return _pushClient.getCidList(count, type);
    }

    // ------------------------------ Shortcuts - notification

    public PushResult sendNotificationAll(String alert)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.alertAll(alert);
        return _pushClient.sendPush(payload);
    }

    /**
     * Send a notification to all.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param alert The notification content.
     * @param sms   The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @return push result
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendNotificationAll(String alert, SMS sms)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.alertAll(alert, sms);
        return _pushClient.sendPush(payload);
    }

    public PushResult sendAndroidNotificationWithAlias(String title, String alert,
            Map<String, String> extras, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.android(alert, title, extras))
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send a notification to Android with alias.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param title  The notification title.
     * @param alert  The notification content.
     * @param sms    The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param extras The extra parameter.
     * @param alias  The users' alias.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendAndroidNotificationWithAlias(String title, String alert, SMS sms,
            Map<String, String> extras, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.android(alert, title, extras))
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    public PushResult sendAndroidNotificationWithRegistrationID(String title, String alert,
            Map<String, String> extras, String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.registrationId(registrationID))
                .setNotification(Notification.android(alert, title, extras))
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send a notification to Android with RegistrationID.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param title          The notification title.
     * @param alert          The notification content.
     * @param sms            The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param extras         The extra parameter.
     * @param registrationID The registration id generated by JPush.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendAndroidNotificationWithRegistrationID(String title, String alert, SMS sms,
            Map<String, String> extras, String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.registrationId(registrationID))
                .setNotification(Notification.android(alert, title, extras))
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    public PushResult sendIosNotificationWithAlias(String alert,
            Map<String, String> extras, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.ios(alert, extras))
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send a notification to iOS with alias.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param alert  The notification content.
     * @param sms    The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param extras The extra parameter.
     * @param alias  The users' alias.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithAlias(String alert, SMS sms,
            Map<String, String> extras, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.ios(alert, extras))
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS notification with alias.
     * If you want to send alert as a Json object, maybe this method is what you needed.
     *
     * @param alert  The wrapper of APNs alert.
     * @param extras The extra params.
     * @param alias  The alias list.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithAlias(IosAlert alert,
            Map<String, String> extras, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.ios(alert, extras))
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS notification with alias.
     * If you want to send alert as a Json object, maybe this method is what you needed.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param alert  The wrapper of APNs alert.
     * @param sms    The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param extras The extra params.
     * @param alias  The alias list.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithAlias(IosAlert alert, SMS sms,
            Map<String, String> extras, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.ios(alert, extras))
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS notification with alias.
     * If you want to send alert as a Json object, maybe this method is what you needed.
     *
     * @param alert  The JSON object of APNs alert.
     * @param extras The extra params.
     * @param alias  The alias list.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithAlias(JsonObject alert,
            Map<String, String> extras, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.ios(alert, extras))
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS notification with alias.
     * If you want to send alert as a Json object, maybe this method is what you needed.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param alert  The JSON object of APNs alert.
     * @param sms    The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param extras The extra params.
     * @param alias  The alias list.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithAlias(JsonObject alert, SMS sms,
            Map<String, String> extras, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.ios(alert, extras))
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    public PushResult sendIosNotificationWithRegistrationID(String alert,
            Map<String, String> extras, String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.registrationId(registrationID))
                .setNotification(Notification.ios(alert, extras))
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS notification with registrationIds.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param alert          The notification content.
     * @param sms            The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param extras         The extra params.
     * @param registrationID The alias list.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithRegistrationID(String alert, SMS sms,
            Map<String, String> extras, String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.registrationId(registrationID))
                .setNotification(Notification.ios(alert, extras))
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS notification with registrationIds.
     * If you want to send alert as a Json object, maybe this method is what you needed.
     *
     * @param alert          The wrapper of APNs alert.
     * @param extras         The extra params.
     * @param registrationID The registration ids.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithRegistrationID(IosAlert alert,
            Map<String, String> extras, String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.registrationId(registrationID))
                .setNotification(Notification.ios(alert, extras))
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS notification with registrationIds.
     * If you want to send alert as a Json object, maybe this method is what you needed.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param alert          The wrapper of APNs alert.
     * @param sms            The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param extras         The extra params.
     * @param registrationID The registration ids.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithRegistrationID(IosAlert alert, SMS sms,
            Map<String, String> extras, String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.registrationId(registrationID))
                .setNotification(Notification.ios(alert, extras))
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS notification with registrationIds.
     * If you want to send alert as a Json object, maybe this method is what you needed.
     *
     * @param alert          The wrapper of APNs alert.
     * @param extras         The extra params.
     * @param registrationID The registration ids.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithRegistrationID(JsonObject alert,
            Map<String, String> extras, String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.registrationId(registrationID))
                .setNotification(Notification.ios(alert, extras))
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS notification with registrationIds.
     * If you want to send alert as a Json object, maybe this method is what you needed.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param alert          The JSON object of APNs alert.
     * @param sms            The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param extras         The extra params.
     * @param registrationID The registration ids.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosNotificationWithRegistrationID(JsonObject alert, SMS sms,
            Map<String, String> extras, String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.registrationId(registrationID))
                .setNotification(Notification.ios(alert, extras))
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    // ---------------------- shortcuts - message

    public PushResult sendMessageAll(String msgContent)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.messageAll(msgContent);
        return _pushClient.sendPush(payload);
    }

    /**
     * Send a message to all
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param msgContent The message content.
     * @param sms        The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendMessageAll(String msgContent, SMS sms)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.messageAll(msgContent, sms);
        return _pushClient.sendPush(payload);
    }

    public PushResult sendAndroidMessageWithAlias(String title, String msgContent, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.alias(alias))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an Android message with alias.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param title      The message title.
     * @param msgContent The message content.
     * @param sms        The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param alias      The alias list.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendAndroidMessageWithAlias(String title, String msgContent, SMS sms,
            String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.alias(alias))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    public PushResult sendAndroidMessageWithRegistrationID(String title, String msgContent,
            String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.registrationId(registrationID))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an Android message with registration id.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param title          The message title.
     * @param msgContent     The message content.
     * @param sms            The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param registrationID The registration id list.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendAndroidMessageWithRegistrationID(String title, String msgContent, SMS sms,
            String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.registrationId(registrationID))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    public PushResult sendIosMessageWithAlias(String title, String msgContent, String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS message with alias.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param title      The message title.
     * @param msgContent The message content.
     * @param sms        The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param alias      The alias list.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosMessageWithAlias(String title, String msgContent, SMS sms,
            String... alias)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    public PushResult sendIosMessageWithRegistrationID(String title, String msgContent,
            String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.registrationId(registrationID))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send an iOS message with registration id.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param title          The message title.
     * @param msgContent     The message content.
     * @param sms            The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param registrationID The registrationIds generated by JPush.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendIosMessageWithRegistrationID(String title, String msgContent, SMS sms,
            String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.registrationId(registrationID))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    public PushResult sendMessageWithRegistrationID(String title, String msgContent,
            String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(registrationID))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Send a message with registrationIds.
     * If it doesn't received within the delay time,JPush will send a SMS to the corresponding users.
     *
     * @param title          The message title.
     * @param msgContent     The message content.
     * @param sms            The SMS content and delay time. If null, sms doesn't work, no effect on Push feature.
     * @param registrationID The registrationIds generated by JPush.
     * @return push result.
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs.
     */
    public PushResult sendMessageWithRegistrationID(String title, String msgContent, SMS sms,
            String... registrationID)
            throws APIConnectionException, APIRequestException {
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(registrationID))
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(msgContent)
                        .build())
                .setSMS(sms)
                .build();
        return _pushClient.sendPush(payload);
    }

    /**
     * Delete a push by msgId.
     *
     * @param msgId The message id
     * @return delete result
     * @throws APIConnectionException if a remote or network exception occurs.
     * @throws APIRequestException    if a request exception occurs
     */
    public DefaultResult deletePush(String msgId)
            throws APIConnectionException, APIRequestException {
        return _pushClient.deletePush(msgId);
    }

    // ----------------------- Device

    public TagAliasResult getDeviceTagAlias(String registrationId)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.getDeviceTagAlias(registrationId);
    }

    public DefaultResult updateDeviceTagAlias(String registrationId, boolean clearAlias,
            boolean clearTag)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.updateDeviceTagAlias(registrationId, clearAlias, clearTag);
    }

    public DefaultResult updateDeviceTagAlias(String registrationId, String alias,
            Set<String> tagsToAdd, Set<String> tagsToRemove)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.updateDeviceTagAlias(registrationId, alias, tagsToAdd, tagsToRemove);
    }

    public TagListResult getTagList()
            throws APIConnectionException, APIRequestException {
        return _deviceClient.getTagList();
    }

    public BooleanResult isDeviceInTag(String theTag, String registrationID)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.isDeviceInTag(theTag, registrationID);
    }

    public DefaultResult addRemoveDevicesFromTag(String theTag,
            Set<String> toAddUsers, Set<String> toRemoveUsers)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.addRemoveDevicesFromTag(theTag, toAddUsers,
                toRemoveUsers);
    }

    public DefaultResult deleteTag(String theTag, String platform)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.deleteTag(theTag, platform);
    }

    public AliasDeviceListResult getAliasDeviceList(String alias,
            String platform) throws APIConnectionException, APIRequestException {
        return _deviceClient.getAliasDeviceList(alias, platform);
    }

    public DefaultResult deleteAlias(String alias, String platform)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.deleteAlias(alias, platform);
    }

    public DefaultResult removeDevicesFromAlias(String alias, Set<String> toRemoveDevice)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.removeDevicesFromAlias(alias, toRemoveDevice);
    }

    public Map<String, OnlineStatus> getUserOnlineStatus(String... registrationIds)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.getUserOnlineStatus(registrationIds);
    }

    public DefaultResult bindMobile(String registrationId, String mobile)
            throws APIConnectionException, APIRequestException {
        return _deviceClient.bindMobile(registrationId, mobile);
    }

    public void close() {
        _pushClient.close();
    }

}

