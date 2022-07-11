package com.easemob.pushapi.device;

import java.util.List;

import com.easemob.push.common.response.BaseResult;
import com.google.gson.annotations.Expose;

public class TagAliasResult extends BaseResult {

    private static final long serialVersionUID = -4765083329495728276L;
    @Expose public List<String> tags;
    @Expose public String alias;

}

