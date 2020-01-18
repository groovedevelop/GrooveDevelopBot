import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    private long chat_id;

    Book book = new Book();
    public static void main(String[] args) {

    }

    public void onUpdateReceived (Update update) { //function is executed when the bot receives a message
        update.getUpdateId(); //update user information

        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());

        chat_id = update.getMessage().getChatId();

        sendMessage.setText(input(update.getMessage().getText()));

        try {
            execute(sendMessage); //sending message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public String input(String msg) { //message processing
        if (msg.contains("Hi") || msg.contains("Hello") || msg.contains("Привет")) {
            return "Здарова!";
        }

        if (msg.contains("Информация о книге")) {
            return getBookInfo();
        }

        if (msg.contains("/person")) {
            msg = msg.replace("/person ", "");
            return getPersonInfo(msg);
        }
        return "Не понял";
    }

    public String getBookInfo() {
        SendPhoto sendPhotoRequest = new SendPhoto();

        try (InputStream in = new URL(book.getImg()).openStream()) {
            Files.copy(in, Paths.get("D:\\srgBook")); //download img
            sendPhotoRequest.setChatId(chat_id);
            sendPhotoRequest.setPhoto(new File("D:\\srgBook"));
            execute(sendPhotoRequest); //sending img
            Files.delete(Paths.get("D:\\srgBook")); //deleting img
        }
        catch (IOException ex) {
            System.out.println("File not found");
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

        String info = book.getTitle()
                + "\nАвтор: " + book.getAuthorName()
                + "\nЖанр: " + book.getGenres()
                + "\n\nОписание:\n" + book.getDescription()
                +"\n\nКоличество лайков: " + book.getLikes()
                +"\n\nПоследние комментарии:\n" + book.getCommentList();
        return info;

    }

    public String getPersonInfo(String msg) {
        Author author = new Author(msg);

        SendPhoto sendPhotoRequest = new SendPhoto();

        try (InputStream in = new URL(author.getImg()).openStream()) {
            Files.copy(in, Paths.get("D:\\srgBook"));
            sendPhotoRequest.setChatId(chat_id);
            sendPhotoRequest.setPhoto(new File("D:\\srgBook"));
            execute(sendPhotoRequest);
            Files.delete(Paths.get("D:\\srgBook"));
        }
        catch (IOException ex) {
            System.out.println("File not found");
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }

        return author.getPersonInfo();
    }

    public String getBotUsername() { return "@GrooveDevelopBot"; }

    public String getBotToken() {
        Properties prop = new Properties();
        try {
            //load a properties file from class path, inside static method
            prop.load(Bot.class.getResourceAsStream("main/resources/config.properties"));

            //get the property value and print it out
            System.out.println(prop.getProperty("token"));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop.getProperty("token");
    }



}
