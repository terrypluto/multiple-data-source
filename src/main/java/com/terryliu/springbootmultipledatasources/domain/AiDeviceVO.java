package com.terryliu.springbootmultipledatasources.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description ai device
 */
@Data
public class AiDeviceVO {
    private String deviceId;
    private String deviceName;
    private Integer applicantId;
    private String companyName;
    private String productCodeId;
    private Date decisionDate;
    private String notes;
}
