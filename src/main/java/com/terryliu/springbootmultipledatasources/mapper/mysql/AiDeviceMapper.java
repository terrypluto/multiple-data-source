package com.terryliu.springbootmultipledatasources.mapper.mysql;

import com.terryliu.springbootmultipledatasources.domain.AiDeviceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description ai device dao
 */
@Mapper
public interface AiDeviceMapper {
    List<AiDeviceVO> getAiDevices(@Param("limit") Integer limit);
}
