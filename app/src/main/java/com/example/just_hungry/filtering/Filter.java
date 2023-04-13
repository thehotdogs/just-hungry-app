package com.example.just_hungry.filtering;

import java.util.ArrayList;

public interface Filter<T> {
    ArrayList<T> filter(ArrayList<T> items);
}
