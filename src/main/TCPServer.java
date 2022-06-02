package main;

import main.Properties;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

public class TCPServer {
    public static int PORT = Integer.parseInt(Properties.getProperty(Properties.HOST));
    private ServerSocket servSocket = null;
    private String fileName = Properties.getProperty(Properties.LOG_FILE);
    private  int intArr[][] = {
            {1,2,3,4,5},
            {6,7,8,9,10},
            {11,12,13,14,15},
            {16,17,18,19,20},
            {21,22,23,24,25}
    };

    private double floatArr[][] = {
            {26.1, 27.2, 28.3, 29.4, 30.5},
            {31.6, 32.7, 33.8, 34.9, 35.10},
            {36.11, 37.12, 38.13, 39.14, 40.15},
            {41.16, 42.17, 43.18, 44.19, 45.20},
            {46.21, 47.22, 48.23, 49.24, 50.25}
    };

    private String stringArr[][] = {
            {"one", "two", "three", "four", "five"},
            {"six", "seven", "eight", "nine", "ten"},
            {"eleven", "twelve", "thirteen", "fourteen", "fifteen"},
            {"sixteen", "seventeen", "eighteen", "nineteen", "twenty"},
            {"twenty one", "twenty two", "twenty three", "twenty four", "twenty five"}
    };

    private static String[] forbiddenIndexes = {};

    public void fileWriter(String fileName, String message){
        Date dt = new Date();
        File f = new File(fileName);
        try{
            if(!f.exists()) f.createNewFile();
            PrintWriter pw = new PrintWriter( new java.io.FileWriter(f.getAbsoluteFile(), true));
            try{
                pw.println("[ " + dt.toString() + " ] " + message);
            }finally{pw.close();}
        }catch(IOException e){throw new RuntimeException();}
    }

    public void sendMessage(BufferedWriter out, String message) throws IOException {
        out.write(message + '\n');
        out.flush();
    }

    public String readMessage(BufferedReader in) throws IOException {
        String message = in.readLine();
        fileWriter(fileName,message);
        return message;
    }

    public boolean findForbiddenIndexes(String index){
        for (String indexes: forbiddenIndexes) {
            if (index.equals(indexes))
                    return true;
        }
        return false;
    };

    public void showArrays(){
        System.out.println("Измененные массивы: ");

        System.out.println("Целочисленный массив:");
        for (int i = 0; i < intArr.length; i++){
            for (int j = 0; j < intArr[i].length; j++) {
                System.out.format("%6s|", intArr[i][j]);
            }
            System.out.println();
        }

        System.out.println("\nМассив вещественных чисел:");
        String double_Arr = "";
        for (int i = 0; i < floatArr.length; i++){
            for (int j = 0; j < floatArr[i].length; j++) {
                System.out.format("%6s|", floatArr[i][j]);
            }
            System.out.println();
        }

        System.out.println("\nМассив строк:");
        for (int i = 0; i < stringArr.length; i++){
            for (int j = 0; j < stringArr[i].length; j++) {
                System.out.format("%17s|", stringArr[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        forbiddenIndexes = args[0].split(",");
        TCPServer tcpServer = new TCPServer();
        tcpServer.go();
    }

    public void arrayDefender(){

    };

    public TCPServer(){
        try{
            servSocket = new ServerSocket(PORT);
        }catch(IOException e){
            System.err.println("Не удаётся открыть сокет для сервера: " + e.toString());
        }
    }
    public void go(){
        class Listener implements Runnable{
            Socket socket;
            public Listener(Socket aSocket){
                socket = aSocket;
            }
            public void run(){
                try{
                    BufferedReader in = null; // поток чтения из сокета
                    BufferedWriter out = null; // поток записи в сокет
                    System.out.println("Клиент подключился");
                    int count = 0;
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    String read_message;
                    while(true) {
                        read_message = readMessage(in);
                        System.out.println("Выполняется действие: " + read_message);
                        if (read_message.equals("1")) {
                            String int_Arr = "";
                            for (int i = 0; i < intArr.length; i++){
                                for (int j = 0; j < intArr[i].length; j++) {
                                    int_Arr += intArr[i][j] + ",";
                                }
                                int_Arr += ";";
                            }
                            sendMessage(out, int_Arr);

                            String double_Arr = "";
                            for (int i = 0; i < floatArr.length; i++){
                                for (int j = 0; j < floatArr[i].length; j++) {
                                    double_Arr += floatArr[i][j] + ",";
                                }
                                double_Arr += ";";
                            }
                            sendMessage(out, double_Arr);

                            String string_Arr = "";
                            for (int i = 0; i < stringArr.length; i++){
                                for (int j = 0; j < stringArr[i].length; j++) {
                                    string_Arr += stringArr[i][j] + ",";
                                }
                                string_Arr += ";";
                            }
                            sendMessage(out, string_Arr);
                        }
                        else {
                            System.out.println("Жду сообщение с индексом");
                            String index[] = readMessage(in).split(" ");
                            for (String ind: index) {
                                System.out.println(ind);
                            }
                            try {
                                if(!(findForbiddenIndexes(index[0] + index[1] + index[2]))) {
                                    int ind_i = Integer.parseInt(index[1]);
                                    int ind_j = Integer.parseInt(index[2]);
                                    if (index[0].equals("1")) {
                                        System.out.println("Изменяю первый массив");
                                        intArr[ind_i][ind_j] = Integer.parseInt(index[3]);
                                        showArrays();
                                        sendMessage(out, "OK");
                                    } else if (index[0].equals("2")) {
                                        System.out.println("Изменяю второй массив");
                                        floatArr[ind_i][ind_j] = Double.parseDouble(index[3]);
                                        showArrays();
                                        sendMessage(out, "OK");
                                    } else if (index[0].equals("3")) {
                                        System.out.println("Изменяю третий массив");
                                        stringArr[ind_i][ind_j] = index[3];
                                        showArrays();
                                        sendMessage(out, "OK");
                                    } else
                                        sendMessage(out, "ERROR");
                                }
                                else
                                    sendMessage(out, "FORBIDDEN");
                            }
                            catch(NumberFormatException e){
                                sendMessage(out, "ERROR");
                            }
                        }
                    }
                }catch(IOException e){
                    System.err.println("Клиент завершил работу!" + e.toString());
                }catch(NullPointerException e){
                    if (socket.isClosed()){
                        System.err.println("Клиент завершил работу" + e.toString());
                    }
                }catch (Exception e){
                    System.err.println("Клиент стоп!" + e.getStackTrace());
                }
            }
        }
        System.out.println("Сервер запущен...");
        while(true){
            try{
                Socket socket = servSocket.accept();
                Listener listener = new Listener(socket);
                Thread thread = new Thread(listener);
                thread.start();
            }catch(IOException e){
                System.out.println("Соединение сброшено");
            }
        }
    }
}
