package com.honeydo5.honeydo.util;

import java.util.ArrayList;

/**
 * Created by aaron on 3/27/2018.
 */

public class TagSystem {
    private static ArrayList<Tag> tagList = new ArrayList<Tag>();

    public static ArrayList<Tag> getTagList() {
        return tagList;
    }

    public static void addTag(String s) {
        tagList.add(new Tag(s));
    }

    public static void init() {
        tagList.add(new Tag("School"));
        tagList.add(new Tag("Chores"));
        tagList.add(new Tag("Work"));
    }
}
