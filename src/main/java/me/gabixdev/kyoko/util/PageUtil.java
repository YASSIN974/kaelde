package me.gabixdev.kyoko.util;

import java.util.List;

public class PageUtil {
    public static List getPage(List l, int pageNum, int pageSize) {
        return l.subList(Math.max(0, pageNum * pageSize), Math.min(l.size(), (pageNum + 1) * pageSize));
    }

    public static int getPageCount(List l, int pageSize) {
        if (pageSize == 0) {
            return 0;
        }
        int size = l.size();

        return size % pageSize == 0 ? (size / pageSize) : (size / pageSize + 1);
    }
}
