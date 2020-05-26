package com.zjc.chatroom.service;

import com.alibaba.fastjson.JSONObject;
import com.zjc.chatroom.component.MyChatDecoder;
import com.zjc.chatroom.component.MyChatEncoder;
import com.zjc.chatroom.config.GetHttpSessionConfigurator;
import com.zjc.chatroom.domain.ChatRoom;
import com.zjc.chatroom.domain.MessageEntity;
import com.zjc.chatroom.domain.User;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
/*
* status规则
* 200   普通消息
* 201   图片消息
* 301   实时人数更新消息
* 302   聊天室信息
* 303   通知用户sessionId
* 400   非法操作，并通知并断开连接
* */
@Service
@ServerEndpoint(value="/chatRoom/{chatRoomId}/{password}/{userName}",
        decoders = MyChatDecoder.class,
        encoders = MyChatEncoder.class,
        configurator = GetHttpSessionConfigurator.class)
public class ChatService {
    private ChatRoom chatRoom;//当前会话所在聊天室
    private User user=new User();//当前会话的用户实例

    private static volatile Integer chatRoomsNumber=1000;//chatRoomID参考
    private static volatile ConcurrentHashMap<Integer, ChatRoom> chatRooms=new ConcurrentHashMap();//聊天室Map（Id，ChatRoom）集合

    private static ReentrantLock reentrantLock=new ReentrantLock();
    private static ExecutorService executorService=Executors.newFixedThreadPool(30);

    /*
    *创建聊天室，返回ID，创建失败返回0
    */
    public static Integer addChatRoom(String chatRoomName,String password){
        Integer chatRoomId=null;
        boolean isIdOccupied=false;

        reentrantLock.lock();
        chatRoomId=++chatRoomsNumber;
        isIdOccupied=chatRooms.get(chatRoomId)!=null?true:false;
        reentrantLock.unlock();

        if(isIdOccupied==true) return 0;

        ChatRoom newChatRoom=new ChatRoom();
        newChatRoom.setChatRoomId(chatRoomId);
        newChatRoom.setChatRoomName(chatRoomName);
        newChatRoom.setPassword(password);
        chatRooms.put(chatRoomId,newChatRoom);//实体类的Id同时作为map的key
        return chatRoomId;
    }

    public static ConcurrentHashMap<Integer, ChatRoom> getChatRooms(){
        return chatRooms;
    }

    public static ChatRoom getChatRoom(Integer chatRoomId){
        return chatRooms.get(chatRoomId);
    }


    @OnOpen
    public void onOpen(Session session,
                       @PathParam("chatRoomId")Integer chatRoomId,
                       @PathParam("password") String password,
                       @PathParam("userName") String userName)  {
        System.out.println("来了一个连接，sessionId是"+session.getId());

        this.user.setUserName(userName);
        this.user.setSession(session);
        this.chatRoom=chatRooms.get(chatRoomId);
        if(this.chatRoom!=null) {
            if(!password.equals(chatRoom.getPassword())){
                errorNotice(session,new MessageEntity(400,"密码错误"));
                onClose(session,new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"Incorrect Password"));
            }else{
                this.chatRoom.addUser(user);
                sendMessage(session,new MessageEntity(303,this.user.getSession().getId()));//随时更新的，系统消息
                sendMessage(session, new MessageEntity(302,this.chatRoom.getChatRoomName()));//随时更新的，系统消息
                sendMessageInRoom(new MessageEntity(200,"系统",userName+"'加入了群聊！"));//聊天消息
                sendMessageInRoom(new MessageEntity(301,this.chatRoom.getUsersNumber().toString()));//随时更新的，系统消息
            }
        }else{
            errorNotice(session,new MessageEntity(400,"聊天室不存在"));//通知用户聊天室不存在，断开连接
            onClose(session,new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"chatRoom not exist"));
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        if(this.chatRoom!=null && this.chatRoom.remove(session)){
            sendMessageInRoom(new MessageEntity(301,this.chatRoom.getUsersNumber().toString()));//系统消息
            sendMessageInRoom(new MessageEntity(200,"系统",this.user.getUserName()+"离开了群聊！"));//聊天消息
        }
        System.out.println("一个连接断开："+closeReason);
    }

    @OnMessage
    public void onMessage(String message) {
        JSONObject pa=JSONObject.parseObject(message);

        if(this.chatRoom!=null){
            //用户发送消息只有message和status，sender要赋值
            MessageEntity messageEntity=new MessageEntity();
            messageEntity.setStatus(pa.getInteger("status"));
            messageEntity.setSender(this.user.getUserName());
            messageEntity.setMessage(pa.getString("message"));
            sendMessageInRoom(messageEntity);//向聊天室广播
        }
    }

    @OnError
    public void onError(Session session,Throwable error){
        error.printStackTrace();
    }

    public void errorNotice(Session session, MessageEntity message) {//用户错误操作提示
        sendMessage(session,message);
    }

    /*
    * 发送没有转成JSON字符串的消息
    * */
    public void sendMessage(Session session, Object message)  {
        String data= JSON.toJSONString(message);
        reentrantLock.lock();
        try {
            session.getBasicRemote().sendText(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        reentrantLock.unlock();
    }
    /*
     * 发送已经转成JSON字符串的消息
     * */
    public void sendMessage(Session session, String data)  {
        reentrantLock.lock();
        try {
            session.getBasicRemote().sendText(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        reentrantLock.unlock();
    }

    public void sendMessageInRoom(MessageEntity messageEntity)  {
        String data= JSON.toJSONString(messageEntity);
        ConcurrentHashMap<String,User> users=this.chatRoom.getUsers();
        for(Map.Entry<String, User> user: users.entrySet()){
            Session receiver=user.getValue().getSession();
            executorService.execute(new sendRunnable(receiver,data));
        }
    }

    class sendRunnable implements Runnable{
        Session receiver;
        String data;
        sendRunnable(Session session,String data){
            this.receiver=session;
            this.data=data;
        }

        @Override
        public void run() {
            sendMessage(receiver,data);
        }
    }
}

