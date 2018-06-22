package com.wbq.spring.service;

/**
 * @author 吴璧钦
 * @date 2018/6/22 16:56
 * @Description
 **/
public interface RedisRedPacketService {

    /**
     * 保存抢红包信息
     *
     * @param redPacketId
     * @param unitAmount
     */
    void saveUserPacketByRedis(Long redPacketId, Double unitAmount);
}
