package com.github.hykes.func;

import com.github.hykes.entity.AllocationSupplyPool;
import com.github.hykes.extend.CollectorsExt;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 资金池操作函数
 *
 * @author hehaiyangwork@gmail.com
 * @date 2019-12-28 19:14:00
 */
public final class AllocationFunc {

    private static final int DEFAULT_SCALE = 2;

    /**
     * 资金池支付方法
     * 默认精度2位小数
     *
     * @param pools 当前资金池
     * @param pay 支付金额
     * @return 支付后资金池
     */
    public static List<AllocationSupplyPool> pay(List<AllocationSupplyPool> pools, BigDecimal pay) {
        return pay(pools, pay, DEFAULT_SCALE);
    }

    /**
     * 资金池支付方法
     *
     * @param pools 当前资金池
     * @param pay 支付金额
     * @param scale 金额精度
     * @return 支付后资金池
     */
    public static List<AllocationSupplyPool> pay(List<AllocationSupplyPool> pools, BigDecimal pay, int scale) {
        if (CollectionUtils.isEmpty(pools)) {
            return Lists.newArrayList();
        }

        // 池内总余额
        BigDecimal balance = pools.stream().collect(
                CollectorsExt.summingBigDecimal(AllocationSupplyPool::getPreAllocationShotfalls));

        // 计算支付后，池内平均余额。向下取整，这时存在偏移量！
        BigDecimal payAfterAvg = balance.subtract(pay)
                .divide(BigDecimal.valueOf(pools.size()), scale, BigDecimal.ROUND_DOWN);

        // 对于大于平均值的资金池，需要进行扣减操作
        List<AllocationSupplyPool> allocationPools = pools.stream()
                .filter(dto -> dto.getPreAllocationShotfalls().compareTo(payAfterAvg) == 1).collect(
                        Collectors.toList());

        // 计算减到平均值时，扣减的总额
        List<BigDecimal> allocations = Lists.newArrayListWithCapacity(allocationPools.size());
        allocationPools.forEach(dto -> allocations.add(dto.getPreAllocationShotfalls().subtract(payAfterAvg)));
        BigDecimal totalAllocationPay = allocations.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算扣减的偏移量
        BigDecimal deviation = pay.subtract(totalAllocationPay);

        for (int i = 0, j = allocationPools.size(); i < j; i++) {
            AllocationSupplyPool pool = allocationPools.get(i);

            // 需要扣减的金额
            BigDecimal allocation = pool.getPreAllocationShotfalls().subtract(payAfterAvg);
            BigDecimal deviationAllocation = deviation
                    .divide(BigDecimal.valueOf((j - i)), scale, BigDecimal.ROUND_DOWN);
            BigDecimal realAllocation = allocation.add(deviationAllocation);

            pool.setAllocationShotfalls(realAllocation);
            pool.setAfterAllocationShotfalls(pool.getPreAllocationShotfalls().subtract(realAllocation));

            // 重新计算偏移量
            deviation = deviation.subtract(deviationAllocation);
        }

        // 对小于平均余额的资金池，不做扣减
        List<AllocationSupplyPool> noAllocationPools = pools.stream()
                .filter(dto -> dto.getPreAllocationShotfalls().compareTo(payAfterAvg) != 1)
                .collect(Collectors.toList());
        for (int i = 0, j = noAllocationPools.size(); i < j; i++) {
            AllocationSupplyPool pool = noAllocationPools.get(i);
            pool.setAllocationShotfalls(BigDecimal.ZERO);
            pool.setAfterAllocationShotfalls(pool.getPreAllocationShotfalls());
        }

        allocationPools.addAll(noAllocationPools);

        // 重新排序
        allocationPools.sort(Comparator.comparing(AllocationSupplyPool::getFundPoolId));
        return allocationPools;
    }

}
