package bruzsal.dnsmanagement.dto;

import java.util.ArrayList;

public record CloudflareResultListDto<T>(
        ArrayList<T> result,
        Boolean success,
        ArrayList<String> errors,
        ArrayList<String> messages,
        ResultInfo result_info
) {

    record ResultInfo(
            int page,
            int per_page,
            int count,
            int total_count,
            int total_pages
    ) {
    }

}
