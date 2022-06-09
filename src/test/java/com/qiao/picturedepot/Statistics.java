package com.qiao.picturedepot;

import com.qiao.picturedepot.util.StatisticsUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Statistics {
    @Autowired
    private StatisticsUtil statisticsUtil;

    @Test
    public void countForUser(){
        statisticsUtil.updateResourceUsage(1L);
    }
}
