package bruzsal.dnsmanagement.todo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TodoItem {

    private String description;
    private LocalDateTime createDate;

}
