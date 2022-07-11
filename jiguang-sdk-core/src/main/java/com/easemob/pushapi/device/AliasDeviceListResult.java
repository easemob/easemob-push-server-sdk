package com.easemob.pushapi.device;

import java.util.ArrayList;
import java.util.List;

import com.easemob.push.common.response.BaseResult;
import com.google.gson.annotations.Expose;

public class AliasDeviceListResult extends BaseResult {

    @Expose public List<String> registration_ids = new ArrayList<String>();

}

