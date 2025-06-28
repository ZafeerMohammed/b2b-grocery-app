package com.b2bapp.grocery.util;

import com.b2bapp.grocery.dto.WholesalerResponseDTO;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;

public class SortUtil {

    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "name", "price", "quantity", "category", "createdDate", "updatedDate"
    );

    public static Sort getValidatedSort(String sortBy, String sortDir) {
        String sortField = sortBy.toLowerCase();
        String direction = sortDir.toLowerCase();

        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "name"; // default fallback
        }

        return direction.equals("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
    }


    public static Comparator<WholesalerResponseDTO> getWholesalerComparator(String sortBy, String sortDir) {
        Comparator<WholesalerResponseDTO> comparator;

        switch (sortBy.toLowerCase()) {
            case "email" -> comparator = Comparator.comparing(WholesalerResponseDTO::getEmail, String.CASE_INSENSITIVE_ORDER);
            case "name" -> comparator = Comparator.comparing(WholesalerResponseDTO::getName, String.CASE_INSENSITIVE_ORDER);
            default -> comparator = Comparator.comparing(WholesalerResponseDTO::getName, String.CASE_INSENSITIVE_ORDER);
        }

        return sortDir.equalsIgnoreCase("desc") ? comparator.reversed() : comparator;
    }


}
