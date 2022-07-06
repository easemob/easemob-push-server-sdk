package com.easemob.push.api.push;

import java.util.ArrayList;
import java.util.List;

import com.easemob.push.common.response.BaseResult;
import com.google.gson.annotations.Expose;

public class CIDResult extends BaseResult {

    @Expose public List<String> cidlist = new ArrayList<String>();
}
