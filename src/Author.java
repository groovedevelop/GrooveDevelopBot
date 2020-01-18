import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Author {
    private Document document; //users page parsing
    private Document booksDoc; //users books page parsing
    private String authorName = "";

    private int booksLikes;
    private int booksViews;
    private int booksComments;

    public Author(String name) {
        authorName = name;
        connect();
    }

    private void connect() { //connect to page
        try {
            document = Jsoup.connect("https://www.surgebook.com/" + authorName).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        Elements personName = document.getElementsByClass("author-name bold");
        return personName.text();
    }

    public String getBio() {
        Elements personBio = document.getElementsByClass("author-bio");
        return personBio.text();
    }

    public String getImg() {
        Elements elements = document.getElementsByClass("user-avatar");
        String url = elements.attr("style");
        url = url.replace("background-image: url('", "");
        url = url.replace("');", "");
        return url;
    }

    public String getPersonInfo() {
        String info = "";

        info += "Имя: " + getName() + "\n";
        info += "Статус: " + getBio() + "\n";

        Elements names = document.getElementsByClass("info-stats-name");
        Elements values = document.getElementsByClass("info-stats-num");

        for (int i = 0; i < names.size(); i++) {
            info += names.get(i).text() + ": " + values.get(i).text() + "\n";
        }
        info += getBooks();
        return info;
    }

    public String getBooks() {
        try {
            booksDoc = Jsoup.connect("https://www.surgebook.com/" + authorName + "/books/all").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String text = "\nСписок книг: \n";
        ArrayList<String> booksUrlArray = new ArrayList<>(); //books links

        Elements books = booksDoc.getElementsByClass("book_view_mv1v2_title"); //books titles parsing
        Elements booksUrl = booksDoc.getElementsByClass("book_view_mv1v2_cover"); //books links parsing

        for (int i = 0; i < books.size(); i++) {
            text += books.get(i).text() + "\n";
            booksUrlArray.add(booksUrl.get(i).attr("href")); //getting links
        }

        getStatistics(booksUrlArray);

        text += "\n\nКоличество лайков на книгах: " + booksLikes + "\n";
        text += "\n\nКоличество просмотров книг: " + booksViews + "\n";
        text += "\n\nКоличество комментариев к книгам: " + booksComments + "\n";

        return text;
    }

    private String getStatistics(ArrayList list) {
        for (int i = 0; i < list.size(); i++) {
            try {
                booksDoc = Jsoup.connect(list.get(i).toString()).get(); //connect to each book
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements elements = booksDoc.getElementsByClass("font-size-14 color-white ml-5");
            booksLikes += Integer.parseInt(elements.get(0).text()); //valueOf
            booksComments += Integer.parseInt(elements.get(1).text());
            booksViews += Integer.parseInt(elements.get(2).text());
        }
        return "";
    }


}
