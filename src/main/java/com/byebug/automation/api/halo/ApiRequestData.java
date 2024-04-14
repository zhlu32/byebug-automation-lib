package com.byebug.automation.api.halo;

import cn.hutool.core.util.RandomUtil;
import com.byebug.automation.api.param.AppendPathWithoutQueryParam;
import com.byebug.automation.api.param.BaseParam;
import com.byebug.automation.api.servicemap.ApiMethod;
import com.byebug.automation.api.servicemap.IServiceMap;
import com.byebug.automation.utils.HaloUtil;
import lombok.Data;
import org.testng.Assert;

import java.net.URLEncoder;
import java.util.Map;

@Data
public class ApiRequestData {
	// 请求的唯一标识
	private String uuid;

	// 请求发送前需要休眠几秒
	private int sleep;

	// 请求体-基本信息
	private IServiceMap serviceMap;

	// 请求体-实体类
	private BaseParam baseParam;

	// 断言体
	private ApiAssert apiAssert;

	public ApiRequestData(IServiceMap iServiceMap, BaseParam baseParam) {
		this(iServiceMap, baseParam, ApiAssert.createSuccess());
	}

	public ApiRequestData(IServiceMap iServiceMap, BaseParam baseParam, ApiAssert apiAssert) {
		uuid = RandomUtil.randomString(10);
		setServiceMap(iServiceMap);
		setBaseParam(iServiceMap, baseParam);
		setApiAssert(apiAssert);
	}

	private void setBaseParam(IServiceMap iServiceMap, BaseParam baseParam) {
		Class paramClass = iServiceMap.getRequestParamClass();
		if(!paramClass.getCanonicalName().equals(baseParam.getClass().getCanonicalName())) {
			Assert.fail("接口 " + iServiceMap.getPath() + " 请求传入的参数类，和定义的请求参数类不一致！！！");
		}

		this.baseParam = baseParam;
	}

	public String getAppendPathParam() {
		if(!ApiMethod.isNoRequestBodyApiMethod(serviceMap.getMethod())) {
			return "";
		}

		String appendPathParam = "";
		// For Sample: user/123
		if(baseParam instanceof AppendPathWithoutQueryParam) {
			appendPathParam = ((AppendPathWithoutQueryParam)baseParam).getAppendPathWithoutQuery();
		} else {
			// For Sample: user?id=123
			Map<String, String> maps = HaloUtil.getObjectMapByReflex(serviceMap.getRequestParamClass(), baseParam);
			if(maps.size() > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("?");
				for(String key : maps.keySet()) {
					String value = maps.get(key);
					sb.append(key);
					sb.append("=");
					sb.append(URLEncoder.encode(value));
					sb.append("&");
				}
				appendPathParam = sb.substring(0, sb.toString().length() - 1);
			}
		}
		return appendPathParam;
	}

}
