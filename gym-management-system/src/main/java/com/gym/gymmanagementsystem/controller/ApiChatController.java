package com.gym.gymmanagementsystem.controller;

import com.gym.gymmanagementsystem.pojo.ClassOrder;
import com.gym.gymmanagementsystem.pojo.Member;
import com.gym.gymmanagementsystem.service.ChatService;
import com.gym.gymmanagementsystem.service.ClassOrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ApiChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ClassOrderService classOrderService;

    @PostMapping("/query")
    public Map<String, Object> query(
            @RequestParam("content") String content,
            @RequestParam(required = false) String model,
            HttpSession session
    ){
        HashMap<String, Object> resp = new HashMap<>();
        try {
            Member member = (Member)session.getAttribute("user");
            String enhancedContent = buildContentWithMemberClasses(content,member);

            String reply = chatService.queryChat(enhancedContent,model);
            resp.put("success",true);
            resp.put("reply",reply);

        } catch (Exception e) {
            resp.put("success",false);
            resp.put("message",e.getMessage());
        }

        return resp;
    }

    // 返回的是已经报名的课程和用户的提问
    private String buildContentWithMemberClasses(String originalContent, Member member){
        if(member == null || member.getMemberAccount() == null){
            return originalContent;
        }

        List<ClassOrder> classOrders = classOrderService.selectClassOrderByMemberAccount(member.getMemberAccount());

        if(classOrders == null || classOrders.isEmpty()){
            return originalContent;
        }

        String classInfo = classOrders.stream()
                .map(order->String.format(
                        "课程名称：%s，教练：%s，上课时间：%s",
                        order.getClassName(),
                        order.getCoach(),
                        order.getClassBegin()

                ))
                .collect(Collectors.joining(";"));

        String memberInfo = String.format("会员姓名：%s，会员账号：%s",
                member.getMemberName(),
                member.getMemberAccount());

        StringBuilder sb = new StringBuilder();
        sb.append(originalContent == null ? "" : originalContent.trim());
        sb.append("\n\n");
        sb.append("【系统补充信息】下面是该会员当前已报名的课程信息，请先根据这些信息，明确地告诉用户“我报名了哪些课程”，然后再结合课程安排回答后续问题：\n");

        sb.append(memberInfo).append("\n");
        sb.append(classInfo);

        return sb.toString();

    }
}
