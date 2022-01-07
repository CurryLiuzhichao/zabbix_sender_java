package com.taiye.sender;


import com.taiye.sender.sender.DataObject;
import com.taiye.sender.sender.Sender;
import com.taiye.sender.sender.SenderResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

@SpringBootTest
class SenderApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void sender() throws IOException {
        //要发送到的server端IP以及端口号
        String host = "192.168.0.25";
        int port = 10051;
        Sender Sender = new Sender(host, port);

        DataObject dataObject = new DataObject();
        //在系统中显示的主机名
        dataObject.setHost("lzc");
        //在系统中设置的键值
        dataObject.setKey("test_item");
        //需要发送的值
        dataObject.setValue("10");
        // 时间戳是秒
        dataObject.setClock(System.currentTimeMillis()/1000);
        SenderResult result = Sender.send(dataObject);

        System.out.println("result:" + result);
        if (result.success()) {
            System.out.println("send success.");
        } else {
            System.err.println("sned fail!");
        }
    }

    @Test
    public void sendList() throws IOException {
        //要发送到的server端IP以及端口号
        String host = "192.168.0.25";
        int port = 10051;

        ArrayList<DataObject> dataObjects = new ArrayList();

        for (int i = 0; i<= 10;i++ ){
            DataObject dataObject = new DataObject();
            //在系统中显示的主机名
            dataObject.setHost("lzc");
            //在系统中设置的键值
            dataObject.setKey("test_item");
            //需要发送的值
            Random r = new Random();
            dataObject.setValue(String.valueOf(r.nextInt(10)));
            System.out.println(dataObject.getValue());
            // 时间戳是秒
            dataObject.setClock(System.currentTimeMillis()/1000);
            dataObjects.add(dataObject);
        }


        Sender Sender = new Sender(host, port);

        SenderResult result = Sender.send(dataObjects);
        System.out.println(result);

    }

}
