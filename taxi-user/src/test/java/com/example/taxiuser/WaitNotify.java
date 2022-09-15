package com.example.taxiuser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * project : taxi-test
 * <p>description:
 * <p>
 * 创建两个线程，wait和notify，前者检查flag的值是否为false，如果为false，则执行后续操作，否则在lock处等待。后者在睡眠一段时间后对lock进行通知。
 * 这里的锁使用的是lock的对象锁
 *
 * @author : consi
 * @since : 2022/8/29
 **/
@Slf4j
public class WaitNotify {

  static boolean flag = true;

  static final Object lock = new Object();

  public static void main(String[] args) throws InterruptedException {
    Thread waitThread = new Thread(new Wait(), "waitThread");
    waitThread.start();
    Thread.sleep(1000);
    Thread notifyThread = new Thread(new Notify(), "notifyThread");
    notifyThread.start();
  }

  static class Wait implements Runnable {
    @Override
    public void run() {
      // 加锁，获取lock对象的monitor
      synchronized (lock) {
        // 当条件不满足时，持续wait，同时释放lock对象的锁
        while (flag) {
          try {
            log.info("{} flag is true. wait @ {}", Thread.currentThread(), new SimpleDateFormat("HH:mm:ss").format(new Date()));
            lock.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        // 条件满足时，完成工作
        log.info("{} flag is false. running @ {}", Thread.currentThread(), new SimpleDateFormat("HH:mm:ss").format(new Date()));
      }
    }
  }

  static class Notify implements Runnable {
    @SneakyThrows
    @Override
    public void run() {
      // 加锁，获取lock对象的monitor
      synchronized (lock) {
        // 获取lock对象的锁，然后进行通知，通知时不会释放lock的锁
        // 直到当前线程释放了lock对象的锁后，WaitThread才能从wait方法中返回
        log.info("{} holding lock. notify @ {}", Thread.currentThread(), new SimpleDateFormat("HH:mm:ss").format(new Date()));
        lock.notifyAll();
        flag = false;
        // sleep状态的线程并不会释放锁
        Thread.sleep(5000);
      }
      // 再次加锁
      synchronized (lock) {
        log.info("{} holding lock again. notify @ {}", Thread.currentThread(), new SimpleDateFormat("HH:mm:ss").format(new Date()));
        Thread.sleep(5000);
      }
    }
  }


}
