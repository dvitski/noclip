package me.andante.noclip.api.client.config;

public enum KeyBehavior {
    TOGGLE, HOLD;

    public boolean toggles() {
        return this == TOGGLE;
    }
}
