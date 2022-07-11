package com.easemob.push.server.client;

import java.util.List;

import com.easemob.push.common.ClientConfig;
import com.easemob.push.common.ServiceHelper;
import com.easemob.push.common.connection.ApacheHttpClient;
import com.easemob.push.common.connection.HttpProxy;
import com.easemob.push.common.connection.IHttpClient;
import com.easemob.push.common.connection.NativeHttpClient;
import com.easemob.push.common.connection.NettyHttpClient;
import com.easemob.push.common.response.APIConnectionException;
import com.easemob.push.common.response.APIRequestException;
import com.easemob.push.common.response.BaseResult;
import com.easemob.push.common.response.DefaultResult;
import com.easemob.push.common.response.ResponseWrapper;
import com.easemob.push.common.utils.Preconditions;
import com.easemob.push.common.utils.StringUtils;
import com.easemob.push.server.model.EMToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * PushClient
 *
 * @author MaoChang Wu
 * @date 2022/07/06 15:20
 */
public class PushClient {
    private IHttpClient httpClient;
    private String baseUrl;
    private String pushPath;
    private String pushValidatePath;
    private String batchRegidPushPath;
    private String batchAliasPushPath;
    private String filePushPath;
    private EMToken token;

    /* If not present, true by default. */
    private int apnsProduction;

    /* If not present, the default value is 86400(s) (one day) */
    private long timeToLive;

    public PushClient() {
    }

    /**
     * Create a Push Client.
     *
     * @param masterSecret API access secret of the appKey.
     * @param appKey       The KEY of one application on Easemob Console.
     */
    public PushClient(String masterSecret, String appKey) {
        this(masterSecret, appKey, null, ClientConfig.getInstance());
    }

    public PushClient(String masterSecret, String appKey, ClientConfig clientConfig) {
        this(masterSecret, appKey, null, clientConfig);
    }


    public PushClient(String masterSecret, String appKey, HttpProxy proxy, ClientConfig conf) {
        ServiceHelper.checkBasic(appKey, masterSecret);

        this.baseUrl = (String) conf.get(ClientConfig.PUSH_HOST_NAME);
        this.pushPath = (String) conf.get(ClientConfig.PUSH_PATH);
        this.pushValidatePath = (String) conf.get(ClientConfig.PUSH_VALIDATE_PATH);
        this.filePushPath = (String) conf.get(ClientConfig.FILE_PUSH_PATH);

        this.batchAliasPushPath = (String) conf.get(ClientConfig.BATCH_ALIAS_PUSH_PATH);
        this.batchRegidPushPath = (String) conf.get(ClientConfig.BATCH_REGID_PUSH_PATH);

        this.apnsProduction = (Integer) conf.get(ClientConfig.APNS_PRODUCTION);
        this.timeToLive = (Long) conf.get(ClientConfig.TIME_TO_LIVE);

        String authCode = ServiceHelper.getBasicAuthorization(appKey, masterSecret);
        this.httpClient = new NativeHttpClient(authCode, proxy, conf);

    }

    public PushClient(String appKey, String clientId, String clientSecret, HttpProxy proxy, ClientConfig conf) {

        ServiceHelper.checkBasic(appKey, masterSecret);

        this.baseUrl = (String) conf.get(ClientConfig.PUSH_HOST_NAME);
        this.pushPath = (String) conf.get(ClientConfig.PUSH_PATH);
        this.pushValidatePath = (String) conf.get(ClientConfig.PUSH_VALIDATE_PATH);
        this.filePushPath = (String) conf.get(ClientConfig.FILE_PUSH_PATH);

        this.batchAliasPushPath = (String) conf.get(ClientConfig.BATCH_ALIAS_PUSH_PATH);
        this.batchRegidPushPath = (String) conf.get(ClientConfig.BATCH_REGID_PUSH_PATH);

        this.apnsProduction = (Integer) conf.get(ClientConfig.APNS_PRODUCTION);
        this.timeToLive = (Long) conf.get(ClientConfig.TIME_TO_LIVE);

        String authCode = ServiceHelper.getBasicAuthorization(appKey, masterSecret);
        this.httpClient = new NativeHttpClient(authCode, proxy, conf);

    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public PushResult sendPush(PushPayload pushPayload)
            throws APIConnectionException, APIRequestException {
        checkPushPayload(pushPayload);

        ResponseWrapper response =
                httpClient.sendPost(baseUrl + pushPath, getEncryptData(pushPayload));

        return BaseResult.fromResponse(response, PushResult.class);
    }

    public PushResult sendPushValidate(PushPayload pushPayload)
            throws APIConnectionException, APIRequestException {
        checkPushPayload(pushPayload);

        ResponseWrapper response =
                httpClient.sendPost(baseUrl + pushValidatePath, getEncryptData(pushPayload));

        return BaseResult.fromResponse(response, PushResult.class);
    }

    public PushResult sendPush(String payloadString)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(payloadString),
                "pushPayload should not be empty");

        try {
            jsonParser.parse(payloadString);
        } catch (JsonParseException e) {
            Preconditions.checkArgument(false, "payloadString should be a valid JSON string.");
        }

        ResponseWrapper response =
                httpClient.sendPost(baseUrl + pushPath, getEncryptData(payloadString));

        return BaseResult.fromResponse(response, PushResult.class);
    }

    public PushResult sendFilePush(PushPayload pushPayload)
            throws APIConnectionException, APIRequestException {
        checkPushPayload(pushPayload);

        ResponseWrapper response =
                httpClient.sendPost(baseUrl + filePushPath, getEncryptData(pushPayload));

        return BaseResult.fromResponse(response, PushResult.class);
    }

    public PushResult sendPushValidate(String payloadString)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(payloadString),
                "pushPayload should not be empty");

        try {
            jsonParser.parse(payloadString);
        } catch (JsonParseException e) {
            Preconditions.checkArgument(false, "payloadString should be a valid JSON string.");
        }

        ResponseWrapper response =
                httpClient.sendPost(baseUrl + pushValidatePath, getEncryptData(payloadString));

        return BaseResult.fromResponse(response, PushResult.class);
    }

    public BatchPushResult batchSendPushByRegId(List<PushPayload> pushPayloadList)
            throws APIConnectionException, APIRequestException {
        return batchSendPush(baseUrl + batchRegidPushPath, pushPayloadList);
    }

    public BatchPushResult batchSendPushByAlias(List<PushPayload> pushPayloadList)
            throws APIConnectionException, APIRequestException {
        return batchSendPush(baseUrl + batchAliasPushPath, pushPayloadList);
    }

    public BatchPushResult batchSendPush(String url, List<PushPayload> pushPayloadList)
            throws APIConnectionException, APIRequestException {

        Preconditions.checkArgument((null != pushPayloadList), "param should not be null");
        Preconditions
                .checkArgument((!pushPayloadList.isEmpty()), "pushPayloadList should not be empty");

        Gson gson = new Gson();

        JsonObject contentJson = new JsonObject();

        CIDResult cidResult = getCidList(pushPayloadList.size(), "push");
        int i = 0;
        JsonObject pushPayLoadList = new JsonObject();
        // setting cid
        for (PushPayload payload : pushPayloadList) {
            String cid = payload.getCid();
            if (cid != null && !cid.trim().isEmpty()) {
                payload.setCid(null);
            } else {
                cid = cidResult.cidlist.get(i++);
            }
            pushPayLoadList.add(cid, payload.toJSON());
        }
        contentJson.add("pushlist", pushPayLoadList);

        ResponseWrapper response =
                httpClient.sendPost(url, getEncryptData(gson.toJson(contentJson)));

        return BatchPushResult.fromResponse(response);

    }

    /**
     * Get cid list, the data form of cid is appKey-uuid.
     *
     * @param count the count of cid list, from 1 to 1000. default is 1.
     * @param type  default is "push", option: "schedule"
     * @return CIDResult, an array of cid
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public CIDResult getCidList(int count, String type)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(count >= 1 && count <= 1000,
                "count should not less than 1 or larger than 1000");
        Preconditions.checkArgument(type == null || type.equals("push") || type.equals("schedule"),
                "type should be \"push\" or \"schedule\"");
        ResponseWrapper responseWrapper;
        if (type != null) {
            responseWrapper = httpClient
                    .sendGet(baseUrl + pushPath + "/cid?count=" + count + "&type=" + type);
        } else {
            responseWrapper = httpClient.sendGet(baseUrl + pushPath + "/cid?count=" + count);
        }
        return BaseResult.fromResponse(responseWrapper, CIDResult.class);
    }

    /**
     * Delete a push by msgId.
     *
     * @param msgId The message id
     * @return delete result
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public DefaultResult deletePush(String msgId)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(msgId), "msgId should not be empty");

        ResponseWrapper responseWrapper =
                httpClient.sendDelete(baseUrl + pushPath + "/" + msgId);

        return DefaultResult.fromResponse(responseWrapper);
    }

    public void setHttpClient(IHttpClient client) {
        this.httpClient = client;
    }

    // 如果使用 NettyHttpClient，在发送请求后需要手动调用 close 方法
    public void close() {
        if (httpClient != null && httpClient instanceof NettyHttpClient) {
            ((NettyHttpClient) httpClient).close();
        } else if (httpClient != null && httpClient instanceof ApacheHttpClient) {
            ((ApacheHttpClient) httpClient).close();
        }
    }

    private void checkPushPayload(PushPayload pushPayload) {
        Preconditions.checkArgument(!(null == pushPayload), "pushPayload should not be null");

        if (apnsProduction > 0) {
            pushPayload.resetOptionsApnsProduction(true);
        } else if (apnsProduction == 0) {
            pushPayload.resetOptionsApnsProduction(false);
        }

        if (timeToLive >= 0) {
            pushPayload.resetOptionsTimeToLive(timeToLive);
        }
    }
}
