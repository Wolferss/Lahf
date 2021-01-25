package org.mmocore.commons.configuration.setup;

/**
 * Create by Mangol on 27.11.2015.
 */
@FunctionalInterface
public interface ISetup {
    /**
     * Используется непосредственно после заполнения основных переменных, делая конфиги более гибкими. Делая финт ушами.
     */
    void setup();
}
