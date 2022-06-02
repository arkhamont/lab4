package main;


import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

public class TCPClient implements Runnable {
    private  int PORT;
    private  String HOST;
    private  BufferedReader in = null;
    private  BufferedWriter out = null;
    private String message = null;
    private String fileName = null;

    public void sendMessage(String message) throws IOException {
        out.write(message + '\n');
        out.flush();
    }

    public String readMessage() throws IOException {
        String message = in.readLine();
        fileWriter(fileName,message);
        return message;
    }

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

    private String name = null;
    public TCPClient(String host, int port){
        this.HOST = host;
        this.PORT = port;
    }
    public void run(){
        try{
            Scanner sc = new Scanner(System.in);
            System.out.println("Введите путь к файлу журнала:");
            fileName = sc.nextLine();

            System.out.println(HOST + "  " +PORT);
            Socket socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Добро пожаловать");
            String message;
            while(true){
                System.out.println("Выберите действие:\n1) Показать массивы\n2) Изменить массивы (exit - для выхода)");
                    message = sc.nextLine();
                    sendMessage(message);
                    if(message.equals("1")){
                        System.out.println("Целочисленный массив:");
                        for (String rows : readMessage().split(";")) {
                            for (String words: rows.split(",")) {
                                System.out.format("%6s|", words);
                            }
                            System.out.println();
                        }
                        System.out.println("\nМассив вещественных чисел:");
                        for (String rows : readMessage().split(";")) {
                            for (String words : rows.split(",")) {
                                System.out.format("%6s|", words);
                            }
                            System.out.println();
                        }
                        System.out.println("\nМассив строк:");
                        for (String rows : readMessage().split(";")) {
                            for (String words : rows.split(",")) {
                                System.out.format("%17s|", words);
                            }
                            System.out.println();
                        }
                    }
                    else if(message.equals("2")) {
                        System.out.println("Выполняется действие 2");
                        System.out.println("Введите номер массива,индекс и новое значения для изменения ячейки;\n" +
                                "номера массивов: 1-целочисленный, 2-вещественный, 3-строковый;" +
                                "(например: 1 2 3 new)");
                        String messageArr = sc.nextLine();
                        System.out.println(messageArr.split(" "));
                        String[] str = messageArr.split(" ");
                        if(messageArr.split(" ").length == 4){
                            System.out.println("Текст отправлен");
                            sendMessage(messageArr);
                            String read_message = readMessage();
                            if(read_message.equals("OK")){
                                System.out.println("Изменения успешно внесены");
                            }
                            else if(read_message.equals("FORBIDDEN")){
                                System.out.println("Изменения запрещены");
                            }
                            else{
                                System.out.println("Изменения не внесены, проверьте введенные данные");
                            }
                        }
                        else
                            System.out.println("Неверный формат данных");

                    }
                    else if(message.equals("exit")) {
                        in.close();
                        out.close();
                        socket.close();
                        break;
                    }
            }

        } catch (UnknownHostException e) {
            System.err.println("Исключение: " + e.toString());
        } catch (IOException e) {
            System.err.println("Исключение: " + e.toString());
        }
    }
    public static void main(String[] args) {
        String[] ip_data = args[0].split(":");

        TCPClient ja = new TCPClient(ip_data[0], Integer.parseInt(ip_data[1]));
        Thread th = new Thread(ja);
        th.start();
    }
}