package com.labs.urlshortener.server;

import com.labs.urlshortener.handler.RequestHandler;
import com.labs.urlshortener.service.UrlShortenerService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
* 서버 시작 → 포트 8080 열기
* 클라이언트 연결 대기 (accept)
* 연결되면 RequestHandler에게 넘김
* 다시 대기
* 연결되면 RequestHandler에게 넘김
* 반복...
* */

public class HttpServer {

    private final int port; // 서버가 열릴 포트 번호
    private final UrlShortenerService service; // URL 단축 비즈니스 로직, 요청처리할 떄 사용 로직
    private final ExecutorService threadPool; // 스레드풀, 여러 클라이언트 동시 처리할 때 사용 로직

    public HttpServer(int port) {
        this.port = port;
        this.service = new UrlShortenerService();
        this.threadPool = Executors.newVirtualThreadPerTaskExecutor();
    }


    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port); //포트 열고 대기
        System.out.println("서버 시작! 포트 : " + port) ;

        while(true) { // 무한루프
            Socket clientSocket = serverSocket.accept(); // 무한루프로 클라이언트 연결 대기
            threadPool.submit(new RequestHandler(clientSocket,service)); // 클라이언트 연결되면 RequestHandler 한테 전달, 스레드 풀이 처리하니까 다음 클라이언트도 바로 받을 있음
        }

    }

}
