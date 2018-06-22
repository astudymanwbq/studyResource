package com.wbq.spring.service;


import com.wbq.spring.RedPacket;

/**
 * @author 吴璧钦
 * @date 2018/6/22 11:20
 * @Description
 **/
public interface RedPacketService {

    /**
     * 获取红包信息
     *
     * @param id 红包ID
     * @return 红包具体信息
     */
    RedPacket getRedPacket(Long id);

    /**
     * 扣减红包数目
     *
     * @param id 红包id
     * @return 更新记录条数
     */
    int decreaseRedPacket(Long id);
}
