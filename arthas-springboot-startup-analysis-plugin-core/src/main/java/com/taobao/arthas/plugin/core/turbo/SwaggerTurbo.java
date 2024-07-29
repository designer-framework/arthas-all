package com.taobao.arthas.plugin.core.turbo;

import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;

public class SwaggerTurbo implements AgentLifeCycleHook {

    private static final String SPRINGFOX_DOCUMENTATION_AUTO_STARTUP = "springfox.documentation.auto-startup";

    @Override
    public void start() {
        System.setProperty(SPRINGFOX_DOCUMENTATION_AUTO_STARTUP, "false");
    }
    
}
