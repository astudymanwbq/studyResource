package com.wbq.spring.dao;

import com.wbq.spring.RedPacket;
import org.springframework.stereotype.Repository;


/**
 * @author 吴璧钦
 * @date 2018/6/22 11:04
 * @Description 大红包信息查询
 **/
@Repository
public interface RedPacketDao {

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
