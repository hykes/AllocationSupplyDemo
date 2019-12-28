package com.github.hykes.test;

import com.github.hykes.entity.AllocationSupplyPool;
import com.github.hykes.extend.CollectorsExt;
import com.github.hykes.func.AllocationFunc;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hehaiyangwork@gmail.com
 * @date 2019-12-28 19:05:00
 */
public class AllocationFuncTest {

    @Test
    public void testPay() {

        // 初始化金额
        List<String> pre = Lists.newArrayList("20", "47", "21", "41", "29", "9", "76", "97", "29", "66", "60", "92", "94", "27", "43", "75", "74", "41", "64", "61");

        List<AllocationSupplyPool> pools = Lists.newArrayListWithCapacity(pre.size());
        for (int i = 0, j = pre.size(); i < j; i++) {
            AllocationSupplyPool temp = new AllocationSupplyPool();
            temp.setFundPoolId(i + 1);
            temp.setPreAllocationShotfalls(new BigDecimal(pre.get(i)));
            temp.setAllocationShotfalls(BigDecimal.ZERO);
            temp.setAfterAllocationShotfalls(BigDecimal.ZERO);
            pools.add(temp);
        }

        BigDecimal pay = new BigDecimal("5000");
        AllocationFunc.pay(pools, pay);
        System.out.println(pools);

        BigDecimal balance = pools.stream().collect(
                CollectorsExt.summingBigDecimal(AllocationSupplyPool::getAllocationShotfalls));
        Assert.assertEquals(pay.compareTo(balance), 0);
    }

    @Test
    public void testPay2() {

        // 初始化金额
        List<String> pre = Lists.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        List<AllocationSupplyPool> pools = Lists.newArrayListWithCapacity(pre.size());
        for (int i = 0, j = pre.size(); i < j; i++) {
            AllocationSupplyPool temp = new AllocationSupplyPool();
            temp.setFundPoolId(i + 1);
            temp.setPreAllocationShotfalls(new BigDecimal(pre.get(i)));
            temp.setAllocationShotfalls(BigDecimal.ZERO);
            temp.setAfterAllocationShotfalls(BigDecimal.ZERO);
            pools.add(temp);
        }

        BigDecimal pay = new BigDecimal("15");
        AllocationFunc.pay(pools, pay);

        System.out.println(pools);

        BigDecimal balance = pools.stream().collect(
                CollectorsExt.summingBigDecimal(AllocationSupplyPool::getAllocationShotfalls));
        Assert.assertEquals(pay.compareTo(balance), 0);
    }

}
