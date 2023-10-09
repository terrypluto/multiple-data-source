package com.terryliu.springbootmultipledatasources.mapper;

import com.alibaba.fastjson2.JSON;
import com.pgvector.PGvector;
import com.terryliu.springbootmultipledatasources.SpringbootMultipleDatasourcesApplicationTests;
import com.terryliu.springbootmultipledatasources.domain.DeviceSimilarity;
import com.terryliu.springbootmultipledatasources.mapper.postgres.SimilarDeviceMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description similar device mapper test
 */
public class SimilarDeviceMapperTest extends SpringbootMultipleDatasourcesApplicationTests {
    @Resource
    private SimilarDeviceMapper mapper;

    @Test
    public void test(){
        float[] floats = new float[512];
        for (int i =0;i<512;i++){
            floats[i] = 0;
        }
        List<DeviceSimilarity> deviceSimilarities = mapper.querySimilarity(new PGvector(floats));
        System.out.println(JSON.toJSONString(deviceSimilarities));
    }
}
