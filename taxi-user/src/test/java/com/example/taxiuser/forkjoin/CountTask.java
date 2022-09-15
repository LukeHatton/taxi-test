package com.example.taxiuser.forkjoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * project : taxi-test
 * <p>description:
 * <p>
 * 使用Fork/Join框架实现基础功能：计数
 *
 * @author : consi
 * @since : 2022/9/5
 **/
public class CountTask extends RecursiveTask<Integer> {

  private static final Logger log = LoggerFactory.getLogger(CountTask.class);

  private static final int THRESHOLD = 2;

  private final int start;

  private final int end;

  public CountTask(int start, int end) {
    this.start = start;
    this.end = end;
  }

  @Override
  protected Integer compute() {
    int sum = 0;

    //如果任务足够小就直接计算
    boolean canCompute = (end - start) <= THRESHOLD;
    if (canCompute) {
      for (int i = start; i <= end; i++) {
        sum += i;
      }
    } else {

      // 如果任务大于阈值，就分为两个子任务计算
      int middle = (start + end) / 2;
      CountTask leftTask = new CountTask(start, middle);
      CountTask rightTask = new CountTask(middle + 1, end);

      // 执行子任务
      leftTask.fork();
      rightTask.fork();

      // 等待子任务执行完，并得到结果
      Integer leftResult = leftTask.join();
      Integer rightResult = rightTask.join();
      sum = leftResult + rightResult;
    }

    return sum;
  }

  public static void main(String[] args) {
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    // 生成一个计算任务，负责计算1~100的整数的和
    CountTask countTask = new CountTask(1, 100);
    ForkJoinTask<Integer> result = forkJoinPool.submit(countTask);
    try {
      System.out.println(result.get());
    } catch (InterruptedException | ExecutionException e) {
      log.error("==> fork/join任务异常", e);
    }
  }

}
