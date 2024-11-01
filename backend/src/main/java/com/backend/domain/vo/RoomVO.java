package com.backend.domain.vo;

import com.backend.domain.entity.Account;
import com.backend.domain.entity.Message;
import com.backend.domain.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomVO {

    private Room room;

    private Account toUser;

    private Message message;

    private Long countMessage;
}
