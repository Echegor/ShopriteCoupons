package com.archelo.coupons.json;

import java.util.List;

public class CouponsArray {
    List<String> available_ids_array;
    List<String> clipped_active_ids_array;

    public CouponsArray(List<String> available_ids_array, List<String> clipped_active_ids_array) {
        this.available_ids_array = available_ids_array;
        this.clipped_active_ids_array = clipped_active_ids_array;
    }

    public List<String> getAvailable_ids_array() {
        return available_ids_array;
    }

    public List<String> getClipped_active_ids_array() {
        return clipped_active_ids_array;
    }

    @Override
    public String toString() {
        return "CouponsArray{" +
                "available_ids_array=" + available_ids_array +
                ", clipped_active_ids_array=" + clipped_active_ids_array +
                '}';
    }
}
