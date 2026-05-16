package com.example.brainbloom.game;

import com.example.brainbloom.R;
import com.example.brainbloom.models.Book;

import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    public static Book getBook(int bookNumber) {
        switch (bookNumber) {
            case 1:
                return new Book(1, "BOOK 1: FLOWER FIELD", "THE FLOWER FIELD", "Plants", 10,
                        "Unlock the Tree Area", R.drawable.ic_book1_pink_flower, R.drawable.bg_floral_field);
            case 2:
                return new Book(2, "BOOK 2: TREE", "THE TREE AREA", "General Knowledge", 10,
                        "Unlock the River", R.drawable.ic_book2_tree, R.drawable.bg_pine_stream);
            case 3:
                return new Book(3, "BOOK 3: RIVER", "THE RIVER", "Mixed Knowledge", 10,
                        "Unlock the Sky", R.drawable.ic_book3_wave, R.drawable.bg_pine_stream);
            case 4:
            default:
                return new Book(4, "BOOK 4: SKY", "THE SKY", "Logic", 10,
                        "Complete and Save the Garden", R.drawable.ic_book4_sun, R.drawable.bg_pastel_sky);
        }
    }

    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        books.add(getBook(1));
        books.add(getBook(2));
        books.add(getBook(3));
        books.add(getBook(4));
        return books;
    }
}
