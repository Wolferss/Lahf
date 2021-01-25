package org.mmocore.gameserver.manager;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pchayka
 */
public class SoIManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoIManager.class);

    private static final long SOI_OPEN_TIME = 24 * 60 * 60 * 1000L;
    private static final Location[] openSeedTeleportLocs = {
            new Location(-179537, 209551, -15504),
            new Location(-179779, 212540, -15520),
            new Location(-177028, 211135, -15520),
            new Location(-176355, 208043, -15520),
            new Location(-179284, 205990, -15520),
            new Location(-182268, 208218, -15520),
            new Location(-182069, 211140, -15520),
            new Location(-176036, 210002, -11948),
            new Location(-176039, 208203, -11949),
            new Location(-183288, 208205, -11939),
            new Location(-183290, 210004, -11939),
            new Location(-187776, 205696, -9536),
            new Location(-186327, 208286, -9536),
            new Location(-184429, 211155, -9536),
            new Location(-182811, 213871, -9504),
            new Location(-180921, 216789, -9536),
            new Location(-177264, 217760, -9536),
            new Location(-173727, 218169, -9536)
    };

    private static Zone _zone = null;

    private SoIManager() {
        LOGGER.info("Seed of Infinity Manager: Loaded. Current stage is: " + getCurrentStage());
        _zone = ReflectionUtils.getZone("[inner_undying01]");
        checkStageAndSpawn();
        if (isSeedOpen()) {
            openSeed(getOpenedTime());
        }
    }

    public static SoIManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static int getCurrentStage() {
        return ServerVariables.getInt("SoI_stage", 1);
    }

    public static void setCurrentStage(final int stage) {
        if (getCurrentStage() == stage) {
            return;
        }
        if (stage == 3) {
            openSeed(SOI_OPEN_TIME);
        } else if (isSeedOpen()) {
            closeSeed();
        }
        ServerVariables.set("SoI_stage", stage);
        setCohemenesCount(0);
        setEkimusCount(0);
        setHoEDefCount(0);
        checkStageAndSpawn();
        LOGGER.info("Seed of Infinity Manager: Set to stage " + stage);
    }

    public static long getOpenedTime() {
        if (getCurrentStage() != 3) {
            return 0;
        }
        return ServerVariables.getLong("SoI_opened", 0) * 1000L - System.currentTimeMillis();
    }

    public static boolean isSeedOpen() {
        return getOpenedTime() > 0;
    }

    public static void openSeed(final long time) {
        if (time <= 0) {
            return;
        }
        ServerVariables.set("SoI_opened", (System.currentTimeMillis() + time) / 1000L);
        LOGGER.info("Seed of Infinity Manager: Opening the seed for " + Util.formatTime((int) time / 1000));
        spawnOpenedSeed();
        ReflectionUtils.getDoor(14240102).openMe();

        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                closeSeed();
                setCurrentStage(4);
            }
        }, time);
    }

    public static void closeSeed() {
        LOGGER.info("Seed of Infinity Manager: Closing the seed.");
        ServerVariables.unset("SoI_opened");
        //Despawning tumors / seeds
        SpawnManager.getInstance().despawn("soi_hos_middle_seeds");
        SpawnManager.getInstance().despawn("soi_hoe_middle_seeds");
        SpawnManager.getInstance().despawn("soi_hoi_middle_seeds");
        SpawnManager.getInstance().despawn("soi_all_middle_stable_tumor");
        ReflectionUtils.getDoor(14240102).closeMe();
        for (final Playable p : getZone().getInsidePlayables()) {
            p.teleToLocation(getZone().getRestartPoints().get(0));
        }
    }

    public static void checkStageAndSpawn() {
        SpawnManager.getInstance().despawn("soi_world_closedmouths");
        SpawnManager.getInstance().despawn("soi_world_mouths");
        SpawnManager.getInstance().despawn("soi_world_abyssgaze2");
        SpawnManager.getInstance().despawn("soi_world_abyssgaze1");
        switch (getCurrentStage()) {
            case 1:
            case 4:
                SpawnManager.getInstance().spawn("soi_world_mouths");
                SpawnManager.getInstance().spawn("soi_world_abyssgaze2");
                break;
            case 5:
                SpawnManager.getInstance().spawn("soi_world_closedmouths");
                SpawnManager.getInstance().spawn("soi_world_abyssgaze2");
                break;
            default:
                SpawnManager.getInstance().spawn("soi_world_closedmouths");
                SpawnManager.getInstance().spawn("soi_world_abyssgaze1");
                break;
        }
    }

    private static Zone getZone() {
        return _zone;
    }

    public static void notifyCohemenesKill() {
        if (getCurrentStage() == 1) {
            if (getCohemenesCount() < 9) {
                setCohemenesCount(getCohemenesCount() + 1);
            } else {
                setCurrentStage(2);
            }
        }
    }

    public static void notifyEkimusKill() {
        if (getCurrentStage() == 2) {
            if (getEkimusCount() < 2) {
                setEkimusCount(getEkimusCount() + 1);
            } else {
                setCurrentStage(3);
            }
        }
    }

    public static void notifyHoEDefSuccess() {
        if (getCurrentStage() == 4) {
            if (getHoEDefCount() < 9) {
                setHoEDefCount(getHoEDefCount() + 1);
            } else {
                setCurrentStage(5);
            }
        }
    }

    public static int getCohemenesCount() {
        return ServerVariables.getInt("SoI_CohemenesCount", 0);
    }

    public static void setCohemenesCount(final int i) {
        ServerVariables.set("SoI_CohemenesCount", i);
    }

    public static int getEkimusCount() {
        return ServerVariables.getInt("SoI_EkimusCount", 0);
    }

    public static void setEkimusCount(final int i) {
        ServerVariables.set("SoI_EkimusCount", i);
    }

    public static int getHoEDefCount() {
        return ServerVariables.getInt("SoI_hoedefkillcount", 0);
    }

    public static void setHoEDefCount(final int i) {
        ServerVariables.set("SoI_hoedefkillcount", i);
    }

    private static void spawnOpenedSeed() {
        SpawnManager.getInstance().spawn("soi_hos_middle_seeds");
        SpawnManager.getInstance().spawn("soi_hoe_middle_seeds");
        SpawnManager.getInstance().spawn("soi_hoi_middle_seeds");
        SpawnManager.getInstance().spawn("soi_all_middle_stable_tumor");
    }

    public static void teleportInSeed(final Player p) {
        p.teleToLocation(openSeedTeleportLocs[Rnd.get(openSeedTeleportLocs.length)]);
    }

    private static class LazyHolder {
        private static final SoIManager INSTANCE = new SoIManager();
    }
}