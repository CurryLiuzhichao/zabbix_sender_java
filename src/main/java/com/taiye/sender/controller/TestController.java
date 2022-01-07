package com.taiye.sender.controller;

import com.taiye.sender.sender.DataObject;
import com.taiye.sender.sender.Sender;
import com.taiye.sender.sender.SenderResult;
import lombok.extern.log4j.Log4j2;
import org.apache.juli.logging.LogFactory;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

@Log4j2
@RestController
@Aspect
@PropertySource(value = {"classpath:application.properties"})
public class TestController {

    @Autowired
    Environment environment;

    @RequestMapping("/senderlist/{extent}")
    public void senderList(@PathVariable Integer extent) throws IOException {
        //要发送到的server端IP以及端口号
        String host = environment.getProperty("sender.host");
        int port = Integer.parseInt(environment.getProperty("sender.port"));
        ArrayList<DataObject> dataObjects = new ArrayList();
        for (int i = 0; i < extent; i++) {
            DataObject dataObject = new DataObject();
            //在系统中显示的主机名
            dataObject.setHost(environment.getProperty("sender.dataObject.setHost"));
            //在系统中设置的键值
            dataObject.setKey(environment.getProperty("sender.dataObject.setKey"));
            //需要发送的值
            Random r = new Random();
            dataObject.setValue(String.valueOf(r.nextInt(100)));
            System.out.println(dataObject.getValue());
            // 时间戳是秒
            dataObject.setClock(System.currentTimeMillis() / 1000);
            dataObjects.add(dataObject);
        }
        Sender Sender = new Sender(host, port);
        SenderResult result = Sender.send(dataObjects);
        System.out.println(result);
    }

    //TODO 创建一个发一个
    @RequestMapping("/sender/{extent}")
    public void sender(@PathVariable Integer extent) throws IOException {
        //要发送到的server端IP以及端口号
        String host = environment.getProperty("sender.host");
        int port = Integer.parseInt(environment.getProperty("sender.port"));

        Sender Sender = new Sender(host, port);

        for (int i = 0; i < extent; i++) {
            DataObject dataObject = new DataObject();
            //在系统中显示的主机名
            dataObject.setHost(environment.getProperty("sender.dataObject.setHost"));
            //在系统中设置的键值
            dataObject.setKey(environment.getProperty("sender.dataObject.setKey"));
            //需要发送的值
            Random r = new Random();
            dataObject.setValue(String.valueOf(r.nextInt(100)));
            System.out.println(dataObject.getValue());
            // 时间戳是秒
            dataObject.setClock(System.currentTimeMillis() / 1000);
            SenderResult result = Sender.send(dataObject);
            System.out.println("result:" + result);
        }

    }

}
