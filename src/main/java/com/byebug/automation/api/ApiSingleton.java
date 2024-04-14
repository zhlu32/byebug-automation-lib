package com.byebug.automation.api;

import com.byebug.automation.listeners.ByeBugCallbackListener;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiSingleton {

    private static ApiSingleton singleton;

    private ByeBugCallbackListener byeBugCallbackListener;

    public static synchronized ApiSingleton getInstance() {
        if (singleton == null) {
            singleton = new ApiSingleton();
        }
        return singleton;
    }

}