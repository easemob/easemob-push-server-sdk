package com.easemob.pushapi.push;

import java.util.Map;

import com.easemob.push.common.ClientConfig;
import com.easemob.push.common.ServiceHelper;
import com.easemob.push.common.connection.HttpProxy;
import com.easemob.push.common.connection.IHttpClient;
import com.easemob.push.common.connection.NativeHttpClient;
import com.easemob.push.common.response.APIConnectionException;
import com.easemob.push.common.response.APIRequestException;
import com.easemob.push.common.response.ResponseWrapper;
import com.easemob.push.common.utils.Base64;
import com.easemob.push.common.utils.Preconditions;
import com.easemob.push.common.utils.sm2.SM2Util;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import com.easemob.pushapi.push.model.EncryptKeys;
import com.easemob.pushapi.push.model.EncryptPushPayload;
import com.easemob.pushapi.push.model.PushPayload;

/**
 * Created by caiyaoguan on 2017/7/4.
 */
public class GroupPushClient {

    private IHttpClient _httpClient;
    private String _baseUrl;
    private String _groupPushPath;
    private String _encryptType;
    private Gson gson = new Gson();
    private JsonParser _jsonParser = new JsonParser();

    public GroupPushClient(String groupMasterSecret, String groupKey) {
        this(groupMasterSecret, groupKey, null, ClientConfig.getInstance());
    }

    public GroupPushClient(String groupMasterSecret, String groupKey, HttpProxy proxy,
            ClientConfig conf) {
        ServiceHelper.checkBasic(groupKey, groupMasterSecret);
        this._baseUrl = (String) conf.get(ClientConfig.PUSH_HOST_NAME);
        this._groupPushPath = (String) conf.get(ClientConfig.GROUP_PUSH_PATH);
        this._encryptType = (String) conf.get(ClientConfig.ENCRYPT_TYPE);
        String authCode =
                ServiceHelper.getBasicAuthorization("group-" + groupKey, groupMasterSecret);
        this._httpClient = new NativeHttpClient(authCode, proxy, conf);
    }

    public GroupPushResult sendGroupPush(PushPayload pushPayload)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(!(null == pushPayload), "pushPayload should not be null");
        ResponseWrapper response =
                _httpClient.sendPost(_baseUrl + _groupPushPath, getEncryptData(pushPayload));
        // 2020-8-6 兼容分组推送新返回的group_msgid结构
        JsonObject responseJson = _jsonParser.parse(response.responseContent).getAsJsonObject();
        String groupMsgIdKey = "group_msgid";
        JsonElement _groupMsgId = responseJson.get(groupMsgIdKey);
        String groupMsgId = null != _groupMsgId ? _groupMsgId.getAsString() : "";
        responseJson.remove(groupMsgIdKey);
        Map<String, PushResult> result =
                gson.fromJson(responseJson, new TypeToken<Map<String, PushResult>>() {
                }.getType());
        return new GroupPushResult(groupMsgId, result);
    }

    /**
     * 获取加密的payload数据
     *
     * @param pushPayload
     * @return
     */
    private String getEncryptData(PushPayload pushPayload) {
        if (_encryptType.isEmpty()) {
            return pushPayload.toString();
        }
        if (EncryptKeys.ENCRYPT_SMS2_TYPE.equals(_encryptType)) {
            EncryptPushPayload encryptPushPayload = new EncryptPushPayload();
            try {
                encryptPushPayload.setPayload(String.valueOf(Base64.encode(
                        SM2Util.encrypt(pushPayload.toString(),
                                EncryptKeys.DEFAULT_SM2_ENCRYPT_KEY))));
            } catch (Exception e) {
                throw new RuntimeException("encrypt word exception", e);
            }
            encryptPushPayload.setAudience(pushPayload.getAudience());
            return encryptPushPayload.toString();
        }
        // 不支持的加密默认不加密
        return pushPayload.toString();
    }

}
