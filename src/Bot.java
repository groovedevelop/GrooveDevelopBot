import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    private long chat_id;

    Book book = new Book();

    String lastMessage = "";
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    public static void main(String[] args) {

    }

    public void onUpdateReceived (Update update) { //function is executed when the bot receives a message
        update.getUpdateId(); //update user information
        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
        chat_id = update.getMessage().getChatId();

        String text = update.getMessage().getText(); //take incoming message text
        sendMessage.setReplyMarkup(replyKeyboardMarkup); //set keyboard for user

        try {
            sendMessage.setText(getMessage(text));
            execute(sendMessage); //sending message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public String getMessage(String msg) {
        ArrayList keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        replyKeyboardMarkup.setSelective(true); //show keyboard only certain users
        replyKeyboardMarkup.setResizeKeyboard(true); //set the keyboard height to the number of buttons
        replyKeyboardMarkup.setOneTimeKeyboard(false); //tells the user to hide the keyboard after use, default - false

        if (msg.equals("Привет") || msg.equals("Меню")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Популярное");
            keyboardFirstRow.add("Новости\uD83D\uDCF0");
            keyboardSecondRow.add("Полезная информация");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard); //refresh the keyboard
            return "Выбрать...";
        }

        if (msg.equals("Полезная информация")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Информация о книге");
            keyboardFirstRow.add("/person bebosehun_");
            keyboardFirstRow.add("Меню");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "ВАЖНО!";
        }

        if (msg.equals("Популярное")) {
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Стихи");
            keyboardFirstRow.add("Книги\uD83D\uDCDA");
            keyboardFirstRow.add("Меню");
            keyboard.add(keyboardFirstRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            return "Выбрать...";
        }

        if (msg.equals("Стихи") || msg.equals("Книги\uD83D\uDCDA")) {
            lastMessage = msg;
            keyboard.clear();
            keyboardFirstRow.clear();
            keyboardFirstRow.add("Сегодня");
            keyboardFirstRow.add("За неделю");
            keyboardFirstRow.add("За месяц");
        }

        return "";

    }


    public String getBookInfo(String href[]) {
        String info = "";

        for (int i = 0; i < href.length; i++) {
            info = "";
            Book book = new Book(href[i]);
            if (Files.exists(Paths.get("D:\\srgBook"))) {
                try {
                    Files.delete(Paths.get("D:\\srgBook"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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

            info = book.getTitle()
                    + "\nАвтор: " + book.getAuthorName()
                    + "\nЖанр: " + book.getGenres()
                    + "\n\nОписание:\n" + book.getDescription()
                    +"\n\nКоличество лайков: " + book.getLikes()
                    +"\n\nПоследние комментарии:\n" + book.getCommentList();

            SendMessage sendMessage = new SendMessage().setChatId(chat_id);
            try {
                sendMessage.setText(info);
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return "\uD83D\uDC40";
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

    public String getPoemTop(String[] text) { //get all poems
        SendMessage sendMessage = new SendMessage().setChatId(chat_id); //create new message
        for (int i = 0; i < text.length; i++) {
            try {
                sendMessage.setText(text[i]);
                execute(sendMessage); //sending
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return "\uD83D\uDC40";
    }

    public String getBotUsername() { return "@GrooveDevelopBot"; }

    public String getBotToken() {
        Properties prop = new Properties();
        try {
            //load a properties file from class path, inside static method
            prop.load(Bot.class.getResourceAsStream("main/resources/config.properties"));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop.getProperty("token");
    }



}
