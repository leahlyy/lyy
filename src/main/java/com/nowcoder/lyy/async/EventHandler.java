package com.nowcoder.lyy.async;

import java.util.List;

public interface EventHandler {
    void doHandle(com.nowcoder.async.EventModel model);
    List<EventType> getSupportEventTypes();
}
