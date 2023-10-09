package com.terryliu.springbootmultipledatasources.mapper.postgres;

import com.pgvector.PGvector;
import com.terryliu.springbootmultipledatasources.domain.DeviceSimilarity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description Query Similar Mapper
 */
@Mapper
public interface SimilarDeviceMapper {
    List<DeviceSimilarity> querySimilarity(@Param("vector") PGvector vector);
}
