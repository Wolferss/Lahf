package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.Announcement;
import org.mmocore.gameserver.model.AutoAnnouncement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Java-man
 */
public class AnnouncementsHolder extends AbstractHolder {
    private static final AnnouncementsHolder INSTANCE = new AnnouncementsHolder();

    private final List<AutoAnnouncement> autoAnnouncements = new ArrayList<>();
    private final List<Announcement> shownOnLoginAnnouncements = new ArrayList<>();
    private final List<Announcement> fullAnnouncements = new ArrayList<>();

    public static AnnouncementsHolder getInstance() {
        return INSTANCE;
    }

    public void addAutoAnnouncement(final Announcement announcement) {
        autoAnnouncements.add(AutoAnnouncement.createAutoAnnouncement(announcement));
    }

    public List<AutoAnnouncement> getAutoAnnouncements() {
        return autoAnnouncements;
    }

    public void addShownOnLoginAnnouncement(final Announcement announcement) {
        shownOnLoginAnnouncements.add(announcement);
    }

    public List<Announcement> getShownOnLoginAnnouncements() {
        return shownOnLoginAnnouncements;
    }

    @Override
    public int size() {
        return autoAnnouncements.size() + shownOnLoginAnnouncements.size();
    }

    public void addFullAnnouncement(final Announcement announcement) {
        fullAnnouncements.add(announcement);
    }

    public List<Announcement> getFullAnnouncement() {
        return fullAnnouncements;
    }

    public Announcement getFullAnnouncementDelete(final int line) {
        return fullAnnouncements.remove(line);
    }

    @Override
    public void clear() {
        autoAnnouncements.clear();
        shownOnLoginAnnouncements.clear();
        fullAnnouncements.clear();
    }
}
