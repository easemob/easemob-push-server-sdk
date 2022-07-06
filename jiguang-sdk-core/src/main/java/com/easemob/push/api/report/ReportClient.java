package com.easemob.push.api.report;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;

import com.easemob.push.common.ClientConfig;
import com.easemob.push.common.ServiceHelper;
import com.easemob.push.common.TimeUnit;
import com.easemob.push.common.connection.HttpProxy;
import com.easemob.push.common.connection.NativeHttpClient;
import com.easemob.push.common.response.APIConnectionException;
import com.easemob.push.common.response.APIRequestException;
import com.easemob.push.common.response.BaseResult;
import com.easemob.push.common.response.ResponseWrapper;
import com.easemob.push.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.easemob.push.api.report.model.CheckMessagePayload;

public class ReportClient {

    private final static Pattern MSGID_PATTERNS = Pattern.compile("[^0-9, ]");
    private final NativeHttpClient _httpClient;
    private String _hostName;
    private String _receivePath;
    private String _userPath;
    private String _messagePath;
    private String _statusPath;
    private String messageDetailPath;
    private String receiveDetailPath;
    private String groupMessageDetailPath;
    private String groupUserPath;

    public ReportClient(String masterSecret, String appKey) {
        this(masterSecret, appKey, null, ClientConfig.getInstance());
    }

    /**
     * This will be removed in the future. Please use ClientConfig{jiguang-common com.easemob.push.common.ClientConfig#setMaxRetryTimes} instead of this constructor.
     *
     * @param masterSecret  API access secret of the appKey.
     * @param appKey        The KEY of one application on JPush.
     * @param maxRetryTimes max retry times.
     */
    @Deprecated
    public ReportClient(String masterSecret, String appKey, int maxRetryTimes) {
        this(masterSecret, appKey, maxRetryTimes, null);
    }

    /**
     * This will be removed in the future. Please use ClientConfig{jiguang-common com.easemob.push.common.ClientConfig#setMaxRetryTimes} instead of this constructor.
     *
     * @param masterSecret  API access secret of the appKey.
     * @param appKey        The KEY of one application on JPush.
     * @param maxRetryTimes max retry times
     * @param proxy         The max retry times.
     */
    @Deprecated
    public ReportClient(String masterSecret, String appKey, int maxRetryTimes, HttpProxy proxy) {
        ServiceHelper.checkBasic(appKey, masterSecret);

        ClientConfig conf = ClientConfig.getInstance();
        conf.setMaxRetryTimes(maxRetryTimes);

        _hostName = (String) conf.get(ClientConfig.REPORT_HOST_NAME);
        _receivePath = (String) conf.get(ClientConfig.REPORT_RECEIVE_PATH);
        _userPath = (String) conf.get(ClientConfig.REPORT_USER_PATH);
        _messagePath = (String) conf.get(ClientConfig.REPORT_MESSAGE_PATH);
        _statusPath = (String) conf.get(ClientConfig.REPORT_STATUS_PATH);

        messageDetailPath = (String) conf.get(ClientConfig.REPORT_MESSAGE_DETAIL_PATH);
        receiveDetailPath = (String) conf.get(ClientConfig.REPORT_RECEIVE_DETAIL_PATH);
        groupMessageDetailPath = (String) conf.get(ClientConfig.REPORT_GROUP_MESSAGE_DETAIL_PATH);
        groupUserPath = (String) conf.get(ClientConfig.REPORT_GROUP_USER_PATH);

        String authCode = ServiceHelper.getBasicAuthorization(appKey, masterSecret);
        _httpClient = new NativeHttpClient(authCode, proxy, conf);
    }

    public ReportClient(String masterSecret, String appKey, HttpProxy proxy, ClientConfig conf) {
        ServiceHelper.checkBasic(appKey, masterSecret);

        _hostName = (String) conf.get(ClientConfig.REPORT_HOST_NAME);
        _receivePath = (String) conf.get(ClientConfig.REPORT_RECEIVE_PATH);
        _userPath = (String) conf.get(ClientConfig.REPORT_USER_PATH);
        _messagePath = (String) conf.get(ClientConfig.REPORT_MESSAGE_PATH);
        _statusPath = (String) conf.get(ClientConfig.REPORT_STATUS_PATH);

        messageDetailPath = (String) conf.get(ClientConfig.REPORT_MESSAGE_DETAIL_PATH);
        receiveDetailPath = (String) conf.get(ClientConfig.REPORT_RECEIVE_DETAIL_PATH);
        groupMessageDetailPath = (String) conf.get(ClientConfig.REPORT_GROUP_MESSAGE_DETAIL_PATH);
        groupUserPath = (String) conf.get(ClientConfig.REPORT_GROUP_USER_PATH);

        String authCode = ServiceHelper.getBasicAuthorization(appKey, masterSecret);
        _httpClient = new NativeHttpClient(authCode, proxy, conf);
    }

    public static void checkMsgids(String msgIds) {
        if (StringUtils.isTrimedEmpty(msgIds)) {
            throw new IllegalArgumentException("msgIds param is required.");
        }

        if (MSGID_PATTERNS.matcher(msgIds).find()) {
            throw new IllegalArgumentException("msgIds param format is incorrect. "
                    + "It should be msg_id (number) which response from JPush Push API. "
                    + "If there are many, use ',' as interval. ");
        }

        msgIds = msgIds.trim();
        if (msgIds.endsWith(",")) {
            msgIds = msgIds.substring(0, msgIds.length() - 1);
        }

        String[] splits = msgIds.split(",");
        try {
            for (String s : splits) {
                s = s.trim();
                if (!StringUtils.isEmpty(s)) {
                    Long.parseLong(s);
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Every msg_id should be valid Long number which splits by ','");
        }
    }

    public ReceivedsResult getReceiveds(String[] msgIdArray)
            throws APIConnectionException, APIRequestException {
        return getReceiveds(StringUtils.arrayToString(msgIdArray));
    }

    public ReceivedsResult getReceiveds(String msgIds)
            throws APIConnectionException, APIRequestException {
        checkMsgids(msgIds);

        String url = _hostName + _receivePath + "?msg_ids=" + msgIds;
        ResponseWrapper response = _httpClient.sendGet(url);

        return ReceivedsResult.fromResponse(response);
    }

    public ReceivedsResult getReceivedsDetail(String msgIds)
            throws APIConnectionException, APIRequestException {
        checkMsgids(msgIds);

        String url = _hostName + receiveDetailPath + "?msg_ids=" + msgIds;
        ResponseWrapper response = _httpClient.sendGet(url);

        return ReceivedsResult.fromResponse(response);
    }

    public MessagesResult getMessages(String msgIds)
            throws APIConnectionException, APIRequestException {
        checkMsgids(msgIds);

        String url = _hostName + _messagePath + "?msg_ids=" + msgIds;
        ResponseWrapper response = _httpClient.sendGet(url);

        return MessagesResult.fromResponse(response);
    }

    public MessageDetailResult getMessagesDetail(String msgIds)
            throws APIConnectionException, APIRequestException {
        checkMsgids(msgIds);

        String url = _hostName + messageDetailPath + "?msg_ids=" + msgIds;
        ResponseWrapper response = _httpClient.sendGet(url);

        return MessageDetailResult.fromResponse(response);
    }

    public GroupMessageDetailResult getGroupMessagesDetail(String groupMsgIds)
            throws APIConnectionException, APIRequestException {
        String url = _hostName + groupMessageDetailPath + "?group_msgids=" + groupMsgIds;
        ResponseWrapper response = _httpClient.sendGet(url);

        return GroupMessageDetailResult.fromResponse(response);
    }

    public Map<String, MessageStatus> getMessagesStatus(CheckMessagePayload payload)
            throws APIConnectionException, APIRequestException {
        String url = _hostName + (_statusPath.endsWith("/message") ?
                _statusPath :
                (_statusPath + "/message"));
        ResponseWrapper result = _httpClient.sendPost(url, payload.toString());
        Type type = new TypeToken<Map<String, MessageStatus>>() {
        }.getType();
        return new Gson().fromJson(result.responseContent, type);
    }

    public UsersResult getUsers(TimeUnit timeUnit, String start, int duration)
            throws APIConnectionException, APIRequestException {
        String startEncoded = null;
        try {
            startEncoded = URLEncoder.encode(start, "utf-8");
        } catch (Exception e) {
        }

        String url = _hostName + _userPath
                + "?time_unit=" + timeUnit.toString()
                + "&start=" + startEncoded + "&duration=" + duration;
        ResponseWrapper response = _httpClient.sendGet(url);

        return BaseResult.fromResponse(response, UsersResult.class);
    }

    public GroupUsersResult getGroupUsers(TimeUnit timeUnit, String start, int duration)
            throws APIConnectionException, APIRequestException {
        String startEncoded = null;
        try {
            startEncoded = URLEncoder.encode(start, "utf-8");
        } catch (Exception e) {
        }

        String url = _hostName + groupUserPath
                + "?time_unit=" + timeUnit.toString()
                + "&start=" + startEncoded + "&duration=" + duration;
        ResponseWrapper response = _httpClient.sendGet(url);

        return BaseResult.fromResponse(response, GroupUsersResult.class);
    }

}


