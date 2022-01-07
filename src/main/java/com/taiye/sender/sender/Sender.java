package com.taiye.sender.sender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Sender {
    //简单工厂方法创建一个正则表达式
    private static final Pattern PATTERN = Pattern.compile("[^0-9\\.]+");
    private final static Charset UTF8 = Charset.forName("UTF-8");

    String host;
    int port;
    int connectTimeout = 3 * 1000;
    int socketTimeout = 3 * 1000;

    /**
     * 设置server端IP以及端口号
     * @param host server端IP地址
     * @param port server端端口号默认为10051
     */
    public Sender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 设置server端IP、端口号、连接超时时间、套接字超时时间
     * @param host server端IP地址
     * @param port server端端口号默认为10051
     * @param connectTimeout 连接超时时间
     * @param socketTimeout 套接字超时时间
     */
    public Sender(String host, int port, int connectTimeout, int socketTimeout) {
        this(host, port);
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
    }

    /**
     * 发送 DataObject格式数据 返回发送结果
     * @param dataObject
     * @return
     * @throws IOException
     */
    public SenderResult send(DataObject dataObject) throws IOException {
        return send(dataObject, System.currentTimeMillis() / 1000);
    }

    /**
     * 发送DataObject+时间数据
     * @param dataObject
     * @param clock
     *            时间戳是秒
     * @return
     * @throws IOException
     */
    public SenderResult send(DataObject dataObject, long clock) throws IOException {
        return send(Collections.singletonList(dataObject), clock);
    }

    /**
     * 以list集合方式发送多条DataObject数据
     * @param dataObjectList
     * @return
     * @throws IOException
     */
    public SenderResult send(List<DataObject> dataObjectList) throws IOException {
        return send(dataObjectList, System.currentTimeMillis() / 1000);
    }

    /**
     *
     * @param dataObjectList
     * @param clock
     *            时间戳是秒
     * @return
     * @throws IOException
     */
    public SenderResult send(List<DataObject> dataObjectList, long clock) throws IOException {
        //创建发送者的结果
        SenderResult senderResult = new SenderResult();

        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            //新建socket对象
            socket = new Socket();
            //设置socket超时时间
            socket.setSoTimeout(socketTimeout);
            //创建链接
            socket.connect(new InetSocketAddress(host, port), connectTimeout);
            //获取输入流、输出流
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            //发送请求 设置data参数和时间参数
            SenderRequest senderRequest = new SenderRequest();
            senderRequest.setData(dataObjectList);
            senderRequest.setClock(clock);
            //输出流写入内容为 senderRequest的字符串编码
            outputStream.write(senderRequest.toBytes());
            //使用flush（）方法强迫输出流（或者缓冲的流）发送数据
            outputStream.flush();

            // 正常情况下 responseData.length < 100
            byte[] responseData = new byte[512];

            int readCount = 0;

            while (true) {
                //inputStream.read方法会读取输入流的下一个字节，并返回字节表示int值（0~255），如果已经读到结尾返回-1表示不能继续读取了。
                int read = inputStream.read(responseData, readCount, responseData.length - readCount);
                if (read <= 0) {
                    break;
                }
                readCount += read;
            }

            if (readCount < 13) {
                // server 返回 "[]"?
                senderResult.setbReturnEmptyArray(true);
            }

            // header('ZBXD\1') + len + 0
            // 5 + 4 + 4
            String jsonString = new String(responseData, 13, readCount - 13, UTF8);
            JSONObject json = JSON.parseObject(jsonString);
            String info = json.getString("info");
            // example info: processed: 1; failed: 0; total: 1; seconds spent:
            // 0.000053
            // after split: [, 1, 0, 1, 0.000053]
            String[] split = PATTERN.split(info);
            //处理返回格式
            senderResult.setProcessed(Integer.parseInt(split[1]));
            senderResult.setFailed(Integer.parseInt(split[2]));
            senderResult.setTotal(Integer.parseInt(split[3]));
            senderResult.setSpentSeconds(Float.parseFloat(split[4]));

        } finally {
            if (socket != null) {
                socket.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }

        return senderResult;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}
