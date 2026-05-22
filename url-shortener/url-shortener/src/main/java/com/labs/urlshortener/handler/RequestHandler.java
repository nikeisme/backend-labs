package com.labs.urlshortener.handler;

import com.labs.urlshortener.service.UrlShortenerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * 1. 클라이언트가 보낸 HTTP 요청 읽기
 * 2. 요청 파싱 (어떤 메서드? 어떤 경로?)
 * 3. 비즈니스 로직 실행 (UrlShortenerService 호출)
 * 4. HTTP 응답 만들어서 클라이언트에게 전송
 */
public class RequestHandler implements Runnable {

    private final Socket clientSocket;
    private final UrlShortenerService service;

    public RequestHandler(Socket clientSocket, UrlShortenerService service) {
        this.clientSocket = clientSocket;
        this.service = service;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream())
        ) {
            // 1. 첫 번째 줄 읽기 (예: GET /abc123 HTTP/1.1)
            String requestLine = in.readLine();
            if (requestLine == null) return;

            System.out.println("요청: " + requestLine);

            // 2. 파싱
            String[] parts = requestLine.split(" ");
            String method = parts[0];  // GET or POST
            String path = parts[1];    // /abc123 or /shorten

            // 3. 라우팅
            if (method.equals("POST") && path.equals("/shorten")) {
                handleShorten(in, out);
            } else if (method.equals("GET") && !path.equals("/")) {
                handleRedirect(path, out);
            } else {
                sendResponse(out, "404 Not Found", "text/plain", "Not Found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // POST /shorten 처리
    private void handleShorten(BufferedReader in, PrintWriter out) throws IOException {
        // 헤더 읽기 (빈 줄 나올 때까지)
        int contentLength = 0;
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(": ")[1].trim());
            }
        }

        // 바디 읽기
        char[] body = new char[contentLength];
        in.read(body);
        String requestBody = new String(body);

        // JSON에서 url 추출 ({"url":"https://naver.com"})
        String originalUrl = requestBody
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .split("url:")[1]
                .trim();

        // 단축 코드 생성
        String code = service.shorten(originalUrl);

        // 응답
        String responseBody = "{\"code\":\"" + code + "\"}";
        sendResponse(out, "200 OK", "application/json", responseBody);
    }

    // GET /{code} 처리
    private void handleRedirect(String path, PrintWriter out) {
        String code = path.substring(1); // 앞에 / 제거
        String originalUrl = service.getOriginalUrl(code);

        if (originalUrl == null) {
            sendResponse(out, "404 Not Found", "text/plain", "Not Found");
            return;
        }

        // 301 리다이렉트
        out.print("HTTP/1.1 301 Moved Permanently\r\n");
        out.print("Location: " + originalUrl + "\r\n");
        out.print("\r\n");
        out.flush();
    }

    // 공통 응답 전송
    private void sendResponse(PrintWriter out, String status, String contentType, String body) {
        out.print("HTTP/1.1 " + status + "\r\n");
        out.print("Content-Type: " + contentType + "\r\n");
        out.print("Content-Length: " + body.length() + "\r\n");
        out.print("\r\n");
        out.print(body);
        out.flush();
    }
}