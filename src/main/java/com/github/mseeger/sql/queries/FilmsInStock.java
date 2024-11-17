package com.github.mseeger.sql.queries;

public class FilmsInStock {
    public static final String[] columnNames = {
            "filmID",
            "title",
            "numInStock"
    };

    private final int filmID;
    private final String title;
    private final int numInStock;

    public FilmsInStock(int filmID, String title, int numInStock) {
        this.filmID = filmID;
        this.title = title;
        this.numInStock = numInStock;
    }

    public int getFilmID() {
        return filmID;
    }

    public String getTitle() {
        return title;
    }

    public int getNumInStock() {
        return numInStock;
    }

    @Override
    public String toString() {
        return "FilmsInStock{" +
                "filmID=" + filmID +
                ", title=" + title +
                ", numInStock=" + numInStock +
                '}';
    }
}
