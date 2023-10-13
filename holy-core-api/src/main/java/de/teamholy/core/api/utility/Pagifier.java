package de.teamholy.core.api.utility;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Pagifier<T> {

    int maxItemsPerPage;
    List<List<T>> pages;

    public Pagifier(int maxItemsPerPage) {
        this.maxItemsPerPage = maxItemsPerPage;
        pages = new LinkedList<>();
        pages.add(new LinkedList<>());
    }

    public void addItem(T item) {
        int pageNum = pages.size() - 1;
        List<T> currentPage = pages.get(pageNum);

        // Add page if full
        if (currentPage.size() >= maxItemsPerPage) {
            currentPage = new LinkedList<>();
            pages.add(currentPage);
        }

        currentPage.add(item);
    }

    public List<T> getPage(int pageNum) {
        if (pages.size() == 0) {
            return null;
        }
        pageNum -= 1;
        if (pageNum < 0) {
            return null;
        }
        if (pageNum > (pages.size() - 1)) {
            return null;
        }
        return this.pages.get(pageNum);
    }

    public void reset() {
        for (List<T> page : pages) {
            page.clear();
        }
        pages.clear();
        pages.add(new LinkedList<>());
    }

    public int getTotalPageItems() {
        int total = 0;
        for (List<T> page : pages) {
            total += page.size();
        }
        return total;
    }

    public boolean containsItem(T item) {
        for (List<T> page : pages) {
            if (page.contains(item)) {
                return true;
            }
        }
        return false;
    }
    public int getTotalPages() {
        return pages.size();
    }

    public List<List<T>> getAllPages() {
        return this.pages;
    }

    public int getMaxItemsPerPage() {
        return this.maxItemsPerPage;
    }
}