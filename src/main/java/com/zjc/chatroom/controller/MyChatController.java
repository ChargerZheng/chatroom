package com.zjc.chatroom.controller;

import com.zjc.chatroom.domain.ChatRoom;
import com.zjc.chatroom.domain.MessageEntity;
import com.zjc.chatroom.domain.User;
import com.zjc.chatroom.service.ChatService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class MyChatController {

    private MessageEntity messageEntity=new MessageEntity();

    @ResponseBody
    @RequestMapping("/addChatRoom")
    public MessageEntity addChatRoom(@RequestParam("chatRoomName")String chatRoomName,@RequestParam("password")String password){
        Integer chatRoomId=ChatService.addChatRoom(chatRoomName,password);
        System.out.println(chatRoomId);
        if(chatRoomId>0){
            messageEntity.setStatus(200);
            messageEntity.setMessage(chatRoomId.toString());
            return messageEntity;
        }
        messageEntity.setStatus(500);
        messageEntity.setMessage("创建失败，请重新尝试。");
        return messageEntity;
    }

    @RequestMapping("/toChatRoom")
    public String toChatRoom(@RequestParam("chatRoomId")Integer chatRoomId,
                             @RequestParam("password")String password,
                             @RequestParam("userName")String userName,
                             HttpServletRequest request){

        request.setAttribute("chatRoomId",chatRoomId);
        request.setAttribute("password",password);
        request.setAttribute("userName",userName);
        return "chatRoom";
    }

    @ResponseBody
    @RequestMapping("/getChatRooms")
    public ArrayList<ChatRoom> getChatRooms(){
        ConcurrentHashMap<Integer, ChatRoom> chatRooms=ChatService.getChatRooms();
        ArrayList<ChatRoom> chatRoomList = new ArrayList<>();

        for(Map.Entry<Integer, ChatRoom> chatRoom: chatRooms.entrySet()){
            chatRoomList.add(new ChatRoom(chatRoom.getKey(),chatRoom.getValue().getChatRoomName(),chatRoom.getValue().getUsersNumber()));
        }
        return chatRoomList;
    }

    @ResponseBody
    @RequestMapping("/changeUserName")
    public MessageEntity changeUserName(@RequestParam("chatRoomId")Integer chatRoomId,
                                        @RequestParam("newUserName")String newUserName,
                                        @RequestParam("sessionId")String sessionId){
        ChatRoom chatRoom=ChatService.getChatRoom(chatRoomId);
        if(chatRoom==null){
            messageEntity.setStatus(400);
            messageEntity.setMessage("聊天室不存在");
            return messageEntity;
        }
        User user=chatRoom.getUser(sessionId);
        if(user==null){
            messageEntity.setStatus(400);
            messageEntity.setMessage("您不在该聊天室");
            return messageEntity;
        }
        user.setUserName(newUserName);
        messageEntity.setStatus(200);
        messageEntity.setMessage("修改成功！");
        return messageEntity;
    }
}
