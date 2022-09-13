package com.example.taxiuser.threadPool;


import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * project : taxi-test
 * <p>description:
 * <p>
 * 创建一个使用线程池技术的简单http server
 *
 * @author : consi
 * @since : 2022/8/31
 **/
public class SimpleHttpServer {

  // 用来处理HttpRequest的线程池
  static ThreadPool<HttpRequestHandler> threadPool = new DefaultThreadPool<>(1);

  // SimpleHttpServer的根路径
  static String basePath;

  static ServerSocket serverSocket;

  // 服务器监听端口
  static int port = 8080;

  public static void setPort(int port) {
    if (port > 0) {
      SimpleHttpServer.port = port;
    }
  }

  public static void setBasePath(String basePath) {
    if (basePath != null && new File(basePath).exists() && new File(basePath).isDirectory()) {
      SimpleHttpServer.basePath = basePath;
    }
  }

  // 启动SimpleHttpServer
  public static void start() throws IOException {
    serverSocket = new ServerSocket(port);
    @Cleanup Socket socket = null;
    while ((socket = serverSocket.accept()) != null) {
      // 接收一个客户端Socket，生成一个HttpRequestHandler，放入线程池执行
      threadPool.execute(new HttpRequestHandler(socket));
    }
  }

  static class HttpRequestHandler implements Runnable {

    private final Socket socket;

    public HttpRequestHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    @SneakyThrows
    public void run() {
      String line = null;
      @Cleanup BufferedReader br = null;
      @Cleanup BufferedReader reader = null;
      @Cleanup PrintWriter out = null;
      @Cleanup InputStream in = null;
      try {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String header = reader.readLine();
        // 由相对路径计算出绝对路径
        String filePath = basePath + header.split(" ")[1];
        out = new PrintWriter(socket.getOutputStream());
        // 如果资源名称后缀为jpg或ico，则读取资源并输出
        if (filePath.endsWith("jpg") || filePath.endsWith("ico")) {
          in = new FileInputStream(filePath);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          int i;
          while ((i = in.read()) != -1) {
            baos.write(i);
          }
          byte[] array = baos.toByteArray();
          out.println("HTTP/1.1 200 OK");
          out.println("Server: Molly");
          out.println("Content-Type: image/jpeg");
          out.println("Content-Length: " + array.length);
          out.println("");
          socket.getOutputStream().write(array, 0, array.length);
        } else {
          br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
          out = new PrintWriter(socket.getOutputStream());
          out.println("HTTP/1.1 200 OK");
          out.println("Server: Molly");
          out.println("Content-Type: text/html; charset=UTF-8");
          out.println("");
          while ((line = br.readLine()) != null) {
            out.println(line);
          }
        }
        out.flush();
      } catch (IOException e) {
        e.printStackTrace();
        out.println("HTTP/1.1 500");
        out.println("");
        out.flush();
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }


}
