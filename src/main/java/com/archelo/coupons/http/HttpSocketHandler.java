package com.archelo.coupons.http;

import java.io.*;
import java.net.Socket;

public class HttpSocketHandler implements Runnable {
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private PrintWriter filePrintWriter;
    private int contentLength;

    public HttpSocketHandler(Socket socket) throws IOException{
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        filePrintWriter = new PrintWriter(new FileWriter(new File("logs/incoming.txt"),true),true);
        System.setProperty("line.separator", "\r\n");
        contentLength = 0;
    }
    public void run(){
        try{
            write("HTTP/1.1 200 OK");
            write("Cache-Control: private");
            write("Content-Type: text/html");
            write("Content-Length: 0");
            endMessage();
            String line;
            while ((line = bufferedReader.readLine())!= null){
                System.out.println("<< \"" +line + "[\\r][\\n]\"");
                parseContent(line);
                filePrintWriter.println(line);
                filePrintWriter.flush();
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    private void parseContent(String line) throws  IOException{
        if(line.equals("")){
            if(contentLength != 0){
                readContentLength();
            }
        } else if(line.startsWith("Content-Length:")){
            String [] delim = line.split(":");
            contentLength = Integer.valueOf(delim[1].trim());
        }
    }

    private void readContentLength() throws IOException{
        //System.out.println("Attempting to read content " + contentLength);
        char[] content = new char[contentLength];
        int count = bufferedReader.read(content);
        String contentString = new String(content);
        if(count == -1 || count != contentLength){
            throw new IOException("Failed to read content length: read " +count + " contentLength was " + contentString);
        }

        System.out.println("<< \"" +contentString + "[\\r][\\n]\"");
        filePrintWriter.println(contentString);
        filePrintWriter.flush();
    }

    public void write(String line){
        printWriter.println(line);
        printWriter.flush();
    }

    public void endMessage(){
        printWriter.println();
        printWriter.flush();
    }
}
