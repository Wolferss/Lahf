package org.mmocore.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 19:13/04.04.2011
 */
public abstract class RunnableImpl implements Runnable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RunnableImpl.class);

    protected abstract void runImpl() throws Exception;

    @Override
    public final void run() {
        try {
            runImpl();
        } catch (Exception e) {
            LOGGER.error("Exception: RunnableImpl.run().", e);
        }
    }
}
