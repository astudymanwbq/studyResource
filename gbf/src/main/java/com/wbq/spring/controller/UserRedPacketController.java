package com.wbq.spring.controller;

import com.wbq.spring.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 吴璧钦
 * @date 2018/6/22 13:28
 * @Description
 **/
@Controller
public class UserRedPacketController {

    private final UserRedPacketService userRedPacketService;

    @Autowired
    public UserRedPacketController(UserRedPacketService userRedPacketService) {
        this.userRedPacketService = userRedPacketService;
    }

    @RequestMapping(value = "/userRedPacket/grabRedPacket")
    @ResponseBody
    public Map<String, Object> grabRedPacket(Long redPacketId, Long userId) {
        int result = userRedPacketService.grabRedPacket(redPacketId, userId);
        Map<String, Object> map = new HashMap<>();
        boolean success = result > 0;
        map.put("success", success);
        map.put("message", success ? "成功" : "失败");
        return map;
    }

    @RequestMapping("/page")
    public String page() {
        return "grab";
    }

    @RequestMapping("/userRedPacket/grabRedPacketByRedis")
    @ResponseBody
    public Map<String, Object> grabRedPacketByRedis(Long redPacketId, Long userId) {
        Long result = userRedPacketService.grabRedPacketByRedis(redPacketId, userId);
        boolean success = result > 0;
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", success ? "成功" : "失败");
        return map;
    }
}
