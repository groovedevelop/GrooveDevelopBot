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
        return msg;
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

    public String getBotUsername() { return "@GrooveDevelopBot"; }

    public String getBotToken() { return "1031625636:AAHUcoj6Y6kdCMtKhOD7-lpBwj-whKE9Mbw"; }
}
