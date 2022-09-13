package com.example.taxiuser.threadPool;

/**
 * project : taxi-test
 * <p>description:
 *
 * @author : consi
 * @since : 2022/8/31
 **/
public interface ThreadPool<Job extends Runnable> {
  void execute(Job job);

  void shutdown();

  void addWorkers(int num);

  void removeWorkers(int num);

  int getJobSize();
}
