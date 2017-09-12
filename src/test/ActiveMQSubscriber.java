package test;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by wuyiming on 2017/9/11.
 */
public class ActiveMQSubscriber{
    private static final String USERNAME="admin"; // 默认的连接用户名
    private static final String PASSWORD="admin"; // 默认的连接密码
//    private static final String BROKEURL="tcp://10.0.0.102:1883"; // 默认的连接地址
    private static final String BROKEURL="tcp://10.0.0.101:61616"; // 默认的连接地址

    public static void main(String[] args) {
        ConnectionFactory connectionFactory; // 连接工厂
        Connection connection = null; // 连接
        Session session; // 会话 接受或者发送消息的线程
        Destination destination; // 消息的目的地
        MessageConsumer messageConsumer; // 消息的消费者

        // 实例化连接工厂
        connectionFactory=new ActiveMQConnectionFactory(USERNAME,PASSWORD,BROKEURL);
        try {
            connection=connectionFactory.createConnection();  // 通过连接工厂获取连接
            connection.start(); // 启动连接
            session=connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE); // 创建Session
            // destination=session.createQueue("FirstQueue1");  // 创建连接的消息队列
            destination=session.createTopic("message.gps.realtime");
            messageConsumer=session.createConsumer(destination); // 创建消息消费者
            messageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try{
                        System.out.println("get message:" + ((TextMessage)message).getText());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }); // 注册消息监听
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
