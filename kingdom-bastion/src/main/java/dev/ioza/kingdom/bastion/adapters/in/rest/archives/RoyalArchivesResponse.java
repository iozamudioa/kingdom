package dev.ioza.kingdom.bastion.adapters.in.rest.archives;

import java.util.List;
import java.util.Map;

public record RoyalArchivesResponse(List<Map<String, Object>> keys) {

    public RoyalArchivesResponse {
        keys = List.copyOf(keys);
    }

}
