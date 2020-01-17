import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Book {
    private Document document; //the page will be stored here

    public Book() { connect(); }

    private void connect() { //connect to page
        try {
            document = Jsoup.connect("https://www.surgebook.com/timeoff/book/36").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return document.title();
    }

    public String getLikes() {
        Element element = document.getElementById("likes");
        return element.text();
    }

    public String getDescription() {
        Element element = document.getElementById("description");
        return element.text();
    }

    public String getGenres() {
        Elements elements = document.getElementsByClass("genres d-block");
        return elements.text();
    }

    public String getCommentList() { //latest comments
        Elements elements = document.getElementsByClass("comment_mv1_item");

        String comment = elements.text();

        comment = comment.replaceAll("Ответить", "\n\n");
        comment = comment.replaceAll("Нравится", "");
        comment = comment.replaceAll("\\d{4}-\\d{2}-\\d{2}", ""); //remove date
        comment = comment.replaceAll("\\d{2}:\\d{2}:\\d{2}", ""); //remove time
        return comment;
    }

    public String getImg() {
        Elements elements = document.getElementsByClass("cover-book");
        String url = elements.attr("style");
        url = url.replace("background-image: url('", "");
        url = url.replace("');", "");
        return url;
    }

    public String getAuthorName() {
        Elements elements = document.getElementsByClass(
                "text-decoration-none column-author-name bold max-w-140 text-overflow-ellipsis");
        return elements.text();
    }

}
