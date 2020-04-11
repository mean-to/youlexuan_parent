package com.offcn;

import com.offcn.sms.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
@Component

public class SmsListener implements MessageListener {
    @Autowired
    private SmsUtil smsUtil;

    @Override
    public void onMessage(Message message) {
        if(message instanceof MapMessage){
            MapMessage mapMessage=(MapMessage)message;
            try {
                String mobile = mapMessage.getString("mobile");
                String signName = mapMessage.getString("signName");
                String templateCode = mapMessage.getString("templateCode");
                String templateParam = mapMessage.getString("templateParam");
                smsUtil.sendTextMessage(mobile,signName,templateCode,templateParam);
                System.out.println(mobile);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        
        
    }
}
