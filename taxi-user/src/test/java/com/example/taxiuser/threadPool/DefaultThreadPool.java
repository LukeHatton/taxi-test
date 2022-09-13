package com.example.taxiuser.threadPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * project : taxi-test
 * <p>description:
 * <p>
 * 尝试创建一个自定义线程池。可以看到，线程池只是使用了线程安全的工作队列来连接工作线程和客户端线程，客户端的线程将任务放入任务队列后就可以直接返回，
 * 而工作线程则从任务队列中取出任务并执行。当任务队列为空时，所有的工作线程就于工作队列的对象锁等待，当有一个任务被提交到任务队列后唤醒任意一个工作线程。
 * 随着更多的任务被提交到任务队列，线程池会唤醒更多的工作线程
 * <p>
 * 这只是一个线程池的简单示例，并没有实现工作线程数的自动扩缩容
 *
 * @author : consi
 * @since : 2022/8/31
 **/
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {

  // 最大工作线程数
  private static final int MAX_WORKER_NUMBER = 10;

  // 默认工作线程数
  private static final int DEFAULT_WORKER_NUMBER = 5;

  // 最小工作线程数
  private static final int MIN_WORKER_NUMBER = 1;

  // 提交到本线程池的任务列表
  private final LinkedList<Job> jobs = new LinkedList<>();

  // 线程池持有的工作线程列表
  private final List<Worker> workers = Collections.synchronizedList(new ArrayList<>());

  // 当前工作线程数
  private int workerNum = DEFAULT_WORKER_NUMBER;

  // 用于生成线程编号的原子引用对象
  private final AtomicInteger threadNum = new AtomicInteger();

  public DefaultThreadPool() {
    initializeWorkers(DEFAULT_WORKER_NUMBER);
  }

  public DefaultThreadPool(int num) {
    this.workerNum = Math.min(MAX_WORKER_NUMBER, Math.max(num, MIN_WORKER_NUMBER));
    initializeWorkers(workerNum);
  }

  /**
   * 向任务队列中添加一个任务。之所以使用了{@link #notify()}方法而不是{@link #notifyAll()}，是因为添加一个任务时其实只需一个工作线程，
   * 并不需要将所有工作线程都从等待队列移动到同步队列，这样做可以减少移动线程的开销
   *
   * @param job 要执行的任务
   */
  @Override
  public void execute(Job job) {
    if (job != null) {
      synchronized (jobs) {
        jobs.addLast(job);
        jobs.notify();
      }
    }
  }

  @Override
  public void shutdown() {
    for (Worker worker : workers) {
      worker.shutdown();
    }
  }

  // 添加一个worker，然后通知在jobs对象锁等待的其他线程
  @Override
  public void addWorkers(int num) {
    synchronized (jobs) {
      if (num + workerNum > MAX_WORKER_NUMBER) {
        num = MAX_WORKER_NUMBER - workerNum;
      }
      initializeWorkers(num);
      workerNum += num;
    }
  }

  @Override
  public void removeWorkers(int num) {
    synchronized (jobs) {
      if (num >= workerNum) {
        throw new IllegalArgumentException("==> 尝试移除的工作线程超过目前工作线程数！");
      }
      // 按照给定的数量停止worker线程
      int count = 0;
      // 一个一个地停止线程
      while (count < num) {
        Worker worker = workers.get(count);
        if (workers.remove(worker)) {
          worker.shutdown();
          count++;
        }
      }
      this.workerNum -= count;
    }
  }

  @Override
  public int getJobSize() {
    return jobs.size();
  }

  // 初始化工作线程。注意，本方法是增量式的，且并没有检查是否超过线程池最大线程数限制
  private void initializeWorkers(int num) {
    for (int i = 0; i < num; i++) {
      Worker worker = new Worker();
      workers.add(worker);
      Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
      thread.start();
    }
  }

  // 工作线程，用于处理提交到线程池的任务
  class Worker implements Runnable {

    // 代表线程的工作状态
    private volatile boolean running = true;

    @Override
    public void run() {
      while (running) {
        Job job;
        synchronized (jobs) {
          // 如果任务队列为空，则工作线程等待
          while (jobs.isEmpty()) {
            try {
              jobs.wait();
            } catch (InterruptedException e) {
              // 感知到外部对工作线程的中断，就直接返回来结束运行，以便线程池可以继续创建新线程
              e.printStackTrace();
              Thread.currentThread().interrupt();
              return;
            }
          }
          // 从任务队列中取出一个job
          job = jobs.removeFirst();
        }
        if (job != null) {
          try {
            job.run();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }

    // 通过将线程的工作状态修改为false来模拟关闭线程
    private void shutdown() {
      running = false;
    }

  }
}
