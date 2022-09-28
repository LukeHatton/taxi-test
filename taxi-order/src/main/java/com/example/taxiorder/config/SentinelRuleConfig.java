package com.example.taxiorder.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * <p>Project: taxi-test
 * <p>Description:
 * <p>sentinel流控规则配置类
 *
 * @author lizhao 2022/9/28
 */
@Configuration
public class SentinelRuleConfig {

  // 限流规则
  @PostConstruct
  public void initFlowRule() {

    ArrayList<FlowRule> rules = new ArrayList<>();
    FlowRule rule = new FlowRule("my-flow-rule");

    // 流控资源名称
    rule.setResource("taxiOrder.getOrderById");
    // 限流类型：QPS
    rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
    // 限流阈值：1
    rule.setCount(1);
    // app名称
    rule.setLimitApp("default");

    // 加载规则列表
    rules.add(rule);
    FlowRuleManager.loadRules(rules);
  }

  // 降级规则
  @PostConstruct
  public void initDegradeRules() {
    ArrayList<DegradeRule> rules = new ArrayList<>();
    DegradeRule rule = new DegradeRule("my-degrade-rule");

    // 流控资源名称
    rule.setResource("taxiOrder.getOrderById");
    // 限流类型：SlowRatioThreshold
    rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
    // 限流阈值：10
    rule.setCount(10);
    // 熔断时长，单位为s
    rule.setTimeWindow(10);
    // app名称
    rule.setLimitApp("default");

    // 加载规则列表
    rules.add(rule);
    DegradeRuleManager.loadRules(rules);
  }

}
