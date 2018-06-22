package com.wbq.spring.service;

/**
 * @author 吴璧钦
 * @date 2018/6/22 11:21
 * @Description
 **/
public interface UserRedPacketService {

    /**
     * 保存抢红包信息
     *
     * @param redPacketId 红包id
     * @param userId      用户id
     * @return 影响记录条数
     */
    int grabRedPacket(Long redPacketId, Long userId);

    /**
     * redis实现抢红包
     *
     * @param redPacketId
     * @param userId
     * @return 0失败 1成功 2成功是最后一个红包
     */
    Long grabRedPacketByRedis(Long redPacketId, Long userId);
}
