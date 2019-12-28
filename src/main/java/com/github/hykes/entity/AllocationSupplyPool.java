package com.github.hykes.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 资金池
 * @author hehaiyangwork@qq.com
 * @date 2019-12-28 19:07:00
 */
@Data
public class AllocationSupplyPool implements Serializable {

    private static final long serialVersionUID = 4621492820089871168L;

    /**
     * 资金池标示
     */
    private Integer fundPoolId;

    /**
     * 支出前
     */
    private BigDecimal preAllocationShotfalls;

    /**
     * 支出数量
     */
    private BigDecimal allocationShotfalls;

    /**
     * 支出后
     */
    private BigDecimal afterAllocationShotfalls;


}
