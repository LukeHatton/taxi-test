package com.example.taxiuser;

import com.example.taxiuser.threadPool.SimpleHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * project : taxi-test
 * <p>description:
 *
 * @author : consi
 * @since : 2022/8/24
 **/
public class SimpleTest {

  private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

  volatile int i = 1;

  public static void main1(String[] args) {
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
    for (ThreadInfo threadInfo : threadInfos) {
      log.info("线程信息：【{}】", threadInfo.getThreadName());
    }
  }

  public static void main(String[] args) throws IOException {
    SimpleHttpServer.setPort(8080);
    SimpleHttpServer.setBasePath("D:\\摄影");
    SimpleHttpServer.start();

  }
}
