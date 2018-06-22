package com.wbq.spring.dao;

import com.wbq.spring.UserRedPacket;
import org.springframework.stereotype.Repository;


/**
 * @author 吴璧钦
 * @date 2018/6/22 11:12
 * @Description 抢红包
 **/
@Repository
public interface UserRedPacketDao {

    /**
     * 插入抢红包信息
     *
     * @param userRedPacket 抢红吧信息
     * @return 影响条数
     */
    int grabRedPacket(UserRedPacket userRedPacket);
}
