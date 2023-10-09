package com.terryliu.springbootmultipledatasources.mapper;

import com.alibaba.fastjson2.JSON;
import com.terryliu.springbootmultipledatasources.SpringbootMultipleDatasourcesApplicationTests;
import com.terryliu.springbootmultipledatasources.domain.AiDeviceVO;
import com.terryliu.springbootmultipledatasources.mapper.mysql.AiDeviceMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description ai device mapper test
 */
public class AiDeviceMapperTest extends SpringbootMultipleDatasourcesApplicationTests {
    @Resource
    private AiDeviceMapper mapper;

    @Test
    public void test(){
        List<AiDeviceVO> aiDevices = mapper.getAiDevices(10);
        System.out.println(JSON.toJSON(aiDevices));
    }
}
