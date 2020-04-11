package com.offcn.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
/*
pom.xml
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.0.3</version>
</dependency>
*/
@Component
public class SmsUtil {
    @Value("AccessKeyID")
    private  String accessKeyId;
    @Value("AccessKeySecret")
    private String accessSecret;

        public CommonResponse sendTextMessage(String mobile,String signName,String templateCode,String templateParam) {
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",accessKeyId,accessSecret);
            IAcsClient client = new DefaultAcsClient(profile);
            CommonRequest request = new CommonRequest();

            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers",mobile);
            request.putQueryParameter("SignName",signName);
            request.putQueryParameter("TemplateCode",templateCode);
            request.putQueryParameter("TemplateParam",templateParam);
            try {
                CommonResponse response = client.getCommonResponse(request);
                System.out.println(response.getData());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

