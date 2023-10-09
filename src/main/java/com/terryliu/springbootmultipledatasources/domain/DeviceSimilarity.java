package com.terryliu.springbootmultipledatasources.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description Device Similarity Entity
 */
@Data
public class DeviceSimilarity implements Serializable {
    private String id;
    private Float cosineSimilarity;
}
